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

package org.netbeans.modules.assistant.settings;

import java.beans.*;

import org.openide.util.NbBundle;
import org.openide.util.*;
import org.openide.*;

public class AssistantSettingsBeanInfo extends SimpleBeanInfo {

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     * 
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean.  May return null if the
     * information should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will
     * belong to the IndexedPropertyDescriptor subclass of PropertyDescriptor.
     * A client of getPropertyDescriptors can use "instanceof" to check
     * if a given PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] properties = null;
        try {
            properties = new PropertyDescriptor[] {
                new PropertyDescriptor(AssistantSettings.PROP_HELP_CONTENT, AssistantSettings.class),  
                new PropertyDescriptor(AssistantSettings.PROP_LINKS_PER_CATEGORY, AssistantSettings.class),    
                new PropertyDescriptor(AssistantSettings.PROP_SEARCH_TYPE , AssistantSettings.class),               
            };
            properties[0].setDisplayName(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("PROP_HELP_CONTENT"));
            properties[0].setShortDescription(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("HINT_PROP_HELP_CONTENT"));
            properties[0].setPropertyEditorClass(HelpContentEditor.class);
            properties[1].setDisplayName(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("PROP_LINKS_PER_CATEGORY"));
            properties[1].setShortDescription(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("HINT_PROP_LINKS_PER_CATEGORY"));
            properties[2].setDisplayName(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("PROP_SEARCH_TYPE"));
            properties[2].setShortDescription(NbBundle.getBundle(AssistantSettingsBeanInfo.class).getString("HINT_PROP_SEARCH_TYPE"));
            properties[2].setPropertyEditorClass(SearchTypeEditor.class);
                       
        } catch (java.beans.IntrospectionException intrexc) {           
            ErrorManager manager = (ErrorManager)Lookup.getDefault().lookup(ErrorManager.class);
            manager.notify();
        }
        return properties;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.   Icon images
     * will typically be GIFs, but may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from
     * this method.
     * <p>
     * There are four possible flavors of icons (16x16 color,
     * 32x32 color, 16x16 mono, 32x32 mono).  If a bean choses to only
     * support a single icon we recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background
     * so they can be rendered onto an existing background.
     *
     * @param  iconKind  The kind of icon requested.  This should be
     *    one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32, 
     *    ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon.  May
     *    return null if no suitable icon is available.
     */
    public java.awt.Image getIcon(int iconKind) {
        switch ( iconKind ) {
            case ICON_COLOR_16x16:
                return Utilities.loadImage("org/netbeans/modules/assistant/resources/assistant.gif"); // NOI18N
            case ICON_COLOR_32x32:
                return Utilities.loadImage("org/netbeans/modules/assistant/resources/assistant.gif"); // NOI18N
        }
        return null;
    }

}

