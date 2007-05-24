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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import java.util.List;
import org.netbeans.modules.java.additional.refactorings.visitors.*;
import org.netbeans.api.java.source.TypeMirrorHandle;

/**
 *
 * @author Tim Boudreau
 */
final class Parameter implements Comparable <Parameter> {
    private final ParamDesc desc;
    private final String origTypeName;
    private final int pos;
    
    public Parameter (String name, TypeMirrorHandle type, int position, String typeName) {
        desc = new ParamDesc (name, type);
        this.pos = position;
        this.origTypeName = typeName;
    }
    
    public Parameter (String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
        this.desc = null;
        this.pos = -1;
        this.origTypeName = null;
        createDefaultValue();
    }
    
    private void createDefaultValue() {
        if (origTypeName != null && ChangeSignaturePanel.isPrimitiveTypeName(origTypeName)) {
            defaultValue = "-1"; //NOI18N
        } else {
            defaultValue = "null"; //NOI18N            
        }
    }
    
    public boolean isPositionChanged (List <Parameter> orig) {
        return pos != orig.indexOf (this);
    }
    
    public boolean isNew() {
        return desc == null;
    }
    
    private String defaultValue;
    public void setDefaultValue (String s) {
        if (isNew()) {
            s = s.trim().length() == 0 ? null : s;
            defaultValue = s;
        }
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public boolean isNameChanged () {
        return desc == null ? true : name != null && !name.equals(desc.getName());
    }
    
    public boolean isTypeChanged() {
        return desc == null ? true : typeName != null && !typeName.equals(origTypeName);
    }
    
    public String getName() {
        return name == null ? desc.getName() : name;
    }
    
    public TypeMirrorHandle getType() {
        return desc == null ? null : desc.getType();
    }
    
    public String getTypeName() {
        return typeName == null ? origTypeName : typeName;
    }
    
    public boolean isModified() {
        return isNameChanged() || isTypeChanged();
    }
    
    private String name;
    public void setName (String s) {
        if (s.trim().length() == 0) s = null;
        name = s;
    }
    
    private String typeName;
    public void setTypeName(String s) {
        if (s.trim().length() == 0) s = null;
        typeName = s;
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Parameter other = (Parameter) obj;
        if (!desc.equals(other.desc)) {
            return false;
        }
        return true;
    }
    
    public int getPosition() {
        return pos;
    }
    
    public int hashCode() {
        int result;
        if (desc == null) {
            result = typeName == null ? 0 : typeName.hashCode();
            result += name == null ? 0 : name.hashCode();
            result *= 31;
        } else {
            result = desc.hashCode() * 11;
        }
        return result;
    }
    
    public String toString() {
        return getName();
    }

    public int compareTo(Parameter o) {
        return pos - o.pos;
    }
}
