/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

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
