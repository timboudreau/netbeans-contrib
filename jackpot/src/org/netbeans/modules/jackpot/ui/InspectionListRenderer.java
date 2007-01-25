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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.netbeans.modules.jackpot.Inspection;
import org.netbeans.modules.jackpot.QuerySet;
import org.openide.awt.HtmlRenderer;

class InspectionListRenderer extends JPanel implements ListCellRenderer {
    private RefactoringManagerPanel mgrPanel;
    private JList list;
    private JCheckBox check = new JCheckBox();
    private HtmlRenderer.Renderer renderer = HtmlRenderer.createRenderer();
    
    /** The components returned by HtmlRenderer.Renderer.getTreeCellRendererComponent() */
    private Component inspectorDisplayer = new JLabel(" ");
    private Component transformerDisplayer = new JLabel(" "); //NOI18N
    
    public InspectionListRenderer(RefactoringManagerPanel panel) {
        setLayout(new RendererLayout());
        mgrPanel = panel;
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.list = list;
        Inspection inspection = (Inspection) value;
        /*
        inspectorDisplayer = 
            renderer.getListCellRendererComponent(list, inspection.getInspector(), index, isSelected, cellHasFocus);
        transformerDisplayer = 
            renderer.getListCellRendererComponent(list, inspection.getTransformer(), index, isSelected, cellHasFocus);
         */
        inspectorDisplayer = new JLabel(inspection.getInspector());
        transformerDisplayer = new JLabel(inspection.getTransformer());

        Color c = isSelected ? list.getSelectionBackground() : list.getBackground();
        check.setBackground(c);
        inspectorDisplayer.setBackground(c);
        transformerDisplayer.setBackground (c);
        
        c = isSelected ? list.getSelectionForeground() : list.getForeground();
        check.setForeground(c);
        inspectorDisplayer.setForeground(c);
        transformerDisplayer.setForeground (c);
        
        // select inspection if it is part of query set
        QuerySet querySet = mgrPanel.getSelectedQuerySet();
        boolean inSet = querySet.indexOf(inspection) >= 0;
        check.setSelected(inSet);

        removeAll();
        add(check);
        add(inspectorDisplayer);
        add(transformerDisplayer);
        return this;
    }
    
    class RendererLayout implements LayoutManager {
        public Dimension preferredLayoutSize(Container parent) {
            Dimension dimCheck = check.getPreferredSize();
            Dimension dimInsp = inspectorDisplayer.getPreferredSize();
            int w = dimCheck.width + dimInsp.width +
                    transformerDisplayer.getPreferredSize().width;
            java.awt.FontMetrics fm = list.getFontMetrics(parent.getFont());
            int h = Math.max(dimCheck.height, fm.getHeight());
            return new Dimension(w, h);
        }

        public Dimension minimumLayoutSize(Container parent) {
            Dimension dim = check.getPreferredSize();
            java.awt.FontMetrics fm = list.getFontMetrics(parent.getFont());
            int h = Math.max(dim.height, fm.getHeight());
            return new Dimension(240, h); // minimum width from form editor layout
        }

        public void layoutContainer(Container parent) {
            int left = 0;
            int right = list.getPreferredScrollableViewportSize().width;
            
            // center checkbox vertically in relation to renderers
            Dimension dimCheck = check.getPreferredSize();
            int checkHeight = dimCheck.height;
            int textHeight = inspectorDisplayer.getPreferredSize().height;
            int y_check = 0;
            int y_label = 0;
            if (checkHeight < textHeight)
                y_check = (textHeight - checkHeight) / 2;
            else
                y_label = (checkHeight - textHeight) / 2;
            check.setBounds(left, y_check, check.getWidth(), checkHeight);

            left += dimCheck.width + HGAP;
            int inspectorWidth = (right - left - HGAP) / 2;
            inspectorDisplayer.setBounds(left, y_label, inspectorWidth, textHeight);

            left += inspectorWidth + HGAP;
            transformerDisplayer.setBounds(left, y_label, right - left, textHeight);
        }
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
    }
    
    private static final int HGAP = 2;
}
