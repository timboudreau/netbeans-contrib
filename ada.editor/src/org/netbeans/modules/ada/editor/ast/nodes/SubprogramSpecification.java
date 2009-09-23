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
 * Represents a procedure/function specification (ARM95 - 6.1 Subprogram Declarations)
 *
 * <pre>e.g.<pre>
 * procedure Traverse_Tree;
 * procedure Increment(X : in out Integer);
 * procedure Right_Indent(Margin : out Line_Size);
 * procedure Switch(From, To : in out Link);
 * function Random return Probability;
 * function Min_Cell(X : Link) return Cell;
 * function Next_Frame(K : Positive) return Frame;
 * function Dot_Product(Left, Right : Vector) return Real;
 * function "*"(Left, Right : Matrix) return Matrix;
 */
public class SubprogramSpecification extends Statement {

    private Identifier subprogramName;
    private final ArrayList<FormalParameter> formalParameters = new ArrayList<FormalParameter>();
    private Identifier subtypeReturn;

    private SubprogramSpecification(int start, int end, Identifier subprogramName, FormalParameter[] formalParameters, Identifier subtypeReturn) {
        super(start, end);

        this.subprogramName = subprogramName;
        for (FormalParameter formalParameter : formalParameters) {
            this.formalParameters.add(formalParameter);
        }
        this.subtypeReturn = subtypeReturn;
    }

    public SubprogramSpecification(int start, int end, Identifier subprogramName, List<FormalParameter> formalParameters) {
        this(start, end, subprogramName, (FormalParameter[]) formalParameters.toArray(new FormalParameter[formalParameters.size()]), null);
    }

    public SubprogramSpecification(int start, int end, Identifier subprogramName, List<FormalParameter> formalParameters, Identifier subtypeReturn) {
        this(start, end, subprogramName, (FormalParameter[]) formalParameters.toArray(new FormalParameter[formalParameters.size()]), subtypeReturn);
    }

    public SubprogramSpecification(int start, int end, Identifier subprogramName) {
        this(start, end, subprogramName, new FormalParameter[0], null);
    }

    /**
     * Subprogram name of this declaration
     *   
     * @return Subprogram name of this specification
     */
    public Identifier getSubprogramName() {
        return subprogramName;
    }

    /**
     * List of the formal parameters of this procedure declaration
     * 
     * @return the parameters of this declaration   
     */
    public List<FormalParameter> getFormalParameters() {
        return this.formalParameters;
    }

    /**
     * 
     * @return
     */
    public Identifier getSubtypeReturn() {
        return subtypeReturn;
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
