/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.util;

import org.openide.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 *
 * @author  Pavel Buzek
 * @version 
 */

public class NotifyDescriptorInputPassword extends NotifyDescriptor.InputLine {
    private javax.swing.JPasswordField passwordField;
    private JLabel textLabel;
    private Character mnemonic = null;

    protected Component createDesign (String text) {
        //      System.out.println ("createDesign("+text+")"+this+" "+System.identityHashCode(this)); // NOI18N
        JPanel panel = new JPanel();
        textLabel = new JLabel(text);
        textLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 6, 6));
        panel.add("West", textLabel); // NOI18N
        passwordField = new javax.swing.JPasswordField (25);
        //      System.out.println("passwordField: "+passwordField); // NOI18N
        panel.add("Center", passwordField); // NOI18N
        passwordField.setBorder(new CompoundBorder(passwordField.getBorder(), new EmptyBorder(2, 0, 2, 0)));
        passwordField.requestFocus();

        javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(
                                          java.awt.event.KeyEvent.VK_ENTER, 0
                                      );
        javax.swing.text.Keymap map = passwordField.getKeymap ();

        map.removeKeyStrokeBinding (enter);
        /*
        passwordField.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
              NotifyDescriptorInputPassword.this.setValue (NotifyDescriptor.InputLine.OK_OPTION);
            }
          }
        );
        */
        textLabel.setLabelFor(passwordField);
        panel.getAccessibleContext().setAccessibleDescription(
            org.openide.util.NbBundle.getBundle(NotifyDescriptorInputPassword.class).getString("ACSD_NotifyDescriptorInputPassword.dialog"));
        passwordField.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(NotifyDescriptorInputPassword.class).getString("ACSD_NotifyDescriptorInputPassword.passwordField"));
        if (mnemonic != null) {
            textLabel.setDisplayedMnemonic(mnemonic.charValue());
        }
        return panel;
    }

    /**
    * Get the text which the user typed into the input line.
    * @return the text entered by the user
    */
    public String getInputText () {
        //System.out.println(this+" "+System.identityHashCode(this)); // NOI18N
        if(passwordField==null) {
            //System.out.println ("passwordField is null"); // NOI18N
            return ""; // NOI18N
        } else return new String(passwordField.getPassword ());
    }

    /**
    * Set the text on the input line.
    * @param text the new text
    */
    public void setInputText (String text) {
        passwordField.setText (text);
    }

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (java.lang.String text, java.lang.String title) {
        super (text, title);
    }
    
    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (java.lang.String text, java.lang.String title, char mnemonic) {
        super (text, title);
        this.mnemonic = new Character(mnemonic);
        textLabel.setDisplayedMnemonic(mnemonic);
    }

    /*
    public NotifyDescriptorInputPassword (java.lang.String text, java.lang.String title, javax.swing.Icon icon) {
     super (text, title, icon);
}
    */

    public NotifyDescriptorInputPassword (java.lang.String text, java.lang.String title, int optionType, int messageType) {
        super (text, title, optionType, messageType);
    }
}
