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

package org.netbeans.a11y;

import java.io.*;
import java.awt.*;
import java.util.*;
import javax.accessibility.*;

/**
 *  A report generator for AccessibilityTester that will show the create a
 *  plain text report with the class, name, description and position of any
 *  component with accessibility problems.
 *
 *  @author Tristan Bonsall
 */
public class TextReport extends AccessibilityTester.ReportGenerator{

  /**
   *  Create a TextReport for an AccessibilityTester.
   *
   *  @param tester the AccesibilityTester
   */
  public TextReport(AccessibilityTester tester){

    super(tester);
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

    String classname = comp.getClass().toString();
    if (classname.startsWith("class ")){
 
      classname = classname.substring(6);
    }
    out.print("Class: " + classname);

    AccessibleContext ac = comp.getAccessibleContext();
    if (ac != null){

      String name = ac.getAccessibleName();
      if (name != null){

        out.print(" Name: " + name);
      }

      String desc = ac.getAccessibleDescription();
      if (desc != null){

        out.print(" Description: " + desc);
      }
    }

    try{

      Point top = getTestTarget().getLocationOnScreen();
      Point child = comp.getLocationOnScreen();
      out.print(" Position: [" + (child.x - top.x) + "," + (child.y - top.y) + "]");
    }
    catch(Exception e){}

    out.println();
  }
}