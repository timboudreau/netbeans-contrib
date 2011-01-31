/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.hints.spi.ErrorRule;
import org.netbeans.modules.java.hints.spi.ErrorRule.Data;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;

/**
 *
 * @author Jan Lahoda
 */
public class MakeStatic implements ErrorRule<Void> {

    private static final Set<String> CODES = new HashSet<String>(Arrays.asList("compiler.err.non-static.cant.be.ref"));

    public Set<String> getCodes() {
        return CODES;
    }

    public List<Fix> run(final CompilationInfo info, String diagnosticKey, int offset, TreePath treePath, Data<Void> data) {
        Element el = info.getTrees().getElement(treePath);
        final TypeElement topLevel = info.getElementUtilities().outermostTypeElement(el);

        //XXX: necessary to verify that el is from the current comp. unit?
        
        if (el.getModifiers().contains(Modifier.STATIC)) {
            //strange, but:
            return null;
        }
        
        if (el.getModifiers().contains(Modifier.ABSTRACT)) {
            //strange, but:
            return null;
        }

        TreePath source = info.getTrees().getPath(el);

        if (source == null) {
            //also strange:
            return null;
        }

        class FoundInstanceReference extends Error {}
        
        TreePathScanner<Void, Void> s = new TreePathScanner<Void, Void>() {
            @Override
            public Void scan(Tree tree, Void p) {
                if (tree == null) return null;
                Element e = info.getTrees().getElement(new TreePath(getCurrentPath(), tree));
                
                if (e != null && KINDS_TO_CHECK.contains(e.getKind())) {
                    TypeElement currentTopLevel = info.getElementUtilities().outermostTypeElement(e);

                    if (topLevel.equals(currentTopLevel)) {
                        if (!e.getModifiers().contains(Modifier.STATIC)) {
                            throw new FoundInstanceReference();
                        }
                    }
                }
                
                return super.scan(tree, p);
            }
        };

        boolean safe;

        try {
            safe = true;
            switch (source.getLeaf().getKind()) {
                case METHOD:
                    MethodTree method = (MethodTree) source.getLeaf();
                    
                    if (method.getBody() != null) {
                        s.scan(new TreePath(treePath, method.getBody()), null);
                    }
                    
                    break;
                case VARIABLE:
                    ExpressionTree initializer = ((VariableTree) source.getLeaf()).getInitializer();

                    if (initializer != null)
                        s.scan(new TreePath(treePath,initializer), null);
                    break;
            }
        } catch (FoundInstanceReference e) {
            safe = false;
        }

        Fix f = new FixImpl(el.getSimpleName().toString(), TreePathHandle.create(source, info), safe);

        return Collections.singletonList(f);
    }

    private static final Set<ElementKind> KINDS_TO_CHECK = EnumSet.of(ElementKind.FIELD, ElementKind.METHOD, ElementKind.CLASS);
    
    public String getId() {
        return MakeStatic.class.getName();
    }

    public String getDisplayName() {
        return "Make Static";
    }

    public void cancel() {}

    static final class FixImpl implements Fix {

        private final String name;
        private final TreePathHandle source;
        private final boolean safe;

        public FixImpl(String name, TreePathHandle source, boolean safe) {
            this.name = name;
            this.source = source;
            this.safe = safe;
        }

        public String getText() {
            return "Make " + name + " static" + (!safe ? " (result may not compile)" : "");
        }

        public ChangeInfo implement() throws Exception {
            JavaSource.forFileObject(source.getFileObject()).runModificationTask(new Task<WorkingCopy>() {
                public void run(WorkingCopy wc) throws Exception {
                    wc.toPhase(Phase.RESOLVED);

                    TreePath source = FixImpl.this.source.resolve(wc);

                    if (source == null) {
                        //XXX: log
                        return ;
                    }

                    ModifiersTree orig = null;

                    switch (source.getLeaf().getKind()) {
                        case METHOD:
                            orig = ((MethodTree) source.getLeaf()).getModifiers();
                            break;
                        case VARIABLE:
                            orig = ((VariableTree) source.getLeaf()).getModifiers();
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }

                    Set<Modifier> mods = new HashSet<Modifier>(orig.getFlags());

                    mods.add(Modifier.STATIC);

                    Tree nue = wc.getTreeMaker().Modifiers(mods, orig.getAnnotations());

                    wc.rewrite(orig, nue);
                }
            }).commit();

            return null;
        }

        String getName() {
            return name;
        }

        boolean isSafe() {
            return safe;
        }

    }
}
