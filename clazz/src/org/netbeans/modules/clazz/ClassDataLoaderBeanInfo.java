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

package org.netbeans.modules.clazz;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import org.openide.loaders.MultiFileLoader;
import org.openide.TopManager;
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
	    TopManager.getDefault().getErrorManager().notify(ie);
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
	    TopManager.getDefault().getErrorManager().notify(e);
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
