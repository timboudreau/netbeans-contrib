/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.debugjavac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.JavaSource.Priority;
import org.netbeans.api.java.source.JavaSourceTaskFactory;
import org.netbeans.api.java.source.support.EditorAwareJavaSourceTaskFactory;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.netbeans.editor.GuardedDocument;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.modules.editor.NbEditorKit;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.java.debugjavac.Decompiler.Input;
import org.netbeans.modules.java.debugjavac.Decompiler.Result;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.TaskIndexingMode;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProviderFactory;
import org.openide.awt.UndoRedo;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author lahvac
 */
public class DecompiledTab {

    private static final String ATTR_DECOMPILED = "decompiled-temporary";
    private static final Map<FileObject, FileObject> source2Decompiled = new WeakHashMap<>();
    private static final RequestProcessor DECOMPILE_RUNNER = new RequestProcessor(DecompiledTab.class.getName(), 1, false, false);

    public static synchronized FileObject findDecompiled(FileObject source) {
        return findDecompiled(source, true);
    }
    
    private static synchronized FileObject findDecompiled(FileObject source, boolean create) {
        FileObject result = source2Decompiled.get(source);

        if (result == null && create) {
            try {
                FileObject decompiledFO = FileUtil.createMemoryFileSystem().getRoot().createData(source.getName(), "djava");

                decompiledFO.setAttribute(ATTR_DECOMPILED, Boolean.TRUE);

                source2Decompiled.put(source, result = decompiledFO);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        return result;
    }
    
    private static @CheckForNull Document decompiledCodeDocument(FileObject file) {
        return decompiledCodeDocument(file, true);
    }

    private static @CheckForNull Document decompiledCodeDocument(FileObject file, boolean create) {
        try {
            FileObject decompiled = findDecompiled(file, create);

            if (decompiled == null) return null;
            
            DataObject decompiledDO = DataObject.find(decompiled);
            EditorCookie ec = decompiledDO.getLookup().lookup(EditorCookie.class);
            return ec.openDocument();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    private static DecompilerDescription findDecompiler(String id) throws MalformedURLException {
        for (DecompilerDescription decompiler : DecompilerDescription.getDecompilers()) {
            if (id.equals(decompiler.id)) return decompiler;
        }

        return null;
    }

    private static void decompileIntoDocumentLater(final FileObject source) {
        DECOMPILE_RUNNER.post(new Runnable() {
            @Override public void run() {
                doDecompileIntoDocument(source);
            }
        });
        
    }

    private static void doDecompileIntoDocument(FileObject source) {
        FileObject decompiled = findDecompiled(source, true);
        final Document doc = decompiledCodeDocument(source);
        
        if (doc == null || doc.getProperty(DECOMPILE_TAB_ACTIVE) != Boolean.TRUE) return ;
        
        try {
            Object compilerDescription = decompiled.getAttribute(CompilerDescription.class.getName());
            Object decompilerId = decompiled.getAttribute(DecompilerDescription.class.getName());
            
            if (!(compilerDescription instanceof CompilerDescription) || !(decompilerId instanceof String)) {
                return ;
            }

            final String decompiledCode;
            
            if (((CompilerDescription) compilerDescription).isValid()) {
                final String code = Source.create(source).createSnapshot().getText().toString();
                UpToDateStatusProviderImpl.get(doc).update(UpToDateStatus.UP_TO_DATE_PROCESSING);
                DecompilerDescription decompiler = findDecompiler((String) decompilerId);
                Result decompileResult = ((CompilerDescription) compilerDescription).decompile(decompiler, new Input(code, Utilities.commandLineParameters(source)));
                if (decompileResult.exception != null) {
                    decompiledCode = "#Section(text/plain) Ooops, an exception occurred while decompiling:\n" + decompileResult.exception;
                } else {
                    decompiledCode = (decompileResult.decompiledOutput != null ? "#Section(" + decompileResult.decompiledMimeType + ") Output:\n" + decompileResult.decompiledOutput + "\n" : "") +
                                     (decompileResult.compileErrors != null ? "#Section(text/plain) Processing Errors:\n" + decompileResult.compileErrors + "\n" : "");
                }
            } else {
                decompiledCode = "Unusable compiler";
            }

            NbDocument.runAtomic((StyledDocument) doc, new Runnable() {
                @Override public void run() {
                    try {
                        doc.remove(0, doc.getLength());
                        if (doc instanceof GuardedDocument) {
                            ((GuardedDocument) doc).getGuardedBlockChain().removeEmptyBlocks();
                        }
                        doc.insertString(0, decompiledCode, null);
                        if (doc instanceof GuardedDocument) {
                            ((GuardedDocument) doc).getGuardedBlockChain().addBlock(0, doc.getLength(), true);
                        }
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
            
            SaveCookie sc = DataObject.find(decompiled).getLookup().lookup(SaveCookie.class);
            
            if (sc != null) sc.save();

            UpToDateStatusProviderImpl.get(doc).update(UpToDateStatus.UP_TO_DATE_OK);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static final String DECOMPILE_TAB_ACTIVE = "decompile-tab-active";
    
    @MultiViewElement.Registration(
        displayName="Decompile",
//        iconBase="org/netbeans/modules/java/resources/class.gif",
        persistenceType=TopComponent.PERSISTENCE_ONLY_OPENED,
        preferredID="java.decompile",
        mimeType="text/x-java",
        position=5000
    )
    public static MultiViewElement createMultiViewEditorElement(Lookup context) {
        final DataObject d = context.lookup(DataObject.class);
        final FileObject decompiled = findDecompiled(d.getPrimaryFile(), true);
        return new MultiViewElement() {
            private JEditorPane pane;
            private JComponent scrollPane;
            private final FileChangeListener fileListener = new FileChangeAdapter() {
                @Override public void fileAttributeChanged(FileAttributeEvent fe) {
                    decompileIntoDocumentLater(d.getPrimaryFile());
                }
            };
            @Override
            public JComponent getVisualRepresentation() {
                if (pane == null) {
                    pane = new JEditorPane();
                    pane.setContentType("text/x-java-decompiled");
                    pane.setEditorKit(new NbEditorKit() {
                        @Override public String getContentType() {
                            return "text/x-java-decompiled";
                        }
                    });
                    Document doc = decompiledCodeDocument(d.getPrimaryFile());
                    if (doc != null)
                        pane.setDocument(doc);
                    scrollPane = doc instanceof NbEditorDocument ? (JComponent) ((NbEditorDocument) doc).createEditor(pane) : new JScrollPane(pane);
                }
                return scrollPane;
            }

            private DecompileToolbar toolbar;
            @Override
            public JComponent getToolbarRepresentation() {
                if (toolbar == null) {
                    FileObject decompiled = findDecompiled(d.getPrimaryFile(), true);
                    toolbar = new DecompileToolbar(decompiled);
                }
                return toolbar;
            }

            @Override
            public Action[] getActions() {
                return new Action[0];
            }

            @Override
            public Lookup getLookup() {
                return Lookup.EMPTY;
            }

            @Override
            public void componentOpened() {
            }

            @Override
            public void componentClosed() {
            }

            @Override
            public void componentShowing() {
                Document doc = decompiledCodeDocument(d.getPrimaryFile());
                if (doc != null) doc.putProperty(DECOMPILE_TAB_ACTIVE, true);
                decompiled.addFileChangeListener(fileListener);
                decompileIntoDocumentLater(d.getPrimaryFile());
            }

            @Override
            public void componentHidden() {
                Document doc = decompiledCodeDocument(d.getPrimaryFile());
                if (doc != null) doc.putProperty(DECOMPILE_TAB_ACTIVE, null);
                decompiled.removeFileChangeListener(fileListener);
            }

            @Override
            public void componentActivated() {
            }

            @Override
            public void componentDeactivated() {
            }

            @Override
            public UndoRedo getUndoRedo() {
                return null;
            }

            @Override
            public void setMultiViewCallback(MultiViewElementCallback callback) {
            }

            @Override
            public CloseOperationState canCloseElement() {
                return CloseOperationState.STATE_OK;
            }
            
        };
    }

    static {
        EditorRegistry.addPropertyChangeListener(new DocL());
    }

    private static final class DocL implements PropertyChangeListener, DocumentListener {

        private Document lastFocused;

        @Override public void propertyChange(PropertyChangeEvent evt) {
            JTextComponent fc = EditorRegistry.focusedComponent();
            Document doc = fc != null ? fc.getDocument() : null;

            if (doc == lastFocused) return ;

            if (lastFocused != null) {
                lastFocused.removeDocumentListener(this);
            }

            if (doc != null) {
                doc.addDocumentListener(this);
            }

            lastFocused = doc;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            update(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update(e);
        }

        private void update(DocumentEvent e) {
            FileObject file = NbEditorUtilities.getFileObject(e.getDocument());

            if (file == null) return ;

            Document doc = decompiledCodeDocument(file, false);

            if (doc == null) return ;

            UpToDateStatusProviderImpl.get(doc).update(UpToDateStatus.UP_TO_DATE_DIRTY);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }

    }

    private static final class Updater implements CancellableTask<CompilationInfo> {
        @Override public void run(CompilationInfo parameter) throws Exception {
            doDecompileIntoDocument(parameter.getFileObject());
//            FileObject sourceFile = parameter.getFileObject();
////            if (sourceFile.getAttribute(ATTR_DECOMPILED) == Boolean.TRUE) return;
//            final FileObject decompiled = findDecompiled(sourceFile, false);
//            
//            if (decompiled == null) return ;
//            
//            final ElementHandle<?> handle = ElementHandle.create(parameter.getTopLevelElements().get(0));
//
//            JavaSource.create(parameter.getClasspathInfo(), decompiled).runModificationTask(new Task<WorkingCopy>() {
//                @Override public void run(WorkingCopy copy) throws Exception {
//                    copy.toPhase(Phase.RESOLVED);
//
//                    copy.rewrite(copy.getCompilationUnit(), CodeGenerator.generateCode(copy, (TypeElement) handle.resolve(copy)));
//                }
//            }).commit();
//            
//            SaveCookie sc = DataObject.find(decompiled).getLookup().lookup(SaveCookie.class);
//            
//            if (sc != null) sc.save();
        }

        @Override public void cancel() {
        }

    }

    @ServiceProvider(service=JavaSourceTaskFactory.class)
    public static final class UpdaterFactory extends EditorAwareJavaSourceTaskFactory {

        public UpdaterFactory() {
            super(Phase.RESOLVED, Priority.LOW, TaskIndexingMode.ALLOWED_DURING_SCAN);
        }

        @Override
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new Updater();
        }

    }

   @MimeRegistration(mimeType="text/x-java-decompiled", service=UpToDateStatusProviderFactory.class)
     public static final class UpToDateStatusProviderFactoryImpl implements UpToDateStatusProviderFactory {
        @Override public UpToDateStatusProvider createUpToDateStatusProvider(Document document) {
            return UpToDateStatusProviderImpl.get(document);
        }
    }

    private static final class UpToDateStatusProviderImpl extends UpToDateStatusProvider {

        public static UpToDateStatusProviderImpl get(Document doc) {
            UpToDateStatusProviderImpl result = (UpToDateStatusProviderImpl) doc.getProperty(UpToDateStatusProviderImpl.class);

            if (result == null) {
                result = new UpToDateStatusProviderImpl(doc);
            }

            return result;
        }

        private final Document doc;

        private UpToDateStatusProviderImpl(Document doc) {
            this.doc = doc;
        }

        @Override
        public UpToDateStatus getUpToDate() {
            UpToDateStatus status = (UpToDateStatus) doc.getProperty(UpToDateStatusProviderImpl.class.getName() + "-status-value");

            if (status == null) status = UpToDateStatus.UP_TO_DATE_DIRTY;

            return status;
        }

        private void update(UpToDateStatus newValue) {
            UpToDateStatus oldValue = getUpToDate();

            doc.putProperty(UpToDateStatusProviderImpl.class.getName() + "-status-value", newValue);
            firePropertyChange(PROP_UP_TO_DATE, oldValue, newValue);

            //TODO: the event above does not always repaint the error stripe, workarounding:
            NbDocument.runAtomic((StyledDocument) doc, new Runnable() {
                @Override public void run() {
                    try {
                        doc.insertString(0, " ", null);
                        doc.remove(0, 1);
                    } catch (BadLocationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });

            DECOMPILE_RUNNER.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        FileObject decompiledFO = NbEditorUtilities.getFileObject(doc);
                        SaveCookie sc = decompiledFO != null ? DataObject.find(decompiledFO).getLookup().lookup(SaveCookie.class) : null;

                        if (sc != null) sc.save();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        }
    }
}
