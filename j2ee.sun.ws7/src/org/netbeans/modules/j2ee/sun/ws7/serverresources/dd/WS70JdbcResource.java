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
/*
 * WS0JdbcResource.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.dd;

/**
 * Code reused from Appserver common API module
 */
public interface WS70JdbcResource {

	static public final String JNDINAME = "JndiName";	// NOI18N
	static public final String DATASOURCECLASS = "DatasourceClass";	// NOI18N
	static public final String MINCONNECTIONS = "MinConnections";	// NOI18N
	static public final String MAXCONNECTIONS = "MaxConnections";	// NOI18N
	static public final String IDLETIMEOUT = "IdleTimeout";	// NOI18N
	static public final String WAITTIMEOUT = "WaitTimeout";	// NOI18N
	static public final String ISOLATIONLEVEL = "IsolationLevel";	// NOI18N
	static public final String ISOLATIONLEVELGUARANTEED = "IsolationLevelGuaranteed";	// NOI18N
	static public final String CONNECTIONVALIDATION = "ConnectionValidation";	// NOI18N
	static public final String CONNECTIONVALIDATIONTABLENAME = "ConnectionValidationTableName";	// NOI18N
	static public final String FAILALLCONNECTIONS = "FailAllConnections";	// NOI18N
	static public final String ENABLED = "Enabled";	// NOI18N
	static public final String DESCRIPTION = "Description";	// NOI18N
	static public final String PROPERTY = "PropertyElement";	// NOI18N// NOI18N
        
	/** Setter for jndi-name property
        * @param value property value
        */
	public void setJndiName(java.lang.String value);
        /** Getter for jndi-name property
        * @return property value
        */
	public java.lang.String getJndiName();
	// This attribute is mandatory
	public void setDatasourceClass(java.lang.String value);

	//
	public java.lang.String getDatasourceClass();

	// This attribute is mandatory
	public void setMinConnections(java.lang.String value);

	//
	public java.lang.String getMinConnections();

	// This attribute is mandatory
	public void setMaxConnections(java.lang.String value);
	//
	public java.lang.String getMaxConnections();

	// This attribute is mandatory
	public void setIdleTimeout(java.lang.String value);

	//
	public java.lang.String getIdleTimeout();

	// This attribute is mandatory
	public void setWaitTimeout(java.lang.String value);

	//
	public java.lang.String getWaitTimeout();

	// This attribute is mandatory
	public void setIsolationLevel(java.lang.String value);

	//
	public java.lang.String getIsolationLevel();

	// This attribute is mandatory
	public void setIsolationLevelGuaranteed(java.lang.String value);

	//
	public java.lang.String getIsolationLevelGuaranteed();

	// This attribute is mandatory
	public void setConnectionValidation(java.lang.String value);

	//
	public java.lang.String getConnectionValidation();
	// This attribute is optional
	public void setConnectionValidationTableName(java.lang.String value);
	//
	public java.lang.String getConnectionValidationTableName();
	// This attribute is mandatory
	public void setFailAllConnections(java.lang.String value);

	//
	public java.lang.String getFailAllConnections();
        /** Setter for enabled property
        * @param value property value
        */
	public void setEnabled(java.lang.String value);
        /** Getter for enabled property
        * @return property value
        */
	public java.lang.String getEnabled();
        /** Setter for description attribute
        * @param value attribute value
        */
	public void setDescription(String value);
        /** Getter for description attribute
        * @return attribute value
        */
	public String getDescription();

	public void setPropertyElement(int index, PropertyElement value);
	public PropertyElement getPropertyElement(int index);
	public int sizePropertyElement();
	public void setPropertyElement(PropertyElement[] value);
	public PropertyElement[] getPropertyElement();
	public int addPropertyElement(PropertyElement value);
	public int removePropertyElement(PropertyElement value);
	public PropertyElement newPropertyElement();

}
