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
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.actions.RenameAction;
import org.netbeans.modules.corba.wizard.nodes.actions.DestroyAction;
import org.netbeans.modules.corba.wizard.nodes.keys.NamedKey;
/** 
 *
 * @author  root
 * @version 
 */
public abstract class AbstractMutableLeafNode extends AbstractMutableIDLNode {

  /** Creates new AbstractMutableLeafNode */
  public AbstractMutableLeafNode (NamedKey key) {
    super (Children.LEAF, key);
  }
  
  
  public SystemAction[] createActions () {
    return new SystemAction[] { SystemAction.get(DestroyAction.class),
                                 SystemAction.get(RenameAction.class)
    };
  }
}