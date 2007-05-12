package org.netbeans.modules.scala.project;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.ProjectGenerator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Martin Krauskopf
 */
public final class ScalaProjectGenerator {
    
    /** User factory methods instead. */
    private ScalaProjectGenerator() { }
    
    /**
     * Create a new empty Scala project.
     *
     * @param dir the top-level directory (need not yet exist but if it does it must be empty)
     * @param name the name for the project
     * @return the helper object permitting it to be further customized
     * @throws IOException in case something went wrong
     */
    public static AntProjectHelper createProject(final File prjDir,
            final String projectName) throws IOException {
        final FileObject prjDirFO = createProjectDir(prjDir);
        AntProjectHelper helper;
        try {
            helper = ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<AntProjectHelper>() {
                public AntProjectHelper run() throws Exception {
                    return createProject(prjDirFO, projectName);
                }
            });
        } catch (MutexException e) {
            throw (IOException) e.getException();
        }
        Project p = ProjectManager.getDefault().findProject(prjDirFO);
        ProjectManager.getDefault().saveProject(p);
        prjDirFO.createFolder(ScalaProject.DEFAULT_SRC_DIR);
        return helper;
    }
    
    private static AntProjectHelper createProject(final FileObject dirFO,
            final String projectName) throws IOException {
        assert ProjectManager.mutex().isWriteAccess();
        AntProjectHelper helper = ProjectGenerator.createProject(dirFO, ScalaProjectType.TYPE);
        Element data = helper.getPrimaryConfigurationData(true);
        Document doc = data.getOwnerDocument();
        Element nameEl = doc.createElementNS(ScalaProjectType.NAMESPACE_SHARED, "name"); // NOI18N
        nameEl.appendChild(doc.createTextNode(projectName));
        data.appendChild(nameEl);
        helper.putPrimaryConfigurationData(data, true);
        //        ep.setProperty("dist.dir", "dist"); // NOI18N
        //        ep.setComment("dist.dir", new String[] {"# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_dist.dir")}, false); // NOI18N
        //        ep.setProperty("dist.jar", "${dist.dir}/" + PropertyUtils.getUsablePropertyName(name) + ".jar"); // NOI18N
        //        ep.setProperty("javac.classpath", new String[0]); // NOI18N
        //        ep.setProperty("build.sysclasspath", "ignore"); // NOI18N
        //        ep.setComment("build.sysclasspath", new String[] {"# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_build.sysclasspath")}, false); // NOI18N
        //        ep.setProperty("run.classpath", new String[] { // NOI18N
        //            "${javac.classpath}:", // NOI18N
        //            "${build.classes.dir}", // NOI18N
        //        });
        //        ep.setProperty("debug.classpath", new String[] { // NOI18N
        //            "${run.classpath}", // NOI18N
        //        });
        //        ep.setProperty("jar.compress", "false"); // NOI18N
        //        if (!isLibrary) {
        //            ep.setProperty("main.class", mainClass == null ? "" : mainClass); // NOI18N
        //        }
        //
        //        ep.setProperty("javac.compilerargs", ""); // NOI18N
        //        ep.setComment("javac.compilerargs", new String[] {
        //            "# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_javac.compilerargs"), // NOI18N
        //        }, false);
        //        SpecificationVersion sourceLevel = getDefaultSourceLevel();
        //        ep.setProperty("javac.source", sourceLevel.toString()); // NOI18N
        //        ep.setProperty("javac.target", sourceLevel.toString()); // NOI18N
        //        ep.setProperty("javac.deprecation", "false"); // NOI18N
        //        ep.setProperty("javac.test.classpath", new String[] { // NOI18N
        //            "${javac.classpath}:", // NOI18N
        //            "${build.classes.dir}:", // NOI18N
        //            "${libs.junit.classpath}", // NOI18N
        //        });
        //        ep.setProperty("run.test.classpath", new String[] { // NOI18N
        //            "${javac.test.classpath}:", // NOI18N
        //            "${build.test.classes.dir}", // NOI18N
        //        });
        //        ep.setProperty("debug.test.classpath", new String[] { // NOI18N
        //            "${run.test.classpath}", // NOI18N
        //        });
        //
        //        ep.setProperty("build.generated.dir", "${build.dir}/generated"); // NOI18N
        //        ep.setProperty("meta.inf.dir", "${src.dir}/META-INF"); // NOI18N
        //
        //        ep.setProperty("build.dir", "build"); // NOI18N
        //        ep.setComment("build.dir", new String[] {"# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_build.dir")}, false); // NOI18N
        //        ep.setProperty("build.classes.dir", "${build.dir}/classes"); // NOI18N
        //        ep.setProperty("build.test.classes.dir", "${build.dir}/test/classes"); // NOI18N
        //        ep.setProperty("build.test.results.dir", "${build.dir}/test/results"); // NOI18N
        //        ep.setProperty("build.classes.excludes", "**/*.java,**/*.form"); // NOI18N
        //        ep.setProperty("dist.javadoc.dir", "${dist.dir}/javadoc"); // NOI18N
        //        ep.setProperty("platform.active", "default_platform"); // NOI18N
        //
        //        ep.setProperty("run.jvmargs", ""); // NOI18N
        //        ep.setComment("run.jvmargs", new String[] {
        //            "# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_run.jvmargs"), // NOI18N
        //            "# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_run.jvmargs_2"), // NOI18N
        //            "# " + NbBundle.getMessage(ScalaProjectGenerator.class, "COMMENT_run.jvmargs_3"), // NOI18N
        //        }, false);
        //
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_PRIVATE, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_NO_TREE, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_USE, "true"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_NO_NAVBAR, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_NO_INDEX, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_SPLIT_INDEX, "true"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_AUTHOR, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_VERSION, "false"); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_WINDOW_TITLE, ""); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_ENCODING, ""); // NOI18N
        //        ep.setProperty(J2SEProjectProperties.JAVADOC_ADDITIONALPARAM, ""); // NOI18N
        //
        //        if (manifestFile != null) {
        //            ep.setProperty("manifest.file", manifestFile); // NOI18N
        //        }
        //        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        return helper;
    }
    
    //    public static AntProjectHelper createProject(final File dir, final String name,
    //            final File[] sourceFolders) throws IOException {
    //        assert sourceFolders != null : "Package roots can't be null";   //NOI18N
    //        final FileObject dirFO = createProjectDir(dir);
    //        // this constructor creates only java application type
    //        final AntProjectHelper h = createProject(dirFO, name, null, null, null, manifestFile, false);
    //        final ScalaProject p = (ScalaProject) ProjectManager.getDefault().findProject(dirFO);
    //        final ReferenceHelper refHelper = p.getReferenceHelper();
    //        try {
    //            ProjectManager.mutex().writeAccess( new Mutex.ExceptionAction() {
    //                public Object run() throws Exception {
    //                    Element data = h.getPrimaryConfigurationData(true);
    //                    Document doc = data.getOwnerDocument();
    //                    NodeList nl = data.getElementsByTagNameNS(ScalaProjectType.NAMESPACE_SHARED,"source-roots");
    //                    assert nl.getLength() == 1;
    //                    Element sourceRoots = (Element) nl.item(0);
    //                    nl = data.getElementsByTagNameNS(ScalaProjectType.NAMESPACE_SHARED,"test-roots");  //NOI18N
    //                    assert nl.getLength() == 1;
    //                    Element testRoots = (Element) nl.item(0);
    //                    for (int i=0; i<sourceFolders.length; i++) {
    //                        String propName;
    //                        if (i == 0) {
    //                            //Name the first src root src.dir to be compatible with NB 4.0
    //                            propName = "src.dir";       //NOI18N
    //                        } else {
    //                            String name = sourceFolders[i].getName();
    //                            propName = name + ".dir";    //NOI18N
    //                        }
    //
    //                        int rootIndex = 1;
    //                        EditableProperties props = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
    //                        while (props.containsKey(propName)) {
    //                            rootIndex++;
    //                            propName = name + rootIndex + ".dir";   //NOI18N
    //                        }
    //                        String srcReference = refHelper.createForeignFileReference(sourceFolders[i], JavaProjectConstants.SOURCES_TYPE_JAVA);
    //                        Element root = doc.createElementNS(ScalaProjectType.NAMESPACE_SHARED,"root");   //NOI18N
    //                        root.setAttribute("id",propName);   //NOI18N
    //                        sourceRoots.appendChild(root);
    //                        props = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
    //                        props.put(propName,srcReference);
    //                        h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props); // #47609
    //                    }
    //                    for (int i = 0; i < testFolders.length; i++) {
    //                        if (!testFolders[i].exists()) {
    //                            testFolders[i].mkdirs();
    //                        }
    //                        String propName;
    //                        if (i == 0) {
    //                            //Name the first test root test.src.dir to be compatible with NB 4.0
    //                            propName = "test.src.dir";  //NOI18N
    //                        } else {
    //                            String name = testFolders[i].getName();
    //                            propName = "test." + name + ".dir"; // NOI18N
    //                        }
    //                        int rootIndex = 1;
    //                        EditableProperties props = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
    //                        while (props.containsKey(propName)) {
    //                            rootIndex++;
    //                            propName = "test." + name + rootIndex + ".dir"; // NOI18N
    //                        }
    //                        String testReference = refHelper.createForeignFileReference(testFolders[i], JavaProjectConstants.SOURCES_TYPE_JAVA);
    //                        Element root = doc.createElementNS(ScalaProjectType.NAMESPACE_SHARED, "root"); // NOI18N
    //                        root.setAttribute("id", propName); // NOI18N
    //                        testRoots.appendChild(root);
    //                        props = h.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH); // #47609
    //                        props.put(propName, testReference);
    //                        h.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, props);
    //                    }
    //                    h.putPrimaryConfigurationData(data,true);
    //                    ProjectManager.getDefault().saveProject(p);
    //                    return null;
    //                }
    //            });
    //        } catch (MutexException me ) {
    //            ErrorManager.getDefault().notify(me);
    //        }
    //        return h;
    //    }
    
    private static FileObject createProjectDir(File dir) throws IOException {
        Stack<String> stack = new Stack<String>();
        while (!dir.exists()) {
            stack.push(dir.getName());
            dir = dir.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(dir);
        if (dirFO == null) {
            refreshFileSystem(dir);
            dirFO = FileUtil.toFileObject(dir);
        }
        assert dirFO != null;
        while (!stack.isEmpty()) {
            dirFO = dirFO.createFolder((String) stack.pop());
        }
        return dirFO;
    }
    
    private static void refreshFileSystem(final File dir) throws FileStateInvalidException {
        File rootF = dir;
        while (rootF.getParentFile() != null) {
            rootF = rootF.getParentFile();
        }
        FileObject dirFO = FileUtil.toFileObject(rootF);
        assert dirFO != null : "At least disk roots must be mounted! " + rootF; // NOI18N
        dirFO.getFileSystem().refresh(false);
    }
    
}
