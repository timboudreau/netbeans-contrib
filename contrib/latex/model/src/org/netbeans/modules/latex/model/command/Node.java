/*
 *                          Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 *
 * The Original Code is the LaTeX module.
 * The Initial Developer of the Original Code is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002,2003.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Jan Lahoda
 */
public interface Node {
    
    /** Returns textual representation of this Node. It should not contain
     *  commands.
     *
     *  @return textual representation of this Node.
     */
    public CharSequence   getText();
    
    /**Returns textual representation of this Node, as close as possible
     * to the source.
     * 
     * @return textual representation of this Node.
     */
    public CharSequence   getFullText();
    
    /** Returns starting position of this Node.
     *
     *  @return starting position of this node.
     */
    public SourcePosition getStartingPosition();
    
    /** Returns ending position of this Node.
     *
     *  @return ending position of this node.
     */
    public SourcePosition getEndingPosition();
    
    /** Returns DocumentNode that contains this Node
     *
     *  @return DocumentNode that contains this Node.
     */
    public DocumentNode   getDocumentNode();
    
    /** Returns parent (a Node one lever up in the
     *  hiearchy) of this Node.
     *
     *  @return parent of this Node
     */
    public Node           getParent();
    
    /** Traverses the tree. For more information
     *  {@link TraverseHandler}.
     *
     *  @return th TraverseHandler directing the traversing
     */
    public void           traverse(TraverseHandler th);

    /** Returns an Iterator over all Tokens that are between this.getStartingPosition() and
     *  this.getEndingPosition(), excluding tokens for children of this.
     *
     *  @return Iterator of Tokens of the node.
     */
    public abstract Iterator/*<Token>*/ getNodeTokens() throws IOException;
    
    /** Returns an Iterator over all Tokens that are between this.getStartingPosition() and
     *  this.getEndingPosition(), including tokens for children of this.
     *
     *  @param n Node we produce tokens for.
     *  @return Iterator of Tokens of the node.
     */
    public abstract Iterator/*<Token>*/ getDeepNodeTokens() throws IOException;
    
    /** Check whether this node contains given position. The children of the node are
     *  not considered when computing.
     *
     *  @param position SourcePosition to check
     *  @return true if the given position is inside this Node, but inside none of its children.
     */
    public abstract boolean contains(SourcePosition position);
    
}
