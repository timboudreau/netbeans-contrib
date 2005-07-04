/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package operations;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcscore.versioning.RevisionItem;


public class ListRevisionsTest extends SCCSTest {

    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();
        File[] files = getReadOnlyTestFiles();
        for (int i = 0; i < files.length; i++) {
            suite.addTest(new ListRevisionsTest(files[i]));
        }
        return suite;
    }
        
    private File file;
    
    ListRevisionsTest(File file) {
        super();
        this.file = file;
    }
    
    public void runTest() throws Exception {
        log("Listing revisions for " + file);
        SFile sFile = new SFile(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        exec(file.getParentFile(),
            new String[] { "prs", "-e", "-d:I:", file.getName() }, out, err);
        try {
            assertEquals(sFile.getExternalRevisions(), out);
        } catch (AssertionFailedError e) {
            logToFile(file.getName() + ".out", out);
            logToFile(file.getName() + ".err", err);
            throw e;
        }
    }
    
    // make sure the output of "sccs prs" matches the RevisionList obtained
    // by parsing the S file
    private void assertEquals(Set revisionList, ByteArrayOutputStream out)
        throws IOException {
        List lines = parseLines(out.toByteArray());
        List revisions1 = new ArrayList(lines);
        List revisions2 = new ArrayList();
        for (Iterator j = revisionList.iterator(); j.hasNext();) {
            RevisionItem item = (RevisionItem) j.next();
            revisions2.add(item.getRevision());
        }
        Collections.sort(revisions1);
        Collections.sort(revisions2);
        assertStringCollectionEquals(revisions1, revisions2);
    }
        
}
