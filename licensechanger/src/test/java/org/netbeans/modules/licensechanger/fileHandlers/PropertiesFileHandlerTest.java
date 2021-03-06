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

import java.util.Collections;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.netbeans.modules.licensechanger.TestUtils.getLicense;
import static org.netbeans.modules.licensechanger.TestUtils.readFile;

/**
 *
 * @author Tim Boudreau
 */
public class PropertiesFileHandlerTest {

    private final Map<String, Object> props = Collections.emptyMap();

    @Test
    public void testStuff() throws Exception {
        System.out.println("testStuff");
        String golden = getGolden();
        String license = getLicense();
        for (int i = 1; i <= 4; i++) {
            testOneVersion(golden, license, "props_" + i + ".txt");
        }
        assertEquals(0, 0);
    }

    private void testOneVersion(String golden, String license, String filename) throws Exception {
        System.out.println("Test " + filename);
        PropertiesFileHandler instance = new PropertiesFileHandler();
        String original = readFile(PropertiesFileHandlerTest.class, filename);
        String processed = instance.transform(original, license, props);
//        if (!original.equals(processed)) {
//            System.out.println("************************************");
//            System.out.println(processed);
//            System.out.println("************************************");
//        }
        JavaFileHandlerTest.assertEqualsLineByLine(golden, processed, filename);
    }

    private static String getGolden() throws Exception {
        return readFile(PropertiesFileHandlerTest.class, "props_golden.txt");
    }
}
