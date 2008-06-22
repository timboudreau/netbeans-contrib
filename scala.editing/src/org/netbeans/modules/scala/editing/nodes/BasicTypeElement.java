/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.scala.editing.nodes;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Caoyuan Deng
 */
public class BasicTypeElement implements TypeElement {
    private Name simpleName;
    private Name qualifiedName;

    public BasicTypeElement(String sName, Name qualifiedName) {
        this.simpleName = new BasicName(sName);
        this.qualifiedName = qualifiedName;
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name getQualifiedName() {
        return qualifiedName;
    }

    public TypeMirror getSuperclass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends TypeMirror> getInterfaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends TypeParameterElement> getTypeParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeMirror asType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ElementKind getKind() {
        return ElementKind.CLASS;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <A extends Annotation> A getAnnotation(Class<A> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Modifier> getModifiers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name getSimpleName() {
        return simpleName;
    }

    public Element getEnclosingElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends Element> getEnclosedElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <R, P> R accept(ElementVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
