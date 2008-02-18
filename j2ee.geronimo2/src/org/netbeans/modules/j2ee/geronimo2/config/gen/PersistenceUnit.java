/**
 *	This generated bean class PersistenceUnit matches the schema element 'persistence-unit'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	
 *	                Configuration of a persistence unit.
 *	
 *	              
 *	===============================================================
 * @Generated
 */

package org.netbeans.modules.j2ee.geronimo2.config.gen;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class PersistenceUnit extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String EENAME = "EeName";	// NOI18N
	static public final String EETRANSACTIONTYPE = "EeTransactionType";	// NOI18N
	static public final String DESCRIPTION = "Description";	// NOI18N
	static public final String PROVIDER = "Provider";	// NOI18N
	static public final String JTA_DATA_SOURCE = "JtaDataSource";	// NOI18N
	static public final String NON_JTA_DATA_SOURCE = "NonJtaDataSource";	// NOI18N
	static public final String MAPPING_FILE = "MappingFile";	// NOI18N
	static public final String JAR_FILE = "JarFile";	// NOI18N
	static public final String CLASS2 = "Class2";	// NOI18N
	static public final String EXCLUDE_UNLISTED_CLASSES = "ExcludeUnlistedClasses";	// NOI18N
	static public final String PROPERTIES = "Properties";	// NOI18N

	public PersistenceUnit() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public PersistenceUnit(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(9);
		this.createProperty("description", 	// NOI18N
			DESCRIPTION, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("provider", 	// NOI18N
			PROVIDER, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("jta-data-source", 	// NOI18N
			JTA_DATA_SOURCE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("non-jta-data-source", 	// NOI18N
			NON_JTA_DATA_SOURCE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("mapping-file", 	// NOI18N
			MAPPING_FILE, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("jar-file", 	// NOI18N
			JAR_FILE, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("class", 	// NOI18N
			CLASS2, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("exclude-unlisted-classes", 	// NOI18N
			EXCLUDE_UNLISTED_CLASSES, 
			Common.TYPE_0_1 | Common.TYPE_BOOLEAN | Common.TYPE_SHOULD_NOT_BE_EMPTY | Common.TYPE_KEY, 
			Boolean.class);
		this.createProperty("properties", 	// NOI18N
			PROPERTIES, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Properties.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {
		if ((options & Common.USE_DEFAULT_VALUES) == Common.USE_DEFAULT_VALUES) {
			setEeTransactionType("JTA");
		}

	}

	// This attribute is mandatory
	public void setEeName(java.lang.String value) {
		setAttributeValue(EENAME, value);
	}

	//
	public java.lang.String getEeName() {
		return getAttributeValue(EENAME);
	}

	// This attribute is mandatory
	public void setEeTransactionType(java.lang.String value) {
		setAttributeValue(EETRANSACTIONTYPE, value);
	}

	//
	public java.lang.String getEeTransactionType() {
		return getAttributeValue(EETRANSACTIONTYPE);
	}

	// This attribute is optional
	public void setDescription(java.lang.String value) {
		this.setValue(DESCRIPTION, value);
	}

	//
	public java.lang.String getDescription() {
		return (java.lang.String)this.getValue(DESCRIPTION);
	}

	// This attribute is optional
	public void setProvider(java.lang.String value) {
		this.setValue(PROVIDER, value);
	}

	//
	public java.lang.String getProvider() {
		return (java.lang.String)this.getValue(PROVIDER);
	}

	// This attribute is optional
	public void setJtaDataSource(java.lang.String value) {
		this.setValue(JTA_DATA_SOURCE, value);
	}

	//
	public java.lang.String getJtaDataSource() {
		return (java.lang.String)this.getValue(JTA_DATA_SOURCE);
	}

	// This attribute is optional
	public void setNonJtaDataSource(java.lang.String value) {
		this.setValue(NON_JTA_DATA_SOURCE, value);
	}

	//
	public java.lang.String getNonJtaDataSource() {
		return (java.lang.String)this.getValue(NON_JTA_DATA_SOURCE);
	}

	// This attribute is an array, possibly empty
	public void setMappingFile(int index, java.lang.String value) {
		this.setValue(MAPPING_FILE, index, value);
	}

	//
	public java.lang.String getMappingFile(int index) {
		return (java.lang.String)this.getValue(MAPPING_FILE, index);
	}

	// Return the number of properties
	public int sizeMappingFile() {
		return this.size(MAPPING_FILE);
	}

	// This attribute is an array, possibly empty
	public void setMappingFile(java.lang.String[] value) {
		this.setValue(MAPPING_FILE, value);
	}

	//
	public java.lang.String[] getMappingFile() {
		return (java.lang.String[])this.getValues(MAPPING_FILE);
	}

	// Add a new element returning its index in the list
	public int addMappingFile(java.lang.String value) {
		int positionOfNewItem = this.addValue(MAPPING_FILE, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeMappingFile(java.lang.String value) {
		return this.removeValue(MAPPING_FILE, value);
	}

	// This attribute is an array, possibly empty
	public void setJarFile(int index, java.lang.String value) {
		this.setValue(JAR_FILE, index, value);
	}

	//
	public java.lang.String getJarFile(int index) {
		return (java.lang.String)this.getValue(JAR_FILE, index);
	}

	// Return the number of properties
	public int sizeJarFile() {
		return this.size(JAR_FILE);
	}

	// This attribute is an array, possibly empty
	public void setJarFile(java.lang.String[] value) {
		this.setValue(JAR_FILE, value);
	}

	//
	public java.lang.String[] getJarFile() {
		return (java.lang.String[])this.getValues(JAR_FILE);
	}

	// Add a new element returning its index in the list
	public int addJarFile(java.lang.String value) {
		int positionOfNewItem = this.addValue(JAR_FILE, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeJarFile(java.lang.String value) {
		return this.removeValue(JAR_FILE, value);
	}

	// This attribute is an array, possibly empty
	public void setClass2(int index, java.lang.String value) {
		this.setValue(CLASS2, index, value);
	}

	//
	public java.lang.String getClass2(int index) {
		return (java.lang.String)this.getValue(CLASS2, index);
	}

	// Return the number of properties
	public int sizeClass2() {
		return this.size(CLASS2);
	}

	// This attribute is an array, possibly empty
	public void setClass2(java.lang.String[] value) {
		this.setValue(CLASS2, value);
	}

	//
	public java.lang.String[] getClass2() {
		return (java.lang.String[])this.getValues(CLASS2);
	}

	// Add a new element returning its index in the list
	public int addClass2(java.lang.String value) {
		int positionOfNewItem = this.addValue(CLASS2, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeClass2(java.lang.String value) {
		return this.removeValue(CLASS2, value);
	}

	// This attribute is optional
	public void setExcludeUnlistedClasses(boolean value) {
		this.setValue(EXCLUDE_UNLISTED_CLASSES, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isExcludeUnlistedClasses() {
		Boolean ret = (Boolean)this.getValue(EXCLUDE_UNLISTED_CLASSES);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
	}

	// This attribute is optional
	public void setProperties(Properties value) {
		this.setValue(PROPERTIES, value);
	}

	//
	public Properties getProperties() {
		return (Properties)this.getValue(PROPERTIES);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Properties newProperties() {
		return new Properties();
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Description");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getDescription();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(DESCRIPTION, 0, str, indent);

		str.append(indent);
		str.append("Provider");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getProvider();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PROVIDER, 0, str, indent);

		str.append(indent);
		str.append("JtaDataSource");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getJtaDataSource();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(JTA_DATA_SOURCE, 0, str, indent);

		str.append(indent);
		str.append("NonJtaDataSource");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getNonJtaDataSource();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(NON_JTA_DATA_SOURCE, 0, str, indent);

		str.append(indent);
		str.append("MappingFile["+this.sizeMappingFile()+"]");	// NOI18N
		for(int i=0; i<this.sizeMappingFile(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getMappingFile(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(MAPPING_FILE, i, str, indent);
		}

		str.append(indent);
		str.append("JarFile["+this.sizeJarFile()+"]");	// NOI18N
		for(int i=0; i<this.sizeJarFile(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getJarFile(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(JAR_FILE, i, str, indent);
		}

		str.append(indent);
		str.append("Class2["+this.sizeClass2()+"]");	// NOI18N
		for(int i=0; i<this.sizeClass2(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getClass2(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(CLASS2, i, str, indent);
		}

		str.append(indent);
		str.append("ExcludeUnlistedClasses");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append((this.isExcludeUnlistedClasses()?"true":"false"));
		this.dumpAttributes(EXCLUDE_UNLISTED_CLASSES, 0, str, indent);

		str.append(indent);
		str.append("Properties");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getProperties();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(PROPERTIES, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("PersistenceUnit\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--

  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.

-->

<xs:schema
    xmlns:openejb="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    targetNamespace="http://geronimo.apache.org/xml/ns/j2ee/ejb/openejb-2.0"
    xmlns:naming="http://geronimo.apache.org/xml/ns/naming-1.2"
    xmlns:app="http://geronimo.apache.org/xml/ns/j2ee/application-2.0"
    xmlns:sys="http://geronimo.apache.org/xml/ns/deployment-1.2"
    xmlns:ee="http://java.sun.com/xml/ns/persistence"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    elementFormDefault="qualified"
    attributeFormDefault="unqualified"
    version="1.0">

    <xs:import namespace="http://geronimo.apache.org/xml/ns/naming-1.2" schemaLocation="geronimo-naming-1.2.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/j2ee/application-2.0" schemaLocation="geronimo-application-2.0.xsd"/>
    <xs:import namespace="http://geronimo.apache.org/xml/ns/deployment-1.2" schemaLocation="geronimo-module-1.2.xsd"/>
    <xs:import namespace="http://java.sun.com/xml/ns/persistence" schemaLocation="http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"/>

    <xs:element name="ejb-jar" type="openejb:geronimo-ejb-jarType"/>

    <xs:complexType name="emptyType"/>

    <xs:complexType name="geronimo-ejb-jarType">
        <xs:sequence>
            <xs:element ref="sys:environment" minOccurs="0"/>

            <xs:element name="openejb-jar" type="openejb:openejb-jarType" minOccurs="0"/>

            <!-- Naming -->
            <xs:group ref="naming:jndiEnvironmentRefsGroup" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element ref="naming:message-destination" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="tss-link" type="openejb:tss-linkType" minOccurs="0" maxOccurs="unbounded"/>

            <xs:element name="web-service-binding" type="openejb:web-service-bindingType" minOccurs="0" maxOccurs="unbounded"/>

            <!-- Security -->
            <xs:element ref="app:security" minOccurs="0"/>

            <!-- GBeans -->
            <xs:choice minOccurs="0" maxOccurs="unbounded">
                <xs:element ref="sys:service"/>
                <xs:element ref="ee:persistence"/>
            </xs:choice>
        </xs:sequence>
    </xs:complexType>

    <!-- TODO there is no need for the extra wrapper other then xmlbean is overly enforcing the unique particle attribution rule -->
    <xs:complexType name="openejb-jarType">
        <xs:sequence>
            <xs:any namespace="##other" processContents="lax"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="tss-linkType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string" minOccurs="0"/>
            <xs:element name="tss-name" type="xs:string" minOccurs="0"/>
            <xs:element name="jndi-name" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-bindingType">
        <xs:sequence>
            <xs:element name="ejb-name" type="xs:string"/>
            <xs:element name="web-service-address" type="xs:string" minOccurs="0"/>
            <xs:element name="web-service-virtual-host" type="xs:string" minOccurs="0" maxOccurs="unbounded"/>
            <xs:element name="web-service-security" type="openejb:web-service-securityType" minOccurs="0"/>
        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="web-service-securityType">
        <xs:sequence>
            <xs:element name="security-realm-name" type="xs:string"/>
            <xs:element name="realm-name" type="xs:string" minOccurs="0"/>
            <xs:element name="transport-guarantee" type="openejb:transport-guaranteeType"/>
            <xs:element name="auth-method" type="openejb:auth-methodType"/>
        </xs:sequence>
    </xs:complexType>

    <xs:simpleType name="transport-guaranteeType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="NONE"/>
            <xs:enumeration value="INTEGRAL"/>
            <xs:enumeration value="CONFIDENTIAL"/>
        </xs:restriction>
    </xs:simpleType>

    <xs:simpleType name="auth-methodType">
        <xs:restriction base="xs:string">
            <xs:enumeration value="BASIC"/>
            <xs:enumeration value="DIGEST"/>
            <xs:enumeration value="CLIENT-CERT"/>
            <xs:enumeration value="NONE"/>
        </xs:restriction>
    </xs:simpleType>

</xs:schema>

*/
