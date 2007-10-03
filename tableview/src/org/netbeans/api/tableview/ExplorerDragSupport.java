/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */
package org.netbeans.api.tableview;

import org.openide.nodes.Node;
import org.openide.util.Utilities;

import java.awt.Dialog;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import java.io.IOException;

import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.tree.*;


/** Support for the drag operations in explorer.
 * 
 * This class has been copied from openide/explorer.
 *
 * @author Jiri Rechtacek
 */
abstract class ExplorerDragSupport implements DragSourceListener, DragGestureListener {

    private static final Logger log = Logger.getLogger(ExplorerDragSupport.class.getName());
    
    // Attributes

    /** True when we are active, false otherwise */
    boolean active = false;

    /** Recognizes default gesture */
    DragGestureRecognizer defaultGesture;

    /** The component which we are supporting (our client) */
    protected JComponent comp;
    ExplorerDnDManager exDnD = ExplorerDnDManager.getDefault();

    abstract int getAllowedDropActions();

    /** Initiating the drag */
    public void dragGestureRecognized(DragGestureEvent dge) {
        // 1. get seleced dragged nodes
        Node[] nodes = obtainNodes(dge);

        // check nodes
        if ((nodes == null) || (nodes.length == 0)) {
            return;
        }

        // 2. detect highest common action
        int possibleNodeAction = getAllowedDragActions();

        for (int i = 0; i < nodes.length; i++) {
            if ((possibleNodeAction & DnDConstants.ACTION_MOVE) != 0) {
                if (!nodes[i].canCut()) {
                    possibleNodeAction = DnDConstants.ACTION_COPY | DnDConstants.ACTION_REFERENCE;
                }
            }

            if ((possibleNodeAction & DnDConstants.ACTION_COPY) != 0) {
                if (!nodes[i].canCopy()) {
                    possibleNodeAction = DnDConstants.ACTION_NONE;
                }
            }
        }

        exDnD = ExplorerDnDManager.getDefault();
        exDnD.setNodeAllowedActions(possibleNodeAction);

        int dragAction = dge.getDragAction();

        boolean dragStatus = canDrag(dragAction, possibleNodeAction);

        // 3. get transferable and start the drag
        try {
            // for MOVE
            Transferable transferable;

            if ((possibleNodeAction & DnDConstants.ACTION_MOVE) != 0) {
                // for MOVE
                transferable = DragDropUtilities.getNodeTransferable(nodes, DnDConstants.ACTION_MOVE);
                exDnD.setDraggedTransferable(transferable, true);

                // for COPY too
                transferable = DragDropUtilities.getNodeTransferable(nodes, DnDConstants.ACTION_COPY);
                exDnD.setDraggedTransferable(transferable, false);
            } else if ((possibleNodeAction & DnDConstants.ACTION_COPY) != 0) {
                // for COPY
                transferable = DragDropUtilities.getNodeTransferable(nodes, DnDConstants.ACTION_COPY);
                exDnD.setDraggedTransferable(transferable, false);
            } else {
                // transferable for NONE
                transferable = Node.EMPTY.drag();
                exDnD.setDraggedTransferable(transferable, false);
            }

            exDnD.setDraggedNodes(nodes);

            Dialog d = (Dialog) SwingUtilities.getAncestorOfClass(Dialog.class, comp);

            if ((d != null) && d.isModal()) {
                exDnD.setDnDActive(false);

                return;
            } else {
                exDnD.setDnDActive(true);
                dge.startDrag(
                    DragDropUtilities.chooseCursor(dge.getComponent(), dragAction, dragStatus),
                    Utilities.loadImage("org/openide/resources/cursorscopysingle.gif"), // NOI18N
                    new Point(16, 16), transferable, this
                );
            }
        } catch (InvalidDnDOperationException exc) {
            // cannot start the drag, notify as informational
            log.log(Level.FINE, "", exc);
            exDnD.setDnDActive(false);
        } catch (IOException exc) {
            // cannot start the drag, notify user
            log.log(Level.SEVERE, "", exc);
            exDnD.setDnDActive(false);
        }
    }

    protected int getAllowedDragActions() {
        return DnDConstants.ACTION_NONE;
    }

    private boolean canDrag(int targetAction, int possibleAction) {
        return (possibleAction & targetAction) != 0;
    }

    public void dragEnter(DragSourceDragEvent dsde) {
        doDragOver(dsde);
    }

    public void dragOver(DragSourceDragEvent dsde) {
        doDragOver(dsde);
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
        dse.getDragSourceContext().setCursor(
            DragDropUtilities.chooseCursor(comp, dse.getDragSourceContext().getSourceActions(), false)
        );
    }

    private void doDragOver(DragSourceDragEvent dsde) {
        dsde.getDragSourceContext().setCursor(exDnD.getCursor());
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        // not transferable for MOVE nor COPY
        exDnD.setDraggedTransferable(null, true);
        exDnD.setDraggedTransferable(null, false);

        // no nodes are dragged
        exDnD.setDraggedNodes(null);

        // no drop candidate
        // TODO check this
        //NodeRenderer.dragExit();

        // no more active
        exDnD.setDnDActive(false);
    }

    /** Activates or deactivates Drag support on asociated JTree
    * component
    * @param active true if the support should be active, false
    * otherwise
    */
    public void activate(boolean active) {
        if (this.active == active) {
            return;
        }

        this.active = active;

        DragGestureRecognizer dgr = getDefaultGestureRecognizer();

        if (active) {
            dgr.setSourceActions(getAllowedDragActions());

            try {
                dgr.removeDragGestureListener(this);
                dgr.addDragGestureListener(this);
            } catch (TooManyListenersException exc) {
                throw new IllegalStateException("Too many listeners for drag gesture."); // NOI18N
            }
        } else {
            dgr.removeDragGestureListener(this);
        }
    }

    /** Safe getter for default gesture<br>
    * (creates the gesture when called for the first time)
    */
    DragGestureRecognizer getDefaultGestureRecognizer() {
        if (defaultGesture == null) {
            DragSource ds = DragSource.getDefaultDragSource();
            defaultGesture = ds.createDefaultDragGestureRecognizer(comp, getAllowedDragActions(), this);
        }

        return defaultGesture;
    }

    /** Utility method. Returns either selected nodes in tree
    * (if cursor hotspot is above some selected node) or the node
    * the cursor points to.
    * @return Node array or null if position of the cursor points
    * to no node.
    */
    abstract Node[] obtainNodes(DragGestureEvent dge);
}
 /* end class ExplorerDragSupport */
