/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.metrics.options;

import java.beans.*;
import java.util.ResourceBundle;
import org.openide.util.NbBundle;

/**
 * Utility methods for metric settings' beaninfo classes.
 *
 * @author tball
 */
public class Util {

    private static ResourceBundle bundle = 
        NbBundle.getBundle (Util.class);

    public static PropertyDescriptor makeProperty(String name, 
						  Class cls, 
						  String getter, 
						  String setter,
						  String shortDesc, 
						  String longDesc) {
        try {
            PropertyDescriptor prop = 
                new PropertyDescriptor(name, cls, getter, setter);
            prop.setShortDescription(
                bundle.getString(longDesc));
            prop.setDisplayName(
                bundle.getString(shortDesc));
            return prop;
        } catch (IntrospectionException e) {
            throw new InternalError(
                // should never happen, unless Util gets corrupted
                "assertion failure creating beaninfo property: " + 
                e.getMessage());
        }
    }
}
