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

/** 
 *
 * @author  tzezula
 * @version 
 */
public class NewProviderPanel extends AbstractNewPanel {

  JTextField factory;
  
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
  short createSubGUI(){
    this.factory = new JTextField(26);
    this.context = new JTextField(26);
    this.authentification = new JTextField(26);
    this.principal = new JTextField(26);
    this.credentials= new JTextField(26);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Factory")),1,2,2,1,7,5,0,5);
    this.add(this.factory,3,2,2,1,7,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_InitialContext")),1,3,2,1,0,5,0,5);
    this.add(this.context,3,3,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Auth")),1,4,2,1,0,5,0,5);
    this.add(this.authentification,3,4,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Principal")),1,5,2,1,0,5,0,5);
    this.add(this.principal,3,5,2,1,0,0,0,5);
    this.add(new JLabel(JndiRootNode.getLocalizedString("TXT_Credentials")),1,6,2,1,0,5,0,5);
    this.add(this.credentials,3,6,2,1,0,0,0,5);
    return 6;
  }
  
}