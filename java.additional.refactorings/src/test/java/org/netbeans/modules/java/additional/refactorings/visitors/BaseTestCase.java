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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.java.additional.refactorings.visitors;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.junit.MockServices;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.Repository;

/**
 *
 * @author Tim
 */
public abstract class BaseTestCase <R,D> extends NbTestCase {
    private final String dataFileName;

    public BaseTestCase(String name, String dataFileName) {
        super (name);
        this.dataFileName = dataFileName;
    }
    
    public BaseTestCase(String name) {
        super (name);
        this.dataFileName = name;
    }
    
    protected TreeVisitor<R,D> visitor;
    private static File dataDir;
    private static LocalFileSystem fs;
    private static FileObject dataDirFo;
    private File rootDir;
    protected FileObject sourceFile;
    protected JavaSource source;
    protected WorkingCopy copy;
    protected D argument;
    protected R scanResult;
    protected String fileContent;
    protected void setUp() throws Exception {
        super.setUp();
        File tmp = new File (System.getProperty("java.io.tmpdir"));        
        String dirName = getClass().getName().replace(".", "_") +
                System.currentTimeMillis();
        rootDir = new File (tmp, dirName);
        int ix = 0;
        while (rootDir.exists()) {
            String nm = dirName + (ix++);
            rootDir = new File  (tmp, nm);
        }
        rootDir.mkdir();
        dataDir = new File (rootDir, "src");
        dataDir.mkdir();
        File userDir = new File (rootDir, "userdir");
        userDir.mkdir();
        File javasource = new File (dataDir, dataFileName + ".java");
        if (!javasource.createNewFile()) {
            fail ("Could not create " + javasource);
        }
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
        
        assertTrue (dataDir.exists());
        assertTrue (userDir.exists());
        
        fs = new LocalFileSystem();
        fs.setRootDirectory(dataDir);
        dataDirFo = fs.getRoot();
        assertNotNull (dataDirFo);
        
        
        srcCpImpl = new Cp (Collections.singletonList(ClassPathSupport.createResource(dataDir.toURI().toURL())));
        sourcePath = ClassPathFactory.createClassPath(srcCpImpl);
        bootPath = JavaPlatformManager.getDefault().getDefaultPlatform().getBootstrapLibraries();
        MockServices.setServices(Rep.class, SFBQ.class);
//        MockServices.setServices(Rep.class, ClassPathProviderImpl.class, SFBQ.class);
//        Repository.getDefault().addFileSystem(fs);
        GlobalPathRegistry.getDefault().register(ClassPath.BOOT, new ClassPath[] {bootPath});
        GlobalPathRegistry.getDefault().register(ClassPath.COMPILE, new ClassPath[] {sourcePath});
        GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, new ClassPath[] {sourcePath});        
        
        //A butt-ugly hack to fake out the parser
        System.setProperty ("netbeans.user", userDir.getPath());
        doParse();
    }
    
    private static Cp srcCpImpl;
    private static ClassPath bootPath;
    private static ClassPath sourcePath;
    
    protected int indexInFile (String txt) {
        //Need this to avoid crlf offset differences depending on whether
        //test is run on windows or unix
        int result = fileContent.indexOf(txt);
        return result;
    }
    
    protected void doParse() throws Exception {
        sourceFile = fs.getRoot().getFileObject(dataFileName + ".java");
        source = JavaSource.forFileObject(sourceFile);
        ModificationResult res = source.runModificationTask(new Stub());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        fs = null;
        copy = null;
        source = null;
        recurseDel (rootDir);
    }
    
    private static void recurseDel (File f) {
        try {
            if (f.isDirectory()) {
                File[] ff = f.listFiles();
                for (int i = 0; i < ff.length; i++) {
                    recurseDel (ff[i]);
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        f.delete();
    }
    
    /**
     * Create a TreeVisitor.  If a TreePathVisitor, the tree returned by
     * getTreeToUse() will be passed to its scan() method;  otherwise
     * accept() on that tree will be called with the visitor.  The result
     * of the scan will be placed into the scanResult protected field.
     */ 
    protected abstract TreeVisitor<R,D> createVisitor(WorkingCopy copy);
    /**
     * Create the argument that should be passed as the second argument
     * to the created visitor's visit method.  The result will be placed
     * into the argument protected field.
     */ 
    protected abstract D createArgument();
    
    /**
     * Get the tree that should be passed to the visitor.  The default
     * implementation simply returns the passed compilation unit tree.
     * If you want to narrow down a particular method, field or class,
     * override to call, e.g.
     * <code>return findTree (tree, Kind.METHOD, "someMethod")</code>.
     */ 
    protected Tree getTreeToUse (CompilationUnitTree root) {
        return root;
    }
    
    private class Stub implements CancellableTask <WorkingCopy> {
        boolean cancelled;
        public void cancel() {
            cancelled = true;
        }

        public void run(WorkingCopy copy) throws Exception {
            if (!cancelled) {
                copy.toPhase(Phase.RESOLVED);
                
                CompilationUnitTree unit = copy.getCompilationUnit();
                assertNotNull ("Compilation unit null", unit);
                BaseTestCase.this.copy = copy;
                BaseTestCase.this.visitor = createVisitor (copy);
                assertNotNull ("Test broken - createVisitor() return null", 
                        visitor);
                
                argument = createArgument();
                Tree tree = getTreeToUse (unit);
                assertNotNull ("Tree is null", tree);
                assertNotNull ("Visitor null", visitor);
                if (visitor instanceof TreeScanner) {
                    TreePath path;
                    if (tree == unit) {
                        path = new TreePath(unit);
                    } else {
                        path = TreePath.getPath(unit, tree);
                    }
                    assertNotNull (path);
                    assertNotNull (path.getCompilationUnit());
                    assertNotNull (path.getLeaf());
                    scanResult = ((TreePathScanner<R,D>) visitor).scan (path, argument);
                } else {
                    scanResult = tree.accept (visitor, argument);
                }
            }
        }
    }
    
    protected static Tree findTree (CompilationUnitTree tree, Kind kind, String name) {
        Finder finder = new Finder(name);
        return finder.scan(tree, kind);
    }
    
    private static class Finder extends TreePathScanner<Tree, Kind> {
        private final String name;
        public Finder (String name) {
            this.name = name;
        }

        int ix = 0;
        @Override
        public Tree scan(Tree tree, Kind kind) {
            String name = nameOf (tree);
            boolean nameMatch = this.name.equals(name);
            boolean kindMatch = (tree != null && tree.getKind() == kind) ;
            boolean match = nameMatch && kindMatch;
            if (match) {
                return tree;
            }
            return super.scan(tree, kind);
        }
        
        private String nameOf (Tree tree) {
            if (tree instanceof ClassTree) {
                return ((ClassTree) tree).getSimpleName().toString();
            } else if (tree instanceof MethodTree) {
                return ((MethodTree) tree).getName().toString();
            } else if (tree instanceof VariableTree) {
                return ((VariableTree) tree).getName().toString();
            } else {
                return null;
            }
        }
    }
    
    public static class ClassPathProviderImpl implements ClassPathProvider {
        public ClassPath findClassPath(final FileObject file, final String type) {
            return type == ClassPath.BOOT ? bootPath : sourcePath;
        }        
    }
    
    
    private static final class Cp implements ClassPathImplementation {
        private List<? extends PathResourceImplementation> impls;
        public Cp () {
             this (Collections.<PathResourceImplementation>emptyList());
        }
        
        public Cp (final List<? extends PathResourceImplementation> impls) {
            assert impls != null;
            this.impls =impls;
        }

        public List<? extends PathResourceImplementation> getResources() {
            return impls;
        }
                
        public void addPropertyChangeListener(final PropertyChangeListener listener) {}

        public void removePropertyChangeListener(final PropertyChangeListener listener) {}
    }    
    
    public static class SFBQ implements SourceForBinaryQueryImplementation {
        public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) { 
            return new SourceForBinaryQuery.Result () {
                public FileObject[] getRoots() {
                    return new FileObject[] {dataDirFo};
                }
                public void addChangeListener(ChangeListener l) {}
                public void removeChangeListener(ChangeListener l) {}
            };
        }
    }
    
    public static class Rep extends Repository {
        public Rep () {
            super (new MultiFileSystem(new FileSystem[] { fs }));
            MultiFileSystem f = (MultiFileSystem) super.getFileSystems().nextElement();
            
        }
    }
}
