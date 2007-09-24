/**
 *	This generated bean class MethodParameters matches the schema element 'method-parameters'.
 *  The root bean class is ImplementationType
 *
 *	Generated on Fri Aug 17 13:02:45 IST 2007
 * @Generated
 */

package org.netbeans.modules.portalpack.saw.palette.items.beans;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class MethodParameters extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String PARAM_NAME = "ParamName";	// NOI18N
	static public final String PARAM_TYPE = "ParamType";	// NOI18N

	public MethodParameters() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public MethodParameters(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("param-name", 	// NOI18N
			PARAM_NAME, 
			Common.TYPE_1_N | Common.TYPE_BOOLEAN | Common.TYPE_SHOULD_NOT_BE_EMPTY | Common.TYPE_KEY, 
			Boolean.class);
		this.createProperty("param-type", 	// NOI18N
			PARAM_TYPE, 
			Common.TYPE_0_1 | Common.TYPE_BOOLEAN | Common.TYPE_SHOULD_NOT_BE_EMPTY | Common.TYPE_KEY, 
			Boolean.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array containing at least one element
	public void setParamName(int index, boolean value) {
		this.setValue(PARAM_NAME, index, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isParamName(int index) {
		Boolean ret = (Boolean)this.getValue(PARAM_NAME, index);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
	}

	// Return the number of properties
	public int sizeParamName() {
		return this.size(PARAM_NAME);
	}

	// This attribute is an array containing at least one element
	public void setParamName(boolean[] value) {
		Boolean[] values = null;
		if (value != null)
		{
			values = new Boolean[value.length];
			for (int i=0; i<value.length; i++)
				values[i] = (value[i] ? Boolean.TRUE : Boolean.FALSE);
		}
		this.setValue(PARAM_NAME, values);
	}

	//
	public boolean[] getParamName() {
		boolean[] ret = null;
		Boolean[] values = (Boolean[])this.getValues(PARAM_NAME);
		if (values != null)
		{
			ret = new boolean[values.length];
			for (int i=0; i<values.length; i++)
				ret[i] = values[i].booleanValue();
		}
		return ret;
	}

	// Add a new element returning its index in the list
	public int addParamName(boolean value) {
		int positionOfNewItem = this.addValue(PARAM_NAME, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeParamName(boolean value) {
		return this.removeValue(PARAM_NAME, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	// Remove an element using its index
	//
	public void removeParamName(int index) {
		this.removeValue(PARAM_NAME, index);
	}

	// This attribute is mandatory
	public void setParamType(boolean value) {
		this.setValue(PARAM_TYPE, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isParamType() {
		Boolean ret = (Boolean)this.getValue(PARAM_TYPE);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
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
		str.append("ParamName["+this.sizeParamName()+"]");	// NOI18N
		for(int i=0; i<this.sizeParamName(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append((this.isParamName(i)?"true":"false"));
			this.dumpAttributes(PARAM_NAME, i, str, indent);
		}

		str.append(indent);
		str.append("ParamType");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append((this.isParamType()?"true":"false"));
		this.dumpAttributes(PARAM_TYPE, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("MethodParameters\n");	// NOI18N
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
