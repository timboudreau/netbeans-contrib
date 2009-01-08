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
 * Represents a procedure declaration
 * <pre>e.g.<pre>
 * procedure Traverse_Tree;
 * procedure Increment(X : in out Integer);
 * procedure Right_Indent(Margin : out Line_Size);
 * procedure Switch(From, To : in out Link);
 */
public class ProcedureDeclaration extends Statement {

    private boolean isSpefication;
    private Identifier name;
    private Identifier nameEnd;
    private final ArrayList<FormalParameter> formalParameters = new ArrayList<FormalParameter>();
    private Block declarations;
    private Block body;

    private ProcedureDeclaration(int start, int end, Identifier procedureName, FormalParameter[] formalParameters, final boolean isSpecification) {
        super(start, end);
        this.isSpefication = isSpecification;
        this.name = procedureName;
        for (FormalParameter formalParameter : formalParameters) {
            this.formalParameters.add(formalParameter);
        }
    }

    public ProcedureDeclaration(int start, int end, Identifier procedureName, List<FormalParameter> formalParameters) {
        this(start, end, procedureName, (FormalParameter[]) formalParameters.toArray(new FormalParameter[formalParameters.size()]), false);
    }

    public Block getDeclarations() {
        return declarations;
    }

    public void setDeclarations(Block declarations) {
        this.declarations = declarations;
    }

    public void setIdentifierEnd(Identifier nameEnd) {
        this.nameEnd = nameEnd;
    }

    public Identifier getIdentifierEnd() {
        return nameEnd;
    }

    /**
     * Body of this procedure declaration
     * 
     * @return Body of this procedure declaration
     */
    public Block getBody() {
        return body;
    }

    public void setBody(Block body) {
        this.isSpefication = false;
        this.body = body;
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
     * Procedure name of this declaration
     *   
     * @return Procedure name of this declaration
     */
    public Identifier getIdentifier() {
        return name;
    }

    /**
     * True if this procedure's return variable will be referenced
     * @return True if this procedure's return variable will be referenced
     */
    public boolean isSpefication() {
        return isSpefication;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
