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

package org.netbeans.modules.groovy.groovyproject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.groovy.support.api.GroovyErrorOutputSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.OutputListener;

/**
 * Logger which should suppress or prettify typical Ant output from a
 * j2seproject's build-impl.xml.
 * @author Jesse Glick
 */
public final class GroovyAntLogger extends AntLogger {
//    static {
//        System.setProperty("org.netbeans.modules.groovy.groovyproject.GroovyAntLogger", "-1");
//    }
    private static final ErrorManager ERR = ErrorManager.getDefault().getInstance(GroovyAntLogger.class.getName());
    private static final boolean LOGGABLE = ERR.isLoggable(ErrorManager.INFORMATIONAL);
    
    /** Default constructor for lookup. */
    public GroovyAntLogger() {}
    
    public boolean interestedInSession(AntSession session) {
        // Even if the initiating project is not a J2SEProject, suppress these messages.
        // However disable our tricks when running at VERBOSE or higher.
        return session.getVerbosity() <= AntEvent.LOG_INFO;
    }
    
    private static boolean isGroovyProject(File dir) {
        FileObject projdir = FileUtil.toFileObject(FileUtil.normalizeFile(dir));
        try {
            Project proj = ProjectManager.getDefault().findProject(projdir);
            if (proj != null) {
                // Check if it is a GroovyProject.
                return proj.getLookup().lookup(GroovyProject.class) != null;
            }
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
        }
        return false;
    }
    
    public boolean interestedInScript(File script, AntSession session) {
        if (script.getName().equals("build-impl.xml")) { // NOI18N
            File parent = script.getParentFile();
            if (parent != null && parent.getName().equals("nbproject")) { // NOI18N
                File parent2 = parent.getParentFile();
                if (parent2 != null) {
                    return isGroovyProject(parent2);
                }
            }
        }
        // Was not a J2SEProject's nbproject/build-impl.xml; ignore it.
        return false;
    }
    
    public String[] interestedInTargets(AntSession session) {
        return AntLogger.ALL_TARGETS;
    }
    
    public String[] interestedInTasks(AntSession session) {
        // XXX will eventually need them all anyway; as is, could list just javac
        return AntLogger.ALL_TASKS;
    }
    
    public int[] interestedInLogLevels(AntSession session) {
        return new int[] {
            AntEvent.LOG_WARN,
        };
    }
    
    public void taskFinished(AntEvent event) {
        if ("javac".equals(event.getTaskName())) { // NOI18N
            Throwable t = event.getException();
            AntSession session = event.getSession();
            if (t != null && !session.isExceptionConsumed(t)) {
                // Some error was thrown from build-impl.xml#compile. Ignore it; generally
                // it will have been a compilation error which we do not wish to show.
                session.consumeException(t);
            }
        }
    }

    public void messageLogged(AntEvent event) {
        // #43968 - filter out following message
        if (!event.isConsumed() && event.getLogLevel() == AntEvent.LOG_WARN &&
            event.getMessage().startsWith("Trying to override old definition of " + // NOI18N
                "task http://www.netbeans.org/ns/j2se-project/1:")) { // NOI18N
            event.consume();
        }
        
        AntSession session = event.getSession();
        String line = event.getMessage();
        if (LOGGABLE) ERR.log("Received message: " + line);
        OutputListener hyperlink = findHyperlink(session, line);
        // XXX should translate tabs to spaces here as a safety measure
        event.getSession().println(line, event.getLogLevel() <= AntEvent.LOG_WARN, hyperlink);
        event.consume();
        
    }
    
    /**
     * Possibly hyperlink a message logged event.
     */
    private static OutputListener findHyperlink(AntSession session, String line) {
        GroovyErrorOutputSupport.HyperlinkData data = GroovyErrorOutputSupport.checkErrorLine( line, FileUtil.toFileObject(session.getOriginatingScript()) );
        if ( data != null ) {
            try {
                return session.createStandardHyperlink(data.file.toURI().toURL(), data.message, data.line1, data.column1, data.line2, data.column2);
            } catch (MalformedURLException e) {
                assert false : e;
            }
        }
        return null;
    }

}
