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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.apisupport.projectinspector;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.JavaClassPathConstants;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.AnnotationProcessingQuery;
import org.netbeans.api.java.queries.BinaryForSourceQuery;
import org.netbeans.api.java.queries.JavadocForBinaryQuery;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.java.queries.UnitTestForSourceQuery;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.ant.AntArtifactQuery;
import org.netbeans.api.project.ant.AntBuildExtender;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.Actions;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 * Displays information about the selected project.
 */
@ActionID(id = "org.netbeans.modules.apisupport.projectinspector.InspectProjectAction", category = "Tools")
@ActionRegistration(displayName = "Inspect Project Metadata")
@ActionReference(path = "Projects/Actions", position = 2000)
public class InspectProjectAction implements ActionListener {

    @StaticResource private static final String REFRESH_ICON = "org/netbeans/modules/apisupport/projectinspector/refresh.png";

    private final List<Project> projects;
    private final Action refreshAction = new AbstractAction("Refresh", ImageUtilities.loadImageIcon(REFRESH_ICON, true)) {
        {
            putValue(SHORT_DESCRIPTION, "Refresh");
        }
        @Override public void actionPerformed(ActionEvent e) {
            InspectProjectAction.this.actionPerformed(e);
        }
    };
    private InputOutput io;

    public InspectProjectAction(List<Project> projects) {
        this.projects = projects;
    }

        public void actionPerformed(ActionEvent e) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    ProjectManager.mutex().readAccess(new Runnable() {
                        public void run() {
                            if (io != null) {
                                io.closeInputOutput();
                            }
                            String title;
                            if (projects.size() == 1) {
                                title = "Metadata: " + ProjectUtils.getInformation(projects.get(0)).getDisplayName();
                            } else {
                                title = "Project Metadata";
                            }
                            io = IOProvider.getDefault().getIO(title, new Action[] {refreshAction});
                            io.select();
                            OutputWriter pw = io.getOut();
                            try {
                                pw.reset();
                                boolean first = true;
                                for (Project p : projects) {
                                    if (!first) {
                                        pw.println();
                                        pw.println("-------------------------------------------");
                                        pw.println();
                                    }
                                    first = false;
                                    dump(p, pw);
                                }
                            } catch (Exception x) {
                                x.printStackTrace(pw);
                            }
                            pw.flush();
                            pw.close();
                        }
                    });
                }
            });
        }

    private static void dump(Project p, final PrintWriter pw) throws Exception {
        pw.println("Project: \"" + ProjectUtils.getInformation(p).getDisplayName() + "\" (" + ProjectUtils.getInformation(p).getName() + ")");
        pw.println("Location: " + FileUtil.getFileDisplayName(p.getProjectDirectory()));
        pw.println("Implementation class: " + p.getClass().getName());
        Lookup l = p.getLookup();
        pw.println("Raw lookup contents:");
        for (Object o : l.lookupAll(Object.class)) {
            pw.println("  " + o);
        }
        SubprojectProvider spp = l.lookup(SubprojectProvider.class);
        if (spp != null && ! spp.getSubprojects().isEmpty()) {
            pw.println();
            pw.println("Subprojects:");
            for (Project sp : spp.getSubprojects()) {
                pw.println("  " + FileUtil.getFileDisplayName(sp.getProjectDirectory()));
            }
        }
        final LogicalViewProvider lvp = l.lookup(LogicalViewProvider.class);
        if (lvp != null) {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    Node root = lvp.createLogicalView();
                    pw.println();
                    pw.println("Logical view:");
                    pw.println("- " + root.getDisplayName());
                    for (Node child : root.getChildren().getNodes(true)) {
                        pw.println("  + " + child.getDisplayName());
                    }
                    pw.println("Root node lookup:");
                    for (Object o : root.getLookup().lookupAll(Object.class)) {
                        pw.println("  " + o);
                    }
                    pw.println("Root node actions:");
                    for (Action a : root.getActions(false)) {
                        if (a != null) {
                            String label = (String) a.getValue(Action.NAME);
                            if (label != null) {
                                label = Actions.cutAmpersand(label);
                            }
                            else {
                                label = "???";
                            }
                            pw.println("  " + label + " [" + a.getClass().getName() + "]");
                        }
                        else {
                            pw.println("  -----------------");
                        }
                    }
                }
            });
        }
        Sources s = ProjectUtils.getSources(p);
        pw.println();
        pw.println("Generic source roots:");
        for (SourceGroup g : s.getSourceGroups(Sources.TYPE_GENERIC)) {
            FileObject r = g.getRootFolder();
            pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName(r));
            dumpSharability(r, p, pw, "    ");
        }
        SourceGroup[] javaGroups = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (javaGroups.length > 0) {
            pw.println();
            pw.println("Java source roots:");
            for (SourceGroup g : javaGroups) {
                FileObject r = g.getRootFolder();
                pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName(r));
                pw.println("    source level: " + SourceLevelQuery.getSourceLevel(r));
                pw.println("    encoding: " + FileEncodingQuery.getEncoding(r).displayName());
                URL[] builtTo = BinaryForSourceQuery.findBinaryRoots(r.toURL()).getRoots();
                if (builtTo.length > 0) {
                    pw.print("    binaries:");
                    for (URL u : builtTo) {
                        FileObject r2 = URLMapper.findFileObject(u);
                        pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName(r2) : u));
                    }
                    pw.println();
                }
                URL[] sources = UnitTestForSourceQuery.findSources(r);
                if (sources.length > 0) {
                    pw.print("    tested source roots:");
                    for (URL u : sources) {
                        FileObject r2 = URLMapper.findFileObject(u);
                        pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName(r2) : u));
                    }
                    pw.println();
                }
                URL[] tests = UnitTestForSourceQuery.findUnitTests(r);
                if (tests.length > 0) {
                    pw.print("    test roots:");
                    for (URL u : tests) {
                        FileObject r2 = URLMapper.findFileObject(u);
                        pw.print(" " + (r2 != null ? FileUtil.getFileDisplayName(r2) : u));
                    }
                    pw.println();
                }
                ClassPath cp = ClassPath.getClassPath(r, ClassPath.SOURCE);
                if (cp != null) {
                    pw.print("    " + ClassPath.SOURCE + ":");
                    for (FileObject r2 : cp.getRoots()) {
                        pw.print(" " + FileUtil.getFileDisplayName(r2));
                    }
                    pw.println();
                }
                for (String kind : new String[] {ClassPath.COMPILE, ClassPath.EXECUTE, ClassPath.BOOT,
                        /*org.netbeans.modules.java.api.common.classpath.ClassPathSupport.ENDORSED*/"classpath/endorsed",
                        JavaClassPathConstants.PROCESSOR_PATH}) {
                    cp = ClassPath.getClassPath(r, kind);
                    if (cp != null) {
                        pw.println("    " + kind + ":");
                        for (ClassPath.Entry entry : cp.entries()) {
                            URL u = entry.getURL();
                            FileObject r2 = entry.getRoot();
                            pw.println("      " + (r2 != null ? FileUtil.getFileDisplayName(r2) : u.toString()));
                            SourceForBinaryQuery.Result2 sfbq = SourceForBinaryQuery.findSourceRoots2(u);
                            FileObject[] source = sfbq.getRoots();
                            if (source.length > 0) {
                                if (sfbq.preferSources()) {
                                    pw.print("        sources (authoritative):");
                                } else {
                                    pw.print("        sources (informational):");
                                }
                                for (FileObject r3 : source) {
                                    pw.print(" " + FileUtil.getFileDisplayName(r3));
                                }
                                pw.println();
                            }
                            URL[] javadoc = JavadocForBinaryQuery.findJavadoc(u).getRoots();
                            if (javadoc.length > 0) {
                                pw.print("        Javadoc:");
                                for (URL u2 : javadoc) {
                                    FileObject r3 = URLMapper.findFileObject(u2);
                                    pw.print(" " + (r3 != null ? FileUtil.getFileDisplayName(r3) : u2));
                                }
                                pw.println();
                            }
                        }
                    }
                }
                AnnotationProcessingQuery.Result ann = AnnotationProcessingQuery.getAnnotationProcessingOptions(r);
                if (!ann.annotationProcessingEnabled().isEmpty()) {
                    pw.println("    annotation processing on " + ann.annotationProcessingEnabled() + ":");
                    if (ann.annotationProcessorsToRun() != null) {
                        pw.println("      processors: " + ann.annotationProcessorsToRun());
                    }
                    URL sU = ann.sourceOutputDirectory();
                    if (sU != null) {
                        FileObject sF = URLMapper.findFileObject(sU);
                        pw.println("      source output: " + (sF != null ? FileUtil.getFileDisplayName(sF) : sU));
                    }
                    pw.println("      options: " + ann.processorOptions());
                }
            }
        }
        SourceGroup[] rsrcGroups = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        if (rsrcGroups.length > 0) {
            pw.println();
            pw.println("Java resource roots:");
            for (SourceGroup g : rsrcGroups) {
                FileObject r = g.getRootFolder();
                pw.println("  \"" + g.getDisplayName() + "\" (" + g.getName() + "): " + FileUtil.getFileDisplayName(r));
            }
        }
        ActionProvider ap = l.lookup(ActionProvider.class);
        if (ap != null) {
            pw.println();
            pw.println("Actions:");
            for (String cmd : new TreeSet<String>(Arrays.asList(ap.getSupportedActions()))) {
                pw.println("  " + cmd);
            }
        }
        CacheDirectoryProvider cdp = l.lookup(CacheDirectoryProvider.class);
        if (cdp != null) {
            pw.println();
            pw.println("Cache directory: " + FileUtil.getFileDisplayName(cdp.getCacheDirectory()));
        }
        ProjectConfigurationProvider<?> pcp = l.lookup(ProjectConfigurationProvider.class);
        if (pcp != null) {
            pw.println();
            pw.println("Configurations:");
            for (ProjectConfiguration cfg : pcp.getConfigurations()) {
                pw.print("  " + cfg.getDisplayName());
                if (cfg.equals(pcp.getActiveConfiguration())) {
                    pw.print(" [active]");
                }
                pw.println();
            }
        }
        PropertyEvaluator eval = p.getLookup().lookup(PropertyEvaluator.class);
        if (eval != null) {
            pw.println();
            pw.println("Properties (from PropertyEvaluator found in lookup):");
        }
        if (eval == null) {
            try {
                for (Field f : p.getClass().getDeclaredFields()) {
                    if (PropertyEvaluator.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        eval = (PropertyEvaluator) f.get(p);
                        pw.println();
                        pw.println("Properties (from field " + f.getName() + "):");
                        break;
                    }
                }
                if (eval == null) {
                    for (Method m : p.getClass().getDeclaredMethods()) {
                        if (PropertyEvaluator.class.isAssignableFrom(m.getReturnType()) && m.getParameterTypes().length == 0) {
                            //System.err.println("found in " + m.getName());
                            m.setAccessible(true);
                            eval = (PropertyEvaluator) m.invoke(p);
                            pw.println();
                            pw.println("Properties (from method " + m.getName() + "()):");
                            break;
                        }
                    }
                }
            } catch (Exception x) {
                Exceptions.printStackTrace(x);
            }
        }
        if (eval != null) {
            Map<String, String> props = eval.getProperties();
            if (props != null) {
                for (Map.Entry<String,String> entry : new TreeMap<String,String>(props).entrySet()) {
                    pw.println("  " + entry.getKey() + "=" + entry.getValue());
                }
            } else {
                pw.println("  <unknown>");
            }
        }
        RecommendedTemplates rt = l.lookup(RecommendedTemplates.class);
        if (rt != null) {
            pw.println();
            pw.println("Recommended template categories:");
            for (String categ : rt.getRecommendedTypes()) {
                pw.println("  " + categ);
            }
        }
        PrivilegedTemplates pt = l.lookup(PrivilegedTemplates.class);
        if (pt != null) {
            pw.println();
            pw.println("Recommended templates:");
            for (String template : pt.getPrivilegedTemplates()) {
                pw.print("  " + template);
                final FileObject fo = FileUtil.getConfigFile(template);
                if (fo != null) {
                    final DataObject d = DataObject.find(fo);
                    EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                            String displayName = d.getNodeDelegate().getDisplayName();
                            if (!displayName.equals(fo.getName())) {
                                pw.print(" (\"" + displayName + "\")");
                            }
                        }
                    });
                }
                pw.println();
            }
        }
        AntArtifact[] artifacts = AntArtifactQuery.findArtifactsByType(p, JavaProjectConstants.ARTIFACT_TYPE_JAR);
        if (artifacts.length > 0) {
            pw.println();
            pw.println("Ant artifacts (build products):");
            for (AntArtifact aa : artifacts) {
                pw.println("  " + aa.getID() + " (" + aa.getScriptLocation() + "#" + aa.getTargetName() + " or #" + aa.getCleanTargetName() + ")");
                for (URI u : aa.getArtifactLocations()) {
                    pw.println("    " + u);
                }
            }
        }
        AntBuildExtender abe = l.lookup(AntBuildExtender.class);
        if (abe != null) {
            pw.println();
            pw.println("Extensible build script targets:");
            for (String target : abe.getExtensibleTargets()) {
                pw.println("  " + target);
            }
        }
    }

    private static void dumpSharability(FileObject fo, Project p, PrintWriter pw, String prefix) {
        pw.print(prefix + fo.getNameExt());
        Project owner = FileOwnerQuery.getOwner(fo);
        if (owner != p) {
            pw.println(" (different owner project: " + (owner != null ? ProjectUtils.getInformation(owner).getDisplayName() : "none") + ")");
            return;
        }
        switch (SharabilityQuery.getSharability(fo)) {
        case MIXED:
            pw.println();
            if (fo.isFolder()) {
                FileObject[] kids = fo.getChildren();
                Arrays.sort(kids, new Comparator<FileObject>() {
                    public int compare(FileObject fo1, FileObject fo2) {
                        return fo1.getNameExt().compareTo(fo2.getNameExt());
                    }
                });
                for (FileObject kid : kids) {
                    if (VisibilityQuery.getDefault().isVisible(kid)) {
                        dumpSharability(kid, p, pw, prefix + "  ");
                    }
                }
            }
            break;
        case NOT_SHARABLE:
            pw.println(" (not sharable)");
            break;
        case SHARABLE:
            pw.println();
            break;
        case UNKNOWN:
            pw.println(" (sharability unknown)");
            break;
        }
    }

}
