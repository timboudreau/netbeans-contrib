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
 * Class IDLUnionTypeNode
 *
 * @author Karel Gardas
 */
public class IDLUnionTypeNode extends IDLTypeNode {

   private static final String UNION_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/union";
     
   private UnionTypeElement _union_type;
   public IDLUnionTypeNode (UnionTypeElement value) {
      super (value);
      _union_type = value;
      setIconBase (UNION_ICON_BASE);
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
	    return _type.getType ();
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("switch type", String.class, "switch type", 
					    "switch type") {
	 public Object getValue () {
	    return _union_type.getSwitchType ();
	 }
      });
      return s;
   }
	    
}

/*
 * $Log
 * $
 */
