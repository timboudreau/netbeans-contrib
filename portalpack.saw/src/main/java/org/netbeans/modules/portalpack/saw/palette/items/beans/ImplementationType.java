/**
 *	This generated bean class ImplementationType matches the schema element 'ImplementationType'.
 *
 *	Generated on Fri Aug 17 13:02:45 IST 2007
 *
 *	This class matches the root element of the XML Schema,
 *	and is the root of the following bean graph:
 *
 *	implementationType <ImplementationType> : ImplementationType
 *		[attr: implType CDATA #IMPLIED  : java.lang.String]
 *		method <Method> : Method[1,n]
 *			[attr: methodName CDATA #IMPLIED  : java.lang.String]
 *			returnType <returnType> : boolean
 *			methodParameters <method-parameters> : MethodParameters
 *				paramName <param-name> : boolean[1,n]
 *				paramType <param-type> : boolean
 *			exceptionList <exception-list> : ExceptionList
 *				exceptionName <exception-name> : boolean[1,n]
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.saw.palette.items.beans;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

// BEGIN_NOI18N

public class ImplementationType extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(4, 2, 0);

	static public final String METHOD = "Method";	// NOI18N

	public ImplementationType() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public ImplementationType(org.w3c.dom.Node doc, int options) {
		this(Common.NO_DEFAULT_VALUES);
		try {
			initFromNode(doc, options);
		}
		catch (Schema2BeansException e) {
			throw new RuntimeException(e);
		}
	}
	protected void initFromNode(org.w3c.dom.Node doc, int options) throws Schema2BeansException
	{
		if (doc == null)
		{
			doc = GraphManager.createRootElementNode("ImplementationType");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "ImplementationType"));
		}
		Node n = GraphManager.getElementNode("ImplementationType", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "ImplementationType", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public ImplementationType(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("ImplementationType", "ImplementationType",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, ImplementationType.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("Method", 	// NOI18N
			METHOD, 
			Common.TYPE_1_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Method.class);
		this.createAttribute(METHOD, "methodName", "MethodName", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createAttribute("implType", "ImplType", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {
		setDefaultNamespace("http://xml.netbeans.org/schema/SAWImplementationDetails");

	}

	// This attribute is an array containing at least one element
	public void setMethod(int index, Method value) {
		this.setValue(METHOD, index, value);
	}

	//
	public Method getMethod(int index) {
		return (Method)this.getValue(METHOD, index);
	}

	// Return the number of properties
	public int sizeMethod() {
		return this.size(METHOD);
	}

	// This attribute is an array containing at least one element
	public void setMethod(Method[] value) {
		this.setValue(METHOD, value);
	}

	//
	public Method[] getMethod() {
		return (Method[])this.getValues(METHOD);
	}

	// Add a new element returning its index in the list
	public int addMethod(org.netbeans.modules.portalpack.saw.palette.items.beans.Method value) {
		int positionOfNewItem = this.addValue(METHOD, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeMethod(org.netbeans.modules.portalpack.saw.palette.items.beans.Method value) {
		return this.removeValue(METHOD, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Method newMethod() {
		return new Method();
	}

	//
	public static void addComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.add(c);
	}

	//
	public static void removeComparator(org.netbeans.modules.schema2beans.BeanComparator c) {
		comparators.remove(c);
	}
	//
	// This method returns the root of the bean graph
	// Each call creates a new bean graph from the specified DOM graph
	//
	public static ImplementationType createGraph(org.w3c.dom.Node doc) {
		return new ImplementationType(doc, Common.NO_DEFAULT_VALUES);
	}

	public static ImplementationType createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static ImplementationType createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static ImplementationType createGraph(java.io.InputStream in, boolean validate) {
		try {
			Document doc = GraphManager.createXmlDocument(in, validate);
			return createGraph(doc);
		}
		catch (Exception t) {
			throw new RuntimeException(Common.getMessage(
				"DOMGraphCreateFailed_msg",
				t));
		}
	}

	//
	// This method returns the root for a new empty bean graph
	//
	public static ImplementationType createGraph() {
		return new ImplementationType();
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Special serializer: output XML as serialization
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(baos);
		String str = baos.toString();;
		// System.out.println("str='"+str+"'");
		out.writeUTF(str);
	}
	// Special deserializer: read XML as deserialization
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
		try{
			init(comparators, runtimeVersion);
			String strDocument = in.readUTF();
			// System.out.println("strDocument='"+strDocument+"'");
			ByteArrayInputStream bais = new ByteArrayInputStream(strDocument.getBytes());
			Document doc = GraphManager.createXmlDocument(bais, false);
			initOptions(Common.NO_DEFAULT_VALUES);
			initFromNode(doc, Common.NO_DEFAULT_VALUES);
		}
		catch (Schema2BeansException e) {
			throw new RuntimeException(e);
		}
	}

	public void _setSchemaLocation(String location) {
		if (beanProp().getAttrProp("xsi:schemaLocation", true) == null) {
			createAttribute("xmlns:xsi", "xmlns:xsi", AttrProp.CDATA | AttrProp.IMPLIED, null, "http://www.w3.org/2001/XMLSchema-instance");
			setAttributeValue("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			createAttribute("xsi:schemaLocation", "xsi:schemaLocation", AttrProp.CDATA | AttrProp.IMPLIED, null, location);
		}
		setAttributeValue("xsi:schemaLocation", location);
	}

	public String _getSchemaLocation() {
		if (beanProp().getAttrProp("xsi:schemaLocation", true) == null) {
			createAttribute("xmlns:xsi", "xmlns:xsi", AttrProp.CDATA | AttrProp.IMPLIED, null, "http://www.w3.org/2001/XMLSchema-instance");
			setAttributeValue("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			createAttribute("xsi:schemaLocation", "xsi:schemaLocation", AttrProp.CDATA | AttrProp.IMPLIED, null, null);
		}
		return getAttributeValue("xsi:schemaLocation");
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Method["+this.sizeMethod()+"]");	// NOI18N
		for(int i=0; i<this.sizeMethod(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getMethod(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(METHOD, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("ImplementationType\n");	// NOI18N
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
