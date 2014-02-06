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

package org.netbeans.modules.dew4nb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.modules.dew4nb.spi.WorkspaceResolver;
import org.netbeans.modules.parsing.api.Source;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;
import org.openide.util.Utilities;

/**
 *
 * @author Tomas Zezula
 */
public final class SourceProvider {

    private static final Logger LOG = Logger.getLogger(SourceProvider.class.getName());

    //@GuardedBy("SourceProvider.class")
    private static SourceProvider instance;
    private final FileSystem tmpRamFs =
        FileUtil.createMemoryFileSystem();
    //@GuardedBy("retain")
    private final Map<FileObject, Reference<Source>> retain
        = Collections.synchronizedMap(new LinkedHashMap <FileObject,Reference<Source>>());
    

    private SourceProvider() {
    }

    @NonNull
    public synchronized static SourceProvider getInstance() {
        if (instance == null) {
            instance = new SourceProvider();
        }
        return instance;
    }

    @CheckForNull
    public Source getSource(
            @NullAllowed WorkspaceResolver.Context ctx,
            @NullAllowed String content) {
        FileObject file = null;
        if (ctx != null) {
            final WorkspaceResolver resolver = WorkspaceResolver.getDefault();
            if (resolver == null) {
                LOG.warning("No WorkspaceResolver in Lookup."); //NOI18N
            } else {
                file = resolver.resolveFile(ctx);
            }
        }
        boolean tmpFile = false;
        if (file == null) {
            file = createTempFile(content);
            tmpFile = true;
        }
        if (file == null) {
            return null;
        }
        synchronized (retain) {
            Reference<Source> r = retain.get(file);
            Source src;
            if (r == null || (src = r.get()) == null) {
                src = Source.create(file);
                r = tmpFile ?
                    new SR (src, retain) :
                    new WR (src, retain);
                retain.put(file, r);
            }
            update(src, content, tmpFile);
            return src;
        }
    }

    @CheckForNull
    private FileObject createTempFile(String content) {
        FileObject file = null;
        if (content != null) {
            try {
                file = tmpRamFs.getRoot().createData(String.format("Test%d.java", System.nanoTime()));
                final FileLock lck = file.lock();
                try (
                   final ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("UTF-8"));
                   final OutputStream out = file.getOutputStream(lck)) {
                    FileUtil.copy(in, out);
                } finally {
                    lck.releaseLock();
                }
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                if (file != null) {
                    try {
                        file.delete();
                    }catch (IOException rmIoe) {
                        Exceptions.printStackTrace(rmIoe);
                    } finally {
                        file = null;
                    }
                }
            }
        }
        return file;
    }

    private void update(
        @NonNull final Source source,
        @NullAllowed final String content,
        final boolean tmpFile) {
        if (tmpFile) {
            return;
        }
        if (content == null) {
            try {
                final DataObject dobj = DataObject.find(source.getFileObject());
                final EditorCookie ec = dobj.getLookup().lookup(EditorCookie.class);
                if (ec != null) {
                    final Document doc = ec.getDocument();
                    if (doc != null) {
                        ec.close();
                    }
                }
            } catch (DataObjectNotFoundException nfe) {
                LOG.log(
                    Level.INFO,
                    "Cannot find DataObject: {0}",  //NOI18N
                    FileUtil.getFileDisplayName(source.getFileObject()));
            }

        } else {
            final Document doc = source.getDocument(true);
            NbDocument.runAtomic(
                (StyledDocument)doc,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String docContent = doc.getText(0, doc.getLength());
                            if (!content.equals(docContent)) {
                                doc.remove(0, doc.getLength());
                                doc.insertString(0, content, null);
                            }
                        } catch (BadLocationException ble) {
                            throw new IllegalStateException(ble);
                        }
                    }
            });
        }
    }



    private static final class SR extends SoftReference<Source> implements Runnable {
        private final FileObject file;
        private final Map<FileObject, Reference<Source>> active;
        
        SR(
            @NonNull final Source src,
            @NonNull final Map<FileObject, Reference<Source>> active) {
            super(src, Utilities.activeReferenceQueue());
            this.file = src.getFileObject();
            if (this.file == null) {
                throw new IllegalArgumentException("No file for Source:" + src);    //NOI18N
            }
            Parameters.notNull("active", active);
            this.active = active;
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }

        @Override
        public boolean equals(@NullAllowed final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof SR)) {
                return false;
            }
            return ((SR)obj).file.equals(this.file);
        }

        @Override
        public void run() {
            LOG.log(
                Level.FINE,
                "Clearing cache for temporary file: {0}",   //NOI18N
                FileUtil.getFileDisplayName(this.file));
            active.remove(this.file);
            try {
                this.file.delete();
            } catch (IOException ex) {
               LOG.log(
                   Level.WARNING,
                   "Cannot delete: {0}",    //NOI18N
                   FileUtil.getFileDisplayName(file));
            }
        }
    }

    private static final class WR extends WeakReference<Source> implements Runnable {

        private final FileObject file;
        private final Map<FileObject,Reference<Source>> active;


        WR(
            @NonNull final Source src,
            @NonNull final Map<FileObject, Reference<Source>> active) {
            super(src, Utilities.activeReferenceQueue());
            this.file = src.getFileObject();
            if (this.file == null) {
                throw new IllegalArgumentException("No file for Source:" + src);    //NOI18N
            }
            Parameters.notNull("active", active);
            this.active = active;
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }

        @Override
        public boolean equals(@NullAllowed final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof WR)) {
                return false;
            }
            return ((WR)obj).file.equals(this.file);
        }

        @Override
        public void run() {
            LOG.log(
                Level.FINE,
                "Clearing cache for permanent file: {0}",   //NOI18N
                FileUtil.getFileDisplayName(this.file));
            active.remove(this.file);
        }
    }
}
