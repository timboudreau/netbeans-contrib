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
import org.openide.util.Utilities;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.Line;

import org.netbeans.modules.html.*;

import org.w3c.tidy.*;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.client.SuggestionManager;
import org.netbeans.modules.tasklist.client.SuggestionPriority;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;


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
    private SuggestionContext env;

    public String[] getTypes() {
        return new String[] { TYPE };
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

    /** Package private rescan: called when you've rewritten
        the HTML for example */
    void rescan() {
        rescan(env, request);
    }

    static boolean isHTML(DataObject dobj) {
         // XXX instanceof not good - I've heard data object
         // instancing like this is going away. Look for
         // some kind of HTML related cookie instead?
         return dobj instanceof HtmlDataObject;
    }

    static boolean isJSP(DataObject dobj) {
        String file = dobj.getPrimaryFile().getNameExt();
        return file.endsWith(".jsp") || // NOI18N
            file.endsWith(".JSP") || // NOI18N
            // There are several data objects in web/core/.../jsploader
            // so just look for the jsploader package instead of
            // and actual classname
            (dobj.getClass().getName().indexOf("jsploader") != -1); // NOI18N
    }

    static boolean isXML(DataObject dobj) {
        String file = dobj.getPrimaryFile().getNameExt();
        return file.endsWith(".xml") || // NOI18N
            file.endsWith(".XML") || // NOI18N
            (dobj.getClass().getName().indexOf("XMLDataObject") != -1); // NOI18N
    }
                         
    public List scan(SuggestionContext env) {

        DataObject dobj = null;
        try {
            dobj = DataObject.find(env.getFileObject());
        } catch (DataObjectNotFoundException e) {
            return null;
        }

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
            String text = (String) env.getCharSequence(); //XXX downcast, InputStream from FileObject
            input = new StringBufferInputStream(text);

            if (tidy == null) {
                tidy = new Tidy();
            }
            tidy.setOnlyErrors(true);
            tidy.setShowWarnings(true);
            tidy.setQuiet(true);
            // XXX Apparently JSP pages (at least those involving
            // JSF) need XML handling in order for JTidy not to choke on them
            tidy.setXmlTags(isXML || isJSP);

            PrintWriter output = new ReportWriter(this);
            tidy.setErrout(output);
            // Where do I direct its output? If it really obeys
            // setQuiet(true) it shouldn't matter...
            tidy.parse(input, System.err);
        }
        return parseTasks;
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
        if (error) {
            Image taskIcon = Utilities.loadImage("org/netbeans/modules/tasklist/html/error.gif"); // NOI18N
            s.setIcon(taskIcon);
            s.setPriority(SuggestionPriority.HIGH);
        }
        if (parseTasks == null) {
            parseTasks = new ArrayList(30);
        }
        parseTasks.add(s);
    }

    private Object request = null;
    private Tidy tidy = null;
}
