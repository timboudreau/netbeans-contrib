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
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.ValueBoxElement;
import org.netbeans.modules.corba.idl.src.IDLType;
import org.netbeans.modules.corba.idl.src.TypeElement;

/**
 * Class IDLValueNode
 *
 * @author Karel Gardas
 */
public class IDLValueBoxNode extends IDLAbstractNode {

    ValueBoxElement _M_value;

    private static final String VALUE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/value"; // NOI18N

    public IDLValueBoxNode (ValueBoxElement value) {
        //super (new IDLDocumentChildren ((IDLElement)value));
        super (Children.LEAF);
        setIconBase (VALUE_ICON_BASE);
        _M_value = value;
        setCookieForDataObject (_M_value.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _M_value;
    }
    /*
      public String getDisplayName () {
      if (_M_value != null)
      return _M_value.getName ();
      else
      return "NoName :)";
      }
    */
    public String getName () {
        return "value box"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_VALUEBOX) { // NOI18N
                    public Object getValue () {
                        return _M_value.getName ();
                    }
                });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_VALUEBOX) { // NOI18N
		public Object getValue () {
		    IDLType __type = _M_value.getType ();
		    int __type_value = __type.getType ();
		    //System.out.println ("type: " + __type +  " value: " + __type_value); // NOI18N
		    if (__type.getType () != IDLType.STRUCT && __type.getType () != IDLType.UNION 
			&& __type.getType () != IDLType.ENUM) {
			//System.out.println ("simple type"); // NOI18N
			return __type.getName ();
		    }
		    else {
			//System.out.println ("constr type"); // NOI18N
			TypeElement __real_type 
			    = (TypeElement)_M_value.getMembers ().elementAt (1);
			return __type.getName () + " " + __real_type.getName (); // NOI18N
		    }
		}
	    });
	return s;
    }

}

/*
 * $Log
 * $
 */

