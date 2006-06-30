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

package org.netbeans.modules.jndi.gui;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.*;
import org.netbeans.modules.jndi.JndiRootNode;


/** This Class represents an Panel used for displaying warning
 *  when some providers are missing
 */
public class NotFoundPanel extends JPanel {


    /** Creates new NotFoundPanel
     *  @param String provider, the provider class
     */
    public NotFoundPanel(String provider) {
        String errMsg = JndiRootNode.getLocalizedString("EXC_ClassNotFound");
        String errTip = JndiRootNode.getLocalizedString ("TIP_Installation");
        JLabel label = new JLabel(errMsg);
        JTextArea comments = new JTextArea(errTip,2,66);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setEnabled(false);
        comments.setBackground(label.getBackground());
        javax.accessibility.AccessibleContext ac = this.getAccessibleContext();
        ac.setAccessibleName (ac.getAccessibleName()+errMsg+errTip);
        ac.setAccessibleDescription (JndiRootNode.getLocalizedString("AD_NotFoundPanel"));
        GridBagConstraints c;
        this.setLayout( new GridBagLayout());
        c  = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets (8,8,4,8);
        c.weightx = 1.0;
        c.weighty = 0.0;
        ((GridBagLayout)this.getLayout()).setConstraints(label,c);
        this.add(label);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets (4,8,8,8);
        c.weightx = 1.0;
        c.weighty = 1.0;
        ((GridBagLayout)this.getLayout()).setConstraints(comments,c);
        this.add(comments);
    }
}