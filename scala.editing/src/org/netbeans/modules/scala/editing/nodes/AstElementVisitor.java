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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.gsf.api.ElementKind;
import xtc.tree.GNode;
import xtc.tree.Node;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElementVisitor extends AstVisitor {

    private AstDefinition packageElement = null;
    private boolean containsValDfn;
    private boolean containsVarDfn;
    private TypeRef varType;

    public AstElementVisitor(Node rootNode, String source, List<Integer> linesOffset) {
        super(rootNode, source, linesOffset);
    }

    public void visitPackage(GNode that) {
        GNode qualId = that.getGeneric(0);
        List<AstElement> ids = visitQualId(qualId);
        StringBuilder sb = new StringBuilder();
        for (Iterator<AstElement> itr = ids.iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        String name = sb.toString();
        AstScope scope = new AstScope(getRange(that));
        AstDefinition definition = new AstDefinition(name, getRange(qualId), scope, ElementKind.PACKAGE);

        packageElement = definition;

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public List<AstElement> visitQualId(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        visitNode(that, false);

        return ids;
    }

    public List<AstElement> visitIds(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        visitNode(that, false);

        return ids;
    }

    public List<AstElement> visitPath(GNode that) {
        List<AstElement> ids = null;

        GNode first = that.getGeneric(0);
        if (first == null || first.getName().equals("Id")) {
            ids = new ArrayList<AstElement>();
            if (first != null) {
                ids.add(visitId(first));
            }
            GNode thisKey = that.getGeneric(1);
            ids.add(new AstElement("this", getNameRange("this", thisKey), ElementKind.VARIABLE));
        } else if (first.getName().equals("StableId")) {
            ids = visitStableId(first);
        }

        visitNode(that, false);

        if (ids != null) {
            return ids;
        } else {
            return Collections.<AstElement>emptyList();
        }
    }

    public List<AstElement> visitStableId(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        visitNode(that, false);

        return ids;
    }

    public AstElement visitId(GNode that) {
        String name = that.getString(0);

        visitNode(that, false);

        return new AstElement(name, getNameRange(name, that), ElementKind.VARIABLE);
    }

    public void visitClassDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        AstScope scope = new AstScope(getRange(that));
        ClassTemplate classTmpl = new ClassTemplate(name, getNameRange(name, id), scope);
        classTmpl.setPackageElement(packageElement);

        scopeStack.peek().addDefinition(classTmpl);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public void visitTraitDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        AstScope scope = new AstScope(getRange(that));
        TraitTamplate traitTmpl = new TraitTamplate(name, getNameRange(name, id), scope);
        traitTmpl.setPackageElement(packageElement);

        scopeStack.peek().addDefinition(traitTmpl);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public void visitObjectDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        AstScope scope = new AstScope(getRange(that));
        ObjectTemplate objectTmpl = new ObjectTemplate(name, getNameRange(name, id), scope);
        objectTmpl.setPackageElement(packageElement);

        scopeStack.peek().addDefinition(objectTmpl);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public void visitFunDcl(GNode that) {
        Function function = visitFunSig(that.getGeneric(0));
        GNode typeNode = that.getGeneric(1);
        if (typeNode != null) {
            TypeRef type = visitType(typeNode);
            function.setType(type);
        }
        AstScope scope = function.getBindingScope();

        scopeStack.peek().addDefinition(function);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public void visitFunDef(GNode that) {
        Function function = visitFunSig(that.getGeneric(0));
        GNode secondNode = that.getGeneric(1);
        if (secondNode != null && secondNode.getName().equals("Type")) {
            TypeRef type = visitType(secondNode);
            function.setType(type);
        }
        AstScope scope = function.getBindingScope();

        scopeStack.peek().addDefinition(function);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public Function visitFunSig(GNode that) {
        AstElement id = visitId(that.getGeneric(0));
        List<Var> params = visitParamClauses(that.getGeneric(2));

        AstScope scope = new AstScope(getRange(that));
        Function function = new Function(id.getName(), id.getNameRange(), scope);
        function.setParam(params);

        for (Var param : params) {
            scope.addDefinition(param);
        }

        visitNode(that, true);

        return function;
    }

    public List<Var> visitParamClauses(GNode that) {
        List<Var> params = new ArrayList<Var>();

        List paramClauseNodes = that.getList(0).list();
        for (Object paramClauseNode : paramClauseNodes) {
            params.addAll(visitParamClause((GNode) paramClauseNode));
        }

        visitNode(that, true);

        return params;
    }

    public List<Var> visitParamClause(GNode that) {
        List<Var> params = null;

        GNode paramsNode = that.getGeneric(0);
        if (paramsNode != null) {
            params = visitParams(paramsNode);
        } else {
            params = Collections.<Var>emptyList();
        }

        visitNode(that, true);

        return params;
    }

    public List<Var> visitParams(GNode that) {
        List<Var> params = new ArrayList<Var>();

        Var first = visitParam(that.getGeneric(0));
        params.add(first);

        List others = that.getList(1).list();
        for (Object param : others) {
            params.add(visitParam((GNode) param));
        }

        visitNode(that, true);

        return params;
    }

    public Var visitParam(GNode that) {
        List annotations = that.getList(0).list();
        AstElement id = visitId(that.getGeneric(1));
        AstScope scope = new AstScope(getRange(that));
        Var var = new Var(id.getName(), id.getNameRange(), scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(2);
        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            var.setType(type);
        }

        visitNode(that, true);

        return var;
    }

    public TypeRef visitParamType(GNode that) {
        Object first = that.get(0);
        GNode typeNode = null;
        WrappedType.More more = WrappedType.More.Pure;
        if (first instanceof GNode) {
            typeNode = (GNode) first;
            if (that.size() == 2) {
                more = WrappedType.More.Star;
            }
        } else {
            typeNode = that.getGeneric(1);
            more = WrappedType.More.Arrow;
        }
        TypeRef wrappedType = visitType(typeNode);
        WrappedType type = new WrappedType(wrappedType.getName(), wrappedType.getNameRange(), ElementKind.CLASS);
        type.setWrappedType(wrappedType);
        type.setMore(more);

        visitNode(that, false);

        return type;
    }

    public void visitConstructorFunDef(GNode that) {
        AstElement id = visitId(that.getGeneric(0)); // // This("this")

        List<Var> params = visitParamClause(that.getGeneric(1));
        List<Var> paramsOther = visitParamClauses(that.getGeneric(2));
        params.addAll(paramsOther);

        AstScope scope = new AstScope(getRange(that));
        Function function = new Function(id.getName(), id.getNameRange(), scope);
        function.setParam(params);

        for (Var param : params) {
            scope.addDefinition(param);
        }

        scopeStack.peek().addDefinition(function);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        visitNode(that, true);
        scopeStack.pop();
    }

    public AstElement visitClassParam(GNode that) {
        List annotations = that.getList(0).list();
        GNode paramTypeNode = that.getGeneric(3);
        AstElement id = visitId(that.getGeneric(2));
        AstScope scope = new AstScope(getRange(that));
        Var var = new Var(id.getName(), id.getNameRange(), scope, ElementKind.PARAMETER);

        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            var.setType(type);
        }

        scopeStack.peek().addDefinition(var);
        scopeStack.peek().addScope(scope);

        visitNode(that, true);

        return id;
    }

    public void visitValDcl(GNode that) {
        List<AstElement> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (AstElement id : ids) {
            AstScope scope = new AstScope(getRange(that));
            Var val = new Var(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);
            val.setVal();
            val.setType(type);

            scopeStack.peek().addDefinition(val);
            scopeStack.peek().addScope(scope);
        }

        visitNode(that, true);
    }

    public void visitVarDcl(GNode that) {
        List<AstElement> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (AstElement id : ids) {
            AstScope scope = new AstScope(getRange(that));
            Var var = new Var(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);
            var.setType(type);

            scopeStack.peek().addDefinition(var);
            scopeStack.peek().addScope(scope);
        }

        visitNode(that, true);
    }

    public void visitValDef(GNode that) {
        containsValDfn = true;
        visitNode(that, true);
        containsValDfn = false;
    }

    public void visitVarDef(GNode that) {
        containsVarDfn = true;
        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Ids")) {
            varType = visitType(that.getGeneric(1));
        } else {
            varType = null;
        }
        visitNode(that, true);
        varType = null;
        containsVarDfn = false;
    }

    public void visitPatDef(GNode that) {
        GNode typeNode = that.getGeneric(2);
        varType = typeNode == null ? null : visitType(typeNode);
        visitNode(that, true);
        varType = null;
    }

    public List<AstElement> visitIdPattern(GNode that) {
        GNode stableId = that.getGeneric(0);
        List<AstElement> ids = visitStableId(stableId);
        if (containsValDfn || containsVarDfn) {
            GNode dfnNode = null;
            if (containsValDfn) {
                dfnNode = findNearsetNode("ValDef");
            } else {
                dfnNode = findNearsetNode("VarDef");
            }

            AstScope scope = new AstScope(getRange(dfnNode));
            /** fetch id is the name @Todo path */
            AstElement id = ids.get(ids.size() - 1);
            Var var = new Var(id.getName(), id.getNameRange(), scope, ElementKind.FIELD);
            if (containsValDfn) {
                var.setVal();
            }
            if (varType != null) {
                var.setType(varType);
            }

            scopeStack.peek().addDefinition(var);
            scopeStack.peek().addScope(scope);
        }

        visitNode(that, true);

        return ids;
    }

    public String visitModifier(GNode that) {
        String modifier = null;

        Object what = that.get(0);
        if (what instanceof GNode) {
            GNode modifierNode = (GNode) what;
            if (modifierNode.getName().equals("LocalModifier")) {
                modifier = visitLocalModifier(modifierNode);
            } else if (modifierNode.getName().equals("AccessModifier")) {
                modifier = visitAccessModifier(modifierNode);
            }
        } else if (what instanceof String) {
            modifier = (String) what;
        }

        visitNode(that, true);

        return modifier;
    }

    public String visitLocalModifier(GNode that) {
        visitNode(that, true);
        return that.getString(0);
    }

    public String visitAccessModifier(GNode that) {
        visitNode(that, true);
        return that.getString(0);
    }

    public void visitSimpleIdExpr(GNode that) {
        List<AstElement> ids = visitPath(that.getGeneric(0));
        AstElement first = ids.get(0);
        AstUsage usage = new AstUsage(first.getName(), first.getNameRange(), ElementKind.VARIABLE);

        scopeStack.peek().addUsage(usage);
        visitNode(that, true);
    }

    public TypeRef visitType(GNode that) {
        TypeRef type = null;

        GNode node = that.getGeneric(0);
        if (node.getName().equals("CallByNameFunType")) {
            FunType funType = new FunType("FunType", getRange(node), ElementKind.CLASS);
            GNode lhsNode = node.getGeneric(0);
            if (lhsNode != null) {
                TypeRef lhs = visitType(lhsNode);
                funType.setLhs(lhs);
            }
            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);
            funType.setRhs(rhs);

            type = funType;
        } else if (node.getName().equals("NormalFunType")) {
            FunType funType = new FunType("FunType", getRange(node), ElementKind.CLASS);
            GNode lhsNode = node.getGeneric(0);
            TypeRef lhs = visitInfixType(lhsNode);
            funType.setLhs(lhs);
            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);
            funType.setRhs(rhs);

            type = funType;
        } else {
            // NotFunType
            GNode infixTypeNode = node.getGeneric(0);
            type = visitInfixType(infixTypeNode);
        }

        return type;
    }

    public TypeRef visitInfixType(GNode that) {
        TypeRef type = null;

        List<TypeRef> types = new ArrayList<TypeRef>();
        List<String> ops = new ArrayList<String>();

        TypeRef first = visitCompoundType(that.getGeneric(0));
        types.add(first);

        List others = that.getList(1).list();
        for (Object rest : others) {
            GNode restNode = (GNode) rest;
            ops.add(restNode.getGeneric(0).getString(0));
            types.add(visitCompoundType(restNode.getGeneric(1)));
        }

        if (ops.size() == 1) {
            type = first;
        } else {
            InfixType infixType = new InfixType(first.getName(), first.getNameRange(), ElementKind.CLASS);
            infixType.setTypes(types);
            infixType.setOps(ops);

            type = infixType;
        }

        return type;
    }

    public TypeRef visitCompoundType(GNode that) {
        TypeRef type = null;

        List<TypeRef> types = new ArrayList<TypeRef>();

        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Refinement")) {
            // Todo
            return new SimpleIdType("{}", getRange(that), ElementKind.CLASS);
        }

        TypeRef first = visitAnnotType(that.getGeneric(0));
        types.add(first);

        List others = that.getList(1).list();
        for (Object typeNode : others) {
            types.add(visitAnnotType((GNode) typeNode));
        }

        if (types.size() == 1) {
            type = first;
        } else {

            CompoundType compoundType = new CompoundType(first.getName(), first.getNameRange(), ElementKind.CLASS);
            compoundType.setTypes(types);

            type = compoundType;
        }

        return type;
    }

    public TypeRef visitAnnotType(GNode that) {
        List annotations = that.getList(0).list();
        SimpleType type = visitSimpleType(that.getGeneric(1));

        return type;
    }

    public SimpleType visitSimpleType(GNode that) {
        SimpleType type = null;

        if (that.getName().equals("SimpleIdType")) {
            type = visitSimpleIdType(that);
        } else if (that.getName().equals("SimpleSingletonType")) {
            type = visitSimpleSingletonType(that);
        } else if (that.getName().equals("SimpleTupleType")) {
            type = visitSimpleTupleType(that);
        }

        assert type != null : "There is other SimpleType? - " + that.getName();

        List<List<TypeRef>> typeArgsList = new ArrayList<List<TypeRef>>();
        for (Object typeArgsNode : that.getList(1).list()) {
            List<TypeRef> typeArgs = visitTypeArgs((GNode) typeArgsNode);
            typeArgsList.add(typeArgs);
        }

        if (typeArgsList.size() > 0) {
            type.setTypeArgsList(typeArgsList);
        }

        return type;
    }

    public List<TypeRef> visitTypeArgs(GNode that) {
        Object what = that.get(0);
        if (what instanceof GNode) {
            return visitTypes((GNode) what);
        } else {
            // wildcard
            return Collections.<TypeRef>emptyList();
        }
    }

    public List<TypeRef> visitTypes(GNode that) {
        List<TypeRef> types = new ArrayList<TypeRef>();

        TypeRef first = visitType(that.getGeneric(0));
        types.add(first);

        List others = that.getList(1).list();
        for (Object typeNode : others) {
            types.add(visitType((GNode) typeNode));
        }

        return types;
    }

    public SimpleType visitSimpleIdType(GNode that) {
        List<AstElement> ids = visitStableId(that.getGeneric(0));
        AstElement first = ids.get(0);
        SimpleIdType type = new SimpleIdType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setIds(ids);

        scopeStack.peek().addUsage(type);

        return type;
    }

    public SimpleType visitSimpleSingletonType(GNode that) {
        List<AstElement> ids = visitPath(that.getGeneric(0));
        AstElement first = ids.get(0);
        SimpleIdType type = new SimpleIdType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setIds(ids);

        scopeStack.peek().addUsage(type);

        return type;
    }

    public SimpleType visitSimpleTupleType(GNode that) {
        List<TypeRef> types = visitTypes(that.getGeneric(0));
        SimpleTupleType type = new SimpleTupleType("TupleType", getRange(that), ElementKind.CLASS);
        type.setTypes(types);

        for (TypeRef typeRef : types) {
            scopeStack.peek().addUsage(typeRef);
        }

        return type;
    }
}
