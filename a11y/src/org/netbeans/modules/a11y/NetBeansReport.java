/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
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
  public NetBeansReport(AccessibilityTester tester, VisualReplicator replicator){

    super(tester);
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

    if (getNoAccess().size() > 0){

      out.println("Doesn't implement Accessible");

      Iterator i = getNoAccess().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    if (getNoName().size() > 0){

      out.println("No Accessible name");

      Iterator i = getNoName().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    if (getNoDesc().size() > 0){

      out.println("No Accessible description");

      Iterator i = getNoDesc().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    if (getNoLabelFor().size() > 0){

      out.println("Label with LABEL_FOR not set");
        
      Iterator i = getNoLabelFor().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    if (getNoLabelForPointing().size() > 0){

      out.println("TextField with no LABEL_FOR pointing to it");

      Iterator i = getNoLabelForPointing().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    if (getNoMnemonic().size() > 0){

      out.println("AbstractButton with no mnemonic");

      Iterator i = getNoMnemonic().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }
 
    if (getNotTraversable().size() > 0){

      out.println("Components not reachable with tab traversal");

      Iterator i = getNotTraversable().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println();
    }

    out.flush();

    /* Commented out because closing the writer for OutputWindow in NetBeans */
    /* erases the contents of the window. Uncomment in future if this changes */
    //out.close();
  }

  /**
   *  Output the details of a component to the writer in text.
   */
  private void printComponentDetails(PrintWriter out, Component comp){

    RADComponent metacomp = replicator.getMetaComponent(comp);
    if (metacomp != null){

      String classname = comp.getClass().toString();
      if (classname.startsWith("class ")){
 
        classname = classname.substring(6);
      }
      if (out instanceof OutputWriter){

        listener.addComponent(metacomp.getName(), metacomp);
        try{

          ((OutputWriter)out).println(classname + " " + metacomp.getName(), listener);
        }
        catch(IOException e){

          out.println(classname + " " + metacomp.getName());
        }
      }
      else{

        out.println(classname + " " + metacomp.getName());
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
          }
          catch (Exception e){}
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