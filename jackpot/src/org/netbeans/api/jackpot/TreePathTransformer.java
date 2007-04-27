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
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.java.source.TreePathHandle;
import org.netbeans.api.java.source.WorkingCopy;
import org.openide.filesystems.FileObject;

/**
 * An abstract Transformer which extends TreePathScanner to visit the project's 
 * AST nodes using the visitor pattern.
 * 
 * @param R the return type used for visitor methods
 * @param P an optional parameter type which is passed to the visitor methods
 * 
 * @author Tom Ball
 */
public abstract class TreePathTransformer<R, P> extends TreePathQuery<R, P> implements Transformer {
    private List<String> failures;
    
    /** Creates a new instance of TreePathTransformer */
    public TreePathTransformer() {
        super();
        failures = new ArrayList<String>();
    }

    /**
     * Forward a change (query match and replacement) to the transformer's context.
     * 
     * @param path the path of the tree that matched the query
     * @param newTree the replacement for the tree path's leaf node
     */
    public final void addChange(TreePath path, Tree newTree) {
        addChange(path, newTree, null);
    }

    /**
     * Forward a change (query match and replacement) to the transformer's context,
     * along with a note describing the change.
     * 
     * @param path the path of the tree that matched the query
     * @param newTree the replacement for the tree path's leaf node
     * @param note optional information about the match, or 
     *        <code>null</code> if there is no note.
     */
    public final void addChange(TreePath path, Tree newTree, String note) {
        CompilationUnitTree unit = path.getCompilationUnit();
        FileObject file = info.getFileObject();
        SourcePositions positions = info.getTrees().getSourcePositions();
        Tree tree = path.getLeaf();
        int start = (int)positions.getStartPosition(unit, tree);
        int end = (int)positions.getEndPosition(unit, tree);
        String label = makeLabel(path);
        if (note == null || note.length() == 0)
            note = createNote(tree, newTree);
        context.addChange(path, file, start, end, label, note, newTree.toString());
        getWorkingCopy().rewrite(path.getLeaf(), newTree);
    }
    
    private String createNote(Tree oldTree, Tree newTree) {
        StringBuffer sb = new StringBuffer();
        sb.append(oldTree.toString());
        sb.append(" => ");
        sb.append(newTree.toString());
        return sb.toString();
    }
    
    public final void transformationFailure(String message) {
        failures.add(message);
    }

    public final WorkingCopy getWorkingCopy() {
        return (WorkingCopy)getCompilationInfo();
    }
    
    /**
     * Returns whether there were any translation failures reported by the
     * transformer.
     * 
     * @return true if any failures were reported
     */
    public final boolean hasFailures() {
        return failures.size() > 0;
    }
    
    /**
     * Returns the list of translation failures reported by the transformer, 
     * which is normally empty.
     * 
     * @return the list of failure messages
     */
    public final List<String> getFailures() {
        return failures;
    }
}
