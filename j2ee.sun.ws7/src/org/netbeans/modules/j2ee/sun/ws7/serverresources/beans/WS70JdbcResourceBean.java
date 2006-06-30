/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70JdbcResourceBean.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;
import java.beans.Beans;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70JdbcResource;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.PropertyElement;

import java.util.Vector;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

/**
 *
 * @author Administrator
 */
public class WS70JdbcResourceBean extends WS70BaseResourceBean implements java.io.Serializable{
    private String dsClass;
    private String minConnections;
    private String maxConnections;
    private String waitTimeout;
    private String idleTimeout;
    private String isolationLevel;
    private String isolationLevelGuaranteed;
    private String connectionValidation;
    private String connectionValidationTablename;
    private String failAllConnections;
    /** Creates a new instance of WS70JdbcResourceBean */
    public WS70JdbcResourceBean() {
    }
    public String getDsClass() {
        return dsClass;
    }
    public void setDsClass(String value) {
        String oldValue = dsClass;
        this.dsClass = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("dsClass", oldValue, dsClass);//NOI18N
    }        
    public String getMinConnections() {
        return minConnections;
    }
    public void setMinConnections(String value) {
        String oldValue = minConnections;
        this.minConnections = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("minConnections", oldValue, minConnections);//NOI18N
    }    
    public String getMaxConnections() {
        return maxConnections;
    }    
    public void setMaxConnections(String value) {
        String oldValue = maxConnections;
        this.maxConnections = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("maxConnections", oldValue, maxConnections);//NOI18N
    }
    public String getIdleTimeout() {
        return idleTimeout;
    }
    public void setIdleTimeout(String value) {
        String oldValue = idleTimeout;
        this.idleTimeout = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("idleTimeout", oldValue, idleTimeout);//NOI18N
    }
    public String getWaitTimeout() {
        return waitTimeout;
    }
    public void setWaitTimeout(String value) {
        String oldValue = waitTimeout;
        this.waitTimeout = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("waitTimeout", oldValue, waitTimeout);//NOI18N
    }
    public String getIsolationLevel() {
        return isolationLevel;
    }
    public void setIsolationLevel(String value) {
        String oldValue = isolationLevel;
        this.isolationLevel = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("isolationLevel", oldValue, isolationLevel);//NOI18N
    }
    public String getIsolationLevelGuaranteed() {
        return isolationLevelGuaranteed;
    }
    public void setIsolationLevelGuaranteed(String value) {
        String oldValue = isolationLevelGuaranteed;
        this.isolationLevelGuaranteed = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("isolationLevelGuaranteed", oldValue, isolationLevelGuaranteed);//NOI18N
    }
    public String getConnectionValidation() {
        return connectionValidation;
    }
    public void setConnectionValidation(String value) {
        String oldValue = connectionValidation;
        this.connectionValidation = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("connectionValidation", oldValue, connectionValidation);//NOI18N
    }
    public String getConnectionValidationTablename() {
        return connectionValidationTablename;
    }
    public void setConnectionValidationTablename(String value) {
        String oldValue = connectionValidationTablename;
        this.connectionValidationTablename = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("connectionValidationTablename", oldValue, connectionValidationTablename);//NOI18N
    }
    public String getFailAllConnections() {
        return failAllConnections;
    }
    public void setFailAllConnections(String value) {
        String oldValue = failAllConnections;
        this.failAllConnections = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("failAllConnections", oldValue, failAllConnections);//NOI18N
    }            
    public static WS70JdbcResourceBean createBean(WS70JdbcResource jdbcresource) {
        WS70JdbcResourceBean bean = new WS70JdbcResourceBean();
        //name attribute in bean is for studio display purpose. 
        //It is not part of the jdbc-resource dtd.
        bean.setDsClass(jdbcresource.getDatasourceClass());
        bean.setName(jdbcresource.getJndiName());        
        bean.setDescription(jdbcresource.getDescription());
        bean.setJndiName(jdbcresource.getJndiName());
        bean.setIsEnabled(jdbcresource.getEnabled());
        bean.setMinConnections(jdbcresource.getMinConnections());
        bean.setMaxConnections(jdbcresource.getMaxConnections());
        bean.setWaitTimeout(jdbcresource.getWaitTimeout());
        bean.setIdleTimeout(jdbcresource.getIdleTimeout());
        bean.setIsolationLevel(jdbcresource.getIsolationLevel());
        bean.setIsolationLevelGuaranteed(jdbcresource.getIsolationLevelGuaranteed());
        bean.setConnectionValidation(jdbcresource.getConnectionValidation());
        bean.setConnectionValidationTablename(jdbcresource.getConnectionValidationTableName());
        bean.setFailAllConnections(jdbcresource.getFailAllConnections());
        PropertyElement[] extraProperties = jdbcresource.getPropertyElement();
        Vector vec = new Vector();       
        for (int i = 0; i < extraProperties.length; i++) {
            NameValuePair pair = new NameValuePair();
            pair.setParamName(extraProperties[i].getName());
            pair.setParamValue(extraProperties[i].getValue());
            vec.add(pair);
        }
        
        if (vec != null && vec.size() > 0) {
            NameValuePair[] props = new NameValuePair[vec.size()];
            bean.setExtraParams((NameValuePair[])vec.toArray(props));
        } 
        
        return bean;
    }
    public WS70Resources getGraph(){
        WS70Resources res = getResourceGraph();
        WS70JdbcResource jdbcresource = res.newWS70JdbcResource();
        jdbcresource.setDatasourceClass(getDsClass());
        jdbcresource.setDescription(getDescription());
        jdbcresource.setJndiName(getJndiName());
        jdbcresource.setMinConnections(getMinConnections());
        jdbcresource.setMaxConnections(getMaxConnections());
        jdbcresource.setIdleTimeout(getIdleTimeout());
        jdbcresource.setIsolationLevel(getIsolationLevel());
        jdbcresource.setIsolationLevelGuaranteed(getIsolationLevelGuaranteed());
        jdbcresource.setIdleTimeout(getIdleTimeout());
        jdbcresource.setWaitTimeout(getWaitTimeout());        
        jdbcresource.setConnectionValidation(getConnectionValidation());        
        jdbcresource.setConnectionValidationTableName(getConnectionValidationTablename());        
        jdbcresource.setFailAllConnections(getFailAllConnections());        
        jdbcresource.setEnabled(getIsEnabled());
        
        res.addWS70JdbcResource(jdbcresource);
        return res;
    }    
    
}
