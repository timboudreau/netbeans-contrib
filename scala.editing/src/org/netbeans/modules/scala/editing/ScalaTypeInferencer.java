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
package org.netbeans.modules.scala.editing;

import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.scala.editing.nodes.AstDef;
import org.netbeans.modules.scala.editing.nodes.AstElement;
import org.netbeans.modules.scala.editing.nodes.AstExpr;
import org.netbeans.modules.scala.editing.nodes.AstRef;
import org.netbeans.modules.scala.editing.nodes.AstScope;
import org.netbeans.modules.scala.editing.nodes.Id;
import org.netbeans.modules.scala.editing.nodes.PathId;
import org.netbeans.modules.scala.editing.nodes.SimpleExpr;
import org.netbeans.modules.scala.editing.nodes.TypeRef;

/**
 *
 * @author Caoyuan Deng
 */
public class ScalaTypeInferencer {

    private AstScope rootScope;
    private TokenHierarchy th;

    public ScalaTypeInferencer(AstScope rootScope, TokenHierarchy th) {
        this.rootScope = rootScope;
        this.th = th;
    }

    public void infer() {
        inferRecursively(rootScope);
    }

    private void inferRecursively(AstScope scope) {
        for (AstExpr expr : scope.getExprs()) {
            if (expr instanceof SimpleExpr) {
                AstElement base = ((SimpleExpr) expr).getBase();
                if (base instanceof PathId) {
                    /** Try to find an AstRef, so we can infer its type via it's def */
                    Id firstId = ((PathId) base).getPaths().get(0);
                    AstElement firstIdRef = rootScope.getDefRef(th, firstId.getIdToken().offset(th));
                    AstDef def = rootScope.findDef(firstIdRef);
                    if (def != null) {
                        TypeRef type = def.getType();
                        if (type != null) {
                            if (firstIdRef.getType() != null) {
                                // @Todo check type of firstId with def's type 
                            } else {
                                firstId.setType(def.getType());
                                firstIdRef.setType(def.getType());
                            }
                        }
                    }
                }
            }
        }

        for (AstRef ref : scope.getRefs()) {
            if (ref.getType() == null) {
                AstDef def = rootScope.findDef(ref);
                if (def != null) {
                    ref.setType(def.getType());
                }
            }
        }

        for (AstScope _Scope : scope.getScopes()) {
            inferRecursively(_Scope);
        }
    }
}
