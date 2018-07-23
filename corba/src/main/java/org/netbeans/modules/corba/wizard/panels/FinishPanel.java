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

import org.netbeans.modules.corba.wizard.CorbaWizardData;
import org.netbeans.modules.corba.wizard.CorbaWizard;
import java.util.ResourceBundle;
import java.text.MessageFormat;

/**
 *
 * @author  Tomas Zezula
 * @version
 */
public class FinishPanel extends  AbstractCORBAWizardPanel {

    /** Creates new form FinishPanel */
    public FinishPanel() {
        initComponents ();
        postInitComponents ();
        putClientProperty(CorbaWizard.PROP_CONTENT_SELECTED_INDEX, new Integer(5));
        this.setName (org.openide.util.NbBundle.getBundle(PackagePanel.class).getString("TXT_CodeGenerator"));
    }
    
    public void readCorbaSettings (CorbaWizardData data) {
        ResourceBundle bundle = ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
        Object[] msgs = new Object[9];
        int genMask = data.getGenerate ();
        msgs[1] = msgs[3] = msgs[5] = msgs[7] = "";
        msgs[0] = bundle.getString ("TXT_ImpIdl");
        byte cnt = 0;
        
        if ((genMask & CorbaWizardData.IMPL) == CorbaWizardData.IMPL) {
            msgs[2]= bundle.getString ("TXT_GenImpl");
            cnt++;
        }
        else {
            msgs[2]="";
        }
        if ((genMask & CorbaWizardData.CLIENT) == CorbaWizardData.CLIENT) {
            msgs[4]= bundle.getString("TXT_GenClient");
            cnt++;
        }
        else {
            msgs[4]="";
        }
        if ((genMask & CorbaWizardData.SERVER) == CorbaWizardData.SERVER) {
            msgs[6]= bundle.getString("TXT_GenServer");
            cnt++;
        }
        else {
            msgs[6]="";
        }
        if ((genMask & CorbaWizardData.CB_CLIENT) == CorbaWizardData.CB_CLIENT) {
            msgs[8]= bundle.getString("TXT_GenCBClient");
            cnt++;
        }
        else {
            msgs[8]="";
        }
        if (cnt == 1)
            msgs[1] = " " + bundle.getString("TXT_AndSeparator") + " ";
        else {
            byte idx = 1;
            while (cnt > 1) {
                if (!msgs[idx + 1].equals("")) {
                    msgs[idx] = bundle.getString("TXT_CommaSeparator") + " ";
                    cnt--;
                }
                idx += 2;
            }
            msgs[idx] = bundle.getString("TXT_CommaSeparator") + " " + bundle.getString("TXT_AndSeparator") + " ";

        }
        String message = MessageFormat.format (bundle.getString("TXT_Generate"),msgs);
        this.jTextArea1.setText(message);
        this.getAccessibleContext().setAccessibleDescription (this.getAccessibleContext().getAccessibleDescription() + message);
    }
    
    public void storeCorbaSettings (CorbaWizardData data) {
    }
    
    private void postInitComponents () {
        this.jTextArea1.setBackground ( this.getBackground ());
        this.jTextArea1.setEditable (false);
        this.jTextArea1.setEnabled (false);
        this.jTextArea1.setDisabledTextColor ((java.awt.Color)javax.swing.UIManager.getDefaults().get ("Label.foreground"));
        this.jTextArea1.setLineWrap (true);
        this.jTextArea1.setWrapStyleWord (true);
        this.jTextArea1.setEditable (false);
        this.getAccessibleContext().setAccessibleDescription (this.bundle.getString("AD_FinishPanel"));
    }
    
    public boolean isValid () {
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(500, 340));
        jTextArea1.setText(bundle.getString("TXT_Generate"));
        jTextArea1.setPreferredSize(new java.awt.Dimension(400, 50));
        jTextArea1.setMinimumSize(new java.awt.Dimension(400, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 6, 12);
        add(jTextArea1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        add(jPanel1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
    private static final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/panels/Bundle");
    
}
