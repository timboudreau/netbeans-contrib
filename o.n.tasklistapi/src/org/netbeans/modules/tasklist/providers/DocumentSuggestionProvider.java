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
