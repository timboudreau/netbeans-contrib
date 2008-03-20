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

package org.netbeans.api.javafx.source;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 * A class representing JavaFX source.
 * 
 * @author nenik
 */
public final class JavaFXSource {

    static Phase moveToPhase(Phase phase, CompilationController aThis, boolean b) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static enum Phase {
        MODIFIED,
        PARSED,
        ELEMENTS_RESOLVED,
        RESOLVED,   
        UP_TO_DATE;
    };

    private static Map<FileObject, Reference<JavaFXSource>> file2Source = new WeakHashMap<FileObject, Reference<JavaFXSource>>();
    private static final Logger LOGGER = Logger.getLogger(JavaFXSource.class.getName());
    private final ClasspathInfo cpInfo;
    private final Collection<? extends FileObject> files;
    
    private JavaFXSource(ClasspathInfo cpInfo, Collection<? extends FileObject> files) throws IOException {
        this.cpInfo = cpInfo;
        this.files = Collections.unmodifiableList(new ArrayList<FileObject>(files));   //Create a defensive copy, prevent modification
    }
    
    
    /**
     * Returns a {@link JavaFXSource} instance associated with given
     * {@link org.openide.filesystems.FileObject}.
     * It returns null if the file doesn't represent JavaFX source file.
     * 
     * @param fileObject for which the {@link JavaFXSource} should be found/created.
     * @return {@link JavaSource} or null
     * @throws {@link IllegalArgumentException} if fileObject is null
     */
    public static JavaFXSource forFileObject(FileObject fileObject) throws IllegalArgumentException {
        if (fileObject == null) {
            throw new IllegalArgumentException ("fileObject == null");  //NOI18N
        }
        if (!fileObject.isValid()) {
            return null;
        }

        try {
            if (   fileObject.getFileSystem().isDefault()
                && fileObject.getAttribute("javax.script.ScriptEngine") != null
                && fileObject.getAttribute("template") == Boolean.TRUE) {
                return null;
            }
            DataObject od = DataObject.find(fileObject);
            
            EditorCookie ec = od.getLookup().lookup(EditorCookie.class);           
        } catch (FileStateInvalidException ex) {
            LOGGER.log(Level.FINE, null, ex);
            return null;
        } catch (DataObjectNotFoundException ex) {
            LOGGER.log(Level.FINE, null, ex);
            return null;
        }
        
        Reference<JavaFXSource> ref = file2Source.get(fileObject);
        JavaFXSource source = ref != null ? ref.get() : null;
        if (source == null) {
            if (!"text/x-fx".equals(FileUtil.getMIMEType(fileObject)) && !"fx".equals(fileObject.getExt())) {  //NOI18N
                return null;
            }
            source = create(ClasspathInfo.create(fileObject), Collections.singletonList(fileObject));
            file2Source.put(fileObject, new WeakReference<JavaFXSource>(source));
        }
        return source;
    }

    private static JavaFXSource create(final ClasspathInfo cpInfo, final Collection<? extends FileObject> files) throws IllegalArgumentException {
        try {
            return new JavaFXSource(cpInfo, files);
        } catch (DataObjectNotFoundException donf) {
            Logger.getLogger("global").warning("Ignoring non existent file: " + FileUtil.getFileDisplayName(donf.getFileObject()));     //NOI18N
        } catch (IOException ex) {            
            Exceptions.printStackTrace(ex);
        }        
        return null;
    }

    public void runUserActionTask( final Task<CompilationController> task, final boolean shared) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException ("Task cannot be null");     //NOI18N
        }

        // XXX: check access an threading
        
        if (this.files.size()<=1) {                        
            // XXX: cancel pending tasks

            CompilationInfo currentInfo = null;
            // XXX: validity check
            final CompilationController clientController = createCurrentInfo (this, null);
            try {
                task.run(clientController);
            } catch (Exception ex) {
                // XXX better handling
                Exceptions.printStackTrace(ex);
            } finally {
                if (shared) {
                    clientController.invalidate();
                }
            }
        }
    }

    private static CompilationController createCurrentInfo (final JavaFXSource js, final String javafxc) throws IOException {                
        CompilationController info = new CompilationController();//js, binding, javac);
        return info;
    }


}
