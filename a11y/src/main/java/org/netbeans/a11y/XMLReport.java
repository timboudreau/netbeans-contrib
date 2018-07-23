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
import java.awt.Point;

import java.io.PrintWriter;
import java.io.Writer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.accessibility.AccessibleContext;


/**
 *  A report generator for AccessibilityTester that will show the create a
 *  XML report with the class, name, description and position of any
 *  component with accessibility problems.
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.Com
 */
public class XMLReport extends AccessibilityTester.ReportGenerator{
    
    /**
     *  Create a XMLReport for an AccessibilityTester.
     *
     *  @param tester the AccesibilityTester
     */
    public XMLReport(AccessibilityTester tester, TestSettings set){
        super(tester,set);
    }
    
    /**
     *  Generate the XML report from the tests.
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
        
        PrintWriter out = getPrintWriter(writer);
        
        /* Write header */
        out.println("<?xml version=\"1.0\"?>");
        out.println("<?xml-stylesheet type=\"text/xsl\" href=\"access.xsl\"?>");
        out.println("<!DOCTYPE accessibilitytest SYSTEM \"a11ytest.dtd\">");
        
        out.println();
        
        out.println("<TestResults>");
        
        out.println("\t <window>");
        out.println("\t\t <title>"+testSettings.getWindowTitle()+"</title>");
        out.println("\t </window>");
        
        printComponents(getNoAccess(),out, "noimplement", testSettings.accessibleInterface);
        printComponents(getNoName(), out, "noname", testSettings.AP_accessibleName);
        printComponents(getNoDesc(), out, "nodesc", testSettings.AP_accessibleDescription);
        printComponents(getNoLabelFor(),out, "labelfornotset", testSettings.AP_labelForSet);
        printComponents(getNoLabelForPointing(),out,"nolabelfor", testSettings.AP_noLabelFor);
        printComponents(getNoMnemonic(),out,"nomnemonic", testSettings.AP_mnemonics);
        printComponents(getWrongMnemonic(),out,"wrongmnemonic", testSettings.AP_mnemonics);
        
        Hashtable hs = getMnemonicConflict();
        if(!hs.isEmpty()){
            out.println("\t <potentialmnemonicsconflict>");
            
            Enumeration enumer = hs.keys();
            
            while(enumer.hasMoreElements()) {
                String key = (String)enumer.nextElement();
                char k = (char) Integer.parseInt(key);
                printComponents((HashSet)hs.get(key),out,"mnemonicconflict mnemonic=\""+k+"\"", testSettings.AP_mnemonics);
            }
            
            out.println("\t </potentialmnemonicsconflict>");
            
        }
        
        
        
        printComponents(getNotTraversable(), out, "nottraversable", testSettings.tabTraversal);
        if(Boolean.getBoolean("a11ytest.name")) {
            printComponents(getNoComponentName(), out, "nocomponentname", testSettings.test_name);
        }
        
        out.println("</TestResults>");
        
        out.flush();
        
        /* Commented out because closing the writer for OutputWindow in NetBeans */
        /* erases the contents of the window. Uncomment in future if this changes */
        //out.close();
    }
    
    
    private void printComponents(HashSet components, PrintWriter pw, String tag, boolean tested) {
        pw.println("\t <"+tag+">");
        if(!tested){
            pw.println("\t\t <not_tested/>");
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
                
            }else{
                pw.println("\t\t <none/>");
            }
        }
        
        pw.println("\t </"+tag+">");
    }
    
    /**
     *  Output the details of a component to the writer, in XML.
     */
    private String printComponentDetails(Component comp){
        StringBuffer componentPrintString = new StringBuffer("");
        
        componentPrintString.append("\t\t <component>\n");
        
        String classname = comp.getClass().toString();
        if (classname.startsWith("class ")){
            classname = classname.substring(6);
        }
        componentPrintString.append("\t\t\t <class>" + classname + "</class>\n");
        
        
        AccessibleContext ac = comp.getAccessibleContext();
        if (ac != null){
            
            if(printName){
                String name = ac.getAccessibleName();
                if (name != null){
                    componentPrintString.append("\t\t\t <name>" + name + "</name>\n");
                }
            }
            
            if(printDescription){
                String desc = ac.getAccessibleDescription();
                
                if (desc != null){
                    componentPrintString.append("\t\t\t <desc>" + desc + "</desc>\n");
                }
            }
        }
        
        if(printPosition) {
            try{
                Point top = getTestTarget().getLocationOnScreen();
                Point child = comp.getLocationOnScreen();
                componentPrintString.append("\t\t\t <position>[" + (child.x - top.x) + "," + (child.y - top.y) + "]</position>\n");
            } catch(Exception e){}
        }
        
        componentPrintString.append("\t\t </component>");
        
        return componentPrintString.toString();
    }
    
}