/**
 *	This generated bean class PluginPackage matches the schema element 'plugin-package'.
 *
 *	Generated on Sun Mar 16 00:24:31 IST 2008
 *
 *	This class matches the root element of the DTD,
 *	and is the root of the following bean graph:
 *
 *	pluginPackage <plugin-package> : PluginPackage
 *		name <name> : String
 *		moduleId <module-id> : String
 *		recommendedDeploymentContext <recommended-deployment-context> : String[0,1]
 *		types <types> : Types
 *			(
 *			  type <type> : String
 *			)[1,n]
 *		tags <tags> : Tags[0,1]
 *			(
 *			  tag <tag> : String
 *			)[0,n]
 *		shortDescription <short-description> : String
 *		longDescription <long-description> : String[0,1]
 *		changeLog <change-log> : String
 *		pageUrl <page-url> : String[0,1]
 *		screenshots <screenshots> : Screenshots[0,1]
 *			(
 *			  screenshot <screenshot> : Screenshot
 *			  	thumbnailUrl <thumbnail-url> : String
 *			  	largeImageUrl <large-image-url> : String
 *			)[1,n]
 *		author <author> : String
 *		licenses <licenses> : Licenses
 *			(
 *			  license <license> : String
 *			  	[attr: osi-approved ENUM #REQUIRED ( true false yes no ) ]
 *			  	[attr: url CDATA #IMPLIED ]
 *			)[1,n]
 *		liferayVersions <liferay-versions> : LiferayVersions
 *			(
 *			  liferayVersion <liferay-version> : String
 *			)[1,n]
 *		deploymentSettings <deployment-settings> : DeploymentSettings[0,1]
 *			(
 *			  setting <setting> : String
 *			  	[attr: name CDATA #IMPLIED ]
 *			  	[attr: value CDATA #IMPLIED ]
 *			)[1,n]
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

// BEGIN_NOI18N

public class PluginPackage extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.websynergy.dd.lpp.impl430.PluginPackageInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String NAME = "Name";	// NOI18N
	static public final String MODULE_ID = "ModuleId";	// NOI18N
	static public final String RECOMMENDED_DEPLOYMENT_CONTEXT = "RecommendedDeploymentContext";	// NOI18N
	static public final String TYPES = "Types";	// NOI18N
	static public final String TAGS = "Tags";	// NOI18N
	static public final String SHORT_DESCRIPTION = "ShortDescription";	// NOI18N
	static public final String LONG_DESCRIPTION = "LongDescription";	// NOI18N
	static public final String CHANGE_LOG = "ChangeLog";	// NOI18N
	static public final String PAGE_URL = "PageUrl";	// NOI18N
	static public final String SCREENSHOTS = "Screenshots";	// NOI18N
	static public final String AUTHOR = "Author";	// NOI18N
	static public final String LICENSES = "Licenses";	// NOI18N
	static public final String LIFERAY_VERSIONS = "LiferayVersions";	// NOI18N
	static public final String DEPLOYMENT_SETTINGS = "DeploymentSettings";	// NOI18N

	public PluginPackage() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public PluginPackage(org.w3c.dom.Node doc, int options) {
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
			doc = GraphManager.createRootElementNode("plugin-package");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "plugin-package"));
		}
		Node n = GraphManager.getElementNode("plugin-package", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "plugin-package", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public PluginPackage(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("plugin-package", "PluginPackage",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, PluginPackage.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(14);
		this.createProperty("name", 	// NOI18N
			NAME, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("module-id", 	// NOI18N
			MODULE_ID, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("recommended-deployment-context", 	// NOI18N
			RECOMMENDED_DEPLOYMENT_CONTEXT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("types", 	// NOI18N
			TYPES, 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Types.class);
		this.createProperty("tags", 	// NOI18N
			TAGS, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Tags.class);
		this.createProperty("short-description", 	// NOI18N
			SHORT_DESCRIPTION, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("long-description", 	// NOI18N
			LONG_DESCRIPTION, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("change-log", 	// NOI18N
			CHANGE_LOG, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("page-url", 	// NOI18N
			PAGE_URL, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("screenshots", 	// NOI18N
			SCREENSHOTS, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Screenshots.class);
		this.createProperty("author", 	// NOI18N
			AUTHOR, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("licenses", 	// NOI18N
			LICENSES, 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Licenses.class);
		this.createProperty("liferay-versions", 	// NOI18N
			LIFERAY_VERSIONS, 
			Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			LiferayVersions.class);
		this.createProperty("deployment-settings", 	// NOI18N
			DEPLOYMENT_SETTINGS, 
			Common.TYPE_0_1 | Common.TYPE_BEAN | Common.TYPE_KEY, 
			DeploymentSettings.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setName(String value) {
		this.setValue(NAME, value);
	}

	//
	public String getName() {
		return (String)this.getValue(NAME);
	}

	// This attribute is mandatory
	public void setModuleId(String value) {
		this.setValue(MODULE_ID, value);
	}

	//
	public String getModuleId() {
		return (String)this.getValue(MODULE_ID);
	}

	// This attribute is optional
	public void setRecommendedDeploymentContext(String value) {
		this.setValue(RECOMMENDED_DEPLOYMENT_CONTEXT, value);
	}

	//
	public String getRecommendedDeploymentContext() {
		return (String)this.getValue(RECOMMENDED_DEPLOYMENT_CONTEXT);
	}

	// This attribute is mandatory
	public void setTypes(Types value) {
		this.setValue(TYPES, value);
	}

	//
	public Types getTypes() {
		return (Types)this.getValue(TYPES);
	}

	// This attribute is optional
	public void setTags(Tags value) {
		this.setValue(TAGS, value);
	}

	//
	public Tags getTags() {
		return (Tags)this.getValue(TAGS);
	}

	// This attribute is mandatory
	public void setShortDescription(String value) {
		this.setValue(SHORT_DESCRIPTION, value);
	}

	//
	public String getShortDescription() {
		return (String)this.getValue(SHORT_DESCRIPTION);
	}

	// This attribute is optional
	public void setLongDescription(String value) {
		this.setValue(LONG_DESCRIPTION, value);
	}

	//
	public String getLongDescription() {
		return (String)this.getValue(LONG_DESCRIPTION);
	}

	// This attribute is mandatory
	public void setChangeLog(String value) {
		this.setValue(CHANGE_LOG, value);
	}

	//
	public String getChangeLog() {
		return (String)this.getValue(CHANGE_LOG);
	}

	// This attribute is optional
	public void setPageUrl(String value) {
		this.setValue(PAGE_URL, value);
	}

	//
	public String getPageUrl() {
		return (String)this.getValue(PAGE_URL);
	}

	// This attribute is optional
	public void setScreenshots(Screenshots value) {
		this.setValue(SCREENSHOTS, value);
	}

	//
	public Screenshots getScreenshots() {
		return (Screenshots)this.getValue(SCREENSHOTS);
	}

	// This attribute is mandatory
	public void setAuthor(String value) {
		this.setValue(AUTHOR, value);
	}

	//
	public String getAuthor() {
		return (String)this.getValue(AUTHOR);
	}

	// This attribute is mandatory
	public void setLicenses(Licenses value) {
		this.setValue(LICENSES, value);
	}

	//
	public Licenses getLicenses() {
		return (Licenses)this.getValue(LICENSES);
	}

	// This attribute is mandatory
	public void setLiferayVersions(LiferayVersions value) {
		this.setValue(LIFERAY_VERSIONS, value);
	}

	//
	public LiferayVersions getLiferayVersions() {
		return (LiferayVersions)this.getValue(LIFERAY_VERSIONS);
	}

	// This attribute is optional
	public void setDeploymentSettings(DeploymentSettings value) {
		this.setValue(DEPLOYMENT_SETTINGS, value);
	}

	//
	public DeploymentSettings getDeploymentSettings() {
		return (DeploymentSettings)this.getValue(DEPLOYMENT_SETTINGS);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Types newTypes() {
		return new Types();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Tags newTags() {
		return new Tags();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Screenshots newScreenshots() {
		return new Screenshots();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Licenses newLicenses() {
		return new Licenses();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public LiferayVersions newLiferayVersions() {
		return new LiferayVersions();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public DeploymentSettings newDeploymentSettings() {
		return new DeploymentSettings();
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
	public static PluginPackage createGraph(org.w3c.dom.Node doc) {
		return new PluginPackage(doc, Common.NO_DEFAULT_VALUES);
	}

	public static PluginPackage createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static PluginPackage createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static PluginPackage createGraph(java.io.InputStream in, boolean validate) {
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
	public static PluginPackage createGraph() {
		return new PluginPackage();
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property name
		if (getName() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getName() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "name", this);	// NOI18N
		}
		// Validating property moduleId
		if (getModuleId() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getModuleId() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "moduleId", this);	// NOI18N
		}
		// Validating property recommendedDeploymentContext
		// Validating property types
		if (getTypes() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getTypes() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "types", this);	// NOI18N
		}
		getTypes().validate();
		// Validating property tags
		if (getTags() != null) {
			getTags().validate();
		}
		// Validating property shortDescription
		if (getShortDescription() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getShortDescription() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "shortDescription", this);	// NOI18N
		}
		// Validating property longDescription
		// Validating property changeLog
		if (getChangeLog() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getChangeLog() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "changeLog", this);	// NOI18N
		}
		// Validating property pageUrl
		// Validating property screenshots
		if (getScreenshots() != null) {
			getScreenshots().validate();
		}
		// Validating property author
		if (getAuthor() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getAuthor() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "author", this);	// NOI18N
		}
		// Validating property licenses
		if (getLicenses() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getLicenses() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "licenses", this);	// NOI18N
		}
		getLicenses().validate();
		// Validating property liferayVersions
		if (getLiferayVersions() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getLiferayVersions() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "liferayVersions", this);	// NOI18N
		}
		getLiferayVersions().validate();
		// Validating property deploymentSettings
		if (getDeploymentSettings() != null) {
			getDeploymentSettings().validate();
		}
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
		str.append("Name");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(NAME, 0, str, indent);

		str.append(indent);
		str.append("ModuleId");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getModuleId();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(MODULE_ID, 0, str, indent);

		str.append(indent);
		str.append("RecommendedDeploymentContext");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRecommendedDeploymentContext();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(RECOMMENDED_DEPLOYMENT_CONTEXT, 0, str, indent);

		str.append(indent);
		str.append("Types");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getTypes();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(TYPES, 0, str, indent);

		str.append(indent);
		str.append("Tags");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getTags();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(TAGS, 0, str, indent);

		str.append(indent);
		str.append("ShortDescription");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getShortDescription();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SHORT_DESCRIPTION, 0, str, indent);

		str.append(indent);
		str.append("LongDescription");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getLongDescription();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(LONG_DESCRIPTION, 0, str, indent);

		str.append(indent);
		str.append("ChangeLog");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getChangeLog();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CHANGE_LOG, 0, str, indent);

		str.append(indent);
		str.append("PageUrl");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPageUrl();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PAGE_URL, 0, str, indent);

		str.append(indent);
		str.append("Screenshots");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getScreenshots();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(SCREENSHOTS, 0, str, indent);

		str.append(indent);
		str.append("Author");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getAuthor();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(AUTHOR, 0, str, indent);

		str.append(indent);
		str.append("Licenses");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getLicenses();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(LICENSES, 0, str, indent);

		str.append(indent);
		str.append("LiferayVersions");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getLiferayVersions();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(LIFERAY_VERSIONS, 0, str, indent);

		str.append(indent);
		str.append("DeploymentSettings");	// NOI18N
		n = (org.netbeans.modules.schema2beans.BaseBean) this.getDeploymentSettings();
		if (n != null)
			n.dump(str, indent + "\t");	// NOI18N
		else
			str.append(indent+"\tnull");	// NOI18N
		this.dumpAttributes(DEPLOYMENT_SETTINGS, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("PluginPackage\n");	// NOI18N
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
