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

import javax.accessibility.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 *  AccessibilityTester assess the accessibility of GUI components.
 *  <p>
 *  To assess a component, {@link #testProperties() testProperties} will
 *  check the relevant properties of the component used when the <code>AccessibiltyTester</code>
 *  was constructed and any children of it (if it is a container).
 *  <p>
 *  To test whether components are reachable with the Tab key,
 *  {@link #testTraversal(Component) testTraversal} will try to visit every focus
 *  traversable component by simulating Tab keypress events.
 *  <p>
 *  System properties used by AccessibiltyTester
 *  <ul>
 *  <li>a11ytest.tab_timeout - timeout (in ms) for cancelling the tab traversal (default = 15000)
 *  <li>a11ytest.tab_delay - the delay (in ms) between tab key presses (default = 200)
 *  <li>a11ytest.excludes - a semicolon separated list of component classes to ignore in the test
 *  </ul>
 *
 *  @author Tristan Bonsall
 */
public class AccessibilityTester{

  /**
   *  Check if components implement Accessible.
   */
  public static final int IMPLEMENTS_ACCESSIBLE = 1;

  /**
   *  Check whether components have an accessible name.
   */
  public static final int ACCESSIBLE_NAME = 2;

  /**
   *  Check whether components have an accessible description.
   */
  public static final int ACCESSIBLE_DESC = 4;

  /**
   *  Check whether the LABEL_FOR value is set for all JLabels.
   */
  public static final int LABEL_FOR_SET = 8;

  /**
   *  Check whether every JTextField has a JLabel whose LABEL_FOR value
   *  is set to that JTextField.
   */
  public static final int NO_LABEL_FOR = 16;

  /**
   *  Check each AbstractButton has a mnemonic.
   */
  public static final int MNEMONIC = 32;

  /**
   *  In Tab traversal, only test showing components
   */
  public static final int SHOWING_ONLY = 64;

  private int tests = IMPLEMENTS_ACCESSIBLE | ACCESSIBLE_NAME | ACCESSIBLE_DESC
                     | LABEL_FOR_SET | NO_LABEL_FOR | MNEMONIC | SHOWING_ONLY;

  /**
   *  Create a new AccessibilityTester for the specified component.
   *  <p>
   *  By default, all tests are performed. To specify which tests to
   *  do, use {@link #setTests(int) setTests} or the constructor
   *  {@link #AccessibilityTester(Component, int) AccessibilityTester(Component, int)}
   *
   *  @param component the component to check
   */
  public AccessibilityTester(Component component){

    this(component, IMPLEMENTS_ACCESSIBLE | ACCESSIBLE_NAME | ACCESSIBLE_DESC
                   | LABEL_FOR_SET | NO_LABEL_FOR | MNEMONIC | SHOWING_ONLY);
  }

  /**
   *  Create a new AccessibilityTester for the specified component
   *  to perform the specified tests.
   *
   *  @param component the component to check
   *  @param tests the tests to perform
   */
  public AccessibilityTester(Component component, int test){

    parent = component;
    tests = test;

    TAB_TRAVERSAL_TIME_OUT = 15000;

    try{

      TAB_TRAVERSAL_TIME_OUT = new Long(System.getProperty("a11ytest.tab_timeout", "15000")).longValue();
    }
    catch(Exception e){

      // Ignore and use default value
    }

    TAB_TRAVERSAL_DELAY = 200;

    try{

      TAB_TRAVERSAL_DELAY = new Long(System.getProperty("a11ytest.tab_delay", "200")).longValue();
    }
    catch(Exception e){

      // Ignore and use default value
    }

    String excludes = System.getProperty("a11ytest.excludes");
    if (excludes != null){

      StringTokenizer st = new StringTokenizer(excludes, ";");

      while (st.hasMoreTokens()){

        excludedClasses.add(st.nextToken());
      }
    }
  }

  /**
   *  Set which tests should be performed. This value should be the
   *  OR of the required tests:
   *  <ul>
   *  <li>{@link #IMPLEMENTS_ACCESSIBLE IMPLEMENTS_ACCESSIBLE}
   *  <li>{@link #ACCESSIBLE_NAME ACCESSIBLE_NAME}
   *  <li>{@link #ACCESSIBLE_DESC ACCESSIBLE_DESC}
   *  <li>{@link #LABEL_FOR_SET LABEL_FOR_SET}
   *  <li>{@link #NO_LABEL_FOR NO_LABEL_FOR}
   *  <li>{@link #MNEMONIC MNEMONIC}
   *  <li>{@link #SHOWING_ONLY SHOWING_ONLY}
   *  </ul>
   *
   *  @param i the tests to perform
   */
  public void setTests(int i){

    tests = i;
  }

  /**
   *  Assess the accessibility properties of the component.
   *  <p>
   *  If the component is null, the method returns.
   *  If the component is a container, all of the components in
   *  the container are assessed for their accessibility recursively.
   */
  public void testProperties(){

    if (parent == null){

      return;
    }

    tests(parent);
    if ((tests & NO_LABEL_FOR) != 0) checkLabelTextFieldPairs();
  }

  /**
   *  The actual test method, which is called recursively.
   */
  private void tests(Component comp){

    /* Test any children of the component */

    if (comp instanceof Container){

      Container container = (Container)comp;
      Component[] children = container.getComponents();

      for (int c = 0; c < children.length; c++){

        tests(children[c]);
      }
    }
 
    if (((tests & SHOWING_ONLY) != 0) && !comp.isShowing()){

      return;
    }

    /* Check if class is excluded from the test */

    String classname = comp.getClass().toString();
    if (classname.startsWith("class ")){
 
      classname = classname.substring(6);
    }
    if (excludedClasses.contains(classname)){

      return;
    }

    if (comp instanceof Accessible){

      /* Check AccessibleContext of component */

      AccessibleContext ac = comp.getAccessibleContext();
      if (ac != null){

        String name = ac.getAccessibleName();
        if (((tests & ACCESSIBLE_NAME) != 0) && (name == null)){

          noName.add(comp);
        }

        String desc = ac.getAccessibleDescription();
        if (((tests & ACCESSIBLE_DESC) != 0) && (desc == null)){

          noDesc.add(comp);
        }
      }
    }
    else if ((tests & IMPLEMENTS_ACCESSIBLE) != 0){

      noAccess.add(comp);
    }

    /* Check LABEL_FOR is set for JLabels */

    if (((tests & LABEL_FOR_SET) != 0) && (comp instanceof JLabel)){

      labels.add(comp);

      Component labelFor = ((JLabel)comp).getLabelFor();
      if (labelFor == null){

        noLabelFor.add(comp);
      }
    }

    if (((tests & NO_LABEL_FOR) != 0) && (comp instanceof JTextField)){

      textFields.add(comp);
    }

    /* Check AbstractButtons have mnemonics */

    if (((tests & MNEMONIC) != 0) && (comp instanceof AbstractButton)){

      AbstractButton button = (AbstractButton)comp;
      if (button.getMnemonic() == 0){

        noMnemonic.add(comp);
      }
    }
  }

  /**
   *  Check that every JTextField has an associated JLabel
   *  whose LABEL_FOR is set to that JTextField.
   */
  private void checkLabelTextFieldPairs(){

    Iterator i = textFields.iterator();
    while(i.hasNext()){

      JTextField textField = (JTextField)(i.next());

      Iterator j = labels.iterator();
      while(j.hasNext()){

        Component labelFor = ((JLabel)(j.next())).getLabelFor();
        if ((labelFor != null) && (labelFor == textField)){

          i.remove();
          break;
        }
      }
    }
  }

  /**
   *  Use Tab traversal from the specified component to check which
   *  components can be reached without the mouse.
   *  <p>
   *  A custom focus listener catches the FOCUS_GAINED events and
   *  adds the focused component to a list. The test terminates when a
   *  component gains the keyboard focus that is already in the list,
   *  hence it has already been visited.
   *  <p>
   *  This list of components is then removed from the list of traversable
   *  components. Any components left in the list at this point are not reachable
   *  by Tab traversal.
   *
   *  @param comp the component to begin traversal from
   */
  public void testTraversal(Component comp){

    if (parent == null){

      return;
    }

    /* Build list of focus traversable components in the parent component */
    /* This is only done once, unless resetReport is called */

    if (!tabTraversalPerformed){

      getTraversableComponents(parent);
    }

    if (!comp.hasFocus()){

      comp.requestFocus();
    }

    tabTraversalFinished = false;

    /* Attach custom focus listeners */

    TraversalFocusListener listener = new TraversalFocusListener();

    Iterator i = traversableComponents.iterator();
    while(i.hasNext()){

      ((Component)(i.next())).addFocusListener(listener);
    }

    TabTraversalTimeOut timeout = new TabTraversalTimeOut();
    timeout.start();

    /* Tab through all components */

    try{

      Robot robot = new Robot();

      if (traversableComponents.size() > 0){

        Component lastFocused = null;

        while(!tabTraversalFinished){

          robot.keyPress(KeyEvent.VK_TAB);
          try{ Thread.currentThread().sleep(TAB_TRAVERSAL_DELAY); } catch(InterruptedException e){}

          Component focused = listener.getFocusedComponent();
          if (focused == lastFocused){

            // Component probably swallowed Tab keypress so try Ctrl-Tab

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            try{ Thread.currentThread().sleep(TAB_TRAVERSAL_DELAY); } catch(InterruptedException e){}

            focused = listener.getFocusedComponent();
            if (focused == lastFocused){

              // Stuck or window lost focus, so give up
              break;
            }
          }
          lastFocused = focused;
        }
      }
    }
    catch(AWTException e){}

    timeout.cancel();

    /* Detach focus listeners */																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																															

    i = traversableComponents.iterator();
    while(i.hasNext()){

      ((Component)(i.next())).removeFocusListener(listener);
    }

    /* Set flag to show at least one tab traversal performed */

    tabTraversalPerformed = true;
  }

  /**
   *  Get all traversable components that are children of the specified
   *  component, including the specified component.
   *
   *  @param comp the component
   */
  private void getTraversableComponents(Component comp){

    if (comp instanceof Container){

      Container container = (Container)comp;
      Component[] children = container.getComponents();

      for (int c = 0; c < children.length; c++){

        getTraversableComponents(children[c]);
      }
    }

    if (!(((tests & SHOWING_ONLY) != 0) && !comp.isShowing()) && comp.isFocusTraversable()){

      traversableComponents.add(comp);
    }
  }

  /**
   *  Time out thread for the tab traversal, in case it goes wrong :)
   *  It may get stuck in a loop if it reaches a component that consumes the
   *  Tab keypress events. In which case, we simply give up.
   */
  private class TabTraversalTimeOut extends Thread{

    /**
     *  Start the timeout
     */
    public void run(){

      try{

        sleep(TAB_TRAVERSAL_TIME_OUT);
      }
      catch(InterruptedException e){}
      if (!cancelled){

        tabTraversalFinished = true;
      }
    }

    /**
     *  Cancel the timeout if it is no longer required, i.e. test is complete.
     */
    public void cancel(){

      cancelled = true;
    }

    private boolean cancelled = false;
  }

  /**
   *  A custom FocusListener that will add a component
   *  to traversedComponents when it obtains keyboard focus.
   *  <p>
   *  If the component is already in traversedComponents, the test
   *  terminates.
   */
   private class TraversalFocusListener extends FocusAdapter{

    public void focusGained(FocusEvent e){

      focused = (Component)(e.getSource());
      if (!traversedComponents.contains(focused)){

        traversedComponents.add(focused);
      }
      else{

        tabTraversalFinished = true;
      }
    }

    public Component getFocusedComponent(){

      return focused;
    }

    private Component focused = null;
  }

  /**
   *  Remove the traversed components from the traversable components
   *  to leave the components which cannot be reached.
   *  <p>
   *  This is called as the report is generated. It shouldn't be called
   *  unless all tests are completed.
   */
  private void removeTraversedComponents(){

    Iterator i = traversedComponents.iterator();
    while(i.hasNext()){

      traversableComponents.remove(i.next());
    }
  }

  /**
   *  Abstract class to generate reports from the tests. A subclass will
   *  implement {@link org.netbeans.a11y.AccessibilityTester.ReportGenerator#getReport(java.io.Writer) getReport}
   *  and use the accessor methods to format the results as required.
   *
   *  @author Tristan Bonsall
   */
  public abstract static class ReportGenerator{

    /**
     *  The AccessibilityTester that the report is being generated for.
     */
    protected AccessibilityTester tester = null;

    /**
     *  Create a ReportGenerator for an AccessibilityTester.
     *
     *  @param at the AccesibilityTester
     */
    public ReportGenerator(AccessibilityTester at){

      tester = at;
    }

    /**
     *  Get a PrintWriter from a Writer.
     *
     *  @param writer a Writer to use for the PrintWriter
     *  @return a PrintWriter for the Writer
     */
    protected PrintWriter getPrintWriter(Writer writer){

      if (writer instanceof PrintWriter){

        return (PrintWriter)writer;
      }
      return new PrintWriter(writer);
    }

    /**
     *  Create the report and send it to the Writer.
     *
     *  @param writer the Writer to output the results to
     */
    public abstract void getReport(Writer writer);

    /**
     *  Get the component that was used when the AccessiblityTester was created.
     *
     *  @return the test target
     */
    protected Component getTestTarget(){

      return tester.parent;
    }

    /**
     *  Get a HashSet containing the components that have no Accessible name.
     *
     *  @return the components
     */
    protected HashSet getNoName(){

      return tester.noName;
    }

    /**
     *  Get a HashSet containing the components that have no Accessible description.
     *
     *  @return the components
     */
    protected HashSet getNoDesc(){

      return tester.noDesc;
    }

    /**
     *  Get a HashSet containing the components that do not implement Accessible.
     *
     *  @return the components
     */
    protected HashSet getNoAccess(){

      return tester.noAccess;
    }

    /**
     *  Get a HashSet containing the JLabels which do not have their LABEL_FOR
     *  field set to a non-null value.
     *
     *  @return the components
     */
    protected HashSet getNoLabelFor(){

      return tester.noLabelFor;
    }

    /**
     *  Get a HashSet containing the JTextFields that do not have a LABEL_FOR
     *  pointing to them.
     *
     *  @return the components
     */
    protected HashSet getNoLabelForPointing(){

      return tester.textFields;
    }

    /**
     *  Get a HashSet containing the AbstractButtons with no mnemonic.
     *
     *  @return the components
     */
    protected HashSet getNoMnemonic(){

      return tester.noMnemonic;
    }

    /**
     *  Get a HashSet containing the components that were not reachable by the
     *  Tab traversal.
     *
     *  @return the components
     */
    protected HashSet getNotTraversable(){

      tester.removeTraversedComponents();
      return tester.traversableComponents;
    }
  }

  /**
   *  Reset the test results.
   */
  public void resetReport(){

    traversableComponents.clear();
    traversedComponents.clear();
    labels.clear();
    textFields.clear();

    noName.clear();
    noDesc.clear();
    noAccess.clear();
    noLabelFor.clear();
    noMnemonic.clear();

    tabTraversalPerformed = false;
  }

  /**
   *  Time out value for the tab traversal. Set by the system property
   *  'a11ytest.tab_timeout' as a time in milliseconds.
   */
  private long TAB_TRAVERSAL_TIME_OUT = 15000;

  /**
   *  Delay between key presses for the tab traversal. Set by the system property
   *  'a11ytest.tab_delay' as a time in milliseconds.
   */
  private long TAB_TRAVERSAL_DELAY = 200;

  private boolean tabTraversalFinished = false;
  private boolean tabTraversalPerformed = false;

  private Component parent = null;

  private HashSet excludedClasses = new HashSet();

  private HashSet traversedComponents = new HashSet();
  private HashSet traversableComponents = new HashSet();
  private HashSet labels = new HashSet();
  private HashSet textFields = new HashSet();

  private HashSet noName = new HashSet();
  private HashSet noDesc = new HashSet();
  private HashSet noAccess = new HashSet();
  private HashSet noLabelFor = new HashSet();
  private HashSet noMnemonic = new HashSet();
}