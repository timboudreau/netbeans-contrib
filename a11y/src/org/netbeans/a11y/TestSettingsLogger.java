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

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.accessibility.*;

/** A properties logger for AccessibilityTester that will show the create a
 *  xml log with properties for result of AccessibilityTester test.
 *  @author Marian.Mirilovic@Sun.com */
public class TestSettingsLogger extends AccessibilityTester.ReportGenerator{
    
    private static TestSettings readedSettings;
    
    /** Create a log with test settings for an AccessibilityTester.
     *  @param tester the AccesibilityTester */
    public TestSettingsLogger(AccessibilityTester t, TestSettings set){
        super(t, set);
    }
    
    
    /** Generate settings
     *  @param out a Writer to send the report to */
    public void getReport(Writer writer){
        PrintWriter out = getPrintWriter(writer);
        
        String correctWindowTitle = testSettings.getCorrectedWindowTitle();
        
        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/xsl\" href=\"access.xsl\"?>");
        out.println("<!DOCTYPE accessibilitytest SYSTEM \"a11ytest.dtd\">");
        
        out.println("<Test>");
        out.println("<TestSettings>");
        out.println("\t <TestSetting name=\"window_title\" value=\""+correctWindowTitle+"\"/>");
        out.println("\t <TestSetting name=\"os\" value=\""+System.getProperty("os.name")+"-"+System.getProperty("os.version")+"-"+System.getProperty("os.arch")+"\"/>");
        out.println("\t <TestSetting name=\"jdk_version\" value=\""+System.getProperty("java.version")+"\"/>");
        out.println("</TestSettings>");
        
        out.println("<TestCases>");
        
        out.println("\t <TestCase name=\"implement_accessible_interface\" value=\""+testSettings.accessibleInterface+"\">");
        out.println("\t\t <Options>");
        out.println("\t\t\t <Option name=\"showingOnly\" value=\""+testSettings.AI_showingOnly+"\"/>");
        out.println("\t\t </Options>");
        out.println("\t </TestCase>");
        
        out.println("\t <TestCase name=\"accessibility_properties\" value=\""+testSettings.accessibleProperties+"\">");
        out.println("\t\t <Options>");
        out.println("\t\t\t <Option name=\"showingOnly\" value=\""+testSettings.AP_showingOnly+"\"/>");
        out.println("\t\t\t <Option name=\"focusTraversableOnly\" value=\""+testSettings.AP_focusTraversableOnly+"\"/>");
        out.println("\t\t </Options>");
        
        out.println("\t\t <Properties>");
        out.println("\t\t\t <Property name=\"accessibilityName\" value=\""+testSettings.AP_accessibleName+"\"/>");
        out.println("\t\t\t <Property name=\"accessibilityDescription\" value=\""+testSettings.AP_accessibleDescription+"\"/>");
        out.println("\t\t\t <Property name=\"labelFor\" value=\""+testSettings.AP_labelForSet+"\"/>");
        
        out.println("\t\t\t <Property name=\"componentsLabelFor\" value=\""+testSettings.AP_noLabelFor+"\">");
        out.println("\t\t\t <SubProperties>");
        out.println("\t\t\t\t <SubProperty name=\"clf_text\" value=\""+testSettings.AP_nlf_text+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"clf_table\" value=\""+testSettings.AP_nlf_table+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"clf_list\" value=\""+testSettings.AP_nlf_list+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"clf_tree\" value=\""+testSettings.AP_nlf_tree+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"clf_tab\" value=\""+testSettings.AP_nlf_tabbedPane+"\"/>");
        out.println("\t\t\t </SubProperties>");
        out.println("\t\t\t </Property>");
        
        out.println("\t\t\t <Property name=\"mnemonics\" value=\""+testSettings.AP_mnemonics+"\">");
        out.println("\t\t\t <SubProperties>");
        out.println("\t\t\t\t <SubProperty name=\"abstractButtons\" value=\""+testSettings.AP_m_abstractButtons+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"labelsWithLabelFor\" value=\""+testSettings.AP_m_label+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"defaultCancel\" value=\""+testSettings.AP_m_defaultCancel+"\"/>");
        out.println("\t\t\t\t <SubProperty name=\"cancelString\" value=\""+testSettings.getCancelLabel()+"\"/>");
        out.println("\t\t\t </SubProperties>");
        out.println("\t\t\t </Property>");
        out.println("\t\t </Properties>");
        out.println("\t </TestCase>");
        
        
        out.println("\t <TestCase name=\"focusTraversable\" value=\""+testSettings.tabTraversal+"\">");
        out.println("\t\t <Options>");
        out.println("\t\t\t <Option name=\"showingOnly\" value=\""+testSettings.TT_showingOnly+"\"/>");
        out.println("\t\t </Options>");
        out.println("\t </TestCase>");
        
        out.println("</TestCases>");
        
        out.println("<ExcludedClasses>");
        HashSet excludedClasses = testSettings.getExcludedClasses();
        if (excludedClasses.size() > 0){
            Iterator i = excludedClasses.iterator();
            while(i.hasNext()){
                out.println("\t <ExcludedClass name=\""+i.next().toString()+"\"/>");
            }
        }
        out.println("</ExcludedClasses>");
        
        out.println("<TestResultsSettings>");
        out.println("\t <TestResult name=\"storeToXML\" value=\""+testSettings.storeToXML+"\"/>");
        out.println("\t <TestResult name=\"printName\" value=\""+printName+"\"/>");
        out.println("\t <TestResult name=\"printDescription\" value=\""+printDescription+"\"/>");
        out.println("\t <TestResult name=\"printPosition\" value=\""+printPosition+"\"/>");
        out.println("</TestResultsSettings>");
        
        out.println("</Test>");
        out.println();
        
        out.flush();
        /* Commented out because closing the writer for OutputWindow in NetBeans */
        /* erases the contents of the window. Uncomment in future if this changes */
        //out.close();
    }
    
    public static TestSettings readSettings(String fileName) throws java.io.IOException{
        readedSettings = new TestSettings();
        
        StringBuffer readedData = new StringBuffer("");
        BufferedReader bufRead = new BufferedReader(new FileReader(fileName));
        String line;
        
        while((line = bufRead.readLine()) != null){
            readedData.append(line);
        }
        
        String data = readedData.toString();
        
        String t = parseTestSetting(data,"window_title");
        readedSettings.setWindowTitle(t);

        String t1 = parseTestCase(data,"implement_accessible_interface");
        readedSettings.accessibleInterface = getValue(t1);
        readedSettings.AI_showingOnly = getValueOfOption(t1, "showingOnly");

        String t2 = parseTestCase(data,"accessibility_properties");
        readedSettings.accessibleProperties = getValue(t2);
        
        if(readedSettings.accessibleProperties){
            readedSettings.AP_showingOnly = getValueOfOption(t2, "showingOnly");
            readedSettings.AP_focusTraversableOnly= getValueOfOption(t2, "focusTraversableOnly");
        
            readedSettings.AP_accessibleName = getValueOfProperty(t2, "accessibilityName");
            readedSettings.AP_accessibleDescription = getValueOfProperty(t2, "accessibilityDescription");
            readedSettings.AP_labelForSet = getValueOfProperty(t2, "labelFor");
            readedSettings.AP_noLabelFor = getValueOfProperty(t2, "componentsLabelFor");
            readedSettings.AP_nlf_text= getValueOfSubProperty(t2, "clf_text");
            readedSettings.AP_nlf_table= getValueOfSubProperty(t2, "clf_table");
            readedSettings.AP_nlf_list= getValueOfSubProperty(t2, "clf_list");
            readedSettings.AP_nlf_tree= getValueOfSubProperty(t2, "clf_tree");
            readedSettings.AP_nlf_tabbedPane= getValueOfSubProperty(t2, "clf_tab");
            readedSettings.AP_mnemonics= getValueOfProperty(t2, "mnemonics");
            readedSettings.AP_m_abstractButtons= getValueOfSubProperty(t2, "abstractButtons");
            readedSettings.AP_m_label= getValueOfSubProperty(t2, "labelsWithLabelFor");
            readedSettings.AP_m_defaultCancel= getValueOfSubProperty(t2, "defaultCancel");
            readedSettings.setCancelLabel(getValueOfSubProperty_string(t2, "cancelString"));
        }else
            readedSettings.setAP(false);
        
        String t3 = parseTestCase(data,"focusTraversable");
        readedSettings.tabTraversal = getValue(t3);
        readedSettings.TT_showingOnly = getValueOfOption(t3, "showingOnly");

        String e = parseExcludedClasses(data);
        StringBuffer bufferEx = new StringBuffer();
        StringBuffer buffer = new StringBuffer(e);
        int counter = 0;
        while(buffer.length()>10 && counter<30){
            counter++;
            String cl = getValueOfExcluded(buffer.toString());
            if(cl != null){
                bufferEx.append(cl);
                bufferEx.append(readedSettings.excludedSeparator);
            }
            int end = buffer.toString().indexOf("/>") + "/>".length()+1;
            buffer.delete(0,end);
        }
        
        readedSettings.setExcludedClasses(bufferEx.toString());
        
        String r = parseTestResults(data);
        readedSettings.storeToXML = getValueOfTestResult(r, "storeToXML");
        readedSettings.report_name = getValueOfTestResult(r, "printName");
        readedSettings.report_description = getValueOfTestResult(r, "printDescription");
        readedSettings.report_position = getValueOfTestResult(r, "printPosition");
        
        return readedSettings;
    }

    private static String parseTestSetting(String data, String settingName) {
        return parse("<TestSetting name=\""+settingName+"\" value=\"", "\"", data);
    }
    
    private static String parseTestCase(String data, String testCaseName) {
        return parse("<TestCase name=\""+testCaseName, "</TestCase>", data);
    }
    
    private static String parseExcludedClasses(String data) {
        return parse("<ExcludedClasses>","</ExcludedClasses>", data);
    }

    private static String parseTestResults(String data) {
        return parse("<TestResultsSettings>","</TestResultsSettings>",data);
    }
    
    private static boolean getValueOfOption(String line, String optionName) {
        return makeBoolean(parse("<Option name=\""+optionName+"\" value=\"", "\"", line));
    }
    
    private static boolean getValueOfProperty(String line, String propertyName) {
        return makeBoolean(parse("<Property name=\""+propertyName+"\" value=\"", "\"", line));
    }
    
    private static boolean getValueOfSubProperty(String line, String subPropertyName) {
        return makeBoolean(parse("<SubProperty name=\""+subPropertyName+"\" value=\"", "\"", line));
    }

    private static String getValueOfSubProperty_string(String line, String subPropertyName) {
        return parse("<SubProperty name=\""+subPropertyName+"\" value=\"", "\"", line);
    }

    private static String getValueOfExcluded(String line) {
        return parse("<ExcludedClass name=\"", "\"", line);
    }
    
    private static boolean getValueOfTestResult(String line, String testResultPropertyName) {
        return makeBoolean(parse("<TestResult name=\""+testResultPropertyName+"\" value=\"", "\"", line));
    }
    
    private static String parse(String start, String end, String line) {
        int startNumber = line.indexOf(start);
        if(startNumber == -1) {
            readedSettings.setReadedCorrectly(false);
            return "";
        }
        startNumber += start.length();
        int endNumber = line.indexOf(end,startNumber);
        if(endNumber == -1) {
            readedSettings.setReadedCorrectly(false);
            return "";
        }
        String ret = line.substring(startNumber,endNumber);
        return ret;
    }
    
    private static boolean getValue(String s) {
        return makeBoolean(parse("value=\"","\"",s));
    }
    
    private static boolean makeBoolean(String s){
        if(s.equals("true"))
            return true;
        else
            return false;
    }
}