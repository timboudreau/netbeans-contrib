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

import java.awt.*;

/**
 *  An abstract class defining a test case for accessibility tests.
 *
 *  @author Tristan Bonsall
 */
public abstract class AccessibilityTestCase{

  /**
   *  Run the test.
   */
  public void execute(){

    AccessibilityTestOptions options = new AccessibilityTestOptions(new javax.swing.JFrame(), true);
    options.show();

    int tests = options.getTests();

    if ((tests == 0) || !(options.doProperties() || options.doTraversal())) return;

    Component comp = setUp();

    if (comp != null){

      System.setProperty("a11ytest.excludes", options.getExcludes());
      AccessibilityTester tester = new AccessibilityTester(comp, options.getTests());
      if (options.doProperties()) tester.testProperties();
      if (options.doTraversal()) tester.testTraversal(comp);

      String filename = options.getFilename();

      if (filename != null){

        try{

          java.io.FileWriter writer = new java.io.FileWriter(filename);
          XMLReport report = new XMLReport(tester);
          report.getReport(writer);
        }
        catch (java.io.IOException e){

          // Do something about it or ignore it?
          // Could just default to Output Window?
        }
      }
      else{

        TextReport report = new TextReport(tester);
        report.getReport(new java.io.PrintWriter(System.out));
      }
    }
    tearDown();
  }

  /**
   *  Initialize the test.
   *  <p>
   *  The implementation of this method should create the component and
   *  make it visible if necessary.
   *
   *  @return a reference to the component to be tested, or null if setup failed
   */
  public abstract Component setUp();

  /**
   *  Clean up after performing the test.
   *  <p>
   *  Typically, this will be closing dialogs, etc. This method
   *  should execute cleanly even if {@link #setUp() setUp} failed.
   */
  public abstract void tearDown();

  /**
   *  Get a description of this test.
   *
   *  @return the description
   */
  public String getDescription(){

    return "No description supplied";
  }

  /**
   *  The writer that the report is sent to. The default value is System.out
   *  but subclasses can specify their own.
   */
  protected java.io.PrintWriter writer = new java.io.PrintWriter(System.out);
}