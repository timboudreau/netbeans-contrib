/**
 *	This generated bean class ArtifactType matches the schema element 'artifactType'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	                
 *	                Refers to either another module running in the server, or
 *	                an entry in the server's Repository.  In either case this effectively uses a
 *	                URI.
 *	
 *	                When this is pointing to a repository entry, the URI must have a form
 *	                acceptable to the repository, which is currently a URI consisting of
 *	                Maven-style identifiers separated by slashes (groupId/artifactId/version/type,
 *	                for example, the URI "postgresql/postgresql-8.0-jdbc/313/jar" for a file like
 *	                "repository/postgresql/postgresql-8.0-jdbc-313.jar").
 *	
 *	                When this is pointing to a module, the URI should match the
 *	                module's moduleId.  This also looks
 *	                like a Maven-style URI discussed above.
 *	
 *	                The artifactType element can take either a straight URI (as in the examples
 *	                above), or maven-style identifier fragments (groupId, type, artifactId, and
 *	                version), which it will compose into a URI by adding up the fragments with
 *	                slashes in between.
 *	
 *	                There is a correspondence between the xml format and a URI.  For example, the URI
 *	
 *	                postgresql/postgresql-8.0-jdbc/313/jar
 *	
 *	                corresponds to the xml:
 *	
 *	                <groupId>postgresql</groupId>
 *	                <artifactId>postgresql-8.0-jdbc</artifactId>
 *	                <version>313</version>
 *	                <type>jar</type>
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

public class ArtifactType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String GROUPID = "GroupId";	// NOI18N
	static public final String ARTIFACTID = "ArtifactId";	// NOI18N
	static public final String VERSION = "Version";	// NOI18N
	static public final String TYPE = "Type";	// NOI18N
	static public final String GROUPID2 = "GroupId2";	// NOI18N
	static public final String ARTIFACTID2 = "ArtifactId2";	// NOI18N
	static public final String VERSION2 = "Version2";	// NOI18N
	static public final String TYPE2 = "Type2";	// NOI18N
	static public final String GROUPID3 = "GroupId3";	// NOI18N
	static public final String ARTIFACTID3 = "ArtifactId3";	// NOI18N
	static public final String VERSION3 = "Version3";	// NOI18N
	static public final String TYPE3 = "Type3";	// NOI18N

	public ArtifactType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public ArtifactType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(12);
		this.createProperty("groupId", 	// NOI18N
			GROUPID, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("artifactId", 	// NOI18N
			ARTIFACTID, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("version", 	// NOI18N
			VERSION, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("type", 	// NOI18N
			TYPE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("groupId", 	// NOI18N
			GROUPID2, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("artifactId", 	// NOI18N
			ARTIFACTID2, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("version", 	// NOI18N
			VERSION2, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("type", 	// NOI18N
			TYPE2, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("groupId", 	// NOI18N
			GROUPID3, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("artifactId", 	// NOI18N
			ARTIFACTID3, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("version", 	// NOI18N
			VERSION3, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.createProperty("type", 	// NOI18N
			TYPE3, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			java.lang.String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is optional
	public void setGroupId(java.lang.String value) {
		this.setValue(GROUPID, value);
	}

	//
	public java.lang.String getGroupId() {
		return (java.lang.String)this.getValue(GROUPID);
	}

	// This attribute is mandatory
	public void setArtifactId(java.lang.String value) {
		this.setValue(ARTIFACTID, value);
	}

	//
	public java.lang.String getArtifactId() {
		return (java.lang.String)this.getValue(ARTIFACTID);
	}

	// This attribute is optional
	public void setVersion(java.lang.String value) {
		this.setValue(VERSION, value);
	}

	//
	public java.lang.String getVersion() {
		return (java.lang.String)this.getValue(VERSION);
	}

	// This attribute is optional
	public void setType(java.lang.String value) {
		this.setValue(TYPE, value);
	}

	//
	public java.lang.String getType() {
		return (java.lang.String)this.getValue(TYPE);
	}

	// This attribute is optional
	public void setGroupId2(java.lang.String value) {
		this.setValue(GROUPID2, value);
	}

	//
	public java.lang.String getGroupId2() {
		return (java.lang.String)this.getValue(GROUPID2);
	}

	// This attribute is mandatory
	public void setArtifactId2(java.lang.String value) {
		this.setValue(ARTIFACTID2, value);
	}

	//
	public java.lang.String getArtifactId2() {
		return (java.lang.String)this.getValue(ARTIFACTID2);
	}

	// This attribute is optional
	public void setVersion2(java.lang.String value) {
		this.setValue(VERSION2, value);
	}

	//
	public java.lang.String getVersion2() {
		return (java.lang.String)this.getValue(VERSION2);
	}

	// This attribute is optional
	public void setType2(java.lang.String value) {
		this.setValue(TYPE2, value);
	}

	//
	public java.lang.String getType2() {
		return (java.lang.String)this.getValue(TYPE2);
	}

	// This attribute is optional
	public void setGroupId3(java.lang.String value) {
		this.setValue(GROUPID3, value);
	}

	//
	public java.lang.String getGroupId3() {
		return (java.lang.String)this.getValue(GROUPID3);
	}

	// This attribute is mandatory
	public void setArtifactId3(java.lang.String value) {
		this.setValue(ARTIFACTID3, value);
	}

	//
	public java.lang.String getArtifactId3() {
		return (java.lang.String)this.getValue(ARTIFACTID3);
	}

	// This attribute is optional
	public void setVersion3(java.lang.String value) {
		this.setValue(VERSION3, value);
	}

	//
	public java.lang.String getVersion3() {
		return (java.lang.String)this.getValue(VERSION3);
	}

	// This attribute is optional
	public void setType3(java.lang.String value) {
		this.setValue(TYPE3, value);
	}

	//
	public java.lang.String getType3() {
		return (java.lang.String)this.getValue(TYPE3);
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
		str.append("GroupId");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getGroupId();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(GROUPID, 0, str, indent);

		str.append(indent);
		str.append("ArtifactId");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getArtifactId();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ARTIFACTID, 0, str, indent);

		str.append(indent);
		str.append("Version");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getVersion();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(VERSION, 0, str, indent);

		str.append(indent);
		str.append("Type");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getType();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(TYPE, 0, str, indent);

		str.append(indent);
		str.append("GroupId2");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getGroupId2();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(GROUPID2, 0, str, indent);

		str.append(indent);
		str.append("ArtifactId2");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getArtifactId2();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ARTIFACTID2, 0, str, indent);

		str.append(indent);
		str.append("Version2");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getVersion2();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(VERSION2, 0, str, indent);

		str.append(indent);
		str.append("Type2");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getType2();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(TYPE2, 0, str, indent);

		str.append(indent);
		str.append("GroupId3");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getGroupId3();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(GROUPID3, 0, str, indent);

		str.append(indent);
		str.append("ArtifactId3");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getArtifactId3();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ARTIFACTID3, 0, str, indent);

		str.append(indent);
		str.append("Version3");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getVersion3();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(VERSION3, 0, str, indent);

		str.append(indent);
		str.append("Type3");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getType3();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(TYPE3, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("ArtifactType\n");	// NOI18N
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
