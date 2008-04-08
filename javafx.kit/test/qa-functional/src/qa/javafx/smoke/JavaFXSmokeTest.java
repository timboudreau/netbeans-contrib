package qa.javafx.smoke;

import java.io.File;
import java.io.FileFilter;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JRadioButtonOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.junit.NbTestSuite;

//import org.junit.Test;
//import static org.junit.Assert.*;

//import org.junit.Test;
//import org.junit.Before;

//import static org.junit.Assert.*;



/**
 *
 * @author Alexandr Scherbatiy sunflower@netbeans.org
 */


public class JavaFXSmokeTest extends JellyTestCase{  

    protected static final String PROJECT_NAME_HELLO_WORLD = "HelloWorld";

    public JavaFXSmokeTest(String name) {
        super(name);
    }

    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        //suite.addTest(new JavaFXSmokeTest("testLoadModule"));
        suite.addTest(new JavaFXSmokeTest("testProjectCreation"));
        suite.addTest(new JavaFXSmokeTest("testMainFile"));
        suite.addTest(new JavaFXSmokeTest("testEditor"));
        //suite.addTest(new JavaFXSmokeTest("testPreviewMode"));
        return suite;
    }

    public void testLoadModule() {
        //System.setOut(getLog());


        new JMenuBarOperator(MainWindowOperator.getDefault()).pushMenuNoBlock("Tools|Plugins");
        String pluginTitle = Bundle.getString("org.netbeans.modules.autoupdate.ui.actions.Bundle", "PluginManager_Panel_Name");

        JDialogOperator plugin = new JDialogOperator(pluginTitle);
        Util.waitProgressBar(plugin);

        JTabbedPaneOperator tabbedPane = new JTabbedPaneOperator(plugin);

        tabbedPane.selectPage("Installed");
        new JTextFieldOperator(tabbedPane).typeText("JavaFX");
        Util.sleep(2000);

        JTableOperator table = new JTableOperator(tabbedPane);

        if (table.getRowCount() == 0) {
            System.out.println("load module] (-) module is not loaded");

            tabbedPane.selectPage("Downloaded");
            //Util.showComponents(plugin.getSource());
            loadPlugins(plugin);

            Util.sleep();
        } else {
            System.out.println("load module] (+) module is loaded");
            new JButtonOperator(plugin, "Close").pushNoBlock();
        }
    }

    public void loadPlugins(JDialogOperator pluginManager) {
        //System.setOut(getLog());
        //String nbmsPath = Util.getXtestNBMsPath();
        //System.out.println("[xtest path] \"" + nbmsPath + "\"");
        File nbmsPath = Util.getXtestNBMsPath();
        System.out.println("[nbms path] \"" + nbmsPath + "\"");

        assertTrue("Dir does not exist:\"" + nbmsPath.getAbsolutePath(), nbmsPath.isDirectory());


        File[] nbmList = nbmsPath.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                System.out.println("[filter] \"" + pathname.getName() + "\"");

                return pathname.getName().endsWith(".nbm");
            }
        });

        System.out.println("[add plugins]");

        for (File file : nbmList) {

            new JButtonOperator(pluginManager, "Add Plugins...").push();

            JDialogOperator addPlugins = new JDialogOperator("Add Plugins");
            JTextFieldOperator textField = new JTextFieldOperator(addPlugins);
            textField.setText(file.getAbsolutePath());
            new JButtonOperator(addPlugins, "Open").push();
            System.out.println("[load] \"" + file.getAbsolutePath() + "\"");

            Util.sleep();
        }

        new JButtonOperator(pluginManager, "Install").pushNoBlock();

        JDialogOperator ideInstaller = new JDialogOperator("NetBeans IDE Installer");
        new JButtonOperator(ideInstaller, "Next >").pushNoBlock();

        new JRadioButtonOperator(ideInstaller).push();
        new JButtonOperator(ideInstaller, "Install").pushNoBlock();

        new JButtonOperator(new JDialogOperator("Validation Warning"), "Continue").push();
        new JButtonOperator(ideInstaller, "Finish").pushNoBlock();

    //  IDE Restarts
    }

    public void testProjectCreation() {
        NewProjectWizardOperator projectWizard = NewProjectWizardOperator.invoke();
        projectWizard.selectCategory("JavaFX");
        projectWizard.selectProject("JavaFX Script Application");
        projectWizard.next();
        new JTextFieldOperator(projectWizard, 0).setText(PROJECT_NAME_HELLO_WORLD);
        new JTextFieldOperator(projectWizard, 1).setText(System.getProperty("netbeans.user"));
        projectWizard.finish();
        
    }

    public void testMainFile() {
        try {
            TopComponentOperator mainFile = new TopComponentOperator("Main.fx");
        } catch (TimeoutExpiredException e) {
            fail("Known issue: Main FX file is not open after project creation");
        }

    }
    

    public void testEditor() {
        //System.setOut(getLog());

        Node projectNode = new Node(ProjectsTabOperator.invoke().tree(), PROJECT_NAME_HELLO_WORLD);

        Node mainFileNode = new Node(projectNode, "Source Packages|helloworld|Main.fx");

        new OpenAction().performPopup(mainFileNode);

        TopComponentOperator main = new TopComponentOperator("Main.fx");
        JTextComponentOperator textComponent = new JTextComponentOperator(main);
        String sample = "samples/helloworld/HelloWorld.fx";
        String text = Util.getSampleText(sample);
        assertNotNull("Sample \"" + sample + "\" was not found", text);
        textComponent.setText(text);

        //new JButtonOperator(main, "Enable Preview").push();
        //ContainerOperator cont = new ContainerOperator(main, new Util.ClassNameComponentChooser("PreviewButton"));
        //Util.showComponents(cont);
        //JToggleButtonOperator preview = new JToggleButtonOperator((JToggleButton) cont.getSource());
        //preview.push();
        //Util.sleep(4000);
        //Util.showComponents(main);
    }


}