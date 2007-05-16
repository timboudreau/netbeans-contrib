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
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.LocalFileSystem;

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
    
    private File dataDir;
    private File rootDir;
    protected LocalFileSystem fs;
    protected FileObject sourceFile;
    protected JavaSource source;
    protected WorkingCopy copy;
    protected D argument;
    protected R scanResult;
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
            out.close();
        }
        assertTrue (dataDir.exists());
        assertTrue (userDir.exists());
        
        fs = new LocalFileSystem();
        fs.setRootDirectory(dataDir);
        
        //A butt-ugly hack to fake out the parser
        FileObject x = fs.getRoot().createFolder("null");
        x = x.createFolder("var");
        x = x.createFolder("log");        
        System.setProperty ("netbeans.user", userDir.getPath());
        doParse();
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
//        if (super.getTestNumber() == 0) {
            return root;
//        } else {
//            return null;
//        }
    }
    
    private class Stub implements CancellableTask <WorkingCopy> {
        boolean cancelled;
        public void cancel() {
            cancelled = true;
        }

        public void run(WorkingCopy copy) throws Exception {
            if (!cancelled) {
                BaseTestCase.this.copy = copy;
                copy.toPhase(Phase.RESOLVED);
                TreeVisitor<R,D> visitor = createVisitor (copy);
                argument = createArgument();
                Tree tree = getTreeToUse (copy.getCompilationUnit());
                if (visitor instanceof TreeScanner) {
                    scanResult = ((TreeScanner<R,D>) visitor).scan (tree, argument);
                } else {
                    scanResult = tree.accept (visitor, argument);
                }
            }
        }
    }
    
    protected static Tree findTree (CompilationUnitTree tree, Kind kind, String name) {
        Finder finder = new Finder(name);
        return finder.scan(tree, null);
    }
    
    private static class Finder extends TreePathScanner<Tree, Kind> {
        private final String name;
        public Finder (String name) {
            this.name = name;
        }

        @Override
        public Tree scan(Tree tree, Kind kind) {
//            if (tree != null) {
//                System.err.println("Scanning for " + kind + " for " + 
//                        " in " + nameOf(tree) + " kind " + tree.getKind() 
//                        + "\nTREE:\n" + tree);
//            }
            if (tree != null && tree.getKind() == kind) {
                String name = nameOf (tree);
                if (name != null && this.name != null && this.name.equals(name)) {
                    return tree;
                } else if (name == null && this.name == null) {
                    return tree;
                }
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
}
