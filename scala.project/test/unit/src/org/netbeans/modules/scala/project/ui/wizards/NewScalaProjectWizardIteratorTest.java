package org.netbeans.modules.scala.project.ui.wizards;

import java.io.File;
import org.netbeans.junit.NbTestCase;
import org.openide.WizardDescriptor;

/**
 * @author Martin Krauskopf
 */
public class NewScalaProjectWizardIteratorTest extends NbTestCase {
    
    public NewScalaProjectWizardIteratorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
    }
    
    public void testTestableInstantiate() throws Exception {
        WizardDescriptor settings = new WizardDescriptor(new WizardDescriptor.Panel[0]);
        NewScalaProjectWizardData data = new NewScalaProjectWizardData(settings);
        data.setProjectFolder(getWorkDirPath() + File.separator + "scalaProject");
        data.setProjectName("Testing Scala Project");
        NewScalaProjectWizardIterator.testableInstantiate(data);
    }
    
}
