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

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a tag's attribute. The attribute has a name, can
 * be required or not, has a type (text, bool and so on) and has a list of possible values.
 * @author leon chiver
 */
public final class AttributeDescriptor {
    
    private boolean required;
    
    private String name;
    
    private List/*<String>*/ values;
    
    private String type;
    
    public AttributeDescriptor(String name, boolean required, List values, String type) {
        this.name = name;
        this.required = required;
        this.values = values;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRequired() {
        return required;
    }

    /**
     * @return a list of strings containing the tag's possible values. May be null.
     */
    public List/*<String>*/ getValues() {
        return values;
    }
    
    /**
     * @return true if the attribute has values to be choosen from
     * @see #getValues()
     */
    public boolean hasValues() {
        return values != null && !values.isEmpty();
    }
    
    public String getType() {
        return type;
    }
}
