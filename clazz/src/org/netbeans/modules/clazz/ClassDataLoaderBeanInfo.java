/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.developer.modules.loaders.clazz;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import com.netbeans.ide.util.NbBundle;


/** BeanInfo for class loader.
*
* @author Dafe Simonek
*/
public final class ClassDataLoaderBeanInfo extends SimpleBeanInfo {

  /** Icons for compiler settings objects. */
  static Image icon;
  static Image icon32;

  /** Propertydescriptors */
  static PropertyDescriptor[] descriptors;


  /** Default constructor.
  */
  public ClassDataLoaderBeanInfo() {
  }

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
        icon = loadImage("/com/netbeans/developer/modules/loaders/class/resources/class.gif");
      return icon;
    } else {
      if (icon32 == null)
        icon32 = loadImage ("/com/netbeans/developer/modules/loaders/class/resources/class32.gif");
      return icon32;
    }
  }

  static void initializeDescriptors () {
    final ResourceBundle bundle =
      NbBundle.getBundle(ClassDataLoaderBeanInfo.class);
    try {
      descriptors =  new PropertyDescriptor[] {
        new PropertyDescriptor ("displayName", ClassDataLoader.class,
                                "getDisplayName", null),
        new PropertyDescriptor ("extensions", ClassDataLoader.class,
                                "getExtensions", "setExtensions")
      };
      descriptors[0].setDisplayName(bundle.getString("PROP_Name"));
      descriptors[0].setShortDescription(bundle.getString("HINT_Name"));
      descriptors[1].setDisplayName(bundle.getString("PROP_Extensions"));
      descriptors[1].setShortDescription(bundle.getString("HINT_Extensions"));
    } catch (IntrospectionException e) {
      e.printStackTrace ();
    }
  }

}

/*
* Log
*  2    src-jtulach1.1         3/22/99  Ian Formanek    Icons moved from 
*       modules/resources to this package
*  1    src-jtulach1.0         2/16/99  David Simonek   
* $
*/
