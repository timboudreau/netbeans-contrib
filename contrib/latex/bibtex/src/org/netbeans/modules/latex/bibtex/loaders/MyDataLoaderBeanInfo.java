/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex.loaders;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.SimpleBeanInfo;

import org.openide.ErrorManager;
import org.openide.util.Utilities;

/** Description of {@link MyDataLoader}.
 *
 * @author Jan Lahoda
 */
public class MyDataLoaderBeanInfo extends SimpleBeanInfo {
    
    // If you have additional properties:
    /*
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor myProp = new PropertyDescriptor("myProp", MyDataLoader.class);
            myProp.setDisplayName(NbBundle.getMessage(MyDataLoaderBeanInfo.class, "PROP_myProp"));
            myProp.setShortDescription(NbBundle.getMessage(MyDataLoaderBeanInfo.class, "HINT_myProp"));
            return new PropertyDescriptor[] {myProp};
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }
     */
    
    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            // I.e. MultiFileLoader.class or UniFileLoader.class.
            return new BeanInfo[] {Introspector.getBeanInfo(MyDataLoader.class.getSuperclass())};
        } catch (IntrospectionException ie) {
            ErrorManager.getDefault().notify(ie);
            return null;
        }
    }
    
    public Image getIcon(int type) {
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            return Utilities.loadImage("org/netbeans/modules/latex/MyDataIcon.gif");
        } else {
            return Utilities.loadImage("org/netbeans/modules/latex/MyDataIcon32.gif");
        }
    }
    
}
