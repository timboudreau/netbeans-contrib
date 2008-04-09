/**
 *	This generated bean class LiferayPortletApp matches the schema element 'liferay-portlet-app'.
 *
 *	Generated on Sun Mar 16 00:21:05 IST 2008
 *
 *	This class matches the root element of the DTD,
 *	and is the root of the following bean graph:
 *
 *	liferayPortletApp <liferay-portlet-app> : LiferayPortletApp
 *		portlet <portlet> : Portlet[0,n]
 *			portletName <portlet-name> : String
 *			icon <icon> : String[0,1]
 *			virtualPath <virtual-path> : String[0,1]
 *			strutsPath <struts-path> : String[0,1]
 *			configurationPath <configuration-path> : String[0,1]
 *			configurationActionClass <configuration-action-class> : String[0,1]
 *			indexerClass <indexer-class> : String[0,1]
 *			openSearchClass <open-search-class> : String[0,1]
 *			schedulerClass <scheduler-class> : String[0,1]
 *			portletUrlClass <portlet-url-class> : String[0,1]
 *			friendlyUrlMapperClass <friendly-url-mapper-class> : String[0,1]
 *			urlEncoderClass <url-encoder-class> : String[0,1]
 *			portletDataHandlerClass <portlet-data-handler-class> : String[0,1]
 *			portletLayoutListenerClass <portlet-layout-listener-class> : String[0,1]
 *			activityTrackerInterpreterClass <activity-tracker-interpreter-class> : String[0,1]
 *			smtpMessageListenerClass <smtp-message-listener-class> : String[0,1]
 *			preferencesCompanyWide <preferences-company-wide> : String[0,1]
 *			preferencesUniquePerLayout <preferences-unique-per-layout> : String[0,1]
 *			preferencesOwnedByGroup <preferences-owned-by-group> : String[0,1]
 *			useDefaultTemplate <use-default-template> : String[0,1]
 *			showPortletAccessDenied <show-portlet-access-denied> : String[0,1]
 *			showPortletInactive <show-portlet-inactive> : String[0,1]
 *			actionUrlRedirect <action-url-redirect> : String[0,1]
 *			restoreCurrentView <restore-current-view> : String[0,1]
 *			maximizeEdit <maximize-edit> : String[0,1]
 *			maximizeHelp <maximize-help> : String[0,1]
 *			popUpPrint <pop-up-print> : String[0,1]
 *			layoutCacheable <layout-cacheable> : String[0,1]
 *			instanceable <instanceable> : String[0,1]
 *			privateRequestAttributes <private-request-attributes> : String[0,1]
 *			privateSessionAttributes <private-session-attributes> : String[0,1]
 *			renderWeight <render-weight> : String[0,1]
 *			ajaxable <ajaxable> : String[0,1]
 *			headerPortalCss <header-portal-css> : String[0,n]
 *			headerPortletCss <header-portlet-css> : String[0,n]
 *			headerPortalJavascript <header-portal-javascript> : String[0,n]
 *			headerPortletJavascript <header-portlet-javascript> : String[0,n]
 *			footerPortalCss <footer-portal-css> : String[0,n]
 *			footerPortletCss <footer-portlet-css> : String[0,n]
 *			footerPortalJavascript <footer-portal-javascript> : String[0,n]
 *			footerPortletJavascript <footer-portlet-javascript> : String[0,n]
 *			cssClassWrapper <css-class-wrapper> : String[0,1]
 *			addDefaultResource <add-default-resource> : String[0,1]
 *			system <system> : String[0,1]
 *			active <active> : String[0,1]
 *			include <include> : String[0,1]
 *		roleMapper <role-mapper> : RoleMapper[0,n]
 *			roleName <role-name> : String
 *			roleLink <role-link> : String
 *		customUserAttribute <custom-user-attribute> : CustomUserAttribute[0,n]
 *			name <name> : String[1,n]
 *			customClass <custom-class> : String
 *
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

// BEGIN_NOI18N

public class LiferayPortletApp extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.LiferayPortletAppInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String PORTLET = "Portlet";	// NOI18N
	static public final String ROLE_MAPPER = "RoleMapper";	// NOI18N
	static public final String CUSTOM_USER_ATTRIBUTE = "CustomUserAttribute";	// NOI18N

	public LiferayPortletApp() {
		this(null, Common.USE_DEFAULT_VALUES);
	}

	public LiferayPortletApp(org.w3c.dom.Node doc, int options) {
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
			doc = GraphManager.createRootElementNode("liferay-portlet-app");	// NOI18N
			if (doc == null)
				throw new Schema2BeansException(Common.getMessage(
					"CantCreateDOMRoot_msg", "liferay-portlet-app"));
		}
		Node n = GraphManager.getElementNode("liferay-portlet-app", doc);	// NOI18N
		if (n == null)
			throw new Schema2BeansException(Common.getMessage(
				"DocRootNotInDOMGraph_msg", "liferay-portlet-app", doc.getFirstChild().getNodeName()));

		this.graphManager.setXmlDocument(doc);

		// Entry point of the createBeans() recursive calls
		this.createBean(n, this.graphManager());
		this.initialize(options);
	}
	public LiferayPortletApp(int options)
	{
		super(comparators, runtimeVersion);
		initOptions(options);
	}
	protected void initOptions(int options)
	{
		// The graph manager is allocated in the bean root
		this.graphManager = new GraphManager(this);
		this.createRoot("liferay-portlet-app", "LiferayPortletApp",	// NOI18N
			Common.TYPE_1 | Common.TYPE_BEAN, LiferayPortletApp.class);

		// Properties (see root bean comments for the bean graph)
		initPropertyTables(3);
		this.createProperty("portlet", 	// NOI18N
			PORTLET, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			Portlet.class);
		this.createProperty("role-mapper", 	// NOI18N
			ROLE_MAPPER, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			RoleMapper.class);
		this.createProperty("custom-user-attribute", 	// NOI18N
			CUSTOM_USER_ATTRIBUTE, 
			Common.TYPE_0_N | Common.TYPE_BEAN | Common.TYPE_KEY, 
			CustomUserAttribute.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is an array, possibly empty
	public void setPortlet(int index, Portlet value) {
		this.setValue(PORTLET, index, value);
	}

	//
	public Portlet getPortlet(int index) {
		return (Portlet)this.getValue(PORTLET, index);
	}

	// Return the number of properties
	public int sizePortlet() {
		return this.size(PORTLET);
	}

	// This attribute is an array, possibly empty
	public void setPortlet(Portlet[] value) {
		this.setValue(PORTLET, value);
	}

	//
	public Portlet[] getPortlet() {
		return (Portlet[])this.getValues(PORTLET);
	}

	// Add a new element returning its index in the list
	public int addPortlet(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.Portlet value) {
		int positionOfNewItem = this.addValue(PORTLET, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removePortlet(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.Portlet value) {
		return this.removeValue(PORTLET, value);
	}

	// This attribute is an array, possibly empty
	public void setRoleMapper(int index, RoleMapper value) {
		this.setValue(ROLE_MAPPER, index, value);
	}

	//
	public RoleMapper getRoleMapper(int index) {
		return (RoleMapper)this.getValue(ROLE_MAPPER, index);
	}

	// Return the number of properties
	public int sizeRoleMapper() {
		return this.size(ROLE_MAPPER);
	}

	// This attribute is an array, possibly empty
	public void setRoleMapper(RoleMapper[] value) {
		this.setValue(ROLE_MAPPER, value);
	}

	//
	public RoleMapper[] getRoleMapper() {
		return (RoleMapper[])this.getValues(ROLE_MAPPER);
	}

	// Add a new element returning its index in the list
	public int addRoleMapper(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.RoleMapper value) {
		int positionOfNewItem = this.addValue(ROLE_MAPPER, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeRoleMapper(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.RoleMapper value) {
		return this.removeValue(ROLE_MAPPER, value);
	}

	// This attribute is an array, possibly empty
	public void setCustomUserAttribute(int index, CustomUserAttribute value) {
		this.setValue(CUSTOM_USER_ATTRIBUTE, index, value);
	}

	//
	public CustomUserAttribute getCustomUserAttribute(int index) {
		return (CustomUserAttribute)this.getValue(CUSTOM_USER_ATTRIBUTE, index);
	}

	// Return the number of properties
	public int sizeCustomUserAttribute() {
		return this.size(CUSTOM_USER_ATTRIBUTE);
	}

	// This attribute is an array, possibly empty
	public void setCustomUserAttribute(CustomUserAttribute[] value) {
		this.setValue(CUSTOM_USER_ATTRIBUTE, value);
	}

	//
	public CustomUserAttribute[] getCustomUserAttribute() {
		return (CustomUserAttribute[])this.getValues(CUSTOM_USER_ATTRIBUTE);
	}

	// Add a new element returning its index in the list
	public int addCustomUserAttribute(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.CustomUserAttribute value) {
		int positionOfNewItem = this.addValue(CUSTOM_USER_ATTRIBUTE, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeCustomUserAttribute(org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.CustomUserAttribute value) {
		return this.removeValue(CUSTOM_USER_ATTRIBUTE, value);
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public Portlet newPortlet() {
		return new Portlet();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public RoleMapper newRoleMapper() {
		return new RoleMapper();
	}

	/**
	 * Create a new bean using it's default constructor.
	 * This does not add it to any bean graph.
	 */
	public CustomUserAttribute newCustomUserAttribute() {
		return new CustomUserAttribute();
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
	public static LiferayPortletApp createGraph(org.w3c.dom.Node doc) {
		return new LiferayPortletApp(doc, Common.NO_DEFAULT_VALUES);
	}

	public static LiferayPortletApp createGraph(java.io.File f) throws java.io.IOException {
		java.io.InputStream in = new java.io.FileInputStream(f);
		try {
			return createGraph(in, false);
		} finally {
			in.close();
		}
	}

	public static LiferayPortletApp createGraph(java.io.InputStream in) {
		return createGraph(in, false);
	}

	public static LiferayPortletApp createGraph(java.io.InputStream in, boolean validate) {
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
	public static LiferayPortletApp createGraph() {
		return new LiferayPortletApp();
	}

	public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
		boolean restrictionFailure = false;
		boolean restrictionPassed = false;
		// Validating property portlet
		for (int _index = 0; _index < sizePortlet(); ++_index) {
			org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.Portlet element = getPortlet(_index);
			if (element != null) {
				element.validate();
			}
		}
		// Validating property roleMapper
		for (int _index = 0; _index < sizeRoleMapper(); ++_index) {
			org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.RoleMapper element = getRoleMapper(_index);
			if (element != null) {
				element.validate();
			}
		}
		// Validating property customUserAttribute
		for (int _index = 0; _index < sizeCustomUserAttribute(); ++_index) {
			org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.CustomUserAttribute element = getCustomUserAttribute(_index);
			if (element != null) {
				element.validate();
			}
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
		str.append("Portlet["+this.sizePortlet()+"]");	// NOI18N
		for(int i=0; i<this.sizePortlet(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getPortlet(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(PORTLET, i, str, indent);
		}

		str.append(indent);
		str.append("RoleMapper["+this.sizeRoleMapper()+"]");	// NOI18N
		for(int i=0; i<this.sizeRoleMapper(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getRoleMapper(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(ROLE_MAPPER, i, str, indent);
		}

		str.append(indent);
		str.append("CustomUserAttribute["+this.sizeCustomUserAttribute()+"]");	// NOI18N
		for(int i=0; i<this.sizeCustomUserAttribute(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			n = (org.netbeans.modules.schema2beans.BaseBean) this.getCustomUserAttribute(i);
			if (n != null)
				n.dump(str, indent + "\t");	// NOI18N
			else
				str.append(indent+"\tnull");	// NOI18N
			this.dumpAttributes(CUSTOM_USER_ATTRIBUTE, i, str, indent);
		}

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("LiferayPortletApp\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N

