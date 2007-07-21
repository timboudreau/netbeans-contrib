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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import org.netbeans.api.lexer.Token;
import org.netbeans.modules.latex.model.lexer.TexTokenId;

/**
 *
 * @author Jan Lahoda
 */
public interface Node extends Attributable {

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
    public abstract Iterable<? extends Token<TexTokenId>> getNodeTokens() throws IOException;
    
    /** Returns an Iterator over all Tokens that are between this.getStartingPosition() and
     *  this.getEndingPosition(), including tokens for children of this.
     *
     *  @param n Node we produce tokens for.
     *  @return Iterator of Tokens of the node.
     */
    public abstract Iterable<? extends Token<TexTokenId>> getDeepNodeTokens() throws IOException;
    
    /** Check whether this node contains given position. The children of the node are
     *  not considered when computing.
     *
     *  @param position SourcePosition to check
     *  @return true if the given position is inside this Node, but inside none of its children.
     */
    public abstract boolean contains(SourcePosition position);
    
}
