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

import java.util.ArrayList;
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
public class UsageVisitor extends ASTVisitor {

    private boolean forIndexing;
    private Map<ASTItem, String> astItemToType = new HashMap<ASTItem, String>();
    private ScalaContext rootCtx = null;
    private boolean containsTypeUsage;
    private Stack<List<ASTItem>> chainStack = new Stack<List<ASTItem>>();
    private Stack<ASTItem> varOrFunctionStack = new Stack<ASTItem>();

    /** states: */
    public UsageVisitor(ScalaContext rootContext) {
        this.rootCtx = rootContext;
    }

    @Override
    boolean visitNote( List<ASTItem> path, String xpath, int ordinal, boolean enter) {
        boolean bypassChildren = false;
        ASTItem leaf = path.get(path.size() - 1);
        if (xpath.endsWith("TypeStableId")) {
            if (enter) {
                chainStack.push(new ArrayList<ASTItem>());
                containsTypeUsage = true;
            } else {
                ScalaContext currCtx = (ScalaContext) rootCtx.getClosestContext(leaf.getOffset());
                // @todo should process package here
                List<ASTItem> pathIds = chainStack.pop();
                if (pathIds != null && pathIds.size() > 0) {
                    ASTItem latestIdNode = pathIds.get(pathIds.size() - 1);
                    ASTToken latestIdTok = (ASTToken) latestIdNode.getChildren().get(0);
                    String idStr = latestIdTok.getIdentifier();
                    Type typeDfn = currCtx.getDefinitionInScopeByName(Type.class, idStr);
                    if (typeDfn != null) {
                        currCtx.addUsage(latestIdTok, typeDfn);
                    } else {
                        Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, idStr);
                        if (tmplDfn != null) {
                            currCtx.addUsage(latestIdTok, tmplDfn);
                        }
                    }
                }
                containsTypeUsage = false;
            }
        } else if (xpath.endsWith("TypeStableId.TypeId.PathId.ScalaId")) {
            if (enter && containsTypeUsage) {
                List<ASTItem> pathIds = chainStack.peek();
                pathIds.add(leaf);
            }
        } else if (xpath.endsWith("SimpleExpr")) {
            if (enter) {
                chainStack.push(new ArrayList<ASTItem>());
            } else {
                List<ASTItem> chain = chainStack.pop();
                if (!varOrFunctionStack.empty()) {
                    // it's a var or function call expr
                    ASTItem idNode = varOrFunctionStack.pop();
                    ASTToken idTok = (ASTToken) idNode.getChildren().get(0);
                    String idStr = idTok.getIdentifier();
                    ScalaContext currCtx = (ScalaContext) rootCtx.getClosestContext(idTok.getOffset());

                    boolean isVar = false;
                    if (chain.size() > 0) {
                        ASTNode first = (ASTNode) chain.get(0);
                        if (first.getNT().equals("ScalaId")) {
                            isVar = true;
                        } else if (first.getNT().equals("Arguments")) {
                            // it's function call
                            // @todo infer args type
                            int arity = 0;
                            for (ASTItem child : first.getChildren()) {
                                if (isNode(child, "Expr")) {
                                    arity++;
                                }
                            }
                            Function funDfn = currCtx.getDefinitionInScopeByName(Function.class, idStr);
                            if (funDfn == null) {
                                /** Is it a imported function call? */
                                funDfn = ErlBuiltIn.getBuiltInFunction(idStr, arity);
                                if (funDfn != null) {
                                    currCtx.addDefinition(funDfn);
                                    currCtx.addUsage(idTok, funDfn);
                                } else {
                                    // It may be a apply/update function of var?
                                    isVar = true;
                                }
                            } else {
                                currCtx.addUsage(idTok, funDfn);
                            }

                        }
                    } else {
                        // plain simple var
                        isVar = true;
                    }

                    if (isVar) {
                        // @todo should process package here
                        Var varDfn = currCtx.getVariableInScope(idStr);
                        if (varDfn != null) {
                            currCtx.addUsage(idTok, varDfn);
                        } else {
                            Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, idStr);
                            if (tmplDfn != null && (tmplDfn.getKind() == Kind.OBJECT || tmplDfn.getKind() == Kind.CLASS)) {
                                currCtx.addUsage(idTok, tmplDfn);
                            }
                        }
                    }
                }
            }
        } else if (xpath.endsWith("SimpleExpr.SimplePathIdExpr.PathId.ScalaId")) {
            // this is a ScalaId starting expr
            if (enter) {
                varOrFunctionStack.push(leaf);
            }
        } else if (xpath.endsWith("SimpleExprRest.PathRest.PathId.ScalaId")) {
            if (enter) {
                List<ASTItem> chain = chainStack.peek();
                if (chain != null) {
                    chain.add(leaf);
                }
            }
        } else if (xpath.endsWith("SimpleExprRest.Arguments")) {
            List<ASTItem> chain = chainStack.peek();
            if (chain != null) {
                chain.add(leaf);
            }
        } else if (xpath.endsWith("NewExpr.ClassParents.AnnotType.SimpleType.TypeStableId")) {
            if (enter) {
                chainStack.push(new ArrayList<ASTItem>());
            } else {
                ScalaContext currCtx = (ScalaContext) rootCtx.getClosestContext(leaf.getOffset());
                List<ASTItem> pathIds = chainStack.pop();
                for (ASTItem idNode : pathIds) {
                    ASTToken idTok = (ASTToken) idNode.getChildren().get(0);
                    String idStr = idTok.getIdentifier();
                    if (idStr.equals("this") || idStr.equals("super")) {
                        // @todo
                    } else {
                        Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, idStr);
                        if (tmplDfn != null && (tmplDfn.getKind() == Kind.CLASS || tmplDfn.getKind() == Kind.TRAIT)) {
                            currCtx.addUsage(idTok, tmplDfn);
                        }
                    }
                }
                pathIds.clear();
            }
        } else if (xpath.endsWith("NewExpr.ClassParents.AnnotType.SimpleType.TypeStableId.TypeId.PathId.ScalaId")) {
            if (enter) {
                List<ASTItem> pathIds = chainStack.peek();
                if (pathIds != null) {
                    pathIds.add(leaf);
                }
            }
        }

        return bypassChildren;
    }

    private void processAnyExpr(ScalaContext rootCtx, ASTItem expr, ScalaContext currCtx, boolean containsVarDef) {
        if (isNode(expr, "Literal")) {
            for (ASTItem item1 : expr.getChildren()) {
                if (isTokenType(item1, "integer")) {
                    astItemToType.put(expr, "Integer");
                    astItemToType.put(item1, "Integer");
                } else if (isTokenType(item1, "float")) {
                    astItemToType.put(expr, "Float");
                    astItemToType.put(item1, "Float");
                } else if (isTokenType(item1, "char")) {
                    astItemToType.put(expr, "Char");
                    astItemToType.put(item1, "Char");
                } else if (isTokenType(item1, "string")) {
                    astItemToType.put(expr, "String");
                    astItemToType.put(item1, "String");
                } else if (isToken(item1, "true") || isToken(item1, "false")) {
                    astItemToType.put(expr, "Boolean");
                    astItemToType.put(item1, "Boolean");
                } else if (isToken(item1, "null")) {
                    astItemToType.put(expr, "Null");
                    astItemToType.put(item1, "Null");
                } else {
                }
            }
//        } else if (isNode(expr, "Type")) {
//            processAnyType(rootCtx, expr, currCtx);
        }
    }

}
