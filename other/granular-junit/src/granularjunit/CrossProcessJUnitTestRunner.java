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
 */
package granularjunit;

import granularjunit.shmem.Shmem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
/**
 *
 * @author Tim Boudreau
 */
public class CrossProcessJUnitTestRunner extends JUnitTestRunner {
    private ExecutorService e;
    public CrossProcessJUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean filtertrace, boolean haltOnFailure) {
        super(test, haltOnError, filtertrace, haltOnFailure, false);
        init();
    }

    /**
     * Constructor for fork=true or when the user hasn't specified a
     * classpath.
     */
    public CrossProcessJUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean filtertrace, boolean haltOnFailure,
                           boolean showOutput) {
        super(test, haltOnError, filtertrace, haltOnFailure, showOutput, null);
        init();
    }

    /**
     * Constructor to use when the user has specified a classpath.
     */
    public CrossProcessJUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean filtertrace, boolean haltOnFailure,
                           ClassLoader loader) {
        super(test, haltOnError, filtertrace, haltOnFailure, false, loader);
        init();
    }

    /**
     * Constructor to use when the user has specified a classpath.
     */
    public CrossProcessJUnitTestRunner(JUnitTest test, boolean haltOnError,
                           boolean filtertrace, boolean haltOnFailure,
                           boolean showOutput, ClassLoader loader) {
        super(test, haltOnError, filtertrace, haltOnFailure, showOutput,
                loader);
        init();
    }
    
    Shmem mem;
    private void init() {
//        e = java.util.concurrent.Executors.newFixedThreadPool(1);
        System.err.println("\n\nCREATING A RUNNER " + this + "\n\n");
        try {
            mem = new Shmem(System.out);
        } catch (IOException ioe) {
            throw new BuildException ("Could not create memory channel");
        }
    }
    
    private static boolean multipleTests = false;
    public static void main(String[] args) throws IOException {
        //Duplicate of JUnitTestRunner.main as we need to call
        //our launch() method, not the standard one, or we'll end up
        //with a JUnitTestRunner, not this class
        boolean haltError = false;
        boolean haltFail = false;
        boolean stackfilter = true;
        Properties props = new Properties();
        boolean showOut = false;

        if (args.length == 0) {
            System.err.println("required argument TestClassName missing");
            System.exit(ERRORS);
        }

        if (args[0].startsWith("testsfile=")) {
            multipleTests = true;
            args[0] = args[0].substring(10 /* "testsfile=".length() */);
        }

        for (int i = 1; i < args.length; i++) {
            System.err.println("ARGS[" + i + "]=" + args[i]);
            if (args[i].startsWith("haltOnError=")) {
                haltError = Project.toBoolean(args[i].substring(12));
            } else if (args[i].startsWith("haltOnFailure=")) {
                haltFail = Project.toBoolean(args[i].substring(14));
            } else if (args[i].startsWith("filtertrace=")) {
                stackfilter = Project.toBoolean(args[i].substring(12));
            } else if (args[i].startsWith("formatter=")) {
                try {
                    createAndStoreFormatter(args[i].substring(10));
                } catch (BuildException be) {
                    System.err.println(be.getMessage());
                    System.exit(ERRORS);
                }
            } else if (args[i].startsWith("propsfile=")) {
                FileInputStream in = new FileInputStream(args[i]
                                                         .substring(10));
                props.load(in);
                in.close();
            } else if (args[i].startsWith("showoutput=")) {
                showOut = Project.toBoolean(args[i].substring(11));
            }
        }
        // Add/overlay system properties on the properties from the Ant project
        Hashtable p = System.getProperties();
        for (Enumeration e = p.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            props.put(key, p.get(key));
        }

        int returnCode = SUCCESS;
        if (multipleTests) {
            try {
                java.io.BufferedReader reader =
                    new java.io.BufferedReader(new java.io.FileReader(args[0]));
                String testCaseName;
                int code = 0;
                boolean errorOccured = false;
                boolean failureOccured = false;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(line, ",");
                    testCaseName = st.nextToken();
                    JUnitTest t = new JUnitTest(testCaseName);
                    t.setTodir(new File(st.nextToken()));
                    t.setOutfile(st.nextToken());
                    code = launch(t, haltError, stackfilter, haltFail,
                                  showOut, props);
                    errorOccured = (code == ERRORS);
                    failureOccured = (code != SUCCESS);
                    if (errorOccured || failureOccured ) {
                        if ((errorOccured && haltError)
                            || (failureOccured && haltFail)) {
                            System.exit(code);
                        } else {
                            if (code > returnCode) {
                                returnCode = code;
                            }
                            System.out.println("TEST " + t.getName()
                                               + " FAILED");
                        }
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            returnCode = launch(new JUnitTest(args[0]), haltError,
                                stackfilter, haltFail, showOut, props);
        }

        System.err.println("Exit main");
        System.exit(returnCode);
    }
    boolean forked = false;
    private static int launch(JUnitTest t, boolean haltError,
                              boolean stackfilter, boolean haltFail,
                              boolean showOut, Properties props) {
        t.setProperties(props);
        CrossProcessJUnitTestRunner runner =
            new CrossProcessJUnitTestRunner(t, haltError, stackfilter, haltFail, showOut);
        
        runner.forked = true;
//        transferFormatters(runner, t);

        runner.run();
        return runner.getRetCode();
     }
    
    
    private static Vector fromCmdLine = new Vector();

    private static void createAndStoreFormatter(String line)
        throws BuildException {
        FormatterElement fe = new FormatterElement();
        int pos = line.indexOf(',');
        if (pos == -1) {
            fe.setClassname(line);
            fe.setUseFile(false);
        } else {
            fe.setClassname(line.substring(0, pos));
            fe.setUseFile(true);
            if (!multipleTests) {
//                fe.setOutfile(new File(line.substring(pos + 1)));
            } else {
                int fName = line.indexOf(IGNORED_FILE_NAME);
                if (fName > -1) {
                    fe.setExtension(line
                                    .substring(fName
                                               + IGNORED_FILE_NAME.length()));
                }
            }
        }
        fromCmdLine.addElement(fe);
    }
    

    public void startTest(Test t) {
        System.err.println("START TEST " + t);
        super.startTest(t);
        post (Commands.BEGIN_TEST, t.countTestCases() + ":" + t.toString());
    }

    public void endTest(Test test) {
        System.err.println("END TEST " + test);
        super.endTest(test);
        post (Commands.END_TEST, test.toString());
    }

    public void addFailure(Test test, Throwable t) {
        System.err.println("ADD FAILUER 1 " + test + " " + t.getMessage());
        super.addFailure(test, t);
        post (Commands.FAIL_TEST, t.getMessage());
    }

    public void addFailure(Test test, AssertionFailedError t) {
        System.err.println("ADD FAILURE " + test);
        super.addFailure(test, t);
        post (Commands.FAIL_TEST, t.getMessage());
    }

    public void addError(Test test, Throwable t) {
        System.err.println("POST ERROR " + t.getMessage());
        super.addError(test, t);
        post (Commands.ERR_TEST, t.getMessage());
    }
    
    public void addTimeout (Test t) {
        post (Commands.TIMEOUT, t.toString());
    }
    
    private void post (int cmd, String content) {
        try {
            mem.send(cmd, content);
        } catch (Exception ex) {
            throw new Error (ex);
        }
    }

    protected void handleOutput(String output) {
        super.handleOutput(output);
    }

    protected int handleInput(byte[] buffer, int offset, int length) throws IOException {
        int retValue;
        
        retValue = super.handleInput(buffer, offset, length);
        return retValue;
    }

    protected void handleErrorOutput(String output) {
        super.handleErrorOutput(output);
    }

    protected void handleFlush(String output) {
        super.handleFlush(output);
    }

    protected void handleErrorFlush(String output) {
        super.handleErrorFlush(output);
    }
    
    
}
