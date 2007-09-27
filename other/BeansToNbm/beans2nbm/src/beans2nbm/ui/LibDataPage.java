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
package beans2nbm.ui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.wizard.WizardPage;

/**
 *
 * @author  Tim Boudreau
 */
public class LibDataPage extends WizardPage {

    /** Creates new form LibDataPage */
    public LibDataPage() {
        initComponents();
    }

    public void addNotify () {
        super.addNotify();
        String codeName = (String) getWizardData("codename");
        if (codeName != null) {
            uidField.setText(codeName);
        }
        String s = validateContents (null, null);
        if (s != null) {
            setProblem (s);
        }
    }
    
    public void requestFocus() {
        displayNameField.requestFocus();
    }

    protected String validateContents(Component component, Object event) {
        String txt = displayNameField.getText();
        if (txt.trim().length() == 0) {
            return "Enter a name for the library containing your JavaBeans";
        }
        if (txt.indexOf("\\") > 0 || txt.indexOf("/") > 0) {
            return "Display name may not contain / or \\ characters";
        }
        if (txt.indexOf('&') > 0 || txt.indexOf(';') > 0 || txt.indexOf(':') > 0) {
            return "Display name may not contain & : or ; characters";
        }
        txt = uidField.getText();
        if (txt.length() == 0) {
            return "Enter a unique ID";
        }
        if (txt.length() < 5) {
            return "Unique ID should be at least 5 characters in length";
        }
        char[] c = txt.toCharArray();
        boolean dotFound = false;
        if (Character.isDigit(c[0])) {
            return ("Unique ID may not start with a number");
        }
        if (c[c.length - 1] == '.') {
            return ("Unique ID may not end with a dot");
        }
        for (int i = 0; i < c.length; i++) {
            dotFound |= c[i] == '.';
            if (Character.isWhitespace(c[i])) {
                return "Unique ID cannot contain whitespace - try a package name";
            } else if (c[i] == '/' || c[i] == '\\') {
                return "Unique ID cannot contain \\ or / characters - try a package name";
            }
        }
        if (!dotFound) {
            return "Unique ID should contain at least 1 dot character - try a package name";
        }
        String s = versionField.getText();
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return ("Not a decimal number: " + s);
        }
        return null;
    }
    
    public static String getStep() {
        return "libInfo";
    }
    
    public static String getDescription() {
        return "Library Description";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        libNameLabel = new javax.swing.JLabel();
        displayNameField = new javax.swing.JTextField();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        uniqueIdLabel = new javax.swing.JLabel();
        uidField = new javax.swing.JTextField();
        versionLabel = new javax.swing.JLabel();
        versionField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setLayout(new java.awt.GridBagLayout());

        libNameLabel.setLabelFor(displayNameField);
        libNameLabel.setText("Library Display Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        add(libNameLabel, gridBagConstraints);

        displayNameField.setToolTipText("The name of your company, or whatever category should be used in the component palette");
        displayNameField.setName("displayName");
        displayNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                displayNameFieldFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(displayNameField, gridBagConstraints);

        descriptionLabel.setText("Description");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 0);
        add(descriptionLabel, gridBagConstraints);

        descriptionField.setColumns(20);
        descriptionField.setFont(UIManager.getFont ("Label.font") == null ? new Font ("SansSerif", 12, Font.BOLD) : UIManager.getFont("Label.font"));
        descriptionField.setLineWrap(true);
        descriptionField.setRows(5);
        descriptionField.setToolTipText("Will be shown in the Module Manager dialog in the IDE");
        descriptionField.setWrapStyleWord(true);
        descriptionField.setName("description");
        jScrollPane1.setViewportView(descriptionField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        uniqueIdLabel.setText("Unique ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        add(uniqueIdLabel, gridBagConstraints);

        uidField.setText("org.foo.mylib");
        uidField.setToolTipText("Should be something resembling a java package name");
        uidField.setName("codeName");
        uidField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                displayNameFieldFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(uidField, gridBagConstraints);

        versionLabel.setText("Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        add(versionLabel, gridBagConstraints);

        versionField.setText("1.0");
        versionField.setToolTipText("A dewey decimal format number such as 1.2");
        versionField.setName("libversion");
        versionField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                displayNameFieldFocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(versionField, gridBagConstraints);

        jLabel1.setText("Minimum Java Version");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        add(jLabel1, gridBagConstraints);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1.4", "1.5", "1.6" }));
        jComboBox1.setName("javaVersion");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(jComboBox1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void displayNameFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_displayNameFieldFocusGained
        JTextComponent jtc = (JTextComponent) evt.getSource();
        jtc.selectAll();
    }//GEN-LAST:event_displayNameFieldFocusGained
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextField displayNameField;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel libNameLabel;
    private javax.swing.JTextField uidField;
    private javax.swing.JLabel uniqueIdLabel;
    private javax.swing.JTextField versionField;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
    
}
