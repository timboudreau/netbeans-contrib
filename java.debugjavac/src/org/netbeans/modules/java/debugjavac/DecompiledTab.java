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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.CheckForNull;
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
import org.netbeans.modules.java.debugjavac.Decompiler.Result;
import org.netbeans.modules.parsing.spi.TaskIndexingMode;
import org.openide.awt.UndoRedo;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.modules.InstalledFileLocator;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author lahvac
 */
public class DecompiledTab {

    private static final String ATTR_DECOMPILED = "decompiled-temporary";
    private static final Map<FileObject, FileObject> source2Decompiled = new WeakHashMap<FileObject, FileObject>();

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
        try {
            FileObject decompiled = findDecompiled(file);
            DataObject decompiledDO = DataObject.find(decompiled);
            EditorCookie ec = decompiledDO.getLookup().lookup(EditorCookie.class);
            return ec.openDocument();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
    
    private static final Map<CompilerDescription, ClassLoader> compilerDescription2ClassLoader = new WeakHashMap<>();
    
    static boolean isValid(CompilerDescription compilerDescription) {
        ClassLoader loader = classLoaderFor(compilerDescription);
        
        if (loader == null) return false;
        
        try {
            Class.forName("javax.tools.ToolProvider", true, loader);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private static ClassLoader classLoaderFor(CompilerDescription compilerDescription) {
        ClassLoader loader = compilerDescription2ClassLoader.get(compilerDescription);
        
        if (loader == null) {
            try {
                List<URL> urls = new ArrayList<>();
                
                urls.addAll(Arrays.asList(compilerDescription.jars));
                urls.add(InstalledFileLocator.getDefault().locate("modules/ext/decompile.jar", null, false).toURI().toURL());
                
                compilerDescription2ClassLoader.put(compilerDescription, loader = new URLClassLoader(urls.toArray(new URL[0]), DecompiledTab.class.getClassLoader()));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return loader;
    }
    
    public static Collection<? extends Decompiler> listDecompilers(CompilerDescription compilerDescription) {
        ClassLoader loader = classLoaderFor(compilerDescription);
        
        if (loader == null) return Collections.emptyList();
        
        return Lookups.metaInfServices(loader).lookupAll(Decompiler.class);
    }
    
    private static Decompiler findDecompiler(CompilerDescription compilerDescription, String id) throws MalformedURLException {
        for (Decompiler decompiler : listDecompilers(compilerDescription)) {
            if (id.equals(decompiler.id())) return decompiler;
        }
        
        return null;
    }
    
    private static void doDecompileIntoDocument(FileObject source) {
        FileObject decompiled = findDecompiled(source, true);
        final Document doc = decompiledCodeDocument(source);
        
        if (doc == null || doc.getProperty(DECOMPILE_TAB_ACTIVE) != Boolean.TRUE) return ;
        
        try {
            Object compilerDescription = decompiled.getAttribute(CompilerDescription.class.getName());
            Object decompilerId = decompiled.getAttribute(Decompiler.class.getName());
            
            if (!(compilerDescription instanceof CompilerDescription) || !(decompilerId instanceof String)) {
                return ;
            }

            final String decompiledCode;
            
            if (((CompilerDescription) compilerDescription).isValid()) {
                Decompiler decompiler = findDecompiler((CompilerDescription) compilerDescription, (String) decompilerId);
                Result decompileResult = decompiler.decompile(source);
                decompiledCode = (decompileResult.decompiledOutput != null ? "#Section(" + decompileResult.decompiledMimeType + ") Output:\n" + decompileResult.decompiledOutput + "\n" : "") +
                                 (decompileResult.compileErrors != null ? "#Section(text/plaing) Processing Errors:\n" + decompileResult.compileErrors + "\n" : "");
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
            
            SaveCookie sc = DataObject.find(findDecompiled(source)).getLookup().lookup(SaveCookie.class);
            
            if (sc != null) sc.save();
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
                    doDecompileIntoDocument(d.getPrimaryFile());
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
            }

            @Override
            public void componentHidden() {
            }

            @Override
            public void componentActivated() {
                Document doc = decompiledCodeDocument(d.getPrimaryFile());
                if (doc != null) doc.putProperty(DECOMPILE_TAB_ACTIVE, true);
                decompiled.addFileChangeListener(fileListener);
                doDecompileIntoDocument(d.getPrimaryFile());//TODO: outside of AWT
            }

            @Override
            public void componentDeactivated() {
                Document doc = decompiledCodeDocument(d.getPrimaryFile());
                if (doc != null) doc.putProperty(DECOMPILE_TAB_ACTIVE, null);
                decompiled.removeFileChangeListener(fileListener);
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
}
