/**
 *	This generated bean class EjbRefType matches the schema element 'ejb-refType'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	                        The element ejb-ref is used to map EJB references to
 *	                        EJB's in other applications using remote home and remote
 *	                        interface. The application which contains the EJB being
 *	                        referenced should either be in same EAR or should be
 *	                        included in dependency list of this application. Also
 *	                        note as the EJB's referenced are in a different JVM all
 *	                        the Client interfaces should also be included in current
 *	                        application.
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

public class EjbRefType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String REF_NAME = "RefName";	// NOI18N
	static public final String PATTERN = "Pattern";	// NOI18N
	static public final String NS_CORBALOC = "NsCorbaloc";	// NOI18N
	static public final String NAME = "Name";	// NOI18N
	static public final String CSS = "Css";	// NOI18N
	static public final String CSS_LINK = "CssLink";	// NOI18N
	static public final String EJB_LINK = "EjbLink";	// NOI18N

	public EjbRefType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public EjbRefType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(7);
		this.createProperty("ref-name", 	// NOI18N
			REF_NAME, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("pattern", 	// NOI18N
			PATTERN, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			PatternType.class);
		this.createProperty("ns-corbaloc", 	// NOI18N
			NS_CORBALOC, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.net.URI.class);
		this.createProperty("name", 	// NOI18N
			NAME, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("css", 	// NOI18N
			CSS, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			PatternType.class);
		this.createProperty("css-link", 	// NOI18N
			CSS_LINK, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("ejb-link", 	// NOI18N
			EJB_LINK, Common.SEQUENCE_OR | 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setRefName(java.lang.String value) {
		this.setValue(REF_NAME, value);
	}

	//
	public java.lang.String getRefName() {
		return (java.lang.String)this.getValue(REF_NAME);
	}

	// This attribute is mandatory
	public void setPattern(PatternType value) {
		this.setValue(PATTERN, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setNsCorbaloc(null);
			setName(null);
			setCss(null);
			setCssLink(null);
			setEjbLink(null);
		}
	}

	//
	public PatternType getPattern() {
		return (PatternType)this.getValue(PATTERN);
	}

	// This attribute is mandatory
	public void setNsCorbaloc(java.net.URI value) {
		this.setValue(NS_CORBALOC, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setPattern(null);
			setEjbLink(null);
		}
	}

	//
	public java.net.URI getNsCorbaloc() {
		return (java.net.URI)this.getValue(NS_CORBALOC);
	}

	// This attribute is mandatory
	public void setName(java.lang.String value) {
		this.setValue(NAME, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setPattern(null);
			setEjbLink(null);
		}
	}

	//
	public java.lang.String getName() {
		return (java.lang.String)this.getValue(NAME);
	}

	// This attribute is mandatory
	public void setCss(PatternType value) {
		this.setValue(CSS, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setCssLink(null);
			setPattern(null);
			setEjbLink(null);
		}
	}

	//
	public PatternType getCss() {
		return (PatternType)this.getValue(CSS);
	}

	// This attribute is mandatory
	public void setCssLink(java.lang.String value) {
		this.setValue(CSS_LINK, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setCss(null);
			setPattern(null);
			setEjbLink(null);
		}
	}

	//
	public java.lang.String getCssLink() {
		return (java.lang.String)this.getValue(CSS_LINK);
	}

	// This attribute is mandatory
	public void setEjbLink(java.lang.String value) {
		this.setValue(EJB_LINK, value);
		if (value != null) {
			// It's a mutually exclusive property.
			setPattern(null);
			setNsCorbaloc(null);
			setName(null);
			setCss(null);
			setCssLink(null);
		}
	}

	//
	public java.lang.String getEjbLink() {
		return (java.lang.String)this.getValue(EJB_LINK);
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
		str.append("RefName");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRefName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(REF_NAME, 0, str, indent);

		str.append(indent);
		str.append("Pattern");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getPattern();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(PATTERN, 0, str, indent);

		str.append(indent);
		str.append("NsCorbaloc");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getNsCorbaloc();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(NS_CORBALOC, 0, str, indent);

		str.append(indent);
		str.append("Name");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(NAME, 0, str, indent);

		str.append(indent);
		str.append("Css");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getCss();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(CSS, 0, str, indent);

		str.append(indent);
		str.append("CssLink");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getCssLink();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CSS_LINK, 0, str, indent);

		str.append(indent);
		str.append("EjbLink");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getEjbLink();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(EJB_LINK, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("EjbRefType\n");	// NOI18N
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
