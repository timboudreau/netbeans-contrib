/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */
package org.netbeans.modules.sysprops;

import java.io.IOException;
import java.util.ResourceBundle;

import org.openide.util.HelpCtx;
import org.openide.util.datatransfer.NewType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/** NewType that can create a new SystemProperty.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 * @version 1.0
 */
class SystemPropertyNewType extends NewType {

    /** ResourceBundle used in this class. */
    private static ResourceBundle bundle = NbBundle.getBundle (SystemPropertyNewType.class);
    
    /** Name of the Property. May be null. */
    private String propertyName = null;
    
    /**
     * Creates a new SystemPropertyNewType with a propertyName.
     */
    public SystemPropertyNewType (String propertyName) {
        this.propertyName = propertyName;
    }    
    
    /**
     * Returns the Name of this NewType.
     * @return a localized display name
     */
    public String getName () {
        return bundle.getString ("LBL_NewProp");
    }

    /**
     * Creates a new SystemProperty and refreshs the SystemProperties.
     * @throws IOException doesn't, actually
     */
    public void create () throws IOException {
        // create a new Dialog to ask the Name of the Property
        String title = bundle.getString ("LBL_NewProp_dialog");
        String msg = bundle.getString ("MSG_NewProp_dialog_key");

        NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine (msg, title);
        if (propertyName != null) {
            desc.setInputText (propertyName + ".");
        }

        DialogDisplayer.getDefault ().notify (desc);

        // return if the user has canceled the dialog
        String key = desc.getInputText ();
        if ("".equals (key)) return;

        // create a new Dialog to ast the Value of the Propertry
        msg = bundle.getString ("MSG_NewProp_dialog_value");
        desc = new NotifyDescriptor.InputLine (msg, title);
        // [PENDING] return if result is a cancel
        DialogDisplayer.getDefault ().notify (desc);
        String value = desc.getInputText ();

        // add the Property to the SystemProperties
        System.setProperty (key, value);

        // refresh the SystemProperties-Node(s)
        PropertiesNotifier.getDefault ().changed ();
    }
}
