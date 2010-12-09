/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2010 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import javax.lang.model.type.TypeMirror;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.GeneratorUtilities;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.jackpot.code.spi.Hint;
import org.netbeans.modules.java.hints.jackpot.code.spi.TriggerPattern;
import org.netbeans.modules.java.hints.jackpot.impl.Utilities;
import org.netbeans.modules.java.hints.jackpot.spi.HintContext;
import org.netbeans.modules.java.hints.jackpot.spi.JavaFix;
import org.netbeans.modules.java.hints.jackpot.spi.support.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.ErrorDescription;

/**
 *
 * @author lahvac
 */
@Hint(category="apisupport") // XXX move to apisupport.refactoring once the required packages are at least available to friends
public class RPD {

    @TriggerPattern(value="org.openide.util.RequestProcessor.getDefault()")
    public static ErrorDescription hint(HintContext ctx) {
        return ErrorDescriptionFactory.forTree(ctx, ctx.getPath(), "RequestProcessor.getDefault()", JavaFix.toEditorFix(new FixImpl(ctx.getInfo(), ctx.getPath())));
    }

    private static final class FixImpl extends JavaFix {

        private static final Map<WorkingCopy, String> fieldCache = new WeakHashMap<WorkingCopy, String>();
        
        public FixImpl(CompilationInfo info, TreePath path) {
            super(info, path);
        }

        @Override
        protected void performRewrite(WorkingCopy wc, TreePath tp, UpgradeUICallback callback) {
            String fieldName = fieldCache.get(wc);
            Scope s = Utilities.constructScope(wc, Collections.<String, TypeMirror>emptyMap());

            if (fieldName == null) {
                TreePath topLevel = tp;

                while (topLevel.getParentPath().getLeaf().getKind() != Kind.COMPILATION_UNIT) {
                    topLevel = topLevel.getParentPath();
                }

                ClassTree ct = (ClassTree) topLevel.getLeaf();
                fieldName = "DEFAULT_WORKER";
                Tree field = Utilities.parseAndAttribute(wc, "private static final org.openide.util.RequestProcessor " + fieldName + " = new org.openide.util.RequestProcessor(" + topLevel.getCompilationUnit().getPackageName() + "." + ct.getSimpleName() + ".class.getName(), 50, false, false);", s);

                wc.rewrite(ct, GeneratorUtilities.get(wc).insertClassMember(ct, GeneratorUtilities.get(wc).importFQNs(field)));
                fieldCache.put(wc, fieldName);
            }

            wc.rewrite(tp.getLeaf(), wc.getTreeMaker().Identifier(fieldName));
        }

        @Override
        protected String getText() {
            return "Create own instance of RequestProcessor";
        }

    }

}
