/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.web.dd;
import org.netbeans.api.web.dd.common.VersionNotSupportedException;
/**
 * Generated interface for WebApp element.<br>
 * The WebApp object is the root of bean graph generated<br>
 * for deployment descriptor(web.xml) file.<br>
 * For getting the root (WebApp object) use the {@link DDProvider#getDDRoot} method.
 *
 *<p><b><font color="red"><em>Important note: Do not provide an implementation of this interface unless you are a DD API provider!</em></font></b>
 *</p>
 */
public interface WebApp extends org.netbeans.api.web.dd.common.RootInterface {
        public static final String PROPERTY_VERSION="dd_version"; //NOI18N
        public static final String VERSION_2_3="2.3"; //NOI18N
        public static final String VERSION_2_4="2.4"; //NOI18N
        public static final int STATE_VALID=0;
        public static final int STATE_INVALID_PARSABLE=1;
        public static final int STATE_INVALID_UNPARSABLE=2;
        public static final String PROPERTY_STATUS="dd_status"; //NOI18N
    
	//public void setVersion(java.lang.String value);
        /** Getter for version property.
         * @return property value
         */        
	public java.lang.String getVersion();
        /** Getter for SAX Parse Error property. 
         * Used when deployment descriptor is in invalid state.
         * @return property value or null if in valid state
         */        
	public org.xml.sax.SAXParseException getError();      
        /** Getter for status property.
         * @return property value
         */        
	public int getStatus();      
        /** Setter for distributable property.
         * @param value property value
         */
        public void setDistributable(boolean value);
        /** Getter for distributable property.
         * @return property value 
         */
	public boolean isDistributable();        

	public void setContextParam(int index, org.netbeans.api.web.dd.InitParam valueInterface);

	public org.netbeans.api.web.dd.InitParam getContextParam(int index);

	public void setContextParam(org.netbeans.api.web.dd.InitParam[] value);

	public org.netbeans.api.web.dd.InitParam[] getContextParam();

	public int sizeContextParam();

	public int addContextParam(org.netbeans.api.web.dd.InitParam valueInterface);

	public int removeContextParam(org.netbeans.api.web.dd.InitParam valueInterface);

	public void setFilter(int index, org.netbeans.api.web.dd.Filter valueInterface);

	public org.netbeans.api.web.dd.Filter getFilter(int index);

	public void setFilter(org.netbeans.api.web.dd.Filter[] value);

	public org.netbeans.api.web.dd.Filter[] getFilter();

	public int sizeFilter();

	public int addFilter(org.netbeans.api.web.dd.Filter valueInterface);

	public int removeFilter(org.netbeans.api.web.dd.Filter valueInterface);

	public void setFilterMapping(int index, org.netbeans.api.web.dd.FilterMapping valueInterface);

	public org.netbeans.api.web.dd.FilterMapping getFilterMapping(int index);

	public void setFilterMapping(org.netbeans.api.web.dd.FilterMapping[] value);

	public org.netbeans.api.web.dd.FilterMapping[] getFilterMapping();

	public int sizeFilterMapping();

	public int addFilterMapping(org.netbeans.api.web.dd.FilterMapping valueInterface);

	public int removeFilterMapping(org.netbeans.api.web.dd.FilterMapping valueInterface);

	public void setListener(int index, org.netbeans.api.web.dd.Listener valueInterface);

	public org.netbeans.api.web.dd.Listener getListener(int index);

	public void setListener(org.netbeans.api.web.dd.Listener[] value);

	public org.netbeans.api.web.dd.Listener[] getListener();

	public int sizeListener();

	public int addListener(org.netbeans.api.web.dd.Listener valueInterface);

	public int removeListener(org.netbeans.api.web.dd.Listener valueInterface);

	public void setServlet(int index, org.netbeans.api.web.dd.Servlet valueInterface);

	public org.netbeans.api.web.dd.Servlet getServlet(int index);

	public void setServlet(org.netbeans.api.web.dd.Servlet[] value);

	public org.netbeans.api.web.dd.Servlet[] getServlet();

	public int sizeServlet();

	public int addServlet(org.netbeans.api.web.dd.Servlet valueInterface);

	public int removeServlet(org.netbeans.api.web.dd.Servlet valueInterface);

	public void setServletMapping(int index, org.netbeans.api.web.dd.ServletMapping valueInterface);

	public org.netbeans.api.web.dd.ServletMapping getServletMapping(int index);

	public void setServletMapping(org.netbeans.api.web.dd.ServletMapping[] value);

	public org.netbeans.api.web.dd.ServletMapping[] getServletMapping();

	public int sizeServletMapping();

	public int addServletMapping(org.netbeans.api.web.dd.ServletMapping valueInterface);

	public int removeServletMapping(org.netbeans.api.web.dd.ServletMapping valueInterface);

        public void setSessionConfig(org.netbeans.api.web.dd.SessionConfig value);
	public org.netbeans.api.web.dd.SessionConfig getSingleSessionConfig();

	public void setMimeMapping(int index, org.netbeans.api.web.dd.MimeMapping valueInterface);

	public org.netbeans.api.web.dd.MimeMapping getMimeMapping(int index);

	public void setMimeMapping(org.netbeans.api.web.dd.MimeMapping[] value);

	public org.netbeans.api.web.dd.MimeMapping[] getMimeMapping();

	public int sizeMimeMapping();

	public int addMimeMapping(org.netbeans.api.web.dd.MimeMapping valueInterface);

	public int removeMimeMapping(org.netbeans.api.web.dd.MimeMapping valueInterface);

        public void setWelcomeFileList(org.netbeans.api.web.dd.WelcomeFileList value);
        public org.netbeans.api.web.dd.WelcomeFileList getSingleWelcomeFileList();
     
	public void setErrorPage(int index, org.netbeans.api.web.dd.ErrorPage valueInterface);

	public org.netbeans.api.web.dd.ErrorPage getErrorPage(int index);

	public void setErrorPage(org.netbeans.api.web.dd.ErrorPage[] value);

	public org.netbeans.api.web.dd.ErrorPage[] getErrorPage();

	public int sizeErrorPage();

	public int addErrorPage(org.netbeans.api.web.dd.ErrorPage valueInterface);

	public int removeErrorPage(org.netbeans.api.web.dd.ErrorPage valueInterface);
        
	public void setJspConfig(org.netbeans.api.web.dd.JspConfig value) throws VersionNotSupportedException;
	public org.netbeans.api.web.dd.JspConfig getSingleJspConfig() throws VersionNotSupportedException;

	public void setSecurityConstraint(int index, org.netbeans.api.web.dd.SecurityConstraint valueInterface);

	public org.netbeans.api.web.dd.SecurityConstraint getSecurityConstraint(int index);

	public void setSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint[] value);

	public org.netbeans.api.web.dd.SecurityConstraint[] getSecurityConstraint();

	public int sizeSecurityConstraint();

	public int addSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint valueInterface);

	public int removeSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint valueInterface);
        
	public void setLoginConfig(org.netbeans.api.web.dd.LoginConfig value);
	public org.netbeans.api.web.dd.LoginConfig getSingleLoginConfig();

	public void setSecurityRole(int index, org.netbeans.api.web.dd.SecurityRole valueInterface);

	public org.netbeans.api.web.dd.SecurityRole getSecurityRole(int index);

	public void setSecurityRole(org.netbeans.api.web.dd.SecurityRole[] value);

	public org.netbeans.api.web.dd.SecurityRole[] getSecurityRole();

	public int sizeSecurityRole();

	public int addSecurityRole(org.netbeans.api.web.dd.SecurityRole valueInterface);

	public int removeSecurityRole(org.netbeans.api.web.dd.SecurityRole valueInterface);

	public void setEnvEntry(int index, org.netbeans.api.web.dd.EnvEntry valueInterface);

	public org.netbeans.api.web.dd.EnvEntry getEnvEntry(int index);

	public void setEnvEntry(org.netbeans.api.web.dd.EnvEntry[] value);

	public org.netbeans.api.web.dd.EnvEntry[] getEnvEntry();

	public int sizeEnvEntry();

	public int addEnvEntry(org.netbeans.api.web.dd.EnvEntry valueInterface);

	public int removeEnvEntry(org.netbeans.api.web.dd.EnvEntry valueInterface);

	public void setEjbRef(int index, org.netbeans.api.web.dd.EjbRef valueInterface);

	public org.netbeans.api.web.dd.EjbRef getEjbRef(int index);

	public void setEjbRef(org.netbeans.api.web.dd.EjbRef[] value);

	public org.netbeans.api.web.dd.EjbRef[] getEjbRef();

	public int sizeEjbRef();

	public int addEjbRef(org.netbeans.api.web.dd.EjbRef valueInterface);

	public int removeEjbRef(org.netbeans.api.web.dd.EjbRef valueInterface);

	public void setEjbLocalRef(int index, org.netbeans.api.web.dd.EjbLocalRef valueInterface);

	public org.netbeans.api.web.dd.EjbLocalRef getEjbLocalRef(int index);

	public void setEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef[] value);

	public org.netbeans.api.web.dd.EjbLocalRef[] getEjbLocalRef();

	public int sizeEjbLocalRef();

	public int addEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef valueInterface);

	public int removeEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef valueInterface);

	public void setServiceRef(int index, org.netbeans.api.web.dd.ServiceRef valueInterface) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.ServiceRef getServiceRef(int index) throws VersionNotSupportedException;

	public void setServiceRef(org.netbeans.api.web.dd.ServiceRef[] value) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.ServiceRef[] getServiceRef() throws VersionNotSupportedException;

	public int sizeServiceRef() throws VersionNotSupportedException;

	public int addServiceRef(org.netbeans.api.web.dd.ServiceRef valueInterface) throws VersionNotSupportedException;

	public int removeServiceRef(org.netbeans.api.web.dd.ServiceRef valueInterface) throws VersionNotSupportedException;

	public void setResourceRef(int index, org.netbeans.api.web.dd.ResourceRef valueInterface);

	public org.netbeans.api.web.dd.ResourceRef getResourceRef(int index);

	public void setResourceRef(org.netbeans.api.web.dd.ResourceRef[] value);

	public org.netbeans.api.web.dd.ResourceRef[] getResourceRef();

	public int sizeResourceRef();

	public int addResourceRef(org.netbeans.api.web.dd.ResourceRef valueInterface);

	public int removeResourceRef(org.netbeans.api.web.dd.ResourceRef valueInterface);

	public void setResourceEnvRef(int index, org.netbeans.api.web.dd.ResourceEnvRef valueInterface);

	public org.netbeans.api.web.dd.ResourceEnvRef getResourceEnvRef(int index);

	public void setResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef[] value);

	public org.netbeans.api.web.dd.ResourceEnvRef[] getResourceEnvRef();

	public int sizeResourceEnvRef();

	public int addResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef valueInterface);

	public int removeResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef valueInterface);

	public void setMessageDestinationRef(int index, org.netbeans.api.web.dd.MessageDestinationRef valueInterface) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.MessageDestinationRef getMessageDestinationRef(int index) throws VersionNotSupportedException;

	public void setMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef[] value) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.MessageDestinationRef[] getMessageDestinationRef() throws VersionNotSupportedException;

	public int sizeMessageDestinationRef() throws VersionNotSupportedException;

	public int addMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef valueInterface) throws VersionNotSupportedException;

	public int removeMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef valueInterface) throws VersionNotSupportedException;

	public void setMessageDestination(int index, org.netbeans.api.web.dd.MessageDestination valueInterface) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.MessageDestination getMessageDestination(int index) throws VersionNotSupportedException;

	public void setMessageDestination(org.netbeans.api.web.dd.MessageDestination[] value) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.MessageDestination[] getMessageDestination() throws VersionNotSupportedException;

	public int sizeMessageDestination() throws VersionNotSupportedException;

	public int addMessageDestination(org.netbeans.api.web.dd.MessageDestination valueInterface) throws VersionNotSupportedException;

	public int removeMessageDestination(org.netbeans.api.web.dd.MessageDestination valueInterface) throws VersionNotSupportedException;

	public org.netbeans.api.web.dd.LocaleEncodingMappingList getSingleLocaleEncodingMappingList() throws VersionNotSupportedException;
        
	public void setLocaleEncodingMappingList(org.netbeans.api.web.dd.LocaleEncodingMappingList value) throws VersionNotSupportedException;

        // due to compatibility with servlet2.3
	public void setTaglib(int index, org.netbeans.api.web.dd.Taglib valueInterface) throws VersionNotSupportedException;
	public org.netbeans.api.web.dd.Taglib getTaglib(int index) throws VersionNotSupportedException;
	public void setTaglib(org.netbeans.api.web.dd.Taglib[] value) throws VersionNotSupportedException;
	public org.netbeans.api.web.dd.Taglib[] getTaglib() throws VersionNotSupportedException;
	public int sizeTaglib() throws VersionNotSupportedException;
	public int addTaglib(org.netbeans.api.web.dd.Taglib valueInterface) throws VersionNotSupportedException;
	public int removeTaglib(org.netbeans.api.web.dd.Taglib valueInterface) throws VersionNotSupportedException;
}
