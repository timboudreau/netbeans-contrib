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


/*
 * @author Karel Gardas
 */

public class RecursiveInheritanceException extends Exception {

    int _M_line;
    String _M_interface_name;

    public RecursiveInheritanceException () {
        super ();
    }

    public void setLine (int __value) {
	_M_line = __value;
    }

    public int getLine () {
	return _M_line;
    }

    public void setName (String __value) {
	_M_interface_name = __value;
    }

    public String getName () {
	return _M_interface_name;
    }
}

