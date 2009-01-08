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
 * Represents a function/procedure declaration in a package
 * Holds the function/procedure modifier
 * @see {@link FunctionDeclaration}
 * @see {@link ProcedureDeclaration}
 */
public class MethodDeclaration extends BodyDeclaration {

    public enum Kind {
        PROCEDURE, FUNCTION;
    }

    private FunctionDeclaration function;
    private ProcedureDeclaration procedure;
    private Kind kind;

    public MethodDeclaration(int start, int end, int modifier, Statement statement) {
        super(start, end, modifier, false);

        if (statement == null) {
            throw new IllegalArgumentException();
        }
        if (statement instanceof FunctionDeclaration) {
            this.function = (FunctionDeclaration)statement;
            this.kind = Kind.FUNCTION;
        } else {
            this.procedure = (ProcedureDeclaration)statement;
            this.kind = Kind.PROCEDURE;
        }

    }

    private MethodDeclaration(int start, int end, int modifier, FunctionDeclaration function, boolean shouldComplete) {
        super(start, end, modifier, shouldComplete);

        if (function == null) {
            throw new IllegalArgumentException();
        }
        this.function = function;
        this.kind = Kind.FUNCTION;
    }

    public MethodDeclaration(int start, int end, int modifier, FunctionDeclaration function) {
        this(start, end, modifier, function, false);
    }

    private MethodDeclaration(int start, int end, int modifier, ProcedureDeclaration procedure, boolean shouldComplete) {
        super(start, end, modifier, shouldComplete);

        if (procedure == null) {
            throw new IllegalArgumentException();
        }
        this.procedure = procedure;
        this.kind = Kind.PROCEDURE;
    }

    public MethodDeclaration(int start, int end, int modifier, ProcedureDeclaration procedure) {
        this(start, end, modifier, procedure, false);
    }

    public Identifier getIdentifier () {
        Identifier identifier;
        if (this.kind == Kind.FUNCTION) {
            identifier = this.getFunction().getIdentifier();
        } else {
            identifier = this.getProcedure().getIdentifier();
        }
        return identifier;
    }

    public Identifier getIdentifierEnd () {
        Identifier identifier;
        if (this.kind == Kind.FUNCTION) {
            identifier = this.getFunction().getIdentifierEnd();
        } else {
            identifier = this.getProcedure().getIdentifierEnd();
        }
        return identifier;
    }

    public void setIdentifierEnd(Identifier nameEnd) {
        if (this.kind == Kind.FUNCTION) {
            this.getFunction().setIdentifierEnd(nameEnd);
        } else {
            this.getProcedure().setIdentifierEnd(nameEnd);
        }
    }

    public String getName () {
        String name;
        if (this.kind == Kind.FUNCTION) {
            name = this.getFunction().getIdentifier().getName();
        } else {
            name = this.getProcedure().getIdentifier().getName();
        }
        return name;
    }

    public String getNameEnd () {
        String name = null;
        if (this.kind == Kind.FUNCTION) {
            if (this.getFunction().getIdentifierEnd() != null) {
                name = this.getFunction().getIdentifierEnd().getName();
            }
        } else if (this.kind == Kind.PROCEDURE) {
            if (this.getProcedure().getIdentifierEnd() != null) {
                name = this.getProcedure().getIdentifierEnd().getName();
            }
        }
        return name;
    }
    
    /**
     * The function declaration component of this method
     *
     * @return function declaration component of this method
     */
    public FunctionDeclaration getFunction() {
        return function;
    }

    /**
     * The function declaration component of this method
     *
     * @return function declaration component of this method
     */
    public ProcedureDeclaration getProcedure() {
        return procedure;
    }

    public Kind getKind() {
        return kind;
    }

    public void setDeclarations(Block declarations) {
        if (this.kind == Kind.FUNCTION) {
            this.getFunction().setDeclarations(declarations);
        } else {
            this.getProcedure().setDeclarations(declarations);
        }
    }

    public void setBody(Block body) {
        if (this.kind == Kind.FUNCTION) {
            this.getFunction().setBody(body);
        } else {
            this.getProcedure().setBody(body);
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
