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
import org.openide.util.WeakSet;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants; // TEMP

import java.util.Iterator;

import javax.swing.JScrollPane;


/**
 * Manager for explorer DnD.
 *
 *
 * @author  Jiri Rechtacek
 *
 * @see TreeViewDragSupport
 * @see TreeViewDropSupport
 */
final class ExplorerDnDManager {
    /** Singleton instance of explorer dnd manager. */
    private static ExplorerDnDManager defaultDnDManager;
    private Node[] draggedNodes;
    private Transferable draggedTransForCut;
    private Transferable draggedTransForCopy;
    private boolean isDnDActive = false;
    private int nodeAllowed = 0;
    private Cursor cursor = null;
    private transient WeakSet setOfTargets;

    /** Creates a new instance of <code>WindowsDnDManager</code>. */
    private ExplorerDnDManager() {
    }

    /** Gets the singleton instance of this window dnd manager. */
    static synchronized ExplorerDnDManager getDefault() {
        if (defaultDnDManager == null) {
            defaultDnDManager = new ExplorerDnDManager();
        }

        return defaultDnDManager;
    }

    void setDraggedNodes(Node[] n) {
        draggedNodes = n;
    }

    Node[] getDraggedNodes() {
        return draggedNodes;
    }

    void setDraggedTransferable(Transferable trans, boolean isCut) {
        if (isCut) {
            draggedTransForCut = trans;
        } else {
            draggedTransForCopy = trans;
        }
    }

    Transferable getDraggedTransferable(boolean isCut) {
        if (isCut) {
            return draggedTransForCut;
        }

        // only for copy
        return draggedTransForCopy;
    }

    void setNodeAllowedActions(int actions) {
        nodeAllowed = actions;
    }

    final int getNodeAllowedActions() {
        return nodeAllowed;
    }

    void setDnDActive(boolean state) {
        isDnDActive = state;

        if ((setOfTargets != null) && !setOfTargets.isEmpty()) {
            Iterator it = setOfTargets.iterator();

            while (it.hasNext()) {
                JScrollPane pane = (JScrollPane) it.next();

                if (pane.isEnabled()) {
                    if (pane instanceof OutlineView) {
                        ((OutlineView) pane).setDropTarget(state);
                    } else if (pane instanceof TableView) {
                        ((TableView) pane).setDropTarget(state);
                    }
                }
            }
        }
    }

    boolean isDnDActive() {
        return isDnDActive;
    }

    void addFutureDropTarget(JScrollPane view) {
        if (setOfTargets == null) {
            setOfTargets = new WeakSet();
        }

        setOfTargets.add(view);
    }

    int getAdjustedDropAction(int action, int allowed) {
        int possibleAction = action;

        if ((possibleAction & allowed) == 0) {
            possibleAction = allowed;
        }

        if ((possibleAction & nodeAllowed) == 0) {
            possibleAction = nodeAllowed;
        }

        return possibleAction;
    }

    void prepareCursor(Cursor c) {
        this.cursor = c;
    }

    Cursor getCursor() {
        return this.cursor;
    }
}
