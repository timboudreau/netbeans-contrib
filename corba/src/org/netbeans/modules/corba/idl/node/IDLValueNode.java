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

import java.util.Vector;

import org.openide.nodes.CookieSet;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.ValueElement;

/**
 * Class IDLValueNode
 *
 * @author Karel Gardas
 */
public class IDLValueNode extends IDLAbstractNode {

    ValueElement _value;

    private static final String VALUE_ICON_BASE =
	"org/netbeans/modules/corba/idl/node/value";

    public IDLValueNode (ValueElement value) {
	super (new IDLDocumentChildren ((IDLElement)value));
	setIconBase (VALUE_ICON_BASE);
	_value = value;
	setCookieForDataObject (_value.getDataObject ());
    }

    public String getDisplayName () {
	if (_value != null)
	    //return ((Identifier)_Value.jjtGetChild (0)).getName ();
	    return _value.getName ();
	else 
	    return "NoName :)";
    }

    public IDLElement getIDLElement () {
	return _value;
    }

    public String getName () {
	return "Value";
    }

    public SystemAction getDefaultAction () {
	SystemAction result = super.getDefaultAction();
	return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
	//System.out.println ("IDLValueNode::createSheet ();");
	Sheet s = Sheet.createDefault ();
	Sheet.Set ss = s.get (Sheet.PROPERTIES);
	ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of value") {
		public Object getValue () {
		    return _value.getName ();
		}
	    });
	ss.put (new PropertySupport.ReadOnly ("custom", String.class, "custom", "is value custom") {
		public Object getValue () {
		    if (_value.isCustom ())
			return "yes";
		    else
			return "no";
		}
	    });
	ss.put (new PropertySupport.ReadOnly ("inherited", String.class, "inherited", 
					      "inherited from") {
		public Object getValue () {
		    String inher = "";
		    if (_value.getParents ().size () > 0) {
			for (int i=0; i<_value.getParents ().size (); i++)
			    //inher = inher + ((Identifier)_Value.getParents ().elementAt (i)).getName () 
			    inher = inher + (String)_value.getParents ().elementAt (i)
				+ ", ";
			inher = inher.substring (0, inher.length () - 2);
		    }
		    else
			inher = "";
		    return inher;
		}
	    });

	ss.put (new PropertySupport.ReadOnly ("supported", String.class, "supported", 
					      "supports interface(s)") {
		public Object getValue () {
		    String __supports = "";
		    Vector __tmp_supported = _value.getSupported ();
		    if (__tmp_supported.size () > 0) {
			for (int __i=0; __i<__tmp_supported.size (); __i++) {
			    //inher = inher + ((Identifier)_Value.getParents ().elementAt (i)).getName () 
			    __supports = __supports + (String)__tmp_supported.elementAt (__i)
				+ ", ";
			}
			__supports = __supports.substring (0, __supports.length () - 2);
		    }
		    else
			__supports = "";
		    return __supports;
		}
	    });

	return s;
    }

}

/*
 * $Log
 * $
 */

