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
package org.netbeans.modules.fortress.editing.visitors;

import com.sun.fortress.nodes.AbsFnDecl;
import com.sun.fortress.nodes.AbsObjectDecl;
import com.sun.fortress.nodes.AbsTraitDecl;
import com.sun.fortress.nodes.Component;
import com.sun.fortress.nodes.FnDef;
import com.sun.fortress.nodes.Id;
import com.sun.fortress.nodes.LValue;
import com.sun.fortress.nodes.LValueBind;
import com.sun.fortress.nodes.LocalVarDecl;
import com.sun.fortress.nodes.MethodInvocation;
import com.sun.fortress.nodes.Node;
import com.sun.fortress.nodes.NodeDepthFirstVisitor_void;
import com.sun.fortress.nodes.ObjectDecl;
import com.sun.fortress.nodes.SimpleName;
import com.sun.fortress.nodes.TraitDecl;
import com.sun.fortress.nodes.VarDecl;
import com.sun.fortress.nodes.VarRef;
import java.util.List;
import java.util.Stack;
import org.netbeans.modules.gsf.api.ElementKind;

/**
 *
 * @author Caoyuan Deng
 */
public class SignatureVisitor extends NodeDepthFirstVisitor_void {

    private Scope rootScope;
    private Stack<Scope> scopeStack = new Stack<Scope>();

    public SignatureVisitor(Scope rootCtx) {
        this.rootScope = rootCtx;
        scopeStack.push(rootCtx);
    }

    @Override
    public void defaultCase(Node that) {
        //System.out.println("Default: " + that.stringName());
    }

    @Override
    public void forComponent(Component that) {
        Scope scope = new Scope(that);
        List<Id> paths = that.getName().getIds();
        Node name = paths.size() > 0 ? paths.get(paths.size() - 1) : that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.FILE);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forComponent(that);
        scopeStack.pop();
    }

    @Override
    public void forTraitDecl(TraitDecl that) {
        Scope scope = new Scope(that);
        Id name = that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.MODULE);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forTraitDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsTraitDecl(AbsTraitDecl that) {
        Scope scope = new Scope(that);
        Id name = that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.MODULE);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forAbsTraitDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forObjectDecl(ObjectDecl that) {
        Scope scope = new Scope(that);
        Id name = that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.CLASS);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forObjectDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsObjectDecl(AbsObjectDecl that) {
        Scope scope = new Scope(that);
        Id name = that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.CLASS);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forAbsObjectDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forFnDef(FnDef that) {
        Scope scope = new Scope(that);
        SimpleName name = that.getName();
        Signature signature = new Signature(that, name, scope, ElementKind.METHOD);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forFnDef(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsFnDecl(AbsFnDecl that) {
        super.forAbsFnDecl(that);
    }

    @Override
    public void forVarDecl(VarDecl that) {
        Scope scope = scopeStack.peek();
        List<LValueBind> lValueBinds = that.getLhs();
        for (LValueBind lValueBind : lValueBinds) {
            Id id = lValueBind.getName();
            Signature signature = new Signature(lValueBind, id, scope, ElementKind.FIELD);
            scope.addDefinition(signature);
        }

        super.forVarDecl(that);
    }

    @Override
    public void forLocalVarDecl(LocalVarDecl that) {
        Scope scope = scopeStack.peek();
        List<LValue> lValues = that.getLhs();
        for (LValue lValue : lValues) {
            if (lValue instanceof LValueBind) {
                Id id = ((LValueBind) lValue).getName();
                Signature signature = new Signature(lValue, id, scope, ElementKind.VARIABLE);
                scope.addDefinition(signature);
            } else {
                System.out.println("LValue: " + lValue.stringName());
            }
        }

        super.forLocalVarDecl(that);
    }


    /******************************
     * Usages
     ******************************/
    
    @Override
    public void forVarRef(VarRef that) {
        Scope scope = scopeStack.peek();
        Id id = that.getVar().getName();
        Signature signature = new Signature(that, id, scope, ElementKind.VARIABLE);
        scope.addUsage(signature);
        
        super.forVarRef(that);
    }

    @Override
    public void forMethodInvocation(MethodInvocation that) {
        Scope scope = scopeStack.peek();
        Id id = that.getMethod();
        Signature signature = new Signature(that, id, scope, ElementKind.METHOD);
        scope.addUsage(signature);

        super.forMethodInvocation(that);
    }    
    
}
