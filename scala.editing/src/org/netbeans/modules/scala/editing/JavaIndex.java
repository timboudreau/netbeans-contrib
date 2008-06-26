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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.java.source.ClassIndex;
import org.netbeans.api.java.source.ClassIndex.NameKind;
import org.netbeans.api.java.source.ClassIndex.SearchScope;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;

/**
 *
 * @author Caoyuan Deng
 */
public class JavaIndex {

    public static final Map<String, List<? extends Element>> TypeQNameToMemebersCache = new HashMap<String, List<? extends Element>>();
    public static final Set<SearchScope> ALL_SCOPE = EnumSet.allOf(SearchScope.class);
    public static final Set<SearchScope> SOURCE_SCOPE = EnumSet.of(SearchScope.SOURCE);
    private final ClassIndex index;
    private final CompilationInfo info;
    private final ScalaIndex scalaIndex;

    public static JavaIndex get(FileObject fo, ScalaIndex scalaIndex) {
        CompilationInfo info = JavaUtilities.getCompilationInfoForScalaFile(fo);

        ClassIndex index = info.getClasspathInfo().getClassIndex();

        return new JavaIndex(index, info, scalaIndex);
    }

    private JavaIndex(ClassIndex index, CompilationInfo info, ScalaIndex scalaIndex) {
        this.index = index;
        this.info = info;
        this.scalaIndex = scalaIndex;
    }

    public Set<IndexedElement> getPackages(String fqnPrefix) {
        Set<String> pkgNames = index.getPackageNames(fqnPrefix, true, ALL_SCOPE);
        Set<IndexedElement> idxElements = new HashSet<IndexedElement>();
        int flags = 0 | IndexedElement.PACKAGE;
        for (String pkgName : pkgNames) {
            if (pkgName.length() > 0) {
                
                IndexedElement idxElement = new IndexedElement(pkgName, pkgName, "", "", flags, null, scalaIndex, ElementKind.PACKAGE);
                idxElements.add(idxElement);
            }
        }
        return idxElements;
    }

    public Set<IndexedElement> getPackageContent(String fqnPrefix) {
        String pkgName = null;
        String prefix = null;

        int lastDot = fqnPrefix.lastIndexOf('.');
        if (lastDot == -1) {
            pkgName = fqnPrefix;
            prefix = "";
        } else if (lastDot == fqnPrefix.length() - 1) {
            pkgName = fqnPrefix.substring(0, lastDot);
            prefix = "";
        } else {
            pkgName = fqnPrefix.substring(0, lastDot);
            prefix = fqnPrefix.substring(lastDot + 1, fqnPrefix.length());
        }

        Types theTypes = info.getTypes();
        Elements theElements = info.getElements();
        PackageElement pe = theElements.getPackageElement(pkgName);
        if (pe != null) {
            Set<Element> foundElements = new HashSet<Element>();
            Set<String> scalaElementNames = new HashSet<String>();

            Set<IndexedElement> idxElements = new HashSet<IndexedElement>();

            for (Element e : pe.getEnclosedElements()) {
                String sName = e.getSimpleName().toString();
                
                TypeMirror tm = e.asType();
                TypeElement te = tm.getKind() == TypeKind.DECLARED
                        ? (TypeElement) ((DeclaredType) tm).asElement()
                        : null;

                if (te != null) {
                    JavaScalaMapping.ScalaKind scalaKind = JavaScalaMapping.getScalaKind(te);
                    if (scalaKind != null) {
                        switch (scalaKind) {
                            case Trait:
                                scalaElementNames.add(sName + "$class");
                                break;
                            case Object:
                                // This class should be end with '$', and there should be a compain class without '$'
                                scalaElementNames.add(sName);
                                //int dollor = sName.lastIndexOf('$');
                                //if (dollor != -1) {
                                //    scalaElementNames.add(sName.substring(0, dollor));
                                //}
                                break;
                        }
                        
                        //continue;
                    }
                }

                if (e.getKind().isClass() || e.getKind().isInterface()) {

                    if (JavaUtilities.startsWith(sName, prefix)) {
                        foundElements.add(e);
                    }
                }

            }

            for (Element e : foundElements) {
                String sName = e.getSimpleName().toString();
                String qName = pkgName + "." + sName;
                
                if (scalaElementNames.contains(sName)) {
                    continue;
                }

                String in = "";
                String attrs = IndexedElement.encodeAttributes(e);

                IndexedElement idxElement = IndexedElement.create(qName, sName, in, attrs, null, scalaIndex, false);
                idxElement.setJavaInfo(e, info);
                idxElements.add(idxElement);
            }

            return idxElements;
        }
        return Collections.<IndexedElement>emptySet();
    }
 
    public Set<GsfElement> getDeclaredTypes(String type, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context) {

        //final Set<GsfElement> idxElements = includeDuplicates ? new DuplicateElementSet() : new HashSet<IndexedElement>();
        final Set<GsfElement> gsfElements = new HashSet<GsfElement>();

        JavaSourceAccessor.getINSTANCE().lockJavaCompiler();

        NameKind originalKind = kind;
        if (kind == NameKind.SIMPLE_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (type == null || type.length() == 0) {
            type = "Object";
        }

        Set<ElementHandle<TypeElement>> dclTypes = index.getDeclaredTypes(type, kind, scope);

        for (ElementHandle<TypeElement> teHandle : dclTypes) {
            TypeElement te = teHandle.resolve(info);

            boolean isScala = JavaScalaMapping.isScala(te);

            if (isScala) {
                continue;
            }

            TypeMirror tm = te.asType();
            TypeElement typeElem = tm.getKind() == TypeKind.DECLARED ? (TypeElement) ((DeclaredType) tm).asElement() : null;
            
            if (te != null) {
                GsfElement gsfElement = new GsfElement(typeElem, null, info);
                gsfElements.add(gsfElement);
            }

        }


        JavaSourceAccessor.getINSTANCE().unlockJavaCompiler();
        return gsfElements;
    }
    
    
    public Set<GsfElement> getMembers(String name, String typeSName, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors, boolean includeMethods, boolean includeProperties, boolean includeDuplicates) {

        final Set<GsfElement> gsfElements = new HashSet<GsfElement>();
        //final Set<GsfElement> idxElements = includeDuplicates ? new DuplicateElementSet() : new HashSet<GsfElement>();

        JavaSourceAccessor.getINSTANCE().lockJavaCompiler();

        NameKind originalKind = kind;
        if (kind == NameKind.SIMPLE_NAME) {
            // I can't do exact searches on methods because the method
            // entries include signatures etc. So turn this into a prefix
            // search and then compare chopped off signatures with the name
            kind = NameKind.PREFIX;
        }

        if (kind == NameKind.CASE_INSENSITIVE_PREFIX || kind == NameKind.CASE_INSENSITIVE_REGEXP) {
            // TODO - can I do anything about this????
            //field = ScalaIndexer.FIELD_BASE_LOWER;
            //terms = FQN_BASE_LOWER;
        }

        String searchUrl = null;
        if (context != null) {
            try {
                searchUrl = context.getFile().getFileObject().getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        Set<String> seenTypes = new HashSet<String>();
        seenTypes.add(typeSName);
        boolean haveRedirected = false;
        boolean inherited = typeSName == null;

        if (typeSName == null || typeSName.length() == 0) {
            typeSName = "Object";
        }

        String pkgName = "";
        String fqn;
        if (typeSName != null && typeSName.length() > 0) {
            int lastDot = typeSName.lastIndexOf('.');
            if (lastDot > 0) {
                pkgName = typeSName.substring(0, lastDot);
                typeSName = typeSName.substring(lastDot + 1, typeSName.length());
            }

            fqn = typeSName + "." + name;
        } else {
            fqn = name;
        }

        String lcfqn = fqn.toLowerCase();

        /** always use NameKind.SIMPLE_NAME search index.getDeclaredTypes */
        kind = NameKind.SIMPLE_NAME;

        Elements theElements = info.getElements();
        Types theTypes = info.getTypes();

        Set<ElementHandle<TypeElement>> dclTypes = index.getDeclaredTypes(typeSName, kind, scope);

        for (ElementHandle<TypeElement> teHandle : dclTypes) {
            GsfElement gsfElement = null;

            TypeElement te = teHandle.resolve(info);
            if (te == null) {
                /** @Note: will this happen? if happens, why? */
                continue;
            }
            String typeQName = te.getQualifiedName().toString();

            List<? extends Element> elements = TypeQNameToMemebersCache.get(typeQName);
            if (elements == null) {

            }

            PackageElement pe = theElements.getPackageOf(te);
            if (pe != null) {
                if (!pkgName.equals("")) {
                    if (!pe.getQualifiedName().toString().equals(pkgName)) {
                        continue;
                    }
                }
            }

            boolean isScala = JavaScalaMapping.isScala(te);

            if (isScala) {
                //continue;
            }

            TypeMirror tm = te.asType();
            TypeElement typeElem = tm.getKind() == TypeKind.DECLARED ? (TypeElement) ((DeclaredType) tm).asElement() : null;
            
            if (te != null) {
                elements = theElements.getAllMembers(te);
                TypeQNameToMemebersCache.put(typeQName, elements);
            }

            if (elements != null) {
                for (Element e : theElements.getAllMembers(te)) {

                    if (e.getModifiers().contains(Modifier.PRIVATE)) {
                        continue;
                    }

                    String simpleName = e.getSimpleName().toString();
                    if (!JavaUtilities.startsWith(simpleName, name)) {
                        continue;
                    }

                    String in = pe.getQualifiedName().toString() + "." + e.getEnclosingElement().getSimpleName().toString();

                    switch (e.getKind()) {
                        case EXCEPTION_PARAMETER:
                        case LOCAL_VARIABLE:
                        case PARAMETER:
                            break;
                        case ENUM_CONSTANT:
                        case FIELD: {
                            if ("this".equals(simpleName) || "class".equals(simpleName) || "super".equals(simpleName)) {
                                //results.add(JavaCompletionItem.createKeywordItem(ename, null, anchorOffset, false));
                            } else {
                                TypeMirror tm1 = tm.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) tm, e) : e.asType();
                            //results.add(JavaCompletionItem.createVariableItem((VariableElement) e, tm, anchorOffset, typeElem != e.getEnclosingElement(), elements.isDeprecated(e), isOfSmartType(env, tm, smartTypes)));
                            }

                            StringBuilder base = new StringBuilder();
                            base.append(simpleName.toLowerCase());
                            base.append(';');
                            if (in != null) {
                                base.append(in);
                            }
                            base.append(';');
                            base.append(simpleName);
                            base.append(';');

                            String attrs = IndexedElement.encodeAttributes(e);
                            base.append(attrs);

                            gsfElement = new GsfElement(e, null, info);
                            //idxElement.setJavaInfo(e, info);
                            break;
                        }
                        case CONSTRUCTOR:
                            simpleName = e.getEnclosingElement().getSimpleName().toString();
                        case METHOD: {
                            ExecutableType et = (ExecutableType) (tm.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) tm, e) : e.asType());
                            
                            StringBuilder base = new StringBuilder();
                            base.append(simpleName.toLowerCase());
                            base.append(';');
                            if (in != null) {
                                base.append(in);
                            }
                            base.append(';');
                            base.append(simpleName);
                            base.append(';');

                            String attrs = IndexedElement.encodeAttributes(e);
                            base.append(attrs);

                            gsfElement = new GsfElement(e, null, info);
                            break;
                        }
                        case CLASS:
                        case ENUM:
                        case INTERFACE:
                        case ANNOTATION_TYPE:
                            DeclaredType dt = (DeclaredType) (tm.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) tm, e) : e.asType());
                            //results.add(JavaCompletionItem.createTypeItem((TypeElement) e, dt, anchorOffset, false, elements.isDeprecated(e), insideNew, false));
                            break;
                    }

                    if (gsfElement == null) {
                        continue;
                    }
                    boolean isMethod = gsfElement.getElement() instanceof ExecutableElement;
                    if (isMethod && !includeMethods) {
                        continue;
                    } else if (!isMethod && !includeProperties) {
                        continue;
                    }

                    if (onlyConstructors && !gsfElement.getKind().name().equals(ElementKind.CONSTRUCTOR.name())) {
                        continue;
                    }

                    if (!haveRedirected) {
                        gsfElement.setSmart(true);
                    }

                    inherited = typeElem != e.getEnclosingElement();
                    if (!inherited) {
                        gsfElement.setInherited(false);
                    }
                    gsfElements.add(gsfElement);
                }
            }

        }


        JavaSourceAccessor.getINSTANCE().unlockJavaCompiler();
        return gsfElements;
    }    
}
