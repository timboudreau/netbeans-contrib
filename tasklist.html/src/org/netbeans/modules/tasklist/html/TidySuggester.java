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

package org.netbeans.modules.tasklist.html;

import org.netbeans.api.tasklist.*;

import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.openide.ErrorManager;
import org.openide.explorer.view.*;


import org.openide.loaders.DataObject;
import org.openide.text.Line;

import org.netbeans.modules.html.*;

import org.w3c.tidy.*;

import org.netbeans.modules.tasklist.core.TLUtils;


/**
 * This class lists problems in HTML documents (based on a
 * doc scan by the Tidy utility)
 * <p>
 *
 * @author Tor Norbye
 */
public class TidySuggester extends DocumentSuggestionProvider 
    implements ErrorReporter  {

    final private static String TYPE = "nb-html-errors"; // NOI18N

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
    
    /** Rescan the given document for suggestions. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example when the document is
     * saved.
     * <p>
     * This method should register the suggestions with the
     * suggestion manager.
     * <p>
     * @param doc The document being scanned
     * @param dobj The Data Object for the file being scanned
     */
    public void rescan(Document doc, DataObject dobj) {
        dataobject = dobj;
        document = doc;
        List newTasks = scan(doc, dobj);
        SuggestionManager manager = SuggestionManager.getDefault();

        if ((newTasks == null) && (showingTasks == null)) {
            return;
        }
        manager.register(TYPE, newTasks, showingTasks);
        showingTasks = newTasks;
    }

    /** Package private rescan: called when you've rewritten
        the HTML for example */
    void rescan() {
        rescan(document, dataobject);
    }

    static boolean isHTML(DataObject dobj) {
         // XXX instanceof not good - I've heard data object
         // instancing like this is going away. Look for
         // some kind of HTML related cookie instead?
         return dobj instanceof HtmlDataObject;
    }

    static boolean isJSP(DataObject dobj) {
        String file = dobj.getPrimaryFile().getName();
        return file.endsWith(".jsp") || // NOI18N
            file.endsWith(".JSP") || // NOI18N
            // There are several data objects in web/core/.../jsploader
            // so just look for the jsploader package instead of
            // and actual classname
            (dobj.getClass().getName().indexOf("jsploader") != -1); // NOI18N
    }

    static boolean isXML(DataObject dobj) {
        String file = dobj.getPrimaryFile().getName();
        return file.endsWith(".xml") || // NOI18N
            file.endsWith(".XML") || // NOI18N
            (dobj.getClass().getName().indexOf("XMLDataObject") != -1); // NOI18N
    }
                         
    
    /** Scan the given document for suggestions. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example as part of a directory
     * scan for suggestions.
     * <p>
     * @param doc The document being hidden
     * @param dobj The Data Object for the file being opened
     * @return List of suggestions scanned for this document type
     *
     */
    public List scan(Document doc, final DataObject dobj) {
         // XXX instanceof not good - I've heard data object
         // instancing like this is going away. Look for
         // some kind of HTML related cookie instead?
         boolean isHTML = isHTML(dobj);
         boolean isJSP = false;
         boolean isXML = false;
         if (!isHTML) {
             isJSP = isJSP(dobj);
             if (!isJSP) {
                 isXML = isXML(dobj);
             }
         }
         if (!(isHTML || isJSP || isXML)) {
             return null;
         }
        SuggestionManager manager = SuggestionManager.getDefault();
        
        parseTasks = null;
        parseObject = dobj;
        if (manager.isEnabled(TYPE)) {
            InputStream input = null;
            try {
                String text = doc.getText(0, doc.getLength());
                input = new StringBufferInputStream(text);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().
                    notify(ErrorManager.WARNING, e);
                return null;
            }
            
            if (tidy == null) {
                tidy = new Tidy();
            }
            tidy.setOnlyErrors(true);
            tidy.setShowWarnings(true);
            tidy.setQuiet(true);
            tidy.setXmlTags(isXML);

            PrintWriter output = new ReportWriter(this);
            tidy.setErrout(output);
            // Where do I direct its output? If it really obeys
            // setQuiet(true) it shouldn't matter...
            tidy.parse(input, System.err);
        }
        return parseTasks;
    }

    /**
      * Remove items added by {@link rescan}.
     * <p>
     * @param document The document previously scanned
     * @param dataobject The Data Object for the file previously scanned
      */
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

    /** List being built during a scan */
    private List parseTasks = null;
    private DataObject parseObject = null;

    public void reportError(int line, int col, boolean error, String message) {
        //System.err.println("reportError(" + line + ", " + col + ", " + error + ", " + message + ")");
        
        SuggestionManager manager = SuggestionManager.getDefault();
        Suggestion s = manager.createSuggestion(TYPE,
                                                message,
                                                null,
                                                this);
        if (line != -1) {
            Line l = TLUtils.getLineByNumber(parseObject, line);
            s.setLine(l);
        }
        s.setPriority(SuggestionPriority.NORMAL);
        if (parseTasks == null) {
            parseTasks = new ArrayList(30);
        }
        parseTasks.add(s);
    }

    private DataObject dataobject = null;
    private Document document = null;
    private Tidy tidy = null;
}
