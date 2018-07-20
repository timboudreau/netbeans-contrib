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

package org.netbeans.modules.corba.wizard.panels;

import org.netbeans.modules.corba.wizard.IDLWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.openide.loaders.TemplateWizard;

/**
 *
 * @author  tzezula
 *
 */
public class IDLFinishPanel extends AbstractIDLWizardPanel implements javax.swing.event.ChangeListener {

    private IDLWizardData data;

    /** Creates new form IDLFInishPanel */
    public IDLFinishPanel(IDLWizardData data) {
        this.data = data;
        initComponents();
        this.jTextArea1.setBackground ( this.getBackground ());
        this.jTextArea1.setForeground (this.getForeground());
        this.jTextArea1.setLineWrap (true);
        this.jTextArea1.setWrapStyleWord (true);
        this.jTextArea1.setEnabled (false);
        this.jCheckBox1.addChangeListener (this);
        this.setName(bundle.getString("TXT_FinishIDL"));
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(3));
        this.jCheckBox1.setMnemonic (this.bundle.getString("TXT_ContinueCORBAWizard_MNE").charAt(0));
        this.jCheckBox1.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_ContinueCORBAWizard"));
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_IDLFinishPanel"));
    }
    
    public void readIDLSettings (TemplateWizard data) {
        if (this.data.getIdlSource()==null) {
            this.jTextArea1.setText (bundle.getString("TXT_Exception"));
            this.getAccessibleContext().setAccessibleDescription (this.getAccessibleContext().getAccessibleDescription()+bundle.getString("TXT_Exception"));
        }
        else {
            try {
                String file_name = this.data.getWizard().getTargetName();
                if (file_name == null || file_name.length() == 0)
                    file_name = bundle.getString("TXT_Requested");
                String package_name = this.data.getWizard().getTargetFolder().getName();
                if (package_name != null && package_name.length() > 0)
                    package_name = " " + java.text.MessageFormat.format(bundle.getString("TXT_InPackage"), new Object[] {package_name});
                    String message = java.text.MessageFormat.format (bundle.getString ("TXT_IDLDoneMessage"),new Object[]{file_name,package_name});
                this.jTextArea1.setText (message);
                this.getAccessibleContext().setAccessibleDescription (this.getAccessibleContext().getAccessibleDescription()+message);
            }catch (java.io.IOException ioe) {}
        }
    }
    
    public void storeIDLSettings (TemplateWizard data) {
    }
    
    public boolean isValid () {
        return this.data.getIdlSource()!=null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jCheckBox1 = new javax.swing.JCheckBox();
        jTextArea1 = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 300));
        jCheckBox1.setText(bundle.getString("TXT_ContinueCORBAWizard"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        add(jCheckBox1, gridBagConstraints);

        jTextArea1.setEditable(false);
        jTextArea1.setDisabledTextColor((java.awt.Color) javax.swing.UIManager.getDefaults().get("Label.foreground"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jTextArea1, gridBagConstraints);

    }//GEN-END:initComponents

    public void stateChanged(javax.swing.event.ChangeEvent event) {
            this.data.continueCorbaWizard (this.jCheckBox1.isSelected());
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    

}
