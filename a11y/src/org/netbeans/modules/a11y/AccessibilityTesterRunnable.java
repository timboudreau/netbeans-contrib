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

package org.netbeans.modules.a11y;

import java.awt.Container;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.JInternalFrame;

import org.netbeans.a11y.ui.AccessibilityPanel;
import org.netbeans.a11y.AccessibilityTestRunner;
import org.netbeans.a11y.TestSettings;

import java.io.Writer;
import java.io.PrintWriter;


/* Class for managing(running) test in the own thread.
 * @author  Marian.Mirilovic@Sun.Com
 */
public class AccessibilityTesterRunnable implements Runnable, AWTEventListener {
    
    private Container testedContainer;
    private AccessibilityPanel aPanel;
    private Writer writer;
    
    private boolean usingDispatcher = false;
    
    
    /** Creates new AccessibilityTesterRunnable. */
    public AccessibilityTesterRunnable(AccessibilityPanel aPanel, Container cont, Writer writer) {
        this(aPanel);
        this.testedContainer = cont;
        this.writer = writer;
        usingDispatcher = false;
    }
    
    
    
    /** Creates new AccessibilityTesterRunnable. */
    public AccessibilityTesterRunnable(AccessibilityPanel aPanel) {
        this.aPanel = aPanel;
        this.writer = new PrintWriter(System.out);
        usingDispatcher = true;
    }
    
    public void eventDispatched(java.awt.AWTEvent awtEvent) {
        if(usingDispatcher) {
            if ((awtEvent instanceof KeyEvent)&&(awtEvent.getID()==KeyEvent.KEY_RELEASED)&&(((KeyEvent)awtEvent).getKeyCode()==KeyEvent.VK_F11)&&(((KeyEvent)awtEvent).getModifiers()==KeyEvent.CTRL_MASK)) {
                if (testedContainer==null) {
                    testedContainer=(Container)awtEvent.getSource();
                    //                    Hack for TopComponents while (!((testedContainer instanceof Window)||(testedContainer instanceof JInternalFrame))) {
                    while (!((testedContainer instanceof Window)||(testedContainer instanceof JInternalFrame) || isTopComponentInIDE())) {
                        testedContainer = testedContainer.getParent();
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }
    
    private boolean isTopComponentInIDE(){
        try{
            Class c = Class.forName("org.openide.windows.TopComponent",true,Thread.currentThread().getContextClassLoader());
            return c.isInstance(testedContainer);
        }catch(Exception exc){
            exc.printStackTrace(System.out);
            return false;
        }
    }
    
    public void run() {
        if(usingDispatcher) {
            try {
                Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.KEY_EVENT_MASK);
                
                while (!Thread.currentThread().interrupted()) {
                    
                    while (testedContainer==null) {
                        Thread.currentThread().sleep(300);
                    }
                    
                    runTest();
                }
                
            } catch (InterruptedException ie) {
                
            }    finally {
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            }
        } else {
            try{
                runTest();
            }catch(InterruptedException ie) {
            }
        }
    }
    
    
    private void runTest() throws InterruptedException {
        while (testedContainer==null) {
            Thread.currentThread().sleep(300);
        }
        
        try {
            String title = getWindowTitle();
            
            TestSettings testSettings = aPanel.getTests();
            testSettings.setWindowTitle(title);
            
            // it seems like tests start so disbale all options
            aPanel.enableAllCheckBoxes(false);
            
            statusLog("Testing testedContainer " + title);
            
            AccessibilityTestRunner testRunner = new AccessibilityTestRunner(testedContainer, testSettings, aPanel);
            
            if(aPanel.doGetModel()){
                aPanel.addTreePanel("Tested container", testRunner.getAWTmodel());
            }
            
            testRunner.testContainer();
            
            statusLog("Saving test report.");
            
            testRunner.writeResults(aPanel.getResultsFileName(), aPanel.saveProperties(), writer);
            statusLog("Test finished.");
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
            statusLog("Error : test canceled.");
        }finally{
            aPanel.enableAllCheckBoxes(true);
        }
        testedContainer=null;
        
    }
    
    /** Write log to status label.
     * @param log text shown in status label */
    private void statusLog(String log) {
        aPanel.setStatusText(log);
    }
    
    /** Get testedContainer title.
     * @return  testedContainer title or label "Window without title" */
    private String getWindowTitle() {
        String testedContainerTitle = "Window without title";
        
        if(testedContainer instanceof Frame){
            testedContainerTitle = ((Frame)testedContainer).getTitle();
        }
        
        if(testedContainer instanceof Dialog){
            testedContainerTitle = ((Dialog)testedContainer).getTitle();
        }
        
        if(testedContainer instanceof JInternalFrame){
            testedContainerTitle = ((JInternalFrame)testedContainer).getTitle();
        }
        
        // hack for IDE testing, because if component is TopComponent - it hasn't set title,
        // but name of the component can get by getComponent() method
        if(isTopComponentInIDE()){
            try {
                //                Class c = Class.forName("org.openide.windows.TopComponent");
                //                java.lang.reflect.Method m = c.getDeclaredMethod("getDisplayName", new Class[] {});
                //
                //                testedContainerTitle = (String)m.invoke(testedContainer, new Object[] {} );
                testedContainerTitle = ((java.awt.Component)testedContainer).getName();
            } catch (Exception x) {
                x.printStackTrace(System.out);
            }
        }
        
        return testedContainerTitle;
    }
    
    
}

