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

    private static String[] orbs = {""};

    public OrbPropertyEditor () {
        if (DEBUG)
            System.out.println ("OrbPropertyEditor ()...");
        CORBASupportSettings css = (CORBASupportSettings) CORBASupportSettings.findObject
                                   (CORBASupportSettings.class, true);
        java.util.Vector names = css.getNames ();
        int length = names.size ();
        if (DEBUG)
            System.out.println ("length: " + length);

        if (length > 0) {
            orbs = new String[length];
            for (int i = 0; i<length; i++) {
                orbs[i] = (String)names.elementAt (i);
                if (DEBUG)
                    System.out.println ("name: " + orbs[i]);
            }
        }
        if (DEBUG) {
            System.out.println ("first:");
            System.out.println ("names: " + orbs[0]);
            System.out.flush ();
        }
    }


    /** @return names of the supported orbs*/
    public String[] getTags() {
        return orbs;
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






