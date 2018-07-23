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
	    org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL, e);
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

