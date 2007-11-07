/*
 * ServerPluginTests.java
 *
 * Created on May 16, 2007, 1:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.sun.ws7;

import org.netbeans.modules.j2ee.sun.ws7.util.HtmlLogger;
import org.netbeans.modules.j2ee.sun.ws7.util.PropBean;
import org.netbeans.modules.j2ee.sun.ws7.util.PropertyHandler;
import org.netbeans.modules.j2ee.sun.ws7.util.ScreenCapturer;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewFileWizardOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.RuntimeTabOperator;
import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jellytools.actions.PropertiesAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.junit.NbTestSuite;
import java.awt.Robot;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.netbeans.jellytools.properties.Property;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.JTableOperator;

/**
 *
 * @author Prabushankar.Chinnasamy
 */
public class ServerPluginTests extends JellyTestCase {

    public static PropBean pb = new PropBean("C:\\nb.properties");

    public static Logger logger = null;
    public static FileHandler fh = null;
    public static ConsoleHandler ch = null;
    public static boolean isRemote = false;
    public static int sleepTimeMedium = 2000;
    public static boolean isFail = false;
    public static String tcName = null;
    public static boolean isRemoveServer = true;
    public static boolean isTestCase = true;

    private int numberOfLabels;

    private int numberOfTextFields;

    private JTextField textFieldOne;

    private int numberOfButtons;

    private JTextField textFieldTwo;

    private int numberOfComboBoxes;


    /** Creates a new instance of ServerPluginTests */
    public ServerPluginTests(String name) {
        super(name);
    }

    /** Creates suite from particular test cases. You can define order of testcases here. */
    public static NbTestSuite suite() {
        BufferedReader input = null;
        NbTestSuite suite = new NbTestSuite();
        try {

            input = new BufferedReader( new FileReader(pb.getTcList()) );
            String line = null;
            while ((( line = input.readLine()) != null)) {
                if( !(line.startsWith("#")))
                    suite.addTest(new ServerPluginTests(line));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        return suite;
    }

    /* Method allowing test execution directly from the IDE. */
    public static void main(java.lang.String[] args) {

        FileHandler fh = null;
        try {
            new File(pb.getLogFile()).delete();
            fh = new FileHandler(pb.getLogFile());
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        logger = logger = Logger.getLogger("org.netbeans.modules.j2ee.sun.ws7");

        fh.setFormatter(new HtmlLogger());
        logger.addHandler(fh);
        logger.setLevel(Level.ALL);

        /*IMPORTANT: Local or Remote*/
        if(pb.isRemote()) {
            pb.setAdminPort(pb.getRemotePort());
            pb.setAdminSSLPort(pb.getRemoteSSLPort());
            pb.setConfigName(pb.getConfigName());
            pb.setVsName(pb.getVsName());
        }

        // run whole suite
        junit.textui.TestRunner.run(suite());
        fh.close();
    }

    /** Called before every test case. */
    public void setUp() {
        logger.info("########  " + getName() + "  #######");
        isFail = true;
    }

    /** Called after every test case. */
    public void tearDown() throws InterruptedException {

        if(isRemoveServer) {
            RuntimeTabOperator rto = new RuntimeTabOperator();
            Node comp1 = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
            JPopupMenuOperator prop2 = comp1.callPopup();
            Thread.sleep(sleepTimeMedium);
            prop2.pushMenuNoBlock("Remove");
            logger.info("Server instance " + pb.getRegistrationName() + " removed.");
            Thread.sleep(sleepTimeMedium);

            JDialogOperator ifSure = new JDialogOperator("Remove Server Instance");
            new JButtonOperator(ifSure, "Yes").clickMouse();

        }
        if(isTestCase) {
            if(isFail) {
                logger.severe("Testcase " + tcName + " <font color=red><b>failed</b></font>.");
            } else{
                logger.info("Testcase " + tcName + " <font color=green><b>passed</b></font>.");
            }
        }
        isFail = false;
        tcName = null;
        isRemoveServer = false;
        isTestCase = true;
    }

    // Add test methods here, they have to start with 'test' name.
    public void TS_COCO_SR_01() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = true;

        logger.info("TS_COCO_SR_01: Register a local webserver with SSL port successfully.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem("Sun Java System Web Server 7.0");
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is " + pb.getRegistrationName());
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getWsInstallDir());

        System.out.println("IsRemote: " + pb.isRemote());
        if(pb.isRemote()) {
            JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
            isRemotecbo.changeSelection(true);

            JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
            adminHostField.requestFocus();
            adminHostField.setText(pb.getRemoteHost());
            Thread.sleep(5000);
        }

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        logger.info("Admin SSL port set to " + pb.getAdminSSLPort());
        adminPortField.setText(pb.getAdminSSLPort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();
        logger.info("Admin user is " + pb.getAdminUser());
        adminUserField.setText(pb.getAdminUser());

        JTextFieldOperator adminPassword = new JTextFieldOperator(jdo,0);
        adminPassword.requestFocus();
        adminPassword.setText(pb.getAdminPwd());

        Thread.sleep(sleepTimeMedium);
        Thread.sleep(sleepTimeMedium);

        JButtonOperator finish = new JButtonOperator(jdo, "Finish");
        finish.clickMouse();

        Thread.sleep(sleepTimeMedium);

        /********Verification********/
        comp.collapse();
        comp.expand();

        Node comp1 = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        JPopupMenuOperator prop = comp1.callPopup();
        prop.pushMenuNoBlock("Properties");
        JDialogOperator propDialog = new JDialogOperator("Servers");

        //pb.getSjsString() = "debug";
        String[] expectedValues = { pb.getRegistrationName(), pb.getSjsString(), pb.getAdminPwd(), pb.isRemote()?pb.getRemoteHost():pb.getLocalHost() + ":" + pb.getAdminSSLPort(), pb.getWsInstallDir(), pb.getAdminUser()};
        String[] names = { "Instance Name", "Server Type", "Admin Password", "Host", "SJSWS7.0 Installation Directory", "Admin User"};
        String msg = null;
        for(int i = 0; i < expectedValues.length; i++) {
            if((expectedValues[i].equals(new JTextFieldOperator(propDialog,i).getText()))) {
                msg = "PASS: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.info(msg);
                isFail = false;
                assertEquals(msg, expectedValues[i], new JTextFieldOperator(propDialog,i).getText());
            } else {
                msg = "FAILED: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.severe(msg);
            }
        }

        Thread.sleep(sleepTimeMedium);

        /*********Screen Capture*********/
        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        Thread.sleep(sleepTimeMedium);

        propDialog.close();

    }

    public void TS_COCO_SR_02() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        String host = pb.getRemoteHost();
        isRemote = false; //localhost
        isRemoveServer = true;

        logger.info("<B>TS_COCO_SR_02: Register a local webserver with non SSL port successfully.</B>");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getWsInstallDir());

        if(pb.isRemote()) {
            JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
            isRemotecbo.changeSelection(true);

            JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
            adminHostField.requestFocus();
            adminHostField.setText(pb.getRemoteHost());
            Thread.sleep(5000);
        }
        JCheckBoxOperator isSSLcbo = new JCheckBoxOperator(jdo,"SSL port");
        isSSLcbo.changeSelection(false);


        Thread.sleep(5000);

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        logger.info("Admin non-SSL port set to " + pb.getAdminPort());
        adminPortField.setText(pb.getAdminPort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();
        logger.info("Admin user is " + pb.getAdminUser());
        adminUserField.setText(pb.getAdminUser());

        JTextFieldOperator adminPassword = new JTextFieldOperator(jdo,0);
        adminPassword.requestFocus();
        adminPassword.setText(pb.getAdminPwd());

        Thread.sleep(sleepTimeMedium);
        Thread.sleep(sleepTimeMedium);

        JButtonOperator finish = new JButtonOperator(jdo, "Finish");
        finish.clickMouse();

        Thread.sleep(sleepTimeMedium);

        /********Verification********/
        comp.collapse();
        comp.expand();

        Node comp1 = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        JPopupMenuOperator prop = comp1.callPopup();
        prop.pushMenuNoBlock("Properties");
        JDialogOperator propDialog = new JDialogOperator("Servers");

        //pb.getSjsString() = "debug";
        String[] expectedValues = { pb.getRegistrationName(), pb.getSjsString(), pb.getAdminPwd(), pb.isRemote()?pb.getRemoteHost():pb.getLocalHost() + ":" + pb.getAdminPort(), pb.getWsInstallDir(), pb.getAdminUser()};
        String[] names = { "Instance Name", "Server Type", "Admin Password", "Host", "SJSWS7.0 Installation Directory", "Admin User"};
        String msg = null;

        for(int i = 0; i < expectedValues.length; i++) {
            if((expectedValues[i].equals(new JTextFieldOperator(propDialog,i).getText()))) {
                msg = "PASS: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.info(msg);
                isFail = false;
                assertEquals(msg, expectedValues[i], new JTextFieldOperator(propDialog,i).getText());
            } else {
                msg = "FAILED: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.severe(msg);
                isFail = true;
            }
        }

        Thread.sleep(sleepTimeMedium);

        /*********Screen Capture*********/
        new ScreenCapturer().capture(getName(),pb.getResultDir(),logger);

        Thread.sleep(sleepTimeMedium);

        propDialog.close();

    }


    public void TS_COCO_SR_03() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = true; //localhost
        isRemoveServer = true;
        String host = pb.getRemoteHost();
        logger.info("TS_COCO_SR_03: Register a remote webserver with SSL port successfully.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getWsInstallDir());

        JCheckBoxOperator isSSLcbo = new JCheckBoxOperator(jdo,"SSL port");
        isSSLcbo.changeSelection(true);

        JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
        isRemotecbo.changeSelection(true);

        JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
        adminHostField.requestFocus();
        adminHostField.setText(host);
        Thread.sleep(5000);

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        logger.info("Admin SSL port set to " + pb.getRemoteSSLPort());
        adminPortField.setText(pb.getRemoteSSLPort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();
        logger.info("Admin user is " + pb.getAdminUser());
        adminUserField.setText(pb.getAdminUser());

        JTextFieldOperator adminPassword = new JTextFieldOperator(jdo,0);
        adminPassword.requestFocus();
        adminPassword.setText(pb.getAdminPwd());

        Thread.sleep(sleepTimeMedium);
        Thread.sleep(sleepTimeMedium);

        JButtonOperator finish = new JButtonOperator(jdo, "Finish");
        finish.clickMouse();

        Thread.sleep(sleepTimeMedium);

        /********Verification********/
        comp.collapse();
        comp.expand();

        Node comp1 = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        JPopupMenuOperator prop = comp1.callPopup();
        prop.pushMenuNoBlock("Properties");
        JDialogOperator propDialog = new JDialogOperator("Servers");

        //pb.getSjsString() = "debug";
        String[] expectedValues = { pb.getRegistrationName(), pb.getSjsString(), pb.getAdminPwd(), host + ":" + pb.getRemoteSSLPort(), pb.getWsInstallDir(), pb.getAdminUser()};
        String[] names = { "Instance Name", "Server Type", "Admin Password", "Host", "SJSWS7.0 Installation Directory", "Admin User"};
        String msg = null;
        for(int i = 0; i < expectedValues.length; i++) {
            if((expectedValues[i].equals(new JTextFieldOperator(propDialog,i).getText()))) {
                msg = "PASS: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.info(msg);
                isFail = false;
                assertEquals(msg, expectedValues[i], new JTextFieldOperator(propDialog,i).getText());
            } else {
                msg = "FAILED: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.severe(msg);
                isFail = true;
            }

        }

        Thread.sleep(sleepTimeMedium);

        /*********Screen Capture*********/
        new ScreenCapturer().capture(getName(),pb.getResultDir(),logger);

        Thread.sleep(sleepTimeMedium);

        propDialog.close();

    }


    public void TS_COCO_SR_04() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = true;
        isRemoveServer = true;
        String host = pb.getRemoteHost();
        logger.info("TS_COCO_SR_04: Register a remote webserver with non SSL port successfully.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getWsInstallDir());

        JCheckBoxOperator isSSLcbo = new JCheckBoxOperator(jdo,"SSL port");
        isSSLcbo.changeSelection(false);

        JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
        isRemotecbo.changeSelection(true);

        JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
        adminHostField.requestFocus();
        adminHostField.setText(host);
        Thread.sleep(5000);

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        logger.info("Admin non-SSL port set to " + pb.getRemotePort());
        adminPortField.setText(pb.getRemotePort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();
        logger.info("Admin user is " + pb.getAdminUser());
        adminUserField.setText(pb.getAdminUser());

        JTextFieldOperator adminPassword = new JTextFieldOperator(jdo,0);
        adminPassword.requestFocus();
        adminPassword.setText(pb.getAdminPwd());

        Thread.sleep(sleepTimeMedium);
        Thread.sleep(sleepTimeMedium);

        JButtonOperator finish = new JButtonOperator(jdo, "Finish");
        finish.clickMouse();

        Thread.sleep(sleepTimeMedium);

        /********Verification********/
        comp.collapse();
        comp.expand();

        Node comp1 = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        JPopupMenuOperator prop = comp1.callPopup();
        prop.pushMenuNoBlock("Properties");
        JDialogOperator propDialog = new JDialogOperator("Servers");

        //pb.getSjsString() = "debug";
        String[] expectedValues = { pb.getRegistrationName(), pb.getSjsString(), pb.getAdminPwd(), host + ":" + pb.getRemotePort(), pb.getWsInstallDir(), pb.getAdminUser()};
        String[] names = { "Instance Name", "Server Type", "Admin Password", "Host", "SJSWS7.0 Installation Directory", "Admin User"};
        String msg = null;
        for(int i = 0; i < expectedValues.length; i++) {
            if((expectedValues[i].equals(new JTextFieldOperator(propDialog,i).getText()))) {
                msg = "PASS: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.info(msg);
                isFail = false;
                assertEquals(msg, expectedValues[i], new JTextFieldOperator(propDialog,i).getText());
            } else {
                msg = "FAIL: " + ">" + names[i] + " expected \"" + expectedValues[i] + "\". Found \"" + new JTextFieldOperator(propDialog,i).getText() + "\"";
                logger.severe(msg);
                isFail = true;
            }
        }

        Thread.sleep(sleepTimeMedium);

        /*********Screen Capture*********/
        new ScreenCapturer().capture(getName(),pb.getResultDir(),logger);

        Thread.sleep(sleepTimeMedium);

        propDialog.close();

    }

    public void TS_COCO_SR_05() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = true;


        logger.info("TS_COCO_SR_05: Registration with local webserver non-existent installation directory.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getWsInstallDir());

        if(pb.isRemote()) {
            JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
            isRemotecbo.changeSelection(true);

            JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
            adminHostField.requestFocus();
            adminHostField.setText(pb.getRemoteHost());
            Thread.sleep(5000);
        }

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();

        JLabelOperator errLabel = new JLabelOperator(jdo,10);
        if(!(errLabel.getText().equals("Please enter valid Administration Port Number"))) {
            isFail = true;
            logger.severe("FAILED:>Over a valid directory path, the message has to be \"Please enter valid Administration Port Number\"");
        }

        /*****Invalid installation directory*****/
        Thread.sleep(sleepTimeMedium);
        installationDirField.requestFocus();
        installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getBogusWsInstallDir());
        Thread.sleep(sleepTimeMedium);
        adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        Thread.sleep(sleepTimeMedium);
        errLabel = new JLabelOperator(jdo,10);

        if((errLabel.getText().equals("Please choose a Valid Sun Java System Web Server 7.0 installation."))) {
            isFail = false;
            String msg = "Expected message was \"Please choose a Valid Sun Java System Web Server 7.0 installation.\" Actual was \"" + errLabel.getText()+ "\"";
            logger.info(msg);
            assertEquals(msg, "Please choose a Valid Sun Java System Web Server 7.0 installation.",errLabel.getText());
        } else {
            isFail = true;
            String msg = "Expected message was \"Please choose a Valid Sun Java System Web Server 7.0 installation.\" Actual was \"" + errLabel.getText()+ "\"";
            logger.severe(msg);
            assertEquals(msg, "Please choose a Valid Sun Java System Web Server 7.0 installation.",errLabel.getText());

        }
        isRemoveServer = false;

        /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(pb.getResultDir() + imageName + ".png"));
            logger.info("Screen captured at " + pb.getResultDir() + imageName + ".png" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Thread.sleep(3000);
        jdo.close();


        System.out.println(errLabel.getText());

    }


    public void TS_COCO_SR_06() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = true;

        logger.info("TS_COCO_SR_06: Registration with remote webserver non-existent installation directory. ");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
        isRemotecbo.changeSelection(true);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.requestFocus();
        installationDirField.setText(pb.getWsInstallDir());

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();

        JLabelOperator errLabel = new JLabelOperator(jdo,10);
        if(!(errLabel.getText().equals("Please enter valid Administration Port Number"))) {
            isFail = true;
            logger.severe("FAILED:>Over a valid directory path, the message has to be \"Please enter valid Administration Port Number\"");
        }

        /*****Invalid installation directory*****/
        Thread.sleep(sleepTimeMedium);
        installationDirField.requestFocus();
        installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.setText(pb.getBogusWsInstallDir());
        Thread.sleep(sleepTimeMedium);
        adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        Thread.sleep(sleepTimeMedium);
        errLabel = new JLabelOperator(jdo,10);

        if((errLabel.getText().equals("Please choose a Valid Sun Java System Web Server 7.0 installation."))) {
            isFail = false;
            String msg = "Expected message was \"Please choose a Valid Sun Java System Web Server 7.0 installation.\" Actual was \"" + errLabel.getText()+ "\"";
            logger.info(msg);
            assertEquals(msg, "Please choose a Valid Sun Java System Web Server 7.0 installation.",errLabel.getText());
        } else {
            isFail = true;
            String msg = "Expected message was \"Please choose a Valid Sun Java System Web Server 7.0 installation.\" Actual was \"" + errLabel.getText()+ "\"";
            logger.severe(msg);
            assertEquals(msg, "Please choose a Valid Sun Java System Web Server 7.0 installation.",errLabel.getText());
        }
        isRemoveServer = false;

        /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(pb.getResultDir() + imageName + ".png"));
            logger.info("Screen captured at " + pb.getResultDir() + imageName + ".png" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Thread.sleep(3000);
        jdo.close();


    }

    public void TS_COCO_SR_07() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = true;

        logger.info("TS_COCO_SR_07: Registration with local webserver non-existent port.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.requestFocus();
        installationDirField.setText(pb.getWsInstallDir());

        if(pb.isRemote()) {
            JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
            isRemotecbo.changeSelection(true);

            JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
            adminHostField.requestFocus();
            adminHostField.setText(pb.getRemoteHost());
            Thread.sleep(5000);
        }

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        adminPortField.setText(pb.getBogusAdminPort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();


        JLabelOperator errLabel = new JLabelOperator(jdo,10);
        if((errLabel.getText().equals("Please enter valid Administration Port Number"))) {
            String msg = "Expected error message is \"Please enter valid Administration Port Number\". Actual is " + errLabel.getText();
            isFail = false;
            logger.info(msg);
            assertEquals(msg,"Please enter valid Administration Port Number",errLabel.getText());
        } else {
            String msg = "Expected error message is \"Please enter valid Administration Port Number\". Actual is " + errLabel.getText();
            isFail = true;
            logger.severe(msg);
            assertEquals(msg,"Please enter valid Administration Port Number",errLabel.getText());
        }

        isRemoveServer = false;

        /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(pb.getResultDir() + imageName + ".png"));
            logger.info("Screen captured at " + pb.getResultDir() + imageName + ".png" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Thread.sleep(3000);
        jdo.close();


    }


    public void TS_COCO_SR_08() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = true;

        logger.info("TS_COCO_SR_08: Registration with remote webserver non-existent port.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.selectItem(pb.getSjsString());
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        jto.clearText();
        logger.info("Registered server instance is Test.");
        jto.setText(pb.getRegistrationName());
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
        isRemotecbo.changeSelection(true);

        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);
        installationDirField.requestFocus();
        installationDirField.setText(pb.getWsInstallDir());

        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.requestFocus();
        adminPortField.setText(pb.getBogusAdminPort());

        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);
        adminUserField.requestFocus();


        JLabelOperator errLabel = new JLabelOperator(jdo,10);
        if((errLabel.getText().equals("Please enter valid Administration Port Number"))) {
            String msg = "Expected error message is \"Please enter valid Administration Port Number\". Actual is " + errLabel.getText();
            isFail = false;
            logger.info(msg);
            assertEquals(msg,"Please enter valid Administration Port Number",errLabel.getText());
        } else {
            String msg = "Expected error message is \"Please enter valid Administration Port Number\". Actual is " + errLabel.getText();
            isFail = true;
            logger.severe(msg);
            assertEquals(msg,"Please enter valid Administration Port Number",errLabel.getText());
        }

        isRemoveServer = false;

        /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(pb.getResultDir() + imageName + ".png"));
            logger.info("Screen captured at " + pb.getResultDir() + imageName + ".png" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Thread.sleep(3000);
        jdo.close();


    }

    public void TS_COCO_JDBC_01() throws InterruptedException{
        isFail = false;
        isRemoveServer = false;
        NewFileWizardOperator nfwo = NewFileWizardOperator.invoke();

        nfwo.selectProject("TestApp");
        Thread.sleep(1500);
        nfwo.selectCategory("Sun Web Server 7.0 Resources");
        Thread.sleep(1500);
        nfwo.selectFileType("JDBC Resource");
        Thread.sleep(1500);
        nfwo.next();
        Thread.sleep(1500);


        JTextFieldOperator jndiName = new JTextFieldOperator(nfwo,0);
        jndiName.requestFocus();
        jndiName.setText("Test");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JComboBoxOperator isEnabledCombo = new JComboBoxOperator(nfwo,"true");
        isEnabledCombo.requestFocus();
        isEnabledCombo.selectItem("true");
        Thread.sleep(1500);

        JComboBoxOperator vendor = new JComboBoxOperator(nfwo,"Oracle-Oracle driver");
        vendor.requestFocus();
        vendor.selectItem("Apache Derby");
        Thread.sleep(1500);

        new JTextFieldOperator(nfwo,1).requestFocus();

        new JTextFieldOperator(nfwo,1).setText("org.apache.derby.jdbc.ClientDataSource");

        JTextFieldOperator desc = new JTextFieldOperator(nfwo,2);
        desc.requestFocus();
        desc.setText("Test JDBC resource");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);

        Thread.sleep(2000);
        new JButtonOperator(nfwo,"Finish").clickMouse();


        /*Registration*/
        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        Node jdbcFile = new Node(pto.tree(),"TestApp" + "|Server Resources|jdbc.sun-ws7-resource");
        Thread.sleep(1500);
        JPopupMenuOperator pop = jdbcFile.callPopup();
        pop.pushMenu("Register");
        Thread.sleep(1500);

        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator infoBar = new NbDialogOperator("Information");
        Thread.sleep(1500);
        logger.info("Alert found. " + new JLabelOperator(infoBar, "Resource").getText());
        new JButtonOperator(infoBar,"Ok").clickMouse();

        RuntimeTabOperator rto = RuntimeTabOperator.invoke();
        Node jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        Thread.sleep(1500);
        JPopupMenuOperator refresh = jdbcResource.callPopup();
        Thread.sleep(1500);
        refresh.pushMenu("Refresh");
        Thread.sleep(1500);

        rto = RuntimeTabOperator.invoke();
        jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() +":" +pb.getVsName() + "|JDBC Resources|Test");
        String expectedPath = "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getVsName() + "|JDBC Resources|Test";
        if(jdbcResource.getPath().equals(expectedPath)) {
            isFail=false;
            logger.info("Path found. " + expectedPath );
        } else {
            logger.severe("FAIL: Path not found. " + expectedPath );
        }
        System.out.println(jdbcResource.getPath());
        Thread.sleep(1500);
        JPopupMenuOperator prop = jdbcResource.callPopup();
        Thread.sleep(1500);
        prop.pushMenu("Properties");
        Thread.sleep(1500);


        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator propSheet = new NbDialogOperator("Test - Properties");
        propSheet.close();
    }

    public void TS_COCO_JDBC_02() throws InterruptedException{
        isFail = false;
        tcName = getName();
        isRemoveServer = false;
        NewFileWizardOperator nfwo = NewFileWizardOperator.invoke();
        /*HARDCODED*/
        nfwo.selectProject("TestApp");
        Thread.sleep(1500);
        nfwo.selectCategory("Sun Web Server 7.0 Resources");
        Thread.sleep(1500);
        nfwo.selectFileType("JDBC Resource");
        Thread.sleep(1500);
        nfwo.next();
        Thread.sleep(1500);

        /*JNDI Name is mandatory*/
        JLabelOperator jndiNameLabel = new JLabelOperator(nfwo,"JNDI");
        if("JNDI Name:".equals(jndiNameLabel.getText())) {
            isFail = true;
            logger.severe("JNDI Name field not marked mandatory(*)");
        }

        /*Database Vendor is mandatory*/
        /*
        JLabelOperator databaseVendor = new JLabelOperator(nfwo,"Database");
        if("Database Vendor:".equals(databaseVendor.getText())) {
            isFail = true;
            logger.severe("Database Vendor field not marked mandatory(*)");
        }*/

        /*Data Source Class Name is mandatory*/
        JLabelOperator dsClassName = new JLabelOperator(nfwo,"Data ");
        if("Data Source Class Name:".equals(dsClassName.getText())) {
            isFail = true;
            logger.severe("Data Source Class Name field not marked mandatory(*)");
        }

        new ScreenCapturer().capture(getName(),pb.getResultDir(),logger);
        nfwo.close();
    }

    public void TS_COCO_JDBC_04() throws InterruptedException {
        isFail = false;
        tcName = getName();
        isRemoveServer = false;
        logger.info("TS_COCO_JDBC_04: Edit a JDBC resource from Runtime tab successfully.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Thread.sleep(1000);
        Node comp = new Node(rto.tree(),"Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() +":" +pb.getVsName() + "|JDBC Resources|Test");
        new PropertiesAction().perform(comp);
        PropertySheetOperator pso = new PropertySheetOperator("Test");
        PropertyHandler ph = new PropertyHandler(pso);

        Property px = null;
        for(int i = 1; i < new JTableOperator(pso).getRowCount(); i++) {
            px = new Property(pso,i);
            logger.info("Initial value of \"" + px.getName() + "\" = " + px.getValue());
        }

        String enabled = ph.getProperty("enabled");
        String waitTimeout = ph.getProperty("wait-timeout");
        String idleTimeout = ph.getProperty("idle-timeout");
        String minConn = ph.getProperty("min-connections");
        String jndi = ph.getProperty("jndi-name");
        String maxConn = ph.getProperty("max-connections");


        new ScreenCapturer().capture(getName() + "_initial",pb.getResultDir(),logger);
        Thread.sleep(2000);

        ph.setProperty("enabled","false");
        ph.setProperty("wait-timeout","120");
        ph.setProperty("idle-timeout","60");
        ph.setProperty("min-connections","16");
        ph.setProperty("max-connections","64");
        //jndi-name is read-only : Is a Bug
        //ph.setProperty("jndi-name","NewTest");

        pso.close();
        Thread.sleep(2000);

        comp = new Node(rto.tree(),"Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() +":" +pb.getVsName() + "|JDBC Resources|Test");
        new PropertiesAction().perform(comp);
        pso = new PropertySheetOperator("Test");
        ph = new PropertyHandler(pso);

        if(!ph.getProperty("enabled").equals("false")) {
            isFail = true;
            logger.severe("Property \"enabled\" not set to false. Remains " + ph.getProperty("enabled") );
        }

        if(!ph.getProperty("wait-timeout").equals("120")) {
            isFail = true;
            logger.severe("Property \"wait-timeout\" not set to 120. Remains " + ph.getProperty("wait-timeout") );
        }

        if(!ph.getProperty("idle-timeout").equals("60")) {
            isFail = true;
            logger.severe("Property \"idle-timeout\" not set to 60. Remains " + ph.getProperty("idle-timeout") );
        }

        if(!ph.getProperty("min-connections").equals("16")) {
            isFail = true;
            logger.severe("Property \"min-connections\" not set to 16. Remains " + ph.getProperty("min-connections") );
        }

        if(!ph.getProperty("max-connections").equals("64")) {
            isFail = true;
            logger.severe("Property \"max-connections\" not set to 64. Remains " + ph.getProperty("max-connections") );
        }

        new ScreenCapturer().capture(getName() + "_edited",pb.getResultDir(),logger);
        Thread.sleep(2000);
        px = null;
        for(int i = 1; i < new JTableOperator(pso).getRowCount(); i++) {
            px = new Property(pso,i);
            logger.info("Final value of \"" + px.getName() + "\" = " + px.getValue() + " after editing.");
        }

        Thread.sleep(1000);
        pso.close();
        isRemoveServer = false;

    }

    public void TS_COCO_JDBC_05() throws InterruptedException {
        isFail = false;
        tcName = getName();
        isRemoveServer = false;
        logger.info("TS_COCO_JDBC_05: Edit a JDBC resource from ProjectsTab tab successfully.");

        ProjectsTabOperator pto = new ProjectsTabOperator();
        Thread.sleep(1000);
        Node comp = new Node(pto.tree(),"TestApp" + "|Server Resources|jdbc.sun-ws7-resource");
        new PropertiesAction().perform(comp);
        PropertySheetOperator pso = new PropertySheetOperator("jdbc.sun-ws7-resource");

        PropertyHandler ph = new PropertyHandler(pso);

        Property px = null;
        for(int i = 1; i < new JTableOperator(pso).getRowCount(); i++) {
            px = new Property(pso,i);
            logger.info("Initial value of \"" + px.getName() + "\" = " + px.getValue());
        }



        new ScreenCapturer().capture(getName() + "_initial",pb.getResultDir(),logger);
        Thread.sleep(2000);

        ph.setProperty("Enabled","false");
        ph.setProperty("Maximum Wait Time","120");
        ph.setProperty("Connection Idle Timeout","60");
        ph.setProperty("Minimum Connections","16");
        ph.setProperty("Maximum Connections","64");
        //jndi-name is read-only : Is a Bug
        //ph.setProperty("jndi-name","NewTest");

        pso.close();
        Thread.sleep(2000);

        comp = new Node(pto.tree(),"TestApp" + "|Server Resources|jdbc.sun-ws7-resource");
        new PropertiesAction().perform(comp);
        pso = new PropertySheetOperator("jdbc");
        ph = new PropertyHandler(pso);

        if(!ph.getProperty("Enabled").equals("false")) {
            isFail = true;
            logger.severe("Property \"Enabled\" not set to false. Remains " + ph.getProperty("Enabled") );
        }

        if(!ph.getProperty("Maximum Wait Time").equals("120")) {
            isFail = true;
            logger.severe("Property \"Maximum Wait Time\" not set to 120. Remains " + ph.getProperty("Maximum Wait Time") );
        }

        if(!ph.getProperty("Connection Idle Timeout").equals("60")) {
            isFail = true;
            logger.severe("Property \"Connection Idle Timeout\" not set to 60. Remains " + ph.getProperty("Connection Idle Timeout") );
        }

        if(!ph.getProperty("Minimum Connections").equals("16")) {
            isFail = true;
            logger.severe("Property \"Minimum Connections\" not set to 16. Remains " + ph.getProperty("Minimum Connections") );
        }

        if(!ph.getProperty("Maximum Connections").equals("64")) {
            isFail = true;
            logger.severe("Property \"Maximum Connections\" not set to 64. Remains " + ph.getProperty("Maximum Connections") );
        }

        new ScreenCapturer().capture(getName() + "_edited",pb.getResultDir(),logger);
        Thread.sleep(2000);


        px = null;
        for(int i = 1; i < new JTableOperator(pso).getRowCount(); i++) {
            px = new Property(pso,i);
            logger.info("Final value of \"" + px.getName() + "\" = " + px.getValue() + " after editing.");
        }
        //jndi-name is read-only : Is a Bug
        //if(!ph.getProperty("jndi-name").equals("NewTest")) {
        //    isFail = true;
        //    logger.severe("Property \"jndi-name\" not set to NewTest. Remains " + ph.getProperty("jndi-name") );
        //}
        /*
        connection-creation-property = 0 Extra Properties
        connection-lease-property = 0 Extra Properties
        connection-validation = false
        connection-validation-table-name = TAB_NAME
        datasource-class = org.apache.derby.jdbc.ClientDataSource
        description =
        enabled = false
        fail-all-connections = false
        idle-timeout = 60
        isolation-level = default
        isolation-level-guaranteed = true
        jndi-name = jdbc
        max-connections = 32
        min-connections = 8
        property = 0 Extra Properties
        wait-timeout = 60
         */
        Thread.sleep(1000);
        pso.close();
        isRemoveServer = false;

    }

    public void TS_COCO_JDBC_06() throws InterruptedException {
        isFail = false;
        tcName = getName();
        isRemoveServer = false;
        logger.info("TS_COCO_JDBC_06: Remove a JDBC resource from Runtime tab successfully.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Thread.sleep(1000);

        /*Refresh*/
        Node comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getConfigName() + "|JDBC Resources");
        //Node comp = new Node(rto.tree(), "Servers|Local|cuspy:cuspy|JDBC Resources|TestJDBCJNDI");

        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Refresh");

        Thread.sleep(2000);
        new ScreenCapturer().capture(getName() + "_1",pb.getResultDir(),logger);

        String path = "Servers| " + pb.getRegistrationName() + "| " + pb.getConfigName() + ":" + pb.getConfigName() + "|JDBC Resources|TestJDBCJNDI";
        System.out.println("Path: " + path);
        System.out.println("Cooked: " + "Servers|Local|cuspy:cuspy|JDBC Resources|TestJDBCJNDI");
        System.out.println("Servers|Local|cuspy:cuspy|JDBC Resources|TestJDBCJNDI".equals(path));

        comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getConfigName() + "|JDBC Resources|"+ pb.getJdbcJNDIName());
        //Node comp = new Node(rto.tree(), "Servers|Local|cuspy:cuspy|JDBC Resources|TestJDBCJNDI");

        popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Delete");

        Thread.sleep(2000);
        comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getConfigName() + "|JDBC Resources");
        popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Refresh");
        Thread.sleep(1000);
        System.out.println(">>>> " + comp.getChildren().length);

        new ScreenCapturer().capture(getName() + "_edited",pb.getResultDir(),logger);

        /*Refresh*/
        comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getConfigName() + "|JDBC Resources");
        //Node comp = new Node(rto.tree(), "Servers|Local|cuspy:cuspy|JDBC Resources|TestJDBCJNDI");

        popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Refresh");

        Thread.sleep(1000);
        isRemoveServer = false;

    }

    public void TS_COCO_JDBC_07() throws InterruptedException {
        isFail = false;
        tcName = getName();
        isRemoveServer = false;
        logger.info("TS_COCO_JDBC_07: Remove a JDBC resource from ProjectsTab tab successfully.");

        ProjectsTabOperator pto = new ProjectsTabOperator();
        Thread.sleep(1000);

        Node comp = new Node(pto.tree(),"TestApp" + "|Server Resources|jdbc.sun-ws7-resource");
        Thread.sleep(2000);
        new ScreenCapturer().capture(getName() + "_1",pb.getResultDir(),logger);

        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Delete");

        Thread.sleep(2000);


        new ScreenCapturer().capture(getName() + "_2",pb.getResultDir(),logger);

        NbDialogOperator confirm = new NbDialogOperator("Confirm Object Deletion");
        Thread.sleep(1000);
        logger.info("Message from popup \"" + new JLabelOperator(confirm).getText() + "\"");

        new JButtonOperator(confirm,"Yes").clickMouse();
        //confirm.ok();
        Thread.sleep(1000);
        isRemoveServer = false;

    }

    public void Deploy_WebApp() throws InterruptedException {
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = false;
        isTestCase = false;

        logger.info("This is not a testcase.");

        NewProjectWizardOperator npw = NewProjectWizardOperator.invoke();
        Thread.sleep(1000);
        npw.selectCategory("Web");
        npw.selectProject("Web Application");
        npw.next();
        JTextFieldOperator projName = new JTextFieldOperator(npw,0);
        projName.setText("TestApp");

        JTextFieldOperator projLoc = new JTextFieldOperator(npw,1);
        projLoc.setText(pb.getTestProjLoc());
        System.out.println();

        System.out.println(new JTextFieldOperator(npw,3).getText());

        /*1 is Server combobox index*/
        JComboBoxOperator server = new JComboBoxOperator(npw,1); //"Bundled Tomcat (5.5.17)");
        Thread.sleep(1000);
        server.selectItem(pb.getRegistrationName());

        /*2 is JavaEEVersion combobox index*/
        JComboBoxOperator javaEEVersion = new JComboBoxOperator(npw,2);    //pb.getJavaEEVersion());
        Thread.sleep(1000);
        javaEEVersion.selectItem(pb.getJavaEEVersion());
        //System.out.println(">>" + javaEEVersion.getTextField().getText());

        npw.finish();
        Thread.sleep(3000);

        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        Node app = new Node(pto.tree(),"TestApp");
        System.out.println("::" + app.getText());

        JPopupMenuOperator jpop = app.callPopup();
        jpop.pushMenu("Run");
        Thread.sleep(5000);



        Thread.sleep(3000);

    }


    public void TS_COCO_WD_01() throws InterruptedException{
        String imageName = getName();
        tcName = getName();
        isRemote = false; //localhost
        isRemoveServer = false;

        logger.info("TS_COCO_WD_01: Deploy a web app in the local web server and check for directory based deployment.");

        NewProjectWizardOperator npw = NewProjectWizardOperator.invoke();
        Thread.sleep(1000);
        npw.selectCategory("Web");
        npw.selectProject("Web Application");
        npw.next();
        JTextFieldOperator projName = new JTextFieldOperator(npw,0);
        projName.setText(getName());

        JTextFieldOperator projLoc = new JTextFieldOperator(npw,1);
        projLoc.setText(pb.getTestProjLoc());
        System.out.println();

        System.out.println(new JTextFieldOperator(npw,3).getText());

        JComboBoxOperator server = new JComboBoxOperator(npw,1);//"Bundled Tomcat (5.5.17)");
        server.selectItem(pb.getRegistrationName());
        //Bundled Tomcat (5.5.17)");


        JComboBoxOperator javaEEVersion = new JComboBoxOperator(npw,2);
        javaEEVersion.selectItem(pb.getJavaEEVersion());
        //System.out.println(">>" + javaEEVersion.getTextField().getText());

        npw.finish();
        Thread.sleep(3000);

        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        Node app = new Node(pto.tree(),"TS_COCO_WD_01");
        System.out.println("::" + app.getText());

        JPopupMenuOperator jpop = app.callPopup();
        jpop.pushMenu("Run");
        System.out.println(":>:" + jpop.getLabel());
        Thread.sleep(5000);

        String webApp = getName();

        /*Verification*/
        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getVsName() + "|Web Applications|" + "/" + getName());
        String expectedPath = "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getVsName() + "|Web Applications|" + "/" + getName();
        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Properties");

        logger.info("Path found. " + expectedPath);
        isFail = false;

        Thread.sleep(3000);
        /*********Screen Capture*********/
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot;

        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            ImageIO.write(image, "png", new File(pb.getResultDir() + imageName + ".png"));
            logger.info("Screen captured at " + pb.getResultDir() + imageName + ".png" );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        PropertySheetOperator propSheet = new PropertySheetOperator();
        propSheet.close();

    }

    public void TS_COCO_JNDI_01() throws InterruptedException{
        isFail = false;
        isRemoveServer = false;
        NewFileWizardOperator nfwo = NewFileWizardOperator.invoke();

        nfwo.selectProject("TestApp");
        Thread.sleep(1500);
        nfwo.selectCategory("Sun Web Server 7.0 Resources");
        Thread.sleep(1500);
        nfwo.selectFileType("External JNDI Resource");
        Thread.sleep(1500);
        nfwo.next();
        Thread.sleep(1500);


        JTextFieldOperator jndiName = new JTextFieldOperator(nfwo,0);
        jndiName.requestFocus();
        jndiName.setText("TestJNDI");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator extJndiName = new JTextFieldOperator(nfwo,1);
        extJndiName.requestFocus();
        extJndiName.setText("TestJNDI");
        extJndiName.pressKey(KeyEvent.VK_A);
        extJndiName.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator resType = new JTextFieldOperator(nfwo,2);
        resType.requestFocus();
        resType.setText("org.apache.derby.jdbc.ClientDataSource");
        resType.pressKey(KeyEvent.VK_A);
        resType.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator factoryClass = new JTextFieldOperator(nfwo,3);
        factoryClass.requestFocus();
        factoryClass.setText("org.apache.derby.jdbc.ClientDataSource");
        factoryClass.pressKey(KeyEvent.VK_A);
        factoryClass.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator desc = new JTextFieldOperator(nfwo,4);
        desc.requestFocus();
        desc.setText("Test JNDI resource");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);

        Thread.sleep(2000);
        new JButtonOperator(nfwo,"Finish").clickMouse();


        /*Registration*/
        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        Node jdbcFile = new Node(pto.tree(),"TestApp" + "|Server Resources|external.sun-ws7-resource");
        Thread.sleep(1500);
        JPopupMenuOperator pop = jdbcFile.callPopup();
        pop.pushMenu("Register");
        Thread.sleep(1500);

        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator infoBar = new NbDialogOperator("Information");
        Thread.sleep(1500);
        logger.info("Alert found. " + new JLabelOperator(infoBar, "Resource").getText());
        new JButtonOperator(infoBar,"Ok").clickMouse();

        RuntimeTabOperator rto = RuntimeTabOperator.invoke();
        Node jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        Thread.sleep(1500);
        JPopupMenuOperator refresh = jdbcResource.callPopup();
        Thread.sleep(1500);
        refresh.pushMenu("Refresh");
        Thread.sleep(1500);

        rto = RuntimeTabOperator.invoke();
        jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() +":" +pb.getVsName() + "|External JNDI Resources|TestJNDI");
        String expectedPath = "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getVsName() + "|External JNDI Resources|TestJNDI";
        if(jdbcResource.getPath().equals(expectedPath)) {
            isFail=false;
            logger.info("Path found. " + expectedPath );
        } else {
            logger.severe("FAIL: Path not found. " + expectedPath );
        }
        System.out.println(jdbcResource.getPath());
        Thread.sleep(1500);
        JPopupMenuOperator prop = jdbcResource.callPopup();
        Thread.sleep(1500);
        prop.pushMenu("Properties");
        Thread.sleep(1500);


        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator propSheet = new NbDialogOperator("TestJNDI - Properties");
        propSheet.close();
    }

    public void TS_COCO_JM_01() throws InterruptedException{
        isFail = false;
        isRemoveServer = false;
        NewFileWizardOperator nfwo = NewFileWizardOperator.invoke();

        nfwo.selectProject("TestApp");
        Thread.sleep(1500);
        nfwo.selectCategory("Sun Web Server 7.0 Resources");
        Thread.sleep(1500);
        nfwo.selectFileType("JavaMail Resource");
        Thread.sleep(1500);
        nfwo.next();
        Thread.sleep(1500);


        JTextFieldOperator jndiName = new JTextFieldOperator(nfwo,0);
        jndiName.requestFocus();
        jndiName.setText("TestJMail");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator mailHost = new JTextFieldOperator(nfwo,1);
        mailHost.requestFocus();
        mailHost.setText(pb.getLocalHost());
        mailHost.pressKey(KeyEvent.VK_A);
        mailHost.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator defaultUser = new JTextFieldOperator(nfwo,2);
        defaultUser.requestFocus();
        defaultUser.setText("root");
        defaultUser.pressKey(KeyEvent.VK_A);
        defaultUser.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator defaultReturnAddress = new JTextFieldOperator(nfwo,3);
        defaultReturnAddress.requestFocus();
        defaultReturnAddress.setText("root" + "@" + pb.getRemoteHost());
        defaultReturnAddress.pressKey(KeyEvent.VK_A);
        defaultReturnAddress.releaseKey(KeyEvent.VK_A);
        Thread.sleep(1500);

        JTextFieldOperator desc = new JTextFieldOperator(nfwo,4);
        desc.requestFocus();
        desc.setText("Test JavaMail resource");
        jndiName.pressKey(KeyEvent.VK_A);
        jndiName.releaseKey(KeyEvent.VK_A);

        Thread.sleep(2000);
        new JButtonOperator(nfwo,"Finish").clickMouse();


        /*Registration*/
        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        Node jdbcFile = new Node(pto.tree(),"TestApp" + "|Server Resources|mail.sun-ws7-resource");
        Thread.sleep(1500);
        JPopupMenuOperator pop = jdbcFile.callPopup();
        pop.pushMenu("Register");
        Thread.sleep(1500);

        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator infoBar = new NbDialogOperator("Information");
        Thread.sleep(1500);
        logger.info("Alert found. " + new JLabelOperator(infoBar, "Resource").getText());
        new JButtonOperator(infoBar,"Ok").clickMouse();

        RuntimeTabOperator rto = RuntimeTabOperator.invoke();
        Node jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName());
        Thread.sleep(1500);
        JPopupMenuOperator refresh = jdbcResource.callPopup();
        Thread.sleep(1500);
        refresh.pushMenu("Refresh");
        Thread.sleep(1500);

        rto = RuntimeTabOperator.invoke();
        jdbcResource = new Node(rto.tree(),"Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() +":" +pb.getVsName() + "|JavaMail Resources|TestJMail");
        String expectedPath = "Servers|" + pb.getRegistrationName() + "|" + pb.getConfigName() + ":" + pb.getVsName() + "|JavaMail Resources|TestJMail";
        if(jdbcResource.getPath().equals(expectedPath)) {
            isFail=false;
            logger.info("Path found. " + expectedPath );
        } else {
            logger.severe("FAIL: Path not found. " + expectedPath );
        }
        System.out.println(jdbcResource.getPath());
        Thread.sleep(1500);
        JPopupMenuOperator prop = jdbcResource.callPopup();
        Thread.sleep(1500);
        prop.pushMenu("Properties");
        Thread.sleep(1500);


        new ScreenCapturer().capture(getName(), pb.getResultDir(),logger);

        NbDialogOperator propSheet = new NbDialogOperator("TestJMail - Properties");
        propSheet.close();
    }

    public void Register_Server() throws InterruptedException {
        String imageName = getName();
        tcName = getName();
        isRemoveServer = false;
        isTestCase = false;

        logger.info("Not a test case.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers");

        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Add Server...");

        //Thread.sleep(sleepTimeMedium);

        JDialogOperator jdo = new JDialogOperator("Add Server Instance");
        JListOperator jco = new JListOperator(jdo, 1);
        jco.clickMouse();
        jco.releaseMouse();
        jco.selectItem(pb.getSjsString());
        jco.requestFocus();
        jco.transferFocus();
        logger.info("Choose " + pb.getSjsString());
        JTextFieldOperator jto = new JTextFieldOperator(jdo,pb.getSjsString());

        /*REGISTRATION NAME*/
        jto.clearText();
        logger.info("Registered server instance is " + pb.getRegistrationName());
        jto.pressKey(KeyEvent.VK_A);
        jto.releaseKey(KeyEvent.VK_A);

        jto.setText(pb.getRegistrationName());
        jto.requestFocus();
        jto.clickMouse();
        jto.releaseMouse();
        jto.pressKey(KeyEvent.VK_A);
        jto.releaseKey(KeyEvent.VK_A);
        jto.transferFocus();
        JButtonOperator next = new JButtonOperator(jdo, "Next");
        next.clickMouse();

        Thread.sleep(sleepTimeMedium);

        /*INSTALLATION DIR*/
        JTextFieldOperator installationDirField = new JTextFieldOperator(jdo,4);

        installationDirField.pressKey(KeyEvent.VK_A);
        installationDirField.releaseKey(KeyEvent.VK_A);
        installationDirField.setText(pb.getWsInstallDir());
        installationDirField.clickMouse();
        installationDirField.releaseMouse();
        installationDirField.pressKey(KeyEvent.VK_A);
        installationDirField.releaseKey(KeyEvent.VK_A);
        installationDirField.requestFocus();
        installationDirField.transferFocus();



        System.out.println("IsRemote: " + pb.isRemote());
        if(pb.isRemote()) {
            JCheckBoxOperator isRemotecbo = new JCheckBoxOperator(jdo,"Remote Server");
            isRemotecbo.changeSelection(true);

            /*ADMIN HOST*/
            JTextFieldOperator adminHostField = new JTextFieldOperator(jdo,"localhost");
            adminHostField.pressKey(KeyEvent.VK_T);
            adminHostField.pressKey(KeyEvent.VK_A);
            adminHostField.releaseKey(KeyEvent.VK_A);
            adminHostField.setText(pb.getRemoteHost());
            adminHostField.clickMouse();
            adminHostField.releaseMouse();
            adminHostField.pressKey(KeyEvent.VK_A);
            adminHostField.releaseKey(KeyEvent.VK_A);
            adminHostField.requestFocus();
            adminHostField.transferFocus();
            Thread.sleep(1000);
        }

        /*PORT*/
        JTextFieldOperator adminPortField = new JTextFieldOperator(jdo,3);
        adminPortField.pressKey(KeyEvent.VK_A);
        adminPortField.releaseKey(KeyEvent.VK_A);
        logger.info("Admin SSL port set to " + pb.getAdminSSLPort());
        adminPortField.setText(pb.getAdminSSLPort());
        adminPortField.clickMouse();
        adminPortField.releaseMouse();
        adminPortField.pressKey(KeyEvent.VK_A);
        adminPortField.releaseKey(KeyEvent.VK_A);
        adminPortField.requestFocus();
        adminPortField.transferFocus();

        /*ADMIN USER*/
        JTextFieldOperator adminUserField = new JTextFieldOperator(jdo,1);

        adminUserField.pressKey(KeyEvent.VK_T);
        adminUserField.pressKey(KeyEvent.VK_A);
        adminUserField.releaseKey(KeyEvent.VK_A);
        logger.info("Admin user is " + pb.getAdminUser());
        adminUserField.setText(pb.getAdminUser());
        adminUserField.clickMouse();
        adminUserField.releaseMouse();
        adminUserField.pressKey(KeyEvent.VK_A);
        adminUserField.releaseKey(KeyEvent.VK_A);
        adminUserField.requestFocus();
        adminUserField.transferFocus();


        /*ADMIN PORT*/
        JTextFieldOperator adminPassword = new JTextFieldOperator(jdo,0);
        adminPassword.pressKey(KeyEvent.VK_A);
        adminPassword.releaseKey(KeyEvent.VK_A);
        adminPassword.pressKey(KeyEvent.VK_T);
        Thread.sleep(1000);
        adminPassword.setText(pb.getAdminPwd());
        adminPassword.clickMouse();
        adminPassword.releaseMouse();
        adminPassword.pressKey(KeyEvent.VK_A);
        adminPassword.releaseKey(KeyEvent.VK_A);
        adminPassword.requestFocus();
        adminPassword.transferFocus();

        Thread.sleep(sleepTimeMedium);
        Thread.sleep(sleepTimeMedium);

        JButtonOperator finish = new JButtonOperator(jdo, "Finish");
        finish.requestFocus();
        finish.clickMouse();

        /*IMPORTANT*/
        System.out.println("Sleeping for 5sec");
        Thread.sleep(5000);

        rto = RuntimeTabOperator.invoke();
        comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName());
        popupMenu = comp.callPopup();
        System.out.println("Sleeping for 2 sec");
        Thread.sleep(2000);
        popupMenu.pushMenuNoBlock("Refresh");
        /*IMPORTANT*/
        System.out.println("Sleeping for 5 sec");
        Thread.sleep(5000);

        popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Start");
        /*IMPORTANT*/
        System.out.println("Sleeping for 20 sec");
        Thread.sleep(20000);

    }

    public void Remove_Server() throws InterruptedException {
        String imageName = getName();
        tcName = getName();
        isRemoveServer = false;
        isTestCase = false;

        logger.info("Not a test case.");

        RuntimeTabOperator rto = new RuntimeTabOperator();
        Node comp = new Node(rto.tree(), "Servers|" + pb.getRegistrationName());

        JPopupMenuOperator popupMenu = comp.callPopup();
        popupMenu.pushMenuNoBlock("Remove");

        NbDialogOperator infoBar = new NbDialogOperator("Remove Server Instance");
        Thread.sleep(1500);
        logger.info("Alert found. " + new JLabelOperator(infoBar, "Do you really want to remove").getText());
        new JButtonOperator(infoBar,"Yes").clickMouse();

        /*IMPORTANT*/
        System.out.println("Sleeping for 15 sec");
        Thread.sleep(15000);

    }



}
