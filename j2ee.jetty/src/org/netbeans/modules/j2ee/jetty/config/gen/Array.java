/**
 *	This generated bean class Array matches the schema element 'Array'.
 *  The root bean class is Configure
 *
 * @Generated
 */

package org.netbeans.modules.j2ee.jetty.config.gen;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class Array extends org.netbeans.modules.schema2beans.BaseBean
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String TYPE = "Type";	// NOI18N
	static public final String ID = "Id";	// NOI18N
	static public final String ITEM = "Item";	// NOI18N

	public Array() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Array(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("Item", 	// NOI18N
			ITEM, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Item.class);
		this.createAttribute(ITEM, "type", "Type", 
						AttrProp.CDATA | AttrProp.IMPLIED,
						null, null);
		this.createAttribute(ITEM, "id", "Id", 
						AttrProp.NMTOKEN | AttrProp.IMPLIED,
						null, null);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is optional
	public void setType(java.lang.String value) {
		setAttributeValue(TYPE, value);
	}

	//
	public java.lang.String getType() {
		return getAttributeValue(TYPE);
	}

	// This attribute is optional
	public void setId(java.lang.String value) {
		setAttributeValue(ID, value);
	}

	//
	public java.lang.String getId() {
		return getAttributeValue(ID);
	}

	// This attribute is an array, possibly empty
	public void setItem(int index, Item value) {
		this.setValue(ITEM, index, value);
	}

	//
	public Item getItem(int index) {
		return (Item)this.getValue(ITEM, index);
	}

	// Return the number of properties
	public int sizeItem() {
		return this.size(ITEM);
	}

	// This attribute is an array, possibly empty
	public void setItem(Item[] value) {
		this.setValue(ITEM, value);
	}

	//
	public Item[] getItem() {
		return (Item[])this.getValues(ITEM);
	}

	// Add a new element returning its index in the list
	public int addItem(org.netbeans.modules.j2ee.jetty.config.gen.Item value) {
		int positionOfNewItem = this.addValue(ITEM, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeItem(org.netbeans.modules.j2ee.jetty.config.gen.Item value) {
		return this.removeValue(ITEM, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Item newItem() {
		return new Item();
	}

        @SuppressWarnings("unchecked")
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
		str.append("Item["+this.sizeItem()+"]");	// NOI18N
		for(int i=0; i<this.sizeItem(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getItem(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(ITEM, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Array\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
This is the document type descriptor for the
org.mortbay.util.XmlConfiguration class.  It allows a java object to be
configured by with a sequence of Set, Put and Call elements.  These tags are 
mapped to methods on the object to be configured as follows:

  <Set  name="Test">value</Set>              ==  obj.setTest("value");
  <Put  name="Test">value</Put>              ==  obj.put("Test","value");
  <Call name="test"><Arg>value</Arg></Call>  ==  obj.test("value");

Values themselves may be configured objects that are created with the
<New> tag or returned from a <Call> tag.

Values are matched to arguments on a best effort approach, but types
my be specified if a match is not achieved.

$Id: Array.java,v 1.3 2008/04/29 06:42:17 cvsuser Exp $
-->

<!ENTITY % CONFIG "Get|Set|Put|Call|New|Ref|Array|Property">
<!ENTITY % VALUE "#PCDATA|Call|New|Get|SystemProperty|Array|Ref|Property">

<!ENTITY % TYPEATTR "type CDATA #IMPLIED " > <!-- String|Character|Short|Byte|Integer|Long|Boolean|Float|Double|char|short|byte|int|long|boolean|float|double|URL|InetAddress|InetAddrPort| #classname -->
<!ENTITY % IMPLIEDCLASSATTR "class NMTOKEN #IMPLIED" >
<!ENTITY % CLASSATTR "class NMTOKEN #REQUIRED" >
<!ENTITY % NAMEATTR "name NMTOKEN #REQUIRED" >
<!ENTITY % DEFAULTATTR "default CDATA #IMPLIED" >
<!ENTITY % IDATTR "id NMTOKEN #IMPLIED" >
<!ENTITY % REQUIREDIDATTR "id NMTOKEN #REQUIRED" >
<!--
Configure Element.
This is the root element that specifies the class of object that
can be configured:

    <Configure class="com.acme.MyClass"> ... </Configure>

A Configure element can contain Set, Put or Call elements.
-->
<!ELEMENT Configure (%CONFIG;)* >
<!ATTLIST Configure %IMPLIEDCLASSATTR; %IDATTR; >


<!--
Set Element.
This element maps to a call to a set method on the current object.
The name and optional type attributes are used to select the set 
method. If the name given is xxx, then a setXxx method is used, or
the xxx field is used of setXxx cannot be found. 
A Set element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.

A Set with a class attribute is treated as a static set method invocation.
-->
<!ELEMENT Set (#PCDATA) >
<!ATTLIST Set %NAMEATTR; %TYPEATTR; %IMPLIEDCLASSATTR; >


<!--
Get Element.
This element maps to a call to a get method or field on the current object.
The name attribute is used to select the get method.
If the name given is xxx, then a getXxx method is used, or
the xxx field is used of setXxx cannot be found. 
A Get element can contain Set, Put and/or Call elements which act on the object
returned by the get call.

A Get with a class attribute is treated as a static get method or field.
-->
<!ELEMENT Get (%CONFIG;)*>
<!ATTLIST Get %NAMEATTR; %IMPLIEDCLASSATTR; %IDATTR; >

<!--
Put Element.
This element maps to a call to a put method on the current object,
which must implement the Map interface. The name attribute is used 
as the put key and the optional type attribute can force the type 
of the value.

A Put element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Put ( %VALUE; )* >
<!ATTLIST Put %NAMEATTR; %TYPEATTR; >


<!--
Call Element.
This element maps to an arbitrary call to a method on the current object,
The name attribute and Arg elements are used to select the method.

A Call element can contain a sequence of Arg elements followed by
a sequence of Set, Put and/or Call elements which act on any object
returned by the original call:

 <Call name="test"><Arg>value1</Arg><Set name="Test">Value2</Set></Call>

This is equivalent to:

 Object o2 = o1.test("value1");
 o2.setTest("value2");

A Call with a class attribute is treated as a static call.

-->
<!ELEMENT Call (Arg*,(%CONFIG;)*)>
<!ATTLIST Call %NAMEATTR; %IMPLIEDCLASSATTR; %IDATTR;>


<!--
Arg Element.
This element defines a positional argument for the Call element.
The optional type attribute can force the type of the value.

An Arg element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Arg ( %VALUE; )* >
<!ATTLIST Arg %TYPEATTR; >



<!--
New Element.
This element allows the creation of a new object as part of a 
value of a Set, Put or Arg element. The class attribute determines
the type of the new object and the contained Arg elements 
are used to select the constructor for the new object.

A New element can contain a sequence of Arg elements followed by
a sequence of Set, Put and/or Call elements which act on the new object:

 <New class="com.acme.MyClass">
   <Arg>value1</Arg><Set name="Test">Value2</Set>
 </New>

This is equivalent to:

 Object o = new com.acme.MyClass("value1");
 o.setTest("value2");

-->
<!ELEMENT New (Arg*,(%CONFIG;)*)>
<!ATTLIST New %CLASSATTR; %IDATTR;>

<!--
Ref Element.
This element allows a previously created object to be reference by id.

A Ref element can contain a sequence of Set, Put and/or Call elements 
which act on the referenced object:

 <Ref id="myobject">
   <Set name="Test">Value2</Set>
 </New>

-->
<!ELEMENT Ref ((%CONFIG;)*)>
<!ATTLIST Ref %REQUIREDIDATTR;>

<!--
Array Element.
This element allows the creation of a new array as part of a 
value of a Set, Put or Arg element. The type attribute determines
the type of the new array and the contained Item elements 
are used for each element of the array

 <Array type="java.lang.String">
   <Item>value0</Item>
   <Item><New class="java.lang.String"><Arg>value1</Arg></New></Item>
 </Array>

This is equivalent to:
 String[] a = new String[] { "value0", new String("value1") };

-->
<!ELEMENT Array (Item*)>
<!ATTLIST Array %TYPEATTR; %IDATTR; >

<!--
Map Element.
This element allows the creation of a new array as part of a 
value of a Set, Put or Arg element. The type attribute determines
the type of the new array and the contained Item elements 
are used for each element of the array

 <Map>
   <Entry>
     <Item>keyName</Item>
     <Item><New class="java.lang.String"><Arg>value1</Arg></New></Item>
   </Entry>
 </Map>

This is equivalent to:
 String[] a = new String[] { "value0", new String("value1") };

-->
<!ELEMENT Map (Entry*)>
<!ATTLIST Map %IDATTR; >
<!ELEMENT Entry (Item,Item)>


<!--
Item Element.
This element defines an entry for the Array or Map Entry elements.
The optional type attribute can force the type of the value.

An Item element can contain value text and/or the value elements Call,
New and SystemProperty. If no value type is specified, then white
space is trimmed out of the value. If it contains multiple value
elements they are added as strings before being converted to any
specified type.
-->
<!ELEMENT Item ( %VALUE; )* >
<!ATTLIST Item %TYPEATTR; %IDATTR; >


<!--
System Property Element.
This element allows JVM System properties to be retrieved as
part of the value of a Set, Put or Arg element.
The name attribute specifies the property name and the optional
default argument provides a default value.

 <SystemProperty name="Test" default="value"/>

This is equivalent to:

 System.getProperty("Test","value");

-->
<!ELEMENT SystemProperty EMPTY>
<!ATTLIST SystemProperty %NAMEATTR; %DEFAULTATTR; %IDATTR;>

<!--
Property Element.
This element allows arbitrary properties to be retrieved as
part of the value of a Set, Put or Arg element.
The name attribute specifies the property name and the optional
default argument provides a default value.

   <Property name="Test" default="value"/>
-->
<!ELEMENT Property EMPTY>
<!ATTLIST Property %NAMEATTR; %DEFAULTATTR; %IDATTR;>




*/
