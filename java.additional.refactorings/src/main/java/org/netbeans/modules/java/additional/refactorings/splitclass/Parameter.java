/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.splitclass;

import org.netbeans.modules.java.additional.refactorings.splitclass.ChangeSignaturePanel;
import org.netbeans.modules.java.additional.refactorings.visitors.*;
import org.netbeans.api.java.source.TypeMirrorHandle;

/**
 * Model objects used in the table model in the UI to represent method
 * parameters.
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
    
    public String getOriginalTypeName() {
        return origTypeName;
    }
    
    private void createDefaultValue() {
        if (origTypeName != null && ChangeSignaturePanel.isPrimitiveTypeName(origTypeName)) {
            defaultValue = "-1"; //NOI18N
        } else {
            defaultValue = "null"; //NOI18N            
        }
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
    
    public String getOriginalName() {
        return desc == null ? null : desc.getName();
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
