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
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;
import org.netbeans.spi.tasklist.LineSuggestionPerformer;
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
    
    private boolean scanning = false;
    
    /**
     * Start creating suggestions when you think of them.
     * (This is typically called when the Suggestions window is shown,
     * for example because the Suggestions window tab is moved to the front,
     * or the user has moved to a workspace containing a Suggestions Window.)
     */
    protected void notifyRun() {
        super.notifyRun();
        scanning = true;
    }

    /**
     * (Temporarily) stop creating suggestions.
     * (This is typically called when the Suggestions window is hidden,
     * for example because a different tab is moved to the front or because
     * the user has moved to another workspace.)
     */
    protected void notifyStop() {
        super.notifyStop();
        scanning = false;

        // Nothing to do here -- docHidden takes care of everything
    }

    
    /** Scanner which reads through the current document and locates tasks */
    private SourceScanner scanner = null;

    /** List updated by the Source Scanner */
    private TaskList tasklist = null;

    
    /**
     * The given document has been edited, and a time interval (by default
     * around 2 seconds I think) has passed without any further edits.
     * Update your Suggestions as necessary. This may mean removing
     * previously registered Suggestions, or editing existing ones,
     * or adding new ones, depending on the current contents of the
     * document.
     * <p>
     * @param document The document being edited
     */
    protected void docEditedStable(Document document, DocumentEvent event,
                                   DataObject dataobject) {
        //System.out.println("docEditedStable(" + document + ")");
	if (scanning && scanner != null) {
            update(document, dataobject);
	}
    }

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    protected void docShown(Document document, DataObject dataobject) {
        //System.out.println("docShown(" + document + ")");
        skipCode = ((Settings)Settings.
                             findObject(Settings.class, true)).getSkipComments();
        update(document, dataobject);
    }

    private boolean skipCode = ((Settings)Settings.
                             findObject(Settings.class, true)).getSkipComments();

    
    /** Update the manager with the current document contents */
    private void update(Document doc, DataObject dobj) {
        List newTasks = scan(doc, dobj);
        SuggestionManager manager = SuggestionManager.getDefault();

        // Remove old contents
        if (showingTasks != null) {
            manager.remove(showingTasks);
        }
        
        showingTasks = newTasks;
        if (showingTasks != null) {
            manager.add(showingTasks);
        }      
    }
    
    protected List scan(Document doc, DataObject dobj) {
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
        
        SuggestionPerformer action = new LineSuggestionPerformer();
        ListIterator it = tasklist.getTasks().listIterator();
        List tasks = null;
        while (it.hasNext()) {
            DocTask subtask = (DocTask)it.next();
            String summary = subtask.getSummary();
            Suggestion s = manager.createSuggestion(TYPE,
                summary,
                action);
            s.setLine(subtask.getLine());
            s.setPriority(SuggestionPriority.NORMAL);
            if (tasks == null) {
                tasks = new ArrayList(tasklist.getTasks().size()+1); // room for copyright
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
    protected void docHidden(Document document, DataObject dataobject) {
        //System.out.println("docHidden(" + document + ")");
	if (scanner != null) {
	    scanner.stop();
	    scanner = null;
        }        

	// Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.remove(showingTasks);
	    showingTasks = null;
	}     
    }

    protected void docClosed(Document document, DataObject dataobject) {
    }

    protected void docOpened(Document document, DataObject dataobject) {
    }

    protected void docEdited(Document document, DocumentEvent event,
                             DataObject dataobject) {
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
}
