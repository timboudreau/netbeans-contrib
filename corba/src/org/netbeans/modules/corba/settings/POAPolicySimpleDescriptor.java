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

package org.netbeans.modules.corba.settings;

/*
 * @author Karel Gardas
 * @version 0.01, Jan 15 2001
 */

public class POAPolicySimpleDescriptor {

    private String _M_name;
    private String _M_value;

    public POAPolicySimpleDescriptor () {
    }

    public String getName () {
	return _M_name;
    }

    public void setName (String __value) {
	_M_name = __value;
    }

    public String getValue () {
	return _M_value;
    }

    public void setValue (String __value) {
	_M_value = __value;
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	__buf.append ("name: ");
	__buf.append (_M_name);
	__buf.append (", value: ");
	__buf.append (_M_value);
	return __buf.toString ();
    }

}



