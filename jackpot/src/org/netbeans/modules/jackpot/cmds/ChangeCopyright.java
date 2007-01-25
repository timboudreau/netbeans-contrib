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
import org.netbeans.api.java.source.query.CommentHandler;
import org.netbeans.api.java.source.query.CommentSet;
import org.netbeans.api.java.source.query.QueryEnvironment;
import org.netbeans.api.java.source.query.SearchEntry;
import org.netbeans.api.java.source.transform.ChangeSet;
import org.netbeans.api.java.source.transform.TransformResult;
import org.netbeans.api.java.source.transform.Transformer;
import com.sun.source.tree.*;
import javax.lang.model.element.Element;
import java.util.List;

/**
 * Replaces the leading comment in a source file with a specified replacement.  
 * If no existing comment exists, the new comment will be added.
 */
public class ChangeCopyright extends Transformer<Void,Object> {
    { queryDescription = "Change copyright"; }
    
    public String copyrightText;  // set by PropertySheetInfo
    private CommentHandler commentHandler;

    @Override
    public void attach(QueryEnvironment env) {
        super.attach(env);
        commentHandler = env.getCommentHandler();
	result = new TopLevelResult(this, env, queryDescription, changes);
    }
    
    @Override
    public void release() {
        super.release();
        commentHandler = null;
    }
    
    @Override
    public void destroy() {
        result = null; // released here because result is shared by invocations
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree tree, Object p) {
        CommentSet comments = commentHandler.getComments(tree);
        List<Comment> precedingComments = comments.getPrecedingComments();
        String matchText = copyrightText.trim();
        for (Comment c : precedingComments)
            if (c.getText().trim().equals(matchText))
                return null; // skip
        CompilationUnitTree newTree = 
            make.CompilationUnit(tree.getPackageName(), tree.getImports(),
                                 tree.getTypeDecls(), tree.getSourceFile());
        setElement(newTree, getElement(tree));
        CommentSet newComments = commentHandler.getComments(newTree);
        newComments.addPrecedingComment(copyrightText);
        newComments.addTrailingComments(comments.getTrailingComments());
        changes.rewrite(tree, newTree);
        addResult(newTree, "updated copyright");
        return null;
    }

    @Override
    protected TransformResult makePreviewResult() {
        return (TransformResult)result;
    }
    
    private static class TopLevelResult extends TransformResult {
	private static final String[] columnNames = { "Source file", "Notes" };

	TopLevelResult(Transformer transformer, QueryEnvironment env, String title, ChangeSet changeSet) {
	    super(transformer, title, changeSet);
            attach(env);
	}
	
        @Override
        protected CompilationUnitTree getTopLevel(Element sym, Tree tree, Tree root) {
	    assert tree instanceof CompilationUnitTree;
	    return (CompilationUnitTree)tree;
	}

	public int getColumnCount() {
	    return 2;
        }
    
	public String getColumnName(int column) {
	    return columnNames[column];
	}
    
	public Object getValueAt(int row, int column) {
	    SearchEntry se = getResults()[row];
	    switch (column) {
		case 0:
                    return getSource(se);
		case 1:
		    return se.getNote();
	    }
	    return "Bug!!";
	}
        
        private static String getSource(SearchEntry se) {
            assert se.tree instanceof CompilationUnitTree;
            return ((CompilationUnitTree)se.tree).getSourceFile().toUri().getPath();
        }
    
        public String asLogMessage(SearchEntry se) {
            StringBuffer sb = new StringBuffer();
            sb.append(getSource(se));
            sb.append(" \""); // NOI18N
            sb.append(se.getNote());
            sb.append('\"');  // NOI18N
            return sb.toString();
        }
    }
}
