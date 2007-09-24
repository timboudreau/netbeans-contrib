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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.portalpack.saw.palette.items.beans;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class ExceptionList extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String EXCEPTION_NAME = "ExceptionName";	// NOI18N

	public ExceptionList() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public ExceptionList(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("exception-name", 	// NOI18N
			EXCEPTION_NAME, 
			Common.TYPE_1_N | Common.TYPE_BOOLEAN | Common.TYPE_SHOULD_NOT_BE_EMPTY | Common.TYPE_KEY, 
			Boolean.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array containing at least one element
	public void setExceptionName(int index, boolean value) {
		this.setValue(EXCEPTION_NAME, index, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isExceptionName(int index) {
		Boolean ret = (Boolean)this.getValue(EXCEPTION_NAME, index);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
	}

	// Return the number of properties
	public int sizeExceptionName() {
		return this.size(EXCEPTION_NAME);
	}

	// This attribute is an array containing at least one element
	public void setExceptionName(boolean[] value) {
		Boolean[] values = null;
		if (value != null)
		{
			values = new Boolean[value.length];
			for (int i=0; i<value.length; i++)
				values[i] = (value[i] ? Boolean.TRUE : Boolean.FALSE);
		}
		this.setValue(EXCEPTION_NAME, values);
	}

	//
	public boolean[] getExceptionName() {
		boolean[] ret = null;
		Boolean[] values = (Boolean[])this.getValues(EXCEPTION_NAME);
		if (values != null)
		{
			ret = new boolean[values.length];
			for (int i=0; i<values.length; i++)
				ret[i] = values[i].booleanValue();
		}
		return ret;
	}

	// Add a new element returning its index in the list
	public int addExceptionName(boolean value) {
		int positionOfNewItem = this.addValue(EXCEPTION_NAME, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeExceptionName(boolean value) {
		return this.removeValue(EXCEPTION_NAME, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	// Remove an element using its index
	//
	public void removeExceptionName(int index) {
		this.removeValue(EXCEPTION_NAME, index);
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
		str.append("ExceptionName["+this.sizeExceptionName()+"]");	// NOI18N
		for(int i=0; i<this.sizeExceptionName(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append((this.isExceptionName(i)?"true":"false"));
			this.dumpAttributes(EXCEPTION_NAME, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("ExceptionList\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="UTF-8"?>

<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
            targetNamespace="http://xml.netbeans.org/schema/SAWImplementationDetails"
            xmlns:tns="http://xml.netbeans.org/schema/SAWImplementationDetails"
            elementFormDefault="qualified">
    <xsd:complexType name="ImplementationType">
        <xsd:sequence>
            <xsd:element name="Method" maxOccurs="unbounded">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsd:element name="returnType"/>
                        <xsd:element name="method-parameters">
                            <xsd:complexType>
                                <xsd:sequence>
                                    <xsd:element name="param-name" maxOccurs="unbounded"/>
                                    <xsd:element name="param-type"/>
                                </xsd:sequence>
                            </xsd:complexType>
                        </xsd:element>
                        <xsd:element name="exception-list">
                            <xsd:complexType>
                                <xsd:sequence>
                                    <xsd:element name="exception-name" maxOccurs="unbounded"/>
                                </xsd:sequence>
                            </xsd:complexType>
                        </xsd:element>
                    </xsd:sequence>
                    <xsd:attribute name="methodName" type="xsd:string"/>
                </xsd:complexType>
            </xsd:element>
        </xsd:sequence>
        <xsd:attribute name="implType" type="xsd:string"/>
    </xsd:complexType>
</xsd:schema>

*/
