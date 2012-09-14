/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.licensechanger.spi.handlers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import org.netbeans.modules.licensechanger.api.FileHandler;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author Tim Boudreau
 */
public class JavaFileHandlerTest {
    
    private final Map<String,Object> props = Collections.emptyMap();

    @Test
    public void testStuff() throws Exception {
        System.out.println("testStuff");
        String golden = getGolden();
        String license = getLicense();
        for (int i=1; i <= 12; i++) {
            String filename = "java_" + i + ".txt";
            testOneVersion(golden, license, filename);
        }
    }

    private void testOneVersion(String golden, String license, String filename) throws Exception {
        System.out.println("Test " + filename);
        JavaFileHandler instance = new JavaFileHandler();
        String original = readFile (filename);
        String processed = instance.transform(original, license, props);
        assertEqualsLineByLine (golden, processed, filename);
    }

    static void assertEqualsLineByLine (String golden, String processed, String filename) {
        String[] g = FileHandler.splitIntoLines(golden);
        String[] p = FileHandler.splitIntoLines(processed);
        for (int i=0; i < Math.min (g.length, p.length); i++) {
            assertEquals ("Difference in " + filename + " at line " + i + "\n", g[i], p[i]);
        }
    }


//    static void assertEqualsLineByLine (String golden, String processed, String filename) {
//        StringTokenizer a = new StringTokenizer (golden, "\n");
//        StringTokenizer b = new StringTokenizer (processed, "\n");
//        int ix = 0;
//        while (a.hasMoreTokens() && b.hasMoreTokens()) {
//            String as = a.nextToken();
//            String bs = b.nextToken();
//            if (!as.equals(bs)) {
//                System.err.println("PROCESSED OUTPUT ");
//            }
//            assertEquals ("Difference in " + filename + " at line " + ix + "\n", as, bs);
//            ix++;
//        }
//
//    }

    private static String getGolden() throws Exception {
        return readFile ("java_golden.txt");
    }

    static String getLicense() throws Exception {
        return readFile ("fake_license.txt");
    }

    static String readFile (String name) throws Exception {
        InputStream in = JavaFileHandlerTest.class.getResourceAsStream(name);
        if (in == null) {
            fail ("No input stream for " + name);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileUtil.copy (in, out);
        } finally {
            in.close();
            out.close();
        }
        String result = new String (out.toByteArray(), "UTF-8");
        result = Utilities.replaceString(result, "\r\n", "\n");
        return result;
    }
 
}