/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * Copied from refactoring module's ResultNodeListener.
 */
class ResultNodeListener implements MouseListener, KeyListener {
    private final boolean isTransformer;
    
    public ResultNodeListener(boolean isTransformer) {
        this.isTransformer = isTransformer;
    }

    public void mouseClicked(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        Point p = e.getPoint();
        int x = e.getX();
        int y = e.getY();
        int row = tree.getRowForLocation(x, y);
        TreePath path = tree.getPathForRow(row);

        if (path != null) {
            ResultNode node = (ResultNode) path.getLastPathComponent();
            if (isTransformer && e.getClickCount() == 1) {
                Rectangle chRect = ResultRenderer.Delegate.getCheckBoxRectangle();
                Rectangle rowRect = tree.getPathBounds(path);
                chRect.setLocation(chRect.x + rowRect.x, chRect.y + rowRect.y);
                if (chRect.contains(p)) {
                    boolean isSelected = !(node.isSelected());
                    node.setSelected(isSelected);
                    if (node.getSelectionMode() == ResultNode.DIG_IN_SELECTION) {
                        if (isSelected)
                            tree.expandPath(path);
                        else
                            tree.collapsePath(path);
                    }
                    ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
                    if (row == 0) {
                        tree.revalidate();
                        tree.repaint();
                    }
                }
            } 
            if (e.getClickCount() == 2) {
                if (tree.isCollapsed(row))
                    tree.expandRow(row);
                else
                    tree.collapseRow(row);
            }
        }
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
        // Enter key was pressed, find the reference in document
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            JTree tree = (JTree) e.getSource();
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                ResultNode node = (ResultNode) path.getLastPathComponent();
                findInSource(node);
            }
        }
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            JTree tree = (JTree) e.getSource();
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                ResultNode node = (ResultNode) path.getLastPathComponent();
                node.setSelected(!node.isSelected());
                e.consume();
            }
        }
    }
    
    static void findInSource(ResultNode node) {
        Object o = node.getUserObject();
        //FIXME:
    }

} // end ResultNodeListener
