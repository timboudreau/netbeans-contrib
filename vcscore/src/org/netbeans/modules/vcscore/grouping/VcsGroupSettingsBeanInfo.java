/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.grouping;

import java.awt.Image;
import java.beans.*;

import org.openide.util.NbBundle;

/** BeanInfo for VcsGroupSettings
*
* @author Milos Kleint
*/
public class VcsGroupSettingsBeanInfo extends SimpleBeanInfo {

    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    static final java.util.ResourceBundle bundle =
        NbBundle.getBundle(VcsGroupSettingsBeanInfo.class);


    /**
    * loads icons
    */
    public VcsGroupSettingsBeanInfo() {
    }

    /** @return the  icon */
    //TODO new ICONS
    public Image getIcon(int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage("/org/netbeans/modules/vcscore/grouping/MainVcsGroupNodeIcon.gif"); // NOI18N
            return icon;
        } else {
            return icon32;
        }

    }

    /** Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            
            PropertyDescriptor autoAddition = new PropertyDescriptor ("autoAddition", VcsGroupSettings.class); // NOI18N
            autoAddition.setDisplayName (bundle.getString("PROP_autoAddition")); // NOI18N
            autoAddition.setShortDescription (bundle.getString("HINT_autoAddition"));      // NOI18N       
            autoAddition.setPropertyEditorClass(AutoAdditionPropertyEditor.class);
            
            PropertyDescriptor showLinks = new PropertyDescriptor ("showLinks", VcsGroupSettings.class); // NOI18N
            showLinks.setDisplayName (bundle.getString("PROP_showLinks")); // NOI18N
            showLinks.setShortDescription (bundle.getString("HINT_showLinks"));      // NOI18N       
            
            PropertyDescriptor disableGroups = new PropertyDescriptor ("disableGroups", VcsGroupSettings.class); // NOI18N
            disableGroups.setDisplayName (bundle.getString("PROP_disableGroups")); // NOI18N
            disableGroups.setShortDescription (bundle.getString("HINT_disableGroups"));      // NOI18N       
            
            
            return new PropertyDescriptor[] {autoAddition, showLinks, disableGroups }; 
            
        } catch (IntrospectionException ie) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
                ie.printStackTrace ();
            return null;
        }
    }
}

