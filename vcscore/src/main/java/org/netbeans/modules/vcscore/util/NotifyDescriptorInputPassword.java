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

public class NotifyDescriptorInputPassword extends DialogDescriptor {//NotifyDescriptor.InputLine {

    private javax.swing.JPasswordField passwordField;

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title) {
        this(text, title, null);
    }

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, String description) {
        this(text, title, null, description, new JPasswordField[1]);
    }
    
    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, char mnemonic) {
        this(text, title, mnemonic, null);
    }

    /** Creates new NotifyDescriptorInputPassword */
    public NotifyDescriptorInputPassword (String text, String title, char mnemonic, String description) {
        this(text, title, new Character(mnemonic), description, new JPasswordField[1]);
    }
    
    /**
     * Use this constructor as a hack to set passwordField variable.
     */
    private NotifyDescriptorInputPassword (String text, String title, Character mnemonic,
                                           String description, JPasswordField[] passwordFieldPtr) {
        super (createDesign(text, description, mnemonic, passwordFieldPtr), title);
        this.passwordField = passwordFieldPtr[0];
    }

    /*
    public NotifyDescriptorInputPassword (String text, String title, javax.swing.Icon icon) {
        super (text, title, icon);
    }
    
    public NotifyDescriptorInputPassword (String text, String title, int optionType, int messageType) {
        super (text, title, optionType, messageType);
    }
     */
    
    private static Component createDesign (String text, String description, Character mnemonic,
                                           JPasswordField passwordFieldPtr[]) {
        //      System.out.println ("createDesign("+text+")"+this+" "+System.identityHashCode(this)); // NOI18N
        JPanel panel = new JPanel();
        JLabel textLabel = new JLabel(text);
        textLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 6, 6));
        //System.out.println("NotifyDescriptorInputPassword: description = "+description);
        if (description != null) {
            JLabel descriptionLabel = new JLabel(description);
            panel.add(descriptionLabel, BorderLayout.NORTH);
            descriptionLabel.setBorder(new CompoundBorder(descriptionLabel.getBorder(), new EmptyBorder(2, 0, 11, 0)));
        }
        panel.add("West", textLabel); // NOI18N
        javax.swing.JPasswordField passwordField = new javax.swing.JPasswordField (25);
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
        passwordFieldPtr[0] = passwordField;
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

}
