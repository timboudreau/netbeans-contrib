/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

import java.io.PrintWriter;
import java.awt.Component;

import org.netbeans.junit.NbTestSuite;

/**
 *  Accessibility test for MainFrame
 *
 *  @author Marian.Mirilovic@Sun.com
 */
public class TesterTest extends org.netbeans.a11y.XTestAccessibilityTestCase {
    
        
    /** Creates a new instance of MainFrameTest */
    public TesterTest(String testName) {
        super(testName);
    }
    
    
    public String setSettingsFile() {
        return null; // or null
    }
    
    /**
     *  Display the frame.
     *  @return a reference to the frame
     */
    public Component getTestedComponent(){    
        //log = System.out;
        //ref = System.out;
        
        TestJFrame tf = new TestJFrame();
        tf.setVisible(true);
        tf.requestFocus();
        
        try{
            Thread.currentThread().sleep(3000);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return (Component)tf;
    }
    
    /**
     *  Tidy up after test
     */
    public void tearDown(){
    }
    
    /** Run test internally
     * @param args arguments from command line                                                             
     */                                                                                                    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(new NbTestSuite(TesterTest.class));
    }    
    
    
}

