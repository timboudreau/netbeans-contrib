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
package org.netbeans.modules.scala.editing.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.modules.scala.editing.semantic.Template.Kind;

/**
 *
 * @author dcaoyuan
 */
public class DefinitionVisitor extends ASTVisitor {

    private boolean forIndexing;
    private Map<ASTItem, String> astItemToType = new HashMap<ASTItem, String>();
    private ScalaContext rootCtx = null;
    private Stack<ScalaContext> currCtx = new Stack<ScalaContext>();
    /** states: */
    private Packaging packaging = null;
    private Function funDfn = null;
    private Template tmplDfn = null;
    private Type typeDfn = null;
    private Var varDfn = null;
    private boolean containsValDfn;
    private boolean containsVarDfn;
    private ASTItem Expr = null;
    private ASTItem postfixExpr = null;

    public DefinitionVisitor(ScalaContext rootContext) {
        this.rootCtx = rootContext;
        currCtx.push(rootContext);
    }

    @Override
    void visitNote( List<ASTItem> path, String xpath, int ordinal, boolean enter) {
        ASTItem leaf = path.get(path.size() - 1);
        if (xpath.endsWith("Packaging")) {
            if (enter) {
                packaging = new Packaging("Packaging", leaf.getOffset(), leaf.getEndOffset());
                currCtx.peek().addDefinition(packaging);
            }
        } else if (xpath.endsWith("Packaging.QualId.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                packaging.addPath(idTok.getIdentifier());
            }
        } else if (xpath.endsWith("Import.ImportStat")) {
            // todo
        } else if (xpath.endsWith("TopStats") ||
                xpath.endsWith("TemplateStats") ||
                xpath.endsWith("BlockStats") ||
                xpath.equals("CaseBlockStats")) {
            if (enter) {
                ScalaContext newCtx = new ScalaContext(ScalaContext.BLOCK, leaf.getOffset(), leaf.getEndOffset());
                currCtx.peek().addContext(newCtx);
                currCtx.push(newCtx);
            } else {
                currCtx.pop();
            }
        } else if (xpath.endsWith("FunDclDef")) {
            if (enter) {
                ScalaContext newCtx = new ScalaContext(ScalaContext.FUNCTION, leaf.getOffset(), leaf.getEndOffset());
                currCtx.peek().addContext(newCtx);
                currCtx.push(newCtx);
            } else {
                currCtx.pop();
            }
        } else if (xpath.endsWith("TypeDclDef")) {
            if (enter) {
                ScalaContext newCtx = new ScalaContext(ScalaContext.TYPE, leaf.getOffset(), leaf.getEndOffset());
                currCtx.peek().addContext(newCtx);
                currCtx.push(newCtx);
            } else {
                currCtx.pop();
            }
        } else if (xpath.endsWith("ObjectDef") ||
                xpath.endsWith("ClassDef") ||
                xpath.endsWith("TraitDef")) {
            if (enter) {
                ScalaContext newCtx = new ScalaContext(ScalaContext.TEMPLATE, leaf.getOffset(), leaf.getEndOffset());
                currCtx.peek().addContext(newCtx);
                currCtx.push(newCtx);
            } else {
                currCtx.pop();
            }
        } else if (xpath.endsWith("FunDclDef.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                funDfn = new Function(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), 0);
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(funDfn);
                currCtx.peek().getParent().addDefinition(funDfn);
                currCtx.peek().getParent().addUsage(idTok, funDfn);
            } else {
                funDfn = null;
            }
        } else if (xpath.endsWith("FunDclDef.this")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                funDfn = new Function(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), 0);
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(funDfn);
                currCtx.peek().getParent().addDefinition(funDfn);
                currCtx.peek().getParent().addUsage(idTok, funDfn);
            } else {
                funDfn = null;
            }
        } else if (xpath.endsWith("FunDclDef.FunTypeParamClause.TypeParam.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                typeDfn = new Type(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset());
                currCtx.peek().addDefinition(typeDfn);
                currCtx.peek().addUsage(idTok, typeDfn);
            } else {
                typeDfn = null;
            }
        } else if (xpath.endsWith("FunDclDef.ParamClauses.ParamClause.Params.Param.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                if (!idTok.getIdentifier().equals("_")) {
                    varDfn = new Var(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Var.Scope.LOCAL);
                    varDfn.setVal(true);
                    currCtx.peek().addDefinition(varDfn);
                    currCtx.peek().addUsage(idTok, varDfn);
                }
            } else {
                varDfn = null;
            }
        } else if (xpath.endsWith("ObjectDef.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                tmplDfn = new Template(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Kind.OBJECT, packaging);
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(tmplDfn);
                currCtx.peek().getParent().addDefinition(tmplDfn);
                currCtx.peek().getParent().addUsage(idTok, tmplDfn);
            } else {
                tmplDfn = null;
            }
        } else if (xpath.endsWith("ClassDef.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                tmplDfn = new Template(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Kind.CLASS, packaging);
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(tmplDfn);
                currCtx.peek().getParent().addDefinition(tmplDfn);
                currCtx.peek().getParent().addUsage(idTok, tmplDfn);
            } else {
                tmplDfn = null;
            }
        } else if (xpath.endsWith("TraitDef.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                tmplDfn = new Template(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Kind.TRAIT, packaging);
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(tmplDfn);
                currCtx.peek().getParent().addDefinition(tmplDfn);
                currCtx.peek().getParent().addUsage(idTok, tmplDfn);
            } else {
                tmplDfn = null;
            }
        } else if (xpath.endsWith("ClassParamClauses.ClassParams.ClassParam.ScalaId")) {
            // under ObjectDef or ClassDef 
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                /** @todo: add a constructor function for this class or object */
                varDfn = new Var(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Var.Scope.PARAMETER);
                currCtx.peek().addDefinition(varDfn);
                currCtx.peek().addUsage(idTok, varDfn);
            } else {
                varDfn = null;
            }
        } else if (xpath.endsWith("TypeDclDef.ScalaId")) {
            if (enter) {
                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                typeDfn = new Type(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset());
                // currCtx.peek() is the context of this definition itself, we should add this definition to parent
                currCtx.peek().setEnclosingDefinition(typeDfn);
                currCtx.peek().getParent().addDefinition(typeDfn);
                currCtx.peek().getParent().addUsage(idTok, typeDfn);
            } else {
                typeDfn = null;
            }
        } else if (xpath.endsWith("ValDclDef.PatDef") ||
                xpath.endsWith("Generator.Pattern1") ||
                xpath.endsWith("ValDefInEnumerator.Pattern1") ||
                xpath.endsWith("CasePattern0")) {
            if (enter) {
                containsValDfn = true;
            } else {
                containsValDfn = false;
            }
        } else if (xpath.endsWith("VarDclDef.PatDef")) {
            if (enter) {
                containsVarDfn = true;
            } else {
                containsVarDfn = false;
            }
        } else if (xpath.endsWith("ScalaId")) {
            if (enter) {
                if (containsValDfn || containsVarDfn) {
                    ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
                    if (!idTok.getIdentifier().equals("_")) {
                        varDfn = new Var(idTok.getIdentifier(), idTok.getOffset(), idTok.getEndOffset(), Var.Scope.LOCAL);
                        if (containsValDfn) {
                            varDfn.setVal(true);
                        } else if (containsVarDfn) {
                            varDfn.setVal(false);
                        }
                        currCtx.peek().addDefinition(varDfn);
                        currCtx.peek().addUsage(idTok, varDfn);
                    }
                }
            } else {
                varDfn = null;
            }
        } /** process anonymous function: */
        else if (xpath.endsWith("Expr") ||
                xpath.endsWith("ExprInParen") ||
                xpath.endsWith("ExprInTemplate") ||
                xpath.endsWith("ExprInBlock") ||
                xpath.endsWith("ExprInCaseBlock")) {
            if (enter) {
                Expr = leaf;
            } else {
                Expr = null;
                postfixExpr = null;
            }
        } else if (xpath.endsWith("PostfixExpr")) {
            if (enter && Expr != null) {
                // remember it for later using
                postfixExpr = leaf;
            }
        } else if (xpath.endsWith("FunExprTail") ||
                xpath.endsWith("FunExprTailInTemplate") ||
                xpath.endsWith("FunExprTailInBlock") ||
                xpath.endsWith("FunExprTailInCaseBlock")) {
            if (enter) {
                if (postfixExpr != null) {
                    // This is an anonymous function expr, the postfixExpt should be only in form of:
                    // (Bindings | id)
                    ScalaContext newCtx = new ScalaContext(ScalaContext.FUNCTION, Expr.getOffset(), Expr.getEndOffset());
                    currCtx.peek().addContext(newCtx);
                    currCtx.push(newCtx);
                    containsValDfn = true;
                }
            } else {
                if (postfixExpr != null) {
                    currCtx.pop();
                    containsValDfn = false;
                }
            }
        }
    }

    /**
     * will also check if item is null
     */
    public static final boolean isNode(ASTItem item, String nt) {
        return item != null && item instanceof ASTNode && ((ASTNode) item).getNT().equals(nt);
    }

    /**
     * will also check if item is null
     */
    public static final boolean isToken(ASTItem item, String id) {
        return item != null && item instanceof ASTToken && ((ASTToken) item).getIdentifier().equals(id);
    }

    /**
     * will also check if item is null
     */
    public static final boolean isTokenType(ASTItem item, String type) {
        if (item != null && item instanceof ASTToken) {
            String typeName = ((ASTToken) item).getTypeName();
            if (typeName != null) {
                return typeName.equals(type);
            } else {
                return false;
            }
        }
        return false;
    }
}
