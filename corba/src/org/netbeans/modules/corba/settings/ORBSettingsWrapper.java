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

public class ORBSettingsWrapper implements java.io.Serializable {

    public static final long serialVersionUID = -173181041826475252L;

    private static final boolean DEBUG = false;
    //private static final boolean DEBUG = true;
    
    private ORBSettings _M_settings;
    private String _M_value;
    
    public ORBSettingsWrapper () {
	if (DEBUG)
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper ()"); // NOI18N
    }  

    public ORBSettingsWrapper (ORBSettings __settings) {
	if (DEBUG) 
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper (" + __settings + ")"); // NOI18N
	_M_settings = __settings;
	if (_M_settings.getServerBindings ().size () > 0)
	    _M_value = (String)_M_settings.getServerBindings ().get (0);
	else
	    _M_value = ""; // NOI18N
    }

    public ORBSettingsWrapper (ORBSettings __settings, String __value) {
	if (DEBUG) 
	    System.out.println ("ORBSettingsWrapper::ORBSettingsWrapper (" + __settings + ", " // NOI18N
				+ __value + ")"); // NOI18N
	_M_settings = __settings;
	_M_value = __value;
    }

    public ORBSettings getSettings () {
	return _M_settings;
    }

    public void setSettings (ORBSettings __settings) {
	_M_settings = __settings;
    }

    public String getValue () {
	return _M_value;
    }

    public void setValue (String __value) {
	if (DEBUG)
	    System.out.println ("ORBSettingsWrapper::setValue (" + __value); // NOI18N
	_M_value = __value;
    }

    public boolean equals (Object __value) {
	if (DEBUG)
	    System.out.print ("ORBSettingsWrapper::equals (...) -> "); // NOI18N
	boolean __return = false;
	try {
	    ORBSettingsWrapper __tmp = (ORBSettingsWrapper)__value;
	    if (this.getSettings ().equals (__tmp.getSettings ()))
		if (this.getValue ().equals (__tmp.getValue ()))
		    __return = true;
	} catch (Exception __e) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		__e.printStackTrace ();
	}
	if (DEBUG)
	    System.out.println (__return);
	return __return;
    }

    public String toString () {
	return _M_value;
    }
}




