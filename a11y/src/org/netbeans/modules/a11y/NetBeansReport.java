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

package org.netbeans.modules.a11y;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.accessibility.*;
import org.netbeans.a11y.*;
import org.netbeans.modules.form.*;
import org.openide.windows.*;
import org.openide.nodes.*;

/**
 *  A report generator for AccessibilityTester that will show the variable name
 *  of components with accessbility problems.
 *
 *  @author Tristan Bonsall
 */
public class NetBeansReport extends AccessibilityTester.ReportGenerator{
    
    /**
     *  Create a NetBeansReport for an AccessibilityTester.
     *
     *  @param tester the AccesibilityTester
     */
    public NetBeansReport(AccessibilityTester tester, VisualReplicator replicator, TestSettings set){
        super(tester, set);
        this.replicator = replicator;
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
        PrintWriter out = getPrintWriter(writer);
        
        out.println("Results of Accessibility test");
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
        
        out.println("\n Components not reachable with tab traversal :");
        printComponents(getNotTraversable(),out, testSettings.tabTraversal);
        
        out.flush();
        
        /* Commented out because closing the writer for OutputWindow in NetBeans */
        /* erases the contents of the window. Uncomment in future if this changes */
        //out.close();
    }
    
    /**
     *  Output the details of a component to the writer in text.
     */
    private void printComponentDetails(PrintWriter out, Component comp){
        StringBuffer componentPrintString = new StringBuffer("");
        
        RADComponent metacomp = replicator.getMetaComponent(comp);
        if (metacomp != null){
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
            
            if (out instanceof OutputWriter){
                listener.addComponent(metacomp.getName(), metacomp);
                try{
                    ((OutputWriter)out).println(componentPrintString.toString(), listener);
                } catch(IOException e){
                    out.println(componentPrintString);
                }
            } else{
                out.println(classname + " " + metacomp.getName());
            }
        }
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
                    printComponentDetails(pw, comp);
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
     *  Implementation of OutputListener to allows selection of lines that represent
     *  components with problems to focus the actual component in the form designer.
     */
    private class ReportListener implements OutputListener{
        
        /**
         *  Add a mapping from the components variable name to the RADComponent.
         *
         *  @param key the variable name
         *  @param comp the RADComponent
         */
        public void addComponent(String key, RADComponent comp){
            if (components.get(key) == null){
                components.put(key, comp);
            }
        }
        
        /**
         *  Focus the component when selected.
         */
        public void outputLineSelected(OutputEvent ev){
            
            String text = ev.getLine();
            String name = text.substring(text.lastIndexOf(' ') + 1);
            RADComponent comp = (RADComponent)(components.get(name));
            if (comp != null){
                Node node = comp.getNodeReference();
                if (node != null){
                    try{
                        ComponentInspector.getInstance().getExplorerManager().setSelectedNodes(new Node[] {node});
                    } catch (Exception e){}
                }
            }
        }
        
        /**
         *  Not implemented.
         */
        public void outputLineAction(OutputEvent ev){}
        
        /**
         *  Not implemented.
         */
        public void outputLineCleared(OutputEvent ev){}
        
        private HashMap components = new HashMap();
    }
    
    private VisualReplicator replicator = null;
    private ReportListener listener = new ReportListener();
}