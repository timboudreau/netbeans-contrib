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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.api.tasklist.*;
import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.*;

import java.beans.*;
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
 * @author Tor Norbye
 */


public class SourceTaskProvider extends DocumentSuggestionProvider
    implements PropertyChangeListener {

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

    private DataObject dataobject = null;
    private Document document = null;
    private Object request = null;

    private void rescan() {
        rescan(document, dataobject, request);
    }
    
    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    public void docShown(Document document, DataObject dataobject) {
        Settings settings = (Settings)Settings.findObject(Settings.class, true);
        settings.addPropertyChangeListener(this);
        skipCode = settings.getSkipComments();
    }

    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     */
    public void docHidden(Document document, DataObject dataobject) {
        Settings settings = (Settings)Settings.findObject(Settings.class, true);
        settings.removePropertyChangeListener(this);

        //System.out.println("docHidden(" + document + ")");
	if (scanner != null) {
	    scanner = null;
        }        
     }

    private boolean skipCode = ((Settings)Settings.
                             findObject(Settings.class, true)).getSkipComments();

    public void propertyChange(PropertyChangeEvent ev) {
        if (Settings.PROP_SCAN_TAGS == ev.getPropertyName()) {
            if (scanner != null) {
                scanner.tokensChanged();
            }
            rescan();
        }
    }

    public void rescan(Document doc, DataObject dobj, Object request) {
        dataobject = dobj;
        document = doc;
        this.request = request;
        List newTasks = scan(doc, dobj);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks, request);
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
            s.setPriority(subtask.getPriority());
            if (tasks == null) {
                tasks = new ArrayList(tasklist.getTasks().size());
            }
            tasks.add(s);
        }
        
        return tasks;
    }
    
    
    public void clear(Document document, DataObject dataobject,
                      Object request) {
	// Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks, request);
	    showingTasks = null;
	}     
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
}
