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
 *
 * Contributor(s): Thomas Ball
 */

package org.netbeans.modules.metrics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;
import org.netbeans.modules.metrics.ApproveMetricsAction.PanelLine;

/**
 * Panel to display list of approvals.
 */
public class ApproveMetricsPanel extends javax.swing.JPanel {
    List approvals;

    public ApproveMetricsPanel(List l) {
        this.approvals = l;
        initComponents();
    }
    
    String getComment() {
        return commentText.getText();
    }
    
    private class ApprovalList extends JList implements ListSelectionListener {
        public ApprovalList() {
            super();
            setModel(new AbstractListModel() {
                public Object getElementAt(int index) {
                    return approvals.get(index);
                }
                public int getSize() {
                    return approvals.size();
                }
            });
            setCellRenderer(new ApprovalListRenderer());
            getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            addListSelectionListener(this);
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int index = getSelectionModel().getMinSelectionIndex();
                PanelLine pl = (PanelLine)getModel().getElementAt(index);
                pl.checked = !pl.checked;
            }
        }
    }
    
    private static class ApprovalListRenderer extends JComponent implements ListCellRenderer {
        private JCheckBox checkBox = new JCheckBox();
        private JLabel name = new JLabel();
        private JLabel metric = new JLabel();

        private static GridBagConstraints checkBoxConstraints, nameConstraints, metricConstraints;
        static {
            checkBoxConstraints = new GridBagConstraints();
            checkBoxConstraints.insets = new Insets(0, 0, 0, 5);
            checkBoxConstraints.anchor = GridBagConstraints.WEST;
            checkBoxConstraints.gridx = 0;

            nameConstraints = new GridBagConstraints();
            nameConstraints.anchor = GridBagConstraints.WEST;
            nameConstraints.weightx = 1.0;
            nameConstraints.gridx = 1;

            metricConstraints = new GridBagConstraints();
            metricConstraints.anchor = GridBagConstraints.WEST;
            metricConstraints.insets = new Insets(0, 5, 0, 0);
            metricConstraints.gridx = 2;
        }

        // from Swing Hacks #16
        private static Color listForeground, listBackground;
        static {
            UIDefaults uid = UIManager.getLookAndFeelDefaults();
            listForeground = uid.getColor("List.foreground");
            listBackground = uid.getColor("List.background");
        }
        
        public ApprovalListRenderer() {
            super();
            setLayout(new GridBagLayout());
            add(checkBox, checkBoxConstraints);
            add(name, nameConstraints);
            metric.setText("MMM=9999");
            Dimension d = metric.getMinimumSize();
            metric.setMinimumSize(d);
            metric.setPreferredSize(d);
            add(metric, metricConstraints);
        }
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            PanelLine pl = (PanelLine)value;
            checkBox.setSelected(pl.checked);
            name.setText(pl.nodeName);
            metric.setText(pl.metricName + '=' + pl.value);
            Component[] comps = getComponents();
            for (int i = 0; i < comps.length; i++) {
                comps[i].setForeground(listForeground);
                comps[i].setBackground(listBackground);
            }
            return this;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        commentText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        approvalsList = new ApprovalList();

        jLabel1.setText("Metrics:");

        jLabel2.setText(java.util.ResourceBundle.getBundle("org/netbeans/modules/metrics/Bundle").getString("STR_Comment"));

        jScrollPane1.setViewportView(approvalsList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane1)
                    .add(commentText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(commentText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList approvalsList;
    private javax.swing.JTextField commentText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
