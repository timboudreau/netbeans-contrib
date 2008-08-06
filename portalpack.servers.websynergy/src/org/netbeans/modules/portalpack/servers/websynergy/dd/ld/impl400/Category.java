/**
 *	This generated bean class Category matches the schema element 'category'.
 *  The root bean class is Display
 *
 *	Generated on Tue Mar 18 20:26:12 IST 2008
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class Category extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.websynergy.dd.ld.impl400.CategoryInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String NAME = "Name";	// NOI18N
	static public final String CATEGORY = "Category";	// NOI18N
	static public final String PORTLET = "Portlet";	// NOI18N
	static public final String PORTLETID = "PortletId";	// NOI18N

	public Category() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Category(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("category", 	// NOI18N
			CATEGORY, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Category.class);
		this.createAttribute(CATEGORY, "name", "Name", 
						AttrProp.CDATA | AttrProp.REQUIRED,
						null, null);
		this.createProperty("portlet", 	// NOI18N
			PORTLET, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createAttribute(PORTLET, "id", "Id", 
						AttrProp.CDATA | AttrProp.REQUIRED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setName(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(CATEGORY) == 0) {
			addValue(CATEGORY, "");
		}
		setAttributeValue(CATEGORY, index, "Name", value);
	}

	//
	public java.lang.String getName(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(CATEGORY) == 0) {
			return null;
		} else {
			return getAttributeValue(CATEGORY, index, "Name");
		}
	}

	// Return the number of properties
	public int sizeName() {
		return this.size(CATEGORY);
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

	// This attribute is an array, possibly empty
	public void setPortlet(int index, String value) {
		this.setValue(PORTLET, index, value);
	}

	//
	public String getPortlet(int index) {
		return (String)this.getValue(PORTLET, index);
	}

	// Return the number of properties
	public int sizePortlet() {
		return this.size(PORTLET);
	}

	// This attribute is an array, possibly empty
	public void setPortlet(String[] value) {
		this.setValue(PORTLET, value);
	}

	//
	public String[] getPortlet() {
		return (String[])this.getValues(PORTLET);
	}

	// Add a new element returning its index in the list
	public int addPortlet(String value) {
		int positionOfNewItem = this.addValue(PORTLET, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removePortlet(String value) {
		return this.removeValue(PORTLET, value);
	}

	// This attribute is an array, possibly empty
	public void setPortletId(int index, java.lang.String value) {
		// Make sure we've got a place to put this attribute.
		if (size(PORTLET) == 0) {
			addValue(PORTLET, "");
		}
		setAttributeValue(PORTLET, index, "Id", value);
	}

	//
	public java.lang.String getPortletId(int index) {
		// If our element does not exist, then the attribute does not exist.
		if (size(PORTLET) == 0) {
			return null;
		} else {
			return getAttributeValue(PORTLET, index, "Id");
		}
	}

	// Return the number of properties
	public int sizePortletId() {
		return this.size(PORTLET);
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
	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
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

		str.append(indent);
		str.append("Portlet["+this.sizePortlet()+"]");	// NOI18N
		for(int i=0; i<this.sizePortlet(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getPortlet(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(PORTLET, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Category\n");	// NOI18N
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
