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

package org.netbeans.modules.corba.idl.src;

import java.util.Vector;

public class ValueAbsElement extends IDLElement {

    protected boolean is_abstract;
    private Vector _M_inherited;

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



