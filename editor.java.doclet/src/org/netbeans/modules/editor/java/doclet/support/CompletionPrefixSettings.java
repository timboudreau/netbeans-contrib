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
