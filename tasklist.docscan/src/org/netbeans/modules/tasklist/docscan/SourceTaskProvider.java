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

import java.beans.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.IOException;

import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;

// I was tempted to use BaseDocument here, since it has various
// advantages such as utilities for computing line numbers, access
// to the contents as an array, etc.
// However, not all documents opened are BaseDocuments - in particular,
// when using the XEmacs integration or the Vim integration, we have
// Documents, not BaseDocuments (a subclass of Document), so I'll
// stick with direct Document manipulation here.
//import org.netbeans.editor.BaseDocument;


/**
 * This class scans the given document for source tasks. It does
 * not provide any fix action.
 *
 * @todo Move the regexp code into tasktags.
 * @todo I should use Line objects for the tasks - especially the
 *       copyright ones - and get rid of getLineContext() and replace
 *       it with Line.getText()
 * @todo If you have multiple hits on the same line, don't create a new
 *       task!
 * @todo PERFORMANCE OPTIMIZE THIS THING!
 * @todo Should I use FileObjects instead of DataObjects when passing
 *       file identity around? It seems weird that I don't allow
 *       scanning on secondary files (although it seems right in the
 *       cases I can think of - we don't want to scan .class files,
 *       .o files, .form files, ...)
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class SourceTaskProvider extends DocumentSuggestionProvider
    implements PropertyChangeListener {

    final static String TYPE = "nb-tasklist-scannedtask"; // NOI18N

    private SuggestionContext env;

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

    public void notifyFinish() {
        Cache.store();
    }

    public void notifyPrepare() {
        Cache.load();
    }

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     */
    public void docShown(SuggestionContext env) {
        Settings settings = (Settings)Settings.findObject(Settings.class, true);
        settings.addPropertyChangeListener(this);
    }

    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     */
    public void docHidden(SuggestionContext env) {
        Settings settings = (Settings)Settings.findObject(Settings.class, true);
        settings.removePropertyChangeListener(this);
     }

    public void propertyChange(PropertyChangeEvent ev) {
        if (Settings.PROP_SCAN_TAGS == ev.getPropertyName()) {
            tokensChanged();
        }
        rescan();
    }

    public void rescan(SuggestionContext env, Object request) {
        this.env = env;
        this.request = request;
        List newTasks = scan(env);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks, request);
        showingTasks = newTasks;
    }
    
    public List scan(final SuggestionContext env) {

        SuggestionManager manager = SuggestionManager.getDefault();
        if (!manager.isEnabled(TYPE)) {
            return null;
        }

        // Would it be better to move caching one level higher
        // to suggestions framework? Such simple cache may be,
        // more advanced caching probably not.
        try {
            if (DataObject.find(env.getFileObject()).isModified() == false) {
                List cached = Cache.get(env);
                if (cached != null) return cached;
            }
        } catch (DataObjectNotFoundException e) {
            // ignore cache
        }

        // Initialize the regular expression, if necessary
        // XXX Move to parent
        Settings settings =
            (Settings)Settings.findObject(Settings.class, true);
        if (tags == null) {
            tags = settings.getTaskTags();
            regexp = tags.getScanRegexp();
        }
        boolean skipCode = settings.getSkipComments();
        List tasks = null;
        if (skipCode) {
            tasks = scanCommentsOnly(env);
        } else {
            tasks = scanAll(env);
        }
        Cache.put(env, tasks);
        return tasks;
    }
    
    
    public void clear(SuggestionContext env,
                      Object request) {
	// Remove existing items
        if (showingTasks != null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            manager.register(TYPE, null, showingTasks, request);
	    showingTasks = null;
	}     
    }


    private void tokensChanged() {
	// XXX Probably should synchronize here -- especially when I get
	// the scanning code into a separate thread running in the background
        tags = null;
	regexp = null;
    }

    /**
     * Given the contents of a buffer, scan it for todo items.
     * @param doc The document to scan
     * @param dobj The data object whose primary file should be scanned
     */
    private List scanCommentsOnly(SuggestionContext env) {
        ArrayList newTasks = new ArrayList();
        SourceCodeCommentParser sccp = null;
        boolean washComment = true;

        String suffix = env.getFileObject().getExt();
            
        // @todo These parameters should be configured somewhere.
        //       Since I don't know how I am going to store the data, I 
        //       support only a set of hardcoded rules... After all, 
        //       I promised only to support a given set of filetypes ;)
        if (suffix.equalsIgnoreCase("java") || // NOI18N
            suffix.equalsIgnoreCase("c") ||  // NOI18N
            suffix.equalsIgnoreCase("cpp")) {  // NOI18N
                // I know that '//' require the C-99 standard, but I think
                // the compiler should sort that out....
            sccp = new SourceCodeCommentParser("//", "/*", "*/");
        } else if (suffix.equalsIgnoreCase("html") ||  // NOI18N
                   suffix.equalsIgnoreCase("htm") ||  // NOI18N
                   suffix.equalsIgnoreCase("xml")) {  // NOI18N
            sccp = new SourceCodeCommentParser("<!--", "-->");
        } else if (suffix.equalsIgnoreCase("jsp")) {  // NOI18N
            sccp = new SourceCodeCommentParser("<%--", "--%>");
        } else if (suffix.equalsIgnoreCase("sh")) { // NOI18N
            sccp = new SourceCodeCommentParser("#"); // NOI18N
        } else {
            sccp = new SourceCodeCommentParser();
        }

        sccp.setDocument(env);
        SourceCodeCommentParser.CommentLine cl =
            new SourceCodeCommentParser.CommentLine();
        
        TaskTag matchTag = null;
        try {
            while (sccp.getNextLine(cl)) {
                // I am inside a comment, scan for todo-items:
                Matcher matcher = regexp.matcher(cl.line);
                if (matcher.find()) {
                    String description = cl.line.trim();
                    
                    matchTag = getTag(cl.line, matcher.start(), matcher.end());

                    // [trond] I personally would like to strip off
                    // non-text characters in front of the task
                    // description, but it seemd that Tor disagreed
                    // there... I'll keep the code here until someone
                    // complains.....
                    // [tor] No, I don't disagree with stripping off
                    // non-text characters; I disagreed with stripping
                    // off all the line contents in front of the
                    // matched token, since I often put the TODO token
                    // at the end of a line and I'd like to see the
                    // relevant comment or code as part of the task
                    if (washComment) {
                        int idx = 0;
                        int stop = matcher.start();

                        while (idx < stop) {
                            char c = description.charAt(idx);
                            if (c == '@' || Character.isLetter(c)) {// NOI18N
                                break;
                            } else {
                                ++idx;
                            }
                        }
                    
                        if (idx != 0) {
                            description = description.substring(idx);
                        }
                    }
                    
                    SuggestionManager manager = SuggestionManager.getDefault();
                    Suggestion item =
                        manager.createSuggestion(SourceTaskProvider.TYPE,
                                                description,
                                                null,
                                                this);
                    try {
                        DataObject dataObject = DataObject.find(env.getFileObject());
                        item.setLine(TLUtils.getLineByNumber(dataObject, cl.lineno));
                    } catch (IOException ex) {

                    }

                    if (matchTag != null) {
                        item.setPriority(matchTag.getPriority());
                    }

                    newTasks.add(item);
                }
            }
        } catch (Exception e) {
            ErrorManager.getDefault().notify(e);
        }
        return newTasks;
    }
    
    /**
     * Given the contents of a buffer, scan it for todo items.
     */
    private List scanAll(SuggestionContext env) {
        ArrayList newTasks = new ArrayList();
 
      	CharSequence text = env.getCharSequence();

        TaskTag matchTag = null;
        try {
            int index = 0;
            int lineno = 1;
            int len = text.length();

            Matcher matcher = regexp.matcher(text);
            while (index < len && matcher.find(index)) {
                int begin = matcher.start();
	            int end   = matcher.end();
                matchTag = getTag(text, begin, end);

                // begin should be the beginning of this line (but avoid 
                // clash if I have two tokens on the same line...
                char c = 'a'; // NOI18N
                int nonwhite = begin;
                while (begin >= index && (c = text.charAt(begin)) != '\n') { // NOI18N
                    if (c != ' ' && c != '\t') { // NOI18N
                        nonwhite = begin;
                    }
                    --begin;
                }
                
                begin = nonwhite;
                
                // end should be the last "nonwhite" character on this line...
                nonwhite = end;
                while (end < len) {
                    c = text.charAt(end);
                    if (c == '\n' || c == '\r') {// NOI18N
                        break;
                    } else if (c != ' ' && c != '\t') {// NOI18N
                            nonwhite = end;
                    }
                    ++end;
                }

                // calculate current line number
                int idx = index;
                while (idx <= begin) {
                    if (text.charAt(idx) == '\n') {// NOI18N
                        ++lineno;
                    }
                    ++idx;
                }
                
                index = end;
                
                String description = text.subSequence(begin, nonwhite+1).toString();

                SuggestionManager manager = SuggestionManager.getDefault();
                Suggestion item =
                    manager.createSuggestion(SourceTaskProvider.TYPE,
                                             description,
                                             null,
                                             this);
                DataObject dataObject = DataObject.find(env.getFileObject());
                item.setLine(TLUtils.getLineByNumber(dataObject, lineno));
                if (matchTag != null) {
                    item.setPriority(matchTag.getPriority());
                }

                newTasks.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newTasks;
    }
    
    private TaskTag getTag(CharSequence text, int start, int end) {
        TaskTag tag = tags.getTag(text, start, end);
        return tag;
    }    

    private void rescan() {
        rescan(env, request);
    }

    /** The list of tasks we're currently showing in the tasklist */
    private List showingTasks = null;

    private Object request = null;

    /** Regular expression used for matching tasks in the todolist */
    private Pattern regexp = null;

    /** Set of tags used for scanning. Equivalent to the regexp above. */
    private TaskTags tags = null;
}
