/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.bluej.upgrade;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;


/**
 * @author Jiri Rechtacek
 */
final class AutoUpgradePanel extends JPanel {

    String source;

    /** Creates new form UpgradePanel */
    public AutoUpgradePanel (String directory) {
        this.source = directory;
        initComponents();
        initAccessibility();
    }

    /** Remove a listener to changes of the panel's validity.
     * @param l the listener to remove
     */
    void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }

    /** Add a listener to changes of the panel's validity.
     * @param l the listener to add
     * @see #isValid
     */
    void addChangeListener(ChangeListener l) {
        if (!changeListeners.contains(l)) {
            changeListeners.add(l);
        }
    }

    private void initAccessibility() {
        this.getAccessibleContext().setAccessibleDescription(bundle.getString("MSG_Confirmation")); // NOI18N
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        txtVersions = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        setName(bundle.getString("LBL_UpgradePanel_Name"));
        txtVersions.setBackground(getBackground());
        txtVersions.setColumns(50);
        txtVersions.setEditable(false);
        txtVersions.setFont(new java.awt.Font("Dialog", 0, 12));
        txtVersions.setLineWrap(true);
        txtVersions.setRows(3);
        txtVersions.setText(NbBundle.getMessage (AutoUpgradePanel.class, "MSG_Confirmation", source));
        txtVersions.setWrapStyleWord(true);
        txtVersions.setMinimumSize(new java.awt.Dimension(100, 50));
        add(txtVersions, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea txtVersions;
    // End of variables declaration//GEN-END:variables

    private static final ResourceBundle bundle = NbBundle.getBundle(AutoUpgradePanel.class);
    private ArrayList changeListeners = new ArrayList(1);
    
}
