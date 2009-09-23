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
package org.netbeans.modules.ada.editor.parser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.ada.editor.AdaMimeResolver;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.ASTUtils;
import org.netbeans.modules.ada.editor.ast.nodes.BodyDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.Modifier;
import org.netbeans.modules.csl.api.OffsetRange;
import org.netbeans.modules.csl.spi.ParserResult;

/**
 * Based on  org.netbeans.modules.php.editor.parser.GSFPHPElementHandle
 * 
 * @author Andrea Lucarelli
 */
public abstract class AdaElementHandle implements ElementHandle {

    final private ParserResult info;

    AdaElementHandle(ParserResult info) {
        this.info = info;
    }

    @Override
    public FileObject getFileObject() {
        return info.getSnapshot().getSource().getFileObject();
    }

    @Override
    public String getMimeType() {
        return AdaMimeResolver.ADA_MIME_TYPE;
    }

    // TODO what is about?
    @Override
    public String getIn() {
        return null;
    }

    @Override
    public boolean signatureEquals(ElementHandle handle) {
        // TODO needs to be done
        return false;
    }

    public abstract ASTNode getASTNode();

    @Override
    public OffsetRange getOffsetRange(ParserResult result) {
        AdaElementHandle h = (AdaElementHandle) this;

        if (h.getASTNode() != null) {
            return new OffsetRange(h.getASTNode().getStartOffset(), h.getASTNode().getEndOffset());
        } else {
            return OffsetRange.NONE;
        }
    }

    public static class PackageSpecificationHandle extends AdaElementHandle {

        private PackageSpecification declaration;

        public PackageSpecificationHandle(ParserResult info, PackageSpecification declaration) {
            super(info);
            this.declaration = declaration;
        }

        @Override
        public String getName() {
            String name = "";
            if (declaration.getName() != null) {
                name = declaration.getName().getName();
            }
            return name;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }
    }

    public static class PackageBodyHandle extends AdaElementHandle {

        private PackageBody declaration;

        public PackageBodyHandle(ParserResult info, PackageBody declaration) {
            super(info);
            this.declaration = declaration;
        }

        @Override
        public String getName() {
            String name = "";
            if (declaration.getName() != null) {
                name = declaration.getName().getName();
            }
            return name;
        }

        @Override
        public ElementKind getKind() {
            return ElementKind.CLASS;
        }

        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }
    }

    public static class TypeDeclarationHandle extends AdaElementHandle {

        private TypeDeclaration declaration;

        public TypeDeclarationHandle (ParserResult info, TypeDeclaration declaration) {
            super (info);
            this.declaration = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }

        public String getName() {
            String name = "";
            Identifier id = declaration.getTypeName();
            String hName = id.getName();
            if (hName != null) {
                name = name + hName;
            }
            return name;
        }

        public ElementKind getKind() {
            // Custom icon
            return ElementKind.FIELD;
        }

        public Set<Modifier> getModifiers() {
            return translateModifiers(declaration.getModifier());
        }
    }

    public static class FieldsDeclarationHandle extends AdaElementHandle {

        private FieldsDeclaration declaration;

        public FieldsDeclarationHandle (ParserResult info, FieldsDeclaration declaration) {
            super (info);
            this.declaration = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }

        public String getName() {
            String name = "";
            Variable[] variables = declaration.getVariableNames();
            for (Variable variable : variables) {
                String hName = ASTUtils.resolveVariableName(variable);
                if (hName != null) {
                    name = name + hName;
                }
            }
            return name;
        }

        public ElementKind getKind() {
            return ElementKind.FIELD;
        }

        public Set<Modifier> getModifiers() {
            return translateModifiers(declaration.getModifier());
        }
    }

    public static class SubprogramSpecificationHandle extends AdaElementHandle {

        private SubprogramSpecification suprog;

        public SubprogramSpecificationHandle (ParserResult info, SubprogramSpecification declaration) {
            super (info);
            this.suprog = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return suprog;
        }

        public String getName() {
            String name = "";
            if (suprog.getSubprogramName() != null) {
                name = suprog.getSubprogramName().getName();
            }
            return name;
        }

        public ElementKind getKind() {
            return ElementKind.METHOD;
        }

        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }
    }

    public static class SubprogramBodyHandle extends AdaElementHandle {

        private SubprogramBody declaration;

        public SubprogramBodyHandle (ParserResult info, SubprogramBody declaration) {
            super (info);
            this.declaration = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }

        public String getName() {
            String name = "";
            if (declaration.getSubprogramSpecification().getSubprogramName() != null) {
                name = declaration.getSubprogramSpecification().getSubprogramName().getName();
            }
            return name;
        }

        public ElementKind getKind() {
            return ElementKind.METHOD;
        }

        public Set<Modifier> getModifiers() {
            return Collections.emptySet();
        }
    }

    public static class MethodSubprogSpecHandle extends SubprogramSpecificationHandle {

        private MethodDeclaration declaration;

        public MethodSubprogSpecHandle (ParserResult info, MethodDeclaration declaration) {
            super (info, declaration.getSubprogramSpecification());
            this.declaration = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return translateModifiers(declaration.getModifier());
        }
    }

    public static class MethodSubprogBodyHandle extends SubprogramBodyHandle {

        private MethodDeclaration declaration;

        public MethodSubprogBodyHandle (ParserResult info, MethodDeclaration declaration) {
            super (info, declaration.getSubprogramBody());
            this.declaration = declaration;
        }

        @Override
        public ASTNode getASTNode() {
            return declaration;
        }

        @Override
        public Set<Modifier> getModifiers() {
            return translateModifiers(declaration.getModifier());
        }
    }

    private static Set<Modifier> translateModifiers(int modifier) {
        Set<Modifier> modifiers = new HashSet<Modifier>();
        if (BodyDeclaration.Modifier.isPrivate(modifier)) {
            modifiers.add(Modifier.PRIVATE);
        } else if (BodyDeclaration.Modifier.isPublic(modifier)) {
            modifiers.add(Modifier.PUBLIC);
        }
        return modifiers;
    }
}
