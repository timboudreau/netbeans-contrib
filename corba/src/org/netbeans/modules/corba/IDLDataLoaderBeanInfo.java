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

package com.netbeans.enterprise.modules.corba;

import java.beans.*;
import java.awt.Image;
import java.util.ResourceBundle;

import com.netbeans.ide.util.NbBundle;


/** BeanInfo for IDL loader.
*
* @author Karel Gardas
*/
public final class IDLDataLoaderBeanInfo extends SimpleBeanInfo {

  /** Icons for compiler settings objects. */
  private static Image icon;
  private static Image icon32;

  /** Propertydescriptors */
  private static PropertyDescriptor[] descriptors;


  /** Default constructor.
  */
  public IDLDataLoaderBeanInfo() {
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
  * @return returns the Idl loader's icon
  */
  public Image getIcon(final int type) {
    if ((type == java.beans.BeanInfo.ICON_COLOR_16x16) ||
        (type == java.beans.BeanInfo.ICON_MONO_16x16)) {
      if (icon == null)
        icon = loadImage("/com/netbeans/enterprise/modules/corba/settings/idl.gif");
      return icon;
    } else {
      if (icon32 == null)
        icon32 = loadImage ("/com/netbeans/enterprise/modules/corba/settings/idl32.gif");
      return icon32;
    }
  }

  private static void initializeDescriptors () {
    final ResourceBundle bundle =
      NbBundle.getBundle(IDLDataLoaderBeanInfo.class);
    try {
      descriptors =  new PropertyDescriptor[] {
        new PropertyDescriptor ("displayName", IDLDataLoader.class,
                                "getDisplayName", null),
        new PropertyDescriptor ("extensions", IDLDataLoader.class,
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
* <<Log>>
*  5    Gandalf   1.4         5/22/99  Karel Gardas    
*  4    Gandalf   1.3         5/15/99  Karel Gardas    
*  3    Gandalf   1.2         5/8/99   Karel Gardas    
*  2    Gandalf   1.1         4/24/99  Karel Gardas    
*  1    Gandalf   1.0         4/23/99  Karel Gardas    
* $
*/
