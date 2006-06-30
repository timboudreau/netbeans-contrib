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

package org.netbeans.modules.corba.poasupport.tools;

import java.util.Vector;
import org.openide.NotifyDescriptor;
import org.netbeans.modules.corba.poasupport.POASupport;

/**
 *
 * @author Dusan Balek
 * @version
 */

public class ComboBoxSelector extends NotifyDescriptor {

    protected javax.swing.JComboBox comboBox;

    /** Creates new ComboBoxSelector */
    public ComboBoxSelector(final String text, final String label, final Vector values) {
        this(text, label, values, OK_CANCEL_OPTION, WARNING_MESSAGE);
    }

    public ComboBoxSelector(final String text, final String label, final Vector values, final int optionType, final int messageType) {
        super(null, getTitleForType(messageType), optionType, messageType, null, null);
        super.setMessage(createDesign(text, label, values));
    }
    
    public Object getSelectedItem () {
        return comboBox.getSelectedItem ();
    }
    
    protected java.awt.Component createDesign (final String text, final String label, final Vector values) {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(text);
        javax.swing.JLabel comboBoxLabel = new javax.swing.JLabel(label + POASupport.getString("LBL_Colon"));
        comboBox = new javax.swing.JComboBox(values);
        textArea.setEditable(false);
        textArea.setBorder(new javax.swing.border.EmptyBorder(0, 0, 6, 0));
        textArea.setForeground(java.awt.Color.black);
        textArea.setBackground(panel.getBackground());
        textArea.setFont(comboBoxLabel.getFont());
        comboBoxLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 6));
        comboBoxLabel.setForeground(java.awt.Color.black);
        panel.setLayout(new java.awt.BorderLayout());
        panel.add("North", textArea); // NOI18N
        panel.add("West", comboBoxLabel); // NOI18N
        panel.add("Center", comboBox); // NOI18N
        comboBox.requestFocus();
        return panel;
    }
}
