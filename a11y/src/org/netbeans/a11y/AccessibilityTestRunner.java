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
 *  Runs an Accessibility test.
 *
 *  The test is contained in an instance of AccessibilityTestCase. The full
 *  class name is read from a11ytest.test_case or, if that is null, from args[0]
 *  and an instance of this test is created and run.
 *
 *  @author Tristan Bonsall
 */
public class AccessibilityTestRunner{

  /**
   *  Run the test. First, the system property a11ytest.test_case is checked for a class name.
   *  If this returns null, args[0] is checked. If this is also null, the method returns,
   *  else the test is instantiated and run.
   */
  public static void main(String[]args){

    String testClassname = System.getProperty("a11ytest.test_case");

    if ((testClassname == null) && (args.length > 0)){
 
      testClassname = args[0];
    }

    if (testClassname != null){

      Class testClass =  null;
      try{

        testClass = Class.forName(testClassname);
      }
      catch(ClassNotFoundException e){

        AccessibilityTestLogger.append(" Couldn't find class " + testClassname);
      }

      if (testClass != null){

        Object object =  null;
        try{

          object = testClass.newInstance();
        }
        catch(InstantiationException e){

          AccessibilityTestLogger.append(" Couldn't instantiate test " + testClassname);
        }
        catch(IllegalAccessException e){

          AccessibilityTestLogger.append(" Couldn't instantiate test " + testClassname);
        }

        if ((object != null) && (object instanceof AccessibilityTestCase)){

          AccessibilityTestCase testCase = (AccessibilityTestCase)object;

          Component comp = testCase.setUp();

          if (comp != null){

            AccessibilityTester tester = new AccessibilityTester(comp);
            
            tester.testProperties();
            tester.testTraversal(comp);
            java.io.Writer writer = null;
            try{

              writer = new java.io.FileWriter(new java.io.File(System.getProperty("a11ytest.result_file")));
            }
            catch(Exception e){

              writer = new java.io.PrintWriter(System.out);
            }
            XMLReport report = new XMLReport(tester);
            report.getReport(writer);
            AccessibilityTestLogger.append(" " + testClassname + " successful");
          }
          testCase.tearDown();
        }
      }
    }

    /* Is this really necessary? It is required by XTest to close NetBeans after the test is complete */

    System.setProperty("test.finished", "true");
  }
}