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

package org.netbeans.a11y;

import org.netbeans.a11y.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.File;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JInternalFrame;

import org.netbeans.junit.NbTestCase;
//import org.netbeans.junit.NbTestSuite;


/**
 *  Abstract Accessibility test usable for XTest
 *  @author Marian.Mirilovic@Sun.com
 */
public abstract class XTestAccessibilityTestCase extends NbTestCase {
    
    protected PrintStream ref;
    protected PrintStream log;

    private String settingsFileName;
    private TestSettings settings;
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
                    }
                    catch(Exception e){
                        e.printStackTrace(log);
                        xml_writer = new java.io.PrintWriter(log);
                    }
                    try {
                        txt_writer = new java.io.FileWriter(new java.io.File(report_filename + ".txt"));
                    }
                    catch(Exception e){
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
