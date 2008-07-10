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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import scala.Option;
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
import scala.tools.nsc.util.Position;

/**
 *
 * @author dcaoyuan
 */
public class ScalaTreeVisitor {

    List<Tree> trees = new ArrayList<Tree>();
    List<Symbol> symbols = new ArrayList<Symbol>();

    public ScalaTreeVisitor(Tree tree) {
        visit(tree);
        Collections.sort(trees, new TreeComparator());
        Collections.sort(symbols, new SymbolComparator());
        //printTrees();
    }

    public <T extends Tree> T findTreeAt(Class<T> clazz, int offset) {
        int low = 0;
        int high = trees.size() - 1;
        while (low <= high) {
            int mid = (low + high) >> 1;
            Tree middle = trees.get(mid);
            if (offset < offset(middle)) {
                high = mid - 1;
            } else if (offset >= offset(middle)) {
                low = mid + 1;
            } else {
                if (clazz.isInstance(mid)) {
                    return (T) middle;
                }
            }
        }

        return null;
    }

    public Symbol findSymbolAt(int offset, String name, TokenId tokenId) {
        int low = 0;
        int high = trees.size() - 1;
        while (low <= high) {
            int mid = (low + high) >> 1;
            Tree midOne = trees.get(mid);
            if (offset < offset(midOne)) {
                high = mid - 1;
            } else if (offset > offset(midOne)) {
                low = mid + 1;
            } else {
                low = mid + 1;
                Symbol symbol = midOne.symbol();
                if (symbol != null) {
                    if (tokenId == ScalaTokenId.This) {
                        if (midOne instanceof This) {
                            return symbol;
                        }
                    } else if (tokenId == ScalaTokenId.Super) {
                        if (midOne instanceof Super) {
                            return symbol;
                        }
                    } else {
                        if (symbol.nameString().equals(name)) {
                            return symbol;
                        }
                    }
                }
            }
        }

        return null;
    }

    private int offset(Tree tree) {
        Option offsetOpt = tree.pos().offset();
        return offsetOpt.isDefined() ? (Integer) offsetOpt.get() : -1;
    }

    private void visit(scala.List trees) {
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
        } else {
            System.out.println("Try to visit: " + head);
        }

        visit(trees.tail());
    }

    private void visit(Tree tree) {
        if (tree == null) {
            return;
        }

        trees.add(tree);
        Symbol symbol = tree.symbol();
        if (symbol != null) {
            symbols.add(symbol);
        }

        if (tree instanceof ClassDef) {
            ClassDef tree1 = (ClassDef) tree;
            visit(tree1.tparams());
            visit(tree1.impl());
        } else if (tree instanceof PackageDef) {
            PackageDef tree1 = (PackageDef) tree;
            visit(tree1.stats());
        } else if (tree instanceof ModuleDef) {
            ModuleDef tree1 = (ModuleDef) tree;
            visit(tree1.impl());
        } else if (tree instanceof ValDef) {
            ValDef tree1 = (ValDef) tree;
            visit(tree1.tpt());
            visit(tree1.rhs());
        } else if (tree instanceof DefDef) {
            DefDef tree1 = (DefDef) tree;
            visit(tree1.tparams());
            visit(tree1.vparamss());
            visit(tree1.tpt());
            visit(tree1.rhs());
        } else if (tree instanceof TypeDef) {
            TypeDef tree1 = (TypeDef) tree;
            visit(tree1.tparams());
            visit(tree1.rhs());
        } else if (tree instanceof LabelDef) {
            LabelDef tree1 = (LabelDef) tree;
            visit(tree1.params());
            visit(tree1.rhs());
        } else if (tree instanceof Import) {
            Import tree1 = (Import) tree;
            visit(tree1.expr());
            visit(tree1.selectors());
        } else if (tree instanceof Annotation) {
            Annotation tree1 = (Annotation) tree;
            visit(tree1.constr());
            visit(tree1.elements());
        } else if (tree instanceof Template) {
            Template tree1 = (Template) tree;
            visit(tree1.parents());
            visit(tree1.body());
            visit(tree1.self());
        } else if (tree instanceof Block) {
            Block tree1 = (Block) tree;
            visit(tree1.stats());
            visit(tree1.expr());
        } else if (tree instanceof Match) {
            Match tree1 = (Match) tree;
            visit(tree1.cases());
            visit(tree1.selector());
        } else if (tree instanceof CaseDef) {
            CaseDef tree1 = (CaseDef) tree;
            visit(tree1.body());
            visit(tree1.guard());
            visit(tree1.pat());
        } else if (tree instanceof Sequence) {
            Sequence tree1 = (Sequence) tree;
            visit(tree1.trees());
        } else if (tree instanceof Alternative) {
            Alternative tree1 = (Alternative) tree;
            visit(tree1.trees());
        } else if (tree instanceof Star) {
            Star tree1 = (Star) tree;

            visit(tree1.elem());
        } else if (tree instanceof Bind) {
            Bind tree1 = (Bind) tree;
            visit(tree1.body());
        } else if (tree instanceof UnApply) {
            UnApply tree1 = (UnApply) tree;
            visit(tree1.args());
            visit(tree1.fun());
        } else if (tree instanceof ArrayValue) {
            ArrayValue tree1 = (ArrayValue) tree;
            visit(tree1.elems());
            visit(tree1.elemtpt());
        } else if (tree instanceof Function) {
            Function tree1 = (Function) tree;
            visit(tree1.body());
            visit(tree1.vparams());
        } else if (tree instanceof Assign) {
            Assign tree1 = (Assign) tree;
            visit(tree1.lhs());
            visit(tree1.rhs());
        } else if (tree instanceof If) {
            If tree1 = (If) tree;
            visit(tree1.cond());
            visit(tree1.elsep());
            visit(tree1.thenp());
        } else if (tree instanceof Return) {
            Return tree1 = (Return) tree;
            visit(tree1.expr());
        } else if (tree instanceof Try) {
            Try tree1 = (Try) tree;
            visit(tree1.block());
            visit(tree1.catches());
            visit(tree1.finalizer());
        } else if (tree instanceof Throw) {
            Throw tree1 = (Throw) tree;
            visit(tree1.expr());
        } else if (tree instanceof New) {
            New tree1 = (New) tree;
            visit(tree1.tpt());
        } else if (tree instanceof Typed) {
            Typed tree1 = (Typed) tree;
            visit(tree1.expr());
            visit(tree1.tpt());
        } else if (tree instanceof TypeApply) {
            TypeApply tree1 = (TypeApply) tree;
            visit(tree1.args());
            visit(tree1.fun());
        } else if (tree instanceof Apply) {
            Apply tree1 = (Apply) tree;
            visit(tree1.args());
            visit(tree1.fun());
        } else if (tree instanceof ApplyDynamic) {
            ApplyDynamic tree1 = (ApplyDynamic) tree;
            visit(tree1.args());
            visit(tree1.qual());
        } else if (tree instanceof Super) {
            Super tree1 = (Super) tree;
        } else if (tree instanceof This) {
            This tree1 = (This) tree;
        } else if (tree instanceof Select) {
            Select tree1 = (Select) tree;
            visit(tree1.qualifier());
        } else if (tree instanceof Ident) {
            Ident tree1 = (Ident) tree;
        } else if (tree instanceof Literal) {
            Literal tree1 = (Literal) tree;
        } else if (tree instanceof TypeTree) {
            TypeTree tree1 = (TypeTree) tree;
            visit(tree1.original());
        } else if (tree instanceof Annotated) {
            Annotated tree1 = (Annotated) tree;
            visit(tree1.annot());
            visit(tree1.arg());
        } else if (tree instanceof SingletonTypeTree) {
            SingletonTypeTree tree1 = (SingletonTypeTree) tree;
            visit(tree1.ref());
        } else if (tree instanceof SelectFromTypeTree) {
            SelectFromTypeTree tree1 = (SelectFromTypeTree) tree;
            visit(tree1.qualifier());
        } else if (tree instanceof CompoundTypeTree) {
            CompoundTypeTree tree1 = (CompoundTypeTree) tree;
            visit(tree1.templ());
        } else if (tree instanceof AppliedTypeTree) {
            AppliedTypeTree tree1 = (AppliedTypeTree) tree;
            visit(tree1.args());
            visit(tree1.tpt());
        } else if (tree instanceof TypeBoundsTree) {
            TypeBoundsTree tree1 = (TypeBoundsTree) tree;
            visit(tree1.hi());
            visit(tree1.lo());
        } else if (tree instanceof ExistentialTypeTree) {
            ExistentialTypeTree tree1 = (ExistentialTypeTree) tree;
            visit(tree1.tpt());
            visit(tree1.whereClauses());
        } else if (tree instanceof StubTree) {
            StubTree tree1 = (StubTree) tree;
        // do nothing
        }

    }

    // --- helper method
    private void printTrees() {
        for (Tree tree : trees) {
            printTree(tree);
        }
    }

    private void printTree(Tree tree) {
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

    private static class TreeComparator implements Comparator<Tree> {

        public TreeComparator() {
        }

        public int compare(Tree o1, Tree o2) {
            Option offsetA = o1.pos().offset();
            Option offsetB = o2.pos().offset();
            int offset1 = offsetA.isDefined() ? (Integer) offsetA.get() : -1;
            int offset2 = offsetB.isDefined() ? (Integer) offsetB.get() : -1;

            return offset1 < offset2 ? -1 : 1;
        }
    }

    private static class SymbolComparator implements Comparator<Symbol> {

        public SymbolComparator() {
        }

        public int compare(Symbol o1, Symbol o2) {
            Option offsetA = o1.pos().offset();
            Option offsetB = o2.pos().offset();
            int offset1 = offsetA.isDefined() ? (Integer) offsetA.get() : -1;
            int offset2 = offsetB.isDefined() ? (Integer) offsetB.get() : -1;

            return offset1 < offset2 ? -1 : 1;
        }
    }
}
