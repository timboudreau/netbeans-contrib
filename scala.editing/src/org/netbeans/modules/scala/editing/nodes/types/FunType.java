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

package org.netbeans.modules.scala.editing.nodes.types;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.HtmlFormatter;
import org.netbeans.modules.scala.editing.nodes.AstScope;

/**
 *
 * @author Caoyuan Deng
 */
public class FunType extends Type {
    
    private Type lhs;
    private Type rhs;
    
    public FunType() {
        super(null, null, TypeKind.DECLARED);
    }
    
    public void setLhs(Type lhs) {
        this.lhs = lhs;
    }
    
    public Type getLhs() {
        return lhs;
    }
    
    public void setRhs(Type rhs) {
        this.rhs = rhs;
    }
    
    public Type getRhs() {
        return rhs;
    }

    @Override
    public AstScope getEnclosingScope() {
        return rhs.getEnclosingScope();
    }    

    /** Since idToken is null, we should implement getPickOffset */
    @Override
    public int getPickOffset(TokenHierarchy th) {
        return -1;
    }

    /** Since idToken is null, we should implement getPickEndOffset */
    @Override
    public int getPickEndOffset(TokenHierarchy th) {
        return -1;
    }    
    
    /** Since name is null, we should implement getSimpleName() */
    @Override
    public Name getSimpleName() {
        StringBuilder sb = new StringBuilder();
        if (lhs == null) {
            sb.append("(");
            sb.append(")");
        } else if (lhs instanceof ParamType && ((ParamType) lhs).getMore() == ParamType.More.ByName) {
            sb.append("(");
            sb.append(lhs.getSimpleName());
            sb.append(")");
        } else {
            sb.append(lhs.getSimpleName());
        }
        sb.append("=>");
        sb.append(rhs.getSimpleName());
        
        setSimpleName(sb);
        return super.getSimpleName();
    }
    
    
    @Override
    public void htmlFormat(HtmlFormatter formatter) {
        if (lhs == null) {
            formatter.appendText("(");
            formatter.appendText(")");
        } else if (lhs instanceof ParamType && ((ParamType) lhs).getMore() == ParamType.More.ByName) {
            formatter.appendText("(");
            lhs.htmlFormat(formatter);
            formatter.appendText(")");
        } else {
            lhs.htmlFormat(formatter);
        }
        formatter.appendText("\u21D2");
        rhs.htmlFormat(formatter);
    }    

}
