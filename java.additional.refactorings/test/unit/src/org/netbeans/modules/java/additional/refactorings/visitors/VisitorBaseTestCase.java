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
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Tim Boudreau
 */
public abstract class VisitorBaseTestCase<R,D> extends BaseTestCase2<R,D> {

    protected FileObject sourceFile;
    protected TreeVisitor<R,D> visitor;
    protected D argument;
    protected R scanResult;
    public VisitorBaseTestCase(String name, String dataFileName) {
        super (name, dataFileName);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        doParse();
    }
    
    protected int indexInFile (String txt) {
        //Need this to avoid crlf offset differences depending on whether
        //test is run on windows or unix
        int result = fileContent.indexOf(txt);
        return result;
    }
    
    protected void doParse() throws Exception {
        sourceFile = FileUtil.toFileObject(javasource);
        source = JavaSource.forFileObject(sourceFile);
        ModificationResult res = source.runModificationTask(new Stub());
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
    
    protected void beforeScan (WorkingCopy copy) {
        
    }
    
    private class Stub implements CancellableTask <WorkingCopy> {
        boolean cancelled;
        public void cancel() {
            cancelled = true;
        }

        public void run(WorkingCopy copy) throws Exception {
            if (!cancelled) {
                copy.toPhase(Phase.RESOLVED);
                beforeScan (copy);
                CompilationUnitTree unit = copy.getCompilationUnit();
                assertNotNull ("Compilation unit null", unit);
                VisitorBaseTestCase.this.copy = copy;
                VisitorBaseTestCase.this.visitor = createVisitor (copy);
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
    

}
