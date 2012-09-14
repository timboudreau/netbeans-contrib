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

import java.util.Collections;
import java.util.Map;
import org.netbeans.modules.licensechanger.spi.handlers.XmlFileHandler;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import org.junit.Test;
import org.netbeans.modules.licensechanger.api.FileHandler;
import static org.junit.Assert.*;

/**
 *
 * @author Tim Boudreau
 */
public class XmlFileHandlerTest {
    private final Map<String,Object> props = Collections.emptyMap();
    
    @Test
    public void testDeclarationFinder() throws Exception {
        String xml = getGolden();
        assertDeclarationFound (xml, "xml_golden.txt");
        for (int i=1; i <= 8; i++) {
            String filename = "xml_" + i + ".txt";
            String content = JavaFileHandlerTest.readFile(filename);
            assertDeclarationFound(content, filename);
        }
    }

    private void assertDeclarationFound (String content, String filename) {
        String[] lines = FileHandler.splitIntoLines(content);
        boolean found = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher m = XmlFileHandler.xmlDeclaration.matcher(line);
            found = m.find();
            if (found) {
                System.err.println("Found declaration in line " + line + " of " + filename + "\n"
                        + m.group(1));
                break;
            }
        }
        assertTrue ("Did not find xml declaration in " + filename, found);
    }

    @Test
    public void testStuff() throws Exception {
        System.out.println("testStuff");
        String golden = getGolden();
        String license = JavaFileHandlerTest.getLicense();
        for (int i=1; i <= 8; i++) {
            String filename = "xml_" + i + ".txt";
            testOneVersion(golden, license, filename);
        }
    }

    private void testOneVersion(String golden, String license, String filename) throws Exception {
        System.out.println("Test " + filename);
        XmlFileHandler instance = new XmlFileHandler();
        String original = JavaFileHandlerTest.readFile (filename);
        String processed = instance.transform(original, license, props);
        assertEqualsLineByLine (golden, processed, filename);
    }

    static void assertEqualsLineByLine (String golden, String processed, String filename) {
        StringTokenizer a = new StringTokenizer (golden, "\n");
        StringTokenizer b = new StringTokenizer (processed, "\n");
        int ix = 0;
        while (a.hasMoreTokens() && b.hasMoreTokens()) {
            String as = a.nextToken();
            String bs = b.nextToken();
            if (!as.equals(bs)) {
                System.err.println("PROCESSED OUTPUT ");
            }
            assertEquals ("Difference in " + filename + " at line " + ix + "\n", as, bs);
            ix++;
        }

    }

    private static String getGolden() throws Exception {
        return JavaFileHandlerTest.readFile ("xml_golden.txt");
    }
}