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

    private SubprogramSpecification subprogramSpecification;
    private SubprogramBody subprogramBody;

    private MethodDeclaration(int start, int end, int modifier, SubprogramSpecification subprogramSpecification, boolean shouldComplete) {
        super(start, end, modifier, shouldComplete);

        if (subprogramSpecification == null) {
            throw new IllegalArgumentException();
        }
        this.subprogramSpecification = subprogramSpecification;
    }

    public MethodDeclaration(int start, int end, int modifier, SubprogramSpecification subprogramSpecification) {
        this(start, end, modifier, subprogramSpecification, false);
    }

    private MethodDeclaration(int start, int end, int modifier, SubprogramBody subprogramBody, boolean shouldComplete) {
        super(start, end, modifier, shouldComplete);

        if (subprogramBody == null) {
            throw new IllegalArgumentException();
        }
        this.subprogramBody = subprogramBody;
    }

    public MethodDeclaration(int start, int end, int modifier, SubprogramBody subprogramBody) {
        this(start, end, modifier, subprogramBody, false);
    }

    public boolean isSpefication() {
        return (this.subprogramBody == null);
    }

    public Identifier getSubrogramName() {
        if (this.subprogramSpecification != null) {
            return (this.subprogramSpecification.getSubprogramName());
        } else {
            return (this.subprogramBody.getSubprogramSpecification().getSubprogramName());
        }
    }

    public Identifier getSubrogramNameEnd() {
        if (!this.isSpefication()) {
            return (this.subprogramBody.getSubprogramNameEnd());
        } else {
            return null;
        }
    }

    public String getMethodName() {
        return (this.getSubrogramName().getName());
    }

    public String getMethodNameEnd() {
        return (this.getSubrogramNameEnd().getName());
    }

    /**
     * The function declaration component of this method
     *
     * @return function declaration component of this method
     */
    public SubprogramSpecification getSubprogramSpecification() {
        return subprogramSpecification;
    }

    /**
     * The function declaration component of this method
     *
     * @return function declaration component of this method
     */
    public SubprogramBody getSubprogramBody() {
        return subprogramBody;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
