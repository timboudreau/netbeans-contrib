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
package org.netbeans.modules.j2ee.sun.ws7.serverresources.loaders;

import java.io.InputStream;
import org.xml.sax.InputSource;

import org.openide.filesystems.*;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;

import org.netbeans.api.xml.cookies.CheckXMLCookie;
import org.netbeans.api.xml.cookies.ValidateXMLCookie;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;


import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.*;


import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70MailResourceBean;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70MailResourceBeanDataNode;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70ExternalResourceBean;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70ExternalResourceBeanDataNode;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70CustomResourceBean;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70CustomResourceBeanDataNode;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70JdbcResourceBean;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.WS70JdbcResourceBeanDataNode;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.openide.util.WeakListeners;

/** Represents a Sun Webserver70 Resource object in the Repository.
 * Code reused from Appserver common API module 
 *
 */
public class SunWS70ResourceDataObject extends XMLDataObject implements FileChangeListener { // extends MultiDataObject{
    


    private ValidateXMLCookie validateCookie = null;
    private CheckXMLCookie checkCookie = null;
    
    private WS70MailResourceBean mailBean = null;
    private WS70ExternalResourceBean externalJndiBean = null;
    private WS70CustomResourceBean customBean = null;
    private WS70JdbcResourceBean jdbcBean = null;

    
    private String resType;
    
    public SunWS70ResourceDataObject(FileObject pf, SunWS70ResourceDataLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        pf.addFileChangeListener((FileChangeListener) WeakListeners.create(FileChangeListener.class, this, pf));
        
        resType = getResource(pf);
    }    
    
    public org.openide.nodes.Node.Cookie getCookie(Class c) {
        Node.Cookie retValue = null;
        if (ValidateXMLCookie.class.isAssignableFrom(c)) {
            if (validateCookie == null) {
                InputSource in = DataObjectAdapters.inputSource(this);
                validateCookie = new ValidateXMLSupport(in);
            }
            return validateCookie;
        } else if (CheckXMLCookie.class.isAssignableFrom(c)) {
            if (checkCookie == null) {
                InputSource in = DataObjectAdapters.inputSource(this);
                checkCookie = new CheckXMLSupport(in);
            }
            return checkCookie;
        }
        
        if (retValue == null) {
            retValue = super.getCookie(c);
        }
        return retValue;
    }
    
    
    public HelpCtx getHelpCtx() {
        return null; // HelpCtx.DEFAULT_HELP;
        // If you add context help, change to:
        // return new HelpCtx(SunWS70ResourceDataObject.class);
    }
    
    protected Node createNodeDelegate() {
        if(resType != null){
             if(this.resType.equals(WS70WizardConstants.__MailResource)){
                    Node node = new WS70MailResourceBeanDataNode(this, getMailBean());
                    return node;
             }else if(this.resType.equals(WS70WizardConstants.__JdbcResource)){
                    Node node = new WS70JdbcResourceBeanDataNode(this, getJdbcBean());
                    return node;
             }else if(this.resType.equals(WS70WizardConstants.__CustomResource)){
                 Node node = new WS70CustomResourceBeanDataNode(this, getCustomBean());
                 return node;
             }else if(this.resType.equals(WS70WizardConstants.__ExternalJndiResource)){
                 Node node = new WS70ExternalResourceBeanDataNode(this, getExternalJndiBean());
                 return node;
             }else{
                    String mess = NbBundle.getMessage(SunWS70ResourceDataObject.class, "Info_notSunWS70Resource"); //NOI18N
                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, mess); 
                    return new SunWS70ResourceDataNode(this);
             }              
        }
        return new SunWS70ResourceDataNode(this);        
         
    }
    
    private String getResource(FileObject primaryFile) {
       String type = null;
       try {
            if((! primaryFile.isFolder()) && primaryFile.isValid()){
                InputStream in = primaryFile.getInputStream();
                WS70Resources resources = this.getResourceGraph(in);                
                // import Mail WS70Resources
                WS70MailResource[] mailResources = resources.getWS70MailResource();
                if(mailResources.length != 0){
                    WS70MailResourceBean currMailBean = WS70MailResourceBean.createBean(mailResources[0]);
                    type = WS70WizardConstants.__MailResource;
                    setMailBean(currMailBean);
                    return type;
                }
                
                // import WS70ExternalJndiResources
                WS70ExternalJndiResource[] extResources = resources.getWS70ExternalJndiResource();
                if(extResources.length != 0){
                    WS70ExternalResourceBean currentextBean = WS70ExternalResourceBean.createBean(extResources[0]);
                    type = WS70WizardConstants.__ExternalJndiResource;
                    setExternalJndiBean(currentextBean);
                    return type;
                }                
                // import WS70CustomResources
                WS70CustomResource[] customResources = resources.getWS70CustomResource();
                if(customResources.length != 0){
                    WS70CustomResourceBean currentCustomBean = WS70CustomResourceBean.createBean(customResources[0]);
                    type = WS70WizardConstants.__CustomResource;
                    setCustomBean(currentCustomBean);
                    return type;
                }
                // import WS70JdbcResources
                WS70JdbcResource[] jdbcResources = resources.getWS70JdbcResource();
                if(jdbcResources.length != 0){
                    WS70JdbcResourceBean currentJdbcBean = WS70JdbcResourceBean.createBean(jdbcResources[0]);
                    type = WS70WizardConstants.__JdbcResource;
                    setJdbcBean(currentJdbcBean);
                    return type;
                }                                                
                return type;
            }else
                return type;
        }catch(NullPointerException npe){
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, npe);
            return type;
        }catch(Exception ex){
            //ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, ex.getLocalizedMessage());
            return type;
        }
       
    }
    public WS70Resources getResourceGraph(java.io.InputStream in){
        return org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.impl.WS70Resources.createGraph(in);
    }    
    
    private void setMailBean(WS70MailResourceBean in_mailBean){
        this.mailBean = in_mailBean;
    }
    
    private WS70MailResourceBean getMailBean(){
        return this.mailBean;
    }
   private void setExternalJndiBean(WS70ExternalResourceBean in_extBean){
        this.externalJndiBean = in_extBean;
    }
    
    private WS70ExternalResourceBean getExternalJndiBean(){
        return this.externalJndiBean;
    }    
   private void setCustomBean(WS70CustomResourceBean in_customBean){
        this.customBean = in_customBean;
    }
    
    private WS70CustomResourceBean getCustomBean(){
        return this.customBean;
    }        
   private void setJdbcBean(WS70JdbcResourceBean in_jdbcBean){
        this.jdbcBean= in_jdbcBean;
    }
    
    private WS70JdbcResourceBean getJdbcBean(){
        return this.jdbcBean;
    }        
 
    
    public void fileAttributeChanged (FileAttributeEvent fe) {
        updateDataObject();
    }
    
    public void fileChanged (FileEvent fe) {
        updateDataObject();
    }
    
    public void fileDataCreated (FileEvent fe) {
        updateDataObject ();
    }
    
    public void fileDeleted (FileEvent fe) {
        updateDataObject ();
    }
    
    public void fileFolderCreated (FileEvent fe) {
        updateDataObject ();
    }
    
    public void fileRenamed (FileRenameEvent fe) {
        updateDataObject ();
    }
    
    private void updateDataObject(){
        resType = getResource(this.getPrimaryFile());       
    }
    
    public String getResourceType(){
        return resType;
    }
    // If you made an Editor Support you will want to add these methods:
     
    /*public final void addSaveCookie(SaveCookie save) {
        getCookieSet().add(save);
    }
     
    public final void removeSaveCookie(SaveCookie save) {
        getCookieSet().remove(save);
    }*/
  
}
