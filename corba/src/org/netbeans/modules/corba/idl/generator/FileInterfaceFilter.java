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

package org.netbeans.modules.corba.idl.generator;

import org.netbeans.modules.corba.idl.src.InterfaceElement;

import org.netbeans.modules.corba.utils.ObjectFilter;
import org.netbeans.modules.corba.utils.Assertion;

/*
 * @author Karel Gardas
 */

public class FileInterfaceFilter implements ObjectFilter {

    private String _M_file_name;

    public FileInterfaceFilter (String __file_name) {
	Assertion.assert (__file_name != null);
	_M_file_name = __file_name;
    }

    public boolean is (Object __value) {
	if (__value instanceof InterfaceElement
	    && ((InterfaceElement)__value).getFileName ().equals (_M_file_name))
	    return true;
	else
	    return false;
    }
}

