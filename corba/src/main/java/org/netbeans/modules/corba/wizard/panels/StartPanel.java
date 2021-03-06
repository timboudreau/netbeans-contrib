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

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.openide.*;
import org.openide.util.*;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import org.netbeans.modules.corba.wizard.CorbaWizardData;

/**
 *
 * @author  tzezula
 * @version
 */
public class StartPanel extends AbstractCORBAWizardPanel implements javax.swing.event.ChangeListener {

    private CorbaWizardData data;

    /** Creates new form StartPanel */
    public StartPanel() {
        initComponents ();
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(1));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_TypeOfApp"));
        this.impl.setMnemonic (this.bundle.getString("TXT_GenerateImpl_MNE").charAt(0));
        this.tie.setMnemonic (this.bundle.getString("TXT_TieImpl_MNE").charAt (0));
        this.client.setMnemonic (this.bundle.getString("TXT_Client_MNE").charAt(0));
        this.server.setMnemonic (this.bundle.getString("TXT_Server_MNE").charAt(0));
        this.cbClient.setMnemonic (this.bundle.getString("TXT_CallBackClient_MNE").charAt(0));
        this.impl.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_GenerateImpl"));
        this.tie.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_TieImpl"));
        this.client.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_Client"));
        this.server.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_Server"));
        this.cbClient.getAccessibleContext().setAccessibleDescription (this.bundle.getString ("AD_CallBackClient"));
        this.getAccessibleContext().setAccessibleDescription(this.bundle.getString("AD_StartPanel"));
        this.tie.addChangeListener(this);
        this.impl.addChangeListener (this);
        this.client.addChangeListener (this);
        this.server.addChangeListener (this);
        this.cbClient.addChangeListener (this);
    }
  
    /** Sets the data in panel by  data from CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void readCorbaSettings (CorbaWizardData data){
        int mask = data.getGenerate();
        if ((mask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER) 
            this.server.setSelected (true); 
        if ((mask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT)
            this.client.setSelected (true);
        if ((mask & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT)
            this.cbClient.setSelected (true);
        if ((mask & CorbaWizardData.IMPL) == CorbaWizardData.IMPL)
            this.impl.setSelected (true);
        this.tie.setSelected (data.getTie());
        this.data = data;
    }
  
    /** Sets the data got from the panel to CorbaWizardDescriptor
     *  @param CorbaWizardDescriptor data
     */
    public void storeCorbaSettings (CorbaWizardData data){
        int mask = (this.impl.isSelected()? CorbaWizardData.IMPL : 0)
            | (this.client.isSelected() ? CorbaWizardData.CLIENT : 0)
            | (this.cbClient.isSelected() ? CorbaWizardData.CB_CLIENT : 0)
            | (this.server.isSelected() ? CorbaWizardData.SERVER : 0);
        data.setGenerate (mask);
        data.setTie (this.tie.isSelected());
    }

    public boolean isValid () {
        return (this.client.isSelected() || this.cbClient.isSelected() || this.server.isSelected() || this.impl.isSelected()) ? true : false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        impl = new javax.swing.JCheckBox();
        tie = new javax.swing.JCheckBox();
        client = new javax.swing.JCheckBox();
        server = new javax.swing.JCheckBox();
        cbClient = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        impl.setText(bundle.getString("TXT_GenerateImpl"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel1.add(impl, gridBagConstraints);

        tie.setText(bundle.getString("TXT_TieImpl"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 32, 4, 8);
        jPanel1.add(tie, gridBagConstraints);

        client.setText(bundle.getString("TXT_Client"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel1.add(client, gridBagConstraints);

        server.setText(bundle.getString("TXT_Server"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel1.add(server, gridBagConstraints);

        cbClient.setText(bundle.getString("TXT_CallBackClient"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel1.add(cbClient, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 12);
        add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

    }//GEN-END:initComponents

    public void stateChanged(javax.swing.event.ChangeEvent event) {
        if (event.getSource() == this.impl) {
            if (!this.impl.isSelected())
                this.tie.setSelected (false);
        }
        else if (event.getSource() == this.tie) {
            if (this.tie.isSelected())
                this.impl.setSelected(true);
        }
        else if (event.getSource() == this.client || event.getSource() == this.cbClient || event.getSource() == this.server) {
            /** Uffff, but has to be done due to design bug in OpenAPI
             *  the storeSettings is called after next panel.
             */
            int mask = (this.impl.isSelected()? CorbaWizardData.IMPL : 0)
                | (this.client.isSelected() ? CorbaWizardData.CLIENT : 0)
                | (this.cbClient.isSelected() ? CorbaWizardData.CB_CLIENT : 0)
                | (this.server.isSelected() ? CorbaWizardData.SERVER : 0);
            this.data.setGenerate (mask);
        }
        this.fireChange(this);
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox impl;
    private javax.swing.JCheckBox tie;
    private javax.swing.JCheckBox cbClient;
    private javax.swing.JCheckBox client;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox server;
    // End of variables declaration//GEN-END:variables

    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");    

}
