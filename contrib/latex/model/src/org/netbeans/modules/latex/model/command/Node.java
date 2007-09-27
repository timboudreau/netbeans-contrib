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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
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
