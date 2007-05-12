package org.netbeans.modules.scala.project;

import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;

/**
 * @author Martin Krauskopf
 */
public class ScalaProjectTest extends TestBase {
    
    public ScalaProjectTest(String testName) {
        super(testName);
    }
    
    public void testProjectGenerally() throws Exception {
        ScalaProject project = generateScalaProject();
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sourceGroups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        assertEquals("SourceGroup for JavaProjectConstants.SOURCES_TYPE_JAVA", 1, sourceGroups.length);
    }
    
    public void testEvaluator() throws Exception {
        ScalaProject project = generateScalaProject();
        assertEquals("src dir evaluated", "src", project.getEvaluator().evaluate("${src.dir}"));
    }
    
}
