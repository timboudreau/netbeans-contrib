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
	ss.put (new PropertySupport.ReadOnly ("supported", String.class, IDLNodeBundle.SUPPORTED, // NOI18N
					      IDLNodeBundle.SUPPORTS_INTERFACES) {
		public Object getValue () {
		    String __supports = ""; // NOI18N
		    Vector __tmp_supported = _value.getSupported ();
		    if (__tmp_supported.size () > 0) {
			for (int __i=0; __i<__tmp_supported.size (); __i++) {
			    //inher = inher + ((Identifier)_Value.getParents ().elementAt (i)).getName () 
			    __supports = __supports + (String)__tmp_supported.elementAt (__i)
				+ ", "; // NOI18N
			}
			__supports = __supports.substring (0, __supports.length () - 2);
		    }
		    else
			__supports = ""; // NOI18N
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

