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
package org.netbeans.modules.ada.editor.indexer;

import java.util.List;
import java.util.Map;
import org.netbeans.modules.ada.editor.CodeUtils;
import org.netbeans.modules.ada.editor.ast.nodes.BodyDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Expression;
import org.netbeans.modules.ada.editor.ast.nodes.FieldsDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.SingleFieldDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Statement;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;
import org.netbeans.modules.gsf.api.ElementKind;

/**
 *
 * @author Radek Matous
 */
public class IdentifierSignature {

    private static final int DECLARATION = 0x1;//else invocation or use
    private static final int IFACE_MEMBER = 0x2;
    private static final int PKG_MEMBER = 0x4;
    private static final int MODIFIER_STATIC = 0x8;
    private static final int MODIFIER_TAGGED = 0x10;
    private static final int MODIFIER_ABSTRACT = 0x20;
    private static final int MODIFIER_PROTECTED = 0x40;
    private static final int MODIFIER_PUBLIC = 0x80;
    private static final int MODIFIER_LIMITED = 0x100;
    private static final int KIND_FNC = 0x200;
    private static final int KIND_VAR = 0x400;
    private static final int KIND_CONST = 0x800;
    private static final int KIND_PACKAGE = 0x1000;
    private String name;
    private int mask;
    private String typeName;

    //for invocations
    private IdentifierSignature(String name) {
        this(name, null, 0);
    }

    private IdentifierSignature(String name, String typeName, int mask) {
        this.name = name.toLowerCase();
        this.mask = mask;
        if (isDeclaration()) {
            this.typeName = typeName;
        }
    }

    private IdentifierSignature(Identifier identifier, int modifier, ElementKind kind, String typeName, boolean declaration, Boolean pkgmember) {
        this.name = identifier.getName().toLowerCase();
        if (declaration) {
            mask |= DECLARATION;
        }

        if (pkgmember != null && pkgmember) {
            mask |= PKG_MEMBER;
        }
        if (pkgmember != null && !pkgmember) {
            mask |= IFACE_MEMBER;
        }
        switch (kind) {
            case METHOD:
                mask |= KIND_FNC;
                break;
            case FIELD:
                mask |= KIND_VAR;
                break;
            case CONSTANT:
                mask |= KIND_CONST;
                break;
            case CLASS:
                mask |= KIND_PACKAGE;
                break;
            default:
                throw new IllegalStateException(kind.toString());
        }

        if (BodyDeclaration.Modifier.isPublic(modifier)) {
            mask |= MODIFIER_PUBLIC;
        }
        if (BodyDeclaration.Modifier.isAbstract(modifier)) {
            mask |= MODIFIER_ABSTRACT;
        }
        if (BodyDeclaration.Modifier.isTagged(modifier)) {
            mask |= MODIFIER_TAGGED;
        }
        if (BodyDeclaration.Modifier.isLimited(modifier)) {
            mask |= MODIFIER_LIMITED;
        }

        if (isDeclaration()) {
            this.typeName = typeName;
        }
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isDeclaration() {
        return (mask & DECLARATION) != 0;
    }

    public boolean isPackageMember() {
        return (mask & PKG_MEMBER) != 0;
    }

    public boolean isIfaceMember() {
        return (mask & IFACE_MEMBER) != 0;
    }

    public boolean isLimited() {
        return (mask & MODIFIER_LIMITED) != 0;
    }

    public boolean isStatic() {
        return (mask & MODIFIER_STATIC) != 0;
    }

    public boolean isTagged() {
        return (mask & MODIFIER_TAGGED) != 0;
    }

    public boolean isAbstract() {
        return (mask & MODIFIER_ABSTRACT) != 0;
    }

    public boolean isPublic() {
        return (mask & MODIFIER_PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (mask & MODIFIER_PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return !isPublic() && !isProtected();
    }

    public boolean isFunction() {
        return (mask & KIND_FNC) != 0 && (!isPackageMember() && !isIfaceMember());
    }

    public boolean isMethod() {
        return (mask & KIND_FNC) != 0 && (isPackageMember() || isIfaceMember());
    }

    public boolean isVariable() {
        return (mask & KIND_VAR) != 0 && (!isPackageMember() && !isIfaceMember());
    }

    public boolean isField() {
        return (mask & KIND_VAR) != 0 && (isPackageMember() || isIfaceMember());
    }

    public boolean isPackage() {
        return (mask & KIND_PACKAGE) != 0;
    }

    public boolean isConstant() {
        return (mask & KIND_CONST) != 0 && (!isPackageMember() && !isIfaceMember());
    }

    public boolean isClassConstant() {
        return (mask & KIND_CONST) != 0 && (isPackageMember() || isIfaceMember());
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(";");//NOI18N
        if (mask != 0) {
            sb.append(mask).append(";");//NOI18N
        } else {
            assert !isDeclaration();
        }
        if (isDeclaration()) {
            sb.append(typeName).append(";");//NOI18N
        }
        return sb.toString();
    }

    public static IdentifierSignature createDeclaration(Signature sign) {
        String name = sign.string(0);
        int mask = sign.integer(1);
        String typeName = ((mask & DECLARATION) != 0) ? sign.string(2) : null;
        return new IdentifierSignature(name, typeName, mask);
    }

    public static IdentifierSignature createInvocation(Signature sign) {
        String name = sign.string(0);
        return new IdentifierSignature(name);
    }

    public static void add(PackageSpecification declaration, List<IdentifierSignature> results) {
        String pkgName = CodeUtils.extractPackageName(declaration);
        add(declaration.getBody().getStatements(), pkgName, true, results);
    }

    public static void add(PackageBody declaration, List<IdentifierSignature> results) {
        String pkgName = CodeUtils.extractPackageName(declaration);
        add(declaration.getBody().getStatements(), pkgName, true, results);
    }

    public static void add(List<Statement> statements, String packageName, boolean isPackage, List<IdentifierSignature> results) {
        for (Statement statement : statements) {
            if (statement instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) statement;
                add(methodDeclaration, packageName, isPackage, results);
            } else if (statement instanceof FieldsDeclaration) {
                FieldsDeclaration fieldsDeclaration = (FieldsDeclaration) statement;
                add(fieldsDeclaration, packageName, isPackage, results);
            }
            if (statement instanceof TypeDeclaration) {
                TypeDeclaration typeDeclaration = (TypeDeclaration) statement;
                add(typeDeclaration, typeDeclaration.getModifier(), packageName, isPackage, results);
            }
        }
    }

    private static void add(MethodDeclaration declaration, String typename, Boolean pkgMember, List<IdentifierSignature> results) {
        if (declaration.getKind() == MethodDeclaration.Kind.FUNCTION) {
            IdentifierSignature is = new IdentifierSignature(declaration.getFunction().getIdentifier(),
                    declaration.getModifier(), ElementKind.METHOD, typename, true, pkgMember);
            results.add(is);
        }
        else {
            IdentifierSignature is = new IdentifierSignature(declaration.getProcedure().getIdentifier(),
                    declaration.getModifier(), ElementKind.METHOD, typename, true, pkgMember);
            results.add(is);
        }
    }

    private static void add(FieldsDeclaration declaration, String typename, Boolean pkgMember, List<IdentifierSignature> results) {
        List<SingleFieldDeclaration> fields = declaration.getFields();
        for (SingleFieldDeclaration fldDeclaration : fields) {
            add(fldDeclaration, declaration.getModifier(), typename, pkgMember, results);
        }
    }

    private static void add(SingleFieldDeclaration field, int modifier, String typename, Boolean pkgMember, List<IdentifierSignature> results) {
        Variable var = field.getName();
        Identifier id = get(var);

        IdentifierSignature is = id != null ? new IdentifierSignature(id, modifier,
                ElementKind.FIELD, typename, true, pkgMember) : null;
        if (is != null) {
            results.add(is);
        }
    }

    private static void add(TypeDeclaration type, int modifier, String typename, Boolean pkgMember, List<IdentifierSignature> results) {
        Identifier id = type.getTypeName();

        IdentifierSignature is = id != null ? new IdentifierSignature(id, modifier,
                ElementKind.FIELD, typename, true, pkgMember) : null;
        if (is != null) {
            results.add(is);
        }
    }

    public static void add(Identifier node, Map<String, IdentifierSignature> results) {
        String name = node.getName().toLowerCase();
        results.put(name, new IdentifierSignature(name));
    }

    private static Identifier get(Variable var) {
        Expression expr = var.getName();
        if (expr instanceof Identifier) {
            return (Identifier) expr;
        }
        return null;
    }
}
