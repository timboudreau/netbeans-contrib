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

import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import com.netbeans.enterprise.modules.corba.idl.src.IDLElement;
import com.netbeans.enterprise.modules.corba.idl.src.UnionMemberElement;
import com.netbeans.enterprise.modules.corba.idl.src.DeclaratorElement;
/**
 * Class IDLUnionMemberNode
 *
 * @author Karel Gardas
 */
public class IDLUnionMemberNode extends IDLAbstractNode {

  UnionMemberElement _unionmember;
  String name;

  private static final String UNIONMEMBER_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/unionmember";

  public IDLUnionMemberNode (UnionMemberElement value) {
    //super (new IDLDocumentChildren ((SimpleNode)value));
    super (Children.LEAF);
    setIconBase (UNIONMEMBER_ICON_BASE);
    _unionmember = value;
    setCookieForDataObject (_unionmember.getDataObject ());
    if (_unionmember != null) {
      /*
	for (int i=0; i<_unionmember.getUnionMembers ().size (); i++)  {
	if (_unionmember.getUnionMember (i) instanceof Identifier) {
	name = ((Identifier)_unionmember.getUnionMember (i)).getName ();
	System.out.println ("found name: " + name + " at " + i + " position");
	}
	}
	}
      */
      name = _unionmember.getName ();
    }
    else 
      name = "NoName :)";
  }

  public IDLElement getIDLElement () {
    return _unionmember;
  }

  public String getDisplayName () {
    return name;
  }

  public String getName () {
    return "unionmember";
  }

  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of union member") {
	public Object getValue () {
	  return _unionmember.getName ();
	}
      });
    ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of union member") {
	public Object getValue () {
	  return _unionmember.getType ().getName ();
	}
      });
    ss.put (new PropertySupport.ReadOnly ("dimension", String.class, "dimension", 
					  "dimension of union member") {
	public Object getValue () {
	  return ((DeclaratorElement)_unionmember.getMember 
		  (_unionmember.getMembers ().size () -1 )).getDimension ();
	}
      });
    ss.put (new PropertySupport.ReadOnly ("case", String.class, "case", 
					  "case of union member") {
	public Object getValue () {
	  return _unionmember.getCases ();
	}
      });

    return s;
  }
	    

}

/*
 * $Log
 * $
 */
