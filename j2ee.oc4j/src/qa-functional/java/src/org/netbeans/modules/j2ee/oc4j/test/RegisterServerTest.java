package org.netbeans.modules.j2ee.oc4j.test;

import org.netbeans.junit.NbTestSuite;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.RuntimeTabOperator;
import org.netbeans.jellytools.modules.j2ee.nodes.J2eeServerNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JPasswordFieldOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

/**
 * A Test based on JellyTestCase. JellyTestCase redirects Jemmy output
 * to a log file provided by NbTestCase. It can be inspected in results.
 * It also sets timeouts necessary for NetBeans GUI testing.
 *
 * Any JemmyException (which is normally thrown as a result of an unsuccessful
 * operation in Jemmy) going from a test is treated by JellyTestCase as a test
 * failure; any other exception - as a test error.
 *
 * Additionally it:
 *    - closes all modal dialogs at the end of the test case (property jemmy.close.modal - default true)
 *    - generates component dump (XML file containing components information) in case of test failure (property jemmy.screen.xmldump - default false)
 *    - captures screen into a PNG file in case of test failure (property jemmy.screen.capture - default true)
 *    - waits at least 1000 ms between test cases (property jelly.wait.no.event - default true)
 *
 * @author pblaha
 * Created on January 19, 2007, 3:38 PM
 */
public class RegisterServerTest extends JellyTestCase {
    
    static final String OC4J_HOME = "/space/oc4j";
    static final String USER = "oc4jadmin";
    static final String PASSWD = "oc4jadmin";
    
    
    private static final String OC4J_LABEL = "OC4J 10g J2EE Container";
    
    /** Constructor required by JUnit */
    public RegisterServerTest(String name) {
        super(name);
    }
    
    /** Creates suite from particular test cases. You can define order of testcases here. */
    public static NbTestSuite suite() {
        NbTestSuite suite = new NbTestSuite();
        suite.addTest(new RegisterServerTest("testRegisterServer"));
        suite.addTest(new RegisterServerTest("testStartServer"));
        suite.addTest(new RegisterServerTest("testStopServer"));
        return suite;
    }
    
    /* Method allowing test execution directly from the IDE. */
    public static void main(java.lang.String[] args) {
        // run whole suite
        junit.textui.TestRunner.run(suite());
        // run only selected test case
        //junit.textui.TestRunner.run(new RegisterServerTest("test1"));
    }
    
    /** Called before every test case. */
    public void setUp() {
        System.out.println("########  "+getName()+"  #######");
    }
    
    /** Called after every test case. */
    public void tearDown() {
    }
    
    // Add test methods here, they have to start with 'test' name.
    
    /** Test case 1. */
    public void testRegisterServer() {
        RuntimeTabOperator runtimeOp =  new RuntimeTabOperator();
        Node serverNode = new Node(runtimeOp.tree(),
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.netbeans.modules.j2ee.deployment.impl.ui.Bundle", "SERVER_REGISTRY_NODE"));
        serverNode.callPopup().pushMenuNoBlock(
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.netbeans.modules.j2ee.deployment.impl.ui.actions.Bundle", "LBL_Add_Server_Instance"));
        JDialogOperator addDialogOp = new JDialogOperator(
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.netbeans.modules.j2ee.deployment.impl.ui.wizard.Bundle", "LBL_ASIW_Title"));
        JComboBoxOperator chooseServerComboOp = new JComboBoxOperator(addDialogOp);
        chooseServerComboOp.selectItem(OC4J_LABEL);
        new JButtonOperator(addDialogOp,
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.openide.Bundle", "CTL_NEXT")).push();
        
        String path = System.getProperty("oc4j.home", OC4J_HOME);
        new JTextFieldOperator(addDialogOp).setText(path);
        new JButtonOperator(addDialogOp,
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.openide.Bundle", "CTL_NEXT")).push();
        String user = System.getProperty("oc4j.user", USER);
        String passwd = System.getProperty("oc4j.passwd", PASSWD);
        new JTextFieldOperator(addDialogOp, 3).setText(user);
        new JPasswordFieldOperator(addDialogOp).setText(passwd);
        new JButtonOperator(addDialogOp,
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.openide.Bundle", "CTL_FINISH")).push();
        Node oc4jNode = new Node(runtimeOp.tree(),
                org.netbeans.jellytools.Bundle.getStringTrimmed("org.netbeans.modules.j2ee.deployment.impl.ui.Bundle", "SERVER_REGISTRY_NODE")
                + "|" + OC4J_LABEL);
    }
    
    public void testStartServer() {
        JemmyProperties.setCurrentTimeout("Waiter.WaitingTime", 240000);
        J2eeServerNode node = new J2eeServerNode(OC4J_LABEL);
        node.start();
    }
    
    public void testStopServer() {
        JemmyProperties.setCurrentTimeout("Waiter.WaitingTime", 240000); 
        J2eeServerNode node = new J2eeServerNode(OC4J_LABEL);
        node.stop();
    }
    
}
