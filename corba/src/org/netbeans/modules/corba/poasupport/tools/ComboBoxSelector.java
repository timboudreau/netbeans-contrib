/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
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
        super(null, NotifyDescriptor.getTitleForType(messageType), optionType, messageType, null, null);
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
