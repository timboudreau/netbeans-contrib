/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.web.dd;
import org.netbeans.api.web.dd.common.*;
/**
 * Generated interface for LoginConfig element.
 *
 */
public interface LoginConfig extends CommonDDBean, CreateCapability {
        /** Setter for auth-method property.
         * @param value property value
         */
	public void setAuthMethod(java.lang.String value);
        /** Getter for auth-method property.
         * @return property value 
         */
	public java.lang.String getAuthMethod();
        /** Setter for realm-name property.
         * @param value property value
         */
	public void setRealmName(java.lang.String value);
        /** Getter for realm-name property.
         * @return property value 
         */
	public java.lang.String getRealmName();
        /** Setter for form-login-config element.
         * @param valueInterface form-login-config element (FormLoginConfig object)
         */
	public void setFormLoginConfig(org.netbeans.api.web.dd.FormLoginConfig valueInterface);
        /** Getter for form-login-config element.
         * @return form-login-config element (FormLoginConfig object)
         */
	public org.netbeans.api.web.dd.FormLoginConfig getFormLoginConfig();

}
