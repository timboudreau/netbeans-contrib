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

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;



/** Base class for handling GridBagLayout */
class GridBagPanel extends JPanel {
  
  public GridBagPanel() {
    this.setLayout(new GridBagLayout());
  }

  // GridBagLayout add version
  protected void add (Component component, int x, int y, int width, int height, int top, int left, int bottom, int right) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx=x;
    c.gridy=y;
    c.gridwidth=width;
    c.gridheight=height;
    c.fill=GridBagConstraints.BOTH;
    c.anchor=GridBagConstraints.NORTHWEST;
    c.insets = new Insets(top,left,bottom,right);
    ((GridBagLayout)this.getLayout()).setConstraints(component,c);
    this.add(component);
  }
    
  protected void add (Component component, int x, int y, int width, int height) {
    add(component,x,y,width,height,0,0,0,0);
  }
}





