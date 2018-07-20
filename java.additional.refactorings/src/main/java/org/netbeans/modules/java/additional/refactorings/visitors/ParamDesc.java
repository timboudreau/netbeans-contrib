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
        this (e.getSimpleName().toString(), 
                TypeMirrorHandle.create(e.asType()));
    }
    
    public ParamDesc (String name, TypeMirrorHandle type) {
        this.name = name;
        this.type = type;
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
