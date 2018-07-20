/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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