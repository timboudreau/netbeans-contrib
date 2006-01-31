/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * PropertyElement.java
 *
 * Code reused from Appserver common API module 
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.dd;

public interface PropertyElement {
    
        public static final String NAME = "Name";	// NOI18N
	public static final String VALUE = "Value";	// NOI18N
	public static final String DESCRIPTION = "Description";	// NOI18N
        
	public void setName(java.lang.String value);

	public java.lang.String getName();

	public void setValue(java.lang.String value);

	public java.lang.String getValue();

	public void setDescription(String value);

	public String getDescription();

}
