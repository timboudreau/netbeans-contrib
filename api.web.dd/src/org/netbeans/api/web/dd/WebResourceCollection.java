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
 * Generated interface for WebResourceCollection element.
 *
 */
public interface WebResourceCollection extends CommonDDBean, DescriptionInterface {
        /** Setter for web-resource-name property.
         * @param value property value
         */
	public void setWebResourceName(java.lang.String value);
        /** Getter for web-resource-name property.
         * @return property value 
         */
	public java.lang.String getWebResourceName();
        /** Setter for url-pattern property.
         * @param index position in the array of url-patterns
         * @param value property value 
         */
	public void setUrlPattern(int index, java.lang.String value);
        /** Getter for url-pattern property.
         * @param index position in the array of url-patterns
         * @return property value 
         */
	public java.lang.String getUrlPattern(int index);
        /** Setter for url-pattern property.
         * @param index position in the array of url-patterns
         * @param value array of url-pattern properties
         */
	public void setUrlPattern(java.lang.String[] value);
        /** Getter for url-pattern property.
         * @return array of url-pattern properties
         */
	public java.lang.String[] getUrlPattern();
        /** Returns size of url-pattern properties.
         * @return number of url-pattern properties 
         */
	public int sizeUrlPattern();
        /** Adds url-pattern property.
         * @param value url-pattern property
         * @return index of new url-pattern
         */
	public int addUrlPattern(java.lang.String value);
        /** Removes url-pattern property.
         * @param value url-pattern property
         * @return index of the removed url-pattern
         */
	public int removeUrlPattern(java.lang.String value);
        /** Setter for http-method property.
         * @param index position in the array of http-methods
         * @param value property value 
         */
	public void setHttpMethod(int index, java.lang.String value);
        /** Getter for http-method property.
         * @param index position in the array of http-methods
         * @return property value 
         */
	public java.lang.String getHttpMethod(int index);
        /** Setter for http-method property.
         * @param index position in the array of http-methods
         * @param value array of http-method properties
         */
	public void setHttpMethod(java.lang.String[] value);
        /** Getter for http-method property.
         * @return array of http-method properties
         */
	public java.lang.String[] getHttpMethod();
        /** Returns size of http-method properties.
         * @return number of http-method properties 
         */
	public int sizeHttpMethod();
        /** Adds http-method property.
         * @param value http-method property
         * @return index of new http-method
         */
	public int addHttpMethod(java.lang.String value);
        /** Removes http-method property.
         * @param value http-method property
         * @return index of the removed http-method
         */
	public int removeHttpMethod(java.lang.String value);

}
