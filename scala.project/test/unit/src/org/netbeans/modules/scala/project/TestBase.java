package org.netbeans.modules.scala.project;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * @author Martin Krauskopf
 */
public class TestBase extends NbTestCase {
    
    protected static final String MAIN_CLASS_PATH = "src/foo/HelloWorld.scala";
    
    public TestBase(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
    }
    
    /**
     * Calls in turn {@link TestBase#generateScalaProject(String)} with the
     * &quot;scalaProject&quot; as the parameter.
     */
    protected ScalaProject generateScalaProject() throws IOException {
        return generateScalaProject("scalaProject");
    }
    
    /**
     * Calls in turn {@link TestBase#generateScalaProject(File, String)}
     * with the {@link #getWorkDir()} as a first parameter.
     */
    protected ScalaProject generateScalaProject(final String prjDir) throws IOException {
        return generateScalaProject(getWorkDir(), prjDir);
    }
    
    /**
     * Returns {@link ScalaProject} created in the {@link #getWorkDir()}/prjDir
     * with default display name set to <em>Testing Scala Project</em>.
     */
    public static ScalaProject generateScalaProject(final File workDir, final String prjDir) throws IOException {
        FileObject prjDirFO = generateScalaProjectDirectory(workDir, prjDir);
        return (ScalaProject) ProjectManager.getDefault().findProject(prjDirFO);
    }
    
    /**
     * The same as {@link #generateStandaloneModule(File, String)} but without
     * <em>opening</em> a generated project.
     */
    public static FileObject generateScalaProjectDirectory(File workDir, String prjDir) throws IOException {
        File prjDirF = file(workDir, prjDir);
        AntProjectHelper helper = ScalaProjectGenerator.createProject(prjDirF, "Testing Scala Project");
        FileObject mainClassFO = FileUtil.createData(helper.getProjectDirectory(), MAIN_CLASS_PATH);
        createMain(mainClassFO);
        return helper.getProjectDirectory();
    }
    
    private static void createMain(final FileObject fo) throws IOException  {
        FileLock lock = fo.lock();
        try {
            PrintWriter pw = new PrintWriter(fo.getOutputStream(lock));
            pw.println("package foo");
            pw.println("object HelloWorld {");
            pw.println("  def main(args : Array[String]) : Unit = {}");
            pw.println("}");
            pw.close();
        } finally {
            lock.releaseLock();
        }
    }
    
    /**
     * Just calls <code>File(root, path.replace('/', File.separatorChar));</code>
     */
    protected static File file(File root, String path) {
        return new File(root, path.replace('/', File.separatorChar));
    }
    
}
