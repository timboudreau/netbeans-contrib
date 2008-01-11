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
package org.netbeans.modules.remotefs.ftpclient;

import java.io.File;
import java.io.IOException;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.remotefs.core.RemoteFileName;
import org.netbeans.modules.remotefs.testutils.MockFTPServer;
import org.openide.util.Exceptions;

/**
 * A Test based on NbTestCase. It is a NetBeans extension to JUnit TestCase
 * which among othres allows to compare files via assertFile methods, create
 * working directories for testcases, write to log files, compare log files
 * against reference (golden) files, etc.
 * 
 * More details here http://xtest.netbeans.org/NbJUnit/NbJUnit-overview.html.
 * 
 * @author dirke
 */
public class FTPClientTest extends NbTestCase {

    private FTPClient client;
    private MockFTPServer mockServer;

    /** Default constructor.
     * @param testName name of particular test case
     */
    public FTPClientTest(String testName) {
        super(testName);
    }

    /** Creates suite from particular test cases. You can define order of testcases here. */
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new FTPClientTest("testConnect"));
        suite.addTest(new FTPClientTest("testTransfer"));
        return suite;
    }

    /* Method allowing test execution directly from the IDE. */
    public static void main(java.lang.String[] args) {
        // run whole suite
        junit.textui.TestRunner.run(suite());
        // run only selected test case
        //junit.textui.TestRunner.run(new FTPClientTest("test1"));
    }

    /** Called before every test case. */
    @Override
    public void setUp() {
        try {
            System.out.println("########  " + getName() + "  #######");
            mockServer = new MockFTPServer();
            mockServer.start();
            
            mockServer.makeTestDir(this);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail("Cannot initialise test folders!");
        }
    }

    /** Called after every test case. */
    @Override
    public void tearDown() {
        
    }

    /** Test Connect. 
     * Attempts to connect to the FTP server using a valid user and pwd.
     * Note that the FTPClient automatically logs in and changes to binary.
     * TODO: Test also if the logging in FTPClient works...
     */
    public void testConnect() {
        try {
            client = new FTPClient(new FTPLogInfo("localhost", MockFTPServer.DEFAULT_SERVER_PORT, "testUser", "foobar"));
            client.connect();
            assertTrue(client.isConnected());
            assertTrue(client.isUnixType());
            assertFalse(client.isPassiveMode());
            client.disconnect();
            assertFalse(client.isConnected());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
    
    public void testTransfer() {
        try {
            File workDir = this.getWorkDir();
            client = new FTPClient(new FTPLogInfo("localhost", MockFTPServer.DEFAULT_SERVER_PORT, "testUser", "foobar"));
            client.connect();
            assertTrue(client.isConnected());
            client.cwd("foo/bar");
            assertEquals("\"CWD foo/bar\" is not correct!",mockServer.getWorkingDir().getPath(), mockServer.getRoot().getPath()+File.separator+"foo"+File.separator+"bar");
            client.cwd("/foo/bar");
            assertEquals("\"CWD foo/bar\" is not correct!",mockServer.getWorkingDir().getPath(), mockServer.getRoot().getPath()+File.separator+"foo"+File.separator+"bar");
            File target = new File(this.getWorkDir(), "nb.txt");
//            RemoteFileName source = new FTPFileName(mockServer.getRoot().getPath()+"");
//            client.get("nb.txt", "where");
//            client.delete("");
//            client.list(directory);
//            client.put(what, where);
//            client.rmdir(path);
            client.disconnect();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
    
    
}
