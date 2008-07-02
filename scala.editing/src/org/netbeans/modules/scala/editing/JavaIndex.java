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

import java.io.File;
import java.io.IOException;
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
import org.netbeans.modules.classfile.AttributeMap;
import org.netbeans.modules.classfile.ClassFile;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;
import org.netbeans.modules.scala.editing.scalasig.ByteArrayReader;
import org.netbeans.modules.scala.editing.scalasig.Entity;
import org.netbeans.modules.scala.editing.scalasig.EntityTable;
import org.netbeans.modules.scala.editing.scalasig.ScalaAttribute;

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

    public Set<GsfElement> getPackages(String fqnPrefix) {
        Set<String> pkgNames = index.getPackageNames(fqnPrefix, true, ALL_SCOPE);
        Set<GsfElement> gsfElements = new HashSet<GsfElement>();
        int flags = 0 | IndexedElement.PACKAGE;
        for (String pkgName : pkgNames) {
            if (pkgName.length() > 0) {

                IndexedElement idxElement = new IndexedElement(pkgName, pkgName, "", "", flags, null, scalaIndex, ElementKind.PACKAGE);
                GsfElement gsfElement = new GsfElement(idxElement, null, info);
                gsfElements.add(gsfElement);
            }
        }
        return gsfElements;
    }

    public Set<GsfElement> getPackageContent(String fqnPrefix) {
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

            Set<GsfElement> gsfElements = new HashSet<GsfElement>();

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
                GsfElement gsfElement = new GsfElement(idxElement, null, info);
                gsfElements.add(gsfElement);
            }

            return gsfElements;
        }
        return Collections.<GsfElement>emptySet();
    }

    public Set<GsfElement> getDeclaredTypes(String type, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context) {

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

    public Set<GsfElement> getMembers(String memberName, String typeQName, NameKind kind,
            Set<SearchScope> scope, ScalaParserResult context,
            boolean onlyConstructors, boolean includeMethods, boolean includeFields, boolean includeDuplicates) {

        final Set<GsfElement> gsfElements = includeDuplicates ? new DuplicateElementSet() : new HashSet<GsfElement>();

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

        boolean haveRedirected = false;
        boolean inherited = typeQName == null;

        if (typeQName == null || typeQName.length() == 0) {
            typeQName = "java.lang.Object";
        }

        kind = NameKind.SIMPLE_NAME;

        Elements theElements = info.getElements();
        Types theTypes = info.getTypes();

        boolean isScala = false;

        TypeElement te = null;
        TypeElement namedTe = theElements.getTypeElement(typeQName);
        if (namedTe != null) {
            te = ElementHandle.<TypeElement>create(namedTe).resolve(info);
        }

        if (te != null) {
            isScala = JavaScalaMapping.isScala(te);
        }

        TypeElement companionTe = null;
        TypeElement namedCompanionTe = theElements.getTypeElement(typeQName + "$");
        if (namedCompanionTe != null) {
            companionTe = ElementHandle.<TypeElement>create(namedCompanionTe).resolve(info);
        }

        if (companionTe != null) {
            isScala = isScala || JavaScalaMapping.isScala(companionTe);
            te = companionTe;
        }

        if (te == null) {
            return gsfElements;
        }

        File f = new File("/Users/dcaoyuan/my-project/scala-test/Foo.class");


//        try {
//            byte[] data = ClassFile.getBytesFromFile(f);
//            // construct a reader for the classfile content
//            ByteArrayReader reader = new ByteArrayReader(data);
//            // parse the classfile
//            scala.tools.scalap.Classfile clazz = new scala.tools.scalap.Classfile(reader);
//            // check if there is a Scala signature attribute
//            scala.List attrs = clazz.attribs();
//            for (scala.tools.scalap.Classfile.Attribute h = (scala.tools.scalap.Classfile.Attribute) attrs.head(); attrs.tail() != null; attrs = attrs.tail()) {
//                if (h.toString().equals("ScalaSig")) {
//                    ScalaAttribute scalaAttrs = new ScalaAttribute(h.reader());
//                    EntityTable symtab = new EntityTable(scalaAttrs);
//                    Entity[] entitys = symtab.table();
//                    for (Entity en : entitys) {
//                        System.out.println(en.toString());
//                    }
//                }
//
//            }
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        Object h = attrs.head();
//        val attrib = clazz.attribs.find(a => a.toString() == "ScalaSignature")
//      attrib match {
//        // if the attribute is found, we have to extract the scope
//        // from the attribute
//        case Some(a) =>
//          processScalaAttribute(args, a.reader)
//        // It must be a Java class
//        case None =>
//          processJavaClassFile(clazz)
//      }


        ClassFile cFile;
        try {
            cFile = new ClassFile("/Users/dcaoyuan/my-project/scala-test/Foo.class", false);
            AttributeMap am = cFile.getAttributes();
            byte[] scalaSig = am.get("ScalaSig");
            if (scalaSig != null) {
                ScalaAttribute scalaAttr = null;
                try {
                    scalaAttr = new ScalaAttribute(new ByteArrayReader(scalaSig));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (scalaAttr != null) {
                    EntityTable symtab = new EntityTable(scalaAttr);
                    Entity[] entitys = symtab.table();
                    for (Entity en : entitys) {
                        System.out.println(en.toString());
                    }
                }

                System.out.println("here");
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //index.getResources(element, searchKind, scope)
        //FileObject classFo = org.netbeans.api.java.source.SourceUtils.getFile(ElementHandle.create(te), info.getClasspathInfo());

        //Set<FileObject> fos = index.getResources(ElementHandle.create(te), Collections.singleton(ClassIndex.SearchKind.IMPLEMENTORS), scope);

        TypeMirror tm = te.asType();
        TypeElement typeElem = tm.getKind() == TypeKind.DECLARED ? (TypeElement) ((DeclaredType) tm).asElement() : null;

        List<? extends Element> elements = null;
        if (te != null) {
            elements = theElements.getAllMembers(te);
        }

        if (elements != null) {
            for (Element e : elements) {

                if (e.getModifiers().contains(Modifier.PRIVATE)) {
                    continue;
                }

                String sName = e.getSimpleName().toString();
                if (!JavaUtilities.startsWith(sName, memberName)) {
                    continue;
                }

                GsfElement gsfElement = null;

                //String in = pe.getQualifiedName().toString() + "." + e.getEnclosingElement().getSimpleName().toString();

                switch (e.getKind()) {
                    case EXCEPTION_PARAMETER:
                    case LOCAL_VARIABLE:
                    case PARAMETER:
                        break;
                    case ENUM_CONSTANT:
                    case FIELD: {
                        if ("this".equals(sName) || "class".equals(sName) || "super".equals(sName)) {
                            //results.add(JavaCompletionItem.createKeywordItem(ename, null, anchorOffset, false));
                            } else {
                            TypeMirror tm1 = tm.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) tm, e) : e.asType();
                        //results.add(JavaCompletionItem.createVariableItem((VariableElement) e, tm, anchorOffset, typeElem != e.getEnclosingElement(), elements.isDeprecated(e), isOfSmartType(env, tm, smartTypes)));
                        }

//                            StringBuilder base = new StringBuilder();
//                            base.append(simpleName.toLowerCase());
//                            base.append(';');
//                            if (in != null) {
//                                base.append(in);
//                            }
//                            base.append(';');
//                            base.append(simpleName);
//                            base.append(';');
//
//                            String attrs = IndexedElement.encodeAttributes(e);
//                            base.append(attrs);

                        gsfElement = new GsfElement(e, null, info);
                        //idxElement.setJavaInfo(e, info);
                        break;
                    }
                    case CONSTRUCTOR:
                        sName = e.getEnclosingElement().getSimpleName().toString();
                    case METHOD: {
                        ExecutableType et = (ExecutableType) (tm.getKind() == TypeKind.DECLARED ? theTypes.asMemberOf((DeclaredType) tm, e) : e.asType());

//                            StringBuilder base = new StringBuilder();
//                            base.append(simpleName.toLowerCase());
//                            base.append(';');
//                            if (in != null) {
//                                base.append(in);
//                            }
//                            base.append(';');
//                            base.append(simpleName);
//                            base.append(';');
//
//                            String attrs = IndexedElement.encodeAttributes(e);
//                            base.append(attrs);

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

                if (isScala) {
                    gsfElement.setScalaFromClass();
                }

                boolean isMethod = gsfElement.getElement() instanceof ExecutableElement;
                if (isMethod && !includeMethods) {
                    continue;
                } else if (!isMethod && !includeFields) {
                    continue;
                }

                if (onlyConstructors && te.getKind() != ElementKind.CONSTRUCTOR) {
                    continue;
                }

                if (!haveRedirected) {
                    gsfElement.setSmart(true);
                }

                inherited = typeElem != e.getEnclosingElement();
                if (inherited) {
                    gsfElement.setInherited(true);
                }
                gsfElements.add(gsfElement);
            }
        }

        return gsfElements;
    }
}
