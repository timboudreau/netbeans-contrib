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

package complete.vss_profile;

import junit.framework.*;
import org.netbeans.junit.*;
import org.openide.util.Utilities;

/** XTest / JUnit test class performing testing of whole VSS support.
 * @author Jiri Kovalsky
 * @version 1.0
 */
public class AllTogether extends NbTestCase {
    
    /** Constructor required by JUnit.
     * @param testName Method name to be used as testcase.
     */
    public AllTogether(String testName) {
        super(testName);
    }
    
    /** Method used for explicit test suite definition.
     * @return AllTogether test suite.
     */
    public static junit.framework.Test suite() {
        TestSuite suite = new NbTestSuite();
        if (Utilities.isUnix()) return suite;
        String workingDirectory = "";
        try { workingDirectory = new AllTogether("").getWorkDir().getAbsolutePath(); }
        catch (java.io.IOException e) {}
        String zipFile = workingDirectory.substring(0, workingDirectory.indexOf("complete")) + "vss.zip";
        if (!new java.io.File(zipFile).exists()) return suite; // This test suite can't run where zip with empty VSS repository is not prepared.
        suite.addTestSuite(RepositoryCreation.class);
        suite.addTestSuite(RegularDevelopment.class);
        suite.addTestSuite(AdditionalCommands.class);
        suite.addTestSuite(AdditionalFeatures.class);
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}