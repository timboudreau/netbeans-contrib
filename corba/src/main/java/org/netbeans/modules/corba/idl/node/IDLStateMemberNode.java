/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
        "org/netbeans/modules/corba/idl/node/state_member"; // NOI18N

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
    /*
      public String getDisplayName () {
      if (_M_state_member != null) {
      //return ((Identifier)_M_state_member.getMember (0)).getName ();
      return _M_declarator_element.getName ();
      }
      else
      return "NoName :)";
      }
    */
    public String getName () {
        return "attribute"; // NOI18N
    }

    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly ("name", String.class, IDLNodeBundle.NAME, IDLNodeBundle.NAME_OF_STATE) { // NOI18N
		public Object getValue () {
		    return _M_declarator_element.getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("type", String.class, IDLNodeBundle.TYPE, IDLNodeBundle.TYPE_OF_STATE) { // NOI18N
		public Object getValue () {
		    return _M_state_member.getType ().getName ();
		}
	    });
        ss.put (new PropertySupport.ReadOnly ("dimension", String.class, IDLNodeBundle.DIMENSION, // NOI18N
                                              IDLNodeBundle.DIMENSION_OF_STATE) {
		public Object getValue () {
		    String retval = ""; // NOI18N
		    Vector dim = _M_declarator_element.getDimension ();
		    for (int i=0; i<dim.size (); i++) {
			retval = retval + "[" + ((Integer)dim.elementAt (i)).toString () + "]"; // NOI18N
		    }
		    return retval;
		}
	    });

        ss.put (new PropertySupport.ReadOnly ("modifier", String.class, IDLNodeBundle.MODIFIER, // NOI18N
                                              IDLNodeBundle.MODIFIER_OF_STATE) {
		public Object getValue () {
		    if (_M_state_member.getModifier () == StateMemberElement.PUBLIC)
			return IDLNodeBundle.PUBLIC;
		    if (_M_state_member.getModifier () == StateMemberElement.PRIVATE)
			return IDLNodeBundle.PRIVATE;
		    return IDLNodeBundle.UNKNOWN;
		}
	    });
	
        return s;
    }


}

/*
 * $Log
 * $
 */
