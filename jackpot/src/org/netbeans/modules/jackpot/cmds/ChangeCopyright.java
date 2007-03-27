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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.jackpot.cmds;

import org.netbeans.api.java.source.Comment;
import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import java.util.List;
import org.netbeans.api.jackpot.TreePathTransformer;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.TreeUtilities;

/**
 * Replaces the leading comment in a source file with a specified replacement.  
 * If no existing comment exists, the new comment will be added.
 */
public class ChangeCopyright extends TreePathTransformer<Void,Object> {
    private TreeMaker make;
    private TreeUtilities treeUtils;
    
    /**
     * JavaBean property set by PropertySheetInfo
     */
    public String copyrightText;

    @Override
    public void attach(CompilationInfo info) {
        super.attach(info);
        treeUtils = info.getTreeUtilities();
        make = getWorkingCopy().getTreeMaker();
    }
    
    @Override
    public void release() {
        super.release();
        make = null;
        treeUtils = null;
    }

    /**
     * Check unit for whether it starts with specified copyright comment; 
     * if not, update it.
     */
    @Override
    public Void visitCompilationUnit(CompilationUnitTree tree, Object p) {
        List<Comment> precedingComments = treeUtils.getComments(tree, true);
        String matchText = copyrightText.trim();
        for (Comment c : precedingComments)
            if (c.getText().trim().equals(matchText))
                return null; // skip
        CompilationUnitTree newTree = 
            make.CompilationUnit(tree.getPackageName(), tree.getImports(),
                                 tree.getTypeDecls(), tree.getSourceFile());
        Comment copyright = Comment.create(copyrightText);
        make.addComment(newTree, copyright, true);
        for (Comment c : precedingComments)
            make.addComment(newTree, c, true);
        addChange(new TreePath(tree), newTree, "updated copyright");
        return null;
    }
}
