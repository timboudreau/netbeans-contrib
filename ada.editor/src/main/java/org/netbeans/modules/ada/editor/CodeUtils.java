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
package org.netbeans.modules.ada.editor;

import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.ada.editor.ast.nodes.Expression;
import org.netbeans.modules.ada.editor.ast.nodes.FormalParameter;
import org.netbeans.modules.ada.editor.ast.nodes.Identifier;
import org.netbeans.modules.ada.editor.ast.nodes.MethodDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.PackageBody;
import org.netbeans.modules.ada.editor.ast.nodes.PackageName;
import org.netbeans.modules.ada.editor.ast.nodes.PackageSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.Reference;
import org.netbeans.modules.ada.editor.ast.nodes.Statement;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramBody;
import org.netbeans.modules.ada.editor.ast.nodes.SubprogramSpecification;
import org.netbeans.modules.ada.editor.ast.nodes.TypeDeclaration;
import org.netbeans.modules.ada.editor.ast.nodes.Variable;

/**
 * Based on org.netbeans.modules.php.editor.CodeUtils
 *
 * @author Andrea Lucarelli
 */
public class CodeUtils {

    public static final String FUNCTION_TYPE_PREFIX = "@fn:";
    public static final String PROCEDURE_TYPE_PREFIX = "@prc:";
    public static final String METHOD_TYPE_PREFIX = "@mtd:";

    private CodeUtils() {
    }

    public static String extractPackageName(PackageName pkgName) {
        Expression name = pkgName.getPackageName();

        assert name instanceof Identifier :
                "unsupported type of PackageName.getName(): " + name.getClass().getName();

        return (name instanceof Identifier) ? ((Identifier) name).getName() : "";//NOI18N
    }

    public static String extractPackageName(PackageSpecification pkgSpecification) {
        return pkgSpecification.getName().getName();
    }

    public static String extractPackageName(PackageBody pkgBody) {
        return pkgBody.getName().getName();
    }

    public static String extractSubprogramName(SubprogramSpecification subprogramSpecification){
        return subprogramSpecification.getSubprogramName().getName();
    }

    public static String extractProcedureName(SubprogramBody subprogramBody){
        return subprogramBody.getSubprogramSpecification().getSubprogramName().getName();
    }

    public static String extractMethodName(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getMethodName();
    }

    @CheckForNull // null for RelectionVariable
    public static String extractVariableName(Variable var) {
        if (var.getName() instanceof Identifier) {
            Identifier id = (Identifier) var.getName();
            StringBuilder varName = new StringBuilder();

            varName.append(id.getName());
            return varName.toString();
        }

        return null;
    }

    @CheckForNull // null for RelectionVariable
    public static String extractTypeName(TypeDeclaration var) {
        if (var.getTypeName() instanceof Identifier) {
            Identifier id = (Identifier) var.getTypeName();
            StringBuilder varName = new StringBuilder();

            varName.append(id.getName());
            return varName.toString();
        }

        return null;
    }

    public static String getParamDisplayName(FormalParameter param) {
        Variable var = param.getParameterName();
        StringBuilder paramName = new StringBuilder();

		Identifier id = (Identifier) var.getName();
		paramName.append(id.getName());

        return paramName.length() == 0 ? null : paramName.toString();
    }
}
