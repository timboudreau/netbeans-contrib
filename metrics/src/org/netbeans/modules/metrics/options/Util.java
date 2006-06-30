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
