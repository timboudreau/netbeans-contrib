/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.omnidebugger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.ErrorManager;
import org.openide.cookies.SourceCookie;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.InstalledFileLocator;
import org.openide.src.ClassElement;
import org.openide.src.Identifier;
import org.openide.src.Import;
import org.openide.src.SourceElement;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Invokes the debugger.
 * @author Jesse Glick
 */
class Debug {
    
    private Debug() {}
    
    private static final int KIND_NONE = 0;
    private static final int KIND_MAIN = 1;
    private static final int KIND_MAIN_WITH_GUI = 2;
    private static final int KIND_JUNIT = 3;
    
    private static int getKind(FileObject clazz) {
        SourceCookie sc;
        try {
            sc = (SourceCookie) DataObject.find(clazz).getCookie(SourceCookie.class);
        } catch (DataObjectNotFoundException e) {
            throw new AssertionError(e);
        }
        if (sc == null) {
            return KIND_NONE;
        }
        SourceElement se = sc.getSource();
        ClassElement[] clazzes = se.getClasses();
        if (clazzes.length == 0) {
            return KIND_NONE;
        }
        ClassElement base = clazzes[0];
        while (true) {
            Identifier supeName = base.getSuperclass();
            if (supeName == null) {
                break;
            }
            String name = supeName.getFullName();
            if (name.equals("junit.framework.TestCase")) { // NOI18N
                return KIND_JUNIT;
            }
            ClassElement supe = ClassElement.forName(name, clazz);
            if (supe == null) {
                break;
            }
            base = supe;
        }
        if (clazzes[0].hasMainMethod()) {
            Import[] imports = se.getImports();
            for (int i = 0; i < imports.length; i++) {
                String name = imports[i].getIdentifier().getFullName();
                if (name.startsWith("java.awt.") || name.startsWith("javax.swing.")) { // NOI18N
                    return KIND_MAIN_WITH_GUI;
                }
            }
            return KIND_MAIN;
        } else {
            return KIND_NONE;
        }
    }
    
    public static boolean enabled(FileObject clazz) {
        if (getKind(clazz) == KIND_NONE) {
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
        FileObject compiledClazz = cp.findResource(sourceCP.getResourceName(clazz, '/', false) + ".class"); // NOI18N
        if (compiledClazz == null || compiledClazz.lastModified().getTime() < clazz.lastModified().getTime()) {
            // #72385: not yet compiled?
            IOException e = new IOException("uncompiled: " + clazz); // NOI18N
            String msg = "You must compile " + FileUtil.getFileDisplayName(clazz) + " before debugging."; // XXX I18N
            ErrorManager.getDefault().annotate(e, ErrorManager.USER, null, msg, null, null);
            throw e;
        }
        FileObject dir = getWorkingDir(clazz);
        FileObject buildXml = createBuildXml(dir, clazz, sourceCP.getResourceName(clazz, '.', false), cp);
        return ActionUtils.runTarget(buildXml, null, null);
    }
    
    private static FileObject getWorkingDir(FileObject clazz) throws IOException {
        Project prj = FileOwnerQuery.getOwner(clazz);
        if (prj != null) {
            CacheDirectoryProvider cdp = (CacheDirectoryProvider) prj.getLookup().lookup(CacheDirectoryProvider.class);
            if (cdp != null) {
                return cdp.getCacheDirectory();
            }
        }
        return FileUtil.toFileObject(new File(System.getProperty("java.io.tmpdir"))); // NOI18N
    }
    
    private static FileObject createBuildXml(FileObject dir, FileObject clazz, String clazzname, ClassPath cp) throws IOException {
        Project prj = FileOwnerQuery.getOwner(clazz);
        int kind = getKind(clazz);
        assert kind != KIND_NONE;
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
    
    private static Document createScript(FileObject clazz, int kind, ClassPath cp, String clazzname, FileObject dir, Project prj) {
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
        if (kind == KIND_JUNIT) {
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
        Iterator roots = cp.entries().iterator();
        while (roots.hasNext()) {
            ClassPath.Entry entry = (ClassPath.Entry) roots.next();
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
        if (kind == KIND_MAIN_WITH_GUI) {
            Element sysproperty = doc.createElement("sysproperty"); // NOI18N
            java.appendChild(sysproperty);
            sysproperty.setAttribute("key", "DONT_SHOW"); // NOI18N
            sysproperty.setAttribute("value", "true"); // NOI18N
        }
        java.appendChild(doc.createComment(" Also permitted: -DPAUSED (start but do not record); or -DDONT_START (just show control window) ")); // XXX I18N
        // Also try to copy system properties where possible. Just guessing at syntax project uses.
        Iterator/*<Map.Entry<String,String>>*/ properties = evaluatorFor(prj).getProperties().entrySet().iterator();
        while (properties.hasNext()) {
            Map.Entry entry = (Map.Entry) properties.next();
            String k = (String) entry.getKey();
            String name;
            if (kind == KIND_JUNIT && k.startsWith("test-unit-sys-prop.")) { // NOI18N
                name = k.substring("test-unit-sys-prop.".length()); // NOI18N
            } else if (kind == KIND_JUNIT && k.startsWith("test-sys-prop.")) { // NOI18N
                name = k.substring("test-sys-prop.".length()); // NOI18N
            } else if (kind != KIND_JUNIT && k.startsWith("run-sys-prop.")) { // NOI18N
                name = k.substring("run-sys-prop.".length()); // NOI18N
            } else {
                name = null;
            }
            if (name != null) {
                Element sysproperty = doc.createElement("sysproperty"); // NOI18N
                java.appendChild(sysproperty);
                sysproperty.setAttribute("key", name); // NOI18N
                sysproperty.setAttribute("value", (String) entry.getValue()); // NOI18N
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

    private static void writeDefaults(FileObject dir, int kind, ClassPath cp, Project prj) throws IOException {
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
                Iterator roots = cp.entries().iterator();
                while (roots.hasNext()) {
                    ClassPath.Entry entry = (ClassPath.Entry) roots.next();
                    URL root = entry.getURL();
                    FileObject[] sourceRoots = SourceForBinaryQuery.findSourceRoots(root).getRoots();
                    for (int i = 0; i < sourceRoots.length; i++) {
                        File sourceRootF = FileUtil.toFile(sourceRoots[i]);
                        if (sourceRootF != null) {
                            String path = sourceRootF.getAbsolutePath();
                            if (!path.endsWith(File.separator)) {
                                path += File.separator;
                            }
                            // Make all source roots in exec CP available for browsing.
                            ps.println("SourceDirectory:	\"" + path + "\""); // NOI18N
                        }
                        if (prj != null && FileOwnerQuery.getOwner(sourceRoots[i]) != prj) {
                            // Sources not in same project. Probably prefer not to instrument.
                            continue;
                        }
                        Enumeration packages = sourceRoots[i].getFolders(true);
                        while (packages.hasMoreElements()) {
                            FileObject pkg = (FileObject) packages.nextElement();
                            FileObject[] kids = pkg.getChildren();
                            boolean hasSources = false;
                            for (int j = 0; j < kids.length; j++) {
                                if (kids[j].isData() && kids[j].hasExt("java")) { // NOI18N
                                    hasSources = true;
                                    break;
                                }
                            }
                            if (!hasSources) {
                                continue;
                            }
                            String name = FileUtil.getRelativePath(sourceRoots[i], pkg).replace('/', '.');
                            // Ask to instrument any package for which we have sources available.
                            // Note that the directive is a prefix, so this spuriously includes subpackages.
                            // Hopefully we have sources for them too anyway.
                            ps.println("OnlyInstrument:		\"" + name + ".\""); // NOI18N
                        }
                    }
                }
                if (kind == KIND_JUNIT) {
                    // Want to instrument System.exit and System.out.print commands, both in this package.
                    // No need to instrument e.g. scanning for test methods, which is in junit.framework.
                    ps.println("OnlyInstrument:		\"junit.textui.\""); // NOI18N
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
        return PropertyUtils.sequentialPropertyEvaluator(null, new PropertyProvider[] {
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "nbproject/private/private.properties")), // NOI18N
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "nbproject/project.properties")), // NOI18N
            PropertyUtils.propertiesFilePropertyProvider(PropertyUtils.resolveFile(basedir, "build.properties")), // NOI18N
        });
    }
    
}
