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

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskList;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.openide.util.Lookup;
import org.openide.TopManager;
import org.openide.ErrorManager;

import org.netbeans.api.tasklist.*;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.api.tasklist.DocumentSuggestionProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.text.EditorSupport;
import org.openide.text.Line;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 * Actual suggestion manager provided to clients when the Suggestions
 * module is running.
 *
 * @author Tor Norbye
 */


final public class SuggestionManagerImpl extends SuggestionManager
    implements DocumentListener, CaretListener, ComponentListener {

    public static final ErrorManager err = ErrorManager.getDefault().getInstance("org.netbeans.modules.tasklist"); // NOI18N

    /** Construct a new SuggestionManager instance. */
    public SuggestionManagerImpl() {
    }

    /**
     * Construct a new suggestion, of the given type, with the given
     * summary, and performing the designated action.
     *
     * @param type The type of this suggestion. This is a unique string
     *             id, which corresponds to the layer-declaration of a
     *             Suggestion Type which has a user description; you can
     *             also query the SuggestionManager to ask if this
     *             type of Suggestion has been disabled by the user.
     * @param summary A one-line summary of the task; this is what appears
     *             in the Suggestions Window main column.
     * @param action An action to be performed when the user "runs" this
     *             task. Some actions may actually fix the problem that
     *             the suggestion is  describing, for example "update
     *             the copyright to include 2002" might edit the document
     *             when the action is run; other actions may point the
     *             user to the problem: the compiler may add tasks to
     *             fix errors and running these actions open the editor
     *             on the relevant source line.
     *
     * @todo Provide specific guidelines here for how these sentences
     *       should be worded so that we get a consistent set of
     *       descriptions.
     *
     */
    public Suggestion createSuggestion(String type,
                                       String summary,
                                       SuggestionPerformer action) {
        SuggestionImpl s = new SuggestionImpl();

        // "Sanitize" the summary: replace newlines with ':'
        // " " or ":" (let's pick one).
        // (Oh crap. What do we do about CRLF's? Replace with ": " ?
        // This won't work right for \r-only systems, but surely OSX didn't
        // keep that bad MacOS habit, did it? XXX
        if (summary.indexOf('\n') != -1) {
            int n = summary.length();
            StringBuffer sb = new StringBuffer(2*n); // worst case
            for (int i = 0; i < n; i++) {
                char c = summary.charAt(i);
                if (c == '\n') {
                    sb.append(':');
                    sb.append(' ');
                } else if (c != '\r') {
                    sb.append(c);
                }
            }
            summary = sb.toString();
        }
        
        s.setSummary(summary);
        s.setAction(action);
        s.setType(type);
        SuggestionType st = SuggestionTypes.getTypes().getType(type);
        s.setSType(st);
        return s;
    }
    
    /**
     * Return true iff the user appears to be "reading" the
     * suggestions. This means it will return false if the
     * Suggestion window is not open. This means that if a suggestion
     * is only temporarily interesting, this method lets you
     * skip creating a suggestion since if the suggestion window
     * isn't open, the user won't see it anyway, and since this
     * is a temporarily-interesting suggestion by the time the
     * window is opened the suggestion isn't relevant anymore.
     * (Yes, it's obviously possible that the suggestion window
     * is open but the user is NOT looking at it; that will be
     * fixed in tasklist version 24.0 when we have eye scanning
     * hardware and access-APIs.)
     * <p>
     *
     * @param id The String id of the Suggestion Type we're
     *    interested in. You may pass null to ask about any/all
     *    Suggestion Types. See the {@link Suggestion} documentation
          for how Suggestion Types are registered and named.
     *
     * @return True iff the suggestions are observed by the user.
     */
    public boolean isObserved(String id) {
	TaskListView view = 
	    TaskListView.getTaskListView(SuggestionsView.CATEGORY); // NOI18N
        if (view == null) {
            return false;
        }
        // TODO: Check if there are filters on the view, and if so,
        // return something appropriate.
        return view.isShowing();
    }


    private void updateCategoryCount(SuggestionImpl category) {
        SuggestionType type = category.getSType();
        int count = category.hasSubtasks() ?
            category.getSubtasks().size() : 0;
        String summary = type.getLocalizedName() + " (" + // NOI18N
            Integer.toString(count) + ")"; // NOI18N
        category.setSummary(summary);
    }

    /** Keep track of our "view state" since we get a few stray
     * messages from the component listener.
     */
    boolean running = false;
    boolean prepared = false;

    /** When the Suggestions window is made visible, we notify all the
        providers that they should run. But that's not correct if there
        was a filter in effect when you left the window. Thus, we need to
        check if a SuggestionProvider should be notified. Since I know
        the filter accepts one and only one SuggestionProvider, for
        performance reasons it's fastest to just remember which
        SuggestionProvider is allowed-through. When null, it means there's
        no filter in effect and all should pass through. */
    SuggestionProvider unfiltered = null;
    
    
    /** Called when the Suggestions View is opened */
    void notifyViewOpened() {
        if (!prepared) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider)it.next();
                provider.notifyPrepare();
            }
            prepared = true;
        }
    }

    /** Called when the Suggestions View is made visible
     *
     * @todo If a filter was in effect when we left the window, we should
     * NOT notify the filtered-out SuggestionProviders!
     */
    void notifyViewShowing() {
        if (!running) {
            if (!prepared) {
                notifyViewOpened();
            }
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider)it.next();
                if ((unfiltered == null) ||
                    (unfiltered == provider)) {
                    provider.notifyRun();
                }
            }
            docStart();
            running = true;
        }
    }

    /** Called when the Suggestions View is hidden */
    void notifyViewHidden() {
        if (running) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider)it.next();
                if ((unfiltered == null) ||
                    (unfiltered == provider)) {
                    provider.notifyStop();
                }
            }
            docStop();
            running = false;
        }
    }

    /** Called when the Suggestions View is closed */
    void notifyViewClosed() {
        if (prepared) {
            if (!running) {
                notifyViewHidden();
            }
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider)it.next();
                provider.notifyFinish();
            }
            prepared = false;
        }
    }

    private List providers = null;
    private List docProviders = null; // subset of elements in providers; these implement DocumentSuggestionProvider
    private Map providersByType = null;
    
    /** Return a list of the providers registered
     * @todo Filter out disabled providers
     */
    List getProviders() {
        if (providers == null) {
            providers = new ArrayList(20);
            docProviders = new ArrayList(20);
            Lookup.Template template =
                new Lookup.Template(SuggestionProvider.class);
            Iterator it = Lookup.getDefault().lookup(template).
                allInstances().iterator();
            while (it.hasNext()) {
                SuggestionProvider sp = (SuggestionProvider)it.next();
                if (sp != null) {
                    providers.add(sp);
                    if (sp instanceof DocumentSuggestionProvider) {
                        docProviders.add(sp);
                    }
                }
            }
        }
        return providers;
    }

    List getDocProviders() {
        if (docProviders == null) {
            getProviders(); // side effect: initialize docProviders
        }
        return docProviders;
    }
    
    
    private SuggestionList list = null;

    /**
     * Return the TaskList that we're managing
     */
    SuggestionList getList() {
        if (list == null) {
            TaskListView view =
            TaskListView.getTaskListView(SuggestionsView.CATEGORY); // NOI18N
            if (view == null) {
            view = new SuggestionsView();
            // Let user open the window
            //   TODO Find a way to manage the tasklist so that I -don't-
            //   have to create it now; only when it's opened by the user!
            //view.showInMode();
            }
            list = (SuggestionList)view.getList();
        }
        return list;
    }

    private File getRegistryFile(boolean create) {
        String loc = System.getProperty("netbeans.user") + // NOI18N
            File.separatorChar + "system" + File.separatorChar + "TaskList" + //NOI18N
            File.separatorChar + "suggestiontype-registry.xml"; // NOI18N
        File file = new File(loc);
        if (create) {
            if (!file.exists()) {
                File parent = file.getParentFile();
                parent.mkdirs();
            }
        }
        return file;
    }
    
    /**
     * Return true iff the type of suggestion indicated by the
     * id argument is enabled. By default, all suggestion types
     * are enabled, but users can disable suggestion types they're
     * not interested in.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type is enabled
     */
    public synchronized boolean isEnabled(String id) {
        if (disabled == null) {
            readTypeRegistry();
            if (disabled == null) {
                disabled = new HashSet(40);
            }
        }
        return !disabled.contains(id);
    }

    /** Map containing names of Suggestion Types that have been disabled
     * by the user. */
    private Set disabled = null;

    /**
     * Store whether or not a particular Suggestion type should be
     * enabled.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     * @param enabled True iff the suggestion type should be enabled
     */
    public synchronized void setEnabled(String id, boolean enabled) {
        SuggestionType type = SuggestionTypes.getTypes().getType(id);

        if (disabled == null) {
            disabled = new HashSet(40);
        }

        if (enabled) {
            disabled.remove(id);
            setConfirm(type, true, false);
        } else {
            disabled.add(id);
        }
        
        // Update the suggestions list: when disabling, rip out suggestions
        // of the same type, and when enabling, trigger a recompute in case
        // we have pending suggestions
        SuggestionProvider provider = getProvider(type);
        if (enabled) {
            // XXX This isn't exactly right. Make sure we do the
            // right life cycle for each provider.
            provider.notifyPrepare();
            provider.notifyRun();
            
            // XXX what about provider.docStart() (or whatever docStart will do to the document?)
        } else {
            // Remove suggestions of this type
            provider.notifyStop();
            // XXX what about provider.docStop() (or whatever docStop will do to the document?)
            provider.notifyFinish();
            getList().removeCategory(type); // XXX should only do this once!
        }

        writeTypeRegistry();
        
    }

    Set getDisabledTypes() {
        return disabled;
    }

    
    /**
     * Return true iff the type of suggestion indicated by the
     * type argument should produce a confirmation dialog
     * By default, all suggestion types have confirmation dialogs
     * (if they provide one) but users can select to skip these.
     * <p>
     * The only way to get it back is to disable the type, and
     * re-enable it.
     *
     * @param type The Suggestion Type. See the
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type should have a 
     *    confirmation dialog.
     */
    public synchronized boolean isConfirm(SuggestionType type) {
        if (noconfirm == null) {
            readTypeRegistry();
            if (noconfirm == null) {
                noconfirm = new HashSet(40);
            }
        }
        return !noconfirm.contains(type);
    }

    
    /** Map containing names of Suggestion Types that the user wants to
     * fix without a confirmation dialog */
    private Set noconfirm = null;

    /**
     * Store whether or not a particular Suggestion type should produce
     * a confirmation popup.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     * @param write Write to disk the update iff true.
     * @param enabled True iff the suggestion type should have a confirmation
     *     dialog
     */
    public synchronized void setConfirm(SuggestionType type, boolean confirm, boolean write) {
        if (noconfirm == null) {
            noconfirm = new HashSet(40);
        }

        if (confirm) {
            noconfirm.remove(type);
        } else {
            noconfirm.add(type);
        }
        if (write) {
            writeTypeRegistry();
        }
    }
    
    /** Notify the SuggestionManager that a particular category filter
     * is in place.
     *
     * @todo Fix this method; currently the bulk of the body is commented out
     *
     * @param type SuggestionType to be shown, or
     *     null if the view should not be filtered (e.g. show all)
     */
    void notifyFiltered(SuggestionList tasklist, SuggestionType type) {
        // "Flatten" the list when I'm filtering so that I don't show
        // category nodes!
        if (type != null) {
            tasklist.clear();
       
            Collection values = tasklist.getCategoryTasks();
            if (values != null) {
                Iterator it = values.iterator();
                ArrayList list = new ArrayList(200);
                while (it.hasNext()) {
                    SuggestionImpl s = (SuggestionImpl)it.next();
                    if (s.hasSubtasks()) {
			    // XXX This will break when I do subtypes or common-grouping
                        list.addAll(s.getSubtasks());
                    }
                }
                tasklist.addRemove(list, null, false, null);
            }
        } else {
            // "Merge" the list when I'm going to no-filter
            tasklist.clearCategoryTasks();
            List oldList = tasklist.getTasks();
            List suggestions = new ArrayList(oldList.size());
            suggestions.addAll(oldList);
            tasklist.clear();
            Iterator it = suggestions.iterator();
            List group = null;
            SuggestionType prevType = null;
            while (it.hasNext()) {
                SuggestionImpl s = (SuggestionImpl)it.next();
                if (s.getSType() != prevType) {
                    if (group != null) {
                        register(prevType.getName(), group, null);
                        group.clear();
                    } else {
                        group = new ArrayList(50);
                    }
                    prevType = s.getSType();
                    group.add(s);
                }
            }
            if ((group != null) && (group.size() > 0)) {
                register(prevType.getName(), group, null);
            }
        }
        
        /** TODO Get this working; it's currently a bit broken so commented
            out

            
        unfiltered = null;
        Collection types = SuggestionTypes.getTypes().getAllTypes();
        Iterator it = types.iterator();

        // TODO
        // XXX Wouldn't it be faster to iterate over the providers
        // and check each one? Here I risk notifying the same provider
        // more than once - and inconsistently!


        
        while (it.hasNext()) {
            SuggestionType t = (SuggestionType)it.next();
            SuggestionProvider provider = getProvider(t);
            if (provider == null) {
                // Some types don't have providers
                continue;
            }
            boolean enabled = (type == t);
            // XXX TODO Keep track of the previous state of each
            // provider
            if (enabled) {
                provider.notifyRun();
                // what about docStart() or the equivalent
                unfiltered = provider;
            } else {
                provider.notifyStop();
                // what about docStop or the equivalent
            }
        }

        */
    }

    /** @return The SuggestionProvider responsible for providing suggestions
     * of a particular type */
    private SuggestionProvider getProvider(SuggestionType type) {
        if (providersByType == null) {
            SuggestionTypes suggestionTypes = SuggestionTypes.getTypes();
            //Collection types = suggestionTypes.getAllTypes();
            List providers = getProviders();
            providersByType = new HashMap(100); // XXXXX ?<??
            // Perhaps use suggestionTypes.getAllTypes()*2 as a seed?
            // Note, this includes suggestion types that do not have
            // providers
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider)it.next();
                String typeNames[] = provider.getTypes();
                if (typeNames == null) {
                    // Should I just let a NullPointerException occur instead?
                    // After all, non null is required for correct operation.
                    TopManager.getDefault().getErrorManager().log("SuggestionProvider " + provider + " provides null value to getTypes()");
                    continue;
                }
                for (int j = 0; j < typeNames.length; j++) {
                    SuggestionType tp = suggestionTypes.getType(typeNames[j]);
                    providersByType.put(tp, provider);
                }
            }
        }
        return (SuggestionProvider)providersByType.get(type);
    }





    private static class TypeXMLHandler extends DefaultHandler {
        private boolean parsingDisabled = false;
        private boolean parsingNoConfirm = false;
        private Set disabled = null;
        private Set noconfirm = null;

        
        TypeXMLHandler() {
        }

        public Set getDisabled() {
            return disabled;
        }
		
        public Set getNoConfirm() {
            return noconfirm;
        }
		
        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String localName,
                                 String name, Attributes attrs)
            throws SAXException {
            if (name.equals("type")) { // NOI18N
                if (parsingDisabled) {
                    String type = (String)attrs.getValue("id"); // NOI18N
                    if (disabled == null) {
                        disabled = new HashSet(50);
                    }
                    disabled.add(type);
                } else if (parsingNoConfirm) {
                    String id = (String)attrs.getValue("id"); // NOI18N
                    if (noconfirm == null) {
                        noconfirm = new HashSet(50);
                    }
                    SuggestionType type = SuggestionTypes.getTypes().getType(id);
                    noconfirm.add(type);
                }
            } else if (name.equals("disabled")) { // NOI18N
                parsingDisabled = true;
            } else if (name.equals("noconfirm")) { // NOI18N
                parsingNoConfirm = true;
            }
        }
            
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (name.equals("disabled")) { // NOI18N
                parsingDisabled = false;
            } else if (name.equals("noconfirm")) { // NOI18N
                parsingNoConfirm = false;
            }

        }
        
        /** No validation - don't read the DTD. Assume importers won't
            require external entities. */
        public InputSource resolveEntity(String pubid, String sysid) {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
    }

    /** Have we read the type registry yet? */
    private boolean registryRead = false;
    
    /** Read in the SuggestionType registry preferences.
     * @return True iff the registry was completely initialized without error
     */
    private boolean readTypeRegistry() {
        if (registryRead) {
            return true;
        }
        registryRead = true;
        File file = getRegistryFile(false);
        if (file.exists()) {
            try {
                Reader fileReader = new BufferedReader(new FileReader(file));
                try {
                    XMLReader reader = XMLUtil.createXMLReader(false);
                    
                    TypeXMLHandler handler = new TypeXMLHandler();
                    reader.setContentHandler(handler);
                    reader.setErrorHandler(handler);
                    reader.setEntityResolver(handler);
                    reader.parse(new InputSource(fileReader));
                    disabled = handler.getDisabled();
                    noconfirm = handler.getNoConfirm();
                    return true;
                } catch (SAXException e) {
                    TopManager.getDefault().getErrorManager().notify(
                                               ErrorManager.INFORMATIONAL, e);
                }
                fileReader.close();
            } catch (Exception e) {
                TopManager.getDefault().getErrorManager().notify(
                                               ErrorManager.INFORMATIONAL, e);
            }
        }
        return false;
    }

    /** Write out the SuggestionType registry preferences.
     * @return True iff the registry was completely written out without error
     */
    private boolean writeTypeRegistry()  {
        File file = getRegistryFile(true);
	try {
            Writer writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\"?>\n"); // NOI18N
            writer.write("<!DOCTYPE suggestionregistry PUBLIC '-//NetBeans//DTD suggestion registry 1.0//EN' 'http://www.netbeans.org/dtds/suggestion-registry-1_0.dtd'>\n"); // NOI18N
            writer.write("<typeregistry>\n"); // NOI18N
            Iterator it;
            if (disabled != null) {
                it = disabled.iterator();
                if (it.hasNext()) {
                    writer.write("  <disabled>\n"); // NOI18N
                    while (it.hasNext()) {
                        String typeName = (String)it.next();
                        writer.write("    <type id=\""); // NOI18N
                        writer.write(typeName);
                        writer.write("\"/>\n"); // NOI18N
                    }
                    writer.write("  </disabled>\n"); // NOI18N
                }
            }

            if (noconfirm != null) {
                it = noconfirm.iterator();
                if (it.hasNext()) {
                    writer.write("  <noconfirm>\n"); // NOI18N
                    while (it.hasNext()) {
                        SuggestionType type = (SuggestionType)it.next();
                        writer.write("    <type id=\""); // NOI18N
                        writer.write(type.getName());
                        writer.write("\"/>\n"); // NOI18N
                    }    
                    writer.write("  </noconfirm>\n"); // NOI18N
                }
            }
            writer.write("</typeregistry>\n"); // NOI18N
            writer.close();
            return true;
        } catch (Exception e) {
            TopManager.getDefault().getErrorManager().notify(
                                           ErrorManager.INFORMATIONAL, e);
        }
        return false;
    }
    
    /** Iterate over the folder recursively (optional) and scan all files.
        We skip CVS and SCCS folders intentionally. Would be nice if
        the filesystem hid these things from us. */
    void scan(DataFolder[] folders, SuggestionList list,
	boolean recursive) {
        // package-private instead of private for the benefit of the testsuite
        for (int i = 0; i < folders.length; i++) {
            DataFolder folder = folders[i];
            scan(folder, recursive, list);
        }
    }
    
    private void scan(DataFolder folder, boolean recursive, SuggestionList list) {
        if (TaskList.err.isLoggable(ErrorManager.INFORMATIONAL)) {
            TaskList.err.log("Scanning folder " + folder.getPrimaryFile().
                             getNameExt());
        }
        
        DataObject[] children = folder.getChildren();
        for (int i = 0; i < children.length; i++) {
            DataObject f = children[i];
            if (TaskList.err.isLoggable(ErrorManager.INFORMATIONAL)) {
                TaskList.err.log(" Checking " + f.getPrimaryFile().getNameExt());
            }
        
            if (f instanceof DataFolder) {
		if (!recursive) {
		    continue;
		}
		
                // Skip CVS and SCCS folders
                String name = f.getPrimaryFile().getNameExt();
                if ("CVS".equals(name) || "SCCS".equals(name)) { // NOI18N
                    continue;
                }

                StatusDisplayer.getDefault ().setStatusText(
                   NbBundle.getMessage(ScanSuggestionsAction.class,
                                    "ScanningFolder",  // NOI18N
                                       f.getPrimaryFile().getNameExt()));

                scan((DataFolder)f, true, list); // recurse!
            } else {
                // Get document, and I do mean now!

                if (!f.isValid()) {
                    continue;
                }

                EditorCookie edit =
                    (EditorCookie)f.getCookie(EditorCookie.class);
                if (edit == null) {
                    continue;
                }
	
                Document doc;
                try {
                    doc = edit.openDocument(); // DOES block
                } catch (IOException e) {
                    continue;
                }
                if (doc == null) {
                    continue;
                }

                StatusDisplayer.getDefault ().setStatusText(
                   NbBundle.getMessage(ScanSuggestionsAction.class,
                                       "ScanningFile", // NOI18N
                                       f.getPrimaryFile().getNameExt()));

                if (TaskList.err.isLoggable(ErrorManager.INFORMATIONAL)) {
                    TaskList.err.log("   About to scan: " + // NOI18N
                                     f.getPrimaryFile().getNameExt());
                }
                
                scan(doc, list, f);
            }
        }
    }
    
    void scan(Document doc, SuggestionList list, DataObject f) {
        List providers = getProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            SuggestionProvider provider = (SuggestionProvider)it.next();
            if (((unfiltered == null) ||
            (unfiltered == provider)) &&
            (provider instanceof DocumentSuggestionProvider)) {
                List l = ((DocumentSuggestionProvider)provider).scan(doc, f);
                if (l != null) {
                    // XXX ensure that scan returns a homogeneous list of tasks
                    register(provider.getTypes()[0], null, l, list);
                }
            }
        }
    }
    
    List erase = null;
    List origIcon = null;
    
    /** Set a series of suggestions as highlighted. Or, clear the current
     * selection of highlighted nodes.
     * <p>
     * @param suggestions List of suggestions that should be highlighted.
     *      If null, the selection is cleared.
     * @param type The type for which this selection applies. Each type
     *      has its own, independent set of highlighted suggestions. If
     *      null, it applies to all types.
     *
     */
    private void highlightNode(SuggestionsView view, Node node, Line line) {
        SuggestionImpl s = (SuggestionImpl)TaskNode.getTask(node);
        if (s.getLine() == line) {
            if (erase == null) {
                origIcon = new ArrayList(20);
                erase = new ArrayList(20);
            }
            origIcon.add(s.getIcon());
            s.setHighlighted(true);
            Image badge = Utilities.loadImage("org/netbeans/modules/tasklist/suggestions/badge.gif"); // NOI18N
            Image image = Utilities.mergeImages(s.getIcon(), badge,
            0, 0);
            s.setIcon(image);
            erase.add(s);
        }

        // Recurse?
        if (s.hasSubtasks() && (view.isExpanded(node))) {
            Node[] nodes = node.getChildren().getNodes();
            int n = (nodes != null) ? nodes.length : 0;
            for (int i = 0; i < n; i++) {
                highlightNode(view, nodes[i], line);
            }
        }
    }


    Line prevLine = null;

    /**
     * Set the current cursor line to the given line position. 
     * Suggestions on the given line will be highlighted.
     *
     * @param line The current line of the cursor.
     */
    public void setCursorLine(Line line) {
        if (line == prevLine) {
            return;
        }
        prevLine = line;

        // Clear out previously highlighted items
        if (erase != null) {
             Iterator it = erase.iterator();
             Iterator itorig = origIcon.iterator();
             while (it.hasNext()) {
                 SuggestionImpl s = (SuggestionImpl)it.next();
                 Image icon = (Image)itorig.next();
                 s.setIcon(icon);
                 s.setHighlighted(false);
             }
        }
        erase = null;
        origIcon = null;

        
        if (line == null) {
            // Prevent line==null from highlighting all suggestions
            // without an associated line position...
            return;
        }

        SuggestionsView view = (SuggestionsView)TaskListView.getCurrent();
        Node node = view.getEffectiveRoot();
        highlightNode(view, node, line);
    }
    
    /** Add and remove lists of suggestions from the suggestion
     * registry.
     * <p>
     * The tasks will remain in the list until the IDE is shut down,
     * or until the user performs the tasks, or until the tasks are explicitly
     * removed by you.
     * <p>
     * Note: if these suggestion corresponds to a disabled suggestion type,
     * they will not be added to the list.  To avoid computing Suggestion
     * objects in the first place, check isEnabled().
     * <p>
     * Note: only suggestions created by calling {@link createSuggestions}
     * should be registered here.
     * <p>
     * @todo Consider adding a "time-to-live" attribute here where you
     *   can indicate the persistence of the task; some suggestions should
     *   probably expire if the user doesn't act on it for 5(?) minutes,
     *   others should perhaps survive even IDE restarts.
     *
     * @param add List of suggestions that should be added
     * @param remove List of suggestions that should be removed. Note that
     *    the remove is performed before the add, so if a task appears
     *    in both list it will not be removed.
     *
     */
    public void register(String type, List add, List remove) {
        //System.out.println("register(" + type + ", " + add +
        //  ", " + remove + ")");
        register(type, add, remove, getList());
    }

    public void register(String typeName, List addList, List removeList,
                         SuggestionList tasklist) {
        // TODO check instanceof Task here, and throw an exception if not?

        // Get the first element, and use its type as the type for all.
        // This works because all elements in the list must have the same
        // (meta?) type.
        SuggestionType type = null;
        type = SuggestionTypes.getTypes().getType(typeName);
        if (type == null) {
            throw new IllegalArgumentException("No such SuggestionType: " + typeName);
        }

        List adds = addList;
        /* Group nodes with identical descriptions together.
           Not very useful. Need full subtypes instead.
           Commented it out for now.
        if ((addList != null) && (addList.size() > 0)) {
            adds = new ArrayList(addList.size());
            ListIterator it = addList.listIterator();
            SuggestionImpl prev = (SuggestionImpl)it.next();
            while (it.hasNext()) {
                SuggestionImpl curr = (SuggestionImpl)it.next();
                if (curr.getSummary().equals(prev.getSummary())) {
                    LinkedList subtasks = new LinkedList();
                    SuggestionImpl clone = new SuggestionImpl();
                    clone.copyFrom(prev);
                    clone.setAction(null);
                    prev.setLine(null); // "category" node shouldn't have
                       //  a line position
                    subtasks.add(clone); // Include self in list of children
                    subtasks.add(curr);
                    String summary = prev.getSummary();
                    
                    // Stupid stupid stupid
                    while (it.hasNext()) {
                        curr = (SuggestionImpl)it.next();
                        if (summary.equals(curr.getSummary())) {
                            // Yup, one more
                            subtasks.add(curr);
                        } else {
                            if (!it.hasNext()) {
                                curr = null;
                            }
                            break;
                        }
                    }
                    prev.setSubtasks(subtasks);
                }
                adds.add(prev);
                prev = curr;
            }
            if (prev != null) {
                adds.add(prev);
            }
        }
        */
        
        SuggestionImpl category = tasklist.getCategoryTask(type);
        // XXX Do I need to set the parent field on each item?
        synchronized(this) {
            tasklist.addRemove(adds, removeList, false, category);
        }

        // Leave category task around? Or simply make it invisible?
        // (Need new Task attribute and appropriate handling in filter
        // and export methods.)    By leaving it around, we don't reorder
        // the tasks on the user.
        //tasklist.removeCategory((SuggestionImpl)suggestions.get(0));

        updateCategoryCount(category); // TODO: skip this when filtered
    } 
    
    
    
    /**
     * Code related to Document scanning. It listens to the source editor and
     * tracks document opens and closes, as well as "current document" changes.
     * <p>
     * For lightweight document analysis, you can redo the scanning
     * whenever the editor is shown and hidden; for more expensive analysis,
     * you may only want to do it when the document is opened (after a timeout).
     * <p>
     * The API does not define which thread these methods are called on,
     * so don't make any assumptions. If you want to post something on
     * the AWT event dispatching thread for example use SwingUtilities.
     * <p>
     * Note that changes in document attributes only are "ignored" (in
     * the sense that they do not cause document edit notification.)
     *
     * @todo Document threading behavior
     * @todo Document timer behavior (some of the methods are called after
     *   a delay, others are called immediately.)
     * @todo Add timer methods; e.g. we both want to know when the document
     *   has been edited (immediately), and when the document has been edited
     *   and has been idle for a while
     *
     */

    
    private Document document = null;
    private DataObject dataobject = null;
    
    
    /** The given document has been opened
     * <p>
     * @param document The document being opened
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    private void docOpened(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docOpened(document, dataobject);
        }
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
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docEdited(Document document, DocumentEvent event,
                                      DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docEdited(document, event, dataobject);
        }
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
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docEditedStable(Document document,
                                            DocumentEvent event,
                                            DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docEditedStable(document, event, dataobject);
        }
    }
    
    // XXX  Do I need separate changedUpdate, insertUpdate, removeUpdate
    // methods, or is edited good enough?

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docShown(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docShown(document, dataobject);
        }
    }

    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docHidden(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docHidden(document, dataobject);
        }
    }

    /**
     * The given document has been closed; stop reporting suggestions
     * for this document and free up associated resources.
     * <p>
     * @param document The document being closed
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void docClosed(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.docClosed(document, dataobject);
        }
    }

    // XXX: Do we need abstract public void docSaved(Document document); ?


    public void changedUpdate(DocumentEvent e) {
	// Do nothing.
	// Changed update is only called for ATTRIBUTE changes in the
	// document, which I define as not relevant to the Document
	// Suggestion Providers.
    }
	
    public void insertUpdate(DocumentEvent e) {
        docEdited(document, e, dataobject);
	scheduleRescan(e, false);
    }

    public void removeUpdate(DocumentEvent e) {
        docEdited(document, e, dataobject);
	scheduleRescan(e, false);
    }

    /** Plan a rescan (meaning: start timer)
     * @param delay If true, don't create a rescan if one isn't already
     * pending, but if one is, delay it.
     * @param docEvent Document edit event. May be null. */
    private void scheduleRescan(final DocumentEvent docEvent, boolean delay) {
        if (wait) {
            waitEvent = docEvent;
            return;
        }
        
        if (delay && (runTimer == null)) {
            return;
        }
        
	// Stop our current timer; the previous node has not
	// yet been scanned; too brief an interval
	if (runTimer != null) {
	    runTimer.stop();
	    runTimer = null;
	}

	runTimer = new Timer(scanDelay,
		     new ActionListener() {
			 public void actionPerformed(ActionEvent evt) {
                             runTimer = null;
                             if (!wait) {
                                 docEditedStable(document, docEvent, dataobject);
                             }
			 }
		     });
	runTimer.setRepeats(false);
	runTimer.setCoalesce(true);
	runTimer.start();
    }

    private DocumentEvent waitEvent = null;
    private boolean wait = false;
    
    /** Tell the DocumentListener to wait updating itself indefinitely
     * (until it's told not to wait again). When it's told to stop waiting,
     * itself, it will call docEditedStable if that was called and cancelled
     * at some point during the wait
     * <p>
     * Typically, you should NOT call this method. It's intended for use
     * by the Suggestions framework to allow for example the modal fix
     * dialog which provides confirmations to suspend document updates until
     * all fix actions have been processed.
     */
    void setFixing(boolean wait) {
        boolean wasWaiting = this.wait;
        this.wait = wait;
        if (!wait && wasWaiting && (waitEvent != null)) {
            scheduleRescan(waitEvent, false);
        }
    }
    
    /** Start scanning for source items. */
    public void docStart() {
	/* OLD:
	org.openide.windows.TopComponent.getRegistry().
	    addPropertyChangeListener(this);

	// Also scan the current node right away: pretend source listener was
	// notified of the change to the current node (which has already occurred)
	// ... unfortunately this is not as easy as just calling getActivatedNodes
	// on the registry -- because that node may not be the last EDITORvisible
	// node... So resort to some hacks.
	Node[] nodes = NewTaskAction.getEditorNodes();
	if (nodes != null) {
	    scanner.propertyChange(new PropertyChangeEvent(
	      this,
	      TopComponent.Registry.PROP_ACTIVATED_NODES,
	      null,
	      nodes));
	} else {
	    // Most likely you're not looking at a panel that has an
	    // associated node, e.g. the welcome screen, or the editor isn't
	    // open
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
            err.log("Couldn't find current nodes...");
            }
	}
	*/

	// NEW:
	/** HACK: We need to always know what the current source file
	    in the editor is - and even when there isn't a source file
	    there, we need to know: if you for example switch to the
	    Welcome screen we should remove the tasks for the formerly
	    shown source file.
	    
	    I've tried listening to the global node, since we should
	    always get notified when the current node changes. However,
	    this has a couple of problems. First, we may get notified
	    of the node change BEFORE the source file is done editing;
	    in that case we can't find the node in the editor (we need
	    to check that a node is in the editor since we don't want
	    the task window to for example show the tasks for the 
	    current selection in the explorer).  Another problem is
	    a scenario I just ran into where if you open A, B from
	    explorer, then select A in the explorer, then select B in
	    the editor: when you now double click A in the explorer
	    there's no rescan. (I may have to debug this).

	    So instead I will go to a more reliable scheme, which 
	    unfortunately smells more like a hack from a NetBeans
	    perspective. The basic idea is this: I can find the
	    source editor, and which top component is showing in
	    the source editor.  I can get notified of when this
	    changes - by listening for componentHidden of the
	    top most pane. Then I just have to go and see which
	    component is now showing, and switch my component listener
	    to this new component. (From the component I can discover
	    which source file it's editing).  This has the benefit
	    that I'll know precisely when a new file has been loaded
	    in, etc. It may have the disadvantage that if you open
	    source files in other modes (by docking and undocking
	    away from the standard configuration) things get
	    broken. Perhaps I can keep my old activated-node-listener
	    scheme in place as a backup solution when locating the
	    source editor mode etc. fails.

	    It gets more complicated. What if you open the task window
	    when the editor is not visible? Then you can't attach a
	    listener to the current window - so you don't get notified
	    when a new file is opened. For that reason we also need to
	    listen to the workspace's property change notification, which
	    will tell us when the set of modes changes in the workspace.

	    ...and of course the workspace itself can change. So we need
	    to listen to the workspace change notification in the window
	    manager as well...
	*/

	/*
	WindowManager manager = TopManager.getDefault().getWindowManager();
	manager.addPropertyChangeListener(this);
	Workspace workspace = TopManager.getDefault().getWindowManager().
	    getCurrentWorkspace();
	workspace.addPropertyChangeListener(this);
	*/
	
	findCurrentFile(false);
    }

    /** The topcomponent we're currently tracking as the showing
	editor component */
    private TopComponent current = null;

    /** FOR DEBUGGING ONLY: Look up the data object for a top
       component, if possible. Returns the object itself if no DO
       was found (suitable for printing: DO is best, but component will
       do.
    private static Object tcToDO(TopComponent c) {
	Node[] nodes = c.getActivatedNodes();	
	if (nodes == null) {
	    return c;
	}
	Node node = nodes[0];
	if (node == null) {
	    return c;
	}
	DataObject dao = (DataObject)node.getCookie(DataObject.class);
	if (dao == null) {
	    return c;
	} else {
	    return dao;
	}
    }
     */

    /** 
        @todo Put this on a delayed timer, such that rapid changes
              in node selection doesn't cause excessive parsing.
              Studio seemed to have a 2 second delay.
        @todo Limit scan to objects shown in the source editor.
              Try getting associated topcomponent, and ensure isShowing(),
              as well as editor component subclass. Check my NewTaskAction
              where I searched for the editor component to get the
              current file name.
        @todo This method doesn't really scan, it really does additional
	      validation that the data object is an appropriate scan target;
	      so factor this into a validation method or something like that
	      along with the propertyChange code, e.g. isCurrentEditorObject
    */
    private void findCurrentFile(boolean delayed) {
	// Unregister previous listeners
	if (current != null) {
	    //System.err.println("Removing component listener from " + tcToDO(current));
	    current.removeComponentListener(this);
	    current = null;
            
	}
	// XXX is this the right thing to do?
        if (document != null) {
	    document.removeDocumentListener(this);
            docHidden(document, dataobject);
        }
        if (editors != null) {
            removeCaretListeners();
        }


	// Find which component is showing in it
	// Add my own component listener to it
	// When componentHidden, unregister my own component listener
	// Redo above

	// Locate source editor
        Workspace workspace = WindowManager.getDefault().getCurrentWorkspace();
        
        // HACK ALERT !!! HACK ALERT!!! HACK ALERT!!!
        // Look for the source editor window, and then go through its
        // top components, pick the one that is showing - that's the
        // front one!
        Mode mode  = workspace.findMode(EditorSupport.EDITOR_MODE);
	if (mode == null) {
	    // No source editor
	    // Perhaps we're on a workspace without an editor. In that
	    // case, look for the editor on the editing workspace.
	    // ... nope, that won't work, it will cause havoc
	    // for my visibility check. Yuck yuck yuck.
	    return;
	}
        TopComponent [] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            TopComponent tc = tcs[j];
            if (tc instanceof EditorSupport.Editor) {
                // Found the source editor...
                if (tc.isShowing()) {
		    current = tc;
                    break;
                }
            } else if (tc instanceof CloneableEditor) {
                // Found the source editor...  html or text most likely
                if (tc.isShowing()) {
		    current = tc;
                    break;
                }
            }
        }
	if (current == null) {
	    // No source editor
	    // "None of the editor components were on top!"
            return;
	}

	// Listen for changes on this component so we know when
	// it's replaced by something else
	//System.err.println("Add component listener to " + tcToDO(current));
	current.addComponentListener(this);

	Node[] nodes = current.getActivatedNodes();
	
	if ((nodes == null) || (nodes.length != 1)) {
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                err.log(
                  "Unexpected editor component activated nodes " + // NOI18N
                  " contents: " + nodes); // NOI18N
            }
	}

	Node node = nodes[0];

	final DataObject dao = (DataObject)node.getCookie(DataObject.class);
	err.log("Considering data object " + dao);
	if (dao == null) {
	    return;
	}

	if (!dao.isValid()) {
	    err.log("The data object is not valid!");
	    return;
	}


        /*
	if (dao == lastDao) {
	    // We've been asked to scan the same dataobject as last time;
	    // don't do that.
	    // Most likely you've temporarily switched to another (non-editor)
	    // node, and switched back (for example, double clicking on a node
	    // in the task window) and we're still on the same file so there's
	    // no reason to rescan.  We track changes to the currently scanned
	    // object differently (through a document listener).
	    err.log("Same dao as last time - not doing anything");
	    return; // Don't scan again
	}
	lastDao = dao;
        */
        
	final EditorCookie edit = (EditorCookie)dao.getCookie(EditorCookie.class);
	if (edit == null) {
	    err.log("No editor cookie - not doing anything");
	    return;
	}
	
	/* This is probably not necessary now with my new editor-tracking
	   scheme: I'm only calling this on visible components. This is
	   a leftover from my noderegistry listener days, and I'm keeping
	   it around in case I leave the NodeRegistry lister code in as
	   a fallback mechanism, since this is all a bit of a hack.
	// See if it looks like this data object is visible
	JEditorPane[] panes = edit.getOpenedPanes();
	if (panes == null) {
	    err.log("No editor panes for this data object");
	    return;
	}
	int k = 0;
	for (; k < panes.length; k++) {
	    if (panes[k].isShowing()) {
		break;
	    }
	}
	if (k == panes.length) {
	    err.log("No editor panes for this data object are visible");
	    return;
	}
	*/

	final Document doc = edit.getDocument(); // Does not block

	/* This comment applies to the old implementation, where
	   we're listening on activated node changes. Now that we're
	   listening for tab changes, the document should already
	   have been read in by the time the tab changes and we're
	   notified of it:

	// We might have a race condition here... you open the
	// document, and our property change listener gets notified -
	// but the document hasn't completed loading yet despite our
	// 1 second timer. Thus we might not get a document... However
	// since we continue listening for changes, eventually we WILL
	// discover the document
	*/
	if (doc == null) {
	    err.log("No document handle...");
	    return;
	}

	if (document != null) {
	    // Might be a duplicate removeDocumentListener -- that's
	    // okay right?
	    document.removeDocumentListener(this);
	}
	document = doc;
	doc.addDocumentListener(this);
        
        dataobject = dao;

	if (delayed) {
	    runTimer = new Timer(scanDelay,
		     new ActionListener() {
			 public void actionPerformed(ActionEvent evt) {
                             if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                                 err.log("Timer expired - time to scan " + dao);
                             }
                             docShown(doc, dataobject);
                             addCaretListeners();
			 }
		     });
	    runTimer.setRepeats(false);
	    runTimer.setCoalesce(true);
	    runTimer.start();
	} else {
	    // Do it right away
            docShown(doc, dataobject);
            addCaretListeners();
	}
    }

    JEditorPane[] editors = null;
    
    private void addCaretListeners() {
	EditorCookie edit = (EditorCookie)dataobject.getCookie(EditorCookie.class);
	if (edit != null) {
            JEditorPane panes[] = edit.getOpenedPanes();
            if ((panes != null) && (panes.length > 0)) {
                // We want to know about cursor changes in ALL panes
                editors = panes;
                for (int i = 0; i < editors.length; i++) {
                    editors[i].addCaretListener(this);
                }
            }
        }
    }
    
    private void removeCaretListeners() {
        if (editors != null) {
            for (int i = 0; i < editors.length; i++) {
                editors[i].removeCaretListener(this);
            }
        }
        editors = null;
    }

    /** The component is now showing: tell debugger to send us updates! */
    public void componentShown(ComponentEvent e) {
        //super.componentShown();
	// Don't care
    }

    /** The component is no longer showing: tell debugger to stop computing
	updates on our behalf! */
    public void componentHidden(ComponentEvent e) {
        //super.componentHidden();
	err.log("componentHidden");
	findCurrentFile(true);
    }

    /** Don't care - but must implement full ComponentListener interface */
    public void componentResized(ComponentEvent e) {
        //super.componentResized();
	// Don't care
    }
    
    /** Don't care - but must implement full ComponentListener interface */
    public void componentMoved(ComponentEvent e) {
        //super.componentMoved();
	// Don't care
    }

    /** Scan the given document for suggestions. Typically called
     * when a document is shown or when a document is edited, but
     * could also be called for example as part of a directory
     * scan for suggestions.
     * <p>
     * @param document The document being hidden
     * @param dataobject The Data Object for the file being opened
     *
     */
    /*
    public List scan(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            provider.scan(document, dataobject);
        }
    } */
    

    /** Stop scanning for source items */
    public void docStop() {
	/*
	WindowManager manager = TopManager.getDefault().getWindowManager();
	manager.removePropertyChangeListener(this);
	Workspace workspace = TopManager.getDefault().getWindowManager().
	    getCurrentWorkspace();
	workspace.removePropertyChangeListener(this);
	*/
	
	
	// Unregister previous listeners
	if (current != null) {
	    //System.err.println("Removing component listener from " + tcToDO(current));
	    current.removeComponentListener(this);
	    current = null;
	}
	// XXX is this the right thing to do?
	if (document != null) {
	    document.removeDocumentListener(this);
	    // NOTE: we do NOT null it out since we still need to
	    // see if the document is unchanged
	}
        if (editors != null) {
            removeCaretListeners();
        }

	/*
	org.openide.windows.TopComponent.getRegistry().
	    removePropertyChangeListener(this);
	*/

        docHidden(document, dataobject);
        document = null;
    }
    
    /** Timer which keeps track of outstanding scan requests; we don't
        scan briefly selected files */
    private Timer runTimer; // make sure we get the right one

    /** Cache for delay settings we want to use the timer for */
    private int scanDelay = 1000;

    
    /** Reacts to changes */
    //public void propertyChange(PropertyChangeEvent ev) {

        //String prop = ev.getPropertyName();
	//err.log("propertyChange: prop name is " + prop);
    
	
	/* OLD This is the old implementation. See the long comment in
	   start() explaining the new scheme which seems to work quite
	   a bit better. The below code relies on this scanner object
	   being added as a property change listener on the
	   TopComponent registry - which is currently not the case
	   (commented out in start() and stop(). You may also have to
	   tweak the code in scan(Node) a bit such that it checks for
	   a corresponding editor component for the node, since we're
	   going to hit lots of nodes in the property change listener
	   which do not correspond to editor window changes

	// [PENDING] Use PROP_ACTIVATED_NODES or PROP_CURRENT_NODES?
	// Study RegistryImpl in org.netbeans.core.windows
	// [PENDING] Listen for TopComponent changes instead?
	if (TopComponent.Registry.PROP_ACTIVATED_NODES.equals(
						     ev.getPropertyName())) {

	    // Stop our current timer; the previous node has not
	    // yet been scanned; too brief an interval
	    if (runTimer != null) {
		runTimer.stop();
		runTimer = null;
	    }

	    Node[] nodes = (Node[])ev.getNewValue();
	    if (nodes == null) {
		return;
	    }
	    
	    if (nodes.length != 1) {
		// If you've selected multiple nodes, this is not a
		// node event we're interested in; we only care when
		// you switch from one editor pane to another, not when
		// you say select a series of files in the explorer
		return;
	    }

	    scan(nodes[0]);
	}
	*/

	/*
	if (WindowManager.PROP_CURRENT_WORKSPACE == ev.getPropertyName()) {
	    Workspace oldWs = (Workspace)ev.getOldValue();
	    Workspace newWs = (Workspace)ev.getNewValue();
	    System.out.println("Current workspace changed: old " + oldWs + "; new " + newWs);
	} else if (Workspace.PROP_MODES == ev.getPropertyName()) {
	    Mode[] oldModes = (Mode[])ev.getOldValue();
	    Mode[] newModes = (Mode[])ev.getNewValue();
	    System.out.println("Mode set changed: old " + oldModes + "; new " + newModes);
	    // Check to see if the editor window has come or left
	}
	*/
    //}

    int prevLineNo = -1;

    
    /** Moving the cursor position should cause a delay in document scanning,
     * but not trigger a new update */
    public void caretUpdate(CaretEvent caretEvent) {
	scheduleRescan(null, true);
        
         // Check to see if I have any existing errors on this line - and if so,
        // highlight them.
        if (document instanceof StyledDocument) {
            int offset = caretEvent.getDot();
            int lineno = NbDocument.findLineNumber((StyledDocument)document, offset);
            if (lineno == prevLineNo) {
                // Just caret motion on the same line as the previous one -- ignore
                return;
            }
            prevLineNo = lineno;

	    // Here we could add 1 to the line number, since findLineNumber
	    // returns a 0-based line number, and most APIs return a 1-based
	    // line number; however, Line.Set.getOriginal also expects
	    // something zero based, so instead of doing the usual bit
	    // of subtracting there, we drop the add and subtract altogether

            // Go to the given line
            Line line = null;
            try {
                LineCookie lc = (LineCookie)dataobject.getCookie(LineCookie.class);
                if (lc != null) {
                    Line.Set ls = lc.getLineSet();
                    if (ls != null) {
                        // XXX HACK
                        // I'm subtracting 1 because empirically I've discovered
                        // that the editor highlights whatever line I ask for plus 1
                        line = ls.getCurrent(lineno);
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            
            if (line != null) {
                setCursorLine(line);
            }
        }
    }
    
    
}
