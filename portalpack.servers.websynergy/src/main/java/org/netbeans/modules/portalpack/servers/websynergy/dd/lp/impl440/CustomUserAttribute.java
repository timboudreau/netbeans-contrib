/**
 *	This generated bean class CustomUserAttribute matches the schema element 'custom-user-attribute'.
 *  The root bean class is LiferayPortletApp
 *
 *	Generated on Sun Mar 16 00:21:05 IST 2008
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class CustomUserAttribute extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.websynergy.dd.lp.impl440.CustomUserAttributeInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String NAME = "Name";	// NOI18N
	static public final String CUSTOM_CLASS = "CustomClass";	// NOI18N

	public CustomUserAttribute() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public CustomUserAttribute(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(2);
		this.createProperty("name", 	// NOI18N
			NAME, 
			Common.TYPE_1_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("custom-class", 	// NOI18N
			CUSTOM_CLASS, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array containing at least one element
	public void setName(int index, String value) {
		this.setValue(NAME, index, value);
	}

	//
	public String getName(int index) {
		return (String)this.getValue(NAME, index);
	}

	// Return the number of properties
	public int sizeName() {
		return this.size(NAME);
	}

	// This attribute is an array containing at least one element
	public void setName(String[] value) {
		this.setValue(NAME, value);
	}

	//
	public String[] getName() {
		return (String[])this.getValues(NAME);
	}

	// Add a new element returning its index in the list
	public int addName(String value) {
		int positionOfNewItem = this.addValue(NAME, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeName(String value) {
		return this.removeValue(NAME, value);
	}

	// This attribute is mandatory
	public void setCustomClass(String value) {
		this.setValue(CUSTOM_CLASS, value);
	}

	//
	public String getCustomClass() {
		return (String)this.getValue(CUSTOM_CLASS);
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
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property name
		if (sizeName() == 0) {
			throw new org.netbeans.modules.schema2beans.ValidateException("sizeName() == 0", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "name", this);	// NOI18N
		}
		// Validating property customClass
		if (getCustomClass() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getCustomClass() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "customClass", this);	// NOI18N
		}
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Name["+this.sizeName()+"]");	// NOI18N
		for(int i=0; i<this.sizeName(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getName(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(NAME, i, str, indent);
		}

		str.append(indent);
		str.append("CustomClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getCustomClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CUSTOM_CLASS, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("CustomUserAttribute\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N

