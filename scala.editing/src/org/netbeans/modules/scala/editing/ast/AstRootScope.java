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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;

/**
 *
 * @author Caoyuan Deng
 */
public class AstRootScope extends AstScope {

    private Map<Token, AstItem> idTokenToItem = new HashMap<Token, AstItem>();
    private List<Token> tokens;
    private boolean tokensSorted;
    private AstExpr exprContainer = new AstExpr();

    public AstRootScope(Token... boundsTokens) {
        super(boundsTokens);
    }

    public AstExpr getExprContainer() {
        return exprContainer;
    }

    public boolean contains(Token idToken) {
        return idTokenToItem.containsKey(idToken);
    }

    public Map<Token, AstItem> getIdTokenToItem(TokenHierarchy th) {
        if (!tokensSorted) {
            tokens = Arrays.asList(idTokenToItem.keySet().toArray(new Token[idTokenToItem.size()]));
            Collections.sort(tokens, new TokenComparator(th));
            tokensSorted = true;
        }

        return idTokenToItem;
    }

    /**
     * To make sure each idToken only corresponds to one AstItem, if more than
     * one AstItem point to the same idToken, only the first one will be stored
     */
    protected boolean tryToPut(Token idToken, AstItem item) {
        AstItem existOne = idTokenToItem.get(idToken);
        if (existOne == null) {
            idTokenToItem.put(idToken, item);
            tokensSorted = false;
            return true;
        } else {
            // if existOne is def and with narrow visible than new one, replace it
            if (item instanceof AstDef && existOne.getSymbol().isPrivateLocal() && item.getSymbol().isPublic()) {
                idTokenToItem.put(idToken, item);
                tokensSorted = false;
                return true;
            }
        }

        return false;
    }

    @Override
    public AstItem findItemAt(TokenHierarchy th, int offset) {
        List<Token> _tokens = getSortedToken(th);

        int lo = 0;
        int hi = _tokens.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >> 1;
            Token middle = _tokens.get(mid);
            if (offset < middle.offset(th)) {
                hi = mid - 1;
            } else if (offset > middle.offset(th) + middle.length()) {
                lo = mid + 1;
            } else {
                return idTokenToItem.get(middle);
            }
        }

        return null;
    }

    private List<Token> getSortedToken(TokenHierarchy th) {
        if (!tokensSorted) {
            tokens = Arrays.asList(idTokenToItem.keySet().toArray(new Token[idTokenToItem.size()]));
            Collections.sort(tokens, new TokenComparator(th));
            tokensSorted = true;
        }

        return tokens == null ? Collections.<Token>emptyList() : tokens;
    }

    public AstItem findItemAt(Token token) {
        return idTokenToItem.get(token);
    }

    public AstItem findFirstItemWithName(String name) {
        for (Map.Entry<Token, AstItem> entry : idTokenToItem.entrySet()) {
            if (entry.getKey().text().toString().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }

    protected void debugPrintTokens(TokenHierarchy th) {
        for (Token token : getSortedToken(th)) {
            System.out.println("AstItem: " + idTokenToItem.get(token));
        }
    }

    private static class TokenComparator implements Comparator<Token> {

        private TokenHierarchy th;

        public TokenComparator(TokenHierarchy th) {
            this.th = th;
        }

        public int compare(Token o1, Token o2) {
            return o1.offset(th) < o2.offset(th) ? -1 : 1;
        }
    }
    // Sinleton EmptyScope
    private static AstRootScope EmptyScope;

    public static AstRootScope emptyScope() {
        if (EmptyScope == null) {
            EmptyScope = new AstRootScope() {

                @Override
                public int getBoundsOffset(TokenHierarchy th) {
                    return -1;
                }

                @Override
                public int getBoundsEndOffset(TokenHierarchy th) {
                    return -1;
                }
            };
        }

        return EmptyScope;
    }
}
