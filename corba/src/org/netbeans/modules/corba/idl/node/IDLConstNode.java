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
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;

import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;

import org.netbeans.modules.corba.idl.src.IDLElement;
import org.netbeans.modules.corba.idl.src.ConstElement;

/**
 * Class IDLConstNode
 *
 * @author Karel Gardas
 */
public class IDLConstNode extends IDLAbstractNode {

    ConstElement _const;
    private static final String CONST_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const"; // NOI18N

    public IDLConstNode (ConstElement value) {
        super (Children.LEAF);
        setIconBase (CONST_ICON_BASE);
        _const = value;
        setCookieForDataObject (_const.getDataObject ());
    }

    public IDLElement getIDLElement () {
        return _const;
    }
    /*
      public String getDisplayName () {
      if (_const != null)
      return _const.getName ();
      else
      return "NoName :)"; // NOI18N
      }
    */
    public String getName () {
        return "const"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }
    
    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_CONSTANT) { // NOI18N
		public Object getValue () {
		    return _const.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_CONSTANT) { // NOI18N
		public Object getValue () {
		    return _const.getType ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("exp", String.class, IDLNodeBundle.EXPRESSION, // NOI18N
                                              IDLNodeBundle.CONSTANT_EXPRESSION) {
		public Object getValue () {
		    if (_const.getExpression () != null)
			return _const.getExpression ();
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
