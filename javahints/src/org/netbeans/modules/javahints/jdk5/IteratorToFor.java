/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints.jdk5;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.java.hints.ErrorDescriptionFactory;
import org.netbeans.spi.java.hints.Hint;
import org.netbeans.spi.java.hints.HintContext;
import org.netbeans.spi.java.hints.JavaFix;
import org.netbeans.spi.java.hints.JavaFix.TransformationContext;
import org.netbeans.spi.java.hints.TriggerPattern;
import org.openide.util.NbBundle.Messages;

@Hint(displayName="#DN_IteratorToFor", description="#DESC_IteratorToFor", category="rules15")
@Messages({
    "DN_IteratorToFor=Use JDK 5 for-loop",
    "DESC_IteratorToFor=Replaces simple uses of Iterator with a corresponding for-loop.",
    "ERR_IteratorToFor=Use of Iterator for simple loop",
    "FIX_IteratorToFor=Convert to for-loop"
})
public class IteratorToFor {

    @TriggerPattern("java.util.Iterator $it = $coll.iterator(); while ($it.hasNext()) {$type $elem = ($type) $it.next(); $rest$;}")
    public static ErrorDescription whileIdiom(HintContext ctx) {
        if (uses(ctx, ctx.getMultiVariables().get("$rest$"), ctx.getVariables().get("$it"))) {
            return null;
        }
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), Bundle.ERR_IteratorToFor(), new WhileFix(ctx).toEditorFix());
    }

    // like JavaFixUtilities.rewriteFix(..., "for ($type $elem : $coll) {$rest$;}"))
    // but does not mess up interior comments
    private static final class WhileFix extends JavaFix {

        private final HintContext hctx;

        WhileFix(HintContext hctx) {
            super(hctx.getInfo(), hctx.getPath());
            // XXX #211273 comment #3: should not be keeping hctx here, ought to rewrite
            this.hctx = hctx;
        }

        @Override protected String getText() {
            return Bundle.FIX_IteratorToFor();
        }

        @Override protected void performRewrite(TransformationContext ctx) {
            WorkingCopy wc = ctx.getWorkingCopy();
            TreeMaker tm = wc.getTreeMaker();
            Map<String,TreePath> vars = hctx.getVariables();
            Map<String,Collection<? extends TreePath>> multivars = hctx.getMultiVariables();
            BlockTree block = (BlockTree) vars.get("$_").getLeaf();
            List<StatementTree> stmts = new ArrayList<StatementTree>(block.getStatements());
            boolean deleted = stmts.remove((StatementTree) vars.get("$it").getLeaf());
            assert deleted;
            int idx = stmts.indexOf((StatementTree) vars.get("$elem").getParentPath().getParentPath().getLeaf());
            assert idx != -1;
            VariableTree decl = tm.Variable(tm.Modifiers(Collections.<Modifier>emptySet()), hctx.getVariableNames().get("$elem"), vars.get("$type").getLeaf(), null);
            ExpressionTree expr = (ExpressionTree) vars.get("$coll").getLeaf();
            List<StatementTree> rest = new ArrayList<StatementTree>();
            for (TreePath p : multivars.get("$rest$")) {
                rest.add((StatementTree) p.getLeaf());
            }
            StatementTree body = tm.Block(rest, false);
            stmts.set(idx, tm.EnhancedForLoop(decl, expr, body));
            wc.rewrite(block, tm.Block(stmts, false));
        }

    }

    @TriggerPattern("for (java.util.Iterator $it = $coll.iterator(); $it.hasNext(); ) {$type $elem = ($type) $it.next(); $rest$;}")
    public static ErrorDescription forIdiom(HintContext ctx) {
        if (uses(ctx, ctx.getMultiVariables().get("$rest$"), ctx.getVariables().get("$it"))) {
            return null;
        }
        return ErrorDescriptionFactory.forName(ctx, ctx.getPath(), Bundle.ERR_IteratorToFor(), new ForFix(ctx).toEditorFix());
    }

    private static final class ForFix extends JavaFix {

        private final HintContext hctx;

        ForFix(HintContext hctx) {
            super(hctx.getInfo(), hctx.getPath());
            this.hctx = hctx;
        }

        @Override protected String getText() {
            return Bundle.FIX_IteratorToFor();
        }

        @Override protected void performRewrite(TransformationContext ctx) {
            WorkingCopy wc = ctx.getWorkingCopy();
            TreeMaker tm = wc.getTreeMaker();
            Map<String,TreePath> vars = hctx.getVariables();
            Map<String,Collection<? extends TreePath>> multivars = hctx.getMultiVariables();
            VariableTree decl = tm.Variable(tm.Modifiers(Collections.<Modifier>emptySet()), hctx.getVariableNames().get("$elem"), vars.get("$type").getLeaf(), null);
            ExpressionTree expr = (ExpressionTree) vars.get("$coll").getLeaf();
            List<StatementTree> rest = new ArrayList<StatementTree>();
            for (TreePath p : multivars.get("$rest$")) {
                rest.add((StatementTree) p.getLeaf());
            }
            StatementTree body = tm.Block(rest, false);
            wc.rewrite(ctx.getPath().getLeaf(), tm.EnhancedForLoop(decl, expr, body));
        }

    }

    // adapted from org.netbeans.modules.java.hints.declarative.conditionapi.Matcher.referencedIn
    private static boolean uses(final HintContext ctx, Collection<? extends TreePath> statements, TreePath var) {
        final Element e = ctx.getInfo().getTrees().getElement(var);
        for (TreePath tp : statements) {
            boolean occurs = Boolean.TRUE.equals(new TreePathScanner<Boolean, Void>() {
                @Override public Boolean scan(Tree tree, Void p) {
                    if (tree == null) {
                        return false;
                    }
                    TreePath currentPath = new TreePath(getCurrentPath(), tree);
                    Element currentElement = ctx.getInfo().getTrees().getElement(currentPath);
                    if (e.equals(currentElement)) {
                        return true;
                    }
                    return super.scan(tree, p);
                }
                @Override public Boolean reduce(Boolean r1, Boolean r2) {
                    if (r1 == null) {
                        return r2;
                    }
                    if (r2 == null) {
                        return r1;
                    }
                    return r1 || r2;
                }
            }.scan(tp, null));
            if (occurs) {
                return true;
            }
        }
        return false;
    }

}
