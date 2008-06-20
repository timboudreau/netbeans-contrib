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

package org.netbeans.modules.autoproject.java.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.TypeElement;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.project.Project;
import org.netbeans.modules.autoproject.java.JavaCacheConstants;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.project.ActionProvider;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 * Implements certain actions directly, and tries to delegate to the build script for others.
 */
public class ActionProviderImpl implements ActionProvider {

    private static final Logger LOG = Logger.getLogger(ActionProviderImpl.class.getName());

    private final Project p;

    public ActionProviderImpl(Project p) {
        this.p = p;
    }

    public String[] getSupportedActions() {
        return new String[] {
            ActionProvider.COMMAND_BUILD,
            ActionProvider.COMMAND_CLEAN,
            ActionProvider.COMMAND_REBUILD,
            ActionProvider.COMMAND_COMPILE_SINGLE,
            ActionProvider.COMMAND_RUN,
            ActionProvider.COMMAND_RUN_SINGLE,
            /* XXX could be added pretty easily, though minus junit module integration:
            ActionProvider.COMMAND_TEST,
            ActionProvider.COMMAND_TEST_SINGLE,
             */
            JavaProjectConstants.COMMAND_JAVADOC,
            /* XXX no debugger integration yet:
            ActionProvider.COMMAND_DEBUG,
            ActionProvider.COMMAND_DEBUG_SINGLE,
            ActionProvider.COMMAND_DEBUG_STEP_INTO,
            ActionProvider.COMMAND_DEBUG_TEST_SINGLE,
            JavaProjectConstants.COMMAND_DEBUG_FIX,
             */
            // XXX deploy, redeploy
            // XXX rename, move, copy, delete
            // XXX profile and debug should work by calling an Ant target for run but using presetdef to override <java> to add extra args
        };
    }

    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (command.equals(ActionProvider.COMMAND_COMPILE_SINGLE)) {
            return compileSetup(context) != null;
        } else if (command.equals(ActionProvider.COMMAND_RUN_SINGLE)) {
            return runSetup(context, false) != null;
        } else {
            return true;
        }
    }

    public void invokeAction(String command, final Lookup context) throws IllegalArgumentException {
        if (command.equals(ActionProvider.COMMAND_COMPILE_SINGLE)) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    doCompile(compileSetup(context));
                }
            });
        } else if (command.equals(ActionProvider.COMMAND_RUN_SINGLE)) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    CompileSetup compileSetup = compileSetup(context);
                    if (compileSetup != null) {
                        boolean ok = doCompile(compileSetup);
                        if (!ok) {
                            return;
                        }
                    }
                    doRun(runSetup(context, true));
                }
            });
        } else {
            String key = FileUtil.toFile(p.getProjectDirectory()) + Cache.ACTION + command;
            String binding = Cache.get(key);
            if (binding == null) {
                // XXX for COMMAND_REBUILD, should just look for bindings for CLEAN and BUILD and put them in order
                new UnboundTargetAlert(p, command).accepted();
                binding = Cache.get(key);
                if (binding == null) {
                    return;
                }
            }
            String[] protocolScriptAndTargets = binding.split(":", 3);
            if (protocolScriptAndTargets[0].equals("ant")) {
                final FileObject script = p.getProjectDirectory().getFileObject(protocolScriptAndTargets[1]); // XXX accept also absolute paths
                if (script == null) {
                    return; // XXX warn
                }
                String[] targets = protocolScriptAndTargets[2].split("\\s+");
                if (targets.length == 0) {
                    targets = null;
                }
                // XXX support -D options, perhaps
                final String[] _targets = targets;
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        try {
                            ActionUtils.runTarget(script, _targets, null);
                        } catch (IOException x) {
                            LOG.log(Level.WARNING, null, x);
                        }
                    }
                });
            } else {
                // XXX support scripting, perhaps
            }
        }
    }

    private static class CompileSetup {
        final Set<File> diskfiles;
        final List<String> options;
        final File destdir;
        public CompileSetup(Set<File> diskfiles, List<String> options, File destdir) {
            this.diskfiles = diskfiles;
            this.options = options;
            this.destdir = destdir;
        }
    }
    private CompileSetup/*|null*/ compileSetup(Lookup context) {
        Set<File> diskfiles = new HashSet<File>();
        File srcdir = null;
        List<String> options = new ArrayList<String>();
        for (DataObject d : context.lookupAll(DataObject.class)) {
            File f = FileUtil.toFile(d.getPrimaryFile());
            if (f == null) {
                // JAR entry, etc.
                return null;
            }
            if (!f.getName().endsWith(".java")) {
                // Some other file selected; could just ignore?
                // XXX would be useful to let you select a package and compile that
                return null;
            }
            if (srcdir != null && !f.getAbsolutePath().startsWith(srcdir.getAbsolutePath())) {
                // Do not currently support compiling files from several roots at once.
                return null;
            } else if (srcdir == null) {
                for (Map.Entry<String,String> entry : Cache.pairs()) {
                    if (entry.getKey().endsWith(JavaCacheConstants.SOURCE)) {
                        String src = entry.getKey().substring(0, entry.getKey().length() - JavaCacheConstants.SOURCE.length());
                        if (f.getAbsolutePath().startsWith(src)) {
                            srcdir = new File(src);
                            options.add("-sourcepath");
                            options.add(entry.getValue());
                            break;
                        }
                    }
                }
                if (srcdir == null) {
                    // Not a file in a known source root.
                    return null;
                }
            }
            diskfiles.add(f);
        }
        if (diskfiles.isEmpty()) {
            // Nothing compilable selected.
            return null;
        }
        assert srcdir != null;
        String d = Cache.get(srcdir + JavaCacheConstants.BINARY);
        if (d == null) {
            // Don't know where to send it.
            return null;
        }
        options.add("-d");
        options.add(d);
        File destdir = new File(d);
        String cp = Cache.get(srcdir + JavaCacheConstants.CLASSPATH);
        if (cp == null) {
            // Don't know how to compile it. (Note: distinct from empty CP.)
            return null;
        }
        if (cp.length() > 0) {
            options.add("-classpath");
            options.add(cp);
        }
        String sourceLevel = Cache.get(srcdir + JavaCacheConstants.SOURCE_LEVEL);
        if (sourceLevel != null) {
            options.add("-source");
            options.add(sourceLevel);
            // XXX might be better to get this from BuildSniffer:
            options.add("-target");
            options.add(sourceLevel);
        } else {
            // Set up to be runnable from default JDK!
            options.add("-target");
            options.add(JavaPlatformManager.getDefault().getDefaultPlatform().getSpecification().getVersion().toString());
        }
        String encoding = Cache.get(srcdir + Cache.ENCODING);
        if (encoding != null) {
            options.add("-encoding");
            options.add(encoding);
        }
        // XXX any other javac options? -debug etc.?
        return new CompileSetup(diskfiles, options, destdir);
    }
    private boolean doCompile(CompileSetup compileSetup) {
        try {
            for (DataObject modified : DataObject.getRegistry().getModified()) {
                SaveCookie save = modified.getCookie(SaveCookie.class);
                if (save != null) {
                    save.save();
                }
            }
            boolean ok = Compiler.compile(compileSetup.diskfiles, compileSetup.options);
            FileUtil.refreshFor(compileSetup.destdir);
            return ok;
        } catch (IOException x) {
            LOG.log(Level.WARNING, null, x);
            return false;
        }
    }

    private static class RunSetup {
        final List<String> options;
        final File cwd;
        public RunSetup(List<String> options, File cwd) {
            this.options = options;
            this.cwd = cwd;
        }
    }
    private RunSetup/*|null*/ runSetup(Lookup context, boolean block) {
        Collection<? extends DataObject> ds = context.lookupAll(DataObject.class);
        if (ds.size() != 1) {
            // No selection, or multiselection.
            return null;
        }
        FileObject fo = ds.iterator().next().getPrimaryFile();
        File f = FileUtil.toFile(fo);
        if (f == null) {
            // JAR selection?
            return null;
        }
        if (!f.getName().endsWith(".java")) {
            // Some other kind of file selected.
            return null;
        }
        final List<String> options = new ArrayList<String>();
        for (Map.Entry<String, String> entry : Cache.pairs()) {
            if (entry.getKey().endsWith(JavaCacheConstants.SOURCE)) {
                String src = entry.getKey().substring(0, entry.getKey().length() - JavaCacheConstants.SOURCE.length());
                if (f.getAbsolutePath().startsWith(src)) {
                    String clazz = Cache.get(src + JavaCacheConstants.BINARY);
                    if (clazz == null) {
                        // Not known where it is compiled to;
                        return null;
                    }
                    String cp = Cache.get(src + JavaCacheConstants.CLASSPATH);
                    if (cp == null) {
                        // No known classpath for it.
                        return null;
                    }
                    options.add("-classpath");
                    // XXX could add source path to pick up noncopied resources?
                    // XXX may in general be necessary to add other entries to run CP, TBD how to guess this...
                    options.add(clazz + File.pathSeparator + cp);
                    final String name = f.getAbsolutePath().substring(src.length() + File.separator.length()).
                            replace('/', '.').replaceAll("^[.]|[.]java$", "");
                    if (block) {
                        // We can block waiting for parse, and figure out whether to run as a test.
                        try {
                            JavaSource.create(ClasspathInfo.create(fo)).runWhenScanFinished(new Task<CompilationController>() {
                                public void run(CompilationController cc) throws Exception {
                                    TypeElement runType = cc.getElements().getTypeElement(name);
                                    TypeElement testCase = cc.getElements().getTypeElement("junit.framework.TestCase");
                                    if (testCase != null && cc.getTypes().isAssignable(runType.asType(), testCase.asType())) {
                                        options.add("junit.textui.TestRunner");
                                    }
                                    // XXX also classes with a "public static junit.framework.Test suite()" method
                                    // XXX also classes containing public methods annotated with @org.junit.Test (run with org.junit.runner.JUnitCore)
                                }
                            }, true).get();
                        } catch (Exception x) {
                            Exceptions.printStackTrace(x);
                        }
                    }
                    options.add(name);
                    return new RunSetup(options, FileUtil.toFile(p.getProjectDirectory()));
                }
            }
        }
        // Not in a known source root.
        return null;
    }
    private void doRun(final RunSetup runSetup) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    Runner.runJava(runSetup.options, runSetup.cwd);
                } catch (IOException x) {
                    LOG.log(Level.WARNING, null, x);
                }
            }
        });
    }

}
