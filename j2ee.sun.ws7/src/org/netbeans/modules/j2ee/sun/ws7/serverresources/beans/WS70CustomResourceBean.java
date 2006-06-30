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
 * WS70CustomResourceBean.java
 *
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70CustomResource;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.PropertyElement;

import java.util.Vector;
import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;


/**
 *
 * @author Administrator
 */
public class WS70CustomResourceBean extends WS70BaseResourceBean implements java.io.Serializable{    
    
    private String resType;
    private String factoryClass;
    
    
    /** Creates a new instance of WS70CustomResourceBean */
    public WS70CustomResourceBean() {
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


    public static WS70CustomResourceBean createBean(WS70CustomResource customresource) {
        WS70CustomResourceBean bean = new WS70CustomResourceBean();
        //name attribute in bean is for studio display purpose. 
        //It is not part of the custom-resource dtd.
        bean.setName(customresource.getJndiName());
        bean.setDescription(customresource.getDescription());
        bean.setJndiName(customresource.getJndiName());        
        bean.setFactoryClass(customresource.getFactoryClass());
        bean.setResType(customresource.getResType());
        bean.setIsEnabled(customresource.getEnabled());
           
        PropertyElement[] extraProperties = customresource.getPropertyElement();
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
        WS70CustomResource customresource = res.newWS70CustomResource();
        customresource.setDescription(getDescription());
        customresource.setJndiName(getJndiName());
        
        customresource.setResType(getResType());
        customresource.setFactoryClass(getFactoryClass());
        customresource.setEnabled(getIsEnabled());
        
        // set properties
        NameValuePair[] params = getExtraParams();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                NameValuePair pair = params[i];
                PropertyElement prop = customresource.newPropertyElement();
                prop = populatePropertyElement(prop, pair);
                customresource.addPropertyElement(prop);
            }
        }  
        
        res.addWS70CustomResource(customresource);
        return res;
    }    
    
}
