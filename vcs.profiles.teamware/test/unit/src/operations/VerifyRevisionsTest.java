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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import junit.framework.*;
import org.netbeans.junit.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionList;

public class VerifyRevisionsTest extends SCCSTest {
    
    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();
        File[] files = getReadOnlyTestFiles();
        for (int i = 0; i < files.length; i++) {
            SFile sFile = new SFile(files[i]);
            SRevisionList revisionList = sFile.getRevisions();
            for (Iterator j = revisionList.iterator(); j.hasNext();) {
                SRevisionItem item = (SRevisionItem) j.next();
                suite.addTest(new VerifyRevisionsTest(files[i], item));
            }
        }
        return suite;
    }
    
    private File file;
    private SRevisionItem revision;

    public VerifyRevisionsTest() {
    }
    
    VerifyRevisionsTest(File file, SRevisionItem revision) {
        super(file.getName() + "." + revision);
        this.file = file;
        this.revision = revision;
    }

    public void runTest() throws Exception {
        if (file == null) {
            throw new NullPointerException("'file' should not be null");
        }
        log("Verifying revision " + revision.getRevision() + " for " + file);
        SFile sFile = new SFile(file);
        // Check with expanded tags
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            File tmpFile = new File(getWorkDir(), file.getName());
            String[] args = {
                    "get",
                    "-r" + revision.getRevision(),
                    "-G" + tmpFile.toString(),
                    file.getName()
            };
            exec(file.getParentFile(), args, out, err);
            out.reset();
            InputStream is = new FileInputStream(tmpFile);
            byte[] buffer = new byte[4096];
            for (int k; (k = is.read(buffer)) != -1;) {
                out.write(buffer, 0, k);
            }
            is.close();
            byte[] b1 = out.toByteArray();
            byte[] b2 = sFile.getAsBytes(revision, true);
            log("Checking binary, expanded");
            check(b1, b2, out, err);
            if (!sFile.isEncoded()) {
                byte[] b3 = sFile.getAsString(revision, true)
                    .getBytes();
                log("Checking ASCII, expanded");
                check(b1, b3, out, err);
            }
            tmpFile.delete();
        }
        // and unexpanded tags
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            File tmpFile = new File(getWorkDir(), file.getName());
            String[] args = {
                    "get",
                    "-r" + revision.getRevision(),
                    "-k",
                    "-G" + tmpFile.toString(),
                    file.getName()
            };
            exec(file.getParentFile(), args, out, err);
            out.reset();
            InputStream is = new FileInputStream(tmpFile);
            byte[] buffer = new byte[4096];
            for (int k; (k = is.read(buffer)) != -1;) {
                out.write(buffer, 0, k);
            }
            is.close();
            byte[] b1 = out.toByteArray();
            byte[] b2 = sFile.getAsBytes(revision, false);
            log("Checking binary, unexpanded");
            check(b1, b2, out, err);
            if (!sFile.isEncoded()) {
                byte[] b3 = sFile.getAsString(revision, false)
                    .getBytes();
                log("Checking ASCII, unexpanded");
                check(b1, b3, out, err);
            }
            tmpFile.delete();
        }
    }

    private void check(byte[] b1, byte[] b2,
        ByteArrayOutputStream out, ByteArrayOutputStream err) {

        try {
            assertEquals(b1, b2);
        } catch (AssertionFailedError e) {
            log("Expected: ");
            log(out);
            log(err);
            log("but got:");
            log(new String(b2));
            throw e;
        }
    }

}
