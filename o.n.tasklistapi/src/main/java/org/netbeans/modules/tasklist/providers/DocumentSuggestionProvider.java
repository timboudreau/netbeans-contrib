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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

package org.netbeans.modules.tasklist.providers;

import java.util.List;
import javax.swing.text.Document;
import org.openide.loaders.DataObject;

/**
 * This class is used for passive SuggestionProviders.
 * Typically, you just need to implement <code>scan()</code> and
 * <code>rescan()</code>.
 * <p>
 * The API does not define which thread these methods are called on,
 * so don't make any assumptions. If you want to post something on
 * the AWT event dispatching thread for example use SwingUtilities.
 * <p>
 * Note that changes in document attributes only are "ignored" (in
 * the sense that they do not cause document edit notification.)
 *
 * <p>
 * @author Tor Norbye
 * @author Petr Kuzel, SuggestionContext refactoring
 * @since 1.3  (well all signatures changed in this version)
 *
 * @todo why it extends SuggestionProvider. Its events are absolutely useless
 * in this request-responce mode. I'd revert it beause being able to push
 * suggestions is more advanced provider side feature tnan simply responding.
 */
abstract public class DocumentSuggestionProvider extends SuggestionProvider {

    /**
     * Scan the given document for suggestions. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example as part of a directory
     * scan for suggestions.
     * <p>
     * @param env The environment being scanned
     * @return list of tasks that result from the scan. May be null.
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     *
     * @todo suggestions are created by SuggestionManager
     * and that disallows to change equals logic
     * that is needed to merge lists by clients. It
     * can be solved by <code>List merge(List old, List updated)</code>
     *
     * @todo provider can find out that condions have
     * changed (it can attach listeners to specifics sources)
     * so it would like to inform consumer about change.
     * E.g. SourceTaskProvider listens on settings change.
     * On the other hand it's strange that SourceTaskProvider
     * does not listen on document changes and leaves
     * it on consumer. It's OK for this method but
     * wrong for SuggestionManager registered ones.
     * <p>
     * Also fixing provides need to notify that fix
     * eliminated the suggestion. Here could help
     * suggestion valid flag intead of changing list
     * membership.
     *
     * @todo another subtle obstacle right here is caused
     * fact that implementation does not allow suggestion/task
     * to be member of more tasklists. So all method clients
     * must clone right now until this bug fixed. See
     * SuggestionsBroker#performRescanInRP.
     *
     */
    abstract public List scan(SuggestionContext env);
}
