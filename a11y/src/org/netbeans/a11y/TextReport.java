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

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.awt.Component;
import java.awt.Point;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;

import javax.accessibility.AccessibleContext;

/**
 *  A report generator for AccessibilityTester that will show the create a
 *  plain text report with the class, name, description and position of any
 *  component with accessibility problems.
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.com
 */
public class TextReport extends AccessibilityTester.ReportGenerator{
    
    /** Create a TextReport for an AccessibilityTester.
     *  @param tester the AccesibilityTester */
    public TextReport(AccessibilityTester tester, TestSettings set){
        super(tester, set);
    }
    
    /**
     *  Generate the text report from the tests.
     *  <p>
     *  Reports are sent to a Writer, for instance:
     *  <ul>
     *  <li><code>new java.io.Writer(System.out)</code> will write to std out
     *  <li><code>new java.io.Writer(new java.io.FileWriter(new java.io.File("result.xml")))</code> will write to the file result.txt
     *  </ul>
     *
     *  @param out a Writer to send the report to
     */
    public void getReport(Writer writer){
        boolean fileWriter=false;
        
        if(writer instanceof FileWriter){
            fileWriter = true;
        }
        
        //System.err.println("============================== TextReport.getReport()");
        //Thread.dumpStack();
        
        
        PrintWriter out = getPrintWriter(writer);
        
        if(fileWriter) {
            //   out.println("<TestResults><![CDATA[");
            out.println("<HTML><HEAD>");
            out.println("<TITLE>Output from UIAccessibilityTester for window with title : "+testSettings.getWindowTitle()+" </TITLE>");
            out.println("</HEAD>");
            out.println("<BODY>");
            out.println("<PRE>");
            out.println("Results of Accessibility test, window with title \""+testSettings.getWindowTitle()+"\"");
        }else{
            out.println("Results of Accessibility test");
        }
        
        out.println();
        
        out.println("\n Doesn't implement Accessible :");
        printComponents(getNoAccess(),out, testSettings.accessibleInterface);
        
        out.println("\n No Accessible name :");
        printComponents(getNoName(),out, testSettings.AP_accessibleName);
        
        out.println("\n No Accessible description :");
        printComponents(getNoDesc(),out, testSettings.AP_accessibleDescription);
        
        out.println("\n Label with LABEL_FOR not set :");
        printComponents(getNoLabelFor(),out, testSettings.AP_labelForSet);
        
        out.println("\n Components with no LABEL_FOR pointing to it :");
        printComponents(getNoLabelForPointing(),out, testSettings.AP_noLabelFor);
        
        out.println("\n Components with no mnemonic :");
        printComponents(getNoMnemonic(),out, testSettings.AP_mnemonics);
        
        out.println("\n Components with wrong mnemonic (mnemonic isn't ASCII , label doesn't contain mnemonic):");
        printComponents(getWrongMnemonic(),out, testSettings.AP_mnemonics);
        
        Hashtable hs = getMnemonicConflict();
        if(!hs.isEmpty()){
            out.println("\n Components with potential mnemonics conflict:");

            Enumeration enum = hs.keys();
        
            while(enum.hasMoreElements()) {
                String key = (String)enum.nextElement();
                char k = (char) Integer.parseInt(key);
                out.println(" - components with mnemonic '"+k+"' :");
                printComponents((HashSet)hs.get(key),out, testSettings.AP_mnemonics);
            }
                
        }

        out.println("\n Components not reachable with tab traversal :");
        printComponents(getNotTraversable(),out, testSettings.tabTraversal);
        
        if(fileWriter){
            out.println("</PRE>");
            out.println("</BODY>");
            out.println("</HTML>");
            //  out.println("]]> </TestResults>");
            //  out.println("</Test>");
        }
        
        out.flush();
        /* Commented out because closing the writer for OutputWindow in NetBeans */
        /* erases the contents of the window. Uncomment in future if this changes */
        //out.close();
    }
    
    private void printComponents(HashSet components, PrintWriter pw, boolean tested) {
        if(!tested){
            pw.println("   - not tested.");
        } else {
            if (components.size() > 0){
                LinkedList componentsString = new LinkedList();
                
                Iterator i = components.iterator();
                while(i.hasNext()){
                    Component comp = (Component)(i.next());
                    componentsString.add(printComponentDetails(comp));
                }
                
                Collections.sort(componentsString);
                
                Iterator is = componentsString.iterator();
                while(is.hasNext()){
                    pw.println(is.next());
                }
                
                pw.println();
            }else{
                pw.println("   - none.");
            }
        }
    }
    
    
    /**
     *  Output the details of a component to the writer in text.
     */
    private String printComponentDetails(Component comp){
        StringBuffer componentPrintString = new StringBuffer("");
        
        String classname = comp.getClass().toString();
        if (classname.startsWith("class ")){
            classname = classname.substring(6);
        }
        componentPrintString.append("   Class: " + classname);
        
        if(printName || printDescription){
            componentPrintString.append(" { ");
            AccessibleContext ac = comp.getAccessibleContext();
            
            if (ac != null){
                
                String name = ac.getAccessibleName();
                if ((name != null) && printName){
                    componentPrintString.append(" " + name);
                }
                
                componentPrintString.append(" | ");
                
                String desc = ac.getAccessibleDescription();
                if ((desc != null) && printDescription){
                    componentPrintString.append(" " + desc);
                }
            }
            componentPrintString.append(" } ");
        }
        
        if(printPosition) {
            try{
                Point top = getTestTarget().getLocationOnScreen();
                Point child = comp.getLocationOnScreen();
                componentPrintString.append(" [" + (child.x - top.x) + "," + (child.y - top.y) + "]");
            } catch(Exception e){}
        }
        
        //componentPrintString.append("\n");
        
        return componentPrintString.toString();
    }
    
}