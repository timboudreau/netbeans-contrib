/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.ioranalyzer;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import org.openide.util.NbBundle;


/** BeanInfo for IOR loader.
*
* @author Dusan Balek
*/
public final class IORDataLoaderBeanInfo extends SimpleBeanInfo {

    private static Image icon;

    /** Default constructor.
    */
    public IORDataLoaderBeanInfo() {
    }

    /** @param type Desired type of the icon
    * @return returns the IOR loader's icon
    */
    public Image getIcon(final int type) {
        if (icon == null)
            icon = loadImage("/org/netbeans/modules/corba/ioranalyzer/resources/ior.gif"); // NOI18N
        return icon;
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            return new BeanInfo[] {
                java.beans.Introspector.getBeanInfo(org.openide.loaders.UniFileLoader.class)
            };
        } catch (IntrospectionException e) {
            // ignore
        }
        return super.getAdditionalBeanInfo();
    }

}

