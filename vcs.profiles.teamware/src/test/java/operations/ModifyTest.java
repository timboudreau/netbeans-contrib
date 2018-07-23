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
import java.util.Set;
import junit.framework.*;
import util.SCCSTest;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcs.profiles.teamware.util.SRevisionItem;


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
        String[] modificationInstructions =
                sFile.getRevisions().getActiveRevision().getMessage().trim().split(";");
        if (modificationInstructions.length == 0) {
            return;
        }
        Modification[] mods = new Modification[modificationInstructions.length];
        for (int i = 0; i < mods.length; i++) {
            try {
                String[] s = modificationInstructions[i].split("[+-]");
                mods[i] = new Modification();
                mods[i].index = Integer.parseInt(s[0]);
                mods[i].add = Integer.parseInt(s[1]);
                mods[i].remove = Integer.parseInt(s[2]);
            } catch (NumberFormatException e) {
                log("Not a modification: " + modificationInstructions[i]);
                return;
            }
        }
        boolean encoded = sFile.isEncoded();
        log("Modifying revision " + file + (encoded ? " (binary)" : ""));
        sFile.edit();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0, index = 1; i < mods.length; index++) {
            if ((mods[i].remove == 0 && index == mods[i].index + 1)
                || (mods[i].remove != 0 && index == mods[i].index)) {
                if (encoded) {
                    for (int j = 0; j < mods[i].add; j++) {
                        baos.write('A');
                    }
                    for (int j = 0; j < mods[i].remove; j++) {
                        in.read();
                        index ++;
                    }
                } else {
                    for (int j = 0; j < mods[i].add; j++) {
                        baos.write('A');
                        baos.write('\n');
                    }
                    for (int j = 0; j < mods[i].remove;) {
                        if (in.read() == '\n') {
                            j ++;
                            index ++;
                        }
                    }
                }
                i++;
            }
            if (encoded) {
                int ch = in.read();
                baos.write(ch);
            } else {
                for (int ch; (ch = in.read()) != '\n' && ch != -1;) {
                    baos.write(ch);
                }
                baos.write('\n');
            }                
        }
        // copy the remainder
        byte[] buffer = new byte[100];
        for (int bytesRead; (bytesRead = in.read(buffer)) != -1;) {
            baos.write(buffer, 0, bytesRead);
        }
        in.close();
        OutputStream out = new FileOutputStream(file);
        out.write(baos.toByteArray());
        out.close();
        sFile.delget("Applied modifications");
        Set revisionList = sFile.getExternalRevisions();
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
    
    static class Modification {
        int index;
        int add;
        int remove;
    }
    
}
