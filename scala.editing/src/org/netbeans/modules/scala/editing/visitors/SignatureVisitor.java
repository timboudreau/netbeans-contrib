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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.lang.model.element.ElementKind;
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
    private ScalaElement packageElement = null;

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
    public void visitPackage(GNode that) {
        GNode qualId = that.getGeneric(0);
        List<String> idNames = visitQualId(qualId);
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> itr = idNames.iterator(); itr.hasNext();) {
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }
        
        String name = sb.toString();
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.PACKAGE);
        Definition definition = new Definition(e, getRange(qualId));
        Scope scope = new Scope(definition, getRange(that));

        packageElement = e;

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitPackage(that);
        scopeStack.pop();
    }

    public List<String> visitQualId(GNode that) {
        List<String> idNames = new ArrayList<String>();
        
        String first = visitId(that.getGeneric(0));
        idNames.add(first);

        List others = that.getList(1).list();
        if (others != null) {
            for (Object id : others) {
                idNames.add(visitId((GNode) id));
            }
        }
        
        return idNames;
    }

    public String visitId(GNode that) {
        return that.getString(0);
    }

    @Override
    public void visitClassDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.CLASS);
        Definition definition = new Definition(e, getRange(id));
        definition.setPackageElement(packageElement);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitClassDef(that);
        scopeStack.pop();
    }

    @Override
    public void visitTraitDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.CLASS);
        Definition definition = new Definition(e, getRange(id));
        definition.setPackageElement(packageElement);
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitTraitDef(that);
        scopeStack.pop();
    }

    @Override
    public void visitObjectDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0) + "$";
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.CLASS);
        Definition definition = new Definition(e, getRange(id));
        definition.setPackageElement(packageElement);
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
        Node id = funSig.getGeneric(0);
        String name = id.getString(0);
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.METHOD);
        Definition definition = new Definition(e, getRange(id));
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
        Node id = funSig.getGeneric(0);
        String name = id.getString(0);
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.METHOD);
        Definition definition = new Definition(e, getRange(id));
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitFunDcl(that);
        scopeStack.pop();
    }

    @Override
    public void visitConstructorFunDef(GNode that) {
        Node id = that.getGeneric(0); // This("this")

        String name = id.getString(0);
        ScalaElement e = new ScalaElement(new ScalaName(name), ElementKind.CONSTRUCTOR);
        Definition definition = new Definition(e, getRange(id));
        Scope scope = new Scope(definition, getRange(that));

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitConstructorFunDef(that);
        scopeStack.pop();
    }
}
