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

import java.util.Vector;

import org.openide.nodes.*;

import com.netbeans.enterprise.modules.corba.idl.src.*;

/**
 * Class IDLDeclaratorNode
 *
 * @author Karel Gardas
 */
public class IDLDeclaratorNode extends AbstractNode {

  DeclaratorElement _declarator;
  String name;

  private static final String DECLARATOR_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/declarator";

  public IDLDeclaratorNode (DeclaratorElement value) {
    //super (new IDLDocumentChildren ((SimpleNode)value));
    super (Children.LEAF);
    setIconBase (DECLARATOR_ICON_BASE);
    _declarator = value;
    if (_declarator != null) {
      /*
	for (int i=0; i<_declarator.getDeclarators ().size (); i++)  {
	if (_declarator.getDeclarator (i) instanceof Identifier) {
	name = ((Identifier)_declarator.getDeclarator (i)).getName ();
	System.out.println ("found name: " + name + " at " + i + " position");
	}
	}
	}
      */
      name = _declarator.getName ();
    }
    else 
      name = "NoName :)";
  }

  public String getDisplayName () {
    return name;
  }

  public String getName () {
    return "declarator";
  }


  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of declarator") {
      public Object getValue () {
	return _declarator.getName ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of declarator") {
      public Object getValue () {
	return _declarator.getType ().getName ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("dimension", String.class, "dimension", 
					  "dimension of declarator") {
      public Object getValue () {
	String retval = "";
	Vector dim = _declarator.getDimension ();
	for (int i=0; i<dim.size (); i++) {
	  retval = retval + "[" + ((Integer)dim.elementAt (i)).toString () + "]";
	}
	return retval;
      }
    });

    return s;
  }
	    

}

/*
 * $Log
 * $
 */


