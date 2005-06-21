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

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author leon chiver
 */
public final class TagDescriptor {
    
    private String name;
    
    public int maxOccurs = 1;
    
    private List/*<AttributeDescriptor>*/ attributeDescriptors;

    private String methodPrefix;
    
    public TagDescriptor(String name, String methodPrefix, List/*<AttributeDescriptor>*/ attributeDescriptors) {
        this.name = name;
        this.attributeDescriptors = attributeDescriptors;
        this.methodPrefix = methodPrefix;
    }
    
    public String getName() {
        return name;
    }
    
    public List/*<AttributeDescritor>*/ getAttributeDescriptors() {
        return attributeDescriptors;
    }
    
    public String getMethodPrefix() {
        return methodPrefix;
    }
    
    public boolean hasAttributes() {
        return attributeDescriptors != null && !attributeDescriptors.isEmpty();
    }
    
    public int getMaxOccurs() {
        return maxOccurs;
    }
    
    public void setMaxOccurs(int o) {
        this.maxOccurs = o;
    }

}
