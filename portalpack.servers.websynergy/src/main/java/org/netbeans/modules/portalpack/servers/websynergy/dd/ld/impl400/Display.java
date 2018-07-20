/**
 *	This generated bean class Display matches the schema element 'display'.
 *
 *	Generated on Tue Mar 18 20:26:12 IST 2008
 *
 *	This class matches the root element of the DTD,
 *	and is the root of the following bean graph:
 *
 *	display <display> : Display
 *		category <category> : Category[0,n]
 *			[attr: name CDATA #REQUIRED ]
 *			category <category> : Category[0,n]...
 *				[attr: name CDATA #REQUIRED ]
 *			portlet <portlet> : String[0,n]
 *				[attr: id CDATA #REQUIRED ]
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

// BEGIN_NOI18N

public class Display extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.DisplayInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String CATEGORY = "Category";	// NOI18N

	public Display() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public Display(org.w3c.dom.Node doc, int options) {
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
			doc = GraphManager.createRootElementNode("display");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "display"));
		}
		Node n = GraphManager.getElementNode("display", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "display", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public Display(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("display", "Display",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, Display.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("category", 	// NOI18N
			CATEGORY, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Category.class);
		this.createAttribute(CATEGORY, "name", "Name", 
						AttrProp.CDATA | AttrProp.REQUIRED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setCategory(int index, Category value) {
		this.setValue(CATEGORY, index, value);
	}

	//
	public Category getCategory(int index) {
		return (Category)this.getValue(CATEGORY, index);
	}

	// Return the number of properties
	public int sizeCategory() {
		return this.size(CATEGORY);
	}

	// This attribute is an array, possibly empty
	public void setCategory(Category[] value) {
		this.setValue(CATEGORY, value);
	}

	//
	public Category[] getCategory() {
		return (Category[])this.getValues(CATEGORY);
	}

	// Add a new element returning its index in the list
	public int addCategory(org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Category value) {
		int positionOfNewItem = this.addValue(CATEGORY, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeCategory(org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.Category value) {
		return this.removeValue(CATEGORY, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Category newCategory() {
		return new Category();
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
	public static Display createGraph(org.w3c.dom.Node doc) {
		return new Display(doc, Common.NO_DEFAULT_VALUES);
	}

	public static Display createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static Display createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static Display createGraph(java.io.InputStream in, boolean validate) {
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
	public static Display createGraph() {
		return new Display();
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
	}

	// Special serializer: output XML as serialization
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException{
		out.defaultWriteObject();
		final int MAX_SIZE = 0XFFFF;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(baos);
		final byte [] array = baos.toByteArray();
		final int numStrings = array.length / MAX_SIZE;
		final int leftover = array.length % MAX_SIZE;
		out.writeInt(numStrings + (0 == leftover ? 0 : 1));
		out.writeInt(MAX_SIZE);
		int offset = 0;
		for (int i = 0; i < numStrings; i++){
			out.writeUTF(new String(array, offset, MAX_SIZE));
			offset += MAX_SIZE;
		}
		if (leftover > 0){
			final int count = array.length - offset;
			out.writeUTF(new String(array, offset, count));
		}
	}
	// Special deserializer: read XML as deserialization
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
		try{
			in.defaultReadObject();
			init(comparators, runtimeVersion);
			// init(comparators, new GenBeans.Version(1, 0, 8))
			final int numStrings = in.readInt();
			final int max_size = in.readInt();
			final StringBuffer sb = new StringBuffer(numStrings * max_size);
			for (int i = 0; i < numStrings; i++){
				sb.append(in.readUTF());
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
			Document doc = GraphManager.createXmlDocument(bais, false);
			initOptions(Common.NO_DEFAULT_VALUES);
			initFromNode(doc, Common.NO_DEFAULT_VALUES);
		}
		catch (Schema2BeansException e){
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
		str.append("Category["+this.sizeCategory()+"]");	// NOI18N
		for(int i=0; i<this.sizeCategory(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getCategory(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(CATEGORY, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Display\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<!--
This is the DTD for the Display parameters for Liferay Portal.

<!DOCTYPE display PUBLIC
	"-//Liferay//DTD Display 4.0.0//EN"
	"http://www.liferay.com/dtd/liferay-display_4_0_0.dtd">
-->

<!--
The display element is the root of the deployment descriptor that describes how
portlets are categorized and displayed for users to choose when personalizing a
page in Liferay Portal.
-->
<!ELEMENT display (category*)>

<!--
The category element organizes a set of portlets. A portlet can exist in more
than one category.
-->
<!ELEMENT category (category*, portlet*)>

<!--
The name of a category is mapped to the portal's Language properties. If the
category name is "test", then the key in the portal's resource bundle will be
"category.test".

See:

http://www.liferay.com/page/guest/documentation/development/languages
-->
<!ATTLIST category
	name CDATA #REQUIRED
>

<!--
The portlet element represents a portlet.
-->
<!ELEMENT portlet (#PCDATA)>

<!--
The id must match the unique portlet-name specified in portlet.xml.
-->
<!ATTLIST portlet
	id CDATA #REQUIRED
>
*/
