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
import org.netbeans.modules.corba.idl.src.MemberElement;

/**
 * Class IDLMemberNode
 *
 * @author Karel Gardas
 */
public class IDLMemberNode extends IDLAbstractNode {

    MemberElement _member;

    //private static final String MEMBER_ICON_BASE =
    //   "org/netbeans/modules/corba/idl/node/member"; // NOI18N
    private static final String MEMBER_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/declarator"; // NOI18N

    public IDLMemberNode (MemberElement value) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (MEMBER_ICON_BASE);
        _member = value;
        setCookieForDataObject (_member.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _member;
    }

    public String getName () {
        return "member"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_MEMBER) { // NOI18N
		public Object getValue () {
		    return _member.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_MEMBER) { // NOI18N
		public Object getValue () {
		    return _member.getType ();
		}
	    });
        return s;
    }


}

/*
 * $Log
 * $
 */
