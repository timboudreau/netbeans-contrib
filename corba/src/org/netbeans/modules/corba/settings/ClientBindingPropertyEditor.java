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

package com.netbeans.enterprise.modules.corba.settings;

import java.beans.*;

import com.netbeans.ide.util.NbBundle;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 April 16, 1999
*/

import com.netbeans.enterprise.modules.corba.*;

public class ClientBindingPropertyEditor extends PropertyEditorSupport 
   implements PropertyChangeListener {

   //public static final boolean DEBUG = true;
   public static final boolean DEBUG = false;


  /** array of orbs */
   /*
  private static final String[] choices = {CORBASupport.CLIENT_NS, 
					   CORBASupport.CLIENT_IOR_FROM_FILE, 
					   CORBASupport.CLIENT_IOR_FROM_INPUT,
					   CORBASupport.CLIENT_BINDER};
   */

   private static String[] choices = {""};


   public ClientBindingPropertyEditor () {
      if (DEBUG)
	 System.out.println ("ClientBindingPropertyEditor () ...");
      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
	 (CORBASupportSettings.class, true);
      choices = css.getClientBindingsChoices ();
      css.addPropertyChangeListener (this);
      //css.setClientBinding (choices[0]);
      for (int i=0; i<choices.length; i++)
	 if (DEBUG)
	    System.out.println ("choice: " + choices[i]);
   }


  /** @return names of the supported orbs*/
  public String[] getTags () {
    return choices;
  }

   public void setTags (String[] s) {
      String[] old = choices;
      choices = s;
      //firePropertyChange ("choices", (Object)old, (Object)choices);
      //CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
      //	 (CORBASupportSettings.class, true);
      //css.fireChangeChoices ();
   }

   public void setChoices (String[] s) {
      setTags (s);
   }

   public void propertyChange (PropertyChangeEvent event) {
      
      if (DEBUG)
      	 System.out.println ("propertyChange in CBPE: " + event.getPropertyName ());
      if (event.getPropertyName ().equals ("orb")) {
	 CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
	    (CORBASupportSettings.class, true);
	 setChoices (css.getClientBindingsChoices ());
	 css.setClientBinding (getTags ()[0]);
	 if (DEBUG) {
	    for (int i=0; i<choices.length; i++)
	       System.out.println ("choice[" + i + "] in cb-editor: " + choices[i]);
	 }
	 
      }
      
   }


  /** @return text for the current value */
  public String getAsText () {
    return (String) getValue();
  }

  /** @param text A text for the current value. */
  public void setAsText (String text) {
      setValue(text);
  }
}

/*
 * <<Log>>
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






