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
package org.netbeans.modules.java.additional.refactorings.visitors;

import javax.lang.model.element.VariableElement;
import org.netbeans.api.java.source.TypeMirrorHandle;

/**
 *
 * @author Tim
 */
public class ParamDesc {
    private TypeMirrorHandle type;
    private String name;
    
    public ParamDesc(VariableElement e) {
        this.type = TypeMirrorHandle.create(e.asType());
        this.name = e.getSimpleName().toString();
    }
    
    public String getName() {
        return name;
    }
    
    public TypeMirrorHandle getType() {
        return type;
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ParamDesc other = (ParamDesc) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(
                other.name))) {
            return false;
        }
        return true;
    }
    
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    public String toString() {
        return name;
    }
}
