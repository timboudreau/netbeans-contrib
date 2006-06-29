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
 * Software is Leon Chiver. All Rights Reserved.
 */

package org.netbeans.modules.editor.java.doclet.ejb;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import org.netbeans.modules.editor.java.doclet.support.CompletionPrefixSettings;

/**
 * @author leon chiver
 */
public class EjbDocletSettingsBeanInfo extends SimpleBeanInfo {

    private PropertyDescriptor[] arr;

    public EjbDocletSettingsBeanInfo() throws IntrospectionException {
        arr = new PropertyDescriptor[1];
        arr[0] = CompletionPrefixSettings.getCompletionPrefixDescriptor(EjbDocletSettings.class);
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return arr;
    }
    
}
