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

/*
 * NewProviderPanel.java
 * Represents panel for adding new providers
 * Created on September 21, 1999, 5:41 PM
 */

package org.netbeans.modules.jndi;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
/**
 *
 * @author  tzezula
 * @version 
 */
public class NewProviderPanel extends AbstractNewPanel {

    JTextField factory;
    private PropertyChangeSupport listeners;

    /** Creates new NewProviderPanel */
    public NewProviderPanel() {
        super();
        this.listeners = new PropertyChangeSupport(this);
    }

    /** Accessor for Factory
    *  @return String name of Factory
    */
    public String getFactory() {
        return (String) factory.getText();
    }

    /** Creates an part of GUI  called from createGUI*/
    JPanel createSubGUI(){
        this.factory = new JTextField();
        this.context = new JTextField();
        this.authentification = new JTextField();
        this.principal = new JTextField();
        this.credentials= new JTextField();
        this.root = new JTextField();
        JPanel p = new JPanel();
        p.setLayout ( new GridBagLayout());
        GridBagConstraints gridBagConstraints;
        JLabel label = new JLabel (JndiRootNode.getLocalizedString("TXT_Factory"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_InitialContext"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Root"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Auth"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Principal"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        label = new JLabel (JndiRootNode.getLocalizedString("TXT_Credentials"));
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        p.add (label, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.factory, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.context, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.root, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.authentification, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.principal, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints ();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets (8, 8, 0, 8);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        p.add (this.credentials, gridBagConstraints);
        return p;
    }


    javax.swing.JPanel createNotesPanel(){
        javax.swing.JPanel p = new javax.swing.JPanel();
        javax.swing.JTextArea area = new javax.swing.JTextArea(JndiRootNode.getLocalizedString("TIP_Installation"),2,66);
        area.setEnabled(false);
        area.setBackground(new javax.swing.JLabel().getBackground());
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        p.add(area);
        return p;
    }

}