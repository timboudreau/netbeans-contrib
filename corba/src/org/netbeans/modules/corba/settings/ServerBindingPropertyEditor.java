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

public class ServerBindingPropertyEditor extends PropertyEditorSupport 
   implements PropertyChangeListener {

   //public static final boolean DEBUG = true;
   public static final boolean DEBUG = false;

  /** array of choices of server binding  */

   /*
   private static final String[] choices = {CORBASupport.SERVER_NS, 
					    CORBASupport.SERVER_IOR_TO_FILE, 
					    CORBASupport.SERVER_IOR_TO_OUTPUT, 
					    CORBASupport.SERVER_BINDER
   };
   */

   private static String[] choices = {""};

   public ServerBindingPropertyEditor () {
      if (DEBUG)
	 System.out.println ("ServerBindingPropertyEditor () ...");
      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
	 (CORBASupportSettings.class, true);
      choices = css.getServerBindingsChoices ();
      css.addPropertyChangeListener (this);
      //css.setServerBinding (choices[0]);
      for (int i=0; i<choices.length; i++)
	 if (DEBUG)
	    System.out.println ("choice: " + choices[i]);
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


  /** @return names of the supported orbs*/
   public String[] getTags() {
      return choices;
   }

   public void propertyChange (PropertyChangeEvent event) {
      
      if (DEBUG)
      	 System.out.println ("propertyChange in SBPE: " + event.getPropertyName ());
      if (event.getPropertyName ().equals ("orb")) {
	 CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject 
	    (CORBASupportSettings.class, true);
	 setChoices (css.getServerBindingsChoices ());
	 css.setServerBinding (getTags ()[0]);
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
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    fixed for reading 
 *       configuration from implementations files
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






