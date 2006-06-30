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

package org.netbeans.modules.jndi;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import org.openide.util.NbBundle;

/** Panel for adding new subdirectory
 *
 *  @author Tomas Zezula
 */
final class NewJndiSubContextPanel extends JPanel {
    /** name of directory */
    private JTextField name;

    /** Constructor
     */
    public NewJndiSubContextPanel() {
        this.setLayout(new GridBagLayout());
        this.name = new JTextField(25);
        this.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AD_NewJndiSubContextPanel"));
        this.name.getAccessibleContext().setAccessibleDescription(JndiRootNode.getLocalizedString("AC_SubContextName"));
        JLabel label = new JLabel (NbBundle.getBundle(NewJndiSubContextPanel.class).getString("TXT_SubContextName"));
        label.setLabelFor (this.name);
        label.setDisplayedMnemonic (JndiRootNode.getLocalizedString ("TXT_SubContextName_MNEM").charAt(0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight =1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets (12,12,12,6);
        ((GridBagLayout)this.getLayout()).setConstraints (label,c);
        this.add (label);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets (12,6,12,12);
        ((GridBagLayout)this.getLayout()).setConstraints (this.name,c);
        this.add (name);
        this.name.requestFocus();
    }

    /** Accessor for directory name
     *  @return String name of Context
     */
    public String getName() {
        return name.getText();
    }
}
