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

package org.netbeans.modules.latex.model.command;

import java.io.IOException;
import java.util.List;
import javax.swing.text.Document;

/**
 *
 * @author Jan Lahoda
 */
public interface CommandUtilities {

    /**Find the deepest node that contains position <code>pos</code>.
     *
     * @param pos positions two find in the tree.
     * @return deepest node that contains given position, or null if no such node.
     * @throws java.io.IOException if something goes wrong during reading the source.
     */
    public abstract Node findNode(SourcePosition pos) throws IOException;

    /**A convenience method equvivalent to calling {@link #findNode(SourcePosition)}
     * with a SourcePosition argument corresponding to given <code>doc</code>
     * and <code>offset</code>
     *
     * @param doc a document to construct SourcePosition from.
     * @param offset an offset to construct SourcePosition from.
     * @return deepest node that contains given position, or null if no such node.
     *
     * @throws java.io.IOException if something goes wrong during reading the source.
     */
    public abstract Node findNode(Document doc, int offset) throws IOException;

    /** Returns list of defined commands at the given position.
     *
     *  @param pos position in the document where defined commands should be found.
     *
     *  @return java.util.List of Command(s) that are defined at given position in the document;
     *
     *  @throws IOException if a document cannot be opened, or some other problem occurs.
     */
    public abstract List getCommands(SourcePosition pos) throws IOException;

    /** Returns a command with given name at the given position. If the command cannot be
     *  found, returns null.
     *
     *  @param pos position in the document where defined command should be found.
     *  @param name the name of the command (meaning the command itself).
     *
     *  @return found Command or null.
     *
     *  @throws IOException if a document cannot be opened, or some other problem occurs.
     */
    public abstract Command getCommand(SourcePosition pos, String name) throws IOException;

    /** Returns list of defined environments at the given position.
     *
     *  @param pos position in the document where defined environments should be found.
     *
     *  @return java.util.List of Environment(s) that are defined at given position in the document;
     *
     *  @throws IOException if a document cannot be opened, or some other problem occurs.
     */
    public abstract List getEnvironments(SourcePosition pos) throws IOException;

    /** Returns an environment with given name at the given position. If the environment cannot be
     *  found, returns null.
     *
     *  @param pos position in the document where defined environment should be found.
     *  @param name the name of the environment (meaning the mandatory argument of the \begin command).
     *
     *  @return found Environment or null.
     *
     *  @throws IOException if a document cannot be opened, or some other problem occurs.
     */
    public abstract Environment getEnvironment(SourcePosition pos, String name) throws IOException;
}
