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

package complete;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.StringTokenizer;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyleConstants.ColorConstants;
import junit.framework.AssertionFailedError;
import org.netbeans.jellytools.*;
import org.netbeans.jellytools.modules.javacvs.nodes.JCVSFileNode;
import org.netbeans.jellytools.modules.vcscore.VCSGroupsFrameOperator;
import org.netbeans.jellytools.modules.vcscore.VersioningFrameOperator;
import org.netbeans.jellytools.modules.vcsgeneric.actions.VCSGroupsAction;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.CVSFileNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.FilesystemHistoryNode;
import org.netbeans.jellytools.modules.vcsgeneric.nodes.PVCSFileNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.properties.PropertySheetOperator;
import org.netbeans.jellytools.properties.PropertySheetTabOperator;
import org.netbeans.jellytools.properties.StringProperty;
import org.netbeans.jellytools.util.StringFilter;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.Dumper;
import org.netbeans.junit.AssertionFailedErrorException;
import org.netbeans.modules.vcscore.runtime.RuntimeCommand;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import util.History;

/**
 * GenericStub is abstract class providing generic vcs functionality
 *
 * For extending GenericStub for some specific vcs profile:
 * 1) Implement all abstract methods
 * 2) Set nRootPrefix field value
 * 3) Create own holder for FileSystem object
 * 4) add GenericNode.<vcs_profile>Node () for easier use parent nodes
 */
public abstract class GenericStub extends JellyTestCase {
    
    public static boolean DEBUG = false;
    
    public GenericStub(String testName) {
        super(testName);
    }
    
    public static String DEFAULT_GROUP = "<Default Group>";

    protected ExplorerOperator exp;
    protected VCSGroupsFrameOperator vgf;
    protected VersioningFrameOperator vfo;
    protected History history;
    protected PrintStream out;
    protected PrintStream info;
    
    protected String nRootPrefix = "Generic ";
    protected static String hRoot = ".";

    protected String serverDirectory;
    protected String clientDirectory;
    protected GenericNode root;
    
    public class GenericNode {
        GenericNode parent;
        String name;
        String[] filenames;
        String historyshort;
        
        public GenericNode (GenericNode parent, String name) {
            this (parent, name, (String[]) null);
        }
        
        public GenericNode (GenericNode parent, String name, String filename) {
            this (parent, name, (filename != null) ? new String[] { filename } : null);
        }
        
        public GenericNode (GenericNode parent, String name, String[] filenames) {
            this.parent = parent;
            this.name = name;
            if (filenames != null) {
                this.filenames = filenames;
                for (int a = 0; a < this.filenames.length; a ++)
                    this.filenames[a] = name + this.filenames[a];
            } else {
                this.filenames = new String[] { name };
            }
        }
        
        public String name () {
            return name;
        }
        
        public GenericNode parent () {
            return parent;
        }
        
        public int count () {
            return filenames.length;
        }
        
        public String file () {
            if (parent != null)
                return parent.file () + '/' + filenames[0];
            else
                return name;
        }
        
        public String filename (int index) {
            return filenames[index];
        }
        
        public void mkdirs () {
            new File (file ()).mkdirs ();
        }
        
        public void save (String content) {
            saveToFile (file (), content);
        }
        
        public void save (int index, String content) {
            saveToFile (filename (index), content);
        }
        
        public void delete () {
            for (int a = 0; a < count(); a ++)
                info.println ("File deletion: File: " + filenames[a] + " Return: " + new File (parent ().file (), filenames[a]).delete());
        }
        
        public boolean isWriteable () {
            for (int a = 0; a < count (); a ++)
                if (!new File (filename (0)).canWrite ())
                    return false;
            return true;
        }
        
        public boolean isNotWriteable () {
            for (int a = 0; a < count (); a ++)
                if (new File (filename (0)).canWrite ())
                    return false;
            return true;
        }
        
        private String historyCore () {
            if (parent != null)
                return parent.historyCore () + name + '/';
            else
                return "";
        }
        
        public String history () {
            if (parent != null)
                return parent.historyCore () + name;
            else
                return hRoot;
        }
        
        public String history (int index) {
            if (parent != null)
                return parent.historyCore () + filenames[index];
            else
                return hRoot;
        }
        
        public String node () {
            if (parent != null)
                return parent.node () + '|' + name;
            else
                return nRootPrefix + name;
        }
        
        public Node genericNode() {
            return new Node (exp.repositoryTab ().tree (), node ());
        }

        public CVSFileNode cvsNode () {
            return new CVSFileNode (exp.repositoryTab ().tree (), node ());
        }

        public PVCSFileNode pvcsNode () {
            return new PVCSFileNode (exp.repositoryTab ().tree (), node ());
        }
        
        public JCVSFileNode jcvsNode () {
            return new JCVSFileNode (exp.repositoryTab ().tree (), node ());
        }

        public CVSFileNode cvsVersioningNode () {
            return cvsVersioningNode ("");
        }

        public CVSFileNode cvsVersioningNode (String suffix) {
            return new CVSFileNode (vfo.treeVersioningTreeView(), node () + suffix);
        }

        public CVSFileNode cvsGroupNode (String group) {
            return cvsGroupNode (group, "");
        }

        public CVSFileNode cvsGroupNode (String group, String suffix) {
            return new CVSFileNode (vgf.treeVCSGroupsTreeView (), group + "|" + name () + suffix);
        }

        public PVCSFileNode pvcsVersioningNode () {
            return pvcsVersioningNode ("");
        }

        public PVCSFileNode pvcsVersioningNode (String suffix) {
            return new PVCSFileNode (vfo.treeVersioningTreeView(), node () + suffix);
        }

        public PVCSFileNode pvcsGroupNode (String group) {
            return pvcsGroupNode (group, "");
        }

        public PVCSFileNode pvcsGroupNode (String group, String suffix) {
            return new PVCSFileNode (vgf.treeVCSGroupsTreeView (), group + "|" + name () + suffix);
        }

        public void waitHistory (String command) {
            for (int a = 0; a < count (); a ++)
                waitCommand(command, history (a));
        }
        
        public void setHistoryShort (String historyshort) {
            this.historyshort = historyshort;
        }
        
        public void waitHistoryShort (String command) {
            if (historyshort != null)
                waitCommand (command, historyshort);
            else
                for (int a = 0; a < count (); a ++)
                    waitCommand(command, filename (a)); // workaround - unreported bug
        }
        
        public void waitHistoryFailed (String command) {
            for (int a = 0; a < count (); a ++)
                waitCommandFailed(command, history (a));
        }
        
        public void waitStatusLock (String status) {
            waitStatus (status, true);
            waitLock (status.indexOf (';') >= 0);
        }

        public void waitStatus(String status) {
            waitStatus (status, true);
        }
        
        public void waitStatus(String status, boolean exact) {
            waitNodeStatus (exp.repositoryTab().tree (), node (), status, exact);
        }

        public void waitLock(boolean lock) {
            String ano = null;
            for (int a = 0; a < 30; a ++) {
                sleep (1000);
                Node n = new Node (exp.repositoryTab ().tree (), node());
                ano = n.getText();
                int i = ano.lastIndexOf('('), j = ano.lastIndexOf(')');
                if (lock  &&  i >= 0  &&  j >= 0)
                    return;
                if (!lock  &&  i < 0  &&  j < 0)
                    return;
            }
            assertTrue("File Lock is not reached: Expected: " + lock + " Got: " + ano, false);
        }

        public void waitVersion(String version) {
            String ano = null;
            for (int a = 0; a < 30; a ++) {
                sleep (1000);
                Node n = new Node (exp.repositoryTab ().tree (), node());
                ano = n.getText();
                int i = ano.lastIndexOf('('), j = ano.lastIndexOf(')');
                if (version == null  &&  i < 0  &&  j < 0)
                    return;
                ano = ano.substring(i + 1, j);
                if (ano.equals (version))
                    return;
            }
            assertTrue("File Lock is not reached: Expected: " + version + " Got: " + ano, false);
        }

    }
    
    public void waitIsShowing (Component comp) {
        for (int a = 0; a < 60; a ++) {
            if (!comp.isShowing())
                return;
            sleep (1000);
        }
        throw new AssertionFailedError ("Timeout: waitIsShowing: Component: " + comp);
    }
    
    public void closeAllWindows (String titlepart) {
        ArrayList al = new ArrayList ();
        for (int a = 0;; a ++) {
            NbFrameOperator fr = NbFrameOperator.find(titlepart, a);
            if (fr == null)
                break;
            getLog ().println ("Found frame: " + fr.getTitle ());
            al.add (fr);
        }
        for (int a = 0; a < al.size (); a ++) {
            NbFrameOperator fr = (NbFrameOperator) al.get (a);
            getLog ().println ("Closing frame: " + fr.getTitle ());
            if (fr.isShowing ()) try {
                fr.close();
            } catch (JemmyException e) {
                getLog ().println ("Exception while closing frame: " + fr.getTitle ());
                e.printStackTrace(getLog ());
            }
            int b = 60;
            while (b > 0  &&  fr.isShowing()) {
                sleep (1000);
                b --;
            }
            if (b <= 0)
                getLog ().println ("Timeout while closing frame: " + fr.getTitle ());
        }
    }
    
    public void closeAllProperties() {
        closeAllWindows ("Propert");
    }
    
    public void closeAllVCSOutputs() {
        closeAllWindows ("Output of VCS Commands [");
    }
    
    public void closeAllCVSOutputs() {
        closeAllWindows ("CVS Output [");
    }
    
    public void closeAllVCSGroups() {
        closeAllWindows ("VCS Groups");
    }
    
    public void closeAllVersionings() {
        closeAllWindows ("Versioning");
    }
    
    public void closeAllVCSWindows () {
        closeAllVCSOutputs ();
        closeAllCVSOutputs ();
        closeAllVCSGroups ();
        closeAllVersionings ();
    }
    
    public void saveToFile (String filename, String text) {
        try {
            FileWriter fr = new FileWriter (filename);
            fr.write(text);
            fr.close ();
        } catch (IOException e) {
            throw new AssertionFailedErrorException ("IOException while saving file: " + filename, e);
        }
    }
    
    public static void waitNoEmpty (JTextAreaOperator text) {
        for (int a = 0; a < 60; a ++) {
            String str = text.getText ();
            if (!"".equals (str))
                return;
            sleep (1000);
        }
        assertTrue ("WaitNoEmpty TextArea: Timeout", false);
    }
    
    public static void waitNoEmpty (JTableOperator table) {
        for (int a = 0; a < 60; a ++) {
            if (table.getRowCount () > 0  &&  table.getColumnCount() > 0)
                return;
            sleep (1000);
        }
        assertTrue ("WaitNoEmpty Table: Timeout", false);
    }
    
    boolean equalPaths (String p1, String p2) {
        p1 = p1.replace ('\\', '/');
        p2 = p2.replace ('\\', '/');
        return p1.equalsIgnoreCase(p2);
    }
    
    protected abstract FileSystem getFileSystem ();
    
    protected abstract void setFileSystem (FileSystem fs);
    
    protected abstract void prepareServer (String dir);
    
    protected abstract void mountVCSFileSystem ();
    
    protected abstract void prepareClient ();
    
    protected abstract void createStructure ();
    
    protected void setUp() throws Exception {
        exp = new ExplorerOperator();
        out = getRef();
        info = getLog();
        if (!"configure".equals (getName ())) {
            history = new History (getFileSystem (), info);
            root = new GenericNode (null, getFileSystem ().getDisplayName().substring (nRootPrefix.length()));
            createStructure();
        }
    }
    
    public void failNotify(Throwable th) {
        info.println ("==== Fail Notify ====");
        if (history != null)
            history.print ();
        else
            info.println ("No History");
        if ("configure".equals (getName ()))
            Dumper.dumpComponent(exp.repositoryTab().tree ().getSource(), getLog ("repository"));
    }    
    protected void findFS () {
        String nRoot = nRootPrefix + clientDirectory;
        boolean found = false;
        info.println("Searching for " + nRootPrefix + "filesystem: " + nRoot);
        for (int a = 0; a < 10; a ++) {
            Enumeration e = Repository.getDefault().getFileSystems();
            while (e.hasMoreElements()) {
                FileSystem f = (FileSystem) e.nextElement();
                info.println("Is it?: " + f.getDisplayName());
                if (equalPaths (f.getDisplayName(), nRoot)) {
                    info.println("Yes");
                    setFileSystem(f);
                    found = true;
                    break;
                }
            }
            if (found == true)
                break;
            sleep (1000);
        }
        assertTrue("Filesystem not found: Filesystem: " + nRoot, found);
    }
    
    protected void configure() {
        String workroot;
        try {
            workroot = getWorkDirPath();
        } catch (IOException e) {
            throw new AssertionFailedErrorException("IOException while getWorkDirPath()", e);
        }
        serverDirectory = workroot + "/server";
        clientDirectory = workroot + "/client";
        if (Utilities.isUnix ()) {
            serverDirectory = serverDirectory.replace ('\\', '/');
            clientDirectory = clientDirectory.replace ('\\', '/');
        } else {
            serverDirectory = serverDirectory.replace ('/', '\\');
            clientDirectory = clientDirectory.replace ('/', '\\');
        }
        
        if (!DEBUG) {
            new File(serverDirectory).mkdirs();
            new File(clientDirectory).mkdirs();
            info.println("Server: " + serverDirectory);
            info.println("Client: " + clientDirectory);

            prepareServer(serverDirectory);
            mountVCSFileSystem ();
            sleep (5000);
        }
        
        findFS ();
        root = new GenericNode (null, getFileSystem ().getDisplayName().substring (nRootPrefix.length()));
        info.println("Working Directory nRoot: " + root.node());
        if (DEBUG)
            history = new History(getFileSystem(), info);
        root.genericNode ();
        
        createStructure();
        if (!DEBUG) {
            closeAllProperties();
            FilesystemHistoryNode cvshistorynode = new FilesystemHistoryNode(exp.runtimeTab().tree(), root.node ());
            cvshistorynode.properties();
            PropertySheetOperator pso = new PropertySheetOperator(PropertySheetOperator.MODE_PROPERTIES_OF_ONE_OBJECT, root.node ());
            PropertySheetTabOperator pst = pso.getPropertySheetTabOperator("Properties");
            new StringProperty(pst, "Number of Finished Commands To Keep").setValue("200");
            pso.close();

            prepareClient ();
        }
    }
    
    protected void waitCommand (String command, GenericNode[] nodes) {
        if (nodes == null)
            return;
        StringBuffer sb = new StringBuffer ();
        for (int a = 0; a < nodes.length; a ++) {
            if (a > 0)
                sb.append ('\n');
            sb.append(nodes[a].history ());
        }
        waitCommand (command, sb.toString ());
    }

    protected void waitCommand (String command, String node) {
        if (!history.waitCommand(command, node)) {
            history.print ();
            assertTrue("Command failed: Command: " + command + " Node: " + node, false);
        }
    }
    
    protected void waitCommandFailed (String command, String node) {
        if (history.waitCommand(command, node)) {
            history.print ();
            assertTrue("Command does not failed: Command: " + command + " Node: " + node, false);
        }
    }
    
    protected void waitNodeStatus (JTreeOperator tree, String node, String status, boolean exact) {
        String ano = null;
        for (int a = 0; a < 30; a ++) {
            sleep (1000);
            Node n = new Node (tree, node);
            ano = n.getText();
            int i = ano.indexOf('[');
            if (i < 0) {
                if (status == null)
                    return;
                continue;
            }
            ano = ano.substring(i + 1);
            i = ano.lastIndexOf(']');
            if (i < 0)
                continue;
            ano = ano.substring(0, i);
            if ((exact  &&  ano.equals(status))  ||  (!exact  &&  ano.indexOf(status) >= 0))
                return;
        }
        assertTrue("File Status is not reached: Expected: " + status + " Got: " + ano, false);
    }
    
    public void viewOutput (String command, GenericNode node) {
        viewOutput (history.getWaitCommand(command, node.history ()));
    }
    
    public static void viewOutput (RuntimeCommand rc) {
        org.openide.nodes.Node n = rc.getNodeDelegate();
        SystemAction sa = n.getDefaultAction();
        sa.actionPerformed(new ActionEvent (n, 0, ""));
    }
    
    public static Color annoWhite = new Color (254, 254, 254);
    public static Color annoGreen = new Color (180, 255, 180);
    public static Color annoBlue = new Color (160, 200, 255);
    public static Color annoRed = new Color (255, 160, 180);
    
    public void dumpColors (StyledDocument sd) {
        int b = sd.getLength();// * 2;
//        out.println ("Len: " + b);
        for (int a = 0; a < b; a ++) {
            Style st = sd.getLogicalStyle(a);
            if (st == null)
                continue;
            Color col = (Color) st.getAttribute(ColorConstants.Background);
            String str;
            if (annoWhite.equals (col))
                str = "White";
            else if (annoGreen.equals (col))
                str = "Green";
            else if (annoBlue.equals (col))
                str = "Blue";
            else if (annoRed.equals (col))
                str = "Red";
            else
                str = col.toString ();
            out.println ("Pos: " + a + " ---- " + str);
            a ++;
        }
    }
    
    protected void dumpDiffGraphical (TopComponentOperator tco) {
        JEditorPaneOperator p1 = new JEditorPaneOperator (tco, 0);
        JEditorPaneOperator p2 = new JEditorPaneOperator (tco, 1);
        out.println ("==== Text - Panel 1 ====");
        out.println (p1.getText ());
        out.println ("==== Text - Panel 2 ====");
        out.println (p2.getText ());
        StyledDocument sd1 = (StyledDocument) p1.getDocument();
        StyledDocument sd2 = (StyledDocument) p2.getDocument();
        out.println ("==== Colors - Panel 1 ====");
        dumpColors(sd1);
        out.println ("==== Colors - Panel 2 ====");
        dumpColors(sd2);
    }
    
    protected void dumpDiffGraphicalGraphical (TopComponentOperator tco) {
        new JComboBoxOperator (tco).selectItem("Graphical Diff Viewer");
        dumpDiffGraphical (tco);
    }
    
    protected void dumpDiffGraphicalTextual (TopComponentOperator tco) {
        new JComboBoxOperator (tco).selectItem("Textual Diff Viewer");
        JEditorPaneOperator p = new JEditorPaneOperator (tco);
        out.println (p.getText ());
    }
    
    public void dumpVerifyGroupTable (JTableOperator table) {
        int height = table.getRowCount();
        int width = table.getColumnCount();
        out.println("Height: " + height);
        out.println("Width: " + width);
        String[] strs = new String[height];
        for (int a = 0; a < height; a ++) {
            String comp = "";
            for (int b = 0; b < width; b ++) {
                if (b != 0)
                    comp += "    ";
                String str = (table.getValueAt(a, b) != null) ? table.getValueAt(a, b).toString () : "<NULL>";
                int i = str.indexOf (root.node ());
                if (i >= 0)
                    str = str.substring (0, i) + "<FS>" + str.substring (i + root.node ().length());
                comp += str;
            }
            strs[a] = comp;
        }
        Arrays.sort (strs);
        for (int a = 0; a < height; a ++)
            out.println (a + ". - " + strs[a]);
    }
    
    public static String loadFile (String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            if (br == null)
                return null;
            try {
                StringBuffer sb = new StringBuffer();
                for (;;) {
                    String str = br.readLine();
                    if (str == null)
                        break;
                    sb.append(str);
                    sb.append('\n');
                }
                return sb.toString();
            } catch (IOException e) {
                try { br.close(); } catch (IOException ee) {}
                throw new AssertionFailedErrorException ("IOException while reading file: " + file, e);
            }
        } catch (FileNotFoundException e) {
            throw new AssertionFailedErrorException ("FileNotFoundException: File: " + file, e);
        }
    }

    public static String loadBinFile (String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            if (fis == null)
                return null;
            try {
                StringBuffer sb = new StringBuffer();
                for (;;) {
                    int i = fis.read ();
                    if (i < 0)
                        break;
                    else if (i < 32) {
                        sb.append ("\\0");
                        sb.append ((i / 8) & 7);
                        sb.append (i & 7);
                    } else if (i == '\\')
                        sb.append ("\\\\");
                    else
                        sb.append ((char) i);
                }
                return sb.toString();
            } catch (IOException e) {
                try { fis.close(); } catch (IOException ee) {}
                throw new AssertionFailedErrorException ("IOException while reading file: " + file, e);
            }
        } catch (FileNotFoundException e) {
            throw new AssertionFailedErrorException ("FileNotFoundException: File: " + file, e);
        }
    }

    protected String getLockText (String text) {
        int i = text.lastIndexOf ('('), j = text.lastIndexOf (')');
        assertTrue ("User not found in text: Text: " + text, i >= 0  &&  j >= 0);
        text = text.substring (i + 1, j);
        info.println ("User Name: " + text);
        return text;
    }
    
    protected String getWorkFilePath () {
        try {
            return getWorkDirPath();
        } catch (IOException e) {
            throw new AssertionFailedErrorException ("IOException while getWorkDirPath", e);
        }
    }
    
    protected void printFiltered (String output, StringFilter sf) {
        StringTokenizer st = new StringTokenizer (output, "\n");
        while (st.hasMoreTokens()) {
            String ss = st.nextToken();
            out.println (sf.filter(ss));
        }
    }
    
    public void assertQuestionYesDialog (String expected) {
        NbDialogOperator dia = new NbDialogOperator ("Question");
        if (expected != null) {
            String str = new JLabelOperator (dia).getText ();
            getLog ().println ("assertQuestionYesDialog: " + str);
            assertEquals("Invalid question dialog message", expected, str);
        }
        dia.yes ();
        dia.waitClosed();
    }
    
    public void assertQuestionNoDialog (String expected) {
        NbDialogOperator dia = new NbDialogOperator ("Question");
        if (expected != null) {
            String str = new JLabelOperator (dia).getText ();
            getLog ().println ("assertQuestionNoDialog: " + str);
            assertEquals("Invalid question dialog message", expected, str);
        }
        dia.no ();
        dia.waitClosed();
    }
    
    public void assertInformationDialog (String expected) {
        NbDialogOperator dia = new NbDialogOperator ("Information");
        if (expected != null) {
            String str = new JLabelOperator (dia).getText ();
            getLog ().println ("assertInformationDialog: " + str);
            assertEquals("Invalid information dialog message", expected, str);
        }
        dia.ok ();
        dia.waitClosed();
    }
    
    public void assertConfirmObjectDeletionYes (String expected) {
        NbDialogOperator dia = new NbDialogOperator ("Confirm Object Deletion");
        if (expected != null) {
            String str = new JLabelOperator (dia).getText ();
            getLog ().println ("assertConfirmObjectDeletionYes: " + str);
            assertEquals("Invalid confirm dialog message", expected, str);
        }
        dia.yes ();
        dia.waitClosed ();
    }
    
    public void assertConfirmObjectDeletionNo (String expected) {
        NbDialogOperator dia = new NbDialogOperator ("Confirm Object Deletion");
        if (expected != null) {
            String str = new JLabelOperator (dia).getText ();
            getLog ().println ("assertConfirmObjectDeletionNo: " + str);
            assertEquals("Invalid confirm dialog message", expected, str);
        }
        dia.no ();
        dia.waitClosed ();
    }
    
    public void openGroupsFrame () {
        new VCSGroupsAction ().perform ();
        vgf = new VCSGroupsFrameOperator ();
    }
    
    public void newVersioningFrame () {
        vfo = new VersioningFrameOperator ();
    }
    
    public void deleteRecursively (File file) {
        File[] fs = file.listFiles();
        if (fs == null)
            return;
        for (int a = 0; a < fs.length; a ++) {
            File f = fs[a];
            if (!f.exists())
                continue;
            if (!file.isFile())
                deleteRecursively (fs[a]);
            info.println ("Deleting: " + file + " Return: " + file.delete ());
        }
    }
    
    public static void sleep (int delay) {
        try {
            Thread.currentThread().sleep (delay);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public static void txtSetTypeText (JTextFieldOperator txt, String text) {
        if (text == null  ||  text.length() <= 0) {
            txt.setText (txt.getText());
            txt.clearText ();
        } else {
            int pos = text.length() - 1;
            txt.setText (text.substring (0, pos));
            txt.typeText(text.substring (pos), pos);
        }
    }
    
}
