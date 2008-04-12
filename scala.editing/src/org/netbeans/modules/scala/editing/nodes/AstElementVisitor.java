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
import org.netbeans.modules.gsf.api.OffsetRange;
import xtc.tree.GNode;
import xtc.tree.Node;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElementVisitor extends AstVisitor {

    public AstElementVisitor(Node rootNode, String source, List<Integer> linesOffset) {
        super(rootNode, source, linesOffset);
    }

    public Packaging visitPackage(GNode that) {
        enter(that);

        GNode qualId = that.getGeneric(0);
        PathId pathId = visitQualId(qualId);
        StringBuilder sb = new StringBuilder();
        for (Iterator<Id> itr = pathId.getPaths().iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        String name = sb.toString();
        AstScope scope = new AstScope(rootScope.getRange());
        Packaging packaging = new Packaging(name, getNameRange(name, qualId), scope);
        packaging.setTop();
        packaging.setIds(pathId.getPaths());

        rootScope.addDef(packaging);

        scopeStack.push(scope);
        visitChildren(that);
        /** @Note do not pop this packaging's scope, since topstats are not its children */
        exit(that);
        return packaging;
    }

    public Packaging visitPackaging(GNode that) {
        enter(that);

        GNode qualId = that.getGeneric(0);
        PathId pathId = visitQualId(qualId);
        StringBuilder sb = new StringBuilder();
        for (Iterator<Id> itr = pathId.getPaths().iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        String name = sb.toString();
        AstScope scope = new AstScope(getRange(that));
        Packaging packaging = new Packaging(name, getNameRange(name, qualId), scope);
        packaging.setIds(pathId.getPaths());

        rootScope.addDef(packaging);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return packaging;
    }

    public PathId visitQualId(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        Id first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        Id nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getName(), nameId.getNameRange(), ElementKind.VARIABLE);
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public List<Id> visitIds(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        Id first = visitId(that.getGeneric(0));
        ids.add(first);

        List others = that.getList(1).list();
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        exit(that);
        return ids;
    }

    public PathId visitPath(GNode that) {
        enter(that);

        PathId pathId = null;

        GNode first = that.getGeneric(0);
        if (first == null || first.getName().equals("Id")) {
            List<Id> ids = new ArrayList<Id>();
            if (first != null) {
                ids.add(visitId(first));
            }
            GNode thisKey = that.getGeneric(1);
            ids.add(new Id("this", getNameRange("this", thisKey), ElementKind.VARIABLE));

            Id nameId = ids.get(ids.size() - 1);
            pathId = new PathId(nameId.getName(), nameId.getNameRange(), ElementKind.VARIABLE);
            pathId.setPaths(ids);
        } else if (first.getName().equals("StableId")) {
            pathId = visitStableId(first);
        }



        exit(that);
        return pathId;
    }

    public PathId visitStableId(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        GNode what = that.getGeneric(0);
        if (what != null) {
            Id first = visitId(what);
            ids.add(first);
        }
        
        List others = null;
        if (that.size() == 2) {
            // Id ( void:".":sep Id )*
            others = that.getList(1).list();
        } else if (that.size() == 3) {
            // ( Id void:".":sep )? ThisKey ( void:".":key Id )*
            Id idThis = visitId(that.getGeneric(1));
            ids.add(idThis);
            
            others = that.getList(2).list();
        } else if (that.size() == 4) {
            // ( Id void:".":sep )? "super":key ClassQualifier? ( void:".":key Id )*
            /** @TODO nameRange */
            Id idSuper = new Id("super", OffsetRange.NONE, ElementKind.VARIABLE);
            ids.add(idSuper);
            
            GNode classQualifierNode = that.getGeneric(2);
            if (classQualifierNode != null) {
                visitChildren(classQualifierNode);
            }
            
            others = that.getList(3).list();
        }

        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        Id nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getName(), nameId.getNameRange(), ElementKind.VARIABLE);
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public Id visitId(GNode that) {
        enter(that);

        String name = that.getString(0);

        exit(that);
        return new Id(name, getNameRange(name, that), ElementKind.VARIABLE);
    }

    public Id visitVarId(GNode that) {
        enter(that);

        String name = that.getString(0);

        exit(that);
        return new Id(name, getNameRange(name, that), ElementKind.VARIABLE);
    }

    public Template visitTmplDef(GNode that) {
        enter(that);

        Template tmpl = null;

        Object what = that.get(1);
        boolean caseOne = false;
        GNode defNode = null;
        if (what != null && what instanceof GNode && ((GNode) what).getName().equals("TraitDef")) {
            defNode = (GNode) what;
        } else {
            caseOne = what != null;
            defNode = that.getGeneric(2);
        }

        if (defNode.getName().equals("ClassDef")) {
            tmpl = visitClassDef(defNode);
            if (caseOne) {
                tmpl.setCaseOne();
            }
        } else if (defNode.getName().equals("ObjectDef")) {
            tmpl = visitObjectDef(defNode);
            if (caseOne) {
                tmpl.setCaseOne();
            }
        } else {
            tmpl = visitTraitDef((GNode) what);
        }


        exit(that);
        return tmpl;
    }

    public ClassTemplate visitClassDef(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getRange(that));
        scopeStack.push(scope);

        Id id = visitId(that.getGeneric(0));
        ClassTemplate classTmpl = new ClassTemplate(id.getName(), id.getNameRange(), scope);

        currScope.addDef(classTmpl);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            visitChildren(typeParamClauseNode);
        }

        String modifier = "public";
        GNode modifierNode = that.getGeneric(3);
        if (modifierNode != null) {
            modifier = visitAccessModifier(modifierNode);
        }

        List<Function> constructors = visitClassParamClauses(that.getGeneric(4));
        for (Function constructor : constructors) {
            constructor.setName(id.getName());
            constructor.setNameRange(id.getNameRange());
        }
        visitChildren(that.getGeneric(5)); // ClassTemplateOpt

        scopeStack.pop();

        exit(that);
        return classTmpl;
    }

    public List<Function> visitClassParamClauses(GNode that) {
        enter(that);

        List<Function> constructors = new ArrayList<Function>();

        List classParamClauseNodes = that.getList(0).list();
        for (Object classParamClauseNode : classParamClauseNodes) {
            constructors.add(visitClassParamClause((GNode) classParamClauseNode));
        }

        exit(that);
        return constructors;
    }

    public Function visitClassParamClause(GNode that) {
        enter(that);

        List<Var> params = null;
        GNode classParamsNode = that.getGeneric(0);
        if (classParamsNode != null) {
            params = visitClassParams(classParamsNode);
        } else {
            params = Collections.<Var>emptyList();
        }

        AstScope scope = new AstScope(getRange(that));
        Function constructor = new Function("this", getRange(that), scope, ElementKind.CONSTRUCTOR);
        constructor.setParam(params);

        scopeStack.peek().addDef(constructor);

        exit(that);
        return constructor;
    }

    public List<Var> visitClassParams(GNode that) {
        enter(that);

        List<Var> params = new ArrayList<Var>();

        Var first = visitClassParam(that.getGeneric(0));
        params.add(first);

        List others = that.getList(1).list();
        for (Object param : others) {
            params.add(visitClassParam((GNode) param));
        }

        exit(that);
        return params;
    }

    public Var visitClassParam(GNode that) {
        enter(that);

        List annotations = that.getList(0).list();
        Id id = visitId(that.getGeneric(2));
        AstScope scope = new AstScope(getRange(that));
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(3);
        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            param.setType(type);
        }

        scopeStack.peek().addDef(param);

        exit(that);
        return param;
    }

    public TraitTemplate visitTraitDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getRange(that));
        TraitTemplate traitTmpl = new TraitTemplate(id.getName(), id.getNameRange(), scope);

        scopeStack.peek().addDef(traitTmpl);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return traitTmpl;
    }

    public ObjectTemplate visitObjectDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getRange(that));
        ObjectTemplate objectTmpl = new ObjectTemplate(id.getName(), id.getNameRange(), scope);

        scopeStack.peek().addDef(objectTmpl);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return objectTmpl;
    }

    public Type visitTypeDcl(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getRange(that));
        Type type = new Type(id.getName(), id.getNameRange(), scope);

        scopeStack.peek().addDef(type);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return type;
    }

    public Type visitTypeDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getRange(that));
        Type type = new Type(id.getName(), id.getNameRange(), scope);

        scopeStack.peek().addDef(type);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return type;
    }

    public void visitFunDcl(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getRange(that));
        scopeStack.push(scope);

        Function function = visitFunSig(that.getGeneric(0));
        GNode typeNode = that.getGeneric(1);
        if (typeNode != null) {
            TypeRef type = visitType(typeNode);
            function.setType(type);
        }

        currScope.addDef(function);

        scopeStack.pop();

        exit(that);
    }

    public Function visitFunDef(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getRange(that));
        scopeStack.push(scope);

        Function function = visitFunSig(that.getGeneric(0));
        GNode secondNode = that.getGeneric(1);
        if (secondNode != null) {
            if (secondNode.getName().equals("Type")) {
                TypeRef type = visitType(secondNode);
                function.setType(type);

                // Expr
                visitChildren(that.getGeneric(2));
            } else {
                // Block
                visitChildren(secondNode);
            }
        } else {
            // Expr
            visitChildren(that.getGeneric(2));
        }

        currScope.addDef(function);

        scopeStack.pop();

        exit(that);
        return function;
    }

    public Function visitConstructorFunDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0)); // // This("this")

        List<Var> params = visitParamClause(that.getGeneric(1));
        List<Var> paramsOther = visitParamClauses(that.getGeneric(2));
        params.addAll(paramsOther);

        visitChildren(that.getGeneric(3));

        AstScope scope = new AstScope(getRange(that));
        Function function = new Function(id.getName(), id.getNameRange(), scope, ElementKind.CONSTRUCTOR);
        function.setParam(params);

        scopeStack.peek().addDef(function);

        exit(that);
        return function;
    }

    public Function visitFunSig(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        GNode funTypeParamClauseNode = that.getGeneric(1);
        if (funTypeParamClauseNode != null) {
            visitChildren(funTypeParamClauseNode);
        }
        List<Var> params = visitParamClauses(that.getGeneric(2));

        Function function = new Function(id.getName(), id.getNameRange(), scopeStack.peek(), ElementKind.METHOD);
        function.setParam(params);

        exit(that);
        return function;
    }

    public List<Var> visitParamClauses(GNode that) {
        enter(that);

        List<Var> params = new ArrayList<Var>();

        List paramClauseNodes = that.getList(0).list();
        for (Object paramClauseNode : paramClauseNodes) {
            params.addAll(visitParamClause((GNode) paramClauseNode));
        }

        exit(that);
        return params;
    }

    public List<Var> visitParamClause(GNode that) {
        enter(that);

        List<Var> params = null;

        GNode paramsNode = that.getGeneric(0);
        if (paramsNode != null) {
            params = visitParams(paramsNode);
        } else {
            params = Collections.<Var>emptyList();
        }

        exit(that);
        return params;
    }

    public List<Var> visitParams(GNode that) {
        enter(that);

        List<Var> params = new ArrayList<Var>();

        Var first = visitParam(that.getGeneric(0));
        params.add(first);

        List others = that.getList(1).list();
        for (Object param : others) {
            params.add(visitParam((GNode) param));
        }

        exit(that);
        return params;
    }

    public Var visitParam(GNode that) {
        enter(that);

        List annotations = that.getList(0).list();
        Id id = visitId(that.getGeneric(1));
        AstScope scope = new AstScope(getRange(that));
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(2);
        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            param.setType(type);
        }

        scopeStack.peek().addDef(param);

        exit(that);
        return param;
    }

    public TypeRef visitParamType(GNode that) {
        enter(that);

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
            more = WrappedType.More.ByName;
        }
        TypeRef wrappedType = visitType(typeNode);
        WrappedType type = new WrappedType(wrappedType.getName(), wrappedType.getNameRange(), ElementKind.CLASS);
        type.setWrappedType(wrappedType);
        type.setMore(more);

        exit(that);
        return type;
    }

    public void visitValDcl(GNode that) {
        enter(that);

        List<Id> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (Id id : ids) {
            AstScope scope = new AstScope(getRange(that));
            Var val = new Var(id, scope, ElementKind.FIELD);
            val.setVal();
            val.setType(type);

            scopeStack.peek().addDef(val);
        }

        exit(that);
    }

    public void visitVarDcl(GNode that) {
        enter(that);

        List<Id> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (Id id : ids) {
            AstScope scope = new AstScope(getRange(that));
            Var var = new Var(id, scope, ElementKind.FIELD);
            var.setType(type);

            scopeStack.peek().addDef(var);
        }

        exit(that);
    }

    public void visitValDef(GNode that) {
        enter(that);

        List<Id> ids = visitPatDef(that.getGeneric(0));
        AstScope scope = new AstScope(getRange(that));
        for (Id id : ids) {
            Var var = new Var(id, scope, ElementKind.FIELD);
            var.setVal();

            scopeStack.peek().addDef(var);
        }

        exit(that);
    }

    public void visitVarDef(GNode that) {
        enter(that);

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Ids")) {
            List<Id> ids = visitIds(what);
            TypeRef type = visitType(that.getGeneric(1));
            AstScope scope = new AstScope(getRange(that));
            for (Id id : ids) {
                Var var = new Var(id, scope, ElementKind.FIELD);
                var.setType(type);

                scopeStack.peek().addDef(var);
            }
        } else {
            List<Id> ids = visitPatDef(what);
            AstScope scope = new AstScope(getRange(that));
            for (Id id : ids) {
                Var var = new Var(id, scope, ElementKind.FIELD);

                scopeStack.peek().addDef(var);
            }
        }

        exit(that);
    }

    public List<Id> visitPatDef(GNode that) {
        enter(that);

        List<Id> ids = visitNoTypedPattern(that.getGeneric(0));
        for (Object o : that.getList(1).list()) {
            ids.addAll(visitNoTypedPattern((GNode) o));
        }

        GNode typeNode = that.getGeneric(2);
        TypeRef type = typeNode == null ? null : visitType(typeNode);
        for (AstElement id : ids) {
            id.setType(type);
        }
        visitChildren(that.getGeneric(3));

        exit(that);
        return ids;
    }

    public void visitCaseClause(GNode that) {
        enter(that);

        AstScope scope = new AstScope(getRange(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Pattern")) {
            // Pattern
            List<Id> ids = visitPattern(what);
            for (Id id : ids) {
                Var var = new Var(id, new AstScope(id.getNameRange()), ElementKind.VARIABLE);

                scopeStack.peek().addDef(var);
            }

            GNode guardNode = that.getGeneric(1);
            if (guardNode != null) {
                visitChildren(guardNode);
            }
            // Block
            visitBlock(that.getGeneric(2));
        } else {
            // in funType
            if (what.getName().endsWith("VarId")) {
                Id id = visitVarId(what);
                Var var = new Var(id, new AstScope(id.getNameRange()), ElementKind.VARIABLE);

                scopeStack.peek().addDef(var);

                // FunTypeInCaseClause
                visitChildren(that.getGeneric(1));
                // Block
                visitBlock(that.getGeneric(2));

            } else {
                // "_" FunTypeInCaseClause
                visitChildren(that.getGeneric(1));
            }
        }


        scopeStack.pop();
        exit(that);
    }

    public List<Id> visitPatterns(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        Object what = that.getGeneric(0);
        if (what instanceof GNode) {
            ids.addAll(visitPattern((GNode) what));
            for (Object o : that.getList(1).list()) {
                ids.addAll(visitPattern((GNode) o));
            }
        } else {
            // "_*"
            ids = Collections.<Id>emptyList();
        }

        exit(that);
        return ids;
    }

    public List<Id> visitPattern(GNode that) {
        enter(that);

        List<Id> ids = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("AlternatePattern")) {
            ids = visitAlternatePattern(what);
        } else {
            ids = visitPattern1(what);
        }

        exit(that);
        return ids;
    }

    public List<Id> visitAlternatePattern(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();
        
        /** @Todo emeger ids with same name (and type) */
        ids.addAll(visitPattern1(that.getGeneric(0)));
        ids.addAll(visitPattern1(that.getGeneric(1)));
        for (Object o : that.getList(2).list()) {
            ids.addAll(visitPattern1((GNode) o));
        }

        exit(that);
        return ids;
    }

    public List<Id> visitPattern1(GNode that) {
        enter(that);

        GNode what = that.getGeneric(0);
        List<Id> ids = what.getName().equals("TypedPattern")
                ? visitTypedPattern(what)
                : visitNoTypedPattern(what);

        exit(that);
        return ids;
    }

    public List<Id> visitTypedPattern(GNode that) {
        enter(that);

        List<Id> ids = Collections.<Id>emptyList();
        Object what = that.get(0);
        if (what instanceof GNode && ((GNode) what).getName().equals("VarId")) {
            Id id = visitVarId((GNode) what);
            ids = new ArrayList<Id>();
            ids.add(id);

            TypeRef type = visitType(that.getGeneric(1));
            if (id != null) {
                id.setType(type);
            }
        }

        exit(that);
        return ids;
    }

    public List<Id> visitNoTypedPattern(GNode that) {
        enter(that);

        GNode what = that.getGeneric(0);
        List<Id> ids = what.getName().equals("AtPattern")
                ? visitAtPattern(what)
                : visitPattern3(what);

        exit(that);
        return ids;
    }

    public List<Id> visitAtPattern(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        ids.add(visitVarId(that.getGeneric(0)));
        ids.addAll(visitPattern3(that.getGeneric(1)));

        exit(that);
        return ids;
    }

    public List<Id> visitPattern3(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        ids.addAll(visitSimplePattern(that.getGeneric(0)));
        if (that.size() == 3) {
            GNode infixPatternTailNode = that.getGeneric(1);
            List others = that.getList(2).list();
            others.add(infixPatternTailNode);
            for (Object o : others) {
                GNode tailNode = (GNode) o;
                ids.addAll(visitSimplePattern(tailNode.getGeneric(1)));
            }
        }

        exit(that);
        return ids;
    }

    public List<Id> visitSimplePattern(GNode that) {
        enter(that);

        List<Id> ids = null;
        if (that.getName().equals("SimpleTuplePattern")) {
            ids = visitSimpleTuplePattern(that);
        } else if (that.getName().equals("SimpleCallPattern")) {
            ids = visitSimpleCallPattern(that);
        } else if (that.getName().equals("SimpleIdPattern")) {
            ids = visitSimpleIdPattern(that);
        } else {
            ids = Collections.<Id>emptyList();
        }

        exit(that);
        return ids;

    }

    public List<Id> visitSimpleCallPattern(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        ids.add(visitStableId(that.getGeneric(0)));
        List<Id> tupleIds = visitTuplePattern(that.getGeneric(1));
        ids.addAll(tupleIds);

        exit(that);
        return ids;
    }

    public List<Id> visitSimpleTuplePattern(GNode that) {
        enter(that);

        List<Id> ids = visitTuplePattern(that.getGeneric(0));

        exit(that);
        return ids;
    }

    public List<Id> visitSimpleIdPattern(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();
        ids.add(visitStableId(that.getGeneric(0)));

        exit(that);
        return ids;
    }

    public List<Id> visitTuplePattern(GNode that) {
        enter(that);

        List<Id> ids = null;
        
        GNode patternsNode = that.getGeneric(0);
        if (patternsNode != null) {
            ids = visitPatterns(patternsNode);
        } else {
            ids = Collections.<Id>emptyList();
        }

        exit(that);
        return ids;
    }

    public String visitModifier(GNode that) {
        enter(that);

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

        visitChildren(that);

        exit(that);
        return modifier;
    }

    public String visitLocalModifier(GNode that) {
        enter(that);

        exit(that);
        return that.getString(0);
    }

    public String visitAccessModifier(GNode that) {
        enter(that);

        exit(that);
        return that.getString(0);
    }

    public void visitBlock(GNode that) {
        enter(that);

        AstScope scope = new AstScope(getRange(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        for (Object o : that.getList(0).list()) {
            // BlockState
            visitChildren((GNode) o);
        }

        GNode resultExprNode = that.getGeneric(1);
        if (resultExprNode != null) {
            visitChildren(resultExprNode);
        }

        scopeStack.pop();
        exit(that);
    }

    public void visitForExpr(GNode that) {
        enter(that);

        AstScope scope = new AstScope(getRange(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        GNode enumeratorsNode = that.getGeneric(0);
        List<Id> ids = visitEnumerators(enumeratorsNode);
        AstScope varBindingScope = new AstScope(getRange(enumeratorsNode));
        for (Id id : ids) {
            Var var = new Var(id, varBindingScope, ElementKind.VARIABLE);

            scopeStack.peek().addDef(var);
        }

        // Expr
        visitChildren(that.getGeneric(2));

        scopeStack.pop();
        exit(that);
    }

    public List<Id> visitEnumerators(GNode that) {
        enter(that);

        List<Id> ids = visitGenerator(that.getGeneric(0));
        for (Object o : that.getList(1).list()) {
            ids.addAll(visitEnumerator((GNode) o));
        }

        exit(that);
        return ids;
    }

    public List<Id> visitEnumerator(GNode that) {
        enter(that);

        List<Id> ids = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Generator")) {
            ids = visitGenerator(what);
        } else if (what.getName().equals("Guard")) {
            ids = Collections.<Id>emptyList();
        } else {
            // void:"val":key Pattern1 "=":key Expr
            ids = visitPattern1(what);
            // Expr
            visitChildren(that.getGeneric(1));
        }

        exit(that);
        return ids;
    }

    public List<Id> visitGenerator(GNode that) {
        enter(that);

        // Pattern1
        List<Id> ids = visitPattern1(that.getGeneric(0));
        // Expr
        visitChildren(that.getGeneric(1));

        GNode guardNode = that.getGeneric(2);
        if (guardNode != null) {
            visitChildren(guardNode);
        }

        exit(that);
        return ids;
    }

    public AstElement visitSimpleExprRest(GNode that) {
        enter(that);

        AstElement element = null;

        Object what = that.get(0);
        if (what instanceof GNode) {
            GNode whatNode = (GNode) what;
            if (whatNode.getName().equals("PathRest")) {
                PathId pathId = visitPath(whatNode.getGeneric(0));
                element = pathId;
            } else {
                element = visitArgumentExprs(whatNode);
            }
        } else {
            element = new AstElement("_", OffsetRange.NONE, ElementKind.OTHER);
        }

        exit(that);
        return element;
    }

    public ArgumentExprs visitArgumentExprs(GNode that) {
        enter(that);

        List<AstElement> args = null;
        
        GNode what = that.getGeneric(0);
        if (what.getName().equals("ParenExpr")) {
            args = visitParenExpr(what);
        } else {
            // BlockExpr
            visitChildren(what);
            // @Todo
            args = Collections.<AstElement>emptyList();
        }
        
        ArgumentExprs argExprs = new ArgumentExprs("args", OffsetRange.NONE, ElementKind.OTHER);
        argExprs.setArgs(args);
        
        exit(that);
        return argExprs;
    }

    public List<AstElement> visitParenExpr(GNode that) {
        enter(that);

        List<AstElement> exprs = null;
        
        GNode exprsNode = that.getGeneric(0);
        if (exprsNode != null) {
            exprs = visitExprs(exprsNode);
        } else {
            exprs = Collections.<AstElement>emptyList();
        }

        exit(that);
        return exprs;
    }

    public List<AstElement> visitExprs(GNode that) {
        enter(that);

        List<AstElement> exprs = new ArrayList<AstElement>();
        GNode first = that.getGeneric(0);
        visitChildren(first);
        exprs.add(new AstElement("expr", OffsetRange.NONE, ElementKind.OTHER));

        for (Object o : that.getList(1).list()) {
            visitChildren(first);
            exprs.add(new AstElement("expr", OffsetRange.NONE, ElementKind.OTHER));
        }

        exit(that);
        return exprs;
    }

    public void visitSimpleIdExpr(GNode that) {
        enter(that);

        PathId id = visitPath(that.getGeneric(0));
        Id first = id.getPaths().get(0);

        List<TypeRef> typeArgs = Collections.<TypeRef>emptyList();
        GNode typeArgsNode = that.getGeneric(1);
        if (typeArgsNode != null) {
            typeArgs = visitTypeArgs(typeArgsNode);
        }

        List<AstElement> rest = new ArrayList<AstElement>();
        for (Object o : that.getList(2).list()) {
            AstElement element = visitSimpleExprRest((GNode) o);
            rest.add(element);
        }

        if (rest.size() > 0 && rest.get(0) instanceof ArgumentExprs) {
            FunRef funRef = new FunRef(first.getName(), first.getNameRange(), ElementKind.CALL);
            funRef.setParams(((ArgumentExprs) rest.get(0)).getArgs());
            
            scopeStack.peek().addRef(funRef);
        } else {
            IdRef idRef = new IdRef(first.getName(), first.getNameRange(), ElementKind.VARIABLE);

            scopeStack.peek().addRef(idRef);
        }

        // Type
        GNode typeNode = that.getGeneric(3);
        if (typeNode != null) {
            visitChildren(typeNode);
        }

        exit(that);
    }

    public TypeRef visitType(GNode that) {
        enter(that);

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

        exit(that);
        return type;
    }

    public TypeRef visitInfixType(GNode that) {
        enter(that);

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

        exit(that);
        return type;
    }

    public TypeRef visitCompoundType(GNode that) {
        enter(that);

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

        exit(that);
        return type;
    }

    public TypeRef visitAnnotType(GNode that) {
        enter(that);

        List annotations = that.getList(0).list();
        SimpleType type = visitSimpleType(that.getGeneric(1));

        exit(that);
        return type;
    }

    public SimpleType visitSimpleType(GNode that) {
        enter(that);

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

        exit(that);
        return type;
    }

    public List<TypeRef> visitTypeArgs(GNode that) {
        enter(that);

        List<TypeRef> typeArgs = null;

        Object what = that.get(0);
        if (what instanceof GNode) {
            typeArgs = visitTypes((GNode) what);
        } else {
            // wildcard
            typeArgs = Collections.<TypeRef>emptyList();
        }

        exit(that);
        return typeArgs;
    }

    public List<TypeRef> visitTypes(GNode that) {
        enter(that);

        List<TypeRef> types = new ArrayList<TypeRef>();

        TypeRef first = visitType(that.getGeneric(0));
        types.add(first);

        List others = that.getList(1).list();
        for (Object typeNode : others) {
            types.add(visitType((GNode) typeNode));
        }

        exit(that);
        return types;
    }

    public SimpleType visitSimpleIdType(GNode that) {
        enter(that);

        PathId id = visitStableId(that.getGeneric(0));
        Id first = id.getPaths().get(0);
        SimpleIdType type = new SimpleIdType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setPaths(id.getPaths());

        scopeStack.peek().addRef(type);

        exit(that);
        return type;
    }

    public SimpleType visitSimpleSingletonType(GNode that) {
        enter(that);

        PathId id = visitPath(that.getGeneric(0));
        Id first = id.getPaths().get(0);
        SimpleIdType type = new SimpleIdType(first.getName(), first.getNameRange(), ElementKind.CLASS);
        type.setPaths(id.getPaths());

        scopeStack.peek().addRef(type);

        exit(that);
        return type;
    }

    public SimpleType visitSimpleTupleType(GNode that) {
        enter(that);

        List<TypeRef> types = visitTypes(that.getGeneric(0));
        SimpleTupleType type = new SimpleTupleType("TupleType", getRange(that), ElementKind.CLASS);
        type.setTypes(types);

        for (TypeRef typeRef : types) {
            scopeStack.peek().addRef(typeRef);
        }

        exit(that);
        return type;
    }
}
