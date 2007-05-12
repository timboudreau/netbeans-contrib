package org.netbeans.modules.scala.project.ui;

import org.netbeans.api.project.Project;
import org.netbeans.modules.scala.project.TestBase;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.filesystems.FileObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 * @author Martin Krauskopf
 */
public class ScalaLogicalViewTest extends TestBase {
    
    public ScalaLogicalViewTest(String testName) {
        super(testName);
    }
    
    public void testFindPath() throws Exception {
        Project project = generateScalaProject();
        LogicalViewProvider lvp = (LogicalViewProvider) project.getLookup().lookup(LogicalViewProvider.class);
        assertNotNull("have a LogicalViewProvider", lvp);
        assertNotNull("found " + TestBase.MAIN_CLASS_PATH, find(lvp, project, TestBase.MAIN_CLASS_PATH));
    }
    
    private Node find(final LogicalViewProvider lvp, final Project p, final String path) throws Exception {
        FileObject f = p.getProjectDirectory().getFileObject(path);
        assertNotNull("found " + path, f);
        Node root = new FilterNode(lvp.createLogicalView());
        return lvp.findPath(root, f);
    }
    
}
