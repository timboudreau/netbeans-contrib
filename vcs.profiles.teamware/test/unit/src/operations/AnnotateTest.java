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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.framework.*;
import org.netbeans.junit.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class AnnotateTest extends SCCSTest {

    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();
        File[] files = getReadOnlyTestFiles();
        for (int i = 0; i < files.length; i++) {
            suite.addTest(new AnnotateTest(files[i]));
        }
        return suite;
    }
        
    private File file;
    
    AnnotateTest(File file) {
        super();
        this.file = file;
    }
    
    public void runTest() throws Exception {
        SFile sFile = new SFile(file);
        if (sFile.isEncoded()) {
            log("Skipping " + file + " since it is encoded");
            return;
        }
        log("Verifying annotations for " + file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        File tmpFile = new File(getWorkDir(), file.getName());
        String[] args = {
                "get",
                "-m",
                "-G" + tmpFile.toString(),
                file.getName()
        };
        exec(file.getParentFile(), args, out, err);
        out.reset();
        InputStream is = new FileInputStream(tmpFile);
        byte[] buffer = new byte[4096];
        for (int j; (j = is.read(buffer)) != -1;) {
            out.write(buffer, 0, j);
        }
        is.close();
        List linesA = parseLines(out.toByteArray());
        final List linesB = new ArrayList();
        CommandOutputListener listener = new CommandOutputListener() {
            public void outputLine(String line) {
                int i = line.indexOf(":");
                String revision = line.substring(0, i);
                i = line.indexOf(":", i + 1);
                i = line.indexOf(":", i + 1);
                linesB.add(revision + "\t" + line.substring(i + 1));
            }
        };
        sFile.annotate(listener);
        try {
            assertStringCollectionEquals(linesA, linesB);
        } catch (AssertionFailedError e) {
            logToFile(file.getName() + ".out", out);
            logToFile(file.getName() + ".err", err);
            throw e;
        }
        tmpFile.delete();
        log("Verified annotations for " + file);
    }
    
}
