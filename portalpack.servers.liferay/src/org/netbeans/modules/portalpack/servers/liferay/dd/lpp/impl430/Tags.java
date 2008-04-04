/**
 *	This generated bean class Tags matches the schema element 'tags'.
 *  The root bean class is PluginPackage
 *
 *	Generated on Sun Mar 16 00:24:31 IST 2008
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.liferay.dd.lpp.impl430;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class Tags extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.liferay.dd.lpp.impl430.TagsInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String TAG = "Tag";	// NOI18N

	public Tags() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Tags(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(1);
		this.createProperty("tag", 	// NOI18N
			TAG, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setTag(int index, String value) {
		this.setValue(TAG, index, value);
	}

	//
	public String getTag(int index) {
		return (String)this.getValue(TAG, index);
	}

	// Return the number of properties
	public int sizeTag() {
		return this.size(TAG);
	}

	// This attribute is an array, possibly empty
	public void setTag(String[] value) {
		this.setValue(TAG, value);
	}

	//
	public String[] getTag() {
		return (String[])this.getValues(TAG);
	}

	// Add a new element returning its index in the list
	public int addTag(String value) {
		int positionOfNewItem = this.addValue(TAG, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeTag(String value) {
		return this.removeValue(TAG, value);
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
		// Validating property tag
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("Tag["+this.sizeTag()+"]");	// NOI18N
		for(int i=0; i<this.sizeTag(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getTag(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(TAG, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Tags\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N


/*
		The following schema file has been used for generation:

<!--
This is the DTD for the Liferay Plugins XML file that lists the plugins
available in a plugin repository

<!DOCTYPE plugin-package PUBLIC
	"-//Liferay//DTD Plugin Package 4.3.0//EN"
	"http://www.liferay.com/dtd/liferay-plugin-package_4_3_0.dtd">
-->

<!--
A boolean type is the string representation of a boolean (true or false)
variable.
-->
<!ENTITY % boolean "(true|false|yes|no)">

<!--
The plugin-package element contains the declarative data of a plugin.
-->
<!ELEMENT plugin-package (name, module-id, recommended-deployment-context?,
types, tags?, short-description, long-description?, change-log, page-url?,
screenshots?, author, licenses, liferay-versions, deployment-settings?)>

<!--
The name element contains the name of the plugin package that will be shown to
users.
-->
<!ELEMENT name (#PCDATA)>

<!--
The module-id element contains the full identifier of the plugin using the
Maven based syntax: groupId/artifactId/version/file-type.

Example: liferay-samples/sample-struts-portlet/4.3.0/war
-->
<!ELEMENT module-id (#PCDATA)>

<!--
The recommended-deployment-context element determines the context to which this
plugin should be deployed. Some portlet packages require this because their own
code references itself through URLs that include the context.
-->
<!ELEMENT recommended-deployment-context (#PCDATA)>

<!--
The types element contains a list of plugin types included in the package.
-->
<!ELEMENT types (type)+>

<!--
The type element contains the type of the plugin. Valid values are: portlets,
layout-templates, and themes.
-->
<!ELEMENT type (#PCDATA)>

<!--
The tags element contains a list of tags to categorize the plugin.
-->
<!ELEMENT tags (tag)*>

<!--
The tag element contains a tag that categorizes the plugin.
-->
<!ELEMENT tag (#PCDATA)>

<!--
The short-description element contains a short description of the plugin.
-->
<!ELEMENT short-description (#PCDATA)>

<!--
The long-description element contains a detailed description of the plugin. It
is recommended that installation or update instructions are provided if the
portal administrator has to perform extra steps to be able to use the plugin
after it is deployed.

Note: the text of this element might contain simple HTML formatting if encoded
within a CDATA section.
-->
<!ELEMENT long-description (#PCDATA)>

<!--
The change-log element contains an explanation of the changes made in the latest
release. It is recommended to try to offer all the information that a user
might need to decide whether to update a previous version.

Note: the text of this element might contain simple HTML formatting if encoded
within a CDATA section.
-->
<!ELEMENT change-log (#PCDATA)>

<!--
The page-url element contains the URL of the home page of the plugin.
-->
<!ELEMENT page-url (#PCDATA)>

<!--
The screenshots element contains a list of screenshots for the plugin.
-->
<!ELEMENT screenshots (screenshot)+>

<!--
The screenshot element contains two URLs for the thumbnail and large images
versions of the screenshot
-->
<!ELEMENT screenshot (thumbnail-url, large-image-url)>

<!--
The thumbnail-url element contains the URL of a thumbnail screenshot of the
plugin. It is recommended that the width of the images is 120 pixels and that
the height is in the same size range.
-->
<!ELEMENT thumbnail-url (#PCDATA)>

<!--
The large-image-url element contains the URL of a large image screenshot of the
plugin.
-->
<!ELEMENT large-image-url (#PCDATA)>

<!--
The author element contains the name of the author of the plugin.
-->
<!ELEMENT author (#PCDATA)>

<!--
The licenses element contains a list of licences under which the plugin is
provided.
-->
<!ELEMENT licenses (license)+>

<!--
The license element contains the name of a licence under which the plugin is
provided.
-->
<!ELEMENT license (#PCDATA)>

<!--
The osi-approved attribute specifies if the license is open source, approved by
the Open Source Initiative (OSI). In that case it's value is true.
-->
<!ATTLIST license
	osi-approved %boolean; #REQUIRED
>

<!--
The url attribute specifies a URL of a page that describes the license.
-->
<!ATTLIST license
	url CDATA #IMPLIED
>

<!--
The liferay-versions element contains a list of Liferay Portal versions that
are supported by the plugin.
-->
<!ELEMENT liferay-versions (liferay-version)+>

<!--
The liferay-version element contains a version of Liferay Portal that is
supported by the plugin.
-->
<!ELEMENT liferay-version (#PCDATA)>

<!--
The deployment-settings element contains a list of parameters that specify how
the package should be deployed.
-->
<!ELEMENT deployment-settings (setting)+>

<!--
The setting element specifies a name value pair that provides information of how
the package should be deployed.
-->
<!ELEMENT setting (#PCDATA)>

<!--
The name attribute specifies the name of the setting.
-->
<!ATTLIST setting
	name CDATA #IMPLIED
>

<!--
The value attribute specifies the value of the setting.
-->
<!ATTLIST setting
	value CDATA #IMPLIED
>
*/
