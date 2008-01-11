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
 * 
 * Contributor(s): Dirk Estievenart
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.remotefs.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import org.openide.util.Exceptions;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import org.netbeans.junit.NbTestCase;

/**
 * Should act as a FTP server. <br/>
 * <ul>
 * <li>Accepts only one client connection.</li>
 * <li>Implemented following RFC 959.  </li>
 * <li>not all commands are implemented, only the ones needed for FTPClient.</li>
 * <li>only STREAM mode and FILE structure is supported.</li>
 * <li>login with user "testUser" and password "foobar"</li>
 * </ul>
 */
public class MockFTPServer extends Thread {
    public static final int DEFAULT_SERVER_PORT = 40021;
    private int serverPort;
    private boolean quit = false;
    private String user = "unknown";
    private boolean loggedIn = false;
    /** FTP server starts in ASCII mode */
    private boolean transferTypeBinary = false;
    /** FTP client socket */
    private Socket client;
    /** FTP server starts in active mode (i.e. connects to port given by client)*/
    private boolean activeMode = true;
    /** server socket for communication */
    private ServerSocket commSS;
        /** socket for data transfer when client is in Active mode*/
    private Socket dataCS;
    /** server socket for data transfer client is in passive mode*/
    private ServerSocket dataSS;
    /** the current working directory */
    private File workingDir;

    /**
     * The root of the server's virtual filesystem.
     */
    private File root;

    /**
     * Default Constructor. <br/>
     * Server will use port 40021.
     */
    public MockFTPServer() {
        this(DEFAULT_SERVER_PORT);
    }
    /**
     * Constructor accepting port number to use.<br/>
     * Note that for port numbers &lt; 1024 on some systems superuser rights are required.
     * 
     * @param port
     */
    public MockFTPServer(int port) {
        super("MockFTPServer");
        serverPort = port;

    }

    /**
     * Starts server. Server accepts only one connection.
     */
    @Override
    public void run() {
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            commSS = new ServerSocket(serverPort);
            commSS.setSoTimeout(10000);
            System.out.println("FTP server: started.");
            client = commSS.accept();
            System.out.println("FTP server: received request.");
            pw = new PrintWriter(client.getOutputStream());
            pw.println("220 test FTP server ready");
            System.out.println("220 test FTP server ready");
            pw.flush();
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = null;
            while (!quit && (line = br.readLine()) != null) {
                System.out.println("CLIENT : "+line);
                String result = handleCommand(line);
                System.out.println("SERVER : "+result);
                pw.println(result);
                pw.flush();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (commSS != null) {
                try {
                    commSS.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     * Process the request of the client
     * @param line
     * @return
     */
    private String handleCommand(String line) {
        String[] sa = line.split(" ");
        if (sa.length > 0) {
            String cmd = sa[0].toUpperCase();
            // just store the user given
            if (cmd.equals("USER")) {
                if (sa.length == 2) {
                    user = sa[1];
                }
                return "331 Password please.";
            }
            // check if pass == foobar and user == testUser
            if (cmd.equals("PASS")) {
                if (!"testUser".equals(user)) {
                    return "503 Login with USER first.";
                }
                if (sa.length == 2 && sa[1].equals("foobar")) {
                    loggedIn = true;
                    return "230 Hi! Thanks for testing.";
                }
                return "530 Don't know you! Go away!";
            }
            // Let's pretend we're unix
            if (cmd.equals("SYST")) {
                return "215 UNIX of course! What else?";
            }
            // ASCII Non-print or Image (binary) are the only ones accepted.
            if (cmd.equals("TYPE")) {
                if (sa.length >= 2) {
                    if (sa[1].equals("A")) {
                        if (sa.length == 2 || sa[2].equals("N")) {
                            transferTypeBinary = false;
                            return "200 Type set to ASCII";
                        } else {
                            return "504 Form must be N";
                        }
                    }
                    if (sa[1].equals("E")) {
                        return "504 Type E not implemented";
                    }
                    if (sa[1].equals("I")) {
                        transferTypeBinary = true;
                        return "200 Type set to BINARY";
                    }
                    if (sa[1].equals("L")) {
                        return "504 Type L not implemented";
                    }
                }
            }
            if (cmd.equals("QUIT")) {
              //  quit = true;
                return "221 CU soon.";
            }
            if (cmd.equals("PASV")) {

            }
            if (cmd.equals("RETR")) {

            }
            if (cmd.equals("STOR")) {

            }
            if (cmd.equals("RNFR")) {

            }
            if (cmd.equals("RNTO")) {

            }
            if (cmd.equals("DELE")) {

            }
            if (cmd.equals("RMD")) {

            }
            if (cmd.equals("PWD")) {

            }
            if (cmd.equals("LIST")) {

            }
            if (cmd.equals("PORT")) {

            }
            if (cmd.equals("CWD")) {
                if(!loggedIn){
                    return "530 Please login with USER and PASS.";
                }
                //if no argument, i.e. a folder path, is given, we return succesful.
                if(sa.length == 2){
                    File cwd = new File(getRoot(), sa[1]);
                    if(cwd.exists() && cwd.isDirectory()){
                        workingDir = cwd;
                        return "250 CWD command succesful.";
                    }else{
                         return "550 "+sa[1]+": No such file or directory.";
                    }
                }else{
                    return "250 CWD command succesful.";
                }
            }
//            if (cmd.equals("NLIST")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("SITE")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("STAT")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("STOU")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("APPE")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("ACCT")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("CDUP")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("REST")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("ABOR")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("STRU")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("MODE")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("HELP")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("NOOP")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("SMNT")) {
//            // not used by FTPClient
//            }
//            if (cmd.equals("REIN")) {
//            // not used by FTPClient
//            }
            
        }
        return "504 " + line + " : Huh? Don't understand you!(Maybe command is not implemented)";
    }

    /**
     * Creates a folder structure to mimic the server's filesystem.<br/>
     * Folders are created in the working directory for the running NbTestCase.
     * <p>
     * /<br/>
     * |_foo<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;|_bar<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|_ nb.txt<br/>
     * </p>
     * @param test
     */
    public void makeTestDir(NbTestCase test) throws IOException {
            File workDir = test.getWorkDir();
            root = new File(workDir, "server");
            getRoot().mkdir();            
            File folders = new File(getRoot(), "foo/bar");
            folders.mkdirs();
            File file = new File(folders, "nb.txt");
            PrintWriter p = new PrintWriter(new FileOutputStream(file));
            p.println("NetBeans - The Only IDE You Need!");
            p.println("=================================");
            p.println("The NetBeans IDE is a free, open-source Integrated Development Environment for software developers. ");
            p.println("You get all the tools you need to create professional desktop, enterprise, web and mobile applications,");
            p.println("in Java, C/C++ and even Ruby. ");
            p.println("The IDE runs on many platforms including Windows, Linux, Mac OS X and Solaris; ");
            p.println("it is easy to install and use straight out of the box.");
            p.flush();
            p.close();
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public File getRoot() {
        return root;
    }
}
