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

package org.netbeans.modules.tasklist.client;

import org.netbeans.modules.tasklist.client.Suggestion;

/**
 * A SuggestionPerformer is a class which is registered with a Suggestion,
 * and when its perform() method is called, it carries out the Suggestion.
 * It also has an interface for confirming the action, and customizing it.
 *
 * @author Tor Norbye
 */
public interface SuggestionPerformer {
    /** Perform the suggestion. This method should perform the
     * task as described in the Suggestion's description.
     * <p>
     * You may perform the action in any thread you want. But you're
     * responsible for ensuring that the action is still valid when
     * it's run. (For example, if your action is going to edit a user
     * document by inserting at a particular position, and your perform
     * method puts this action on a thread which kicks in after 15 seconds,
     * you have to be able to adjust the file position if the user edits
     * the document in the mean time.)
     * <p>
     * Do not forget to adjust suggestion (in)validity.
     *
     * @param suggestion The suggestion to be performed
     */
    void perform(Suggestion suggestion);

    /** Return a confirmation message (or component) for the action.
     * This can be a Component, or a String (or any object whose
     * toString() method returns the confirmation message you want).
     * If you return null, the Suggestion will not provide any
     * confirmation message to the user. That's not recommended for
     * suggestions which modify any user data; also, users can easily
     * disable suggestions they don't like through the confirmation
     * dialog so modules are strongly encouraged to provide a confirmation
     * description or component.
     * <p>
     * You can write out more detailed confirmations here; for
     * example, a task to clean up the Imports in a file may include
     * a listbox which describes all the import statements about to
     * be removed. You can also let users edit this listbox (add
     * a Remove button which removes selected items for example),
     * and the perform() method can read values the panel back.  If
       you do this [add state to be considered
     * by the perform() method] you should store this state in the
     * SuggestionPerformer and make sure that you create a unique
     * SuggestionPerformer instance for each Suggestion.
     * <p>
     * 
     * @param suggestion The suggestion that we want a confirmation
     *    description for.
     * @return A component or string which contains a summary of what
     *    executing the Suggestion will accomplish. May be null.
     */
    Object getConfirmation(Suggestion suggestion);
    
    /** 
     * Indicate whether this action has a confirmation panel or text
     * (without actually having to create it.)
     *
     * @return True iff this performer has a confirmation.
     */
    boolean hasConfirmation();
}
