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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.InterfaceForwardElement;

/**
 * Class IDLInterfaceNode
 *
 * @author Karel Gardas
 */
public class IDLInterfaceForwardNode extends IDLAbstractNode {

  InterfaceForwardElement _interface;

  private static final String INTERFACE_ICON_BASE =
    "org/netbeans/modules/corba/idl/node/interface";

  public IDLInterfaceForwardNode (InterfaceForwardElement value) {
    //super (new IDLDocumentChildren ((IDLElement)value));
    super (Children.LEAF);
    setIconBase (INTERFACE_ICON_BASE);
    _interface = value;
    setCookieForDataObject (_interface.getDataObject ());
  }

  public IDLElement getIDLElement () {
    return _interface;
  }

  public String getDisplayName () {
    if (_interface != null)
      return _interface.getName ();
    else 
      return "NoName :)";
  }

  public String getName () {
    return "interface";
  }

  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of interface") {
      public Object getValue () {
	return _interface.getName ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("abstract", String.class, "abstract", "is interface abstract") {
      public Object getValue () {
	if (_interface.isAbstract ())
	  return "yes";
	else
	  return "no";
      }
    });

    return s;
  }

}

/*
 * $Log
 * $
 */

