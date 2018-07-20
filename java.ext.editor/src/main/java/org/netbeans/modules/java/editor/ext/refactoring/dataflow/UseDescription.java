/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.editor.ext.refactoring.dataflow;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import java.io.CharConversionException;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.ElementHandle;
import org.netbeans.api.java.source.TreePathHandle;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;

public final class UseDescription {

    public final String displayName;
    public final TreePathHandle tph;
    public final boolean localOnly;
    //TODO: scoping?
    public final boolean leaf;
    public final ElementHandle<ExecutableElement> referencedMethod;
    public final int index;
    public final ElementKind referencedKind;
    public final ElementHandle<TypeElement> parent;
    public final int offset;

    private UseDescription(String displayName, TreePathHandle tph, boolean localOnly, boolean leaf, ElementHandle<ExecutableElement> referencedMethod, int index, ElementKind referencedKind, ElementHandle<TypeElement> parent, int offset) {
        this.displayName = displayName;
        this.tph = tph;
        this.localOnly = localOnly;
        this.leaf = leaf;
        this.referencedMethod = referencedMethod;
        this.index = index;
        this.referencedKind = referencedKind;
        this.parent = parent;
        this.offset = offset;
    }

    String toDebugString(CompilationInfo info) {
        return displayName + ":" + leaf;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UseDescription other = (UseDescription) obj;
        if (this.tph != other.tph && (this.tph == null || !this.tph.equals(other.tph))) {
            return false;
        }
        //XXX: TreePathHandle.equals will check only FileObject and position, but that may the equivalent for several TreePath(Handle)s
        if (!this.displayName.equals(other.displayName)) {
            return false;
        }
        if (offset != other.offset) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.tph != null ? this.tph.hashCode() : 0);
        hash = 19 * hash + displayName.hashCode();
        hash = 19 * hash + offset;
        return hash;
    }

    public static UseDescription create(CompilationInfo info, TreePath tp) {
        boolean local;
        boolean leaf;
        Element el = info.getTrees().getElement(tp);
        ElementHandle<ExecutableElement> referencedMethod;
        int index = 0;
        ElementKind referencedKind;
        ElementHandle<TypeElement> parent;
        String displayName = null;
        if (el != null && SEARCHABLE.contains(referencedKind = el.getKind())) {
            local = referencedKind == ElementKind.EXCEPTION_PARAMETER || referencedKind == ElementKind.LOCAL_VARIABLE;
            if (referencedKind == ElementKind.PARAMETER) {
                if (tp.getParentPath().getLeaf().getKind() == Kind.METHOD) {
                    displayName = methodDeclaration(tp.getParentPath(), (VariableTree) tp.getLeaf());
                    //private method, etc.
                } else {
                    local = true;
                }
            }

            //compromise: private fields, final fields are considered local only:
            //TODO: enum constants?
            if (referencedKind == ElementKind.FIELD || referencedKind == ElementKind.ENUM_CONSTANT) {
                if (el.getModifiers().contains(Modifier.PRIVATE) || el.getModifiers().contains(Modifier.FINAL)) {
                    local = true;
                }
            }
            leaf = false;

            if (referencedKind == ElementKind.PARAMETER) {
                ExecutableElement method = (ExecutableElement) el.getEnclosingElement();
                
                referencedMethod = ElementHandle.create(method);

                for (VariableElement ve : method.getParameters()) {
                    if (ve.equals(el)) {
                        break;
                    }

                    index++;
                }
            } else {
                referencedMethod = null;
            }

            parent = ElementHandle.create(info.getElementUtilities().enclosingTypeElement(el));
        } else {
            local = true;
            leaf = true;
            referencedMethod = null;
            referencedKind = null;
            parent = null;
        }

        try {
        if (displayName == null) {
            LineMap lm = info.getCompilationUnit().getLineMap();
            int start = (int) info.getTrees().getSourcePositions().getStartPosition(tp.getCompilationUnit(), tp.getLeaf());
            int end   = (int) info.getTrees().getSourcePositions().getEndPosition(tp.getCompilationUnit(), tp.getLeaf());
            int lineStart = (int) lm.getStartPosition(lm.getLineNumber(start));
            int lineEnd   = (int) lm.getStartPosition(lm.getLineNumber(end) + 1);
            String line = info.getText().substring(lineStart, lineEnd);

            displayName = XMLUtil.toElementContent(line.substring(0, start - lineStart)) + "<b>" + XMLUtil.toElementContent(line.substring(start - lineStart, end - lineStart)) + "</b>" + XMLUtil.toElementContent(line.substring(end - lineStart));

            displayName = displayName.trim().replaceAll("[ \t\n]+", " ");
        }
        } catch (CharConversionException ex) {
            Exceptions.printStackTrace(ex);
            displayName = "";
        }

        int offset = (int) info.getTrees().getSourcePositions().getStartPosition(tp.getCompilationUnit(), tp.getLeaf());

        return new UseDescription(displayName, TreePathHandle.create(tp, info), local, leaf, referencedMethod, index, referencedKind, parent, offset);
    }

    private static String methodDeclaration(TreePath methodPath, VariableTree param) {
        MethodTree mt = (MethodTree) methodPath.getLeaf();
        StringBuilder result = new StringBuilder();

        if (isConstructor(methodPath)) {
            result.append(((ClassTree) methodPath.getParentPath().getLeaf()).getSimpleName());
        } else {
            result.append(mt.getName());
        }

        result.append('(');

        boolean first = true;

        for (VariableTree p : mt.getParameters()) {
            if (p == param) {
                result.append("<b>");
            }

            if (!first) {
                result.append(", ");
            }

            first = false;
            
            try {
                result.append(XMLUtil.toElementContent(p.toString()));
            } catch (CharConversionException ex) {
                Exceptions.printStackTrace(ex);
                return "";
            }
            
            if (p == param) {
                result.append("</b>");
            }
        }

        result.append(')');

        return result.toString();
    }

    private static String methodInvocation(MethodInvocationTree mit, ExpressionTree param) {
        StringBuilder result = new StringBuilder();

        result.append(mit.getMethodSelect().toString()); //TODO
        result.append('(');

        boolean first = true;

        for (ExpressionTree p : mit.getArguments()) {
            if (p == param) {
                result.append("<b>");
            }

            if (!first) {
                result.append(", ");
            }

            first = false;

            result.append(p.toString()); //TODO

            if (p == param) {
                result.append("</b>");
            }
        }

        result.append(')');

        return result.toString();
    }


    private static final Set<ElementKind> SEARCHABLE = EnumSet.of(
            ElementKind.LOCAL_VARIABLE, ElementKind.EXCEPTION_PARAMETER, ElementKind.PARAMETER,
            ElementKind.ENUM_CONSTANT, ElementKind.FIELD);

    public static boolean isConstructor(TreePath method) {
        MethodTree mt = (MethodTree) method.getLeaf();

        return mt.getReturnType() == null;
    }
    
}
