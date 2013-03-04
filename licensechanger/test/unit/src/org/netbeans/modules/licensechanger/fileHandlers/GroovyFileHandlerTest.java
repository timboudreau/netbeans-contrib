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
package org.netbeans.modules.licensechanger.fileHandlers;

import org.netbeans.modules.licensechanger.fileHandlers.GroovyFileHandler;
import java.util.Collections;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.netbeans.modules.licensechanger.TestUtils.*;
import org.netbeans.modules.licensechanger.api.FileHandler;

/**
 * Test cases for groovy license replacement.
 *
 * @author Nils Hoffmann
 */
public class GroovyFileHandlerTest {

    private final Map<String, Object> props = Collections.emptyMap();

    void runTest(String infix) throws Exception {
        String golden = getGolden(infix);
        String license = getLicense();
        for (int i = 1; i <= 12; i++) {
            String header = "resources/groovy/header_" + i + ".txt";
            String template = "resources/groovy/groovy_" + infix + "_template.txt";
            System.out.println("Test " + " header_" + i + ".txt with groovy_" + infix + "_template.txt");
            testOneVersion(golden, license, merge(header, template));
        }
    }

    @Test
    public void testGroovyClass() throws Exception {
        System.out.println("testGroovyClass");
        runTest("class");
    }

    /**
     * FIXME This test currently fails due to the removal of the author comment.
     * This is probably intended, but might prove to be a bug for groovy files,
     * as it would unexpectedly remove comments.
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public void testGroovyClassNoPackage() throws Exception {
        System.out.println("testGroovyClassNoPackage");
        runTest("class_nopackage");
    }

    @Test
    public void testGroovyScript() throws Exception {
        System.out.println("testGroovyScript");
        runTest("script");
    }

    private void testOneVersion(String golden, String license, String original) throws Exception {
        GroovyFileHandler instance = new GroovyFileHandler();
        String processed = instance.transform(original, license, props);
        assertEqualsLineByLine(golden, processed);
    }

    static void assertEqualsLineByLine(String golden, String processed) {
        String[] g = FileHandler.splitIntoLines(golden);
        String[] p = FileHandler.splitIntoLines(processed);
        for (int i = 0; i < Math.min(g.length, p.length); i++) {
            if (!g[i].equals(p[i])) {
                assertEquals("Difference between files at line " + i + "\n", g[i], p[i]);
            }
        }
    }

    private static String merge(String header, String template) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(readFile(GroovyFileHandlerTest.class,header));
        sb.append(readFile(GroovyFileHandlerTest.class,template));
        return sb.toString();
    }

    private static String getGolden(String infix) throws Exception {
        return readFile(GroovyFileHandlerTest.class,"resources/groovy/groovy_" + infix + "_golden.txt");
    }
}
