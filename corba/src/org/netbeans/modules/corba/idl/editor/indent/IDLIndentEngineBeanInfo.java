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
package org.netbeans.modules.corba.idl.editor.indent;

/**
 *
 * @author  tzezula
 */
import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.MethodDescriptor;
import java.beans.BeanDescriptor;
import org.openide.util.NbBundle;

public class IDLIndentEngineBeanInfo extends SimpleBeanInfo {
    
    private BeanDescriptor beanDescriptor;
    private PropertyDescriptor[] propertyDescriptors;
    private java.awt.Image icon;
    private java.util.ResourceBundle bundle;

    /** Creates new IDLindentEngineBeanInfo */
    public IDLIndentEngineBeanInfo() {
    }
    
    public BeanDescriptor getBeanDescriptor () {
        if (this.beanDescriptor == null) {
            this.beanDescriptor = new BeanDescriptor (IDLIndentEngine.class);
        }
        return this.beanDescriptor;
    }
    
    public PropertyDescriptor[] getPropertyDescriptors () {
        if (this.propertyDescriptors == null) {
            try {
                this.propertyDescriptors = new PropertyDescriptor[] {
                    new PropertyDescriptor("tabWidth",IDLIndentEngine.class)    // No I18N
                };
                this.propertyDescriptors[0].setBound(true);
                this.propertyDescriptors[0].setDisplayName(getLocalizedString("TXT_tabWidthDisplayName"));  // No I18N
                this.propertyDescriptors[0].setShortDescription(getLocalizedString("TXT_tabWidthShortDescription")); // No I18N
            }catch (java.beans.IntrospectionException introspectionException) {
                this.propertyDescriptors = new PropertyDescriptor[0];
            }
        }
        return this.propertyDescriptors;
    }
    
    public int getDefaultPropertyIndex () {
        return 0;
    }
    
    public java.awt.Image getIcon (int kind) {
        if (this.icon == null)
            this.icon = this.loadImage ("/org/netbeans/modules/editor/resources/indentEngine.gif"); // No I18N
        return icon;
    }
    
    protected String getLocalizedString (String message) {
        if (this.bundle == null)
            this.bundle = NbBundle.getBundle (IDLIndentEngineBeanInfo.class);
        return this.bundle.getString (message);
    }

}
