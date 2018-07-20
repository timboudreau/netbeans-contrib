/**
 *	This generated bean class EnvironmentType matches the schema element 'environmentType'.
 *  The root bean class is EjbJar
 *
 *	===============================================================
 *	
 *	                The server-environment element is used only by Application
 *	                Client modules to define server side module environment
 *	                settings. It defines elements to store information like
 *	                moduleId, dependencies, and classloader Info for the server-side
 *	                of client application module. This information is used to
 *	                identify the module in the server environment only.
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

public class EnvironmentType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String MODULEID = "ModuleId";	// NOI18N
	static public final String DEPENDENCIES = "Dependencies";	// NOI18N
	static public final String HIDDEN_CLASSES = "HiddenClasses";	// NOI18N
	static public final String NON_OVERRIDABLE_CLASSES = "NonOverridableClasses";	// NOI18N
	static public final String INVERSE_CLASSLOADING = "InverseClassloading";	// NOI18N
	static public final String SUPPRESS_DEFAULT_ENVIRONMENT = "SuppressDefaultEnvironment";	// NOI18N
	static public final String MODULEID2 = "ModuleId2";	// NOI18N
	static public final String DEPENDENCIES2 = "Dependencies2";	// NOI18N
	static public final String HIDDEN_CLASSES2 = "HiddenClasses2";	// NOI18N
	static public final String NON_OVERRIDABLE_CLASSES2 = "NonOverridableClasses2";	// NOI18N
	static public final String INVERSE_CLASSLOADING2 = "InverseClassloading2";	// NOI18N
	static public final String SUPPRESS_DEFAULT_ENVIRONMENT2 = "SuppressDefaultEnvironment2";	// NOI18N
	static public final String MODULEID3 = "ModuleId3";	// NOI18N
	static public final String DEPENDENCIES3 = "Dependencies3";	// NOI18N
	static public final String HIDDEN_CLASSES3 = "HiddenClasses3";	// NOI18N
	static public final String NON_OVERRIDABLE_CLASSES3 = "NonOverridableClasses3";	// NOI18N
	static public final String INVERSE_CLASSLOADING3 = "InverseClassloading3";	// NOI18N
	static public final String SUPPRESS_DEFAULT_ENVIRONMENT3 = "SuppressDefaultEnvironment3";	// NOI18N

	public EnvironmentType() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public EnvironmentType(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(18);
		this.createProperty("moduleId", 	// NOI18N
			MODULEID, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ArtifactType.class);
		this.createProperty("dependencies", 	// NOI18N
			DEPENDENCIES, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependenciesType.class);
		this.createProperty("hidden-classes", 	// NOI18N
			HIDDEN_CLASSES, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("non-overridable-classes", 	// NOI18N
			NON_OVERRIDABLE_CLASSES, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("inverse-classloading", 	// NOI18N
			INVERSE_CLASSLOADING, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.createProperty("suppress-default-environment", 	// NOI18N
			SUPPRESS_DEFAULT_ENVIRONMENT, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.createProperty("moduleId", 	// NOI18N
			MODULEID2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ArtifactType.class);
		this.createProperty("dependencies", 	// NOI18N
			DEPENDENCIES2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependenciesType.class);
		this.createProperty("hidden-classes", 	// NOI18N
			HIDDEN_CLASSES2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("non-overridable-classes", 	// NOI18N
			NON_OVERRIDABLE_CLASSES2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("inverse-classloading", 	// NOI18N
			INVERSE_CLASSLOADING2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.createProperty("suppress-default-environment", 	// NOI18N
			SUPPRESS_DEFAULT_ENVIRONMENT2, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.createProperty("moduleId", 	// NOI18N
			MODULEID3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ArtifactType.class);
		this.createProperty("dependencies", 	// NOI18N
			DEPENDENCIES3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DependenciesType.class);
		this.createProperty("hidden-classes", 	// NOI18N
			HIDDEN_CLASSES3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("non-overridable-classes", 	// NOI18N
			NON_OVERRIDABLE_CLASSES3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ClassFilterType.class);
		this.createProperty("inverse-classloading", 	// NOI18N
			INVERSE_CLASSLOADING3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.createProperty("suppress-default-environment", 	// NOI18N
			SUPPRESS_DEFAULT_ENVIRONMENT3, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			EmptyType.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is optional
	public void setModuleId(ArtifactType value) {
		this.setValue(MODULEID, value);
	}

	//
	public ArtifactType getModuleId() {
		return (ArtifactType)this.getValue(MODULEID);
	}

	// This attribute is optional
	public void setDependencies(DependenciesType value) {
		this.setValue(DEPENDENCIES, value);
	}

	//
	public DependenciesType getDependencies() {
		return (DependenciesType)this.getValue(DEPENDENCIES);
	}

	// This attribute is optional
	public void setHiddenClasses(ClassFilterType value) {
		this.setValue(HIDDEN_CLASSES, value);
	}

	//
	public ClassFilterType getHiddenClasses() {
		return (ClassFilterType)this.getValue(HIDDEN_CLASSES);
	}

	// This attribute is optional
	public void setNonOverridableClasses(ClassFilterType value) {
		this.setValue(NON_OVERRIDABLE_CLASSES, value);
	}

	//
	public ClassFilterType getNonOverridableClasses() {
		return (ClassFilterType)this.getValue(NON_OVERRIDABLE_CLASSES);
	}

	// This attribute is optional
	public void setInverseClassloading(EmptyType value) {
		this.setValue(INVERSE_CLASSLOADING, value);
	}

	//
	public EmptyType getInverseClassloading() {
		return (EmptyType)this.getValue(INVERSE_CLASSLOADING);
	}

	// This attribute is optional
	public void setSuppressDefaultEnvironment(EmptyType value) {
		this.setValue(SUPPRESS_DEFAULT_ENVIRONMENT, value);
	}

	//
	public EmptyType getSuppressDefaultEnvironment() {
		return (EmptyType)this.getValue(SUPPRESS_DEFAULT_ENVIRONMENT);
	}

	// This attribute is optional
	public void setModuleId2(ArtifactType value) {
		this.setValue(MODULEID2, value);
	}

	//
	public ArtifactType getModuleId2() {
		return (ArtifactType)this.getValue(MODULEID2);
	}

	// This attribute is optional
	public void setDependencies2(DependenciesType value) {
		this.setValue(DEPENDENCIES2, value);
	}

	//
	public DependenciesType getDependencies2() {
		return (DependenciesType)this.getValue(DEPENDENCIES2);
	}

	// This attribute is optional
	public void setHiddenClasses2(ClassFilterType value) {
		this.setValue(HIDDEN_CLASSES2, value);
	}

	//
	public ClassFilterType getHiddenClasses2() {
		return (ClassFilterType)this.getValue(HIDDEN_CLASSES2);
	}

	// This attribute is optional
	public void setNonOverridableClasses2(ClassFilterType value) {
		this.setValue(NON_OVERRIDABLE_CLASSES2, value);
	}

	//
	public ClassFilterType getNonOverridableClasses2() {
		return (ClassFilterType)this.getValue(NON_OVERRIDABLE_CLASSES2);
	}

	// This attribute is optional
	public void setInverseClassloading2(EmptyType value) {
		this.setValue(INVERSE_CLASSLOADING2, value);
	}

	//
	public EmptyType getInverseClassloading2() {
		return (EmptyType)this.getValue(INVERSE_CLASSLOADING2);
	}

	// This attribute is optional
	public void setSuppressDefaultEnvironment2(EmptyType value) {
		this.setValue(SUPPRESS_DEFAULT_ENVIRONMENT2, value);
	}

	//
	public EmptyType getSuppressDefaultEnvironment2() {
		return (EmptyType)this.getValue(SUPPRESS_DEFAULT_ENVIRONMENT2);
	}

	// This attribute is optional
	public void setModuleId3(ArtifactType value) {
		this.setValue(MODULEID3, value);
	}

	//
	public ArtifactType getModuleId3() {
		return (ArtifactType)this.getValue(MODULEID3);
	}

	// This attribute is optional
	public void setDependencies3(DependenciesType value) {
		this.setValue(DEPENDENCIES3, value);
	}

	//
	public DependenciesType getDependencies3() {
		return (DependenciesType)this.getValue(DEPENDENCIES3);
	}

	// This attribute is optional
	public void setHiddenClasses3(ClassFilterType value) {
		this.setValue(HIDDEN_CLASSES3, value);
	}

	//
	public ClassFilterType getHiddenClasses3() {
		return (ClassFilterType)this.getValue(HIDDEN_CLASSES3);
	}

	// This attribute is optional
	public void setNonOverridableClasses3(ClassFilterType value) {
		this.setValue(NON_OVERRIDABLE_CLASSES3, value);
	}

	//
	public ClassFilterType getNonOverridableClasses3() {
		return (ClassFilterType)this.getValue(NON_OVERRIDABLE_CLASSES3);
	}

	// This attribute is optional
	public void setInverseClassloading3(EmptyType value) {
		this.setValue(INVERSE_CLASSLOADING3, value);
	}

	//
	public EmptyType getInverseClassloading3() {
		return (EmptyType)this.getValue(INVERSE_CLASSLOADING3);
	}

	// This attribute is optional
	public void setSuppressDefaultEnvironment3(EmptyType value) {
		this.setValue(SUPPRESS_DEFAULT_ENVIRONMENT3, value);
	}

	//
	public EmptyType getSuppressDefaultEnvironment3() {
		return (EmptyType)this.getValue(SUPPRESS_DEFAULT_ENVIRONMENT3);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ArtifactType newArtifactType() {
		return new ArtifactType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public DependenciesType newDependenciesType() {
		return new DependenciesType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ClassFilterType newClassFilterType() {
		return new ClassFilterType();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public EmptyType newEmptyType() {
		return new EmptyType();
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
		str.append("ModuleId");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getModuleId();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(MODULEID, 0, str, indent);

		str.append(indent);
		str.append("Dependencies");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependencies();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(DEPENDENCIES, 0, str, indent);

		str.append(indent);
		str.append("HiddenClasses");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getHiddenClasses();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(HIDDEN_CLASSES, 0, str, indent);

		str.append(indent);
		str.append("NonOverridableClasses");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getNonOverridableClasses();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(NON_OVERRIDABLE_CLASSES, 0, str, indent);

		str.append(indent);
		str.append("InverseClassloading");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getInverseClassloading();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(INVERSE_CLASSLOADING, 0, str, indent);

		str.append(indent);
		str.append("SuppressDefaultEnvironment");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getSuppressDefaultEnvironment();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(SUPPRESS_DEFAULT_ENVIRONMENT, 0, str, indent);

		str.append(indent);
		str.append("ModuleId2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getModuleId2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(MODULEID2, 0, str, indent);

		str.append(indent);
		str.append("Dependencies2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependencies2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(DEPENDENCIES2, 0, str, indent);

		str.append(indent);
		str.append("HiddenClasses2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getHiddenClasses2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(HIDDEN_CLASSES2, 0, str, indent);

		str.append(indent);
		str.append("NonOverridableClasses2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getNonOverridableClasses2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(NON_OVERRIDABLE_CLASSES2, 0, str, indent);

		str.append(indent);
		str.append("InverseClassloading2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getInverseClassloading2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(INVERSE_CLASSLOADING2, 0, str, indent);

		str.append(indent);
		str.append("SuppressDefaultEnvironment2");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getSuppressDefaultEnvironment2();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(SUPPRESS_DEFAULT_ENVIRONMENT2, 0, str, indent);

		str.append(indent);
		str.append("ModuleId3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getModuleId3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(MODULEID3, 0, str, indent);

		str.append(indent);
		str.append("Dependencies3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getDependencies3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(DEPENDENCIES3, 0, str, indent);

		str.append(indent);
		str.append("HiddenClasses3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getHiddenClasses3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(HIDDEN_CLASSES3, 0, str, indent);

		str.append(indent);
		str.append("NonOverridableClasses3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getNonOverridableClasses3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(NON_OVERRIDABLE_CLASSES3, 0, str, indent);

		str.append(indent);
		str.append("InverseClassloading3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getInverseClassloading3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(INVERSE_CLASSLOADING3, 0, str, indent);

		str.append(indent);
		str.append("SuppressDefaultEnvironment3");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getSuppressDefaultEnvironment3();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(SUPPRESS_DEFAULT_ENVIRONMENT3, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("EnvironmentType\n");	// NOI18N
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
