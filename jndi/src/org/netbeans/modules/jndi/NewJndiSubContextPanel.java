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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;

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
    this.setLayout(new BorderLayout());
    this.name = new JTextField(25);
    this.add("North", new JLabel("Subcontext name:"));
    this.add("Center", this.name); 
  }
    
  /** Accessor for directory name
   *  @return String name of Context
   */
  public String getName() {
    return name.getText();
  }
}

/*
 * <<Log>>
 *  5    Gandalf   1.4         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  4    Gandalf   1.3         7/9/99   Ales Novak      localization + code 
 *       requirements followed
 *  3    Gandalf   1.2         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         6/8/99   Ales Novak      sources beautified + 
 *       subcontext creation
 *  1    Gandalf   1.0         6/4/99   Ales Novak      
 * $
 */
