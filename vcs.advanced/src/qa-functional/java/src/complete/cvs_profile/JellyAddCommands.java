/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package complete.cvs_profile;

import complete.GenericStub.GenericNode;
import java.util.StringTokenizer;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsOutputOperator;
import org.netbeans.jellytools.modules.vcscore.VCSCommandsInOutputOperator;
import org.netbeans.jellytools.nodes.FilesystemNode;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.test.oo.gui.jelly.vcsgeneric.cvs_profile.*;
import util.Filter;


public class JellyAddCommands extends CVSStub {
    
    public JellyAddCommands(String testName) {
        super(testName);
    }
    
    public static Test suite() {
//        complete.GenericStub.DEBUG = true;
        TestSuite suite = new NbTestSuite();
        suite.addTest(new JellyAddCommands("configure"));
        suite.addTest(new JellyAddCommands("testEdit"));
        suite.addTest(new JellyAddCommands("testEditors"));
        suite.addTest(new JellyAddCommands("testUndoEdit"));
        suite.addTest(new JellyAddCommands("testNoEdit"));
        suite.addTest(new JellyAddCommands("testSetWatchers"));
//        suite.addTest(new JellyAddCommands("testLock")); // not testable on local cvs
//        suite.addTest(new JellyAddCommands("testUnlock")); // not testable on local cvs
//        suite.addTest(new JellyAddCommands("testViewBranches")); // not testable component
        suite.addTest(new JellyAddCommands("testDiffPrepare"));
        suite.addTest(new JellyAddCommands("testDefaultDiffGraphical")); // not fully covered
        suite.addTest(new JellyAddCommands("testDefaultDiffGraphicalTextual"));
        suite.addTest(new JellyAddCommands("testDefaultDiffTextual"));
        suite.addTest(new JellyAddCommands("testDefaultPatch"));
        suite.addTest(new JellyAddCommands("testUnmount"));
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    static String username;
    GenericNode editdir, editfile;
    GenericNode lockdir, lockfile;
    GenericNode diffdir, difffile;
   
    protected void createStructure() {
        editdir = new GenericNode (root, "editdir");
        editfile = new GenericNode (editdir, "editfile");

        lockdir = new GenericNode (root, "lockdir");
        lockfile = new GenericNode (lockdir, "lockfile");
        
        diffdir = new GenericNode (root, "diffdir");
        difffile = new GenericNode (diffdir, "difffile");
    }
    
    public void configure () {
        super.configure();
    }

    public void testEdit () {
        editdir.mkdirs ();
        editfile.save("Init");
        
        refresh (root);
        addDirectory (editdir);
        addFile (editfile, "InitialState");
        commitFile (editfile, null, "InitialCommit");
        editfile.waitStatus("Up-to-date; 1.1");
        editdir.cvsNode ().cVSEditingEdit();
        CVSEditFolderAdvDialog edit = new CVSEditFolderAdvDialog ();
        edit.checkProcessDirectoriesRecursively(true);
        edit.checkSpecifyActionsForTemporaryWatch(true);
        edit.setTemporaryWatch(CVSEditFolderAdvDialog.ITEM_ALL);
        edit.oK ();
        edit.waitClosed ();
        editdir.waitHistory("Edit");
        assertInformationDialog ("The file \"" + editdir.name () + "\" is prepared to edit.");
    }
    
    public void testEditors () {
        closeAllVCSOutputs();
        editdir.cvsNode ().cVSEditingEditors();
        CVSEditorsFolderAdvDialog ed = new CVSEditorsFolderAdvDialog ();
        ed.checkProcessDirectoriesRecursively(true);
        ed.oK();
        ed.waitClosed ();
        editdir.waitHistory ("Editors");
        VCSCommandsInOutputOperator coo = new VCSCommandsInOutputOperator ("Editors");
        waitNoEmpty (coo.txtStandardOutput ());
        String str = coo.txtStandardOutput().getText ();
        info.println (str);
        assertTrue ("Editor is not set on editfile: Output: " + str, str.startsWith (editfile.history ()));
    }
    
    public void testUndoEdit () {
        editfile.save ("Modification");
        refresh (editdir);
        editfile.waitStatus ("Locally Modified; 1.1");
        editdir.cvsNode().cVSEditingUndoEdit();
        CVSUndoEditFolderAdvDialog undo = new CVSUndoEditFolderAdvDialog ();
        undo.checkProcessDirectoriesRecursively(true);
        undo.oK ();
        undo.waitClosed ();
        assertQuestionYesDialog ("Are you sure you want to revert the changes in these files: \"" + editfile.history () + "\"?");
        editdir.waitHistory ("Undo Edit");
        assertInformationDialog("Changes reverted in the file \"" + editdir.history () + "\".");
        editfile.waitStatus ("Up-to-date; 1.1");
    }
    
    public void testNoEdit () {
        editfile.cvsNode().cVSEditingUndoEdit();
        assertInformationDialog ("No files has been changed or the edit command was not issued before.");
    }
    
    public void testSetWatchers () {
        GenericNode watchdir = new GenericNode (root, "watchdir");
        GenericNode watchfile = new GenericNode (watchdir, "watchfile");
        watchdir.mkdirs ();
        watchfile.save("Init");

        refresh (root);
        addDirectory (watchdir);
        addFile (watchfile, "InitialState");
        commitFile (watchfile, null, "InitialCommit");
        watchfile.waitStatus ("Up-to-date; 1.1");

        watchfile.cvsNode ().cVSWatchesSetWatch();
        CVSWatchSetFileAdvDialog wa = new CVSWatchSetFileAdvDialog ();
        wa.checkCommit(true);
        wa.checkEdit(true);
        wa.checkUnedit(true);
        wa.oK ();
        wa.waitClosed ();
        watchfile.waitHistory ("Set Watch");
        
        closeAllVCSOutputs ();
        watchfile.cvsNode ().cVSWatchesWatchers();
        watchfile.waitHistory ("Watchers");
        VCSCommandsInOutputOperator coo = new VCSCommandsInOutputOperator ("Watchers");
        waitNoEmpty (coo.txtStandardOutput ());
        String str = coo.txtStandardOutput().getText ();
        info.println (str);
        StringTokenizer st = new StringTokenizer (str, "\n");
        int c = st.countTokens();
        assertTrue ("Invalid watchers lines: Count: " + c, c == 1);
        str = st.nextToken();
        info.println (str);
        assertTrue ("Watcher on watchfile not exists", str.startsWith (watchfile.name ()));
        c = str.indexOf ("edit");
        if (c < 1  ||  str.charAt (c - 1) == 'n')
            c = -1;
        assertTrue ("Edit watcher does not exist", c >= 0);
        assertTrue ("Unedit watcher does not exist", str.indexOf ("unedit") >= 0);
        assertTrue ("Commit watcher does not exist", str.indexOf ("commit") >= 0);

        watchdir.cvsNode ().cVSWatchesSetWatch();
        CVSWatchSetFolderAdvDialog war = new CVSWatchSetFolderAdvDialog ();
        war.setWatchRecursively();
        war.checkCommit(false);
        war.checkEdit(false);
        war.checkUnedit(false);
        war.oK ();
        war.waitClosed ();
        watchdir.waitHistory ("Set Watch");
        
        closeAllVCSOutputs ();
        watchdir.cvsNode ().cVSWatchesWatchers();
        CVSWatchersFolderAdvDialog wars = new CVSWatchersFolderAdvDialog ();
        wars.checkProcessDirectoriesRecursively(true);
        wars.oK ();
        wars.waitClosed();
        watchdir.waitHistory ("Watchers");
        coo = new VCSCommandsInOutputOperator ("Watchers");
        try {
            coo.btStandardOutput().waitComponentEnabled();
        } catch (InterruptedException e) {}
    }
/*    cannot create test due to using of local cvs filesystem
    public void testLock () {
        lockdir.mkdirs ();
        lockfile.save("Init");

        refresh (root);
        addDirectory (lockdir);
        addFile (lockfile, "InitialState");
        commitFile (lockfile, null, "InitialCommit");
        lockfile.waitStatus ("Up-to-date; 1.1");

        lockfile.cvsNode ().cVSLockingLock();
        CVSLockFileAdvDialog lock = new CVSLockFileAdvDialog ();
        lock.oK ();
        lock.waitClosed();
        lockfile.waitHistory("Lock");
        assertInformationDialog("The file \"" + lockfile.name () + "\" was locked successfully.");
        lockfile.save ("Mod");
        refresh (lockdir);
        lockfile.waitStatus ("Locally Modified; 1.1");
        commitFile (lockfile, null, "Trying to commit locked file");
        // !!! do it - check for no commit
    }
    
    public void testUnlock () {
        // !!! do it
    }
    
    public void testViewBranches () {
        // !!! do it
    }
*/    
    public void testDiffPrepare () {
        diffdir.mkdirs ();
        difffile.save ("Line 1\nLine 3\nLine 5\n");
        refresh (root);
        addDirectory(diffdir);
        addFile (difffile, "InitialState");
        commitFile (difffile, null, "Commit1");
        difffile.waitStatus ("Up-to-date; 1.1");
        difffile.save ("Line 2\nLine 3\nLine 4\nLine 5\n");
        commitFile (difffile, null, "Commit2");
        difffile.waitStatus ("Up-to-date; 1.2");
        difffile.save ("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\n");
        commitFile (difffile, null, "Commit3");
        difffile.waitStatus ("Up-to-date; 1.3");
    }
    
    public void testDefaultDiffGraphical () {
        difffile.cvsNode ().cVSDiffGraphical();
        CVSGraphicalDiffFileAdvDialog gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.oK();
        gr.waitClosed ();
        assertInformationDialog ("No differences were found in " + difffile.name () + ".");
        difffile.waitHistory("Diff Graphical");

        difffile.cvsNode ().cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.1");
        gr.setRevisionOrTag2("1.2");
        gr.oK ();
        gr.waitClosed ();
        difffile.waitHistory("Diff Graphical");
        
        TopComponentOperator tco = new TopComponentOperator ("Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphicalGraphical (tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }

        difffile.cvsNode ().cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.2");
        gr.setRevisionOrTag2("1.3");
        gr.oK ();
        gr.waitClosed ();
        difffile.waitHistory("Diff Graphical");
        
        tco = new TopComponentOperator ("Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.2 and 1.3 ==== !!!!");
            dumpDiffGraphicalGraphical (tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }

        compareReferenceFiles();
    }
    
    public void testDefaultDiffGraphicalTextual () {
        difffile.cvsNode ().cVSDiffGraphical();
        CVSGraphicalDiffFileAdvDialog gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.1");
        gr.setRevisionOrTag2("1.2");
        gr.oK ();
        gr.waitClosed ();
        difffile.waitHistory("Diff Graphical");
        
        TopComponentOperator tco = new TopComponentOperator ("Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.1 and 1.2 ==== !!!!");
            dumpDiffGraphicalTextual (tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }

        difffile.cvsNode ().cVSDiffGraphical();
        gr = new CVSGraphicalDiffFileAdvDialog ();
        gr.setRevisionOrTag1("1.2");
        gr.setRevisionOrTag2("1.3");
        gr.oK ();
        gr.waitClosed ();
        difffile.waitHistory("Diff Graphical");
        
        tco = new TopComponentOperator ("Diff: " + difffile.name ());
        try {
            out.println ("!!!! ==== Comparing revisions: 1.2 and 1.3 ==== !!!!");
            dumpDiffGraphicalTextual (tco);
        } finally {
            tco.close();
            waitIsShowing(tco.getSource());
        }

        compareReferenceFiles();
    }
    
    public void testDefaultDiffTextual () {
        VCSCommandsInOutputOperator coo;
        String str;
        Filter filt = new Filter ();
        filt.addFilterAfter("RCS file: ");

        difffile.cvsNode ().cVSDiffTextual();
        CVSTextualDiffFileAdvDialog diff = new CVSTextualDiffFileAdvDialog ();
        diff.oK();
        diff.waitClosed ();
        difffile.waitHistory("Diff Textual");

        closeAllVCSOutputs ();
        difffile.cvsNode ().cVSDiffTextual();
        diff = new CVSTextualDiffFileAdvDialog ();
        diff.setRevisionOrTag1("1.1");
        diff.setRevisionOrTag2("1.2");
        info.println ("PASS3");
        history.print();
        diff.oK();
        diff.waitClosed ();
        difffile.waitHistoryFailed("Diff Textual");
        coo = new VCSCommandsInOutputOperator ("Diff Textual");
        waitNoEmpty(coo.txtStandardOutput());
        str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Diffing: 1.1 and 1.2 ====");
        filt.filterStringLinesToStream(out, str);
        
        closeAllVCSOutputs ();
        difffile.cvsNode ().cVSDiffTextual();
        diff = new CVSTextualDiffFileAdvDialog ();
        diff.setRevisionOrTag1("1.2");
        diff.setRevisionOrTag2("1.3");
        diff.oK();
        diff.waitClosed ();
        difffile.waitHistoryFailed ("Diff Textual");
        coo = new VCSCommandsInOutputOperator ("Diff Textual");
        waitNoEmpty(coo.txtStandardOutput());
        str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Diffing: 1.2 and 1.3 ====");
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testDefaultPatch () {
        Filter filt = new Filter ();
        filt.addFilterAfter ("*** " + difffile.history () + ":1.2");
        filt.addFilterAfter ("--- " + difffile.history ());

        closeAllVCSOutputs ();
        difffile.cvsNode ().cVSPatch ();
        CVSPatchFileAdvDialog diff = new CVSPatchFileAdvDialog ();
        diff.oK();
        diff.waitClosed ();
        difffile.waitHistory ("Patch");
        VCSCommandsInOutputOperator coo = new VCSCommandsInOutputOperator ("Patch");
        waitNoEmpty(coo.txtStandardOutput());
        String str = coo.txtStandardOutput ().getText ();
        info.println (str);
        out.println ("==== Patch between: 1.2 and 1.3 ====");
        filt.filterStringLinesToStream(out, str);
        
        compareReferenceFiles ();
    }
    
    public void testUnmount() {
        new FilesystemNode(repository.tree(), root.node ()).unmount();
        new Node (repository.tree (), "").waitChildNotPresent(root.node ());
    }

}
