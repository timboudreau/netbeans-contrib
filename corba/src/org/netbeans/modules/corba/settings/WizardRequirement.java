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
 * @version 0.01, Feb 19 2001
 */

public class WizardRequirement {
    
    private String _M_value;
    private String _M_title;
    private String _M_type;

    public WizardRequirement () {
	_M_value = "";
	_M_title = "";
	_M_type = "";
    }

    public WizardRequirement (String __value, String __title, String __type) {
	_M_value = __value;
	_M_title = __title;
	_M_type = __type;
    }
    
    public String getValue () {
	return _M_value;
    }

    public void setValue (String __value) {
	_M_value = __value;
    }

    public String getTitle () {
	return _M_title;
    }

    public void setTitle (String __title) {
	_M_title = __title;
    }

    public String getType () {
	return _M_type;
    }

    public void setType (String __type) {
	_M_type = __type;
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();

	__buf.append ("value: ");
	__buf.append (_M_value);
	__buf.append ("; title: ");
	__buf.append (_M_title);
	__buf.append ("; type: ");
	__buf.append (_M_type);
	
	return __buf.toString ();
    }
}

