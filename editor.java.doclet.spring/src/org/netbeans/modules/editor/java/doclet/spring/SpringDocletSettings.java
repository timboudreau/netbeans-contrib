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
        
import org.netbeans.modules.editor.java.doclet.support.CompletionPrefixSettings;


/**
 * Settings for spring doclet descriptor.
 * @author leon
 */
public class SpringDocletSettings extends CompletionPrefixSettings {
    
    public String displayName() {
        // TODO - i18n
        return "Spring XDoclet completion";
    }
    
    public String getInitialValue() {
        return "@sp";
    }
}
