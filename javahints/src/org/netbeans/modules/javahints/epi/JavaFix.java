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

package org.netbeans.modules.javahints.epi;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.javahints.batch.JavaFixImpl;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.Fix;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Jan Lahoda
 */
public abstract class JavaFix {

    private final TreePathHandle handle;

    protected JavaFix(CompilationInfo info, TreePath tp) {
        this.handle = TreePathHandle.create(tp, info);
    }

    protected abstract String getText();

    protected abstract void performRewrite(WorkingCopy wc, TreePath tp);

    final ChangeInfo process(WorkingCopy wc) throws Exception {
        TreePath tp = handle.resolve(wc);

        if (tp == null) {
            Logger.getLogger(JavaFix.class.getName()).log(Level.SEVERE, "Cannot resolve handle={0}", handle);
            return null;
        }

        performRewrite(wc, tp);

        return null;
    }

    final FileObject getFile() {
        return handle.getFileObject();
    }

    public static Fix rewriteFix(CompilationInfo info, final String displayName, TreePath what, final String to, TreePath... parameters) {
        Map<String, TreePath> params = new HashMap<String, TreePath>();
        int c = 1;

        for (TreePath t : parameters) {
            params.put("$" + c, t);
            c++;
        }

        return rewriteFix(info, displayName, what, to, params);
    }

    public static Fix rewriteFix(CompilationInfo info, final String displayName, TreePath what, final String to, Map<String, TreePath> parameters) {
        final Map<String, TreePathHandle> params = new HashMap<String, TreePathHandle>();

        for (Entry<String, TreePath> e : parameters.entrySet()) {
            params.put(e.getKey(), TreePathHandle.create(e.getValue(), info));
        }

        return toEditorFix(new JavaFix(info, what) {
            @Override
            protected String getText() {
                return displayName;
            }
            @Override
            protected void performRewrite(final WorkingCopy wc, TreePath tp) {
                final Map<String, TreePath> parameters = new HashMap<String, TreePath>();

                for (Entry<String, TreePathHandle> e : params.entrySet()) {
                    TreePath p = e.getValue().resolve(wc);

                    if (tp == null) {
                        Logger.getLogger(JavaFix.class.getName()).log(Level.SEVERE, "Cannot resolve handle={0}", e.getValue());
                    }

                    parameters.put(e.getKey(), p);
                }

                Tree parsed = wc.getTreeUtilities().parseExpression(to, new SourcePositions[1]);
                Scope s = wc.getTrees().getScope(tp);
                
                wc.getTreeUtilities().attributeTree(parsed, s);

                new TreePathScanner<Void, Void>() {
                    @Override
                    public Void visitIdentifier(IdentifierTree node, Void p) {
                        TreePath tp = parameters.get(node.getName().toString());

                        if (tp != null) {
                            wc.rewrite(node, tp.getLeaf());
                        }

                        return super.visitIdentifier(node, p);
                    }
                    @Override
                    public Void visitMemberSelect(MemberSelectTree node, Void p) {
                        Element e = wc.getTrees().getElement(getCurrentPath());

                        if (e == null || (e.getKind() == ElementKind.CLASS && ((TypeElement) e).asType().getKind() == TypeKind.ERROR)) {
                            return super.visitMemberSelect(node, p);
                        }

                        wc.rewrite(node, wc.getTreeMaker().QualIdent(e));

                        return null;
                    }
                }.scan(new TreePath(new TreePath(tp.getCompilationUnit()), parsed), null);

                wc.rewrite(tp.getLeaf(), parsed);
            }
        });
    }
    
    public static Fix toEditorFix(final JavaFix jf) {
        return new JavaFixImpl(jf);
    }

    static {
        JavaFixImpl.Accessor.INSTANCE = new JavaFixImpl.Accessor() {
            @Override
            public String getText(JavaFix jf) {
                return jf.getText();
            }
            @Override
            public ChangeInfo process(JavaFix jf, WorkingCopy wc) throws Exception {
                return jf.process(wc);
            }
            @Override
            public FileObject getFile(JavaFix jf) {
                return jf.getFile();
            }
        };
    }
    
}
