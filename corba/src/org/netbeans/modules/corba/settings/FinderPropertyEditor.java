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

/** property editor for skel property CORBASupportSettings class
*
* @author Karel Gardas
* @version 0.01 March 8, 2001
*/

import org.netbeans.modules.corba.*;

public class FinderPropertyEditor extends PropertyEditorSupport {

    /** array of hosts */
    private static final String[] viewers = {ORBSettingsBundle.PACKAGE, ORBSettingsBundle.PACKAGE_AND_SUB_PACKAGES, ORBSettingsBundle.FILESYSTEM, ORBSettingsBundle.REPOSITORY};

    /** @return names of the supported LookAndFeels */
    public String[] getTags() {
        return viewers;
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

