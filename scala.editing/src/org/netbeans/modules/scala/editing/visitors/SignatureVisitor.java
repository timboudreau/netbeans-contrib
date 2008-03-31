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
package org.netbeans.modules.scala.editing.visitors;

import java.util.List;
import java.util.Stack;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.scala.editing.nodes.AstVisitor;
import xtc.tree.GNode;
import xtc.tree.Location;
import xtc.tree.Node;


/**
 *
 * @author Caoyuan Deng
 */
public class SignatureVisitor extends AstVisitor {

    private Scope rootScope;
    private Stack<Scope> scopeStack = new Stack<Scope>();
    private List<Integer> linesOffset;

    public SignatureVisitor(Node rootNode, List<Integer> linesOffset) {
        this.linesOffset = linesOffset;
        // set linesOffset before call getRange(Node)
        this.rootScope = new Scope(null, getRange(rootNode));
        scopeStack.push(rootScope);
    }

    public Scope getRootScope() {
        return rootScope;
    }

    private OffsetRange getRange(Node node) {
        Location loc = node.getLocation();
        return new OffsetRange(loc.offset, loc.endOffset);
    }

    @Override
    public void visitClassDef(GNode that) {
        Node name = that.getGeneric(0);
        Definition definition = new Definition(that, name, getRange(name), ElementKind.CLASS);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitClassDef(that);
        scopeStack.pop();
    }

    @Override
    public void visitTraitDef(GNode that) {
        Node name = that.getGeneric(0);
        Definition definition = new Definition(that, name, getRange(name), ElementKind.MODULE);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitTraitDef(that);
        scopeStack.pop();        
    }

    @Override
    public void visitObjectDef(GNode that) {
        Node name = that.getGeneric(0);
        Definition definition = new Definition(that, name, getRange(name), ElementKind.CLASS);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitObjectDef(that);
        scopeStack.pop();        
    }

    @Override
    public void visitFunDcl(GNode that) {
        Node funSig = that.getGeneric(0);
        Node name = funSig.getGeneric(0);
        Definition definition = new Definition(that, name, getRange(name), ElementKind.METHOD);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitFunDcl(that);
        scopeStack.pop();
    }

    @Override
    public void visitFunDef(GNode that) {
        Node funSig = that.getGeneric(0);
        Node name = funSig.getGeneric(0);
        Definition definition = new Definition(that, name, getRange(name), ElementKind.METHOD);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitFunDcl(that);
        scopeStack.pop();
    }

    @Override
    public void visitConstructorFunDef(GNode that) {
        Node name = that.getGeneric(0); // This("this")
        Definition definition = new Definition(that, name, getRange(name), ElementKind.CONSTRUCTOR);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitConstructorFunDef(that);
        scopeStack.pop();
    }

    
}
