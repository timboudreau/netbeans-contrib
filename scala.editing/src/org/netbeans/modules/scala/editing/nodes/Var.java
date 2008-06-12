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

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;
import org.netbeans.modules.gsf.api.HtmlFormatter;

/**
 *
 * @author Caoyuan Deng
 */
public class Var extends AstDef implements VariableElement {

    private boolean val;
    private boolean implicate;
    private AstExpr expr;

    public Var(AstId id, AstScope bindingScope, ElementKind kind) {
        super(id.getName(), id.getPickToken(), bindingScope, kind);
        setType(id.getType());
    }

    public Object getConstantValue() {
        return null;
    }

    public void setVal() {
        val = true;
    }

    public boolean isVal() {
        return val;
    }

    public void setImplicate() {
        implicate = true;
    }

    public boolean getImplicate() {
        return implicate;
    }

    public void setExpr(AstExpr expr) {
        this.expr = expr;
        getBindingScope().addExpr(expr);
    }

    @Override
    public boolean referredBy(AstRef ref) {
        if (ref instanceof IdRef) {
            return getName().equals(ref.getName());
        }
        
        return false;
    }

    @Override
    public TypeRef getType() {
        if (type != null) {
            return type;
        }

        if (expr != null) {
            return expr.getType();
        }

        return null;
    }

    @Override
    public boolean mayEqual(AstDef def) {
        switch (def.getKind()) {
            case LOCAL_VARIABLE:
            case PARAMETER:
            case FIELD:
                return getName().equals(def.getName());
            default:
                return false;
        }
    }

    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        super.htmlFormat(formatter);
        TypeRef myType = getType();
        if (myType != null) {
            formatter.type(true);
            formatter.appendHtml(" :");
            myType.htmlFormat(formatter);
            formatter.type(false);
        }
    }
}
