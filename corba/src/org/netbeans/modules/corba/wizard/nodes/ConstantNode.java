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

package org.netbeans.modules.corba.wizard.nodes;

import org.netbeans.modules.corba.wizard.nodes.keys.*;
/** 
 *
 * @author  root
 * @version 
 */
public class ConstantNode extends AbstractMutableLeafNode {

  private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/const";
  
  /** Creates new CreateConstantAction */
  public ConstantNode (NamedKey key) {
    super (key);
    this.setName (key.getName ());
    this.setIconBase (ICON_BASE);
  }
  
  
  
  public String generateSelf (int indent){
    String code = new String ();
    String fill = new String ();
    for (int i=0; i<indent; i++)
      fill = fill + "    "; // No I18N
    ConstKey key = (ConstKey) this.key;
    code = fill + "const "+ key.getType () + " "+ this.getName()+ " = "+key.getValue()+";\n"; // No I18N
    return code;
  }
  
}