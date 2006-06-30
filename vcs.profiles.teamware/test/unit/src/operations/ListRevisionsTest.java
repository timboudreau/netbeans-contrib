/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
