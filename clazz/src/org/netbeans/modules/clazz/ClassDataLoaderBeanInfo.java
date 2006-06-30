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

package org.netbeans.modules.clazz;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import org.openide.loaders.MultiFileLoader;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/** BeanInfo for class loader.
*
* @author Dafe Simonek
*/
public final class ClassDataLoaderBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (MultiFileLoader.class) };
        } catch (IntrospectionException ie) {
	    ErrorManager.getDefault().notify(ie);
            return null;
        }
    }



    /**
    * @return Returns an array of PropertyDescriptors
    * describing the editable properties supported by this bean.
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
	final ResourceBundle bundle =
            NbBundle.getBundle(ClassDataLoaderBeanInfo.class);
        try {
	    PropertyDescriptor[] descriptors =  new PropertyDescriptor[] {
                               new PropertyDescriptor ("extensions", ClassDataLoader.class, // NOI18N
                                                       "getExtensions", null) // NOI18N
                           };
            descriptors[0].setDisplayName(bundle.getString("PROP_Extensions"));
            descriptors[0].setShortDescription(bundle.getString("HINT_Extensions"));
    	    return descriptors;
        } catch (IntrospectionException e) {
	    ErrorManager.getDefault().notify(e);
	    return null;
        }
    }

    /** @param type Desired type of the icon
    * @return returns the Txt loader's icon
    */
    public Image getIcon(final int type) {
        if ((type == BeanInfo.ICON_COLOR_16x16) || (type == BeanInfo.ICON_MONO_16x16)) {
	    return Utilities.loadImage("org/netbeans/modules/clazz/resources/class.gif"); // NOI18N
        } else {
            return Utilities.loadImage ("org/netbeans/modules/clazz/resources/class32.gif"); // NOI18N
        }
    }
}
