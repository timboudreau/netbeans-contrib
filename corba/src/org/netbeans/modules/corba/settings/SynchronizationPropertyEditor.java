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

/** property editor for synchro property of CORBASupportSettings class
*
* @author Karel Gardas
* @version 0.01 Nov 2, 1999
*/

import org.netbeans.modules.corba.*;

public class SynchronizationPropertyEditor extends PropertyEditorSupport {

    private static final String[] modes = {CORBASupport.SYNCHRO_DISABLE,
                                           CORBASupport.SYNCHRO_ON_UPDATE,
                                           CORBASupport.SYNCHRO_ON_SAVE};

    public String[] getTags() {
        return modes;
    }

    public String getAsText () {
        return (String) getValue();
    }

    public void setAsText (String text) {
        setValue(text);
    }
}

/*
 * $Log 
 * $
 */






