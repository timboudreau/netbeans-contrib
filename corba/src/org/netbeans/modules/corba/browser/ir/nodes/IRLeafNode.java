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

package com.netbeans.enterprise.modules.corba.browser.ir.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;


public abstract class IRLeafNode extends IRAbstractNode {

  
  /** Creates new IRLeafNode */
  public IRLeafNode() {
    super(Children.LEAF);
  }
  
  public SystemAction[] createActions () {
    return new SystemAction[] {SystemAction.get(com.netbeans.enterprise.modules.corba.browser.ir.actions.GenerateCodeAction.class),
                                null,
                                SystemAction.get(org.openide.actions.PropertiesAction.class)
                                };
  }
  
  
}
