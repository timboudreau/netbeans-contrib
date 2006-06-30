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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class ValueAbsElement extends IDLElement {

    protected boolean is_abstract;
    private Vector _M_inherited;
    private Vector _M_supported;

    public ValueAbsElement(int id) {
        super(id);
        is_abstract = false;
        _M_inherited = new Vector ();
    }

    public ValueAbsElement(IDLParser p, int id) {
        super(p, id);
        is_abstract = false;
        _M_inherited = new Vector ();
    }

    public void setParents (Vector parents) {
        _M_inherited = parents;
    }

    public Vector getParents () {
        return _M_inherited;
    }

    public void setSupported (Vector __value) {
	_M_supported = __value;
    }

    public Vector getSupported () {
	return _M_supported;
    }

    public void setAbstract (boolean value) {
        is_abstract = value;
    }

    public boolean isAbstract () {
        //return is_abstract;
	return true;
    }

    public void jjtClose () {
        super.jjtClose ();
        Vector __members = super.getMembers ();
	//java.util.List __tm = new java.util.ArrayList ();
	//__tm.addAll (__members);
	//System.out.println (this + " -> __tm: " + __tm);
	try {
	    if (__members.size () > 0) {
		ValueInheritanceSpecElement __inheritance
		    = (ValueInheritanceSpecElement)__members.get (1);
		//System.out.println ("inherited: " + __inheritance.getValues ()); // NOI18N
		this.setParents (__inheritance.getValues ());
                //System.out.println ("supports: " + __inheritance.getInterfaces ()); // NOI18N
	        this.setSupported (__inheritance.getInterfaces ());
	    }
	} catch (ClassCastException __ex) {
	    // this valuetype don't inherits any value
	} catch (Exception __ex) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) { // NOI18N
		__ex.printStackTrace ();
		//System.out.println ("__ex for element: " + this + " from file: " + this.getFileName ());
	    }
	}

    }

}



