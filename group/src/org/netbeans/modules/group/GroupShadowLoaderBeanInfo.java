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


package org.netbeans.modules.group;


import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.openide.loaders.DataLoader;
import org.openide.TopManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/** <code>GroupShadow</code> bean info.
 *
 * @author  Martin Ryzl
 */
public class GroupShadowLoaderBeanInfo extends SimpleBeanInfo {

    /** Gets property descriptors. */
    public PropertyDescriptor[] getPropertyDescriptors () {
        try {
            PropertyDescriptor extensions = new PropertyDescriptor ("extensions", GroupShadowLoader.class); // NOI18N
            extensions.setDisplayName (NbBundle.getBundle (GroupShadowLoaderBeanInfo.class).getString ("PROP_Extensions"));
            extensions.setShortDescription (NbBundle.getBundle (GroupShadowLoaderBeanInfo.class).getString ("HINT_Extensions"));
            return new PropertyDescriptor[] { extensions };
        } catch (IntrospectionException ie) {
            TopManager.getDefault().getErrorManager().notify(ie);
            
            return null;
        }
    }

    /** Gets additional bean infos. */
    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (DataLoader.class) };
        } catch (IntrospectionException ie) {
            TopManager.getDefault().getErrorManager().notify(ie);
            
            return null;
        }
    }

    /** Gets icon. */
    public Image getIcon(int type) {
        if((type == java.beans.BeanInfo.ICON_COLOR_16x16) || (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            return Utilities.loadImage("org/netbeans/modules/group/resources/groupShadow.gif"); // NOI18N
        } else {
            return Utilities.loadImage("org/netbeans/modules/group/resources/groupShadow32.gif"); // NOI18N
        }
    }
}
