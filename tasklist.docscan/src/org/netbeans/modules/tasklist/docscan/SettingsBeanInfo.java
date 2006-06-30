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


package org.netbeans.modules.tasklist.docscan;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Bean info for Settings. It's used for SystemOption serialization.
 *
 * @author Tor Norbye
 */
public final class SettingsBeanInfo extends SimpleBeanInfo {

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bdesc = new BeanDescriptor(Settings.class);
        bdesc.setDisplayName(NbBundle.getMessage(SettingsBeanInfo.class,
                "OPTION_TASK_SETTINGS_NAME"));   //NOI18N
        bdesc.setShortDescription(NbBundle.getMessage(SettingsBeanInfo.class,
                "HINT_TASK_SETTINGS_NAME"));   //NOI18N
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
                new PropertyDescriptor(Settings.PROP_SCAN_SKIP,
                        Settings.class),
                new PropertyDescriptor(Settings.PROP_SCAN_TAGS,
                        Settings.class),
                new PropertyDescriptor(Settings.PROP_MODIFICATION_TIME, Settings.class),
                new PropertyDescriptor(Settings.PROP_USABILITY_LIMIT, Settings.class),
            };

            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "PROP_SCAN_SKIP"));	    //NOI18N
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "HINT_SCAN_SKIP"));	    //NOI18N
            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "PROP_SCAN_TAGS"));	    //NOI18N
            desc[i].setPropertyEditorClass(TaskTagEditor.class);
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,
                    "HINT_SCAN_TAGS"));	    //NOI18N
            desc[i++].setHidden(true);

            desc[i].setDisplayName(NbBundle.getMessage(
                    SettingsBeanInfo.class,"BK0002")); // NOI18N
            desc[i].setExpert(true);
            desc[i++].setShortDescription(NbBundle.getMessage(
                    SettingsBeanInfo.class,"BK0003")); // NOI18N
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            throw new InternalError();
        }
        return desc;
    }

    /** (Placeholder) icon in options window */
    public Image getIcon(int type) {
        // XXX this icon is wrong
        return Utilities.loadImage("org/netbeans/modules/tasklist/docscan/settings.gif"); //NOI18N
    }
}
