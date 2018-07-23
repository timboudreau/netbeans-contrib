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

package org.netbeans.modules.tasklist.docscan;

import java.io.File;
import org.openide.loaders.DataObject;
import org.openide.filesystems.Repository;
import java.net.URL;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.tasklist.providers.SuggestionContexts;
import org.netbeans.modules.tasklist.suggestions.Types;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.LocalFileSystem;

/**
 * Test the source scanner list functionality
 *
 * @author  Tor Norbye
 */
public class SourceTaskProviderTest extends TestCase {

    public SourceTaskProviderTest (String name) {
        super (name);
    }
    
    /** NB filesytem initialized during setUp. */
    private FileSystem dataFS;

    protected void setUp () throws Exception {
        URL url = this.getClass().getResource("data");
        String resString = NbTestCase.convertNBFSURL(url);
        LocalFileSystem fs = new LocalFileSystem();
        fs.setRootDirectory(new File(resString));
        Repository.getDefault().addFileSystem(fs);
        dataFS = fs;

        Types.installSuggestionTypes();
    }

    protected void tearDown () throws Exception {
        Repository.getDefault().removeFileSystem(dataFS);
    }

    /** Test the source scanner
     * @todo: try Windows format (\r\n) and Macintosh format (\r) as well
     * @todo: Try varying the options (regexp, etc.)
     *        and make sure the result is as expected
     * @todo: Do a document edit and make sure the copyright fixer is disabled,
     *        line positions updated, etc.
     * @todo: Try passing in different filenames (.java, .html, etc.) and
     *        see if the source scanner handles comments correctly
    */
    public void testSourceScanner() throws Exception {
        Settings settings = Settings.getDefault();
        settings.setSkipComments(true);
	    assertTrue("Skip Comments bean doesn't work", settings.getSkipComments());

        SourceTaskProvider scanner = new SourceTaskProvider();
        FileObject fo = dataFS.findResource("Comments.java");
        DataObject dobj = DataObject.find(fo);
        List result = scanner.scan(SuggestionContexts.forDataObject(dobj));

        assertTrue(result.size() == 7);

        // Make sure that the scanned list is correct

        // Check that the list we read in is indeed correct
//        Task root = list.getRoot();
//        List subtasks = root.getSubtasks();
//        assertTrue("Not all tasks in the list found: found " + subtasks.size() +
//                   " elements",
//                   subtasks.size() == 3);
//        ListIterator it = subtasks.listIterator();
//        int count = 0;
//        while (it.hasNext()) {
//            DocTask task = (DocTask)it.next();
//            switch (count) {
//            case 0: {
//                assertEquals("Wrong copyright description",
//                             "Update Copyright to 1999-2002; currently is 1999",
//                             task.getSummary());
//                assertEquals("Wrong line number", 1, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                assertTrue("Missing fixer", task.getAction() != null);
//                break;
//            }
//            case 1: {
//                assertEquals("Wrong scanned description",
//                             "TODO fix this",
//                             task.getSummary());
//                assertEquals("Wrong line number", 3, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                break;
//            }
//            case 2: {
//                assertEquals("Wrong scanned description",
//                             "XXX another one",
//                             task.getSummary());
//                assertEquals("Wrong line number", 5, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                break;
//            }
//            }
//            count++;
//        }
//
//        settings.setSkipComments(false);
//        // The testsuite isn't listening to TaskEdSettings changes? So jiggle
//        // it.
//        scanner.commentsToggled(false);
//
//        scanner.scan(doc, dobj, true, true);
//
//         // Check that the list we read in is indeed correct
//        root = list.getRoot();
//        subtasks = root.getSubtasks();
//        assertTrue("Not all tasks in the list found: found " + subtasks.size() +
//                   " elements",
//                   subtasks.size() == 4);
//        it = subtasks.listIterator();
//        count = 0;
//        while (it.hasNext()) {
//            DocTask task = (DocTask)it.next();
//            switch (count) {
//                // TODO Update the below so it doesn't have 2002 but current
//                // year - that way the testcase won't fail next year!
//            case 0: {
//                assertEquals("Wrong copyright description",
//                             "Update Copyright to 1999-2002; currently is 1999",
//                             task.getSummary());
//                assertEquals("Wrong line number", 1, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                assertTrue("Missing fixer", task.getAction() != null);
//                break;
//            }
//            case 1: {
//                assertEquals("Wrong scanned description",
//                             "int x = 0; // TODO fix this",
//                             task.getSummary());
//                assertEquals("Wrong line number", 3, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                break;
//            }
//            case 2: {
//                assertEquals("Wrong scanned description",
//                             "int y = 5; // XXX another one",
//                             task.getSummary());
//                assertEquals("Wrong line number", 5, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                break;
//            }
//            case 3: {
//                assertEquals("Wrong scanned description",
//                             "int XXX = 5; // outside of comment ignored?",
//                             task.getSummary());
//                assertEquals("Wrong line number", 7, task.getLineNumber());
//                assertEquals("Wrong filename", "foo.c", task.getFileBaseName());
//                break;
//            }
//            }
//            count++;
//        }
//
//
    }


   /**
    * Make sure we handle duplicates in the source correctly; see
    * http://www.netbeans.org/issues/show_bug.cgi?id=27459
    */
    public void testDuplicates27459() throws Exception {

       Settings settings = Settings.getDefault();
       settings.setSkipComments(true);
       assertTrue("Skip Comments bean doesn't work", settings.getSkipComments());

       SourceTaskProvider scanner = new SourceTaskProvider();
       FileObject fo = dataFS.findResource("iz27459.java");
       DataObject dobj = DataObject.find(fo);
       List result = scanner.scan(SuggestionContexts.forDataObject(dobj));

       assertTrue(result.size() == 3);

        // Make sure that the scanned list is correct

//        // Check that the list we read in is indeed correct
//        Task root = list.getRoot();
//        List subtasks = root.getSubtasks();
//        assertTrue("Not all tasks in the list found: found " + subtasks.size() +
//                   " elements",
//                   subtasks.size() == 3);
//        ListIterator it = subtasks.listIterator();
//        int count = 0;
//        while (it.hasNext()) {
//            DocTask task = (DocTask)it.next();
//            switch (count) {
//            case 0: {
//                assertEquals("Wrong scanned description",
//                             "TODO Fix me!",
//                             task.getSummary());
//                assertEquals("Wrong line number", 4, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            case 1: {
//                assertEquals("Wrong scanned description",
//                             "OTHER FIXME",
//                             task.getSummary());
//                assertEquals("Wrong line number", 5, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            case 2: {
//                assertEquals("Wrong scanned description",
//                             "TODO Fix me!",
//                             task.getSummary());
//                assertEquals("Wrong line number", 6, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            }
//            count++;
//        }
//
//
//        try {
//            doc.insertString(0, "TODO Fix me!\n", null);
//        } catch (BadLocationException e) {
//            fail("BadLocationException");
//        }
//
//        // scanner.rescan(); Can't use rescan; lastDocument not set
//        //   since we skipped scan(Node, boolean)
//        scanner.scan(doc, dobj, false, true);
//
//        // Check that the list we read in is indeed correct
//        root = list.getRoot();
//        subtasks = root.getSubtasks();
//        assertTrue("Not all tasks in the list found: found " + subtasks.size() +
//                   " elements",
//                   subtasks.size() == 4);
//        it = subtasks.listIterator();
//        count = 0;
//        while (it.hasNext()) {
//            DocTask task = (DocTask)it.next();
//            switch (count) {
//            case 0: {
//                assertEquals("Wrong scanned description",
//                             "TODO Fix me!",
//                             task.getSummary());
//                assertEquals("Wrong line number", 7, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            case 1: {
//                assertEquals("Wrong scanned description",
//                             "TODO Fix me!",
//                             task.getSummary());
//                assertEquals("Wrong line number", 1, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            case 2: {
//                assertEquals("Wrong scanned description",
//                             "OTHER FIXME",
//                             task.getSummary());
//                assertEquals("Wrong line number", 6, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            case 3: {
//                assertEquals("Wrong scanned description",
//                             "TODO Fix me!",
//                             task.getSummary());
//                assertEquals("Wrong line number", 5, task.getLineNumber());
//                assertEquals("Wrong filename", "foo", task.getFileBaseName());
//                break;
//            }
//            }
//            count++;
//        }
    }

   /**
    * Make sure that scanning a directory works correctly
    */
    public void testDirectoryScan() throws Exception {
	// XXX todo

//        DataObject folder = null;
//	try {
//	    // Create filesystem where my to-be-scanned files are
//	    File data = new File (getClass ().getResource ("data").getFile ());
//	    LocalFileSystem lfs = new LocalFileSystem ();
//	    lfs.setRootDirectory (data);
//            Repository.getDefault().addFileSystem(lfs);
//
//	    folder = DataObject.find (lfs.findResource("scanfiles"));
//	    assertTrue(folder != null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//
//
//
//	TaskList list = new TaskList(new DocTask("RootScanTask", null, null, 0));
//	SourceScanner scanner = new SourceScanner(list, true);
//        ScanTasksAction.scan(scanner, (DataFolder)folder, true);
//
//        //System.err.println("After scan:");
//        //list.print();
//
//        assertTrue("Didn't get the correct number of list elements",
//                   list.size() == 2);
//        Task root = list.getRoot();
//        List subtasks = root.getSubtasks();
//        assertTrue("Not all tasks in the list found: found " + subtasks.size() +
//                   " elements",
//                   subtasks.size() == 2);
//        ListIterator it = subtasks.listIterator();
//        DocTask task = (DocTask)it.next();
//        assertTrue("Wrong element found in scanned list",
//                   task.getSummary().equals("TODO This is ANOTHER test") ||
//                   task.getSummary().equals("XXX This is a test"));
//        // TODO -- better/more complete check - check both elements, filename,
//        // etc.?
//        /* Expecting:
//          EditorTask["TODO This is ANOTHER test", /snorre/nb40/tasklist/test/work/sys/tests/unit/src/org/netbeans/modules/tasklist/docscan/data/scanfiles/test3.html:4]
//          EditorTask["XXX This is a test", /snorre/nb40/tasklist/test/work/sys/tests/unit/src/org/netbeans/modules/tasklist/docscan/data/scanfiles/test2.html:2]
//        */
    }
}
