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

package org.netbeans.modules.javafx.platform.wizard;

import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.modules.javafx.platform.PlatformUiSupport;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Utilities;

/**
 * This Panel launches autoconfiguration during the New JavaFX Platform sequence.
 * The UI views properties of the platform, reacts to the end of detection by
 * updating itself. It triggers the detection task when the button is pressed.
 * The inner class WizardPanel acts as a controller, reacts to the UI completness
 * (jdk name filled in) and autoconfig result (passed successfully) - and manages
 * Next/Finish button (valid state) according to those.
 *
 * @author Svata Dedic
 */
public class DetectPanel extends javax.swing.JPanel {

    private static final Icon BADGE = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/platform/resources/platformBadge.gif")); // NOI18N
    private static final Icon EMPTY = new ImageIcon(Utilities.loadImage("org/netbeans/modules/javafx/platform/resources/empty.gif")); // NOI18N

    private final ChangeSupport cs = new ChangeSupport(this);

    /**
     * Creates a detect panel
     * start the task and update on its completion
     * @param primaryPlatform the platform being customized.
     */
    public DetectPanel() {
        initComponents();
        postInitComponents ();
        putClientProperty("WizardPanel_contentData",
            new String[] {
                NbBundle.getMessage(DetectPanel.class,"TITLE_PlatformName"),
        });
        this.setName (NbBundle.getMessage(DetectPanel.class,"TITLE_PlatformName"));
    }

    public void addNotify() {
        super.addNotify();        
    }    

    private void postInitComponents () {
        platformCombo.setModel(PlatformUiSupport.createPlatformComboBoxModel(null, "j2se")); //NOI18N
        platformCombo.setRenderer(PlatformUiSupport.createPlatformListCellRenderer());
        DocumentListener lsn = new DocumentListener () {
            public void insertUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            public void removeUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            public void changedUpdate(DocumentEvent e) {
                cs.fireChange();
            }
        };                
        radioBtChng(null);
        jdkName.getDocument().addDocumentListener(lsn);
        javaFolder.getDocument().addDocumentListener(lsn);
        fxFolder.getDocument().addDocumentListener(lsn);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel3 = new javax.swing.JLabel();
        jdkName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        platformCombo = new javax.swing.JComboBox();
        jRadioButton2 = new javax.swing.JRadioButton();
        javaFolder = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        fxFolder = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jLabel3.setLabelFor(jdkName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, NbBundle.getBundle(DetectPanel.class).getString("LBL_DetailsPanel_Name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 0);
        add(jdkName, gridBagConstraints);
        jdkName.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_PlatformName")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DetectPanel.class, "TXT_JavaPlatform")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jLabel2, gridBagConstraints);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton1, org.openide.util.NbBundle.getMessage(DetectPanel.class, "TXT_InstalledJDK")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtChng(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jRadioButton1, gridBagConstraints);

        platformCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platformComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(platformCombo, gridBagConstraints);

        buttonGroup1.add(jRadioButton2);
        org.openide.awt.Mnemonics.setLocalizedText(jRadioButton2, org.openide.util.NbBundle.getMessage(DetectPanel.class, "TXT_CustomJava")); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtChng(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jRadioButton2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(javaFolder, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(DetectPanel.class, "LBL_BrowsePlatform")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jButton3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(DetectPanel.class, "TXT_FX")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 0);
        add(fxFolder, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, org.openide.util.NbBundle.getMessage(DetectPanel.class, "LBL_BrowseFX")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 5, 0, 0);
        add(jButton4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(DetectPanel.class).getString("AD_DetectPanel")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

private void radioBtChng(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtChng
    boolean sel = jRadioButton1.isSelected();
    platformCombo.setEnabled(sel);
    javaFolder.setEnabled(!sel);
    jButton3.setEnabled(!sel);
}//GEN-LAST:event_radioBtChng

private void platformComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platformComboActionPerformed
    cs.fireChange();
}//GEN-LAST:event_platformComboActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        JFileChooser chooser = new JFileChooser ();
        final FileSystemView fsv = chooser.getFileSystemView();
        chooser.setFileView(new FileView() {
            private Icon lastOriginal;
            private Icon lastMerged;
            public Icon getIcon(File _f) {
                File f = FileUtil.normalizeFile(_f);
                Icon original = fsv.getSystemIcon(f);
                if (original == null) {
                    // L&F (e.g. GTK) did not specify any icon.
                    original = EMPTY;
                }
                if (new File(f, "bin/java").isFile() || new File(f, "bin/java.exe").isFile()) {
                    if ( original.equals( lastOriginal ) ) {
                        return lastMerged;
                    }
                    lastOriginal = original;
                    lastMerged = new MergedIcon(original, BADGE, -1, -1);                
                    return lastMerged;
                } else {
                    return original;
                }
            }
        });
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = new File (javaFolder.getText());
        chooser.setSelectedFile(f);
        chooser.setDialogTitle (NbBundle.getMessage(DetectPanel.class, "TITLE_SelectJava"));
        if (chooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION) {
            javaFolder.setText(chooser.getSelectedFile().getAbsolutePath());
        }
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser chooser = new JFileChooser ();
        final FileSystemView fsv = chooser.getFileSystemView();
        chooser.setFileView(new FileView() {
            private Icon lastOriginal;
            private Icon lastMerged;
            public Icon getIcon(File _f) {
                File f = FileUtil.normalizeFile(_f);
                Icon original = fsv.getSystemIcon(f);
                if (original == null) {
                    // L&F (e.g. GTK) did not specify any icon.
                    original = EMPTY;
                }
                if (new File(f, "javafxc.jar").isFile() && new File(f, "javafxrt.jar").isFile()) {
                    if ( original.equals( lastOriginal ) ) {
                        return lastMerged;
                    }
                    lastOriginal = original;
                    lastMerged = new MergedIcon(original, BADGE, -1, -1);                
                    return lastMerged;
                } else {
                    return original;
                }
            }
        });
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File f = new File (fxFolder.getText());
        chooser.setSelectedFile(f);
        chooser.setDialogTitle (NbBundle.getMessage(DetectPanel.class, "TITLE_SelectFX"));
        if (chooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION) {
            fxFolder.setText(chooser.getSelectedFile().getAbsolutePath());
        }
}//GEN-LAST:event_jButton4ActionPerformed
    
    public final synchronized void addChangeListener (ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public final synchronized void removeChangeListener (ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public String getPlatformName() {
	 return jdkName.getText();
    }
    
    public File getPlatformFolder() {
        return jRadioButton1.isSelected() ? FileUtil.toFile(PlatformUiSupport.getPlatform(platformCombo.getSelectedItem()).getInstallFolders().iterator().next()) 
                                          : FileUtil.normalizeFile(new File(javaFolder.getText()));
    }
    
    public File getFxFolder() {
	 return FileUtil.normalizeFile(new File(fxFolder.getText()));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField fxFolder;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField javaFolder;
    private javax.swing.JTextField jdkName;
    private javax.swing.JComboBox platformCombo;
    // End of variables declaration//GEN-END:variables

    /**
     * Controller for the outer class: manages wizard panel's valid state
     * according to the user's input and detection state.
     */
    static class WizardPanel implements WizardDescriptor.Panel<WizardDescriptor>,ChangeListener {
        private DetectPanel         component;
        private final JavaFXWizardIterator  iterator;
        private final ChangeSupport cs = new ChangeSupport(this);
        private boolean             valid;
        private WizardDescriptor    wiz;

        WizardPanel(JavaFXWizardIterator iterator) {            
	    this.iterator = iterator;
        }

        public void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }

        public java.awt.Component getComponent() {
            if (component == null) {
                component = new DetectPanel();
                component.addChangeListener (this);
            }
            return component;
        }

        void setValid(boolean v) {
            if (v == valid) return;
            valid = v;
            cs.fireChange();
        }

        public HelpCtx getHelp() {
            return new HelpCtx (DetectPanel.class);
        }

        public boolean isValid() {
            return valid;
        }

        public void readSettings(WizardDescriptor settings) {           
            this.wiz = settings;
            String name;
            int i = 1;
            while (!checkName(name = NbBundle.getMessage(DetectPanel.class, "TXT_DefaultPlaformName", String.valueOf(i)))) i++;
            component.jdkName.setText(name);
            File fxPath = InstalledFileLocator.getDefault().locate("javafx-sdk1.0/lib/javafxc.jar", "org.netbeans.modules.javafx", false);
            if (fxPath != null && fxPath.isFile()) component.fxFolder.setText(fxPath.getParent());
            File f = component.getPlatformFolder();
            if (f != null && f.isDirectory()) component.javaFolder.setText(f.getAbsolutePath());
            checkValid();
        }

        public void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

	/**
	 Updates the Platform's display name with the one the user
	 has entered. Stores user-customized display name into the Platform.
	 */
        public void storeSettings(WizardDescriptor settings) {
            if (isValid()) {                                
                iterator.platformName = component.getPlatformName(); 
                iterator.installFolder = component.getPlatformFolder();
                iterator.fxFolder = component.getFxFolder();
            }
        }

        public void stateChanged(ChangeEvent e) {
             this.checkValid();
        }

        private void setErrorMessage(String key) {
             this.wiz.putProperty("WizardPanel_errorMessage", NbBundle.getMessage(DetectPanel.class, key));    //NOI18N
             setValid(false);
        }
        
        private boolean checkName(String name) {
            JavaPlatform[] platforms = JavaPlatformManager.getDefault().getInstalledPlatforms();                
            for (int i=0; i<platforms.length; i++) {
                if (name.equals (platforms[i].getDisplayName())) {
                    setErrorMessage("ERROR_UsedDisplayName");    //NOI18N
                    return false;
                }
            }
            return true;
        }
        
        private void checkValid () {
            String name = this.component.getPlatformName ();            
            if (name.length() == 0) {
                setErrorMessage("ERROR_InvalidDisplayName"); //NOI18N
                return;
            }                
            if (!checkName(name)) {
                setErrorMessage("ERROR_UsedDisplayName");    //NOI18N
                return;
            }
            File f = component.getPlatformFolder();
            if (!new File(f, "bin/java").isFile() && !new File(f, "bin/java.exe").isFile()) {
                setErrorMessage("ERROR_WrongJavaPlatformLocation"); //NOI18N
                 return;
            }
            f = component.getFxFolder();
            if (!new File(f, "javafxc.jar").isFile() || !new File(f, "javafxrt.jar").isFile()) {
                setErrorMessage("ERROR_WrongFxLocation"); //NOI18N
                 return;
            }
            this.wiz.putProperty("WizardPanel_errorMessage", ""); //NOI18N
            setValid(true);            
        }
    }    

    private static class MergedIcon implements Icon {
        
        private Icon icon1;
        private Icon icon2;
        private int xMerge;
        private int yMerge;
        
        MergedIcon( Icon icon1, Icon icon2, int xMerge, int yMerge ) {
            
            this.icon1 = icon1;
            this.icon2 = icon2;
            
            if ( xMerge == -1 ) {
                xMerge = icon1.getIconWidth() - icon2.getIconWidth();
            }
            
            if ( yMerge == -1 ) {
                yMerge = icon1.getIconHeight() - icon2.getIconHeight();
            }
            
            this.xMerge = xMerge;
            this.yMerge = yMerge;
        }
        
        public int getIconHeight() {
            return Math.max( icon1.getIconHeight(), yMerge + icon2.getIconHeight() );
        }
        
        public int getIconWidth() {
            return Math.max( icon1.getIconWidth(), yMerge + icon2.getIconWidth() );
        }
        
        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            icon1.paintIcon( c, g, x, y );
            icon2.paintIcon( c, g, x + xMerge, y + yMerge );
        }
        
    }
}
