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