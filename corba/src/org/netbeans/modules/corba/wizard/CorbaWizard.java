/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.event.*;
import org.openide.*;
import org.openide.util.RequestProcessor;
import org.netbeans.modules.corba.wizard.panels.*;
import org.netbeans.modules.projects.NewProjectAction;
import java.io.IOException;
import java.lang.reflect.Method;

/** 
 *
 * @author  Tomas Zezula
 * @version 1.0
 */
public class CorbaWizard extends Object implements PropertyChangeListener, WizardDescriptor.Iterator {
  
  private static final boolean DEBUG = true;
//  private static final boolean DEBUG = flase;
  
  private int index;
  private Dialog dialog;
  private WizardDescriptor.Panel[] panels;
  private boolean locked;

  /** Creates new CorbaWizard */
  public CorbaWizard() {
    this.index = 0;
    this.panels = new WizardDescriptor.Panel[] {new ProjectPanel(), new PackagePanel() ,new StartPanel(), new ORBPanel(), new IDLPanel()};
  }
  
  
  /** Returns current panel
   *  @return WizardDescriptor.Panel
   */
  public WizardDescriptor.Panel current() {
    System.out.println("current");
    return panels[index];
  }
  
  /** Can the iterater return next panel
   *  @return boolean 
   */
  public boolean hasNext() {
    System.out.println("hasNext");
    return (index < this.panels.length-1);
  }
  
  /** Can the iterator return previous panel
   *  @return boolean
   */
  public boolean hasPrevious () {
    System.out.println("hasPrevious");
    return index > 1;
  }
  
  /** Returns total count of panels
   *  @return int count
   */
  private int totalCount () {
    System.out.println("totalCount");
    return this.panels.length;
  }
  
  /** Return index of current panel
   *  @return int current index
   */
  private int currentIndex () {
    System.out.println("currentIndex");
    return index;
  }
  
  /** Returns the name of Wizard
   *  @return String wizard name
   */
  public String name () {
    return CorbaWizardAction.getLocalizedString("TITLE_CorbaWizard");
  }
  
  /** Returns the next panel
   *  @return WizardDescriptor.Panel 
   */
  public synchronized void nextPanel () {
     
    if (this.index == 0){
	TopManager.getDefault().addPropertyChangeListener ( new PropertyChangeListener () {
	    public void propertyChange (PropertyChangeEvent event){
		TopManager.getDefault().removePropertyChangeListener (this);
		}
	    });	
	try {
	  Class newProjectActionClazz = Class.forName ("org.netbeans.modules.projects.NewProjectAction");
          NewProjectAction action = (NewProjectAction) newProjectActionClazz.newInstance();
          Method perform = newProjectActionClazz.getMethod ("performAction", new Class[0]);
          perform.invoke (action, new Object[0]);
       }catch (ClassNotFoundException cnfe){
         System.out.println("No Class, No Project :-(");
	}
	catch (java.lang.reflect.InvocationTargetException ite){
	}
	catch (IllegalAccessException iae){
	}
	catch (NoSuchMethodException nsme){
	}
	catch (InstantiationException ie){
	}
    }
    if (index < this.panels.length-1)
      this.index++;
  }
  
  /** Returns the previous panel
   *  @return WizardDescriptor.Panel
   */
  public synchronized void  previousPanel () {
    if (index > 1)
      this.index--;
  }
  
  /** Starts the wizard
   *  @see CorbaWizardAction
   */
  public void run () {
    if (DEBUG)
      System.out.println("Starting CORBA Wizard...");
    CorbaWizardData data = new CorbaWizardData ();
    WizardDescriptor descriptor = new WizardDescriptor (CorbaWizard.this, data);
    descriptor.setTitleFormat(new java.text.MessageFormat ("CORBA Wizard[{1}]"));
    descriptor.addPropertyChangeListener (CorbaWizard.this);
    dialog = TopManager.getDefault().createDialog (descriptor);
    dialog.show();
    if (descriptor.getValue() == WizardDescriptor.FINISH_OPTION){
      //Generate code here
      if (DEBUG)
	System.out.println("CORBA Wizard: generating...");
    }
  }
  
  /** Adds ChangeListener
   *  @param ChangeListener listener
   */
  public void addChangeListener (ChangeListener listener){
  }
  
  /** Removes ChangeListener
   * @param ChangeListener listener
   */
  public void removeChangeListener (ChangeListener listener){
  }
  
  /** Callback for CorbaWizardDescriptor
   *  @param PropertyChangeListener event
   */
  public void propertyChange(final PropertyChangeEvent event) {
    if (event.getPropertyName().equals(DialogDescriptor.PROP_VALUE)){
      Object option = event.getNewValue();
      if (option == WizardDescriptor.FINISH_OPTION || option == WizardDescriptor.CANCEL_OPTION){
        dialog.setVisible(false);
        dialog.dispose();
      }
    }
  }
  
  
}