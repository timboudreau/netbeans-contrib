/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.beans.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.regex.*;
import java.io.IOException;

import org.openide.ErrorManager;
import org.openide.text.Line;
import org.openide.util.WeakSet;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.netbeans.modules.tasklist.core.*;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionAgent;
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
 * @todo If you have multiple hits on the same line, don't create a new
 *       task!
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public final class SourceTaskProvider extends DocumentSuggestionProvider
    implements PropertyChangeListener {

    final static String TYPE = "nb-tasklist-scannedtask"; // NOI18N

    /** The Set<SuggetionAgent> of recent task agents. Agent is identified by line. */
    private WeakSet agents = new WeakSet();

    // context being scanned for recent request
    private SuggestionContext env;

    private Settings settings;

    /**
     * Return the typenames of the suggestions that this provider
     * will create.
     * @return An array of string names. Should never be null. Most
     *  providers will create Suggestions of a single type, so it will
     *  be an array with one element.
     */
    public String getType() {
        return TYPE;
    }

    public void notifyFinish() {
        Cache.store();
        settings = null;
    }

    public void notifyPrepare() {
        // Cache.load(); too slow call it here
        settings = (Settings)Settings.findObject(Settings.class, true);
    }

    public void notifyRun() {
        settings().addPropertyChangeListener(this);
    }

    public void notifyStop() {
        settings().removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent ev) {
        // is comes asynchronously from settings
        // if everything goes well it rescan suggestions
        // for recently opened document
        if (Settings.PROP_SCAN_TAGS.equals(ev.getPropertyName())
        ||  Settings.PROP_SCAN_SKIP.equals(ev.getPropertyName())) {
            if (env == null) return;
            // TODO propagate to consumers ()
        }
    }

    // Q: why it this one requestless?
    // A; because it synchronously returns results
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

        boolean skipCode = settings().getSkipComments();
        List tasks;
    
        if (skipCode) {
            tasks = scanCommentsOnly(env);
        } else {
            tasks = scanAll(env);
        }
        Cache.put(env, tasks);

        return tasks;
    }
    
    
    /**
     * Given the contents of a buffer, scan it for todo items. Ignore
     * all items found outside comment sections...
     * @param doc The document to scan
     * @param dobj The data object whose primary file should be scanned
     */
    private List scanCommentsOnly(SuggestionContext env) {
        ArrayList newTasks = new ArrayList();
        SourceCodeCommentParser sccp;
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

        CharSequence text = env.getCharSequence();
        sccp.setDocument(env);

        SourceCodeCommentParser.CommentRegion reg =
            new SourceCodeCommentParser.CommentRegion();
        
        TaskTag matchTag = null;

        try {
            Matcher matcher = settings.getTaskTags().getScanRegexp().matcher(text);
            int len = text.length();
            int lineno = 1;
            int index = 0;

            // find the first comment region
            if (!sccp.nextRegion(reg)) {
                // Done searching the document... bail out..
                return newTasks;
            }

            while (index < len && matcher.find(index)) {
                int begin = matcher.start();
                int end   = matcher.end();
                boolean toosoon = false;
                boolean goahead;

                do {
                    goahead = true;

                    // A match within the source comment?                   
                    if (begin < reg.start) {
                        toosoon = true;
                        // too soon.. get next match
                    } else if (begin > reg.stop) {
                        goahead = false;
                        if (!sccp.nextRegion(reg)) {
                            // Done searching the document... bail out..
                            return newTasks;
                        }
                    } 
                } while (!goahead);

                if (toosoon) {
                    // find next match!
                    index = end;
                    continue;
                }

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
                int idx = 0;
                while (idx <= begin) {
                    if (text.charAt(idx) == '\n') {// NOI18N
                        ++lineno;
                    }
                    ++idx;
                }
                
                index = end;
                
                String description = text.subSequence(begin, nonwhite+1).toString();

                DataObject dataObject = DataObject.find(env.getFileObject());
                Line line = TLUtils.getLineByNumber(dataObject, lineno);

                Suggestion task = prepareSuggestion(matchTag, description, line);
                newTasks.add(task);
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

            Matcher matcher = settings().getTaskTags().getScanRegexp().matcher(text);
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

                DataObject dataObject = DataObject.find(env.getFileObject());
                Line line = TLUtils.getLineByNumber(dataObject, lineno);

                Suggestion task = prepareSuggestion(matchTag, description, line);
                newTasks.add(task);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newTasks;
    }

    /** Merges found sugegstions with known ones if one exists. */
    private Suggestion prepareSuggestion(TaskTag matchTag, String description, Line line) {
        Suggestion suggestion = null;
        if (line != null) {
            SuggestionAgent agent = getAgent(line);
            if (agent != null) {
                suggestion = agent.getSuggestion();
                agent.setSummary(description);
                if (matchTag != null) {
                    agent.setPriority(matchTag.getPriority());
                }
            }
        }

        if (suggestion == null) {
            SuggestionManager manager = SuggestionManager.getDefault();
            SuggestionAgent agent = manager.createSuggestion(SourceTaskProvider.TYPE,
                                         description,
                                         null,
                                         this);

            agent.setLine(line);
            if (matchTag != null) {
                agent.setPriority(matchTag.getPriority());
            }
            agents.add(agent);
            suggestion = agent.getSuggestion();
        }

        return suggestion;
    }

    private TaskTag getTag(CharSequence text, int start, int end) {
        TaskTag tag = settings().getTaskTags().getTag(text, start+1, (end - start)-1);
        return tag;
    }    

    private Settings settings() {
        if (settings == null) {
            // FIXME manifests missing prepare event
            settings = (Settings)Settings.findObject(Settings.class, true);
        }
        return settings;
    }

    private SuggestionAgent getAgent(Line l) {
        Iterator it = agents.iterator();
        while (it.hasNext()) {
            SuggestionAgent next = (SuggestionAgent) it.next();
            if (next == null) continue;
            if (l.equals(next.getSuggestion().getLine())) return next;
        }
        return null;
    }
}


