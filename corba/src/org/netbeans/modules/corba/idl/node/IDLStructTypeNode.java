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
 * Class IDLStructTypeNode
 *
 * @author Karel Gardas
 */
public class IDLStructTypeNode extends IDLTypeNode {

   public static final String STRUCT_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/struct";
     
   public IDLStructTypeNode (TypeElement value) {
      super (value);
      //System.out.println ("IDLStructTypeNode...");
      setIconBase (STRUCT_ICON_BASE);
   }
	    
}

/*
 * $Log
 * $
 */
