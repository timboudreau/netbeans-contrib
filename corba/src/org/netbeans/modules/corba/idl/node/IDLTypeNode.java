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

import org.openide.nodes.*;

import com.netbeans.enterprise.modules.corba.idl.src.*;

/**
 * Class IDLTypeNode
 *
 * @author Karel Gardas
 */
public class IDLTypeNode extends AbstractNode {

  TypeElement _type;
  private static final String TYPE_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/type";

  String name;

  public IDLTypeNode (TypeElement value) {
    super (new IDLDocumentChildren ((IDLElement)value));
    setIconBase (TYPE_ICON_BASE);
    _type = value;
    if (_type != null) {
      name = _type.getName ();
      //name = _type.getType ();
    }
    else 
      name = "NoName :)";
  }

  public String getDisplayName () {
    return name;
  }

  public String getName () {
    return "type";
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of typedef") {
      public Object getValue () {
	return _type.getName ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type") {
      public Object getValue () {
	return _type.getType ().getName ();
      }
    });
    return s;
  }
	    


}

/*
 * $Log
 * $
 */
