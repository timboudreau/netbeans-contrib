/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.editing;

import com.sun.javadoc.Doc;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.regex.Pattern;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;

import org.netbeans.api.java.source.ClasspathInfo;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.SourceUtils;
import org.netbeans.api.java.source.Task;
import org.netbeans.modules.java.source.JavaSourceAccessor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Dusan Balek
 */
public class JavaUtilities {

    private static final String CAPTURED_WILDCARD = "<captured wildcard>"; //NOI18N
    private static final String ERROR = "<error>"; //NOI18N
    private static final String UNKNOWN = "<unknown>"; //NOI18N
    private static boolean caseSensitive = true;
    private static boolean showDeprecatedMembers = true;
    private static boolean inited;
    private static String cachedPrefix = null;
    private static Pattern cachedPattern = null;

    public static boolean startsWith(String theString, String prefix) {
        if (theString == null || theString.length() == 0 || ERROR.equals(theString)) {
            return false;
        }
        if (prefix == null || prefix.length() == 0) {
            return true;
        }
        return isCaseSensitive() ? theString.startsWith(prefix) : theString.toLowerCase().startsWith(prefix.toLowerCase());
    }

    public static boolean startsWithCamelCase(String theString, String prefix) {
        if (theString == null || theString.length() == 0 || prefix == null || prefix.length() == 0) {
            return false;
        }
        if (!prefix.equals(cachedPrefix) || cachedPattern == null) {
            StringBuilder sb = new StringBuilder();
            int lastIndex = 0;
            int index;
            do {
                index = findNextUpper(prefix, lastIndex + 1);
                String token = prefix.substring(lastIndex, index == -1 ? prefix.length() : index);
                sb.append(token);
                sb.append(index != -1 ? "[\\p{javaLowerCase}\\p{Digit}_\\$]*" : ".*"); // NOI18N         
                lastIndex = index;
            } while (index != -1);
            cachedPrefix = prefix;
            cachedPattern = Pattern.compile(sb.toString());
        }
        return cachedPattern.matcher(theString).matches();
    }

    private static int findNextUpper(String text, int offset) {
        for (int i = offset; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isCaseSensitive() {
        //lazyInit();
        return caseSensitive;
    }

    public static void setCaseSensitive(boolean b) {
        //lazyInit();
        caseSensitive = b;
    }

    public static boolean isShowDeprecatedMembers() {
        //lazyInit();
        return showDeprecatedMembers;
    }

    public static void setShowDeprecatedMembers(boolean b) {
        //lazyInit();
        showDeprecatedMembers = b;
    }

//    private static void lazyInit() {
//        if (!inited) {
//            inited = true;
//            Settings.addSettingsChangeListener(settingsListener);
//            setCaseSensitive(SettingsUtil.getBoolean(JavaKit.class,
//                    ExtSettingsNames.COMPLETION_CASE_SENSITIVE,
//                    ExtSettingsDefaults.defaultCompletionCaseSensitive));
//            setShowDeprecatedMembers(SettingsUtil.getBoolean(JavaKit.class,
//                    ExtSettingsNames.SHOW_DEPRECATED_MEMBERS,
//                    ExtSettingsDefaults.defaultShowDeprecatedMembers));
//        }
//    }
    public static int getImportanceLevel(String fqn) {
        int weight = 50;
        if (fqn.startsWith("java.lang") || fqn.startsWith("java.util")) // NOI18N
        {
            weight -= 10;
        } else if (fqn.startsWith("org.omg") || fqn.startsWith("org.apache")) // NOI18N
        {
            weight += 10;
        } else if (fqn.startsWith("com.sun") || fqn.startsWith("com.ibm") || fqn.startsWith("com.apple")) // NOI18N
        {
            weight += 20;
        } else if (fqn.startsWith("sun") || fqn.startsWith("sunw") || fqn.startsWith("netscape")) // NOI18N
        {
            weight += 30;
        }
        return weight;
    }

    public static boolean hasAccessibleInnerClassConstructor(Element e, Scope scope, Trees trees) {
        DeclaredType dt = (DeclaredType) e.asType();
        for (TypeElement inner : ElementFilter.typesIn(e.getEnclosedElements())) {
            if (trees.isAccessible(scope, inner, dt)) {
                DeclaredType innerType = (DeclaredType) inner.asType();
                for (ExecutableElement ctor : ElementFilter.constructorsIn(inner.getEnclosedElements())) {
                    if (trees.isAccessible(scope, ctor, innerType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static TreePath getPathElementOfKind(Tree.Kind kind, TreePath path) {
        return getPathElementOfKind(EnumSet.of(kind), path);
    }

    public static TreePath getPathElementOfKind(EnumSet<Tree.Kind> kinds, TreePath path) {
        while (path != null) {
            if (kinds.contains(path.getLeaf().getKind())) {
                return path;
            }
            path = path.getParentPath();
        }
        return null;
    }

    public static CharSequence getTypeName(TypeMirror type, boolean fqn) {
        return getTypeName(type, fqn, false);
    }

    public static CharSequence getTypeName(TypeMirror type, boolean fqn, boolean varArg) {
        if (type == null) {
            return "";
        } //NOI18N
        return new TypeNameVisitor(varArg).visit(type, fqn);
    }

    public static CharSequence getElementName(Element el, boolean fqn) {
        if (el == null || el.asType().getKind() == TypeKind.NONE) {
            return "";
        } //NOI18N
        return new ElementNameVisitor().visit(el, fqn);
    }

    public static Collection<? extends Element> getForwardReferences(TreePath path, int pos, SourcePositions sourcePositions, Trees trees) {
        HashSet<Element> refs = new HashSet<Element>();
        while (path != null) {
            switch (path.getLeaf().getKind()) {
                case BLOCK:
                case CLASS:
                    return refs;
                case VARIABLE:
                    refs.add(trees.getElement(path));
                    TreePath parent = path.getParentPath();
                    if (parent.getLeaf().getKind() == Tree.Kind.CLASS) {
                        boolean isStatic = ((VariableTree) path.getLeaf()).getModifiers().getFlags().contains(Modifier.STATIC);
                        for (Tree member : ((ClassTree) parent.getLeaf()).getMembers()) {
                            if (member.getKind() == Tree.Kind.VARIABLE && sourcePositions.getStartPosition(path.getCompilationUnit(), member) >= pos &&
                                    (isStatic || !((VariableTree) member).getModifiers().getFlags().contains(Modifier.STATIC))) {
                                refs.add(trees.getElement(new TreePath(parent, member)));
                            }
                        }
                    }
                    return refs;
                case ENHANCED_FOR_LOOP:
                    EnhancedForLoopTree efl = (EnhancedForLoopTree) path.getLeaf();
                    if (sourcePositions.getEndPosition(path.getCompilationUnit(), efl.getExpression()) >= pos) {
                        refs.add(trees.getElement(new TreePath(path, efl.getVariable())));
                    }
            }
            path = path.getParentPath();
        }
        return refs;
    }

    public static List<String> varNamesSuggestions(TypeMirror type, String prefix, Types types, Elements elements, Iterable<? extends Element> locals, boolean isConst) {
        List<String> result = new ArrayList<String>();
        if (type == null) {
            return result;
        }
        List<String> vnct = varNamesForType(type, types, elements);
        if (isConst) {
            List<String> ls = new ArrayList<String>(vnct.size());
            for (String s : vnct) {
                ls.add(getConstName(s));
            }
            vnct = ls;
        }
        String p = prefix;
        while (p != null && p.length() > 0) {
            List<String> l = new ArrayList<String>();
            for (String name : vnct) {
                if (startsWith(name, p)) {
                    l.add(name);
                }
            }
            if (l.isEmpty()) {
                p = nextName(p);
            } else {
                vnct = l;
                prefix = prefix.substring(0, prefix.length() - p.length());
                p = null;
            }
        }
        for (String name : vnct) {
            boolean isPrimitive = type.getKind().isPrimitive();
            if (prefix != null && prefix.length() > 0) {
                if (isConst) {
                    name = prefix.toUpperCase() + '_' + name;
                } else {
                    name = prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }
            }
            int cnt = 1;
            while (isClashing(name, locals)) {
                if (isPrimitive) {
                    char c = name.charAt(0);
                    name = Character.toString(++c);
                    if (c == 'z') //NOI18N
                    {
                        isPrimitive = false;
                    }
                } else {
                    name += cnt++;
                }
            }
            result.add(name);
        }
        return result;
    }

    public static boolean inAnonymousOrLocalClass(TreePath path) {
        if (path == null) {
            return false;
        }
        TreePath parentPath = path.getParentPath();
        if (path.getLeaf().getKind() == Tree.Kind.CLASS && parentPath.getLeaf().getKind() != Tree.Kind.COMPILATION_UNIT && parentPath.getLeaf().getKind() != Tree.Kind.CLASS) {
            return true;
        }
        return inAnonymousOrLocalClass(parentPath);
    }

    private static List<String> varNamesForType(TypeMirror type, Types types, Elements elements) {
        switch (type.getKind()) {
            case ARRAY:
                TypeElement iterableTE = elements.getTypeElement("java.lang.Iterable"); //NOI18N
                TypeMirror iterable = iterableTE != null ? types.getDeclaredType(iterableTE) : null;
                TypeMirror ct = ((ArrayType) type).getComponentType();
                if (ct.getKind() == TypeKind.ARRAY && iterable != null && types.isSubtype(ct, iterable)) {
                    return varNamesForType(ct, types, elements);
                }
                List<String> vnct = new ArrayList<String>();
                for (String name : varNamesForType(ct, types, elements)) {
                    vnct.add(name.endsWith("s") ? name + "es" : name + "s");
                } //NOI18N
                return vnct;
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case DOUBLE:
            case FLOAT:
            case INT:
            case LONG:
            case SHORT:
                return Collections.<String>singletonList(type.toString().substring(0, 1));
            case TYPEVAR:
                return Collections.<String>singletonList(type.toString().toLowerCase());
            case ERROR:
                String tn = ((ErrorType) type).asElement().getSimpleName().toString();
                if (tn.toUpperCase().contentEquals(tn)) {
                    return Collections.<String>singletonList(tn.toLowerCase());
                }
                StringBuilder sb = new StringBuilder();
                ArrayList<String> al = new ArrayList<String>();
                if ("Iterator".equals(tn)) //NOI18N
                {
                    al.add("it");
                } //NOI18N
                while ((tn = nextName(tn)).length() > 0) {
                    al.add(tn);
                    sb.append(tn.charAt(0));
                }
                if (sb.length() > 0) {
                    al.add(sb.toString());
                }
                return al;
            case DECLARED:
                iterableTE = elements.getTypeElement("java.lang.Iterable"); //NOI18N
                iterable = iterableTE != null ? types.getDeclaredType(iterableTE) : null;
                tn = ((DeclaredType) type).asElement().getSimpleName().toString();
                if (tn.toUpperCase().contentEquals(tn)) {
                    return Collections.<String>singletonList(tn.toLowerCase());
                }
                sb = new StringBuilder();
                al = new ArrayList<String>();
                if ("Iterator".equals(tn)) //NOI18N
                {
                    al.add("it");
                } //NOI18N
                while ((tn = nextName(tn)).length() > 0) {
                    al.add(tn);
                    sb.append(tn.charAt(0));
                }
                if (iterable != null && types.isSubtype(type, iterable)) {
                    List<? extends TypeMirror> tas = ((DeclaredType) type).getTypeArguments();
                    if (tas.size() > 0) {
                        TypeMirror et = tas.get(0);
                        if (et.getKind() == TypeKind.ARRAY || (et.getKind() != TypeKind.WILDCARD && types.isSubtype(et, iterable))) {
                            al.addAll(varNamesForType(et, types, elements));
                        } else {
                            for (String name : varNamesForType(et, types, elements)) {
                                al.add(name.endsWith("s") ? name + "es" : name + "s");
                            } //NOI18N
                        }
                    }
                }
                if (sb.length() > 0) {
                    al.add(sb.toString());
                }
                return al;
            case WILDCARD:
                TypeMirror bound = ((WildcardType) type).getExtendsBound();
                if (bound == null) {
                    bound = ((WildcardType) type).getSuperBound();
                }
                if (bound != null) {
                    return varNamesForType(bound, types, elements);
                }
        }
        return Collections.<String>emptyList();
    }

    private static String getConstName(String s) {
        StringBuilder sb = new StringBuilder();
        boolean prevUpper = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!prevUpper) {
                    sb.append('_');
                }
                sb.append(c);
                prevUpper = true;
            } else {
                sb.append(Character.toUpperCase(c));
                prevUpper = false;
            }
        }
        return sb.toString();
    }

    private static String nextName(CharSequence name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                char lc = Character.toLowerCase(c);
                sb.append(lc);
                sb.append(name.subSequence(i + 1, name.length()));
                break;
            }
        }
        return sb.toString();
    }

    private static boolean isClashing(String varName, Iterable<? extends Element> locals) {
        return false;
//        if (JavaTokenContext.getKeyword(varName) != null)
//            return true;
//        for (Element e : locals) {
//            if ((e.getKind() == ElementKind.LOCAL_VARIABLE || e.getKind() == ElementKind.PARAMETER || e.getKind() == ElementKind.EXCEPTION_PARAMETER) && varName.contentEquals(e.getSimpleName()))
//                return true;
//        }
//        return false;
    }

    private static class TypeNameVisitor extends SimpleTypeVisitor6<StringBuilder, Boolean> {

        private boolean varArg;

        private TypeNameVisitor(boolean varArg) {
            super(new StringBuilder());
            this.varArg = varArg;
        }

        @Override
        public StringBuilder defaultAction(TypeMirror t, Boolean p) {
            return DEFAULT_VALUE.append(t);
        }

        @Override
        public StringBuilder visitDeclared(DeclaredType t, Boolean p) {
            Element e = t.asElement();
            if (e instanceof TypeElement) {
                TypeElement te = (TypeElement) e;
                DEFAULT_VALUE.append((p ? te.getQualifiedName() : te.getSimpleName()).toString());
                Iterator<? extends TypeMirror> it = t.getTypeArguments().iterator();
                if (it.hasNext()) {
                    DEFAULT_VALUE.append("<"); //NOI18N
                    while (it.hasNext()) {
                        visit(it.next(), p);
                        if (it.hasNext()) {
                            DEFAULT_VALUE.append(", ");
                        } //NOI18N
                    }
                    DEFAULT_VALUE.append(">"); //NOI18N
                }
                return DEFAULT_VALUE;
            } else {
                return DEFAULT_VALUE.append(UNKNOWN); //NOI18N
            }
        }

        @Override
        public StringBuilder visitArray(ArrayType t, Boolean p) {
            boolean isVarArg = varArg;
            varArg = false;
            visit(t.getComponentType(), p);
            return DEFAULT_VALUE.append(isVarArg ? "..." : "[]"); //NOI18N
        }

        @Override
        public StringBuilder visitTypeVariable(TypeVariable t, Boolean p) {
            Element e = t.asElement();
            if (e != null) {
                String name = e.getSimpleName().toString();
                if (!CAPTURED_WILDCARD.equals(name)) {
                    return DEFAULT_VALUE.append(name);
                }
            }
            DEFAULT_VALUE.append("?"); //NOI18N
            TypeMirror bound = t.getLowerBound();
            if (bound != null && bound.getKind() != TypeKind.NULL) {
                DEFAULT_VALUE.append(" super "); //NOI18N
                visit(bound, p);
            } else {
                bound = t.getUpperBound();
                if (bound != null && bound.getKind() != TypeKind.NULL) {
                    DEFAULT_VALUE.append(" extends "); //NOI18N
                    if (bound.getKind() == TypeKind.TYPEVAR) {
                        bound = ((TypeVariable) bound).getLowerBound();
                    }
                    visit(bound, p);
                }
            }
            return DEFAULT_VALUE;
        }

        @Override
        public StringBuilder visitWildcard(WildcardType t, Boolean p) {
            int len = DEFAULT_VALUE.length();
            DEFAULT_VALUE.append("?"); //NOI18N
            TypeMirror bound = t.getSuperBound();
            if (bound == null) {
                bound = t.getExtendsBound();
                if (bound != null) {
                    DEFAULT_VALUE.append(" extends "); //NOI18N
                    if (bound.getKind() == TypeKind.WILDCARD) {
                        bound = ((WildcardType) bound).getSuperBound();
                    }
                    visit(bound, p);
                } else if (len == 0) {
                    bound = SourceUtils.getBound(t);
                    if (bound != null && (bound.getKind() != TypeKind.DECLARED || !((TypeElement) ((DeclaredType) bound).asElement()).getQualifiedName().contentEquals("java.lang.Object"))) { //NOI18N
                        DEFAULT_VALUE.append(" extends "); //NOI18N
                        visit(bound, p);
                    }
                }
            } else {
                DEFAULT_VALUE.append(" super "); //NOI18N
                visit(bound, p);
            }
            return DEFAULT_VALUE;
        }

        @Override
        public StringBuilder visitError(ErrorType t, Boolean p) {
            Element e = t.asElement();
            if (e instanceof TypeElement) {
                TypeElement te = (TypeElement) e;
                return DEFAULT_VALUE.append((p ? te.getQualifiedName() : te.getSimpleName()).toString());
            }
            return DEFAULT_VALUE;
        }
    }

    private static class ElementNameVisitor extends SimpleElementVisitor6<StringBuilder, Boolean> {

        private ElementNameVisitor() {
            super(new StringBuilder());
        }

        @Override
        public StringBuilder visitPackage(PackageElement e, Boolean p) {
            return DEFAULT_VALUE.append((p ? e.getQualifiedName() : e.getSimpleName()).toString());
        }

        @Override
        public StringBuilder visitType(TypeElement e, Boolean p) {
            return DEFAULT_VALUE.append((p ? e.getQualifiedName() : e.getSimpleName()).toString());
        }
    }

    /**
     * @since 2.12
     */
    public static ExecutableElement fuzzyResolveMethodInvocation(CompilationInfo info, TreePath path, TypeMirror[] proposed, int[] index) {
        assert path.getLeaf().getKind() == Kind.METHOD_INVOCATION || path.getLeaf().getKind() == Kind.NEW_CLASS;

        if (path.getLeaf().getKind() == Kind.METHOD_INVOCATION) {
            List<TypeMirror> actualTypes = new LinkedList<TypeMirror>();
            MethodInvocationTree mit = (MethodInvocationTree) path.getLeaf();

            for (Tree a : mit.getArguments()) {
                TreePath tp = new TreePath(path, a);
                actualTypes.add(info.getTrees().getTypeMirror(tp));
            }

            String methodName;
            TypeMirror on;

            switch (mit.getMethodSelect().getKind()) {
                case IDENTIFIER:
                    Scope s = info.getTrees().getScope(path);
                    TypeElement enclosingClass = s.getEnclosingClass();
                    on = enclosingClass != null ? enclosingClass.asType() : null;
                    methodName = ((IdentifierTree) mit.getMethodSelect()).getName().toString();
                    break;
                case MEMBER_SELECT:
                    on = info.getTrees().getTypeMirror(new TreePath(path, ((MemberSelectTree) mit.getMethodSelect()).getExpression()));
                    methodName = ((MemberSelectTree) mit.getMethodSelect()).getIdentifier().toString();
                    break;
                default:
                    throw new IllegalStateException();
            }

            if (on == null || on.getKind() != TypeKind.DECLARED) {
                return null;
            }

            return resolveMethod(info, actualTypes, (DeclaredType) on, false, false, methodName, proposed, index);
        }

        if (path.getLeaf().getKind() == Kind.NEW_CLASS) {
            List<TypeMirror> actualTypes = new LinkedList<TypeMirror>();
            NewClassTree nct = (NewClassTree) path.getLeaf();

            for (Tree a : nct.getArguments()) {
                TreePath tp = new TreePath(path, a);
                actualTypes.add(info.getTrees().getTypeMirror(tp));
            }

            TypeMirror on = info.getTrees().getTypeMirror(new TreePath(path, nct.getIdentifier()));

            if (on == null || on.getKind() != TypeKind.DECLARED) {
                return null;
            }

            return resolveMethod(info, actualTypes, (DeclaredType) on, false, true, null, proposed, index);
        }

        return null;
    }

    private static Iterable<ExecutableElement> execsIn(CompilationInfo info, TypeElement e, boolean constr, String name) {
        if (constr) {
            return ElementFilter.constructorsIn(info.getElements().getAllMembers(e));
        }

        List<ExecutableElement> result = new LinkedList<ExecutableElement>();

        for (ExecutableElement ee : ElementFilter.methodsIn(info.getElements().getAllMembers(e))) {
            if (name.equals(ee.getSimpleName().toString())) {
                result.add(ee);
            }
        }

        return result;
    }

    private static ExecutableElement resolveMethod(CompilationInfo info, List<TypeMirror> foundTypes, DeclaredType on, boolean statik, boolean constr, String name, TypeMirror[] candidateType, int[] index) {
        ExecutableElement found = null;

        OUTER:
        for (ExecutableElement ee : execsIn(info, (TypeElement) on.asElement(), constr, name)) {
            if (ee.getParameters().size() == foundTypes.size() /*XXX: variable arg count*/) {
                TypeMirror innerCandidate = null;
                int innerIndex = -1;
                ExecutableType et = (ExecutableType) info.getTypes().asMemberOf(on, ee);
                Iterator<? extends TypeMirror> formal = et.getParameterTypes().iterator();
                Iterator<? extends TypeMirror> actual = foundTypes.iterator();
                boolean mismatchFound = false;
                int i = 0;

                while (formal.hasNext() && actual.hasNext()) {
                    TypeMirror currentFormal = formal.next();
                    TypeMirror currentActual = actual.next();

                    if (!info.getTypes().isAssignable(currentActual, currentFormal)) {
                        if (mismatchFound) {
                            //only one mismatch supported:
                            continue OUTER;
                        }
                        mismatchFound = true;
                        innerCandidate = currentFormal;
                        innerIndex = i;
                    }

                    i++;
                }

                if (mismatchFound) {
                    if (candidateType[0] == null) {
                        candidateType[0] = innerCandidate;
                        index[0] = innerIndex;
                        found = ee;
                    } else {
                        //see testFuzzyResolveConstructor2:
                        if (index[0] != innerIndex || !info.getTypes().isSameType(candidateType[0], innerCandidate)) {
                            return null;
                        }
                    }
                }
            }
        }

        return found;
    }
    private static Map<FileObject, Reference<JavaSource>> scalaFileToJavaSource =
            new WeakHashMap<FileObject, Reference<JavaSource>>();
    private static Map<FileObject, Reference<CompilationInfo>> scalaFileToJavaCompilationInfo =
            new WeakHashMap<FileObject, Reference<CompilationInfo>>();

    public static CompilationInfo getCompilationInfoForScalaFile(FileObject fo) {
        Reference<CompilationInfo> infoRef = scalaFileToJavaCompilationInfo.get(fo);
        CompilationInfo info = infoRef != null ? infoRef.get() : null;

        if (info == null) {
            final CompilationInfo[] javaControllers = new CompilationInfo[1];

            JavaSource source = getJavaSourceForScalaFile(fo);
            try {
                source.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller) throws Exception {
                        controller.toPhase(Phase.ELEMENTS_RESOLVED);
                        javaControllers[0] = controller;
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            info = javaControllers[0];
            scalaFileToJavaCompilationInfo.put(fo, new WeakReference<CompilationInfo>(info));
        }

        return info;
    }

    /** 
     * @Note: We cannot create javasource via JavaSource.forFileObject(fo) here, which
     * does not support virtual source yet (only ".java" and ".class" files 
     * are supported), but we can create js via JavaSource.create(cpInfo);
     */
    private static JavaSource getJavaSourceForScalaFile(FileObject fo) {
        Reference<JavaSource> sourceRef = scalaFileToJavaSource.get(fo);
        JavaSource source = sourceRef != null ? sourceRef.get() : null;

        if (source == null) {
            ClasspathInfo javaCpInfo = ClasspathInfo.create(fo);
            source = JavaSource.create(javaCpInfo);
            scalaFileToJavaSource.put(fo, new WeakReference<JavaSource>(source));

        }

        return source;
    }

    public static String getDocComment(CompilationInfo info, final Element e) throws IOException {
        String docComment = null;
        
        // to resolve javadoc, only needs Phase.ELEMENT_RESOLVED, and we have reached when create info
        Doc javaDoc = info.getElementUtilities().javaDocFor(e);
        if (javaDoc != null) {
            docComment = javaDoc.getRawCommentText();
        }

        return docComment;
    }

    /**
     * Get fileobject that is origin source of element from current comilationInfo
     */
    public static FileObject getOriginFileObject(CompilationInfo info, Element e) {
        final ElementHandle handle = ElementHandle.create(e);
        return org.netbeans.api.java.source.SourceUtils.getFile(handle, info.getClasspathInfo());
    }

    public static int getOffset(CompilationInfo info, final Element e) throws IOException {
        final int[] offset = new int[]{-1};

        FileObject originFo = getOriginFileObject(info, e);
        if (originFo == null) {
            return -1;
        }

        /** @Note
         * We should create a element handle and a new CompilationInfo, then resolve
         * a new element from this hanlde and info
         */
        final ElementHandle handle = ElementHandle.create(e);

        JavaSource source = JavaSource.forFileObject(originFo);
        if (JavaSourceAccessor.getINSTANCE().isDispatchThread()) {
            // already under javac's lock
            CompilationInfo newInfo = JavaSourceAccessor.getINSTANCE().getCurrentCompilationInfo(source, Phase.RESOLVED);

            Element el = handle.resolve(newInfo);
            FindDeclarationVisitor v = new FindDeclarationVisitor(el, newInfo);

            CompilationUnitTree cu = newInfo.getCompilationUnit();

            v.scan(cu, null);
            Tree elTree = v.declTree;

            if (elTree != null) {
                offset[0] = (int) newInfo.getTrees().getSourcePositions().getStartPosition(cu, elTree);
            }

        } else {
            try {
                source.runUserActionTask(new Task<CompilationController>() {

                    public void run(CompilationController controller) throws Exception {
                        controller.toPhase(Phase.RESOLVED);

                        CompilationInfo newInfo = controller;
                        
                        Element el = handle.resolve(newInfo);
                        FindDeclarationVisitor v = new FindDeclarationVisitor(el, newInfo);

                        CompilationUnitTree cu = newInfo.getCompilationUnit();

                        v.scan(cu, null);
                        Tree elTree = v.declTree;

                        if (elTree != null) {
                            offset[0] = (int) newInfo.getTrees().getSourcePositions().getStartPosition(cu, elTree);
                        }
                    }
                }, true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }

        return offset[0];
    }

    // Private innerclasses ----------------------------------------------------
    private static class FindDeclarationVisitor
            extends TreePathScanner<Void, Void> {

        private Element element;
        private Tree declTree;
        private CompilationInfo info;

        public FindDeclarationVisitor(Element element, CompilationInfo info) {
            this.element = element;
            this.info = info;
        }

        @Override
        public Void visitClass(ClassTree tree, Void d) {
            handleDeclaration();
            super.visitClass(tree, d);
            return null;
        }

        @Override
        public Void visitMethod(MethodTree tree, Void d) {
            handleDeclaration();
            super.visitMethod(tree, d);
            return null;
        }

        @Override
        public Void visitVariable(VariableTree tree, Void d) {
            handleDeclaration();
            super.visitVariable(tree, d);
            return null;
        }

        public void handleDeclaration() {
            Element found = info.getTrees().getElement(getCurrentPath());

            if (element.equals(found)) {
                declTree = getCurrentPath().getLeaf();
            }
        }
    }
}
