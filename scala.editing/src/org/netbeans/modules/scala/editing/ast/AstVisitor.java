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

import java.util.Iterator;
import java.util.Stack;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.scala.editing.lexer.ScalaLexUtilities;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import scala.Option;
import scala.Tuple2;
import scala.tools.nsc.ast.Trees.Alternative;
import scala.tools.nsc.ast.Trees.Annotated;
import scala.tools.nsc.ast.Trees.Annotation;
import scala.tools.nsc.ast.Trees.AppliedTypeTree;
import scala.tools.nsc.ast.Trees.Apply;
import scala.tools.nsc.ast.Trees.ApplyDynamic;
import scala.tools.nsc.ast.Trees.ArrayValue;
import scala.tools.nsc.ast.Trees.Assign;
import scala.tools.nsc.ast.Trees.Bind;
import scala.tools.nsc.ast.Trees.Block;
import scala.tools.nsc.ast.Trees.CaseDef;
import scala.tools.nsc.ast.Trees.ClassDef;
import scala.tools.nsc.ast.Trees.CompoundTypeTree;
import scala.tools.nsc.ast.Trees.DefDef;
import scala.tools.nsc.ast.Trees.ExistentialTypeTree;
import scala.tools.nsc.ast.Trees.Function;
import scala.tools.nsc.ast.Trees.Ident;
import scala.tools.nsc.ast.Trees.If;
import scala.tools.nsc.ast.Trees.Import;
import scala.tools.nsc.ast.Trees.LabelDef;
import scala.tools.nsc.ast.Trees.Literal;
import scala.tools.nsc.ast.Trees.Match;
import scala.tools.nsc.ast.Trees.ModuleDef;
import scala.tools.nsc.ast.Trees.New;
import scala.tools.nsc.ast.Trees.PackageDef;
import scala.tools.nsc.ast.Trees.Return;
import scala.tools.nsc.ast.Trees.Select;
import scala.tools.nsc.ast.Trees.SelectFromTypeTree;
import scala.tools.nsc.ast.Trees.Sequence;
import scala.tools.nsc.ast.Trees.SingletonTypeTree;
import scala.tools.nsc.ast.Trees.Star;
import scala.tools.nsc.ast.Trees.StubTree;
import scala.tools.nsc.ast.Trees.Super;
import scala.tools.nsc.ast.Trees.Template;
import scala.tools.nsc.ast.Trees.This;
import scala.tools.nsc.ast.Trees.Throw;
import scala.tools.nsc.ast.Trees.Tree;
import scala.tools.nsc.ast.Trees.Try;
import scala.tools.nsc.ast.Trees.TypeApply;
import scala.tools.nsc.ast.Trees.TypeBoundsTree;
import scala.tools.nsc.ast.Trees.TypeDef;
import scala.tools.nsc.ast.Trees.TypeTree;
import scala.tools.nsc.ast.Trees.Typed;
import scala.tools.nsc.ast.Trees.UnApply;
import scala.tools.nsc.ast.Trees.ValDef;
import scala.tools.nsc.symtab.Symbols.Symbol;
import scala.tools.nsc.symtab.Types.Type;
import scala.tools.nsc.util.BatchSourceFile;
import scala.tools.nsc.util.Position;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class AstVisitor {

    private int indentLevel;
    private BatchSourceFile sourceFile;
    protected TokenHierarchy th;
    protected AstScope rootScope;
    protected Stack<Tree> astPath = new Stack<Tree>();
    protected Stack<AstScope> scopes = new Stack<AstScope>();

    public AstVisitor(Tree rootTree, TokenHierarchy th, BatchSourceFile sourceFile) {
        this.th = th;
        this.sourceFile = sourceFile;
        this.rootScope = new AstScope(getBoundsTokens(offset(rootTree), sourceFile.length()));
        scopes.push(rootScope);
        visit(rootTree);
    }

    public AstScope getRootScope() {
        return rootScope;
    }

    protected void visit(scala.List trees) {
        if (trees.isEmpty()) {
            return;
        }

        Object head = trees.head();
        if (head instanceof scala.Nil$) {
            // do nothing;
        } else if (head instanceof Tree) {
            visit((Tree) head);
        } else if (head instanceof scala.List) {
            visit((scala.List) head);
        } else if (head instanceof Tuple2) {
            System.out.println("Visit Tuple: " + head + " class=" + head.getClass().getCanonicalName());

            Object o1 = ((Tuple2) head)._1();
            if (o1 != null) {
                System.out.println("Visit Tuple: " + o1 + " class=" + o1.getClass().getCanonicalName());
            }
            Object o2 = ((Tuple2) head)._2();
            if (o2 != null) {
                System.out.println("Visit Tuple: " + o2 + " class=" + o2.getClass().getCanonicalName());
            }
        } else {
            System.out.println("Try to visit: " + head + " class=" + head.getClass().getCanonicalName());
        }

        visit(trees.tail());
    }

    protected void visit(Tree tree) {
        if (tree == null) {
            return;
        }

        if (offset(tree) == -1) {
            /** It may be EmptyTree, emptyValDef$, or remote TypeTree which present an inferred Type etc */
            return;
        }

        enter(tree);

        if (tree instanceof ClassDef) {
            visitClassDef((ClassDef) tree);
        } else if (tree instanceof PackageDef) {
            visitPackageDef((PackageDef) tree);
        } else if (tree instanceof ModuleDef) {
            visitModuleDef((ModuleDef) tree);
        } else if (tree instanceof ValDef) {
            visitValDef((ValDef) tree);
        } else if (tree instanceof DefDef) {
            visitDefDef((DefDef) tree);
        } else if (tree instanceof TypeDef) {
            visitTypeDef((TypeDef) tree);
        } else if (tree instanceof LabelDef) {
            visitLabelDef((LabelDef) tree);
        } else if (tree instanceof Import) {
            visitImport((Import) tree);
        } else if (tree instanceof Annotation) {
            visitAnnotation((Annotation) tree);
        } else if (tree instanceof Template) {
            visitTemplate((Template) tree);
        } else if (tree instanceof Block) {
            visitBlock((Block) tree);
        } else if (tree instanceof Match) {
            visitMatch((Match) tree);
        } else if (tree instanceof CaseDef) {
            visitCaseDef((CaseDef) tree);
        } else if (tree instanceof Sequence) {
            visitSequence((Sequence) tree);
        } else if (tree instanceof Alternative) {
            visitAlternative((Alternative) tree);
        } else if (tree instanceof Star) {
            visitStar((Star) tree);
        } else if (tree instanceof Bind) {
            visitBind((Bind) tree);
        } else if (tree instanceof UnApply) {
            visitUnApply((UnApply) tree);
        } else if (tree instanceof ArrayValue) {
            visitArrayValue((ArrayValue) tree);
        } else if (tree instanceof Function) {
            visitFunction((Function) tree);
        } else if (tree instanceof Assign) {
            visitAssign((Assign) tree);
        } else if (tree instanceof If) {
            visitIf((If) tree);
        } else if (tree instanceof Return) {
            visitReturn((Return) tree);
        } else if (tree instanceof Try) {
            visitTry((Try) tree);
        } else if (tree instanceof Throw) {
            visitThrow((Throw) tree);
        } else if (tree instanceof New) {
            visitNew((New) tree);
        } else if (tree instanceof Typed) {
            visitTyped((Typed) tree);
        } else if (tree instanceof TypeApply) {
            visitTypeApply((TypeApply) tree);
        } else if (tree instanceof Apply) {
            visitApply((Apply) tree);
        } else if (tree instanceof ApplyDynamic) {
            visitApplyDynamic((ApplyDynamic) tree);
        } else if (tree instanceof Super) {
            visitSuper((Super) tree);
        } else if (tree instanceof This) {
            visitThis((This) tree);
        } else if (tree instanceof Select) {
            visitSelect((Select) tree);
        } else if (tree instanceof Ident) {
            visitIdent((Ident) tree);
        } else if (tree instanceof Literal) {
            visitLiteral((Literal) tree);
        } else if (tree instanceof TypeTree) {
            visitTypeTree((TypeTree) tree);
        } else if (tree instanceof Annotated) {
            visitAnnotated((Annotated) tree);
        } else if (tree instanceof SingletonTypeTree) {
            visitSingletonTypeTree((SingletonTypeTree) tree);
        } else if (tree instanceof SelectFromTypeTree) {
            visitSelectFromTypeTree((SelectFromTypeTree) tree);
        } else if (tree instanceof CompoundTypeTree) {
            visitCompoundTypeTree((CompoundTypeTree) tree);
        } else if (tree instanceof AppliedTypeTree) {
            visitAppliedTypeTree((AppliedTypeTree) tree);
        } else if (tree instanceof TypeBoundsTree) {
            visitTypeBoundsTree((TypeBoundsTree) tree);
        } else if (tree instanceof ExistentialTypeTree) {
            visitExistentialTypeTree((ExistentialTypeTree) tree);
        } else if (tree instanceof StubTree) {
            visitStubTree((StubTree) tree);
        }

        exit(tree);
    }

    public void visitPackageDef(PackageDef tree) {
    }

    public void visitClassDef(ClassDef tree) {
    }

    public void visitModuleDef(ModuleDef tree) {
    }

    public void visitValDef(ValDef tree) {
    }

    public void visitDefDef(DefDef tree) {
    }

    public void visitTypeDef(TypeDef tree) {
    }

    public void visitLabelDef(LabelDef tree) {
    }

    public void visitImport(Import tree) {
    }

    public void visitAnnotation(Annotation tree) {
    }

    public void visitTemplate(Template tree) {
    }

    public void visitBlock(Block tree) {
    }

    public void visitMatch(Match tree) {
    }

    public void visitCaseDef(CaseDef tree) {
    }

    public void visitSequence(Sequence tree) {
    }

    public void visitAlternative(Alternative tree) {
    }

    public void visitStar(Star tree) {
    }

    public void visitBind(Bind tree) {
    }

    public void visitUnApply(UnApply tree) {
    }

    public void visitArrayValue(ArrayValue tree) {
    }

    public void visitFunction(Function tree) {
    }

    public void visitAssign(Assign tree) {
    }

    public void visitIf(If tree) {
    }

    public void visitReturn(Return tree) {
    }

    public void visitTry(Try tree) {
    }

    public void visitThrow(Throw tree) {
    }

    public void visitNew(New tree) {
    }

    public void visitTyped(Typed tree) {
    }

    public void visitTypeApply(TypeApply tree) {
    }

    public void visitApply(Apply tree) {
    }

    public void visitApplyDynamic(ApplyDynamic tree) {
    }

    public void visitSuper(Super tree) {
    }

    public void visitThis(This tree) {
    }

    public void visitSelect(Select tree) {
    }

    public void visitIdent(Ident tree) {
    }

    public void visitLiteral(Literal tree) {
    }

    public void visitTypeTree(TypeTree tree) {
    }

    public void visitAnnotated(Annotated tree) {
    }

    public void visitSingletonTypeTree(SingletonTypeTree tree) {
    }

    public void visitSelectFromTypeTree(SelectFromTypeTree tree) {
    }

    public void visitCompoundTypeTree(CompoundTypeTree tree) {
    }

    public void visitAppliedTypeTree(AppliedTypeTree tree) {
    }

    public void visitTypeBoundsTree(TypeBoundsTree tree) {
    }

    public void visitExistentialTypeTree(ExistentialTypeTree tree) {
    }

    public void visitStubTree(StubTree tree) {
    }

    // ---- Helper methods
    protected Tree getParent() {
        assert astPath.size() >= 2;
        return astPath.get(astPath.size() - 2);
    }

    protected String getAstPathString() {
        StringBuilder sb = new StringBuilder();

        for (Iterator<Tree> itr = astPath.iterator(); itr.hasNext();) {
            sb.append(itr.next().getClass().getSimpleName());
            if (itr.hasNext()) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    protected void enter(Tree tree) {
        indentLevel++;
        astPath.push(tree);
        
        //debugPrintAstPath(tree);        
    }

    protected void exit(Tree node) {
        indentLevel--;
        astPath.pop();
    }

    protected int offset(Tree tree) {
        Option offsetOpt = tree.pos().offset();
        return offsetOpt.isDefined() ? (Integer) offsetOpt.get() : -1;
    }

    protected int offset(Symbol symbol) {
        Option offsetOpt = symbol.pos().offset();
        return offsetOpt.isDefined() ? (Integer) offsetOpt.get() : -1;
    }

    protected Token[] getBoundsTokens(int offset, int endOffset) {
        return new Token[]{getBoundsToken(offset), getBoundsEndToken(endOffset)};
    }

    protected Token getBoundsToken(int offset) {
        if (offset == -1) {
            return null;
        }

        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, offset);

        ts.move(offset);
        if (!ts.moveNext() && !ts.movePrevious()) {
            assert false : "Should not happen!";
        }

        Token startToken = ScalaLexUtilities.findNextNonWs(ts);
        if (startToken.isFlyweight()) {
            startToken = ts.offsetToken();
        }

        if (startToken == null) {
            System.out.println("null start token(" + offset + ")");
        }

        return startToken;
    }

    protected Token getBoundsEndToken(int endOffset) {
        if (endOffset == -1) {
            return null;
        }

        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, endOffset);

        ts.move(endOffset);
        if (!ts.movePrevious() && !ts.moveNext()) {
            assert false : "Should not happen!";
        }
        Token endToken = ScalaLexUtilities.findPreviousNonWs(ts);
        if (endToken.isFlyweight()) {
            endToken = ts.offsetToken();
        }

        return endToken;
    }

    /**
     * @Note: nameNode may contains preceding void productions, and may also contains
     * following void productions, but nameString has stripped the void productions,
     * so we should adjust nameRange according to name and its length.
     */
    protected Token getIdToken(Tree tree) {
        Symbol symbol = tree.symbol();
        assert symbol != null : "";
        // Do not use symbol.nameString() here, for example, a constructor Dog()'s nameString maybe "this"
        String name = symbol.idString();

        int offset = offset(tree);
        TokenSequence<ScalaTokenId> ts = ScalaLexUtilities.getTokenSequence(th, offset);
        ts.move(offset);
        if (!ts.moveNext() && !ts.movePrevious()) {
            assert false : "Should not happen!";
        }

        Token token;
        if (tree instanceof This) {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.This);
        } else if (tree instanceof Super) {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.Super);
        } else if (name.endsWith("expected")) {
            token = ts.token();
        } else if (name.equals("_")) {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.Wild);
        } else {
            token = ScalaLexUtilities.findNext(ts, ScalaTokenId.Identifier);
        }

        if (token.isFlyweight()) {
            token = ts.offsetToken();
        }

        //System.out.println("idToken: " + token.text().toString());

        return token;
    }

    protected void printSingleTree(Tree tree) {
        Position pos = tree.pos();
        if (pos.offset() != null) {
            Type type = tree.tpe();
            String name = "";
            String symTypeName = "";
            Symbol sym = tree.symbol();
            if (sym != null) {
                name = sym.nameString();
                symTypeName = sym.tpe().termSymbol().nameString();
            }


            System.out.println("(" + pos.line() + ":" + pos.column() + ") name=" + name + ", symTypeName=" + symTypeName + ", type=" + type + " tree: " + tree.getClass().getCanonicalName());
        }
    }

    protected void debugPrintAstPath(Tree tree) {
        Symbol symbol = tree.symbol();
        String symbolStr = "<null>";
        if (symbol != null) {
            symbolStr = symbol.toString();
        }
        
        Position pos = tree.pos();
        
        System.out.println("AstPath: " + getAstPathString() + "(" + pos.line() + ":" + pos.column() + "), symbol: " + symbolStr);
    }
}
