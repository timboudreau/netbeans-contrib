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

import javax.swing.text.Document;
import javax.swing.event.DocumentEvent;

import javax.swing.text.*;
import javax.swing.event.*;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import java.io.Externalizable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import org.openide.awt.StatusDisplayer;
import org.openide.TopManager;
import org.openide.text.EditorSupport;
import org.openide.text.NbDocument;
import org.openide.cookies.EditorCookie;
import org.openide.windows.WindowManager;
import org.openide.windows.TopComponent;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerPanel;
import org.openide.explorer.view.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.WeakListener;




import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JLabel;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.Workspace;


/**
 * This class scans the given document for source tasks and
 * copyright errors.
 *
 * @todo Consider showing only a few of the scanned tasks (to prevent a file
 *     with lots of todos from hiding say the syntax errors). It might be
 *     useful to show the N tasks closest to the cursor position!
 * @todo Process copyrights and source tasks separately
 *
 * @author Tor Norbye
 */


public class SourceTaskProvider extends DocumentSuggestionProvider {

    final private static String COMMENTTYPE = "nb-tasklist-scannedtask"; // NOI18N
    final private static String COPYRIGHTTYPE = "nb-tasklist-copyright"; // NOI18N
    
    /**
     * Return the typenames of the suggestions that this provider
     * will create.
     * @return An array of string names. Should never be null. Most
     *  providers will create Suggestions of a single type, so it will
     *  be an array with one element.
     */
    public String[] getTypes() {
        return new String[] { COMMENTTYPE, COPYRIGHTTYPE };
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
            scan(document, dataobject);
	}
    }

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    protected void docShown(Document document, DataObject dataobject) {
        //System.out.println("docShown(" + document + ")");
	Settings settings = (Settings)Settings.
	    findObject(Settings.class, true);
	boolean skipCode = settings.getSkipComments();
        tasklist = new TaskList();
	scanner = new SourceScanner(tasklist, skipCode);
	//scanner.start(false);

        scan(document, dataobject);
    }

    private void scan(Document doc, DataObject dobj) {
        SuggestionManager manager = SuggestionManager.getDefault();
        
        if (!manager.isEnabled(COMMENTTYPE) &&
            !manager.isEnabled(COPYRIGHTTYPE)) {
            return;
        }

        // Don't update old tasks - just generate new ones!
        tasklist.clear();

        // Remove old contents
        if (showingTasks != null) {
            manager.remove(showingTasks);
            showingTasks.clear();
        } else {
            int defSize = 10;
            showingTasks = new ArrayList(defSize);
        }

        if (manager.isEnabled(COMMENTTYPE)) {
            scanner.scan(doc, dobj, false, false);

            SuggestionPerformer action = new LineSuggestionPerformer();
            ListIterator it = tasklist.getTasks().listIterator();
            while (it.hasNext()) {
                DocTask subtask = (DocTask)it.next();
                String summary = subtask.getSummary();
                Suggestion s = manager.createSuggestion(COMMENTTYPE,
                                                        summary,
                                                        action);
                s.setLine(subtask.getLine());
                s.setPriority(SuggestionPriority.NORMAL);
                showingTasks.add(s);
            }
            manager.add(showingTasks);
        }
        
        
        if (manager.isEnabled(COPYRIGHTTYPE)) {
            DocTask copyright = scanner.checkCopyright(doc, dobj);
            if (copyright != null) {
                String summary = copyright.getSummary();
                SuggestionPerformer action = copyright.getAction();
                Suggestion s = manager.createSuggestion(COPYRIGHTTYPE,
                                                        summary,
                                                        action);
                s.setLine(copyright.getLine());
                s.setPriority(SuggestionPriority.NORMAL);
                showingTasks.add(s);
                manager.add(s);
            }
        }
        
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
