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

package org.netbeans.modules.javafx.editor.completion.environment;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javafx.api.JavafxcTrees;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.JavaFXSource.Phase;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionEnvironment;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionItem;
import org.netbeans.modules.javafx.editor.completion.JavaFXCompletionProvider;
import static org.netbeans.modules.javafx.editor.completion.JavaFXCompletionQuery.*;
import static javax.lang.model.element.ElementKind.*;

/**
 *
 * @author David Strupl
 */
public class MemberSelectTreeEnvironment extends JavaFXCompletionEnvironment<MemberSelectTree> {
    
    private static final Logger logger = Logger.getLogger(MemberSelectTreeEnvironment.class.getName());
    private static final boolean LOGGABLE = logger.isLoggable(Level.FINE);

    @Override
    protected void inside(MemberSelectTree fa) throws IOException {
        log("inside MemberSelectTree " + fa);
            int expEndPos = (int)sourcePositions.getEndPosition(root, fa.getExpression());
            boolean afterDot = false;
            boolean afterLt = false;
            int openLtNum = 0;
            JFXTokenId lastNonWhitespaceTokenId = null;
            TokenSequence<JFXTokenId> ts = controller.getTokenHierarchy().tokenSequence(JFXTokenId.language());
            ts.move(expEndPos);
            while (ts.moveNext()) {
                if (ts.offset() >= offset) {
                    break;
                }
                switch (ts.token().id()) {
                    case DECIMAL_LITERAL:
                        if (ts.offset() != expEndPos || ts.token().text().charAt(0) != '.')
                            break;
                    case DOT:
                        afterDot = true;
                        break;
                    case LT:
                        afterLt = true;
                        openLtNum++;
                        break;
                    case GT:
                        openLtNum--;
                        break;
                }
                switch (ts.token().id()) {
                    case WS:
                    case LINE_COMMENT:
                    case COMMENT:
                    case DOC_COMMENT:
                        break;
                    default:
                        lastNonWhitespaceTokenId = ts.token().id();
                }
            }
            if (!afterDot) {
                if (expEndPos <= offset)
                    insideExpression(new TreePath(path, fa.getExpression()));
                return;
            }
        
            if (lastNonWhitespaceTokenId != JFXTokenId.STAR) {
                controller.toPhase(Phase.ANALYZED);
                TreePath parentPath = path.getParentPath();
                Tree parent = parentPath != null ? parentPath.getLeaf() : null;
                TreePath grandParentPath = parentPath != null ? parentPath.getParentPath() : null;
                Tree grandParent = grandParentPath != null ? grandParentPath.getLeaf() : null;
                ExpressionTree exp = fa.getExpression();
                TreePath expPath = new TreePath(path, exp);
                TypeMirror type = controller.getTrees().getTypeMirror(expPath);
                if (type != null) {
                    EnumSet<ElementKind> kinds;
                    DeclaredType baseType = null;
                    Set<TypeMirror> exs = null;
                    boolean inImport = false;
                    boolean insideNew = false;
                    if (parent.getKind() == Tree.Kind.CLASS && ((ClassTree)parent).getExtendsClause() == fa) {
                        kinds = EnumSet.of(CLASS);
                    } else if (parent.getKind() == Tree.Kind.CLASS && ((ClassTree)parent).getImplementsClause().contains(fa)) {
                        kinds = EnumSet.of(INTERFACE);
                    } else if (parent.getKind() == Tree.Kind.IMPORT) {
                        inImport = true;
                        kinds = ((ImportTree)parent).isStatic() ? EnumSet.of(CLASS, ENUM, INTERFACE, ANNOTATION_TYPE, FIELD, METHOD, ENUM_CONSTANT) : EnumSet.of(CLASS, ANNOTATION_TYPE, ENUM, INTERFACE);
                    } else if (parent.getKind() == Tree.Kind.NEW_CLASS && ((NewClassTree)parent).getIdentifier() == fa) {
                        insideNew = true;
                        kinds = EnumSet.of(CLASS, INTERFACE, ANNOTATION_TYPE);
                        if (grandParent.getKind() == Tree.Kind.THROW)
                            baseType = controller.getTypes().getDeclaredType(controller.getElements().getTypeElement("java.lang.Throwable")); //NOI18N
                    } else if (parent.getKind() == Tree.Kind.PARAMETERIZED_TYPE && ((ParameterizedTypeTree)parent).getTypeArguments().contains(fa)) {
                        kinds = EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE);
                    } else if (parent.getKind() == Tree.Kind.ANNOTATION) {
                        if (((AnnotationTree)parent).getAnnotationType() == fa) {
                            kinds = EnumSet.of(ANNOTATION_TYPE);
                        } else {
                            Iterator<? extends ExpressionTree> it = ((AnnotationTree)parent).getArguments().iterator();
                            if (it.hasNext()) {
                                ExpressionTree et = it.next();
                                if (et == fa || (et.getKind() == Tree.Kind.ASSIGNMENT && ((AssignmentTree)et).getExpression() == fa)) {
                                    Element el = controller.getTrees().getElement(expPath);
                                    if (type.getKind() == TypeKind.ERROR && el.getKind().isClass()) {
                                        el = controller.getElements().getPackageElement(((TypeElement)el).getQualifiedName());
                                    }
                                    if (el instanceof PackageElement)
                                        addPackageContent((PackageElement)el, EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE), null, false);
                                    else if (type.getKind() == TypeKind.DECLARED)
                                        addMemberConstantsAndTypes((DeclaredType)type, el);
                                    return;
                                }
                            }
                            kinds = EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE, FIELD, METHOD, ENUM_CONSTANT);
                        }
                    } else if (parent.getKind() == Tree.Kind.ASSIGNMENT && ((AssignmentTree)parent).getExpression() == fa && grandParent != null && grandParent.getKind() == Tree.Kind.ANNOTATION) {
                        Element el = controller.getTrees().getElement(expPath);
                        if (type.getKind() == TypeKind.ERROR && el.getKind().isClass()) {
                            el = controller.getElements().getPackageElement(((TypeElement)el).getQualifiedName());
                        }
                        if (el instanceof PackageElement)
                            addPackageContent((PackageElement)el, EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE), null, false);
                        else if (type.getKind() == TypeKind.DECLARED)
                            addMemberConstantsAndTypes((DeclaredType)type, el);
                        return;
                    } else if (parent.getKind() == Tree.Kind.VARIABLE && ((VariableTree)parent).getType() == fa && grandParent.getKind() == Tree.Kind.CATCH) {
                        if (query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE) {
                            // TODO:
//                            exs = controller.getTreeUtilities().getUncaughtExceptions(grandParentPath.getParentPath());
                        }
                        kinds = EnumSet.of(CLASS, INTERFACE);
                        baseType = controller.getTypes().getDeclaredType(controller.getElements().getTypeElement("java.lang.Throwable")); //NOI18N
                    } else if (parent.getKind() == Tree.Kind.METHOD && ((MethodTree)parent).getThrows().contains(fa)) {
                        Types types = controller.getTypes();
                        if (query.queryType == JavaFXCompletionProvider.COMPLETION_QUERY_TYPE && ((MethodTree)parent).getBody() != null) {
                            controller.toPhase(Phase.RESOLVED);
                            // TODO:
                            // exs = controller.getTreeUtilities().getUncaughtExceptions(new TreePath(path, ((MethodTree)parent).getBody()));
                            JavafxcTrees trees = controller.getTrees();
                            for (ExpressionTree thr : ((MethodTree)parent).getThrows()) {
                                if (sourcePositions.getEndPosition(root, thr) >= offset)
                                    break;
                                TypeMirror t = trees.getTypeMirror(new TreePath(path, thr));
                                for (Iterator<TypeMirror> it = exs.iterator(); it.hasNext();)
                                    if (types.isSubtype(it.next(), t))
                                        it.remove();
                            }
                        }
                        kinds = EnumSet.of(CLASS, INTERFACE);
                        baseType = controller.getTypes().getDeclaredType(controller.getElements().getTypeElement("java.lang.Throwable")); //NOI18N
                    } else if (parent.getKind() == Tree.Kind.METHOD && ((MethodTree)parent).getDefaultValue() == fa) {
                        Element el = controller.getTrees().getElement(expPath);
                        if (type.getKind() == TypeKind.ERROR && el.getKind().isClass()) {
                            el = controller.getElements().getPackageElement(((TypeElement)el).getQualifiedName());
                        }
                        if (el instanceof PackageElement)
                            addPackageContent((PackageElement)el, EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE), null, false);
                        else if (type.getKind() == TypeKind.DECLARED)
                            addMemberConstantsAndTypes((DeclaredType)type, el);
                        return;
                    } else if (afterLt) {
                        kinds = EnumSet.of(METHOD);
                    } else if (parent.getKind() == Tree.Kind.ENHANCED_FOR_LOOP && ((EnhancedForLoopTree)parent).getExpression() == fa) {
                        insideForEachExpressiion();
                        kinds = EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE, FIELD, METHOD, ENUM_CONSTANT);
                    } else {
                        kinds = EnumSet.of(CLASS, ENUM, ANNOTATION_TYPE, INTERFACE, FIELD, METHOD, ENUM_CONSTANT);
                    }
                    switch (type.getKind()) {
                        case TYPEVAR:
                            while(type != null && type.getKind() == TypeKind.TYPEVAR)
                                type = ((TypeVariable)type).getUpperBound();
                            if (type == null)
                                return;
                            type = controller.getTypes().capture(type);
                        case ARRAY:
                        case DECLARED:
                        case BOOLEAN:
                        case BYTE:
                        case CHAR:
                        case DOUBLE:
                        case FLOAT:
                        case INT:
                        case LONG:
                        case SHORT:
                        case VOID:
                            boolean b = exp.getKind() == Tree.Kind.PARENTHESIZED || exp.getKind() == Tree.Kind.TYPE_CAST;
                            while(b) {
                                if (exp.getKind() == Tree.Kind.PARENTHESIZED) {
                                    exp = ((ParenthesizedTree)exp).getExpression();
                                    expPath = new TreePath(expPath, exp);
                                } else if (exp.getKind() == Tree.Kind.TYPE_CAST) {
                                    exp = ((TypeCastTree)exp).getExpression();
                                    expPath = new TreePath(expPath, exp);
                                } else {
                                    b = false;
                                }
                            }
                            Element el = controller.getTrees().getElement(expPath);
                            if (el != null && (el.getKind().isClass() || el.getKind().isInterface())) {
                                if (parent.getKind() == Tree.Kind.NEW_CLASS && ((NewClassTree)parent).getIdentifier() == fa && prefix != null) {
                                    String typeName = el.toString() + "." + prefix; //NOI18N
                                    log("NOT IMPLEMENTED: handling members of " + typeName);
//                                    TypeMirror tm = controller.getTreeUtilities().parseType(typeName, getScope().getEnclosingClass());
//                                    if (tm != null && tm.getKind() == TypeKind.DECLARED)
//                                        addMembers(tm, ((DeclaredType)tm).asElement(), EnumSet.of(CONSTRUCTOR), null, inImport, insideNew);
                                }
                            }
                            if (exs != null) {
                                Elements elements = controller.getElements();
                                for (TypeMirror ex : exs)
                                    if (ex.getKind() == TypeKind.DECLARED) {
                                        Element e = ((DeclaredType)ex).asElement();
                                        if (e.getEnclosingElement() == el && JavaFXCompletionProvider.startsWith(e.getSimpleName().toString(), prefix)) { 
                                            addResult(JavaFXCompletionItem.createTypeItem((TypeElement)e, (DeclaredType)ex, getOffset(), elements.isDeprecated(e), insideNew, true));
                                        }
                                    }
                            } else {
                                addMembers(type);
                            }
                            break;
                        default:
                            el = controller.getTrees().getElement(expPath);
                            if (type.getKind() == TypeKind.ERROR && el != null && el.getKind().isClass()) {
                                el = controller.getElements().getPackageElement(((TypeElement)el).getQualifiedName());
                            }
                            if (el != null && el.getKind() == PACKAGE) {                                
                                if (parent.getKind() == Tree.Kind.NEW_CLASS && ((NewClassTree)parent).getIdentifier() == fa && prefix != null) {
                                    String typeName = el + "." + prefix; //NOI18N
                                    log("NOT IMPLEMENTED: handling members of " + typeName);
//                                    TypeMirror tm = controller.getTreeUtilities().parseType(typeName, getScope().getEnclosingClass());
//                                    if (tm != null && tm.getKind() == TypeKind.DECLARED)
//                                        addMembers(tm, ((DeclaredType)tm).asElement(), EnumSet.of(CONSTRUCTOR), null, inImport, insideNew);
                                }
                                if (exs != null) {
                                    Elements elements = controller.getElements();
                                    for (TypeMirror ex : exs)
                                        if (ex.getKind() == TypeKind.DECLARED) {
                                            Element e = ((DeclaredType)ex).asElement();
                                            if (e.getEnclosingElement() == el && JavaFXCompletionProvider.startsWith(e.getSimpleName().toString(), prefix) ) {
                                                addResult(JavaFXCompletionItem.createTypeItem((TypeElement)e, (DeclaredType)ex, getOffset(), elements.isDeprecated(e), false, true));
                                            }
                                        }
                                } else {
                                    addPackageContent((PackageElement)el, kinds, baseType, insideNew);
                                }
//                                if (query.results.isEmpty() && ((PackageElement)el).getQualifiedName() == el.getSimpleName()) {
                                    // no package content? Check for unimported class
//                                    ClassIndex ci = controller.getClasspathInfo().getClassIndex();
//                                    if (el.getEnclosedElements().isEmpty() && ci.getPackageNames(el.getSimpleName() + ".", true, EnumSet.allOf(ClassIndex.SearchScope.class)).isEmpty()) {
//                                        Trees trees = controller.getTrees();
//                                        Scope scope = getScope();
//                                        for (ElementHandle<TypeElement> teHandle : ci.getDeclaredTypes(el.getSimpleName().toString(), ClassIndex.NameKind.SIMPLE_NAME, EnumSet.allOf(ClassIndex.SearchScope.class))) {
//                                            TypeElement te = teHandle.resolve(controller);
//                                            if (te != null && trees.isAccessible(scope, te))
//                                                addMembers(te.asType(), te, kinds, baseType, inImport, insideNew);
//                                        }
//                                    }
//                                }
                            }
                    }
                } else if (parent.getKind() == Tree.Kind.COMPILATION_UNIT && ((CompilationUnitTree)parent).getPackageName() == fa) {
                    PackageElement pe = controller.getElements().getPackageElement(fullName(exp));
                    if (pe != null)
                        addPackageContent(pe, EnumSet.of(ElementKind.PACKAGE), null, false);
                }
            }
    }

    private static void log(String s) {
        if (LOGGABLE) {
            logger.fine(s);
        }
    }
}
