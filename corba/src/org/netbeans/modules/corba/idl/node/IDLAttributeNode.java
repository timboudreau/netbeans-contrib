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
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import com.netbeans.enterprise.modules.corba.idl.src.IDLElement;
import com.netbeans.enterprise.modules.corba.idl.src.AttributeElement;

/**
 * Class IDLAttributeNode
 *
 * @author Karel Gardas
 */
public class IDLAttributeNode extends IDLAbstractNode {

  AttributeElement _attribute;
  private static final String ATTRIBUTE_ICON_BASE =
    "com/netbeans/enterprise/modules/corba/idl/node/attribute";

  public IDLAttributeNode (AttributeElement value) {
    //super (new IDLDocumentChildren ((SimpleNode)value));
    super (Children.LEAF);
    setIconBase (ATTRIBUTE_ICON_BASE);
    _attribute = value;
    setCookieForDataObject (_attribute.getDataObject ());
  }

  public IDLElement getIDLElement () {
    return _attribute;
  }

  public String getDisplayName () {
    if (_attribute != null) {
      //return ((Identifier)_attribute.getMember (0)).getName ();
      return _attribute.getName ();
    }
    else 
      return "NoName :)";
  }

  public String getName () {
    return "attribute";
  }

  public SystemAction getDefaultAction () {
    SystemAction result = super.getDefaultAction();
    return result == null ? SystemAction.get(OpenAction.class) : result;
  }

  protected Sheet createSheet () {
    Sheet s = Sheet.createDefault ();
    Sheet.Set ss = s.get (Sheet.PROPERTIES);
    ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of attribute") {
      public Object getValue () {
	return _attribute.getName ();
      }
    });
    ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of attribute") {
      public Object getValue () {
	return (_attribute.getType ()).getName ();
      }
    });
    /*
      ss.put (new PropertySupport.ReadOnly ("other", String.class, "other", 
      "other attribute whith same type") {
      public Object getValue () {
      String other;
      if (_attribute.getOther () != null) 
      if (_attribute.getOther ().size () > 0) {
      other = (String)_attribute.getOther ().elementAt (0);
      for (int i=1; i<_attribute.getOther ().size (); i++)
      other = other + ", " + (String)_attribute.getOther ().elementAt (i);
      return other;
      }
      else 
      return "";
      else
      return "";
      }
      });
    */
    ss.put (new PropertySupport.ReadOnly ("readonly", String.class, "readonly", 
					  "readonly attribute") {
      public Object getValue () {
	if (_attribute.getReadOnly ())
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
