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

package org.netbeans.modules.autoproject.java;

import org.netbeans.modules.autoproject.spi.Cache;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.PathMatcher;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

// XXX check against scenarios here: http://wiki.netbeans.org/OSProjectsEvaluation

/**
 * Tracks progress of Ant builds and looks for calls to important tasks like javac.
 * These are analyzed for interesting information.
 */
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
        return p.getLookup().lookup(ClassPathProviderImpl.class) != null;
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
        // XXX may also want/need: jar, delete, copy, javadoc, ...
        return new String[] {"javac"};
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
        List<String> sources = new ArrayList<String>();
        List<String> classpath = new ArrayList<String>();
        List<String> destdir = new ArrayList<String>();
        appendPath(task.getAttribute("srcdir"), event, sources, true);
        appendPath(task.getAttribute("destdir"), event, destdir, false);
        appendPath(task.getAttribute("classpath"), event, classpath, true);
        String cpref = task.getAttribute("classpathref");
        if (cpref != null) {
            appendPath(event.getProperty(cpref), event, classpath, true);
        }
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals("src")) {
                appendPathStructure(child, event, sources);
            } else if (child.getName().equals("classpath")) {
                appendPathStructure(child, event, classpath);
            }
        }
        State state = (State) event.getSession().getCustomData(this);
        if (state == null) {
            state = new State();
            event.getSession().putCustomData(this, state);
        }
        for (String s : sources) {
            // Check to see if this is a real root. srcdir on <javac> is sometimes wrong.
            File origRoot = new File(s);
            File realRoot = checkForRealRoot(origRoot);
            if (realRoot != null && !realRoot.equals(origRoot)) {
                LOG.log(Level.FINE, "Corrected root {0} to {1} based on package decl", new Object[] {origRoot, realRoot});
                s = realRoot.getAbsolutePath();
            }
            writePath(s + JavaCacheConstants.SOURCE, sources, state);
            writePath(s + JavaCacheConstants.CLASSPATH, classpath, state);
            if (!destdir.isEmpty()) {
                assert destdir.size() == 1;
                Cache.put(s + JavaCacheConstants.BINARY, destdir.get(0));
            }
            // XXX could also sniff JavaCacheConstants.BOOTCLASSPATH if specified
            String sourceLevel = task.getAttribute("source");
            if (sourceLevel != null) {
                Cache.put(s + JavaCacheConstants.SOURCE_LEVEL, event.evaluate(sourceLevel));
            }
            String encoding = task.getAttribute("encoding");
            if (encoding != null) {
                Cache.put(s + Cache.ENCODING, event.evaluate(encoding));
            }
        }
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

    private static void appendPath(String raw, AntEvent event, List<String> entries, boolean split) {
        if (raw == null) {
            return;
        }
        String eval = event.evaluate(raw);
        for (String piece : split ? eval.split("[:;]") : new String[] {eval}) {
            if (piece.contains("${") || piece.length() == 0) {
                continue;
            }
            // XXX would be nice if AntEvent had a handy method to resolve relative file paths against basedir...
            File f = new File(piece);
            if (!f.isAbsolute()) {
                f = new File(event.evaluate("${basedir}/" + piece));
            }
            entries.add(FileUtil.normalizeFile(f).getAbsolutePath());
        }
    }

    private static void appendPathStructure(TaskStructure s, AntEvent event, List<String> entries) {
        appendPath(s.getAttribute("path"), event, entries, true);
        String ref = s.getAttribute("refid");
        if (ref != null) {
            appendPath(event.getProperty(ref), event, entries, true);
        }
        for (TaskStructure c : s.getChildren()) {
            if (c.getName().equals("path")) {
                appendPathStructure(c, event, entries);
            } else if (c.getName().equals("pathelement")) {
                appendPath(s.getAttribute("path"), event, entries, true);
                appendPath(s.getAttribute("location"), event, entries, false);
            } else if (c.getName().equals("fileset")) {
                String dir = c.getAttribute("dir");
                if (dir != null) {
                    File d = FileUtil.normalizeFile(new File(event.evaluate(dir)));
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
                }
            } else {
                LOG.warning("Ignoring unknown path-like structure child <" + c.getName() + "> in " + event.getScriptLocation());
            }
        }
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

    private static File checkForRealRoot(File f) {
        if (f.isDirectory()) {
            for (File kid : f.listFiles()) {
                File root = checkForRealRoot(kid);
                if (root != null) {
                    return root;
                }
            }
        } else if (f.isFile() && f.getName().endsWith(".java")) {
            return ClassPathProviderImpl.inferRootFromPackage(f);
        }
        return null;
    }

}
