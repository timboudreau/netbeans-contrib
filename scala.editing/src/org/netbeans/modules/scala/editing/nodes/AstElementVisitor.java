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
import java.util.Comparator;
import java.util.List;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.scala.editing.nodes.FunRef.ApplyFunRef;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Pair;

/**
 *
 * @author Caoyuan Deng
 */
public class AstElementVisitor extends AstVisitor {

    public AstElementVisitor(Node rootNode, TokenHierarchy th) {
        super(rootNode, th);
    }

    public Packaging visitPackage(GNode that) {
        enter(that);

        GNode qualId = that.getGeneric(0);
        PathId pathId = visitQualId(qualId);
        Id latest = pathId.getPaths().get(pathId.getPaths().size() - 1);

        AstScope scope = new AstScope(rootScope.getBoundsTokens());
        scopeStack.peek().addScope(scope);

        Packaging packaging = new Packaging(latest.getIdToken(), scope);
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

        GNode qualIdNode = that.getGeneric(0);
        PathId pathId = visitQualId(qualIdNode);
        Id latest = pathId.getPaths().get(pathId.getPaths().size() - 1);

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);

        Packaging packaging = new Packaging(latest.getIdToken(), scope);
        packaging.setIds(pathId.getPaths());

        rootScope.addDef(packaging);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return packaging;
    }

    public void visitTopStat(GNode that) {
        enter(that);

        int size = that.size();
        if (size == 0) {
            // empty stat
        } else if (size == 1) {
            // Import or Expr
            GNode node = that.getGeneric(0);
            if (node.getName().equals("Import")) {
                visitImport(node);
            } else if (node.getName().equals("Packaging")) {
                visitPackaging(node);
            }
        } else if (size == 3) {
            for (Object anno : that.getList(0)) {
                // @Todo
            }

            List<String> modifiers = null;
            for (Object modifierNode : that.getList(1)) {
                if (modifiers == null) {
                    modifiers = new ArrayList<String>();
                }
                String modifier = visitModifier((GNode) modifierNode);
                modifiers.add(modifier);
            }

            GNode thirdNode = that.getGeneric(2);
            AstDef def = null;
            if (thirdNode.getName().equals("TmplDef")) {
                def = visitTmplDef(thirdNode);
            }

            if (modifiers != null) {
                for (String modifier : modifiers) {
                    def.addModifier(modifier);
                }
            }
        }

        exit(that);
    }

    public void visitTemplateStat(GNode that) {
        enter(that);

        int size = that.size();
        if (size == 0) {
            // empty stat
        } else if (size == 1) {
            // Import or Expr
            GNode node = that.getGeneric(0);
            if (node.getName().equals("Import")) {
                visitImport(node);
            } else if (node.getName().equals("Expr")) {
                visitExpr(node);
            }
        } else if (size == 3) {
            for (Object anno : that.getList(0)) {
                // @Todo
            }

            List<String> modifiers = null;
            for (Object modifierNode : that.getList(1)) {
                if (modifiers == null) {
                    modifiers = new ArrayList<String>();
                }
                String modifier = visitModifier((GNode) modifierNode);
                modifiers.add(modifier);
            }

            GNode thirdNode = that.getGeneric(2);
            List<? extends AstDef> defs = null;
            if (thirdNode.getName().equals("Def")) {
                defs = visitDef(thirdNode);
            } else if (thirdNode.getName().equals("Dcl")) {
                defs = visitDcl(thirdNode);
            }

            if (modifiers != null) {
                for (AstDef def : defs) {
                    for (String modifier : modifiers) {
                        def.addModifier(modifier);
                    }
                }
            }
        }

        exit(that);
    }

    public List<Import> visitImport(GNode that) {
        enter(that);

        List<Import> imports = new ArrayList<Import>();

        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Error")) {
            visitError(firstNode);
        } else {
            Import first = visitImportExpr(firstNode);
            imports.add(first);

            for (Object other : that.getList(1)) {
                imports.add(visitImportExpr((GNode) other));
            }
        }

        exit(that);
        return imports;
    }

    public Import visitImportExpr(GNode that) {
        enter(that);

        PathId pathId = visitStableId(that.getGeneric(0));
        List<Id> paths = pathId.getPaths();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);

        // We put lastId as the idToken, so when search closest element on caret, will return this def
        Import importDef = new Import(paths.get(paths.size() - 1).getIdToken(), scope);

        scopeStack.peek().addDef(importDef);

        scopeStack.push(scope);

        GNode what = that.getGeneric(1);
        if (what != null) {
            if (what.getName().equals("WildKey")) {
                importDef.setWild();
            } else {
                List<TypeRef> importedTypes = visitImportSelectors(what);
                importDef.setImportedTypes(importedTypes);
            }
        } else {
            // latest id is imported type
            Id latest = paths.get(paths.size() - 1);
            paths.remove(latest);
            SimpleType type = new SimpleType(latest.getName(), latest.getIdToken(), ElementKind.CLASS);

            scopeStack.peek().addRef(type);

            List<TypeRef> importedTypes = Collections.<TypeRef>singletonList(type);
            importDef.setImportedTypes(importedTypes);
        }

        importDef.setPaths(paths);

        scopeStack.pop();
        exit(that);
        return importDef;
    }

    public List<TypeRef> visitImportSelectors(GNode that) {
        enter(that);

        List<TypeRef> types = new ArrayList<TypeRef>();
        for (Object other : that.getList(0)) {
            types.add(visitImportSelector((GNode) other));
        }

        TypeRef latest = null;
        GNode what = that.getGeneric(1);
        if (what.getName().equals("WildKey")) {
            latest = new SimpleType("_", getIdToken(what), ElementKind.CLASS);
        } else {
            latest = visitImportSelector(what);
        }
        types.add(latest);

        exit(that);
        return types;
    }

    public TypeRef visitImportSelector(GNode that) {
        enter(that);

        TypeRef type = null;

        Id id = visitId(that.getGeneric(0));
        SimpleType idType = new SimpleType(id.getName(), id.getIdToken(), ElementKind.CLASS);

        GNode funTypeTail = that.getGeneric(1);
        if (funTypeTail != null) {
            FunType funType = new FunType();
            funType.setLhs(idType);

            SimpleType tailType = null;
            if (funTypeTail.getName().equals("WildKey")) {
                tailType = new SimpleType("_", getIdToken(funTypeTail), ElementKind.CLASS);
            } else {
                Id tailId = visitId(funTypeTail);
                tailType = new SimpleType(tailId.getName(), tailId.getIdToken(), ElementKind.CLASS);
            }

            funType.setRhs(tailType);

            type = funType;
        } else {
            type = idType;
        }

        exit(that);
        return type;
    }

    public PathId visitQualId(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        Id first = visitId(that.getGeneric(0));
        ids.add(first);

        Pair others = that.getList(1);
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        Id nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getIdToken(), ElementKind.VARIABLE);
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public List<Id> visitIds(GNode that) {
        enter(that);

        List<Id> ids = new ArrayList<Id>();

        Id first = visitId(that.getGeneric(0));
        ids.add(first);

        Pair others = that.getList(1);
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
            ids.add(new Id("this", getIdToken(thisKey), ElementKind.VARIABLE));

            Id nameId = ids.get(ids.size() - 1);
            pathId = new PathId(nameId.getIdToken(), ElementKind.VARIABLE);
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

        GNode firstIdNode = that.getGeneric(0);
        if (firstIdNode != null) {
            Id first = visitId(firstIdNode);
            ids.add(first);
        }

        Pair others = null;
        GNode error = null;
        if (that.size() == 3) {
            // Id ( void:".":sep Id )* ( void:"." SKIP ErrorIdExpected )?
            others = that.getList(1);

            error = that.getGeneric(2);
        } else if (that.size() == 4) {
            // ( Id void:".":sep )? ThisKey ( void:".":key Id )* ( void:"." SKIP ErrorIdExpected )?
            Id idThis = visitId(that.getGeneric(1));
            ids.add(idThis);

            others = that.getList(2);

            error = that.getGeneric(3);
        } else if (that.size() == 5) {
            // ( Id void:".":sep )? SuperKey ClassQualifier? ( void:".":key Id )* ( void:"." SKIP ErrorIdExpected )?
            Id idSuper = visitId(that.getGeneric(1));
            ids.add(idSuper);

            GNode classQualifierNode = that.getGeneric(2);
            if (classQualifierNode != null) {
                visitChildren(classQualifierNode);
            }

            others = that.getList(3);

            error = that.getGeneric(4);
        }

        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        if (error != null) {
            visitError(error);
        }

        Id nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getIdToken(), ElementKind.VARIABLE);
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public Id visitId(GNode that) {
        enter(that);

        Id id = new Id(that.getString(0), getIdToken(that), ElementKind.VARIABLE);

        exit(that);
        return id;
    }

    public Id visitVarId(GNode that) {
        enter(that);

        exit(that);
        return new Id(that.getString(0), getIdToken(that), ElementKind.VARIABLE);
    }

    public Literal visitLiteral(GNode that) {
        enter(that);

        Literal literal = new Literal(getBoundsTokens(that));

        Object first = that.getGeneric(0);
        GNode literalNode = null;
        if (first != null) {
            if (first instanceof GNode) {
                literalNode = (GNode) first;
            } else {
                literalNode = that.getGeneric(1);
            }
        } else {
            literalNode = that.getGeneric(1);
        }

        if (literalNode.getName().equals("FloatingPointLiteral")) {
            literal.setType(TypeRef.Float);
        } else if (literalNode.getName().equals("IntegerLiteral")) {
            literal.setType(TypeRef.Int);
        } else if (literalNode.getName().equals("BooleanLiteral")) {
            literal.setType(TypeRef.Boolean);
        } else if (literalNode.getName().equals("NullLiteral")) {
            literal.setType(TypeRef.Null);
        } else if (literalNode.getName().equals("CharacterLiteral")) {
            literal.setType(TypeRef.Char);
        } else if (literalNode.getName().equals("StringLiteral")) {
            literal.setType(TypeRef.String);
        } else if (literalNode.getName().equals("SymbolLiteral")) {
            literal.setType(TypeRef.Symbol);
        }

        exit(that);
        return literal;
    }

    public List<? extends AstDef> visitDef(GNode that) {
        enter(that);

        List<? extends AstDef> defs = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("PatVarDef")) {
            List<Var> vars = visitPatVarDef(what);
            defs = vars;
        } else if (what.getName().equals("FunDef")) {
            AstDef def = visitFunDef(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TypeDef")) {
            AstDef def = visitTypeDef(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TmplDef")) {
            AstDef def = visitTmplDef(what);
            defs = Collections.singletonList(def);
        }

        exit(that);
        return defs;
    }

    public List<? extends AstDef> visitDcl(GNode that) {
        enter(that);

        List<? extends AstDef> defs = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ValDcl")) {
            List<Var> vars = visitValDcl(what);
            defs = vars;
        } else if (what.getName().equals("VarDcl")) {
            List<Var> vars = visitVarDcl(what);
            defs = vars;
        } else if (what.getName().equals("FunDcl")) {
            AstDef def = visitFunDcl(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TypeDcl")) {
            AstDef def = visitTypeDcl(what);
            defs = Collections.singletonList(def);
        }

        exit(that);
        return defs;
    }

    public Template visitTmplDef(GNode that) {
        enter(that);

        Template tmpl = null;

        Object what = that.get(0);
        boolean caseOne = false;
        GNode defNode = null;
        if (what != null && what instanceof GNode && ((GNode) what).getName().equals("TraitDef")) {
            defNode = (GNode) what;
        } else {
            caseOne = what != null;
            defNode = that.getGeneric(1);
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
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Id id = visitId(that.getGeneric(0));
        ClassTemplate classTmpl = new ClassTemplate(id, scope);

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
            constructor.setIdToken(id.getIdToken());
        }

        List<SimpleType> parents = visitClassTemplateOpt(that.getGeneric(5));
        classTmpl.setExtendsWith(parents);
        for (SimpleType parent : parents) {
            scopeStack.peek().addRef(parent);
        }

        scopeStack.pop();

        exit(that);
        return classTmpl;
    }

    public TraitTemplate visitTraitDef(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Id id = visitId(that.getGeneric(0));
        TraitTemplate traitTmpl = new TraitTemplate(id, scope);

        currScope.addDef(traitTmpl);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            visitChildren(typeParamClauseNode);
        }

        List<SimpleType> parents = visitTraitTemplateOpt(that.getGeneric(2));
        traitTmpl.setExtendsWith(parents);
        for (SimpleType parent : parents) {
            scopeStack.peek().addRef(parent);
        }

        scopeStack.pop();

        exit(that);
        return traitTmpl;
    }

    public List<SimpleType> visitClassTemplateOpt(GNode that) {
        enter(that);

        List<SimpleType> parents = Collections.<SimpleType>emptyList();

        if (that.size() == 2) {
            int extendsType = 0;
            GNode extendsTypeNode = that.getGeneric(0);
            if (extendsTypeNode != null) {
                if (extendsTypeNode.getString(0).equals("extends")) {
                    extendsType = 1;
                } else {
                    // "<:"
                    extendsType = 2;
                }
            }

            GNode what = that.getGeneric(1);
            if (what.getName().equals("ClassTemplate")) {
                parents = visitClassTemplate(what);
            } else {
                // TemplateBody
                visitChildren(what);
            }
        }

        exit(that);
        return parents;
    }

    public List<SimpleType> visitClassTemplate(GNode that) {
        enter(that);

        GNode earlyDefsNode = that.getGeneric(0);
        if (earlyDefsNode != null) {
            visitChildren(earlyDefsNode);
        }

        List<SimpleType> parents = visitClassParents(that.getGeneric(1));

        GNode templateBodyNode = that.getGeneric(2);
        if (templateBodyNode != null) {
            visitChildren(templateBodyNode);
        }

        exit(that);
        return parents;
    }

    public List<SimpleType> visitClassParents(GNode that) {
        enter(that);

        List<SimpleType> parents = new ArrayList<SimpleType>();

        SimpleType extendsParent = visitConstr(that.getGeneric(0));
        parents.add(extendsParent);

        for (Object o : that.getList(1)) {
            SimpleType withParent = visitAnnotType((GNode) o);
            parents.add(withParent);
        }

        exit(that);
        return parents;
    }

    public SimpleType visitConstr(GNode that) {
        enter(that);

        SimpleType annotType = visitAnnotType(that.getGeneric(0));

        for (Object argExprs : that.getList(1)) {
            visitArgumentExprs((GNode) argExprs);
        }

        exit(that);
        return annotType;
    }

    public List<SimpleType> visitTraitTemplateOpt(GNode that) {
        enter(that);

        List<SimpleType> parents = Collections.<SimpleType>emptyList();

        if (that.size() == 2) {
            int extendsType = 0;
            GNode extendsTypeNode = that.getGeneric(0);
            if (extendsTypeNode != null) {
                if (extendsTypeNode.getString(0).equals("extends")) {
                    extendsType = 1;
                } else {
                    // "<:"
                    extendsType = 2;
                }
            }

            GNode what = that.getGeneric(1);
            if (what.getName().equals("TraitTemplate")) {
                parents = visitTraitTemplate(what);
            } else {
                // TemplateBody
                visitChildren(what);
            }
        }

        exit(that);
        return parents;
    }

    public List<SimpleType> visitTraitTemplate(GNode that) {
        enter(that);

        GNode earlyDefsNode = that.getGeneric(0);
        if (earlyDefsNode != null) {
            visitChildren(earlyDefsNode);
        }

        List<SimpleType> parents = visitTraitParents(that.getGeneric(1));

        GNode templateBodyNode = that.getGeneric(2);
        if (templateBodyNode != null) {
            visitChildren(templateBodyNode);
        }

        exit(that);
        return parents;
    }

    public List<SimpleType> visitTraitParents(GNode that) {
        enter(that);

        List<SimpleType> parents = new ArrayList<SimpleType>();

        SimpleType firstParent = visitAnnotType(that.getGeneric(0));
        parents.add(firstParent);

        for (Object o : that.getList(1)) {
            SimpleType withParent = visitAnnotType((GNode) o);
            parents.add(withParent);
        }

        exit(that);
        return parents;
    }

    public List<Function> visitClassParamClauses(GNode that) {
        enter(that);

        List<Function> constructors = new ArrayList<Function>();

        Pair classParamClauseNodes = that.getList(0);
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

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);

        Function constructor = new Function("this", null, scope, ElementKind.CONSTRUCTOR);
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

        Pair others = that.getList(1);
        for (Object param : others) {
            params.add(visitClassParam((GNode) param));
        }

        exit(that);
        return params;
    }

    public Var visitClassParam(GNode that) {
        enter(that);

        Pair annotations = that.getList(0);
        Id id = visitId(that.getGeneric(2));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(3);
        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            param.setType(type);

            scope.addRef(type);
        }

        scopeStack.peek().addDef(param);

        exit(that);
        return param;
    }

    public ObjectTemplate visitObjectDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        ObjectTemplate objectTmpl = new ObjectTemplate(id, scope);

        scopeStack.peek().addDef(objectTmpl);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return objectTmpl;
    }

    public TypeAlias visitTypeDcl(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        TypeAlias type = new TypeAlias(id, scope);

        scopeStack.peek().addDef(type);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return type;
    }

    public TypeAlias visitTypeDef(GNode that) {
        enter(that);

        Id id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        TypeAlias type = new TypeAlias(id, scope);

        scopeStack.peek().addDef(type);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return type;
    }

    public Function visitFunDcl(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Function function = visitFunSig(that.getGeneric(0));
        GNode typeNode = that.getGeneric(1);
        if (typeNode != null) {
            TypeRef type = visitType(typeNode);
            scope.addRef(type);

            function.setType(type);
        }

        currScope.addDef(function);

        scopeStack.pop();

        exit(that);
        return function;
    }

    public Function visitFunDef(GNode that) {
        enter(that);

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Function function = visitFunSig(that.getGeneric(0));
        GNode secondNode = that.getGeneric(1);
        if (secondNode != null) {
            if (secondNode.getName().equals("Type")) {
                TypeRef type = visitType(secondNode);
                scope.addRef(type);

                function.setType(type);

                visitExpr(that.getGeneric(2));
            } else {
                visitBlock(secondNode);
            }
        } else {
            visitExpr(that.getGeneric(2));
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

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Function function = new Function(id.getName(), id.getIdToken(), scope, ElementKind.CONSTRUCTOR);
        function.setParam(params);

        Template enclosingTemplate = scopeStack.peek().getEnclosingDef(Template.class);
        if (enclosingTemplate != null) {
            function.setName(enclosingTemplate.getName());
        }

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

        Function function = new Function(id.getName(), id.getIdToken(), scopeStack.peek(), ElementKind.METHOD);
        function.setParam(params);

        exit(that);
        return function;
    }

    /**
     * @Note: null params is meaningful, it's diffrent from empty params
     * @return null or list of Var 
     */
    public List<Var> visitParamClauses(GNode that) {
        enter(that);

        List<Var> params = null;

        Pair paramClauseNodes = that.getList(0);
        if (!paramClauseNodes.isEmpty()) {
            params = new ArrayList<Var>();
            for (Object paramClauseNode : paramClauseNodes) {
                params.addAll(visitParamClause((GNode) paramClauseNode));
            }
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

        Pair others = that.getList(1);
        for (Object param : others) {
            params.add(visitParam((GNode) param));
        }

        exit(that);
        return params;
    }

    public Var visitParam(GNode that) {
        enter(that);

        Pair annotations = that.getList(0);
        Id id = visitId(that.getGeneric(1));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(2);
        if (paramTypeNode != null) {
            TypeRef type = visitParamType(paramTypeNode);
            param.setType(type);
            scope.addRef(type);
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
        TypeRef toWrap = visitType(typeNode);
        WrappedType type = new WrappedType(toWrap.getIdToken(), ElementKind.CLASS);
        type.setWrappedType(toWrap);
        type.setMore(more);

        exit(that);
        return type;
    }

    public List<Var> visitPatVarDef(GNode that) {
        enter(that);

        List<Var> vars = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ValDef")) {
            vars = visitValDef(what);
        } else if (what.getName().equals("VarDef")) {
            vars = visitVarDef(what);
        }

        exit(that);
        return vars;
    }

    public List<Var> visitValDcl(GNode that) {
        enter(that);

        List<Var> vars = new ArrayList<Var>();

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        List<Id> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (Id id : ids) {
            Var val = new Var(id, scope, ElementKind.FIELD);
            val.setVal();
            val.setType(type);
            scope.addRef(type);

            currScope.addDef(val);
            vars.add(val);
        }

        scopeStack.pop();
        exit(that);
        return vars;
    }

    public List<Var> visitVarDcl(GNode that) {
        enter(that);

        List<Var> vars = new ArrayList<Var>();

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        List<Id> ids = visitIds(that.getGeneric(0));
        TypeRef type = visitType(that.getGeneric(1));

        for (Id id : ids) {
            Var var = new Var(id, scope, ElementKind.FIELD);
            var.setType(type);
            scope.addRef(type);

            currScope.addDef(var);
            vars.add(var);
        }

        scopeStack.pop();
        exit(that);
        return vars;
    }

    public List<Var> visitValDef(GNode that) {
        enter(that);

        List<Var> vars = new ArrayList<Var>();

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Object[] patDef = visitPatDef(that.getGeneric(0));
        List<Id> ids = (List<Id>) patDef[0];
        AstExpr expr = (AstExpr) patDef[1];
        for (Id id : ids) {
            Var var = new Var(id, scope, ElementKind.FIELD);
            var.setVal();
            var.setExpr(expr);

            currScope.addDef(var);
            vars.add(var);
        }

        scopeStack.pop();
        exit(that);
        return vars;
    }

    public List<Var> visitVarDef(GNode that) {
        enter(that);

        List<Var> vars = new ArrayList<Var>();

        AstScope currScope = scopeStack.peek();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Ids")) {
            List<Id> ids = visitIds(what);
            TypeRef type = visitType(that.getGeneric(1));
            for (Id id : ids) {
                Var var = new Var(id, scope, ElementKind.FIELD);
                var.setType(type);
                scope.addRef(type);

                currScope.addDef(var);
                vars.add(var);
            }
        } else {
            Object[] patDef = visitPatDef(what);
            List<Id> ids = (List<Id>) patDef[0];
            AstExpr expr = (AstExpr) patDef[1];
            scope = new AstScope(getBoundsTokens(that));
            for (Id id : ids) {
                Var var = new Var(id, scope, ElementKind.FIELD);
                var.setExpr(expr);

                currScope.addDef(var);
                vars.add(var);
            }
        }

        scopeStack.pop();
        exit(that);
        return vars;
    }

    public Object[] visitPatDef(GNode that) {
        enter(that);

        List<Id> ids = visitNoTypedPattern(that.getGeneric(0));
        for (Object o : that.getList(1)) {
            ids.addAll(visitNoTypedPattern((GNode) o));
        }

        GNode typeNode = that.getGeneric(2);
        TypeRef type = typeNode == null ? null : visitType(typeNode);
        for (Id id : ids) {
            if (type != null) {
                id.setType(type);
                scopeStack.peek().addRef(type);
            }
        }

        AstExpr expr = visitExpr(that.getGeneric(3));

        exit(that);
        return new Object[]{ids, expr};
    }
    
    public void visitCaseClauses(GNode that) {
        enter(that);
        
        visitCaseClause(that.getGeneric(0)); // first
        
        for (Object o : that.getList(1)) {
            visitCaseClause((GNode) o);
        }
        
        exit(that);
    }

    public void visitCaseClause(GNode that) {
        enter(that);

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Object what = that.get(0);
        if (what instanceof GNode) {
            GNode whatNode = (GNode) what;

            if (whatNode.getName().equals("Pattern")) {
                // Pattern
                AstScope varScope = new AstScope(getBoundsTokens(whatNode));
                scopeStack.peek().addScope(varScope);

                List<Id> ids = visitPattern(whatNode);
                for (Id id : ids) {
                    Var var = new Var(id, varScope, ElementKind.VARIABLE);

                    scopeStack.peek().addDef(var);
                }

                GNode guardNode = that.getGeneric(1);
                if (guardNode != null) {
                    visitChildren(guardNode);
                }
                // Block
                visitBlock(that.getGeneric(2));
            } else if (whatNode.getName().endsWith("VarId")) {
                AstScope varScope = new AstScope(getBoundsTokens(whatNode));
                scopeStack.peek().addScope(varScope);

                Id id = visitVarId(whatNode);
                Var var = new Var(id, varScope, ElementKind.VARIABLE);

                scopeStack.peek().addDef(var);

                // FunTypeInCaseClause
                visitChildren(that.getGeneric(1));
                // Block
                visitBlock(that.getGeneric(2));
            }
        } else {
            // what = "_"
            // "_" FunTypeInCaseClause Block
            visitChildren(that.getGeneric(1));
            // Block
            visitBlock(that.getGeneric(2));
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
            for (Object o : that.getList(1)) {
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
        for (Object o : that.getList(2)) {
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
                scopeStack.peek().addRef(type);
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
        } else {
            modifier = "abstract";
        }

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

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        for (Object o : that.getList(0)) {
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

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        GNode enumeratorsNode = that.getGeneric(0);
        List<Id> ids = visitEnumerators(enumeratorsNode);
        AstScope varScope = new AstScope(getBoundsTokens(enumeratorsNode));
        scopeStack.peek().addScope(varScope);
        for (Id id : ids) {
            Var var = new Var(id, varScope, ElementKind.VARIABLE);

            scopeStack.peek().addDef(var);
        }

        // AstExpr
        AstExpr yieldExpr = visitExpr(that.getGeneric(1));

        scopeStack.pop();
        exit(that);
    }

    public List<Id> visitEnumerators(GNode that) {
        enter(that);

        List<Id> ids = visitGenerator(that.getGeneric(0));
        for (Object o : that.getList(1)) {
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
            // AstExpr
            AstExpr expr = visitExpr(that.getGeneric(1));
        }

        exit(that);
        return ids;
    }

    public List<Id> visitGenerator(GNode that) {
        enter(that);

        // Pattern1
        List<Id> ids = visitPattern1(that.getGeneric(0));
        // AstExpr
        AstExpr expr = visitExpr(that.getGeneric(1));

        GNode guardNode = that.getGeneric(2);
        if (guardNode != null) {
            visitChildren(guardNode);
        }

        exit(that);
        return ids;
    }

    public ArgumentExprs visitArgumentExprs(GNode that) {
        enter(that);

        List<AstExpr> args = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ParenExpr")) {
            args = visitParenExpr(what);
        } else {
            BlockExpr arg = visitBlockExpr(what);
            args = Collections.<AstExpr>singletonList(arg);
        }

        ArgumentExprs argExprs = new ArgumentExprs(ElementKind.OTHER);
        argExprs.setArgs(args);

        exit(that);
        return argExprs;
    }

    public List<AstExpr> visitParenExpr(GNode that) {
        enter(that);

        List<AstExpr> exprs;

        GNode exprsNode = that.getGeneric(0);
        if (exprsNode != null) {
            exprs = visitExprs(exprsNode);
        } else {
            exprs = Collections.<AstExpr>emptyList();
        }

        exit(that);
        return exprs;
    }
    
    public BlockExpr visitBlockExpr(GNode that) {
        enter(that);
        
        GNode what = that.getGeneric(0);
        if (what.getName().equals("CaseClauses")) {
            visitCaseClauses(what);
        } else if (what.getName().equals("Block")) {
            visitBlock(what);
        }
        
        // @todo
        BlockExpr expr = new BlockExpr(getBoundsTokens(that));
        
        exit(that);
        return expr;
    }

    public List<AstExpr> visitExprs(GNode that) {
        enter(that);

        List<AstExpr> exprs = new ArrayList<AstExpr>();
        GNode first = that.getGeneric(0);
        AstExpr firstExpr = visitExpr(first);
        exprs.add(firstExpr);

        for (Object o : that.getList(1)) {
            AstExpr expr = visitExpr((GNode) o);
            exprs.add(expr);
        }

        exit(that);
        return exprs;
    }

    public AstExpr visitExpr(GNode that) {
        enter(that);

        AstExpr expr = null;

        boolean hasAddedToScope = false;
        GNode what = that.getGeneric(0);
        if (what.getName().equals("NotFunExpr")) {
            expr = visitNotFunExpr(what);
            hasAddedToScope = true;
        } else if (what.getName().equals("IfExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("WhileExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("TryExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("DoExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("ForExpr")) {
            visitForExpr(what);
        } else if (what.getName().equals("ThrowExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("ReturnExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("AssignmentExpr")) {
            expr = visitAssignmentExpr(what);
        } else if (what.getName().equals("AscriptionExpr")) {
            visitChildren(what);
        } else if (what.getName().equals("MatchExpr")) {
            visitChildren(what);
        }

        if (expr != null) {
            if (!hasAddedToScope) {
                scopeStack.peek().addExpr(expr);
            }
        } else {
            /** @Todo */
            expr = new AstExpr(getBoundsTokens(that));
        }

        exit(that);
        return expr;
    }

    public AstExpr visitAssignmentExpr(GNode that) {
        enter(that);

        AssignmentExpr expr = new AssignmentExpr(getBoundsTokens(that));

        AstExpr lhs = visitSimpleExpr(that.getGeneric(0));
        AstExpr rhs = visitExpr(that.getGeneric(1));
        lhs.setType(rhs.getType());
        expr.setLhs(lhs);
        expr.setRhs(rhs);

        exit(that);
        return expr;
    }

    public AstExpr visitNotFunExpr(GNode that) {
        enter(that);

        /**
         * all other sub node has been specify a generic node's name via '@',
         * except pure PostfixExpr
         */
        AstExpr expr = visitPostfixExpr(that.getGeneric(0));
        /* Since NotFunExpr can be dispatched to here by visitChildren, bypassing
         * visitExpr, we should add to scope in visitPostfixExpr or visitInfixExpr
         */

        exit(that);
        return expr;
    }

    public AstExpr visitPostfixExpr(GNode that) {
        enter(that);

        AstExpr expr = visitInfixExpr(that.getGeneric(0));
        assert expr instanceof Postfixable;

        GNode postfixOpNode = that.getGeneric(1);
        if (postfixOpNode != null) {
            ((Postfixable) expr).setPostfixOp(visitId(postfixOpNode));
        }

        exit(that);
        return expr;
    }

    public AstExpr visitInfixExpr(GNode that) {
        enter(that);

        AstExpr expr = null;

        SimpleExpr first = visitPrefixExpr(that.getGeneric(0));

        // Should add expr to scope here
        scopeStack.peek().addExpr(first);

        Pair others = that.getList(1);
        if (!others.isEmpty()) {
            List<SimpleExpr> exprs = new ArrayList<SimpleExpr>();
            exprs.add(first);
            List<Id> ops = new ArrayList<Id>();

            List<InfixOpExprs> infixOpExprsList = new ArrayList<InfixOpExprs>();

            SimpleExpr lExpr = first;
            for (int i = 0; i < others.size(); i++) {
                GNode otherNode = (GNode) others.get(i);

                Id op = visitId(otherNode.getGeneric(0));
                SimpleExpr rExpr = visitPrefixExpr(otherNode.getGeneric(1));

                // Should add expr to scope here
                scopeStack.peek().addExpr(rExpr);

                ops.add(op);
                exprs.add(rExpr);

                /** precedence:
                (all letters)
                |
                ^
                &
                < >
                = !
                :
                + -
                 * / %
                (all other special characters)
                 */
                int precedence = 0;
                String opName = op.getName();
                if (opName.equals("*") || opName.equals("/") || opName.equals("%")) {
                    precedence = 1 * 10000 + i;
                } else if (opName.equals("+") || opName.equals("-")) {
                    precedence = 2 * 10000 + i;
                } else if (opName.equals(":")) {
                    precedence = 3 * 10000 + i;
                } else if (opName.equals("=") || opName.equals("!")) {
                    precedence = 4 * 10000 + i;
                } else if (opName.equals("<") || opName.equals(">")) {
                    precedence = 5 * 10000 + i;
                } else if (opName.equals("&")) {
                    precedence = 6 * 10000 + i;
                } else if (opName.equals("^")) {
                    precedence = 7 * 10000 + i;
                } else if (opName.equals("|")) {
                    precedence = 8 * 10000 + i;
                } else if (Character.isISOControl(opName.charAt(0))) {
                    precedence = 0 * 10000 + i;
                } else {
                    precedence = 9 * 10000 + i;
                }

                InfixOpExprs opExprs = new InfixOpExprs();
                opExprs.op = op;
                opExprs.precedence = precedence;
                opExprs.lhs = lExpr;
                opExprs.rhs = rExpr;

                infixOpExprsList.add(opExprs);

                lExpr = rExpr;
            }
            InfixExpr infixExpr = new InfixExpr(getBoundsTokens(that));
            infixExpr.setOps(ops);
            infixExpr.setExprs(exprs);

            Collections.sort(infixOpExprsList, InfixOpExprsComparator.getInstance());

            AstElement currLhs = infixOpExprsList.get(0).lhs;
            FunRef lastFunRef = null;
            for (InfixOpExprs currInfixOpExprs : infixOpExprsList) {
                Id callId = currInfixOpExprs.op;
                lastFunRef = new FunRef(callId.getIdToken(), ElementKind.CALL);
                lastFunRef.setBase(currLhs);
                lastFunRef.setCall(callId);
                lastFunRef.setParams(Collections.<AstElement>singletonList(currInfixOpExprs.rhs));

                currLhs = lastFunRef;

                scopeStack.peek().addRef(lastFunRef);
            }

            infixExpr.setTopFunRef(lastFunRef);

            expr = infixExpr;
            scopeStack.peek().addExpr(infixExpr);
        } else {
            expr = first;
            // has been added to scope 
        }

        exit(that);
        return expr;
    }

    public SimpleExpr visitPrefixExpr(GNode that) {
        enter(that);

        SimpleExpr expr = visitSimpleExpr(that.getGeneric(1));

        String prefixOp = that.getString(0);
        if (prefixOp != null) {
            expr.setPrefix(prefixOp.trim());
        }

        exit(that);
        return expr;
    }

    public SimpleExpr visitSimpleExpr(GNode that) {
        enter(that);

        SimpleExpr expr = new SimpleExpr(getBoundsTokens(that));

        AstElement base = null;
        GNode baseNode = that.getGeneric(0);
        GNode typeArgsNode = null;
        Pair memberList = null;
        GNode errorNode = null;
        GNode typeNode = null;
        if (baseNode.getName().equals("XmlExpr")) {
            visitChildren(baseNode);
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
        } else if (baseNode.getName().equals("Literal")) {
            Literal literal = visitLiteral(baseNode);
            base = literal;
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
        } else if (baseNode.getName().equals("Path")) {
            PathId id = visitPath(baseNode);
            base = id;
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(3);
            errorNode = that.getGeneric(4);
            typeNode = that.getGeneric(5);
        } else if (baseNode.getName().equals("WildKey")) {
            Id id = visitId(baseNode);
            base = id;
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
        } else if (baseNode.getName().equals("ParenExpr")) {
            visitChildren(baseNode);
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
            typeNode = that.getGeneric(4);
        } else if (baseNode.getName().equals("BlockExpr")) {
            visitChildren(baseNode);
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
        } else if (baseNode.getName().equals("NewExpr")) {
            NewExpr newExpr = visitNewExpr(baseNode);
            base = newExpr;
            typeArgsNode = that.getGeneric(1);
            memberList = that.getList(2);
            errorNode = that.getGeneric(3);
        }

        if (base == null) {
            // @TODO
            base = new AstExpr(getBoundsTokens(
                    baseNode)) {

                @Override
                public String getName() {
                    return "todo";
                }
            };

        }

        expr.setBase(base);
        AstElement currBase = base;

        if (typeArgsNode != null) {
            List<TypeRef> typeArgs = visitTypeArgs(typeArgsNode);
            expr.setTypeArgs(typeArgs);
        }

        if (baseNode.getName().equals("Path")) {
            GNode directArgExprsNode = that.getGeneric(2);
            ArgumentExprs argExprs = directArgExprsNode == null ? null : visitArgumentExprs(directArgExprsNode);

            PathId pathId = (PathId) base;
            List<Id> paths = pathId.getPaths();
            Id firstId = paths.get(0);

            if (argExprs != null) {
                // dog.sound.concat(arg0)
                // Function ref, we should fetch last id of Paths as call name of funRef
                Id callId = paths.get(paths.size() - 1);
                paths.remove(callId);

                FunRef funRef = new FunRef(callId.getIdToken(), ElementKind.CALL);

                if (paths.size() > 0) {
                    // Cannot resolve the ref here, should be done when global type inference
                    funRef.setBase(pathId);
                }
                
                funRef.setCall(callId);

                funRef.setParams(argExprs.getArgs());

                expr.setBase(funRef);

                scopeStack.peek().addRef(funRef);

                currBase = funRef;
            } else {
                // dog.sound
                // Similest case, only one PathId
                IdRef idRef = new IdRef(firstId.getName(), firstId.getIdToken(), ElementKind.VARIABLE);

                scopeStack.peek().addRef(idRef);

                if (paths.size() > 1) {
                    // first's field
                    Id fieldId = paths.get(paths.size() - 1);
                    paths.remove(fieldId);

                    FieldRef fieldRef = new FieldRef(fieldId.getIdToken());

                    // Cannot resolve the ref here, should be done when global type inference
                    fieldRef.setBase(pathId);
                    fieldRef.setField(fieldId);

                    expr.setBase(fieldRef);

                    scopeStack.peek().addRef(fieldRef);

                    currBase = fieldRef;
                } else {
                    expr.setBase(idRef);

                    currBase = idRef;
                }
            }

        }

        List<AstRef> memberChain = null;
        for (Object o : memberList) {
            if (memberChain == null) {
                memberChain = new ArrayList<AstRef>();
            }
            AstRef member = visitMember((GNode) o);
            if (member instanceof FunRef) {
                ((FunRef) member).setBase(currBase);
            } else if (member instanceof FieldRef) {
                ((FieldRef) member).setBase(currBase);
            }

            currBase = member;

            scopeStack.peek().addRef(member);

            memberChain.add(member);
        }
        if (memberChain == null) {
            memberChain = Collections.<AstRef>emptyList();
        }
        expr.setMemberChain(memberChain);

        if (errorNode != null) {
            visitError(errorNode);
        }

        if (typeNode != null) {
            TypeRef type = visitType(typeNode);
            expr.setType(type);

            scopeStack.peek().addRef(type);
        }

        exit(that);
        return expr;
    }

    public AstRef visitMember(GNode that) {
        enter(that);

        AstRef ref;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("WildKey")) {
            Id id = visitId(what);
            ref = new IdRef("_", id.getIdToken(), ElementKind.OTHER);
        } else if (what.getName().equals("ArgumentExprs")) {
            // apply call
            ArgumentExprs argExprs = visitArgumentExprs(what);
            ApplyFunRef apply = new ApplyFunRef();
            apply.setParams(argExprs.getArgs());
            // base not set yet, should be set later by SimpleExpr
            ref = apply;
        } else {
            // void:".":sep Id TypeArgs? ArgumentExprs?
            Id id = visitId(what);

            GNode typeArgsNode = that.getGeneric(1);
            if (typeArgsNode != null) {
                List<TypeRef> typeArgs = visitTypeArgs(typeArgsNode);
            }

            GNode argsNode = that.getGeneric(2);
            if (argsNode != null) {
                ArgumentExprs argExprs = visitArgumentExprs(argsNode);

                FunRef funRef = new FunRef(id.getIdToken(), ElementKind.CALL);
                funRef.setCall(id);
                funRef.setParams(argExprs.getArgs());
                // base not set yet, should be set later by SimpleExpr
                ref = funRef;
            } else {
                FieldRef fieldRef = new FieldRef(id.getIdToken());
                fieldRef.setField(id);
                // base not set yet, should be set later by SimpleExpr
                ref = fieldRef;
            }
        }

        exit(that);
        return ref;
    }

    public NewExpr visitNewExpr(GNode that) {
        enter(that);
        NewExpr expr = new NewExpr(getBoundsTokens(that));

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ClassTemplate")) {
            List<SimpleType> parents = visitClassTemplate(what);
            expr.setParents(parents);
        } else if (what.getName().equals("TemplateBody")) {
            // TemplateBody
            visitChildren(what);
        } else {
            visitError(what);
        }

        exit(that);
        return expr;
    }

    public TypeRef visitType(GNode that) {
        enter(that);

        TypeRef type = null;

        GNode node = that.getGeneric(0);
        if (node.getName().equals("CallByNameFunType")) {
            GNode lhsNode = node.getGeneric(0);
            TypeRef lhs = null;
            if (lhsNode != null) {
                lhs = visitType(lhsNode);
            }

            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);

            // use rhs as the idToken
            FunType funType = new FunType();
            funType.setLhs(lhs);
            funType.setRhs(rhs);

            type = funType;
        } else if (node.getName().equals("NormalFunType")) {
            GNode lhsNode = node.getGeneric(0);
            TypeRef lhs = visitInfixType(lhsNode);
            GNode rhsNode = node.getGeneric(1);
            TypeRef rhs = visitType(rhsNode);

            FunType funType = new FunType();
            funType.setLhs(lhs);
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

        TypeRef first = visitCompoundType(that.getGeneric(0));

        Pair others = that.getList(1);
        if (!others.isEmpty()) {
            List<TypeRef> types = new ArrayList<TypeRef>();
            List<Id> ops = new ArrayList<Id>();
            types.add(first);

            for (Object rest : others) {
                GNode restNode = (GNode) rest;
                ops.add(visitId(restNode.getGeneric(0)));
                types.add(visitCompoundType(restNode.getGeneric(1)));
            }

            InfixType infixType = new InfixType(first.getIdToken(), ElementKind.CLASS);
            infixType.setTypes(types);
            infixType.setOps(ops);

            type = infixType;
        } else {
            type = first;
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
            return new SimpleIdType(null, ElementKind.CLASS) {

                @Override
                public String getName() {
                    return "{...}";
                }
            };
        }

        TypeRef first = visitAnnotType(that.getGeneric(0));
        types.add(first);

        Pair others = that.getList(1);
        for (Object typeNode : others) {
            types.add(visitAnnotType((GNode) typeNode));
        }

        if (types.size() == 1) {
            type = first;
        } else {

            CompoundType compoundType = new CompoundType(first.getIdToken(), ElementKind.CLASS);
            compoundType.setTypes(types);

            type = compoundType;
        }

        exit(that);
        return type;
    }

    public SimpleType visitAnnotType(GNode that) {
        enter(that);

        Pair annotations = that.getList(0);
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
        for (Object typeArgsNode : that.getList(1)) {
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

        Pair others = that.getList(1);
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
        SimpleIdType type = new SimpleIdType(first.getIdToken(), ElementKind.CLASS);
        type.setPaths(id.getPaths());

        scopeStack.peek().addRef(type);

        exit(that);
        return type;
    }

    public SimpleType visitSimpleSingletonType(GNode that) {
        enter(that);

        PathId id = visitPath(that.getGeneric(0));
        Id first = id.getPaths().get(0);
        SimpleIdType type = new SimpleIdType(first.getIdToken(), ElementKind.CLASS);
        type.setPaths(id.getPaths());

        scopeStack.peek().addRef(type);

        exit(that);
        return type;
    }

    public SimpleType visitSimpleTupleType(GNode that) {
        enter(that);

        List<TypeRef> types = visitTypes(that.getGeneric(0));
        // idToken is used to get the offset here, just set first type's idToken as idToken, 
        SimpleTupleType type = new SimpleTupleType(types.get(0).getIdToken(), ElementKind.CLASS);
        type.setTypes(types);

        for (TypeRef typeRef : types) {
            scopeStack.peek().addRef(typeRef);
        }

        exit(that);
        return type;
    }
    // ----- Helper inner classes
    private static class InfixOpExprs {

        Id op;
        int precedence;
        AstElement lhs;
        AstElement rhs;
    }

    private static class InfixOpExprsComparator implements Comparator<InfixOpExprs> {

        private static InfixOpExprsComparator instance;

        static InfixOpExprsComparator getInstance() {
            if (instance == null) {
                instance = new InfixOpExprsComparator();
            }
            return instance;
        }

        private InfixOpExprsComparator() {
        }

        public int compare(InfixOpExprs o1, InfixOpExprs o2) {
            return o1.precedence < o2.precedence ? -1 : 1;
        }
    }
}
