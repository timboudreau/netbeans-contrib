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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;

/**
 * Represents a procedure/function body (ARM95 - 6.3 Subprogram Bodies)
 *
 * <pre>e.g.<pre>
 * procedure Push(E : in Element_Type; S : in out Stack) is
 * begin
 *    if S.Index = S.Size then
 *       raise Stack_Overflow;
 *    else
 *       S.Index := S.Index + 1;
 *       S.Space(S.Index) := E;
 *    end if;
 * end Push;
 * function Dot_Product(Left, Right : Vector) return Real is
 *    Sum : Real := 0.0;
 * begin
 *    Check(Left'First = Right'First and Left'Last = Right'Last);
 *    for J in Left'Range loop
 *       Sum := Sum + Left(J)*Right(J);
 *    end loop;
 *    return Sum;
 * end Dot_Product;
 */
public class SubprogramBody extends Statement {

	private SubprogramSpecification subprogramSpecification;
	private Identifier subprogramNameEnd;
    private Block declarations;
    private Block body;

    public SubprogramBody(int start, int end, SubprogramSpecification subprogramSpecification, Block declarations, Block body, Identifier subprogramNameEnd) {
        super(start, end);
		this.subprogramSpecification = subprogramSpecification;
        this.declarations = declarations;
        this.body = body;
        this.subprogramNameEnd = subprogramNameEnd;
    }

    /**
     * Local declarations of this subprogram
     * 
     * @return Body of this subprogram
     */
    public Block getDeclarations() {
        return declarations;
    }

    /**
     * Body of this subprogram
     * 
     * @return Body of this subprogram
     */
    public Block getBody() {
        return body;
    }

	public SubprogramSpecification getSubprogramSpecification() {
		return subprogramSpecification;
	}

	public Identifier getSubprogramNameEnd() {
        return subprogramNameEnd;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
