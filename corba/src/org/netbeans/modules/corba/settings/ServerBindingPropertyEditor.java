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
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

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

    private static String[] _M_choices = {""}; // NOI18N

    private ORBSettingsWrapper _M_settings;

    public ServerBindingPropertyEditor () {
	//Thread.currentThread ().dumpStack ();
	if (DEBUG)
	    System.out.println ("ServerBindingPropertyEditor () ..."); // NOI18N
    }

    /** @return names of the supported orbs*/
    public String[] getTags() {
	try {
	    if (DEBUG)
		System.out.println ("ServerBindingPropertyEditor::getTags () -> " + _M_choices); // NOI18N
	    _M_settings = (ORBSettingsWrapper)getValue ();
	    List __bindings = _M_settings.getSettings ().getServerBindings ();
	    String[] __choices = new String[__bindings.size ()];
	    ORBBindingDescriptor __binding = null;
	    for (int __i=0; __i<__bindings.size (); __i++) {
		__binding = (ORBBindingDescriptor)__bindings.get (__i);
		__choices[__i] = _M_settings.getSettings ().getLocalizedString
		    (__binding.getName ());
	    }
		
	    //__choices = (String[])_M_settings.getSettings ().getServerBindings ().toArray 
	    //(__choices);
	    _M_choices = __choices;
	    return _M_choices;
	} catch (Exception e) {
	    if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
		e.printStackTrace ();
	}
	return new String[] {""}; // NOI18N
    } 

    /** @return text for the current value */
    public String getAsText () {
	try {
	    ORBSettingsWrapper __tmp = (ORBSettingsWrapper)getValue ();
	    //System.out.println ("__tmp: " + __tmp + " : " + System.identityHashCode (__tmp));
	    ORBSettings __settings = __tmp.getSettings ();
	    //System.out.println ("settings: " + __settings);
	    //System.out.println ("settings: " + System.identityHashCode (__settings));

	    return __settings.getLocalizedString (__tmp.getValue ());
	    //return ((ORBSettingsWrapper)getValue ()).getValue ();
	} catch (Exception __e) {
	    //if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
	    //__e.printStackTrace ();
	}
	return ""; // NOI18N
    }

    /** @param text A text for the current value. */
    public void setAsText (String __value) {
	if (DEBUG)
	    System.out.println ("ServerBindingPropertyEditor::setAsText (" + __value + ")"); // NOI18N
	_M_settings = (ORBSettingsWrapper)getValue ();
	List __bindings = _M_settings.getSettings ().getServerBindings ();
	List __localized_bindings = new LinkedList ();
	Iterator __iterator = __bindings.iterator ();
	ORBBindingDescriptor __binding = null;
	ORBSettings __settings = _M_settings.getSettings ();
	while (__iterator.hasNext ()) {
	    __binding = (ORBBindingDescriptor)__iterator.next ();
	    __localized_bindings.add (__settings.getLocalizedString (__binding.getName ()));
	}
	__binding = (ORBBindingDescriptor)__bindings.get
	    (__localized_bindings.indexOf (__value));
	String __not_locallized_value = __binding.getName ();
	//System.out.println ("-> " + __not_locallized_value);
	this.setValue (new ORBSettingsWrapper (__settings, __not_locallized_value));
        //((ORBSettingsWrapper)getValue ()).setValue (__value);
	/*
	  this.setValue (new ORBSettingsWrapper (((ORBSettingsWrapper)this.getValue ()).getSettings (), 
	  __value));
	*/
    }

    /*
      public void setValue (Object __object) {
      super.setValue (__object);
      System.out.println ("set value: " + __object);
      }
    */

}

