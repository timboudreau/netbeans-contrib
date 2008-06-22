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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.scala.editing.nodes.exprs.SimpleExpr;
import org.netbeans.modules.scala.editing.nodes.exprs.InfixExpr;
import org.netbeans.modules.scala.editing.nodes.exprs.ArgumentExprs;
import org.netbeans.modules.scala.editing.nodes.exprs.BlockExpr;
import org.netbeans.modules.scala.editing.nodes.exprs.NewExpr;
import org.netbeans.modules.scala.editing.nodes.exprs.AssignmentExpr;
import org.netbeans.modules.scala.editing.nodes.exprs.Postfixable;
import org.netbeans.modules.scala.editing.nodes.FunctionCall.ApplyFunctionCall;
import org.netbeans.modules.scala.editing.nodes.tmpls.ObjectTemplate;
import org.netbeans.modules.scala.editing.nodes.tmpls.Template;
import org.netbeans.modules.scala.editing.nodes.tmpls.ClassTemplate;
import org.netbeans.modules.scala.editing.nodes.tmpls.TraitTemplate;
import org.netbeans.modules.scala.editing.nodes.types.TypeDef;
import org.netbeans.modules.scala.editing.nodes.types.ParamType;
import org.netbeans.modules.scala.editing.nodes.types.InfixType;
import org.netbeans.modules.scala.editing.nodes.types.CompoundType;
import org.netbeans.modules.scala.editing.nodes.types.SimpleTupleType;
import org.netbeans.modules.scala.editing.nodes.types.SimpleIdType;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.scala.editing.nodes.types.FunType;
import org.netbeans.modules.scala.editing.nodes.types.TypeParam;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Pair;

/**
 *
 * @author Caoyuan Deng
 */
public class AstNodeVisitor extends AstVisitor {

    public AstNodeVisitor(Node rootNode, TokenHierarchy th) {
        super(rootNode, th);
    }

    public Packaging visitPackage(GNode that) {
        enter(that);

        GNode qualId = that.getGeneric(0);
        PathId pathId = visitQualId(qualId);
        AstId latest = pathId.getPaths().get(pathId.getPaths().size() - 1);

        AstScope scope = new AstScope(rootScope.getBoundsTokens());
        scopeStack.peek().addScope(scope);

        Packaging packaging = new Packaging(latest.getPickToken(), scope);
        packaging.setTop();
        packaging.setIds(pathId.getPaths());

        rootScope.addElement(packaging);

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
        AstId latest = pathId.getPaths().get(pathId.getPaths().size() - 1);

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);

        Packaging packaging = new Packaging(latest.getPickToken(), scope);
        packaging.setIds(pathId.getPaths());

        rootScope.addElement(packaging);

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
            for (Object annot : that.getList(0)) {
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
            AstElement def = null;
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
            List<? extends AstElement> defs = null;
            if (thirdNode.getName().equals("Def")) {
                defs = visitDef(thirdNode);
            } else if (thirdNode.getName().equals("Dcl")) {
                defs = visitDcl(thirdNode);
            }

            if (modifiers != null) {
                for (AstElement def : defs) {
                    for (String modifier : modifiers) {
                        def.addModifier(modifier);
                    }
                }
            }
        }

        exit(that);
    }

    public List<Importing> visitImport(GNode that) {
        enter(that);

        List<Importing> imports = new ArrayList<Importing>();

        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Error")) {
            visitError(firstNode);
        } else {
            Importing first = visitImportExpr(firstNode);
            imports.add(first);

            for (Object other : that.getList(1)) {
                imports.add(visitImportExpr((GNode) other));
            }
        }

        exit(that);
        return imports;
    }

    public Importing visitImportExpr(GNode that) {
        enter(that);

        PathId pathId = visitStableId(that.getGeneric(0));
        List<AstId> paths = pathId.getPaths();
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);

        // We put lastId as the idToken, so when search closest element on caret, will return this def
        Importing importDef = new Importing(paths.get(paths.size() - 1).getPickToken(), scope);

        scopeStack.peek().addElement(importDef);

        scopeStack.push(scope);

        GNode what = that.getGeneric(1);
        if (what != null) {
            if (what.getName().equals("WildKey")) {
                importDef.setWild();
            } else {
                List<Type> importedTypes = visitImportSelectors(what);
                importDef.setImportedTypes(importedTypes);
            }
        } else {
            // latest id is imported type
            AstId latest = paths.get(paths.size() - 1);
            paths.remove(latest);
            Type type = new Type(latest.getSimpleName(), latest.getPickToken(), TypeKind.DECLARED);

            scopeStack.peek().addMirror(type);

            List<Type> importedTypes = Collections.<Type>singletonList(type);
            importDef.setImportedTypes(importedTypes);
        }

        importDef.setPaths(paths);

        scopeStack.pop();
        exit(that);
        return importDef;
    }

    public List<Type> visitImportSelectors(GNode that) {
        enter(that);

        List<Type> types = new ArrayList<Type>();
        for (Object other : that.getList(0)) {
            types.add(visitImportSelector((GNode) other));
        }

        Type latest = null;
        GNode what = that.getGeneric(1);
        if (what.getName().equals("WildKey")) {
            latest = new Type("_", getIdToken(what), TypeKind.OTHER);
        } else {
            latest = visitImportSelector(what);
        }
        types.add(latest);

        exit(that);
        return types;
    }

    public Type visitImportSelector(GNode that) {
        enter(that);

        Type type = null;

        AstId id = visitId(that.getGeneric(0));
        Type idType = new Type(id.getSimpleName(), id.getPickToken(), TypeKind.OTHER);

        GNode funTypeTail = that.getGeneric(1);
        if (funTypeTail != null) {
            FunType funType = new FunType();
            funType.setLhs(idType);

            Type tailType = null;
            if (funTypeTail.getName().equals("WildKey")) {
                tailType = new Type("_", getIdToken(funTypeTail), TypeKind.OTHER);
            } else {
                AstId tailId = visitId(funTypeTail);
                tailType = new Type(tailId.getSimpleName(), tailId.getPickToken(), TypeKind.OTHER);
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

        List<AstId> ids = new ArrayList<AstId>();

        AstId first = visitId(that.getGeneric(0));
        ids.add(first);

        Pair others = that.getList(1);
        for (Object id : others) {
            ids.add(visitId((GNode) id));
        }

        AstId nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getPickToken());
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public List<AstId> visitIds(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        AstId first = visitId(that.getGeneric(0));
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
            List<AstId> ids = new ArrayList<AstId>();
            if (first != null) {
                ids.add(visitId(first));
            }
            GNode thisKey = that.getGeneric(1);
            ids.add(new AstId("this", getIdToken(thisKey)));

            AstId nameId = ids.get(ids.size() - 1);
            pathId = new PathId(nameId.getPickToken());
            pathId.setPaths(ids);
        } else if (first.getName().equals("StableId")) {
            pathId = visitStableId(first);
        }

        exit(that);
        return pathId;
    }

    public PathId visitStableId(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        GNode firstIdNode = that.getGeneric(0);
        if (firstIdNode != null) {
            AstId first = visitId(firstIdNode);
            ids.add(first);
        }

        Pair others = null;
        GNode error = null;
        if (that.size() == 3) {
            // AstId ( void:".":sep AstId )* ( void:"." SKIP ErrorIdExpected )?
            others = that.getList(1);

            error = that.getGeneric(2);
        } else if (that.size() == 4) {
            // ( AstId void:".":sep )? ThisKey ( void:".":key AstId )* ( void:"." SKIP ErrorIdExpected )?
            AstId idThis = visitId(that.getGeneric(1));
            ids.add(idThis);

            others = that.getList(2);

            error = that.getGeneric(3);
        } else if (that.size() == 5) {
            // ( AstId void:".":sep )? SuperKey ClassQualifier? ( void:".":key AstId )* ( void:"." SKIP ErrorIdExpected )?
            AstId idSuper = visitId(that.getGeneric(1));
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

        AstId nameId = ids.get(ids.size() - 1);
        PathId pathId = new PathId(nameId.getPickToken());
        pathId.setPaths(ids);

        exit(that);
        return pathId;
    }

    public AstId visitId(GNode that) {
        enter(that);

        AstId id = new AstId(that.getString(0), getIdToken(that));

        exit(that);
        return id;
    }

    public AstId visitVarId(GNode that) {
        enter(that);

        exit(that);
        return new AstId(that.getString(0), getIdToken(that));
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
            literal.setType(Type.Float);
        } else if (literalNode.getName().equals("IntegerLiteral")) {
            literal.setType(Type.Int);
        } else if (literalNode.getName().equals("BooleanLiteral")) {
            literal.setType(Type.Boolean);
        } else if (literalNode.getName().equals("NullLiteral")) {
            literal.setType(Type.Null);
        } else if (literalNode.getName().equals("CharacterLiteral")) {
            literal.setType(Type.Char);
        } else if (literalNode.getName().equals("StringLiteral")) {
            literal.setType(Type.String);
        } else if (literalNode.getName().equals("SymbolLiteral")) {
            literal.setType(Type.Symbol);
        }

        exit(that);
        return literal;
    }

    public List<? extends AstElement> visitDef(GNode that) {
        enter(that);

        List<? extends AstElement> defs = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("PatVarDef")) {
            List<Var> vars = visitPatVarDef(what);
            defs = vars;
        } else if (what.getName().equals("FunDef")) {
            AstElement def = visitFunDef(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TypeDef")) {
            AstElement def = visitTypeDef(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TmplDef")) {
            AstElement def = visitTmplDef(what);
            defs = Collections.singletonList(def);
        }

        exit(that);
        return defs;
    }

    public List<? extends AstElement> visitDcl(GNode that) {
        enter(that);

        List<? extends AstElement> defs = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ValDcl")) {
            List<Var> vars = visitValDcl(what);
            defs = vars;
        } else if (what.getName().equals("VarDcl")) {
            List<Var> vars = visitVarDcl(what);
            defs = vars;
        } else if (what.getName().equals("FunDcl")) {
            AstElement def = visitFunDcl(what);
            defs = Collections.singletonList(def);
        } else if (what.getName().equals("TypeDcl")) {
            AstElement def = visitTypeDcl(what);
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

        AstId id = visitId(that.getGeneric(0));
        ClassTemplate classTmpl = new ClassTemplate(id, scope);

        currScope.addElement(classTmpl);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            List<TypeParam> typeParams = visitTypeParamClause(typeParamClauseNode);
            classTmpl.setTypeParameters(typeParams);
        }

        String modifier = "public";
        GNode modifierNode = that.getGeneric(3);
        if (modifierNode != null) {
            modifier = visitAccessModifier(modifierNode);
        }

        List<Function> constructors = visitClassParamClauses(that.getGeneric(4));
        for (Function constructor : constructors) {
            constructor.setSimpleName(id.getSimpleName());
            constructor.setPickToken(id.getPickToken());
        }

        List<Type> parents = visitClassTemplateOpt(that.getGeneric(5));
        if (!parents.isEmpty()) {
            for (Type parent : parents) {
                scopeStack.peek().addMirror(parent);
            }
            // first element is super class
            classTmpl.setSuperClass(parents.get(0));
            // others are traits
            parents.remove(0);
            classTmpl.setWithTraits(parents);
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

        AstId id = visitId(that.getGeneric(0));
        TraitTemplate traitTmpl = new TraitTemplate(id, scope);

        currScope.addElement(traitTmpl);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            List<TypeParam> typeParams = visitTypeParamClause(typeParamClauseNode);
            traitTmpl.setTypeParameters(typeParams);
        }

        List<Type> parents = visitTraitTemplateOpt(that.getGeneric(2));
        if (!parents.isEmpty()) {
            for (Type parent : parents) {
                scopeStack.peek().addMirror(parent);
            }
            // first element is super class
            traitTmpl.setSuperClass(parents.get(0));
            // others are traits
            parents.remove(0);
            traitTmpl.setWithTraits(parents);
        }

        scopeStack.pop();

        exit(that);
        return traitTmpl;
    }

    public List<Type> visitClassTemplateOpt(GNode that) {
        enter(that);

        List<Type> parents = Collections.<Type>emptyList();

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

    public List<Type> visitClassTemplate(GNode that) {
        enter(that);

        GNode earlyDefsNode = that.getGeneric(0);
        if (earlyDefsNode != null) {
            visitChildren(earlyDefsNode);
        }

        List<Type> parents = visitClassParents(that.getGeneric(1));

        GNode templateBodyNode = that.getGeneric(2);
        if (templateBodyNode != null) {
            visitChildren(templateBodyNode);
        }

        exit(that);
        return parents;
    }

    /**
     * @return list of Types, the first one is super class, others are 'with' traits 
     */
    public List<Type> visitClassParents(GNode that) {
        enter(that);

        List<Type> parents = new ArrayList<Type>();

        Type extendsParent = visitConstr(that.getGeneric(0));
        parents.add(extendsParent);

        for (Object o : that.getList(1)) {
            Type withParent = visitAnnotType((GNode) o);
            parents.add(withParent);
        }

        exit(that);
        return parents;
    }

    public Type visitConstr(GNode that) {
        enter(that);

        Type annotType = visitAnnotType(that.getGeneric(0));

        for (Object argExprs : that.getList(1)) {
            visitArgumentExprs((GNode) argExprs);
        }

        exit(that);
        return annotType;
    }

    public List<Type> visitTraitTemplateOpt(GNode that) {
        enter(that);

        List<Type> parents = Collections.<Type>emptyList();

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

    public List<Type> visitTraitTemplate(GNode that) {
        enter(that);

        GNode earlyDefsNode = that.getGeneric(0);
        if (earlyDefsNode != null) {
            visitChildren(earlyDefsNode);
        }

        List<Type> parents = visitTraitParents(that.getGeneric(1));

        GNode templateBodyNode = that.getGeneric(2);
        if (templateBodyNode != null) {
            visitChildren(templateBodyNode);
        }

        exit(that);
        return parents;
    }

    public List<Type> visitTraitParents(GNode that) {
        enter(that);

        List<Type> parents = new ArrayList<Type>();

        Type firstParent = visitAnnotType(that.getGeneric(0));
        parents.add(firstParent);

        for (Object o : that.getList(1)) {
            Type withParent = visitAnnotType((GNode) o);
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

        Function constructor = new Function("this", null, scope, true);
        constructor.setParameters(params);

        scopeStack.peek().addElement(constructor);

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
        AstId id = visitId(that.getGeneric(2));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(3);
        if (paramTypeNode != null) {
            Type type = visitParamType(paramTypeNode);
            param.setType(type);

            scope.addMirror(type);
        }

        scopeStack.peek().addElement(param);

        exit(that);
        return param;
    }

    public ObjectTemplate visitObjectDef(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        ObjectTemplate objectTmpl = new ObjectTemplate(id, scope);

        scopeStack.peek().addElement(objectTmpl);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return objectTmpl;
    }

    public TypeDef visitTypeDcl(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        TypeDef type = new TypeDef(id, scope);

        scopeStack.peek().addElement(type);

        scopeStack.push(scope);
        visitChildren(that);
        scopeStack.pop();

        exit(that);
        return type;
    }

    public TypeDef visitTypeDef(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        TypeDef typeAlias = new TypeDef(id, scope);

        scopeStack.peek().addElement(typeAlias);

        scopeStack.push(scope);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            visitTypeParamClause(typeParamClauseNode);
        }

        Type alias = visitType(that.getGeneric(2));
        typeAlias.setValue(alias);

        scopeStack.pop();

        exit(that);
        return typeAlias;
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
            Type type = visitType(typeNode);
            scope.addMirror(type);

            function.setType(type);
        }

        currScope.addElement(function);

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
                Type type = visitType(secondNode);
                scope.addMirror(type);

                function.setType(type);

                visitExpr(that.getGeneric(2));
            } else {
                visitBlock(secondNode);
            }
        } else {
            visitExpr(that.getGeneric(2));
        }

        currScope.addElement(function);

        scopeStack.pop();

        exit(that);
        return function;
    }

    public Function visitConstructorFunDef(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0)); // // This("this")

        List<Var> params = visitParamClause(that.getGeneric(1));
        List<Var> paramsOther = visitParamClauses(that.getGeneric(2));
        params.addAll(paramsOther);

        visitChildren(that.getGeneric(3));

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Function function = new Function(id.getSimpleName(), id.getPickToken(), scope, true);
        function.setParameters(params);

        Template enclosingTemplate = scopeStack.peek().getEnclosingElement(Template.class);
        if (enclosingTemplate != null) {
            function.setSimpleName(enclosingTemplate.getSimpleName());
        }

        scopeStack.peek().addElement(function);

        exit(that);
        return function;
    }

    public Function visitFunSig(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0));
        List<Var> params = visitParamClauses(that.getGeneric(2));

        Function function = new Function(id.getSimpleName(), id.getPickToken(), scopeStack.peek(), false);
        function.setParameters(params);

        GNode funTypeParamClauseNode = that.getGeneric(1);
        if (funTypeParamClauseNode != null) {
            List<TypeParam> typeParams = visitFunTypeParamClause(funTypeParamClauseNode);
            function.setTypeParameters(typeParams);
        }

        exit(that);
        return function;
    }

    public List<TypeParam> visitTypeParamClause(GNode that) {
        enter(that);

        List<TypeParam> typeParams = new ArrayList<TypeParam>();

        TypeParam first = visitVariantTypeParam(that.getGeneric(0));
        typeParams.add(first);

        for (Object o : that.getList(1)) {
            TypeParam other = visitVariantTypeParam((GNode) o);
            typeParams.add(other);
        }

        exit(that);
        return typeParams;
    }

    public List<TypeParam> visitFunTypeParamClause(GNode that) {
        enter(that);

        List<TypeParam> typeParams = new ArrayList<TypeParam>();

        TypeParam first = visitTypeParam(that.getGeneric(0));
        typeParams.add(first);

        for (Object o : that.getList(1)) {
            TypeParam other = visitTypeParam((GNode) o);
            typeParams.add(other);
        }

        exit(that);
        return typeParams;
    }

    public TypeParam visitVariantTypeParam(GNode that) {
        enter(that);

        String variant = that.getString(0);

        TypeParam typeParam = visitTypeParam(that.getGeneric(1));

        typeParam.setVariant(variant);

        exit(that);
        return typeParam;
    }

    public TypeParam visitTypeParam(GNode that) {
        enter(that);

        AstId id = visitId(that.getGeneric(0));

        TypeParam typeParam = new TypeParam(id, AstScope.emptyScope());

        scopeStack.peek().addElement(typeParam);

        GNode typeParamClauseNode = that.getGeneric(1);
        if (typeParamClauseNode != null) {
            List<TypeParam> params = visitTypeParamClause(typeParamClauseNode);
            typeParam.setParams(params);
        }

        GNode boundTypeNode = that.getGeneric(2);
        if (boundTypeNode != null) {
            typeParam.setBound(">:");
            typeParam.setBoundType(visitType(boundTypeNode));
        }

        boundTypeNode = that.getGeneric(3);
        if (boundTypeNode != null) {
            typeParam.setBound("<:");
            typeParam.setBoundType(visitType(boundTypeNode));
        }

        boundTypeNode = that.getGeneric(4);
        if (boundTypeNode != null) {
            typeParam.setBound("<%");
            typeParam.setBoundType(visitType(boundTypeNode));
        }

        exit(that);
        return typeParam;
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
        AstId id = visitId(that.getGeneric(1));
        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        Var param = new Var(id, scope, ElementKind.PARAMETER);

        GNode paramTypeNode = that.getGeneric(2);
        if (paramTypeNode != null) {
            Type type = visitParamType(paramTypeNode);
            param.setType(type);
            scope.addMirror(type);
        }

        scopeStack.peek().addElement(param);

        exit(that);
        return param;
    }

    public Type visitParamType(GNode that) {
        enter(that);

        Object first = that.get(0);
        GNode typeNode = null;
        ParamType.More more = ParamType.More.Raw;
        if (first instanceof GNode) {
            typeNode = (GNode) first;
            if (that.size() == 2) {
                more = ParamType.More.Star;
            }
        } else {
            typeNode = that.getGeneric(1);
            more = ParamType.More.ByName;
        }
        Type toWrap = visitType(typeNode);
        ParamType type = new ParamType(toWrap.getPickToken());
        type.setRawType(toWrap);
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

        ElementKind kind = ElementKind.LOCAL_VARIABLE;
        AstElement enclodingElement = currScope.getBindingElement();
        if (enclodingElement != null && enclodingElement instanceof Template) {
            kind = ElementKind.FIELD;
        }

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        List<AstId> ids = visitIds(that.getGeneric(0));
        Type type = visitType(that.getGeneric(1));

        for (AstId id : ids) {
            Var val = new Var(id, scope, kind);
            val.setVal();
            val.setType(type);
            scope.addMirror(type);

            currScope.addElement(val);
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

        ElementKind kind = ElementKind.LOCAL_VARIABLE;
        AstElement enclodingElement = currScope.getBindingElement();
        if (enclodingElement != null && enclodingElement instanceof Template) {
            kind = ElementKind.FIELD;
        }

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        List<AstId> ids = visitIds(that.getGeneric(0));
        Type type = visitType(that.getGeneric(1));

        for (AstId id : ids) {
            Var var = new Var(id, scope, kind);
            var.setType(type);
            scope.addMirror(type);

            currScope.addElement(var);
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

        ElementKind kind = ElementKind.LOCAL_VARIABLE;
        AstElement enclodingElement = currScope.getBindingElement();
        if (enclodingElement != null && enclodingElement instanceof Template) {
            kind = ElementKind.FIELD;
        }

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        Object[] patDef = visitPatDef(that.getGeneric(0));
        List<AstId> ids = (List<AstId>) patDef[0];
        AstExpression expr = (AstExpression) patDef[1];
        for (AstId id : ids) {
            Var var = new Var(id, scope, kind);
            var.setVal();
            var.setExpr(expr);

            currScope.addElement(var);
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

        ElementKind kind = ElementKind.LOCAL_VARIABLE;
        AstElement enclodingElement = currScope.getBindingElement();
        if (enclodingElement != null && enclodingElement instanceof Template) {
            kind = ElementKind.FIELD;
        }

        AstScope scope = new AstScope(getBoundsTokens(that));
        scopeStack.peek().addScope(scope);
        scopeStack.push(scope);

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Ids")) {
            List<AstId> ids = visitIds(what);
            Type type = visitType(that.getGeneric(1));
            for (AstId id : ids) {
                Var var = new Var(id, scope, kind);
                var.setType(type);
                scope.addMirror(type);

                currScope.addElement(var);
                vars.add(var);
            }
        } else {
            Object[] patDef = visitPatDef(what);
            List<AstId> ids = (List<AstId>) patDef[0];
            AstExpression expr = (AstExpression) patDef[1];
            scope = new AstScope(getBoundsTokens(that));
            for (AstId id : ids) {
                Var var = new Var(id, scope, kind);
                var.setExpr(expr);

                currScope.addElement(var);
                vars.add(var);
            }
        }

        scopeStack.pop();
        exit(that);
        return vars;
    }

    public Object[] visitPatDef(GNode that) {
        enter(that);

        List<AstId> ids = visitNoTypedPattern(that.getGeneric(0));
        for (Object o : that.getList(1)) {
            ids.addAll(visitNoTypedPattern((GNode) o));
        }

        GNode typeNode = that.getGeneric(2);
        Type type = typeNode == null ? null : visitType(typeNode);
        for (AstId id : ids) {
            if (type != null) {
                id.setType(type);
                scopeStack.peek().addMirror(type);
            }
        }

        AstExpression expr = visitExpr(that.getGeneric(3));

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

                List<AstId> ids = visitPattern(whatNode);
                for (AstId id : ids) {
                    Var var = new Var(id, varScope, ElementKind.LOCAL_VARIABLE);

                    scopeStack.peek().addElement(var);
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

                AstId id = visitVarId(whatNode);
                Var var = new Var(id, varScope, ElementKind.LOCAL_VARIABLE);

                scopeStack.peek().addElement(var);

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

    public List<AstId> visitPatterns(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        Object what = that.getGeneric(0);
        if (what instanceof GNode) {
            ids.addAll(visitPattern((GNode) what));
            for (Object o : that.getList(1)) {
                ids.addAll(visitPattern((GNode) o));
            }
        } else {
            // "_*"
            ids = Collections.<AstId>emptyList();
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitPattern(GNode that) {
        enter(that);

        List<AstId> ids = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("AlternatePattern")) {
            ids = visitAlternatePattern(what);
        } else {
            ids = visitPattern1(what);
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitAlternatePattern(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        /** @Todo emeger ids with same name (and type) */
        ids.addAll(visitPattern1(that.getGeneric(0)));
        ids.addAll(visitPattern1(that.getGeneric(1)));
        for (Object o : that.getList(2)) {
            ids.addAll(visitPattern1((GNode) o));
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitPattern1(GNode that) {
        enter(that);

        GNode what = that.getGeneric(0);
        List<AstId> ids = what.getName().equals("TypedPattern")
                ? visitTypedPattern(what)
                : visitNoTypedPattern(what);

        exit(that);
        return ids;
    }

    public List<AstId> visitTypedPattern(GNode that) {
        enter(that);

        List<AstId> ids = Collections.<AstId>emptyList();
        Object what = that.get(0);
        if (what instanceof GNode && ((GNode) what).getName().equals("VarId")) {
            AstId id = visitVarId((GNode) what);
            ids = new ArrayList<AstId>();
            ids.add(id);

            Type type = visitType(that.getGeneric(1));
            if (id != null) {
                id.setType(type);
                scopeStack.peek().addMirror(type);
            }
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitNoTypedPattern(GNode that) {
        enter(that);

        GNode what = that.getGeneric(0);
        List<AstId> ids = what.getName().equals("AtPattern")
                ? visitAtPattern(what)
                : visitPattern3(what);

        exit(that);
        return ids;
    }

    public List<AstId> visitAtPattern(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        ids.add(visitVarId(that.getGeneric(0)));
        ids.addAll(visitPattern3(that.getGeneric(1)));

        exit(that);
        return ids;
    }

    public List<AstId> visitPattern3(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

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

    public List<AstId> visitSimplePattern(GNode that) {
        enter(that);

        List<AstId> ids = null;
        if (that.getName().equals("SimpleTuplePattern")) {
            ids = visitSimpleTuplePattern(that);
        } else if (that.getName().equals("SimpleCallPattern")) {
            ids = visitSimpleCallPattern(that);
        } else if (that.getName().equals("SimpleIdPattern")) {
            ids = visitSimpleIdPattern(that);
        } else {
            ids = Collections.<AstId>emptyList();
        }

        exit(that);
        return ids;

    }

    public List<AstId> visitSimpleCallPattern(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();

        ids.add(visitStableId(that.getGeneric(0)));
        List<AstId> tupleIds = visitTuplePattern(that.getGeneric(1));
        ids.addAll(tupleIds);

        exit(that);
        return ids;
    }

    public List<AstId> visitSimpleTuplePattern(GNode that) {
        enter(that);

        List<AstId> ids = visitTuplePattern(that.getGeneric(0));

        exit(that);
        return ids;
    }

    public List<AstId> visitSimpleIdPattern(GNode that) {
        enter(that);

        List<AstId> ids = new ArrayList<AstId>();
        ids.add(visitStableId(that.getGeneric(0)));

        exit(that);
        return ids;
    }

    public List<AstId> visitTuplePattern(GNode that) {
        enter(that);

        List<AstId> ids = null;

        GNode patternsNode = that.getGeneric(0);
        if (patternsNode != null) {
            ids = visitPatterns(patternsNode);
        } else {
            ids = Collections.<AstId>emptyList();
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
        List<AstId> ids = visitEnumerators(enumeratorsNode);
        AstScope varScope = new AstScope(getBoundsTokens(enumeratorsNode));
        scopeStack.peek().addScope(varScope);
        for (AstId id : ids) {
            Var var = new Var(id, varScope, ElementKind.LOCAL_VARIABLE);

            scopeStack.peek().addElement(var);
        }

        // AstExpression
        AstExpression yieldExpr = visitExpr(that.getGeneric(1));

        scopeStack.pop();
        exit(that);
    }

    public List<AstId> visitEnumerators(GNode that) {
        enter(that);

        List<AstId> ids = visitGenerator(that.getGeneric(0));
        for (Object o : that.getList(1)) {
            ids.addAll(visitEnumerator((GNode) o));
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitEnumerator(GNode that) {
        enter(that);

        List<AstId> ids = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("Generator")) {
            ids = visitGenerator(what);
        } else if (what.getName().equals("Guard")) {
            ids = Collections.<AstId>emptyList();
        } else {
            // void:"val":key Pattern1 "=":key Expr
            ids = visitPattern1(what);
            // AstExpression
            AstExpression expr = visitExpr(that.getGeneric(1));
        }

        exit(that);
        return ids;
    }

    public List<AstId> visitGenerator(GNode that) {
        enter(that);

        // Pattern1
        List<AstId> ids = visitPattern1(that.getGeneric(0));
        // AstExpression
        AstExpression expr = visitExpr(that.getGeneric(1));

        GNode guardNode = that.getGeneric(2);
        if (guardNode != null) {
            visitChildren(guardNode);
        }

        exit(that);
        return ids;
    }

    public ArgumentExprs visitArgumentExprs(GNode that) {
        enter(that);

        List<AstExpression> args = null;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ParenExpr")) {
            args = visitParenExpr(what);
        } else {
            BlockExpr arg = visitBlockExpr(what);
            args = Collections.<AstExpression>singletonList(arg);
        }

        ArgumentExprs argExprs = new ArgumentExprs(getBoundsTokens(that));
        argExprs.setArgs(args);

        exit(that);
        return argExprs;
    }

    public List<AstExpression> visitParenExpr(GNode that) {
        enter(that);

        List<AstExpression> exprs;

        GNode exprsNode = that.getGeneric(0);
        if (exprsNode != null) {
            exprs = visitExprs(exprsNode);
        } else {
            exprs = Collections.<AstExpression>emptyList();
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

    public List<AstExpression> visitExprs(GNode that) {
        enter(that);

        List<AstExpression> exprs = new ArrayList<AstExpression>();
        GNode first = that.getGeneric(0);
        AstExpression firstExpr = visitExpr(first);
        exprs.add(firstExpr);

        for (Object o : that.getList(1)) {
            AstExpression expr = visitExpr((GNode) o);
            exprs.add(expr);
        }

        exit(that);
        return exprs;
    }

    public AstExpression visitExpr(GNode that) {
        enter(that);

        AstExpression expr = null;

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
                scopeStack.peek().addExpression(expr);
            }
        } else {
            /** @Todo */
            expr = new AstExpression(getBoundsTokens(that));
        }

        exit(that);
        return expr;
    }

    public AstExpression visitAssignmentExpr(GNode that) {
        enter(that);

        AssignmentExpr expr = new AssignmentExpr(getBoundsTokens(that));

        AstExpression lhs = visitSimpleExpr(that.getGeneric(0));
        AstExpression rhs = visitExpr(that.getGeneric(1));
        lhs.setType(rhs.asType());
        expr.setLhs(lhs);
        expr.setRhs(rhs);

        exit(that);
        return expr;
    }

    public AstExpression visitNotFunExpr(GNode that) {
        enter(that);

        /**
         * all other sub node has been specify a generic node's name via '@',
         * except pure PostfixExpr
         */
        AstExpression expr = visitPostfixExpr(that.getGeneric(0));
        /* Since NotFunExpr can be dispatched to here by visitChildren, bypassing
         * visitExpr, we should add to scope in visitPostfixExpr or visitInfixExpr
         */

        exit(that);
        return expr;
    }

    public AstExpression visitPostfixExpr(GNode that) {
        enter(that);

        AstExpression expr = visitInfixExpr(that.getGeneric(0));
        assert expr instanceof Postfixable;

        GNode postfixOpNode = that.getGeneric(1);
        if (postfixOpNode != null) {
            ((Postfixable) expr).setPostfixOp(visitId(postfixOpNode));
        }

        exit(that);
        return expr;
    }

    public AstExpression visitInfixExpr(GNode that) {
        enter(that);

        AstExpression expr = null;

        SimpleExpr first = visitPrefixExpr(that.getGeneric(0));

        // Should add expr to scope here
        scopeStack.peek().addExpression(first);

        Pair others = that.getList(1);
        if (!others.isEmpty()) {
            List<SimpleExpr> exprs = new ArrayList<SimpleExpr>();
            exprs.add(first);
            List<AstId> ops = new ArrayList<AstId>();

            List<InfixOpExprs> infixOpExprsList = new ArrayList<InfixOpExprs>();

            SimpleExpr lExpr = first;
            for (int i = 0; i < others.size(); i++) {
                GNode otherNode = (GNode) others.get(i);

                AstId op = visitId(otherNode.getGeneric(0));
                SimpleExpr rExpr = visitPrefixExpr(otherNode.getGeneric(1));

                // Should add expr to scope here
                scopeStack.peek().addExpression(rExpr);

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
                String opName = op.getSimpleName().toString();
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

            AstNode currLhs = infixOpExprsList.get(0).lhs;
            FunctionCall lastFunRef = null;
            for (InfixOpExprs currInfixOpExprs : infixOpExprsList) {
                AstId callId = currInfixOpExprs.op;
                lastFunRef = new FunctionCall(callId.getPickToken());
                lastFunRef.setBase(currLhs);
                lastFunRef.setCall(callId);
                lastFunRef.setArgs(Collections.<AstNode>singletonList(currInfixOpExprs.rhs));

                currLhs = lastFunRef;

                scopeStack.peek().addMirror(lastFunRef);
            }

            infixExpr.setTopFunRef(lastFunRef);

            expr = infixExpr;
            scopeStack.peek().addExpression(infixExpr);
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

        AstNode base = null;
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
            AstId id = visitId(baseNode);
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
            base = new AstExpression(getBoundsTokens(baseNode)) {

                @Override
                public Name getSimpleName() {
                    return new BasicName("todo");
                }
            };

        }

        expr.setBase(base);
        AstNode currBase = base;

        if (typeArgsNode != null) {
            List<Type> typeArgs = visitTypeArgs(typeArgsNode);
            expr.setTypeArgs(typeArgs);
        }

        if (baseNode.getName().equals("Path")) {
            GNode directArgExprsNode = that.getGeneric(2);
            ArgumentExprs argExprs = directArgExprsNode == null ? null : visitArgumentExprs(directArgExprsNode);

            PathId pathId = (PathId) base;
            List<AstId> paths = pathId.getPaths();
            AstId firstId = paths.get(0);

            if (argExprs != null) {
                // dog.sound.concat(arg0)
                // Function ref, we should fetch last id of Paths as call name of funRef
                AstId callId = paths.get(paths.size() - 1);
                paths.remove(callId);

                FunctionCall funRef = new FunctionCall(callId.getPickToken());

                if (paths.size() > 0) {
                    // Cannot resolve the ref here, should be done when global type inference
                    funRef.setBase(pathId);
                }

                funRef.setCall(callId);

                funRef.setArgs(argExprs.getArgs());

                expr.setBase(funRef);

                scopeStack.peek().addMirror(funRef);

                currBase = funRef;
            } else {
                // dog.sound
                // Similest case, only one PathId
                IdCall idRef = new IdCall(firstId.getSimpleName(), firstId.getPickToken());

                scopeStack.peek().addMirror(idRef);

                if (paths.size() > 1) {
                    // first's field
                    AstId fieldId = paths.get(paths.size() - 1);
                    paths.remove(fieldId);

                    FieldCall fieldRef = new FieldCall(fieldId.getPickToken());

                    // Cannot resolve the ref here, should be done when global type inference
                    fieldRef.setBase(pathId);
                    fieldRef.setField(fieldId);

                    expr.setBase(fieldRef);

                    scopeStack.peek().addMirror(fieldRef);

                    currBase = fieldRef;
                } else {
                    expr.setBase(idRef);

                    currBase = idRef;
                }
            }

        }

        List<AstMirror> memberChain = null;
        for (Object o : memberList) {
            if (memberChain == null) {
                memberChain = new ArrayList<AstMirror>();
            }
            AstMirror member = visitMember((GNode) o);
            if (member instanceof FunctionCall) {
                ((FunctionCall) member).setBase(currBase);
            } else if (member instanceof FieldCall) {
                ((FieldCall) member).setBase(currBase);
            }

            currBase = member;

            scopeStack.peek().addMirror(member);

            memberChain.add(member);
        }
        if (memberChain == null) {
            memberChain = Collections.<AstMirror>emptyList();
        }
        expr.setMemberChain(memberChain);

        if (errorNode != null) {
            visitError(errorNode);
        }

        if (typeNode != null) {
            Type type = visitType(typeNode);
            expr.setType(type);

            scopeStack.peek().addMirror(type);
        }

        exit(that);
        return expr;
    }

    public AstMirror visitMember(GNode that) {
        enter(that);

        AstMirror mirror;

        GNode what = that.getGeneric(0);
        if (what.getName().equals("WildKey")) {
            AstId id = visitId(what);
            mirror = new IdCall("_", id.getPickToken());
        } else if (what.getName().equals("ArgumentExprs")) {
            // apply call
            ArgumentExprs argExprs = visitArgumentExprs(what);
            ApplyFunctionCall apply = new ApplyFunctionCall();
            apply.setArgs(argExprs.getArgs());
            // base not set yet, should be set later by SimpleExpr
            mirror = apply;
        } else {
            // void:".":sep AstId TypeArgs? ArgumentExprs?
            AstId id = visitId(what);

            GNode typeArgsNode = that.getGeneric(1);
            if (typeArgsNode != null) {
                List<Type> typeArgs = visitTypeArgs(typeArgsNode);
            }

            GNode argsNode = that.getGeneric(2);
            if (argsNode != null) {
                ArgumentExprs argExprs = visitArgumentExprs(argsNode);

                FunctionCall funRef = new FunctionCall(id.getPickToken());
                funRef.setCall(id);
                funRef.setArgs(argExprs.getArgs());
                // base not set yet, should be set later by SimpleExpr
                mirror = funRef;
            } else {
                FieldCall fieldRef = new FieldCall(id.getPickToken());
                fieldRef.setField(id);
                // base not set yet, should be set later by SimpleExpr
                mirror = fieldRef;
            }
        }

        exit(that);
        return mirror;
    }

    public NewExpr visitNewExpr(GNode that) {
        enter(that);
        NewExpr expr = new NewExpr(getBoundsTokens(that));

        GNode what = that.getGeneric(0);
        if (what.getName().equals("ClassTemplate")) {
            List<Type> parents = visitClassTemplate(what);
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

    public Type visitType(GNode that) {
        enter(that);

        Type type = null;

        GNode node = that.getGeneric(0);
        if (node.getName().equals("CallByNameFunType")) {
            GNode lhsNode = node.getGeneric(0);
            Type lhs = null;
            if (lhsNode != null) {
                lhs = visitType(lhsNode);
            }

            GNode rhsNode = node.getGeneric(1);
            Type rhs = visitType(rhsNode);

            // use rhs as the idToken
            FunType funType = new FunType();
            funType.setLhs(lhs);
            funType.setRhs(rhs);

            type = funType;
        } else if (node.getName().equals("NormalFunType")) {
            GNode lhsNode = node.getGeneric(0);
            Type lhs = visitInfixType(lhsNode);
            GNode rhsNode = node.getGeneric(1);
            Type rhs = visitType(rhsNode);

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

    public Type visitInfixType(GNode that) {
        enter(that);

        Type type = null;

        Type first = visitCompoundType(that.getGeneric(0));

        Pair others = that.getList(1);
        if (!others.isEmpty()) {
            List<Type> types = new ArrayList<Type>();
            List<AstId> ops = new ArrayList<AstId>();
            types.add(first);

            for (Object rest : others) {
                GNode restNode = (GNode) rest;
                ops.add(visitId(restNode.getGeneric(0)));
                types.add(visitCompoundType(restNode.getGeneric(1)));
            }

            InfixType infixType = new InfixType(first.getPickToken());
            infixType.setTypes(types);
            infixType.setOps(ops);

            type = infixType;
        } else {
            type = first;
        }

        exit(that);
        return type;
    }

    public Type visitCompoundType(GNode that) {
        enter(that);

        Type type = null;

        List<Type> types = new ArrayList<Type>();

        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("Refinement")) {
            // Todo
            return new SimpleIdType(null) {

                @Override
                public Name getSimpleName() {
                    return new BasicName("{...}");
                }
            };
        }

        Type first = visitAnnotType(that.getGeneric(0));
        types.add(first);

        Pair others = that.getList(1);
        for (Object typeNode : others) {
            types.add(visitAnnotType((GNode) typeNode));
        }

        if (types.size() == 1) {
            type = first;
        } else {

            CompoundType compoundType = new CompoundType(first.getPickToken());
            compoundType.setTypes(types);

            type = compoundType;
        }

        exit(that);
        return type;
    }

    public Type visitAnnotType(GNode that) {
        enter(that);

        Pair annotations = that.getList(0);
        Type type = visitSimpleType(that.getGeneric(1));

        exit(that);
        return type;
    }

    public Type visitSimpleType(GNode that) {
        enter(that);

        Type type = null;

        if (that.getName().equals("SimpleIdType")) {
            type = visitSimpleIdType(that);
        } else if (that.getName().equals("SimpleSingletonType")) {
            type = visitSimpleSingletonType(that);
        } else if (that.getName().equals("SimpleTupleType")) {
            type = visitSimpleTupleType(that);
        }

        assert type != null : "There is other SimpleType? - " + that.getName();

        GNode typeArgsNode = that.getGeneric(1);
        if (typeArgsNode != null) {
            List<Type> typeArgs = visitTypeArgs((GNode) typeArgsNode);
            type.setTypeArgs(typeArgs);
        }

        exit(that);
        return type;
    }

    public List<Type> visitTypeArgs(GNode that) {
        enter(that);

        List<Type> typeArgs = visitTypes(that.getGeneric(0));

        exit(that);
        return typeArgs;
    }

    public List<Type> visitTypes(GNode that) {
        enter(that);

        List<Type> types = new ArrayList<Type>();

        Type type;
        GNode firstNode = that.getGeneric(0);
        if (firstNode.getName().equals("WildKey")) {
            AstId id = visitId(firstNode);
            type = new SimpleIdType(id.getPickToken());
            ((SimpleIdType) type).setPaths(Collections.<AstId>singletonList(id));
            scopeStack.peek().addMirror(type);
        } else {
            type = visitType(firstNode);
        }
        types.add(type);

        for (Object other : that.getList(1)) {
            GNode otherNode = (GNode) other;
            if (otherNode.getName().equals("WildKey")) {
                AstId id = visitId(otherNode);
                type = new SimpleIdType(id.getPickToken());
                ((SimpleIdType) type).setPaths(Collections.<AstId>singletonList(id));
                scopeStack.peek().addMirror(type);
            } else {
                type = visitType(otherNode);
            }
            types.add(type);
        }

        exit(that);
        return types;
    }

    public Type visitSimpleIdType(GNode that) {
        enter(that);

        PathId id = visitStableId(that.getGeneric(0));
        AstId first = id.getPaths().get(0);
        SimpleIdType type = new SimpleIdType(first.getPickToken());
        type.setPaths(id.getPaths());

        scopeStack.peek().addMirror(type);

        exit(that);
        return type;
    }

    public Type visitSimpleSingletonType(GNode that) {
        enter(that);

        PathId id = visitPath(that.getGeneric(0));
        AstId first = id.getPaths().get(0);
        SimpleIdType type = new SimpleIdType(first.getPickToken());
        type.setPaths(id.getPaths());

        scopeStack.peek().addMirror(type);

        exit(that);
        return type;
    }

    public Type visitSimpleTupleType(GNode that) {
        enter(that);

        List<Type> types = visitTypes(that.getGeneric(0));
        // idToken is used to get the offset here, just set first type's idToken as idToken, 
        SimpleTupleType type = new SimpleTupleType(types.get(0).getPickToken());
        type.setTypes(types);

        exit(that);
        return type;
    }
    // ----- Helper inner classes
    private static class InfixOpExprs {

        AstId op;
        int precedence;
        AstNode lhs;
        AstNode rhs;
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
