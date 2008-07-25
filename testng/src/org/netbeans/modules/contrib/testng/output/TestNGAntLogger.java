/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.contrib.testng.output;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.modules.contrib.testng.output.antutils.AntProject;
import org.netbeans.modules.contrib.testng.output.antutils.TestCounter;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 * Ant logger interested in task &quot;junit&quot;,
 * dispatching events to instances of the {@link TestNGOutputReader} class.
 * There is one <code>TestNGOutputReader</code> instance created per each
 * Ant session.
 *
 * @see  TestNGOutputReader
 * @see  Report
 * @author  Marian Petras
 * @author  Lukas Jungmann
 */
//-Dtestng.show.stack.frames=true
public final class TestNGAntLogger extends AntLogger {

    private static final Logger LOGGER = Logger.getLogger(TestNGAntLogger.class.getName());
    /** levels of interest for logging (info, warning, error, ...) */
    private static final int[] LEVELS_OF_INTEREST = {
        AntEvent.LOG_INFO,
        AntEvent.LOG_WARN, //test failures
        AntEvent.LOG_VERBOSE,
        AntEvent.LOG_ERR
    };
    public static final String TASK_JAVA = "java";                      //NOI18N
    public static final String TASK_TESTNG = "testng";                    //NOI18N
    private static final String[] INTERESTING_TASKS = {TASK_JAVA, TASK_TESTNG};
    private static final String ANT_TEST_RUNNER_CLASS_NAME =
            "org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner";//NOI18N
    private static final String XML_FORMATTER_CLASS_NAME =
            "org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter";//NOI18N

    /**
     * Default constructor for lookup
     */
    public TestNGAntLogger() {
    }

    @Override
    public boolean interestedInSession(AntSession session) {
        return true;
    }

    @Override
    public String[] interestedInTargets(AntSession session) {
        return AntLogger.ALL_TARGETS;
    }

    @Override
    public String[] interestedInTasks(AntSession session) {
        return INTERESTING_TASKS;
    }

    /**
     * Detects type of the Ant task currently running.
     *
     * @param  event  event produced by the currently running Ant session
     * @return  {@code TaskType.TEST_TASK} if the task is a JUnit test task,
     *          {@code TaskType.DEBUGGING_TEST_TASK} if the task is a JUnit
     *             test task running in debugging mode,
     *          {@code TaskType.OTHER_TASK} if the task is not a JUnit test
     *             task;
     *          or {@code null} if no Ant task is currently running
     */
    private static TaskType detectTaskType(AntEvent event) {
        final String taskName = event.getTaskName();
        LOGGER.info("in dTT with task: " + taskName);

        if (taskName == null) {
            LOGGER.info("re: null");
            return null;
        }

        if (taskName.equals(TASK_TESTNG)) {
            LOGGER.info("re: TEST_TASK");
            return TaskType.TEST_TASK;
        }

        if (taskName.equals(TASK_JAVA)) {
            TaskStructure taskStructure = event.getTaskStructure();

            String className = taskStructure.getAttribute("classname"); //NOI18N
            if (className == null) {
                return TaskType.OTHER_TASK;
            }

            className = event.evaluate(className);
            if (className.equals("junit.textui.TestRunner") //NOI18N
                    || className.startsWith("org.junit.runner.") //NOI18N
                    || className.equals(
                    "org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner")) {  //NOI18N
                TaskStructure[] nestedElems = taskStructure.getChildren();
                for (TaskStructure ts : nestedElems) {
                    if (ts.getName().equals("jvmarg")) {                //NOI18N
                        String value = ts.getAttribute("value");        //NOI18N
                        if ((value != null) && event.evaluate(value).equals("-Xdebug")) {                //NOI18N
                            LOGGER.info("re: DEBUGGING_TEST_TASK");
                            return TaskType.DEBUGGING_TEST_TASK;
                        }
                    }
                }
                LOGGER.info("re: TEST_TASK");
                return TaskType.TEST_TASK;
            }

            LOGGER.info("re: OTHER_TASK");
            return TaskType.OTHER_TASK;
        }

        assert false : "Unhandled task name";                           //NOI18N
        return TaskType.OTHER_TASK;
    }

    /**
     * Tells whether the given task type is a test task type or not.
     *
     * @param  taskType  taskType to be checked; may be {@code null}
     * @return  {@code true} if the given task type marks a test task;
     *          {@code false} otherwise
     */
    private static boolean isTestTaskType(TaskType taskType) {
        return (taskType != null) && (taskType != TaskType.OTHER_TASK);
    }

    @Override
    public boolean interestedInScript(File script, AntSession session) {
        return true;
    }

    @Override
    public int[] interestedInLogLevels(AntSession session) {
        return LEVELS_OF_INTEREST;
    }

    /**
     */
    @Override
    public void messageLogged(final AntEvent event) {
        if (isTestTaskRunning(event)) {
            final String msg = event.getMessage();
            AntSession session = event.getSession();
            int messageLevel = event.getLogLevel();
            int sessionLevel = session.getVerbosity();
            AntSessionInfo data = getSessionData(session);
            assert msg != null;

            // Look for classpaths.
            if (messageLevel == AntEvent.LOG_VERBOSE) {
                Matcher m2 = CLASSPATH_ARGS.matcher(msg);
                if (m2.find()) {
                    String cp = m2.group(1);
                    data.setClasspath(cp);
                }
                // XXX should also probably clear classpath when taskFinished called
                m2 = JAVA_EXECUTABLE.matcher(msg);
                if (m2.find()) {
                    String executable = m2.group(1);
                    ClassPath platformSources = findPlatformSources(executable);
                    if (platformSources != null) {
                        data.setPlatformSources(platformSources);
                    }
                }
            }


            //handle stacktraces here:
            //Matcher m = RegexpUtils.getInstance().getTestcaseExceptionPattern().matcher(msg);
            Matcher m = STACK_TRACE.matcher(msg);

            //LOGGER.info("Do I match \"" + msg + "\": " + m.matches());

            if (m.matches()) {
                // We have a stack trace.
                String pkg = m.group(2);
                String filename = m.group(3);
                String resource = pkg.replace('.', '/') + filename;
                int lineNumber = Integer.parseInt(m.group(4));
                // Check to see if the class is listed in our per-task sourcepath.
                // XXX could also look for -Xbootclasspath etc., but probably less important
                Iterator it = getCurrentSourceRootsForClasspath(data).iterator();
                while (it.hasNext()) {
                    FileObject root = (FileObject) it.next();
                    // XXX this is apparently pretty expensive; try to use java.io.File instead
                    FileObject source = root.getFileObject(resource);
                    if (source != null) {
                        // Got it!
                        hyperlink(msg, session, event, source, messageLevel, sessionLevel, data, lineNumber);
                        break;
                    }
                }
                // Also check global sourcepath (sources of open projects, and sources
                // corresponding to compile or boot classpaths of open projects).
                // Fallback in case a JAR file is copied to an unknown location, etc.
                // In this case we can't be sure that this source file really matches
                // the .class used in the stack trace, but it is a good guess.
                if (!event.isConsumed()) {
                    FileObject source = GlobalPathRegistry.getDefault().findResource(resource);
                    if (source != null) {
                        hyperlink(msg, session, event, source, messageLevel, sessionLevel, data, lineNumber);
                    }
                }
            }
            if (event.getLogLevel() != AntEvent.LOG_VERBOSE) {
                getOutputReader(event).messageLogged(event);
            } else {
                /* verbose messages are logged no matter which task produced them */
                getOutputReader(event).verboseMessageLogged(event);
            }
        }
    }
    
    /**
     * Regexp matching one line (not the first) of a stack trace.
     * Captured groups:
     * <ol>
     * <li>package
     * <li>filename
     * <li>line number
     * </ol>
     */
    private static final Pattern STACK_TRACE = Pattern.compile(
            //"(?:\t|\\[catch\\] )at ((?:[a-zA-Z_$][a-zA-Z0-9_$]*\\.)*)[a-zA-Z_$][a-zA-Z0-9_$]*\\.[a-zA-Z_$<][a-zA-Z0-9_$>]*\\(([a-zA-Z_$][a-zA-Z0-9_$]*\\.java):([0-9]+)\\)"); // NOI18N
            "(\\s)*at ((?:[a-zA-Z_$][a-zA-Z0-9_$]*\\.)*)[a-zA-Z_$][a-zA-Z0-9_$]*\\.[a-zA-Z_$<][a-zA-Z0-9_$>]*\\(([a-zA-Z_$][a-zA-Z0-9_$]*\\.java):(([0-9]+))\\)"); // NOI18N
    /**
     * Regexp matching the first line of a stack trace, with the exception message.
     * Captured groups:
     * <ol>
     * <li>unqualified name of exception class plus possible message
     * </ol>
     */
    private static final Pattern EXCEPTION_MESSAGE = Pattern.compile(
            // #42894: JRockit uses "Main Thread" not "main"
            "(?:Exception in thread \"(?:main|Main Thread)\" )?(?:(?:[a-zA-Z_$][a-zA-Z0-9_$]*\\.)+)([a-zA-Z_$][a-zA-Z0-9_$]*(?:: .+)?)"); // NOI18N
    /**
     * Regexp matching part of a Java task's invocation debug message
     * that specifies the classpath.
     * Hack to find the classpath an Ant task is using.
     * Cf. Commandline.describeArguments, issue #28190.
     * Captured groups:
     * <ol>
     * <li>the classpath
     * </ol>
     */
    private static final Pattern CLASSPATH_ARGS = Pattern.compile("\r?\n'-classpath'\r?\n'(.*)'\r?\n"); // NOI18N
    /**
     * Regexp matching part of a Java task's invocation debug message
     * that specifies java executable.
     * Hack to find JDK used for execution.
     */
    private static final Pattern JAVA_EXECUTABLE = Pattern.compile("^Executing '(.*)' with arguments:$", Pattern.MULTILINE); // NOI18N

    /**
     */
    private boolean isTestTaskRunning(AntEvent event) {
        return isTestTaskType(
                getSessionInfo(event.getSession()).currentTaskType);
    }

    /**
     */
    @Override
    public void taskStarted(final AntEvent event) {
        TaskType taskType = detectTaskType(event);
        if (isTestTaskType(taskType)) {
            AntSessionInfo sessionInfo = getSessionInfo(event.getSession());
            assert !isTestTaskType(sessionInfo.currentTaskType);
            sessionInfo.timeOfTestTaskStart = System.currentTimeMillis();
            sessionInfo.currentTaskType = taskType;
            if (sessionInfo.sessionType == null) {
                sessionInfo.sessionType = taskType;
            }

            /*
             * Count the test classes in the try-catch block so that
             * 'testTaskStarted(...)' is called even if counting fails
             * (throws an exception):
             */
            int testClassCount;
            try {
                testClassCount = TestCounter.getTestClassCount(event);
            } catch (Exception ex) {
                testClassCount = 0;
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }

            final boolean hasXmlOutput = hasXmlOutput(event);
            getOutputReader(event).testTaskStarted(testClassCount, hasXmlOutput);
        }
    }

    /**
     */
    @Override
    public void taskFinished(final AntEvent event) {
        AntSessionInfo sessionInfo = getSessionInfo(event.getSession());
        if (isTestTaskType(sessionInfo.currentTaskType)) {
            getOutputReader(event).testTaskFinished();
            sessionInfo.currentTaskType = null;
        }

    }

    /**
     */
    @Override
    public void buildFinished(final AntEvent event) {
        AntSession session = event.getSession();
        AntSessionInfo sessionInfo = getSessionInfo(session);

        if (isTestTaskType(sessionInfo.sessionType)) {
            getOutputReader(event).buildFinished(event);
        }

        session.putCustomData(this, null);          //forget AntSessionInfo
    }

    /**
     * Retrieve existing or creates a new reader for the given session.
     *
     * @param  session  session to return a reader for
     * @return  output reader for the session
     */
    private TestNGOutputReader getOutputReader(final AntEvent event) {
        assert isTestTaskType(getSessionInfo(event.getSession()).sessionType);

        final AntSession session = event.getSession();
        final AntSessionInfo sessionInfo = getSessionInfo(session);
        TestNGOutputReader outputReader = sessionInfo.outputReader;
        if (outputReader == null) {
            outputReader = new TestNGOutputReader(
                    session,
                    sessionInfo.sessionType,
                    sessionInfo.getTimeOfTestTaskStart());
            sessionInfo.outputReader = outputReader;
        }
        return outputReader;
    }

    /**
     */
    private AntSessionInfo getSessionInfo(final AntSession session) {
        Object o = session.getCustomData(this);
        assert (o == null) || (o instanceof AntSessionInfo);

        AntSessionInfo sessionInfo;
        if (o != null) {
            sessionInfo = (AntSessionInfo) o;
        } else {
            sessionInfo = new AntSessionInfo();
            session.putCustomData(this, sessionInfo);
        }
        return sessionInfo;
    }

    /**
     * Finds whether the test report will be generated in XML format.
     */
    private static boolean hasXmlOutput(AntEvent event) {
        final String taskName = event.getTaskName();
        if (taskName.equals(TASK_TESTNG)) {
            return hasXmlOutputJunit(event);
        } else if (taskName.equals(TASK_JAVA)) {
            return hasXmlOutputJava(event);
        } else {
            assert false;
            return false;
        }
    }

    /**
     * Finds whether the test report will be generated in XML format.
     */
    private static boolean hasXmlOutputJunit(AntEvent event) {
        TaskStructure taskStruct = event.getTaskStructure();
        for (TaskStructure child : taskStruct.getChildren()) {
            String childName = child.getName();
            if (childName.equals("formatter")) {                        //NOI18N
                String type = child.getAttribute("type");               //NOI18N
                type = (type != null) ? event.evaluate(type) : null;
                String usefile = child.getAttribute("usefile");         //NOI18N
                usefile = (usefile != null) ? event.evaluate(usefile) : null;
                if ((type != null) && type.equals("xml") //NOI18N
                        && (usefile != null) && !AntProject.toBoolean(usefile)) {
                    String ifPropName = child.getAttribute("if");       //NOI18N
                    String unlessPropName = child.getAttribute("unless");//NOI18N

                    if ((ifPropName == null || event.getProperty(ifPropName) != null) && (unlessPropName == null || event.getProperty(unlessPropName) == null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds whether the test report will be generated in XML format.
     */
    private static boolean hasXmlOutputJava(AntEvent event) {
        TaskStructure taskStruct = event.getTaskStructure();

        String classname = taskStruct.getAttribute("classname");        //NOI18N
        if ((classname == null) ||
                !event.evaluate(classname).equals(ANT_TEST_RUNNER_CLASS_NAME)) {
            return false;
        }

        for (TaskStructure child : taskStruct.getChildren()) {
            String childName = child.getName();
            if (childName.equals("arg")) {                              //NOI18N
                String argValue = child.getAttribute("value");          //NOI18N
                if (argValue == null) {
                    argValue = child.getAttribute("line");              //NOI18N
                }
                if (argValue == null) {
                    continue;
                }
                argValue = event.evaluate(argValue);
                if (argValue.startsWith("formatter=")) {                //NOI18N
                    String formatter = argValue.substring("formatter=".length());//NOI18N
                    int commaIndex = formatter.indexOf(',');
                    if ((commaIndex != -1) && formatter.substring(0, commaIndex).equals(XML_FORMATTER_CLASS_NAME)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void hyperlink(String line, AntSession session, AntEvent event, FileObject source, int messageLevel, int sessionLevel, AntSessionInfo data, int lineNumber) {
        LOGGER.info("consumed: " + event.isConsumed());
        LOGGER.info("" + messageLevel);
        LOGGER.info("" + sessionLevel);
        LOGGER.info("" + (messageLevel <= sessionLevel));
        if (/*messageLevel <= sessionLevel && */!event.isConsumed()) {
            event.consume();
        }
        try {
            session.println(line, true, session.createStandardHyperlink(source.getURL(), guessExceptionMessage(data), lineNumber, -1, -1, -1));
        } catch (FileStateInvalidException e) {
            assert false : e;
        }

    }

    /**
     * Finds source roots corresponding to the apparently active classpath
     * (as reported by logging from Ant when it runs the Java launcher with -cp).
     */
    private static Collection/*<FileObject>*/ getCurrentSourceRootsForClasspath(AntSessionInfo data) {
        if (data.classpath == null) {
            return Collections.EMPTY_SET;
        }
        if (data.classpathSourceRoots == null) {
            data.classpathSourceRoots = new LinkedHashSet<FileObject>();
            StringTokenizer tok = new StringTokenizer(data.classpath, File.pathSeparator);
            while (tok.hasMoreTokens()) {
                String binrootS = tok.nextToken();
                File f = FileUtil.normalizeFile(new File(binrootS));
                URL binroot = FileUtil.urlForArchiveOrDir(f);
                if (binroot == null) {
                    continue;
                }
                FileObject[] someRoots = SourceForBinaryQuery.findSourceRoots(binroot).getRoots();
                data.classpathSourceRoots.addAll(Arrays.asList(someRoots));
            }
            if (data.platformSources != null) {
                data.classpathSourceRoots.addAll(Arrays.asList(data.platformSources.getRoots()));
            } else {
                // no platform found. use default one:
                JavaPlatform plat = JavaPlatform.getDefault();
                // in unit tests the default platform may be null:
                if (plat != null) {
                    data.classpathSourceRoots.addAll(Arrays.asList(plat.getSourceFolders().getRoots()));
                }
            }
        }
        return data.classpathSourceRoots;
    }

    private static String guessExceptionMessage(AntSessionInfo data) {
        if (data.possibleExceptionText != null) {
            if (data.lastExceptionMessage == null) {
                Matcher m = EXCEPTION_MESSAGE.matcher(data.possibleExceptionText);
                if (m.matches()) {
                    data.lastExceptionMessage = m.group(1);
                } else {
                    data.possibleExceptionText = null;
                }
            }
            return data.lastExceptionMessage;
        }
        return null;
    }

    /**
     * Data stored in the session.
     */
    private static final class SessionData {

        public ClassPath platformSources = null;
        public String classpath = null;
        public Collection<FileObject> classpathSourceRoots = null;
        public String possibleExceptionText = null;
        public String lastExceptionMessage = null;

        public SessionData() {
        }

        public void setClasspath(String cp) {
            classpath = cp;
            classpathSourceRoots = null;
        }

        public void setPlatformSources(ClassPath platformSources) {
            this.platformSources = platformSources;
            classpathSourceRoots = null;
        }
    }

    private AntSessionInfo getSessionData(AntSession session) {
        AntSessionInfo data = (AntSessionInfo) session.getCustomData(this);
        if (data == null) {
            data = new AntSessionInfo();
            session.putCustomData(this, data);
        }
        return data;
    }

    private ClassPath findPlatformSources(String javaExecutable) {
        for (JavaPlatform p : JavaPlatformManager.getDefault().getInstalledPlatforms()) {
            FileObject fo = p.findTool("java"); // NOI18N
            if (fo != null) {
                File f = FileUtil.toFile(fo);
                if (f.getAbsolutePath().startsWith(javaExecutable)) {
                    return p.getSourceFolders();
                }
            }
        }
        return null;
    }
}
