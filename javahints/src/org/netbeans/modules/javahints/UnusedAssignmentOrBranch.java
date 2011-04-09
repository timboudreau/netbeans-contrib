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

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.api.editor.settings.EditorStyleConstants;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.EditorAwareJavaSourceTaskFactory;
import org.netbeans.modules.java.hints.introduce.Flow;
import org.netbeans.modules.java.hints.introduce.Flow.FlowResult;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lahvac
 */
public class UnusedAssignmentOrBranch implements CancellableTask<CompilationInfo> {

    private final AtomicBoolean cancel = new AtomicBoolean();
    
    public void run(final CompilationInfo info) throws Exception {
        cancel.set(false);
        
        Document doc = info.getDocument();

        if (doc == null) return ;

        FlowResult flow = Flow.assignmentsForUse(info, cancel);
        final Set<Tree> usedAssignments = new HashSet<Tree>();

        for (Iterable<? extends TreePath> i : flow.getAssignmentsForUse().values()) {
            for (TreePath tp : i) {
                usedAssignments.add(tp.getLeaf());
            }
        }

        final OffsetsBag unusedValue = new OffsetsBag(doc);

        new TreePathScanner<Void, Void>() {
            @Override public Void visitAssignment(AssignmentTree node, Void p) {
                if (!usedAssignments.contains(node.getExpression())) {
                    unusedValue(node.getExpression());
                }
                return super.visitAssignment(node, p);
            }
            @Override public Void visitVariable(VariableTree node, Void p) {
                if (node.getInitializer() != null && !usedAssignments.contains(node.getInitializer())) {
                    unusedValue(node.getInitializer());
                }
                return super.visitVariable(node, p);
            }
            private void unusedValue(Tree t) {
                int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), t);
                int end   = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), t);

                if (start < 0 || end < 0) return;

                unusedValue.addHighlight(start, end, UNUSED_VALUE);
            }
        }.scan(info.getCompilationUnit(), null);

        for (Tree t : flow.getDeadBranches()) {
            int start = (int) info.getTrees().getSourcePositions().getStartPosition(info.getCompilationUnit(), t);
            int end   = (int) info.getTrees().getSourcePositions().getEndPosition(info.getCompilationUnit(), t);

            if (start < 0 || end < 0) return;

            unusedValue.addHighlight(start, end, DEAD_BRANCH);
        }

        getBag(doc).setHighlights(unusedValue);
    }

    public void cancel() {
        cancel.set(true);
    }

    private static final AttributeSet UNUSED_VALUE = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.GRAY, EditorStyleConstants.Tooltip, "The assigned value is never used");
    private static final AttributeSet DEAD_BRANCH = AttributesUtilities.createImmutable(StyleConstants.Foreground, Color.GRAY, EditorStyleConstants.Tooltip, "The branch is never used");
    
    private static OffsetsBag getBag(Document doc) {
        OffsetsBag bag = (OffsetsBag) doc.getProperty(UnusedAssignmentOrBranch.class);

        if (bag == null) {
            doc.putProperty(UnusedAssignmentOrBranch.class, bag = new OffsetsBag(doc));
        }

        return bag;
    }

    @MimeRegistration(mimeType="text/x-java", service=HighlightsLayerFactory.class)
    public static final class HighlightsFactoryImpl implements HighlightsLayerFactory {

        public HighlightsLayer[] createLayers(Context context) {
            return new HighlightsLayer[] {
                HighlightsLayer.create(UnusedAssignmentOrBranch.class.getName(), ZOrder.CARET_RACK, true, getBag(context.getDocument()))
            };
        }

    }

    @ServiceProvider(service=JavaSourceTaskFactory.class)
    public static final class JavaFactoryImpl extends EditorAwareJavaSourceTaskFactory {

        public JavaFactoryImpl() {
            super(Phase.RESOLVED, Priority.LOW);
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new UnusedAssignmentOrBranch();
        }
    }
}
