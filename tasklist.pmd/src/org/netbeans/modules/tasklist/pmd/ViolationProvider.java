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

package org.netbeans.modules.tasklist.pmd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleViolation;

import pmd.*;
import pmd.config.ConfigUtils;
import pmd.config.PMDOptionsSettings;


import org.netbeans.api.tasklist.*;
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;
import org.netbeans.spi.tasklist.LineSuggestionPerformer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;


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

import org.openide.cookies.SourceCookie;
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
import java.awt.Image;
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
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.Workspace;


/**
 * This class uses the PMD rule checker to provide rule violation
 * suggestions.
 *
 * @author Tor Norbye
 */


public class ViolationProvider extends DocumentSuggestionProvider {

    final private static String SUGGESTIONTYPE = "pmd-violations"; // NOI18N

    /**
     * Return the typenames of the suggestions that this provider
     * will create.
     * @return An array of string names. Should never be null. Most
     *  providers will create Suggestions of a single type, so it will
     *  be an array with one element.
     */
    public String[] getTypes() {
        return new String[] { SUGGESTIONTYPE };
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
    }

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
        if (scanning) {
            scan(document, dataobject);
        }
    }

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    protected void docShown(Document document, DataObject dataobject) {
        if ((document == null) || (dataobject == null)) {
            return;
        }
        scan(document, dataobject);
    }

    /** The actual workhorse of this class - scan a document for rule violations */
    private void scan(Document doc, DataObject dobj) {
        try {
            
        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(SUGGESTIONTYPE)) {
            return;
        }

        // Remove old contents
        if (showingTasks != null) {
            manager.remove(showingTasks);
            showingTasks.clear();
        }
        
        SourceCookie cookie =
            (SourceCookie)dobj.getCookie(SourceCookie.class);

        // The file is not a java file
        if(cookie == null) {
            return;
        }

        if (showingTasks == null) {
            int defSize = 10;
            showingTasks = new ArrayList(defSize);
        }

        String text = null;
        try {
            int len = doc.getLength();
            text = doc.getText(0, len);
        } catch (BadLocationException e) {
            TopManager.getDefault().
                getErrorManager().notify(ErrorManager.WARNING, e);
            return;
        }
        Reader reader = new StringReader(text);
        String name = cookie.getSource().getClasses()[0].getName().getFullName();
        PMD pmd = new PMD();
        RuleContext ctx = new RuleContext();
        Report report = new Report();
        ctx.setReport(report);
        ctx.setSourceCodeFilename(name);

        RuleSet set = new RuleSet();
        List rlist = ConfigUtils.createRuleList(
                             PMDOptionsSettings.getDefault().getRules());
        Iterator it = rlist.iterator();
        while(it.hasNext()) {
            set.addRule((Rule)it.next());
        }
        pmd.processFile(reader, set, ctx);
        Iterator iterator = ctx.getReport().iterator();

        Image taskIcon = Utilities.loadImage("org/netbeans/modules/tasklist/pmd/fixable.gif"); // NOI18N

        if(!ctx.getReport().isEmpty()) {
            ArrayList list = new ArrayList(ctx.getReport().size());
            while(iterator.hasNext()) {
                RuleViolation violation = (RuleViolation)iterator.next();
                try {
                    
                    if (manager.isEnabled(SUGGESTIONTYPE)) {
                        // Violation line numbers seem to be 0-based
                        final Line line = getLine(dobj, violation.getLine());

                        //System.out.println("Next violation = " + violation.getRule().getName() + " with description " + violation.getDescription() + " on line " + violation.getLine());
                        
                        boolean fixable = false;
                        SuggestionPerformer action = null;

                        String rulename = violation.getRule().getName();
                        if (rulename.equals("UnusedImports") || // NOI18N
                            rulename.equals("DuplicateImports")) { // NOI18N
                            fixable = true;
                            action = new SuggestionPerformer() {
                                    public void perform(Suggestion s) {
                                        // Remove the particular line
                                        deleteLine(line, "import ", // NOI18N
                                                   false); // whitespace etc.?
                                    }
                                    public Object getConfirmation(Suggestion s) {
                                        return null; // TODO provide a confirmation!
                                    }
                                };
                        } else if ((rulename.equals("UnusedPrivateField") || // NOI18N
                                    rulename.equals("UnusedLocalVariable")) && // NOI18N
                                   deleteLine(line, "", true)) { // only a check
                            fixable = true;
                            action = new SuggestionPerformer() {
                                    public void perform(Suggestion s) {
                                        // Remove the particular line
                                        deleteLine(line, "", false);
                                    }
                                    public Object getConfirmation(Suggestion s) {
                                        return null; // TODO provide a confirmation!
                                    }
                                };
                        } else {
                            action = new LineSuggestionPerformer();
                        }
                      
                        Suggestion s = manager.createSuggestion(
                                                    SUGGESTIONTYPE,
                                                    rulename + " : " + // NOI18N
                                                    violation.getDescription(),
                                                    action);
                        // XXX Is there a priority for each rule?
                        s.setPriority(SuggestionPriority.NORMAL);
                        s.setLine(line);
                        if (fixable) {
                            s.setIcon(taskIcon);
                        }
                        
                        showingTasks.add(s);
                    }
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(e);
                }
            }

            manager.add(showingTasks);
        }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
    }

    /** Look up the Line object for a particular file:linenumber */
    Line getLine(DataObject dataobject, int lineno) {
        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dataobject.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // XXX HACK
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno-1);
                    return l;
                }
            }
        } catch (Exception e) {
            TopManager.getDefault().getErrorManager().
                notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
        
    }
    
    /** Remove a particular line. Make sure that the line begins with
     * a given prefix, just in case.
     * @param prefix A prefix that the line to be deleted must start with
     * @param checkOnly When true, don't actually delete the line, only
     *         report whether the deletion should be attempted or not
     */
    boolean deleteLine(Line line, String prefix, boolean checkOnly) {
        DataObject dao = line.getDataObject();
        if (!dao.isValid()) {
            return false;
        }

	final EditorCookie edit = (EditorCookie)dao.getCookie(EditorCookie.class);
	if (edit == null) {
	    return false;
	}

	Document d = edit.getDocument(); // Does not block
	if (d == null) {
	    return false;
	}

        if (!(d instanceof StyledDocument)) {
            return false;
        }
            
        StyledDocument doc = (StyledDocument)d;

        Element e = doc.getParagraphElement(0).getParentElement();
        if (e == null) {
            // try default root (should work for text/plain)
            e = doc.getDefaultRootElement ();
        }
        int lineNumber = line.getLineNumber();
        Element elm = e.getElement(lineNumber);
        if (elm == null) {
            return false;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();

        try {
            String text = doc.getText(offset, endOffset-offset);
            if (!text.startsWith(prefix)) {
                return false;
            }
            if (checkOnly) {
                return isDeleteSafe(text);
            } else {
                doc.remove(offset, endOffset-offset);
            }
        } catch (BadLocationException ex) {
            TopManager.getDefault().
                getErrorManager().notify(ErrorManager.WARNING, ex);
        }
        return false;
    }

    
    /**
     * Checks designed to prevent deleting additional content on the
     * line - for example, it won't delete anything if it detects
     * multiple statements on the line, or function calls. It may err
     * on the safe side; e.g. not delete even when it would be safe to do so.
     */
    private boolean isDeleteSafe(String text) {
        // Does this line contain multiple statements?
        // I consider that to be the case when there is at least one
        //   - comma, or
        //   - semicolon
        // and the next nonspace character is not "/"
        // TODO - fix it such that the following doesn't trip us up:
        //   int y = "a,b";

        /*
          What about a weird corner case like this:
          int z = 0;
          for (int y = 0;
          z < 5;
          z++) {
          importantCall();
          }
          Will I delete the "for(int y = 0" line since y is unused?
        */

        // A small statemachine to figure out if the line can
        // be "safely" deleted
        int n = text.length();
        boolean inString = false;
        boolean escaped = false;

        //  What do we initialize comment too? It's POSSIBLE that you
        //  have code like this
        //  /* Begin comment:
        //     end */    int unused = 5;
        //  ...and I begin in the middle of a comment. But I think this
        // is an unusual scenario...
        boolean comment = false;
                
        boolean seenSemi = false;
        boolean seenComma = false;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            if (comment) {
                if ((c == '*') && (i < (n-1)) &&
                    ((text.charAt(i+1) == '/'))) {
                    comment = false;
                } else {
                    continue;
                }
            } else if (c == '\\') {
                escaped = !escaped;
            } else if (c == '"') {
                if (!escaped) {
                    inString = !inString;
                }
            } else if ((c == '/') && (i < (n-1)) &&
                       ((text.charAt(i+1) == '*'))) {
                comment = true;
            } else if (c == '(') {
                if (!inString && !escaped) {
                    // BAIL! "(" on a line makes me nervous, e.g. unused
                    // variable "success" in
                    //   boolean success = saveData();
                    //System.out.println("BAILING: function call on the line!");
                    return false;
                }
            } else if (c == ',') {
                if (!inString && !escaped) {
                    seenComma = true;
                }
            } else if (c == ';') {
                if (!inString && !escaped) {
                    seenSemi = true;
                }
            } else if (Character.isWhitespace(c)) {
                // do nothing
            } else {
                // Some other character
                if (!inString && !escaped && (seenSemi || seenComma)) {
                    // BAIL -- we've seen text after a semicolon or
                    // comma - multiple statements on the line!
                    //System.out.println("BAILING: character after semi=" + seenSemi + " or comma=" + seenComma);
                    return false;
                }
            }
        }
        return true;
    }
    

    /** 
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     */
    protected void docHidden(Document document, DataObject dataobject) {
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
