/*
 * ASAdminTasks.java
 *
 * Created on March 23, 2006, 3:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ide.avk.actions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.netbeans.modules.j2ee.deployment.plugins.api.InstanceProperties;
import org.netbeans.modules.j2ee.sun.api.ExtendedClassLoader;
import org.netbeans.modules.j2ee.sun.api.ServerLocationManager;
import org.netbeans.modules.j2ee.sun.api.SunDeploymentManagerInterface;
import org.netbeans.modules.j2ee.sun.api.SunURIManager;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author bshankar@sun.com
 */
public class AdminTasks {
    
    private SunDeploymentManagerInterface sdm;
    private InstanceProperties instProps;
    
    private static final String CALLFLOW_MBEAN_NAME = "amx:j2eeType=X-CallFlowMonitor,name=server,X-ServerRootMonitor=server";
    private static final String VERIFIER_MBEAN_NAME = "com.sun.enterprise.appverification.mbeans.AppVerification";
    private static final String VERIFIER_JAR_FILE = "javke.jar";
    
    private static final String CREATE_MBEAN_NAME = "com.sun.appserv:category=config,type=applications";
    private static final String CREATE_MBEAN_OPERATION_NAME = "createMBean";
    
    private static final String CREATE_RULE_MBEAN_NAME = "com.sun.appserv:type=management-rules,config=server-config,category=config";
    private static final String CREATE_RULE_OPERATION_NAME = "createManagementRule";
    
    public AdminTasks(SunDeploymentManagerInterface sdm) {
        this.sdm = sdm;
        this.instProps = getInstanceProperties(sdm);
        printInstanaceProperties();
    }
    
    private InstanceProperties getInstanceProperties(SunDeploymentManagerInterface sdm) {
        InstanceProperties instProps = SunURIManager.getInstanceProperties(sdm.getPlatformRoot(), sdm.getHost(), sdm.getPort());
        if(instProps == null) {
            try {
                instProps = SunURIManager.createInstanceProperties(
                        sdm.getPlatformRoot(),
                        sdm.getHost(),
                        Integer.toString(sdm.getPort()),
                        sdm.getUserName(),
                        sdm.getPassword() ,
                        sdm.getHost() + ":" + sdm.getPort());
            } catch(Exception ex) {
                return null;
            }
        }
        instProps.setProperty("httpportnumber", sdm.getNonAdminPortNumber());
        return instProps;
    }
    
    /**
     * Copies javke.jar to $AS_HOME/lib/javke.jar
     * Restarts the server if it is running.
     */
    public boolean copyJar() {
        File f = InstalledFileLocator.getDefault().locate("javke5/lib/javke.jar", null, true);
        String targetFile = sdm.getPlatformRoot() + File.separator + "lib" + File.separator + VERIFIER_JAR_FILE;
        AdminTasks.debug("javke.jar  = " + f.getAbsoluteFile() + ", exists = " + f.exists());
        
        if(!f.exists()) return false;
        try {
            BufferedInputStream bis= new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            try {
                byte[] data = new byte[8*1024];
                int numOfBytesRead;
                while((numOfBytesRead = bis.read(data,0,data.length)) != -1) o.write(data, 0, numOfBytesRead);
            } catch(Exception ex) { // TODO : log error message.
            }
            FileOutputStream fo = new FileOutputStream(targetFile);
            fo.write(o.toByteArray());
            fo.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        /*
        ResourceToFile.getFile(VERIFIER_JAR_FILE,
                sdm.getPlatformRoot() + File.separator + "lib" + File.separator + VERIFIER_JAR_FILE);
         */
        
        if(sdm.isRunning()) {
            AdminTasks.debug("copyJar() - restarting server....");
            restartServer();
        }
        return true;
    }
    
    /**
     * Create AVK MBean and Rule.
     */
    public boolean createVerifierMBeanAndRule() {
        
        createVerifierMBean();
        
        createVerifierRule();
        
        return true;
    }
    
    /**
     * Do the necessary AVK setup on the target appserver.
     * @return true if AVK setup on the target appserver is successful, false otherwise.
     */
    public boolean setup() {
        /**
         * Step1. Copy javke.jar to $AS_HOME/lib directory.
         */
        copyJar();
        /**
         * Step2. Start the target application server, if not already running.
         */
        startServer();
        
        /**
         * Step3. Create verifier MBean and Rule.
         */
        createVerifierMBeanAndRule();
        
        return true;
    }
    
    private boolean createVerifierMBean() {
        Map<String,String> params = new HashMap<String,String>();
        params.put("impl-class-name", VERIFIER_MBEAN_NAME);
        params.put("name", "AppVerification");
        
        Object[] paramsInfo = new Object[] {"server", params};
        String[] typesInfo = new String[] {String.class.getName(), Map.class.getName()};
        
        if (sdm == null) return false;
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.invoke(new ObjectName(CREATE_MBEAN_NAME), CREATE_MBEAN_OPERATION_NAME, paramsInfo, typesInfo);
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    private boolean createVerifierRule() {
        Properties p = new Properties();
        p.put("tracepoint","*");
        
        Object[] paramsInfo = new Object[] {
            "app-verification",  // rule name
            new Boolean(true), // rule enabled
            null, // rule descrption
            "trace", // event type
            "FINEST", // event log level
            new Boolean(true), // record event
            null, // event description
            p, // event properties
            "AppVerification" // action mbean name
        };
        String[] typesInfo = new String[] {
            String.class.getName(),
            Boolean.class.getName(),
            String.class.getName(),
            String.class.getName(),
            String.class.getName(),
            Boolean.class.getName(),
            String.class.getName(),
            Properties.class.getName(),
            String.class.getName()
        };
        
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.invoke(new ObjectName(CREATE_RULE_MBEAN_NAME), CREATE_RULE_OPERATION_NAME, paramsInfo, typesInfo);
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * @return true if AVK is setup on the target appserver, false otherwise.
     */
    public boolean isSetup() {
        File avkJar = new File(sdm.getPlatformRoot() + File.separator + "lib" + File.separator + VERIFIER_JAR_FILE);
        return avkJar.exists();
    }
    
    public boolean showReport(String resultsDir) {
        File report = new File(resultsDir + File.separator + "results" + File.separator + "suiteSummary.html");
        try {
            URLDisplayer.getDefault().showURL(report.toURI().toURL());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
    
    public boolean generateReport(String resultsDir) {
        /**
         * Set the following system properties :
         *      com.sun.aas.installRoot
         *      com.sun.aas.instanceRoot
         *
         */
        if (sdm == null) return false;
        File javkeInstallDir = InstalledFileLocator.getDefault().locate("javke5", null, true);
        if(javkeInstallDir != null && javkeInstallDir.exists()) {
            AdminTasks.debug("JAVKE_INSTALL_DIR = " + javkeInstallDir.getAbsolutePath());
        }
        String domainDir = instProps.getProperty("LOCATION") + File.separator + instProps.getProperty("DOMAIN");
        
        /**
         * Invoke report generator.
         */
        try {
            /*
             * Invoking Introspector using Reflection APIs fails for some standalone modules.
             * So to be uniform, using Runtime.exec(...) also to invoke the ReportTool.
             *
             * Running ReportTool using Runtime.exec(...) solves the ClassLoading issue
             * caused during verifying AS9.0 and AS8.x applications simultaneously.
             */
            /*
            System.setProperty("j2ee.appverification.home", javkeInstallDir.getAbsolutePath());
            System.setProperty("com.sun.aas.installRoot", sdm.getPlatformRoot().getAbsolutePath());
            System.setProperty("com.sun.aas.instanceRoot", domainDir);
            
            ExtendedClassLoader cl = (ExtendedClassLoader)ServerLocationManager.getNetBeansAndServerClassLoader(sdm.getPlatformRoot());
            System.out.println("cl = " + cl + ", cl.parent = " + cl.getParent());
            //cl.addURL(new File(sdm.getPlatformRoot() + File.separator + "lib" + File.separator + "avk-appserv.jar"));
            cl.addURL(new File(sdm.getPlatformRoot() + File.separator + "lib" + File.separator + VERIFIER_JAR_FILE));
            Class c = cl.loadClass("com.sun.enterprise.appverification.report.ReportTool");
            System.out.println("c = " + Integer.toHexString(c.hashCode()));
            Object o = c.newInstance();
            Method m = c.getMethod("generateReport", new Class[]{String.class});
            m.invoke(c.newInstance(), new Object[]{resultsDir});
             */
            String fs = File.separator;
            String ps = File.pathSeparator;
            String commandArray[] = new String[] {
                System.getProperty("java.home") + fs + "bin" + fs + "java",
                "-classpath",
                javkeInstallDir.getAbsolutePath() + fs + "lib" + fs + VERIFIER_JAR_FILE + ps + sdm.getPlatformRoot().getAbsolutePath() + fs + "lib" + fs + "appserv-rt.jar",
                "-Dj2ee.appverification.home=" + javkeInstallDir.getAbsolutePath(),
                "-Dcom.sun.aas.instanceRoot=" + domainDir,
                "-DinputDir=" + resultsDir,
                "-DoutputDir=" + resultsDir,
                "com.sun.enterprise.appverification.report.ReportTool"
            };
            exec(commandArray);
        } catch(Throwable t) {
            t.printStackTrace();
        }
        
        return true;
        
    }
    
    public boolean storeIntrospectionResults(String deployDir, String resultsDir) {
        /**
         * Set the following system properties :
         *      com.sun.aas.installRoot
         *      com.sun.aas.instanceRoot
         *
         */
        if (sdm == null) return false;
        File javkeInstallDir = InstalledFileLocator.getDefault().locate("javke5", null, true);
        if(javkeInstallDir != null && javkeInstallDir.exists()) {
            AdminTasks.debug("JAVKE_INSTALL_DIR = " + javkeInstallDir.getAbsolutePath());
        }
        String domainDir = instProps.getProperty("LOCATION") + File.separator + instProps.getProperty("DOMAIN");
        
        /**
         * Invoke introspector.
         */
        try {
            /*
             * Invoking Introspector using Reflection APIs fails for some standalone modules.
             * Hence using Runtime.exec(...) to invoke the Introspector.
             */
            /*
            System.setProperty("j2ee.appverification.home", javkeInstallDir.getAbsolutePath());
            System.setProperty("com.sun.aas.installRoot", sdm.getPlatformRoot().getAbsolutePath());
            System.setProperty("com.sun.aas.instanceRoot", domainDir);
             
            ExtendedClassLoader cl = (ExtendedClassLoader)ServerLocationManager.getNetBeansAndServerClassLoader(sdm.getPlatformRoot());
            cl.addURL(new File(sdm.getPlatformRoot() + File.separator + "lib" + File.separator + VERIFIER_JAR_FILE));
            Class c = cl.loadClass("com.sun.enterprise.appverification.introspector.Introspector");
            Object o = c.newInstance();
            Method m = c.getMethod("introspect", new Class[]{String.class, String.class});
            m.invoke(o, new Object[]{deployDir, resultsDir});
             */
            String fs = File.separator;
            String ps = File.pathSeparator;
            String[] commandArray = new String[] {
                System.getProperty("java.home") + fs + "bin" + fs + "java",
                "-classpath",
                javkeInstallDir.getAbsolutePath() + fs + "lib" + fs + VERIFIER_JAR_FILE + ps + sdm.getPlatformRoot().getAbsolutePath() + fs + "lib" + fs + "appserv-rt.jar" + ps + sdm.getPlatformRoot().getAbsolutePath() + fs + "lib" + fs + "javaee.jar",
                "-Dj2ee.appverification.home=" + javkeInstallDir.getAbsolutePath(),
                "-Dcom.sun.aas.installRoot=" + sdm.getPlatformRoot().getAbsolutePath(),
                "-Dcom.sun.aas.instanceRoot=" + domainDir,
                "-DarchivePaths=" + deployDir,
                "-DresultDir=" + resultsDir,
                "com.sun.enterprise.appverification.introspector.Introspector"
            };
            exec(commandArray);
        } catch(Throwable t) {
            t.printStackTrace();
        }
        
        return true;
    }
    
    
    /**
     * Calls verifier mbean and the instrumentation results are dumped to file.
     */
    public boolean storeInstrumentationResults(String resultsDir, boolean cleanResults) {
        if (sdm == null) return false;
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.invoke(lookupMBean(VERIFIER_MBEAN_NAME), "writeInstrumentationResults", new Object[]{resultsDir, Boolean.valueOf(cleanResults)}, new String[]{String.class.getName(), Boolean.class.getName()});
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean startAutoWriteInstrumentationResults(String resultsDir) {
        if (sdm == null) return false;
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.invoke(lookupMBean(VERIFIER_MBEAN_NAME), "start", new Object[]{resultsDir}, new String[]{String.class.getName()});
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean stopAutoWriteInstrumentationResults() {
        if (sdm == null) return false;
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.invoke(lookupMBean(VERIFIER_MBEAN_NAME), "stop", new Object[]{}, new String[]{});
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    private ObjectName lookupMBean(String objectNamePattern) throws Exception {
        ObjectName objectName = null;
        MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
        Set s = mbsc.queryNames(new ObjectName("user*:*"), null);
        for (Object o : s) {
            String str = ((ObjectName) o).toString();
            if (str.indexOf(objectNamePattern) != -1) {
                objectName = (ObjectName) o;
                break;
            }
        }
        return objectName;
    }
    
    private boolean setCallFlowStatus(boolean status) {
        if (sdm == null) return false;
        try {
            MBeanServerConnection mbsc = sdm.getManagement().getMBeanServerConnection();
            mbsc.setAttribute(
                    new ObjectName(CALLFLOW_MBEAN_NAME),
                    new Attribute("Enabled", status));
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean stopInstrumentation() {
        if (!sdm.isRunning()) return true;
        return setCallFlowStatus(false);
    }
    
    public boolean startInstrumentation(boolean startServer) {
        if (startServer) startServer();
        return startInstrumentation();
    }
    
    public boolean startInstrumentation() {
        return setCallFlowStatus(true);
    }
    
    private boolean startServer() {
        if(sdm.isRunning()) return true;
        String[] command = new String[] {
            sdm.getPlatformRoot() + File.separator + "bin" + File.separator + "asadmin",
            "start-domain"
        };
        try {
            exec(command);
        } catch(Exception ex) {
            return false;
        }
        return true;
        
    }
    
    private boolean stopServer() {
        if(!sdm.isRunning()) return true;
        String[] command = new String[] {
            sdm.getPlatformRoot() + File.separator + "bin" + File.separator + "asadmin",
            "stop-domain"
        };
        try {
            exec(command);
        } catch(Exception ex) {
            return false;
        }
        return true;
        
    }
    
    private boolean restartServer() {
        return stopServer() & startServer();
    }
    
    private int exec(String[] commandString) throws Exception {
        final Process subProcess;
        if (commandString.length == 1) {
            subProcess = Runtime.getRuntime().exec(commandString[0]);
        } else {
            subProcess = Runtime.getRuntime().exec(commandString);
        }
        
        new Thread(){
            public void run(){
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));
                    String str;
                    while ((str = br.readLine()) != null) {
                        AdminTasks.debug(str);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }.start();
        
        new Thread(){
            public void run(){
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(subProcess.getErrorStream()));
                    String str;
                    while ((str = br.readLine()) != null) {
                        AdminTasks.debug(str);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }//run
        }.start();
        
        return subProcess.waitFor();
    }
    
    /*
    private static InstanceProperties getInstanceProperties() {
        for(String s : InstanceProperties.getInstanceList()) {
            if (s.indexOf("Sun:AppServer") > -1) {
                InstanceProperties p = InstanceProperties.getInstanceProperties(s);
                return p;
            }
        }
        return null;
    }
     */
    
    private void printInstanaceProperties() {
        debug("Current instace properties : \n");
        Enumeration e = instProps.propertyNames();
        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            debug("key = " + key + ", value = " + instProps.getProperty(key));
        }
    }
    
    public static void debug(String msg) {
        //System.out.println(msg);
    }
}
