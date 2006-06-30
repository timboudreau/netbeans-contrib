/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
import org.netbeans.modules.corba.idl.src.UnionMemberElement;
import org.netbeans.modules.corba.idl.src.DeclaratorElement;
/**
 * Class IDLUnionMemberNode
 *
 * @author Karel Gardas
 */
public class IDLUnionMemberNode extends IDLAbstractNode {

    UnionMemberElement _unionmember;

    private static final String UNIONMEMBER_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/unionmember"; // NOI18N

    public IDLUnionMemberNode (UnionMemberElement value) {
        //super (new IDLDocumentChildren ((SimpleNode)value));
        super (Children.LEAF);
        setIconBase (UNIONMEMBER_ICON_BASE);
        _unionmember = value;
        setCookieForDataObject (_unionmember.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _unionmember;
    }

    public String getName () {
        return "unionmember"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_UNION_MEMBER) { // NOI18N
		public Object getValue () {
		    return _unionmember.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_UNION_MEMBER) { // NOI18N
		public Object getValue () {
		    return _unionmember.getType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("dimension", String.class, IDLNodeBundle.DIMENSION, // NOI18N
                                              IDLNodeBundle.DIMENSION_OF_DECLARATOR) {
		public Object getValue () {
		    return ((DeclaratorElement)_unionmember.getMember
			    (_unionmember.getMembers ().size () -1 )).getDimension ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("case", String.class, IDLNodeBundle.CASE, // NOI18N
                                              IDLNodeBundle.CASE_OF_UNION_MEMBER) {
		public Object getValue () {
		    return _unionmember.getCases ();
		}
	    });
	
        return s;
    }


}

/*
 * $Log
 * $
 */
