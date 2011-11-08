/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javahints;

import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.java.hints.spi.AbstractHint;
import org.netbeans.spi.editor.hints.ErrorDescription;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.source.TreeUtilities;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.openide.util.NbBundle.Messages;

public class PrivateTopLevelClass extends AbstractHint {

    public PrivateTopLevelClass() {
        super(true, true, HintSeverity.WARNING);
    }

    @Override public String getDescription() {
        return getDisplayName(); // XXX how do these differ?
    }

    @Override public Set<Kind> getTreeKinds() {
        return TreeUtilities.CLASS_TREE_KINDS;
    }

    @Override public List<ErrorDescription> run(CompilationInfo info, TreePath treePath) {
        VisitorImpl v = new VisitorImpl(info);
        v.scan(treePath, null);
        return v.warnings;
    }

    @Override public String getId() {
        return PrivateTopLevelClass.class.getName();
    }

    @Messages("PrivateTopLevelClass.displayName=JDK 1.0-style top-level private class")
    @Override public String getDisplayName() {
        return Bundle.PrivateTopLevelClass_displayName();
    }

    @Override public void cancel() {}

    private final class VisitorImpl extends TreePathScanner<Void,Void> {

        private final CompilationInfo info;
        private final List<ErrorDescription> warnings;

        VisitorImpl(CompilationInfo info) {
            this.info = info;
            this.warnings = new LinkedList<ErrorDescription>();
        }

        @Override public Void visitClass(ClassTree node, Void p) {
            if (!node.getSimpleName().contentEquals(info.getFileObject().getName())) {
                for (Tree t : info.getTrees().getPath(info.getCompilationUnit(), node)) {
                    if (t != node && TreeUtilities.CLASS_TREE_KINDS.contains(t.getKind())) {
                        return null;
                    }
                }
                warnings.add(ErrorDescriptionFactory.createErrorDescription(
                        getSeverity().toEditorSeverity(),
                        getDisplayName(),
                        // XXX add a fix to make into a private static nested class
                        // e.g.: https://issues.apache.org/jira/secure/attachment/12502864/LUCENE-3525.diff
                        info.getFileObject(),
                        // XXX works only if there are no modifiers; how to find location of the identifier?
                        (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), node) + 6,
                        (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), node) + node.getSimpleName().length() + 6
                        ));
            }
            return null;
        }

    }

}
