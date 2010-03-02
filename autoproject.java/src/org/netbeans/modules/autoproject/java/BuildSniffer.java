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

import java.io.BufferedReader;
import org.netbeans.modules.autoproject.spi.Cache;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.spi.AntEvent;
import org.apache.tools.ant.module.spi.AntLogger;
import org.apache.tools.ant.module.spi.AntSession;
import org.apache.tools.ant.module.spi.TaskStructure;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.spi.AutomaticProjectMarker;
import org.netbeans.spi.java.project.support.JavadocAndSourceRootDetection;
import org.netbeans.spi.project.support.ant.PathMatcher;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;

/**
 * Tracks progress of Ant builds and looks for calls to important tasks like javac.
 * These are analyzed for interesting information.
 */
@ServiceProvider(service=AntLogger.class)
public class BuildSniffer extends AntLogger {

    private static final Logger LOG = Logger.getLogger(BuildSniffer.class.getName());

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

    private final String[] INTERESTING_TASKS = {"javac", "fileset", "jar"};
    @Override
    public String[] interestedInTasks(AntSession session) {
        return INTERESTING_TASKS;
    }

    /**
     * Per-session state.
     */
    private static class State {

        /**
         * Cache keys of any sort to be written in this session.
         * Prefer to write them all at once at end of build to minimize the event storm
         * and subsequent rescanning.
         */
        final Map<String,String> toWrite = new LinkedHashMap<String,String>();

        /**
         * Cache keys of path type already written during this session.
         * If they come up again in the same session, append to them rather than overwriting them.
         * This is useful in case a script compiles the same source root several times
         * with different includes and possibly using a different classpath each time.
         * The IDE cannot really model this, but it can offer the union of those classpaths and/or includes.
         */
        final Map<String,Set<String>> writtenKeys = new HashMap<String,Set<String>>();

        /**
         * Binary destination dirs mapped back to source dirs.
         * If more than one source root gets mapped to a given binary dir,
         * we consolidate them into one compilation unit for Retouche's benefit.
         */
        final Map<String,Set<String>> sourceForBinary = new HashMap<String,Set<String>>();

        /**
         * Base dirs of filesets.
         * Since AbstractFileSet.toString prints only relative path names,
         * we need to keep track of these when the fileset is defined,
         * so we can later handle appending of <fileset refid='...'/> to a CP.
         * The values are not resolved yet, as necessary properties might not yet be defined.
         */
        final Map<String,String> filesetBasedirs = new HashMap<String,String>();

    }

    public @Override void buildFinished(AntEvent event) {
        State state = (State) event.getSession().getCustomData(this);
        if (state == null) {
            return;
        }
        for (Map.Entry<String,String> entry : state.toWrite.entrySet()) {
            Cache.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void taskFinished(AntEvent event) {
        State state = (State) event.getSession().getCustomData(this);
        if (state == null) {
            state = new State();
            event.getSession().putCustomData(this, state);
        }
        String taskName = event.getTaskName();
        if (taskName.equals("fileset")) {
            handleFileset(event, state);
        } else if (taskName.equals("javac")) {
            handleJavac(event, state);
        } else if (taskName.equals("jar")) {
            handleJar(event, state);
        } else {
            assert false : event;
        }
    }

    private void handleFileset(AntEvent event, State state) {
        String id = event.getTaskStructure().getAttribute("id");
        String dir = event.getTaskStructure().getAttribute("dir");
        if (id != null && dir != null) {
            state.filesetBasedirs.put(id, dir);
        }
    }

    private void handleJavac(AntEvent event, State state) {
        TaskStructure task = event.getTaskStructure();
        if (task == null) {
            return;
        }
        List<String> sources = new ArrayList<String>();
        appendPath(task.getAttribute("srcdir"), event, sources, true);
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals("src")) {
                appendPathStructure(child, event, sources, state);
            }
        }
        List<String> _destdir = new ArrayList<String>();
        appendPath(task.getAttribute("destdir"), event, _destdir, false);
        assert _destdir.size() <= 1;
        String destdir = _destdir.isEmpty() ? null : _destdir.get(0);
        String buildSysclasspath = event.getProperty("build.sysclasspath");
        if (buildSysclasspath == null) {
            String includeAntRuntime = task.getAttribute("includeantruntime");
            // XXX need AntEvent method to check whether a value is true
            if (includeAntRuntime != null && event.evaluate(includeAntRuntime).matches("(?i)false|no|off")) {
                buildSysclasspath = "ignore";
            } else {
                buildSysclasspath = "last";
            }
        }
        if (!buildSysclasspath.equals("ignore")) { // warn that this is a bad idea
            File script = event.getScriptLocation();
            if (script != null) {
                String message = "set includeantruntime=\"false\" on <javac> for repeatable builds";
                try {
                    event.getSession().println(script + ":" + event.getLine() + ": warning: " + message, true,
                            event.getSession().createStandardHyperlink(script.toURI().toURL(), message, event.getLine(), -1, -1, -1));
                } catch (MalformedURLException x) {
                    LOG.log(Level.WARNING, null, x);
                }
            }
        }
        List<String> classpath = new ArrayList<String>();
        if (buildSysclasspath.matches("only|first")) {
            appendJavaClassPath(classpath);
        }
        if (!buildSysclasspath.equals("only")) {
            appendPath(task.getAttribute("classpath"), event, classpath, true);
            String cpref = task.getAttribute("classpathref");
            if (cpref != null) {
                appendPath(event.getProperty(cpref), event, classpath, true);
            }
            for (TaskStructure child : task.getChildren()) {
                if (child.getName().equals("classpath")) {
                    appendPathStructure(child, event, classpath, state);
                }
            }
        }
        if (buildSysclasspath.equals("last")) {
            appendJavaClassPath(classpath);
        }
        List<String> bootclasspath = new ArrayList<String>();
        // XXX pay attention to build.sysclasspath if defined
        appendPath(task.getAttribute("bootclasspath"), event, bootclasspath, true);
        String bootcpref = task.getAttribute("bootclasspathref");
        if (bootcpref != null) {
            appendPath(event.getProperty(bootcpref), event, bootclasspath, true);
        }
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals("bootclasspath")) {
                appendPathStructure(child, event, bootclasspath, state);
            }
        }
        // Check to see if source roots are correct; srcdir on <javac> is sometimes wrong.
        ListIterator<String> sourcesIt = sources.listIterator();
        while (sourcesIt.hasNext()) {
            String s = sourcesIt.next();
            File origRoot = new File(s);
            FileObject origRootFO = FileUtil.toFileObject(origRoot);
            if (origRootFO != null && origRootFO.isFolder()) {
                FileObject realRootFO = JavadocAndSourceRootDetection.findSourceRoot(origRootFO);
                if (realRootFO != null && realRootFO != origRootFO) {
                    File realRoot = FileUtil.toFile(realRootFO);
                    LOG.log(Level.FINE, "Corrected root {0} to {1} based on package decl", new Object[] {origRoot, realRoot});
                    sourcesIt.set(realRoot.getAbsolutePath());
                }
            }
        }
        if (destdir != null) {
            Set<String> otherSourceRoots = state.sourceForBinary.get(destdir);
            if (otherSourceRoots != null) {
                otherSourceRoots.addAll(sources);
                sources = new ArrayList<String>(otherSourceRoots);
            } else {
                otherSourceRoots = new LinkedHashSet<String>(sources);
                state.sourceForBinary.put(destdir, otherSourceRoots);
            }
        }
        String processorPath = null;
        StringBuilder processorOptions = null;
        List<String> compilerargs = new ArrayList<String>();
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals("compilerarg")) {
                compilerargs.addAll(parseCLI(child, event));
            }
        }
        Iterator<String> compilerargsIt = compilerargs.iterator();
        while (compilerargsIt.hasNext()) {
            String arg = compilerargsIt.next();
            if (arg.matches("-proc:.+|-A.+")) {
                if (processorOptions == null) {
                    processorOptions = new StringBuilder();
                } else {
                    processorOptions.append(' ');
                }
                processorOptions.append(arg);
            } else if (arg.matches("-s|-processor")) {
                if (processorOptions == null) {
                    processorOptions = new StringBuilder();
                } else {
                    processorOptions.append(' ');
                }
                processorOptions.append(arg).append(' ').append(compilerargsIt.next());
            } else if (arg.equals("-processorpath")) {
                processorPath = compilerargsIt.next();
            }
        }
        List<String> includes = findInclExclProp(event, task, "includes", "includesfile", "include", true);
        List<String> excludes = findInclExclProp(event, task, "excludes", "excludesfile", "exclude", false);
        for (String s : sources) {
            String includesKey = s + JavaCacheConstants.INCLUDES;
            if (includes.isEmpty()) {
                state.toWrite.put(includesKey, null);
                state.writtenKeys.put(includesKey, null);
            } else {
                writePath(includesKey, includes, state, true, ',');
            }
            writePath(s + JavaCacheConstants.EXCLUDES, excludes.isEmpty() ? null : excludes, state, false, ',');
            writePath(s + JavaCacheConstants.CLASSPATH, classpath, state, true, File.pathSeparatorChar);
            if (!bootclasspath.isEmpty()) {
                writePath(s + JavaCacheConstants.BOOTCLASSPATH, bootclasspath, state, true, File.pathSeparatorChar);
            }
            if (destdir != null) {
                state.toWrite.put(s + JavaCacheConstants.BINARY, destdir);
            }
            String sourceLevel = task.getAttribute("source");
            if (sourceLevel != null) {
                String level = event.evaluate(sourceLevel);
                if (level.matches("\\d+")) {
                    level = "1." + level;
                }
                state.toWrite.put(s + JavaCacheConstants.SOURCE_LEVEL, level);
            }
            String encoding = task.getAttribute("encoding");
            if (encoding != null) {
                state.toWrite.put(s + Cache.ENCODING, event.evaluate(encoding));
            }
            if (processorPath != null) {
                state.toWrite.put(s + JavaCacheConstants.PROCESSORPATH, processorPath);
            }
            if (processorOptions != null) {
                state.toWrite.put(s + JavaCacheConstants.PROCESSOR_OPTIONS, processorOptions.toString());
            }
            writePath(s + JavaCacheConstants.SOURCE, sources, state, true, File.pathSeparatorChar);
        }
    }

    /** Parses a command-line-arguments element. */
    private List<String> parseCLI(TaskStructure cli, AntEvent event) {
        List<String> r = new ArrayList<String>();
        if (cli.getAttribute("value") != null) {
            r.add(event.evaluate(cli.getAttribute("value")));
        } else if (cli.getAttribute("file") != null) {
            r.add(resolve(event, event.evaluate(cli.getAttribute("file"))).getAbsolutePath());
        } else if (cli.getAttribute("path") != null) {
            StringBuilder b = new StringBuilder();
            for (String piece : event.evaluate(cli.getAttribute("path")).split(File.pathSeparator)) {
                if (!piece.isEmpty()) {
                    if (b.length() > 0) {
                        b.append(File.pathSeparatorChar);
                    }
                    b.append(resolve(event, piece));
                }
            }
            r.add(b.toString());
        } else if (cli.getAttribute("pathref") != null) {
            StringBuilder b = new StringBuilder();
            for (String piece : event.getProperty(cli.getAttribute("pathref")).split(File.pathSeparator)) {
                if (!piece.isEmpty()) {
                    if (b.length() > 0) {
                        b.append(File.pathSeparatorChar);
                    }
                    b.append(resolve(event, piece));
                }
            }
            r.add(b.toString());
        } else if (cli.getAttribute("line") != null) {
            r.addAll(Arrays.asList(event.evaluate(cli.getAttribute("line")).split(" ")));
        } else {
            LOG.warning("strange CLI object had no expected attributes");
        }
        String prefix = cli.getAttribute("prefix");
        String suffix = cli.getAttribute("suffix");
        if (prefix != null || suffix != null) {
            ListIterator<String> it = r.listIterator();
            while (it.hasNext()) {
                it.set((prefix != null ? prefix : "") + it.next() + (suffix != null ? suffix : ""));
            }
        }
        return r;
    }

    private List<String> findInclExclProp(AntEvent event, TaskStructure task, String prop, String fileprop, String nested, boolean includesMode) {
        List<String> val = new ArrayList<String>();
        String propV = task.getAttribute(prop);
        if (propV != null) {
            for (String pattern : event.evaluate(propV).split("[, ]+")) {
                addInclExcl(val, pattern, includesMode);
            }
        }
        String fileV = task.getAttribute(fileprop);
        if (fileV != null) {
            File f = resolve(event, event.evaluate(fileV));
            if (f.isFile()) {
                try {
                    Reader r = new FileReader(f);
                    try {
                        BufferedReader b = new BufferedReader(r);
                        String line;
                        while ((line = b.readLine()) != null) {
                            addInclExcl(val, line, includesMode);
                        }
                    } finally {
                        r.close();
                    }
                } catch (IOException x) {
                    LOG.log(Level.INFO, "failed to read " + f, x);
                }
            }
        }
        for (TaskStructure child : task.getChildren()) {
            if (child.getName().equals(nested)) {
                String ifProp = child.getAttribute("if");
                if (ifProp != null && event.getProperty(ifProp) == null) {
                    continue;
                }
                String unlessProp = child.getAttribute("unless");
                if (unlessProp != null && event.getProperty(unlessProp) != null) {
                    continue;
                }
                addInclExcl(val, event.evaluate(child.getAttribute("name")), includesMode);
            }
        }
        return val;
    }
    private void addInclExcl(List<String> val, String pattern, boolean includesMode) {
        if (pattern.length() == 0) {
            return;
        }
        if (includesMode && pattern.equals("**/*.java")) {
            // Of course we are including Java source files! Poorly written build script.
            // Ignore, or we would not include folders from SourceGroup.contains!
            return;
        }
        if (pattern.endsWith("/**")) {
            // Simplify a bit for readability...
            pattern = pattern.substring(0, pattern.length() - 2);
        } else if (pattern.endsWith("/**/*.*")) {
            // Again a poorly written build script which really meant to exclude the _folder_.
            pattern = pattern.substring(0, pattern.length() - 6);
        }
        val.add(pattern);
    }

    private void handleJar(AntEvent event, State state) {
        String jar = event.getTaskStructure().getAttribute("destfile");
        if (jar == null) {
            jar = event.getTaskStructure().getAttribute("jarfile");
        }
        if (jar == null) {
            return;
        }
        String key = resolve(event, jar) + JavaCacheConstants.JAR;
        List<String> basedirs = new ArrayList<String>();
        String basedir = event.getTaskStructure().getAttribute("basedir");
        if (basedir != null) {
            basedirs.add(resolve(event, basedir).getAbsolutePath());
        }
        for (TaskStructure child : event.getTaskStructure().getChildren()) {
            if (child.getName().equals("fileset")) {
                basedir = child.getAttribute("dir");
                if (basedir != null) {
                    basedirs.add(resolve(event, basedir).getAbsolutePath());
                }
            }
        }
        writePath(key, basedirs, state, true, File.pathSeparatorChar);
    }

    private static void writePath(String key, List<String> path, State state, boolean union, char separator) {
        Set<String> writtenPath;
        synchronized (state.writtenKeys) {
            writtenPath = state.writtenKeys.get(key);
            if (writtenPath == null) {
                if (path != null && /*#177718*/!state.writtenKeys.containsKey(key)) {
                    writtenPath = new LinkedHashSet<String>(path);
                    state.writtenKeys.put(key, writtenPath);
                }
            } else if (union) {
                if (path != null) {
                    writtenPath.addAll(path);
                }
            } else {
                if (path != null) {
                    writtenPath.retainAll(path);
                } else {
                    writtenPath = null;
                }
            }
        }
        state.toWrite.put(key, writtenPath != null ? join(writtenPath, separator) : null);
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
            entries.add(resolve(event, piece).getAbsolutePath());
        }
    }

    private static void appendJavaClassPath(List<String> entries) {
        File jdkLibs = canonicalizeFile(new File(new File(System.getProperty("java.home")).getParentFile(), "lib"));
        for (String piece : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (piece.length() == 0) {
                continue;
            }
            File entry = new File(piece);
            if (!canonicalizeFile(entry).getParentFile().equals(jdkLibs) || entry.getName().equals("tools.jar")) {
                // XXX probably also safe to collapse ant/lib/*.jar (and java2/ant/patches/*.jar) to ant/lib/ant.jar
                entries.add(FileUtil.normalizeFile(entry).getAbsolutePath());
            }
        }
    }
    private static File canonicalizeFile(File f) {
        try {
            return f.getCanonicalFile();
        } catch (IOException x) {
            // ignore and use original
            return f;
        }
    }

    /**
     * Try to resolve a file path.
     * XXX would preferably be a method in AntEvent itself.
     * @param event an event for context
     * @param file an <em>evaluated</em> file path, relative or absolute
     * @return the normalized, absolute file
     */
    private static File resolve(AntEvent event, String file) {
        File f = new File(file);
        if (!f.isAbsolute()) {
            f = new File(event.getProperty("basedir"), file);
        }
        return FileUtil.normalizeFile(f);
    }

    private static void appendPathStructure(TaskStructure s, AntEvent event, List<String> entries, State state) {
        appendPath(s.getAttribute("location"), event, entries, false);
        appendPath(s.getAttribute("path"), event, entries, true);
        String ref = s.getAttribute("refid");
        if (ref != null) {
            appendPath(event.getProperty(ref), event, entries, true);
        }
        for (TaskStructure c : s.getChildren()) {
            if (c.getName().equals("path")) {
                appendPathStructure(c, event, entries, state);
            } else if (c.getName().equals("pathelement")) {
                appendPath(c.getAttribute("path"), event, entries, true);
                appendPath(c.getAttribute("location"), event, entries, false);
            } else if (c.getName().equals("fileset")) {
                String dir = c.getAttribute("dir");
                if (dir != null) {
                    File d = resolve(event, event.evaluate(dir));
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
                ref = c.getAttribute("refid");
                if (ref != null) {
                    String basedir = state.filesetBasedirs.get(ref);
                    if (basedir != null) {
                        String includedFiles = event.getProperty(ref);
                        if (includedFiles != null) {
                            for (String include : includedFiles.split("[:;]")) {
                                appendPath(basedir + "/" + include, event, entries, false);
                            }
                        } else {
                            LOG.warning("Cannot evaluate <fileset refid='" + ref + "'/> in " + event.getScriptLocation());
                        }
                    } else {
                        LOG.warning("Unknown basedir for <fileset refid='" + ref + "'/> in " + event.getScriptLocation());
                    }
                }
            } else if (c.getName().equals("dirset")) {
                // OpenDS uses the silly construct:
                // <classpath><dirset dir="${classes.dir}"/></classpath>
                // Of course this is bogus - you do NOT want to add every subpackage to the CP! - but that is what it does.
                // What it MEANT to do was:
                // <classpath><pathelement location="${classes.dir}"/></classpath>
                // so pretend that is what they actually wrote.
                appendPath(c.getAttribute("dir"), event, entries, false);
            } else {
                LOG.warning("Ignoring unknown path-like structure child <" + c.getName() + "> in " + event.getScriptLocation());
            }
        }
    }
    private static void scanPath(File dir, String prefix, PathMatcher m, List<String> entries) {
        String[] kids = dir.list();
        if (kids == null) {
            return;
        }
        for (String n : kids) {
            File f = new File(dir, n);
            if (f.isDirectory()) {
                scanPath(f, prefix + n + "/", m, entries);
            } else if (m.matches(prefix + n, false)) {
                entries.add(f.getAbsolutePath());
            }
        }
    }

    private static String join(Iterable<String> path, char separator) {
        StringBuilder b = new StringBuilder();
        for (String p : path) {
            if (b.length() > 0) {
                b.append(separator);
            }
            b.append(p);
        }
        return b.toString();
    }

}
