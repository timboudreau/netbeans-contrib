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
 * AVKSupport.java
 *
 * Created on September 8, 2005, 9:46 AM
 *
 */

package org.netbeans.modules.j2ee.sun.ide.avk;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.lang.reflect.Method;
import java.io.InputStreamReader;

import javax.enterprise.deploy.spi.DeploymentManager;

import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.modules.InstalledFileLocator;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.netbeans.modules.j2ee.sun.ide.Installer;
import org.netbeans.modules.j2ee.sun.ide.j2ee.ui.Util;
import org.netbeans.modules.j2ee.sun.api.InstrumentAVK;
import org.netbeans.modules.j2ee.sun.api.ExtendedClassLoader;
import org.netbeans.modules.j2ee.sun.ide.j2ee.PluginProperties;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.netbeans.modules.j2ee.sun.ide.j2ee.DeploymentManagerProperties;
import org.netbeans.modules.j2ee.sun.ide.j2ee.runtime.actions.ViewLogAction;
import org.netbeans.modules.j2ee.sun.api.ServerLocationManager;

import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;

/**
 *
 * @author Nitya Doraisamy
 */
public class AVKSupport implements InstrumentAVK {
    
    private static final String FILE_BACKUP_EXTENSION = "backup";
    private static SunDeploymentManagerInterface sunDm;
    private static boolean serverRunning = true;
           
    protected static final ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.modules.j2ee.sun.ide.avk.actions.Bundle");// NOI18N
    
    /** Creates a new instance of AVKSupport */
    public AVKSupport() {
    }
    
    public AVKSupport(SunDeploymentManagerInterface sdm) {
        setDeploymentManager(sdm);
    }
    
    public void setDeploymentManager(SunDeploymentManagerInterface sdm){
        this.sunDm = sdm;
    }
    
    public void setAVK(boolean onOff){
        if(onOff){
            //Turn On AVK
            instrumentAVK();
        }else{
            //Turn Off AVK
            uninstrumentAVK();
        }
    }
    
    public void generateReport() {
        File report = null;
        try{
            boolean success = runReportTool();
            if(success){
                report = new File(getAVKReportLocation() + File.separator + "results" + File.separator + "suiteSummary.html"); //NOI18N
                URLDisplayer.getDefault().showURL(report.toURI().toURL());
            }
        }catch(Exception ex){
            if(report != null){
                String message = MessageFormat.format(bundle.getString("Err_ShowReport"), new Object[]{report.getAbsolutePath()}); //NOI18N
                Util.showError(message);
            }
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return;
        }
    }
    
    private void instrumentAVK()  {
        if(this.sunDm.isLocal() == false){
            return;
        }
        try{
            DeploymentManagerProperties dmProps = new DeploymentManagerProperties((DeploymentManager) this.sunDm);
            if(!dmProps.getAVKOn()){
                setTopManagerStatus(bundle.getString("MSG_Instrument")); //NOI18N
                FileUtil.clearResults(this.sunDm, dmProps);
                if(this.sunDm.isRunning()){
                    stopStartForInstrument(dmProps);
                }else{
                    instrument(dmProps);
                    startAfterInstrument(dmProps);
                }
            }else{
                if(! this.sunDm.isRunning()){
                    startAfterInstrument(dmProps);
                }
            }
        }catch(Exception ex){
            Util.showError(bundle.getString("Err_Instrument")); //NOI18N
            ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, ex.getLocalizedMessage());
        }
    }
    
    private void stopStartForInstrument(DeploymentManagerProperties dmProps) throws Exception {
        try{
            setTopManagerStatus(bundle.getString("MSG_Stop")); //NOI18N
            stopServer(dmProps, this.sunDm);
            dmProps.getInstanceProperties().refreshServerInstance();
            instrument(dmProps);
            startAfterInstrument(dmProps);
        }catch(Exception ex){
            throw ex;
        }    
    }
    
    private void startAfterInstrument(DeploymentManagerProperties dmProps) throws Exception {
        try{
            setTopManagerStatus(bundle.getString("MSG_Start")); //NOI18N
            startServer(dmProps, this.sunDm);
            dmProps.getInstanceProperties().refreshServerInstance();
            setTopManagerStatus(bundle.getString("MSG_AVK_Running")); //NOI18N
        }catch(Exception ex){
            throw ex;
        }
    }
    
    private void instrument(DeploymentManagerProperties dmProps){
        editPolicyFile(dmProps);
        editStoppedDomainConfig(dmProps, true);
        dmProps.setAVKOn(true);
        setTopManagerStatus(bundle.getString("MSG_AVK_Stopped")); //NOI18N
    }
    
    public void uninstrumentAVK()  {
        DeploymentManagerProperties dmProps = new DeploymentManagerProperties((DeploymentManager) this.sunDm);
        try{
            setTopManagerStatus(bundle.getString("MSG_Generating")); //NOI18N
            //Parse Domain Config to remove elements
            editStoppedDomainConfig(dmProps, false);
        }catch(Exception ex){
            Util.showError(bundle.getString("Err_UnInstrument")); //NOI18N
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
            return;
        }
    }
    
    public Object loadClass(String methodName, SunDeploymentManagerInterface sdm) throws Exception{
        Object result = null;
        File avkHome = getAVKHome();
        DeploymentManager deployMgr = (DeploymentManager)sdm;
        ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
        try{
            Class[] argClass = new Class[2];
            argClass[0] = javax.enterprise.deploy.spi.DeploymentManager.class;
            argClass[1] = java.io.File.class;
            Object[] argObject = new Object[2];
            argObject[0] = deployMgr;
            argObject[1] = avkHome;
            Class controllerUtilClass = ServerLocationManager.getNetBeansAndServerClassLoader(sdm.getPlatformRoot()).
                    loadClass("org.netbeans.modules.j2ee.sun.util.InstrumentAVK"); //NOI18N
            
            
            Method method = controllerUtilClass.getMethod(methodName, argClass);
            
            Thread.currentThread().setContextClassLoader(
                    ServerLocationManager.getNetBeansAndServerClassLoader(sdm.getPlatformRoot()));
            
            result = method.invoke(controllerUtilClass.newInstance(), argObject);
            
        } catch (Exception e){
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(origClassLoader);
        }
        return result;
    }
    
    public boolean runReportTool(){
        try{
            System.setProperty("j2ee.appverification.home", getAVKHome().getAbsolutePath());
            System.setProperty("com.sun.aas.installRoot", sunDm.getPlatformRoot().getAbsolutePath());
            DeploymentManagerProperties dmProps = new DeploymentManagerProperties((DeploymentManager) this.sunDm);
	    ExtendedClassLoader loader = (ExtendedClassLoader) ServerLocationManager.getServerOnlyClassLoader(sunDm.getPlatformRoot());
            if(loader != null){
                File f = getAVKJarLocation();
                loader.addURL(f);
            }

            if(loader != null){
                String domainDir = dmProps.getLocation() + File.separator + dmProps.getDomainName();
                String args[] = {"-result", "-domainDir", domainDir, "-resultsDir", getAVKReportLocation()}; 
                Class cc = loader.loadClass("com.sun.enterprise.appverification.tools.ReportTool");
                
                Method method = cc.getMethod("main", new Class[] {args.getClass()});
                method.invoke(null, new Object[] { args });
            }
            return true;
        }catch(Exception ex){
            return false;
        }
    }
    
    public File getAVKHome(){
        File f = getAVKJarLocation();
        if(f != null)
            f = f.getParentFile().getParentFile();
        return f;             
    }
    
    private File getAVKJarLocation(){
        File f = InstalledFileLocator.getDefault().locate("javke142/lib/javke.jar", null, true);
        return f;
    }
    
    private String getAVKReportLocation(){
        String resultsDir = System.getProperty("netbeans.user") + File.separator + "avkreport"; //NOI18N
        return resultsDir;
    }
    
    public void stopServer(DeploymentManagerProperties dmProps, SunDeploymentManagerInterface sdm) throws Exception{
        String asadminCmd = sdm.getPlatformRoot() + File.separator +
                "bin" +
                File.separator +
                "asadmin";          //NOI18N
        
        if (File.separator.equals("\\")) {
            asadminCmd = asadminCmd + ".bat"; //NOI18N
        }
        String args[]={asadminCmd, "stop-domain",
                dmProps.getDomainName()};
        ViewLogAction.viewLog(sdm);        
        exec(args);
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<args.length;++i){
            sb.append(args[i]+ " ");
        }
    }
    
    public void startServer(DeploymentManagerProperties dmProps, SunDeploymentManagerInterface sdm) throws Exception{
        String asadminCmd = sdm.getPlatformRoot() + File.separator +
                "bin" +
                File.separator +
                "asadmin";          //NOI18N
        
        if (File.separator.equals("\\")) {
            asadminCmd = asadminCmd + ".bat"; //NOI18N
        }
        String args[]={asadminCmd, "start-domain",
                dmProps.getDomainName()};
        ViewLogAction.viewLog(sdm);        
        exec(args);
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<args.length;++i){
            sb.append(args[i]+ " ");
        }
    }
    
    private int exec(String[] arr) throws Exception{
        final Process subProcess = Runtime.getRuntime().exec(arr);
        //start a new thread that reads from the subprocess's output stream and prints it in deploytool log file.
        new Thread(){ //an inner class
            public void run(){
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
                    String line=null;
                    while ( (line = br.readLine()) != null){
                        //logger.info(line);
                    }
                } catch (IOException ioe) {
                    //logger.log(Level.SEVERE, "Exception" ,ioe);
                }
            }//run
        }.start();
        //start a new thread that reads from the subprocess's error stream and prints it in deploytool log file.
        new Thread(){ //an inner class
            public void run(){
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(subProcess.getErrorStream()));
                    String line=null;
                    while ( (line = br.readLine()) != null) {
                        //logger.info(line);
                    }
                } catch (IOException ioe) {
                    //logger.log(Level.SEVERE, "Exception" ,ioe);
                }
            }//run
        }.start();
        while(true){
            try{
                int exitValue=subProcess.waitFor();
                //logger.log(Level.FINER, "Process exited with exit value " + exitValue);
                return exitValue;
            }catch(InterruptedException e){
            }
        }
    }
    
    public void editPolicyFile(DeploymentManagerProperties dmProps){
        System.setProperty("j2ee.appverification.home", getAVKHome().getAbsolutePath()); //NOI18N
        String policyFile = dmProps.getLocation() + File.separator + dmProps.getDomainName() + File.separator + "config" + File.separator + "server.policy";
        if(policyFile == null)
            return;
        try{
            DomainParser.backupFile(policyFile);
            BufferedReader textIn = new BufferedReader(new FileReader(policyFile));
            String line;
            boolean edited = false;
            while ((line = textIn.readLine()) != null){
                if(line.indexOf("j2ee.appverification.home") != -1){ //NOI18N
                    edited = true;
                    break;
                }
                
            }
            textIn.close();
            if(! edited){
                PrintWriter textOut = new PrintWriter(new BufferedWriter(new FileWriter(policyFile, true)));
                textOut.println("// permissions for avkit classes"); //NOI18N
                textOut.println("grant codeBase \"file:${j2ee.appverification.home}/-\" {"); //NOI18N
                textOut.println("permission java.security.AllPermission;"); //NOI18N
                textOut.println("};"); //NOI18N
                textOut.flush();
                textOut.close();
            }
        }catch(Exception ex){
            DomainParser.restoreFile(policyFile);
        }
    } 
    
    public void editStoppedDomainConfig(DeploymentManagerProperties dmProps, boolean onOff){
        File avkHome = getAVKHome();
        String domainXmlLoc = getDomainConfigLoc(dmProps) + File.separator + "domain.xml";
        DomainParser.backupFile(domainXmlLoc);
        boolean success = DomainParser.editSupportInDomain(domainXmlLoc, getAVKJarLocation().getAbsolutePath(), avkHome.getAbsolutePath(), onOff);
        if(!success){
            DomainParser.restoreFile(domainXmlLoc);
        }    
    }
    
    private String getDomainConfigLoc(DeploymentManagerProperties dmProps){
        String configLoc = dmProps.getLocation() + File.separator + dmProps.getDomainName() + File.separator + "config"; //NOI18N
        return configLoc;
    }
    
    private void setTopManagerStatus(String msg){
        StatusDisplayer.getDefault().setStatusText(msg);
    }
    
    public boolean createAVKSupport(DeploymentManager dm, J2eeModuleProvider modProvider){
        AddAVKSupport support = new AddAVKSupport();
        return support.createAVKSupport(dm, modProvider);
    }
    
}
