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
 * WS70MailResourceBean.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70MailResource;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.WS70Resources;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.PropertyElement;
/**
 *
 * @author Administrator
 */
public class WS70MailResourceBean extends WS70BaseResourceBean implements java.io.Serializable{

    private String storeProt;
    private String storeProtClass;
    private String transProt;
    private String transProtClass;
    private String hostName;
    private String userName;
    private String fromAddr;
    
    /** Creates a new instance of WS70MailResourceBean */
    public WS70MailResourceBean() {
    }
    public String getStoreProt() {
        return storeProt;
    }
    public void setStoreProt(String value) {
        String oldValue = storeProt;
        this.storeProt = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("storeProt", oldValue, storeProt);//NOI18N
    }
    
    public String getStoreProtClass() {
        return storeProtClass;
    }
    public void setStoreProtClass(String value) {
        String oldValue = storeProtClass;
        this.storeProtClass = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("storeProtClass", oldValue, storeProtClass);//NOI18N
    }
    
    public String getTransProt() {
        return transProt;
    }
    public void setTransProt(String value) {
        String oldValue = transProt;
        this.transProt = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("transProt", oldValue, transProt);//NOI18N
    }
    
    public String getTransProtClass() {
        return transProtClass;
    }
    public void setTransProtClass(String value) {
        String oldValue = transProtClass;
        this.transProtClass = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("transProtClass", oldValue, transProtClass);//NOI18N
    }
    
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String value) {
        String oldValue = hostName;
        this.hostName = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("hostName", oldValue, hostName);//NOI18N
    }
    
    public String getUserName() {
        return userName;
    }
    public void setUserName(String value) {
        String oldValue = userName;
        this.userName = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("userName", oldValue, userName);//NOI18N
    }
    public String getFromAddr() {
        return fromAddr;
    }
    public void setFromAddr(String value) {
        String oldValue = fromAddr;
        this.fromAddr = value;
        initPropertyChangeSupport();
        propertySupport.firePropertyChange ("fromAddr", oldValue, fromAddr);//NOI18N
    }
    
    public static WS70MailResourceBean createBean(WS70MailResource mailresource) {
        WS70MailResourceBean bean = new WS70MailResourceBean();
        //name attribute in bean is for studio display purpose. 
        //It is not part of the mail-resource dtd.
        bean.setName(mailresource.getJndiName());
        bean.setDescription(mailresource.getDescription());
        bean.setJndiName(mailresource.getJndiName());
        bean.setStoreProt(mailresource.getStoreProtocol());
        bean.setStoreProtClass(mailresource.getStoreProtocolClass());
        bean.setTransProt(mailresource.getTransportProtocol());
        bean.setTransProtClass(mailresource.getTransportProtocolClass());
        bean.setHostName(mailresource.getHost());
        bean.setUserName(mailresource.getUser());
        bean.setFromAddr(mailresource.getFrom());
        
        bean.setIsEnabled(mailresource.getEnabled());           
        return bean;
    }
    
    public WS70Resources getGraph(){
        WS70Resources res = getResourceGraph();
        WS70MailResource mlresource = res.newWS70MailResource();
        mlresource.setDescription(getDescription());
        mlresource.setJndiName(getJndiName());
        mlresource.setStoreProtocol(getStoreProt());
        mlresource.setStoreProtocolClass(getStoreProtClass());
        mlresource.setTransportProtocol(getTransProt());
        mlresource.setTransportProtocolClass(getTransProtClass());
        mlresource.setHost(getHostName());
        mlresource.setUser(getUserName());
        mlresource.setFrom(getFromAddr());        
        mlresource.setEnabled(getIsEnabled());
        
        res.addWS70MailResource(mlresource);
        return res;
    }
}
