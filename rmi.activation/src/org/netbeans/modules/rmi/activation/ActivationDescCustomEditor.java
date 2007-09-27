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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.activation.*;

import javax.swing.JFileChooser;

import org.openide.*;
import org.openide.cookies.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;

/**
 * Custom editor for java.rmi.activation.ActivationDesc.
 * @author  Jan Pokorsky
 * @version
 */
public class ActivationDescCustomEditor extends javax.swing.JPanel
implements EnhancedCustomPropertyEditor
{
    private final static boolean debug = Boolean.getBoolean("org.netbeans.modules.rmi.test"); // NOI18N
    private final static boolean exceptions = Boolean.getBoolean("netbeans.debug.exceptions"); // NOI18N
    
    private static JFileChooser locationChooser = null;
    
    /** Creates new form ActivationDescCustomEditor.
     * @param desc an activation descriptor, cannot be <code>null</code>.
     * @throws IllegalArgumentException if desc is <code>null</code>.
     */
    public ActivationDescCustomEditor(ActivationDesc desc) {
        if (desc == null) throw new IllegalArgumentException();
        
        initComponents ();
        
        internalization();
        this.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor"));
        
        // set all edited values
        jtfClassName.setText(desc.getClassName());
        jtfLocation.setText(desc.getLocation());
        jRadioButton1.setSelected(desc.getRestartMode());
        jRadioButton2.setSelected( !jRadioButton1.isSelected());

        gid = desc.getGroupID();
        
        // read serialized data
        data = desc.getData();
        if (data == null) return;
        try {
            deserialized = MarshalledObjectSupport.get(data);
            jtfData.setText(deserialized.getClass().getName());
        } catch (ClassNotFoundException ex) {
            if (exceptions) ex.printStackTrace();
            jtfData.setText(
                NbBundle.getMessage(ActivationDescCustomEditor.class,
                    "ERR_ActivationDescCustom.ClassNotFoundException", // NOI18N
                    ex.getMessage()
                )
            );
            deserialized = null;
        } catch (ThreadDeath ex) {
            throw ex;
        } catch (Throwable ex) {
            if (exceptions) ex.printStackTrace();
            jtfData.setText(getString("ERR_ActivationDescCustom.IOException")); // NOI18N
            jtfData.setToolTipText(ex.getMessage());
            deserialized = null;
        }
    }
    
    private void internalization() {
        jButtonClass.setText(getString("LBL_ActivationDescCustom.ClassButton")); // NOI18N
        jButtonLocation.setText(getString("LBL_ActivationDescCustom.LocationButton")); // NOI18N
        jButtonData.setText(getString("LBL_ActivationDescCustom.DataButton")); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jLabel1 = new javax.swing.JLabel();
        jtfClassName = new javax.swing.JTextField();
        jButtonClass = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jtfLocation = new javax.swing.JTextField();
        jButtonLocation = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jtfData = new javax.swing.JTextField();
        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        jButtonData = new javax.swing.JButton();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        jLabel1.setText(getString("LBL_ActivationDescCustom.Classname"));
        jLabel1.setLabelFor(jtfClassName);
        jLabel1.setDisplayedMnemonic(getMnemonic("LBL_ActivationDescCustom.Classname"));
        jLabel1.setLabelFor(jtfClassName);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel1, gridBagConstraints1);
        
        jtfClassName.setColumns(30);
        jtfClassName.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.ClassName"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(12, 11, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        add(jtfClassName, gridBagConstraints1);
        
        jButtonClass.setText("...");
        jButtonClass.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.ClassButton"));
        jButtonClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClassActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(12, 5, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jButtonClass, gridBagConstraints1);
        
        jLabel2.setText(getString("LBL_ActivationDescCustom.Location"));
        jLabel2.setLabelFor(jtfLocation);
        jLabel2.setDisplayedMnemonic(getMnemonic("LBL_ActivationDescCustom.Location"));
        jLabel2.setLabelFor(jtfLocation);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(11, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel2, gridBagConstraints1);
        
        jtfLocation.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.Location"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(11, 11, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        add(jtfLocation, gridBagConstraints1);
        
        jButtonLocation.setText("...");
        jButtonLocation.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.LocationButton"));
        jButtonLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocationActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(11, 5, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jButtonLocation, gridBagConstraints1);
        
        jLabel3.setText(getString("LBL_ActivationDescCustom.Mode"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(11, 12, 0, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel3, gridBagConstraints1);
        
        jRadioButton1.setMnemonic(getMnemonic("LBL_ActivationDescCustom.TRUE"));
        jRadioButton1.setText(" True");
        jRadioButton1.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.true"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(11, 11, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jRadioButton1, gridBagConstraints1);
        
        jRadioButton2.setMnemonic(getMnemonic("LBL_ActivationDescCustom.FALSE"));
        jRadioButton2.setText(" False");
        jRadioButton2.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.false"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(5, 12, 0, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jRadioButton2, gridBagConstraints1);
        
        jLabel4.setText(getString("LBL_ActivationDescCustom.Data"));
        jLabel4.setLabelFor(jtfData);
        jLabel4.setDisplayedMnemonic(getMnemonic("LBL_ActivationDescCustom.Data"));
        jLabel4.setLabelFor(jtfData);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.insets = new java.awt.Insets(11, 12, 12, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel4, gridBagConstraints1);
        
        jtfData.setEditable(false);
        jtfData.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.Data"));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(11, 11, 12, 0);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        add(jtfData, gridBagConstraints1);
        
        jButtonData.setText("...");
        group.add(jRadioButton1);
        group.add(jRadioButton2);
        jButtonData.getAccessibleContext().setAccessibleDescription(getString("AD_ActivationDescCustomEditor.DataButton"));
        jButtonData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDataActionPerformed(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.insets = new java.awt.Insets(11, 5, 12, 12);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add(jButtonData, gridBagConstraints1);
        
    }//GEN-END:initComponents

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
                ActivationModule.getErrorManager(ActivationDescCustomEditor.class).notify(ex);
            } catch (IOException ex) {
                ActivationModule.getErrorManager(ActivationDescCustomEditor.class).notify(ex);
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
                                    //data = new MarshalledObject(deserialized);
                                    data = MarshalledObjectSupport.create(deserialized);
                                    jtfData.setText(deserialized.getClass().getName());
                                }
                            } catch (ThreadDeath ex) {
                                throw ex;
                            } catch (Throwable ex) {
                                deserialized = null;
                                ErrorManager err = ActivationModule.getErrorManager(ActivationDescCustomEditor.class);
                                err.annotate(ex, getString("ERR_ActivationDescCustomEditor.Serialization")); // NOI18N
                                err.notify(ex);
                            }
                        }
                    }
                }
            )
        );
        dialog.show();
    }//GEN-LAST:event_jButtonDataActionPerformed
    
    
    /** Gets new activation descriptor.
     * @return ActivationDesc
     * @throws IllegalStateException Illegal state of the activation descriptor
     */
    public java.lang.Object getPropertyValue() throws IllegalStateException {
        String className = jtfClassName.getText();
        String location = jtfLocation.getText();
        boolean restartMode = jRadioButton1.isSelected()  ;
        
        if (className.length() == 0) className = null;
        if (location.length() == 0) location = null;
        
        return new ActivationDesc(gid, className, location, data, restartMode);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jtfClassName;
    private javax.swing.JButton jButtonClass;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jtfLocation;
    private javax.swing.JButton jButtonLocation;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jtfData;
    private javax.swing.JButton jButtonData;
    // End of variables declaration//GEN-END:variables
    
    /** An Id of an activation group which the activatable object belongs to. */
    private ActivationGroupID gid;
    /** The object's initialization data. */
    private MarshalledObject data;
    /** Deserialized initialization data. */
    private Object deserialized = null;
    /** Create instance dialog. */
    private Dialog dialog;
  
    /** Localization. */
    private String getString(java.lang.String key) {
       return org.openide.util.NbBundle.getBundle (ActivationDescCustomEditor.class).getString(key);
    }    
  
    private static final String mnemonic_suffix = ".mnemonic"; // NOI18N
   
    private char getMnemonic(java.lang.String key) {
       return org.openide.util.NbBundle.getBundle (ActivationDescCustomEditor.class).getString(key + mnemonic_suffix).charAt(0);
    }
    
    private JFileChooser getLocationChooser() {
        if (locationChooser == null) {
            locationChooser = new JFileChooser();
            locationChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            locationChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            locationChooser.addChoosableFileFilter(new FileExtChooserFilter(
                                        getString("LBL_ActivationDescCustomEditor.Filter.Description"), // NOI18N
                                        getString("FMT_ActivationDescCustomEditor.Filter") // NOI18N
            ));
        }
        return locationChooser;
    }
    
}
