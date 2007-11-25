/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */

package org.netbeans.modules.java.addproperty.impl;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import java.io.IOException;
import java.util.Collections;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.modules.java.addproperty.api.AddPropertyGenerator;
import org.netbeans.modules.java.addproperty.ui.AddPropertyPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.modules.java.addproperty.api.AddPropertyConfig;
import org.netbeans.modules.java.editor.codegen.CodeGenerator;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;

/**
 *
 * @author lahvac
 */
public class AddPropertyCodeGenerator implements CodeGenerator {

    public AddPropertyCodeGenerator() {
    }

    public String getDisplayName() {
        return "Add Property...";
    }

    public void invoke(JTextComponent component) {
        Object o = component.getDocument().getProperty(Document.StreamDescriptionProperty);

        if (o instanceof DataObject) {
            DataObject d = (DataObject) o;

            perform(d.getPrimaryFile(), component);
        }
    }

    public static void perform(FileObject file, JTextComponent pane) {
        final AddPropertyPanel addPropertyPanel = AddPropertyPanel.getINSTANCE(file);
        NotifyDescriptor d =
                new NotifyDescriptor.Confirmation(addPropertyPanel, "Add Property",
                NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE);
        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
            insertCode(file, pane, addPropertyPanel.getAddPropertyConfig());
        }
    }

    static void insertCode(final FileObject file, final JTextComponent pane, final AddPropertyConfig config) {
        try {
            final Document doc = pane.getDocument();
            final Reformat r = Reformat.get(pane.getDocument());

            r.lock();

            try {
                NbDocument.runAtomicAsUser((StyledDocument) doc, new Runnable() {
                    public void run() {
                        try {
                            String code = AddPropertyGenerator.getDefault().generate(config);
                            int startOffset = pane.getCaretPosition();

                            doc.insertString(startOffset, code, null);

                            final Position start = doc.createPosition(startOffset);
                            final Position end = doc.createPosition(startOffset + code.length());

                            JavaSource.forFileObject(file).runModificationTask(new Task<WorkingCopy>() {
                                public void run(WorkingCopy parameter) throws Exception {
                                    parameter.toPhase(Phase.RESOLVED);

                                    new ImportFQNsHack(parameter, start.getOffset(), end.getOffset()).scan(parameter.getCompilationUnit(), null);

                                    CompilationUnitTree cut = parameter.getCompilationUnit();

                                    parameter.rewrite(cut, parameter.getTreeMaker().CompilationUnit(cut.getPackageName(), cut.getImports(), cut.getTypeDecls(), cut.getSourceFile()));
                                }
                            }).commit();

                            r.reformat(start.getOffset(), end.getOffset());

                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    });
            } finally {
                r.unlock();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        }

    private static final class ImportFQNsHack extends TreePathScanner<Void, Void> {

        private WorkingCopy wc;
        private int start;
        private int end;

        public ImportFQNsHack(WorkingCopy wc, int start, int end) {
            this.wc = wc;
            this.start = start;
            this.end = end;
        }

        @Override
        public Void visitMemberSelect(MemberSelectTree node, Void p) {
            int s = (int) wc.getTrees().getSourcePositions().getStartPosition(wc.getCompilationUnit(), node);
            int e = (int) wc.getTrees().getSourcePositions().getEndPosition(wc.getCompilationUnit(), node);

            if (s >= start && e <= end) {
                Element el = wc.getTrees().getElement(getCurrentPath());

                if (el != null && (el.getKind().isClass() || el.getKind().isInterface()) && ((TypeElement) el).asType().getKind() != TypeKind.ERROR) {
                    wc.rewrite(node, wc.getTreeMaker().QualIdent(el));
                    return null;
                }
            }

            return super.visitMemberSelect(node, p);
        }

        @Override
        public Void visitMethod(MethodTree node, Void p) {
            return super.visitMethod(node, p);
        }

    }

    public static final class Factory implements CodeGenerator.Factory {

        public Iterable<? extends CodeGenerator> create(CompilationController cc, TreePath path) throws IOException {
            while (path != null && path.getLeaf().getKind() != Kind.CLASS) {
                path = path.getParentPath();
            }

            if (path == null) {
                return Collections.emptyList();
            }

            return Collections.singleton(new AddPropertyCodeGenerator());
        }

    }
}
