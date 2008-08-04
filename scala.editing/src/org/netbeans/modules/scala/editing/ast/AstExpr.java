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
package org.netbeans.modules.scala.editing.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;

/**
 *
 * @author dcaoyuan
 */
public class AstExpr {

    private AstExpr parent;
    private List<AstExpr> subExprs;
    private Set<Token> tokens = new HashSet<Token>();

    public boolean isRoot() {
        return parent == null;
    }

    public void addSubExpr(AstExpr expr) {
        if (subExprs == null) {
            subExprs = new ArrayList<AstExpr>();
        }
        expr.parent = this;
        subExprs.add(expr);
    }

    public List<AstExpr> getSubExprs() {
        return subExprs == null ? Collections.<AstExpr>emptyList() : subExprs;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public AstExpr getExprContains(Token token) {        
        return getExprContainsDownside(this, token);
    }
    
    private AstExpr getExprContainsDownside(AstExpr expr, Token token) {
        if (expr.tokens.contains(token)) {
            return expr;
        }
        
        for (AstExpr subExpr : expr.getSubExprs()) {
            return getExprContainsDownside(subExpr, token);
        }
        
        return expr;
    }

    /**
     * @param Token1 token before token2
     * @param Token2 token after  token1
     * @return -1 if false, level if true
     */
    public int inSameExpr(Token token1, Token token2) {
        AstExpr expr1 = getExprContains(token1);
        AstExpr expr2 = getExprContains(token2);
        if (expr1 != null && expr2 != null) {
            if (expr1 == expr2) {
                return 0;
            }

            int level = 0;
            AstExpr curr = expr2;
            while (curr.parent != null) {
                level++;
                if (expr1 == curr.parent) {
                    return level;
                }
            }            
        }

        return -1;
    }
    
    public void print() {
        System.out.println("Exprs:");
        printRecursively(this, 0);
    }

    private void printRecursively(AstExpr expr, int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print(' ');
        }
        System.out.println(expr);

        for (AstExpr _expr : expr.getSubExprs()) {
            printRecursively(_expr, indent + 4);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Iterator<Token> itr = tokens.iterator(); itr.hasNext();) {
            Token token = itr.next();
            if (token.id() == ScalaTokenId.Nl) {
                continue;
            }

            sb.append(token.text());
            if (itr.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
