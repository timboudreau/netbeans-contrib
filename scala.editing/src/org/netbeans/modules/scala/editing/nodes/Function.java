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
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;
import org.netbeans.modules.scala.editing.nodes.types.WithTypeParams;

/**
 *
 * @author Caoyuan Deng
 */
public class Function extends AstDef  implements WithTypeParams {

    private List<TypeParam> typeParams;
    private List<Var> params;

    public Function(String name, Token idToken, AstScope bindingScope, ElementKind kind) {
        super(name, idToken, bindingScope, kind);
    }

    public void setTypeParam(List<TypeParam> typeParams) {
        this.typeParams = typeParams;
    }

    public List<TypeParam> getTypeParams() {
        return typeParams == null ? Collections.<TypeParam>emptyList() : typeParams;
    }

    public void assignTypeParams(List<TypeRef> typeArgs) {
        assert getTypeParams().size() == typeArgs.size();
        List<TypeParam> _typeParams = getTypeParams();
        for (int i = 0 ; i < _typeParams.size(); i++) {
            TypeParam typeParam = _typeParams.get(i);
            TypeRef typeArg = typeArgs.get(i);
            typeParam.setValue(typeArg);
        }
    }        
    
    public void setParam(List<Var> params) {
        this.params = params;
    }

    /**
     * @return null or params 
     */
    public List<Var> getParams() {
        return params;
    }

    @Override
    public boolean referredBy(AstRef ref) {
        if (ref instanceof FunRef) {
            FunRef funRef = (FunRef) ref;
            // only check local call only
            if (funRef.isLocal()) {
                return getName().equals(funRef.getCall().getName()) && params != null && params.size() == funRef.getArgs().size();
            }
        }

        return false;
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        super.htmlFormat(formatter);
        if (!getTypeParams().isEmpty()) {
            formatter.appendHtml("[");

            for (Iterator<TypeParam> itr = getTypeParams().iterator(); itr.hasNext();) {
                TypeParam typeParam = itr.next();
                typeParam.htmlFormat(formatter);

                if (itr.hasNext()) {
                    formatter.appendHtml(", ");
                }
            }

            formatter.appendHtml("]");
        }

        if (params != null) {
            formatter.appendHtml("(");
            if (!params.isEmpty()) {
                formatter.parameters(true);

                for (Iterator<Var> itr = getParams().iterator(); itr.hasNext();) {
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

        if (getType() != null) {
            formatter.appendHtml(" :");
            getType().htmlFormat(formatter);
        }
    }
}
