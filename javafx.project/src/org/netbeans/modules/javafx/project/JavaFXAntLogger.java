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

package org.netbeans.modules.javafx.project;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.javafx.platform.JavaFXPlatform;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 * 
 * @author answer
 */
public final class JavaFXAntLogger extends AntLogger {

    private static final Pattern HYPERLINK = Pattern.compile("\"?(.+?)\"?(?::|, line )(?:(\\d+):(?:(\\d+):(?:(\\d+):(\\d+):)?)?)? +(.+)"); // NOI18N

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
    "(?:\t|\\[catch\\] )at ((?:[a-zA-Z_$][a-zA-Z0-9_$]*\\.)*)[a-zA-Z_$][a-zA-Z0-9_$]*\\.[a-zA-Z_$<][a-zA-Z0-9_$>]*\\(([a-zA-Z_$][a-zA-Z0-9_$]*\\.fx):([0-9]+)\\)"); // NOI18N
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
    
    private static final int[] LEVELS_OF_INTEREST = {
//        AntEvent.LOG_VERBOSE, 
        AntEvent.LOG_INFO, 
        AntEvent.LOG_WARN, 
        AntEvent.LOG_ERR, 
    };
    
    /** Default constructor for lookup. */
    public JavaFXAntLogger() {}
    
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
        return AntLogger.ALL_TASKS;
    }
    
    @Override
    public int[] interestedInLogLevels(AntSession session) {
        return LEVELS_OF_INTEREST;
    }
    
    @Override
    public boolean interestedInScript(File script, AntSession session) {
        if (script.getName().equals("build-impl.xml")) { // NOI18N
            File parent = script.getParentFile();
            if (parent != null && parent.getName().equals("nbproject")) { // NOI18N
                File parent2 = parent.getParentFile();
                if (parent2 != null) {
                    return isJavaFXProject(parent2);
                }
            }
        }
        // Was not a JavaFXProject's nbproject/build-impl.xml; ignore it.
        return false;
    }
       
    public void taskFinished(AntEvent event) {
        if ("java".equals(event.getTaskName()) || "javafx".equals(event.getTaskName()) || "javac".equals(event.getTaskName()) || "javafxc".equals(event.getTaskName())) { // NOI18N
            Throwable t = event.getException();
            AntSession session = event.getSession();
            if (t != null && !session.isExceptionConsumed(t)) {
                // To cleanup the message in the output window we filter all the final exceptions from compilation and execution
                session.consumeException(t);
            }
        }
    }
 
    @Override
    public void messageLogged(AntEvent event) {
        AntSession session = event.getSession();
        int messageLevel = event.getLogLevel();
        int sessionLevel = session.getVerbosity();
        SessionData data = getSessionData(session);
        String line = event.getMessage();
        assert line != null;

        Matcher m = STACK_TRACE.matcher(line);
        if (m.matches()) {
            // We have a stack trace.
            String pkg = m.group(1);
            String filename = m.group(2);
            String resource = pkg.replace('.', '/') + filename;
            int lineNumber = Integer.parseInt(m.group(3));
            // Check to see if the class is listed in our per-task sourcepath.
            // XXX could also look for -Xbootclasspath etc., but probably less important
            Iterator it = getCurrentSourceRootsForClasspath(data).iterator();
            while (it.hasNext()) {
                FileObject root = (FileObject)it.next();
                // XXX this is apparently pretty expensive; try to use java.io.File instead
                FileObject source = root.getFileObject(resource);
                if (source != null) {
                    // Got it!
                    hyperlink(line, session, event, source, messageLevel, sessionLevel, data, lineNumber);
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
                    hyperlink(line, session, event, source, messageLevel, sessionLevel, data, lineNumber);
                }
            }
        } else {
            // Track the last line which was not a stack trace - probably the exception message.
            data.possibleExceptionText = line;
            data.lastExceptionMessage = null;
        }
        
        // Look for classpaths.
        if (messageLevel == AntEvent.LOG_VERBOSE) {
            Matcher m2 = CLASSPATH_ARGS.matcher(line);
            if (m2.find()) {
                String cp = m2.group(1);
                data.setClasspath(cp);
            }
            // XXX should also probably clear classpath when taskFinished called
            m2 = JAVA_EXECUTABLE.matcher(line);
            if (m2.find()) {
                String executable = m2.group(1);
                ClassPath platformSources = findPlatformSources(executable);
                if (platformSources != null) {
                    data.setPlatformSources(platformSources);
                }
            }
        }

        m = HYPERLINK.matcher(line);
        if (m.matches()) {
            String path = m.group(1);
            if (path.startsWith("file:")) {
                try{
                    File file = new File(new URI(path));
                    FileObject fileObject = FileUtil.toFileObject(file);
                    if (fileObject.getExt().equalsIgnoreCase("fx")){
                        event.consume();
                        session.println(line, true, null);
                    }
                } catch (Exception e) {
                }
            } else {
                 if (path.startsWith("Error in file:")) {
                    try{
                        path = path.substring(9);
                        File file = new File(new URI(path));
                        FileObject fileObject = FileUtil.toFileObject(file);
                        if (fileObject.getExt().equalsIgnoreCase("fx")){
                            event.consume();
                            int lineNumber = Integer.parseInt(m.group(2));
                            hyperlink(line.replace("%20", " "), session, event, fileObject, messageLevel, sessionLevel, data, lineNumber);
                        }
                    } catch (Exception e) {
                    }
                }   
            }
        }
    }
    
    /**
     * Finds source roots corresponding to the apparently active classpath
     * (as reported by logging from Ant when it runs the Java launcher with -cp).
     */
    private static Collection/*<FileObject>*/ getCurrentSourceRootsForClasspath(SessionData data) {
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
                JavaPlatform plat = JavaFXPlatform.getDefaultFXPlatform();
                // in unit tests the default platform may be null:
                if (plat != null) {
                    data.classpathSourceRoots.addAll(Arrays.asList(plat.getSourceFolders().getRoots()));
                }
            }
        }
        return data.classpathSourceRoots;
    }

    // private methods    
    private ClassPath findPlatformSources(String javaExecutable) {
        for (JavaPlatform p : JavaPlatformManager.getDefault().getInstalledPlatforms()) {
            return p.getSourceFolders();
        }
        return null;
    }
    

    private void hyperlink(String line, AntSession session, AntEvent event, FileObject source, int messageLevel, int sessionLevel, SessionData data, int lineNumber) {
        if (messageLevel <= sessionLevel) {
            try {
                session.println(line, true, session.createStandardHyperlink(source.getURL(), "", lineNumber, -1, -1, -1));
                event.consume();
            } catch (FileStateInvalidException e) {
                assert false : e;
            }
        }
    }

    private static boolean isJavaFXProject(File dir) {
        FileObject projdir = FileUtil.toFileObject(FileUtil.normalizeFile(dir));
        try {
            Project proj = ProjectManager.getDefault().findProject(projdir);
            if (proj != null) {
                // Check if it is a JavaFXProject.
                return proj.getLookup().lookup(JavaFXProject.class) != null;
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return false;
    }

    private SessionData getSessionData(AntSession session) {
        SessionData data = (SessionData) session.getCustomData(this);
        if (data == null) {
            data = new SessionData();
            session.putCustomData(this, data);
        }
        return data;
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
        public long startTime;
        public Stack<File> currentDir = new Stack<File>();
        public SessionData() {}
        public void setClasspath(String cp) {
            classpath = cp;
            classpathSourceRoots = null;
        }
        public void setPlatformSources(ClassPath platformSources) {
            this.platformSources = platformSources;
            classpathSourceRoots = null;
        }
    }
}