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

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.ValueAbsElement;

/**
 * Class IDLValueNode
 *
 * @author Karel Gardas
 */
public class IDLValueAbsNode extends IDLAbstractNode {

    ValueAbsElement _value;

    private static final String VALUE_ICON_BASE =
	"org/netbeans/modules/corba/idl/node/value"; // NOI18N

    public IDLValueAbsNode (ValueAbsElement value) {
	super (new IDLDocumentChildren ((IDLElement)value));
	setIconBase (VALUE_ICON_BASE);
	_value = value;
	setCookieForDataObject (_value.getDataObject ());
    }

    public IDLElement getIDLElement () {
	return _value;
    }
    /*
      public String getDisplayName () {
      if (_value != null)
      //return ((Identifier)_Value.jjtGetChild (0)).getName ();
      return _value.getName ();
      else 
      return "NoName :)";
      }
    */
    public String getName () {
	return "value"; // NOI18N
    }

    public SystemAction getDefaultAction () {
	SystemAction result = super.getDefaultAction();
	return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
	Sheet s = Sheet.createDefault ();
	Sheet.Set ss = s.get (Sheet.PROPERTIES);
	ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_VALUE) { // NOI18N
		public Object getValue () {
		    return _value.getName ();
		}
	    });
	ss.put (new PropertySupport.ReadOnly ("abstract", String.class, IDLNodeBundle.ABSTRACT, IDLNodeBundle.ABSTRACT_VALUE) { // NOI18N
		public Object getValue () {
		    if (_value.isAbstract ())
			return IDLNodeBundle.YES;
		    else
			return IDLNodeBundle.NO;
		}
	    });
	ss.put (new PropertySupport.ReadOnly ("inherited", String.class, IDLNodeBundle.INHERITED, // NOI18N
					      IDLNodeBundle.INHERITED_FROM) {
		public Object getValue () {
		    String inher = ""; // NOI18N
		    if (_value.getParents ().size () > 0) {
			for (int i=0; i<_value.getParents ().size (); i++)
			    //inher = inher + ((Identifier)_Value.getParents ().elementAt (i)).getName () 
			    inher = inher + (String)_value.getParents ().elementAt (i)
				+ ", "; // NOI18N
			inher = inher.substring (0, inher.length () - 2);
		    }
		    else
			inher = ""; // NOI18N
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

