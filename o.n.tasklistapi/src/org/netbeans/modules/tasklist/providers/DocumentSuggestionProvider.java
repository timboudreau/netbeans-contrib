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

import java.util.List;
import javax.swing.text.Document;
import org.openide.loaders.DataObject;

/**
 * This class is used for SuggestionProviders which operate on documents.
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
 *
 * @author Tor Norbye
 * @author Petr Kuzel, SuggestionContext refactoring
 * @since 1.3  (well all signatures changed in this version)
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
     */
    abstract public List scan(SuggestionContext env);


    /**
     * Rescan the given document for suggestions, and register
     * these with the SuggestionManager. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example when the document is
     * saved.
     * <p>
     * NOTE: This method should also remove items previously scanned
     * by this method (in other words, "update" the list). That typically
     * means calling {@link clear}. This is not automatically done
     * by the framework because this rescan method can be clever and
     * simply update a few suggestions and leave most of them alone,
     * instead of removing and readding a large number of suggestions
     * when only one or two things have changed.
     * <p>
     * @param env The environment being scanned
     * @param request A reference for this request. You <strong>must</strong>
     *   provide this reference to the SuggestionManager.register() call
     *   when returning the results!
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    abstract public void rescan(SuggestionContext env, Object request);

    /**
      * Remove items added by {@link #rescan}.
     * <p>
     * @param env The environment being previously scanned
     * @param request A reference for this request. You <strong>must</strong>
     *   provide this reference to the SuggestionManager.register() call
     *   when clearing out suggestions.
      */
    abstract public void clear(SuggestionContext env,
                               Object request);


    /** The given document has been opened
     * <p>
     * @param document The document being opened
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
/* I'm not yet generating this event - perhaps I shouldn't show it
   here either.
    public void docOpened(Document document, DataObject dataobject) {
    }
*/

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param env The environment being shown
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docShown(SuggestionContext env) {
    }

    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param env The environment being hidden
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docHidden(SuggestionContext env) {
    }

    /**
     * The given document has been closed; stop reporting suggestions
     * for this document and free up associated resources.
     * <p>
     * @param document The document being closed
     * @param dataobject The Data Object for the file being closed
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
/* I'm not yet generating this event - perhaps I shouldn't show it
   here either.
    public void docClosed(Document document, DataObject dataobject) {
    }
*/
}
