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

import java.util.Iterator;
import java.util.HashSet;
import java.util.Enumeration;

import java.awt.Component;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Container;


import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.accessibility.*;
import java.awt.event.KeyEvent;

// Special JDK 1.4 Imports
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;


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
 *  <li>a11ytest.tab_delay - the delay (in ms) between tab key presses (default = 300)
 *  </ul>
 *
 *  @author Tristan Bonsall, Marian.Mirilovic@sun.com
 */
public class AccessibilityTester{

    /* AWT Robot - used for testing focus traversal.*/
    private static Robot robot;
    
    /* logging debug if true */
    public static boolean debugLog = false;
    
    /* Parent component of tested window. */
    private Component parent = null;
    
    /** Time out value for the tab traversal. Set by the system property
     *  'a11ytest.tab_timeout' as a time in milliseconds.  */
    private long TAB_TRAVERSAL_TIME_OUT = 15000;
    
    /** Delay between key presses for the tab traversal. Set by the system property
     *  'a11ytest.tab_delay' as a time in milliseconds.  */
    private long TAB_TRAVERSAL_DELAY = 300;
    
    private static String LOG_CAPTION = "[org.netbeans.modules.a11y.AccessibilityTester] ";
    
    private boolean tabTraversalFinished = false;
    private boolean tabTraversalPerformed = false;
    private boolean switchTabbed = false;
    
    private HashSet excludedClasses = new HashSet();
    
    private HashSet traversedComponents = new HashSet();
    private HashSet traversableComponents = new HashSet();
    private HashSet labels = new HashSet();
    private HashSet labelForPointingComponents = new HashSet();
    
    private HashSet noName = new HashSet();
    private HashSet noDesc = new HashSet();
    private HashSet noAccess = new HashSet();
    private HashSet noLabelFor = new HashSet();
    private HashSet noMnemonic = new HashSet();
    private HashSet wrongMnemonic = new HashSet();
    
    private HashSet focused = new HashSet();
    
    /** Set where to store components which doesn't have name. */
    private HashSet noComponentName = new HashSet();
    
    private java.util.Hashtable mnemonicConflict = new java.util.Hashtable();
    
    private boolean merlinTesting = false;
    
    private TestSettings testSettings;
    
    /** Create a new AccessibilityTester for the specified component to perform the specified tests.
     *  @param component the component to check
     *  @param tests the tests to perform  */
    public AccessibilityTester(Component component, TestSettings set){
        parent = component;
        testSettings = set;
        
        
        // Logs read from arguments
        debugLog = Boolean.getBoolean("a11ytest.log");
        
        /* hack for Merlin -
         * if Tester is running on jdk 1.4 , testing approach is another as on 1.3 <= reason of new Focus Management implementation */
        String javaVersion = System.getProperty("java.version");
        if(javaVersion.indexOf("1.4") != -1)
            merlinTesting = true;
        /* - end hack for Merlin */
        
        TAB_TRAVERSAL_TIME_OUT = 15000;
        try{
            TAB_TRAVERSAL_TIME_OUT = Long.getLong("a11ytest.tab_timeout", new Long(15000)).longValue();
        }catch(Exception e){
            // Ignore and use default value
        }
        
        TAB_TRAVERSAL_DELAY = 300;
        try{
            TAB_TRAVERSAL_DELAY = Long.getLong("a11ytest.tab_delay", new Long(300)).longValue();
        }catch(Exception e){
            // Ignore and use default value
        }
        
        excludedClasses = set.getExcludedClasses();
        
        /* hack for Merlin -
         * must be added parent, needed to provide test all tests */
        if(merlinTesting) {
            focused.add(parent);
            findFocusableContainer_merlin((Container)parent);
            
            //- LOG ONLY
            if(debugLog) {
                System.err.println(LOG_CAPTION+" - <init> Merlin testing start ");
                System.err.println(LOG_CAPTION+" - <init> Parent components="+((Container)parent).getComponentCount());
                
                Iterator i = focused.iterator();
                int j=0;
                while(i.hasNext()){
                    j++;
                    Component fcp = (Component)(i.next());
                    System.err.println(LOG_CAPTION+" - <init> ["+j+"] = " + fcp);
                }
                
                System.err.println(LOG_CAPTION+" - <init> Merlin testing end ");
            }
            // LOG ONLY -/
        }
        /* - end hack for Merlin */
        
    }
    
    private static synchronized Robot getRobot() throws AWTException {
        String os = System.getProperty("os.name");
        
        //- LOG ONLY
        if(debugLog) {
            System.err.println(LOG_CAPTION+" - <getRobot()> OS => " + os);
            
            if(robot != null)
                System.err.println(LOG_CAPTION+" - <getRobot()> - >>> robot = " + robot.hashCode());
            else
                System.err.println(LOG_CAPTION+" - <getRobot()> - >>> robot = null");
        } // LOG ONLY -/
        
        if(os.indexOf("Linux")!=-1){
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <getRobot()> !!!! Linux hack ===> make robot null.");
            
            robot = null;
        }
        
        if(robot == null) {
            robot = new Robot();
        }
        
        return robot;
    }
    
    
    /** Return AWT - tree of tested container.
     *  Call <code> addToModel(parent); </code>
     * @return  parent node of AWT tree model of tested container */
    public DefaultMutableTreeNode getModel(){
        return addToModel(parent);
    }
    
    
    /** Create AWT tree of tested conatiner.
     * @param comp parent of next sub-tree.
     * @return tree parent node */
    private DefaultMutableTreeNode addToModel(Component comp){
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <addToModel(Component)> ++++ Component="+comp);
        
        DefaultMutableTreeNode treeNode;
        
        AccComponent acc_comp = new AccComponent(comp);
        treeNode = new DefaultMutableTreeNode(acc_comp);
        
        if(comp instanceof Container){
            Container container = (Container)comp;
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <addToModel(Component)> - Container="+container);
            
            Component[] children = container.getComponents();
            
            for (int c = 0; c < children.length; c++){
                treeNode.add(addToModel(children[c]));
                
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <addToModel(Component)> - added ["+c+"] child => "+children[c].getClass().getName()+"/"+container);
                
            }
        }
        
        return treeNode;
    }
    
    /* Run all tests.
     * This method run Testing of Accessible Properties and Testing of Focus Traversability of tested container.
     */
    public void startTests() {
        if(testSettings.accessibleProperties || testSettings.accessibleInterface)
            testProperties();
        if(testSettings.tabTraversal)
            testTraversal();
    }
    
    /** Assess the accessibility properties of the component.
     *  <p>
     *  If the component is null, the method returns.
     *  If the component is a container, all of the components in
     *  the container are assessed for their accessibility recursively. */
    public void testProperties(){
        
        if (parent == null)
            return;
        
        tests(parent);
        
        if (testSettings.AP_mnemonics){
            
            //- LOG ONLY - list of conflicts before cleaning
            if(debugLog) {
                System.err.println(LOG_CAPTION+" - <testProperties()> - CONFLICTS = ");
                Enumeration conf = mnemonicConflict.elements();
                while(conf.hasMoreElements())  System.err.println(LOG_CAPTION+" - <testProperties()> -  "+conf.nextElement().toString());
            } // LOG ONLY -/
            
            cleanMnemonicsConflict();
            
            // LOG ONLY - list of conflicts after cleaning
            if(debugLog) {
                System.err.println(LOG_CAPTION+" - <testProperties()> - CONFLICTS after clean up = ");
                Enumeration conf = mnemonicConflict.elements();
                while(conf.hasMoreElements())   System.err.println(LOG_CAPTION+" -  <testProperties()> - "+conf.nextElement().toString());
            } // LOG ONLY -/
            
        }
        
        if (testSettings.AP_noLabelFor)
            checkLabelTextComponentPairs();
    }
    
    /** The actual test method, which is called recursively. */
    private void tests(Component comp){
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <tests(Component)> - ======= Tested component="+comp.getClass().getName());
        
        
        testContainer(comp);
        
        /* H1 -> Hack for JLabels - JLabel isn't focusTraversable, but If we want test labelFor pointing we must have all JLabels. */
        if (testSettings.AP_noLabelFor) {
            if (testSettings.AP_showingOnly && !comp.isShowing()){
                if(debugLog) System.err.println(LOG_CAPTION+" - <tests("+comp.getClass().getName()+")> - Label For => NOT TESTED - because : testSettings.AP_showingOnly="+testSettings.AP_showingOnly+" && !!comp.isShowing()="+!comp.isShowing());
                return;
            }
            
            if(comp instanceof JLabel) {
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <tests(Component)> - \t -add label  ="+comp);
                
                labels.add(comp);
            }
        }
        
        /* Check Labels which have set labelFor have mnemonics */
        if ((comp instanceof JLabel) && testSettings.AP_mnemonics && testSettings.AP_m_label) {
            JLabel label = (JLabel) comp;
            int mnemonic = label.getDisplayedMnemonic();
            
            // LOG ONLY -/
            if(debugLog) System.err.print(LOG_CAPTION+" - <tests(Component)> - \t - label Mnemonic="+mnemonic+"  label.getLabelFor()="+label.getLabelFor()+" label.getDisplayedMnemonic()="+label.getDisplayedMnemonic());
            
            Component labelF = label.getLabelFor();
            
            if (labelF != null){
                // hack for Merlin if((labelF.isFocusTraversable()) && (label.getDisplayedMnemonic() <= 0))
                if(testFocusability(labelF) && (mnemonic <= 0))
                    noMnemonic.add(comp);
                
                testMnemonics(label.getText(), mnemonic, comp);
                
            }
        }
        

        // Test implement Accessible
        testImplementAccessible(comp);
        
        /* Test parent (Window) although will not test non-showed or non-traversable*/
        if(!comp.equals(parent)){
            if (testSettings.AP_showingOnly && !comp.isShowing()){
                if(debugLog) System.err.println(LOG_CAPTION+" - <tests("+comp.getClass().getName()+")> - NOT TESTED - because : testSettings.AP_showingOnly="+testSettings.AP_showingOnly+" && !!comp.isShowing()="+!comp.isShowing());
                return;
            }
            
            // hack for Merlin if (testSettings.AP_focusTraversableOnly && !comp.isFocusTraversable()){
            if (testSettings.AP_focusTraversableOnly && !testFocusability(comp)){
                if(debugLog) System.err.println(LOG_CAPTION+" - <tests("+comp.getClass().getName()+")> - NOT TESTED - because : testSettings.AP_focusTraversableOnly="+testSettings.AP_focusTraversableOnly+"  && !testFocusability(comp)="+!testFocusability(comp));
                return;
            }
        }
        
        /* Check if class is excluded from the test */
        String classname = comp.getClass().toString();
        if (classname.startsWith("class ")){
            classname = classname.substring(6);
        }
        if (excludedClasses.contains(classname)){
            if(debugLog) System.err.println(LOG_CAPTION+" - <tests("+comp.getClass().getName()+")> - NOT TESTED - because : Excluded classes contains this class.");
            return;
        }
        
        
        // Test Accessible Name and Accessible Description
        if(comp instanceof Accessible){
            AccessibleContext comp_AC = comp.getAccessibleContext();
            
            if(comp_AC != null) {
                testAccessibleName(comp, comp_AC);
                testAccessibleDescription(comp, comp_AC);
            } else {
                noName.add(comp);
                noDesc.add(comp);
            }
        }
        
        
        // Test Label For set
        testLabelForSet(comp);
        
        // Test Buttons Mnemonics
        testButtonsMnemonics(comp);
        
        // Test Component Name
        if(testSettings.test_name){
            testComponentName(comp);
        }
        
    }

    
    /* Test any children of the component */
    private void testContainer(Component comp) {
        if ((comp instanceof Container)  && !(comp instanceof JComboBox)){
            Container container = (Container)comp;
            Component[] children = container.getComponents();
            
            for (int c = 0; c < children.length; c++){
                tests(children[c]);
            }
        }
    }
    
    
    
    /* Check if component implement accessible*/
    private void testImplementAccessible(Component comp) {
        if (testSettings.accessibleInterface && !(comp instanceof Accessible)) {
            if (testSettings.AI_showingOnly){
                if(comp.isShowing())
                    noAccess.add(comp);
            } else
                noAccess.add(comp);
        }
    }
    
    /* Check if component has set accessible name, AccessibleName!=null */
    private void testAccessibleName(Component comp, AccessibleContext comp_AC) {
        String name = comp_AC.getAccessibleName();
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <testAccessibleName()> - accessibleName="+name);
        
        if (testSettings.AP_accessibleName && (name == null)){
            noName.add(comp);
        }
    }
    
    
    /* Check if component has set accessible description, AccessibleDescription!=null */
    private void testAccessibleDescription(Component comp, AccessibleContext comp_AC) {
        String desc = comp_AC.getAccessibleDescription();
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <testAccessibleDescription()> - accessibleDescription="+desc);
        
        if (testSettings.AP_accessibleDescription && (desc == null)){
            noDesc.add(comp);
        }
    }
    
    
    /* Check if button has set mnemonic, mnemonic>0 */
    private void testButtonsMnemonics(Component comp) {
        if (testSettings.AP_mnemonics) {
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <testButtonsMnemonics(Component)> - Check mnemonics ");
            
            /* Check AbstractButtons have mnemonics */
            if(testSettings.AP_m_abstractButtons && (comp instanceof AbstractButton)) {
                AbstractButton button = (AbstractButton)comp;
                
                // LOG ONLY -/
                if(debugLog) System.err.print(LOG_CAPTION+" - <testButtonsMnemonics(Component)> \t - button Mnemonic="+button.getMnemonic());
                
                int mnemonic =  button.getMnemonic();
                
                if (mnemonic <= 0){
                    if (!testSettings.AP_m_defaultCancel && (button instanceof JButton)) {
                        JButton jButton = (JButton)button;
                        String tt;
                        if(!(jButton.isDefaultButton() ||
                        (((tt=jButton.getText()) != null) && tt.equals(testSettings.getCancelLabel() )))) // tt - hack for rising NPE if text is null
                            noMnemonic.add(comp);
                    }else
                        noMnemonic.add(comp);
                }
                
                testMnemonics(button.getText(), mnemonic, comp);
            }
            
        }
        
    }

    
    /* Check if component has set label for, labelFor!=null */
    private void testLabelForSet(Component comp) {
        if (testSettings.AP_labelForSet && (comp instanceof JLabel)){
            // H1- hack for JLabels      labels.add(comp);
            Component labelFor = ((JLabel)comp).getLabelFor();
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <testLabelForSet()> - labelFor="+labelFor);
            
            if (labelFor == null){
                noLabelFor.add(comp);
            }
        }
        
        if (testSettings.AP_noLabelFor) {
            if ((testSettings.AP_nlf_text && (comp instanceof JTextComponent)) ||
            (testSettings.AP_nlf_table && (comp instanceof JTable))        ||
            (testSettings.AP_nlf_list && (comp instanceof JList))          ||
            (testSettings.AP_nlf_tree && (comp instanceof JTree))          ||
            (testSettings.AP_nlf_tabbedPane && (comp instanceof JTabbedPane))){
                labelForPointingComponents.add(comp);
            }
        }
        
    }
    

    /* Check if component has set mnemonics-Labels, mnemonics are ASCII, report mnemonics conflict */
    private void testMnemonics(String str, int mnemonic, Component comp) {
        if(mnemonic!=0){
            // test whether label of button contains mnemonic || mnemonic is ASCII
            if( !labelContainsMnemonic(str, mnemonic) || mnemonic > 127){
                
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <testMnemonics(String,int,Component)> - WRONG_MNEMONIC " + str + " indexOf="+str.indexOf(mnemonic)+" mnemonic="+mnemonic);
                
                wrongMnemonic.add(comp);
            }
            
            // if mnemonics conflict
            if(!mnemonicConflict.containsKey(""+mnemonic)){
                HashSet mn = new HashSet();
                mn.add(comp);
                mnemonicConflict.put(""+mnemonic,mn);
            }else {
                // BAD - do something
                HashSet mn = (HashSet)mnemonicConflict.get(""+mnemonic);
                mn.add(comp);
                mnemonicConflict.put(""+mnemonic,mn);
            }
        }
        
    }
    
    
    /** Test whether label contains mnemonic
     * @param label tested label 
     * @param mnemonic tested mnemonic
     * @return  true - if label contains mnemonic, false - if it doesn't  */    
    private boolean labelContainsMnemonic(String label, int mnemonic) {
        return (label.toLowerCase().indexOf(mnemonic)!=-1 || label.toUpperCase().indexOf(mnemonic)!=-1);
    }

    /** Clean mnemonics conflict hash table, if each mnemonic is set only for one component.
     */
    private void cleanMnemonicsConflict() {
        Enumeration enumer = mnemonicConflict.keys();
        
        while(enumer.hasMoreElements()) {
            String key = (String)enumer.nextElement();
            HashSet hs = (HashSet)mnemonicConflict.get(key);
            if(hs.size()==1)
                mnemonicConflict.remove(key);
        }
        
    }
    
    /** Clean label for components and labels pointing to it.
     */
    private void checkLabelTextComponentPairs(){
        Iterator i = labelForPointingComponents.iterator();
        while(i.hasNext()){
            Component component = (Component)(i.next());
            Iterator j = labels.iterator();
            
            while(j.hasNext()){
                Component labelFor = ((JLabel)(j.next())).getLabelFor();
                if ((labelFor != null) && (labelFor == component)){
                    i.remove();
                    break;
                }
            }
        }
    }
    
    /** Use Tab traversal from the specified component to check which
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
     *  @param comp the component to begin traversal from */
    public boolean testTraversal() {
        if (parent == null){
            return false;
        }
        return testTraversalComponent(parent);
    }
    
    
    
    /** Run TAB traversal test
     * @param comp started component
     * @return  if false - TAB traversal doesn't finished correct*/    
    private boolean testTraversalComponent(Component comp){
        
        if (parent == null){
            return false;
        }
        
        /* Build list of focus traversable components in the parent component
         * This is only done once, unless resetReport is called */
        if (!tabTraversalPerformed){
            getTraversableComponents(parent);
        }
        
        if (!comp.hasFocus()){
//            try{
                comp.requestFocus();
            // Hack for our Windowing system - it invokes IllegalStateException if requestFocus isn't called from AWT thread    
//            }catch(IllegalStateException exc){
//                // LOG ONLY -/
//                if(debugLog) {
//                    System.err.println("EXCEPTION " + exc.getMessage());
//                    System.err.println("TRY TO CALL IT FROM AWT THREAD");
//                }
//                final Component comp_final = comp;
//                try{
//                    SwingUtilities.invokeAndWait(
//                        new Runnable() {
//                            public void run() {
//                                comp_final.requestFocus();
//                            }
//                        });
//                }catch(Exception ex){
//                if(debugLog) System.err.println("EXCEPTION " + ex.getMessage());
//                    
//                }
//            }
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - After request focus | comp="+comp.getClass().getName()+" hasFocus()="+comp.hasFocus());
        }
        
        tabTraversalFinished = false;
        switchTabbed = false;
        
        
        /* Attach custom focus listeners */
        org.netbeans.a11y.AccessibilityTester.TraversalFocusListener listener = new org.netbeans.a11y.AccessibilityTester.TraversalFocusListener();
        
        Iterator i = traversableComponents.iterator();
        while(i.hasNext()){
            ((Component)(i.next())).addFocusListener(listener);
        }
        
        org.netbeans.a11y.AccessibilityTester.TabTraversalTimeOut timeout = new org.netbeans.a11y.AccessibilityTester.TabTraversalTimeOut();
        timeout.start();
        
        /* Tab through all components */
        try{
            Robot robot = getRobot();
            
            // - LOG ONLY
            if(debugLog) {
                System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - Robot="+robot.hashCode());
                Iterator it = traversableComponents.iterator();
                int ssl=0;
                while(it.hasNext()){
                    ssl++;
                    Component fcp = (Component)(it.next());
                    System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - ["+ssl+"] "+fcp);
                }
                System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - Traversable Components + end - =======");
            } // LOG ONLY -/
            
            
            if (traversableComponents.size() > 0){
                Component lastFocused = null;
                
                while(!tabTraversalFinished){
                    robot.keyPress(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_TAB);
                    
                    // LOG ONLY -/
                    if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> TAB Last Focused component="+lastFocused);
                    
                    try{
                        Thread.currentThread().sleep(TAB_TRAVERSAL_DELAY);
                    } catch(InterruptedException e){}
                    
                    Component focusedComponent = listener.getFocusedComponent();
                    
                    // LOG ONLY -/
                    if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> Focused component="+focusedComponent);
                    
                    Component reallyFocusedComponent = SwingUtilities.findFocusOwner(parent);
                    
                    // LOG ONLY -/
                    if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> Really Focused component="+reallyFocusedComponent);
                    
                    if((focusedComponent instanceof JTabbedPane) && !testSettings.TT_showingOnly) {
                        robot.keyPress(KeyEvent.VK_RIGHT);
                        robot.keyRelease(KeyEvent.VK_RIGHT);
                        
                        // LOG ONLY -/
                        if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> VK_LEFT Focused component="+lastFocused);
                        
                        try{
                            Thread.currentThread().sleep(TAB_TRAVERSAL_DELAY);
                        } catch(InterruptedException e){}
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        
                        // LOG ONLY -/
                        if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> TAB Focused component="+lastFocused);
                        
                        switchTabbed = true;
                    }else
                        switchTabbed = false;
                    
                    if((focusedComponent==null && lastFocused==null) &&
                    !(  (reallyFocusedComponent instanceof JTextComponent) ||
                    (reallyFocusedComponent instanceof JTabbedPane) ||
                    (reallyFocusedComponent instanceof JTable)) ) {
                        
                        if(debugLog) System.err.println(LOG_CAPTION+" - ERROR : It's impossible test Tab traversal. After TAB, CTRL + TAB or RIGHT hitting nothing happends.");
                        
                        return false;
                    }
                    
                    if (focusedComponent==lastFocused){
                        // Component probably swallowed Tab keypress so try Ctrl-Tab
                        robot.keyPress(KeyEvent.VK_CONTROL);
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.keyRelease(KeyEvent.VK_TAB);
                        robot.keyRelease(KeyEvent.VK_CONTROL);
                        
                        // LOG ONLY -/
                        if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - --> CONTROL_TAB Focused component="+focusedComponent);
                        
                        try{ Thread.currentThread().sleep(TAB_TRAVERSAL_DELAY); } catch(InterruptedException e){}
                        
                        focusedComponent = listener.getFocusedComponent();
                        
                        if ((focusedComponent == lastFocused) && (!switchTabbed)){
                            
                            // LOG ONLY -/
                            if(debugLog) System.err.println(LOG_CAPTION+" - <testTraversalComponent(Component)> - !!!!!  LOCK !!!!!  "+focusedComponent + "==" + lastFocused);
                            
                            // Stuck or window lost focus, so give up
                            break;
                        }
                    }
                    lastFocused = focusedComponent;
                }
            }
        }catch(AWTException e){ e.printStackTrace();}
        
        timeout.cancel();
        
        /* Detach focus listeners */
        i = traversableComponents.iterator();
        while(i.hasNext()){
            ((Component)(i.next())).removeFocusListener(listener);
        }
        
        /* Set flag to show at least one tab traversal performed */
        tabTraversalPerformed = true;
        
        return true;
    }
    
    /** Get all traversable components that are children of the specified
     *  component, including the specified component.
     *  @param comp the component  */
    private void getTraversableComponents(Component comp){
        
        if ((comp instanceof Container)){
            Container container = (Container)comp;
            Component[] children = container.getComponents();
            for (int c = 0; c < children.length; c++){
                getTraversableComponents(children[c]);
            }
        }
        
        //hack for Merlin if (!(testSettings.TT_showingOnly && !comp.isShowing()) && comp.isFocusTraversable()){
        if (!(testSettings.TT_showingOnly && !comp.isShowing()) && testFocusability(comp)){
            traversableComponents.add(comp);
        }else{
        }
    }
 
    
    
    /** Method for testing if component did traversed before.
     * @param component tested component
     * @return true - if component didn't traversed time before or it's JTabbedPane(means should be traversed more than once), false - component was traversed time before */    
    private boolean contains(Component component) {
        if(component instanceof JTabbedPane) {
            /*Iterator i = traversedComponents.iterator();
            int numberContains=0;
            while(i.hasNext()){
                if(i.next() == component)
                    numberContains++;
            }
            if(((JTabbedPane)component).getTabCount() > numberContains) {
                tabTraversalTabbedPane = true;
                return true;
            }*/
            return true;
        }else
            return !traversedComponents.contains(component);
    }
    

    
    /** Testing focusability of component.
     * @param aComponent tested component
     * @return true - of isFocusTraversable/JDK1.3,2; traversable components contains this components/JDK1.4 */    
    private boolean testFocusability(Component aComponent) {
        
        if(merlinTesting)
            return focused.contains(aComponent);
        else
            return aComponent.isFocusTraversable();
        
    }
    
    
    /** Method for testing focus traversal on Merlin(JDK1.4).
     * @param cont  tested container */    
    private void findFocusableContainer_merlin(Container cont){
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)> -TTTTTT CONT = " + cont);
        
        FocusTraversalPolicy fp;
        
        if(cont.isFocusTraversalPolicySet()) {
            fp = cont.getFocusTraversalPolicy();
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)>  - GET FocusTraversalPolicy from CONT");
            
        }else{
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)>  - GET FocusTraversalPolicy from KeyboardFocusManager.getCurrentKeyboardFocusManager();");
            
            KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            fp = kfm.getDefaultFocusTraversalPolicy();
        }
        
        Component next = fp.getFirstComponent(cont);
        
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)> - next="+next);
        
        if(next == cont || focused.contains(next)){
            
            //- LOG ONLY
            if(debugLog)System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)> - next==cont / "+ (next==cont) + " ><  focused.contains() / "+focused.contains(next));
            
            return;
        }else{
            
            // LOG ONLY -/
            if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableContainer_merlin(Container)> - ADDED");
            
            focused.add(next);
        }
        
        if(next != null)
            findFocusableComponent_merlin(next, cont, fp);
        return;
        
    }
    
    
    /** Method for testing focus traversal on Merlin(JDK1.4).
     * @param comp tested component
     * @param cont tested container
     * @param fp focus traversal policy */
    private void findFocusableComponent_merlin(Component comp, Container cont, FocusTraversalPolicy fp) {
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - COMP= " + comp);
        
        if(comp instanceof Container) {
            Container comp_cont = (Container) comp;
            if(comp_cont.isFocusCycleRoot()){
                
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - is root");
                
                findFocusableContainer_merlin(comp_cont);
            } else {
                
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - isn't root");
                
//                Component next = fp.getComponentAfter(cont,comp);
                Component next = fp.getComponentAfter(comp.getFocusCycleRootAncestor(),comp);                
                
                // LOG ONLY -/
                if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - COMP next="+next);
                
                if(next == cont || focused.contains(next)){
                    
                    //- LOG ONLY
                        if(debugLog)System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - next==cont / "+ (next==cont) + " ><  focused.contains() / "+focused.contains(next));
                    
                    return;
                }else{
                    
                    // LOG ONLY -/
                    if(debugLog) System.err.println(LOG_CAPTION+" - <findFocusableComponent_merlin(Component,Container,FocusTraversalPolicy)> - ADDED");
                    
                    focused.add(next);
                }
                
                findFocusableComponent_merlin(next, cont, fp);
            }
        }
    }
    
    
    /** Time out thread for the tab traversal, in case it goes wrong :)
     *  It may get stuck in a loop if it reaches a component that consumes the
     *  Tab keypress events. In which case, we simply give up. */
    private class TabTraversalTimeOut extends Thread{
        
        /** Start the timeout */
        public void run(){
            try{
                sleep(TAB_TRAVERSAL_TIME_OUT);
            }
            catch(InterruptedException e){}
            if (!cancelled){
                tabTraversalFinished = true;
            }
        }
        
        /** Cancel the timeout if it is no longer required, i.e. test is complete. */
        public void cancel(){
            cancelled = true;
        }
        
        private boolean cancelled = false;
    }
    
    /** A custom FocusListener that will add a component
     *  to traversedComponents when it obtains keyboard focus.
     *  <p> If the component is already in traversedComponents, the test
     *  terminates. */
    private class TraversalFocusListener extends java.awt.event.FocusAdapter{
        
        private String TRAVERSAL_LOG_CAPTION = "[org.netbeans.modules.a11y.AccessibilityTester$TraversalFocusListener] ";
        
        public void focusGained(java.awt.event.FocusEvent e){
            focusedComponent = (Component)(e.getSource());
            //            if (!traversedComponents.contains(focusedComponent)){
            if (contains(focusedComponent)){
                
                // LOG ONLY -/
                if(debugLog) System.err.println(TRAVERSAL_LOG_CAPTION + " - <focusGained()> -  FOCUS GAINED - ADD ="+focusedComponent);
                
                traversedComponents.add(focusedComponent);
            }else{
                
                // LOG ONLY -/
                if(debugLog) System.err.println(TRAVERSAL_LOG_CAPTION + " - <focusGained()> - FOCUS GAINED - FINISH ="+focusedComponent);
                
                tabTraversalFinished = true;
            }
        }
        
        public Component getFocusedComponent(){
            return focusedComponent;
        }
        
        private Component focusedComponent = null;
    }
    
    /** Remove the traversed components from the traversable components
     *  to leave the components which cannot be reached.
     *  <p> This is called as the report is generated. It shouldn't be called
     *  unless all tests are completed. */
    private void removeTraversedComponents(){
        // hack for not traversing and focusing Window on JDK1.4
        traversableComponents.remove(parent);
        
        Iterator i = traversedComponents.iterator();
        while(i.hasNext()){
            traversableComponents.remove(i.next());
        }
    }
    
    /** Abstract class to generate reports from the tests. A subclass will
     *  implement {@link org.netbeans.a11y.AccessibilityTester.ReportGenerator#getReport(java.io.Writer) getReport}
     *  and use the accessor methods to format the results as required.
     *  @author Tristan Bonsall, Marian.Mirilovic@Sun.com */
    public abstract static class ReportGenerator{
        
        /**  The AccessibilityTester that the report is being generated for. */
        protected AccessibilityTester tester = null;
        protected TestSettings testSettings;
        protected boolean printName;
        protected boolean printDescription;
        protected boolean printPosition;
        
        
        /** Create a ReportGenerator for an AccessibilityTester.
         *  @param at the AccesibilityTester  */
        public ReportGenerator(AccessibilityTester at){
            tester = at;
        }
        
        /** Create a ReportGenerator for an AccessibilityTester.
         *  @param at the AccesibilityTester */
        public ReportGenerator(AccessibilityTester at, TestSettings set){
            tester = at;
            testSettings = set;
            printName = set.report_name;
            printDescription = set.report_description;
            printPosition = set.report_position;
        }
        
        /** Get a PrintWriter from a Writer.
         *  @param writer a Writer to use for the PrintWriter
         *  @return a PrintWriter for the Writer
         */
        protected java.io.PrintWriter getPrintWriter(java.io.Writer writer){
            if (writer instanceof java.io.PrintWriter){
                return (java.io.PrintWriter)writer;
            }
            return new java.io.PrintWriter(writer);
        }
        
        
        /** Create the report and send it to the Writer.
         *  @param writer the Writer to output the results to */
        public abstract void getReport(java.io.Writer writer);
        
        /** Get the component that was used when the AccessiblityTester was created.
         *  @return the test target
         */
        protected Component getTestTarget(){
            return tester.parent;
        }
        
        /** Get a HashSet containing the components that have no Accessible name.
         *  @return the components
         */
        protected HashSet getNoName(){
            return tester.noName;
        }
        
        /** Get a HashSet containing the components that have no Accessible description.
         *  @return the components
         */
        protected HashSet getNoDesc(){
            return tester.noDesc;
        }
        
        /** Get a HashSet containing the components that do not implement Accessible.
         *  @return the components
         */
        protected HashSet getNoAccess(){
            return tester.noAccess;
        }
        
        /** Get a HashSet containing the JLabels which do not have their LABEL_FOR
         *  field set to a non-null value.
         *  @return the components
         */
        protected HashSet getNoLabelFor(){
            return tester.noLabelFor;
        }
        
        /** Get a HashSet containing the JTextComponents that do not have a LABEL_FOR  pointing to them.
         *  @return the components
         */
        protected HashSet getNoLabelForPointing(){
            return tester.labelForPointingComponents;
        }
        
        /** Get a HashSet containing components with no mnemonic.
         *  @return the components
         */
        protected HashSet getNoMnemonic(){
            return tester.noMnemonic;
        }
        
        /** Get a HashSet containing components with wrong mnemonic.
         *  @return the components
         */
        protected HashSet getWrongMnemonic(){
            return tester.wrongMnemonic;
        }
        
        /** Get a Hashtable containing mnemonics with components.
         *  @return the components
         */
        protected java.util.Hashtable getMnemonicConflict(){
            return tester.mnemonicConflict;
        }
        
        
        /**  Get a HashSet containing the components that were not reachable by the Tab traversal.
         *  @return the components
         */
        protected HashSet getNotTraversable(){
            tester.removeTraversedComponents();
            return tester.traversableComponents;
        }

        /** Get a HashSet containing components with no component name.
         *  @return the components
         */
        protected HashSet getNoComponentName(){
            return tester.noComponentName;
        }
    }
    
    /** Reset the test results. */
/*    public void resetReport(){
 
        traversableComponents.clear();
        traversedComponents.clear();
        labels.clear();
        labelForPointingComponents.clear();
 
        noName.clear();
        noDesc.clear();
        noAccess.clear();
        noLabelFor.clear();
        noMnemonic.clear();
        wrongMnemonic.clear();
        mnemonicConflict.clear();
 
        tabTraversalPerformed = false;
    }
 */
    
    /* Check if component has set name, comp.getName() != null */
    private void testComponentName(Component comp) {
        String name = comp.getName();
        // LOG ONLY -/
        if(debugLog) System.err.println(LOG_CAPTION+" - <testComponentName()> - componentName="+name);
        
        if (testSettings.test_name && (name == null)){
            noComponentName.add(comp);
        }
    }

}

