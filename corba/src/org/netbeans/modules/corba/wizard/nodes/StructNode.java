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

import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
/** 
 *
 * @author  root
 * @version 
 */
public class StructNode extends SENode {

  private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/struct";
  
  /** Creates new StructNode */
  public StructNode(NamedKey key) {
    super (key);
    this.setName (key.getName());
    this.setIconBase (ICON_BASE);
  }
  
  public String generateSelf (int indent) {
    String code = new String ();
    String fill = new String ();
    for ( int i=0; i< indent; i++)
      fill = fill + "    ";
    code = fill + "struct "+this.getName()+" {\n"; // No I18N
    Node[] nodes = this.getChildren().getNodes();
    for (int i=0; i<nodes.length; i++) {
      code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent+1);
    }
    code = code + fill + "};\n";
    return code;
  }
}