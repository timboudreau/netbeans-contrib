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

package com.netbeans.enterprise.modules.corba.idl.node;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import com.netbeans.enterprise.modules.corba.idl.src.IDLElement;
import com.netbeans.enterprise.modules.corba.idl.src.ModuleElement;


/**
 * Class IDLModuleNode
 *
 * @author Karel Gardas
 */

public class IDLModuleNode extends IDLAbstractNode {

  ModuleElement _module;

  private static final String MODULE_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/module";

  public IDLModuleNode (ModuleElement value) {
    super (new IDLDocumentChildren ((IDLElement)value));
    setIconBase (MODULE_ICON_BASE);
    _module = value;
    setCookieForDataObject (_module.getDataObject ());
  }

  public IDLElement getIDLElement () {
    return _module;
  }

  public String getDisplayName () {
    if (_module != null)
      //return ((Identifier)_interface.jjtGetChild (0)).getName ();
      return _module.getName ();
    else 
      return "NoName :)";
  }

  public String getName () {
    return "module";
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of module") {
	public Object getValue () {
	  return _module.getName ();
	}
      });

    return s;
  }

}

/*
 * $Log
 * $
 */
