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

package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.sunappserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.PSLogViewer;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.ErrorManager;
import org.xml.sax.SAXException;

/**
 *
 * @author satya
 */
public class SunAppServerStartServer extends PSStartServerInf{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSDeploymentManager dm;
    private PSConfigObject psconfig;
    /** Creates a new instance of SunAppServerStartServer */
    public SunAppServerStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    public void doStartServer() throws Exception {  
        File pwdFile = prepareTempPWDFile();
        runProcess(makeStartCommand(pwdFile), true); //NO I18N
        pwdFile.delete();
        viewAdminLogs();      
    }
        
    public void doStopServer() throws Exception {       
        runProcess(makeStopCommand(), true); //NO I18N       
    }
     
    private int runProcess(Command cmd, boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmd.getCmdArray());
             
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,cmd.toString() + System.currentTimeMillis());
        if (wait)
            child.waitFor();        
        return child.exitValue(); 
        
    }
    
    private File prepareTempPWDFile() throws IOException
    {
        String passwordFile = ".pcpwd.txt";
        File file = new File(passwordFile);
        if(file.exists())
            file.delete();
        FileOutputStream fout = null;
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
             fout.flush();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch(IOException ex){
            throw ex;
        }finally{
            try{
                fout.close();
            }catch(Exception e){
                //do nothing.
            }
        }
        return file;
    }
    
    private Command makeStartCommand(File passwordFile)
    {
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("start-domain"); 
        cmd.add("--user");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(passwordFile.getAbsolutePath());
        cmd.add(psconfig.getDefaultDomain());
                
        logger.info(cmd.toString());    
        logger.info("Password file: "+passwordFile.toString());
        return cmd;
        
    }
    
     private Command makeStopCommand() {
        
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("stop-domain"); 
        cmd.add(psconfig.getDefaultDomain());
        
        return cmd;
    }
    

    private Command makeStartDebugCommand(File passwordFile) {
        
        Command cmd = new Command();
        String ext = "";
         if (org.openide.util.Utilities.isWindows()){
            ext = ".bat";
        }
        cmd.add(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin" + ext);
        cmd.add("start-domain"); 
        cmd.add("--user");
        cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
        cmd.add("--passwordfile");
        cmd.add(passwordFile.getAbsolutePath());
        cmd.add("--debug=true");
        cmd.add(psconfig.getDefaultDomain());
                
        logger.info(cmd.toString());    
        logger.info("Password file: "+passwordFile.toString());
        return cmd;
    }
    
    private void viewAdminLogs(){
        String uri = dm.getUri();
        String location = psconfig.getDomainDir() + File.separator + "logs" + File.separator +"server.log";
        
        
        PSLogViewer logViewer = new PSLogViewer(new File(location));
        
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }
      
    private void viewInstanceLogs(){
        String uri = dm.getUri();
        String domainDir = psconfig.getDomainDir();
        File fdomainDir = new File(domainDir);
        String domainName = fdomainDir.getName();
        String location = domainDir +
                File.separator+"logs"+File.separator+"errors";
        
        PSLogViewer logViewer = new PSLogViewer(new File(location));
        
        try{
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }

    public void doStartDebug() throws Exception {
        File pwdFile = prepareTempPWDFile();
        runProcess(makeStartDebugCommand(pwdFile), true); //NO I18N
        pwdFile.delete();
        viewAdminLogs();
    }

    public void doStopDebug() throws Exception {
        doStopServer();
    }

    public int getDebugPort(){
        try {
            SunAppConfigUtil appConfigUtil = new SunAppConfigUtil(new File(psconfig.getDomainDir()));
            String debugAddress = appConfigUtil.getDebugAddress();
            return Integer.parseInt(debugAddress.trim());
        } catch (SunAppConfigUtil.ReadAccessDeniedException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        } catch (SAXException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        } catch (IOException ex) {
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        } catch(Exception ex){
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        }
        
        
    }

    public FindJSPServlet getFindJSPServlet(PSDeploymentManager dm) {
        return new SunAppFindJSPServletImpl(dm);
    }
    
}
