/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * WS70ExternalResourceBean.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70ExternalJndiResource;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.PropertyElement;

import java.util.Vector;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

/**
 *
 * @author Administrator
 */
public class WS70ExternalResourceBean extends WS70BaseResourceBean implements java.io.Serializable{
    
    private String externalJndiName;
    private String resType;
    private String factoryClass;
    
    /**
     * Creates a new instance of WS70ExternalResourceBean
     */
    public WS70ExternalResourceBean() {
    }
    public String getExternalJndiName() {
        return externalJndiName;
    }
    public void setExternalJndiName(String value) {
        String oldValue = externalJndiName;
        this.externalJndiName = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("externalJndiName", oldValue, externalJndiName);//NOI18N
    }
    public String getResType() {
        return resType;
    }
    public void setResType(String value) {
        String oldValue = resType;
        this.resType = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("resType", oldValue, resType);//NOI18N
    }    
    public String getFactoryClass() {
        return factoryClass;
    }
    public void setFactoryClass(String value) {
        String oldValue = factoryClass;
        this.factoryClass = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("factoryClass", oldValue, factoryClass);//NOI18N
    }    

    public static WS70ExternalResourceBean createBean(WS70ExternalJndiResource externaljndiresource) {
        WS70ExternalResourceBean bean = new WS70ExternalResourceBean();
        //name attribute in bean is for studio display purpose. 
        //It is not part of the external-resource dtd.
        bean.setName(externaljndiresource.getJndiName());
        bean.setDescription(externaljndiresource.getDescription());
        bean.setJndiName(externaljndiresource.getJndiName());
        bean.setExternalJndiName(externaljndiresource.getExternalJndiName());
        bean.setFactoryClass(externaljndiresource.getFactoryClass());
        bean.setResType(externaljndiresource.getResType());
        bean.setIsEnabled(externaljndiresource.getEnabled());
           
        PropertyElement[] extraProperties = externaljndiresource.getPropertyElement();
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
        WS70ExternalJndiResource extresource = res.newWS70ExternalJndiResource();
        extresource.setDescription(getDescription());
        extresource.setJndiName(getJndiName());
        extresource.setExternalJndiName(getExternalJndiName());
        extresource.setResType(getResType());
        extresource.setFactoryClass(getFactoryClass());
        extresource.setEnabled(getIsEnabled());
        
        // set properties
        NameValuePair[] params = getExtraParams();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                NameValuePair pair = params[i];
                PropertyElement prop = extresource.newPropertyElement();
                prop = populatePropertyElement(prop, pair);
                extresource.addPropertyElement(prop);
            }
        }  
        
        res.addWS70ExternalJndiResource(extresource);
        return res;
    }    
}
