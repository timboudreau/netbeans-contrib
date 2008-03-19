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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.javafx.dataloader;

import java.io.IOException;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.PrintCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.SaveAsCapable;
import org.openide.nodes.CookieSet.Factory;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Cookie;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.DataEditorSupport;
import org.openide.util.Lookup;
import org.openide.windows.CloneableOpenSupport;

/**
 *
 * @author answer
 */

public class JavaFXDataObject extends MultiDataObject implements Lookup.Provider {
    private JavaFXEditorSupport jes;
    
    public JavaFXDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException {
        super(pf, loader);
        getCookieSet().assign( SaveAsCapable.class, new SaveAsCapable() {
            public void saveAs( FileObject folder, String fileName ) throws IOException {
                createJavaFXEditorSupport().saveAs( folder, fileName );
            }
        });
        getCookieSet().add(JavaFXEditorSupport.class, new Factory() {
            public <T extends Cookie> T createCookie(Class<T> klass) {
                return klass.cast(createJavaFXEditorSupport());
            }
        });
    }

    public @Override Node createNodeDelegate() {
        return JavaFXDataSupport.createJavaFXNode(getPrimaryFile());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }
    
    protected DataObject handleCopyRename(DataFolder df, String name, String ext) throws IOException {
        FileObject fo = getPrimaryEntry ().copyRename (df.getPrimaryFile (), name, ext);
        DataObject dob = DataObject.find( fo );
        //TODO invoke refactoring here (if needed)
        return dob;
    }
    
    private synchronized JavaFXEditorSupport createJavaFXEditorSupport () {
        if (jes == null) {
            jes = new JavaFXEditorSupport (this);
        }
        return jes;
    }            
    
    public static final class JavaFXEditorSupport extends DataEditorSupport implements OpenCookie, EditCookie, EditorCookie, PrintCookie, EditorCookie.Observable {
        
        private static final class Environment extends DataEditorSupport.Env {
            
            private static final long serialVersionUID = -1;
            
            private transient SaveSupport saveCookie = null;
            
            private final class SaveSupport implements SaveCookie {
                public void save() throws java.io.IOException {
                    ((JavaFXEditorSupport)findCloneableOpenSupport()).saveDocument();
                    getDataObject().setModified(false);
                }
            }
            
            public Environment(JavaFXDataObject obj) {
                super(obj);
            }
            
            protected FileObject getFile() {
                return this.getDataObject().getPrimaryFile();
            }
            
            protected FileLock takeLock() throws java.io.IOException {
                return ((MultiDataObject)this.getDataObject()).getPrimaryEntry().takeLock();
            }
            
            public @Override CloneableOpenSupport findCloneableOpenSupport() {
                return (CloneableEditorSupport) ((JavaFXDataObject)this.getDataObject()).getCookie(EditorCookie.class);
            }
            
            
            public void addSaveCookie() {
                JavaFXDataObject javafxData = (JavaFXDataObject) this.getDataObject();
                if (javafxData.getCookie(SaveCookie.class) == null) {
                    if (this.saveCookie == null)
                        this.saveCookie = new SaveSupport();
                    javafxData.getCookieSet().add(this.saveCookie);
                    javafxData.setModified(true);
                }
            }
            
            public void removeSaveCookie() {
                JavaFXDataObject javafxData = (JavaFXDataObject) this.getDataObject();
                if (javafxData.getCookie(SaveCookie.class) != null) {
                    javafxData.getCookieSet().remove(this.saveCookie);
                    javafxData.setModified(false);
                }
            }
        }
        
        public JavaFXEditorSupport(JavaFXDataObject dataObject) {
            super(dataObject, new Environment(dataObject));
            setMIMEType("text/x-fx"); // NOI18N
        }
        
        
        protected boolean notifyModified() {
            if (!super.notifyModified())
                return false;
            ((Environment)this.env).addSaveCookie();
            return true;
        }
        
        
        protected @Override void notifyUnmodified() {
            super.notifyUnmodified();
            ((Environment)this.env).removeSaveCookie();
        }

        protected @Override CloneableEditor createCloneableEditor() {
            return new JavaFXEditor(this);
        }
        
        public @Override boolean close(boolean ask) {
            return super.close(ask);
        }

    }
    
    private static final class JavaFXEditor extends CloneableEditor {
        
        private static final long serialVersionUID = -1;        
        
        public JavaFXEditor() {
        }
        
        public JavaFXEditor(JavaFXEditorSupport sup) {
            super(sup);
        }
        
    }
    
    /**
     * XXX: Change this when there will be a write model.
     * When there will be a refactoring it shoud be called only in case of handleCreateFromTemplate
     */  
    
    
    static void renameFO(final FileObject fileToUpdate, 
            final String packageName, 
            final String newName, 
            final String originalName) throws IOException 
    {
/*        
        JavaSource javaSource = JavaSource.forFileObject (fileToUpdate);

        Task<WorkingCopy> task = new Task<WorkingCopy>() {
            
            public void run(WorkingCopy workingCopy) throws IOException {
                workingCopy.toPhase(Phase.RESOLVED);
                TreeMaker make = workingCopy.getTreeMaker();
                CompilationUnitTree compilationUnitTree = workingCopy.getCompilationUnit();
                // change the package when file was move to different dir.
                CompilationUnitTree cutCopy = make.CompilationUnit(
                        "".equals(packageName) ? null : make.Identifier(packageName),
                        compilationUnitTree.getImports(),
                        compilationUnitTree.getTypeDecls(),
                        compilationUnitTree.getSourceFile()
                );
                workingCopy.rewrite(compilationUnitTree, cutCopy);
                // go to rename also the top level class too...
                if (originalName != null && !originalName.equals(newName)) {
                    for (Tree typeDecl : compilationUnitTree.getTypeDecls()) {
                        if (Tree.Kind.CLASS == typeDecl.getKind()) {
                            ClassTree clazz = (ClassTree) typeDecl;
                            if (originalName.contentEquals(clazz.getSimpleName())) {
                                Tree copy = make.setLabel(typeDecl, newName);
                                workingCopy.rewrite(typeDecl, copy);
                            }
                        }
                    }
                }
            }                
        };
        javaSource.runModificationTask(task).commit();
 */ 
    }
  
}
