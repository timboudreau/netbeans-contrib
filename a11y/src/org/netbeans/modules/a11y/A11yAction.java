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

package org.netbeans.modules.a11y;

import javax.swing.*;
import java.awt.*;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.*;
import org.openide.windows.*;
import org.openide.*;
import org.openide.util.actions.*;
import org.openide.windows.*;
import org.netbeans.modules.form.*;
import org.netbeans.a11y.*;

/**
 *  Accessibility test action.
 *
 *  @author Tristan Bonsall
 */
public class A11yAction extends CookieAction {

  /**
   *  Human presentable name of the action. This should be
   *  presented as an item in a menu.
   *
   *  @return the name of the action
   */
  public String getName(){

    return NbBundle.getBundle(A11yAction.class).getString("ACT_A11y"); // NOI18N
  }

  /** Help context where to find more about the action.
   * @return the help context for this action
   */
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

    java.awt.Frame mainFrame = TopManager.getDefault().getWindowManager().getMainWindow();
    AccessibilityTestOptions options = new AccessibilityTestOptions(frame, true);
    options.show();

    int tests = options.getTests();
    if ((tests == 0) || !(options.doProperties() || options.doTraversal())) return;
      
    RADVisualFormContainer formContainer = (RADVisualFormContainer) formComp;

    // a JFrame or Frame will be used (depending on form is Swing or AWT)
    Object formInstance = formContainer.getBeanInstance();
    Class frameClass = formInstance instanceof JComponent
                      || formInstance instanceof JFrame
                      || formInstance instanceof JDialog
                      || formInstance instanceof JApplet
                      || formInstance instanceof JWindow
                      || (!(formInstance instanceof Window)
                      && !(formInstance instanceof Panel))
                       ? JFrame.class : Frame.class;

    try{

      //frame = (Frame)FormDesigner.createContainerView(formContainer, frameClass);

      VisualReplicator replicator = new VisualReplicator(frameClass, null, 0);
      replicator.setTopMetaComponent(formContainer);
      frame = (Frame) replicator.createClone();

      // prepare close operation
      if (frame instanceof JFrame){

        ((JFrame)frame).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      }

      // set size
      if (formContainer.getFormSizePolicy() == RADVisualFormContainer.GEN_BOUNDS && formContainer.getGenerateSize()){

        frame.setSize(formContainer.getFormSize());
      }
      else{

        frame.pack();
      }

      // set location
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      frame.setLocation(screenSize.width+20 > frameSize.width ?
                       (screenSize.width - frameSize.width) / 2 : 0,
                        screenSize.height+20 > frameSize.height ?
                       (screenSize.height - frameSize.height) / 2 : 0);

      frame.show();

      try{ Thread.currentThread().sleep(200); } catch(InterruptedException e){}

      System.setProperty("a11ytest.excludes", options.getExcludes());
      AccessibilityTester tester = new AccessibilityTester(frame, options.getTests());
      if (options.doProperties()) tester.testProperties();
      if (options.doTraversal()) tester.testTraversal(frame);

      String filename = options.getFilename();

      if (filename != null){

        try{

          java.io.Writer writer = new java.io.FileWriter(filename);
          NetBeansReport report = new NetBeansReport(tester, replicator);
          report.getReport(writer);
        }
        catch (java.io.IOException e){

          // Do something about it or ignore it?
          // Could just default to Output Window?
        }
      }
      else{

        OutputWriter writer = TopManager.getDefault().getIO("Accessibility results").getOut();
        NetBeansReport report = new NetBeansReport(tester, replicator);
        report.getReport(writer);
      }
    }
    catch (Exception ex){

      if (Boolean.getBoolean("netbeans.debug.exceptions")){ // NOI18N

         ex.printStackTrace();
      }
    }

    if (frame != null){

      frame.setVisible(false);
      frame.dispose();
    }
  }
}
