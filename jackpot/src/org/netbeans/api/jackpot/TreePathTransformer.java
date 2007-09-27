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

package org.netbeans.api.jackpot;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreePath;
import java.util.ArrayList;
import java.util.List;
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
