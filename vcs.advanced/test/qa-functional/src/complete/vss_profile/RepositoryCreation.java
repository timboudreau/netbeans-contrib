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
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;


/** XTest / JUnit test class performing repository creation of VSS filesystem.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class RepositoryCreation extends NbTestCase {
    
    public static String VERSIONING_MENU = "Versioning";
    public static String MOUNT_MENU = VERSIONING_MENU + "|Mount Version Control|Generic VCS";
    public static String UNMOUNT_MENU = "File|Unmount Filesystem";
    public static String CREATE_PROJECT = "VSS|Create Project";
    public static String REFRESH = "VSS|Refresh";
    public static String REFRESH_RECURSIVELY = "VSS|Refresh Recursively";
    public static String ADD = "VSS|Add";
    public static String workingDirectory;
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public RepositoryCreation(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return RepositoryCreation test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String zipFile = "C:\\Program Files\\Microsoft Visual Studio\\vss.zip";
        if (!new File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTest(new RepositoryCreation("testCreateProjects"));
        suite.addTest(new RepositoryCreation("testAddSingleFile"));
        suite.addTest(new RepositoryCreation("testAddMultipleFiles"));
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new File(workingDir).mkdirs();
        File outputFile = new File(workingDir + "/output.txt");
        outputFile.createNewFile();
        File errorFile = new File(workingDir + "/error.txt");
        errorFile.createNewFile();
        PrintWriter outputWriter = new PrintWriter(new FileWriter(outputFile));
        PrintWriter errorWriter = new PrintWriter(new FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }

    /** Method responsible for VSS repository creation physically on disc.
     */
    private void createVSSRepository() throws Exception {
        String zipFile = "\"C:\\Program Files\\Microsoft Visual Studio\\vss.zip\"";
        String destinationDirectory = workingDirectory + File.separator + "Repo";
        String command = "unzip " + zipFile + " -d " + destinationDirectory;
        if (new ExternalCommand(command).exec() != 0) captureScreen("Error: Can't unzip VSS repository.");
    }

    /** Creates new file or directory at given place.
     * @param name Name of directory or file to create.
     * @throws Any unexpected exception thrown during test.
     */
    public void createFile(String name) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name));
        writer.write("/** This is testing file.\n */\n\n public class Testing_File {\n }\n");
        writer.flush();
        writer.close();
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
    
    /** Checks that it is possible to create VSS projects.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testCreateProjects() throws Exception {
        System.out.print(".. Testing projects creation ..");
        new ActionNoBlock(MOUNT_MENU, null).perform();
        VCSWizardProfile wizard = new VCSWizardProfile();
        String profile = VCSWizardProfile.VSS_WIN_NT;
        int os = Utilities.getOperatingSystem();
        if ((os == Utilities.OS_WIN95) | (os == Utilities.OS_WIN98))
            profile = VCSWizardProfile.VSS_WIN_95;
        wizard.setProfile(profile);
        workingDirectory = getWorkDir().getAbsolutePath();
        createVSSRepository();
        new File(workingDirectory + File.separator + "Work" + File.separator + "test" + File.separator + "another").mkdirs();
        wizard.setWorkingDirectory(workingDirectory + File.separator + "Work");
        wizard.setVSSCommand(workingDirectory + File.separator + "Repo" + File.separator + "VSS" + File.separator + "win32" + File.separator + "SS.EXE");
        wizard.setVSSSSDIR(workingDirectory + File.separator + "Repo" + File.separator + "VSS");
        wizard.finish();
        Thread.currentThread().sleep(3000);
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node( filesystemNode, "test [Local]");
        new Action(VERSIONING_MENU + "|" + CREATE_PROJECT, CREATE_PROJECT).perform(testNode);
        try { new JButtonOperator(new NbDialogOperator("Password:"), "OK").push(); }
        catch(TimeoutExpiredException e) {}
        CreateProjectCommandOperator createProject = new CreateProjectCommandOperator("test");
        createProject.setComment("For testing purpose.");
        createProject.ok();
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The project \"test\" was created successfully.");
        information.ok();
        testNode = new Node( filesystemNode, "test [Current]");
        Node anotherNode = new Node( testNode, "another [Local]");
        new Action(VERSIONING_MENU + "|" + CREATE_PROJECT, CREATE_PROJECT).perform(anotherNode);
        createProject = new CreateProjectCommandOperator("another");
        createProject.setComment("For testing purpose.");
        createProject.ok();
        information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The project \"another\" was created successfully.");
        information.ok();
        anotherNode = new Node( testNode, "another [Current]");
        System.out.println(". done !");
    }

    /** Tries to add single file to VSS repository.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddSingleFile() throws Exception {
        System.out.print(".. Testing single file addition ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        createFile( workingDirectory + File.separator + "Work" + File.separator + "A_File.java" );
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(filesystemNode);
        MainWindowOperator.getDefault().waitStatusText("Command Refresh finished.");
        Node fileNode = new Node( filesystemNode, "A_File [Local]");
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD, ADD).perform(fileNode);
        AddCommandOperator addCommand = new AddCommandOperator("A_File.java");
        addCommand.setComment("Generated A_File class.");
        addCommand.ok();
        NbDialogOperator information = new NbDialogOperator("Information");
        new JLabelOperator(information, "The file \"A_File.java\" was successfully added.");
        information.ok();
        fileNode = new Node( filesystemNode, "A_File [Current]");
        System.out.println(". done !");
    }

    /** Tries to add more files to VSS repository at once.
     * @throws Exception any unexpected exception thrown during test.
     */
    public void testAddMultipleFiles() throws Exception {
        System.out.print(".. Testing multiple files addition ..");
        String filesystem = "VSS " + workingDirectory + File.separator + "Work";
        Node filesystemNode = new Node(new ExplorerOperator().repositoryTab().getRootNode(), filesystem);
        Node testNode = new Node(filesystemNode, "test [Current]");
        Node anotherNode = new Node(testNode, "another [Current]");
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "B_File.java" );
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "C_File.java" );
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "C_File.form" );
        createFile( workingDirectory + File.separator + "Work" + File.separator + "test"  + File.separator + "another"  + File.separator + "D_File.java" );
        new Action(VERSIONING_MENU + "|" + REFRESH, REFRESH).perform(new Node[] {testNode, anotherNode});
        anotherNode.expand();
        Node B_FileNode = new Node( testNode, "B_File [Local]");
        Node C_FileNode = new Node( testNode, "C_File [Local]");
        Node D_FileNode = new Node( anotherNode, "D_File [Local]");
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD, ADD).perform(new Node[] {B_FileNode, C_FileNode});
        AddCommandOperator addCommand = new AddCommandOperator("B_File.java ...");
        addCommand.setComment("Auto-generated class file.");
        addCommand.ok();
        new NbDialogOperator("Information").ok();
        new NbDialogOperator("Information").ok();
        new NbDialogOperator("Information").ok();
        new ActionNoBlock(VERSIONING_MENU + "|" + ADD, ADD).perform(new Node[] {D_FileNode});
        addCommand = new AddCommandOperator("D_File.java");
        addCommand.setComment("Auto-generated class file.");
        addCommand.ok();
        new NbDialogOperator("Information").ok();
        new Action(VERSIONING_MENU + "|" + REFRESH_RECURSIVELY, REFRESH_RECURSIVELY).perform(filesystemNode);
        new NbDialogOperator("Retrieving...").closeByButton();
        B_FileNode = new Node( testNode, "B_File [Current]");
        C_FileNode = new Node( testNode, "C_File [Current]");
        D_FileNode = new Node( anotherNode, "D_File [Current]");
        System.out.println(". done !");
    }
}