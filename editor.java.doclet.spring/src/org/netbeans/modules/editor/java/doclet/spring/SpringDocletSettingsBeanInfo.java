/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet.spring;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.netbeans.modules.editor.java.doclet.support.CompletionPrefixSettings;

/**
 * @author leon chiver
 */
public class SpringDocletSettingsBeanInfo extends SimpleBeanInfo {
    
    private PropertyDescriptor[] arr;
    
    public SpringDocletSettingsBeanInfo() throws IntrospectionException {
        arr = new PropertyDescriptor[1];
        arr[0] = CompletionPrefixSettings.getCompletionPrefixDescriptor(SpringDocletSettings.class);
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return arr;
    }
    
}
