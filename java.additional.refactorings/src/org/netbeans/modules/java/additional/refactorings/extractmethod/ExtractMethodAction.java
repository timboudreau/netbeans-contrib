/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.java.additional.refactorings.extractmethod;

import org.netbeans.modules.java.additional.refactorings.*;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.ErrorManager;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import javax.tools.FileObject;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.modules.refactoring.spi.ui.RefactoringUI;
import org.netbeans.modules.refactoring.spi.ui.UI;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author Tim Boudreau
 */
public class ExtractMethodAction extends JavaRefactoringGlobalAction {

    public ExtractMethodAction() {
        super ("Extract Method", null);
        putValue (NAME, "Extract Method");        
        System.err.println("Created an action instance");
    }

    public void performAction(final Lookup context) {
        System.err.println("Perform Action on " + context);
        EditorCookie ec = super.getTextComponent(context.lookup(Node.class));
        if (ec != null) {
            new TextComponentRunnable(ec) {
                @Override
                protected RefactoringUI createRefactoringUI(TreePathHandle selectedElement,int startOffset,int endOffset, CompilationInfo info) {
                    return new ExtractMethodUI(context, selectedElement, startOffset, endOffset, info);
                }
            }.run();
        }
    }

    protected boolean enable(Lookup context) {
        EditorCookie ck = super.getTextComponent(context.lookup(Node.class));
        return ck != null && ck.getOpenedPanes().length > 0 && ck.getOpenedPanes()[0].getSelectionStart() != ck.getOpenedPanes()[0].getSelectionEnd();
    }
    
   public static abstract class TextComponentRunnable implements Runnable {
        private JTextComponent textC;
        private int caret;
        private int start;
        private int end;
        private RefactoringUI ui;
        
        public TextComponentRunnable(EditorCookie ec) {
            this.textC = ec.getOpenedPanes()[0];
            this.caret = textC.getCaretPosition();
            this.start = textC.getSelectionStart();
            this.end = textC.getSelectionEnd();
//            translatePositionsForCrlf();
            assert caret != -1;
            assert start != -1;
            assert end != -1;
        }
        
        public final void run() {
            try {
                JavaSource source = JavaSource.forDocument(textC.getDocument());
                source.runUserActionTask(new CancellableTask<CompilationController>() {
                    public void cancel() {
                    }
                    
                    public void run(CompilationController cc) throws Exception {
                        TreePath selectedElement = null;
                        cc.toPhase(Phase.RESOLVED);
                        selectedElement = cc.getTreeUtilities().pathFor(caret);
                        //workaround for issue 89064
                        if (selectedElement.getLeaf().getKind() == Tree.Kind.COMPILATION_UNIT) {
                            List<? extends Tree> decls = cc.getCompilationUnit().getTypeDecls();
                            if (!decls.isEmpty()) {
                                selectedElement = TreePath.getPath(cc.getCompilationUnit(), decls.get(0));
                            }
                        }
                        ui = createRefactoringUI(TreePathHandle.create(selectedElement, cc), start, end, cc);
                    }
                }, false);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                return ;
            }
            TopComponent activetc = TopComponent.getRegistry().getActivated();
            
            if (ui!=null) {
                UI.openRefactoringUI(ui, activetc);
            } else {
                JOptionPane.showMessageDialog(null,NbBundle.getMessage(ExtractMethodAction.class, "ERR_CannotRenameKeyword"));
            }
        }
        
        protected abstract RefactoringUI createRefactoringUI(TreePathHandle selectedElement,int startOffset,int endOffset, CompilationInfo info);
    }    
}
