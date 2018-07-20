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
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.FileLogViewerSupport;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.NetbeanConstants;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.xml.sax.SAXException;

/**
 *
 * @author satya
 */
public class SunAppServerStartServer extends PSStartServerInf{
    private static Logger logger = Logger.getLogger(NetbeanConstants.PORTAL_LOGGER);
    private PSDeploymentManager dm;
    private PSConfigObject psconfig;
    private String storedMPW = null;
    /** Creates a new instance of SunAppServerStartServer */
    public SunAppServerStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    public void doStartServer(String[] env) throws Exception {
        File pwdFile = prepareTempPWDFile();
        runProcess(makeStartCommand(pwdFile),env, true); //NO I18N
        pwdFile.delete();
        viewAdminLogs();      
    }
        
    public void doStopServer(String[] env) throws Exception {
        runProcess(makeStopCommand(),env, true); //NO I18N
    }
     
    private int runProcess(Command cmd, String[] env,boolean wait) throws Exception {
        final Process child = Runtime.getRuntime().exec(cmd.getCmdArray(),setEnv(env));
             
        LogManager manager = new LogManager(dm);
        manager.openServerLog(child,cmd.toString() + System.currentTimeMillis());
        if (wait)
            child.waitFor();        
        return child.exitValue(); 
        
    }

     private String[] setEnv(String[] addEnv) {

        if(addEnv == null)
            return null;
        int addLength = addEnv.length;
        String[] env = new String[1 + addLength];

        //needed for Windows OS
        String systemRoot = System.getenv("SystemRoot");
        if (systemRoot == null) {
            systemRoot = "";
        }
        env[0] = "SystemRoot=" + systemRoot;
        System.out.println(System.getenv("SystemRoot"));
        if(addLength > 0) {
            for(int i=0;i<addEnv.length;i++) {
                env[1+i] = addEnv[i];
            }
        }

        return env;
    }
    
    private File prepareTempPWDFile() throws Exception
    {   
        File file = File.createTempFile("pwd", null);
        if(file.exists())
            file.deleteOnExit();
        FileOutputStream fout = null;
        String mpw = null;
        try{
            mpw = readMasterPasswordFile();
            if(mpw == null) {
                throw new Exception("Bad Master Password. Could not start the server.");
            }
        }catch(IllegalStateException e) {
            throw new Exception("Could not start server without Master Password.");
        }
        try {
             fout = new FileOutputStream(file);
             fout.write(new String("AS_ADMIN_PASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
             fout.write(new String("\nAS_ADMIN_ADMINPASSWORD="+psconfig.getProperty(SunAppServerConstants.SERVER_PASSWORD)).getBytes());
             if(mpw != null && mpw.trim().length() != 0)
                fout.write(new String("\nAS_ADMIN_MASTERPASSWORD="+mpw).getBytes());

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

        String gfVersion = getGlassFishVersion();

        if(SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {

            cmd.add("start-domain");
            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(passwordFile.getAbsolutePath());
            cmd.add(psconfig.getDefaultDomain());

        } else if(SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {

            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(passwordFile.getAbsolutePath());
            cmd.add("start-domain");
            cmd.add(psconfig.getDefaultDomain());

        }
                
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

        String gfVersion = getGlassFishVersion();

        if(SunAppServerConstants.GLASSFISH_V2.equals(gfVersion)) {

            cmd.add("start-domain");
            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(passwordFile.getAbsolutePath());
            cmd.add("--debug=true");
            cmd.add(psconfig.getDefaultDomain());
            
        } else if(SunAppServerConstants.GLASSFISH_V3.equals(gfVersion)) {

            cmd.add("--user");
            cmd.add(psconfig.getProperty(SunAppServerConstants.SERVER_USER));
            cmd.add("--passwordfile");
            cmd.add(passwordFile.getAbsolutePath());
            cmd.add("start-domain"); 
            cmd.add("--debug=true");
            cmd.add(psconfig.getDefaultDomain());
        }
                
        logger.info(cmd.toString());    
        logger.info("Password file: "+passwordFile.toString());
        return cmd;
    }
    
    private void viewAdminLogs(){
        
        String location = psconfig.getDomainDir() + File.separator + "logs" + File.separator +"server.log";
        
        FileLogViewerSupport logViewer = FileLogViewerSupport.getLogViewerSupport(new File(location), dm.getUri(), 2000, true);       
        try{
            logViewer.showLogViewer(true);
        }catch(Exception ex){
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }

    public void doStartDebug(String[] env) throws Exception {
        File pwdFile = prepareTempPWDFile();
        runProcess(makeStartDebugCommand(pwdFile),env, true); //NO I18N
        pwdFile.delete();
        viewAdminLogs();
    }

    public void doStopDebug(String[] env) throws Exception {
        doStopServer(env);
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
            ex.printStackTrace();
            return 0;
        } catch(Exception ex){
            logger.log(Level.SEVERE,"Error",ex);
            return 0;
        }
        
        
    }

    /* can return null if no mpw is known or entered by user
     **/
    private static final String MASTER_PASSWORD_ALIAS="master-password";//NOI18N
    private char[] getMasterPasswordPassword() {
        return MASTER_PASSWORD_ALIAS.toCharArray();
    }
    private String readMasterPasswordFile() throws IllegalStateException {
        String mpw= "changeit";//NOI18N

        //String lDomain = psconfig.getDefaultDomain();
        String lDomainDir = psconfig.getDomainDir();
        //final File pwdFile = new File(lDomainDir + File.separator + lDomain  +File.separator+"master-password");
        final File pwdFile = new File(lDomainDir + File.separator+"master-password");
        if (pwdFile.exists()) {
            try {
                ClassLoader loader = ((GlassFishServerDeployHandler)dm.getServerDeployHandler()).getServerClassLoader(new File(psconfig.getServerHome()));
                Class pluginRootFactoryClass =loader.loadClass("com.sun.enterprise.security.store.PasswordAdapter");//NOI18N
                java.lang.reflect.Constructor constructor =pluginRootFactoryClass.getConstructor(new Class[] {String.class, getMasterPasswordPassword().getClass()});
                Object PasswordAdapter =constructor.newInstance(new Object[] {pwdFile.getAbsolutePath(),getMasterPasswordPassword() });
                Class PasswordAdapterClazz = PasswordAdapter.getClass();
                java.lang.reflect.Method method =PasswordAdapterClazz.getMethod("getPasswordForAlias", new Class[]{  MASTER_PASSWORD_ALIAS.getClass()});//NOI18N
                mpw = (String)method.invoke(PasswordAdapter, new Object[] {MASTER_PASSWORD_ALIAS });

                return mpw;
            } catch (Exception ex) {
                //    ex.printStackTrace();
                return mpw;
            }
        } else {
            if (null == storedMPW) {
                MasterPasswordInputDialog d = new MasterPasswordInputDialog();
                if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                    mpw = d.getInputText();
                    //now validate the password:
                    try {

                        File pwdFile2 = new File(lDomainDir + File.separator + "config/domain-passwords");
                        ClassLoader loader = ((GlassFishServerDeployHandler)dm.getServerDeployHandler()).getServerClassLoader(new File(psconfig.getServerHome()));
                        Class pluginRootFactoryClass = loader.loadClass("com.sun.enterprise.security.store.PasswordAdapter");//NOI18N
                        java.lang.reflect.Constructor constructor = pluginRootFactoryClass.getConstructor(new Class[]{String.class, getMasterPasswordPassword().getClass()});
                        //this would throw an ioexception of the password is not the good one
                        constructor.newInstance(new Object[]{pwdFile2.getAbsolutePath(), mpw.toCharArray()});
                        storedMPW = mpw;
                        return mpw;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                } else {
                    throw new IllegalStateException();
                }
            } else {
                return storedMPW;
            }
        }
    }

//    public String getStartUpScripts() {
//        String ext = "";
//        String varName = "$JAVA_OPTS";
//        String customScript = psconfig.getProperty(PSStartServerInf.USE_CUSTOM_STARTUP_SCRIPT);
//        if(customScript == null || customScript.trim().length() == 0)
//            return "asadmin";
//
//        try {
//            if (org.openide.util.Utilities.isWindows()) {
//                ext = ".bat";
//                varName = "%JAVA_OPTS%";
//            }
//            File customAsAdmin = new File(psconfig.getServerHome() + File.separator + "bin" + File.separator + "asadmin_netbeans" + ext);
//            if (customAsAdmin.exists()) {
//                return "asadmin_netbeans";
//            }
//            File binFolder = new File(psconfig.getServerHome() + File.separator + "bin");
//            File asAdmin = new File(binFolder, "asadmin" + ext);
//            //FileUtil.copyFile(FileUtil.toFileObject(asAdmin), FileUtil.toFileObject(binFolder), "asadmin_netbeans");
//
//            BufferedReader rd = new BufferedReader(new FileReader(asAdmin));
//            BufferedWriter bw = new BufferedWriter(new FileWriter(customAsAdmin));
//
//            String line = rd.readLine();
//
//            while(line != null) {
//
//                StringTokenizer st = new StringTokenizer(line," ");
//                if(st.hasMoreTokens()) {
//                    String token = st.nextToken();
//                    if(token.indexOf("java") != -1) {
//                        line = line.replace(token, token + " " + varName + " ");
//                    }
//                }
//                bw.write(line);
//                bw.write("\n");
//                line = rd.readLine();
//                System.out.println(line);
//            }
//
//            if(bw != null) {
//                bw.flush();
//                bw.close();
//            }
//
//            if(rd != null) {
//                rd.close();
//            }
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return "asadmin_netbeans";
//
//    }

    public FindJSPServlet getFindJSPServlet(PSDeploymentManager dm) {
        return new SunAppFindJSPServletImpl(dm);
    }

    private String getGlassFishVersion() {
        String gfVersion = psconfig.getProperty(SunAppServerConstants.GLASSFISH_VERSON);
        if (gfVersion == null || gfVersion.trim().length() == 0) {
            gfVersion = SunAppServerConstants.GLASSFISH_V2;
        }
        return gfVersion;
    }
    
}
