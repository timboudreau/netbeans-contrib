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

package com.netbeans.enterprise.modules.jndi;

import javax.swing.JTextField;
import javax.swing.JLabel;

final class NewPropertyPanel extends GridBagPanel {

  private JTextField name;
  private JTextField value;
  
  
  public NewPropertyPanel() {
    name = new JTextField(20);
    value= new JTextField(20);
    add(new JLabel("Property Name:"),1,1,2,1);
    add(this.name,1,2,2,1);
    add(new JLabel("Property Value:"),1,3,2,1);
    add(this.value,1,4,2,1);
  }

  public String getName() {
    return name.getText();
  }

  public String getValue() {
    return value.getText();
  }
  
  public void setName(String name) {
    this.name.setText(name);
  }
  
  public void setValue(String value) {
    this.value.setText(value);
  }
}
