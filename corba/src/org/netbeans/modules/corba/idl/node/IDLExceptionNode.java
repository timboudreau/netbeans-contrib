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
 * Class IDLExceptionNode
 *
 * @author Karel Gardas
 */
public class IDLExceptionNode extends AbstractNode {

   ExceptionElement _exception;
   private static final String EXCEPTION_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/exception";

   public IDLExceptionNode (ExceptionElement value) {
      super (new IDLDocumentChildren ((Element)value));
      setIconBase (EXCEPTION_ICON_BASE);
      _exception = value;
   }

   public String getDisplayName () {
      if (_exception != null)
	 return ((Identifier)_exception.jjtGetChild (0)).getName ();
      else 
	 return "NoName :)";
   }

   public String getName () {
      return "exception";
   }

}

/*
 * $Log
 * $
 */
