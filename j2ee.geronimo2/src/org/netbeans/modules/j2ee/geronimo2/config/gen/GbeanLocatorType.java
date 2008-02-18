/**
 *	This generated bean class GbeanLocatorType matches the schema element 'gbean-locatorType'.
 *  The root bean class is WebApp
 *
 *	===============================================================
 *	
 *	                The element web-container defines the binding between web
 *	                application and web container instance. It specifies which
 *	                container application should be deployed to.
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

public class GbeanLocatorType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String PATTERN = "Pattern";	// NOI18N
	static public final String GBEAN_LINK = "GbeanLink";	// NOI18N

	public GbeanLocatorType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public GbeanLocatorType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("pattern", 	// NOI18N
			PATTERN, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			PatternType.class);
		this.createProperty("gbean-link", 	// NOI18N
			GBEAN_LINK, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setPattern(PatternType value) {
		this.setValue(PATTERN, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setGbeanLink(null);
		}
	}

	//
	public PatternType getPattern() {
		return (PatternType)this.getValue(PATTERN);
	}

	// This attribute is mandatory
	public void setGbeanLink(java.lang.String value) {
		this.setValue(GBEAN_LINK, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setPattern(null);
		}
	}

	//
	public java.lang.String getGbeanLink() {
		return (java.lang.String)this.getValue(GBEAN_LINK);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public PatternType newPatternType() {
		return new PatternType();
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
		str.append("Pattern");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getPattern();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(PATTERN, 0, str, indent);

		str.append(indent);
		str.append("GbeanLink");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getGbeanLink();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(GBEAN_LINK, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("GbeanLocatorType\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>
<!--

    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
-->

<!-- $Rev: 573465 $ $Date: 2008/02/14 09:28:16 $ -->

<xs:schema
    xmlns:web="http://geronimo.apache.org/xml/ns/j2ee/web-2.0"
    targetNamespace="http://geronimo.apache.org/xml/ns/j2ee/web-2.0"
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
    <xs:import namespace="http://java.sun.com/xml/ns/persistence" schemaLocation="persistence-1.0.xsd"/>

    <xs:element name="web-app" type="web:web-appType"/>
    <xs:annotation>
        <xs:documentation>
            The web-app element is the root of the deployment descriptor for a Geronimo web  
            application. Note that the sub-elements of this element should be as in the given order because it is 
            defined as a sequence.
        </xs:documentation>
    </xs:annotation>
    <xs:annotation>
        <xs:documentation>
            This group keeps the usage of the contained JNDI environment
            reference elements consistent across J2EE deployment descriptors.
        </xs:documentation>
    </xs:annotation>

    <xs:complexType name="web-appType">
        <xs:sequence>
            <xs:element ref="sys:environment" minOccurs="0">
                <xs:annotation>
                    <xs:documentation>                         
                        This is the first part of the URL used to access the web application.
                        For example context-root of "Sample-App" will have URL of 
                        http://host:port/Sample-App" and a context-root of "/" would be make this the default web application to the server.

                        If the web application is packaged as an EAR that can use application context
                        in the "application.xml". This element is necessary unless you want context root to default to the WAR 
                        name.
                    </xs:documentation>
                </xs:annotation>
            </xs:element>

            <xs:element name="context-root" type="xs:string" minOccurs="0"/>
            <!--<xs:element name="context-priority-classloader" type="xs:boolean" minOccurs="0"/>-->
            <xs:element ref="naming:web-container" minOccurs="0"/>
            <xs:element name="container-config" type="web:container-configType" minOccurs="0">
                <xs:annotation>
                    <xs:documentation>
                        Geronimo supports both Jetty and Tomcat web containers. This element is
                        for a web application needs to take container specific settings. It can hold either a Tomcat element or a Jetty element or both.
                    </xs:documentation>
                </xs:annotation>
            </xs:element>

            <xs:group ref="naming:jndiEnvironmentRefsGroup"/>
            <xs:element ref="naming:message-destination" minOccurs="0" maxOccurs="unbounded"/>

            <xs:sequence minOccurs="0">
                <xs:element name="security-realm-name" type="xs:string"/>
                <xs:element ref="app:security" minOccurs="0"/>
            </xs:sequence>

            <xs:choice minOccurs="0" maxOccurs="unbounded">
                <xs:element ref="sys:service" minOccurs="0" maxOccurs="unbounded">
                    <xs:annotation>
                        <xs:documentation>
                            Reference to abstract service element defined in imported
                            "geronimo-module-1.2.xsd"
                        </xs:documentation>
                    </xs:annotation>
                </xs:element>
                <xs:element ref="ee:persistence"/>
            </xs:choice>
            

        </xs:sequence>
    </xs:complexType>

    <xs:complexType name="container-configType">
        <xs:sequence>
            <xs:any namespace="##other" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
        </xs:sequence>
    </xs:complexType>

</xs:schema>

*/
