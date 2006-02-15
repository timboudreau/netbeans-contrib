/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UTUtils;

/**
 * Parts of this class were copied from the Linux JDK 1.4
 * BasicDragGestureRecognizer and BasicTableUI.
 *
 * Drag gesture recognizer for TreeTable. Default swing drag gesture
 * recognizer also starts a DnD operation if the user pressed his mouse
 * over ther JTree's toggle. This makes it impossible to collapse/expand
 * selected nodes.
 *
 * @author tl
 */
public class TreeTableDragGestureRecognizer implements MouseListener, 
        MouseMotionListener {
    /**
     * Enables Drag&Drop for a TreeTable
     *
     * @param tt a TreeTable
     */
    public static void enableDnD(TreeTable tt) {
        TreeTableDragGestureRecognizer l = new TreeTableDragGestureRecognizer();
        tt.addMouseListener(l);
        tt.addMouseMotionListener(l);
    }
    
    private MouseEvent dndArmedEvent = null;
    
    private static int motionThreshold;
    
    private static boolean checkedMotionThreshold = false;

    private static int getMotionThreshold() {
        if (checkedMotionThreshold) {
            return motionThreshold;
        } else {
            checkedMotionThreshold = true;
            try {
                motionThreshold = ((Integer)Toolkit.getDefaultToolkit().
                        getDesktopProperty(
                        "DnD.gestureMotionThreshold")).intValue();
            } catch (Exception e) {
                motionThreshold = 5;
            }
        }
        return motionThreshold;
    }
    
    protected int mapDragOperationFromModifiers(MouseEvent e) {
        int mods = e.getModifiersEx();
        
        if ((mods & InputEvent.BUTTON1_DOWN_MASK) != 
                InputEvent.BUTTON1_DOWN_MASK) {
            return TransferHandler.NONE;
        }

        JComponent c = getComponent(e);
        TransferHandler th = c.getTransferHandler();
        return convertModifiersToDropAction(mods, th.getSourceActions(c)); 
    }
    
    /**
     * Many thanks to JReversePro
     *
     * sun.awt.dnd.SunDragSourceContextPeer
     *
     * @param modifiers keyboard modifiers
     * @param sourceActions available source actions
     */
    private static int convertModifiersToDropAction(int modifiers, 
            int sourceActions)
    {
         int k = 0;
         switch (modifiers & (InputEvent.SHIFT_DOWN_MASK |
                 InputEvent.CTRL_DOWN_MASK)) {
              case InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                   k = DnDConstants.ACTION_LINK;
                   break;
              case InputEvent.CTRL_DOWN_MASK:
                   k = DnDConstants.ACTION_COPY;
                   break;
              case InputEvent.SHIFT_DOWN_MASK:
                   k = DnDConstants.ACTION_MOVE;
                   break;
              // without a modifier
              default:
                   if ((sourceActions & DnDConstants.ACTION_MOVE) != 0) {
                        k = DnDConstants.ACTION_MOVE;
                        break;
                   }
                   else if ((sourceActions & DnDConstants.ACTION_COPY) != 0) {
                        k = DnDConstants.ACTION_COPY;
                        break;
                   }
                   else if ((sourceActions & DnDConstants.ACTION_LINK) != 0)
                        k = DnDConstants.ACTION_LINK;


         }
         return (k & sourceActions);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        dndArmedEvent = null;

	if (isDragPossible(e) && mapDragOperationFromModifiers(e) != 
                TransferHandler.NONE) {
            dndArmedEvent = e;
	    e.consume();
	}
    }
    
    public void mouseReleased(MouseEvent e) {
        dndArmedEvent = null;
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (dndArmedEvent != null) {
            e.consume();
            
            int action = mapDragOperationFromModifiers(e);
            
            if (action == TransferHandler.NONE) {
                return;
            }
            
	    int dx = Math.abs(e.getX() - dndArmedEvent.getX());
	    int dy = Math.abs(e.getY() - dndArmedEvent.getY());
            if ((dx > getMotionThreshold()) || (dy > getMotionThreshold())) {
		// start transfer... shouldn't be a click at this point
                JComponent c = getComponent(e);
		TransferHandler th = c.getTransferHandler();
		th.exportAsDrag(c, dndArmedEvent, action);
		dndArmedEvent = null;
	    }
	}
    }
    
    public void mouseMoved(MouseEvent e) {
    }
    
    private TransferHandler getTransferHandler(MouseEvent e) {
        JComponent c = getComponent(e);
        return c == null ? null : c.getTransferHandler();
    }
    
    /**
     * Determines if the following are true:
     * <ul>
     * <li>the press event is located over a selection
     * <li>the dragEnabled property is true
     * <li>A TranferHandler is installed
     * </ul>
     * <p>
     * This is implemented to perform the superclass behavior
     * followed by a check if the dragEnabled
     * property is set and if the location picked is selected.
     */
    protected boolean isDragPossible(MouseEvent e) {
        JComponent c = getComponent(e);
        if ((c == null) ? true : (c.getTransferHandler() != null)) {
            TreeTable table = (TreeTable) this.getComponent(e);
            //if (table.getDragEnabled()) {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int column = table.columnAtPoint(p);
                if ((column != -1) && (row != -1) && 
                        table.isCellSelected(row, column)) {
                    if (table.isTreeColumn(column)) {
                        TreePath tp = table.getTree().getPathForLocation(
                                e.getX() - 
                                table.getCellRect(0, column, true).x, 
                                e.getY());
                        UTUtils.LOGGER.fine(tp + "");
                        return tp != null;
                    } else {
                        return true;
                    }
                }
            //}
        }
        return false;
    }

    protected JComponent getComponent(MouseEvent e) {
	Object src = e.getSource();
	if (src instanceof JComponent) {
	    JComponent c = (JComponent) src;
	    return c;
	}
	return null;
    }

}
