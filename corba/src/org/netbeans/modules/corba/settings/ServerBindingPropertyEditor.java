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

package org.netbeans.modules.corba.settings;

import java.beans.*;

import java.util.Vector;

import org.openide.util.NbBundle;

import org.openide.nodes.Node;
import org.openide.TopManager;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 April 16, 1999
*/

import org.netbeans.modules.corba.*;

public class ServerBindingPropertyEditor extends PropertyEditorSupport {
    //implements PropertyChangeListener {

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

    private static String[] _M_choices = {""};

    private ORBSettingsWrapper _M_settings;

    public ServerBindingPropertyEditor () {
	//Thread.currentThread ().dumpStack ();
	if (DEBUG)
	    System.out.println ("ServerBindingPropertyEditor () ...");
	/*
	  CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
	  (CORBASupportSettings.class, true);
	*/
	// joke :-))
	/*
	  Node[] __nodes = TopManager.getDefault ().getWindowManager ().getRegistry ()
	  .getActivated ().getActivatedNodes ();
	  for (int i=0; i<__nodes.length; i++)
	  if (DEBUG)
	  System.out.println ("choice: " + __nodes[i]);
	*/
	//choices = css.getServerBindingsChoices ();
	/*
	  css.addPropertyChangeListener (this);
	*/
	//css.setServerBinding (choices[0]);
	/*
	  for (int i=0; i<choices.length; i++)
	  if (DEBUG)
	  System.out.println ("choice: " + choices[i]);
	*/
    }


    /*
      public ServerBindingPropertyEditor (Object __settings) {
      if (DEBUG)
      System.out.println ("ServerBindingPropertyEditor (" + __settings + ") ...");
      _M_settings = (ORBSettings)__settings;
      }
    */
    /*
      public void setValue (Object __settings) {
      //Thread.currentThread ().dumpStack ();
      if (DEBUG)
      System.out.println 
      ("ServerBindingPropertyEditor::setValue (" 
      + __settings + " : " 
      + ((ORBSettingsWrapper)__settings).getSettings ().getServerBindings () 
      + ") ...");
      _M_settings = (ORBSettingsWrapper)__settings;
      String[] __tmp = new String[_M_settings.getSettings ().getServerBindings ().size ()];
      try {
      __tmp = (String[])_M_settings.getSettings ().getServerBindings ().toArray (__tmp);
      } catch (Exception e) {
      e.printStackTrace ();
      }
      setChoices (__tmp);
      System.out.println ("OK1");
      }


      public Object getValue () {
      System.out.println ("getValue () -> " + _M_settings);
      return _M_settings;
      }
    */
    /*
      public void setTags (String[] s) {
      if (DEBUG)
      System.out.println ("ServerBindingPropertyEditor::setTags (" + choices + ")");
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
    */

    /** @return names of the supported orbs*/
    public String[] getTags() {
	try {
	    if (DEBUG)
		System.out.println ("ServerBindingPropertyEditor::getTags () -> " + _M_choices);
	    _M_settings = (ORBSettingsWrapper)getValue ();
	    String[] __choices 
		= new String[_M_settings.getSettings ().getServerBindings ().size ()];
	    __choices = (String[])_M_settings.getSettings ().getServerBindings ().toArray 
		(__choices);
	    _M_choices = __choices;
	    return _M_choices;
	} catch (Exception e) {
	    e.printStackTrace ();
	}
	return new String[] {""};
    } 

    /*
      public void propertyChange (PropertyChangeEvent event) {
      
      if (event == null || event.getPropertyName () == null)
      return;
      
      if (DEBUG)
      System.out.println ("propertyChange in SBPE: " + event.getPropertyName ());
      if (event.getPropertyName ().equals ("orb")) {
      CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
      (CORBASupportSettings.class, true);
      //setChoices (css.getServerBindingsChoices ());
      //css.setServerBinding (getTags ()[0]);
      if (DEBUG) {
      for (int i=0; i<_M_choices.length; i++)
      System.out.println ("choice[" + i + "] in cb-editor: " + _M_choices[i]);
      }
      
      }

      }
    */


    /** @return text for the current value */
    public String getAsText () {
	try {
	    ORBSettingsWrapper __tmp = (ORBSettingsWrapper)getValue ();
	    if (DEBUG) {
		System.out.println ("ServerBindingPropertyEditor::getAsText () -> " 
				    + __tmp.getSettings () 
				    + __tmp.getSettings ().displayName () + " : " 
				    + __tmp.getValue ());
	    }
	    CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
		(CORBASupportSettings.class, true);
	    java.lang.Object[] __beans = css.getBeans ();
	    if (DEBUG) {
		for (int __i = 0; __i < __beans.length; __i++) {
		    System.out.println (__i + " : " + __beans[__i]);
		}
	    }
	    return ((ORBSettingsWrapper)getValue ()).getValue ();
	} catch (Exception __e) {
	    __e.printStackTrace ();
	}
	return "";
    }

    /** @param text A text for the current value. */
    public void setAsText (String __value) {
	if (DEBUG)
	    System.out.println ("ServerBindingPropertyEditor::setAsText (" + __value + ")");
        //((ORBSettingsWrapper)getValue ()).setValue (__value);
	setValue (new ORBSettingsWrapper (((ORBSettingsWrapper)getValue ()).getSettings (), 
					  __value));
    }
}

/*
 * <<Log>>
 *  14   Gandalf   1.13        3/7/00   Karel Gardas    naming service browser 
 *       bugfix
 *  13   Gandalf   1.12        11/4/99  Karel Gardas    - update from CVS
 *  12   Gandalf   1.11        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  11   Gandalf   1.10        10/1/99  Karel Gardas    updates from CVS
 *  10   Gandalf   1.9         8/3/99   Karel Gardas    
 *  9    Gandalf   1.8         7/10/99  Karel Gardas    
 *  8    Gandalf   1.7         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  7    Gandalf   1.6         5/28/99  Karel Gardas    
 *  6    Gandalf   1.5         5/28/99  Karel Gardas    
 *  5    Gandalf   1.4         5/22/99  Karel Gardas    fixed for reading 
 *       configuration from implementations files
 *  4    Gandalf   1.3         5/15/99  Karel Gardas    
 *  3    Gandalf   1.2         5/8/99   Karel Gardas    
 *  2    Gandalf   1.1         4/24/99  Karel Gardas    
 *  1    Gandalf   1.0         4/23/99  Karel Gardas    
 * $
 */






