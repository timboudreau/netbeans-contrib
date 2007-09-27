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

package org.netbeans.modules.rmi.activation;

import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.activation.*;
import java.util.*;

import javax.swing.JFileChooser;

import org.openide.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;

/**
 * Custom editor for java.rmi.activation.ActivationGroupDesc.
 * @author  Jan Pokorsky
 * @version 
 */
public class ActivationGroupDescCustomEditor extends javax.swing.JPanel
                                        implements EnhancedCustomPropertyEditor
{

    private static final boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    private static final boolean exceptions = Boolean.getBoolean("netbeans.debug.exceptions"); // NOI18N
    
    private static JFileChooser locationChooser = null;
    private static JFileChooser processChooser = null;
    
    /** Creates new form ActivationGroupDescCustomEditor */
    public ActivationGroupDescCustomEditor() {
        this(null);
    }
    /** Creates new form ActivationGroupDescCustomEditor */
    public ActivationGroupDescCustomEditor(ActivationGroupDesc desc) {
        initComponents ();
        
        internalization();
        this.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor"));

        editorOverrides = PropertyEditorManager.findEditor(java.util.Properties.class);
        editorOptions = PropertyEditorManager.findEditor(String[].class);
        
        // set all edited values
        if (desc == null) {
            jtfClassName.setText(EMPTY_STRING);
            jtfLocation.setText(EMPTY_STRING);
            jtfProcess.setText(EMPTY_STRING);
            editorOptions.setValue(null);
            editorOverrides.setValue(null);
            data = null;
        } else {
            jtfClassName.setText(desc.getClassName());
            jtfLocation.setText(desc.getLocation());
            editorOverrides.setValue(desc.getPropertyOverrides());
            
            ActivationGroupDesc.CommandEnvironment cmd = desc.getCommandEnvironment();
            if (cmd == null) {
                jtfProcess.setText(EMPTY_STRING);
                editorOptions.setValue(null);
            } else {
                jtfProcess.setText(cmd.getCommandPath());
                editorOptions.setValue(cmd.getCommandOptions());
            }
            
            data = desc.getData();
        }
        
        // set custom editors
        custOverridesEditor = findCustomEditor(editorOverrides);
        if ( !(custOverridesEditor instanceof EnhancedCustomPropertyEditor) )
            custOverridesEditor = new javax.swing.JLabel(getString("ERR_CustomEditor")); // NOI18N
        
        // StringArrayEditor does not implement EnhancedCustomPropertyEditor yet.
        custOptionsEditor = findCustomEditor(editorOptions);
        jPanelOverrides.add(custOverridesEditor, java.awt.BorderLayout.CENTER);
        jPanelOptions.add(custOptionsEditor, java.awt.BorderLayout.CENTER);
        
        // set serialized data
        if (data == null) return;
        try {
            deserialized = MarshalledObjectSupport.get(data);
            jtfData.setText(deserialized.getClass().getName());
        } catch (ClassNotFoundException ex) {
            if (exceptions) ex.printStackTrace();
            jtfData.setText(
                NbBundle.getMessage(ActivationGroupDescCustomEditor.class,
                    "ERR_ActivationGroupDescCustom.ClassNotFoundException", // NOI18N
                    ex.getMessage()
                )
            );
            deserialized = null;
        } catch (ThreadDeath ex) {
            throw ex;
        } catch (Throwable ex) {
            if (exceptions) ex.printStackTrace();
            jtfData.setText(getString("ERR_ActivationGroupDescCustom.IOException")); // NOI18N
            jtfData.setToolTipText(ex.getMessage());
            deserialized = null;
        }
    }
    
    private void internalization() {
        jLabel1.setText(getString("LBL_ActivationGroupDescCustom.Process")); // NOI18N
        jLabel1.setDisplayedMnemonic(getMnemonic("LBL_ActivationGroupDescCustom.Process")); // NOI18N
        jLabel1.setLabelFor(jtfProcess);
        jLabel2.setText(getString("LBL_ActivationGroupDescCustom.Properties")); // NOI18N
        jLabel2.setDisplayedMnemonic(getMnemonic("LBL_ActivationGroupDescCustom.Properties")); // NOI18N
        jLabel2.setLabelFor(jPanelOverrides);
        jLabel3.setText(getString("LBL_ActivationGroupDescCustom.Class")); // NOI18N
        jLabel3.setDisplayedMnemonic(getMnemonic("LBL_ActivationGroupDescCustom.Class")); // NOI18N
        jLabel3.setLabelFor(jtfClassName); // NOI18N
        jLabel4.setText(getString("LBL_ActivationGroupDescCustom.Location")); // NOI18N
        jLabel4.setDisplayedMnemonic(getMnemonic("LBL_ActivationGroupDescCustom.Location")); // NOI18N
        jLabel4.setLabelFor(jtfLocation);
        jLabel5.setText(getString("LBL_ActivationGroupDescCustom.Data")); // NOI18N
        jLabel5.setDisplayedMnemonic(getMnemonic("LBL_ActivationGroupDescCustom.Data")); // NOI18N
        jLabel5.setLabelFor(jtfData);
        jButtonClass.setText(getString("LBL_ActivationGroupDescCustom.ClassButton")); // NOI18N
        jButtonLocation.setText(getString("LBL_ActivationGroupDescCustom.LocationButton")); // NOI18N
        jButtonProcess.setText(getString("LBL_ActivationGroupDescCustom.ProcessButton")); // NOI18N
        jButtonData.setText(getString("LBL_ActivationGroupDescCustom.DataButton")); // NOI18N
    }
    
    /** Finds proper custom editor.
     * @param pe property editor.
     * @return proper editor component or label with a notification.
     */
    private java.awt.Component findCustomEditor(PropertyEditor pe) {
        java.awt.Component c = null;
        if (pe != null && pe.supportsCustomEditor())
            c = pe.getCustomEditor();
        if (c == null)
            c = new javax.swing.JLabel(getString("ERR_CustomEditor")); // NOI18N
        return c;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel3 = new javax.swing.JLabel();
        jtfClassName = new javax.swing.JTextField();
        jButtonClass = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jtfLocation = new javax.swing.JTextField();
        jButtonLocation = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtfProcess = new javax.swing.JTextField();
        jButtonProcess = new javax.swing.JButton();
        jPanelOptions = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanelOverrides = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jtfData = new javax.swing.JTextField();
        jButtonData = new javax.swing.JButton();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        jLabel3.setText("Class");
        jLabel3.setLabelFor(jtfClassName);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel3, gridBagConstraints1);
        
        jtfClassName.setColumns(32);
        jtfClassName.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.ClassName"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(jtfClassName, gridBagConstraints1);
        
        jButtonClass.setText("...");
        jButtonClass.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.ClassButton"));
        jButtonClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClassActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(12, 5, 0, 11);
        add(jButtonClass, gridBagConstraints1);
        
        jLabel4.setText("Location");
        jLabel4.setLabelFor(jtfLocation);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(6, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel4, gridBagConstraints1);
        
        jtfLocation.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.Location"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(6, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jtfLocation, gridBagConstraints1);
        
        jButtonLocation.setText("...");
        jButtonLocation.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.LocationButton"));
        jButtonLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocationActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(6, 5, 0, 11);
        add(jButtonLocation, gridBagConstraints1);
        
        jPanel3.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        jPanel3.setBorder(new javax.swing.border.TitledBorder(" " + getString("LBL_ActivationGroupDescCustom.CmdEnv") + " "));
        jLabel1.setText("Process");
        jLabel1.setLabelFor(jtfProcess);
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.insets = new java.awt.Insets(8, 12, 0, 0);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add(jLabel1, gridBagConstraints2);
        
        jtfProcess.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.Process"));
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints2.weightx = 1.0;
        jPanel3.add(jtfProcess, gridBagConstraints2);
        
        jButtonProcess.setText("...");
        jButtonProcess.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.ProcessButton"));
        jButtonProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProcessActionPerformed(evt);
            }
        });
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.insets = new java.awt.Insets(12, 6, 0, 13);
        jPanel3.add(jButtonProcess, gridBagConstraints2);
        
        jPanelOptions.setLayout(new java.awt.BorderLayout());
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        jPanel3.add(jPanelOptions, gridBagConstraints2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 12);
        gridBagConstraints1.weighty = 1.0;
        add(jPanel3, gridBagConstraints1);
        
        jLabel2.setText("Properties");
        jLabel2.setLabelFor(jPanelOverrides);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jLabel2, gridBagConstraints1);
        
        jPanelOverrides.setLayout(new java.awt.BorderLayout());
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(4, 5, 0, 5);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(jPanelOverrides, gridBagConstraints1);
        
        jLabel5.setText("Data");
        jLabel5.setLabelFor(jtfData);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(0, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel5, gridBagConstraints1);
        
        jtfData.setEditable(false);
        jtfData.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.Data"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(0, 11, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(jtfData, gridBagConstraints1);
        
        jButtonData.setText("...");
        jButtonData.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationGroupDescCustomEditor.DataButton"));
        jButtonData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDataActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 5;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(0, 6, 0, 11);
        add(jButtonData, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void jButtonProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProcessActionPerformed
        // Add your handling code here:
        JFileChooser chooser = getProcessChooser();
        
        String path = jtfProcess.getText();
        File oldLocation = null;
        if (path.length() > 0) oldLocation = new File(path);
        if (oldLocation != null && oldLocation.canRead()) chooser.setCurrentDirectory(oldLocation);
        
        if (org.openide.util.Utilities.showJFileChooser(chooser, this, null) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            jtfProcess.setText(f.getAbsolutePath());
        }
    }//GEN-LAST:event_jButtonProcessActionPerformed

    private void jButtonLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocationActionPerformed
        // Add your handling code here:
        JFileChooser chooser = getLocationChooser();
        String path = jtfLocation.getText();
        File oldLocation = null;
        if (path.length() > 0) {
            if (path.startsWith("file:")) { // NOI18N
                try {
                    oldLocation = new File(new URL(path).getPath());
                } catch (MalformedURLException ex){
                }
            } else {
                oldLocation = new File(path);
            }
        }
        
        if(oldLocation != null && oldLocation.canRead()) chooser.setCurrentDirectory(oldLocation);
 
        if (org.openide.util.Utilities.showJFileChooser(chooser, this, null) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try {
                jtfLocation.setText(f.toURL().toExternalForm());
            } catch (MalformedURLException ex) {
                ActivationModule.getErrorManager(ActivationGroupDescCustomEditor.class).notify(ex);
            } catch (IOException ex) {
                ActivationModule.getErrorManager(ActivationGroupDescCustomEditor.class).notify(ex);
            }
        }
    }//GEN-LAST:event_jButtonLocationActionPerformed

    private void jButtonClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClassActionPerformed
        // Add your handling code here:
        ClassChooser chooser = ClassChooser.getInstance();
        try {
            chooser.show();
            jtfClassName.setText(chooser.getFullClassName());
            jtfLocation.setText(chooser.getPathToClass());
        } catch (UserCancelException ex) {
            // nothing to do
        }
    }//GEN-LAST:event_jButtonClassActionPerformed

    private void jButtonDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDataActionPerformed
        // Add your handling code here:
        final CustomizeInstance panel = new CustomizeInstance(deserialized);
        dialog = DialogDisplayer.getDefault().createDialog(
            new DialogDescriptor(
                panel,
                getString("LBL_CustomizeInstance.Title"), // NOI18N
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.DEFAULT_ALIGN,
                new HelpCtx(CustomizeInstance.class),
                new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent ae) {
                        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
                            deserialized = panel.getCustomizedInstance();
                            jtfData.setToolTipText(null);
                            jtfData.setText(""); // NOI18N
                            try {
                                data = null;
                                if (deserialized != null) {
                                    data = MarshalledObjectSupport.create(deserialized);
                                    jtfData.setText(deserialized.getClass().getName());
                                }
                            } catch (ThreadDeath ex) {
                                throw ex;
                            } catch (Throwable ex) {
                                deserialized = null;
                                ErrorManager err = ActivationModule.getErrorManager(ActivationGroupDescCustomEditor.class);
                                err.annotate(ex, getString("ERR_ActivationGroupDescCustomEditor.Serialization")); // NOI18N
                                err.notify(ex);
                            }
                        }
                    }
                }
            )
        );
        dialog.show();
    }//GEN-LAST:event_jButtonDataActionPerformed
    
    /** Gets new activation group descriptor.
     * @return ActivationGroupDesc
     * @throws IllegalStateException  when the custom property editor
     * does not contain a valid property value (and thus it should not be set).
     */
    public java.lang.Object getPropertyValue() throws IllegalStateException {
        if(custOptionsEditor instanceof javax.swing.JLabel ||
           !(custOverridesEditor instanceof EnhancedCustomPropertyEditor))
            throw new IllegalStateException();
        String className = jtfClassName.getText();
        String location = jtfLocation.getText();
        Properties prop = (Properties)
           ((EnhancedCustomPropertyEditor) custOverridesEditor).getPropertyValue();
        String commandPath = jtfProcess.getText();
        String[] options = (String[]) editorOptions.getValue();
        
        if (className.length() == 0) className = null;
        if (location.length() == 0) location = null;
        if (prop.isEmpty()) prop = null;
        if (commandPath.length() == 0) commandPath = null;
        if (options != null) {
            // clear array which can contain emty elements
            List l = new ArrayList(Arrays.asList(options));
            while (l.remove(EMPTY_STRING));
            options = (String[]) l.toArray(new String[0]);
            if (options.length == 0) options = null;
        }
        
        ActivationGroupDesc.CommandEnvironment cmd = null;
        if (commandPath != null || options != null)
            cmd = new ActivationGroupDesc.CommandEnvironment(commandPath, options);
        
        return new ActivationGroupDesc(className, location, data, prop, cmd);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jtfClassName;
    private javax.swing.JButton jButtonClass;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jtfLocation;
    private javax.swing.JButton jButtonLocation;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jtfProcess;
    private javax.swing.JButton jButtonProcess;
    private javax.swing.JPanel jPanelOptions;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelOverrides;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jtfData;
    private javax.swing.JButton jButtonData;
    // End of variables declaration//GEN-END:variables

    /** Custom editor component for <code>java.util.Properties</code>. */
    private java.awt.Component custOverridesEditor;
    /** Custom editor component for <code>java.lang.String[]</code>. */
    private java.awt.Component custOptionsEditor;
    
    private static final String EMPTY_STRING = ""; //NOI18N
    /** Property editor for <code>java.util.Properties</code>. */
    private PropertyEditor editorOverrides;
    /** Property editor for <code>java.lang.String[]</code>. */
    private PropertyEditor editorOptions;
    /** The group's initialization data.*/
    private MarshalledObject data;
    /** Deserialized initialization data. */
    private Object deserialized = null;
    /** Create instance dialog. */
    private java.awt.Dialog dialog;
    
    /** Localization. */
    private String getString(java.lang.String key) {
        return org.openide.util.NbBundle.getBundle (ActivationGroupDescCustomEditor.class).getString(key);
    }    

    private static final String mnemonic_suffix = ".mnemonic"; // NOI18N

    private char getMnemonic(java.lang.String key) {
        return org.openide.util.NbBundle.getBundle (ActivationGroupDescCustomEditor.class).getString(key + mnemonic_suffix).charAt(0);
    }
      
    private JFileChooser getLocationChooser() {
        if (locationChooser == null) {
            locationChooser = new JFileChooser();
            locationChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            locationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            locationChooser.addChoosableFileFilter(new FileExtChooserFilter(
                                        getString("LBL_ActivationDescCustomEditor.Filter.Description"), // NOI18N
                                        getString("FMT_ActivationDescCustomEditor.Filter") // NOI18N
            ));
        }
        return locationChooser;
    }
      
    private JFileChooser getProcessChooser() {
        if (processChooser == null) {
            processChooser = new JFileChooser();
            processChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            processChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        return processChooser;
    }
}
