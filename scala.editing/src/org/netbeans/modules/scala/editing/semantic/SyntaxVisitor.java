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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;
import org.netbeans.modules.scala.editing.semantic.Template.Kind;

/**
 *
 * @author dcaoyuan
 */
public class SyntaxVisitor extends ASTVisitor {

    private boolean forIndexing;
    private Map<ASTItem, String> astItemToType = new HashMap<ASTItem, String>();
    private ScalaContext rootCtx = null;
    private boolean containsTypeUsage;
    private boolean containsVarUsage;
    private List<ASTToken> pathIds = new ArrayList<ASTToken>();

    private int nlCount;
    
    /** states: */
    public SyntaxVisitor(ScalaContext rootContext) {
        this.rootCtx = rootContext;
    }

    @Override
    void visitNote( List<ASTItem> path, String xpath, int ordinal, boolean enter) {
        ASTItem leaf = path.get(path.size() - 1);
        if (xpath.endsWith("PostfixExpr")) {
            if (enter) {
            } else {
                nlCount = 0;
            }
        } else if (xpath.endsWith("PostfixExpr.SimpleExpr")) {
            if (enter) {
//                ASTToken idTok = (ASTToken) leaf.getChildren().get(0);
//                pathIds.add(idTok);
            }
        } else if (xpath.endsWith("PostfixExpr.SimpleExpr.nl")) {
            if (enter) {
                nlCount++;
            }        
        }
    }


    private void processSimpleExpr(ScalaContext rootCtx, ASTItem simpleExpr, ScalaContext currCtx) {
        boolean isFunCall = false;
        boolean isLocalCall = false;
        boolean isVar = false;
        List<ASTItem> pathIdsWithTypeArgs = null;
        List<ASTItem> argsChain = null;
        List<ASTItem> children = simpleExpr.getChildren();
        List<ASTItem> pendingItems = new ArrayList<ASTItem>();
        for (ASTItem item : children) {
            if (isNode(item, "PathIdWithTypeArgs")) {
                if (pathIdsWithTypeArgs == null) {
                    pathIdsWithTypeArgs = new ArrayList<ASTItem>();
                }
                pathIdsWithTypeArgs.add(item);
            } else if (isNode(item, "Arguments")) {
                isFunCall = true;
                if (argsChain == null) {
                    argsChain = new ArrayList<ASTItem>();
                }
                argsChain.add(item);
                pendingItems.add(item);
            } else {
                pendingItems.add(item);
            }
        }

        isLocalCall = isFunCall && pathIdsWithTypeArgs != null && pathIdsWithTypeArgs.size() > 0 && isNode(children.get(0), "PathIdWithTypeArgs");
        isVar = !isLocalCall && pathIdsWithTypeArgs != null && pathIdsWithTypeArgs.size() > 0 && isNode(children.get(0), "PathIdWithTypeArgs");
        if (isLocalCall) {
            int arityInt = 0;
            if (argsChain != null && argsChain.size() > 0) {
                // @todo infer arg type
                for (ASTItem child : argsChain.get(0).getChildren()) {
                    if (isNode(child, "Expr")) {
                        arityInt++;
                    }
                }
            }

            ASTItem PathIdWithTypeArgs = pathIdsWithTypeArgs.get(0);
            ASTItem nameId = null;
            for (ASTItem item : PathIdWithTypeArgs.getChildren()) {
                if (isNode(item, "PathId")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "NameId")) {
                            nameId = item1;
                            break;
                        }
                    }
                    break;
                }
            }
            if (nameId == null) {
                return;
            } // @todo process this super ?

            ASTToken funName = (ASTToken) nameId.getChildren().get(0);
            if (funName != null) {
                /** @todo get all functions with the same name, then find the same Type params one */
                Function funDfn = currCtx.getDefinitionInScopeByName(Function.class, funName.getIdentifier());
                if (funDfn == null) {
                    /** Is it a imported function call? */
                    funDfn = ErlBuiltIn.getBuiltInFunction(funName.getIdentifier(), arityInt);
                    if (funDfn != null) {
                        currCtx.addDefinition(funDfn);
                        currCtx.addUsage(funName, funDfn);
                    } else {
                        // It may be a apply/update function of var?
                        isVar = true;
                    }
                } else {
                    currCtx.addUsage(funName, funDfn);
                }
            }

        }

        if (isVar) {
            ASTItem pathIdWithTypeArgs = pathIdsWithTypeArgs.get(0);
            ASTItem nameId = null;
            for (ASTItem item : pathIdWithTypeArgs.getChildren()) {
                if (isNode(item, "PathId")) {
                    for (ASTItem item1 : item.getChildren()) {
                        if (isNode(item1, "NameId")) {
                            nameId = item1;
                            break;
                        }
                    }
                    break;
                }
            }
            if (nameId == null) {
                return;
            } // @todo process this super ?

            ASTToken varId = (ASTToken) nameId.getChildren().get(0);
            if (varId != null && !(varId.getIdentifier().equals("_"))) {
                Var varDfn = currCtx.getVariableInScope(varId.getIdentifier());
                if (varDfn != null) {
                    currCtx.addUsage(varId, varDfn);
                } else {
                    Template tmplDfn = currCtx.getDefinitionInScopeByName(Template.class, varId.getIdentifier());
                    if (tmplDfn != null && (tmplDfn.getKind() == Kind.OBJECT || tmplDfn.getKind() == Kind.CLASS)) {
                        currCtx.addUsage(varId, tmplDfn);
                    }
                }
            }
        } else {
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

    //private static final String xpathRegrex = "((\\.)?(([a-z]|[A-Z])([a-z]|[A-Z]|[0-9])*(\\[([0-9]+)\\])?))+";
    //private static final Pattern xpathPattern = Pattern.compile(xpathRegrex);
    public static List<ASTItem> query(ASTItem fromItem, String relativePath) {
        List<String> pathNames = new ArrayList<String>();
        List<Integer> pathPositions = new ArrayList<Integer>();
        String[] elements = relativePath.split("/");
        for (String element : elements) {
            int pos1 = element.indexOf('[');
            int pos2 = element.indexOf(']');
            int pos = (pos1 > 0 && pos2 > 0) ? Integer.parseInt(element.substring(pos1 + 1, pos2)) : -1;
            pathNames.add(element);
            pathPositions.add(pos);
        }
        List<ASTItem> fromItems = new ArrayList<ASTItem>();
        fromItems.add(fromItem);
        return query(fromItems, 0, pathNames, pathPositions);
    }

    private static List<ASTItem> query(List<ASTItem> fromItems, int fromDepth, List<String> pathNames, List<Integer> pathPositions) {
        if (pathNames.size() == 0) {
            return Collections.<ASTItem>emptyList();
        }
        List<ASTItem> result = new ArrayList<ASTItem>();
        String wantedName = pathNames.get(fromDepth);
        int wantedPos = pathPositions.get(fromDepth);
        for (ASTItem fromItem : fromItems) {
            int pos = 0;
            for (ASTItem child : fromItem.getChildren()) {
                String name = child instanceof ASTToken ? ((ASTToken) child).getIdentifier() : ((ASTNode) child).getNT();
                if (name.equals(wantedName)) {
                    if (pos == wantedPos || wantedPos == -1) {
                        result.add(child);
                    } else {
                        pos++;
                    }
                }
            }
        }
        fromDepth++;
        if (fromDepth == pathNames.size()) { // reach leaf now            

            return result;
        } else {
            return query(result, fromDepth, pathNames, pathPositions);
        }
    }
}
