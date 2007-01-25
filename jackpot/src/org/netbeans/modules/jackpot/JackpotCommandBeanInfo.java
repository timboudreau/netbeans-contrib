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

package org.netbeans.modules.jackpot;

import java.beans.*;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class JackpotCommandBeanInfo extends SimpleBeanInfo {
    
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(JackpotCommand.class);
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = new PropertyDescriptor[] { 
                getDescriptor("inspector", true),
                getDescriptor("transformer", true),
                getDescriptor("description", true),
                getDescriptor("command", false)
            };
        } catch (IntrospectionException ex) {
            org.openide.ErrorManager.getDefault().notify(ex);
            descriptors = null;
        }
        return descriptors;
    }
    
    private static PropertyDescriptor getDescriptor(String property, boolean hidden) 
      throws IntrospectionException {
        PropertyDescriptor desc = new PropertyDescriptor(property, JackpotCommand.class);
        desc.setDisplayName(getString("PROP_" + property));
        desc.setShortDescription(getString("HINT_" + property));
        desc.setHidden(hidden);
        return desc;
    }
    
    public java.awt.Image getIcon(int iconKind) {
        switch (iconKind) {
            case ICON_COLOR_16x16:
                return loadImage("resources/refactoring.png"); // NOI18N
        }
        return null;
    }
    
    private static String getString(String key) {
        return NbBundle.getMessage(JackpotCommandBeanInfo.class, key);
    }
}
