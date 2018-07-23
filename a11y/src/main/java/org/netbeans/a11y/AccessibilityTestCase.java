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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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