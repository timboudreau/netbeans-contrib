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


public class ViolationProvider extends DocumentSuggestionProvider {

    final private static String SUGGESTIONTYPE = "pmd-violations"; // NOI18N

public ViolationProvider() {
ErrorManager.getDefault().log(ErrorManager.USER, "Here in ViolationProvider constructor!");
}
    
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
        //System.out.println("notifyRun");
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
        //System.out.println("notifyStop");
        scanning = false;

        // Nothing to do here -- docHidden takes care of everything
    }

    
    
    /** The given document has been opened
     * <p>
     * @param document The document being opened
     */
    protected void docOpened(Document document, DataObject dataobject) {
        //System.out.println("docOpened(" + document + ")");

        // Nothing to do here -- docShown will be called soon and
        // we'll parse it there
    }

    /**
     * The given document has been edited right now. <b>Don't</b>
     * do heavy processing here, since this is invoked immediately
     * as the user is typing. Use this method to invalidate pending
     * document editing actions. Use {@link #docEditedStable} to
     * start rescanning a document, since that method is called after
     * a time interval after the last edit.
     * <p>
     * @param document The document being edited
     */
    protected void docEdited(Document document, DocumentEvent event,
                             DataObject dataobject) {
        //System.out.println("docEdited(" + document + ")");
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
        //System.out.println("docEditedStable(" + document + ")");
        if (scanning) {
            scan(document, dataobject);
        }
    }

    
    // XXX  Do I need separate changedUpdate, insertUpdate, removeUpdate
    // methods, or is edited good enough?

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    protected void docShown(Document document, DataObject dataobject) {
        //System.out.println("docShown(" + document + ")");
        scan(document, dataobject);
    }

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
        List rlist = ConfigUtils.createRuleList(PMDOptionsSettings.getDefault().getRules());
        Iterator it = rlist.iterator();
        while(it.hasNext()) {
            set.addRule((Rule)it.next());
        }
        /* Old way
        RuleSet set = null;
        try {
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            set = ruleSetFactory.createRuleSet(
                  PMDOptionsSettings.getDefault().getRulesets());
        } catch(RuleSetNotFoundException e) {
            e.printStackTrace();
            TopManager.getDefault().getErrorManager().notify(e);
        }
        */
        pmd.processFile(reader, set, ctx);
        Iterator iterator = ctx.getReport().iterator();

        Image taskIcon = Utilities.loadImage("org/netbeans/modules/tasklist/pmd/fixable.gif"); // NOI18N

        if(!ctx.getReport().isEmpty()) {
            ArrayList list = new ArrayList(ctx.getReport().size());
            while(iterator.hasNext()) {
                RuleViolation violation = (RuleViolation)iterator.next();
                // BEGIN Suggestion modifications
                try {
                    
                    if (manager.isEnabled(SUGGESTIONTYPE) /* && manager.isObserved(SUGGESTIONTYPE) */
                        ) {
                        // Violation line numbers seem to be 0-based
                        final Line line = getLine(dobj, violation.getLine());

                        //System.out.println("Next violation = " + violation.getRule().getName() + " with description " + violation.getDescription() + " on line " + violation.getLine());
                        boolean fixable = false;
                        SuggestionPerformer action = null;
                        // Allow lines to be deleted - automatically. Note, this has to use Line objects such that it works correctly.

                        
                        if (violation.getRule().getName().equals("UnusedImports") ||
                            violation.getRule().getName().equals("DuplicateImports")) {
                            fixable = true;
                            action = new SuggestionPerformer() {
                                    public void perform(Suggestion s) {
                                        // Remove the particular line
                                        deleteLine(line, "import "); // whitespace etc.?
                                    }
                                    public Object getConfirmation(Suggestion s) {
                                        return null; // TODO provide a confirmation!
                                    }
                                };
                        } else if (violation.getRule().getName().equals("UnusedPrivateField") ||
                            violation.getRule().getName().equals("UnusedLocalVariable")) {
                            // XXX Dangerous - what if there are
                            // multiple unused private fields on the
                            // same line?
                            fixable = true;
                            action = new SuggestionPerformer() {
                                    public void perform(Suggestion s) {
                                        // Remove the particular line
                                        deleteLine(line, "");
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
                                                    violation.getRule().getName() + " : " + violation.getDescription(),
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
     */
    void deleteLine(Line line, String prefix) {
        // TODO - check the prefix
        
        DataObject dao = line.getDataObject();
        //System.out.println("deleteLine(" + dao + ", " + line.getLineNumber());
        
        if (!dao.isValid()) {
            System.out.println("DATAOBJECT " + dao + " NOT VALID!");
            return;
        }

	final EditorCookie edit = (EditorCookie)dao.getCookie(EditorCookie.class);
	if (edit == null) {
	    System.out.println("No editor cookie - not doing anything");
	    return;
	}

	Document d = edit.getDocument(); // Does not block
	if (d == null) {
	    System.out.println("No document handle...");
	    return;
	}

        if (!(d instanceof StyledDocument)) {
	    System.out.println("d is not a StyledDocument! d =" + d.getClass().getName());
            return;
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
            System.out.println("NO such line (" + lineNumber + ")");
            return;
        }
        int offset = elm.getStartOffset();
        int endOffset = elm.getEndOffset();
        //System.out.println("Offset for line " + lineNumber + " is " + offset + " and endOffset = " + endOffset);

        try {
            String text = doc.getText(offset, endOffset-offset);
            //System.out.println("The text is '" + text + "'");
            if (!text.startsWith(prefix)) {
                System.out.println("WRONG PREFIX!");
                return;
            }
        
            doc.remove(offset, endOffset-offset);
        } catch (BadLocationException ex) {
            TopManager.getDefault().
                getErrorManager().notify(ErrorManager.WARNING, ex);
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

	// Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.remove(showingTasks);
	    showingTasks = null;
	}     
    }

    /**
     * The given document has been closed; stop reporting suggestions
     * for this document and free up associated resources.
     * <p>
     * @param document The document being closed
     */
    protected void docClosed(Document document, DataObject dataobject) {
        //System.out.println("docClosed(" + document + ")");
    }

    // TODO make sure we get rid of the various component listeners!

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;
}
