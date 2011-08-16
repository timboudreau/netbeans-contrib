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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.runner.JavaRunner;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.autoproject.java.JavaCacheConstants;
import org.netbeans.modules.autoproject.spi.Cache;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.SingleMethod;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
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
            // JavaRunner provides the impl:
            ActionProvider.COMMAND_RUN_SINGLE,
            ActionProvider.COMMAND_TEST_SINGLE,
            ActionProvider.COMMAND_DEBUG_SINGLE,
            ActionProvider.COMMAND_DEBUG_TEST_SINGLE,
            SingleMethod.COMMAND_RUN_SINGLE_METHOD,
            SingleMethod.COMMAND_DEBUG_SINGLE_METHOD,
            // You provide the impl:
            ActionProvider.COMMAND_BUILD,
            ActionProvider.COMMAND_CLEAN,
            ActionProvider.COMMAND_REBUILD,
            ActionProvider.COMMAND_RUN,
            ActionProvider.COMMAND_TEST,
            JavaProjectConstants.COMMAND_JAVADOC,
            // XXX deploy, redeploy
        };
    }

    public boolean isActionEnabled(String command, Lookup context) throws IllegalArgumentException {
        if (command.equals(ActionProvider.COMMAND_RUN_SINGLE) || command.equals(ActionProvider.COMMAND_TEST_SINGLE) ||
                command.equals(ActionProvider.COMMAND_DEBUG_SINGLE) || command.equals(ActionProvider.COMMAND_DEBUG_TEST_SINGLE) ||
                command.equals(SingleMethod.COMMAND_RUN_SINGLE_METHOD) || command.equals(SingleMethod.COMMAND_DEBUG_SINGLE_METHOD)) {
            return runSetup(context, command) != null;
        } else {
            return true;
        }
    }

    public void invokeAction(final String command, final Lookup context) throws IllegalArgumentException {
        if (command.equals(ActionProvider.COMMAND_RUN_SINGLE) || command.equals(ActionProvider.COMMAND_TEST_SINGLE) ||
                command.equals(ActionProvider.COMMAND_DEBUG_SINGLE) || command.equals(ActionProvider.COMMAND_DEBUG_TEST_SINGLE) ||
                command.equals(SingleMethod.COMMAND_RUN_SINGLE_METHOD) || command.equals(SingleMethod.COMMAND_DEBUG_SINGLE_METHOD)) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    RunSetup setup = runSetup(context, command);
                    if (setup != null) {
                        try {
                            JavaRunner.execute(setup.command, setup.props);
                        } catch (IOException x) {
                            LOG.log(Level.WARNING, null, x);
                        }
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });
        } else {
            String binding = getOrDefineCommandBinding(command, false);
            if (binding == null && command.equals(ActionProvider.COMMAND_REBUILD)) {
                // Assume it is just CLEAN and BUILD together.
                String cleanBinding = getOrDefineCommandBinding(ActionProvider.COMMAND_CLEAN, true);
                String buildBinding = getOrDefineCommandBinding(ActionProvider.COMMAND_BUILD, true);
                if (cleanBinding != null && cleanBinding.startsWith("ant:") &&
                        buildBinding != null && buildBinding.startsWith("ant:")) {
                    int colon = cleanBinding.lastIndexOf(':');
                    if (buildBinding.startsWith(cleanBinding.substring(0, colon + 1))) {
                        binding = cleanBinding + " " + buildBinding.substring(colon + 1);
                    }
                }
            }
            if (binding == null) {
                binding = getOrDefineCommandBinding(command, true);
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
                            cleanGeneratedClassfiles(p);
                            ActionUtils.runTarget(script, _targets, null);
                        } catch (IOException x) {
                            LOG.log(Level.WARNING, null, x);
                        }
                    }
                });
            }
        }
    }
    private String getOrDefineCommandBinding(String command, boolean prompt) {
        String binding = Cache.get(FileUtil.toFile(p.getProjectDirectory()) + Cache.ACTION + command);
        if (binding == null && prompt) {
            new UnboundTargetAlert(p, command).accepted();
            return getOrDefineCommandBinding(command, false);
        } else {
            return binding;
        }
    }

    static void cleanGeneratedClassfiles(Project p) throws IOException { // #145243
        List<ClassPath> executePaths = new ArrayList<ClassPath>();
        for (SourceGroup g : ProjectUtils.getSources(p).getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA)) {
            FileObject root = g.getRootFolder();
            ClassPath cp = ClassPath.getClassPath(root, ClassPath.EXECUTE);
            if (cp != null) {
                executePaths.add(cp);
            }
        }
        int res = JavaRunner.execute(JavaRunner.QUICK_CLEAN, Collections.singletonMap(
                JavaRunner.PROP_EXECUTE_CLASSPATH, ClassPathSupport.createProxyClassPath(executePaths.toArray(new ClassPath[0])))).
                result();
        if (res != 0) {
            throw new IOException("Failed to clean NetBeans-generated classes");
        }
    }

    /** @see JavaRunner */
    private static class RunSetup {
        final String command;
        final Map<String, Object> props;
        public RunSetup(String command, Map<String, Object> props) {
            this.command = command;
            this.props = props;
        }
    }
    private RunSetup/*|null*/ runSetup(Lookup context, String command) {
        String methodname;
        final FileObject fo;
        Collection<? extends SingleMethod> methods = context.lookupAll(SingleMethod.class);
        if (methods.size() == 1) {
            SingleMethod method = methods.iterator().next();
            fo = method.getFile();
            methodname = method.getMethodName();
        } else {
            Collection<? extends DataObject> ds = context.lookupAll(DataObject.class);
            if (ds.size() != 1) {
                // No selection, or multiselection.
                return null;
            }
            fo = ds.iterator().next().getPrimaryFile();
            methodname = null;
        }
        File f = FileUtil.toFile(fo);
        if (f == null) {
            // JAR selection?
            return null;
        }
        if (!f.getName().endsWith(".java")) {
            // Some other kind of file selected.
            return null;
        }
        final ClassPath sourcepath = ClassPath.getClassPath(fo, ClassPath.SOURCE);
        if (sourcepath == null) {
            return null;
        }
        FileObject root = sourcepath.findOwnerRoot(fo);
        assert root != null : fo;
        Map<String, Object> properties = new HashMap<String, Object>();
        FileObject toRun = fo;
        boolean test;
        if (command.equals(SingleMethod.COMMAND_RUN_SINGLE_METHOD) || command.equals(SingleMethod.COMMAND_DEBUG_SINGLE_METHOD)) {
            test = true;
            if (methodname == null) {
                return null;
            }
            properties.put("methodname", methodname); // NOI18N
        } else if (command.equals(ActionProvider.COMMAND_TEST_SINGLE) || command.equals(ActionProvider.COMMAND_DEBUG_TEST_SINGLE)) {
            test = true;
            // Check for a matching unit test.
            String testResource = sourcepath.getResourceName(fo, '/', false) + "Test.java";
            for (URL u : UnitTestForSourceQuery.findUnitTests(root)) {
                try {
                    FileObject _toRun = URLMapper.findFileObject(new URL(u, testResource));
                    if (_toRun != null) {
                        toRun = _toRun;
                        break;
                    }
                } catch (MalformedURLException x) {
                    assert false : x;
                }
            }
        } else {
            assert command.equals(ActionProvider.COMMAND_RUN_SINGLE) || command.equals(ActionProvider.COMMAND_DEBUG_SINGLE) : command;
            test = false;
        }
        List<String> vmargs = new ArrayList<String>();
        String vmargPrefix = FileUtil.toFile(root).getAbsolutePath() + JavaCacheConstants.VMARGS;
        for (int i = 0; ; i++) {
            String vmarg = Cache.get(vmargPrefix + i);
            if (vmarg == null) {
                break;
            } else {
                vmargs.add(vmarg);
            }
        }
        if (!vmargs.isEmpty()) {
            properties.put(JavaRunner.PROP_RUN_JVMARGS, vmargs);
        }
        properties.put(JavaRunner.PROP_EXECUTE_FILE, toRun);
        boolean debug = command.equals(ActionProvider.COMMAND_DEBUG_SINGLE) ||
                command.equals(ActionProvider.COMMAND_DEBUG_TEST_SINGLE) ||
                command.equals(SingleMethod.COMMAND_DEBUG_SINGLE_METHOD);
        return new RunSetup(debug ?
            (test ? JavaRunner.QUICK_TEST_DEBUG : JavaRunner.QUICK_DEBUG) :
            (test ? JavaRunner.QUICK_TEST : JavaRunner.QUICK_RUN),
            properties);
    }

}
