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

package org.netbeans.modules.portalpack.servers.sunps7.impl;

import com.sun.portal.admin.common.PSMBeanException;
import java.util.Collections;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.swing.JOptionPane;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.impl.DefaultPSTaskHandler;
import org.netbeans.modules.portalpack.servers.core.ui.CreateContainerChannelPanel;
import org.netbeans.modules.portalpack.servers.sunps7.ui.ChannelTypeChooser;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author Satya
 */
public class PSCommandHandler extends DefaultPSTaskHandler {
    
    private Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    
    private String DEFAULT_AUTHLESS_UID = "DefaultAuthlessUID";
    private String AUTHORIZED_AUTHLESS_UIDS = "AuthorizedAuthlessUserIds";
    private String OPT_COMPONENT = "component";
    private String OPT_DN = "dn";
    private String OPT_ATTR_NAMES = "attribute-names";
    
    private static final String MSG_PREFIX = "portlet.";
    private static PSCommandHandler instance = null;
    private PSConfigObject psconfig;
    private JMXHelper jmxHelper;
    
    private AMObjectSearchHandler amObjectSearchHandler;
    private String uri;
    private LogPublisher logPublisher;
    /** Creates a new instance of PSCommandHandler */
    public  PSCommandHandler(String uri) {
        
        psconfig = PSConfigObject.getPSConfigObject(uri);
        jmxHelper = new JMXHelper(psconfig);
        this.uri = uri;
        try{
            logPublisher = new LogPublisher(UISupport.getServerIO(uri).getOut());
        }catch(Exception e){
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
        }
    }
    
    private AMObjectSearchHandler getAMObjectSearchHandler() throws Exception {
        if(amObjectSearchHandler == null)
            amObjectSearchHandler = new AMObjectSearchHandler(jmxHelper,psconfig,logPublisher);
        return amObjectSearchHandler;
    }
    
    
    // public static PSCommandHandler getInstance() {
        /*if(instance == null)
            instance = new PSCommandHandler();
        return instance;*/
    
    //}
    
    public String deploy(String warfile,String serveruri) throws Exception {
        
        if(psconfig.isRemote()) {
            return _deployOnRemote(warfile,serveruri);
        } else{
            return _deployOnLocal(warfile,serveruri);
        }
    }
    
    public String _deployOnLocal(String warfile,String serveruri) throws Exception {
        MBeanServerConnection m_msc = jmxHelper.getMBeanServerConnection();
        //jmxHelper.invokeMethod(m_msc);
        
        List instanceList = new ArrayList();
        instanceList.add(psconfig.getIntanceId());
        
        //start deploying
        try{
            Object[] params = {"",new Boolean(true),warfile,new Properties(),new Properties(),
            new Boolean(true), instanceList,new Boolean(false), new Boolean(false)};
            String[] signature ={"java.lang.String", "java.lang.Boolean", "java.lang.String", "java.util.Properties",
            "java.util.Properties","java.lang.Boolean","java.util.List","java.lang.Boolean", "java.lang.Boolean"};
            
            LinkedList path = new LinkedList();
            path.addFirst(psconfig.getDefaultDomain());
            path.addFirst(psconfig.getPortalId());
            path.addFirst("PortletAdmin");
            
            ObjectName objName = JMXAdminHelperUtil.getResourceMBeanObjectName("PortalDomain.Portal.PortletAdmin", path);
            Boolean deployedAtAllInstances = (Boolean)m_msc.invoke(objName, "deployAll",params,signature);
            if (!deployedAtAllInstances.booleanValue()){
                return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Could_not_be_deployed.");
            }
            return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Deployed_Successfully");
        }catch(MBeanException me){
            //logMessage("PSPL_CSPPAM0012",tokens,me);
            // if (!isContinue){
            //PSMBeanException pme = (PSMBeanException)me.getTargetException();
            //throw new PSMBeanException(pme.getErrorKey(),pme.getTokens());
            
            PSMBeanException pme = (PSMBeanException)me.getTargetException();
            String errMsg = null;
            try{
                errMsg = NbBundle.getMessage(PSCommandHandler.class, pme.getErrorKey(),pme.getTokens());
            }catch(Exception e){
                errMsg = pme.getMessage();
            }
            writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_MBean_Exception")+errMsg);
            
            if(pme.getErrorKey().equals("portlet.errorPortletAlreadyDeployed")) {
                try {
                    if(tryToRedeploy((String) pme.getTokens()[0],warfile,uri))
                        return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Deployed_Successfully");
                }catch(Exception e){}
            }
            throw me;
            
            //}
            // deployedAtAllInstances = new Boolean(false);
            // continue;
        } catch(Exception e){
            //String[] tokens = {nameId[1],psconfig.getHost()};
            //   logMessage("PSPL_CSPPAM0012",tokens,e);
            // if (!isContinue){
            throw new PSMBeanException(MSG_PREFIX + "errorDeploy" ,e.getMessage());
            // }
            //       deployedAtAllInstances = new Boolean(false);
            //     continue;
        }finally{
            jmxHelper.closeJMXConnector();
        }
        
    }
    
    private String _deployOnRemote(String warfile,String uri) throws Exception {
        MBeanServerConnection m_msc = jmxHelper.getMBeanServerConnection();
        //jmxHelper.invokeMethod(m_msc);
        
        List instanceList = new ArrayList();
        instanceList.add(psconfig.getIntanceId());
        
        
        
        String[] nameId = null;
        try{
            writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Uploading_file_to_remote_server")+psconfig.getHost());
            //upload this file to this host and get the absolute filename of uploaded file.
            nameId = JMXAdminHelperUtil.uploadFile(m_msc, psconfig.getDefaultDomain(), new File(warfile),0);
        }catch(UploadDownloadException upde){
            String[] tokens = {nameId[1],psconfig.getHost()};
            //logMessage("PSPL_CSPPAM0011",tokens,upde);
            //if (!isContinue){
            writeErrorToOutput(uri,upde);
            writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Error_uploading_file_to")+psconfig.getHost());
            throw new PSMBeanException(org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Error_uploading_file")+warfile + " to server .");
            // }
           /* //iterate to next host
            deployedAtAllInstances = new Boolean(false);
            continue;*/
        }
        
        //start deploying
        try{
            writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Start_deploying")+nameId[1]);
            Object[] params = {"",new Boolean(true),nameId[1],new Properties(),new Properties(),
            new Boolean(true), instanceList,new Boolean(false), new Boolean(false)};
            String[] signature ={"java.lang.String", "java.lang.Boolean", "java.lang.String", "java.util.Properties",
            "java.util.Properties","java.lang.Boolean","java.util.List","java.lang.Boolean", "java.lang.Boolean"};
            
            LinkedList path = new LinkedList();
            path.addFirst(psconfig.getDefaultDomain());
            path.addFirst(psconfig.getPortalId());
            path.addFirst("PortletAdmin");
            
            ObjectName objName = JMXAdminHelperUtil.getResourceMBeanObjectName("PortalDomain.Portal.PortletAdmin", path);
            
            Boolean deployedAtAllInstances = (Boolean)m_msc.invoke(objName, "deployAll",params,signature);
            if(deployedAtAllInstances != null) {
                if (!deployedAtAllInstances.booleanValue()){
                    return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Could_not_be_deployed.");
                }
            }else{
                logger.log(Level.SEVERE,"Some serious error has occured in Server. So that deployedAtAllInstances is null which should not be");
            }
            writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Deployed_Successfully"));
            return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Deployed_Successfully");
        }catch(MBeanException me){
            /*String[] tokens = {nameId[1],psconfig.getHost()};
            //logMessage("PSPL_CSPPAM0012",tokens,me);
            // if (!isContinue){
            writeToOutput(uri,"Error in deployment "+me.getMessage());
            PSMBeanException pme = (PSMBeanException)me.getTargetException();
            throw new PSMBeanException(pme.getErrorKey(),pme.getTokens());
             */
            logger.log(Level.WARNING,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),me);
            if(me.getTargetException() instanceof PSMBeanException) {
                PSMBeanException pme = (PSMBeanException)me.getTargetException();
                String errMsg = "";
                String errKey = "";
                try{
                    errKey = pme.getErrorKey();
                    errMsg = NbBundle.getMessage(PSCommandHandler.class, errKey,pme.getTokens());
                }catch(Exception e){
                    logger.log(Level.WARNING,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
                    errMsg = pme.getMessage();
                }
                writeErrorToOutput(uri,"MBean Exception : ["+errKey +  "]  " + errMsg);
                if(pme.getErrorKey().equals("portlet.errorPortletAlreadyDeployed")) {
                    try{
                        if(tryToRedeploy((String) pme.getTokens()[0],warfile,uri))
                            return org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Deployed_Successfully");
                    }catch(Exception e){}
                }else if(pme.getErrorKey().equals("portlet.errorGettingPortalInstances")){
                    NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getMessage(PSCommandHandler.class, "portlet.errorGettingPortalInstances"), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(d);
                } else{
                    writeErrorToOutput(uri,pme);
                }
            } else {
                writeErrorToOutput(uri,me);
            }
            throw me;
            //}
            // deployedAtAllInstances = new Boolean(false);
            // continue;
        } catch(Exception e){
            String[] tokens = {nameId[1],psconfig.getHost()};
            //   logMessage("PSPL_CSPPAM0012",tokens,e);
            // if (!isContinue){
            writeErrorToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Error_in_deployment")+e.getMessage());
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
            writeErrorToOutput(uri,e);
            throw new PSMBeanException(MSG_PREFIX + "errorDeploy" ,tokens);
            // }
            //       deployedAtAllInstances = new Boolean(false);
            //     continue;
        }finally {
            //do file cleanup
            try{
                JMXAdminHelperUtil.uploadDownloadCleanUp(m_msc,psconfig.getDefaultDomain(), nameId[0]);
            }catch(UploadDownloadException upe){
                logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),upe);
            }finally{
                
                jmxHelper.closeJMXConnector();
            }
        }
        
    }
    
    
    
    public void undeploy(String portletAppName,String dn) throws Exception{
        boolean global = false;
        boolean verbose = true;
        
        List instanceList = new ArrayList();
        instanceList.add(psconfig.getIntanceId());
        try{
            logger.log(Level.FINEST,"Trying to undeploy portlet app "+portletAppName+" from "+dn);
            Object[] params =
            {dn,new Boolean(global),portletAppName,new Boolean(verbose),instanceList,new Boolean(false)};
            
            String[] signature ={"java.lang.String", "java.lang.Boolean", "java.lang.String","java.lang.Boolean","java.util.List","java.lang.Boolean"};
            //get Mbean Server Connection based on uid/pwd
            MBeanServerConnection msc =
                    jmxHelper.getMBeanServerConnection();
            
            LinkedList path = new LinkedList();
            path.addFirst(psconfig.getDefaultDomain());
            path.addFirst(psconfig.getPortalId());
            path.addFirst("PortletAdmin");
            
            ObjectName objName = JMXAdminHelperUtil.getResourceMBeanObjectName("PortalDomain.Portal.PortletAdmin", path);
            Boolean undeployedAtAllInstances = (Boolean)msc.invoke(objName, "undeployAll",params,signature);
            
        } catch (InstanceNotFoundException ie) {
            logger.log(Level.SEVERE,"Portal Instance not found.."+ie.getMessage(),ie);
            writeErrorToOutput(uri,ie);
            throw ie;
            
        } catch (MBeanException me) {
            logger.log(Level.SEVERE,"MBeanException",me);
            PSMBeanException pme = (PSMBeanException)me.getTargetException();
            String errMsg = "";
            String errKey = "";
            try{
                errKey = pme.getErrorKey();
                errMsg = NbBundle.getMessage(PSCommandHandler.class, errKey,pme.getTokens());
                
            }catch(Exception e){
                errMsg = pme.getMessage();
            }
            if(errKey.equals("portlet.errorGettingPortalInstances")){
                NotifyDescriptor d = new NotifyDescriptor.Message(
                        NbBundle.getMessage(PSCommandHandler.class, "portlet.errorGettingPortalInstances"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
            writeErrorToOutput(uri,me);
            writeToOutput(uri,"MBean Exception : ["+errKey+"] "+errMsg);
            throw me;
        } catch (MalformedObjectNameException mle) {
            logger.log(Level.SEVERE,"MalformedException : "+mle.getMessage(),mle);
            writeErrorToOutput(uri,mle);
            throw mle;
            
        } catch (Exception ex) {
            writeErrorToOutput(uri,ex);
            logPublisher.log("Exception : "+ex.getMessage());
            throw ex;
        }finally{
            jmxHelper.closeJMXConnector();
        }
    }
    
    
    public String[] getPortlets(String dn) {
        Object[] params={dn};
        String[] signature={"java.lang.String"};
        // portlets = null;
        
        Set ps = null;
        try {
            ps = (Set)invokeMethod("PortletAdmin","getExistingPortlets",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
        }
        
        if(ps !=null) {
            return (String [])ps.toArray(new String[0]);
        }
        
        return new String[]{};
    }
    
    private Object invokeMethod(String namePath, String operation,
            Object[] params,
            String[] signature)
            throws MBeanException, Exception {
        
        Object returnVal = null;
        LinkedList path = new LinkedList();
        path.addFirst(psconfig.getDefaultDomain());
        path.addFirst(psconfig.getPortalId());
        path.addFirst(namePath);
        
        //log,"CreateChannelBean.invokeMethod(), dn: " + dn);
        //log(Level.FINE,"CreateChannelBean.invokeMethod(), portalId: " + portalId);
        //log(Level.FINE,"CreateChannelBean.invokeMethod(), operation: " + operation);
        
        
        try {
            ObjectName on = JMXAdminHelperUtil.getResourceMBeanObjectName(
                    JMXAdminHelperUtil.PORTAL_MBEAN_TYPE
                    + "." + namePath, path);
            returnVal = jmxHelper.getMBeanServerConnection().invoke(on, operation, params, signature);
        } catch(MBeanException me) {
            throw me;
        } catch (Exception e) {
            throw e;
        }finally{
            jmxHelper.closeJMXConnector();
        }
        
        return returnVal;
    }
    
    
    public void createChannel(String dn,String providerName,String channelName) throws MBeanException, Exception {
        
        Object[] params= new Object[3];
        String[] signature={"java.lang.String",
        "java.lang.String", "java.lang.String"};
        
        params[0] = (String)dn;
        params[1] = channelName;
        params[2] = providerName;
        writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Creating_channel")+channelName + "  dn="+dn);
        invokeMethod("DisplayProfile","createChannel",
                    params, signature);
        writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Created_channel")+channelName + "   dn="+dn);
        
    }
    
    
    public void createContainer(String dn, String container, String provider) throws MBeanException, Exception {
        
        Object[] params= new Object[3];
        String[] signature={"java.lang.String",
        "java.lang.String", "java.lang.String"};
        
        params[0] = (String)dn;
        params[1] = container;
        params[2] = provider;
        writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Creating_container")+container + "  dn="+dn);
        invokeMethod("DisplayProfile","createContainer",params,signature);
        writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Created_container")+container + "  dn="+dn);
        
        //set title of the container
        try{
            String title = "Container";
            int index = container.lastIndexOf("/");
            if(index == -1)
                title = container;
            else{
                title = container.substring(index + 1);
            }
            Object[] param1 = new Object[5];
            String[] signature1={"java.lang.String",
            "java.lang.String", "java.lang.String", "java.lang.String", "java.util.List"};
            
            param1[0] = dn;
            param1[1] = container;
            param1[2] = "title";
            param1[3] = title;
            param1[4] = Collections.EMPTY_LIST;
            invokeMethod("DisplayProfile","setStringProperty",param1,signature1);
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error setting the tab name",e);
        }
        
    }
    
    public String[] getExistingProviders(String baseDn) throws MBeanException, Exception{
        
        Object[] params= new Object[1];
        String[] signature={"java.lang.String"};
        params[0] = baseDn;
        
        logger.log(Level.FINEST,"finding providers for dn::: "+baseDn);
        Set ps = null;
        try {
            ps = (Set)invokeMethod("DisplayProfile","getExistingProviders",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error",e);
        }
        
        if(ps !=null) {
            return (String [])ps.toArray(new String[0]);
        }
        
        return new String[]{};
        
    }
    
    
    public String[] getExistingContainerProviders(String baseDn) throws MBeanException, Exception{
        
        Object[] params= new Object[1];
        String[] signature={"java.lang.String"};
        params[0] = baseDn;
        
        logger.log(Level.FINEST,"finding providers for dn::: "+baseDn);
        Set ps = null;
        try {
            ps = (Set)invokeMethod("DisplayProfile","getExistingContainerProviders",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
        }
        
        if(ps !=null) {
            return (String [])ps.toArray(new String[0]);
        }
        
        return new String[]{};
    }
    
    private void writeToOutput(String uri,String msg) {
        msg = "Portlet Plugin : "+msg;
        UISupport.getServerIO(uri).getOut().println(msg);
    }
    private void writeErrorToOutput(String uri,String msg) {
        msg = "Portlet Plugin : "+msg;
        UISupport.getServerIO(uri).getErr().println(msg);
    }
    private void writeErrorToOutput(String uri,Exception e) {
        e.printStackTrace(UISupport.getServerIO(uri).getErr());
    }
    
    private void logToOutput(String msg) {
        msg = "Portlet Plugin : "+msg;
        //UISupport.getServerIO(uri).getOut().println(msg);
        System.out.println(msg);
    }
    
    private boolean tryToRedeploy(String appName,String warfile, String uri) {
        
        if(JOptionPane.showConfirmDialog(null,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Application_is_already_deployed_Want_To_Redeploy"),
                "Undeploy",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            if(appName != null)
            {
                if(appName.indexOf(".war") != -1)
                {
                    appName = appName.substring(0,appName.lastIndexOf(".war"));
                }
            }
            try {
                writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Trying_to_undeploy")+appName);
                undeploy(appName,NodeTypeConstants.GLOBAL);
                writeToOutput(uri,appName + org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Undeployed_successfully"));
                deploy(warfile,uri);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),ex);
                writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Unable_to_undeploy_the_app")+" "+appName);
                return false;
            }
            return true;
        }
        
        return false;
        
    }
    
    public String[] getExistingContainers(String baseDn, boolean all) throws Exception {
        
        Object[] params= new Object[2];
        String[] signature={"java.lang.String","java.lang.Boolean"};
        params[0] = baseDn;
        params[1] = new Boolean(all);
        
        logger.finest("finding containers for dn::: "+baseDn);
        Set ps = null;
        try {
            ps = (Set)invokeMethod("DisplayProfile","getExistingContainers",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
        }
        
        System.out.println(ps);
        if(ps !=null) {
            return (String [])ps.toArray(new String[0]);
        }
        
        return new String[]{};
    }
    
    public boolean deleteChannel(String baseDn, String channelName, String parentcontainer) throws Exception {
        Object[] params= new Object[3];
        String[] signature={"java.lang.String","java.lang.String","java.lang.String"};
        params[0] = baseDn;
        params[1] = channelName;
        params[2] = parentcontainer;
        
        writeToOutput(uri,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Deleting_container")+channelName+org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_from")+parentcontainer);
        
        try {
            invokeMethod("DisplayProfile","deleteChannel",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
            return false;
        }
        
        return true;
    }
    
    public void createPortletChannel(String baseDN,String channelName, String portletName) throws Exception{
        
        Object[] params=new Object[3];
        String[] signature={"java.lang.String","java.lang.String","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = channelName;
        params[2] = portletName;
        
        try {
            invokeMethod("PortletAdmin","createPortletChannel",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error",e);
            throw e;
        }
        
        writeToOutput(uri,"\""+channelName+"\"" + org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("MSG_Portlet_Channel_is_created"));
    }
    
    public void setSelectedChannels(String baseDN, List selected, String containerName) throws Exception{
        Object[] params=new Object[3];
        String[] signature={"java.lang.String","java.util.List","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = selected;
        params[2] = containerName;
        
        try {
            invokeMethod("DisplayProfile","setSelectedChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
            throw e;
        }
    }
    
    public List getSelectedChannels(String baseDN, String containerName) throws Exception {
        
        Object[] params=new Object[2];
        String[] signature={"java.lang.String","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = containerName;
        
        List list = null;
        try {
            list = (List) invokeMethod("DisplayProfile","getSelectedChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error",e);
            throw e;
        }
        
        return list;
        
    }
    
    public List getAvailableChannels(String baseDN, String containerName) throws Exception {
        Object[] params=new Object[2];
        String[] signature={"java.lang.String","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = containerName;
        
        List list = null;
        try {
            list = (List) invokeMethod("DisplayProfile","getAvailableChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error",e);
            throw e;
        }
        
        return list;
    }
    
    public void setAvailableChannels(String baseDN, List selected, String containerName) throws Exception{
        Object[] params=new Object[3];
        String[] signature={"java.lang.String","java.util.List","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = selected;
        params[2] = containerName;
        
        try {
            invokeMethod("DisplayProfile","setAvailableChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Error",e);
            throw e;
        }
    }
    
    public Set getExistingChannels(String baseDN, Boolean all) throws Exception {
        
        Object[] params=new Object[2];
        String[] signature={"java.lang.String","java.lang.Boolean"};
        
        params[0] = baseDN;
        params[1] = Boolean.TRUE;
        
        Set set = null;
        try {
            set = (Set) invokeMethod("DisplayProfile","getExistingChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
            throw e;
        }
        
        return set;
    }
    
    public Set getAssignableChannels(String baseDN, String container) throws Exception {
        Object[] params=new Object[2];
        String[] signature={"java.lang.String","java.lang.String"};
        
        params[0] = baseDN;
        params[1] = container;
        
        Set set = null;
        try {
            set = (Set) invokeMethod("DisplayProfile","getAssignableChannels",
                    params, signature);
        } catch (Exception e) {
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error"),e);
            throw e;
        }
        
        return set;
    }
    
    public LogPublisher getLogger() {
        return logPublisher;
    }
    
    public String getAuthlessUser(String baseDN) throws Exception {
        
        Map uidOptionsMap = new HashMap();
        List uidObjectList = new ArrayList();
        uidOptionsMap.put(OPT_COMPONENT, "desktop");
        uidOptionsMap.put(OPT_DN,baseDN);
        uidOptionsMap.put("org","DeveloperSample");
        uidOptionsMap.put("operation", "get");
        Set authlessAttrs = new HashSet();
        authlessAttrs.add(AUTHORIZED_AUTHLESS_UIDS);
        //authlessAttrs.add(DEFAULT_AUTHLESS_UID);
        uidOptionsMap.put(OPT_ATTR_NAMES, authlessAttrs);
        logger.log(Level.INFO,"Base Dn is : "+baseDN);
        // Setting the params and signature
        Object[] params = {uidOptionsMap};
        String[] signature = {"java.util.Map"};
        try {
            ObjectName objName = JMXAdminHelperUtil.getPortalMBeanObjectName(psconfig.getDefaultDomain(),psconfig.getPortalId());
            Map authlessAttrValues =
                    (Map)jmxHelper.getMBeanServerConnection().invoke(objName, "getAttributes", params, signature);
            
            logger.log(Level.INFO,authlessAttrValues.toString());
            if(authlessAttrValues == null)
                return null;
            
            List authlessUsersWithPasswd =(List)authlessAttrValues.get(AUTHORIZED_AUTHLESS_UIDS);
            
            if(authlessUsersWithPasswd == null || authlessUsersWithPasswd.size() == 0)
                return null;
            
            //TODO the following code needs to be changed for sub-org authless ids
            for(int i=0;i<authlessUsersWithPasswd.size();i++) {
                String authlessId = getAuthlessUserDnFromEncodedDn((String)authlessUsersWithPasswd.get(i),baseDN);
                if(authlessId == null)
                    continue;
                return authlessId;
            }
            
            return null;
            
            
        }catch(Exception e){
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getBundle(PSCommandHandler.class).getString("Error_")  + e.getMessage(), e);
        }
        
        return null;
    }
    
    private String getAuthlessUserDnFromEncodedDn(String encodeDnWithPasswd,String baseDn) {
        if(encodeDnWithPasswd == null)
            return null;
        
        String[] splits = encodeDnWithPasswd.split("\\|");
        //System.out.println(splits[0]);
        if(splits.length == 0)
            return null;
        encodeDnWithPasswd = splits[0];
        
        if(encodeDnWithPasswd.indexOf(baseDn) != -1)
            return encodeDnWithPasswd;
        return null;
    }
    
    public Map getObjects(String type, String searchFilter, String baseDN) throws Exception {
        return getAMObjectSearchHandler().getAMObjects(type,searchFilter,baseDN);
    }
    
    public String constructPortletViewURL(String dn,String name) {
        
        try {
            createPortletChannel(dn,NetbeanConstants.CHANNEL_PREFIX + name,name);
        } catch (MBeanException ex) {
            logger.log(Level.SEVERE,"Error creating channel : " + NetbeanConstants.CHANNEL_PREFIX + name,ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Error creating channel : " + NetbeanConstants.CHANNEL_PREFIX + name,ex);
        }
        String  contextUri = psconfig.getPortalUri();
        if(contextUri.startsWith("/")) {
            if(contextUri.length() > 1)
                contextUri = contextUri.substring(1);
        }
        
        String authlessUser = null;
        try{
            authlessUser = getAuthlessUser(dn);
        }catch(Exception e){
            logger.log(Level.SEVERE,"Error getting authless users for : "+dn,e);
        }
        
        String portalUrl = null;
        
        if(authlessUser == null)
            portalUrl = "http://" + psconfig.getHost() + ":" + psconfig.getPort() +"/"+contextUri+"/dt?action=content&provider="+ NetbeanConstants.CHANNEL_PREFIX +name+"&last=false";
        else
            portalUrl = "http://" + psconfig.getHost() + ":" + psconfig.getPort() +"/"+contextUri+"/dt?desktop.suid="+authlessUser+"&action=content&provider="+ NetbeanConstants.CHANNEL_PREFIX +name+"&last=false";
        
        return portalUrl;
    }
    
    public String constructAdminToolURL(){
        
        return "http://" + psconfig.getHost()+":" + psconfig.getPort() + "/psconsole";
    }

    public String getClientURL(){
        String  contextUri = psconfig.getPortalUri();
        if(contextUri.startsWith("/")) {
            if(contextUri.length() > 1)
                contextUri = contextUri.substring(1);
        }
        return "http://" + psconfig.getHost() + ":" + psconfig.getPort() +"/"+contextUri+"/dt";
    }
    
    public void addChannel(String dn) throws Exception{
        
        ChannelTypeChooser cTypeChooser = new ChannelTypeChooser(WindowManager.getDefault().getMainWindow(),true);
        String channelType = cTypeChooser.getChannelType();
        
        if(channelType == null)
            return;
       
        if(channelType.equals(ChannelTypeChooser.PROVIDER_CHANNEL_TYPE)) {
            String[] providers;
            try {
                providers = getExistingProviders(dn);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                return;
            }
            if(providers == null)
                providers = new String[0];
            
            CreateContainerChannelPanel dialog = new CreateContainerChannelPanel(WindowManager.getDefault().getMainWindow(),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Channel"),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Provider"),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Create_a_channel"),
                                                        providers);
            
            dialog.setVisible(true);
            
            String channelName = dialog.getName();
            String providerName = dialog.getType();
            if(channelName == null || channelName.trim().length() == 0)
                return;
            createChannel(dn,providerName,channelName);
            
        } else if(channelType.equals(ChannelTypeChooser.PORTLET_CHANNEL_TYPE)) {
            
            String[] portlets;
            try {
                portlets = getPortlets(dn);
            } catch (Exception ex) {
                logger.log(Level.SEVERE,"Error",ex);
                return;
            }
            if(portlets == null)
                portlets = new String[0];
            
            CreateContainerChannelPanel dialog = new CreateContainerChannelPanel(WindowManager.getDefault().getMainWindow(),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Channel"),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Provider"),
                                                        org.openide.util.NbBundle.getMessage(PSCommandHandler.class, "LBL_Create_a_channel"),
                                                        portlets);
            
            dialog.setVisible(true);
            
            String channelName = dialog.getName();
            String portletName = dialog.getType();
            if(channelName == null || channelName.trim().length() == 0)
                return;
            createPortletChannel(dn,channelName,portletName);
            
        }
        
        logger.fine("Channel Added Successfully...");
    }
}
