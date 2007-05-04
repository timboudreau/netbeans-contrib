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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.jackpot;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.support.CancellableTreePathScanner;
import org.openide.filesystems.FileObject;

/**
 * An abstract Query which extends TreePathScanner to visit the project's AST 
 * nodes using the visitor pattern.  
 * 
 * @param R the return type used for visitor methods
 * @param P an optional parameter type which is passed to the visitor methods
 * 
 * @author Tom Ball
 * @see com.sun.source.util.TreePath
 * @see com.sun.source.util.TreePathScanner
 * @see org.netbeans.api.java.source.support.CancellableTreePathScanner
 */
public abstract class TreePathQuery<R, P> extends CancellableTreePathScanner<R, P> implements Query {
    QueryContext context;
    JavaSource source;
    CompilationInfo info;
    
    public void init(QueryContext context, JavaSource source) {
        this.context = context;
        this.source = source;
    }

    public void attach(CompilationInfo info) {
        this.info = info;
    }
    
    public void run() {
        scan(new TreePath(info.getCompilationUnit()), null);
    }

    public void release() {
        info = null;
    }

    public void destroy() {
        source = null;
    }
    
    public final JavaSource getJavaSource() {
        return source;
    }
    
    public final CompilationInfo getCompilationInfo() {
        return info;
    }
    
    /**
     * Returns the context the query is executing within.
     * 
     * @return the query's context
     */
    public final QueryContext getContext() {
        return context;
    }
    
    /**
     * Adds a result using the scanner's current TreePath, without any comment.
     * This method is normally called only from queries that match on a single
     * condition, such as FindUnusedVariables where a variable is only added
     * if it is unused.  Queries which match on multiple possible conditions
     * should include a comment giving the user a reason for the specific
     * result.
     */
    public final void addResult() {
        addResult(getCurrentPath(), "");
    }

    /**
     * Adds a result using the scanner's current TreePath.
     * 
     * @param note optional information about the match, or 
     *        <code>null</code> if there is no note.
     */
    public final void addResult(String note) {
        addResult(getCurrentPath(), note);
    }

    /**
     * Forward a result (query match) to the query's context.
     * 
     * @param path the path of the tree that matched the query
     * @param note optional information about the match, or 
     *        <code>null</code> if there is no note.
     */
    public final void addResult(TreePath path, String note) {
        CompilationUnitTree unit = path.getCompilationUnit();
        FileObject file = info.getFileObject();
        SourcePositions positions = info.getTrees().getSourcePositions();
        Tree tree = path.getLeaf();
        int start = (int)positions.getStartPosition(unit, tree);
        int end = (int)positions.getEndPosition(unit, tree);
        String label = makeLabel(path);
        context.addResult(path, file, start, end, label, note);
    }
    
    String makeLabel(TreePath path) {
        Element element = getPathElement(path);
        String cls = null;
        String pkg = "";
        Element e = element;
        while (e != null) {
            if (e instanceof TypeElement) {
                String s = e.getSimpleName().toString();
                cls = cls == null ? s : s + '.' + cls;
            }
            if (e instanceof PackageElement) {
                pkg = "(" + ((PackageElement)e).getQualifiedName().toString() + ")";
                break;
            }
            e = e.getEnclosingElement();
        }
        StringBuilder sb = new StringBuilder();
        if (cls != null) {
            sb.append(cls);
            sb.append('.');
        }
        sb.append(element.getSimpleName());
        if (pkg.length() > 0) {
            sb.append(' ');
            sb.append(pkg);
        }
        return sb.toString();
    }
    
    private Element getPathElement(TreePath path) {
        if (path == null)
            return null;
        Element e = info.getTrees().getElement(path);
        return e != null ? e : getPathElement(path.getParentPath());
    }
}
