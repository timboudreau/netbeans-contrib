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

//import java.util.Vector;

import com.netbeans.enterprise.modules.corba.idl.src.*;

/**
 * Class IDLConstNode
 *
 * @author Karel Gardas
 */
public class IDLConstNode extends AbstractNode {

   ConstElement _const;
   private static final String CONST_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/const";

   public IDLConstNode (ConstElement value) {
      super (Children.LEAF);
      setIconBase (CONST_ICON_BASE);
      _const = value;
   }

   public String getDisplayName () {
      if (_const != null)
	 return _const.getName ();
      else 
	 return "NoName :)";
   }

   public String getName () {
      return "const";
   }

   protected Sheet createSheet () {
      Sheet s = Sheet.createDefault ();
      Sheet.Set ss = s.get (Sheet.PROPERTIES);
      ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of constant") {
	 public Object getValue () {
	    return _const.getName ();
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of constant") {
	 public Object getValue () {
	    return _const.getType ();
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("exp", String.class, "expression", 
					    "constant expression") {
	 public Object getValue () {
	    if (_const.getExpression () != null)
	       return _const.getExpression ();
	    else 
	       return "";
	 }
      });
      return s;
   }
	    

}

/*
 * $Log
 * $
 */
