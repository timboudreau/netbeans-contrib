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

package org.netbeans.a11y;

import java.awt.Component;

/** An abstract class defining a test case for accessibility tests.
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.Com
 */
public abstract class AccessibilityTestCase{

    /**
     *  Run the test.
     */
    public void execute(){

        String settingsFileName = setSettingsFile();
        String resultsFileName = setResultFile();

        TestSettings settings = initializeSettings(settingsFileName);

        Component comp = setUp();

        if (comp != null){
            //System.setProperty("a11ytest.excludes", options.getExcludes());
            AccessibilityTester tester = new AccessibilityTester(comp, settings);
            
            tester.startTests();
            
            if (resultsFileName != null){
                try{
                    java.io.FileWriter writer = new java.io.FileWriter(resultsFileName);
                    XMLReport report = new XMLReport(tester, settings);
                    report.getReport(writer);
                } catch (java.io.IOException e){
                    // Do something about it or ignore it?
                    // Could just default to Output Window?
                }
            } else{
                TextReport report = new TextReport(tester, settings);
                report.getReport(new java.io.PrintWriter(System.out));
            }
        }
        
        tearDown();
    }
    
    
    /** Initialize Test Settings from file fileName, if loading fails - return default settings.
     * @param fileName file consists Test Settings
     * @return  loaded Test Setings */
    public static TestSettings initializeSettings(String fileName) {
        
        TestSettings settings = new TestSettings();
        if(fileName != null)
            try {
                settings = TestSettingsLogger.readSettings(fileName);
            } catch(java.io.IOException exc) {
                exc.printStackTrace(System.err);
                settings.setDefaultSettings();
            } else
                settings.setDefaultSettings();
        
        return settings;
    }
    
    
    /** Initialize name of settings file
     * <p>
     * The implementation of this method should return the settings file name
     * @return a settings file name
     */
    public abstract String setSettingsFile();
    
    /** Initialize name of result file
     * <p>
     * The implementation of this method should return the result file name
     * @return a result file name
     */
    public abstract String setResultFile();
    
    /** Initialize the test.
     *  <p>
     *  The implementation of this method should create the component and
     *  make it visible if necessary.
     *  @return a reference to the component to be tested, or null if setup failed
     */
    public abstract Component setUp();
    
    /** Clean up after performing the test.
     *  <p>
     *  Typically, this will be closing dialogs, etc. This method
     *  should execute cleanly even if {@link #setUp() setUp} failed. */
    public abstract void tearDown();
    
    /** Get a description of this test.
     *  @return the description
     */
    public String getDescription(){
        return "No description supplied";
    }
    
    /** The writer that the report is sent to. The default value is System.out
     *  but subclasses can specify their own. */
    protected java.io.PrintWriter writer = new java.io.PrintWriter(System.out);
    
}