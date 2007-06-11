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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
