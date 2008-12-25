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
package org.netbeans.modules.ada.editor.navigator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.FunctionDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.ProcedureDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement.Kind;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.PackageElement;
import org.netbeans.modules.gsf.api.ColoringAttributes;
import org.netbeans.modules.gsf.api.CompilationInfo;
import org.netbeans.modules.gsf.api.OccurrencesFinder;
import org.netbeans.modules.gsf.api.OffsetRange;

/**
 * Based on org.netbeans.modules.php.editor.nav.OccurrencesFinderImpl
 *
 * @author Andrea Lucarelli
 */
public class AdaOccurrencesFinder implements OccurrencesFinder {

    private int offset;
    private Map<OffsetRange, ColoringAttributes> range2Attribs;

    public void setCaretPosition(int position) {
        this.offset = position;
        this.range2Attribs = new HashMap<OffsetRange, ColoringAttributes>();
    }

    public Map<OffsetRange, ColoringAttributes> getOccurrences() {
        return range2Attribs;
    }

    public void cancel() {
    }

    public void run(CompilationInfo parameter) throws Exception {
        for (OffsetRange r : compute(parameter, offset)) {
            range2Attribs.put(r, ColoringAttributes.MARK_OCCURRENCES);
        }
    }

    static Collection<OffsetRange> compute(final CompilationInfo parameter, final int offset) {
        final List<OffsetRange> result = new LinkedList<OffsetRange>();
        final List<ASTNode> path = NavUtils.underCaret(parameter, offset);
        final SemiAttribute a = SemiAttribute.semiAttribute(parameter);
        final AttributedElement el = NavUtils.findElement(parameter, path, offset, a);

        if (el == null) {
            return result;
        }
        Identifier id = null;
        Collections.reverse(path);
        for (ASTNode astNode : path) {
            if (astNode instanceof Identifier) {
                Identifier identifier = (Identifier) astNode;
                if (identifier != null) {
                    id = identifier;
                }
            }
        }

        final Identifier identifier = id;
        final List<ASTNode> usages = new LinkedList<ASTNode>();
        final List<ASTNode> memberDeclaration = new LinkedList<ASTNode>();

        new DefaultVisitor() {

            private String pkgName = null;

            @Override
            public void visit(MethodDeclaration node) {
                boolean found = false;
                if (el instanceof SemiAttribute.PackageMemberElement) {
                    SemiAttribute.PackageMemberElement clsEl = (SemiAttribute.PackageMemberElement) el;
                    String methName = CodeUtils.extractMethodName(node);
                    Identifier methNode;
                    if (node.getKind() == MethodDeclaration.Kind.FUNCTION) {
                        methNode = node.getFunction().getFunctionName();
                    } else {
                        methNode = node.getProcedure().getProcedureName();
                    }

                    if (pkgName != null && clsEl.getPackageName().equals(pkgName) && clsEl.getName().equals(methName)) {
                        memberDeclaration.add(methNode);
                        usages.add(methNode);
                        found = true;
                    }
                    PackageElement superClass = clsEl.getPackageElement().getSuperClass();
                    while(!found && superClass != null) {
                        if (superClass != null && pkgName != null && superClass.getName().equals(pkgName) && clsEl.getName().equals(methName)) {
                            memberDeclaration.add(0, methNode);
                            usages.add(methNode);
                            found = true;
                        }
                        superClass = superClass.getSuperClass();
                    }
                }
                if (!found) {
                    super.visit(node);
                }
            }

            @Override
            public void visit(TypeDeclaration node) {
                boolean found = false;
                if (el instanceof SemiAttribute.PackageMemberElement) {
                    SemiAttribute.PackageMemberElement pkgEl = (SemiAttribute.PackageMemberElement) el;
                    Identifier id = node.getTypeName();
                    String typeName = id.getName();
                    if (pkgName != null && pkgEl.getPackageName().equals(pkgName) && pkgEl.getName().equals(typeName)) {
                        memberDeclaration.add(id);
                        usages.add(id);
                        found = true;
                    }
                }
                if (!found) {
                    super.visit(node);
                }
            }

            @Override
            public void visit(SingleFieldDeclaration node) {
                boolean found = false;
                if (el instanceof SemiAttribute.PackageMemberElement) {
                    SemiAttribute.PackageMemberElement pkgEl = (SemiAttribute.PackageMemberElement) el;
                    Variable variable = node.getName();
                    String varName = CodeUtils.extractVariableName(variable);
                    if (pkgName != null && pkgEl.getPackageName().equals(pkgName) && pkgEl.getName().equals(varName)) {
                        memberDeclaration.add(variable);
                        usages.add(variable);
                        found = true;
                    }
                }
                if (!found) {
                    super.visit(node);
                }
            }

            @Override
            public void visit(FunctionDeclaration node) {
                if (!(el instanceof SemiAttribute.PackageMemberElement)) {
                    if (el == a.getElement(node)) {
                        usages.add(node.getFunctionName());
                    }
                }
                super.visit(node);
            }

            @Override
            public void visit(ProcedureDeclaration node) {
                if (!(el instanceof SemiAttribute.PackageMemberElement)) {
                    if (el == a.getElement(node)) {
                        usages.add(node.getProcedureName());
                    }
                }
                super.visit(node);
            }

            @Override
            public void visit(PackageSpecification node) {
                if (el == a.getElement(node)) {
                    usages.add(node.getName());
                    if (node.getNameEnd().getName() != null) {
                        usages.add(node.getNameEnd());
                    }
                }
                pkgName = CodeUtils.extractPackageName(node);
                super.visit(node);
                /*
                 * do not mark two method decl., if happens then remove
                 * the superclass method.
                 */
                while (memberDeclaration.size() > 1) {
                    usages.remove(memberDeclaration.remove(0));
                }
            }

            @Override
            public void visit(PackageBody node) {
                if (el == a.getElement(node)) {
                    usages.add(node.getName());
                    if (node.getNameEnd().getName() != null) {
                        usages.add(node.getNameEnd());
                    }
                }
                pkgName = CodeUtils.extractPackageName(node);
                super.visit(node);
                /*
                 * do not mark two method decl., if happens then remove
                 * the superclass method.
                 */
                while (memberDeclaration.size() > 1) {
                    usages.remove(memberDeclaration.remove(0));
                }
            }

            @Override
            public void visit(Variable node) {
                if (el == a.getElement(node)) {
                    usages.add(node);
                }
                super.visit(node);
            }
        }.scan(ASTUtils.getRoot(parameter));

        for (ASTNode n : usages) {
            OffsetRange forNode = forNode(n, el.getKind());
            if (forNode != null) {
                result.add(forNode);
            }
        }

        return result;
    }

    private static OffsetRange forNode(ASTNode n, Kind kind) {
        return new OffsetRange(n.getStartOffset(), n.getEndOffset());
    }
}
