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
 */


package org.netbeans.modules.tasklist.copyright;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *  Bean info for TaskEdSettings
 * @author Tor Norbye
 */
public class CopyrightSettingsBeanInfo extends SimpleBeanInfo {

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bdesc = new BeanDescriptor(CopyrightSettings.class);
        bdesc.setDisplayName(NbBundle.getMessage(CopyrightSettingsBeanInfo.class,
                "OPTION_COPYRIGHT_SETTINGS_NAME"));   //NOI18N
        bdesc.setShortDescription(NbBundle.getMessage(CopyrightSettingsBeanInfo.class,
                "HINT_COPYRIGHT_SETTINGS_NAME"));   //NOI18N
        return bdesc;
    }

    /**
     *  Descriptor of valid properties.
     *
     *  @return array of properties
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        int i = 0;
        PropertyDescriptor[] desc = null;
        try {
            desc = new PropertyDescriptor[]{
                new PropertyDescriptor(CopyrightSettings.PROP_SCAN_COPYRIGHT,
                        CopyrightSettings.class)
            };

            desc[i].setDisplayName(NbBundle.getMessage(
                    CopyrightSettingsBeanInfo.class,
                    "PROP_SCAN_COPYRIGHT")); //NOI18N
            desc[i++].setShortDescription(NbBundle.getMessage(
                    CopyrightSettingsBeanInfo.class,
                    "HINT_SCAN_COPYRIGHT")); //NOI18N
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            throw new InternalError();
        }
        return desc;
    }

    /** (Placeholder) icon in options window */
    public Image getIcon(int type) {
        // XXX this icon is wrong
        return Utilities.loadImage("org/netbeans/modules/tasklist/copyright/settings.gif"); //NOI18N
    }
}
