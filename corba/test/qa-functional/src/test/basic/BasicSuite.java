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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package test.basic;

public class BasicSuite {

    public static junit.framework.Test suite() {
        org.netbeans.junit.NbTestSuite suite = new org.netbeans.junit.NbTestSuite();
        suite.addTest(test.settings.Main.suite ());
        suite.addTest(test.overall.Main.suite ());
        suite.addTest (test.parser.Main.suite ());
        suite.addTest (test.ioranalyzer.Main.suite ());
        suite.addTest (test.nsbrowser.Main.suite ());
        suite.addTest (test.indentation.Main.suite ());
        suite.addTest (test.corbawizard.Main.suite ());
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
}
