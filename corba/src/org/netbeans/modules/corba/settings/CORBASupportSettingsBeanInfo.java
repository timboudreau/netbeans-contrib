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

import java.awt.Image;
import java.beans.*;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;
import org.netbeans.beaninfo.editors.NbProcessDescriptorEditor;


/** BeanInfo for CORBASupportSettings - defines property editor
*
* @author Karel Gardas
* @version 0.11, March 27, 1999
*/

import org.netbeans.modules.corba.*;

public class CORBASupportSettingsBeanInfo extends SimpleBeanInfo {
    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    /** Array of property descriptors. */
    private static PropertyDescriptor[] desc;

    // initialization of the array of descriptors
    static {
        try {
            desc = new PropertyDescriptor[] {
		new PropertyDescriptor ("_M_orb_name", CORBASupportSettings.class, // NOI18N
					"getOrb", "setOrb"), // NOI18N
		new PropertyDescriptor ("_M_orb_tag", CORBASupportSettings.class, // NOI18N
					"getORBTag", "setORBTag"), // NOI18N
		new PropertyDescriptor ("namingChildren", // NOI18N
					CORBASupportSettings.class, // NOI18N
					"getNamingServiceChildren", // NOI18N
					"setNamingServiceChildren"), // NOI18N
		new PropertyDescriptor ("IRChildren", CORBASupportSettings.class, // NOI18N
					"getInterfaceRepositoryChildren", // NOI18N
					"setInterfaceRepositoryChildren"), // NOI18N
		new PropertyDescriptor ("_S_implementations", // NOI18N
					CORBASupportSettings.class, // NOI18N
					"getBeans", "setBeans") // NOI18N
		    };
	    desc[0].setDisplayName (ORBSettingsBundle.PROP_ORB);
	    desc[0].setShortDescription (ORBSettingsBundle.HINT_ORB);
	    desc[0].setPropertyEditorClass (OrbPropertyEditor.class);
	    
	    // hidden options for serialization
	    desc[1].setHidden (true);  // ORB Serialization Tag
	    desc[2].setHidden (true);  // children of persistent NamingService Browser
	    desc[3].setHidden (true);  // children of persistent IR Browser
	    desc[4].setHidden (true);  // _M_implementations
	} catch (IntrospectionException ex) {
            //throw new InternalError ();
	    //if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
	    ex.printStackTrace ();
        }
    }

    /**
     * loads icons
     */
    public CORBASupportSettingsBeanInfo () {
    }

    /** Returns the ExternalCompilerSettings' icon */
    public Image getIcon(int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) 
	    || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage
		    ("/org/netbeans/modules/corba/settings/orb.gif"); // NOI18N
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage
		    ("/org/netbeans/modules/corba/settings/orb32.gif"); // NOI18N
            return icon32;
        }
    }

    /** Descriptor of valid properties
     * @return array of properties
     */
    public PropertyDescriptor[] getPropertyDescriptors () {
        return desc;
    }
}

