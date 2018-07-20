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


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JInternalFrame;

import org.netbeans.junit.NbTestCase;


/**
 *  Abstract Accessibility test usable for XTest
 *  @author Marian.Mirilovic@Sun.com
 */
public abstract class XTestAccessibilityTestCase extends NbTestCase {

    protected PrintStream ref;
    protected PrintStream log;

    private String settingsFileName;
    private Component testedComponent;
    
    
    /** Creates a new instance of XTestAccessibilityTestCase
     * @param testName  name of the test*/
    public XTestAccessibilityTestCase(String testName) {
        super(testName);
    }
    
    
    public void setUp() {
        //err = System.out;
        log = getLog();
        ref = getRef();
        
        try {
            testedComponent = getTestedComponent();
        }catch(Exception exc){
            exc.printStackTrace(log);
            fail("Test failed: " + exc.getMessage());
        }
        
    }
    
    /**
     *  Run the test.
     */
    public void testAccessibility(){
        try{
            settingsFileName = setSettingsFile();
            TestSettings settings = AccessibilityTestCase.initializeSettings(settingsFileName);
            
            settings.setWindowTitle(getWindowTitle(testedComponent));
            
            if (testedComponent != null){
                
                AccessibilityTester tester = new AccessibilityTester(testedComponent, settings);
                
                tester.startTests();
                
                java.io.Writer xml_writer = null;
                java.io.Writer txt_writer = null;
                
                TextReport text_report = new TextReport(tester, settings);
                
                String report_filename = setResultFile();
                
                if (report_filename != null) {
                    try {
                        xml_writer = new java.io.FileWriter(new java.io.File(report_filename + ".xml"));
                    } catch(Exception e){
                        e.printStackTrace(log);
                        xml_writer = new java.io.PrintWriter(log);
                    }
                    try {
                        txt_writer = new java.io.FileWriter(new java.io.File(report_filename + ".txt"));
                    } catch(Exception e){
                        e.printStackTrace(log);
                        txt_writer = new java.io.PrintWriter(log);
                    }
                    
                    XMLReport report = new XMLReport(tester, settings);
                    report.getReport(xml_writer);
                    
                    text_report.getReport(txt_writer);
                }else {
                    text_report.getReport(new PrintWriter(ref));
                }
                
                compareReferenceFiles();
                
            }else {
                fail("Tested component is NULL.");
            }
        }catch(Exception exc){
            exc.printStackTrace(log);
            fail("Test failed: " + exc.getMessage());
        }
    }
    
    /** Set result file.
     * @return  path to result file */
    public String setResultFile(){
        String name = System.getProperty("a11ytest.result_dir");
        if(name == null)
            return null;
        return name +  File.separator + "a11y_" + this.getClass().getName();
    }
    
    
    /** Get window title
     * @param component
     * @return  window title */
    private String getWindowTitle(Component component){
        if(component instanceof Frame) {
            return ((Frame)component).getTitle();
        }else if (component instanceof JInternalFrame) {
            return ((JInternalFrame)component).getTitle();
        }else if (component instanceof Dialog) {
            return ((Dialog)component).getTitle();
        }else
            return "";
    }
    
    
    /** Set path to settings file
     * @return  path to settings file */
    public abstract String setSettingsFile();
    
    
    /** Get tested component. This method must return tested component. It's called from from <code>setUp</code>.
     * If this method return null, test <b>failed</b>.
     * @return  tested component */
    public abstract Component getTestedComponent();
}
