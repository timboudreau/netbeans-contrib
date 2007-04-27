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

package org.netbeans.modules.jackpot.engine;

import com.sun.source.util.TreePath;
import org.openide.filesystems.FileObject;

/**
 * A Tree node which was matched during query execution, along with an optional
 * comment regarding its selection.  This Tree is defined using a TreePath, so
 * that it can be located in the source file.
 * 
 * @author Tom Ball
 */
public final class Result {
    TreePath path;
    FileObject file;
    int startPos;
    int endPos;
    String label;
    String note;
    String newSource;

    /**
     * Create a new query result.
     * @param path the tree path of the matched node.
     * @param file the file the match was found
     * @param start the offset of the beginning of the node in the file
     * @param end the offset of the end of the node in the file
     * @param label a label describing the node to the user
     * @param note an optional note about the match, or null
     * @param newSource the replacement source, or null if not a transformer
     */
    public Result(TreePath path, FileObject file, int start, int end, 
                  String label, String note, String newSource) {
        this.path = path;
        this.file = file;
        this.startPos = start;
        this.endPos = end;
        this.label = label;
        this.note = note;
        this.newSource = newSource;
    }
    
    /**
     * Returns a handle to the path of the AST node of this result.
     * @return the tree path handle
     */
    public TreePath getTreePath() {
        return path;
    }
    
    /**
     * Returns the FileObject that contains the matched node.
     * @return the file
     */
    public FileObject getFileObject() {
        return file;
    }
    
    /**
     * Returns the beginning offset of the matched node in the source file text.
     * @return the starting offset
     */
    public int getStartPos() {
        return startPos;
    }
    
    /**
     * Returns the end offset of the matched node in the source file text.
     * @return the end offset
     */
    public int getEndPos() {
        return endPos;
    }
    
    /**
     * Returns a user-readable label describing the AST node.
     * @return the node's label
     */
    public String getLabel() {
        return label;
    }
        
    /**
     * Returns a note describing this result's match.
     * @return the note, or null if no note is available
     */
    public String getNote() {
        return note;
    }
    
    /**
     * Returns a simple version of the replacement source code.
     * @return the source code, or null if not a transformation
     */
    public String getNewSource() {
        return newSource;
    }
    
    /**
     * @return true if this is a transformation result
     */
    public boolean isTransformerResult() {
        return newSource != null && newSource.length() > 0;
    }
}
