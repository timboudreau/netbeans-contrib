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
