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
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.ada.editor.ast.ASTNode;
import org.netbeans.modules.ada.editor.ast.nodes.visitors.Visitor;

/**
 * Represents a fields declaration of package
 *
 * <pre>e.g.<pre>
 * Count, Sum  : Integer;
 * Size        : Integer range 0 .. 10_000 := 0;
 * Sorted      : Boolean := False;
 * Color_Table : array(1 .. Max) of Color;
 * Option      : Bit_Vector(1 .. 10) := (others => True);
 * Hello       : constant String := "Hi, world.";
 */
public class FieldsDeclaration extends Statement {

    private final ArrayList<SingleFieldDeclaration> fields = new ArrayList<SingleFieldDeclaration>();

    public FieldsDeclaration(int start, int end, List variablesAndDefaults) {
        super(start, end);

        System.out.println("FieldsDeclaration: " + variablesAndDefaults.size());
        
        if (variablesAndDefaults == null || variablesAndDefaults.size() == 0) {
            throw new IllegalArgumentException();
        }

        for (Iterator iter = variablesAndDefaults.iterator(); iter.hasNext();) {
            final Object next = iter.next();
            if (next instanceof SingleFieldDeclaration) {
                System.out.println("FieldsDeclaration: next " + next);
                this.fields.add((SingleFieldDeclaration) next);
            } else {
                ASTNode[] element = (ASTNode[]) next;
                System.out.println("FieldsDeclaration: element[0] " + element[0]);
                System.out.println("FieldsDeclaration: element[1] " + element[1]);
                SingleFieldDeclaration field = createField((Variable) element[0], (Expression) element[1]);
                this.fields.add(field);
            }
        }
    }

    private SingleFieldDeclaration createField(Variable name, Expression value) {
        int start = name.getStartOffset();
        int end = value == null ? name.getEndOffset() : value.getEndOffset();
        final SingleFieldDeclaration result = new SingleFieldDeclaration(start, end, name, value);
        return result;
    }

    /**
     * The list of single fields that are declared
     * 
     * @return List of single fields
     */
    public List<SingleFieldDeclaration> getFields() {
        return this.fields;
    }

    public Expression[] getInitialValues() {
        Expression[] result = new Expression[this.fields.size()];
        int i = 0;
        for (SingleFieldDeclaration field : this.fields) {
            result[i++] = field.getValue();
        }
        return result;
    }

    public Variable[] getVariableNames() {
        Variable[] result = new Variable[this.fields.size()];
        int i = 0;
        for (SingleFieldDeclaration field : this.fields) {
            result[i++] = field.getName();
        }
        return result;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
