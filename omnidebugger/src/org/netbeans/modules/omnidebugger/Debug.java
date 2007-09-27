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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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

package org.netbeans.modules.omnidebugger;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.Tree;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Invokes the debugger.
 * @author Jesse Glick
 */
class Debug {
    
    private Debug() {}

    private enum ClassKind {NONE, MAIN, MAIN_WITH_GUI, JUNIT}
    
    private static ClassKind getKind(FileObject source) {
        final ClassKind[] result = new ClassKind[] {ClassKind.NONE};
        try {
            JavaSource src = JavaSource.forFileObject(source);
            if (src == null) {
                return ClassKind.NONE;
            }
            src.runUserActionTask(new CancellableTask<CompilationController>() {
                public void run(CompilationController controller) throws Exception {
                    controller.toPhase(JavaSource.Phase.RESOLVED);
                    CompilationUnitTree compunit = controller.getCompilationUnit();
                    for (Tree t : compunit.getTypeDecls()) {
                        TypeElement clazz = (TypeElement) controller.getTrees().getElement(controller.getTrees().getPath(compunit, t));
                        if (clazz == null) {
                            return; // ???
                        }
                        TypeElement testCase = controller.getElements().getTypeElement("junit.framework.TestCase");
                        if (testCase != null) {
                            if (controller.getTypes().isSubtype(clazz.asType(), testCase.asType())) {
                                result[0] = ClassKind.JUNIT;
                                return;
                            }
                        }
                        for (javax.lang.model.element.Element child : clazz.getEnclosedElements()) {
                            if (child.getKind() == ElementKind.METHOD) {
                                ExecutableElement method = (ExecutableElement) child;
                                if (method.getSimpleName().contentEquals("main")) {
                                    for (ImportTree imprt : compunit.getImports()) {
                                        String name = imprt.getQualifiedIdentifier().toString();
                                        if (name.startsWith("java.awt.") || name.startsWith("javax.swing.")) {
                                            result[0] = ClassKind.MAIN_WITH_GUI;
                                            return;
                                        }
                                    }
                                    result[0] = ClassKind.MAIN;
                                    return;
                                }
                            }
                        }
                    }
                }
                public void cancel() {}
            }, true);
        } catch (IOException x) {
            Exceptions.printStackTrace(x);
        }
        Logger.getLogger(Debug.class.getName()).log(Level.FINE, "Got {0} from {1}", new Object[] {result[0], source});
        return result[0];
    }
    
    public static boolean enabled(FileObject clazz) {
        if (getKind(clazz) == ClassKind.NONE) {
            return false;
        }
        if (ClassPath.getClassPath(clazz, ClassPath.EXECUTE) == null) {
            return false;
        }
        if (ClassPath.getClassPath(clazz, ClassPath.SOURCE) == null) {
            return false;
        }
        return true;
    }
    
    public static ExecutorTask start(FileObject clazz) throws IOException {
        ClassPath sourceCP = ClassPath.getClassPath(clazz, ClassPath.SOURCE);
        assert sourceCP != null;
        ClassPath cp = ClassPath.getClassPath(clazz, ClassPath.EXECUTE);
        assert cp != null;
        FileBuiltQuery.Status status = FileBuiltQuery.getStatus(clazz);
        if (status != null && !status.isBuilt()) {
            // #72385: not yet compiled?
            throw Exceptions.attachLocalizedMessage(new IOException("uncompiled: " + clazz),
                    // XXX I18N
                    "You must compile " + FileUtil.getFileDisplayName(clazz) + " before debugging.");
        }
        FileObject dir = getWorkingDir(clazz);
        FileObject buildXml = createBuildXml(dir, clazz, sourceCP.getResourceName(clazz, '.', false), cp);
        return ActionUtils.runTarget(buildXml, null, null);
    }
    
    private static FileObject getWorkingDir(FileObject clazz) throws IOException {
        Project prj = FileOwnerQuery.getOwner(clazz);
        if (prj != null) {
            CacheDirectoryProvider cdp = prj.getLookup().lookup(CacheDirectoryProvider.class);
            if (cdp != null) {
                return cdp.getCacheDirectory();
            }
        }
        return FileUtil.toFileObject(new File(System.getProperty("java.io.tmpdir"))); // NOI18N
    }
    
    private static FileObject createBuildXml(FileObject dir, FileObject clazz, String clazzname, ClassPath cp) throws IOException {
        Project prj = FileOwnerQuery.getOwner(clazz);
        ClassKind kind = getKind(clazz);
        assert kind != ClassKind.NONE;
        Document doc = createScript(clazz, kind, cp, clazzname, dir, prj);
        FileObject buildXml = FileUtil.createData(dir, "omnidebug.xml"); // NOI18N
        FileLock lock = buildXml.lock();
        try {
            OutputStream os = buildXml.getOutputStream(lock);
            try {
                XMLUtil.write(doc, os, "UTF-8"); // NOI18N
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
        writeDefaults(dir, kind, cp, prj);
        return buildXml;
    }
    
    private static Document createScript(FileObject clazz, ClassKind kind, ClassPath cp, String clazzname, FileObject dir, Project prj) {
        Document doc = XMLUtil.createDocument("project", "antlib:org.apache.tools.ant", null, null); // NOI18N
        Element root = doc.getDocumentElement();
        root.setAttribute("default", "debug"); // NOI18N
        if (prj != null) {
            root.setAttribute("name", "omnidebug-" + ProjectUtils.getInformation(prj).getName()); // NOI18N
            // So you can use something=${basedir}/... in your system properties:
            root.setAttribute("basedir", FileUtil.toFile(prj.getProjectDirectory()).getAbsolutePath()); // NOI18N
        } else {
            root.setAttribute("name", "omnidebug"); // NOI18N
        }
        Element targ = doc.createElement("target"); // NOI18N
        root.appendChild(targ);
        targ.setAttribute("name", "debug"); // NOI18N
        Element java = doc.createElement("java"); // NOI18N
        targ.appendChild(java);
        java.setAttribute("fork", "true"); // NOI18N
        java.setAttribute("classname", "com.lambda.Debugger.Debugger"); // NOI18N
        java.setAttribute("dir", FileUtil.toFile(dir).getAbsolutePath()); // NOI18N
        if (kind == ClassKind.JUNIT) {
            Element arg = doc.createElement("arg"); // NOI18N
            java.appendChild(arg);
            arg.setAttribute("value", "junit.textui.TestRunner"); // NOI18N
        }
        Element arg = doc.createElement("arg"); // NOI18N
        java.appendChild(arg);
        arg.setAttribute("value", clazzname); // NOI18N
        Element jvmarg = doc.createElement("jvmarg"); // NOI18N
        java.appendChild(jvmarg);
        // XXX too much? too little?
        jvmarg.setAttribute("value", "-Xmx256m"); // NOI18N
        /* XXX -ea crashes unit tests using AntProjectHelperSingleton; apparently debugger has odd eval order for <clinit>:
        jvmarg = doc.createElement("jvmarg"); // NOI18N
        java.appendChild(jvmarg);
        jvmarg.setAttribute("value", "-ea"); // NOI18N
         */
        Element classpath = doc.createElement("classpath"); // NOI18N
        java.appendChild(classpath);
        Element pathelement = doc.createElement("pathelement"); // NOI18N
        classpath.appendChild(pathelement);
        File debuggerJar = InstalledFileLocator.getDefault().locate("modules/ext/omni-debugger.jar", "org.netbeans.modules.omnidebugger", false); // NOI18N
        assert debuggerJar != null;
        pathelement.setAttribute("location", debuggerJar.getAbsolutePath()); // NOI18N
        for (ClassPath.Entry entry : cp.entries()) {
            URL r = entry.getURL();
            URL _r = FileUtil.getArchiveFile(r);
            if (_r != null && r.equals(FileUtil.getArchiveRoot(_r))) {
                r = _r;
            }
            if ("file".equals(r.getProtocol())) { // NOI18N
                String path = new File(URI.create(r.toExternalForm())).getAbsolutePath();
                pathelement = doc.createElement("pathelement"); // NOI18N
                classpath.appendChild(pathelement);
                pathelement.setAttribute("location", path); // NOI18N
            }
        }
        if (kind == ClassKind.MAIN_WITH_GUI) {
            Element sysproperty = doc.createElement("sysproperty"); // NOI18N
            java.appendChild(sysproperty);
            sysproperty.setAttribute("key", "DONT_SHOW"); // NOI18N
            sysproperty.setAttribute("value", "true"); // NOI18N
        }
        java.appendChild(doc.createComment(" Also permitted: -DPAUSED (start but do not record); or -DDONT_START (just show control window) ")); // XXX I18N
        // Also try to copy system properties where possible. Just guessing at syntax project uses.
        for (Map.Entry<String,String> entry : evaluatorFor(prj).getProperties().entrySet()) {
            String k = entry.getKey();
            String name;
            if (kind == ClassKind.JUNIT && k.startsWith("test-unit-sys-prop.")) { // NOI18N
                name = k.substring("test-unit-sys-prop.".length()); // NOI18N
            } else if (kind == ClassKind.JUNIT && k.startsWith("test-sys-prop.")) { // NOI18N
                name = k.substring("test-sys-prop.".length()); // NOI18N
            } else if (kind != ClassKind.JUNIT && k.startsWith("run-sys-prop.")) { // NOI18N
                name = k.substring("run-sys-prop.".length()); // NOI18N
            } else {
                name = null;
            }
            if (name != null) {
                Element sysproperty = doc.createElement("sysproperty"); // NOI18N
                java.appendChild(sysproperty);
                sysproperty.setAttribute("key", name); // NOI18N
                sysproperty.setAttribute("value", entry.getValue()); // NOI18N
            }
        }
        return doc;
    }
    
    /**
     * Adapted from what java -jar debugger.jar writes itself. Just SourceDirectory & OnlyInstrument removed.
     */
    private static final String DEFAULT_DEFAULTS =
            "# ODB Defaults -- You may edit by hand. See Manual for details\n" + // NOI18N
            "\n" + // NOI18N
            "#                        Class & method names must be complete. '*' must be freestanding.\n" + // NOI18N
            "# DidntInstrument:       This is informative only. (You may change to InstrumentOnly.)\n" + // NOI18N
            "# DontInstrumentMethod:  These methods won't be instrumented (but may be recorded).\n" + // NOI18N
            "# DontRecordMethod:      These methods won't be recorded (ie, from the calling method).\n" + // NOI18N
            "# DontEither:            These methods won't be recorded or instrumented.\n" + // NOI18N
            "# MaxTimeStamps:         This is overridded by the command line argument, hence seldom used.\n" + // NOI18N
            "# StartPattern:          Recording will start when this pattern is matched.\n" + // NOI18N
            "# StopPattern:           Recording will stop when this pattern is matched (no restarts!).\n" + // NOI18N
            "# SourceDirectory:       If sources can't be found normally, look here.\n" + // NOI18N
            "# OnlyInstrument:        Only classes which match this prefix will be instrumented.\n" + // NOI18N
            "# OnlyInstrument:        \"\" means default package only. No entry means everything.\n" + // NOI18N
            "# UserSelectedField:     This instance variable (a final String) will be appended to the display string\n" + // NOI18N
            "# UserSelectedField:     \"com.lambda.Thing name\"  ->   <Thing_23 John>\n" + // NOI18N
            "# SpecialFormatter:      com.lambda.Debugger.SpecialTimeStampFormatter\n" + // NOI18N
            "\n" + // NOI18N
            "MaxTimeStamps:		400000\n" + // NOI18N
            "StartPattern:		\n" + // NOI18N
            "StopPattern:		\n" + // NOI18N
            "DidntInstrument:	\n" + // NOI18N
            "DontInstrumentMethod:	\n" + // NOI18N
            "DontInstrumentMethod:	\"*	 toString\"\n" + // NOI18N
            "DontInstrumentMethod:	\"*	 valueOf\"\n" + // NOI18N
            "DontRecordMethod:	\n" + // NOI18N
            "DontRecordMethod:	\"*	 toString\"\n" + // NOI18N
            "DontRecordMethod:	\"*	 valueOf\"\n" + // NOI18N
            "DontRecordMethod:	\"java.lang.StringBuffer	 *\"\n" + // NOI18N
            "DontRecordMethod:	\"java.lang.Object	 new\"\n" + // NOI18N
            "UserSelectedField:	\"com.lambda.Debugger.DemoThing	 name\"\n"; // NOI18N

    private static void writeDefaults(FileObject dir, ClassKind kind, ClassPath cp, Project prj) throws IOException {
        /* XXX would be OK but need to replace existing SourceDirectory and OnlyInstrument directives
        FileObject defFile = dir.getFileObject(".debuggerDefaults"); // NOI18N
        if (defFile != null) {
            // don't overwrite
            return;
        }
        defFile = dir.createData(".debuggerDefaults"); // NOI18N
         */
        FileObject defFile = FileUtil.createData(dir, ".debuggerDefaults"); // NOI18N
        FileLock lock = defFile.lock();
        try {
            OutputStream os = defFile.getOutputStream(lock);
            try {
                PrintStream ps = new PrintStream(os);
                ps.print(DEFAULT_DEFAULTS);
                Set<String> sourceDirectories = new TreeSet<String>();
                Set<String> onlyInstruments = new TreeSet<String>();
                for (ClassPath.Entry entry : cp.entries()) {
                    URL root = entry.getURL();
                    for (FileObject sourceRoot : SourceForBinaryQuery.findSourceRoots(root).getRoots()) {
                        File sourceRootF = FileUtil.toFile(sourceRoot);
                        if (sourceRootF != null) {
                            String path = sourceRootF.getAbsolutePath();
                            if (!path.endsWith(File.separator)) {
                                path += File.separator;
                            }
                            // Make all source roots in exec CP available for browsing.
                            sourceDirectories.add(path);
                        }
                        if (prj != null && FileOwnerQuery.getOwner(sourceRoot) != prj) {
                            // Sources not in same project. Probably prefer not to instrument.
                            continue;
                        }
                        Enumeration<? extends FileObject> files = sourceRoot.getChildren(true);
                        while (files.hasMoreElements()) {
                            FileObject file = files.nextElement();
                            if (file.isData() && file.hasExt("java")) { // NOI18N
                                String name = FileUtil.getRelativePath(sourceRoot, file).replace('/', '.');
                                assert name.endsWith(".java");
                                onlyInstruments.add(name.substring(0, name.length() - 5));
                            }
                        }
                    }
                }
                if (kind == ClassKind.JUNIT) {
                    // Want to instrument System.exit and System.out.print commands, both in this package.
                    // No need to instrument e.g. scanning for test methods, which is in junit.framework.
                    onlyInstruments.add("junit.textui."); // NOI18N
                }
                // Also instrument any package corresponding to an open file, since you are probably debugging them:
                for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
                    Node[] nodes = tc.getActivatedNodes();
                    if (nodes == null) {
                        continue;
                    }
                    for (Node node : nodes) {
                        DataObject d = node.getCookie(DataObject.class);
                        if (d != null) {
                            FileObject f = d.getPrimaryFile();
                            if (f.hasExt("java")) { // NOI18N
                                ClassPath src = ClassPath.getClassPath(f, ClassPath.SOURCE);
                                if (src != null) {
                                    String name = src.getResourceName(f, '.', false);
                                    assert name != null : f;
                                    onlyInstruments.add(name);
                                }
                            }
                        }
                    }
                }
                // Write out all such directives in sorted order:
                for (String sourcedir : sourceDirectories) {
                    ps.println("SourceDirectory:	\"" + sourcedir + "\""); // NOI18N
                }
                for (String pkg : onlyInstruments) {
                    ps.println("OnlyInstrument:		\"" + pkg + "\""); // NOI18N
                }
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    /**
     * Try to guess at properties available in a project.
     */
    public static PropertyEvaluator evaluatorFor(Project project) {
        File basedir = FileUtil.toFile(project.getProjectDirectory());
        if (basedir == null) {
            return PropertyUtils.sequentialPropertyEvaluator(null, new PropertyProvider[0]);
        }
        return PropertyUtils.sequentialPropertyEvaluator(null,
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "nbproject/private/private.properties")), // NOI18N
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "nbproject/project.properties")), // NOI18N
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "build.properties"))); // NOI18N
    }
    
}
