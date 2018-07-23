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
 * John Platts
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. Portions Copyrighted 2009 John Platts. All Rights Reserved.
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
package org.netbeans.modules.portalpack.servers.core.impl.j2eeservers.jboss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.j2ee.deployment.plugins.spi.FindJSPServlet;
import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import org.netbeans.modules.portalpack.servers.core.api.PSStartServerInf;
import org.netbeans.modules.portalpack.servers.core.common.LogManager;
import org.netbeans.modules.portalpack.servers.core.util.Base64Routines;
import org.netbeans.modules.portalpack.servers.core.util.PSConfigObject;
import org.openide.execution.NbProcessDescriptor;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

/**
 *
 * @author Kirill Sorokin
 */
public class JBStartServer extends PSStartServerInf implements JBConstant {

    private PSConfigObject psconfig;
    private PSDeploymentManager dm;

    public static final String RUN_SH = "run.sh"; //NOI18N
    public static final String RUN_BAT = "run.bat"; //NOI18N

    public static final String JAVA_EXECUTABLE =
            "bin" + File.separator + "java"; //NOI18N

    private static int CMD_START = 0;
    private static int CMD_STOP = 1;

    /** For how long should we keep trying to get response from the server. */
    private static final long TIMEOUT_DELAY = 180000;

    /** Creates a new instance of TomcatStartServer */
    public JBStartServer(PSDeploymentManager dm) {
        this.dm = dm;
        this.psconfig = dm.getPSConfig();
    }

    @Override
    public void doStartServer(String[] env) throws Exception {
        startJBossServer(env,false);
    }

    private static final String JBOSS_SHUTDOWN_URL =
            "/jmx-console/HtmlAdaptor";
    private static final String JBOSS_SHUTDOWN_PARAMS =
            "action=invokeOpByName" +
            "&name=jboss.system%3Atype%3DServer" +
            "&methodName=shutdown";
    @Override
    public void doStopServer(String[] env) throws Exception {
        shutdownJBossServer();
    }

    public int shutdownJBossServer() throws Exception {
        sendShutdownMessage(null, null);

        if(hasStopCommandSucceeded()) {
            return 1;
        }

        return 0;
    }

    private static final String HTTP_RESPONSE_PATTERN_STRING =
            "^HTTP/1.1 ([1-5][0-9][0-9]) (.*)$";
    private static final Pattern HTTP_RESPONSE_PATTERN =
            Pattern.compile(HTTP_RESPONSE_PATTERN_STRING);

    public void sendShutdownMessage(String userName,
            String password) throws Exception {
        String authorization = null;
        if(userName != null) {
            StringBuilder sbUserNamePassword =
                    new StringBuilder();
            sbUserNamePassword.append(userName);
            sbUserNamePassword.append(':');
            sbUserNamePassword.append(password);

            ByteBuffer passwordByteBuf =
                    Charset.forName("ISO-8859-1").encode(
                    CharBuffer.wrap(sbUserNamePassword));

            authorization = "Basic " +
                    Base64Routines.base64Encode(passwordByteBuf);
        }

        int serverPort;
        try {
            String portStr = psconfig.getPort();

            if(portStr == null ||
                    (portStr = portStr.trim()).length() < 1) {
                serverPort = 80;
            } else {
                serverPort = Integer.parseInt(portStr);

                if(serverPort < 0) {
                    serverPort = 80;
                }
            }
        } catch(NumberFormatException e) {
            serverPort = 80;
        }

        SocketChannel socketChannel =
                SocketChannel.open();
        try {
            socketChannel.configureBlocking(true);

            Socket socket = socketChannel.socket();

            socket.connect(new InetSocketAddress("localhost", serverPort),
                    20000);
            socket.setSoTimeout(20000);

            Writer socketWriter =
                    Channels.newWriter(socketChannel, "ISO-8859-1");

            socketWriter.write("POST " + JBOSS_SHUTDOWN_URL +
                    " HTTP/1.1\r\n");
            socketWriter.write("Host: localhost" +
                    ((serverPort != 80) ?
                        (":" + Integer.toString(serverPort)) : "") + "\r\n");
            socketWriter.write("Content-Type: application/x-www-form-urlencoded;charset=ISO-8859-1\r\n");
            socketWriter.write("Content-Length: " + JBOSS_SHUTDOWN_PARAMS.length() + "\r\n");
            if(authorization != null) {
                socketWriter.write("Authorization: " + authorization + "\r\n");
            }
            socketWriter.write("Connection: close\r\n");
            socketWriter.write("\r\n");
            socketWriter.write(JBOSS_SHUTDOWN_PARAMS);

            socketWriter.flush();

            BufferedReader socketReader =
                    new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String statusLine = socketReader.readLine();
            Matcher statusLineMatcher;
            if(statusLine == null ||
                    !(statusLineMatcher =
                    HTTP_RESPONSE_PATTERN.matcher(statusLine)).matches() ||
                    statusLineMatcher.groupCount() < 2) {
                throw new JBShutdownException("A response was not returned by " +
                        "the HTTP server.");
            }

            int statusCode;
            try {
                statusCode = Integer.parseInt(statusLineMatcher.group(1));
            } catch(NumberFormatException e) {
                statusCode = -1;
            }

            if(statusCode == 200) {
                return;
            }

            if(statusCode == 401) {
                if(userName != null) {
                    throw new JBShutdownException("An invalid username or " +
                            "password was specified.");
                }

                String adminUser = psconfig.getAdminUser();
                String adminPassword = psconfig.getAdminPassWord();

                if(adminUser != null &&
                        (adminUser = adminUser.trim()).length() > 1) {
                    if(adminPassword == null) {
                        adminPassword = "";
                    }

                    sendShutdownMessage(adminUser, adminPassword);
                    return;
                }
            }

            throw new JBShutdownException("An response other than a 200 OK " +
                    "response was returned by the HTTP server.");
        } finally {
            socketChannel.close();
        }
    }

    @Override
    public void doStartDebug(String[] env) throws Exception {
        startJBossServer(env,true);
    }

    @Override
    public void doStopDebug(String[] env) throws Exception {
        doStopServer(env);
    }

    public int startJBossServer(String[] env, boolean debug) throws Exception {
        final String serverName = psconfig.getProperty(JBConstant.SERVER);
        final String jbossRootLocation = psconfig.getProperty(JBConstant.ROOT_DIR);
        final String javaHome = psconfig.getProperty(JBConstant.JAVA_HOME);
        File jbossRoot = (new File(jbossRootLocation)).getAbsoluteFile();
        File jbossBin = new File(jbossRoot, "bin");

        String runScript =
                (Utilities.isWindows()) ? RUN_BAT : RUN_SH;

        File runScriptFile = new File(jbossBin, runScript);

        if(!runScriptFile.isFile()) {
            throw new Exception("The " + runScript + " script file must exist " +
                    "in the JBoss bin directory.");
        }

        StringBuilder sbArguments = new StringBuilder();
        if("all".equals(serverName)) {
            sbArguments.append("-b 127.0.0.1 ");
        }
        sbArguments.append("-c ").append(serverName);

		StringBuilder sbJavaOpts = new StringBuilder();

		if (env != null) {
			for(String e:env) {
				if(e.startsWith("JAVA_OPTS=")) {
					sbJavaOpts.append(e.replace("\"", ""));
					sbJavaOpts.append(" ");
				}
			}
		}

		if(sbJavaOpts.length() == 0)
			sbJavaOpts.append("JAVA_OPTS=");

        if(debug) {
            sbJavaOpts.append("-Xdebug -Xrunjdwp:transport=dt_socket,address=" +
                    Integer.toString(getDebugPort()) + ",server=y,suspend=n");
        }

		System.out.println("************* " + sbJavaOpts.toString());

        NbProcessDescriptor nbProcessDescriptor =
                new NbProcessDescriptor(runScriptFile.getAbsolutePath(),
                sbArguments.toString().trim());

        Process process = nbProcessDescriptor.exec(null,
                new String[] { sbJavaOpts.toString(),
                ("JAVA_HOME=" + javaHome),
                "JAVA="},
                true, jbossBin);

        LogManager manager = new LogManager(dm);
        manager.openServerLog(process, nbProcessDescriptor.getProcessName());

        if(hasStartCommandSucceeded(process)) {
            return 1;
        }

        return 0;
    }

    public static final int DEFAULT_DEBUG_PORT = 8787;

    @Override
    public int getDebugPort() {
        try {
            int debugPort =
                    Integer.parseInt(psconfig.getProperty(JBConstant.DEBUG_PORT));
            return debugPort;
        } catch(NumberFormatException e) {
        }
        return DEFAULT_DEBUG_PORT;
    }

    @Override
    public FindJSPServlet getFindJSPServlet(PSDeploymentManager dm) {
        return new JBFindJSPServlet(dm);
    }

    /**
     * Try to get response from the server, whether the START/STOP command has
     * succeeded.
     *
     * @return <code>true</code> if START/STOP command completion was verified,
     *         <code>false</code> if time-out ran out.
     */
    private boolean hasStartCommandSucceeded(Process p) {
        ///long timeout = System.currentTimeMillis() + TIMEOUT_DELAY;
        while (true) {
            boolean isRunning = isRunning(p, (int) TIMEOUT_DELAY, true);

            if (isRunning) {
                return true;
            }
        }
    }

    private boolean hasStopCommandSucceeded() {
        while(true) {
            if(isStopped((int)TIMEOUT_DELAY)) {
                return true;
            }

            // give server a few secs to finish its shutdown, not responding
            // does not necessarily mean its is still not running
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }
        }
    }

    private boolean isStopped(int timeout) {
        return !isReallyRunning(timeout);
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
            return isReallyRunning(timeout);
        } else {
            return false; // cannot resolve the state

        }
    }

    public boolean isReallyRunning(int timeout) {
        return safeTrueTest(new IsRunningSafeTrueTest(), timeout);
    }

    private class IsRunningSafeTrueTest extends SafeTrueTest {
        @Override
        public void run() {
            int serverPort;
            try {
                String portStr = psconfig.getPort();

                if(portStr == null ||
                        (portStr = portStr.trim()).length() < 1) {
                    serverPort = 80;
                } else {
                    serverPort = Integer.parseInt(portStr);

                    if(serverPort < 0) {
                        serverPort = 80;
                    }
                }
            } catch(NumberFormatException e) {
                serverPort = 80;
            }

            result = pingJBoss(serverPort, 20000);
        }
    }
    public static boolean pingJBoss(int port, int timeout) {
        // checking whether a socket can be created is not reliable enough, see #47048
        SocketChannel socketChannel = null;
        try {
            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
                
                Socket socket = socketChannel.socket();

                socket.connect(new InetSocketAddress("localhost", port),
                        timeout); // NOI18N

                socket.setSoTimeout(timeout);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    try {
                        // request
                        out.println("HEAD /netbeans-tomcat-status-test HTTP/1.1\r\n" +
                                "Host: localhost:" + port + "\r\n"); // NOI18N

                        // response
                        String text = in.readLine();
                        if (text == null || !text.startsWith("HTTP/")) { // NOI18N

                            return false; // not an http response

                        }
                        Map headerFields = new HashMap();
                        while ((text = in.readLine()) != null && text.length() > 0) {
                            int colon = text.indexOf(':');
                            if (colon <= 0) {
                                return false; // not an http header

                            }
                            String name = text.substring(0, colon).trim();
                            String value = text.substring(colon + 1).trim();
                            List list = (List) headerFields.get(name);
                            if (list == null) {
                                list = new ArrayList();
                                headerFields.put(name, list);
                            }
                            list.add(value);
                        }
                        List/*<String>*/ server = (List/*<String>*/) headerFields.get("Server"); // NIO18N

                        if (server != null) {
                            if (server.contains("Apache-Coyote/1.1")) { // NOI18N

                                List xPoweredBy = (List)headerFields.get("X-Powered-By");
                                if(xPoweredBy != null) {
                                    boolean poweredByJBoss = false;

                                    for(Object o : xPoweredBy) {
                                        if(o == null) {
                                            continue;
                                        }

                                        String s = o.toString();
                                        if(s == null) {
                                            continue;
                                        }

                                        if(s.contains("JBoss")) {
                                            poweredByJBoss = true;
                                            break;
                                        }
                                    }

                                    if(poweredByJBoss) {
                                        return true;
                                    }
                                } else {
                                    return true;
                                }
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
                if(socketChannel != null) {
                    socketChannel.close();
                }
            }
        } catch (IOException ioe) {
            return false;
        }
    }

    /** Safe true/false test useful. */
    private abstract static class SafeTrueTest implements Runnable {
        protected boolean result = false;

        public abstract void run();

        public final boolean result() {
            return result;
        }
    };

    /** Return the result of the test or false if the given time-out ran out. */
    private boolean safeTrueTest(SafeTrueTest test, int timeout) {
        try {
            new RequestProcessor().post(test).waitFinished(timeout);
        } catch (InterruptedException ie) {
            // no op
        }
        return test.result();
    }
}
