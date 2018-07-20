/**
 *	This generated bean class ClassFilterType matches the schema element 'classFilterType'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	                        A list of classes which will only be loaded from parent
 *	                        ClassLoaders of this module (never from the module's own
 *	                        ClassLoader). For example, this is used to prevent a web
 *	                        application from redefining "javax.servlet", so those
 *	                        classes will *always* be loaded from the server instead
 *	                        of from the web web application's own ClassPath.
 *	
 *	                        The classes are specified in zero or more child "filter"
 *	                        elements where each filter element specifies a
 *	                        fully-qualified class name or prefix. Essentially, any
 *	                        class that starts with one of the prefixes listed here
 *	                        will be treated as hidden. For example, specifying two
 *	                        filter elements containing "javax.servlet" and
 *	                        "javax.ejb" would protect some of the core J2EE classes
 *	                        from being overridden.
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

public class ClassFilterType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String FILTER = "Filter";	// NOI18N
	static public final String FILTER2 = "Filter2";	// NOI18N
	static public final String FILTER3 = "Filter3";	// NOI18N

	public ClassFilterType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public ClassFilterType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(3);
		this.createProperty("filter", 	// NOI18N
			FILTER, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("filter", 	// NOI18N
			FILTER2, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("filter", 	// NOI18N
			FILTER3, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setFilter(int index, java.lang.String value) {
		this.setValue(FILTER, index, value);
	}

	//
	public java.lang.String getFilter(int index) {
		return (java.lang.String)this.getValue(FILTER, index);
	}

	// Return the number of properties
	public int sizeFilter() {
		return this.size(FILTER);
	}

	// This attribute is an array, possibly empty
	public void setFilter(java.lang.String[] value) {
		this.setValue(FILTER, value);
	}

	//
	public java.lang.String[] getFilter() {
		return (java.lang.String[])this.getValues(FILTER);
	}

	// Add a new element returning its index in the list
	public int addFilter(java.lang.String value) {
		int positionOfNewItem = this.addValue(FILTER, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFilter(java.lang.String value) {
		return this.removeValue(FILTER, value);
	}

	// This attribute is an array, possibly empty
	public void setFilter2(int index, java.lang.String value) {
		this.setValue(FILTER2, index, value);
	}

	//
	public java.lang.String getFilter2(int index) {
		return (java.lang.String)this.getValue(FILTER2, index);
	}

	// Return the number of properties
	public int sizeFilter2() {
		return this.size(FILTER2);
	}

	// This attribute is an array, possibly empty
	public void setFilter2(java.lang.String[] value) {
		this.setValue(FILTER2, value);
	}

	//
	public java.lang.String[] getFilter2() {
		return (java.lang.String[])this.getValues(FILTER2);
	}

	// Add a new element returning its index in the list
	public int addFilter2(java.lang.String value) {
		int positionOfNewItem = this.addValue(FILTER2, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFilter2(java.lang.String value) {
		return this.removeValue(FILTER2, value);
	}

	// This attribute is an array, possibly empty
	public void setFilter3(int index, java.lang.String value) {
		this.setValue(FILTER3, index, value);
	}

	//
	public java.lang.String getFilter3(int index) {
		return (java.lang.String)this.getValue(FILTER3, index);
	}

	// Return the number of properties
	public int sizeFilter3() {
		return this.size(FILTER3);
	}

	// This attribute is an array, possibly empty
	public void setFilter3(java.lang.String[] value) {
		this.setValue(FILTER3, value);
	}

	//
	public java.lang.String[] getFilter3() {
		return (java.lang.String[])this.getValues(FILTER3);
	}

	// Add a new element returning its index in the list
	public int addFilter3(java.lang.String value) {
		int positionOfNewItem = this.addValue(FILTER3, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFilter3(java.lang.String value) {
		return this.removeValue(FILTER3, value);
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
		str.append("Filter["+this.sizeFilter()+"]");	// NOI18N
		for(int i=0; i<this.sizeFilter(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFilter(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FILTER, i, str, indent);
		}

		str.append(indent);
		str.append("Filter2["+this.sizeFilter2()+"]");	// NOI18N
		for(int i=0; i<this.sizeFilter2(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFilter2(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FILTER2, i, str, indent);
		}

		str.append(indent);
		str.append("Filter3["+this.sizeFilter3()+"]");	// NOI18N
		for(int i=0; i<this.sizeFilter3(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFilter3(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FILTER3, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("ClassFilterType\n");	// NOI18N
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
