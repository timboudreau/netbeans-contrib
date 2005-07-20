/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Jesse Glick, Michael Ruflin
 */

package org.netbeans.modules.sysprops;

import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.NewType;

/** NewType that can create a new SystemProperty.
 *
 * @author Jesse Glick
 * @author Michael Ruflin
 */
class SystemPropertyNewType extends NewType {
    
    /** Name of the Property. May be null. */
    private String propertyName = null;
    
    /**
     * Creates a new SystemPropertyNewType with a propertyName.
     */
    public SystemPropertyNewType(String propertyName) {
        this.propertyName = propertyName;
    }
    
    /**
     * Returns the Name of this NewType.
     * @return a localized display name
     */
    public String getName() {
        return NbBundle.getMessage(SystemPropertyNewType.class, "LBL_NewProp");
    }
    
    /**
     * Creates a new SystemProperty and refreshs the SystemProperties.
     * @throws IOException doesn't, actually
     */
    public void create() throws IOException {
        // create a new Dialog to ask the Name of the Property
        String title = NbBundle.getMessage(SystemPropertyNewType.class, "LBL_NewProp_dialog");
        String msg = NbBundle.getMessage(SystemPropertyNewType.class, "MSG_NewProp_dialog_key");
        
        NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine(msg, title);
        if (propertyName != null) {
            desc.setInputText(propertyName + ".");
        }
        
        if (!DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
            // User cancelled.
            return;
        }
        
        String key = desc.getInputText();
        
        // create a new Dialog to ast the Value of the Propertry
        msg = NbBundle.getMessage(SystemPropertyNewType.class, "MSG_NewProp_dialog_value");
        desc = new NotifyDescriptor.InputLine(msg, title);
        if (!DialogDisplayer.getDefault().notify(desc).equals(NotifyDescriptor.OK_OPTION)) {
            return;
        }
        String value = desc.getInputText();
        
        // add the Property to the SystemProperties
        System.setProperty(key, value);
        
        // refresh the SystemProperties-Node(s)
        PropertiesNotifier.getDefault().changed();
    }
    
}
