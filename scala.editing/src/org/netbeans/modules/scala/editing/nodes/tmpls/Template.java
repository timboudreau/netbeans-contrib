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
package org.netbeans.modules.scala.editing.nodes.tmpls;

import java.util.Collections;
import java.util.List;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstRef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.AstId;
import org.netbeans.modules.scala.editing.nodes.IdRef;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class Template extends AstDef implements TypeElement {

    private boolean caseOne;
    private List<TypeRef> extendsWith;
    private List<TypeParam> typeParameters;

    protected Template(AstId id, AstScope bindingScope, ElementKind kind) {
        super(id.getSimpleName(), id.getPickToken(), bindingScope, kind);
    }

    public List<? extends TypeMirror> getInterfaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TypeMirror getSuperclass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTypeParameters(List<TypeParam> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public List<? extends TypeParam> getTypeParameters() {
        return typeParameters == null ? Collections.<TypeParam>emptyList() : typeParameters;
    }

    public void assignTypeParams(List<TypeRef> typeArgs) {
        assert getTypeParameters().size() == typeArgs.size();
        List<? extends TypeParam> _typeParams = getTypeParameters();
        for (int i = 0; i < _typeParams.size(); i++) {
            TypeParam typeParam = _typeParams.get(i);
            TypeRef typeArg = typeArgs.get(i);
            typeParam.setValue(typeArg);
        }
    }

    public void setCaseOne() {
        this.caseOne = true;
    }

    public boolean isCaseOne() {
        return caseOne;
    }

    public void setExtendsWith(List<TypeRef> extendsWith) {
        this.extendsWith = extendsWith;
    }

    public List<TypeRef> getExtendsWith() {
        return extendsWith == null ? Collections.<TypeRef>emptyList() : extendsWith;
    }

    @Override
    public boolean referredBy(AstRef ref) {
        if (ref instanceof TypeRef) {
            return getSimpleName().equals(ref.getSimpleName());
        } else if (ref instanceof IdRef) {
            if (isCaseOne()) {
                return getSimpleName().equals(ref.getSimpleName());
            }
        }

        return false;
    }
}
