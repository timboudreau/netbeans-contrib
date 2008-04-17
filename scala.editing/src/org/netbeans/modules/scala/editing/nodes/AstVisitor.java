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
package org.netbeans.modules.scala.editing.nodes;

import java.util.Iterator;
import java.util.Stack;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import xtc.tree.Annotation;
import xtc.tree.GNode;
import xtc.tree.Location;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.util.Pair;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class AstVisitor extends Visitor {

    private int indentLevel;
    private TokenHierarchy th;
    protected AstRootScope rootScope;
    protected Stack<GNode> astPath = new Stack<GNode>();
    protected Stack<AstScope> scopeStack = new Stack<AstScope>();

    public AstVisitor(Node rootNode, TokenHierarchy th) {
        this.th = th;
        this.rootScope = new AstRootScope(getBoundsTokens(rootNode));
        scopeStack.push(rootScope);
    }

    public void visit(GNode node) {
        enter(node);
        visitChildren(node);
        exit(node);
    }

    protected void enter(GNode node) {
        indentLevel++;
        astPath.push(node);
    }

    protected void exit(GNode node) {
        indentLevel--;
        astPath.pop();
    }

    protected void visitChildren(GNode node) {
        for (Iterator itr = node.iterator(); itr.hasNext();) {
            Object o = itr.next();
            if (o instanceof GNode) {
                dispatch((GNode) o);
            } else if (o instanceof Pair) {
                visitPair((Pair) o);
            }
        }
    }

    private void visitPair(Pair pair) {
        //System.out.println(indent() + "[");
        indentLevel++;
        for (Iterator itr = pair.iterator(); itr.hasNext();) {
            Object o = itr.next();
            if (o instanceof GNode) {
                dispatch((GNode) o);
            } else if (o instanceof Pair) {
                visitPair((Pair) o);
            }
        }
        indentLevel--;
    //System.out.println(indent() + "]");
    }

    @Override
    public Object visit(Annotation a) {
        System.out.println(indent() + "@" + a.toString());
        return null;
    }

    public AstScope getRootScope() {
        return rootScope;
    }

    protected Token[] getBoundsTokens(Node node) {
        Location loc = node.getLocation();
        TokenSequence<? extends ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, loc.offset);
        
        ts.move(loc.offset);
        if (!ts.moveNext() && !ts.movePrevious()) {
            assert false : "Should not happen!";
        }
        
        Token startToken = ScalaLexUtilities.findNextNonWs(ts);
        if (startToken.isFlyweight()) {
            startToken = ts.offsetToken();
        }
        
        ts.move(loc.endOffset);
        if (!ts.moveNext() && !ts.movePrevious()) {
            assert false : "Should not happen!";
        }
        Token endToken = ScalaLexUtilities.findPreviousNonWs(ts);
        if (endToken.isFlyweight()) {
            endToken = ts.offsetToken();
        }
        
        return new Token[] {startToken, endToken};
    }

    /**
     * @Note: nameNode may contains preceding void productions, and may also contains
     * following void productions, but nameString has stripped the void productions,
     * so we should adjust nameRange according to name and its length.
     */
    protected Token getIdToken(Node idNode) {
        Location loc = idNode.getLocation();
        TokenSequence<? extends ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, loc.offset);
        ts.move(loc.offset);
        if (!ts.moveNext() && !ts.movePrevious()) {
            assert false : "Should not happen!";
        }
        
        String name = idNode.getString(0).trim();
        Token token = null;
        if (name.equals("this")) {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.This);
        } else if (name.equals("super")) {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.Super);
        } else {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.Identifier);
        }
        
        if (token.isFlyweight()) {
            token = ts.offsetToken();
        }
        
        return token;
    }

    protected String getAstPathString() {
        StringBuilder sb = new StringBuilder();

        for (Iterator<GNode> itr = astPath.iterator(); itr.hasNext();) {
            sb.append(itr.next().getName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    protected GNode findNearsetNode(String name) {
        GNode result = null;

        for (Iterator<GNode> itr = astPath.iterator(); itr.hasNext();) {
            GNode node = itr.next();
            if (node.getName().equals(name)) {
                result = node;
            }
        }

        return result;
    }

    private String indent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
}
