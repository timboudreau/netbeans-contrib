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

package org.netbeans.modules.groovy.groovyproject.ui.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.NbBundle;

/**
 *
 * @author  phrebejk
 */
public class PanelOptionsVisual extends SettingsPanel implements ActionListener, PropertyChangeListener {
    
    private static boolean lastMainScriptCheck = true; // XXX Store somewhere
    
    private PanelConfigureProject panel;
    private boolean valid;
    
    /** Creates new form PanelOptionsVisual */
    public PanelOptionsVisual( PanelConfigureProject panel, int type ) {
        initComponents();
        this.panel = panel;

        switch (type) {
            case NewGroovyProjectWizardIterator.TYPE_LIB:
                setAsMainCheckBox.setVisible( false );
                createMainCheckBox.setVisible( false );
                mainScriptTextField.setVisible( false );
                break;
            case NewGroovyProjectWizardIterator.TYPE_APP:
                createMainCheckBox.addActionListener( this );
                createMainCheckBox.setSelected( lastMainScriptCheck );
                mainScriptTextField.setEnabled( lastMainScriptCheck );
                break;
        }
        
        this.mainScriptTextField.getDocument().addDocumentListener( new DocumentListener () {
            
            public void insertUpdate(DocumentEvent e) {
                mainScriptChanged ();
            }
            
            public void removeUpdate(DocumentEvent e) {
                mainScriptChanged ();
            }
            
            public void changedUpdate(DocumentEvent e) {
                mainScriptChanged ();
            }
            
        });
    }

    public void actionPerformed( ActionEvent e ) {        
        if ( e.getSource() == createMainCheckBox ) {
            lastMainScriptCheck = createMainCheckBox.isSelected();
            mainScriptTextField.setEnabled( lastMainScriptCheck );        
            this.panel.fireChangeEvent();
        }                
    }
    
    public void propertyChange (PropertyChangeEvent event) {
        if (PanelProjectLocationVisual.PROP_PROJECT_NAME.equals(event.getPropertyName())) {
            String newProjectName = NewGroovyProjectWizardIterator.getPackageName((String) event.getNewValue());
            this.mainScriptTextField.setText (MessageFormat.format(
                NbBundle.getMessage (PanelOptionsVisual.class,"TXT_ScriptName"), new Object[] {newProjectName}
            ));
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        setAsMainCheckBox = new javax.swing.JCheckBox();
        createMainCheckBox = new javax.swing.JCheckBox();
        mainScriptTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSN_PanelOptionsVisual"));
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(PanelOptionsVisual.class, "ACSD_PanelOptionsVisual"));
        setAsMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(setAsMainCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_setAsMainCheckBox"));
        setAsMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(setAsMainCheckBox, gridBagConstraints);
        setAsMainCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSN_setAsMainCheckBox"));
        setAsMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSD_setAsMainCheckBox"));

        createMainCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createMainCheckBox, org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("LBL_createMainCheckBox"));
        createMainCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(createMainCheckBox, gridBagConstraints);
        createMainCheckBox.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSN_createMainCheckBox"));
        createMainCheckBox.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSD_createMainCheckBox"));

        mainScriptTextField.setText("com.myapp.Main");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(mainScriptTextField, gridBagConstraints);
        mainScriptTextField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ASCN_mainScriptTextFiled"));
        mainScriptTextField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ASCD_mainScriptTextFiled"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);
        jPanel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ACSN_jPanel1"));
        jPanel1.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(PanelOptionsVisual.class).getString("ASCD_jPanel1"));

    }//GEN-END:initComponents
    
    boolean valid(WizardDescriptor settings) {
        if (mainScriptTextField.isVisible () && mainScriptTextField.isEnabled ()) {
            if (!valid) {
                settings.putProperty( "WizardPanel_errorMessage", // NOI18N
                    NbBundle.getMessage(PanelOptionsVisual.class,"ERROR_IllegalMainScriptName")); //NOI18N
            }
            return this.valid;
        }
        else {
            return true;
        }
    }
    
    void read (WizardDescriptor d) {
        //TODO:
    }
    
    void validate (WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    void store( WizardDescriptor d ) {
        d.putProperty( /*XXX Define somewhere */ "setAsMain", setAsMainCheckBox.isSelected() && setAsMainCheckBox.isVisible() ? Boolean.TRUE : Boolean.FALSE ); // NOI18N
        d.putProperty( /*XXX Define somewhere */ "mainScript", createMainCheckBox.isSelected() && createMainCheckBox.isVisible() ? mainScriptTextField.getText() : null ); // NOI18N
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox createMainCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField mainScriptTextField;
    private javax.swing.JCheckBox setAsMainCheckBox;
    // End of variables declaration//GEN-END:variables
    
    private void mainScriptChanged () {
        String mainScriptName = this.mainScriptTextField.getText ();
        this.valid = mainScriptName.length() > 0;
        this.panel.fireChangeEvent();
    }
}

