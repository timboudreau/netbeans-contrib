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

package org.netbeans.modules.tasklist.providers;

/**
 * A suggestion provider provides Suggestions to the SuggestionManager.
 *
 * Classes which exist exclusively to produce suggestions are most likely
 * SuggestionProviders rather than simple SuggestionManager clients.
 * (If you're computing something and have a result that may be useful
 * as a Suggestion, you don't need to make this into a SuggestionProvider;
 * simply look up the SuggestionManager and register the Suggestion.)
 * <p>
 * The API does not define which thread these methods are called on,
 * so don't make any assumptions. If you want to post something on
 * the AWT event dispatching thread for example use SwingUtilities.
 * <p>
 * @todo Document threading behavior
 * @todo Document timer behavior (some of the methods are called after
 *   a delay, others are called immediately.)
 * @todo Add a reference to how SuggestionProviders are registered here?
 *
 * @author Tor Norbye
 */


abstract public class SuggestionProvider {

    /**
     * Return the typename of the suggestions that this provider
     * will create. TODO It's still in question if more providers
     * can create same suggestion types without
     * introducing duplications.
     *
     * @return typename, must not be be <code>null</code>.
     */
    abstract public String getType();
    
    /**
     * Prepare to start creating suggestions. Do "heavy" computations
     * related to starting creating suggestions here, such as creating
     * database connections, constructing large objects etc, depending
     * on what you need to create suggestions obviously.
     * <p>
     * Note - don't start creating suggestions until you get called
     * with notifyRun().
     * <p>
     * This method is called internally by the toolkit and should not be 
     * called directly by programs.
     * <p>
     * (This is typically called when the Suggestions window is opened.
     * It may not be showing yet, but we want to do heavy-duty preparations
     * here since it's not good to do that every time the window is
     * shown/hidden.)
     */
    public void notifyPrepare() {
    }

    /**
     * Finish creating suggestions. You may free up associated resources.
     * (This is typically called when the Suggestions window is closed.
     * Now we now that we don't need to create Suggestions for a while, so
     * it's a good time to free up resources.)
     * <p>
     * This method is called internally by the toolkit and should not be 
     * called directly by programs.
     */
    public void notifyFinish() {
    }

    /**
     * Start creating suggestions when you think of them.
     * (This is typically called when the Suggestions window is shown,
     * for example because the Suggestions window tab is moved to the front,
     * or the user has moved to a workspace containing a Suggestions Window.)
     * <p>
     * This method is called internally by the toolkit and should not be 
     * called directly by programs.
     */
    public void notifyRun() {
    }

    /**
     * (Temporarily) stop creating suggestions.
     * (This is typically called when the Suggestions window is hidden,
     * for example because a different tab is moved to the front or because
     * the user has moved to another workspace.)
     * <p>
     * This method is called internally by the toolkit and should not be 
     * called directly by programs.
     */
    public void notifyStop() {
    }

}
