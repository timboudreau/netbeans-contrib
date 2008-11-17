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

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementHandle;
import org.netbeans.modules.scala.editing.ScalaMimeResolver;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.Type;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class AstItem {

    protected final static String NO_MEANING_NAME = "-1";
    /** 
     * @Note: 
     * 1. Not all AstItem has pickToken, such as Expr etc.
     * 2. Due to strange behavior of StructureAnalyzer, we can not rely on 
     *    pickToken's text as name, pickToken may be <null> and pickToken.text() 
     *    will return null when an Identifier token modified, seems sync issue
     */
    private Symbol symbol;
    private Token idToken;
    private AstScope enclosingScope;
    private Type resultType;
    private String name;

    protected AstItem() {
        this(null, null);
    }

    protected AstItem(Symbol symbol) {
        this(symbol, null);
    }

    protected AstItem(Token idToken) {
        this(null, idToken);
    }

    protected AstItem(Symbol symbol, Token idToken) {
        this.symbol = symbol;
        this.idToken = idToken;
    }

    private void setName(Token idToken) {
        /**
         * symbol.nameString() is same as idToken's text, for editor, it's always
         * better to use idToken's text, for example, we'll use this name to
         * decide occurrences etc.
         */
        /** @todo why will throws NPE here? */
        try {
            this.name = idToken.text().toString();
        } catch (Exception ex) {
            int l = idToken.length();
            StringBuilder sb = new StringBuilder(l);
            for (int i = 0; i < l; i++) {
                sb.append(" ");
            }
            this.name = sb.toString();
            System.out.println("NPE in AstItem#getName:" + getIdToken().id());
        }
    }

    public void setResultType(Type tpe) {
        this.resultType = tpe;
    }

    public Type getResultType() {
        return resultType;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }
    
    public void setIdToken(Token idToken) {
        this.idToken = idToken;
        setName(idToken);
    }

    public Token getIdToken() {
        return idToken;
    }

    public int getIdOffset(TokenHierarchy th) {
        if (idToken != null) {
            return idToken.offset(th);
        } else {
            assert false : getName() + ": Should implement getIdOffset(th)";
            return -1;
        }
    }

    public int getIdEndOffset(TokenHierarchy th) {
        if (idToken != null) {
            return idToken.offset(th) + idToken.length();
        } else {
            assert false : getName() + ": Should implement getIdEndOffset(th)";
            return -1;
        }
    }

    public String getBinaryName() {
        if (getSymbol().isModule()) {
            /** According to Symbol#kindString, an object template isModule() */
            return getName() + "$";
        } else {
            return getName();
        }
    }

    public <T extends AstDef> T getEnclosingDef(Class<T> clazz) {
        return getEnclosingScope().getEnclosingDef(clazz);
    }

    /**
     * @Note: enclosingScope will be set when call
     *   {@link AstScope#addElement(Element)} or {@link AstScope#addMirror(Mirror)}
     */
    public void setEnclosingScope(AstScope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    /**
     * @return the scope that encloses this item 
     */
    public AstScope getEnclosingScope() {
        assert enclosingScope != null : getName() + ": Each item should set enclosing scope!, except native TypeRef";
        return enclosingScope;
    }

    public String getMimeType() {
        return ScalaMimeResolver.MIME_TYPE;
    }

    public boolean signatureEquals(ElementHandle handle) {
        // XXX TODO
        return false;
    }

    public String getIn() {
        return symbol.enclClass().nameString();
    }

    public boolean isSameNameAsEnclClass() {
        return
                symbol.isConstructor() ||
                symbol.isClass() ||
                symbol.isModule() ||
                symbol.isTrait() ||
                (symbol.isMethod() && symbol.nameString().equals("apply"));
    }
}
