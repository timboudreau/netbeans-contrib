/**
 *	This generated bean class Portlet matches the schema element 'portlet'.
 *  The root bean class is LiferayPortletApp
 *
 *	Generated on Sun Mar 16 00:21:05 IST 2008
 * @Generated
 */

package org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440;

import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;

// BEGIN_NOI18N

public class Portlet extends org.netbeans.modules.schema2beans.BaseBean
	 implements org.netbeans.modules.portalpack.servers.liferay.dd.lp.impl440.PortletInterface
{

	static Vector comparators = new Vector();
	private static final org.netbeans.modules.schema2beans.Version runtimeVersion = new org.netbeans.modules.schema2beans.Version(5, 0, 0);

	static public final String PORTLET_NAME = "PortletName";	// NOI18N
	static public final String ICON = "Icon";	// NOI18N
	static public final String VIRTUAL_PATH = "VirtualPath";	// NOI18N
	static public final String STRUTS_PATH = "StrutsPath";	// NOI18N
	static public final String CONFIGURATION_PATH = "ConfigurationPath";	// NOI18N
	static public final String CONFIGURATION_ACTION_CLASS = "ConfigurationActionClass";	// NOI18N
	static public final String INDEXER_CLASS = "IndexerClass";	// NOI18N
	static public final String OPEN_SEARCH_CLASS = "OpenSearchClass";	// NOI18N
	static public final String SCHEDULER_CLASS = "SchedulerClass";	// NOI18N
	static public final String PORTLET_URL_CLASS = "PortletUrlClass";	// NOI18N
	static public final String FRIENDLY_URL_MAPPER_CLASS = "FriendlyUrlMapperClass";	// NOI18N
	static public final String URL_ENCODER_CLASS = "UrlEncoderClass";	// NOI18N
	static public final String PORTLET_DATA_HANDLER_CLASS = "PortletDataHandlerClass";	// NOI18N
	static public final String PORTLET_LAYOUT_LISTENER_CLASS = "PortletLayoutListenerClass";	// NOI18N
	static public final String ACTIVITY_TRACKER_INTERPRETER_CLASS = "ActivityTrackerInterpreterClass";	// NOI18N
	static public final String SMTP_MESSAGE_LISTENER_CLASS = "SmtpMessageListenerClass";	// NOI18N
	static public final String PREFERENCES_COMPANY_WIDE = "PreferencesCompanyWide";	// NOI18N
	static public final String PREFERENCES_UNIQUE_PER_LAYOUT = "PreferencesUniquePerLayout";	// NOI18N
	static public final String PREFERENCES_OWNED_BY_GROUP = "PreferencesOwnedByGroup";	// NOI18N
	static public final String USE_DEFAULT_TEMPLATE = "UseDefaultTemplate";	// NOI18N
	static public final String SHOW_PORTLET_ACCESS_DENIED = "ShowPortletAccessDenied";	// NOI18N
	static public final String SHOW_PORTLET_INACTIVE = "ShowPortletInactive";	// NOI18N
	static public final String ACTION_URL_REDIRECT = "ActionUrlRedirect";	// NOI18N
	static public final String RESTORE_CURRENT_VIEW = "RestoreCurrentView";	// NOI18N
	static public final String MAXIMIZE_EDIT = "MaximizeEdit";	// NOI18N
	static public final String MAXIMIZE_HELP = "MaximizeHelp";	// NOI18N
	static public final String POP_UP_PRINT = "PopUpPrint";	// NOI18N
	static public final String LAYOUT_CACHEABLE = "LayoutCacheable";	// NOI18N
	static public final String INSTANCEABLE = "Instanceable";	// NOI18N
	static public final String PRIVATE_REQUEST_ATTRIBUTES = "PrivateRequestAttributes";	// NOI18N
	static public final String PRIVATE_SESSION_ATTRIBUTES = "PrivateSessionAttributes";	// NOI18N
	static public final String RENDER_WEIGHT = "RenderWeight";	// NOI18N
	static public final String AJAXABLE = "Ajaxable";	// NOI18N
	static public final String HEADER_PORTAL_CSS = "HeaderPortalCss";	// NOI18N
	static public final String HEADER_PORTLET_CSS = "HeaderPortletCss";	// NOI18N
	static public final String HEADER_PORTAL_JAVASCRIPT = "HeaderPortalJavascript";	// NOI18N
	static public final String HEADER_PORTLET_JAVASCRIPT = "HeaderPortletJavascript";	// NOI18N
	static public final String FOOTER_PORTAL_CSS = "FooterPortalCss";	// NOI18N
	static public final String FOOTER_PORTLET_CSS = "FooterPortletCss";	// NOI18N
	static public final String FOOTER_PORTAL_JAVASCRIPT = "FooterPortalJavascript";	// NOI18N
	static public final String FOOTER_PORTLET_JAVASCRIPT = "FooterPortletJavascript";	// NOI18N
	static public final String CSS_CLASS_WRAPPER = "CssClassWrapper";	// NOI18N
	static public final String ADD_DEFAULT_RESOURCE = "AddDefaultResource";	// NOI18N
	static public final String SYSTEM = "System";	// NOI18N
	static public final String ACTIVE = "Active";	// NOI18N
	static public final String INCLUDE = "Include";	// NOI18N

	public Portlet() {
		this(Common.USE_DEFAULT_VALUES);
	}

	public Portlet(int options)
	{
		super(comparators, runtimeVersion);
		// Properties (see root bean comments for the bean graph)
		initPropertyTables(46);
		this.createProperty("portlet-name", 	// NOI18N
			PORTLET_NAME, 
			Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("icon", 	// NOI18N
			ICON, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("virtual-path", 	// NOI18N
			VIRTUAL_PATH, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("struts-path", 	// NOI18N
			STRUTS_PATH, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("configuration-path", 	// NOI18N
			CONFIGURATION_PATH, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("configuration-action-class", 	// NOI18N
			CONFIGURATION_ACTION_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("indexer-class", 	// NOI18N
			INDEXER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("open-search-class", 	// NOI18N
			OPEN_SEARCH_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("scheduler-class", 	// NOI18N
			SCHEDULER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("portlet-url-class", 	// NOI18N
			PORTLET_URL_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("friendly-url-mapper-class", 	// NOI18N
			FRIENDLY_URL_MAPPER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("url-encoder-class", 	// NOI18N
			URL_ENCODER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("portlet-data-handler-class", 	// NOI18N
			PORTLET_DATA_HANDLER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("portlet-layout-listener-class", 	// NOI18N
			PORTLET_LAYOUT_LISTENER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("activity-tracker-interpreter-class", 	// NOI18N
			ACTIVITY_TRACKER_INTERPRETER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("smtp-message-listener-class", 	// NOI18N
			SMTP_MESSAGE_LISTENER_CLASS, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("preferences-company-wide", 	// NOI18N
			PREFERENCES_COMPANY_WIDE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("preferences-unique-per-layout", 	// NOI18N
			PREFERENCES_UNIQUE_PER_LAYOUT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("preferences-owned-by-group", 	// NOI18N
			PREFERENCES_OWNED_BY_GROUP, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("use-default-template", 	// NOI18N
			USE_DEFAULT_TEMPLATE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("show-portlet-access-denied", 	// NOI18N
			SHOW_PORTLET_ACCESS_DENIED, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("show-portlet-inactive", 	// NOI18N
			SHOW_PORTLET_INACTIVE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("action-url-redirect", 	// NOI18N
			ACTION_URL_REDIRECT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("restore-current-view", 	// NOI18N
			RESTORE_CURRENT_VIEW, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("maximize-edit", 	// NOI18N
			MAXIMIZE_EDIT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("maximize-help", 	// NOI18N
			MAXIMIZE_HELP, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("pop-up-print", 	// NOI18N
			POP_UP_PRINT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("layout-cacheable", 	// NOI18N
			LAYOUT_CACHEABLE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("instanceable", 	// NOI18N
			INSTANCEABLE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("private-request-attributes", 	// NOI18N
			PRIVATE_REQUEST_ATTRIBUTES, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("private-session-attributes", 	// NOI18N
			PRIVATE_SESSION_ATTRIBUTES, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("render-weight", 	// NOI18N
			RENDER_WEIGHT, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("ajaxable", 	// NOI18N
			AJAXABLE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("header-portal-css", 	// NOI18N
			HEADER_PORTAL_CSS, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("header-portlet-css", 	// NOI18N
			HEADER_PORTLET_CSS, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("header-portal-javascript", 	// NOI18N
			HEADER_PORTAL_JAVASCRIPT, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("header-portlet-javascript", 	// NOI18N
			HEADER_PORTLET_JAVASCRIPT, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("footer-portal-css", 	// NOI18N
			FOOTER_PORTAL_CSS, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("footer-portlet-css", 	// NOI18N
			FOOTER_PORTLET_CSS, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("footer-portal-javascript", 	// NOI18N
			FOOTER_PORTAL_JAVASCRIPT, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("footer-portlet-javascript", 	// NOI18N
			FOOTER_PORTLET_JAVASCRIPT, 
			Common.TYPE_0_N | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("css-class-wrapper", 	// NOI18N
			CSS_CLASS_WRAPPER, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("add-default-resource", 	// NOI18N
			ADD_DEFAULT_RESOURCE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("system", 	// NOI18N
			SYSTEM, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("active", 	// NOI18N
			ACTIVE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.createProperty("include", 	// NOI18N
			INCLUDE, 
			Common.TYPE_0_1 | Common.TYPE_STRING | Common.TYPE_KEY, 
			String.class);
		this.initialize(options);
	}

	// Setting the default values of the properties
	void initialize(int options) {

	}

	// This attribute is mandatory
	public void setPortletName(String value) {
		this.setValue(PORTLET_NAME, value);
	}

	//
	public String getPortletName() {
		return (String)this.getValue(PORTLET_NAME);
	}

	// This attribute is optional
	public void setIcon(String value) {
		this.setValue(ICON, value);
	}

	//
	public String getIcon() {
		return (String)this.getValue(ICON);
	}

	// This attribute is optional
	public void setVirtualPath(String value) {
		this.setValue(VIRTUAL_PATH, value);
	}

	//
	public String getVirtualPath() {
		return (String)this.getValue(VIRTUAL_PATH);
	}

	// This attribute is optional
	public void setStrutsPath(String value) {
		this.setValue(STRUTS_PATH, value);
	}

	//
	public String getStrutsPath() {
		return (String)this.getValue(STRUTS_PATH);
	}

	// This attribute is optional
	public void setConfigurationPath(String value) {
		this.setValue(CONFIGURATION_PATH, value);
	}

	//
	public String getConfigurationPath() {
		return (String)this.getValue(CONFIGURATION_PATH);
	}

	// This attribute is optional
	public void setConfigurationActionClass(String value) {
		this.setValue(CONFIGURATION_ACTION_CLASS, value);
	}

	//
	public String getConfigurationActionClass() {
		return (String)this.getValue(CONFIGURATION_ACTION_CLASS);
	}

	// This attribute is optional
	public void setIndexerClass(String value) {
		this.setValue(INDEXER_CLASS, value);
	}

	//
	public String getIndexerClass() {
		return (String)this.getValue(INDEXER_CLASS);
	}

	// This attribute is optional
	public void setOpenSearchClass(String value) {
		this.setValue(OPEN_SEARCH_CLASS, value);
	}

	//
	public String getOpenSearchClass() {
		return (String)this.getValue(OPEN_SEARCH_CLASS);
	}

	// This attribute is optional
	public void setSchedulerClass(String value) {
		this.setValue(SCHEDULER_CLASS, value);
	}

	//
	public String getSchedulerClass() {
		return (String)this.getValue(SCHEDULER_CLASS);
	}

	// This attribute is optional
	public void setPortletUrlClass(String value) {
		this.setValue(PORTLET_URL_CLASS, value);
	}

	//
	public String getPortletUrlClass() {
		return (String)this.getValue(PORTLET_URL_CLASS);
	}

	// This attribute is optional
	public void setFriendlyUrlMapperClass(String value) {
		this.setValue(FRIENDLY_URL_MAPPER_CLASS, value);
	}

	//
	public String getFriendlyUrlMapperClass() {
		return (String)this.getValue(FRIENDLY_URL_MAPPER_CLASS);
	}

	// This attribute is optional
	public void setUrlEncoderClass(String value) {
		this.setValue(URL_ENCODER_CLASS, value);
	}

	//
	public String getUrlEncoderClass() {
		return (String)this.getValue(URL_ENCODER_CLASS);
	}

	// This attribute is optional
	public void setPortletDataHandlerClass(String value) {
		this.setValue(PORTLET_DATA_HANDLER_CLASS, value);
	}

	//
	public String getPortletDataHandlerClass() {
		return (String)this.getValue(PORTLET_DATA_HANDLER_CLASS);
	}

	// This attribute is optional
	public void setPortletLayoutListenerClass(String value) {
		this.setValue(PORTLET_LAYOUT_LISTENER_CLASS, value);
	}

	//
	public String getPortletLayoutListenerClass() {
		return (String)this.getValue(PORTLET_LAYOUT_LISTENER_CLASS);
	}

	// This attribute is optional
	public void setActivityTrackerInterpreterClass(String value) {
		this.setValue(ACTIVITY_TRACKER_INTERPRETER_CLASS, value);
	}

	//
	public String getActivityTrackerInterpreterClass() {
		return (String)this.getValue(ACTIVITY_TRACKER_INTERPRETER_CLASS);
	}

	// This attribute is optional
	public void setSmtpMessageListenerClass(String value) {
		this.setValue(SMTP_MESSAGE_LISTENER_CLASS, value);
	}

	//
	public String getSmtpMessageListenerClass() {
		return (String)this.getValue(SMTP_MESSAGE_LISTENER_CLASS);
	}

	// This attribute is optional
	public void setPreferencesCompanyWide(String value) {
		this.setValue(PREFERENCES_COMPANY_WIDE, value);
	}

	//
	public String getPreferencesCompanyWide() {
		return (String)this.getValue(PREFERENCES_COMPANY_WIDE);
	}

	// This attribute is optional
	public void setPreferencesUniquePerLayout(String value) {
		this.setValue(PREFERENCES_UNIQUE_PER_LAYOUT, value);
	}

	//
	public String getPreferencesUniquePerLayout() {
		return (String)this.getValue(PREFERENCES_UNIQUE_PER_LAYOUT);
	}

	// This attribute is optional
	public void setPreferencesOwnedByGroup(String value) {
		this.setValue(PREFERENCES_OWNED_BY_GROUP, value);
	}

	//
	public String getPreferencesOwnedByGroup() {
		return (String)this.getValue(PREFERENCES_OWNED_BY_GROUP);
	}

	// This attribute is optional
	public void setUseDefaultTemplate(String value) {
		this.setValue(USE_DEFAULT_TEMPLATE, value);
	}

	//
	public String getUseDefaultTemplate() {
		return (String)this.getValue(USE_DEFAULT_TEMPLATE);
	}

	// This attribute is optional
	public void setShowPortletAccessDenied(String value) {
		this.setValue(SHOW_PORTLET_ACCESS_DENIED, value);
	}

	//
	public String getShowPortletAccessDenied() {
		return (String)this.getValue(SHOW_PORTLET_ACCESS_DENIED);
	}

	// This attribute is optional
	public void setShowPortletInactive(String value) {
		this.setValue(SHOW_PORTLET_INACTIVE, value);
	}

	//
	public String getShowPortletInactive() {
		return (String)this.getValue(SHOW_PORTLET_INACTIVE);
	}

	// This attribute is optional
	public void setActionUrlRedirect(String value) {
		this.setValue(ACTION_URL_REDIRECT, value);
	}

	//
	public String getActionUrlRedirect() {
		return (String)this.getValue(ACTION_URL_REDIRECT);
	}

	// This attribute is optional
	public void setRestoreCurrentView(String value) {
		this.setValue(RESTORE_CURRENT_VIEW, value);
	}

	//
	public String getRestoreCurrentView() {
		return (String)this.getValue(RESTORE_CURRENT_VIEW);
	}

	// This attribute is optional
	public void setMaximizeEdit(String value) {
		this.setValue(MAXIMIZE_EDIT, value);
	}

	//
	public String getMaximizeEdit() {
		return (String)this.getValue(MAXIMIZE_EDIT);
	}

	// This attribute is optional
	public void setMaximizeHelp(String value) {
		this.setValue(MAXIMIZE_HELP, value);
	}

	//
	public String getMaximizeHelp() {
		return (String)this.getValue(MAXIMIZE_HELP);
	}

	// This attribute is optional
	public void setPopUpPrint(String value) {
		this.setValue(POP_UP_PRINT, value);
	}

	//
	public String getPopUpPrint() {
		return (String)this.getValue(POP_UP_PRINT);
	}

	// This attribute is optional
	public void setLayoutCacheable(String value) {
		this.setValue(LAYOUT_CACHEABLE, value);
	}

	//
	public String getLayoutCacheable() {
		return (String)this.getValue(LAYOUT_CACHEABLE);
	}

	// This attribute is optional
	public void setInstanceable(String value) {
		this.setValue(INSTANCEABLE, value);
	}

	//
	public String getInstanceable() {
		return (String)this.getValue(INSTANCEABLE);
	}

	// This attribute is optional
	public void setPrivateRequestAttributes(String value) {
		this.setValue(PRIVATE_REQUEST_ATTRIBUTES, value);
	}

	//
	public String getPrivateRequestAttributes() {
		return (String)this.getValue(PRIVATE_REQUEST_ATTRIBUTES);
	}

	// This attribute is optional
	public void setPrivateSessionAttributes(String value) {
		this.setValue(PRIVATE_SESSION_ATTRIBUTES, value);
	}

	//
	public String getPrivateSessionAttributes() {
		return (String)this.getValue(PRIVATE_SESSION_ATTRIBUTES);
	}

	// This attribute is optional
	public void setRenderWeight(String value) {
		this.setValue(RENDER_WEIGHT, value);
	}

	//
	public String getRenderWeight() {
		return (String)this.getValue(RENDER_WEIGHT);
	}

	// This attribute is optional
	public void setAjaxable(String value) {
		this.setValue(AJAXABLE, value);
	}

	//
	public String getAjaxable() {
		return (String)this.getValue(AJAXABLE);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortalCss(int index, String value) {
		this.setValue(HEADER_PORTAL_CSS, index, value);
	}

	//
	public String getHeaderPortalCss(int index) {
		return (String)this.getValue(HEADER_PORTAL_CSS, index);
	}

	// Return the number of properties
	public int sizeHeaderPortalCss() {
		return this.size(HEADER_PORTAL_CSS);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortalCss(String[] value) {
		this.setValue(HEADER_PORTAL_CSS, value);
	}

	//
	public String[] getHeaderPortalCss() {
		return (String[])this.getValues(HEADER_PORTAL_CSS);
	}

	// Add a new element returning its index in the list
	public int addHeaderPortalCss(String value) {
		int positionOfNewItem = this.addValue(HEADER_PORTAL_CSS, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeHeaderPortalCss(String value) {
		return this.removeValue(HEADER_PORTAL_CSS, value);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortletCss(int index, String value) {
		this.setValue(HEADER_PORTLET_CSS, index, value);
	}

	//
	public String getHeaderPortletCss(int index) {
		return (String)this.getValue(HEADER_PORTLET_CSS, index);
	}

	// Return the number of properties
	public int sizeHeaderPortletCss() {
		return this.size(HEADER_PORTLET_CSS);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortletCss(String[] value) {
		this.setValue(HEADER_PORTLET_CSS, value);
	}

	//
	public String[] getHeaderPortletCss() {
		return (String[])this.getValues(HEADER_PORTLET_CSS);
	}

	// Add a new element returning its index in the list
	public int addHeaderPortletCss(String value) {
		int positionOfNewItem = this.addValue(HEADER_PORTLET_CSS, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeHeaderPortletCss(String value) {
		return this.removeValue(HEADER_PORTLET_CSS, value);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortalJavascript(int index, String value) {
		this.setValue(HEADER_PORTAL_JAVASCRIPT, index, value);
	}

	//
	public String getHeaderPortalJavascript(int index) {
		return (String)this.getValue(HEADER_PORTAL_JAVASCRIPT, index);
	}

	// Return the number of properties
	public int sizeHeaderPortalJavascript() {
		return this.size(HEADER_PORTAL_JAVASCRIPT);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortalJavascript(String[] value) {
		this.setValue(HEADER_PORTAL_JAVASCRIPT, value);
	}

	//
	public String[] getHeaderPortalJavascript() {
		return (String[])this.getValues(HEADER_PORTAL_JAVASCRIPT);
	}

	// Add a new element returning its index in the list
	public int addHeaderPortalJavascript(String value) {
		int positionOfNewItem = this.addValue(HEADER_PORTAL_JAVASCRIPT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeHeaderPortalJavascript(String value) {
		return this.removeValue(HEADER_PORTAL_JAVASCRIPT, value);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortletJavascript(int index, String value) {
		this.setValue(HEADER_PORTLET_JAVASCRIPT, index, value);
	}

	//
	public String getHeaderPortletJavascript(int index) {
		return (String)this.getValue(HEADER_PORTLET_JAVASCRIPT, index);
	}

	// Return the number of properties
	public int sizeHeaderPortletJavascript() {
		return this.size(HEADER_PORTLET_JAVASCRIPT);
	}

	// This attribute is an array, possibly empty
	public void setHeaderPortletJavascript(String[] value) {
		this.setValue(HEADER_PORTLET_JAVASCRIPT, value);
	}

	//
	public String[] getHeaderPortletJavascript() {
		return (String[])this.getValues(HEADER_PORTLET_JAVASCRIPT);
	}

	// Add a new element returning its index in the list
	public int addHeaderPortletJavascript(String value) {
		int positionOfNewItem = this.addValue(HEADER_PORTLET_JAVASCRIPT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeHeaderPortletJavascript(String value) {
		return this.removeValue(HEADER_PORTLET_JAVASCRIPT, value);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortalCss(int index, String value) {
		this.setValue(FOOTER_PORTAL_CSS, index, value);
	}

	//
	public String getFooterPortalCss(int index) {
		return (String)this.getValue(FOOTER_PORTAL_CSS, index);
	}

	// Return the number of properties
	public int sizeFooterPortalCss() {
		return this.size(FOOTER_PORTAL_CSS);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortalCss(String[] value) {
		this.setValue(FOOTER_PORTAL_CSS, value);
	}

	//
	public String[] getFooterPortalCss() {
		return (String[])this.getValues(FOOTER_PORTAL_CSS);
	}

	// Add a new element returning its index in the list
	public int addFooterPortalCss(String value) {
		int positionOfNewItem = this.addValue(FOOTER_PORTAL_CSS, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFooterPortalCss(String value) {
		return this.removeValue(FOOTER_PORTAL_CSS, value);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortletCss(int index, String value) {
		this.setValue(FOOTER_PORTLET_CSS, index, value);
	}

	//
	public String getFooterPortletCss(int index) {
		return (String)this.getValue(FOOTER_PORTLET_CSS, index);
	}

	// Return the number of properties
	public int sizeFooterPortletCss() {
		return this.size(FOOTER_PORTLET_CSS);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortletCss(String[] value) {
		this.setValue(FOOTER_PORTLET_CSS, value);
	}

	//
	public String[] getFooterPortletCss() {
		return (String[])this.getValues(FOOTER_PORTLET_CSS);
	}

	// Add a new element returning its index in the list
	public int addFooterPortletCss(String value) {
		int positionOfNewItem = this.addValue(FOOTER_PORTLET_CSS, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFooterPortletCss(String value) {
		return this.removeValue(FOOTER_PORTLET_CSS, value);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortalJavascript(int index, String value) {
		this.setValue(FOOTER_PORTAL_JAVASCRIPT, index, value);
	}

	//
	public String getFooterPortalJavascript(int index) {
		return (String)this.getValue(FOOTER_PORTAL_JAVASCRIPT, index);
	}

	// Return the number of properties
	public int sizeFooterPortalJavascript() {
		return this.size(FOOTER_PORTAL_JAVASCRIPT);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortalJavascript(String[] value) {
		this.setValue(FOOTER_PORTAL_JAVASCRIPT, value);
	}

	//
	public String[] getFooterPortalJavascript() {
		return (String[])this.getValues(FOOTER_PORTAL_JAVASCRIPT);
	}

	// Add a new element returning its index in the list
	public int addFooterPortalJavascript(String value) {
		int positionOfNewItem = this.addValue(FOOTER_PORTAL_JAVASCRIPT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFooterPortalJavascript(String value) {
		return this.removeValue(FOOTER_PORTAL_JAVASCRIPT, value);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortletJavascript(int index, String value) {
		this.setValue(FOOTER_PORTLET_JAVASCRIPT, index, value);
	}

	//
	public String getFooterPortletJavascript(int index) {
		return (String)this.getValue(FOOTER_PORTLET_JAVASCRIPT, index);
	}

	// Return the number of properties
	public int sizeFooterPortletJavascript() {
		return this.size(FOOTER_PORTLET_JAVASCRIPT);
	}

	// This attribute is an array, possibly empty
	public void setFooterPortletJavascript(String[] value) {
		this.setValue(FOOTER_PORTLET_JAVASCRIPT, value);
	}

	//
	public String[] getFooterPortletJavascript() {
		return (String[])this.getValues(FOOTER_PORTLET_JAVASCRIPT);
	}

	// Add a new element returning its index in the list
	public int addFooterPortletJavascript(String value) {
		int positionOfNewItem = this.addValue(FOOTER_PORTLET_JAVASCRIPT, value);
		return positionOfNewItem;
	}

	//
	// Remove an element using its reference
	// Returns the index the element had in the list
	//
	public int removeFooterPortletJavascript(String value) {
		return this.removeValue(FOOTER_PORTLET_JAVASCRIPT, value);
	}

	// This attribute is optional
	public void setCssClassWrapper(String value) {
		this.setValue(CSS_CLASS_WRAPPER, value);
	}

	//
	public String getCssClassWrapper() {
		return (String)this.getValue(CSS_CLASS_WRAPPER);
	}

	// This attribute is optional
	public void setAddDefaultResource(String value) {
		this.setValue(ADD_DEFAULT_RESOURCE, value);
	}

	//
	public String getAddDefaultResource() {
		return (String)this.getValue(ADD_DEFAULT_RESOURCE);
	}

	// This attribute is optional
	public void setSystem(String value) {
		this.setValue(SYSTEM, value);
	}

	//
	public String getSystem() {
		return (String)this.getValue(SYSTEM);
	}

	// This attribute is optional
	public void setActive(String value) {
		this.setValue(ACTIVE, value);
	}

	//
	public String getActive() {
		return (String)this.getValue(ACTIVE);
	}

	// This attribute is optional
	public void setInclude(String value) {
		this.setValue(INCLUDE, value);
	}

	//
	public String getInclude() {
		return (String)this.getValue(INCLUDE);
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
		// Validating property portletName
		if (getPortletName() == null) {
			throw new org.netbeans.modules.schema2beans.ValidateException("getPortletName() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, "portletName", this);	// NOI18N
		}
		// Validating property icon
		// Validating property virtualPath
		// Validating property strutsPath
		// Validating property configurationPath
		// Validating property configurationActionClass
		// Validating property indexerClass
		// Validating property openSearchClass
		// Validating property schedulerClass
		// Validating property portletUrlClass
		// Validating property friendlyUrlMapperClass
		// Validating property urlEncoderClass
		// Validating property portletDataHandlerClass
		// Validating property portletLayoutListenerClass
		// Validating property activityTrackerInterpreterClass
		// Validating property smtpMessageListenerClass
		// Validating property preferencesCompanyWide
		// Validating property preferencesUniquePerLayout
		// Validating property preferencesOwnedByGroup
		// Validating property useDefaultTemplate
		// Validating property showPortletAccessDenied
		// Validating property showPortletInactive
		// Validating property actionUrlRedirect
		// Validating property restoreCurrentView
		// Validating property maximizeEdit
		// Validating property maximizeHelp
		// Validating property popUpPrint
		// Validating property layoutCacheable
		// Validating property instanceable
		// Validating property privateRequestAttributes
		// Validating property privateSessionAttributes
		// Validating property renderWeight
		// Validating property ajaxable
		// Validating property headerPortalCss
		// Validating property headerPortletCss
		// Validating property headerPortalJavascript
		// Validating property headerPortletJavascript
		// Validating property footerPortalCss
		// Validating property footerPortletCss
		// Validating property footerPortalJavascript
		// Validating property footerPortletJavascript
		// Validating property cssClassWrapper
		// Validating property addDefaultResource
		// Validating property system
		// Validating property active
		// Validating property include
	}

	// Dump the content of this bean returning it as a String
	public void dump(StringBuffer str, String indent){
		String s;
		Object o;
		org.netbeans.modules.schema2beans.BaseBean n;
		str.append(indent);
		str.append("PortletName");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPortletName();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PORTLET_NAME, 0, str, indent);

		str.append(indent);
		str.append("Icon");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getIcon();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ICON, 0, str, indent);

		str.append(indent);
		str.append("VirtualPath");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getVirtualPath();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(VIRTUAL_PATH, 0, str, indent);

		str.append(indent);
		str.append("StrutsPath");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getStrutsPath();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(STRUTS_PATH, 0, str, indent);

		str.append(indent);
		str.append("ConfigurationPath");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getConfigurationPath();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CONFIGURATION_PATH, 0, str, indent);

		str.append(indent);
		str.append("ConfigurationActionClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getConfigurationActionClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CONFIGURATION_ACTION_CLASS, 0, str, indent);

		str.append(indent);
		str.append("IndexerClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getIndexerClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(INDEXER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("OpenSearchClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getOpenSearchClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(OPEN_SEARCH_CLASS, 0, str, indent);

		str.append(indent);
		str.append("SchedulerClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getSchedulerClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SCHEDULER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("PortletUrlClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPortletUrlClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PORTLET_URL_CLASS, 0, str, indent);

		str.append(indent);
		str.append("FriendlyUrlMapperClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getFriendlyUrlMapperClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(FRIENDLY_URL_MAPPER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("UrlEncoderClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getUrlEncoderClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(URL_ENCODER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("PortletDataHandlerClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPortletDataHandlerClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PORTLET_DATA_HANDLER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("PortletLayoutListenerClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPortletLayoutListenerClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PORTLET_LAYOUT_LISTENER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("ActivityTrackerInterpreterClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getActivityTrackerInterpreterClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ACTIVITY_TRACKER_INTERPRETER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("SmtpMessageListenerClass");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getSmtpMessageListenerClass();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SMTP_MESSAGE_LISTENER_CLASS, 0, str, indent);

		str.append(indent);
		str.append("PreferencesCompanyWide");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPreferencesCompanyWide();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PREFERENCES_COMPANY_WIDE, 0, str, indent);

		str.append(indent);
		str.append("PreferencesUniquePerLayout");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPreferencesUniquePerLayout();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PREFERENCES_UNIQUE_PER_LAYOUT, 0, str, indent);

		str.append(indent);
		str.append("PreferencesOwnedByGroup");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPreferencesOwnedByGroup();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PREFERENCES_OWNED_BY_GROUP, 0, str, indent);

		str.append(indent);
		str.append("UseDefaultTemplate");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getUseDefaultTemplate();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(USE_DEFAULT_TEMPLATE, 0, str, indent);

		str.append(indent);
		str.append("ShowPortletAccessDenied");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getShowPortletAccessDenied();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SHOW_PORTLET_ACCESS_DENIED, 0, str, indent);

		str.append(indent);
		str.append("ShowPortletInactive");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getShowPortletInactive();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SHOW_PORTLET_INACTIVE, 0, str, indent);

		str.append(indent);
		str.append("ActionUrlRedirect");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getActionUrlRedirect();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ACTION_URL_REDIRECT, 0, str, indent);

		str.append(indent);
		str.append("RestoreCurrentView");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRestoreCurrentView();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(RESTORE_CURRENT_VIEW, 0, str, indent);

		str.append(indent);
		str.append("MaximizeEdit");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getMaximizeEdit();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(MAXIMIZE_EDIT, 0, str, indent);

		str.append(indent);
		str.append("MaximizeHelp");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getMaximizeHelp();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(MAXIMIZE_HELP, 0, str, indent);

		str.append(indent);
		str.append("PopUpPrint");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPopUpPrint();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(POP_UP_PRINT, 0, str, indent);

		str.append(indent);
		str.append("LayoutCacheable");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getLayoutCacheable();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(LAYOUT_CACHEABLE, 0, str, indent);

		str.append(indent);
		str.append("Instanceable");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getInstanceable();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(INSTANCEABLE, 0, str, indent);

		str.append(indent);
		str.append("PrivateRequestAttributes");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPrivateRequestAttributes();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PRIVATE_REQUEST_ATTRIBUTES, 0, str, indent);

		str.append(indent);
		str.append("PrivateSessionAttributes");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getPrivateSessionAttributes();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(PRIVATE_SESSION_ATTRIBUTES, 0, str, indent);

		str.append(indent);
		str.append("RenderWeight");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getRenderWeight();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(RENDER_WEIGHT, 0, str, indent);

		str.append(indent);
		str.append("Ajaxable");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getAjaxable();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(AJAXABLE, 0, str, indent);

		str.append(indent);
		str.append("HeaderPortalCss["+this.sizeHeaderPortalCss()+"]");	// NOI18N
		for(int i=0; i<this.sizeHeaderPortalCss(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getHeaderPortalCss(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(HEADER_PORTAL_CSS, i, str, indent);
		}

		str.append(indent);
		str.append("HeaderPortletCss["+this.sizeHeaderPortletCss()+"]");	// NOI18N
		for(int i=0; i<this.sizeHeaderPortletCss(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getHeaderPortletCss(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(HEADER_PORTLET_CSS, i, str, indent);
		}

		str.append(indent);
		str.append("HeaderPortalJavascript["+this.sizeHeaderPortalJavascript()+"]");	// NOI18N
		for(int i=0; i<this.sizeHeaderPortalJavascript(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getHeaderPortalJavascript(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(HEADER_PORTAL_JAVASCRIPT, i, str, indent);
		}

		str.append(indent);
		str.append("HeaderPortletJavascript["+this.sizeHeaderPortletJavascript()+"]");	// NOI18N
		for(int i=0; i<this.sizeHeaderPortletJavascript(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getHeaderPortletJavascript(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(HEADER_PORTLET_JAVASCRIPT, i, str, indent);
		}

		str.append(indent);
		str.append("FooterPortalCss["+this.sizeFooterPortalCss()+"]");	// NOI18N
		for(int i=0; i<this.sizeFooterPortalCss(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFooterPortalCss(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FOOTER_PORTAL_CSS, i, str, indent);
		}

		str.append(indent);
		str.append("FooterPortletCss["+this.sizeFooterPortletCss()+"]");	// NOI18N
		for(int i=0; i<this.sizeFooterPortletCss(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFooterPortletCss(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FOOTER_PORTLET_CSS, i, str, indent);
		}

		str.append(indent);
		str.append("FooterPortalJavascript["+this.sizeFooterPortalJavascript()+"]");	// NOI18N
		for(int i=0; i<this.sizeFooterPortalJavascript(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFooterPortalJavascript(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FOOTER_PORTAL_JAVASCRIPT, i, str, indent);
		}

		str.append(indent);
		str.append("FooterPortletJavascript["+this.sizeFooterPortletJavascript()+"]");	// NOI18N
		for(int i=0; i<this.sizeFooterPortletJavascript(); i++)
		{
			str.append(indent+"\t");
			str.append("#"+i+":");
			str.append(indent+"\t");	// NOI18N
			str.append("<");	// NOI18N
			o = this.getFooterPortletJavascript(i);
			str.append((o==null?"null":o.toString().trim()));	// NOI18N
			str.append(">\n");	// NOI18N
			this.dumpAttributes(FOOTER_PORTLET_JAVASCRIPT, i, str, indent);
		}

		str.append(indent);
		str.append("CssClassWrapper");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getCssClassWrapper();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(CSS_CLASS_WRAPPER, 0, str, indent);

		str.append(indent);
		str.append("AddDefaultResource");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getAddDefaultResource();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ADD_DEFAULT_RESOURCE, 0, str, indent);

		str.append(indent);
		str.append("System");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getSystem();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(SYSTEM, 0, str, indent);

		str.append(indent);
		str.append("Active");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getActive();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(ACTIVE, 0, str, indent);

		str.append(indent);
		str.append("Include");	// NOI18N
		str.append(indent+"\t");	// NOI18N
		str.append("<");	// NOI18N
		o = this.getInclude();
		str.append((o==null?"null":o.toString().trim()));	// NOI18N
		str.append(">\n");	// NOI18N
		this.dumpAttributes(INCLUDE, 0, str, indent);

	}
	public String dumpBeanNode(){
		StringBuffer str = new StringBuffer();
		str.append("Portlet\n");	// NOI18N
		this.dump(str, "\n  ");	// NOI18N
		return str.toString();
	}}

// END_NOI18N

