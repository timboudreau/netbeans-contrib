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

package org.netbeans.modules.vcscore.grouping;

import java.awt.Image;
import java.beans.*;

import org.openide.util.NbBundle;

/** BeanInfo for VcsGroupSettings
*
* @author Milos Kleint
*/
public class VcsGroupSettingsBeanInfo extends SimpleBeanInfo {


    /**
    * loads icons
    */
    public VcsGroupSettingsBeanInfo() {
    }

    /** @return the  icon */
    //TODO new ICONS
    public Image getIcon(int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            return org.openide.util.Utilities.loadImage("org/netbeans/modules/vcscore/grouping/vcs_groups.png"); //NOI18N;
        } else {
            return null;
        }

    }

    /** Descriptor of valid properties
    * @return array of properties
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
           java.util.ResourceBundle bundle = NbBundle.getBundle(VcsGroupSettingsBeanInfo.class);
            
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
            org.openide.ErrorManager.getDefault().notify();
            return null;
        }
    }
}

