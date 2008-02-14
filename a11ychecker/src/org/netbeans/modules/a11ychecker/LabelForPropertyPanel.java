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

package org.netbeans.modules.a11ychecker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.netbeans.modules.form.RADComponent;

/**
 * Panel used for selecting LabelFor property
 * @author  Max Sauer
 * @author Martin Novak
 */
public class LabelForPropertyPanel extends javax.swing.JPanel {
    private List<RADComponent> compList;
    private LinkedList<String> compNames;
    
    /** Creates new form LabelForPropertyPanel */
    public LabelForPropertyPanel(LinkedList<RADComponent> compList) {
	this.compList = compList;
	compNames = new LinkedList<String>();
//        we know it contains only valid components, so we simply add them all
	Iterator<RADComponent> it = compList.iterator();
	while(it.hasNext()) {
	    RADComponent comp = it.next();
		compNames.add(comp.getName());
	}        
	initComponents();
	labelForCombo.setModel(new javax.swing.DefaultComboBoxModel(compNames.toArray()));
    }
    
    /** Creates new form LabelForPropertyPanel */
    public LabelForPropertyPanel(List<RADComponent> compList) {
	this.compList = compList;
	compNames = new LinkedList<String>();
	setCompNames();
	initComponents();
	labelForCombo.setModel(new javax.swing.DefaultComboBoxModel(compNames.toArray()));
//        labelForCombo.getModel().setSelectedItem(???);
    }
    
    /**
     * Provides selected component from combo
     * @return currently selected component 
     */ 
    public RADComponent getSelectedComponent() {
	Iterator<RADComponent> it = compList.iterator();
	while(it.hasNext()) {
	    RADComponent comp = it.next();
	    if(comp.getName().equals(labelForCombo.getSelectedItem().toString()))
		return comp;
	}
	return null;
    }
    
    /**
     * Provides selected component name as string
     * @return the name
     */ 
    public String getSelectedComponentName() {
	return labelForCombo.getSelectedItem().toString();
    }
    
    /**
     * Set up components name list, which will be displayed inside combo
     */ 
    private void setCompNames() {
	Iterator<RADComponent> it = compList.iterator();
	while(it.hasNext()) {
	    RADComponent comp = it.next();
            Class bc=comp.getBeanClass();
	    if(!bc.equals(JLabel.class) && !bc.equals(JFrame.class) && !bc.equals(JMenuBar.class)
                    && !bc.equals(JMenu.class) && !bc.equals(JMenuItem.class)
                    && !bc.equals(JRadioButtonMenuItem.class) && !bc.equals(JCheckBoxMenuItem.class))
		compNames.add(comp.getName());
	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        comboLabel = new javax.swing.JLabel();
        labelForCombo = new javax.swing.JComboBox();

        comboLabel.setDisplayedMnemonic('c');
        comboLabel.setLabelFor(labelForCombo);
        comboLabel.setText(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "jLabel1.text_3")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(comboLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labelForCombo, 0, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comboLabel)
                    .add(labelForCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        comboLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.comboLabel.AccessibleContext.accessibleName")); // NOI18N
        comboLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.comboLabel.AccessibleContext.accessibleDescription")); // NOI18N
        labelForCombo.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.labelForCombo.AccessibleContext.accessibleName")); // NOI18N
        labelForCombo.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.labelForCombo.AccessibleContext.accessibleDescription")); // NOI18N

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.AccessibleContext.accessibleName")); // NOI18N
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(LabelForPropertyPanel.class, "LabelForPropertyPanel.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel comboLabel;
    private javax.swing.JComboBox labelForCombo;
    // End of variables declaration//GEN-END:variables
    
}
