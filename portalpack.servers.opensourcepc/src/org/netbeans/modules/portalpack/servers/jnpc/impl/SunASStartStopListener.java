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

package org.netbeans.modules.portalpack.servers.jnpc.impl;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.ServerStartStopListener;
import org.netbeans.modules.portalpack.servers.core.api.StartStopEvent;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.jnpc.common.JNPCConstants;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.OutputWriter;

/**
 *
 * @author root
 */
public class SunASStartStopListener implements ServerStartStopListener{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    
    private PSDeploymentManager dm;
    private PSConfigObject psconfig;
    /** Creates a new instance of SunASStartStopListener */
    public SunASStartStopListener(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }
    
    public void performAction(StartStopEvent evt) {
        
        logger.finest("Inside perform Action with evt."+evt.getEventType());
        if(evt.getEventType().equals(StartStopEvent.AFTER_START)){
            checkAndInstallPC();
        }
    }
    
    public void checkAndInstallPC(){
        
        if(!installRequiredForPC()) {
            logger.finest("No Need to Install PC again.....");
            return;
        }
        //Just save this property for now.
        psconfig.setAndSaveProperty(JNPCConstants.SETUP_DONE,"true");
        
        
        //   logger.info("Trying to install PC............");
            /* ProgressHandle handle = ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "INSTALLING_PORTLET_CONTAINER"));
        handle.start();
        try{
             
             
            String pcHome = psconfig.getPSHome();
            String serverHome = psconfig.getServerHome();
             
           // String pcBase = getPCBaseDir(psconfig);
            pcHome = changeToOSSpecificPath(pcHome);
            serverHome = changeToOSSpecificPath(serverHome);
         //   pcBase = changeToOSSpecificPath(pcBase);
            String domainDir = psconfig.getDomainDir();
            domainDir = changeToOSSpecificPath(domainDir);
             
             
             
             
            Properties props  = new Properties();
            props.setProperty("portlet_container_home",pcHome);
          //  props.setProperty("portlet_container_base",pcBase);
            props.setProperty("GLASSFISH_HOME",serverHome);
            props.setProperty("DOMAIN",psconfig.getDefaultDomain());
            props.setProperty("AS_ADMIN_USER",psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            props.setProperty("AS_ADMIN_PASSWORD",psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD));
             
            //find setup.xml
             
            File file = new File(pcHome + File.separator + "setup.xml");
            if(!file.exists()) {
                logger.log(Level.SEVERE,org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "SETUP_XML_NOT_FOUND"));
                return;
            }
             
            FileObject setUpXmlObj = FileUtil.toFileObject(file);
             
            ExecutorTask executorTask = ActionUtils.runTarget(setUpXmlObj,new String[]{"deploy_on_glassfish"},props);
            psconfig.setAndSaveProperty(JNPCConstants.SETUP_DONE,"true");
            executorTask.waitFinished();
             
            try{
                handle.finish();
                handle = ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "STARTING_APPSERVER"));
                handle.start();
            }catch(Exception e){
             
            }*/
        
        //logger.info("Starting Glassfish Server.....");
        /// dm.getStartServerHandler().startServer();
        
      /*  }catch(Exception e){
            logger.log(Level.SEVERE,org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "ERROR_INSTALLING_PC"),e);
        }finally{
            handle.finish();
        }*/
        
    }
    
    private boolean installRequiredForPC() {
        boolean installRequire = false;
        final URL url;
        try {
            psconfig.setAndSaveProperty(JNPCConstants.SETUP_DONE,"true");
            if(psconfig.getProperty(JNPCConstants.SETUP_DONE) != null &&
                    psconfig.getProperty(JNPCConstants.SETUP_DONE).trim().equals("true")) {
                url = new URL("http://" + psconfig.getHost() + ":" + psconfig.getPort() + "/"
                        + psconfig.getProperty(JNPCConstants.ADMIN_CONSOLE_URI));
                
                int responseCode = ((HttpURLConnection)url.openConnection()).getResponseCode();
                final OutputWriter inOut = UISupport.getServerIO(dm.getUri()).getOut();
                inOut.write(org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "CHECK_PORTLET_CONTAINER_INSTALLATION"));
                if(responseCode == 404 || responseCode == 503) //Not Found
                {
                    logger.info("404 - Not Found Exception for pc.....");
                    installRequire = true;
                    //inOut.write("PC Home not found ...........Assumes not installed.............");
                    inOut.write(org.openide.util.NbBundle.getMessage(SunASStartStopListener.class, "PORTLET_CONTAINER_IS_NOT_INSTALLED"));
                    NotifyDescriptor d = new NotifyDescriptor.Message(NbBundle.getMessage(SunASStartStopListener.class, "PORTLET_CONTAINER_IS_NOT_INSTALLED", new Object[]{url.toString()}), NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(d);
                    
                }else
                    inOut.write("CHECK_SUCCESSFUL");
            }else {
                logger.finest("This is the first time server is getting started.......so install required...");
                installRequire = true;
                
                //check if portal base directory exists. This directory is different for different servers.
                
              /*  String pcBaseDir = getPCBaseDir(psconfig);
               
                logger.finest("PC BASE DIR ***********************************" + pcBaseDir);
                File file = new File(pcBaseDir);
               
                if(file.exists())
                    file.delete();
               
                file.mkdirs();*/
                
            }
            
            return installRequire;
            
            
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return installRequire;
    }
    
    private int runProcess(String str, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(str);
        
        if (wait)
            child.waitFor();
        return child.exitValue();
        
    }
    
    public String changeToOSSpecificPath(String path) {
        if (org.openide.util.Utilities.isWindows()){
            path = path.replace("\\","/");
            
        }
        
        return path;
    }
    
    public String getPCBaseDir(PSConfigObject psconfig){
        File nbBase = new File(System.getProperty("netbeans.user"));
        //Strinng displayName = psconfig.getDisplayName();
        return nbBase + File.separator + "pcbase" + psconfig.getDisplayName();
    }
    
}
