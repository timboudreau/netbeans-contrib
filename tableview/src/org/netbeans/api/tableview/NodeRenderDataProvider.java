/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.api.tableview;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RenderDataProvider;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;

/**
 *
 * @author David Strupl
 */
class NodeRenderDataProvider implements RenderDataProvider {
    
    private JTable table;
    
    /** Creates a new instance of NodeRenderDataProvider */
    public NodeRenderDataProvider(JTable table) {
        this.table = table;
    }

    public java.awt.Color getBackground(Object o) {
        return table.getBackground();
    }

    public String getDisplayName(Object o) {
        Node n = Visualizer.findNode(o);
        if (n == null) {
            throw new IllegalStateException("TreeNode must be VisualizerNode but was: " + o + " of class " + o.getClass().getName());
        }
        return n.getDisplayName();
    }

    public java.awt.Color getForeground(Object o) {
        return table.getForeground();
    }

    public javax.swing.Icon getIcon(Object o) {
        Node n = Visualizer.findNode(o);
        if (n == null) {
            throw new IllegalStateException("TreeNode must be VisualizerNode but was: " + o + " of class " + o.getClass().getName());
        }
        boolean expanded = false;
        if (o instanceof TreeNode) {
            TreeNode tn = (TreeNode)o;
            ArrayList al = new ArrayList();
            while (tn != null) {
                al.add(tn);
                tn = tn.getParent();
            }
            Collections.reverse(al);
            TreePath tp = new TreePath(al.toArray());
            AbstractLayoutCache layout = ((Outline)table).getLayoutCache();
            expanded = layout.isExpanded(tp);
        }
        java.awt.Image image = null;
        if (expanded) {
            image = n.getOpenedIcon(java.beans.BeanInfo.ICON_COLOR_16x16);
        } else {
            image = n.getIcon(java.beans.BeanInfo.ICON_COLOR_16x16);
        }
        return new ImageIcon(image);
    }

    public String getTooltipText(Object o) {
        Node n = Visualizer.findNode(o);
        if (n == null) {
            throw new IllegalStateException("TreeNode must be VisualizerNode but was: " + o + " of class " + o.getClass().getName());
        }
        return n.getShortDescription();
    }

    public boolean isHtmlDisplayName(Object o) {
        return false;
    }
    
}
