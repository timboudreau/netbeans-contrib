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

public class InitParamDeclElement extends IDLElement  {

    private IDLType _M_type;

    public InitParamDeclElement(int id) {
	super(id);
    }

    public InitParamDeclElement(IDLParser p, int id) {
	super(p, id);
    }

    public void setType (IDLType __type) {
	_M_type = __type;
    }

    public IDLType getType () {
	return _M_type;
    }

    public String toString () {
	try {
	    return super.toString () + ": type = " + this.getType ().toString ();
	} catch (Exception __ex) {
	    return super.toString () + ": type = null";
	}
    }

}
