/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.autoproject.web;

import org.netbeans.modules.autoproject.spi.Cache;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.spi.AutomaticProjectMarker;
import org.netbeans.spi.project.support.ant.PathMatcher;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

public class BuildSniffer extends AntLogger {

    private static final Logger LOG = Logger.getLogger(BuildSniffer.class.getName());

    /** Default constructor for lookup. */
    public BuildSniffer() {}

    @Override
    public boolean interestedInSession(AntSession session) {
        FileObject script = FileUtil.toFileObject(session.getOriginatingScript());
        if (script == null) {
            return false;
        }
        Project p = FileOwnerQuery.getOwner(script);
        if (p == null) {
            return false;
        }
        return p.getLookup().lookup(AutomaticProjectMarker.class) != null;
    }

    @Override
    public boolean interestedInAllScripts(AntSession session) {
        return true;
    }

    @Override
    public String[] interestedInTargets(AntSession session) {
        return AntLogger.ALL_TARGETS;
    }

    @Override
    public String[] interestedInTasks(AntSession session) {
        return new String[] {"war"};
    }

    /**
     * Per-session state.
     */
    private static class State {

        /**
         * Cache keys of path type already written during this session.
         * If they come up again in the same session, append to them rather than overwriting them.
         * This is useful in case a script compiles the same source root several times
         * with different includes using a different classpath each time.
         * The IDE cannot really model this, but it can offer the union of those classpaths.
         */
        final Map<String,List<String>> writtenKeys = new HashMap<String,List<String>>();

    }

    @Override
    public void taskFinished(AntEvent event) {
        TaskStructure task = event.getTaskStructure();
        if (task == null) {
            return;
        }
        
        String baseDir = event.evaluate("${basedir}");
        
        // war= destfile/warfile attr
        String war = getFileAttribute(task, "warfile", event);
        if (war == null) {
            war = getFileAttribute(task, "destfile", event);
        }
        Cache.put(baseDir + WebCacheConstants.WAR_FILE, war);
        
        // web.xml = webxml attr
        String webxml = getFileAttribute(task, "webxml", event);
        Cache.put(baseDir + WebCacheConstants.WEB_XML, webxml);

        
        // classpath=lib files + classes filesets
        List<String> classpath = new ArrayList<String>();
        // docroot= war-task's filesets
        List<String> docroots = new ArrayList<String>();
        // webinf dir=webinf's filesets
        List<String> webinfs = new ArrayList<String>();
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals("lib") || child.getName().equals("classes")) {
                appendFileSet(child, event, classpath);
            }
            if (child.getName().equals("fileset")) {
                if (child.getAttribute("dir") != null) {
                    docroots.add(getFileAttribute(child, "dir", event));
                } else {
                    // TODO: does this make sense? if it is file then add its parent; other option would be to ignore the file
                    File f = new File(getFileAttribute(task, "file", event));
                    docroots.add(f.getParentFile().getAbsolutePath());
                }
            }
            if (child.getName().equals("webinf")) {
                if (child.getAttribute("dir") != null) {
                    webinfs.add(getFileAttribute(child, "dir", event));
                } else {
                    // TODO: does this make sense? if it is file then add its parent; other option would be to ignore the file
                    File f = new File(getFileAttribute(task, "file", event));
                    webinfs.add(f.getParentFile().getAbsolutePath());
                }
            }
        }
        
        State state = (State) event.getSession().getCustomData(this);
        if (state == null) {
            state = new State();
            event.getSession().putCustomData(this, state);
        }
        writePath(baseDir + WebCacheConstants.CLASSPATH, classpath, state);
        writePath(baseDir + WebCacheConstants.DOCROOT, docroots, state);
        writePath(baseDir + WebCacheConstants.WEBINF, webinfs, state);
    }
    
    private static void writePath(String key, List<String> path, State state) {
        List<String> writtenPath;
        synchronized (state.writtenKeys) {
            writtenPath = state.writtenKeys.get(key);
            if (writtenPath == null) {
                writtenPath = new ArrayList<String>();
            }
            for (String piece : path) {
                if (!writtenPath.contains(piece)) {
                    writtenPath.add(piece);
                }
            }
            state.writtenKeys.put(key, writtenPath);
        }
        Cache.put(key, join(writtenPath));
    }

    private static void scanPath(File dir, String prefix, PathMatcher m, List<String> entries) {
        for (String n : dir.list()) {
            File f = new File(dir, n);
            if (f.isDirectory()) {
                scanPath(f, prefix + n + "/", m, entries);
            } else if (m.matches(prefix + n, false)) {
                entries.add(f.getAbsolutePath());
            }
        }
    }

    private static String join(List<String> path) {
        StringBuilder b = new StringBuilder();
        for (String p : path) {
            if (b.length() > 0) {
                b.append(File.pathSeparatorChar);
            }
            b.append(p);
        }
        return b.toString();
    }

    private static String getFileAttribute(TaskStructure task, String attributeName, AntEvent event) {
        String value = task.getAttribute(attributeName);
        if (value == null) {
            return null;
        }
        value = event.evaluate(value);
        File f = new File(value);
        if (!f.isAbsolute()) {
            value = FileUtil.normalizeFile(new File(event.evaluate("${basedir}/" + value))).getAbsolutePath();
        }
        value = FileUtil.normalizeFile(new File(value)).getAbsolutePath();
        return value;
    }

    private static void appendFileSet(TaskStructure c, AntEvent event, List<String> entries) {
        String dir = getFileAttribute(c, "dir", event);
        if (dir != null) {
            File d = new File(dir);
            String includes = "";
            String excludes = "";
            String a = c.getAttribute("includes");
            if (a != null) {
                includes = event.evaluate(a);
            }
            a = c.getAttribute("excludes");
            if (a != null) {
                excludes = event.evaluate(a);
            }
            for (TaskStructure inex : c.getChildren()) {
                if (inex.getName().equals("include")) {
                    if (includes.length() > 0) {
                        includes += ",";
                    }
                    includes += event.evaluate(inex.getAttribute("name"));
                } else if (inex.getName().equals("exclude")) {
                    if (excludes.length() > 0) {
                        excludes += ",";
                    }
                    excludes += event.evaluate(inex.getAttribute("name"));
                } else {
                    LOG.warning("Ignoring unknown fileset structure child <" + c.getName() + "> in " + event.getScriptLocation());
                }
            }
            if (includes.length() == 0) {
                includes = "**";
            }
            PathMatcher m = new PathMatcher(includes, excludes, d);
            scanPath(d, "", m, entries);
        } else {
            assert c.getAttribute("file") != null : "Expecting dir or file: " + c.getAttributeNames();
            entries.add(getFileAttribute(c, "file", event));
        }
    }

}
