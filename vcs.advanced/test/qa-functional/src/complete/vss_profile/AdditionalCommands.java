/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package complete.vss_profile;

import java.io.*;
import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.util.PNGEncoder;
import org.netbeans.jemmy.operators.*;
import org.openide.util.Utilities;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.actions.*;
import org.netbeans.jellytools.modules.vcsgeneric.wizard.*;
import org.netbeans.jellytools.modules.vcsgeneric.vss.*;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;

/** XTest / JUnit test class performing additional commands and options testing on VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AdditionalCommands extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String REFRESH = "VSS|Refresh";
    public static String REFRESH_RECURSIVELY = "VSS|Refresh Recursively";
    public static String REMOVE = "VSS|Remove";
    public static String RECOVER = "VSS|Recover";
    public static String CHECK_OUT = "VSS|Check Out";
    public static String CHECK_IN = "VSS|Check In";
    public static String HISTORY = "VSS|History";
    public static String PROPERTIES = "VSS|Properties";
    public static String GET_LATEST_VERSION = "VSS|Get Latest Version";
    public static String UNDO_CHECK_OUT = "VSS|Undo Check Out";
    public static String workingDirectory;
    public static String userName;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AdditionalCommands(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return AdditionalCommands test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        try { workingDirectory = new AdditionalCommands("testRemoveFile").getWorkDir().getAbsolutePath(); }
        catch (IOException e) {}
        String zipFile = workingDirectory.substring(0, workingDirectory.indexOf("complete")) + "vss.zip";
        if (!new File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTest(new AdditionalCommands("testRemoveFile"));
        suite.addTest(new AdditionalCommands("testRecoverFile"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Redirects system output.
     */
    protected void setUp() {
        JemmyProperties.setCurrentOutput(TestOut.getNullOutput());
    }

    /** Method will create a file and capture the screen.
     */
    private void captureScreen(String reason) throws Exception {
        File file;
        try {
            file = new File(getWorkDirPath() + "/dump.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch(IOException e) { throw new Exception("Error: Can't create dump file."); }
        PNGEncoder.captureScreen(file.getAbsolutePath());
        throw new Exception(reason);
    }
    
    /** Tries to remove a file from VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRemoveFile() throws Exception {
        System.out.print(".. Testing removal of a file ..");
        String workingPath = getWorkDirPath();
        workingDirectory = workingPath.substring(0, workingPath.indexOf("AdditionalCommands")) + "RepositoryCreation" + File.separator + "testCreateProjects";
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        new Action(VERSIONING_MENU + "|" + REMOVE, REMOVE).perform(D_FileNode);
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The file \"D_File.java\" was removed successfully.");
        information.ok();
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Local]");
        System.out.println(". done !");
    }

    /** Tries to recover a file back to VSS project repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testRecoverFile() throws Exception {
        System.out.print(".. Testing file recovery ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Local]");
        new Action(VERSIONING_MENU + "|" + RECOVER, RECOVER).perform(D_FileNode);
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The file \"D_File.java\" was recovered successfully.");
        information.ok();
        D_FileNode = new Node( filesystemNode, "test [Current]|another [Current]|D_File [Current]");
        System.out.println(". done !");
    }
}