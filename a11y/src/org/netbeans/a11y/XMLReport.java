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
 *  XML report with the class, name, description and position of any
 *  component with accessibility problems.
 *
 *  @author Tristan Bonsall
 */
public class XMLReport extends AccessibilityTester.ReportGenerator{

  /**
   *  Create a XMLReport for an AccessibilityTester.
   *
   *  @param tester the AccesibilityTester
   */
  public XMLReport(AccessibilityTester tester){

    super(tester);
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

    out.println("<accessibilitytest>");

    if (getNoAccess().size() > 0){

      out.println("<noimplement>");

      Iterator i = getNoAccess().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</noimplement>");
    }

    if (getNoName().size() > 0){

      out.println("<noname>");

      Iterator i = getNoName().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</noname>");
    }

    if (getNoDesc().size() > 0){

      out.println("<nodesc>");

      Iterator i = getNoDesc().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</nodesc>");
    }

    if (getNoLabelFor().size() > 0){

      out.println("<labelfornotset>");
        
      Iterator i = getNoLabelFor().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</labelfornotset>");
    }

    if (getNoLabelForPointing().size() > 0){

      out.println("<nolabelfor>");

      Iterator i = getNoLabelForPointing().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</nolabelfor>");
    }

    if (getNoMnemonic().size() > 0){

      out.println("<nomnemonic>");

      Iterator i = getNoMnemonic().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</nomnemonic>");
    }

    if (getNotTraversable().size() > 0){

      out.println("<nottraversable>");

      Iterator i = getNotTraversable().iterator();
      while(i.hasNext()){

        Component comp = (Component)(i.next());
        printComponentDetails(out, comp);
      }

      out.println("</nottraversable>");
    }

    out.println("</accessibilitytest>");

    out.flush();

    /* Commented out because closing the writer for OutputWindow in NetBeans */
    /* erases the contents of the window. Uncomment in future if this changes */
    //out.close();
  }

  /**
   *  Output the details of a component to the writer, in XML.
   */
  private void printComponentDetails(PrintWriter out, Component comp){

    out.println("<component>");

    String classname = comp.getClass().toString();
    if (classname.startsWith("class ")){
 
      classname = classname.substring(6);
    }
    out.println("<class>" + classname + "</class>");

    AccessibleContext ac = comp.getAccessibleContext();
    if (ac != null){

      String name = ac.getAccessibleName();
      if (name != null){

        out.println("<name>" + name + "</name>");
      }

      String desc = ac.getAccessibleDescription();
      if (desc != null){

        out.println("<desc>" + desc + "</desc>");
      }
    }

    try{

      Point top = getTestTarget().getLocationOnScreen();
      Point child = comp.getLocationOnScreen();
      out.println("<position>[" + (child.x - top.x) + "," + (child.y - top.y) + "]</position>");
    }
    catch(Exception e){}

    out.println("</component>");
  }
}