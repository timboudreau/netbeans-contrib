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
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import com.netbeans.enterprise.modules.corba.idl.src.*;

/**
 * Class IDLInterfaceNode
 *
 * @author Karel Gardas
 */
public class IDLInterfaceNode extends AbstractNode {

   InterfaceElement _interface;

   private static final String INTERFACE_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/interface";

   public IDLInterfaceNode (InterfaceElement value) {
      super (new IDLDocumentChildren ((Element)value));
      setIconBase (INTERFACE_ICON_BASE);
      _interface = value;
   }

   public String getDisplayName () {
      if (_interface != null)
	 //return ((Identifier)_interface.jjtGetChild (0)).getName ();
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
      ss.put (new PropertySupport.ReadOnly ("inherited", String.class, "inherited", 
					    "inherited from") {
	 public Object getValue () {
	    String inher = "";
	    if (_interface.getParents ().size () > 0) {
	       for (int i=0; i<_interface.getParents ().size (); i++)
		  inher = inher + ((Identifier)_interface.getParents ().elementAt (i)).getName () 
		     + ", ";
	       inher = inher.substring (0, inher.length () - 2);
	    }
	    else
	       inher = "";
	    return inher;
	 }
      });

      return s;
   }

}

/*
 * $Log
 * $
 */
