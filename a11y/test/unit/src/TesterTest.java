/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

