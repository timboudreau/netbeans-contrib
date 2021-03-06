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
 * Based on org.netbeans.modules.php.editor.parser.astnodes.Variable
 * 
 * Holds a variable. 
 * <pre>e.g.<pre>:
 * Count, Sum  : Integer;
 * Size        : Integer range 0 .. 10_000 := 0;
 * Sorted      : Boolean := False;
 * Color_Table : array(1 .. Max) of Color;
 * Option      : Bit_Vector(1 .. 10) := (others => True);
 * Hello       : constant String := "Hi, world.";
 *
 * @author Andrea Lucarelli
 */
public class Variable extends VariableBase {

    public enum Kind {
        DEFAULT,
        CONSTANT,
        ALIASED,
        ALIASED_CONSTANT,
    }

    private Identifier name;
    private Kind variableKind;
	private TypeName variableType;

    public Variable(int start, int end, Identifier variableName, Variable.Kind kind, TypeName type) {
        super(start, end);
        this.name = variableName;
        this.variableKind = kind;
        this.variableType = type;
    }

	public Variable(int start, int end, Identifier variableName, Variable.Kind kind) {
		this(start, end, variableName, kind, null);
	}

	public Variable(int start, int end, Identifier variableName) {
		this(start, end, variableName, Kind.DEFAULT, null);
	}

    /**
     * Returns the name (Identifier) of this variable
     * 
     * @return the identifier name node
     */
    public Identifier getName() {
        return this.name;
    }

	/**
     * Returns the type of this variable
     * 
     * @return the type node
     */
    public TypeName getVariableType() {
        return this.variableType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
