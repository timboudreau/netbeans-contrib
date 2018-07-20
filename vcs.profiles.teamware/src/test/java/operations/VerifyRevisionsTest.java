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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package operations;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;


public class VerifyRevisionsTest extends SCCSTest {

    public static Test suite() throws IOException {
        TestSuite suite = new TestSuite();
        File[] files = getReadOnlyTestFiles();
        for (int i = 0; i < files.length; i++) {
            SFile sFile = new SFile(files[i]);
            Set revisionList = sFile.getExternalRevisions();
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
            if (!sFile.isEncoded()) {
                byte[] b3 = sFile.getAsString(revision, true)
                    .getBytes();
                log("Checking ASCII, expanded");
                checkASCII(b1, b3, out, err);
            }
            log("Checking binary, expanded");
            check(b1, b2, out, err);
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
            if (!sFile.isEncoded()) {
                byte[] b3 = sFile.getAsString(revision, false)
                    .getBytes();
                log("Checking ASCII, unexpanded");
                checkASCII(b1, b3, out, err);
            }
            log("Checking binary, unexpanded");
            check(b1, b2, out, err);
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

    private void checkASCII(byte[] b1, byte[] b2,
        ByteArrayOutputStream out, ByteArrayOutputStream err) throws IOException {

        List lines1 = parseLines(b1);
        List lines2 = parseLines(b2);
        try {
            assertStringCollectionEquals(lines1, lines2);
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
