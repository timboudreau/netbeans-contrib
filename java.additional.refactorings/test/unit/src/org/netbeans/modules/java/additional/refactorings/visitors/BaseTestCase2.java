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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.java.additional.refactorings.visitors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.java.source.usages.RepositoryUpdater;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

public abstract class BaseTestCase2<R,D> extends NbTestCase {
    
    protected static FileObject srcRoot;
    protected static FileObject binRoot;
    protected static ClassPath sourcePath;
    protected static ClassPath compilePath;
    protected static ClassPath bootPath;
    protected static MutableCp spiCp;
    protected static MutableCp spiSrc;
    protected static File src;
    protected static File javasource;
    
    protected JavaSource source;
    protected WorkingCopy copy;
    protected D argument;
    protected R scanResult;
    protected String fileContent;
    

    protected final String dataFileName;    
    public BaseTestCase2 (String name, String dataFileName) {
        super (name);
        this.dataFileName = dataFileName;
    }

    @Override
    protected void setUp() throws Exception {
        System.err.println("\n\nJAVA HOME:'" + System.getProperty("java.home") + "\n\n");
        clearAllButCaches ();                
        File cache = new File(getWorkDir(), "cache");       //NOI18N
        System.setProperty ("netbeans.home", FileUtil.normalizeFile(cache).getPath()); //NOI18N        
        System.setProperty ("netbeans.user", FileUtil.normalizeFile(cache).getPath()); //NOI18N        
        cache.mkdirs();
        File logdir = new File (cache, INDEX_DIR);
        logdir.mkdirs();
        System.err.println("LogDir " + logdir.getPath());
        assertTrue (logdir.exists());
        hackIndexUtil (cache);
        src = new File(getWorkDir(), "src");           //NOI18N
        src.mkdirs();
        srcRoot = FileUtil.toFileObject(src);
        File bin = new File(getWorkDir(), "build");        
        bin.mkdirs();
        binRoot = FileUtil.toFileObject(bin);
        spiSrc = new MutableCp (Collections.singletonList(ClassPathSupport.createResource(srcRoot.getURL())));
        sourcePath = ClassPathFactory.createClassPath(spiSrc);
        spiCp = new MutableCp (Collections.singletonList(ClassPathSupport.createResource(binRoot.getURL())));
        compilePath = ClassPathFactory.createClassPath(spiCp);
        bootPath = JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries();
        javasource = FileUtil.normalizeFile(setUpFile());
        MockServices.setServices(ClassPathProviderImpl.class, SFBQ.class);
        assertNotNull (FileUtil.toFileObject(javasource));
        assertNotNull (JavaSource.forFileObject(FileUtil.toFileObject(javasource)));
        
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, new ClassPath[] {bootPath});
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, new ClassPath[] {compilePath});
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, new ClassPath[] {sourcePath});        
        
        RepositoryUpdater.getDefault().scheduleCompilationAndWait(srcRoot, srcRoot).await();
    }
    
    private static final int VERSION = 0;
    private static final int SUBVERSION = 3;
    private static final String INDEX_DIR = "var"+File.separatorChar+"cache"+File.separatorChar+"index"+File.separatorChar+VERSION+'.'+SUBVERSION;    //NOI18N
    
    protected String pkgname = null;
    protected File setUpFile() throws Exception {
        if (!src.exists()) {
            src.mkdirs();
        }
        assertTrue (src.exists());
        File dir = src;
        if (pkgname != null) {
            String packagedir = pkgname.replace ('.', File.separatorChar);
            dir = new File (dir, packagedir);
            dir.mkdirs();
        }
        File javasource = new File (dir, dataFileName + ".java");
        if (javasource.exists()) {
            javasource.delete();
        }
        javasource.createNewFile();
        assertTrue ("Could not create " + javasource.getPath(), 
                javasource.exists());
        
        InputStream stream = BaseTestCase.class.getResourceAsStream(
                "data/" + dataFileName + ".txt");        
        
        assertNotNull ("Could not find data file " + "data/" + 
                dataFileName + ".txt relative to " + getClass().getName(),
                stream);
        OutputStream out = new FileOutputStream (javasource);
        try {
            FileUtil.copy(stream, out);
        } finally {
            out.flush();
            out.close();
            stream.close();
        }
        stream = BaseTestCase.class.getResourceAsStream(
            "data/" + dataFileName + ".txt");
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        FileUtil.copy (stream, dest);
        try {
            byte[] b = dest.toByteArray();
            Charset set = Charset.forName("UTF-8");        
            fileContent = set.decode(ByteBuffer.wrap(b)).toString();
        } finally {
            stream.close();
            dest.close();
        }
        srcRoot.refresh();
        return javasource;
    }
    
    private void hackIndexUtil (File f) throws Exception {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        ClassLoader nue = Lookup.getDefault().lookup (ClassLoader.class);
//        Thread.currentThread().setContextClassLoader(nue);
        try {
            Class clazz = nue.loadClass ("org.netbeans.modules.java.source.usages.Index");        
            Method m = clazz.getDeclaredMethod ("setCacheFolder", File.class);
            m.setAccessible(true);
            m.invoke(null, f);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }
    
    //where
    private void clearAllButCaches () throws IOException {
        File f = new File (getWorkDir(),"src/foo");         //NOI18N
        File[] c = f.listFiles();
        if (c != null) {
            for (File x : c) {
                x.delete();
            }
        }
        f.delete();
    }
    

    @Override
    protected void tearDown() throws Exception {
        MockServices.setServices();
    }        
    
    private static void createFile (final String path, final String content) throws IOException {
        assert srcRoot != null && srcRoot.isValid();
        srcRoot.getFileSystem().runAtomicAction(new FileSystem.AtomicAction () {
            public void run () throws IOException {
                final FileObject data = FileUtil.createData(srcRoot, path);
                assert data != null;
                final FileLock lock = data.lock();
                try {
                    PrintWriter out = new PrintWriter (new OutputStreamWriter (data.getOutputStream(lock)));
                    try {
                        out.print (content);
                    } finally {
                        out.close ();
                    }
                } finally {
                    lock.releaseLock();
                }
            }
        });                
    }
    
    private static void deleteFile (final String path) throws IOException {
        assert srcRoot != null && srcRoot.isValid();
        final FileObject data  = srcRoot.getFileObject(path);
        if (data != null) {
            data.delete();
        }
    }
       

    public static class ClassPathProviderImpl implements ClassPathProvider {
        public ClassPath findClassPath(final FileObject file, final String type) {
            final FileObject[] roots = sourcePath.getRoots();
            for (FileObject root : roots) {
                if (root.equals(file) || FileUtil.isParentOf(root, file)) {
                    if (type == ClassPath.SOURCE) {
                        return sourcePath;
                    }
                    if (type == ClassPath.COMPILE) {
                        return compilePath;
                    }
                    if (type == ClassPath.BOOT) {
                        return bootPath;
                    }
                }
            }
            return null;
        }        
    }
    
    public static class SFBQ implements SourceForBinaryQueryImplementation {

        public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) { 
            try {
                if (binaryRoot.equals(binRoot.getURL())) {
                    return new SourceForBinaryQuery.Result () {

                        public FileObject[] getRoots() {
                            return new FileObject[] {srcRoot};
                        }

                        public void addChangeListener(ChangeListener l) {
                        }

                        public void removeChangeListener(ChangeListener l) {
                        }

                    };
                }
            } catch (FileStateInvalidException e) {}
            return null;
        }
        
    }
    
    private static final class MutableCp implements ClassPathImplementation {
        
        private final PropertyChangeSupport support;
        private List<? extends PathResourceImplementation> impls;
        
        
        public MutableCp () {
             this (Collections.<PathResourceImplementation>emptyList());
        }
        
        public MutableCp (final List<? extends PathResourceImplementation> impls) {
            assert impls != null;
            support = new PropertyChangeSupport (this);
            this.impls =impls;
        }

        public List<? extends PathResourceImplementation> getResources() {
            return impls;
        }
                
        public void addPropertyChangeListener(final PropertyChangeListener listener) {
            assert listener != null;
            this.support.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(final PropertyChangeListener listener) {
            assert listener != null;
            this.support.removePropertyChangeListener(listener);
        }
        
        
        void setImpls (final List<? extends PathResourceImplementation> impls) {
            assert impls != null;
            this.impls = impls;
            this.support.firePropertyChange(PROP_RESOURCES, null, null);
        }
        
    }
}
