/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package complete.pvcs_profile;

import junit.framework.*;
import org.netbeans.junit.*;
import org.netbeans.test.oo.gui.jelly.JellyProperties;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TestOut;
import org.openide.util.Utilities;

/** XTest / JUnit test class performing testing of whole PVCS support.
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
        try { System.out.println ("vlog return code: " + Runtime.getRuntime().exec("vlog").waitFor()); }
        catch (Exception e) { e.printStackTrace (); return suite; }
        suite.addTestSuite(RepositoryCreation.class);
        suite.addTestSuite(RegularDevelopment.class);
        suite.addTestSuite(AdditionalFeatures.class);
        return suite;
    }
    
    /** Use for internal test execution inside IDE.
     * @param args Command line arguments.
     */
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /** Method called before each testcase. Sets default timeouts, redirects system
     * output and maps main components.
     */
    protected void setUp() throws Exception {
        String workingDir = getWorkDirPath();
        new java.io.File(workingDir).mkdirs();
        java.io.File outputFile = new java.io.File(workingDir + "/output.txt");
        outputFile.createNewFile();
        java.io.File errorFile = new java.io.File(workingDir + "/error.txt");
        errorFile.createNewFile();
        java.io.PrintWriter outputWriter = new java.io.PrintWriter(new java.io.FileWriter(outputFile));
        java.io.PrintWriter errorWriter = new java.io.PrintWriter(new java.io.FileWriter(errorFile));
        org.netbeans.jemmy.JemmyProperties.setCurrentOutput(new org.netbeans.jemmy.TestOut(System.in, outputWriter, errorWriter));
    }
    
    /** Method called after each testcase. Resets Jemmy WaitComponentTimeout.
     */
    protected void tearDown() {
        JellyProperties.setDefaults();
    }
}
