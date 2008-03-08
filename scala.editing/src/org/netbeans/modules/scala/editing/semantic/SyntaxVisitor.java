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
package org.netbeans.modules.scala.editing.semantic;

import java.util.List;
import org.netbeans.api.languages.ASTItem;
import org.netbeans.api.languages.ASTNode;
import org.netbeans.api.languages.ASTToken;

/**
 *
 * @author dcaoyuan
 */
public class SyntaxVisitor extends ASTVisitor {

    private ScalaContext rootCtx = null;
    private int nlCount;
    private int ordinalCount;
    private boolean possibleOp;

    public SyntaxVisitor(ScalaContext rootContext) {
        this.rootCtx = rootContext;
    }

    @Override
    boolean visitNote( List<ASTItem> path, String xpath, int ordinal, boolean enter) {
        ASTItem leaf = path.get(path.size() - 1);
        if (xpath.endsWith("PostfixExpr")) {
            if (enter) {
                StringBuilder sb = new StringBuilder();
                boolean prefixOpPreceding = false;
                boolean infixExprPreceding = false;
                int count = 0;
                for (ASTItem simpleExpr : leaf.getChildren()) {
                    if (isScalaId(simpleExpr)) {
                        if (prefixOpPreceding) {
                            sb.append(".InfixExpr");
                            infixExprPreceding = true;
                        }
                        if (isPreOp(leaf)) {
                            prefixOpPreceding = true;
                        }
                        count++;

                    } else {
                        prefixOpPreceding = false;
                    }
                }
                return true;
            } else {
                nlCount = 0;
            }
        } else if (xpath.endsWith("nl") && xpath.contains("PostfixExpr.SimpleExpr")) {
            if (enter) {
                nlCount++;
            }
        }

        return false;
    }

    private boolean isScalaId(ASTItem item) {
        if (item instanceof ASTNode) {
            String id = ((ASTNode) item).getNT();
            if (id.equals("ScalaId")) {
                return true;
            } else {
                return false;
            }
        } else {
            int astNodeCount = 0;
            ASTItem next = null;
            for (ASTItem child : item.getChildren()) {
                if (child instanceof ASTNode) {
                    next = child;
                    astNodeCount++;
                }
            }
            if (astNodeCount == 1) {
                return isScalaId(next);
            } else {
                return false;
            }
        }
    }

    private boolean isPreOp(ASTItem item) {
        if (item instanceof ASTToken) {
            String id = ((ASTToken) item).getIdentifier();
            if (id.equals("+") || id.equals("-") || id.equals("~") || id.equals("!")) {
                return true;
            } else {
                return false;
            }
        } else {
            int astNodeCount = 0;
            ASTItem next = null;
            for (ASTItem child : item.getChildren()) {
                if (child instanceof ASTNode) {
                    next = child;
                    astNodeCount++;
                }
            }
            if (astNodeCount == 1) {
                return isScalaId(next);
            } else {
                return false;
            }
        }
    }
}
