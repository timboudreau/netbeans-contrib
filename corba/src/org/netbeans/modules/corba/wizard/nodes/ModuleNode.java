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

import java.io.OutputStream;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;

/** 
 *
 * @author  root
 * @version 
 */
public class ModuleNode extends FMNode {

  private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/module";
  
  /** Creates new ModuleNode */
  public ModuleNode (NamedKey key) {
    super (key);
    this.getCookieSet().add(this);
    this.setName (key.getName());
    this.setIconBase (ICON_BASE);
  }
  
  public SystemAction[] createActions () {
    return new SystemAction[] {
      SystemAction.get (CreateAliasAction.class),
      SystemAction.get (CreateConstantAction.class),
      SystemAction.get (CreateEnumAction.class),
      SystemAction.get (CreateExceptionAction.class),
      SystemAction.get (CreateInterfaceAction.class),
      SystemAction.get (CreateModuleAction.class),
      SystemAction.get (CreateStructAction.class),
      SystemAction.get (CreateUnionAction.class),
      null,
      SystemAction.get (DestroyAction.class),
      SystemAction.get (RenameAction.class)
    };
  }
  
  public String generateSelf (int indent) {
    String code = new String ();
    String fill = new String ();
    for (int i=0; i< indent; i++)
      fill = fill + "    ";
    code = fill + "module " + this.getName () +" {\n";  // No I18N
    Node[] nodes = this.getChildren().getNodes ();
    for (int i=0; i<nodes.length; i++) {
      code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent + 1);
      code = code + "\n";
    }
    code = code + fill + "};\n";
    return code;
  }
  
}