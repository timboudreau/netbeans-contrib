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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.scala.editing.nodes.types.WithTypeParams;

/**
 *
 * @author Caoyuan Deng
 */
public class Function extends AstElement implements WithTypeParams, ExecutableElement {

    private List<TypeParam> typeParameters;
    private List<Var> parameters;

    public Function(CharSequence name, Token pickToken, AstScope bindingScope, boolean isConstructor) {
        super(name, pickToken, bindingScope, ElementKind.METHOD);
        if (isConstructor) {
            setKind(ElementKind.CONSTRUCTOR);
        }
    }

    public AnnotationValue getDefaultValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<? extends TypeMirror> getThrownTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isVarArgs() {
        List<Var> params = getParameters();
        boolean containsVariableLengthArg = false;
        for (Var param : params) {
            String paramSName = param.getSimpleName().toString();
            TypeMirror paramType = param.asType();
            if (paramType != null) {
                String paramTypeSName = Type.simpleNameOf(paramType);
                if (paramTypeSName.endsWith("*")) {
                    containsVariableLengthArg = true;
                    break;
                }
            }
        }

        return containsVariableLengthArg;
    }

    public TypeMirror getReturnType() {
        return type;
    }

    public List<? extends TypeParam> getTypeParameters() {
        return typeParameters == null ? Collections.<TypeParam>emptyList() : typeParameters;
    }

    public void setTypeParameters(List<TypeParam> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public void assignTypeParameters(List<Type> typeArgs) {
        assert getTypeParameters().size() == typeArgs.size();
        List<? extends TypeParam> _typeParams = getTypeParameters();
        for (int i = 0; i < _typeParams.size(); i++) {
            TypeParam typeParam = _typeParams.get(i);
            Type typeArg = typeArgs.get(i);
            typeParam.setValue(typeArg);
        }
    }

    public void setParameters(List<Var> parameters) {
        this.parameters = parameters;
    }

    public List<Var> getParameters() {
        return parameters == null ? Collections.<Var>emptyList() : parameters;
    }

    @Override
    public boolean isMirroredBy(AstMirror mirror) {
        if (mirror instanceof FunctionCall) {
            FunctionCall funRef = (FunctionCall) mirror;
            // only check local call only
            if (funRef.isLocal()) {
                return getSimpleName().toString().equals(funRef.getCall().getSimpleName().toString()) &&
                        parameters != null &&
                        parameters.size() == funRef.getArgs().size();
            }
        }

        return false;
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        super.htmlFormat(formatter);
        if (!getTypeParameters().isEmpty()) {
            formatter.appendHtml("[");

            for (Iterator<? extends TypeParam> itr = getTypeParameters().iterator(); itr.hasNext();) {
                TypeParam typeParam = itr.next();
                typeParam.htmlFormat(formatter);

                if (itr.hasNext()) {
                    formatter.appendHtml(", ");
                }
            }

            formatter.appendHtml("]");
        }

        if (parameters != null) {
            formatter.appendHtml("(");
            if (!parameters.isEmpty()) {
                formatter.parameters(true);

                for (Iterator<Var> itr = getParameters().iterator(); itr.hasNext();) {
                    Var param = itr.next();
                    param.htmlFormat(formatter);

                    if (itr.hasNext()) {
                        formatter.appendHtml(", ");
                    }
                }

                formatter.parameters(false);
            }
            formatter.appendHtml(")");
        }
        
        TypeMirror retType = getReturnType();

        if (retType != null) {
            formatter.appendHtml(" :");
            if (type instanceof Type) {
                ((Type) retType).htmlFormat(formatter);
            }
        }
    }

    public static boolean isVarArgs(ExecutableElement function) {
        if (function instanceof Function) {
            return ((Function) function).isVarArgs();
        } else {
            if (function.isVarArgs()) {
                return true;
            } else {
                for (VariableElement param : function.getParameters()) {
                    TypeMirror paramType = param.asType();
                    String qName = Type.qualifiedNameOf(paramType);
                    if (qName != null && qName.equals("scala.Seq")) {
                        return true;
                    }
                }

                return false;
            }
        }
    }
}
