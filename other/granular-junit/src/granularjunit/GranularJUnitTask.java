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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.tools.ant.taskdefs.optional.junit.Enumerations;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask.ForkMode;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.SummaryJUnitResultFormatter;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.LoaderUtils;

/**
 *
 * @author Tim Boudreau
 */
public class GranularJUnitTask extends JUnitTask {
    private Path antRuntimeClasses;
    /** Creates a new instance of GranularJUnitTask */
    public GranularJUnitTask() throws Exception {
        System.out.println("\n\nCREATING A TASK\n\n");
        antRuntimeClasses = new Path(getProject());
    }
    
    public void init() {
        super.init();
        addClasspathEntry("granularjunit/GranularJUnitTestRunner.class");
        addClasspathEntry("/junit/framework/TestCase.class");
        addClasspathEntry("/org/apache/tools/ant/launch/AntMain.class");
        addClasspathEntry("/org/apache/tools/ant/Task.class");
        addClasspathEntry("/org/apache/tools/ant/taskdefs/optional/junit/JUnitTestRunner.class");
        addClasspathEntry("granularjunit/CrossProcessJUnitTestRunner.class");
        addClasspathEntry("granularjunit/GranularJUnitTask.class");
        getCommandline()
            .setClassname("granularjunit.CrossProcessJUnitTestRunner");
        System.err.println("CMD LINE " + getCommandline());
    }
    
    private File dir;
    public void setDir(File dir) {
        super.setDir (dir);
        this.dir = dir;
    }

    private boolean summary;
    public void setPrintsummary(SummaryAttribute value) {
        super.setPrintsummary (value);
        summary = value.asBoolean();
    }
    private boolean newEnvironment;
    public void setNewenvironment(boolean newenv) {
        super.setNewenvironment(newenv);
        newEnvironment = newenv;
    }
    
    private File tmpDir;
    private File createTempPropertiesFile(String prefix) {
        
        File propsFile =
            FileUtils.newFileUtils().createTempFile(prefix, ".properties",
                                                    tmpDir != null ? tmpDir : getProject().getBaseDir());
        propsFile.deleteOnExit();
        return propsFile;
    }

    protected void execute(JUnitTest arg) throws BuildException {
        //Only supporting the forked version
        //Duplicates logic from JUnitTask - mainly because executeAsForked 
        //forces the class passed on the commandline to be 
        //JUnitTestRunner no matter what we want.
        getCommandline()
            .setClassname("granularjunit.CrossProcessJUnitTestRunner");
        ExecuteWatchdog watchdog = createWatchdog();
        System.err.println("will fork");
        JUnitTest test = (JUnitTest) arg.clone();
        int exitValue = executeAsForked(test, watchdog, null);
        boolean wasKilled = false;
        // null watchdog means no timeout, you'd better not check with null
        if (watchdog != null) {
            wasKilled = watchdog.killedProcess();
        }
        actOnTestResult(exitValue, wasKilled, test, "Test " + test.getName());
    }
    
    private boolean includeAntRuntime = false;
    public void setIncludeantruntime(boolean b) {
        super.setIncludeantruntime(b);
        includeAntRuntime = b;
    }
    
    private static final int STRING_BUFFER_SIZE = 128;
    private int executeAsForked(JUnitTest test, ExecuteWatchdog watchdog, 
                                File casesFile)
        throws BuildException {

        System.err.println("EXECUTE FORKED");
        CommandlineJava cmd = null;
        try {
            cmd = (CommandlineJava) getCommandline().clone();
        } catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
        System.err.println("CMD IS " + cmd);

        cmd.setClassname("granularjunit.CrossProcessJUnitTestRunner");
        if (casesFile == null) {
            cmd.createArgument().setValue(test.getName());
        } else {
            log("Running multiple tests in the same VM", Project.MSG_VERBOSE);
            cmd.createArgument().setValue("testsfile=" + casesFile);
        }
        
        cmd.createArgument().setValue("filtertrace=" + test.getFiltertrace());
        cmd.createArgument().setValue("haltOnError=" + test.getHaltonerror());
        cmd.createArgument().setValue("haltOnFailure="
                                      + test.getHaltonfailure());
        if (includeAntRuntime) {
            Vector v = Execute.getProcEnvironment();
            Enumeration e = v.elements();
            while (e.hasMoreElements()) {
                String s = (String) e.nextElement();
                if (s.startsWith("CLASSPATH=")) {
                    cmd.createClasspath(getProject()).createPath()
                        .append(new Path(getProject(),
                                         s.substring(10 // "CLASSPATH=".length()
                                                     )));
                }
            }
            log("Implicitly adding " + antRuntimeClasses + " to CLASSPATH",
                Project.MSG_VERBOSE);
            cmd.createClasspath(getProject()).createPath()
                .append(antRuntimeClasses);
        }

        if (summary) {
            log("Running " + test.getName(), Project.MSG_INFO);
            cmd.createArgument()
                .setValue("formatter"
                          + "=org.apache.tools.ant.taskdefs.optional.junit.SummaryJUnitResultFormatter");
        }

        cmd.createArgument().setValue("showoutput="
                                      + String.valueOf(showOutput));

//        StringBuffer formatterArg = new StringBuffer(STRING_BUFFER_SIZE);
//        final FormatterElement[] feArray = mergeFormatters(test);
//        for (int i = 0; i < feArray.length; i++) {
//            FormatterElement fe = feArray[i];
//            if (fe.shouldUse(this)) {
//                formatterArg.append("formatter=");
//                formatterArg.append(fe.getClassname());
//                File outFile = getOutput(fe, test);
//                if (outFile != null) {
//                    formatterArg.append(",");
//                    formatterArg.append(outFile);
//                }
//                cmd.createArgument().setValue(formatterArg.toString());
//                formatterArg = new StringBuffer();
//            }
//        }
        String formatterArg = "formatter=org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter";
        cmd.createArgument().setValue (formatterArg);


        File propsFile = createTempPropertiesFile("junit");
        cmd.createArgument().setValue("propsfile="
                                      + propsFile.getAbsolutePath());
        Hashtable p = getProject().getProperties();
        Properties props = new Properties();
        for (Enumeration e = p.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            props.put(key, p.get(key));
        }
        try {
            FileOutputStream outstream = new FileOutputStream(propsFile);
            props.store(outstream, "Ant JUnitTask generated properties file");
            outstream.close();
        } catch (java.io.IOException e) {
            propsFile.delete();
            throw new BuildException("Error creating temporary properties "
                                     + "file.", e, getLocation());
        }

        Execute execute = new Execute(new LogStreamHandler(this,
                                                           Project.MSG_INFO,
                                                           Project.MSG_WARN),
                                      watchdog);
        execute.setCommandline(cmd.getCommandline());
        execute.setAntRun(getProject());
        if (dir != null) {
            execute.setWorkingDirectory(dir);
        }

//        String[] environment = env.getVariables();
//        if (environment != null) {
//            for (int i = 0; i < environment.length; i++) {
//                log("Setting environment variable: " + environment[i],
//                    Project.MSG_VERBOSE);
//            }
//        }
//        execute.setNewenvironment(newEnvironment);
//        execute.setEnvironment(environment);

        log(cmd.describeCommand(), Project.MSG_VERBOSE);
        int retVal;
        try {
            retVal = execute.execute();
        } catch (IOException e) {
            throw new BuildException("Process fork failed.", e, getLocation());
        } finally {
            if (watchdog != null && watchdog.killedProcess()) {
//                logTimeout(feArray, test);
            }

            if (!propsFile.delete()) {
                throw new BuildException("Could not delete temporary "
                                         + "properties file.");
            }
        }
        return retVal;
    }
    
    private boolean showOutput = true;
    public void setShowOutput(boolean showOutput) {
        super.setShowOutput(showOutput);
        this.showOutput = showOutput;
    }
}
