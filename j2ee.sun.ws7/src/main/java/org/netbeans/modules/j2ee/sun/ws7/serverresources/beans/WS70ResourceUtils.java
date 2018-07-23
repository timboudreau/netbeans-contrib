/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

/*
 * WS70ResourceUtils.java
 */

package org.netbeans.modules.j2ee.sun.ws7.serverresources.beans;



import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;

import java.util.Map;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.management.AttributeList;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.sun.api.SunURIManager;

import org.openide.util.NbBundle;
import org.openide.ErrorManager;

import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.nodes.Node.Property;
import org.openide.cookies.SaveCookie;

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileAlreadyLockedException;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.netbeans.modules.j2ee.sun.ide.editors.NameValuePair;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.WS70WizardConstants;
import org.netbeans.modules.j2ee.sun.ide.editors.IsolationLevelEditor;

import org.netbeans.modules.j2ee.sun.ws7.serverresources.wizards.ResourceConfigData;
import org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.*;
import org.netbeans.modules.j2ee.sun.ws7.dm.WS70SunDeploymentManager;
import org.netbeans.modules.j2ee.sun.ws7.j2ee.ResourceType;
import org.netbeans.modules.j2ee.sun.ws7.ui.Util;

import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;



/*
 * Code reused from Appserver common API module 
 * 
 */
public class WS70ResourceUtils implements WS70WizardConstants{
    
    static final ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.modules.j2ee.sun.ws7.serverresources.beans.Bundle");// NOI18N
    private static String MAIL_PROP_PREFIX="mail."; //NOI18N // To FIX issue# 89106.
    
    /**
     * Creates a new instance of WS70ResourceUtils
     */
    public WS70ResourceUtils() {
    }
    
    public static void saveNodeToXml(FileObject resFile, WS70Resources res){
        try {             
            res.write(FileUtil.toFile(resFile));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex); 
        }
    } 
    public static void registerResource(WS70Resources resources, String resourceType, String config, 
                                           WS70SunDeploymentManager dm) throws Exception{    
        boolean added = false;
        String jndiName = null;
        if(resourceType==WS70WizardConstants.__MailResource){
            WS70MailResource mailres = resources.getWS70MailResource(0);
            HashMap attrMap = new HashMap();
            jndiName = mailres.getJndiName();
// START- FIX issue# 89106. attributes in mail-resource are now replaced with name/value pair as property element.       
            ArrayList mailresProps = new ArrayList();
            mailresProps.add(MAIL_PROP_PREFIX+WS70WizardConstants.__Host+"="+mailres.getHost());
            mailresProps.add(MAIL_PROP_PREFIX+WS70WizardConstants.__MailUser+"="+mailres.getUser());
            mailresProps.add(MAIL_PROP_PREFIX+WS70WizardConstants.__From+"="+mailres.getFrom());
            mailresProps.add(WS70WizardConstants.__StoreProtocol+"="+ mailres.getStoreProtocol());
            mailresProps.add(WS70WizardConstants.__StoreProtocolClass+"="+ mailres.getStoreProtocolClass());
            mailresProps.add(WS70WizardConstants.__TransportProtocol+"="+ mailres.getTransportProtocol());
            mailresProps.add(WS70WizardConstants.__TransportProtocolClass+"="+ mailres.getTransportProtocolClass());

/*
            attrMap.put(WS70WizardConstants.__Host, mailres.getHost());
            attrMap.put(WS70WizardConstants.__User, mailres.getUser());
            attrMap.put(WS70WizardConstants.__From, mailres.getFrom());
            attrMap.put(WS70WizardConstants.__StoreProtocol, mailres.getStoreProtocol());
            attrMap.put(WS70WizardConstants.__StoreProtocolClass, mailres.getStoreProtocolClass());
            attrMap.put(WS70WizardConstants.__TransportProtocol, mailres.getTransportProtocol());
            attrMap.put(WS70WizardConstants.__TransportProtocolClass, mailres.getTransportProtocolClass());
 **/
// END- FIX issue# 89106.
            attrMap.put(WS70WizardConstants.__Enabled, mailres.getEnabled());
            attrMap.put(WS70WizardConstants.__Description, mailres.getDescription());

            if(!isAlreadyRegisterdResource(jndiName, config, ResourceType.MAIL, dm)){
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Registering_Resource", jndiName));
                dm.addMailResource(config, jndiName, attrMap);
                added = true;
            }else{
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Updating_Resource", jndiName)); 
                dm.setResource(ResourceType.MAIL, config, jndiName, attrMap, false);
            }
// START- FIX issue# 89106.            
            dm.setUserResourceProp(config, WS70WizardConstants.__MailResource, jndiName, "property", mailresProps, false);
// END- FIX issue# 89106.            
            ErrorManager.getDefault().log(ErrorManager.USER, 
                NbBundle.getMessage(WS70ResourceUtils.class, "MSG_DeployConfig"));
             
            dm.deployAndReconfig(config);
        }else if(resourceType==WS70WizardConstants.__CustomResource){
            WS70CustomResource customres = resources.getWS70CustomResource(0);
            HashMap attrMap = new HashMap();
            jndiName = customres.getJndiName();
            attrMap.put(WS70WizardConstants.__ResType, customres.getResType());
            attrMap.put(WS70WizardConstants.__FactoryClass, customres.getFactoryClass());
            attrMap.put(WS70WizardConstants.__Enabled, customres.getEnabled());
            attrMap.put(WS70WizardConstants.__Description, customres.getDescription());
            List props = getExtraProperties(customres.getPropertyElement());
            if(!isAlreadyRegisterdResource(jndiName, config, ResourceType.CUSTOM, dm)){
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Registering_Resource", jndiName));
                 
                dm.addCustomResource(config, jndiName, attrMap);
                added = true;
            }else{
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Updating_Resource", jndiName));
 
                dm.setResource(ResourceType.CUSTOM, config, jndiName, attrMap, false);
            }
            dm.setUserResourceProp(config, WS70WizardConstants.__CustomResource, jndiName, "property", props, false);
            ErrorManager.getDefault().log(ErrorManager.USER, 
                NbBundle.getMessage(WS70ResourceUtils.class, "MSG_DeployConfig"));
             
            dm.deployAndReconfig(config);
        }else if(resourceType==WS70WizardConstants.__ExternalJndiResource){
            WS70ExternalJndiResource extres = resources.getWS70ExternalJndiResource(0);
            HashMap attrMap = new HashMap();
            jndiName = extres.getJndiName();           
            attrMap.put(WS70WizardConstants.__ExternalJndiName, extres.getExternalJndiName());
            attrMap.put(WS70WizardConstants.__ResType, extres.getResType());            
            attrMap.put(WS70WizardConstants.__FactoryClass, extres.getFactoryClass());
            attrMap.put(WS70WizardConstants.__Enabled, extres.getEnabled());
            attrMap.put(WS70WizardConstants.__Description, extres.getDescription());
            List props = getExtraProperties(extres.getPropertyElement());
            if(!isAlreadyRegisterdResource(jndiName, config, ResourceType.JNDI, dm)){
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Registering_Resource", jndiName));
                 
                dm.addJNDIResource(config, jndiName, attrMap);
                added = true;
            }else{
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Updating_Resource", jndiName));
 
                dm.setResource(ResourceType.JNDI, config, jndiName, attrMap, false);
            }
            dm.setUserResourceProp(config, WS70WizardConstants.__ExternalJndiResource, jndiName, "property", props, false);
            ErrorManager.getDefault().log(ErrorManager.USER, 
                NbBundle.getMessage(WS70ResourceUtils.class, "MSG_DeployConfig"));
             
            dm.deployAndReconfig(config);
        }else if(resourceType==WS70WizardConstants.__JdbcResource){
            WS70JdbcResource jdbcres = resources.getWS70JdbcResource(0);
            HashMap attrMap = new HashMap();
            jndiName = jdbcres.getJndiName();
            attrMap.put(__DatasourceClassname, jdbcres.getDatasourceClass());                
            attrMap.put(__MinConnections, jdbcres.getMinConnections());                
            attrMap.put(__MaxConnections, jdbcres.getMaxConnections());                
            attrMap.put(__IdleTimeout, jdbcres.getIdleTimeout());                
            attrMap.put(__WaitTimeout, jdbcres.getWaitTimeout());                
            attrMap.put(__IsolationLevel, jdbcres.getIsolationLevel());                
            attrMap.put(__IsolationLevelGuaranteed, jdbcres.getIsolationLevelGuaranteed());                
            attrMap.put(__ConnectionValidation, jdbcres.getConnectionValidation());                
            attrMap.put(__ConnectionValidationTableName, jdbcres.getConnectionValidationTableName());                
            attrMap.put(__FailAllConnections, jdbcres.getFailAllConnections());
            attrMap.put(WS70WizardConstants.__Enabled, jdbcres.getEnabled());
            attrMap.put(WS70WizardConstants.__Description, jdbcres.getDescription());            
            List props = getExtraProperties(jdbcres.getPropertyElement());
            if(!isAlreadyRegisterdResource(jndiName, config, ResourceType.JDBC, dm)){
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Registering_Resource", jndiName));
                 
                dm.addJdbcResource(config, jndiName, attrMap);
                added = true;
            }else{
                ErrorManager.getDefault().log(ErrorManager.USER, 
                    NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Updating_Resource", jndiName));
 
                dm.setResource(ResourceType.JDBC, config, jndiName, attrMap, false);
            }
            dm.setUserResourceProp(config, WS70WizardConstants.__JdbcResource, jndiName, "property", props, false);
            ErrorManager.getDefault().log(ErrorManager.USER, 
                NbBundle.getMessage(WS70ResourceUtils.class, "MSG_DeployConfig"));
             
            dm.deployAndReconfig(config);                
        }else{
            Util.showError(NbBundle.getMessage(WS70ResourceUtils.class, "ERR_UNKNOWN_RESOURCE"),
                NbBundle.getMessage(WS70ResourceUtils.class, "ERR_UNKNOWN_RESOURCE")
            );
            return;
        }
        String msg = null;
        if(added){
            msg = NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Resource_Added", jndiName);
        }else{
            msg = NbBundle.getMessage(WS70ResourceUtils.class, "MSG_Resource_Updated", jndiName);
        }
        Util.showInformation(msg);
    }
    private static boolean isAlreadyRegisterdResource(String jndiName, String configName, 
                            ResourceType resType, WS70SunDeploymentManager manager) throws Exception{
        List resources = manager.getResources(resType, configName);

        Object[] res = resources.toArray();                       
        for (int i = 0; i < res.length; i ++){
            String name = (String)((HashMap)res[i]).get("jndi-name");
            if(name!=null && name.equals(jndiName)){
                return true;
            }
         }        
        return false;
    }
    private static List getExtraProperties(PropertyElement[] props) throws Exception {        
        ArrayList list = new ArrayList();
        for(int i=0; i<props.length; i++){
            String name = props[i].getName();
            String value = props[i].getValue();
            if(value != null && value.trim().length() != 0){
                list.add(name+"="+value);
                
            }
        }
        return list;
    }    
    
    public static void saveJDBCResourceDatatoXml(ResourceConfigData dsData) {
        try{
            WS70Resources res = getResourceGraph();
            WS70JdbcResource datasource = res.newWS70JdbcResource();
           
            String[] keys = dsData.getFieldNames();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (key.equals(__Properties)){
                    Vector props = (Vector)dsData.getProperties();
                    for (int j = 0; j < props.size(); j++) {
                        NameValuePair pair = (NameValuePair)props.elementAt(j);
                        PropertyElement prop = datasource.newPropertyElement();
                        prop = populatePropertyElement(prop, pair);
                        datasource.addPropertyElement(prop);
                    }
                }else{
                    String value = dsData.getString(key);
                    if (key.equals(__JndiName))
                        datasource.setJndiName(value);
                    else if (key.equals(__DatasourceClassname))
                        datasource.setDatasourceClass(value);
                    else if (key.equals(__MinConnections))
                        datasource.setMinConnections(value);
                    else if (key.equals(__MaxConnections))
                        datasource.setMaxConnections(value);
                    else if (key.equals(__IdleTimeout))
                        datasource.setIdleTimeout(value);
                    else if (key.equals(__WaitTimeout))
                        datasource.setWaitTimeout(value);    
                    else if (key.equals(__IsolationLevel))
                        datasource.setIsolationLevel(value);
                    else if (key.equals(__IsolationLevelGuaranteed))
                        datasource.setIsolationLevelGuaranteed(value);
                    else if (key.equals(__ConnectionValidation))
                        datasource.setConnectionValidation(value);
                    else if (key.equals(__ConnectionValidationTableName))
                        datasource.setConnectionValidationTableName(value);
                    else if (key.equals(__FailAllConnections))
                        datasource.setFailAllConnections(value);                    
                    else if (key.equals(__Enabled))
                        datasource.setEnabled(value);
                    else if (key.equals(__Description))
                        datasource.setDescription(value); 
                }
                
            } //for
            res.addWS70JdbcResource(datasource);
            //if(cpData != null){
                //MYWORKRESsaveConnPoolDatatoXml(cpData);
            //}
            createFile(dsData.getTargetFileObject(), dsData.getTargetFile(), res);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Unable to saveJDBCResourceDatatoXml ");
        }
    }
    
    
    public static void saveMailResourceDatatoXml(ResourceConfigData data) {
        try{
            Vector vec = data.getProperties();
            WS70Resources res = getResourceGraph();
            WS70MailResource mlresource = res.newWS70MailResource();
                        
            String[] keys = data.getFieldNames();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (key.equals(__Properties)) {
                    Vector props = (Vector)data.getProperties();
                    for (int j = 0; j < props.size(); j++) {
                        NameValuePair pair = (NameValuePair)props.elementAt(j);
                    }
                }else{
                    String value = data.getString(key);
                    if (key.equals(__JndiName))
                        mlresource.setJndiName(value);
                    else if (key.equals(__StoreProtocol))
                        mlresource.setStoreProtocol(value);
                    else if (key.equals(__StoreProtocolClass))
                        mlresource.setStoreProtocolClass(value);
                    else if (key.equals(__TransportProtocol))
                        mlresource.setTransportProtocol(value);
                    else if (key.equals(__TransportProtocolClass))
                        mlresource.setTransportProtocolClass(value);
                    else if (key.equals(__Host))
                        mlresource.setHost(value);
                    else if (key.equals(__MailUser))
                        mlresource.setUser(value);
                    else if (key.equals(__From))
                        mlresource.setFrom(value);
                    else if (key.equals(__Enabled))
                        mlresource.setEnabled(value);                    
                    else if (key.equals(__Description))
                        mlresource.setDescription(value); 
                }    
            } //for
            
            res.addWS70MailResource(mlresource);
            createFile(data.getTargetFileObject(), data.getTargetFile(), res);
        }catch(Exception ex){
            System.out.println("Unable to saveMailResourceDatatoXml ");
        }
    }
    public static void saveCustomResourceDatatoXml(ResourceConfigData data) {
        try{
            Vector vec = data.getProperties();
            WS70Resources res = getResourceGraph();
            WS70CustomResource customresource = res.newWS70CustomResource();
                        
            String[] keys = data.getFieldNames();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (key.equals(__Properties)) {
                    Vector props = (Vector)data.getProperties();
                    for (int j = 0; j < props.size(); j++) {
                        NameValuePair pair = (NameValuePair)props.elementAt(j);
                        PropertyElement prop = customresource.newPropertyElement();
                        prop = populatePropertyElement(prop, pair);
                        customresource.addPropertyElement(prop);
                    }
                }else{
                    String value = data.getString(key);
                    if (key.equals(__JndiName))
                        customresource.setJndiName(value);
                    else if (key.equals(__ResType))
                        customresource.setResType(value);
                    else if (key.equals(__FactoryClass))
                        customresource.setFactoryClass(value);
                    else if (key.equals(__Enabled))
                        customresource.setEnabled(value);                    
                    else if (key.equals(__Description))
                        customresource.setDescription(value); 
                }    
            } //for
            
            res.addWS70CustomResource(customresource);
            createFile(data.getTargetFileObject(), data.getTargetFile(), res);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Unable to saveCustomResourceDatatoXml ");
        }
    }    
    public static void saveExternalJndiResourceDatatoXml(ResourceConfigData data) {
        try{
            Vector vec = data.getProperties();
            WS70Resources res = getResourceGraph();
            WS70ExternalJndiResource ejndiresource = res.newWS70ExternalJndiResource();
                        
            String[] keys = data.getFieldNames();
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                if (key.equals(__Properties)) {
                    Vector props = (Vector)data.getProperties();
                    for (int j = 0; j < props.size(); j++) {
                        NameValuePair pair = (NameValuePair)props.elementAt(j);
                        PropertyElement prop = ejndiresource.newPropertyElement();
                        prop = populatePropertyElement(prop, pair);
                        ejndiresource.addPropertyElement(prop);
                    }
                }else{                    
                    String value = data.getString(key);
                    if (key.equals(__JndiName))
                        ejndiresource.setJndiName(value);
                    else if (key.equals(__ExternalJndiName))
                        ejndiresource.setExternalJndiName(value);
                    else if (key.equals(__ResType))
                        ejndiresource.setResType(value);
                    else if (key.equals(__FactoryClass))
                        ejndiresource.setFactoryClass(value);
                    else if (key.equals(__Enabled))
                        ejndiresource.setEnabled(value);                    
                    else if (key.equals(__Description))
                        ejndiresource.setDescription(value); 
                }    
            } //for
            
            res.addWS70ExternalJndiResource(ejndiresource);
            createFile(data.getTargetFileObject(), data.getTargetFile(), res);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Unable to saveExternalJNDIResourceDatatoXml ");
        }
    }    
    
    public static void createFile(FileObject targetFolder, String filename, final WS70Resources res){
        try{
            //jdbc and jdo jndi names might be of format jdbc/ and jdo/
            if(filename.indexOf("/") != -1){ //NOI18N
                filename = filename.substring(0, filename.indexOf("/")) + "_" + filename.substring(filename.indexOf("/")+1, filename.length()); //NOI18N
            }
            if(filename.indexOf("\\") != -1){ //NOI18N
                filename = filename.substring(0, filename.indexOf("\\")) + "_" + filename.substring(filename.indexOf("\\")+1, filename.length()); //NOI18N
            }
            String oldName = filename;
            targetFolder = setUpExists(targetFolder);
            filename =  createUniqueFileName(filename, targetFolder, null);        
	    if(!filename.equals(oldName)){
                String msg = java.text.MessageFormat.format(NbBundle.getMessage(WS70ResourceUtils.class, "LBL_UniqueResourceName"), new Object[]{oldName, filename}); //NOI18N
                org.openide.awt.StatusDisplayer.getDefault().setStatusText(msg);
            }
            
            final String resFileName = filename;
            final FileObject resTargetFolder  = targetFolder;
            
            FileSystem fs = targetFolder.getFileSystem();
            fs.runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws java.io.IOException {
                    FileObject newfile = resTargetFolder.createData(resFileName, "sun-ws7-resource"); //NOI18N
                    
                    FileLock lock = newfile.lock();
                    try {
                        PrintWriter to = new PrintWriter(newfile.getOutputStream(lock));
                        try {
                            res.write(to);
                            to.flush();
                        } catch(Exception ex){
                            //Unable to create file
                        } finally {
                            to.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
            //Unable to create file
            System.out.println("Error while creating file");
        }
    }
    
    public static String createUniqueFileName(String in_targetName, FileObject fo, String defName){
        String targetName = in_targetName;
        
        if (targetName == null || targetName.length() == 0) {
            targetName = FileUtil.findFreeFileName(fo, defName, __SunResourceExt);
        }else{
            //Fix for bug# 5025573 - Check for invalid file names
            if(! isFriendlyFilename(targetName)){
                if(defName != null)
                    targetName = defName;
                else
                    targetName = makeLegalFilename(targetName);
            }
            targetName = FileUtil.findFreeFileName(fo, targetName, __SunResourceExt);
        }
        return targetName;
    }
    
    public static FileObject setUpExists(FileObject targetFolder){
        FileObject pkgLocation = getResourceDirectory(targetFolder);
        if(pkgLocation == null){
            //resource will be created under existing structure
            return targetFolder;
        }else{
            return pkgLocation;
        }
    }
    
    private static WS70Resources getResourceGraph(){
        return  org.netbeans.modules.j2ee.sun.ws7.serverresources.dd.impl.WS70Resources.createGraph();        
    }
    
    private static PropertyElement populatePropertyElement(PropertyElement prop, NameValuePair pair){
        prop.setName(pair.getParamName()); 
        prop.setValue(pair.getParamValue()); 
        return prop;
    }
    
    //Obtained from com.iplanet.ias.util.io.FileUtils - Byron's
    public static boolean isLegalFilename(String filename) {
        for(int i = 0; i < ILLEGAL_FILENAME_CHARS.length; i++)
            if(filename.indexOf(ILLEGAL_FILENAME_CHARS[i]) >= 0)
                return false;
        
        return true;
    }
    
    public static boolean isFriendlyFilename(String filename) {
        if(filename.indexOf(BLANK) >= 0 || filename.indexOf(DOT) >= 0)
            return false;
        
        return isLegalFilename(filename);
    }
    
    public static String makeLegalFilename(String filename) {
        for(int i = 0; i < ILLEGAL_FILENAME_CHARS.length; i++)
            filename = filename.replace(ILLEGAL_FILENAME_CHARS[i], REPLACEMENT_CHAR);
        
        return filename;
    }
    
    public static boolean isLegalResourceName(String filename) {
        for(int i = 0; i < ILLEGAL_RESOURCE_NAME_CHARS.length; i++)
            if(filename.indexOf(ILLEGAL_RESOURCE_NAME_CHARS[i]) >= 0)
                return false;
        
        return true;
    }
    
    public static FileObject getResourceDirectory(FileObject fo){
        Project holdingProj = FileOwnerQuery.getOwner(fo);
        FileObject resourceDir = fo;
        if (holdingProj != null){
            J2eeModuleProvider provider = (J2eeModuleProvider) holdingProj.getLookup().lookup(J2eeModuleProvider.class);
            File resourceLoc = provider.getJ2eeModule().getResourceDirectory();
            if(resourceLoc != null){
                if(resourceLoc.exists()){
                    resourceDir = FileUtil.toFileObject(resourceLoc);
                }else{
                    resourceLoc.mkdirs();
                    resourceDir = FileUtil.toFileObject(resourceLoc);
                }
            }
        }
        return resourceDir;
    }
    
    private final static char BLANK = ' ';
    private final static char DOT   = '.';
    private final static char REPLACEMENT_CHAR = '_';
    private final static char[]	ILLEGAL_FILENAME_CHARS	= {'/', '\\', ':', '*', '?', '"', '<', '>', '|', ',' };
    private final static char[]	ILLEGAL_RESOURCE_NAME_CHARS	= {':', '*', '?', '"', '<', '>', '|', ',' };
}
