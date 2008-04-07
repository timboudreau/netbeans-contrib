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
import java.util.Stack;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.OffsetRange;
import xtc.tree.GNode;
import xtc.tree.Location;
import xtc.tree.Node;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElementVisitor extends AstVisitor {

    private AstScope rootScope;
    private List<Integer> linesOffset;
    private String source;
    private Stack<AstScope> scopeStack = new Stack<AstScope>();
    private AstDefinition packageElement = null;
    private boolean containsValDfn;
    private boolean containsVarDfn;
    private TypeRef varType;

    public AstElementVisitor(Node rootNode, String source, List<Integer> linesOffset) {
        this.source = source;
        this.linesOffset = linesOffset;
        // set linesOffset before call getRange(Node)
        this.rootScope = new AstScope(getRange(rootNode));
        scopeStack.push(rootScope);
    }

    public AstScope getRootScope() {
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
        super.visitPackage(that);
        scopeStack.pop();
    }

    @Override
    public List<AstElement> visitQualId(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        super.visitQualId(that);
        return ids;
    }

    @Override
    public List<AstElement> visitIds(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        super.visitIds(that);
        return ids;
    }

    @Override
    public List<AstElement> visitPath(GNode that) {
        super.visitPath(that);

        GNode first = that.getGeneric(0);
        if (first == null || first.getName().equals("Id")) {
            List<AstElement> ids = new ArrayList<AstElement>();
            if (first != null) {
                ids.add(visitId(first));
            }
            GNode thisKey = that.getGeneric(1);
            ids.add(new AstElement("this", getNameRange("this", thisKey), ElementKind.VARIABLE));
            return ids;
        } else if (first.getName().equals("StableId")) {
            return visitStableId(first);
        }

        return Collections.emptyList();
    }

    @Override
    public List<AstElement> visitStableId(GNode that) {
        List<AstElement> ids = new ArrayList<AstElement>();

        AstElement first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        super.visitStableId(that);
        return ids;
    }

    @Override
    public AstElement visitId(GNode that) {
        super.visitId(that);
        String name = that.getString(0);
        return new AstElement(name, getNameRange(name, that), ElementKind.VARIABLE);
    }

    @Override
    public void visitClassDef(GNode that) {
        Node id = that.getGeneric(0);
        String name = id.getString(0);
        AstScope scope = new AstScope(getRange(that));
        AstDefinition definition = new AstDefinition(name, getNameRange(name, id), scope, ElementKind.CLASS);
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
        AstScope scope = new AstScope(getRange(that));
        AstDefinition definition = new AstDefinition(name, getNameRange(name, id), scope, ElementKind.MODULE);
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
        AstScope scope = new AstScope(getRange(that));
        AstDefinition definition = new AstDefinition(name, getNameRange(name, id), scope, ElementKind.CLASS);
        definition.setPackageElement(packageElement);

        scopeStack.peek().addDefinition(definition);
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);
        super.visitObjectDef(that);
        scopeStack.pop();
    }

    @Override
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
        super.visitFunDcl(that);
        scopeStack.pop();
    }

    @Override
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
        super.visitFunDcl(that);
        scopeStack.pop();
    }

    @Override
    public Function visitFunSig(GNode that) {
        super.visitFunSig(that);
        AstElement id = visitId(that.getGeneric(0));
        List<Var> params = visitParamClauses(that.getGeneric(2));

        AstScope scope = new AstScope(getRange(that));
        Function function = new Function(id.getName(), id.getNameRange(), scope, ElementKind.METHOD);
        function.setParam(params);

        for (Var param : params) {
            scope.addDefinition(param);
        }

        return function;
    }

    @Override
    public List<Var> visitParamClauses(GNode that) {
        super.visitParamClauses(that);

        List<Var> params = new ArrayList<Var>();

        List paramClauseNodes = that.getList(0).list();
        for (Object paramClauseNode : paramClauseNodes) {
            params.addAll(visitParamClause((GNode) paramClauseNode));
        }

        return params;
    }

    @Override
    public List<Var> visitParamClause(GNode that) {
        super.visitParamClause(that);
        GNode paramsNode = that.getGeneric(0);
        if (paramsNode != null) {
            return visitParams(paramsNode);
        } else {
            return Collections.<Var>emptyList();
        }
    }

    @Override
    public List<Var> visitParams(GNode that) {
        List<Var> params = new ArrayList<Var>();

        Var first = visitParam(that.getGeneric(0));
        params.add(first);

        List others = that.getList(1).list();
        for (Object param : others) {
            params.add(visitParam((GNode) param));
        }

        super.visitParams(that);
        return params;
    }

    @Override
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

        super.visitParam(that);
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

        return type;
    }

    @Override
    public void visitConstructorFunDef(GNode that) {
        AstElement id = visitId(that.getGeneric(0)); // // This("this")

        List<Var> params = visitParamClause(that.getGeneric(1));
        List<Var> paramsOther = visitParamClauses(that.getGeneric(2));
        params.addAll(paramsOther);

        AstScope scope = new AstScope(getRange(that));
        Function function = new Function(id.getName(), id.getNameRange(), scope, ElementKind.METHOD);
        function.setParam(params);

        for (Var param : params) {
            scope.addDefinition(param);
        }

        scopeStack.peek().addDefinition(function);
        scopeStack.peek().addScope(scope);

        scopeStack.push(scope);
        super.visitConstructorFunDef(that);
        scopeStack.pop();
    }

    @Override
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

        super.visitParam(that);
        return id;
    }

    @Override
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

        super.visitValDcl(that);
    }

    @Override
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
        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Ids")) {
            varType = visitType(that.getGeneric(1));
        } else {
            varType = null;
        }
        super.visitVarDef(that);
        varType = null;
        containsVarDfn = false;
    }

    public void visitPatDef(GNode that) {
        GNode typeNode = that.getGeneric(2);
        varType = typeNode == null ? null : visitType(typeNode);
        visit(that);
        varType = null;
    }

    @Override
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

    @Override
    public void visitSimpleIdExpr(GNode that) {
        List<AstElement> ids = visitPath(that.getGeneric(0));
        AstElement first = ids.get(0);
        AstUsage usage = new AstUsage(first.getName(), first.getNameRange(), ElementKind.VARIABLE);

        scopeStack.peek().addUsage(usage);
        super.visitSimpleIdExpr(that);
    }

    public TypeRef visitType(GNode that) {
        GNode node = that.getGeneric(0);
        if (node.getName().equals("CallByNameFunType")) {
            FunType type = new FunType("FunType", getRange(node), ElementKind.CLASS);
            GNode lhsNode = node.getGeneric(0);
            if (lhsNode != null) {
                TypeRef lhs = visitType(lhsNode);
                type.setLhs(lhs);
            }
            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);
            type.setRhs(rhs);
            
            return type;
        } else if (node.getName().equals("NormalFunType")) {
            FunType type = new FunType("FunType", getRange(node), ElementKind.CLASS);
            GNode lhsNode = node.getGeneric(0);
            TypeRef lhs = visitInfixType(lhsNode);
            type.setLhs(lhs);
            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);
            type.setRhs(rhs);
            
            return type;
        } else {
            // NotFunType
            GNode infixTypeNode = node.getGeneric(0);
            TypeRef type = visitInfixType(infixTypeNode);
            
            return type;
        }
    }

    public TypeRef visitInfixType(GNode that) {
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
            return first;
        }

        InfixType type = new InfixType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setTypes(types);
        type.setOps(ops);

        return type;
    }

    public TypeRef visitCompoundType(GNode that) {
        List<TypeRef> types = new ArrayList<TypeRef>();

        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Refinement")) {
            // Todo
            return new SimpleIdType("{}", getRange(that), ElementKind.CLASS);
        }

        TypeRef first = visitAnnotType(that.getGeneric(0));
        types.add(first);

        List others = that.getList(1).list();
        for (Object type : others) {
            types.add(visitAnnotType((GNode) type));
        }

        if (types.size() == 1) {
            return first;
        }

        CompoundType type = new CompoundType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setTypes(types);

        return type;
    }

    @Override
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
        
        visit(that);
        
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
        for (Object type : others) {
            types.add(visitType((GNode) type));
        }
      
        return types;        
    }
    
    @Override
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
