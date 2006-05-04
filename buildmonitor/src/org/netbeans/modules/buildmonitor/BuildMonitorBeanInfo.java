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
 */


package org.netbeans.modules.buildmonitor;

import java.beans.*;
import org.openide.util.NbBundle;

/**
 * BeanInfo definition for the BuildMonitor class.
 *
 * @author Tom Ball
 */
public class BuildMonitorBeanInfo extends SimpleBeanInfo {
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(BuildMonitor.class);
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = new PropertyDescriptor[] { 
                getDescriptor("URL", false), //NOI18N
                getDescriptor("pollMinutes", false) //NOI18N
            };
        } catch (IntrospectionException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            descriptors = null;
        }
        return descriptors;
    }
    
    private static PropertyDescriptor getDescriptor(String property, boolean hidden) 
      throws IntrospectionException {
        PropertyDescriptor desc = new PropertyDescriptor(property, BuildMonitor.class);
        desc.setDisplayName(getString("PROP_" + property)); //NOI18N
        desc.setShortDescription(getString("HINT_" + property)); //NOI18N
        desc.setHidden(hidden);
        return desc;
    }
    
    public java.awt.Image getIcon(int iconKind) {
        switch (iconKind) {
            case ICON_COLOR_16x16:
                return loadImage("resources/build_passed.gif"); // NOI18N
        }
        return null;
    }
    
    private static String getString(String key) {
        return NbBundle.getMessage(BuildMonitorBeanInfo.class, key);
    }
}
