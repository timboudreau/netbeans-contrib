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

package org.netbeans.modules.jndi.settings;

import java.awt.Image;
import java.beans.SimpleBeanInfo;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import org.netbeans.modules.jndi.JndiRootNode;
/** 
 *
 * @author  tzezula
 * @version 
 */
public class JndiSystemOptionBeanInfo extends SimpleBeanInfo {
  
  private static final String iconC16="/org/netbeans/modules/jndi/resources/jndi.gif";
  private static final String iconC32=null;
  private static final String iconM16=null;
  private static final String iconM32=null;

  /** Creates new JndiSystemOptionBeanInfo */
  public JndiSystemOptionBeanInfo() {
    super();
  }
  
  public BeanDescriptor getBeanDescriptor () {
     return new BeanDescriptor (JndiSystemOption.class);
  }
  
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    try{
      return new PropertyDescriptor[] { createPropertyDescriptor(JndiSystemOption.class, "timeOut",JndiRootNode.getLocalizedString("TITLE_TimeOut"),JndiRootNode.getLocalizedString("TIP_TimeOut"))};
    }catch (IntrospectionException ie) {return new PropertyDescriptor[0];}
  }
  
  private static PropertyDescriptor createPropertyDescriptor (Class clazz, String name, String displayName, String description) throws IntrospectionException {
    PropertyDescriptor descriptor = new PropertyDescriptor (name, clazz);
    descriptor.setShortDescription(description);
    descriptor.setDisplayName(displayName);
    return descriptor;
  }
  
  public int getDefaultProperyIndex() {
    return 0;
  }
  
  public EventSetDescriptor[] getEventSetDescriptors() {
    try{
      return new EventSetDescriptor[] {createEventSetDescriptor(JndiSystemOption.class, "propertyChangeListener",java.beans.PropertyChangeListener.class, "addPropertyChangeListener","removePropertyChangeListener","")};
    }catch(IntrospectionException ie) { return new EventSetDescriptor[0];}
  }
  
  private static EventSetDescriptor createEventSetDescriptor(Class clazz, String name, Class listenerClazz, String adder , String remover, String description) throws IntrospectionException {
    EventSetDescriptor descriptor = new EventSetDescriptor(clazz, name, listenerClazz, new String[0], adder, remover);
    descriptor.setShortDescription(description);
    return descriptor;
  }
  
  public int getDefaultEventIndex() {
    return 0;
  }
  
  public Image getIcon (int kind) {
    String name=null;
    switch (kind){
      case SimpleBeanInfo.ICON_COLOR_16x16:
        name = iconC16;
        break;
      case SimpleBeanInfo.ICON_COLOR_32x32:
        name = iconC32;
        break;
      case SimpleBeanInfo.ICON_MONO_16x16:
        name = iconM16;
        break;
      case SimpleBeanInfo.ICON_MONO_32x32:
        name = iconM32;
        break;
    }
    if (name != null)
      return loadImage(name);
    else return null;
  }
}