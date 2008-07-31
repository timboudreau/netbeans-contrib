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
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.tomcat;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.j2ee.deployment.plugins.api.UISupport;
import org.netbeans.modules.portalpack.servers.core.PSLogViewer;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.Command;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.ErrorManager;
import org.openide.execution.NbProcessDescriptor;

/**
 *  
 * @author satya
 */
public class TomcatStartServer extends PSStartServerInf implements TomcatConstant {

    private PSConfigObject psconfig;
    private PSDeploymentManager dm;
    private static int CMD_START = 0;
    private static int CMD_STOP = 1;
    /** For how long should we keep trying to get response from the server. */
    private static final long TIMEOUT_DELAY = 180000;

    /** Creates a new instance of TomcatStartServer */
    public TomcatStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    public void doStartServer() throws Exception {
        //runStartProcess(makeStartCommand(),true);
        runProcess(CMD_START, makeStartCommand(), setEnv(), true);
    //viewLogs();


    }

    public void doStopServer() throws Exception {
        //runStopProcess(makeStopCommand(), true);
        runProcess(CMD_STOP, makeStopCommand(), setEnv(), true);
    }
    
    public void doStartDebug() throws Exception {
        runProcess(CMD_START, makeDebugCommand(), setDebugEnv(), true);
    }

    public void doStopDebug() throws Exception {
        runProcess(CMD_STOP, makeStopCommand(), setEnv(), true);
    }

    public int getDebugPort() {
        return 9090;
    }

    public FindJSPServlet getFindJSPServlet(PSDeploymentManager dm) {
        return new TomcatFindJSPServletImpl(dm);
    }

    private NbProcessDescriptor makeStartCommand() {
        Command cmd = new Command();

        String script = "";
        if (org.openide.util.Utilities.isWindows()) {
            script = "catalina.bat";
        } else {
            script = "catalina.sh";
        }
        cmd.add(psconfig.getProperty(CATALINA_HOME) + File.separator + "bin" + File.separator + script);
        ///cmd.add("run");

        NbProcessDescriptor nd = new NbProcessDescriptor(cmd.toString(), "run");

        //return cmd;
        return nd;
    //System.out.println(cmd.toString());
    //return cmd.toString();
    }

    private NbProcessDescriptor makeStopCommand() {
        Command cmd = new Command();

        String script = "";
        if (org.openide.util.Utilities.isWindows()) {
            script = "catalina.bat";
        } else {
            script = "catalina.sh";
        }
        cmd.add(psconfig.getProperty(CATALINA_HOME) + File.separator + "bin" + File.separator + script);

        ///cmd.add("stop");
        ///System.out.println(cmd.toString());
        NbProcessDescriptor nd = new NbProcessDescriptor(cmd.toString(), "stop");
        return nd;
    ///return cmd.toString();
    }
    
    private NbProcessDescriptor makeDebugCommand() {
        Command cmd = new Command();

        String script = "";
        if (org.openide.util.Utilities.isWindows()) {
            script = "catalina.bat";
        } else {
            script = "catalina.sh";
        }
        cmd.add(psconfig.getProperty(CATALINA_HOME) + File.separator + "bin" + File.separator + script);
        ///cmd.add("run");

        NbProcessDescriptor nd = new NbProcessDescriptor(cmd.toString(), "jpda run");

       // NbProcessDescriptor nd = new NbProcessDescriptor(cmd.toString(), "stop");
        //return cmd;
        return nd;
    //System.out.println(cmd.toString());
    //return cmd.toString();
    }

    private int runProcess(int commandType, NbProcessDescriptor nbProcess, String[] env, boolean wait) throws Exception {
        /* Process child = null;
        if(env == null || env.length == 0)
        child = Runtime.getRuntime().exec(cmd.getCmdArray());
        else
        child = Runtime.getRuntime().exec(cmd.getCmdArray(),env);*/

        //NbProcessDescriptor nd = new NbProcessDescriptor(cmd.toString(), "run");

        File binDir = new File(psconfig.getProperty(TomcatConstant.CATALINA_HOME) + File.separator + "bin");
        Process child = nbProcess.exec(null, env, binDir);
        LogManager manager = new LogManager(dm);
        //manager.openServerLog(child, cmd.toString() + System.currentTimeMillis());
        manager.openServerLog(child, nbProcess.getProcessName());
        //// if (wait)
        ////     child.waitFor();        
        //return child.exitValue();

        if (hasCommandSucceeded(child, commandType)) {
            return 1;
        }

        return 0;

    }

    private int runStartProcess(String str, boolean wait) throws Exception {
        String[] env = setEnv();
        //env[2] = "JAVA_HOME="+System.getProperty("java.dir");
        //System.out.println(System.getProperty("java.home"));
        final Process child = Runtime.getRuntime().exec(str, env);

        LogManager manager = new LogManager(dm);
        manager.openServerLog(child, str + System.currentTimeMillis());
        if (wait) {
            try {
                //child.waitFor();
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        while (!dm.isRunning()) {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return 1;
    //  return child.exitValue();


    }

    private int runStopProcess(String str, boolean wait) throws Exception {
        String[] env = setEnv();
        //env[2] = "JAVA_HOME="+System.getProperty("java.dir");
        //System.out.println(System.getProperty("java.home"));
        final Process child = Runtime.getRuntime().exec(str, env);

        LogManager manager = new LogManager(dm);
        manager.openServerLog(child, str + System.currentTimeMillis());
        if (wait) //child.waitFor();
        {
            Thread.sleep(4000);
        }
        while (dm.isRunning()) {
            Thread.sleep(2000);
        }
        return child.exitValue();


    }

    private String[] setEnv() {

        String[] env = new String[5];
        env[0] = "CATALINA_HOME=" + psconfig.getProperty(CATALINA_HOME);
        env[1] = "CATALINA_BASE=" + psconfig.getProperty(CATALINA_BASE);
        env[2] = "JRE_HOME=" + psconfig.getProperty(JAVA_HOME);
        env[3] = "JAVA_HOME=" + psconfig.getProperty(JAVA_HOME);


        //needed for Windows OS
        String systemRoot = System.getenv("SystemRoot");
        if (systemRoot == null) {
            systemRoot = "";
        }
        env[4] = "SystemRoot=" + systemRoot;
        System.out.println(System.getenv("SystemRoot"));

        return env;
    }
     
                            
    private String[] setDebugEnv() {
        String[] env = new String[7];
        env[0] = "CATALINA_HOME=" + psconfig.getProperty(CATALINA_HOME);
        env[1] = "CATALINA_BASE=" + psconfig.getProperty(CATALINA_BASE);
        env[2] = "JRE_HOME=" + psconfig.getProperty(JAVA_HOME);
        env[3] = "JAVA_HOME=" + psconfig.getProperty(JAVA_HOME);
        
        //needed for Windows OS
        String systemRoot = System.getenv("SystemRoot");
        if (systemRoot == null) {
            systemRoot = "";
        }
        env[4] = "SystemRoot=" + systemRoot;
        System.out.println(System.getenv("SystemRoot"));
        env[5] = "JPDA_TRANSPORT=dt_socket";        // NOI18
        try{
            int debugPort = Integer.parseInt(psconfig.getProperty(TomcatConstant.DEBUG_PORT));
            env[6] = "JPDA_ADDRESS=" + debugPort;
        }catch(NumberFormatException e) {
            env[6] = "JPDA_ADDRESS=11540";
        }
        
        return env;
     }                        

    private void viewLogs() {
        String uri = dm.getUri();
        String location = psconfig.getProperty(CATALINA_BASE) + File.separator + "logs" + File.separator + "catalina.out";


        PSLogViewer logViewer = new PSLogViewer(new File(location));


        try {
            logViewer.showLogViewer(UISupport.getServerIO(uri));
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ex);
        }
    }

   

    /**
     * Try to get response from the server, whether the START/STOP command has 
     * succeeded.
     *
     * @return <code>true</code> if START/STOP command completion was verified,
     *         <code>false</code> if time-out ran out.
     */
    private boolean hasCommandSucceeded(Process p, int commandType) {
        ///long timeout = System.currentTimeMillis() + TIMEOUT_DELAY;
        while (true) {

            boolean isRunning = isRunning(p, (int) TIMEOUT_DELAY, true);

            if (commandType == CMD_START) {
                if (isRunning) {
                    return true;
                }
            }

            if (commandType == CMD_STOP) {
                if (isStopped(p, (int) TIMEOUT_DELAY)) {
                    // give server a few secs to finish its shutdown, not responding
                    // does not necessarily mean its is still not running
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                    }
                    return true;
                }
            }
        }
    }

    public boolean isRunning(Process proc, int timeout, boolean checkResponse) {
        ////Process proc = getTomcatProcess();
        if (proc != null) {
            try {
                // process is stopped
                proc.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
                // process is running
                if (!checkResponse) {
                    return true;
                }
            }
        }
        if (checkResponse) {
            int port = Integer.parseInt(psconfig.getPort());
            return pingTomcat(port, timeout); // is tomcat responding?

        } else {
            return false; // cannot resolve the state

        }
    }

    private boolean isStopped(Process proc, int timeout) {
        if (proc != null) {
            try {
                proc.exitValue();
                // process is stopped
                return true;
            } catch (IllegalThreadStateException e) {
                // process is still running
                return false;
            }
        } else {
            int port = Integer.parseInt(psconfig.getPort());
            return !pingTomcat(port, timeout);
        }
    }

    /** Return true if a Tomcat server is running on the specifed port */
    public static boolean pingTomcat(int port, int timeout) {
        // checking whether a socket can be created is not reliable enough, see #47048
        Socket socket = new Socket();
        try {
            try {
                socket.connect(new InetSocketAddress("localhost", port), timeout); // NOI18N

                socket.setSoTimeout(timeout);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        // request
                        out.println("HEAD /netbeans-tomcat-status-test HTTP/1.1\nHost: localhost:" + port + "\n"); // NOI18N

                        // response
                        String text = in.readLine();
                        if (text == null || !text.startsWith("HTTP/")) { // NOI18N

                            return false; // not an http response

                        }
                        Map headerFileds = new HashMap();
                        while ((text = in.readLine()) != null && text.length() > 0) {
                            int colon = text.indexOf(':');
                            if (colon <= 0) {
                                return false; // not an http header

                            }
                            String name = text.substring(0, colon).trim();
                            String value = text.substring(colon + 1).trim();
                            List list = (List) headerFileds.get(name);
                            if (list == null) {
                                list = new ArrayList();
                                headerFileds.put(name, list);
                            }
                            list.add(value);
                        }
                        List/*<String>*/ server = (List/*<String>*/) headerFileds.get("Server"); // NIO18N

                        if (server != null) {
                            if (server.contains("Apache-Coyote/1.1")) { // NOI18N

                                if (headerFileds.get("X-Powered-By") == null) { // NIO18N
                                    // if X-Powered-By header is set, it is probably jboss

                                    return true;
                                }
                            } else if (server.contains("Sun-Java-System/Web-Services-Pack-1.4")) {  // NOI18N
                                // it is probably Tomcat with JWSDP installed

                                return true;
                            }
                        }
                        return false;
                    } finally {
                        in.close();
                    }
                } finally {
                    out.close();
                }
            } finally {
                socket.close();
            }
        } catch (IOException ioe) {
            return false;
        }
    }
}
