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
 * @version 0.01, Jan 8 2001
 */

public class ORBBindingDescriptor {

    private String _M_name;
    private String _M_template_tag;
    private String _M_local_tag;
    private String _M_import;
    private String _M_code;

    public ORBBindingDescriptor () {
    }

    public String getName () {
	return _M_name;
    }

    public void setName (String __value) {
	_M_name = __value;
    }

    public String getTemplateTag () {
	return _M_template_tag;
    }

    public void setTemplateTag (String __value) {
	_M_template_tag = __value;
    }

    public String getLocalTag () {
	return _M_template_tag;
    }

    public void setLocalTag (String __value) {
	_M_template_tag = __value;
    }

    public String getImport () {
	if (_M_import == null)
	    return "";
	return _M_import;
    }

    public void setImport (String __value) {
	_M_import = __value;
    }

    public String getCode () {
	if (_M_code == null)
	    return "";
	return _M_code;
    }

    public void setCode (String __value) {
	_M_code = __value;
    }

    public String toString () {
	StringBuffer __buf = new StringBuffer ();
	__buf.append ("name: ");
	__buf.append (_M_name);
	__buf.append (", template-tag: ");
	__buf.append (_M_template_tag);
	__buf.append (", local-tag: ");
	__buf.append (_M_local_tag);
	__buf.append ("\nimport: ");
	__buf.append (_M_import);
	__buf.append ("\ncode: ");
	__buf.append (_M_code);
	return __buf.toString ();
    }
}

