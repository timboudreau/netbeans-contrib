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
