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
