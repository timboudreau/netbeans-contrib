/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.a11y;

import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFrame;

import org.openide.nodes.Node;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

import org.netbeans.modules.form.RADComponentCookie;
import org.netbeans.modules.form.RADComponent;
import org.netbeans.modules.form.FormCookie;
import org.netbeans.modules.form.FormModel;
import org.netbeans.modules.form.RADVisualFormContainer;
import org.netbeans.modules.form.ViewConverter;
import org.netbeans.modules.form.VisualReplicator;

import org.openide.windows.IOProvider;
import org.openide.windows.OutputWriter;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;


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
        return "org/netbeans/modules/a11y/resources/a11ytest.gif"; // NOI18N
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
            
        } else if (node.getCookie(FormCookie.class) != null){
            FormCookie cookie = (FormCookie) node.getCookie(FormCookie.class);
            formModel = cookie.getFormModel();
        }
        
        if (formModel == null) return;
        
        RADComponent formComp = formModel.getTopRADComponent();
        
        Frame frame = null;
        
        if (!(formComp instanceof RADVisualFormContainer)) return;
        
        A11YTesterTopComponent at = A11YTesterTopComponent.getInstance();
        Mode formMode = WindowManager.getDefault().findMode("Form");
        
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
        at.requestActive();
        
        
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
            
            VisualReplicator replicator = new VisualReplicator(false, new ViewConverter[] { new VisualReplicator.DefaultConverter() }, null); 
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
            
            frame.setVisible(true);
            
            
            threadInterrupt();
            
            OutputWriter writer = IOProvider.getDefault().getIO("Accessibility results", true).getOut();
            
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
