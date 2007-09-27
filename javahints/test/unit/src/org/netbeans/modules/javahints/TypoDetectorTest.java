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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.javahints;

import com.sun.source.util.TreePath;
import java.util.List;
import java.util.logging.Level;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.infrastructure.ErrorHintsTestBase;
import org.netbeans.spi.editor.hints.Fix;

/**
 *
 * @author Jan Lahoda
 */
public class TypoDetectorTest extends ErrorHintsTestBase {
    
    public TypoDetectorTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        TypoDetector.LOG.setLevel(Level.FINE);
    }

    public void testSimple1() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {private int test; public void dfgh() {tfst = 0;}}", 119 - 48, "rename: test");
    }
    
    public void testSimple2() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void dfgh() {tfst ffff = null;} private int test;}", 101 - 48, "rename: test");
    }
    
    public void testSimple3() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {tfst();} private int test;}", 101 - 48, "rename: test");
    }
    
    public void testSimple4() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {java.util.List<tfst> t = null;}}", 116 - 48, "rename: test");
    }
    
    public void testSimple5() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {public void test() {java.util.List<tfst> t = null;} private int test;}", 116 - 48, "rename: test");
    }
    
    public void testSimple6() throws Exception {
        performAnalysisTest("test/Test.java", "package test; public class test {@Overide public int hashCode() {return 0;} private int Overidf;}", 86 - 48, "rename: Override");
    }
    
    protected List<Fix> computeFixes(CompilationInfo info, int pos, TreePath path) {
        return new TypoDetector().run(info, "compiler.err.doesnt.exist", pos, path, null);
    }

    @Override
    protected String toDebugString(CompilationInfo info, Fix f) {
        return ((TypoDetector.FixImpl) f).toDebugString();
    }
    
    
}
