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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.modules.gsf.api.ElementKind;
import org.netbeans.modules.scala.editing.lexer.ScalaTokenId;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
import scala.tools.nsc.util.BatchSourceFile;

/**
 *
 * @author Caoyuan Deng
 */
public class AstTreeVisitor extends AstVisitor {

    private final FileObject fo;

    public AstTreeVisitor(Tree rootTree, TokenHierarchy th, BatchSourceFile sourceFile) {
        super(rootTree, th, sourceFile);
        setBoundsEndToken(rootScope);
        if (sourceFile != null) {
            File file = new File(sourceFile.path());
            if (file != null && file.exists()) {
                // it's a real file and not archive file
                fo = FileUtil.toFileObject(file);
            } else {
                fo = null;
            }
        } else {
            fo = null;
        }
    }

    private void setBoundsEndToken(AstScope fromScope) {
        assert fromScope.isScopesSorted() == false;

        List<AstScope> children = fromScope.getSubScopes();
        Iterator<AstScope> itr = children.iterator();
        AstScope curr = itr.hasNext() ? itr.next() : null;
        while (curr != null) {
            if (itr.hasNext()) {
                AstScope next = itr.next();
                int offset = next.getBoundsOffset(th);
                if (offset != -1) {
                    Token endToken = getBoundsEndToken(offset - 1);
                    curr.setBoundsEndToken(endToken);
                } else {
                    System.out.println("Scope without start token: " + next);
                }
                curr = next;
            } else {
                AstScope parent = curr.getParent();
                if (parent != null) {
                    curr.setBoundsEndToken(parent.getBoundsEndToken());
                }
                curr = null;
            }
        }

        for (AstScope child : children) {
            setBoundsEndToken(child);
        }
    }

    @Override
    public void visitPackageDef(PackageDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, ElementKind.PACKAGE, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        scopes.push(scope);
        visit(tree.stats());
        scopes.pop();
    }

    @Override
    public void visitClassDef(ClassDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, ElementKind.CLASS, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        scopes.push(scope);
        visit(tree.tparams());
        visit(tree.impl());
        scopes.pop();
    }

    @Override
    public void visitModuleDef(ModuleDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, ElementKind.MODULE, fo);
        if (scopes.peek().addDef(def)) {
            if (debug) {
                System.out.println("\tAdded: " + def);
            }
        }

        scopes.push(scope);
        visit(tree.impl());
        scopes.pop();
    }

    @Override
    public void visitValDef(ValDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        ElementKind kind = ElementKind.VARIABLE;
        Tree parent = getCurrentParent();
        if (parent instanceof Template) {
            kind = ElementKind.FIELD;
        } else if (parent instanceof DefDef) {
            kind = ElementKind.PARAMETER;
        }

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, kind, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        scopes.push(scope);
        visit(tree.tpt());
        visit(tree.rhs());
        scopes.pop();
    }

    @Override
    public void visitDefDef(DefDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        ElementKind kind = tree.symbol().isConstructor() ? ElementKind.CONSTRUCTOR : ElementKind.METHOD;

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, kind, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        scopes.push(scope);
        visit(tree.tparams());
        visit(tree.vparamss());
        visit(tree.tpt());
        visit(tree.rhs());
        scopes.pop();
    }

    @Override
    public void visitTypeDef(TypeDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, ElementKind.CLASS, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        scopes.push(scope);
        visit(tree.tparams());
        visit(tree.rhs());
        scopes.pop();
    }

    @Override
    public void visitLabelDef(LabelDef tree) {
        visit(tree.params());
        visit(tree.rhs());
    }

    @Override
    public void visitTemplate(Template tree) {
        /** @Note
         * Do not start a new scope for template, since the scope should be Class/Object/Module def 
         **/
        visit(tree.self());
        visit(tree.parents());
        visit(tree.body());
    }

    @Override
    public void visitImport(Import tree) {
        visit(tree.selectors());
        visit(tree.expr());
    }

    @Override
    public void visitAnnotation(Annotation tree) {
        visit(tree.constr());
        visit(tree.elements());
    }

    @Override
    public void visitBlock(Block tree) {
        Tree parent = getCurrentParent();
        if (parent != null && parent instanceof DefDef) {
            AstScope scope = new AstScope(getBoundsToken(offset(tree)));
            scopes.peek().addScope(scope);

            scopes.push(scope);
            visit(tree.stats());
            visit(tree.expr());
            scopes.pop();
        } else {
            visit(tree.stats());
            visit(tree.expr());
        }
    }

    @Override
    public void visitMatch(Match tree) {
        visit(tree.selector());
        visit(tree.cases());
    }

    @Override
    public void visitCaseDef(CaseDef tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        scopes.push(scope);
        visit(tree.pat());
        visit(tree.guard());
        visit(tree.body());
        scopes.pop();
    }

    @Override
    public void visitSequence(Sequence tree) {
        visit(tree.trees());
    }

    @Override
    public void visitAlternative(Alternative tree) {
        visit(tree.trees());
    }

    @Override
    public void visitStar(Star tree) {
        visit(tree.elem());
    }

    @Override
    public void visitBind(Bind tree) {
        AstScope scope = new AstScope(getBoundsToken(offset(tree)));
        scopes.peek().addScope(scope);

        AstDef def = new AstDef(tree.symbol(), getIdToken(tree), scope, ElementKind.VARIABLE, fo);
        if (scopes.peek().addDef(def)) {
            info("\tAdded: ", def);
        }

        visit(tree.body());
    }

    @Override
    public void visitUnApply(UnApply tree) {
        visit(tree.fun());
        visit(tree.args());
    }

    @Override
    public void visitArrayValue(ArrayValue tree) {
        visit(tree.elems());
        visit(tree.elemtpt());
    }

    @Override
    public void visitFunction(Function tree) {
        visit(tree.vparams());
        visit(tree.body());
    }

    @Override
    public void visitAssign(Assign tree) {
        visit(tree.lhs());
        visit(tree.rhs());
    }

    @Override
    public void visitIf(If tree) {
        visit(tree.cond());
        visit(tree.thenp());
        visit(tree.elsep());
    }

    @Override
    public void visitReturn(Return tree) {
        visit(tree.expr());
    }

    @Override
    public void visitTry(Try tree) {
        visit(tree.block());
        visit(tree.catches());
        visit(tree.finalizer());
    }

    @Override
    public void visitThrow(Throw tree) {
        visit(tree.expr());
    }

    @Override
    public void visitNew(New tree) {
        visit(tree.tpt());
    }

    @Override
    public void visitTyped(Typed tree) {
        visit(tree.expr());
        visit(tree.tpt());
    }

    @Override
    public void visitTypeApply(TypeApply tree) {
        /**
         * @todo just ignore type apply's fun, it's fun apply, for example:
         * val tuple = (a, b, c)
         * where (a, c, c) will be Apply::TypeApply
         */
        //visit(tree.fun());
        visit(tree.args());
    }

    @Override
    public void visitApply(Apply tree) {
        AstExpr expr = new AstExpr();
        exprs.peek().addSubExpr(expr);

        exprs.push(expr);
        Tree fun = tree.fun();
        if (fun instanceof TypeApply) {
            // do not visit fun here, the fun will be visited in other form of Tree
        } else {
            visit(fun);
        }
        visit(tree.args());
        exprs.pop();
    }

    @Override
    public void visitApplyDynamic(ApplyDynamic tree) {
        visit(tree.qual());
        visit(tree.args());
    }

    @Override
    public void visitSuper(Super tree) {
        Symbol symbol = tree.symbol();
        Token idToken = getIdToken(tree);
        if (idToken.id() == ScalaTokenId.Super && !symbol.isPackageClass()) {
            AstRef ref = new AstRef(symbol, idToken);
            if (scopes.peek().addRef(ref)) {
                info("\tAdded: ", ref);
            }
        }
    }

    @Override
    public void visitThis(This tree) {
        Symbol symbol = tree.symbol();
        Token idToken = getIdToken(tree);
        if (idToken.id() == ScalaTokenId.This && !symbol.isPackageClass()) {
            AstRef ref = new AstRef(symbol, idToken);
            if (scopes.peek().addRef(ref)) {
                info("\tAdded: ", ref);
            }
        }
    }

    @Override
    public void visitSelect(Select tree) {
        Token idToken = getIdToken(tree);
        AstRef ref = new AstRef(tree.symbol(), idToken);
        if (scopes.peek().addRef(ref)) {
            info("\tAdded: ", ref);
        }

        AstExpr expr = new AstExpr();
        exprs.peek().addSubExpr(expr);

        exprs.push(expr);
        // For Select tree, should its idToken to the same expr
        exprs.peek().addToken(idToken);
        visit(tree.qualifier());
        exprs.pop();
    }

    @Override
    public void visitIdent(Ident tree) {
        Symbol symbol = tree.symbol();
        if (symbol != null) {
            if (symbol.toString().equals("<none>")) {
                //System.out.println("A NoSymbol found");
            }
            AstRef ref = new AstRef(symbol, getIdToken(tree));
            if (scopes.peek().addRef(ref)) {
                info("\tAdded: ", ref);
            }
        }
    }

    @Override
    public void visitLiteral(Literal tree) {
        // none symbol
    }

    @Override
    public void visitAnnotated(Annotated tree) {
        visit(tree.annot());
        visit(tree.arg());
    }

    @Override
    public void visitTypeTree(TypeTree tree) {
        AstRef ref = new AstRef(tree.symbol(), getIdToken(tree));
        if (scopes.peek().addRef(ref)) {
            info("\tAdded: ", ref);
        }

        visit(tree.original());
    }

    @Override
    public void visitSingletonTypeTree(SingletonTypeTree tree) {
        visit(tree.ref());
    }

    @Override
    public void visitSelectFromTypeTree(SelectFromTypeTree tree) {
        visit(tree.qualifier());
    }

    @Override
    public void visitCompoundTypeTree(CompoundTypeTree tree) {
        visit(tree.templ());
    }

    @Override
    public void visitAppliedTypeTree(AppliedTypeTree tree) {
        visit(tree.args());
        visit(tree.tpt());
    }

    @Override
    public void visitTypeBoundsTree(TypeBoundsTree tree) {
        visit(tree.hi());
        visit(tree.lo());
    }

    @Override
    public void visitExistentialTypeTree(ExistentialTypeTree tree) {
        visit(tree.tpt());
        visit(tree.whereClauses());
    }

    @Override
    public void visitStubTree(StubTree tree) {
    }
}
