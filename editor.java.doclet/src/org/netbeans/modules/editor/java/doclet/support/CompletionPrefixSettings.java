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

package org.netbeans.modules.editor.java.doclet.support;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import org.openide.options.SystemOption;

/**
 * @author leon chiver
 */
public abstract class CompletionPrefixSettings extends SystemOption {

    public final static String PROPERTY_COMPLETION_PREFIX = "completionPrefix";

    public String getCompletionPrefix() {
        return (String) getProperty(PROPERTY_COMPLETION_PREFIX);
    }

    public void setCompletionPrefix(String prefix) {
        putProperty(PROPERTY_COMPLETION_PREFIX, prefix, true);
    }

  protected void initialize() {
        super.initialize();
        putProperty(PROPERTY_COMPLETION_PREFIX, getInitialValue(), true);
    }
    
    public abstract String getInitialValue();
    
    public static PropertyDescriptor getCompletionPrefixDescriptor(Class settingsClass) 
            throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(
                PROPERTY_COMPLETION_PREFIX, settingsClass);
        // TODO - i18n
        pd.setDisplayName("Completion prefix");
        pd.setShortDescription("Tag prefix for which code completion delivers results");
        return pd;
    }
}
