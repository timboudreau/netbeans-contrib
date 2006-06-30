/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
    
    private static boolean initialized = false;
    private static String[] orbs; // NOI18N
    private static String[] ext_orbs; // NOI18N
    
    private boolean extended = false;
    
    public OrbPropertyEditor() {
        if (DEBUG)
            System.out.println("OrbPropertyEditor ()..."); // NOI18N
        if (!initialized) {
            CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
            (CORBASupportSettings.class, true);
            java.util.Vector names = css.getNames();
            int length = names.size();
            if (DEBUG)
                System.out.println("length: " + length); // NOI18N
            orbs = new String[length];
            ext_orbs = new String[length + 1];
            ext_orbs[0] = ORBSettingsBundle.CTL_DEFAULT_ORB;
            for (int i = 0; i<length; i++) {
                orbs[i] = (String)names.elementAt(i);
                ext_orbs[i+1] = (String)names.elementAt(i);
                if (DEBUG)
                    System.out.println("name: " + orbs[i]); // NOI18N
            }
            initialized = true;
        }
    }
    
    public OrbPropertyEditor(boolean _extended) {
        this();
        extended = _extended;
    }
    
    /** @return names of the supported orbs*/
    public String[] getTags() {
        return (extended) ? ext_orbs : orbs;
    }
    
    /** @return text for the current value */
    public String getAsText() {
        //System.out.println ("OrbPropertyEditor::getAsText () -> " + this.getValue());
        String name = (String) this.getValue();
        return name != null ? name : ORBSettingsBundle.CTL_DEFAULT_ORB;
    }
    
    /** @param text A text for the current value. */
    public void setAsText(String __value) {
        String __tmp = null;
        if (!extended || !ORBSettingsBundle.CTL_DEFAULT_ORB.equals(__value))
            __tmp = __value;
        //System.out.println ("OrbPropertyEditor::setAsText () <- " + __value);
        //if (__value.endsWith (ORBSettingsBundle.CTL_UNSUPPORTED)) {
        //   __tmp = __value.substring
        //	(0, __value.length () - (ORBSettingsBundle.CTL_UNSUPPORTED.length () + 1));
        //}
        //System.out.println ("OrbPropertyEditor::setAsText (): setValue () <- " + __tmp);
        this.setValue(__tmp);
    }
}



