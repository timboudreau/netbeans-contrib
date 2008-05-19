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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.nodes.exprs.AssignmentExpr;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstExpr;
import org.netbeans.modules.scala.editing.nodes.AstRef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.FieldRef;
import org.netbeans.modules.scala.editing.nodes.FunRef;
import org.netbeans.modules.scala.editing.nodes.Id;
import org.netbeans.modules.scala.editing.nodes.IdRef;
import org.netbeans.modules.scala.editing.nodes.Importing;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.modules.scala.editing.nodes.PathId;
import org.netbeans.modules.scala.editing.nodes.exprs.SimpleExpr;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef;
import org.netbeans.modules.scala.editing.nodes.types.TypeRef.PseudoTypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaTypeInferencer {

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
    // ----- private used vars:
    private AstScope rootScope;
    private TokenHierarchy th;
    private Map<AstRef, AstScope> newResolvedRefs = new HashMap<AstRef, AstScope>();

    public ScalaTypeInferencer(AstScope rootScope, TokenHierarchy th) {
        this.rootScope = rootScope;
        this.th = th;
    }

    public void infer() {
        // anything can do? should all inder global?
    }
    // ------ Global infer
    public void globalInfer(CompilationInfo info) {
        ScalaIndex index = ScalaIndex.get(info);

        newResolvedRefs.clear();
        globalInferRecursively(index, rootScope);
        for (Entry<AstRef, AstScope> entry : newResolvedRefs.entrySet()) {
            entry.getValue().addRef(entry.getKey());
        }

        /** 
         * Since we do not compute type inference dependencies yet, we are not sure
         * the proper inference order. To resolve dependencies, rhe simplest way 
         * here is doing it twice:
         */
        newResolvedRefs.clear();
        globalInferRecursively(index, rootScope);
        for (Entry<AstRef, AstScope> entry : newResolvedRefs.entrySet()) {
            entry.getValue().addRef(entry.getKey());
        }

    }

    private void globalInferRecursively(ScalaIndex index, AstScope scope) {
        for (AstExpr expr : scope.getExprs()) {
            globalInferExpr(expr, null);
        }
        
        for (AstRef ref : scope.getRefs()) {
            TypeRef toResolve = null;
            if (ref instanceof FunRef) {
                globalInferFunRef(index, (FunRef) ref);
                continue;
            } else if (ref instanceof FieldRef) {
                globalInferFieldRef(index, (FieldRef) ref);
                continue;
            } else if (ref instanceof TypeRef) {
                toResolve = (TypeRef) ref;
            } else {
                toResolve = ref.getType();
            }

            if (toResolve == null || toResolve != null && toResolve.isResolved()) {
                continue;
            }

            globalInferTypeRef(index, toResolve);
        }

        for (AstScope subScope : scope.getScopes()) {
            globalInferRecursively(index, subScope);
        }
    }

    private void globalInferFunRef(ScalaIndex index, FunRef funRef) {
        TypeRef retType = funRef.getType();
        if (retType != null && retType.isResolved()) {
            return;
        }

        String baseTypeTmpl = null;
        String baseTypeStr = null;
        String callName = null;

        // resolve return type of funRef:
        AstElement base = funRef.getBase();

        if (base != null) {
            TypeRef baseType = null;

            if (base instanceof PathId) {
                // shoudl convert it to FieldRef first
                List<Id> paths = ((PathId) base).getPaths();
                assert paths.isEmpty() == false;
                // Is this a qualifiered name or member chain?
                // let's try member chain first
                Iterator<Id> itr = paths.iterator();
                Id firstId = itr.next();
                IdRef idRef = new IdRef(firstId.getName(), firstId.getIdToken(), ElementKind.VARIABLE);
                idRef.setEnclosingScope(funRef.getEnclosingScope());
                newResolvedRefs.put(idRef, funRef.getEnclosingScope());
                AstRef currBase = idRef;
                while (itr.hasNext()) {
                    Id field = itr.next();
                    FieldRef aFieldRef = new FieldRef(field.getIdToken());
                    aFieldRef.setBase(currBase);
                    aFieldRef.setField(field);
                    globalInferFieldRef(index, aFieldRef);
                    TypeRef aFieldRefType = aFieldRef.getType();
                    if (aFieldRefType != null && aFieldRefType.isResolved()) {
                        newResolvedRefs.put(aFieldRef, funRef.getEnclosingScope());

                        currBase = aFieldRef;
                    } else {
                        // @Todo cannot be resolved, should be qualifiered name?
                        break;
                    }
                }

                funRef.setBase(currBase);

                base = currBase;
            }

            baseType = base.getType();
            if (baseType != null) {
                if (!baseType.isResolved()) {
                    globalInferTypeRef(index, baseType);
                }

                if (baseType.isResolved()) {
                    baseTypeStr = baseType.getQualifiedName();
                } else {
                    // @todo resolve it first
                }
            }

            if (baseTypeStr == null) {
                return;
            }

            Id call = funRef.getCall();
            callName = call == null ? "apply" : call.getName();

        } else {
            // it's a local call or Object's apply
            TypeRef type = funRef.getType();
            if (type != null && type.isResolved()) {
                // a local call, should has been resolved
                return;
            } else {
                Id objectName = funRef.getCall();

                List<Importing> importings = funRef.getEnclosingScope().getDefsInScope(Importing.class);
                List<String> importPkgs = new ArrayList<String>();
                for (Importing importing : importings) {
                    if (importing.isWild()) {
                        importPkgs.add(importing.getPackageName());
                    }
                }
                Packaging packaging = funRef.getPackageElement();
                String ofPackage = packaging == null ? null : packaging.getName();

                String qualifiedName = globalInferTypeRef(index, objectName.getName(), ofPackage, importPkgs);
                if (qualifiedName != null) {
                    baseTypeStr = qualifiedName;
                    funRef.setBase(new PseudoTypeRef(qualifiedName));
                    funRef.setCall(new Id("apply", objectName.getIdToken(), ElementKind.VARIABLE));

                    funRef.setApply();
                    callName = "apply";
                    baseTypeTmpl = "object";
                }

            }
        }

        if (baseTypeStr == null || callName == null) {
            return;
        }

        Set<IndexedElement> members = index.getElements(callName, baseTypeStr, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null, false);
        for (IndexedElement member : members) {
            if (member instanceof IndexedFunction) {
                IndexedFunction idxFunction = (IndexedFunction) member;

                if (idxFunction.isReferredBy(funRef)) {
                    String idxRetTypeStr = idxFunction.getTypeString();
                    if (idxRetTypeStr == null) {
                        idxRetTypeStr = "void";
                    }
                    if (idxRetTypeStr.equals("void")) {
                        funRef.setRetTypeStr("void");
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

                    funRef.setRetTypeStr(idxRetTypeStr);
                    break;
                }
            }
        }
    }

    private void globalInferFieldRef(ScalaIndex index, FieldRef fieldRef) {
        TypeRef retType = fieldRef.getType();
        if (retType != null && retType.isResolved()) {
            return;
        }

        // resolve return type of fieldRef:
        AstElement base = fieldRef.getBase();
        if (base != null) {

            String baseTypeStr = null;
            TypeRef baseType = base.getType();

            if (base instanceof PathId) {
                List<Id> paths = ((PathId) base).getPaths();
                assert paths.isEmpty() == false;
                // Is this a qualifiered name or member chain?
                // let's try member chain first
                Iterator<Id> itr = paths.iterator();
                Id firstId = itr.next();
                IdRef idRef = new IdRef(firstId.getName(), firstId.getIdToken(), ElementKind.VARIABLE);
                idRef.setEnclosingScope(fieldRef.getEnclosingScope());
                newResolvedRefs.put(idRef, fieldRef.getEnclosingScope());
                AstRef currBase = idRef;
                while (itr.hasNext()) {
                    Id field = itr.next();
                    FieldRef aFieldRef = new FieldRef(field.getIdToken());
                    aFieldRef.setBase(currBase);
                    aFieldRef.setField(field);
                    globalInferFieldRef(index, aFieldRef);
                    TypeRef aFieldRefType = aFieldRef.getType();
                    if (aFieldRefType != null && aFieldRefType.isResolved()) {
                        newResolvedRefs.put(aFieldRef, fieldRef.getEnclosingScope());

                        currBase = aFieldRef;
                    } else {
                        // @Todo cannot be resolved, should be qualifiered name?
                        break;
                    }
                }

                fieldRef.setBase(currBase);

                base = currBase;
            }

            baseType = base.getType();
            if (baseType != null) {
                if (!baseType.isResolved()) {
                    globalInferTypeRef(index, baseType);
                }

                if (baseType.isResolved()) {
                    baseTypeStr = baseType.getQualifiedName();
                } else {
                    // @todo resolve it first
                }
            }

            if (baseTypeStr == null) {
                return;
            }

            Id field = fieldRef.getField();
            String fieldName = field.getName();

            Set<IndexedElement> members = index.getElements(fieldName, baseTypeStr, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null, false);
            for (IndexedElement member : members) {
                boolean isCandicate = false;
                String idxRetTypeStr = null;

                if (member instanceof IndexedFunction) {
                    IndexedFunction idxFunction = (IndexedFunction) member;
                    if (idxFunction.isNullArgs()) {
                        isCandicate = true;
                        idxRetTypeStr = idxFunction.getTypeString();
                    }
                } else if (member instanceof IndexedField) {
                    IndexedField idxField = (IndexedField) member;
                    isCandicate = true;
                    idxRetTypeStr = idxField.getTypeString();
                }

                if (isCandicate) {
                    if (idxRetTypeStr == null) {
                        idxRetTypeStr = "void";
                    }
                    if (idxRetTypeStr.equals("void")) {
                        fieldRef.setRetTypeStr("void");
                        break;
                    }

                    int lastDot = idxRetTypeStr.lastIndexOf('.');
                    if (lastDot == -1) {
                        /** try to find pkg of idxRetTypeStr */
                        String hisIn = member.getIn();
                        if (hisIn != null) {
                            int pkgNameEnd = hisIn.lastIndexOf('.');
                            if (pkgNameEnd != -1) {
                                String hisPkgName = hisIn.substring(0, pkgNameEnd);
                                Set<String> importPkgs = getImportPkgs(index, hisIn);
                                idxRetTypeStr = globalInferTypeRef(index, idxRetTypeStr, hisPkgName, importPkgs);
                            } else {
                                System.out.println("found idx element without package: " + member.getName());
                            }
                        } else {
                            // @todo
                        }
                    }

                    fieldRef.setRetTypeStr(idxRetTypeStr);
                    break;
                }
            }
        }
    }

    private String globalInferTypeRef(ScalaIndex index, TypeRef type) {
        List<Importing> importings = type.getEnclosingScope().getDefsInScope(Importing.class);
        List<String> importPkgs = new ArrayList<String>();
        for (Importing importing : importings) {
            if (importing.isWild()) {
                importPkgs.add(importing.getPackageName());
            }
        }
        Packaging packaging = type.getPackageElement();
        String ofPackage = packaging == null ? null : packaging.getName();
        String qualifiedName = globalInferTypeRef(index, type.getName(), ofPackage, importPkgs);
        if (qualifiedName != null) {
            type.setQualifiedName(qualifiedName);
        }
        return qualifiedName;
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

        // 5. search auto-imported "scala." package
        for (IndexedElement element : getScalaPackageTypes(index)) {
            if (element instanceof IndexedType) {
                if (element.getName().equals(simpleName)) {
                    return "scala." + simpleName;
                }
            }
        }

        // 6. search auto-imported "java.lang." package
        for (IndexedElement element : getJavaLangPackageTypes(index)) {
            if (element instanceof IndexedType) {
                if (element.getName().equals(simpleName)) {
                    return "java.lang." + simpleName;
                }
            }
        }

        return null;
    }

    private void globalInferExpr(AstExpr expr, TypeRef knownExprType) {
        if (knownExprType != null) {
            expr.setType(knownExprType);
        }

        if (expr instanceof SimpleExpr) {
            globalInferSimpleExpr((SimpleExpr) expr);
        } else if (expr instanceof AssignmentExpr) {
            globalInferAssignmentExpr((AssignmentExpr) expr);
        }
    }

    private void globalInferSimpleExpr(SimpleExpr expr) {
        TypeRef exprType = expr.getType();
        if (exprType == null) {
            return;
        }

        AstElement base = expr.getBase();
        TypeRef baseType = base.getType();
        if (baseType != null && baseType.isResolved()) {
            return;
        }

        if (base instanceof IdRef) {
            // resolve its def's type.
            AstDef def = rootScope.findDef(base);
            if (def != null) {
                TypeRef type = def.getType();
                if (type != null) {
                    // @Todo check type of def with expr's type 
                } else {
                    def.setType(exprType);
                }
            }
        }
    }

    private void globalInferAssignmentExpr(AssignmentExpr expr) {
        AstExpr lhs = expr.getLhs();
        AstExpr rhs = expr.getRhs();
        globalInferExpr(rhs, null);
        globalInferExpr(lhs, rhs.getType());
    }

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
