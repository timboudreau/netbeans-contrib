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

import org.netbeans.modules.corba.idl.src.UnionTypeElement;
import org.netbeans.modules.corba.idl.src.TypeElement;

/**
 * Class IDLUnionTypeNode
 *
 * @author Karel Gardas
 */
public class IDLUnionTypeNode extends IDLTypeNode {

    private static final String UNION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/union"; // NOI18N

    private UnionTypeElement _union_type;

    public IDLUnionTypeNode (TypeElement value) {
        super (value);
        _union_type = (UnionTypeElement) value;
        setIconBase (UNION_ICON_BASE);
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_UNION) { // NOI18N
		public Object getValue () {
		    return _type.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE) { // NOI18N
		public Object getValue () {
		    return _type.getType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("switch type", String.class, IDLNodeBundle.SWITCH_TYPE, IDLNodeBundle.SWITCH_TYPE) { // NOI18N
		public Object getValue () {
		    return _union_type.getSwitchType ();
		}
	    });
        return s;
    }

}

/*
 * $Log
 * $
 */
