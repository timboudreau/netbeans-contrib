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
        this.required =required;
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
