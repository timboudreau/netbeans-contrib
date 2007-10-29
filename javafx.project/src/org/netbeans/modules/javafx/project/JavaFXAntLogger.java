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
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 * 
 * @author answer
 */
public final class JavaFXAntLogger extends AntLogger {

    private static final Pattern HYPERLINK = Pattern.compile("\"?(.+?)\"?(?::|, line )(?:(\\d+):(?:(\\d+):(?:(\\d+):(\\d+):)?)?)? +(.+)"); // NOI18N
    
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
    
    @Override
    public void messageLogged(AntEvent event) {
        AntSession session = event.getSession();
        int messageLevel = event.getLogLevel();
        int sessionLevel = session.getVerbosity();
        SessionData data = getSessionData(session);
        String line = event.getMessage();
        assert line != null;

        Matcher m = HYPERLINK.matcher(line);
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

// private methods    
    private void hyperlink(String line, AntSession session, AntEvent event, FileObject source, int messageLevel, int sessionLevel, SessionData data, int lineNumber) {
        if (messageLevel <= sessionLevel) {
            try {
                session.println(line, true, session.createStandardHyperlink(source.getURL(), "", lineNumber, -1, -1, -1));
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

    private static final class SessionData {
        public long startTime;
        public Stack<File> currentDir = new Stack<File>();
        public SessionData() {}
    }   
}