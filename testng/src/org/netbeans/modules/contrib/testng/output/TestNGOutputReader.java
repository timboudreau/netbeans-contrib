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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.UnsupportedCharsetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gsf.testrunner.api.Manager;
import org.netbeans.modules.gsf.testrunner.api.OutputLine;
import org.netbeans.modules.gsf.testrunner.api.Report;
import org.netbeans.modules.gsf.testrunner.api.Status;
import org.netbeans.modules.gsf.testrunner.api.TestSession;
import org.netbeans.modules.gsf.testrunner.api.TestSession.SessionType;
import org.netbeans.modules.gsf.testrunner.api.TestSuite;
import org.netbeans.modules.gsf.testrunner.api.Testcase;
import org.netbeans.modules.gsf.testrunner.api.Trouble;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;
import static java.util.Calendar.MILLISECOND;
import static java.util.logging.Level.FINER;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.ADD_ERROR_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.ADD_FAILURE_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.END_OF_TEST_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.START_OF_TEST_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.TESTCASE_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.TEST_LISTENER_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.TESTS_COUNT_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.TESTSUITE_PREFIX;
import static org.netbeans.modules.contrib.testng.output.RegexpUtils.TESTSUITE_STATS_PREFIX;

/**
 * Obtains events from a single session of an Ant <code>junit</code> task
 * and builds a {@link Report}.
 * The events are delivered by the {@link TestNGAntLogger}.
 *
 * @see  TestNGAntLogger
 * @see  Report
 * @author  Marian Petras
 * @author  Lukas Jungmann
 */
final class TestNGOutputReader {

    private static final int MAX_REPORT_FILE_SIZE = 1 << 22;    //2 MiB
    /** */
    private final NumberFormat numberFormat = NumberFormat.getInstance();
    /** */
    private final SessionType sessionType;
    /** whether XML report is expected */
    private boolean expectXmlReport;
    /** */
    private final File antScript;
    /** */
    private final long timeOfSessionStart;
    private long lastSuiteTime = 0;
    private final Logger LOG;
    private final Logger progressLogger;
    /** */
    private RegexpUtils regexp = RegexpUtils.getInstance();
    /** */
    private boolean lastHeaderBrief;
    /** */
    private final Manager manager = Manager.getInstance();
    /** */
    private ClassPath platformSources;
    private TestSession testSession;
    private Project project;
    private File resultsDir;
    private TestNGTestcase testcase;
    private Report report;

    enum State {

        DEFAULT, SUITE_STARTED, TESTCASE_STARTED, SUITE_FINISHED, TESTCASE_ISSUE
    };
    private State state = State.DEFAULT;

    /** Creates a new instance of TestNGOutputReader */
    TestNGOutputReader(final AntSession session,
            final AntSessionInfo sessionInfo,
            final Project project,
            final Properties props) {
        this.project = project;
        this.sessionType = sessionInfo.getSessionType();
        this.antScript = FileUtil.normalizeFile(session.getOriginatingScript());
        this.timeOfSessionStart = sessionInfo.getTimeOfTestTaskStart();
        if (project == null) {
            FileObject fileObj = FileUtil.toFileObject(antScript);
            this.project = FileOwnerQuery.getOwner(fileObj);
        }
        this.testSession = new TestNGTestSession("", this.project, sessionType, new TestNGTestNodeFactory()); //NOI18N
        testSession.setRerunHandler(new TestNGExecutionManager(session, testSession, props));
        this.progressLogger = Logger.getLogger(
                "org.netbeans.modules.contrib.testng.outputreader.progress");    //NOI18N
        this.LOG = Logger.getLogger(TestNGOutputReader.class.getName());
    }

    Project getProject() {
        return project;
    }

    TestSession getTestSession() {
        return testSession;
    }

    void verboseMessageLogged(final AntEvent event) {
        final String msg = event.getMessage();
        if (msg == null) {
            return;
        }
        verboseMessageLogged(msg);
        displayOutput(msg, event.getLogLevel() == AntEvent.LOG_WARN);
    }

    /**
     */
    synchronized void verboseMessageLogged(String msg) {
        Matcher matcher = RegexpUtils.JAVA_EXECUTABLE.matcher(msg);
        if (matcher.find()) {
            String executable = matcher.group(1);
            ClassPath platformSrcs = findPlatformSources(executable);
            if (platformSrcs != null) {
                this.platformSources = platformSrcs;
            }
        }
//        switch (state) {
//            case SUITE_STARTED: {
//                if (msg.startsWith(TEST_LISTENER_PREFIX)) {
//                    String testListenerMsg = msg.substring(TEST_LISTENER_PREFIX.length());
//                    if (testListenerMsg.startsWith(TESTS_COUNT_PREFIX)) {
////                        String countStr = testListenerMsg.substring(TESTS_COUNT_PREFIX.length());
//                        return;
//                    }
//
//                    int leftBracketIndex = testListenerMsg.indexOf('(');
//                    if (leftBracketIndex == -1) {
//                        return;
//                    }
//
//                    final String shortMsg = testListenerMsg.substring(0, leftBracketIndex);
//                    if (shortMsg.equals(START_OF_TEST_PREFIX)) {
//                        String restOfMsg = testListenerMsg.substring(START_OF_TEST_PREFIX.length());
//                        if (restOfMsg.length() != 0) {
//                            char firstChar = restOfMsg.charAt(0);
//                            char lastChar = restOfMsg.charAt(restOfMsg.length() - 1);
//                            if ((firstChar == '(') && (lastChar == ')')) {
//                                testCaseStarted(restOfMsg.substring(1, restOfMsg.length() - 1));
//                            }
//                        }
//                        return;
//                    }
//                }
//                break;
//            }
//            case TESTCASE_STARTED: {
//                if (msg.startsWith(TEST_LISTENER_PREFIX)) {
//                    String testListenerMsg = msg.substring(TEST_LISTENER_PREFIX.length());
//                    int leftBracketIndex = testListenerMsg.indexOf('(');
//                    if (leftBracketIndex == -1) {
//                        return;
//                    }
//                    final String shortMsg = testListenerMsg.substring(0, leftBracketIndex);
//
//                    if (shortMsg.equals(END_OF_TEST_PREFIX)) {
//                        String restOfMsg = testListenerMsg.substring(END_OF_TEST_PREFIX.length());
//                        if (restOfMsg.length() != 0) {
//                            char firstChar = restOfMsg.charAt(0);
//                            char lastChar = restOfMsg.charAt(restOfMsg.length() - 1);
//                            if ((firstChar == '(') && (lastChar == ')')) {
//                                String name = restOfMsg.substring(1, restOfMsg.length() - 1);
//                                if (name.equals(testSession.getCurrentTestCase().getName())) {
//                                    testCaseFinished();
//                                }
//                            }
//                        }
//                        return;
//                    } else if (shortMsg.equals(ADD_FAILURE_PREFIX)
//                            || shortMsg.equals(ADD_ERROR_PREFIX)) {
//                        int lastCharIndex = testListenerMsg.length() - 1;
//
//                        String insideBrackets = testListenerMsg.substring(
//                                shortMsg.length() + 1,
//                                lastCharIndex);
//                        int commaIndex = insideBrackets.indexOf(',');
//                        String testName = (commaIndex == -1)
//                                ? insideBrackets
//                                : insideBrackets.substring(0, commaIndex);
//                        if (!testName.equals(testSession.getCurrentTestCase().getName())) {
//                            return;
//                        }
//                        testSession.getCurrentTestCase().setTrouble(new Trouble(shortMsg.equals(ADD_ERROR_PREFIX)));
//                        boolean hasErrMsg = (commaIndex != -1)
//                                && ((commaIndex + 2) <= insideBrackets.length()); // #166912
//                        if (hasErrMsg) {
//                            int errMsgStart;
//                            if (Character.isSpaceChar(insideBrackets.charAt(commaIndex + 1))) {
//                                errMsgStart = commaIndex + 2;
//                            } else {
//                                errMsgStart = commaIndex + 1;
//                            }
//                            String troubleMsg = insideBrackets.substring(errMsgStart);
//                            if (!troubleMsg.equals("null")) {                   //NOI18N
//                                addStackTraceLine(testSession.getCurrentTestCase(), troubleMsg, false);
//                            }
//                        }
//                        return;
//                    }
//                }
//                break;
//            }
//            case DEFAULT:
//            case SUITE_FINISHED:
//            case TESTCASE_ISSUE: {
//                Matcher matcher = RegexpUtils.JAVA_EXECUTABLE.matcher(msg);
//                if (matcher.find()) {
//                    String executable = matcher.group(1);
//                    ClassPath platformSrcs = findPlatformSources(executable);
//                    if (platformSrcs != null) {
//                        this.platformSources = platformSrcs;
//                    }
//                }
//                break;
//            }
//        }
    }

    synchronized void messageLogged(final AntEvent event) {
        final String msg = event.getMessage();
        if (msg == null) {
            return;
        }
        displayOutput(msg, event.getLogLevel() == AntEvent.LOG_WARN);
//        switch (state) {
//            case TESTCASE_ISSUE:
//            case SUITE_FINISHED: {
//                if (msg.startsWith(TESTCASE_PREFIX)) {
//                    String header = msg.substring(TESTCASE_PREFIX.length());
//                    boolean success =
//                            lastHeaderBrief
//                            ? tryParseBriefHeader(header)
//                            || !(lastHeaderBrief = !tryParsePlainHeader(header))
//                            : tryParsePlainHeader(header)
//                            || (lastHeaderBrief = tryParseBriefHeader(header));
//                    if (success) {
//                        state = State.TESTCASE_ISSUE;
//                    }
//                    break;
//                }
//            }
//            case DEFAULT: {
//                if (msg.contains(TESTSUITE_PREFIX)) {
//                    String suiteName = msg.substring(TESTSUITE_PREFIX.length());
//                    if (regexp.getFullJavaIdPattern().matcher(suiteName).matches()) {
//                        suiteStarted(suiteName);
//                        resultsDir = determineResultsDir(event);
//                    }
//                }
//
//                if (state.equals(State.TESTCASE_ISSUE) && !msg.equals("")) {
//                    addStackTraceLine(testcase, msg, true);
//                }
//                break;
//            }
//            case SUITE_STARTED: {
//                if (msg.startsWith(TESTSUITE_STATS_PREFIX)) {
//                    Matcher matcher = regexp.getSuiteStatsPattern().matcher(msg);
//                    if (matcher.matches()) {
//                        try {
//                            suiteFinished(Integer.parseInt(matcher.group(1)),
//                                    Integer.parseInt(matcher.group(2)),
//                                    Integer.parseInt(matcher.group(3)),
//                                    parseTime(matcher.group(4)));
//                        } catch (NumberFormatException ex) {
//                            assert false;
//                        }
//                    } else {
//                        assert false;
//                    }
//                    break;
//                }
//            }
//            case TESTCASE_STARTED: {
//                int posTestListener = msg.indexOf(TEST_LISTENER_PREFIX);
//                if (posTestListener != -1) {
//                    displayOutput(msg.substring(0, posTestListener), event.getLogLevel() == AntEvent.LOG_WARN);
//                    verboseMessageLogged(msg.substring(posTestListener));
//                } else {
//                    displayOutput(msg, event.getLogLevel() == AntEvent.LOG_WARN);
//                }
//                break;
//            }
//        }
    }

    /**
     */
    private int parseTime(String timeString) {
        int timeMillis;
        try {
            double seconds = numberFormat.parse(timeString).doubleValue();
            timeMillis = Math.round((float) (seconds * 1000.0));
        } catch (ParseException ex) {
            timeMillis = -1;
        }
        return timeMillis;
    }

    /**
     * Tries to determine test results directory.
     *
     * @param  event  Ant event serving as a source of information
     * @return  <code>File<code> object representing the results directory,
     *          or <code>null</code> if the results directory could not be
     *          determined
     */
    private static File determineResultsDir(final AntEvent event) {
        File resultsDir = null;

        final String taskName = event.getTaskName();
        if (taskName != null) {
            if (taskName.equals("testng")) {                             //NOI18N
                resultsDir = determineTestNGTaskResultsDir(event);
            } else if (taskName.equals("java")) {                       //NOI18N
                resultsDir = determineJavaTaskResultsDir(event);
            }
        }

        if ((resultsDir != null) && resultsDir.exists() && resultsDir.isDirectory()) {
            return resultsDir;
        } else {
            return null;
        }
    }

    /**
     */
    private static File determineTestNGTaskResultsDir(final AntEvent event) {
        final TaskStructure taskStruct = event.getTaskStructure();
        if (taskStruct == null) {
            return null;
        }
        String todirAttr = (taskStruct.getAttribute("outputdir") != null) //NOI18N
                ? taskStruct.getAttribute("outputdir") : "test-output"; //NOI18N
        File resultsDir = getFile(todirAttr, event);
        return findAbsolutePath(resultsDir, taskStruct, event);
    }

    /**
     */
    private static File determineJavaTaskResultsDir(final AntEvent event) {
        final TaskStructure taskStruct = event.getTaskStructure();
        if (taskStruct == null) {
            return null;
        }

        String todirPath = null;

        for (TaskStructure taskChild : taskStruct.getChildren()) {
            String taskChildName = taskChild.getName();
            if (taskChildName.equals("arg")) {                          //NOI18N
                String valueAttr = taskChild.getAttribute("value");     //NOI18N
                if (valueAttr == null) {
                    valueAttr = taskChild.getAttribute("line");         //NOI18N
                }
                if (valueAttr != null) {
                    valueAttr = event.evaluate(valueAttr);
                    int index = valueAttr.indexOf("-d "); //NOI18N
                    if (-1 < index) {
                        todirPath = valueAttr.substring(index + 3);
                        if (todirPath.contains(" ")) {
                            index = todirPath.startsWith("\"") //NOI18N
                                    ? todirPath.indexOf("\"", 1) + 1 //NOI18N
                                    : todirPath.indexOf(" "); //NOI18N
                            todirPath = todirPath.substring(0, index);
                        }
                    }
                }
            }
        }

        File resultsDir = (todirPath != ".") ? new File(todirPath) //NOI18N
                : null;
        return findAbsolutePath(resultsDir, taskStruct, event);
    }

    private static File findAbsolutePath(File path, TaskStructure taskStruct, AntEvent event) {
        if (isAbsolute(path)) {
            return path;
        }
        return combine(getBaseDir(event), path);
    }

    private static File combine(File parentPath, File path) {
        return (path != null) ? new File(parentPath, path.getPath())
                : parentPath;
    }

    private static boolean isAbsolute(File path) {
        return (path != null) && path.isAbsolute();
    }

    private static File getFile(String attrValue, AntEvent event) {
        return new File(event.evaluate(attrValue));
    }

    private static File getBaseDir(AntEvent event) {
        return new File(event.getProperty("basedir"));                  //NOI18N
    }

    /**
     */
    private ClassPath findPlatformSources(final String javaExecutable) {

        /* Copied from JavaAntLogger */

        final JavaPlatform[] platforms = JavaPlatformManager.getDefault().getInstalledPlatforms();
        for (int i = 0; i < platforms.length; i++) {
            FileObject fo = platforms[i].findTool("java");              //NOI18N
            if (fo != null) {
                File f = FileUtil.toFile(fo);
                //XXX - look for a "subpath" in case of forked JRE; is there a better way?
                String path = f.getAbsolutePath();
                if (path.startsWith(javaExecutable)
                        || javaExecutable.startsWith(path.substring(0, path.length() - 8))) {
                    return platforms[i].getSourceFolders();
                }
            }
        }
        return null;
    }

    /**
     * Notifies that a test (Ant) task was just started.
     *
     * @param  expectedSuitesCount  expected number of test suites going to be
     *                              executed by this task
     */
    void testTaskStarted(int expectedSuitesCount, boolean expectXmlOutput, AntEvent event) {
        this.expectXmlReport = expectXmlOutput;
        manager.testStarted(testSession);
//        manager.displaySuiteRunning(testSession, TestSuite.ANONYMOUS_TEST_SUITE);
        resultsDir = determineResultsDir(event);
    }

    /**
     */
    void testTaskFinished() {
        state = State.SUITE_FINISHED;
        closePreviousReport(); // #171050
    }

    private void closePreviousReport() {
        //get results from report xml file
        if (resultsDir != null) {
            File reportFile = findReportFile();
            if ((reportFile != null) && isValidReportFile(reportFile)) {
                TestNGSuite reportSuite = parseReportFile(reportFile, testSession);
                for (TestNGTestSuite ts : reportSuite.getTestSuites()) {
                    manager.displaySuiteRunning(testSession, ts);
                    testSession.addSuite(ts);
                    report = testSession.getReport(ts.getElapsedTime());
                    manager.displayReport(testSession, report, true);
                }
            }
            report = null;
        }
    }

    /**
     */
    void buildFinished(final AntEvent event) {
        manager.sessionFinished(testSession);
    }

    //------------------ UPDATE OF DISPLAY -------------------

    /**
     */
    private void displayOutput(final String text, final boolean error) {
        manager.displayOutput(testSession, text, error);
        if (state == State.TESTCASE_STARTED) {
            List<String> addedLines = new ArrayList<String>();
            addedLines.add(text);
            Testcase tc = testSession.getCurrentTestCase();
            if (tc != null) {
                tc.addOutputLines(addedLines);
            }
        }
    }

    //--------------------------------------------------------
    /**
     */
    private boolean tryParsePlainHeader(String testcaseHeader) {
        final Matcher matcher = regexp.getTestcaseHeaderPlainPattern().matcher(testcaseHeader);
        if (matcher.matches()) {
            String methodName = matcher.group(1);
            String timeString = matcher.group(2);

            testcase = findTest(testSession.getCurrentSuite(), methodName);
            testcase.setTimeMillis(parseTime(timeString));

            return true;
        } else {
            return false;
        }
    }

    private TestNGTestcase findTest(TestSuite suite, String methodName) {
        TestNGTestcase ret = null;
        for (Testcase tcase : suite.getTestcases()) {
            if (tcase.getName().equals(methodName)) {
                ret = (TestNGTestcase) tcase;
                break;
            }
        }
        return ret;
    }

    private File findReportFile() {
        File file = new File(resultsDir, "testng-results.xml"); //NOI18N
        return (file.isFile() ? file : null);
    }

    /**
     */
    private boolean isValidReportFile(File reportFile) {
        if (!reportFile.canRead()) {
            return false;
        }

        if (reportFile.canRead()) {
            return true;
        }

        long lastModified = reportFile.lastModified();
        long timeDelta = lastModified - timeOfSessionStart;

        final Logger logger = Logger.getLogger("org.netbeans.modules.contrib.testng.outputreader.timestamps");//NOI18N
        final Level logLevel = FINER;
        if (logger.isLoggable(logLevel)) {
            logger.log(logLevel, "Report file: " + reportFile.getPath());//NOI18N

            final GregorianCalendar timeStamp = new GregorianCalendar();

            timeStamp.setTimeInMillis(timeOfSessionStart);
            logger.log(logLevel, "Session start:    " + String.format("%1$tT.%2$03d", timeStamp, timeStamp.get(MILLISECOND)));//NOI18N

            timeStamp.setTimeInMillis(lastModified);
            logger.log(logLevel, "Report timestamp: " + String.format("%1$tT.%2$03d", timeStamp, timeStamp.get(MILLISECOND)));//NOI18N
        }

        if (timeDelta >= 0) {
            return true;
        }

        /*
         * Normally we would return 'false' here, but:
         *
         * We must take into account that modification timestamps of files
         * usually do not hold milliseconds, just seconds.
         * The worst case we must accept is that the session started
         * on YYYY.MM.DD hh:mm:ss.999 and the file was saved exactly in the same
         * millisecond but its time stamp is just YYYY.MM.DD hh:mm:ss, i.e
         * 999 milliseconds earlier.
         */
        return -timeDelta <= timeOfSessionStart % 1000;

//        if (timeDelta < -999) {
//            return false;
//        }
//
//        final GregorianCalendar sessStartCal = new GregorianCalendar();
//        sessStartCal.setTimeInMillis(timeOfSessionStart);
//        int sessStartMillis = sessStartCal.get(MILLISECOND);
//        if (timeDelta < -sessStartMillis) {
//            return false;
//        }
//
//        final GregorianCalendar fileModCal = new GregorianCalendar();
//        fileModCal.setTimeInMillis(lastModified);
//        if (fileModCal.get(MILLISECOND) != 0) {
//            /* So the file's timestamp does hold milliseconds! */
//            return false;
//        }
//
//        /*
//         * Now we know that milliseconds are not part of file's timestamp.
//         * Let's substract the milliseconds part and check whether the delta is
//         * non-negative, now that we only check seconds:
//         */
//        return lastModified >= (timeOfSessionStart - sessStartMillis);
    }

    private static TestNGSuite parseReportFile(File reportFile, TestSession session) {
        TestNGSuite reports = null;
        try {
            reports = XmlOutputParser.parseXmlOutput(
                    new InputStreamReader(
                    new FileInputStream(reportFile),
                    "UTF-8"), session);                                  //NOI18N
        } catch (UnsupportedCharsetException ex) {
            assert false;
        } catch (SAXException ex) {
            /* This exception has already been handled. */
        } catch (IOException ex) {
            /*
             * Failed to read the report file - but we still have
             * the report built from the Ant output.
             */
            Logger.getLogger(TestNGOutputReader.class.getName()).log(Level.INFO, "I/O exception while reading JUnit XML report file from JUnit: ", ex);//NOI18N
        }
        return reports;
    }
}
