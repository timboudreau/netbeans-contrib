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

import com.sun.fortress.nodes.APIName;
import com.sun.fortress.nodes.Component;
import com.sun.fortress.nodes.FnDef;
import com.sun.fortress.nodes.Id;
import com.sun.fortress.nodes.Node;
import com.sun.fortress.nodes.NodeDepthFirstVisitor_void;
import com.sun.fortress.nodes.ObjectDecl;
import com.sun.fortress.nodes.SimpleName;
import java.util.Stack;
import org.netbeans.modules.gsf.api.ElementKind;

/**
 *
 * @author Caoyuan Deng
 */
public class DefinitionVisitor extends NodeDepthFirstVisitor_void {

    private Scope rootScope;
    private Stack<Scope> scopeStack = new Stack<Scope>();

    public DefinitionVisitor(Scope rootCtx) {
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
        APIName name = that.getName();
        Element signature = new Element(that, name, scope, ElementKind.MODULE);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forComponent(that);
        scopeStack.pop();
    }

    @Override
    public void forObjectDecl(ObjectDecl that) {
        Scope scope = new Scope(that);
        Id name = that.getName();
        Element signature = new Element(that, name, scope, ElementKind.CLASS);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forObjectDecl(that);
        scopeStack.pop();
    }

    @Override
    public void forFnDef(FnDef that) {
        Scope scope = new Scope(that);
        SimpleName name = that.getName();
        Element signature = new Element(that, name, scope, ElementKind.METHOD);

        scopeStack.peek().addDefinition(signature);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.forFnDef(that);
        scopeStack.pop();
    }
}
