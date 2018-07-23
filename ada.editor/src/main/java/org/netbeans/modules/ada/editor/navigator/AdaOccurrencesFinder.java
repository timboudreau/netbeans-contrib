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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Scalar;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.TypeName;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.DefaultVisitor;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement;
import org.netbeans.modules.ada.editor.navigator.SemiAttribute.AttributedElement.Kind;
import org.netbeans.modules.csl.api.ColoringAttributes;
import org.netbeans.modules.csl.api.OccurrencesFinder;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.GsfUtilities;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.spi.Parser.Result;

/**
 * Based on org.netbeans.modules.php.editor.nav.OccurrencesFinderImpl
 *
 * @author Andrea Lucarelli
 */
public class AdaOccurrencesFinder extends OccurrencesFinder {

    private static final Logger LOGGER = Logger.getLogger(AdaOccurrencesFinder.class.getName());
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

    public void run(Result result, SchedulerEvent event) {
        for (OffsetRange r : compute((ParserResult) result, GsfUtilities.getLastKnownCaretOffset(result.getSnapshot(), event))) {
            range2Attribs.put(r, ColoringAttributes.MARK_OCCURRENCES);
        }
    }

    static Collection<OffsetRange> compute(final ParserResult parameter, final int offset) {
        final List<OffsetRange> result = new LinkedList<OffsetRange>();
        final List<ASTNode> path = NavUtils.underCaret(parameter, offset);
        final SemiAttribute attribute = SemiAttribute.semiAttribute(parameter);
        final AttributedElement element = NavUtils.findElement(parameter, path, offset, attribute);

        if (element == null) {
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

        LOGGER.setLevel(Level.FINE);

        new DefaultVisitor() {

            private String pkgName = null;

            @Override
            public void visit(MethodDeclaration node) {
                boolean found = false;
                if (element instanceof SemiAttribute.PackageMemberElement) {
                    SemiAttribute.PackageMemberElement pkgEl = (SemiAttribute.PackageMemberElement) element;
                    String methName = CodeUtils.extractMethodName(node);
                    Identifier methNode = node.getSubrogramName();

                    if (pkgName != null && pkgEl.getPackageName().equals(pkgName) && pkgEl.getName().equals(methName)) {
                        memberDeclaration.add(methNode);
                        usages.add(methNode);
                        if (node.getMethodName() != null) {
                            usages.add(node.getSubrogramNameEnd());
                        }
                        found = true;
                    }
                }
                if (!found) {
                    super.visit(node);
                }
            }

            @Override
            public void visit(TypeDeclaration node) {
                LOGGER.fine("called visist(TypeDeclaration): " + node.getTypeName().getName());
                boolean found = false;
                if (element instanceof SemiAttribute.PackageMemberElement) {
                    LOGGER.fine("package element: " + node.getTypeName().getName());
                    SemiAttribute.PackageMemberElement pkgEl = (SemiAttribute.PackageMemberElement) element;
                    Identifier type = node.getTypeName();
                    String typeName = type.getName();
                    if (pkgName != null && pkgEl.getPackageName().equals(pkgName) && pkgEl.getName().equals(typeName)) {
                        LOGGER.fine("if: " + node.getTypeName().getName());
                        memberDeclaration.add(type);
                        usages.add(type);
                        found = true;
                    }
                }
                if (!found) {
                    super.visit(node);
                }
            }

            @Override
            public void visit(SingleFieldDeclaration node) {
                LOGGER.fine("called visist(SingleFieldDeclaration): " + CodeUtils.extractVariableName(node.getName()));
                boolean found = false;
                if (element instanceof SemiAttribute.PackageMemberElement) {
                    LOGGER.fine("package element: " + CodeUtils.extractVariableName(node.getName()));
                    SemiAttribute.PackageMemberElement pkgEl = (SemiAttribute.PackageMemberElement) element;
                    Variable variable = node.getName();
                    String varName = CodeUtils.extractVariableName(variable);
                    if (pkgName != null && pkgEl.getPackageName().equals(pkgName) && pkgEl.getName().equals(varName)) {
                        LOGGER.fine("if: " + CodeUtils.extractVariableName(node.getName()));
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
            public void visit(SubprogramSpecification node) {
                if (!(element instanceof SemiAttribute.PackageMemberElement)) {
                    if (element == attribute.getElement(node)) {
                        usages.add(node.getSubprogramName());
                    }
                }
                super.visit(node);
            }

            @Override
            public void visit(SubprogramBody node) {
                if (!(element instanceof SemiAttribute.PackageMemberElement)) {
                    if (element == attribute.getElement(node)) {
                        usages.add(node.getSubprogramSpecification().getSubprogramName());
                        if (node.getSubprogramNameEnd().getName() != null) {
                            usages.add(node.getSubprogramNameEnd());
                        }
                    }
                }
                super.visit(node);
            }

            @Override
            public void visit(PackageSpecification node) {
                if (element == attribute.getElement(node)) {
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
                if (element == attribute.getElement(node)) {
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
            public void visit(FormalParameter node) {
                Variable parameterName = node.getParameterName();
                if (parameterName != null) {
                    String name = parameterName.getName().getName();
                    if (name != null && element == attribute.getElement(parameterName)) {
                        usages.add(parameterName);
                    }
                }
                TypeName parameterType = node.getParameterType();
                if (parameterType != null) {
                    String name = parameterType.getTypeName().getName();
                    if (name != null && element == attribute.getElement(parameterType)) {
                        usages.add(parameterType);
                    }
                }
                super.visit(node);
            }

            @Override
            public void visit(Variable node) {
                if (element == attribute.getElement(node)) {
                    usages.add(node);
                }
                super.visit(node);
            }

            @Override
            public void visit(TypeName node) {
                if (element == attribute.getElement(node)) {
                    usages.add(node);
                }
                super.visit(node);
            }

            @Override
            public void visit(Scalar scalar) {
                if (element == attribute.getElement(scalar)) {
                    usages.add(scalar);
                }
                super.visit(scalar);
            }
        }.scan(ASTUtils.getRoot(parameter));

        for (ASTNode n : usages) {
            OffsetRange forNode = forNode(n, element.getKind());
            if (forNode != null) {
                LOGGER.fine("usage item: " + forNode);
                result.add(forNode);
            }
        }

        return result;
    }

    private static OffsetRange forNode(ASTNode n, Kind kind) {
        OffsetRange retval = null;
        if (n instanceof Scalar && ((Scalar) n).getScalarType() == Scalar.Type.STRING && NavUtils.isQuoted(((Scalar) n).getStringValue())) {
            retval = new OffsetRange(n.getStartOffset(), n.getEndOffset());
        } else if (n instanceof Variable) {
            retval = new OffsetRange(n.getStartOffset(), n.getEndOffset());
        } else if (n != null) {
            retval = new OffsetRange(n.getStartOffset(), n.getEndOffset());
        }
        return retval;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.CURSOR_SENSITIVE_TASK_SCHEDULER;
    }
}
