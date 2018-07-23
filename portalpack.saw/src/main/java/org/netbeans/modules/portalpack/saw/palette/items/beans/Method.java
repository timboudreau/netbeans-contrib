/**
 *	This generated bean class Method matches the schema element 'Method'.
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

public class Method extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String RETURNTYPE = "ReturnType";	// NOI18N
	static public final String METHOD_PARAMETERS = "MethodParameters";	// NOI18N
	static public final String EXCEPTION_LIST = "ExceptionList";	// NOI18N

	public Method() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Method(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(3);
		this.createProperty("returnType", 	// NOI18N
			RETURNTYPE, 
			Common.TYPE_0_1 | Common.TYPE_BOOLEAN | Common.TYPE_SHOULD_NOT_BE_EMPTY | Common.TYPE_KEY, 
			Boolean.class);
		this.createProperty("method-parameters", 	// NOI18N
			METHOD_PARAMETERS, 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			MethodParameters.class);
		this.createProperty("exception-list", 	// NOI18N
			EXCEPTION_LIST, 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			ExceptionList.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setReturnType(boolean value) {
		this.setValue(RETURNTYPE, (value ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE));
	}

	//
	public boolean isReturnType() {
		Boolean ret = (Boolean)this.getValue(RETURNTYPE);
		if (ret == null)
			ret = (Boolean)Common.defaultScalarValue(Common.TYPE_BOOLEAN);
		return ((java.lang.Boolean)ret).booleanValue();
	}

	// This attribute is mandatory
	public void setMethodParameters(MethodParameters value) {
		this.setValue(METHOD_PARAMETERS, value);
	}

	//
	public MethodParameters getMethodParameters() {
		return (MethodParameters)this.getValue(METHOD_PARAMETERS);
	}

	// This attribute is mandatory
	public void setExceptionList(ExceptionList value) {
		this.setValue(EXCEPTION_LIST, value);
	}

	//
	public ExceptionList getExceptionList() {
		return (ExceptionList)this.getValue(EXCEPTION_LIST);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public MethodParameters newMethodParameters() {
		return new MethodParameters();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public ExceptionList newExceptionList() {
		return new ExceptionList();
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
		str.append("ReturnType");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append((this.isReturnType()?"true":"false"));
		this.dumpAttributes(RETURNTYPE, 0, str, indent);

		str.append(indent);
		str.append("MethodParameters");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getMethodParameters();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(METHOD_PARAMETERS, 0, str, indent);

		str.append(indent);
		str.append("ExceptionList");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getExceptionList();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(EXCEPTION_LIST, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Method\n");	// NOI18N
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
