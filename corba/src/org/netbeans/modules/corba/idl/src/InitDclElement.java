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

import java.util.ArrayList;

public class InitDclElement extends IDLElement {

    ArrayList _M_parameters;

    public InitDclElement(int id) {
        super(id);
	_M_parameters = new ArrayList ();
    }

    public InitDclElement(IDLParser p, int id) {
        super(p, id);
	_M_parameters = new ArrayList ();
    }

    public void setParams (ArrayList __params) {
	_M_parameters = __params;
    }

    public ArrayList getParams () {
	return _M_parameters;
    }

}
