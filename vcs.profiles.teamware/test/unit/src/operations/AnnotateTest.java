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
