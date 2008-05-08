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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementKind;
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
import org.netbeans.modules.scala.editing.nodes.Template;
import org.netbeans.modules.scala.editing.nodes.TypeRef;
import org.netbeans.modules.scala.editing.nodes.Var;

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

        for (AstDef def : scope.getDefs()) {
            if (def instanceof Var) {
                if (def.getKind() != ElementKind.PARAMETER) {
                    Template enclosingTmpl = def.getEnclosingDef(Template.class);
                    if (enclosingTmpl != null && enclosingTmpl.getBindingScope() == def.getEnclosingScope()) {
                        def.setKind(ElementKind.FIELD);
                    } else {
                        def.setKind(ElementKind.VARIABLE);
                    }
                }
            }
        }

        for (AstRef ref : scope.getRefs()) {
            AstDef def = rootScope.findDef(ref);
            if (def == null) {
                continue;
            }

            if (def instanceof Var) {
                ref.setKind(def.getKind());
            }

            if (ref.getType() == null) {
                ref.setType(def.getType());
            }
        }

        for (AstScope _scope : scope.getScopes()) {
            inferRecursively(_scope);
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
            AstElement firstIdRef = rootScope.findDefRef(th, firstId.getPickOffset(th));
            if (firstIdRef == null) {
                // this should not happen
                System.out.println("Null IdRef of PathId: " + base.toString());
                return;
            }
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
    // ------ Global infer
    public void globalInfer(CompilationInfo info) {
        ScalaIndex index = ScalaIndex.get(info);
        globalInferRecursively(index, rootScope);
    }

    private void globalInferRecursively(ScalaIndex index, AstScope scope) {
        for (AstRef ref : scope.getRefs()) {
            TypeRef toResolve = null;
            if (ref instanceof FunRef) {
                globalInferFunRef(index, (FunRef) ref);
                continue;
            } else if (ref instanceof TypeRef) {
                toResolve = (TypeRef) ref;
            } else {
                toResolve = ref.getType();
            }

            if (toResolve == null) {
                continue;
            }

            if (toResolve.isResolved()) {
                continue;
            }

            List<Import> imports = toResolve.getEnclosingScope().getDefsInScope(Import.class);
            List<String> importPkgs = new ArrayList<String>();
            for (Import importExpr : imports) {
                if (importExpr.isWild()) {
                    importPkgs.add(importExpr.getPackageName());
                }
            }
            Packaging packaging = toResolve.getPackageElement();
            String ofPackage = packaging == null ? null : packaging.getName();
            String qualifiedName = globalInferTypeRef(index, toResolve.getName(), ofPackage, importPkgs);
            if (qualifiedName != null) {
                toResolve.setQualifiedName(qualifiedName);
            }
        }

        for (AstScope _scope : scope.getScopes()) {
            globalInferRecursively(index, _scope);
        }
    }

    private void globalInferFunRef(ScalaIndex index, FunRef funRef) {
        TypeRef retType = funRef.getType();
        if (retType != null && (retType.isResolved() || funRef.getRetType() != null)) {
            return;
        }

        // resolve return type of funRef:
        AstElement base = funRef.getBase();
        if (base != null) {

            String baseTypeStr = null;
            TypeRef baseType = base.getType();
            if (baseType == null || (baseType != null && !baseType.isResolved())) {
                if (base instanceof FunRef) {
                    baseTypeStr = ((FunRef) base).getRetType();
                } else {
                    // @todo resolve it first
                }
            } else {
                baseTypeStr = baseType.getQualifiedName();
            }
            if (baseTypeStr == null) {
                // @todo resolve it first
                return;
            }

            Id call = funRef.getCall();
            String callName = call == null ? "apply" : call.getName();

            Set<IndexedElement> members = index.getElements(callName, baseTypeStr, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null);
            for (IndexedElement member : members) {
                if (member instanceof IndexedFunction) {
                    IndexedFunction idxFunction = (IndexedFunction) member;
                    if (idxFunction.getParameters().size() == funRef.getParams().size()) {
                        String idxRetTypeStr = idxFunction.getTypeString();
                        if (idxRetTypeStr == null) {
                            idxRetTypeStr = "void";
                        }
                        if (idxRetTypeStr.equals("void")) {
                            funRef.setRetType("void");
                            break;
                        }

                        int lastDot = idxRetTypeStr.lastIndexOf('.');
                        if (lastDot == -1) {
                            /** try to find pkg of idxRetTypeStr */
                            String hisIn = idxFunction.getIn();
                            if (hisIn != null) {
                                int pkgNameEnd = hisIn.lastIndexOf('.');
                                if (pkgNameEnd != -1) {
                                    String hisPkgName = hisIn.substring(0, pkgNameEnd);
                                    Set<String> importPkgs = getImportPkgs(index, hisIn);
                                    idxRetTypeStr = globalInferTypeRef(index, idxRetTypeStr, hisPkgName, importPkgs);
                                } else {
                                    System.out.println("found idx function without package: " + idxFunction.getName());
                                }
                            } else {
                                // @todo
                            }
                        }

                        funRef.setRetType(idxRetTypeStr);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 
     * @return null or full qualifier type name 
     */
    private String globalInferTypeRef(ScalaIndex index, String simpleName, String ofPackage, Collection<String> importPkgs) {
        // 1. search imported types first
        for (String pkgName : importPkgs) {
            pkgName = pkgName + ".";
            if (pkgName.startsWith("_root_.")) {
                pkgName = pkgName.substring(7, pkgName.length());
            }

            for (IndexedElement element : getImportedTypes(index, pkgName)) {
                if (element instanceof IndexedType) {
                    if (element.getName().equals(simpleName)) {
                        return pkgName + simpleName;
                    }
                }
            }
        }

        // 2. search packages with the same preceding of current packaging
        if (ofPackage != null) {
            for (String pkgName : importPkgs) {
                pkgName = pkgName + ".";
                if (pkgName.startsWith("_root_.")) {
                    continue;
                }

                /* package name with the same preceding of current packaging can omit packaging name */
                pkgName = ofPackage + "." + pkgName;
                for (IndexedElement element : getImportedTypes(index, pkgName)) {
                    if (element instanceof IndexedType) {
                        if (element.getName().equals(simpleName)) {
                            return pkgName + simpleName;
                        }
                    }
                }

            }
        }

        // 3. search "scala" packages 
        for (String pkgName : importPkgs) {
            pkgName = pkgName + ".";
            if (pkgName.startsWith("_root_.")) {
                continue;
            }

            /* package name starts with "scala" can omit "scala" */
            pkgName = "scala." + pkgName;
            for (IndexedElement element : getScalaPrecedingPackageTypes(index, pkgName)) {
                if (element instanceof IndexedType) {
                    if (element.getName().equals(simpleName)) {
                        return pkgName + simpleName;
                    }
                }
            }

        }

        // 4. then search types under the same package
        if (ofPackage != null) {
            String pkgName = ofPackage + ".";
            for (IndexedElement element : getPackageTypes(index, pkgName)) {
                if (element instanceof IndexedType) {
                    if (element.getName().equals(simpleName)) {
                        return pkgName + simpleName;
                    }
                }
            }
        }

        // 6. search auto-imported "scala." package
        for (IndexedElement element : getScalaPackageTypes(index)) {
            if (element instanceof IndexedType) {
                if (element.getName().equals(simpleName)) {
                    return "scala." + simpleName;
                }
            }
        }

        // 5. search auto-imported "java.lang." package
        for (IndexedElement element : getJavaLangPackageTypes(index)) {
            if (element instanceof IndexedType) {
                if (element.getName().equals(simpleName)) {
                    return "java.lang." + simpleName;
                }
            }
        }

        return null;
    }
    /** Map<InClassQName + "." + TypeRefSName, TypeRefQName> */
    private static Map<String, String> globalTypeRefsCache;
    private static Map<String, Set<String>> classToImportPkgsCache;
    /* types of "java.lang." will be automatically imported */
    private static Set<IndexedElement> javaLangPackageTypes;
    /* types of "scala." will be automatically imported */
    private static Set<IndexedElement> scalaPackageTypes;
    /* package name starts with "scala" can omit "scala" */
    private static Map<String, Set<IndexedElement>> scalaPrecedingPackageTypes;
    private Map<String, Set<IndexedElement>> importedTypesCache;
    private Map<String, Set<IndexedElement>> packageTypesCache;

    /**
     * @Note: need to be updated when class is modified   
     */
    private static Set<String> getImportPkgs(ScalaIndex index, String classQName) {
        if (classToImportPkgsCache == null) {
            classToImportPkgsCache = new HashMap<String, Set<String>>();
        }

        Set<String> importPkgs = classToImportPkgsCache.get(classQName);
        if (importPkgs == null) {
            importPkgs = index.getImports(classQName, ScalaIndex.ALL_SCOPE);

            classToImportPkgsCache.put(classQName, importPkgs);
        }

        return importPkgs;
    }
    
    public static void updateClassToImportPkgsCache(String classQName, Set<String> importPkgs) {
        if (classToImportPkgsCache != null && classToImportPkgsCache.containsKey(classQName)) {
            classToImportPkgsCache.put(classQName, importPkgs);
        }
    }

    private static Set<IndexedElement> getJavaLangPackageTypes(ScalaIndex index) {
        if (javaLangPackageTypes == null) {
            javaLangPackageTypes = index.getPackageContent("java.lang.", NameKind.PREFIX, ScalaIndex.ALL_SCOPE);
        }

        return javaLangPackageTypes;
    }

    private static Set<IndexedElement> getScalaPackageTypes(ScalaIndex index) {
        if (scalaPackageTypes == null) {
            scalaPackageTypes = index.getPackageContent("scala.", NameKind.PREFIX, ScalaIndex.ALL_SCOPE);
        }

        return scalaPackageTypes;
    }

    private static Set<IndexedElement> getScalaPrecedingPackageTypes(ScalaIndex index, String pkgName) {
        if (scalaPrecedingPackageTypes == null) {
            scalaPrecedingPackageTypes = new HashMap<String, Set<IndexedElement>>();
        }

        Set<IndexedElement> idxElements = scalaPrecedingPackageTypes.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            scalaPrecedingPackageTypes.put(pkgName, idxElements);
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

    private Set<IndexedElement> getPackageTypes(ScalaIndex index, String pkgName) {
        if (packageTypesCache == null) {
            packageTypesCache = new HashMap<String, Set<IndexedElement>>();
        }

        Set<IndexedElement> idxElements = packageTypesCache.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            packageTypesCache.put(pkgName, idxElements);
        }

        return idxElements;
    }
}
