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

import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.StateMemberElement;
import org.netbeans.modules.corba.idl.src.DeclaratorElement;

/**
 * Class IDLStateMemberNode
 *
 * @author Karel Gardas
 */
public class IDLStateMemberNode extends IDLAbstractNode {

    StateMemberElement _M_state_member;
    DeclaratorElement _M_declarator_element;
    private static final String STATE_MEMBER_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/state_member";

    //public IDLStateMemberNode (StateMemberElement value) {
    public IDLStateMemberNode (DeclaratorElement __declarator, StateMemberElement __state) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (STATE_MEMBER_ICON_BASE);
	_M_declarator_element = __declarator;
        _M_state_member = __state;
        setCookieForDataObject (_M_state_member.getDataObject ());
    }

    public IDLElement getIDLElement () {
        //return _M_state_member;
	return _M_declarator_element;
    }

    public String getDisplayName () {
        if (_M_state_member != null) {
            //return ((Identifier)_M_state_member.getMember (0)).getName ();
            return _M_declarator_element.getName ();
        }
        else
            return "NoName :)";
    }

    public String getName () {
        return "attribute";
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, "name", "name of state") {
		public Object getValue () {
		    return _M_declarator_element.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, "type", "type of state") {
		public Object getValue () {
		    return _M_state_member.getType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("dimension", String.class, "dimension",
                                              "dimension of state") {
		public Object getValue () {
		    String retval = "";
		    Vector dim = _M_declarator_element.getDimension ();
		    for (int i=0; i<dim.size (); i++) {
			retval = retval + "[" + ((Integer)dim.elementAt (i)).toString () + "]";
		    }
		    return retval;
		}
	    });

        ss.put (new PropertySupport.ReadOnly ("modifier", String.class, "modifier",
                                              "state modifier") {
		public Object getValue () {
		    if (_M_state_member.getModifier () == StateMemberElement.PUBLIC)
			return "public";
		    if (_M_state_member.getModifier () == StateMemberElement.PRIVATE)
			return "private";
		    return "unknown";
		}
	    });
	
        return s;
    }


}

/*
 * $Log
 * $
 */
