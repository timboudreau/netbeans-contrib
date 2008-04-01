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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.javafx.editor.fold;

import com.sun.javafx.api.tree.ClassDeclarationTree;
import com.sun.javafx.api.tree.ForExpressionInClauseTree;
import com.sun.javafx.api.tree.ForExpressionTree;
import com.sun.javafx.api.tree.FunctionDefinitionTree;
import com.sun.javafx.api.tree.InstantiateTree;
import com.sun.javafx.api.tree.ObjectLiteralPartTree;
import com.sun.javafx.api.tree.TriggerTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.javafx.lexer.JFXTokenId;
import org.netbeans.api.javafx.source.CompilationInfo;
import org.netbeans.api.javafx.source.support.CancellableTreePathScanner;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.editor.SettingsChangeEvent;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.ext.java.JavaFoldManager;
import org.netbeans.editor.ext.java.JavaSettingsNames;
import org.netbeans.modules.javafx.editor.semantic.ScanningCancellableTask;
import org.netbeans.spi.editor.fold.FoldHierarchyTransaction;
import org.netbeans.spi.editor.fold.FoldOperation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author Jan Lahoda
 */
public class JavaFXElementFoldManager extends JavaFoldManager {
    
    private FoldOperation operation;
    private FileObject    file;
    private JavaFXElementFoldTask task;
    
    // Folding presets
    private boolean foldImportsPreset;
    private boolean foldInnerClassesPreset;
    private boolean foldJavadocsPreset;
    private boolean foldCodeBlocksPreset;
    private boolean foldInitialCommentsPreset;
    
    /** Creates a new instance of JavaFXElementFoldManager */
    public JavaFXElementFoldManager() {
    }

    public void init(FoldOperation operation) {
        this.operation = operation;
        
        settingsChange(null);
    }

    public synchronized void initFolds(FoldHierarchyTransaction transaction) {
        Document doc = operation.getHierarchy().getComponent().getDocument();
        DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
        
        if (od != null) {
            currentFolds = new HashMap<FoldInfo, Fold>();
            task = JavaFXElementFoldTask.getTask(od.getPrimaryFile());
            task.setJavaElementFoldManager(JavaFXElementFoldManager.this);
        }
    }
    
    public void insertUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
    }

    public void removeUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
    }

    public void changedUpdate(DocumentEvent evt, FoldHierarchyTransaction transaction) {
    }

    public void removeEmptyNotify(Fold emptyFold) {
        removeDamagedNotify(emptyFold);
    }

    public void removeDamagedNotify(Fold damagedFold) {
        currentFolds.remove(operation.getExtraInfo(damagedFold));
        if (importsFold == damagedFold) {
            importsFold = null;//not sure if this is correct...
        }
        if (initialCommentFold == damagedFold) {
            initialCommentFold = null;//not sure if this is correct...
        }
    }

    public void expandNotify(Fold expandedFold) {
    }

    public synchronized void release() {
        if (task != null)
            task.setJavaElementFoldManager(null);
        
        task         = null;
        file         = null;
        currentFolds = null;
        importsFold  = null;
        initialCommentFold = null;
    }
    
    public void settingsChange(SettingsChangeEvent evt) {
        // Get folding presets
        foldInitialCommentsPreset = getSetting(JavaSettingsNames.CODE_FOLDING_COLLAPSE_INITIAL_COMMENT);
        foldImportsPreset = getSetting(JavaSettingsNames.CODE_FOLDING_COLLAPSE_IMPORT);
        foldCodeBlocksPreset = getSetting(JavaSettingsNames.CODE_FOLDING_COLLAPSE_METHOD);
        foldInnerClassesPreset = getSetting(JavaSettingsNames.CODE_FOLDING_COLLAPSE_INNERCLASS);
        foldJavadocsPreset = getSetting(JavaSettingsNames.CODE_FOLDING_COLLAPSE_JAVADOC);
    }
    
    private boolean getSetting(String settingName){
        JTextComponent tc = operation.getHierarchy().getComponent();
        return SettingsUtil.getBoolean(org.netbeans.editor.Utilities.getKitClass(tc), settingName, false);
    }
    
    private static void dumpPositions(Tree tree, int start, int end) {
        System.err.println("decl = " + tree);
        System.err.println("startOffset = " + start);
        System.err.println("endOffset = " + end);
        
        if (start == (-1) || end == (-1)) {
            System.err.println("ERROR: the positions are outside document.");
        }

    }
    
    static final class JavaFXElementFoldTask extends ScanningCancellableTask<CompilationInfo> {
        
        //XXX: this will hold JavaFXElementFoldTask as long as the FileObject exists:
        private static Map<FileObject, JavaFXElementFoldTask> file2Task = new WeakHashMap<FileObject, JavaFXElementFoldTask>();
        
        static JavaFXElementFoldTask getTask(FileObject file) {
            JavaFXElementFoldTask task = file2Task.get(file);
            
            if (task == null) {
                file2Task.put(file, task = new JavaFXElementFoldTask());
            }
            
            return task;
        }
        
        private Reference<JavaFXElementFoldManager> manager;
        
        synchronized void setJavaElementFoldManager(JavaFXElementFoldManager manager) {
            this.manager = new WeakReference<JavaFXElementFoldManager>(manager);
        }
        
        public void run(final CompilationInfo info) {
            resume();
            
            JavaFXElementFoldManager manager;
            
            //the synchronized section should be as limited as possible here
            //in particular, "scan" should not be called in the synchronized section
            //or a deadlock could appear: sy(this)+document read lock against
            //document write lock and this.cancel/sy(this)
            synchronized (this) {
                manager = this.manager != null ? this.manager.get() : null;
            }
            
            if (manager == null)
                return ;
            
            long startTime = System.currentTimeMillis();

            final CompilationUnitTree cu = info.getCompilationUnit();
            final JavaFXElementFoldVisitor v = manager.new JavaFXElementFoldVisitor(info, cu, info.getTrees().getSourcePositions());
            
            scan(v, cu, null);
            
            if (v.stopped || isCancelled())
                return ;
            
            //check for initial fold:
            v.checkInitialFold();
            
            if (v.stopped || isCancelled())
                return ;
            
            SwingUtilities.invokeLater(manager.new CommitFolds(v.folds));
            
            long endTime = System.currentTimeMillis();
            
            Logger.getLogger("TIMER").log(Level.FINE, "Folds - 1",
                    new Object[] {info, endTime - startTime});
        }
        
    }
    
    private class CommitFolds implements Runnable {
        
        private boolean insideRender;
        private List<FoldInfo> infos;
        private long startTime;
        
        public CommitFolds(List<FoldInfo> infos) {
            this.infos = infos;
        }
        
        public void run() {
            if (!insideRender) {
                startTime = System.currentTimeMillis();
                insideRender = true;
                operation.getHierarchy().getComponent().getDocument().render(this);
                
                return;
            }
            
            operation.getHierarchy().lock();
            
            try {
                FoldHierarchyTransaction tr = operation.openTransaction();
                
                try {
                    if (currentFolds == null)
                        return ;
                    
                    Map<FoldInfo, Fold> added   = new TreeMap<FoldInfo, Fold>();
                    List<FoldInfo>      removed = new ArrayList<FoldInfo>(currentFolds.keySet());
                    
                    for (FoldInfo i : infos) {
                        if (removed.remove(i)) {
                            continue ;
                        }
                        
                        int start = i.start.getOffset();
                        int end   = i.end.getOffset();
                        
                        if (end > start && (end - start) > (i.template.getStartGuardedLength() + i.template.getEndGuardedLength())) {
                            Fold f    = operation.addToHierarchy(i.template.getType(),
                                                                 i.template.getDescription(),
                                                                 i.collapseByDefault,
                                                                 start,
                                                                 end,
                                                                 i.template.getStartGuardedLength(),
                                                                 i.template.getEndGuardedLength(),
                                                                 i,
                                                                 tr);
                            
                            added.put(i, f);
                            
                            if (i.template == IMPORTS_FOLD_TEMPLATE) {
                                importsFold = f;
                            }
                            if (i.template == INITIAL_COMMENT_FOLD_TEMPLATE) {
                                initialCommentFold = f;
                            }
                        }
                    }
                    
                    for (FoldInfo i : removed) {
                        Fold f = currentFolds.remove(i);
                        
                        operation.removeFromHierarchy(f, tr);
                        
                        if (importsFold == f ) {
                            importsFold = null;
                        }
                        
                        if (initialCommentFold == f) {
                            initialCommentFold = f;
                        }
                    }
                    
                    currentFolds.putAll(added);
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(e);
                } finally {
                    tr.commit();
                }
            } finally {
                operation.getHierarchy().unlock();
            }
            
            long endTime = System.currentTimeMillis();
            
            Logger.getLogger("TIMER").log(Level.FINE, "Folds - 2",
                    new Object[] {file, endTime - startTime});
        }
    }
    
    private Map<FoldInfo, Fold> currentFolds;
    private Fold initialCommentFold;
    private Fold importsFold;
    
    private final class JavaFXElementFoldVisitor extends CancellableTreePathScanner<Object, Object> {

        private List<FoldInfo> folds = new ArrayList<JavaFXElementFoldManager.FoldInfo>();
        private CompilationInfo info;
        private CompilationUnitTree cu;
        private SourcePositions sp;
        private boolean stopped;
        
        public JavaFXElementFoldVisitor(CompilationInfo info, CompilationUnitTree cu, SourcePositions sp) {
            this.info = info;
            this.cu = cu;
            this.sp = sp;
        }
        
        public void checkInitialFold() {
            try {
                TokenHierarchy<?> th = info.getTokenHierarchy();
                if (th == null) {
                    stopped = true;
                    return;
                }
                TokenSequence<JFXTokenId>  ts = th.tokenSequence(JFXTokenId.language());
                
                while (ts.moveNext()) {
                    Token<JFXTokenId> token = ts.token();
                    
                    if (token.id() == JFXTokenId.COMMENT) {
                        Document doc   = operation.getHierarchy().getComponent().getDocument();
                        int startOffset = ts.offset();
                        boolean collapsed = foldInitialCommentsPreset;
                        
                        if (initialCommentFold != null) {
                            collapsed = initialCommentFold.isCollapsed();
                        }
                        
                        folds.add(new FoldInfo(doc, startOffset, startOffset + token.length(), INITIAL_COMMENT_FOLD_TEMPLATE, collapsed));
                    }
                    
                    if (token.id() != JFXTokenId.WS)
                        break;
                }
            } catch (BadLocationException e) {
                //the document probably changed, stop
                stopped = true;
            } catch (ConcurrentModificationException e) {
                //from TokenSequence, document probably changed, stop
                stopped = true;
            }
        }
        
        private void handleJavadoc(Tree t) throws BadLocationException, ConcurrentModificationException {
            int start = (int) sp.getStartPosition(cu, t);
            
            if (start == (-1)) {
                // debug:
                dumpPositions(t, start, -15);
                return ;
            }
            
            TokenHierarchy<?> th = info.getTokenHierarchy();
            if (th == null) {
                return;
            }
            TokenSequence<JFXTokenId>  ts = th.tokenSequence(JFXTokenId.language());
            
            if (ts.move(start) == Integer.MAX_VALUE) {
                return;//nothing
            }
            
            while (ts.movePrevious()) {
                Token<JFXTokenId> token = ts.token();
                
                if (token.id() == JFXTokenId.DOC_COMMENT) {
                    Document doc   = operation.getHierarchy().getComponent().getDocument();
                    int startOffset = ts.offset();
                    folds.add(new FoldInfo(doc, startOffset, startOffset + token.length(), JAVADOC_FOLD_TEMPLATE, foldJavadocsPreset));
                }
                if (   token.id() != JFXTokenId.WS
                    && token.id() != JFXTokenId.COMMENT
                    && token.id() != JFXTokenId.LINE_COMMENT)
                    break;
            }
        }
        
        private void handleTree(Tree node, Tree javadocTree, boolean handleOnlyJavadoc) {
            try {
                if (!handleOnlyJavadoc) {
                    Document doc = operation.getHierarchy().getComponent().getDocument();
                    int start = (int)sp.getStartPosition(cu, node);
                    int end   = (int)sp.getEndPosition(cu, node);
                    
                    if (start != (-1) && end != (-1)) {
                        folds.add(new FoldInfo(doc, start, end, CODE_BLOCK_FOLD_TEMPLATE, foldCodeBlocksPreset));
                    } else {
                        // debug:
                        dumpPositions(node, start, end);
                    }
                }
                
                handleJavadoc(javadocTree != null ? javadocTree : node);
            } catch (BadLocationException e) {
                //the document probably changed, stop
                stopped = true;
            } catch (ConcurrentModificationException e) {
                //from TokenSequence, document probably changed, stop
                stopped = true;
            }
        }
        
        @Override
        public Object visitMethod(MethodTree node, Object p) {
            super.visitMethod(node, p);
            handleTree(node.getBody(), node, false);
            return null;
        }

        @Override
        public Object visitClass(ClassTree node, Object p) {
            super.visitClass(node, p);
            try {
                Document doc = operation.getHierarchy().getComponent().getDocument();
                int start = findBodyStart(node, cu, sp, doc);
                int end   = (int)sp.getEndPosition(cu, node);

                if (start != (-1) && end != (-1)) {
                    folds.add(new FoldInfo(doc, start, end, CODE_BLOCK_FOLD_TEMPLATE, foldInnerClassesPreset));
                } else {
                    dumpPositions(node, start, end);
                }
                
                handleJavadoc(node);
            } catch (BadLocationException e) {
                //the document probably changed, stop
                stopped = true;
            } catch (ConcurrentModificationException e) {
                //from TokenSequence, document probably changed, stop
                stopped = true;
            }
            return null;
        }

        @Override
        public Object visitInstantiate(InstantiateTree node, Object p) {
            super.visitInstantiate(node, p);
            try {
                Document doc = operation.getHierarchy().getComponent().getDocument();
                int start = findBodyStart(node, cu, sp, doc);
                int end   = (int)sp.getEndPosition(cu, node);

                if (start != (-1) && end != (-1)) {
                    folds.add(new FoldInfo(doc, start, end, CODE_BLOCK_FOLD_TEMPLATE, foldInnerClassesPreset));
                } else {
                    dumpPositions(node, start, end);
                }
                
                handleJavadoc(node);
            } catch (BadLocationException e) {
                //the document probably changed, stop
                stopped = true;
            } catch (ConcurrentModificationException e) {
                //from TokenSequence, document probably changed, stop
                stopped = true;
            }
            return null;
        }

        @Override
        public Object visitObjectLiteralPart(ObjectLiteralPartTree node, Object p) {
            super.visitObjectLiteralPart(node, p);
            handleTree(node.getExpression(), null, true);
            return null;
        }

        @Override
        public Object visitClassDeclaration(ClassDeclarationTree node, Object p) {
            super.visitClassDeclaration(node, p);
            try {
                Document doc = operation.getHierarchy().getComponent().getDocument();
                int start = findBodyStart(node, cu, sp, doc);
                int end   = (int)sp.getEndPosition(cu, node);

                if (start != (-1) && end != (-1)) {
                    folds.add(new FoldInfo(doc, start, end, CODE_BLOCK_FOLD_TEMPLATE, foldInnerClassesPreset));
                } else {
                    dumpPositions(node, start, end);
                }
                
                handleJavadoc(node);
            } catch (BadLocationException e) {
                //the document probably changed, stop
                stopped = true;
            } catch (ConcurrentModificationException e) {
                //from TokenSequence, document probably changed, stop
                stopped = true;
            }
            return null;
        }

        @Override
        public Object visitForExpression(ForExpressionTree arg0, Object arg1) {
            return super.visitForExpression(arg0, arg1);
        }

        @Override
        public Object visitForExpressionInClause(ForExpressionInClauseTree arg0, Object arg1) {
            return super.visitForExpressionInClause(arg0, arg1);
        }

        @Override
        public Object visitFunctionDefinition(FunctionDefinitionTree node, Object p) {
            super.visitFunctionDefinition(node, p);
            handleTree(node.getFunctionValue(), node, false);
            return null;
        }

        @Override
        public Object visitTrigger(TriggerTree arg0, Object arg1) {
            return super.visitTrigger(arg0, arg1);
        }
        
        @Override
        public Object visitVariable(VariableTree node,Object p) {
            super.visitVariable(node, p);
            handleTree(node, null, true);
            return null;
        }
        
        @Override
        public Object visitBlock(BlockTree node, Object p) {
            super.visitBlock(node, p);
            //check static/dynamic initializer:
            TreePath path = getCurrentPath();
            
            if (path.getParentPath().getLeaf().getKind() == Kind.CLASS) {
                handleTree(node, null, false);
            }
            
            return null;
        }
        
        @Override
        public Object visitCompilationUnit(CompilationUnitTree node, Object p) {
            int importsStart = Integer.MAX_VALUE;
            int importsEnd   = -1;
            
            for (ImportTree imp : node.getImports()) {
                int start = (int) sp.getStartPosition(cu, imp);
                int end   = (int) sp.getEndPosition(cu, imp);
                
                if (importsStart > start)
                    importsStart = start;
                
                if (end > importsEnd) {
                    importsEnd = end;
                }
            }
            
            if (importsEnd != (-1) && importsStart != (-1)) {
                try {
                    Document doc   = operation.getHierarchy().getComponent().getDocument();
                    boolean collapsed = foldImportsPreset;
                    
                    if (importsFold != null) {
                        collapsed = importsFold.isCollapsed();
                    }
                    
                    importsStart += 7/*"import ".length()*/;
                    
                    if (importsStart < importsEnd) {
                        folds.add(new FoldInfo(doc, importsStart , importsEnd, IMPORTS_FOLD_TEMPLATE, collapsed));
                    }
                } catch (BadLocationException e) {
                    //the document probably changed, stop
                    stopped = true;
                }
            }
            return super.visitCompilationUnit(node, p);
        }

    }
    
    protected static final class FoldInfo implements Comparable {
        
        private Position start;
        private Position end;
        private FoldTemplate template;
        private boolean collapseByDefault;
        
        public FoldInfo(Document doc, int start, int end, FoldTemplate template, boolean collapseByDefault) throws BadLocationException {
            this.start = doc.createPosition(start);
            this.end   = doc.createPosition(end);
            this.template = template;
            this.collapseByDefault = collapseByDefault;
        }
        
        @Override
        public int hashCode() {
            return 1;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FoldInfo))
                return false;
            
            return compareTo(o) == 0;
        }
        
        public int compareTo(Object o) {
            FoldInfo remote = (FoldInfo) o;
            
            if (start.getOffset() < remote.start.getOffset()) {
                return -1;
            }
            
            if (start.getOffset() > remote.start.getOffset()) {
                return 1;
            }
            
            if (end.getOffset() < remote.end.getOffset()) {
                return -1;
            }
            
            if (end.getOffset() > remote.end.getOffset()) {
                return 1;
            }
            
            return 0;
        }
        
    }
    public static int findBodyStart(final Tree cltree, final CompilationUnitTree cu, final SourcePositions positions, final Document doc) {
        final int[] result = new int[1];
        
        doc.render(new Runnable() {
            public void run() {
                result[0] = findBodyStartImpl(cltree, cu, positions, doc);
            }
        });
        
        return result[0];
    }
    
    private static int findBodyStartImpl(Tree cltree, CompilationUnitTree cu, SourcePositions positions, Document doc) {
        int start = (int)positions.getStartPosition(cu, cltree);
        int end   = (int)positions.getEndPosition(cu, cltree);
        
        if (start == (-1) || end == (-1)) {
            // debug:
            dumpPositions(cltree, start, end);
            return -1;
        }
        
        if (start > doc.getLength() || end > doc.getLength()) {
            // debug:
            dumpPositions(cltree, start, end);
            return (-1);
        }
        try {
            String text = doc.getText(start, end - start);
            
            int index = text.indexOf('{');
            
            if (index == (-1)) {
                return -1;
//                throw new IllegalStateException("Should NEVER happen.");
            }
            
            return start + index;
        } catch (BadLocationException e) {
            Exceptions.printStackTrace(e);
        }
        
        return (-1);

    }
}
