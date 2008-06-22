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
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstMirror;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.AstId;
import org.netbeans.modules.scala.editing.nodes.BasicName;
import org.netbeans.modules.scala.editing.nodes.IdCall;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import org.netbeans.modules.scala.editing.nodes.types.Type;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class Template extends AstElement implements TypeElement {

    private boolean caseOne;
    private Name qualifiedName;
    private Type superClass;
    private List<Type> withTraits;
    private List<TypeParam> typeParameters;

    protected Template(AstId id, AstScope bindingScope, ElementKind kind) {
        super(id.getSimpleName(), id.getPickToken(), bindingScope, kind);
    }

    public List<Type> getInterfaces() {
        return withTraits == null ? Collections.<Type>emptyList() : withTraits;
    }

    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Type getSuperclass() {
        /** @todo if superClass is null, return "java.lang.Object" ? or just null */
        return superClass;
    }

    public List<? extends TypeParam> getTypeParameters() {
        return typeParameters == null ? Collections.<TypeParam>emptyList() : typeParameters;
    }

    public void setQualifiedName(CharSequence qName) {
        if (qName != null) {
            if (qName instanceof Name) {
                this.qualifiedName = (Name) qName;
            } else {
                this.qualifiedName = new BasicName(qName);
            }
        } else {
            this.qualifiedName = null;
        }
    }

    public Name getQualifiedName() {
        if (qualifiedName == null) {
            Packaging packaging = getPackageElement();
            qualifiedName = packaging == null ? getSimpleName() : new BasicName(packaging.getQualifiedName() + "." + getSimpleName());
        }

        return qualifiedName;
    }

    public void assignTypeParams(List<Type> typeArgs) {
        assert getTypeParameters().size() == typeArgs.size();
        List<? extends TypeParam> _typeParams = getTypeParameters();
        for (int i = 0; i < _typeParams.size(); i++) {
            TypeParam typeParam = _typeParams.get(i);
            Type typeArg = typeArgs.get(i);
            typeParam.setValue(typeArg);
        }
    }

    public void setCaseOne() {
        this.caseOne = true;
    }

    public boolean isCaseOne() {
        return caseOne;
    }

    public void setSuperClass(Type superClass) {
        this.superClass = superClass;
    }

    public void setWithTraits(List<Type> withTraits) {
        this.withTraits = withTraits;
    }

    public void setTypeParameters(List<TypeParam> typeParameters) {
        this.typeParameters = typeParameters;
    }

    @Override
    public boolean isMirroredBy(AstMirror mirror) {
        if (mirror instanceof Type) {
            return getSimpleName().toString().equals(mirror.getSimpleName().toString());
        } else if (mirror instanceof IdCall) {
            if (isCaseOne()) {
                return getSimpleName().toString().equals(mirror.getSimpleName().toString());
            }
        }

        return false;
    }
}
