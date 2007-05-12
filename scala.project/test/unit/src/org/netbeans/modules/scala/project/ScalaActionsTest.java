package org.netbeans.modules.scala.project;

import static org.netbeans.spi.project.ActionProvider.*;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ActionProvider;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * @author Martin Krauskopf
 */
public class ScalaActionsTest extends TestBase {
    
    private ScalaActions actionProvider;
    
    private DataObject mainClassDO;
    
    public ScalaActionsTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        Project prj = generateScalaProject();
        mainClassDO = DataObject.find(prj.getProjectDirectory().getFileObject(
                TestBase.MAIN_CLASS_PATH));
        actionProvider = (ScalaActions) prj.getLookup().lookup(ScalaActions.class);
    }
    
    public void testGetTargetNames() throws Exception {
        //        Properties p = new Properties();
        Lookup context = null;
        String[] targets;
        
        //        assertTrue(COMMAND_BUILD + " enabled",
        //                actionProvider.isActionEnabled(ActionProvider.COMMAND_BUILD, context));
        //        actionProvider.invokeAction(COMMAND_BUILD, context);
        //
        // test COMMAND_RUN_SINGLE
        //        context = Lookups.fixed(mainClassDO);
        //        assertTrue(COMMAND_RUN_SINGLE + " enabled",
        //                actionProvider.isActionEnabled(ActionProvider.COMMAND_RUN_SINGLE, context));
        //        actionProvider.invokeAction(COMMAND_RUN_SINGLE, context);
        
        
        //        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        //        try {
        //            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        //        } finally {
        //            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        //        }
        //        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        //        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        //        assertEquals("Unexpected target name", "run-single", targets[0]);
        //        assertEquals("There must be one target parameter", 2, p.keySet().size());
        //        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        //        assertEquals("There must be be target parameter", "foo.Main", p.getProperty("run.class"));
        //        p = new Properties();
        //        context = Lookups.fixed(new DataObject[] {someSource2});
        //        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.FALSE;
        //        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        //        try {
        //            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        //        } finally {
        //            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        //            AppletSupport.unitTestingSupport_isApplet = null;
        //        }
        //        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        //        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        //        assertEquals("Unexpected target name", "run-applet", targets[0]);
        //        assertEquals("There must be one target parameter", 2, p.keySet().size());
        //        assertEquals("There must be be target parameter", "foo/Main.java", p.getProperty("javac.includes"));
        //        FileObject appletHtml = build.getFileObject("Main", "html");
        //        assertNotNull("Applet HTML page must be generated", appletHtml);
        //        URL appletUrl = URLMapper.findURL(appletHtml, URLMapper.EXTERNAL);
        //        assertEquals("There must be be target parameter", appletUrl.toExternalForm(), p.getProperty("applet.url"));
        //        p = new Properties();
        //        context = Lookups.fixed(new DataObject[] {someTest1});
        //        MainClassChooser.unitTestingSupport_hasMainMethodResult = Boolean.TRUE;
        //        AppletSupport.unitTestingSupport_isApplet = Boolean.TRUE;
        //        try {
        //            targets = actionProvider.getTargetNames(ActionProvider.COMMAND_RUN_SINGLE, context, p);
        //        } finally {
        //            MainClassChooser.unitTestingSupport_hasMainMethodResult = null;
        //            AppletSupport.unitTestingSupport_isApplet = null;
        //        }
        //        assertNotNull("Must found some targets for COMMAND_RUN_SINGLE", targets);
        //        assertEquals("There must be one target for COMMAND_RUN_SINGLE", 1, targets.length);
        //        assertEquals("Unexpected target name", "test-single", targets[0]);
        //        assertEquals("There must be one target parameter", 2, p.keySet().size());
        //        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("javac.includes"));
        //        assertEquals("There must be be target parameter", "foo/BarTest.java", p.getProperty("test.includes"));
    }
    
}
