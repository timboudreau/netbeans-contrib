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
import java.util.List;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.scala.editing.nodes.types.Type;

/**
 *
 * @author Caoyuan Deng
 */
public class FunctionCall extends AstMirror {

    /** base may be AstExpression, FunctionCall, FieldCall, IdCall etc */
    private AstNode base;
    private TypeMirror baseType;
    private AstId call;
    private List<? extends AstNode> args;
    private boolean apply;

    public FunctionCall(Token pickToken) {
        super(null, pickToken);
    }

    public void setBase(AstNode base) {
        this.base = base;
    }

    public AstNode getBase() {
        return base;
    }
    
    public void setBaseType(TypeMirror baseType) {
        this.baseType = baseType;
    }
    
    public TypeMirror getBaseType() {
        if (baseType != null) {
            return baseType;
        } else {
            if (base != null) {
                return base.asType();
            }
        }
        
        return null;
    }

    public void setCall(AstId call) {
        this.call = call;
    }

    public AstId getCall() {
        return call;
    }

    public void setArgs(List<? extends AstNode> args) {
        this.args = args;
    }

    public List<? extends AstNode> getArgs() {
        return args == null ? Collections.<AstNode>emptyList() : args;
    }

    public boolean isLocal() {
        return base == null;
    }

    public void setApply() {
        apply = true;
    }

    public boolean isApply() {
        return apply;
    }

    @Override
    public Name getSimpleName() {
        StringBuilder sb = new StringBuilder();
        if (base != null) {
            TypeMirror baseType = base.asType();
            if (baseType != null) {
                sb.append(" :").append(Type.simpleNameOf(baseType));
            }
        }
        sb.append('.').append(call.getSimpleName());
        
        setSimpleName(sb);
        return super.getSimpleName();
    }

    // ----- Special FunctionCall
    public static class ApplyFunctionCall extends FunctionCall {

        public ApplyFunctionCall() {
            super(null);
        }

        @Override
        public Name getSimpleName() {
            return new BasicName("apply");
        }        
        
        @Override
        public int getPickOffset(TokenHierarchy th) {
            return getBase().getPickOffset(th);            
        }

        @Override
        public int getPickEndOffset(TokenHierarchy th) {
            return getBase().getPickEndOffset(th);            
        }
    }
}
