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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.NameKind;
import org.netbeans.modules.scala.editing.nodes.AstNode;
import org.netbeans.modules.scala.editing.nodes.exprs.AssignmentExpr;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstExpression;
import org.netbeans.modules.scala.editing.nodes.AstMirror;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.FieldCall;
import org.netbeans.modules.scala.editing.nodes.FunctionCall;
import org.netbeans.modules.scala.editing.nodes.AstId;
import org.netbeans.modules.scala.editing.nodes.IdCall;
import org.netbeans.modules.scala.editing.nodes.Importing;
import org.netbeans.modules.scala.editing.nodes.Packaging;
import org.netbeans.modules.scala.editing.nodes.PathId;
import org.netbeans.modules.scala.editing.nodes.exprs.SimpleExpr;
import org.netbeans.modules.scala.editing.nodes.types.Type;
import org.netbeans.modules.scala.editing.nodes.BasicType;
import org.netbeans.modules.scala.editing.nodes.types.PredefinedTypes;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaTypeInferencer {

    /** Map<InClassQName + "." + TypeRefSName, TypeRefQName> */
    private static Map<String, String> globalTypeRefsCache;
    private static Map<String, Set<String>> classToImportPkgsCache;
    /* types of "java.lang." will be automatically imported */
    private static Set<GsfElement> javaLangPackageTypes;
    /* types of "scala." will be automatically imported */
    private static Set<GsfElement> scalaPackageTypes;
    /* package name starts with "scala" can omit "scala" */
    private static Map<String, Set<GsfElement>> scalaPrecedingPackageTypes;
    /** @Todo should the following not be static ? */
    private static Map<String, Set<GsfElement>> importedTypesCache;
    private static Map<String, Set<GsfElement>> packageTypesCache;
    // ----- private used vars:
    private AstScope rootScope;
    private TokenHierarchy th;
    private Map<AstMirror, AstScope> newResolvedMirrors = new HashMap<AstMirror, AstScope>();

    public ScalaTypeInferencer(AstScope rootScope, TokenHierarchy th) {
        this.rootScope = rootScope;
        this.th = th;
    }

    public void infer() {
        // anything can do? should all inder global?
    }

    public void globalInfer(ScalaIndex index) {
        long start = System.currentTimeMillis();

        newResolvedMirrors.clear();
        globalInferRecursively(index, rootScope);
        for (Entry<AstMirror, AstScope> entry : newResolvedMirrors.entrySet()) {
            entry.getValue().addMirror(entry.getKey());
        }

        /** 
         * Since we do not compute type inference dependencies yet, we are not sure
         * the proper inference order. To resolve dependencies, the simplest way 
         * here is doing it twice:
         */
        newResolvedMirrors.clear();
        globalInferRecursively(index, rootScope);
        for (Entry<AstMirror, AstScope> entry : newResolvedMirrors.entrySet()) {
            entry.getValue().addMirror(entry.getKey());
        }

        long time = System.currentTimeMillis() - start;
        System.out.println("Infer time: " + time / 1000.0f + "s");
    }

    private void globalInferRecursively(ScalaIndex index, AstScope scope) {
        for (AstExpression expr : scope.getExpressions()) {
            globalInferExpr(expr, null);
        }

        for (AstMirror mirror : scope.getMirrors()) {
            TypeMirror toResolve = null;
            if (mirror instanceof FunctionCall) {
                globalInferFunctionCall(index, (FunctionCall) mirror);
                continue;
            } else if (mirror instanceof FieldCall) {
                globalInferFieldCall(index, (FieldCall) mirror);
                continue;
            } else if (mirror instanceof TypeMirror) {
                toResolve = (TypeMirror) mirror;
            } else {
                toResolve = mirror.asType();
            }

            if (toResolve == null || toResolve != null && Type.isResolved(toResolve)) {
                continue;
            }

            globalInferType(index, toResolve);
        }

        for (AstScope subScope : scope.getScopes()) {
            globalInferRecursively(index, subScope);
        }
    }

    private void globalInferFunctionCall(ScalaIndex index, FunctionCall functionCall) {
        TypeMirror retType = functionCall.asType();
        if (retType != null && Type.isResolved(retType)) {
            return;
        }

        String baseTypeQName = null;
        String callName = null;

        // resolve return type of funRef:
        AstNode base = functionCall.getBase();
        if (base != null) {
            TypeMirror baseType = functionCall.getBaseType();
            if (baseType == null) {
                if (base instanceof PathId) {
                    // shoudl convert it to FieldCall first
                    List<AstId> paths = ((PathId) base).getPaths();
                    assert !paths.isEmpty();
                    // Is this a qualifiered name or member chain?
                    // let's try member chain first
                    Iterator<AstId> itr = paths.iterator();
                    AstId firstId = itr.next();
                    IdCall idCall = new IdCall(firstId.getSimpleName(), firstId.getPickToken());
                    idCall.setEnclosingScope(functionCall.getEnclosingScope());
                    newResolvedMirrors.put(idCall, functionCall.getEnclosingScope());
                    AstMirror currBase = idCall;
                    while (itr.hasNext()) {
                        AstId field = itr.next();
                        FieldCall aFieldCall = new FieldCall(field.getPickToken());
                        aFieldCall.setBase(currBase);
                        aFieldCall.setField(field);
                        globalInferFieldCall(index, aFieldCall);
                        TypeMirror aFieldRefType = aFieldCall.asType();
                        if (aFieldRefType != null && Type.isResolved(aFieldRefType)) {
                            newResolvedMirrors.put(aFieldCall, functionCall.getEnclosingScope());

                            currBase = aFieldCall;
                        } else {
                            // @Todo cannot be resolved, should be qualifiered name?
                            break;
                        }
                    }

                    functionCall.setBase(currBase);

                    base = currBase;
                }

                baseType = base.asType();
            }

            if (baseType != null) {
                if (!Type.isResolved(baseType)) {
                    if (baseType instanceof BasicType && ((BasicType) baseType).getSimpleName() == null) {
                        /** @todo */
                        System.out.println("A BasicType has no simpleName");
                    } else {
                        globalInferType(index, baseType);
                    }
                }

                if (Type.isResolved(baseType)) {
                    baseTypeQName = Type.qualifiedNameOf(baseType);
                } else {
                    // @todo resolve it first
                }
            }

            if (baseTypeQName == null) {
                return;
            }

            AstId call = functionCall.getCall();
            callName = call == null ? "apply" : call.getSimpleName().toString();

        } else {
            // it's a local call or Object's apply
            TypeMirror type = functionCall.asType();
            if (type != null && Type.isResolved(type)) {
                // a local call, should has been resolved
                return;
            } else {
                AstId objectName = functionCall.getCall();

                List<Importing> importings = functionCall.getEnclosingScope().getVisibleElements(Importing.class);
                List<String> importPkgs = new ArrayList<String>();
                for (Importing importing : importings) {
                    if (importing.isWild()) {
                        importPkgs.add(importing.getPackageName());
                    }
                }
                Packaging packaging = functionCall.getPackageElement();
                String ofPackage = packaging == null ? null : packaging.getQualifiedName().toString();

                TypeElement typeElement = globalInferType(index, objectName.getSimpleName().toString(), ofPackage, importPkgs);
                if (typeElement != null) {
                    TypeMirror baseType = new BasicType(typeElement);
                    baseTypeQName = Type.qualifiedNameOf(baseType);

                    functionCall.setBaseType(baseType);
                    functionCall.setCall(new AstId("apply", objectName.getPickToken()));

                    functionCall.setApply();
                    callName = "apply";
                    //String baseTypeTmpl = "object";
                }

            }
        }

        if (baseTypeQName == null || callName == null) {
            return;
        }

        Set<GsfElement> gsfElements = index.getMembers(callName, baseTypeQName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null, false);
        for (GsfElement gsfElement : gsfElements) {
            if (!gsfElement.getElement().getSimpleName().toString().equals(callName)) {
                continue;
            }

            if (gsfElement.getElement() instanceof ExecutableElement) {
                ExecutableElement mFunction = (ExecutableElement) gsfElement.getElement();

                if (AstElement.isMirroredBy(mFunction, functionCall)) {
                    TypeMirror mRetType = mFunction.getReturnType();
                    if (mRetType != null && Type.isResolved(mRetType)) {
                        TypeElement mRetTe = Type.asElement(mRetType);
                        functionCall.setType(new BasicType(mRetTe));
                        break;
                    }

                    String mRetTypeSName = mRetType == null ? null : Type.simpleNameOf(mRetType);
                    if (mRetTypeSName == null) {
                        mRetTypeSName = "void";
                    }

                    if (mRetTypeSName.equals("void")) {
                        functionCall.setType(PredefinedTypes.NullType);
                        break;
                    }

                    /** try to find pkg of mRetType from gsfElement */
                    TypeElement mRetTe = null;
                    String itsIn = gsfElement.getIn();
                    if (itsIn != null) {
                        int pkgNameEnd = itsIn.lastIndexOf('.');
                        if (pkgNameEnd != -1) {
                            String itsPkgName = itsIn.substring(0, pkgNameEnd);
                            Set<String> importPkgs = getImportPkgs(index, itsIn);
                            mRetTe = globalInferType(index, mRetTypeSName, itsPkgName, importPkgs);

                        } else {
                            System.out.println("found idx function without package: " + mFunction.getSimpleName().toString());
                        }
                    } else {
                        // @todo
                    }

                    if (mRetTe != null) {
                        functionCall.setType(new BasicType(mRetTe));
                        break;
                    }
                }
            }
        }
    }

    private void globalInferFieldCall(ScalaIndex index, FieldCall fieldCall) {
        TypeMirror retType = fieldCall.asType();
        if (retType != null && Type.isResolved(retType)) {
            return;
        }

        // resolve return type of fieldRef:
        String baseTypeQName = null;
        AstNode base = fieldCall.getBase();
        if (base != null) {
            TypeMirror baseType = fieldCall.getBaseType();
            if (baseType == null) {
                if (base instanceof PathId) {
                    List<AstId> paths = ((PathId) base).getPaths();
                    assert !paths.isEmpty();
                    // Is this a qualifiered name or member chain?
                    // let's try member chain first
                    Iterator<AstId> itr = paths.iterator();
                    AstId firstId = itr.next();
                    IdCall idCall = new IdCall(firstId.getSimpleName(), firstId.getPickToken());
                    idCall.setEnclosingScope(fieldCall.getEnclosingScope());
                    newResolvedMirrors.put(idCall, fieldCall.getEnclosingScope());
                    AstMirror currBase = idCall;
                    while (itr.hasNext()) {
                        AstId field = itr.next();
                        FieldCall aFieldCall = new FieldCall(field.getPickToken());
                        aFieldCall.setBase(currBase);
                        aFieldCall.setField(field);
                        globalInferFieldCall(index, aFieldCall);
                        TypeMirror aFieldRefType = aFieldCall.asType();
                        if (aFieldRefType != null && Type.isResolved(aFieldRefType)) {
                            newResolvedMirrors.put(aFieldCall, fieldCall.getEnclosingScope());

                            currBase = aFieldCall;
                        } else {
                            // @Todo cannot be resolved, should be qualifiered name?
                            break;
                        }
                    }

                    fieldCall.setBase(currBase);

                    base = currBase;
                }

                baseType = base.asType();
            }

            if (baseType != null) {
                if (!Type.isResolved(baseType)) {
                    globalInferType(index, baseType);
                }

                if (Type.isResolved(baseType)) {
                    baseTypeQName = Type.qualifiedNameOf(baseType);
                } else {
                    // @todo resolve it first
                }
            }

            if (baseTypeQName == null) {
                return;
            }
        }


        AstId field = fieldCall.getField();
        String fieldName = field.getSimpleName().toString();

        Set<GsfElement> gsfElements = index.getMembers(fieldName, baseTypeQName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE, null, false);
        for (GsfElement gsfElement : gsfElements) {
            if (!gsfElement.getElement().getSimpleName().toString().equals(fieldName)) {
                continue;
            }

            boolean isCandicate = false;
            TypeMirror mRetType = null;

            if (gsfElement.getElement() instanceof ExecutableElement) {
                ExecutableElement mFunction = (ExecutableElement) gsfElement.getElement();
                if (mFunction.getParameters().size() == 0) {
                    isCandicate = true;
                    mRetType = mFunction.asType();
                    if (mRetType != null && Type.isResolved(mRetType)) {
                        TypeElement mRetTe = Type.asElement(mRetType);
                        fieldCall.setType(new BasicType(mRetTe));
                        break;
                    }
                }
            } else if (gsfElement.getElement() instanceof VariableElement) {
                VariableElement mField = (VariableElement) gsfElement.getElement();
                isCandicate = true;
                mRetType = mField.asType();
                if (mRetType != null && Type.isResolved(mRetType)) {
                    TypeElement mRetTe = Type.asElement(mRetType);
                    fieldCall.setType(new BasicType(mRetTe));
                    break;
                }
            }

            String mRetTypeSName = mRetType == null ? null : Type.simpleNameOf(mRetType);
            if (isCandicate) {
                if (mRetTypeSName == null) {
                    mRetTypeSName = "Unit";
                }
                if (mRetTypeSName.equals("Unit")) {
                    fieldCall.setType(PredefinedTypes.NullType);
                    break;
                }

                /** try to find pkg of mRetType from gsfElement */
                TypeElement mRetTe = null;
                String itsIn = gsfElement.getIn();
                if (itsIn != null) {
                    int pkgNameEnd = itsIn.lastIndexOf('.');
                    if (pkgNameEnd != -1) {
                        String hisPkgName = itsIn.substring(0, pkgNameEnd);
                        Set<String> importPkgs = getImportPkgs(index, itsIn);
                        mRetTe = globalInferType(index, mRetTypeSName, hisPkgName, importPkgs);
                    } else {
                        System.out.println("found idx element without package: " + gsfElement.toString());
                    }
                } else {
                    // @todo
                }

                if (mRetTe != null) {
                    fieldCall.setType(new BasicType(mRetTe));
                }
                break;
            }
        }

    }

    private static TypeElement globalInferType(ScalaIndex index, TypeMirror type) {
        String sName = Type.simpleNameOf(type);
        if (sName == null) {
            return null;
        }

        if (type instanceof Type) {
            Type astType = (Type) type;
            List<Importing> importings = astType.getEnclosingScope().getVisibleElements(Importing.class);
            List<String> importedPkgs = new ArrayList<String>();
            for (Importing importing : importings) {
                if (importing.isWild()) {
                    importedPkgs.add(importing.getPackageName());
                }
            }

            Packaging packaging = astType.getPackageElement();
            String ofPackage = packaging == null ? null : packaging.getQualifiedName().toString();
            TypeElement typeElement = globalInferType(index, sName, ofPackage, importedPkgs);
            if (typeElement != null) {
                astType.setElement(typeElement);
            }
            return typeElement;
        } else if (type instanceof BasicType) {
            BasicType basicType = (BasicType) type;
            TypeElement typeElement = globalInferType(index, sName, null, Collections.<String>emptyList());
            if (typeElement != null) {
                basicType.setElement(typeElement);
            }
            return typeElement;
        }

        return null;
    }

    /**
     * 
     * @return null or full qualifier type name 
     */
    public static TypeElement globalInferType(ScalaIndex index, String sName, String ofPackage, Collection<String> importedPkgs) {
        // 1. search imported types first
        for (String pkgName : importedPkgs) {
            pkgName = pkgName + ".";
            if (pkgName.startsWith("_root_.")) {
                pkgName = pkgName.substring(7, pkgName.length());
            }

            for (GsfElement gsfElement : getImportedTypes(index, pkgName)) {
                IndexedElement element = (IndexedElement) gsfElement.getElement();
                if (element instanceof IndexedTypeElement) {
                    if (element.getSimpleName().toString().equals(sName)) {
                        return (IndexedTypeElement) element;
                    }
                }
            }
        }

        // 2. search packages with the same preceding of current packaging
        if (ofPackage != null) {
            for (String pkgName : importedPkgs) {
                pkgName = pkgName + ".";
                if (pkgName.startsWith("_root_.")) {
                    continue;
                }

                /* package name with the same preceding of current packaging can omit packaging name */
                pkgName = ofPackage + "." + pkgName;
                for (GsfElement gsfElement : getImportedTypes(index, pkgName)) {
                    IndexedElement element = (IndexedElement) gsfElement.getElement();
                    if (element instanceof IndexedTypeElement) {
                        if (element.getSimpleName().toString().equals(sName)) {
                            return (IndexedTypeElement) element;
                        }
                    }
                }

            }
        }

        // 3. search "scala" packages 
        for (String pkgName : importedPkgs) {
            pkgName = pkgName + ".";
            if (pkgName.startsWith("_root_.")) {
                continue;
            }

            /* package name starts with "scala" can omit "scala" */
            pkgName = "scala." + pkgName;
            for (GsfElement gsfElement : getScalaPrecedingPackageTypes(index, pkgName)) {
                IndexedElement element = (IndexedElement) gsfElement.getElement();
                if (element instanceof IndexedTypeElement) {
                    if (element.getSimpleName().toString().equals(sName)) {
                        return (IndexedTypeElement) element;
                    }
                }
            }

        }

        // 4. then search types under the same package
        if (ofPackage != null) {
            String pkgName = ofPackage + ".";
            for (GsfElement gsfElement : getPackageTypes(index, pkgName)) {
                IndexedElement element = (IndexedElement) gsfElement.getElement();
                if (element instanceof IndexedTypeElement) {
                    if (element.getSimpleName().toString().equals(sName)) {
                        return (IndexedTypeElement) element;
                    }
                }
            }
        }

        // 5. search auto-imported "scala." package
        for (GsfElement gsfElement : getScalaPackageTypes(index)) {
            IndexedElement element = (IndexedElement) gsfElement.getElement();
            if (element instanceof IndexedTypeElement) {
                if (element.getSimpleName().toString().equals(sName)) {
                    //return "scala." + sName;
                    return (IndexedTypeElement) element;
                }
            }
        }

        // 6. search auto-imported "java.lang." package
        for (GsfElement gsfElement : getJavaLangPackageTypes(index)) {
            IndexedElement element = (IndexedElement) gsfElement.getElement();
            if (element instanceof IndexedTypeElement) {
                if (element.getSimpleName().toString().equals(sName)) {
                    return (IndexedTypeElement) element;
                //return "java.lang." + sName;
                }
            }
        }

        return null;
    }

    private void globalInferExpr(AstExpression expr, TypeMirror knownExprType) {
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
        TypeMirror exprType = expr.asType();
        if (exprType == null) {
            return;
        }

        AstNode base = expr.getBase();
        TypeMirror baseType = base.asType();
        if (baseType != null && Type.isResolved(baseType)) {
            return;
        }

        if (base instanceof IdCall) {
            // resolve its element's type.
            AstElement element = rootScope.findElementOf(base);
            if (element != null) {
                TypeMirror type = element.asType();
                if (type != null) {
                    // @Todo check type of def with expr's type 
                } else {
                    element.setType(exprType);
                }
            }
        }
    }

    private void globalInferAssignmentExpr(AssignmentExpr expr) {
        AstExpression lhs = expr.getLhs();
        AstExpression rhs = expr.getRhs();
        globalInferExpr(rhs, null);
        globalInferExpr(lhs, rhs.asType());
    }

    public static TypeElement resolveType(Type type, ScalaIndex index) {
        String sName = type.getSimpleName().toString();

        List<Importing> importings = type.getEnclosingScope().getVisibleElements(Importing.class);
        List<String> importedPkgs = new ArrayList<String>();
        for (Importing importing : importings) {
            if (importing.isWild()) {
                importedPkgs.add(importing.getPackageName());
            }
        }

        Packaging packaging = type.getPackageElement();
        String ofPackage = packaging == null ? null : packaging.getQualifiedName().toString();

        TypeElement te = ScalaTypeInferencer.globalInferType(index, sName, ofPackage, importedPkgs);

        return te;
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

    private static Set<GsfElement> getJavaLangPackageTypes(ScalaIndex index) {
        if (javaLangPackageTypes == null) {
            javaLangPackageTypes = index.getPackageContent("java.lang.", NameKind.PREFIX, ScalaIndex.ALL_SCOPE);
        }

        return javaLangPackageTypes;
    }

    private static Set<GsfElement> getScalaPackageTypes(ScalaIndex index) {
        if (scalaPackageTypes == null) {
            scalaPackageTypes = index.getPackageContent("scala.", NameKind.PREFIX, ScalaIndex.ALL_SCOPE);
        }

        return scalaPackageTypes;
    }

    private static Set<GsfElement> getScalaPrecedingPackageTypes(ScalaIndex index, String pkgName) {
        if (scalaPrecedingPackageTypes == null) {
            scalaPrecedingPackageTypes = new HashMap<String, Set<GsfElement>>();
        }

        Set<GsfElement> gsfElements = scalaPrecedingPackageTypes.get(pkgName);
        if (gsfElements == null) {
            gsfElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            scalaPrecedingPackageTypes.put(pkgName, gsfElements);
        }

        return gsfElements;
    }

    private static Set<GsfElement> getImportedTypes(ScalaIndex index, String pkgName) {
        if (importedTypesCache == null) {
            importedTypesCache = new HashMap<String, Set<GsfElement>>();
        }

        Set<GsfElement> idxElements = importedTypesCache.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            importedTypesCache.put(pkgName, idxElements);
        }

        return idxElements;
    }

    private static Set<GsfElement> getPackageTypes(ScalaIndex index, String pkgName) {
        if (packageTypesCache == null) {
            packageTypesCache = new HashMap<String, Set<GsfElement>>();
        }

        Set<GsfElement> idxElements = packageTypesCache.get(pkgName);
        if (idxElements == null) {
            idxElements = index.getPackageContent(pkgName, NameKind.PREFIX, ScalaIndex.ALL_SCOPE);

            packageTypesCache.put(pkgName, idxElements);
        }

        return idxElements;
    }
}
