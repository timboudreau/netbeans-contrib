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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver.SunAppServerConstants;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.netbeans.modules.portalpack.servers.jnpc.ServerDeployHandler;
import org.openide.filesystems.FileObject;

/**
 *
 * @author root
 */
public class SunAppServerDeployHandler implements ServerDeployHandler{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    /**
     * Creates a new instance of SunAppServerDeployHandler
     */
    public SunAppServerDeployHandler(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }
    
     private void deployOnGlassFish(String warFile) throws Exception
    {
        String passwordFile = ".pcpwd.txt";
        File file = new File(passwordFile);
        if(file.exists())
            file.delete();
        FileOutputStream fout = null;
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch(IOException ex){
            throw ex;
        }
        
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("deploy");
        cmd.add("--port");
        cmd.add(psconfig.getAdminPort());
        cmd.add("-u");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(passwordFile);
        cmd.add(warFile);
                
        logger.info(cmd.toString());
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw ex;
        }
        logger.info("Password file: "+passwordFile);
        file.delete();
        
    }
     
     
     private void unDeployFromGlassFish(String appName) throws Exception
    {
        String passwordFile = ".pcpwd.txt";
        File file = new File(passwordFile);
        if(file.exists())
            file.delete();
        FileOutputStream fout = null;
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch(IOException ex){
            throw ex;
        }
             
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("undeploy");
        cmd.add("--port");
        cmd.add(psconfig.getAdminPort());
        cmd.add("-u");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(passwordFile);
        cmd.add(appName);
                
        logger.info(cmd.toString());    
       
        try {
            
            runProcess(cmd.getCmdArray(),true);
        } catch (Exception ex) {
            throw ex;
        }
        logger.info("Password file: "+passwordFile);
        file.delete();
        
    } 
      
    private int runProcess(String[] cmdArray, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmdArray);
        
        
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,"" + System.currentTimeMillis());
        if (wait)
            child.waitFor();
        
        return child.exitValue();
        
    }

    public boolean deploy(String warFile) throws Exception {
        try{
            deployOnGlassFish(warFile);
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            return false;
        }
        return true;
    }

    public boolean undeploy(String appName) throws Exception {
        try{
            unDeployFromGlassFish(appName);
        }catch(Exception e){
            logger.log(Level.SEVERE, "Error",e);
            return false;
        }
        return true;
    }

    public boolean install() throws Exception {
        return true;
    }
   
}
