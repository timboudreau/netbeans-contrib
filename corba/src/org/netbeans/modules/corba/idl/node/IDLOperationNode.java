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

package org.netbeans.modules.corba.idl.node;

import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.OperationElement;
import org.netbeans.modules.corba.idl.src.ParameterElement;

/**
 * Class IDLOperationNode
 *
 * @author Karel Gardas
 */
public class IDLOperationNode extends IDLAbstractNode {

    OperationElement _operation;
    private static final String OPERATION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/operation"; // NOI18N

    public IDLOperationNode (OperationElement value) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (OPERATION_ICON_BASE);
        _operation = value;
        setCookieForDataObject (_operation.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _operation;
    }
    /*
      public String getDisplayName () {
      if (_operation != null)
      return _operation.getName();
      //	 return ((Identifier)_operation.jjtGetChild (0)).getName ();
      else
      return "NoName :)";
      }
    */
    public String getName () {
        return "operation"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_OPERATION) { // NOI18N
		public Object getValue () {
		    return _operation.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("result", String.class, IDLNodeBundle.RESULT, IDLNodeBundle.TYPE_OF_RESULT) { // NOI18N
		public Object getValue () {
		    return _operation.getReturnType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("attribute", String.class, IDLNodeBundle.ATTRIBUTE, // NOI18N
                                              IDLNodeBundle.ATTRIBUTE_OF_OPERATION) {
		public Object getValue () {
		    if (_operation.getAttribute () != null)
			return _operation.getAttribute ();
		    else
			return ""; // NOI18N
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("params", String.class, IDLNodeBundle.PARAMETERS, // NOI18N
                                              IDLNodeBundle.PARAMETERS_OF_OPERATION) {
		public Object getValue () {
		    if (_operation.getParameters () != null) {
			String params = ""; // NOI18N
			for (int i=0; i<_operation.getParameters ().size (); i++) {
			    ParameterElement param = (ParameterElement)_operation.getParameters ().
				elementAt (i);
			    String attr = ""; // NOI18N
			    switch (param.getAttribute ())
                                {
                                case 0: attr = "in"; break; // NOI18N
                                case 1: attr = "inout"; break; // NOI18N
                                case 2: attr = "out"; break; // NOI18N
                                }
			    params = params + attr + " " + param.getType ().getName () + " " // NOI18N
				+ param.getName () + ", "; // NOI18N
			}
			// if operation has some parameters we will destroy last ", " // NOI18N
			if (!params.equals ("")) // NOI18N
			    params = params.substring (0, params.length () - 2);
			return params;
		    }
		    else
			return ""; // NOI18N
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("exceptions", String.class, IDLNodeBundle.EXCEPTIONS, IDLNodeBundle.EXCEPTIONS_OF_OPERATION) { // NOI18N
		public Object getValue () {
		    if (_operation.getExceptions () != null) {
			String exs = ""; // NOI18N
			for (int i=0; i<_operation.getExceptions ().size (); i++) {
			    exs = exs + (String)_operation.getExceptions ().elementAt (i) + ", "; // NOI18N
			}
			if (!exs.equals ("")) // NOI18N
			    exs = exs.substring (0, exs.length () - 2);
			return exs;
		    }
		    else
			return ""; // NOI18N
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("contexts", String.class, IDLNodeBundle.CONTEXTS, // NOI18N
                                              IDLNodeBundle.CONTEXTS_OF_OPERATION) {
		public Object getValue () {
		    if (_operation.getContexts () != null) {
			String ctxs = ""; // NOI18N
			for (int i=0; i<_operation.getContexts ().size (); i++) {
			    ctxs = ctxs + (String)_operation.getContexts ().elementAt (i) + ", "; // NOI18N
			}
			if (!ctxs.equals ("")) // NOI18N
			    ctxs = ctxs.substring (0, ctxs.length () - 2);
			return ctxs;
		    }
		    else
			return ""; // NOI18N
		}
	    });
	
        return s;
    }
    

}

/*
 * $Log
 * $
 */
