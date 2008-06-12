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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.lexer.Token;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElement extends AstNode implements Element {

    private ElementKind kind;

    public AstElement(ElementKind kind) {
        this(null, kind);
    }

    public AstElement(Token pickToken, ElementKind kind) {
        this(null, pickToken, kind);
    }

    public AstElement(String name, Token pickToken, ElementKind kind) {
        super(name, pickToken);
        this.kind = kind;
    }

    public <R, P> R accept(ElementVisitor<R, P> arg0, P arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends Element> getEnclosedElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element getEnclosingElement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Name getSimpleName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <A extends Annotation> A getAnnotation(Class<A> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeMirror asType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setKind(ElementKind kind) {
        this.kind = kind;
    }

    public ElementKind getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return getName() + "(kind=" + getKind() + ", type=" + getType() + ")";
    }
}
