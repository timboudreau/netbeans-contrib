/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
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
    
    /** Indicate whether this action has a confirmation panel or text
     * (without actually having to create it.)
     * @return True iff this performer has a confirmation.
     */
    boolean hasConfirmation();
}
