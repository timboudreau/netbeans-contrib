/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.api.tasklist.*;
import org.netbeans.api.tasklist.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.*;


import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import org.openide.explorer.view.*;
import org.openide.nodes.*;




import org.openide.loaders.DataObject;


/**
 * This class scans the given document for source tasks and
 * copyright errors.
 *
 * @todo Consider showing only a few of the scanned tasks (to prevent a file
 *     with lots of todos from hiding say the syntax errors). It might be
 *     useful to show the N tasks closest to the cursor position!
 *
 * @author Tor Norbye
 */


public class SourceTaskProvider extends DocumentSuggestionProvider {

    final private static String TYPE = "nb-tasklist-scannedtask"; // NOI18N
    
    /**
     * Return the typenames of the suggestions that this provider
     * will create.
     * @return An array of string names. Should never be null. Most
     *  providers will create Suggestions of a single type, so it will
     *  be an array with one element.
     */
    public String[] getTypes() {
        return new String[] { TYPE };
    }
    
    /** Scanner which reads through the current document and locates tasks */
    private SourceScanner scanner = null;

    /** List updated by the Source Scanner */
    private TaskList tasklist = null;

    
    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    public void docShown(Document document, DataObject dataobject) {
        skipCode = ((Settings)Settings.
                             findObject(Settings.class, true)).getSkipComments();
    }

    private boolean skipCode = ((Settings)Settings.
                             findObject(Settings.class, true)).getSkipComments();

    
    /**
     * Rescan the given document for suggestions. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example when the document is
     * saved.
     * <p>
     * This method should register the suggestions with the
     * suggestion manager.
     * <p>
     * @param doc The document being scanned
     * @param dobj The Data Object for the file being scanned
     * @return list of tasks that result from the scan. May be null.
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void rescan(Document doc, DataObject dobj) {
        List newTasks = scan(doc, dobj);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks);
        showingTasks = newTasks;
    }
    
    public List scan(Document doc, DataObject dobj) {
        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }

        // Don't update old tasks - just generate new ones!
        if (tasklist == null) {
            tasklist = new TaskList();
        } else {
            tasklist.clear();
        }
        if (scanner == null) {
            scanner = new SourceScanner(tasklist, skipCode);
        }

        scanner.scan(doc, dobj, false, false);
        
        ListIterator it = tasklist.getTasks().listIterator();
        List tasks = null;
        while (it.hasNext()) {
            DocTask subtask = (DocTask)it.next();
            String summary = subtask.getSummary();
            Suggestion s = manager.createSuggestion(TYPE,
                summary,
                null,
                this);
            s.setLine(subtask.getLine());
            if (tasks == null) {
                tasks = new ArrayList(tasklist.getTasks().size());
            }
            tasks.add(s);
        }
        
        return tasks;
    }
    
    
    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     */
    public void docHidden(Document document, DataObject dataobject) {
        //System.out.println("docHidden(" + document + ")");
	if (scanner != null) {
	    scanner.stop();
	    scanner = null;
        }        
     }

     public void clear(Document document, DataObject dataobject) {
	// Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks);
	    showingTasks = null;
	}     
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
}
