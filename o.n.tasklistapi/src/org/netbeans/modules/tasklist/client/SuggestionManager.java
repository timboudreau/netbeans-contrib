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

import org.openide.util.Lookup;
import org.netbeans.modules.tasklist.client.Suggestion;

import java.util.List;

/**
 * The SuggestionManager manages suggestions, suggestion providers,
 * etc. Modules can obtain a reference to the SuggestionManager
 * and then register suggestions.
 * <p>
 * Here's how you add suggestions to the tasklist:
 * <ol>
 *  <li> Obtain a reference to the SuggestionManager:
 *    <pre>
 *      SuggestionManager manager = SuggestionManager.getDefault();
 *    </pre>
 *    Note: this may return null (in the case where the Suggestions
 *    module is not available/enabled) so check the return value!
 *    <p>
 *  <li> Check to make sure that the type of suggestion you're about
 *       to add is welcome by the user:
 *    <pre>
 *       if (manager.isEnabled(suggestionTypeId) &&
 *           manager.isObserved(suggestionTypeId)) {
 *    </pre>
 *    <p>
 *  <li> Create a new {@link org.netbeans.modules.tasklist.client.Suggestion}:
 *    <pre>
 *       Suggestion suggestion = manager.createSuggestion("myid",
 *                                     "my suggestion description",
 *           new SuggestionPerformer() {
 *             public void perform(Suggestion s) {
 *                beep();
 *             }
 *             public Object getConfirmation(Suggestion s) {
 *                return null;
 *             }
 *             public boolean hasConfirmation() {
 *                return false;
 *             }
 *           },
 *           null
 *       );
 *       suggestion.setPriority(SuggestionPriority.HIGH);
 *    </pre>
 *   <li> Register the suggestion with the SuggestionManager:
 *    <pre>
 *       List addList = new ArrayList(1);
 *       addList.add(suggestion);
 *       manager.register("myid", addList, null, null);
 *    </pre>
 * </ol>
 * Done! (Also see the {@link org.netbeans.modules.tasklist.providers.SuggestionProvider}
 * API for how to write specialized suggestion providers. The above
 * scenario is optimized for the case where you're already doing some
 * computation where you have suggestions as a side effect. If you
 * want to write some code which is only run if the suggestions window
 * is visible etc. etc. that's a better route.)
 * <p>
 *
 * @author Tor Norbye
 */


abstract public class SuggestionManager {

    /** Construct a new SuggestionManager instance. */
    protected SuggestionManager() {
    }

    /**
     * Construct a new suggestion, of the given type, with the given
     * summary, and performing the designated action.
     *
     * @param type The type of this suggestion. This is a unique string
     *             id, which corresponds to the layer-declaration of a
     *             Suggestion Type which has a user description; you can
     *             also query the SuggestionManager to ask if this
     *             type of Suggestion has been disabled by the user.
     * @param summary A one-line summary of the task; this is what appears
     *             in the Suggestions Window main column.
     * @param action An action to be performed when the user "runs" this
     *             task. Some actions may actually fix the problem that
     *             the suggestion is  describing, for example "update
     *             the copyright to include 2002" might edit the document
     *             when the action is run; other actions may point the
     *             user to the problem: the compiler may add tasks to
     *             fix errors and running these actions open the editor
     *             on the relevant source line. This parameter may be
     *             null. This is typically the case for suggestions where
     *             we can't safely fix the problem, and only "Go To
     *             Source" is provided.
     * @param seed Initial provider's data provided for this suggestion,
     *             if any. May be null.
     * @return A new suggestion matching the input parameters which can then
     *             be registered with the manager.
     *
     * @since 1.4
     *
     * @todo Provide specific guidelines here for how these sentences
     *       should be worded so that we get a consistent set of
     *       descriptions.
     *
     */
    abstract public Suggestion createSuggestion(String type,
                                                String summary,
                                                SuggestionPerformer action,
                                                Object seed);
    
    /**
     * Return true iff the type of suggestion indicated by the
     * id argument is enabled. By default, all suggestion types
     * are enabled, but users can disable suggestion types they're
     * not interested in.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link org.netbeans.modules.tasklist.client.Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type is enabled
     */
    abstract public boolean isEnabled(String id);

    /**
     * Return true iff the user appears to be "reading" the
     * suggestions. This means it will return false if the
     * Suggestion window is not open. This means that if a suggestion
     * is only temporarily interesting, this method lets you
     * skip creating a suggestion since if the suggestion window
     * isn't open, the user won't see it anyway, and since this
     * is a temporarily-interesting suggestion by the time the
     * window is opened the suggestion isn't relevant anymore.
     * (Yes, it's obviously possible that the suggestion window
     * is open but the user is NOT looking at it; that will be
     * fixed in tasklist version 24.0 when we have eye scanning
     * hardware and access-APIs.)
     * <p>
     *
     * @param id The String id of the Suggestion Type we're
     *    interested in. You may pass null to ask about any/all
     *    Suggestion Types. See the {@link org.netbeans.modules.tasklist.client.Suggestion} documentation
          for how Suggestion Types are registered and named.
     *
     *
     * @return True iff the suggestions are observed by the user.
     */
    abstract public boolean isObserved(String id);

    /**
     * Add and remove lists of suggestions from the suggestion
     * registry.
     * <p>
     * The tasks will remain in the list until the IDE is shut down,
     * or until the user performs the tasks, or until the tasks are explicitly
     * removed by you.
     * <p>
     * If you have multiple suggestions to register for the same type,
     * register them all at once. This is better for performance, but
     * also allows the suggestions framework to decide to group a series
     * of related suggestions together, for example.
     * <p>
     * Note: if these suggestion corresponds to a disabled suggestion type,
     * they will not be added to the list.  To avoid computing Suggestion
     * objects in the first place, check isEnabled().
     * <p>
     * Note: only suggestions created by calling {@link #createSuggestion}
     * should be registered here.
     * <p>
     * @todo Consider adding a "time-to-live" attribute here where you
     *   can indicate the persistence of the task; some suggestions should
     *   probably expire if the user doesn't act on it for 5(?) minutes,
     *   others should perhaps survive even IDE restarts.
     *
     * @param type The type of suggestions being added. May be null,
     *   in which case the suggestion manager will check each suggestion
     *   for its declared type. This allows you to register suggestions
     *   of multiple types, but means more work for the suggestion manager
     *   (so specifying a homogeneous list gives better performance.)
     * @param add List of suggestions that should be added
     * @param remove List of suggestions that should be removed. Note that
     *    the remove is performed before the add, so if a task appears
     *    in both list it will not be removed.
     * @param request Normally null. For DocumentSuggestionProviders,
     *    pass in the request object provided in rescan() here, such
     *    that results can be routed correctly even when results
     *    are provided asynchronously.
     */
    abstract public void register(String type, List add, List remove,
                                  Object request);

    /** Get the default Suggestion Manager.
     * <p>
     * @return the default instance from lookup
     */
    public static SuggestionManager getDefault() {
        return (SuggestionManager)Lookup.getDefault().
            lookup(SuggestionManager.class);
    }
}
