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
/*
 * DefaultOutlineTreeCellRenderer.java
 *
 * Created on January 28, 2004, 7:49 PM
 */

package org.netbeans.swing.outline;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

/**
 *
 * @author  tim
 */
public class DefaultOutlineCellRenderer extends DefaultTableCellRenderer {
    private boolean expanded = false;
    private boolean leaf = true;
    private boolean showHandle = true;
    private boolean hasFocus = false;
    private int nestingDepth = 0;
    private static final Border expansionBorder = new ExpansionHandleBorder();
    
    /** Creates a new instance of DefaultOutlineTreeCellRenderer */
    public DefaultOutlineCellRenderer() {
        setBorder (expansionBorder);
    }
    
    private static Icon getDefaultOpenIcon() {
	return UIManager.getIcon("Tree.openIcon"); //NOI18N
    }

    private static Icon getDefaultClosedIcon() {
	return UIManager.getIcon("Tree.closedIcon"); //NOI18N
    }

    private static Icon getDefaultLeafIcon() {
	return UIManager.getIcon("Tree.leafIcon"); //NOI18N
    }
    
    private static Icon getExpandedIcon() {
        return UIManager.getIcon ("Tree.collapsedIcon"); //NOI18N
    }
    
    private static Icon getCollapsedIcon() {
        return UIManager.getIcon ("Tree.expandedIcon"); //NOI18N
    }
    
    static int getNestingWidth() {
        return getExpansionHandleWidth();
    }
    
    static int getExpansionHandleWidth() {
        return getExpandedIcon().getIconWidth();
    }
    
    static int getExpansionHandleHeight() {
        return getExpandedIcon().getIconHeight();
    }
    
    private void setNestingDepth (int i) {
        nestingDepth = i;
    }
    
    private void setExpanded (boolean val) {
        expanded = val;
    }
    
    private void setLeaf (boolean val) {
        leaf = val;
    }
    
    private void setShowHandle (boolean val) {
        showHandle = val;
    }
    
    private void setHasFocus(boolean val) {
        hasFocus = val;
    }
    
    private boolean isLeaf () {
        return leaf;
    }
    
    private boolean isExpanded () {
        return expanded;
    }
    
    private boolean isShowHandle() {
        return showHandle;
    }
    
    private boolean isHasFocus() {
        return hasFocus;
    }
    
    private int getNestingDepth() {
        return nestingDepth;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, 
                          int column) {
    
        Component c = (DefaultOutlineCellRenderer) super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
        setHasFocus(hasFocus);
        if (column == 0) {
            Outline tbl = (Outline) table;
            AbstractLayoutCache layout = tbl.getLayoutCache();
            
            boolean leaf = tbl.getOutlineModel().isLeaf(value);
            setLeaf(leaf);
            TreePath path = layout.getPathForRow(row);
            boolean expanded = !layout.isExpanded(path);
            setExpanded (expanded);
            setNestingDepth (path.getPathCount() - 1);
            RenderDataProvider rendata = tbl.getRenderDataProvider();
            Icon icon = null;
            if (rendata != null) {
                String displayName = rendata.getDisplayName(value);
                if (displayName != null) {
                    setText (displayName);
                }
                setToolTipText (rendata.getTooltipText(value));
                Color bg = rendata.getBackground(value);
                Color fg = rendata.getForeground(value);
                if (bg != null && !isSelected) {
                    setBackground (bg);
                } else {
                    setBackground (isSelected ? 
                        tbl.getSelectionBackground() : tbl.getBackground());
                }
                if (fg != null && !isSelected) {
                    setForeground (fg);
                } else {
                    setForeground (isSelected ? 
                        tbl.getSelectionForeground() : tbl.getForeground());
                }
                icon = rendata.getIcon(value);
            } 
            if (icon == null) {
                if (!leaf) {
                    if (expanded) {
                        setIcon (getDefaultOpenIcon());
                    } else {
                        setIcon (getDefaultClosedIcon());
                    }
                } else {
                    setIcon (getDefaultLeafIcon());
                }
            }
        
        } else {
            setIcon(null);
            setShowHandle(false);
        }
        setBorder(expansionBorder);
        return this;
    }
    
    private static class ExpansionHandleBorder implements Border {
        private Insets insets = new Insets(0,0,0,0);
        public Insets getBorderInsets(Component c) {
            DefaultOutlineCellRenderer ren = (DefaultOutlineCellRenderer) c;
            if (ren.isShowHandle()) {
                insets.left = getExpansionHandleWidth() + (ren.getNestingDepth() *
                    getNestingWidth());
                //Defensively adjust all the insets fields
                insets.top = 1;
                insets.right = 1;
                insets.bottom = 1;
            } else {
                //Defensively adjust all the insets fields
                insets.left = 1;
                insets.top = 1;
                insets.right = 1;
                insets.bottom = 1;
            }
            return insets;
        }
        
        public boolean isBorderOpaque() {
            return false;
        }
        
        public void paintBorder(Component c, java.awt.Graphics g, int x, int y, int width, int height) {
            DefaultOutlineCellRenderer ren = (DefaultOutlineCellRenderer) c;
            if (ren.isShowHandle() && !ren.isLeaf()) {
                Icon icon = ren.isExpanded() ? getExpandedIcon() : getCollapsedIcon();
                int iconY;
                int iconX = ren.getNestingDepth() * getNestingWidth();
                if (icon.getIconHeight() < height) {
                    iconY = (height / 2) - (icon.getIconHeight() / 2);
                } else {
                    iconY = 0;
                }
                icon.paintIcon(c, g, iconX, iconY);
            }
            if (ren.isHasFocus()) {
                Color color = g.getColor();
                g.setColor (UIManager.getColor("controlShadow")); //NOI18N
                g.drawRect (x, y, width - 1, height - 1);
                g.setColor (color);
            }
        }
        
    }
}
