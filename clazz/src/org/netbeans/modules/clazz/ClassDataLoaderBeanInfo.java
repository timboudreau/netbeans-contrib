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
import org.openide.util.NbBundle;


/** BeanInfo for class loader.
*
* @author Dafe Simonek
*/
public final class ClassDataLoaderBeanInfo extends SimpleBeanInfo {

    public BeanInfo[] getAdditionalBeanInfo () {
        try {
            return new BeanInfo[] { Introspector.getBeanInfo (MultiFileLoader.class) };
        } catch (IntrospectionException ie) {
            if (Boolean.getBoolean ("netbeans.debug.exceptions")) // NOI18N
                ie.printStackTrace ();
            return null;
        }
    }

    /** Icons for compiler settings objects. */
    static Image icon;
    static Image icon32;

    /** Propertydescriptors */
    static PropertyDescriptor[] descriptors;


    /**
    * @return Returns an array of PropertyDescriptors
    * describing the editable properties supported by this bean.
    */
    public PropertyDescriptor[] getPropertyDescriptors () {
        if (descriptors == null) initializeDescriptors();
        return descriptors;
    }

    /** @param type Desired type of the icon
    * @return returns the Txt loader's icon
    */
    public Image getIcon(final int type) {
        if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) ||
                (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
            if (icon == null)
                icon = loadImage("/org/netbeans/modules/clazz/resources/class.gif"); // NOI18N
            return icon;
        } else {
            if (icon32 == null)
                icon32 = loadImage ("/org/netbeans/modules/clazz/resources/class32.gif"); // NOI18N
            return icon32;
        }
    }

    static void initializeDescriptors () {
        final ResourceBundle bundle =
            NbBundle.getBundle(ClassDataLoaderBeanInfo.class);
        try {
            descriptors =  new PropertyDescriptor[] {
                               new PropertyDescriptor ("extensions", ClassDataLoader.class, // NOI18N
                                                       "getExtensions", null) // NOI18N
                           };
            descriptors[0].setDisplayName(bundle.getString("PROP_Extensions"));
            descriptors[0].setShortDescription(bundle.getString("HINT_Extensions"));
        } catch (IntrospectionException e) {
            e.printStackTrace ();
        }
    }

}

/*
* Log
*  7    src-jtulach1.6         1/16/00  Jesse Glick     
*  6    src-jtulach1.5         1/13/00  David Simonek   i18n
*  5    src-jtulach1.4         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  4    src-jtulach1.3         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  3    src-jtulach1.2         3/22/99  Ian Formanek    Icons location fixed
*  2    src-jtulach1.1         3/22/99  Ian Formanek    Icons moved from 
*       modules/resources to this package
*  1    src-jtulach1.0         2/16/99  David Simonek   
* $
*/
