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
import com.sun.fortress.nodes.Block;
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
import com.sun.fortress.nodes_util.SourceLoc;
import com.sun.fortress.nodes_util.Span;
import java.util.List;
import java.util.Stack;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 *
 * @author Caoyuan Deng
 */
public class SignatureVisitor extends NodeDepthFirstVisitor_void {

    private Scope rootScope;
    private Stack<Scope> scopeStack = new Stack<Scope>();
    private List<Integer> linesOffset;

    public SignatureVisitor(Node rootNode, List<Integer> linesOffset) {
        this.linesOffset = linesOffset;
        // set linesOffset before call getRange(Node)
        this.rootScope = new Scope(null, getOffsetRange(rootNode));
        scopeStack.push(rootScope);
    }

    public Scope getRootScope() {
        return rootScope;
    }

    private OffsetRange getOffsetRange(Node node) {
        Span span = node.getSpan();
        SourceLoc begin = span.getBegin();
        SourceLoc end = span.getEnd();
        return new OffsetRange(
                linesOffset.get(begin.getLine() - 1) + begin.column() + 1,
                linesOffset.get(end.getLine() - 1) + end.column() + 1);
    }

    @Override
    public void defaultCase(Node that) {
        //System.out.println("Default: " + that.stringName());
    }

    /******************************
     * Definitions
     ******************************/
    @Override
    public void forComponent(Component that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        List<Id> paths = that.getName().getIds();
        Node name = paths.size() > 0 ? paths.get(paths.size() - 1) : that.getName();
        Definition definition = new Definition(that, name, getOffsetRange(name), scope, ElementKind.FILE);
        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forComponent(that);
        scopeStack.pop();
    }

    @Override
    public void forTraitDecl(TraitDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        Id name = that.getName();
        Definition signature = new Definition(that, name, getOffsetRange(name), scope, ElementKind.MODULE);
        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forTraitDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsTraitDecl(AbsTraitDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        Id name = that.getName();
        Definition definition = new Definition(that, name, getOffsetRange(name), scope, ElementKind.MODULE);
        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forAbsTraitDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forObjectDecl(ObjectDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        Id name = that.getName();
        Definition definition = new Definition(that, name, getOffsetRange(name), scope, ElementKind.CLASS);
        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forObjectDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsObjectDecl(AbsObjectDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        Id name = that.getName();
        Definition definition = new Definition(that, name, getOffsetRange(name), scope, ElementKind.CLASS);
        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forAbsObjectDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forFnDef(FnDef that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        SimpleName name = that.getName();
        Definition definition = new Definition(that, name, getOffsetRange(name), scope, ElementKind.METHOD);
        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forFnDef(that);
        scopeStack.pop();
    }

    @Override
    public void forAbsFnDecl(AbsFnDecl that) {
        super.forAbsFnDecl(that);
    }

    /**
     * @Note: for VarDecl/LocalVarDecl etc, do not push the new scope into scopeStack:
     * 1. It's a pseud scope;
     * 2. Fortress's node tree here is waired, which wrap the followed exprs in 
     *    to this VarDecl's body, thus become children of this node.
     */
    @Override
    public void forVarDecl(VarDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        List<LValueBind> lValueBinds = that.getLhs();
        for (LValueBind lValueBind : lValueBinds) {
            Id id = lValueBind.getName();
            Definition definition = new Definition(lValueBind, id, getOffsetRange(id), scope, ElementKind.FIELD);
            scopeStack.peek().addDefinition(definition);
        }
        scopeStack.peek().addScope(scope);

        super.forVarDecl(that);
    }

    @Override
    public void forLocalVarDecl(LocalVarDecl that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        List<LValue> lValues = that.getLhs();
        for (LValue lValue : lValues) {
            if (lValue instanceof LValueBind) {
                Id id = ((LValueBind) lValue).getName();
                Definition definition = new Definition(lValue, id, getOffsetRange(id), scope, ElementKind.VARIABLE);
                scopeStack.peek().addDefinition(definition);
            } else {
                System.out.println("LValue: " + lValue.stringName());
            }
        }
        scopeStack.peek().addScope(scope);

        super.forLocalVarDecl(that);
    }

    
    /******************************
     * Blocks
     ******************************/    
    @Override
    public void forBlock(Block that) {
        Scope scope = new Scope(that, getOffsetRange(that));
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.forBlock(that);
        scopeStack.pop();
    }
    
    

    /******************************
     * Usages
     ******************************/
    @Override
    public void forVarRef(VarRef that) {
        Scope scope = scopeStack.peek();
        Id id = that.getVar().getName();
        Usage usage = new Usage(that, id, getOffsetRange(id), scope, ElementKind.VARIABLE);
        scope.addUsage(usage);

        super.forVarRef(that);
    }

    @Override
    public void forMethodInvocation(MethodInvocation that) {
        Scope scope = scopeStack.peek();
        Id id = that.getMethod();
        Usage usage = new Usage(that, id, getOffsetRange(id), scope, ElementKind.METHOD);
        scope.addUsage(usage);

        super.forMethodInvocation(that);
    }
}
