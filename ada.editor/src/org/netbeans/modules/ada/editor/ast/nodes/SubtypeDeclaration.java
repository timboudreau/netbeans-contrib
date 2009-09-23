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
package org.netbeans.modules.ada.editor.ast.nodes;

import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;

/**
 * Represents a type declaration of package
 *
 * <pre>e.g.<pre>
 * subtype Rainbow is Color range Red .. Blue;
 * subtype Red_Blue is Rainbow;
 * subtype Int is Integer;
 * subtype Small_Int is Integer range -10 .. 10;
 * subtype Up_To_K is Column range 1 .. K;
 * subtype Square is Matrix(1 .. 10, 1 .. 10);
 * subtype Male is Person(Sex => M);
 */
public class SubtypeDeclaration extends TypeDeclaration {

    private Identifier subTypeName;
	private TypeName parentType;

    public SubtypeDeclaration(int start, int end, Identifier subTypeName, TypeName parentType) {
        super(start, end, subTypeName);
        this.subTypeName = subTypeName;
        this.parentType = parentType;
    }

    /**
     * @return the name of the type
     */
    public Identifier getSubTypeName() {
        return this.subTypeName;
    }
    
	/**
     * Returns the parent type of this subtype
     * 
     * @return the type node
     */
    public TypeName getParentType() {
        return this.parentType;
    }

	@Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
