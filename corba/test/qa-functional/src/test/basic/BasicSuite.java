/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
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
        suite.addTest (test.idlwizard.Main.suite ());
        suite.addTest (test.poasupport.Main.suite ());
        suite.addTest (test.indentation.Main.suite ());
        suite.addTest (test.corbawizard.Main.suite ());
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
}
