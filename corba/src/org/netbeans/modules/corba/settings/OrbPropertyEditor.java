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

import org.openide.util.NbBundle;

/** property editor for viewer property AppletSettings class
*
* @author Karel Gardas
* @version 0.01 March 29, 1999
*/

import org.netbeans.modules.corba.*;

public class OrbPropertyEditor extends PropertyEditorSupport {

    /** array of orbs */
    //private static final String[] orbs = {CORBASupport.ORBIX, CORBASupport.VISIBROKER,
    //					CORBASupport.ORBACUS, CORBASupport.JAVAORB};

    //private static final boolean DEBUG = true;
    private static final boolean DEBUG = false;

    private static String[] orbs = {""}; // NOI18N

    public OrbPropertyEditor () {
        if (DEBUG)
            System.out.println ("OrbPropertyEditor ()..."); // NOI18N
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                   (CORBASupportSettings.class, true);
        java.util.Vector names = css.getNames ();
        int length = names.size ();
        if (DEBUG)
            System.out.println ("length: " + length); // NOI18N

        if (length > 0) {
            orbs = new String[length];
            for (int i = 0; i<length; i++) {
                orbs[i] = (String)names.elementAt (i);
                if (DEBUG)
                    System.out.println ("name: " + orbs[i]); // NOI18N
            }
        }
        if (DEBUG) {
            System.out.println ("first:"); // NOI18N
            System.out.println ("names: " + orbs[0]); // NOI18N
            System.out.flush ();
        }
    }


    /** @return names of the supported orbs*/
    public String[] getTags() {
        return orbs;
    }

    /** @return text for the current value */
    public String getAsText () {
	//System.out.println ("OrbPropertyEditor::getAsText () -> " + this.getValue());
        return (String)this.getValue();
    }

    /** @param text A text for the current value. */
    public void setAsText (String __value) {
	String __tmp = __value;
	//System.out.println ("OrbPropertyEditor::setAsText () <- " + __value);
	//if (__value.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
	//   __tmp = __value.substring 
	//	(0, __value.length () - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
	//}
	//System.out.println ("OrbPropertyEditor::setAsText (): setValue () <- " + __tmp);
        this.setValue (__tmp);
    }
}



