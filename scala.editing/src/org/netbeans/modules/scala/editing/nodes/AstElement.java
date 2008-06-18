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
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 * Element with AstNode information
 * 
 * Represents a program element such as a package, class, or method. Each element 
 * represents a static, language-level construct (and not, for example, a runtime 
 * construct of the virtual machine). 
 * 
 * @author Caoyuan Deng
 */
public abstract class AstElement extends AstNode implements Element {

    private ElementKind kind;
    private AstScope bindingScope;

    protected AstElement(CharSequence name, Token pickToken, AstScope bindingScope, ElementKind kind) {
        super(name, pickToken);
        this.kind = kind;
        if (bindingScope != null) {
            this.bindingScope = bindingScope;
            this.bindingScope.setBindingDef(this);
        }
    }

    public <R, P> R accept(ElementVisitor<R, P> arg0, P arg1) {
        return arg0.visit(this, arg1);
    }

    public List<? extends AstElement> getEnclosedElements() {
        if (bindingScope != null) {
            return bindingScope.getElements();
        } else {
            return Collections.<AstElement>emptyList();
        }
    }

    public AstElement getEnclosingElement() {
        return getEnclosingScope().getBindingElement();
    }

    public <A extends Annotation> A getAnnotation(Class<A> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
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
        return getSimpleName() + "(kind=" + getKind() + ", type=" + asType() + ")";
    }

    public AstScope getBindingScope() {
        assert bindingScope != null : toString() + ": Each definition should set binding scope!";
        return bindingScope;
    }

    public int getBoundsOffset(TokenHierarchy th) {
        return getBindingScope().getBoundsOffset(th);
    }

    public int getBoundsEndOffset(TokenHierarchy th) {
        return getBindingScope().getBoundsEndOffset(th);
    }

    public OffsetRange getRange(TokenHierarchy th) {
        return getBindingScope().getRange(th);
    }

    public boolean isMirroredBy(AstMirror mirror) {
        return getSimpleName().toString().equals(mirror.getSimpleName().toString());
    }

    public boolean mayEqual(AstElement element) {
        return getSimpleName().toString().equals(element.getSimpleName().toString());
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        super.htmlFormat(formatter);
        formatter.appendText(getSimpleName().toString());
    }

    public static boolean isMirroredBy(Element element, AstMirror mirror) {
        if (element instanceof ExecutableElement && mirror instanceof FunctionCall) {
            ExecutableElement function = (ExecutableElement) element;
            FunctionCall funCall = (FunctionCall) mirror;
            List<? extends VariableElement> params = function.getParameters();
            // only check local call only
            if (funCall.isLocal()) {
                return element.getSimpleName().toString().equals(funCall.getCall().getSimpleName().toString()) &&
                        params != null &&
                        params.size() == funCall.getArgs().size();
            } else {
                boolean containsVariableLengthArg = function.isVarArgs();
                if (element.getSimpleName().toString().equals(funCall.getCall().getSimpleName().toString()) || element.getSimpleName().toString().equals("apply") && funCall.isLocal()) {
                    if (params.size() == funCall.getArgs().size() || containsVariableLengthArg) {
                        return true;
                    }
                }

                return false;
            }
        } else if (element instanceof VariableElement) {
            if (element.getSimpleName().toString().equals(mirror.getSimpleName().toString())) {
                return true;
            }
        }

        return false;
    }
}
