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



/** Panel with GridBagLayout
 *
 * @author Tomas Zezula
 */
class GridBagPanel extends JPanel {
  
  public GridBagPanel() {
    this.setLayout(new GridBagLayout());
  }

  /** Adds componet to panel
   *  @param component the component to be inserted
   *  @param x the horizontal position
   *  @param y the vertical position
   *  @param width the width of the component
   *  @param height the height of the component
   *  @param top the top inset
   *  @param left the left inset
   *  @param bottom the bottom inset
   *  @param right the right inset
   */
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
  
  /** Adds component to panel
   *  @param component the component to be inserted
   *  @param x the horizontal position
   *  @param y the vertical position
   *  @param width the width of the component
   *  @param height the height of the component
   */
  protected void add (Component component, int x, int y, int width, int height) {
    add(component,x,y,width,height,0,0,0,0);
  }
}



/*
 * <<Log>>
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  4    Gandalf   1.3         6/10/99  Ales Novak      gemstone support + 
 *       localizations
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */
