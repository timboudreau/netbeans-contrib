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
public class ElementVisitor extends AstVisitor {

    private Scope rootScope;
    private List<Integer> linesOffset;
    private String source;
    private Stack<Scope> scopeStack = new Stack<Scope>();
    private Definition packageElement = null;
    private boolean containsValDfn;
    private boolean containsVarDfn;

    public ElementVisitor(Node rootNode, String source, List<Integer> linesOffset) {
        this.source = source;
        this.linesOffset = linesOffset;
        // set linesOffset before call getRange(Node)
        this.rootScope = new Scope(getRange(rootNode));
        scopeStack.push(rootScope);
    }

    public Scope getRootScope() {
        return rootScope;
    }

    private OffsetRange getRange(Node node) {
        Location loc = node.getLocation();
        return new OffsetRange(loc.offset, loc.endOffset);
    }

    /**
     * @Note: nameNode may contains preceding void productions, and may also contains
     * following void productions, but nameString has stripped the void productions,
     * so we should adjust nameRange according to name and its length.
     */
    private OffsetRange getNameRange(String name, Node node) {
        Location loc = node.getLocation();
        int length = name.length();
        for (int i = loc.offset; i < loc.endOffset; i++) {
            if (source.substring(i, i + length).equals(name)) {
                return new OffsetRange(i, i + length);
            }
        }

        return new OffsetRange(loc.offset, loc.endOffset);
    }

    @Override
    public void visitPackage(GNode that) {
        GNode qualId = that.getGeneric(0);
        List<Element> ids = visitQualId(qualId);
        StringBuilder sb = new StringBuilder();
        for (Iterator<Element> itr = ids.iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        String name = sb.toString();
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getRange(qualId), scope, ElementKind.PACKAGE);

        packageElement = definition;

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitPackage(that);
        scopeStack.pop();
    }

    @Override
    public List<Element> visitQualId(GNode that) {
        List<Element> ids = new ArrayList<Element>();

        Element first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        if (others != null) {
            for (Object id : others) {
                ids.add(visitId((GNode) id));
            }
        }

        super.visitQualId(that);
        return ids;
    }

    @Override
    public List<Element> visitIds(GNode that) {
        List<Element> ids = new ArrayList<Element>();

        Element first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        if (others != null) {
            for (Object id : others) {
                ids.add(visitId((GNode) id));
            }
        }

        super.visitIds(that);
        return ids;
    }

    @Override
    public List<Element> visitStableId(GNode that) {
        List<Element> ids = new ArrayList<Element>();

        Element first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        super.visitStableId(that);
        return ids;
    }

    @Override
    public Element visitId(GNode that) {
        super.visitId(that);
        String name = that.getString(0);
        return new Element(name, getNameRange(name, that), ElementKind.VARIABLE);
    }

    @Override
    public void visitClassDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.CLASS);
        definition.setPackageElement(packageElement);

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
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.MODULE);
        definition.setPackageElement(packageElement);

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
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.CLASS);
        definition.setPackageElement(packageElement);

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
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.METHOD);

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
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.METHOD);

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
        Scope scope = new Scope(getRange(that));
        Definition definition = new Definition(name, getNameRange(name, id), scope, ElementKind.METHOD);

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitConstructorFunDef(that);
        scopeStack.pop();
    }

    @Override
    public void visitValDcl(GNode that) {
        GNode ids = that.getGeneric(0);

        for (Element id : visitIds(ids)) {
            Scope scope = new Scope(getRange(that));
            Definition definition = new Definition(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);

            scopeStack.peek().addDefinition(definition);
            scopeStack.peek().addScope(scope);
        }

        super.visitValDcl(that);
    }

    @Override
    public void visitVarDcl(GNode that) {
        GNode ids = that.getGeneric(0);

        for (Element id : visitIds(ids)) {
            Scope scope = new Scope(getRange(that));
            Definition definition = new Definition(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);

            scopeStack.peek().addDefinition(definition);
            scopeStack.peek().addScope(scope);
        }

        super.visitVarDcl(that);
    }

    @Override
    public void visitValDef(GNode that) {
        containsValDfn = true;
        super.visitValDef(that);
        containsValDfn = false;
    }

    @Override
    public void visitVarDef(GNode that) {
        containsVarDfn = true;
        super.visitVarDef(that);
        containsVarDfn = false;
    }

    @Override
    public List<Element> visitIdPattern(GNode that) {
        GNode stableId = that.getGeneric(0);
        List<Element> ids = visitStableId(stableId);
        if (containsValDfn || containsVarDfn) {
            GNode dfnNode = null;
            if (containsValDfn) {
                dfnNode = findNearsetNode("ValDef");
            } else {
                dfnNode = findNearsetNode("VarDef");
            }

            Scope scope = new Scope(getRange(dfnNode));
            /** fetch id is the name @Todo path */
            Element id = ids.get(ids.size() - 1);
            Definition definition = new Definition(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);

            scopeStack.peek().addDefinition(definition);
            scopeStack.peek().addScope(scope);
        }

        super.visitIdPattern(that);
        return ids;
    }

    @Override
    public String visitModifier(GNode that) {
        super.visitModifier(that);

        Object modifier = that.get(0);
        if (modifier instanceof GNode) {
            GNode modifierNode = (GNode) modifier;
            if (modifierNode.getName().equals("LocalModifier")) {
                return visitLocalModifier(modifierNode);
            } else if (modifierNode.getName().equals("AccessModifier")) {
                return visitAccessModifier(modifierNode);
            }
        } else if (modifier instanceof String) {
            return (String) modifier;
        }

        return null;
    }

    @Override
    public String visitLocalModifier(GNode that) {
        super.visitLocalModifier(that);
        return that.getString(0);
    }

    @Override
    public String visitAccessModifier(GNode that) {
        super.visitAccessModifier(that);
        return that.getString(0);
    }
}
