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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import junit.framework.*;
import org.netbeans.junit.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionList;
import org.netbeans.modules.vcs.profiles.teamware.util.UU;

public class ModifyTest extends SCCSTest {
    
    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();
        File[] files = getReadOnlyTestFiles();
        for (int i = 0; i < files.length; i++) {
            suite.addTest(new ModifyTest(files[i]));
        }
        return suite;
    }
    
    private File file;
    private String revision;

    public ModifyTest() { }
    
    ModifyTest(File file) throws IOException {
        super("ModifyTest." + file.getName());
        this.file = file;
    }

    public void runTest() throws Exception {
        if (file == null) {
            throw new NullPointerException("'file' should not be null");
        }
        file = getFileCopy(file);
        SFile sFile = new SFile(file);
        boolean encoded = sFile.isEncoded();
        log("Modifying revision " + file + (encoded ? " (binary)" : ""));
        sFile.edit();
        byte[] buffer = new byte[100];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        for (int bytesRead, i = 0; (bytesRead = in.read(buffer)) != -1; i++) {
            baos.write(buffer, 0, bytesRead);
            baos.write('A');
            if (i % 3 == 0) {
                baos.write('\n');
            }
        }
        in.close();
        OutputStream out = new FileOutputStream(file);
        out.write(baos.toByteArray());
        out.close();
        sFile.delget("Test comment (\"test\"");
        SRevisionList revisionList = sFile.getRevisions();
        for (Iterator i = revisionList.iterator(); i.hasNext();) {
            verifyRevision(file, sFile, (SRevisionItem) i.next());
        }
        file.delete();
        new File(file.getParent(),
            "SCCS" + File.separator + "s." + file.getName())
                .delete();
    }
    
    private void verifyRevision(File file, SFile sFile, SRevisionItem revision)
            throws Exception {
        
        log("Checking revision " + revision + " of " + file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        File tmpFile = new File(getWorkDir(),
            file.getName() + "." + revision.getRevision() + ".txt");
        String[] args = {
                "get",
                "-r" + revision.getRevision(),
                "-k",
                "-G" + tmpFile.toString(),
                file.getName()
        };
        exec(file.getParentFile(), args, out, err);
        log(err);
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
