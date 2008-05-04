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
package org.netbeans.modules.scala.editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.nodes.AssignmentExpr;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstExpr;
import org.netbeans.modules.scala.editing.nodes.AstRef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.FunRef;
import org.netbeans.modules.scala.editing.nodes.Id;
import org.netbeans.modules.scala.editing.nodes.Import;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.modules.scala.editing.nodes.PathId;
import org.netbeans.modules.scala.editing.nodes.SimpleExpr;
import org.netbeans.modules.scala.editing.nodes.TypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaTypeInferencer {

    private AstScope rootScope;
    private TokenHierarchy th;

    public ScalaTypeInferencer(AstScope rootScope, TokenHierarchy th) {
        this.rootScope = rootScope;
        this.th = th;
    }

    public void infer() {
        inferRecursively(rootScope);
    }

    private void inferRecursively(AstScope scope) {
        for (AstExpr expr : scope.getExprs()) {
            inferExpr(expr, null);
        }

        for (AstRef ref : scope.getRefs()) {
            if (ref.getType() == null) {
                AstDef def = rootScope.findDef(ref);
                if (def != null) {
                    ref.setType(def.getType());
                }
            }
        }

        for (AstScope _Scope : scope.getScopes()) {
            inferRecursively(_Scope);
        }
    }

    private void inferExpr(AstExpr expr, TypeRef knownExprType) {
        if (expr instanceof SimpleExpr) {
            inferSimpleExpr((SimpleExpr) expr, knownExprType);
        } else if (expr instanceof AssignmentExpr) {
            inferAssignmentExpr((AssignmentExpr) expr);
        }

    }

    private void inferSimpleExpr(SimpleExpr expr, TypeRef knownExprType) {
        AstElement base = ((SimpleExpr) expr).getBase();
        if (base instanceof PathId) {
            /** Try to find an AstRef, so we can infer its type via it's def */
            Id firstId = ((PathId) base).getPaths().get(0);
            AstElement firstIdRef = rootScope.getDefRef(th, firstId.getIdToken().offset(th));
            AstDef def = rootScope.findDef(firstIdRef);
            TypeRef type = null;
            if (def != null) {
                type = def.getType();
            } else if (knownExprType != null) {
                type = knownExprType;
            }

            if (type != null) {
                if (firstIdRef.getType() != null) {
                    // @Todo check type of firstId with def's type 
                } else {
                    firstId.setType(type);
                    firstIdRef.setType(type);
                }
            }
        }
    }

    private void inferAssignmentExpr(AssignmentExpr expr) {
        AstExpr lhs = ((AssignmentExpr) expr).getLhs();
        AstExpr rhs = ((AssignmentExpr) expr).getRhs();
        inferExpr(rhs, null);
        inferExpr(lhs, rhs.getType());
    }

    public void globalInfer(CompilationInfo info) {
        ScalaIndex index = ScalaIndex.get(info);
        globalInferRecursively(index, rootScope);
    }

    private void globalInferRecursively(ScalaIndex index, AstScope scope) {
        for (AstRef ref : scope.getRefs()) {
            TypeRef toResolve = null;
            if (ref instanceof FunRef) {
                /*
                FunRef funRef = (FunRef) ref;
                toResolve = funRef.getType();
                if (toResolve != null && !toResolve.getQualifiedName().equals(TypeRef.UNRESOLVED)) {
                toResolve = null;
                continue;
                }
                if (funRef.getBase() != null) {
                TypeRef baseType = funRef.getBase().getType();
                if (baseType == null) {
                // @todo resolve it first
                continue;
                }
                Id call = funRef.getCall();
                Set<IndexedElement> members = index.getElements(call.getName(), baseType.getQualifiedName(), NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null);
                for (IndexedElement member : members) {
                if (member instanceof IndexedFunction) {
                IndexedFunction idxFunction = (IndexedFunction) member;
                if (idxFunction.getParameters().size() == funRef.getParams().size()) {
                String pkgName = idxFunction.getIn() == null ? "" : idxFunction.getIn() + ".";
                funRef.setRetType(pkgName + idxFunction.getTypeString());
                }
                }
                }
                }
                continue;
                 */
            } else if (ref instanceof TypeRef) {
                toResolve = (TypeRef) ref;
            } else {
                toResolve = ref.getType();
            }

            if (toResolve != null && toResolve.isResolved()) {
                toResolve = null;
            }

            if (toResolve == null) {
                continue;
            }

            String simpleName = toResolve.getName();
            boolean resolved = false;

            // 1. search imported types first
            List<Import> imports = toResolve.getEnclosingScope().getDefsInScope(Import.class);
            for (Import importExpr : imports) {
                if (!importExpr.isWild()) {
                    continue;
                }

                String pkgName = importExpr.getPackageName() + ".";
                for (IndexedElement element : getImportedTypes(index, pkgName)) {
                    if (element instanceof IndexedType) {
                        if (element.getName().equals(simpleName)) {
                            toResolve.setQualifiedName(pkgName + simpleName);
                            resolved = true;
                            break;
                        }
                    }
                }

                if (resolved) {
                    break;
                }
            }

            if (resolved) {
                continue;
            }

            // 2. search "scala" packages 
            for (Import importExpr : imports) {
                if (!importExpr.isWild()) {
                    continue;
                }

                /* package name starts with "scala" can omit "scala" */
                String pkgName = "scala." + importExpr.getPackageName() + ".";
                for (IndexedElement element : getScalaPackageTypes(index, pkgName)) {
                    if (element instanceof IndexedType) {
                        if (element.getName().equals(simpleName)) {
                            toResolve.setQualifiedName(pkgName + simpleName);
                            resolved = true;
                            break;
                        }
                    }
                }

                if (resolved) {
                    break;
                }
            }

            if (resolved) {
                continue;
            }

            // 3. then search types under the same package
            Packaging packaging = toResolve.getPackageElement();
            if (packaging != null) {
                String pkgName = packaging.getName() + ".";
                for (IndexedElement element : getPackageTypes(index, packaging)) {
                    if (element instanceof IndexedType) {
                        if (element.getName().equals(simpleName)) {
                            toResolve.setQualifiedName(pkgName + simpleName);
                            break;
                        }
                    }
                }
            }

            if (resolved) {
                continue;
            }
        }

        for (AstScope _Scope : scope.getScopes()) {
            globalInferRecursively(index, _Scope);
        }
    }
    /* package name starts with "scala" can omit "scala" */
    private static Map<String, Set<IndexedElement>> scalaPackageTypes;
    private Map<String, Set<IndexedElement>> importedTypesCache;
    private Map<Packaging, Set<IndexedElement>> packageTypesCache;

    private static Set<IndexedElement> getScalaPackageTypes(ScalaIndex index, String pkgName) {
        if (scalaPackageTypes == null) {
            scalaPackageTypes = new HashMap<String, Set<IndexedElement>>();
        }

        Set<IndexedElement> idxElements = scalaPackageTypes.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            scalaPackageTypes.put(pkgName, idxElements);
        }

        return idxElements;
    }

    private Set<IndexedElement> getImportedTypes(ScalaIndex index, String pkgName) {
        if (importedTypesCache == null) {
            importedTypesCache = new HashMap<String, Set<IndexedElement>>();
        }

        Set<IndexedElement> idxElements = importedTypesCache.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            importedTypesCache.put(pkgName, idxElements);
        }

        return idxElements;
    }

    private Set<IndexedElement> getPackageTypes(ScalaIndex index, Packaging packaging) {
        if (packageTypesCache == null) {
            packageTypesCache = new HashMap<Packaging, Set<IndexedElement>>();
        }

        Set<IndexedElement> idxElements = packageTypesCache.get(packaging);
        if (idxElements == null) {
            String pkgName = packaging.getName() + ".";
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            packageTypesCache.put(packaging, idxElements);
        }

        return idxElements;
    }
}
