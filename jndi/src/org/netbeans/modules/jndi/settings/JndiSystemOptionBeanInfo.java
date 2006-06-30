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

    private static final String iconC16="/org/netbeans/modules/jndi/resources/jndi.gif";    // No I18N
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
            PropertyDescriptor[] pds=  new PropertyDescriptor[] { createPropertyDescriptor(JndiSystemOption.class, "timeOut",JndiRootNode.getLocalizedString("TITLE_TimeOut"),JndiRootNode.getLocalizedString("TIP_TimeOut")),  // NO I18N  
                                                                  createPropertyDescriptor(JndiSystemOption.class, "initialContexts",JndiRootNode.getLocalizedString("TITLE_InitialContexts"), JndiRootNode.getLocalizedString("TIP_InitialContexts"))};    //No I18N
            pds[1].setHidden (true);
            return pds;
        }catch (IntrospectionException ie) {return new PropertyDescriptor[0];}
    }

    private static PropertyDescriptor createPropertyDescriptor (Class clazz, String name, String displayName, String description) throws IntrospectionException {
        PropertyDescriptor descriptor = new PropertyDescriptor (name, clazz);
        descriptor.setShortDescription(description);
        descriptor.setDisplayName(displayName);
        descriptor.setBound (true);
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