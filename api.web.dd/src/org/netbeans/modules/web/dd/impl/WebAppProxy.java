/*
 * WebAppProxi.java
 *
 * Created on Streda, 2003, november 12, 12:58
 */

package org.netbeans.modules.web.dd.impl;

import org.netbeans.api.web.dd.WebApp;
import java.beans.PropertyChangeSupport;
/**
 *
 * @author  mk115033
 */
public class WebAppProxy implements WebApp {
    private WebApp webApp;
    private String version;
    private java.util.List listeners;
    
    /** Creates a new instance of WebAppProxy */
    public WebAppProxy(WebApp wepApp) {
        this.webApp=wepApp;
        version = webApp.getVersion();
        listeners = new java.util.ArrayList();
    }
    
    public void setOriginal(WebApp webApp) {
        if (this.webApp!=webApp) {
            for (int i=0;i<listeners.size();i++) {
                java.beans.PropertyChangeListener pcl = 
                    (java.beans.PropertyChangeListener)listeners.get(i);
                this.webApp.removePropertyChangeListener(pcl);
                webApp.addPropertyChangeListener(pcl);
                
            }
            this.webApp=webApp;
            setProxyVersion(webApp.getVersion());
        }
    }
    
    public WebApp getOriginal() {
        return webApp;
    }
    
    private void setProxyVersion(java.lang.String value) {
        if (!version.equals(value)) {
            java.beans.PropertyChangeEvent evt = 
                new java.beans.PropertyChangeEvent(this, PROPERTY_VERSION, version, value);
            version=value;
            for (int i=0;i<listeners.size();i++) {
                ((java.beans.PropertyChangeListener)listeners.get(i)).propertyChange(evt);
            }
        }
    }
    
    public void setVersion(java.lang.String value) {
    }
    
    public java.lang.String getVersion() {
        return version;
    }
    
    public void addPropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        webApp.addPropertyChangeListener(pcl);
        listeners.add(pcl);
    }
    
    public void removePropertyChangeListener(java.beans.PropertyChangeListener pcl) {
        webApp.removePropertyChangeListener(pcl);
        listeners.remove(pcl);
    }
    
    public int addContextParam(org.netbeans.api.web.dd.InitParam value) {
        return webApp.addContextParam(value);
    }
    
    public int addEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef value) {
        return webApp.addEjbLocalRef(value);
    }
    
    public int addEjbRef(org.netbeans.api.web.dd.EjbRef value) {
        return webApp.addEjbRef(value);
    }
    
    public int addEnvEntry(org.netbeans.api.web.dd.EnvEntry value) {
        return webApp.addEnvEntry(value);
    }
    
    public int addErrorPage(org.netbeans.api.web.dd.ErrorPage value) {
        return webApp.addErrorPage(value);
    }
    
    public int addFilter(org.netbeans.api.web.dd.Filter value) {
        return webApp.addFilter(value);
    }
    
    public int addFilterMapping(org.netbeans.api.web.dd.FilterMapping value) {
        return webApp.addFilterMapping(value);
    }
    
    public int addListener(org.netbeans.api.web.dd.Listener value) {
        return webApp.addListener(value);
    }
    
    public int addMessageDestination(org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.addMessageDestination(value);
    }
    
    public int addMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.addMessageDestinationRef(value);
    }
    
    public int addMimeMapping(org.netbeans.api.web.dd.MimeMapping value) {
        return webApp.addMimeMapping(value);
    }
    
    public int addResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef value) {
        return addResourceEnvRef(value);
    }
    
    public int addResourceRef(org.netbeans.api.web.dd.ResourceRef value) {
        return webApp.addResourceRef(value);
    }
    
    public int addSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint value) {
        return webApp.addSecurityConstraint(value);
    }
    
    public int addSecurityRole(org.netbeans.api.web.dd.SecurityRole value) {
        return webApp.addSecurityRole(value);
    }
    
    public int addServiceRef(org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.addServiceRef(value);
    }
    
    public int addServlet(org.netbeans.api.web.dd.Servlet value) {
        return webApp.addServlet(value);
    }
    
    public int addServletMapping(org.netbeans.api.web.dd.ServletMapping value) {
        return webApp.addServletMapping(value);
    }
    
    public int addTaglib(org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.addTaglib(value);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean createBean(String beanName) throws ClassNotFoundException {
        return webApp.createBean(beanName);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean addBean(String beanName, String[] propertyNames, Object[] propertyValues, String keyProperty) throws ClassNotFoundException, org.netbeans.api.web.dd.common.NameAlreadyUsedException {
        return webApp.addBean(beanName, propertyNames, propertyValues, keyProperty);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean addBean(String beanName) throws ClassNotFoundException {
        return webApp.addBean(beanName);
    }
    
    public org.netbeans.api.web.dd.common.CommonDDBean findBeanByName(String beanName, String propertyName, String value) {
        return webApp.findBeanByName(beanName, propertyName, value);
    }
    
    public java.util.Map getAllDescriptions() {
        return webApp.getAllDescriptions();
    }
    
    public java.util.Map getAllDisplayNames() {
        return webApp.getAllDisplayNames();
    }
    
    public java.util.Map getAllIcons() {
        return webApp.getAllIcons();
    }
    
    public org.netbeans.api.web.dd.InitParam[] getContextParam() {
        return webApp.getContextParam();
    }
    
    public org.netbeans.api.web.dd.InitParam getContextParam(int index) {
        return webApp.getContextParam(index);
    }
    
    public String getDefaultDescription() {
        return webApp.getDefaultDescription();
    }
    
    public String getDefaultDisplayName() {
        return webApp.getDefaultDisplayName();
    }
    
    public org.netbeans.api.web.dd.Icon getDefaultIcon() {
        return webApp.getDefaultIcon();
    }
    
    public String getDescription(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getDescription(locale);
    }
    
    public String getDisplayName(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getDisplayName(locale);
    }
    
    public org.netbeans.api.web.dd.EjbLocalRef[] getEjbLocalRef() {
        return webApp.getEjbLocalRef();
    }
    
    public org.netbeans.api.web.dd.EjbLocalRef getEjbLocalRef(int index) {
        return webApp.getEjbLocalRef(index);
    }
    
    public org.netbeans.api.web.dd.EjbRef[] getEjbRef() {
        return webApp.getEjbRef();
    }
    
    public org.netbeans.api.web.dd.EjbRef getEjbRef(int index) {
        return webApp.getEjbRef(index);
    }
    
    public org.netbeans.api.web.dd.EnvEntry[] getEnvEntry() {
        return webApp.getEnvEntry();
    }
    
    public org.netbeans.api.web.dd.EnvEntry getEnvEntry(int index) {
        return webApp.getEnvEntry(index);
    }
    
    public org.netbeans.api.web.dd.ErrorPage[] getErrorPage() {
        return webApp.getErrorPage();
    }
    
    public org.netbeans.api.web.dd.ErrorPage getErrorPage(int index) {
        return webApp.getErrorPage(index);
    }
    
    public org.netbeans.api.web.dd.Filter[] getFilter() {
        return webApp.getFilter();
    }
    
    public org.netbeans.api.web.dd.Filter getFilter(int index) {
        return webApp.getFilter(index);
    }
    
    public org.netbeans.api.web.dd.FilterMapping[] getFilterMapping() {
        return webApp.getFilterMapping();
    }
    
    public org.netbeans.api.web.dd.FilterMapping getFilterMapping(int index) {
        return webApp.getFilterMapping(index);
    }
    
    public java.lang.String getId() {
        return webApp.getId();
    }
    
    public String getLargeIcon() {
        return webApp.getLargeIcon();
    }
    
    public String getLargeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getLargeIcon(locale);
    }
    
    public org.netbeans.api.web.dd.Listener[] getListener() {
        return webApp.getListener();
    }
    
    public org.netbeans.api.web.dd.Listener getListener(int index) {
        return webApp.getListener(index);
    }
    
    public org.netbeans.api.web.dd.LocaleEncodingMappingList getSingleLocaleEncodingMappingList() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getSingleLocaleEncodingMappingList();
    }
    
    public org.netbeans.api.web.dd.MessageDestination[] getMessageDestination() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getMessageDestination();
    }
    
    public org.netbeans.api.web.dd.MessageDestination getMessageDestination(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getMessageDestination(index);
    }
    
    public org.netbeans.api.web.dd.MessageDestinationRef[] getMessageDestinationRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getMessageDestinationRef();
    }
    
    public org.netbeans.api.web.dd.MessageDestinationRef getMessageDestinationRef(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getMessageDestinationRef(index);
    }
    
    public org.netbeans.api.web.dd.MimeMapping[] getMimeMapping() {
        return webApp.getMimeMapping();
    }
    
    public org.netbeans.api.web.dd.MimeMapping getMimeMapping(int index) {
        return webApp.getMimeMapping(index);
    }
    
    public org.netbeans.api.web.dd.ResourceEnvRef[] getResourceEnvRef() {
        return webApp.getResourceEnvRef();
    }
    
    public org.netbeans.api.web.dd.ResourceEnvRef getResourceEnvRef(int index) {
        return webApp.getResourceEnvRef(index);
    }
    
    public org.netbeans.api.web.dd.ResourceRef[] getResourceRef() {
        return webApp.getResourceRef();
    }
    
    public org.netbeans.api.web.dd.ResourceRef getResourceRef(int index) {
        return webApp.getResourceRef(index);
    }
    
    public org.netbeans.api.web.dd.SecurityConstraint[] getSecurityConstraint() {
        return webApp.getSecurityConstraint();
    }
    
    public org.netbeans.api.web.dd.SecurityConstraint getSecurityConstraint(int index) {
        return webApp.getSecurityConstraint(index);
    }
    
    public org.netbeans.api.web.dd.SecurityRole[] getSecurityRole() {
        return webApp.getSecurityRole();
    }
    
    public org.netbeans.api.web.dd.SecurityRole getSecurityRole(int index) {
        return webApp.getSecurityRole(index);
    }
    
    public org.netbeans.api.web.dd.ServiceRef[] getServiceRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getServiceRef();
    }
    
    public org.netbeans.api.web.dd.ServiceRef getServiceRef(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getServiceRef(index);
    }
    
    public org.netbeans.api.web.dd.Servlet[] getServlet() {
        return webApp.getServlet();
    }
    
    public org.netbeans.api.web.dd.Servlet getServlet(int index) {
        return webApp.getServlet(index);
    }
    
    public org.netbeans.api.web.dd.ServletMapping[] getServletMapping() {
        return webApp.getServletMapping();
    }
    
    public org.netbeans.api.web.dd.ServletMapping getServletMapping(int index) {
        return webApp.getServletMapping(index);
    }
    
    public org.netbeans.api.web.dd.JspConfig getSingleJspConfig() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getSingleJspConfig();
    }
    
    public org.netbeans.api.web.dd.LoginConfig getSingleLoginConfig() {
        return webApp.getSingleLoginConfig();
    }
    
    public org.netbeans.api.web.dd.SessionConfig getSingleSessionConfig() {
        return webApp.getSingleSessionConfig();
    }
    
    public org.netbeans.api.web.dd.WelcomeFileList getSingleWelcomeFileList() {
        return webApp.getSingleWelcomeFileList();
    }
    
    public String getSmallIcon() {
        return webApp.getSmallIcon();
    }
    
    public String getSmallIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getSmallIcon(locale);
    }
    
    public org.netbeans.api.web.dd.Taglib[] getTaglib() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getTaglib();
    }
    
    public org.netbeans.api.web.dd.Taglib getTaglib(int index) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.getTaglib(index);
    }
    
    public Object getValue(String name) {
        return webApp.getValue(name);
    }
    
    public boolean isDistributable() {
        return webApp.isDistributable();
    }
    
    public void merge(org.netbeans.api.web.dd.common.RootInterface bean, int mode) {
        if (bean instanceof WebAppProxy)
            webApp.merge(((WebAppProxy)bean).getOriginal(), mode);
        else webApp.merge(bean, mode); 
    }
    
    public void removeAllDescriptions() {
        webApp.removeAllDescriptions();
    }
    
    public void removeAllDisplayNames() {
        webApp.removeAllDisplayNames();
    }
    
    public void removeAllIcons() {
        webApp.removeAllIcons();
    }
    
    public int removeContextParam(org.netbeans.api.web.dd.InitParam value) {
        return webApp.removeContextParam(value);
    }
    
    public void removeDescription() {
        webApp.removeDescription();
    }
    
    public void removeDescriptionForLocale(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.removeDescriptionForLocale(locale);
    }
    
    public void removeDisplayName() {
        webApp.removeDisplayName();
    }
    
    public void removeDisplayNameForLocale(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.removeDisplayNameForLocale(locale);
    }
    
    public int removeEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef value) {
        return webApp.removeEjbLocalRef(value);
    }
    
    public int removeEjbRef(org.netbeans.api.web.dd.EjbRef value) {
        return webApp.removeEjbRef(value);
    }
    
    public int removeEnvEntry(org.netbeans.api.web.dd.EnvEntry value) {
        return webApp.removeEnvEntry(value);
    }
    
    public int removeErrorPage(org.netbeans.api.web.dd.ErrorPage value) {
        return webApp.removeErrorPage(value);
    }
    
    public int removeFilter(org.netbeans.api.web.dd.Filter value) {
        return webApp.removeFilter(value);
    }
    
    public int removeFilterMapping(org.netbeans.api.web.dd.FilterMapping value) {
        return webApp.removeFilterMapping(value);
    }
    
    public void removeIcon() {
        webApp.removeIcon();
    }
    
    public void removeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.removeIcon(locale);
    }
    
    public void removeLargeIcon() {
        webApp.removeLargeIcon();
    }
    
    public void removeLargeIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.removeLargeIcon(locale);
    }
    
    public int removeListener(org.netbeans.api.web.dd.Listener value) {
        return webApp.removeListener(value);
    }
    
    public int removeMessageDestination(org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.removeMessageDestination(value);
    }
    
    public int removeMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.removeMessageDestinationRef(value);
    }
    
    public int removeMimeMapping(org.netbeans.api.web.dd.MimeMapping value) {
        return webApp.removeMimeMapping(value);
    }
    
    public int removeResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef value) {
        return webApp.removeResourceEnvRef(value);
    }
    
    public int removeResourceRef(org.netbeans.api.web.dd.ResourceRef value) {
        return webApp.removeResourceRef(value);
    }
    
    public int removeSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint value) {
        return webApp.removeSecurityConstraint(value);
    }
    
    public int removeSecurityRole(org.netbeans.api.web.dd.SecurityRole value) {
        return webApp.removeSecurityRole(value);
    }
    
    public int removeServiceRef(org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.removeServiceRef(value);
    }
    
    public int removeServlet(org.netbeans.api.web.dd.Servlet value) {
        return webApp.removeServlet(value);
    }
    
    public int removeServletMapping(org.netbeans.api.web.dd.ServletMapping value) {
        return webApp.removeServletMapping(value);
    }
    
    public void removeSmallIcon() {
        webApp.removeSmallIcon();
    }
    
    public void removeSmallIcon(String locale) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.removeSmallIcon(locale);
    }
    
    public int removeTaglib(org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.removeTaglib(value);
    }
    
    public void setAllDescriptions(java.util.Map descriptions) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setAllDescriptions(descriptions);
    }
    
    public void setAllDisplayNames(java.util.Map displayNames) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setAllDisplayNames(displayNames);
    }
    
    public void setAllIcons(String[] locales, String[] smallIcons, String[] largeIcons) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setAllIcons(locales, smallIcons, largeIcons);
    }
    
    public void setContextParam(org.netbeans.api.web.dd.InitParam[] value) {
        webApp.setContextParam(value);
    }
    
    public void setContextParam(int index, org.netbeans.api.web.dd.InitParam value) {
        webApp.setContextParam(index, value);
    }
    
    public void setDescription(String description) {
        webApp.setDescription(description);
    }
    
    public void setDescription(String locale, String description) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setDescription(locale, description);
    }
    
    public void setDisplayName(String displayName) {
        webApp.setDisplayName(displayName);
    }
    
    public void setDisplayName(String locale, String displayName) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setDisplayName(locale, displayName);
    }
    
    public void setDistributable(boolean value) {
        webApp.setDistributable(value);
    }
    
    public void setEjbLocalRef(org.netbeans.api.web.dd.EjbLocalRef[] value) {
        webApp.setEjbLocalRef(value);
    }
    
    public void setEjbLocalRef(int index, org.netbeans.api.web.dd.EjbLocalRef value) {
        webApp.setEjbLocalRef(index, value);
    }
    
    public void setEjbRef(org.netbeans.api.web.dd.EjbRef[] value) {
        webApp.setEjbRef(value);
    }
    
    public void setEjbRef(int index, org.netbeans.api.web.dd.EjbRef value) {
        webApp.setEjbRef(index, value);
    }
    
    public void setEnvEntry(org.netbeans.api.web.dd.EnvEntry[] value) {
        webApp.setEnvEntry(value);
    }
    
    public void setEnvEntry(int index, org.netbeans.api.web.dd.EnvEntry value) {
        webApp.setEnvEntry(index, value);
    }
    
    public void setErrorPage(org.netbeans.api.web.dd.ErrorPage[] value) {
        webApp.setErrorPage(value);
    }
    
    public void setErrorPage(int index, org.netbeans.api.web.dd.ErrorPage value) {
        webApp.setErrorPage(index, value);
    }
    
    public void setFilter(org.netbeans.api.web.dd.Filter[] value) {
        webApp.setFilter(value);
    }
    
    public void setFilter(int index, org.netbeans.api.web.dd.Filter value) {
        webApp.setFilter(index, value);
    }
    
    public void setFilterMapping(org.netbeans.api.web.dd.FilterMapping[] value) {
        webApp.setFilterMapping(value);
    }
    
    public void setFilterMapping(int index, org.netbeans.api.web.dd.FilterMapping value) {
        webApp.setFilterMapping(index, value);
    }
    
    public void setIcon(org.netbeans.api.web.dd.Icon icon) {
        webApp.setIcon(icon);
    }
    
    public void setId(java.lang.String value) {
        webApp.setId(value);
    }
    
    public void setJspConfig(org.netbeans.api.web.dd.JspConfig value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setJspConfig(value);
    }
    
    public void setLargeIcon(String icon) {
        webApp.setLargeIcon(icon);
    }
    
    public void setLargeIcon(String locale, String icon) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setLargeIcon(locale, icon);
    }
    
    public void setListener(org.netbeans.api.web.dd.Listener[] value) {
        webApp.setListener(value);
    }
    
    public void setListener(int index, org.netbeans.api.web.dd.Listener value) {
        webApp.setListener(index, value);
    }
    
    public void setLocaleEncodingMappingList(org.netbeans.api.web.dd.LocaleEncodingMappingList value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setLocaleEncodingMappingList(value);
    }
    
    public void setLoginConfig(org.netbeans.api.web.dd.LoginConfig value) {
        webApp.setLoginConfig(value);
    }
    
    public void setMessageDestination(org.netbeans.api.web.dd.MessageDestination[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setMessageDestination(value);
    }
    
    public void setMessageDestination(int index, org.netbeans.api.web.dd.MessageDestination value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setMessageDestination(index, value);
    }
    
    public void setMessageDestinationRef(org.netbeans.api.web.dd.MessageDestinationRef[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setMessageDestinationRef(value);
    }
    
    public void setMessageDestinationRef(int index, org.netbeans.api.web.dd.MessageDestinationRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setMessageDestinationRef(index, value);
    }
    
    public void setMimeMapping(org.netbeans.api.web.dd.MimeMapping[] value) {
        webApp.setMimeMapping(value);
    }
    
    public void setMimeMapping(int index, org.netbeans.api.web.dd.MimeMapping value) {
        webApp.setMimeMapping(index, value);
    }
    
    public void setResourceEnvRef(org.netbeans.api.web.dd.ResourceEnvRef[] value) {
        webApp.setResourceEnvRef(value);
    }
    
    public void setResourceEnvRef(int index, org.netbeans.api.web.dd.ResourceEnvRef value) {
        webApp.setResourceEnvRef(index, value);
    }
    
    public void setResourceRef(org.netbeans.api.web.dd.ResourceRef[] value) {
        webApp.setResourceRef(value);
    }
    
    public void setResourceRef(int index, org.netbeans.api.web.dd.ResourceRef value) {
        webApp.setResourceRef(index, value);
    }
    
    public void setSecurityConstraint(org.netbeans.api.web.dd.SecurityConstraint[] value) {
        webApp.setSecurityConstraint(value);
    }
    
    public void setSecurityConstraint(int index, org.netbeans.api.web.dd.SecurityConstraint value) {
        webApp.setSecurityConstraint(index, value);
    }
    
    public void setSecurityRole(org.netbeans.api.web.dd.SecurityRole[] value) {
        webApp.setSecurityRole(value);
    }
    
    public void setSecurityRole(int index, org.netbeans.api.web.dd.SecurityRole value) {
        webApp.setSecurityRole(index, value);
    }
    
    public void setServiceRef(org.netbeans.api.web.dd.ServiceRef[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setServiceRef(value);
    }
    
    public void setServiceRef(int index, org.netbeans.api.web.dd.ServiceRef value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setServiceRef(index, value);
    }
    
    public void setServlet(org.netbeans.api.web.dd.Servlet[] value) {
        webApp.setServlet(value);
    }
    
    public void setServlet(int index, org.netbeans.api.web.dd.Servlet value) {
        webApp.setServlet(index, value);
    }
    
    public void setServletMapping(org.netbeans.api.web.dd.ServletMapping[] value) {
        webApp.setServletMapping(value);
    }
    
    public void setServletMapping(int index, org.netbeans.api.web.dd.ServletMapping value) {
        webApp.setServletMapping(index, value);
    }
    
    public void setSessionConfig(org.netbeans.api.web.dd.SessionConfig value) {
        webApp.setSessionConfig(value);
    }
    
    public void setSmallIcon(String icon) {
        webApp.setSmallIcon(icon);
    }
    
    public void setSmallIcon(String locale, String icon) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setSmallIcon(locale, icon);
    }
    
    public void setTaglib(org.netbeans.api.web.dd.Taglib[] value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setTaglib(value);
    }
    
    public void setTaglib(int index, org.netbeans.api.web.dd.Taglib value) throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        webApp.setTaglib(index, value);
    }
    
    public void setWelcomeFileList(org.netbeans.api.web.dd.WelcomeFileList value) {
        webApp.setWelcomeFileList(value);
    }
    
    public int sizeContextParam() {
        return webApp.sizeContextParam();
    }
    
    public int sizeEjbLocalRef() {
        return webApp.sizeEjbLocalRef();
    }
    
    public int sizeEjbRef() {
        return webApp.sizeEjbRef();
    }
    
    public int sizeEnvEntry() {
        return webApp.sizeEnvEntry();
    }
    
    public int sizeErrorPage() {
        return webApp.sizeErrorPage();
    }
    
    public int sizeFilter() {
        return webApp.sizeFilter();
    }
    
    public int sizeFilterMapping() {
        return webApp.sizeFilterMapping();
    }
    
    public int sizeListener() {
        return webApp.sizeListener();
    }
    
    public int sizeMessageDestination() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.sizeMessageDestination();
    }
    
    public int sizeMessageDestinationRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.sizeMessageDestinationRef();
    }
    
    public int sizeMimeMapping() {
        return webApp.sizeMimeMapping();
    }
    
    public int sizeResourceEnvRef() {
        return webApp.sizeResourceEnvRef();
    }
    
    public int sizeResourceRef() {
        return webApp.sizeResourceRef();
    }
    
    public int sizeSecurityConstraint() {
        return webApp.sizeSecurityConstraint();
    }
    
    public int sizeSecurityRole() {
        return webApp.sizeSecurityRole();
    }
    
    public int sizeServiceRef() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.sizeServiceRef();
    }
    
    public int sizeServlet() {
        return webApp.sizeServlet();
    }
    
    public int sizeServletMapping() {
        return webApp.sizeServletMapping();
    }
    
    public int sizeTaglib() throws org.netbeans.api.web.dd.common.VersionNotSupportedException {
        return webApp.sizeTaglib();
    }
    
    public void write(java.io.OutputStream os) throws java.io.IOException {
        webApp.write(os);
    }
    
    public void write(org.openide.filesystems.FileObject fo) throws java.io.IOException {
        webApp.write(fo);
    }    
    
    public Object clone() {
        return webApp.clone();
    }
    
}
