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
 * Class IDLOperationNode
 *
 * @author Karel Gardas
 */
public class IDLOperationNode extends AbstractNode {

   OperationElement _operation;
   private static final String OPERATION_ICON_BASE =
      "com/netbeans/enterprise/modules/corba/idl/node/operation";

   public IDLOperationNode (OperationElement value) {
      //super (new IDLDocumentChildren ((SimpleNode)value));
      super (Children.LEAF);
      setIconBase (OPERATION_ICON_BASE);
      _operation = value;
   }

   public String getDisplayName () {
      if (_operation != null)
	 return _operation.getName();
	 //	 return ((Identifier)_operation.jjtGetChild (0)).getName ();
      else 
	 return "NoName :)";
   }

   public String getName () {
      return "operation";
   }

   protected Sheet createSheet () {
      Sheet s = Sheet.createDefault ();
      Sheet.Set ss = s.get (Sheet.PROPERTIES);
      ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of operation") {
	 public Object getValue () {
	    return _operation.getName ();
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("result", String.class, "result", "type of result") {
	 public Object getValue () {
	    return _operation.getReturnType ();
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("attribute", String.class, "attribute", 
					    "attribute of operation") {
	 public Object getValue () {
	    if (_operation.getAttribute () != null)
	       return _operation.getAttribute ();
	    else
	       return "";
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("params", String.class, "parameters", 
					    "parameters of operation") {
	 public Object getValue () {
	    if (_operation.getParameters () != null) {
	       String params = "";
	       for (int i=0; i<_operation.getParameters ().size (); i++) {
		  ParameterElement param = (ParameterElement)_operation.getParameters ().
		     elementAt (i);
		  String attr = "";
		  switch (param.getAttribute ()) 
		     {
		     case 0: attr = "in"; break;
		     case 1: attr = "inout"; break;
		     case 2: attr = "out"; break;
		     }
		  params = params + attr + " " + param.getType () + " " + param.getName () + ", ";
	       }
	       // if operation has some parameters we will destroy last ", "
	       if (!params.equals (""))
		  params = params.substring (0, params.length () - 2);
	       return params;
	    }
	    else
	       return "";
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("exceptions", String.class, "exceptions", 
					    "exceptions which can operation thrown") {
	 public Object getValue () {
	    if (_operation.getExceptions () != null) {
	       String exs = "";
	       for (int i=0; i<_operation.getExceptions ().size (); i++) {
		  exs = exs + (String)_operation.getExceptions ().elementAt (i) + ", ";
	       }
	       if (!exs.equals (""))
		  exs = exs.substring (0, exs.length () - 2);
	       return exs;
	    }
	    else
	       return "";
	 }
      });
      ss.put (new PropertySupport.ReadOnly ("contexts", String.class, "contexts", 
					    "contexts") {
	 public Object getValue () {
	    if (_operation.getContexts () != null) {
	       String ctxs = "";
	       for (int i=0; i<_operation.getContexts ().size (); i++) {
		  ctxs = ctxs + (String)_operation.getContexts ().elementAt (i) + ", ";
	       }
	       if (!ctxs.equals (""))
		  ctxs = ctxs.substring (0, ctxs.length () - 2);
	       return ctxs;
	    }
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
