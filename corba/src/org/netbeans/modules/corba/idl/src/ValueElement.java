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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class ValueElement extends ValueAbsElement {

    private boolean _M_is_custom;

    public ValueElement(int id) {
        super(id);
        _M_is_custom = false;
    }

    public ValueElement(IDLParser p, int id) {
        super(p, id);
        _M_is_custom = false;
    }

    public boolean isAbstract () {
        //return is_abstract; // because ValueElement is never abstract
	return false;
    }

    public void setCustom (boolean value) {
        _M_is_custom = value;
    }

    public boolean isCustom () {
        return _M_is_custom;
    }
    /*
      public void setInherited (Vector __value) {
      _M_inherited = __value;
      }
      
      public Vector getInherited () {
      return _M_inherited;
      }
    */

    public void jjtClose () {
        super.jjtClose ();
        Vector __members = super.getMembers ();
	Vector __new_members = new Vector ();
	Object __element;
	// translates states -> sates with one DeclaratorElement
	for (int __i=0; __i<__members.size (); __i++) {
	    __element = __members.elementAt (__i);
	    if (__element instanceof StateMemberElement) {
		
	    }
	    else {
		__new_members.add (__element);
	    }
	} 
	//System.out.println ("members: " + __members); // NOI18N
        ValueHeaderElement __header = (ValueHeaderElement)__members.elementAt (0);
	//System.out.println ("members of header: " + __header.getMembers ()); // NOI18N
	try {
	    ValueInheritanceSpecElement __inheritance
		= (ValueInheritanceSpecElement)__header.getMembers ().elementAt (1);
	    //System.out.println ("inherited: " + __inheritance.getValues ()); // NOI18N
	    this.setParents (__inheritance.getValues ());
	    //System.out.println ("supports: " + __inheritance.getInterfaces ()); // NOI18N
	    this.setSupported (__inheritance.getInterfaces ());
	} catch (ClassCastException __ex) {
	    // this valuetype don't inherits or supports any value or interface(s)
	} catch (Exception __ex) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) { // NOI18N
		__ex.printStackTrace ();
	    }
	}
        this.setName (__header.getName ());
	this.setLine (__header.getLine ());
	this.setColumn (__header.getColumn ());
        this.setCustom (__header.isCustom ());
	this.setFileName (__header.getFileName ());
    }

}


