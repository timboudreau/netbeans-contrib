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

package org.netbeans.modules.a11y;

import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFrame;

import org.openide.TopManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.OutputWriter;

import org.netbeans.modules.form.RADComponentCookie;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.FormCookie;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADVisualFormContainer;
import org.netbeans.modules.form.VisualReplicator;

import javax.swing.SwingUtilities;

import org.openide.windows.Workspace;
import org.openide.windows.Mode;


/**
 *  Accessibility test action. Testing form designed in Form Editor.
 *  @author Tristan Bonsall, Marian.Mirilovic@Sun.com
 */
public class A11yAction extends CookieAction {
    
    /** Test thread. */
    private Thread thread;
    
    /**
     *  Human presentable name of the action. This should be
     *  presented as an item in a menu.
     *  @return the name of the action */
    public String getName(){
        return NbBundle.getBundle(A11yAction.class).getString("ACT_A11y"); // NOI18N
    }
    
    /** Help context where to find more about the action.
     * @return the help context for this action */
    public HelpCtx getHelpCtx(){
        return new HelpCtx("gui.modes"); // NOI18N
    }
    
    public int mode(){
        return MODE_EXACTLY_ONE;
    }
    
    public Class[] cookieClasses(){
        return new Class[] {RADComponentCookie.class, FormCookie.class};
    }
    
    /** @return resource for the action icon */
    protected String iconResource(){
        return "/org/netbeans/modules/a11y/resources/a11ytest.gif"; // NOI18N
    }
    
    public void performAction(Node[] selectedNodes){
        if (selectedNodes.length > 0){
            testForm(selectedNodes[0]);
        }
    }
    
    private void testForm(Node node){
        FormModel formModel = null;
        
        if (node.getCookie(RADComponentCookie.class) != null){
            RADComponentCookie cookie = (RADComponentCookie) node.getCookie(RADComponentCookie.class);
            formModel = cookie.getRADComponent().getFormModel();
            
        }
        else if (node.getCookie(FormCookie.class) != null){
            FormCookie cookie = (FormCookie) node.getCookie(FormCookie.class);
            formModel = cookie.getFormModel();
        }
        
        if (formModel == null) return;
        
        RADComponent formComp = formModel.getTopRADComponent();
        
        Frame frame = null;
        
        if (!(formComp instanceof RADVisualFormContainer)) return;
        
        A11YTesterTopComponent at = A11YTesterTopComponent.getInstance();
        Workspace ws = TopManager.getDefault().getWindowManager().getCurrentWorkspace();
        Mode formMode = ws.findMode("Form");
        
        /* Hack for our window system, I want open top component docked not in center but now it si impossible;
         * solution => reflection
         *    ((org.netbeans.core.windows.ModeImpl)formMode).dockInto(at, "EAST");
         */
        try {
            Class c = Class.forName("org.netbeans.core.windows.ModeImpl"); // NOI18N
            if (formMode != null) {
                // use reflection now
                java.lang.reflect.Method m = c.getDeclaredMethod("dockInto", // NOI18N
                    new Class[] {
                        org.openide.windows.TopComponent.class,
                        java.lang.Object.class });
                        
                m.invoke(formMode, new Object[] { at, "EAST" } );
            }
        } catch (Exception x) {
            System.out.println("Exception from A11yAction"+x);
            x.printStackTrace();
        }
        /* End of hack Window System
         */
        
        at.open();
        at.requestFocus();
        
        
        RADVisualFormContainer formContainer = (RADVisualFormContainer) formComp;
        
        // a JFrame or Frame will be used (depending on form is Swing or AWT)
        Object formInstance = formContainer.getBeanInstance();
        Class frameClass = formInstance instanceof javax.swing.JComponent
                        || formInstance instanceof JFrame
                        || formInstance instanceof javax.swing.JDialog
                        || formInstance instanceof javax.swing.JApplet
                        || formInstance instanceof javax.swing.JWindow
                        || (!(formInstance instanceof Window) && !(formInstance instanceof java.awt.Panel))
            ? JFrame.class : Frame.class;
        
        try{
            
            //frame = (Frame)FormDesigner.createContainerView(formContainer, frameClass);
            
            VisualReplicator replicator = new VisualReplicator(frameClass, null, 0);
            replicator.setTopMetaComponent(formContainer);
            frame = (Frame) replicator.createClone();
            
            // prepare close operation
            if (frame instanceof JFrame){
                ((JFrame)frame).setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            }
            
            // set size
            if (formContainer.getFormSizePolicy() == RADVisualFormContainer.GEN_BOUNDS && formContainer.getGenerateSize()){
                frame.setSize(formContainer.getFormSize());
            } else{
                frame.pack();
            }
            
            // set location
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            java.awt.Dimension frameSize = frame.getSize();
            frame.setLocation(  screenSize.width+20 > frameSize.width ?     (screenSize.width - frameSize.width) / 2 : 0,
                                screenSize.height+20 > frameSize.height ?   (screenSize.height - frameSize.height) / 2 : 0);
            
            frame.show();
            
            
            threadInterrupt();
            
            OutputWriter writer = TopManager.getDefault().getIO("Accessibility results").getOut();
        
            thread = new Thread(new AccessibilityTesterRunnable(at.getPanel(), frame, writer));
            thread.start();

            //wait while finished test thread - because if you close frame first, test should be affected
            thread.join();
            
            //            while(thread.isAlive()){
            //                System.err.println("alive="+(alive++));
            //                wait(500);
            //            }
            
      } catch (Exception ex){
            if (Boolean.getBoolean("netbeans.debug.exceptions")){ // NOI18N
                ex.printStackTrace();
            }
        }

        if (frame != null){
            frame.setVisible(false);
            frame.dispose();
        }
        
    }
    
    
    /** Interrupt thread */
    private void threadInterrupt() {
        if (thread!=null) {
            thread.interrupt();
            thread=null;
        }
    }
    
}
