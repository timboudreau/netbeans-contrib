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
 
package com.netbeans.enterprise.modules.jndi;

import javax.swing.*;
import java.awt.*;
/** 
 *
 * @author  tzezula
 * @version 
 */
public class NewProviderPanel extends AbstractNewPanel {

  JTextField factory;
  
  static final long serialVersionUID =129555131347808701L;
  /** Creates new NewProviderPanel */
  public NewProviderPanel() {
    super();
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
    
    
//    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Factory")),1,2,2,1,7,5,0,5);
//    this.add(this.factory,3,2,2,1,7,0,0,5);
//    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_InitialContext")),1,3,2,1,0,5,0,5);
//    this.add(this.context,3,3,2,1,0,0,0,5);
//    this.add ( new JLabel(JndiRootNode.getLocalizedString("TXT_Root")),1,4,2,1,0,5,0,5);
//    this.add(this.root,3,4,2,1,0,0,0,5);
//    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Auth")),1,5,2,1,0,5,0,5);
//    this.add(this.authentification,3,5,2,1,0,0,0,5);
//    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Principal")),1,6,2,1,0,5,0,5);
//    this.add(this.principal,3,6,2,1,0,0,0,5);
//    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Credentials")),1,7,2,1,0,5,0,5);
//    this.add(this.credentials,3,7,2,1,0,0,0,5);
    return p;
  }
  
}