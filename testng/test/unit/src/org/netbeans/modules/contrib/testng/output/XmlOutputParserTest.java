/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.contrib.testng.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.contrib.testng.output.Report.Testcase;
import org.xml.sax.SAXException;

/**
 *
 * @author lukas
 */
public class XmlOutputParserTest extends NbTestCase {

    public XmlOutputParserTest(String name) {
        super(name);
    }

    public void testParseSimpleXmlOutput() throws Exception {
        List<Report> reports = parseResultXML(new File(getDataDir(), "results/testng-results.xml"));
        assertEquals(7, reports.size());
        Report result = reports.get(0);
        assertEquals("test.FailPassSkipTest", result.suiteClassName);
        assertEquals(3, result.getTests().size());
        Testcase[] tcs = result.getTests().toArray(new Testcase[3]);
        assertEquals("cTest", tcs[1].name);
        assertEquals(1, result.failures);
        assertEquals(1, result.skips);
        assertEquals(3, result.totalTests);
        assertEquals(1, result.detectedPassedTests);
        assertEquals(0, result.confSkips);
        assertEquals(0, result.confFailures);
        assertNotNull(tcs[1].trouble);
        assertFalse(tcs[1].trouble.isFailure());
        assertNotNull(tcs[0].trouble);
        assertTrue(tcs[0].trouble.isFailure());

        result = reports.get(5);
        assertEquals("test.SetUpTest", result.suiteClassName);
        assertEquals(3, result.getTests().size());
        tcs = result.getTests().toArray(new Testcase[3]);
        assertEquals("setUp", tcs[0].name);
        assertEquals(0, result.failures);
        assertEquals(1, result.skips);
        assertEquals(1, result.totalTests);
        assertEquals(0, result.detectedPassedTests);
        assertEquals(1, result.confSkips);
        assertEquals(1, result.confFailures);
        assertNotNull(tcs[0].trouble);
        assertTrue(tcs[0].trouble.isFailure());
        assertNotNull(tcs[1].trouble);
        assertFalse(tcs[1].trouble.isFailure());
    }

    public void testParseXmlOutput() throws Exception {
        List<Report> reports = parseResultXML(new File(getDataDir(), "results/testng-results_1.xml"));
        assertEquals(71, reports.size());
    }

    public void testParseXmlOutput2() throws Exception {
        List<Report> reports = parseResultXML(new File(getDataDir(), "results/testng-results_2.xml"));
        assertEquals(1, reports.size());
    }

    static List<Report> parseResultXML(File f) throws IOException, SAXException {
        Reader reader = new BufferedReader(new FileReader(f));
        return XmlOutputParser.parseXmlOutput(reader);
    }
}