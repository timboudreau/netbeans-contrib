/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.api.web.dd;
import org.netbeans.api.web.dd.common.*;
/**
 * Generated interface for EjbRef element.
 *
 *<p><b><font color="red"><em>Important note: Do not provide an implementation of this interface unless you are a DD API provider!</em></font></b>
 *</p>
 * @deprecated Use the API for web module deployment descriptor in j2ee/ddapi module.
 */
public interface EjbRef extends CommonDDBean, DescriptionInterface {
        /** Setter for ejb-ref-name property.
         * @param value property value
         */
	public void setEjbRefName(java.lang.String value);
        /** Getter for ejb-ref-name property.
         * @return property value 
         */
	public java.lang.String getEjbRefName();
        /** Setter for ejb-ref-type property.
         * @param value property value
         */
	public void setEjbRefType(java.lang.String value);
        /** Getter for ejb-ref-type property.
         * @return property value 
         */
	public java.lang.String getEjbRefType();
        /** Setter for home property.
         * @param value property value
         */
	public void setHome(java.lang.String value);
        /** Getter for home property.
         * @return property value 
         */
	public java.lang.String getHome();
        /** Setter for remote property.
         * @param value property value
         */
	public void setRemote(java.lang.String value);
        /** Getter for remote property.
         * @return property value 
         */
	public java.lang.String getRemote();
        /** Setter for ejb-link property.
         * @param value property value
         */
	public void setEjbLink(java.lang.String value);
        /** Getter for ejb-link property.
         * @return property value 
         */
	public java.lang.String getEjbLink();
}
