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

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TaskList;
import org.netbeans.modules.tasklist.core.Task;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

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

/**
 * Actual suggestion manager provided to clients when the Suggestions
 * module is running.
 *
 * @author Tor Norbye
 */


final public class SuggestionManagerImpl extends SuggestionManager {

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
                } else if (c == '\r') {
                } else {
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


    // register; should also take a "confirmation" panel.
    /**
     * Add a suggestion to the list of suggestions. The task will remain
     * in the list until the IDE is shut down, or until the user performs
     * the task, or until the task is explicitly removed by you by calling
     * remove().
     * <p>
     * Note: if this suggestion corresponds to a disabled suggestion type,
     * it will not be added to the list.  To avoid computing Suggestion
     * objects in the first place, check isEnabled().
     * <p>
     *
     *
     * @todo Consider adding a "time-to-live" attribute here where you
     *   can indicate the persistence of the task; some suggestions should
     *   probably expire if the user doesn't act on it for 5(?) minutes,
     *   others should perhaps survive even IDE restarts.
     *
     * @param suggestion The Suggestion to be added to the list.
     */
    public void add(Suggestion suggestion) {
        // TODO check instanceof Task here, and throw an exception if not?
        SuggestionImpl s = null;
        try {
            s = (
                 SuggestionImpl)suggestion;
        } catch (ClassCastException e) {
            TopManager.getDefault().getErrorManager().notify(
                                         ErrorManager.ERROR, e);
            return;
        }

        SuggestionType type = s.getSType();
        SuggestionImpl category = getCategoryTask(type);
        s.setParent(category);
        getList().add(s, false, false);
        updateCategoryCount(category); // TODO: skip this when filtered
    }


    
    /**
     * Same as {@link add(Suggestion)}, but register a set of suggestions
     * in one operation. Useful when you for example want to preserve
     * a particular order among a set of related suggestions, since registering
     * them one by one means the Suggestion Manager gets to order them
     * (so for example, if it prepends each item your suggestions end up
     * in the reverse order of the order you registered them.)
     * <p>
     * NOTE: All Suggestions should have the same SuggestionType.
     */
    public void add(List suggestions) {
        if (suggestions.size() == 0) {
            return;
        }
        // TODO check instanceof Task here, and throw an exception if not?

        // Get the first element, and use its type as the type for all.
        // This works because all elements in the list must have the same
        // (meta?) type.
        SuggestionType type = null;
        try {
            SuggestionImpl s = (SuggestionImpl)suggestions.get(0);
            type = s.getSType();
        } catch (ClassCastException e) {
            TopManager.getDefault().getErrorManager().notify(
                                         ErrorManager.ERROR, e);
            return;
        }

        SuggestionImpl category = getCategoryTask(type);
        // XXX Do I need to set the parent field on each item?
        getList().addRemove(suggestions, null, false, category);
        updateCategoryCount(category); // TODO: skip this when filtered
    }

    private SuggestionImpl getCategoryTask(SuggestionType type) {
        SuggestionImpl category = null;
        if (categoryTasks != null) {
            category = (SuggestionImpl)categoryTasks.get(type);
        }
        if (category == null) {
            category = new SuggestionImpl();

            category.setSummary(type.getLocalizedName());
            category.setAction(null);
            category.setType(type.getName());
            category.setSType(type);
            category.setIcon(type.getIconImage());
            if (categoryTasks == null) {
                categoryTasks = new HashMap(20);
            }
            categoryTasks.put(type, category);
            getList().add(category, false, false);
        }
        return category;
    }
    private HashMap categoryTasks = null;

    private void updateCategoryCount(SuggestionImpl category) {
        SuggestionType type = category.getSType();
        int count = category.hasSubtasks() ?
            category.getSubtasks().size() : 0;
        String summary = type.getLocalizedName() + " (" + // NOI18N
            Integer.toString(count) + ")"; // NOI18N
        category.setSummary(summary);
    }


    
    /**
     * Remove a suggestion from the list of suggestions.
     * Suggestions are automatically removed after they have been "performed",
     * so you don't have to do this explicitly unless you know that the
     * task has gone "stale" so you want to remove it.
     * It's okay to remove a task that has already been removed, so
     * you don't need to check first if the task is still in the list.
     * <p>
     *
     * @param suggestion The Suggestion to be added to the list.
     */
    public void remove(Suggestion suggestion) {
        // TODO check instanceof Task here, and throw an exception if not?
        //getList().remove((Task)suggestion);

        SuggestionImpl s = null;
        try {
            s = (SuggestionImpl)suggestion;
        } catch (ClassCastException e) {
            TopManager.getDefault().getErrorManager().notify(
                                         ErrorManager.ERROR, e);
            return;
        }

        getList().remove(s);

        
        // Leave category task around? Or simply make it invisible?
        // (Need new Task attribute and appropriate handling in filter
        // and export methods.)    By leaving it around, we don't reorder
        // the tasks on the user.
        //removeCategory(s);

        SuggestionType type = s.getSType();
        SuggestionImpl category = getCategoryTask(type);
        updateCategoryCount(category); // TODO: skip this when filtered
    }

    private void removeCategory(SuggestionImpl s) {
        SuggestionImpl category = (SuggestionImpl)s.getParent();
        if ((category != null) && !category.hasSubtasks()) {
            getList().remove(category);
            categoryTasks.remove(category.getSType());
        }
    }
    
    /**
     * Same as {@link remove(Suggestion)}, but unregister a set of
     * suggestions one operation. In addition to being a convenience
     * and offering symmetry to {@link add(List)}, this offers better
     * performance than calling {@link remove(Suggestion} on each
     * suggestion in the List, because the UI will only be updated
     * once at the end of the removal, instead of after every removal.
     */
    public void remove(List suggestions) {
        if (suggestions.size() == 0) {
            return;
        }

        // TODO check instanceof Task here, and throw an exception if not?
        getList().addRemove(null, suggestions, false, null);

        // Leave category task around? Or simply make it invisible?
        // (Need new Task attribute and appropriate handling in filter
        // and export methods.)    By leaving it around, we don't reorder
        // the tasks on the user.
        //removeCategory((SuggestionImpl)suggestions.get(0));

        SuggestionType type = ((SuggestionImpl)(suggestions.get(0))).getSType();
        SuggestionImpl category = getCategoryTask(type);
        updateCategoryCount(category); // TODO: skip this when filtered

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
                notifyPrepare(provider);
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
                    notifyRun(provider);
                }
            }
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
                    notifyStop(provider);
                }
            }
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
                notifyFinish(provider);
            }
            prepared = false;
        }
    }

    private List providers = null;
    private Map providersByType = null;
    
    /** Return a list of the providers registered
     * @todo Filter out disabled providers
     */
    List getProviders() {
        if (providers == null) {
            providers = new ArrayList(20);
            Lookup.Template template =
                new Lookup.Template(SuggestionProvider.class);
            Iterator it = Lookup.getDefault().lookup(template).
                allInstances().iterator();
            while (it.hasNext()) {
                SuggestionProvider sp = (SuggestionProvider)it.next();
                if (sp != null) {
                    providers.add(sp);
                }
            }
        }
        return providers;
    }

    /**
     * Return the TaskList that we're managing
     */
    TaskList getList() {
	TaskListView view = 
	    TaskListView.getTaskListView(SuggestionsView.CATEGORY); // NOI18N
        if (view == null) {
            view = new SuggestionsView();
            /* Let user open the window
               TODO Find a way to manage the tasklist so that I -don't-
               have to create it now; only when it's opened by the user!
            view.showInMode();  
            */
        }
        TaskList list = view.getList();
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
    public boolean isEnabled(String id) {
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
    public void setEnabled(String id, boolean enabled) {
        if (disabled == null) {
            disabled = new HashSet(40);
        }

        if (enabled) {
            disabled.remove(id);
        } else {
            disabled.add(id);
        }

        SuggestionType type = SuggestionTypes.getTypes().getType(id);
        
        // Update the suggestions list: when disabling, rip out suggestions
        // of the same type, and when enabling, trigger a recompute in case
        // we have pending suggestions
        if (enabled) {
            SuggestionProvider provider = getProvider(type);
            // XXX This isn't exactly right. Make sure we do the
            // right life cycle for each provider.
            notifyPrepare(provider);
            notifyRun(provider);
        } else {
            // Remove suggestions of this type
            List tasks = getList().getTasks();
            Iterator ti = tasks.iterator();
            ArrayList removeTasks = new ArrayList(50);
            while (ti.hasNext()) {
                SuggestionImpl suggestion = (SuggestionImpl)ti.next();
                if (suggestion.getSType() == type) {
                    removeTasks.add(suggestion);
                }
            }
            remove(removeTasks);
        }

        writeTypeRegistry();
        
    }

    Set getDisabledTypes() {
        return disabled;
    }

    
    /** Notify the SuggestionManager that a particular category filter
     * is in place.
     *
     * @todo Fix this method; currently the bulk of the body is commented out
     *
     * @param type SuggestionType to be shown, or
     *     null if the view should not be filtered (e.g. show all)
     */
    void notifyFiltered(SuggestionType type) {

        // "Flatten" the list when I'm filtering so that I don't show
        // category nodes!
        if (type != null) {
            getList().clear();
        
            if (categoryTasks != null) {
                Collection values = categoryTasks.values();
                Iterator it = values.iterator();
                ArrayList list = new ArrayList(200);
                while (it.hasNext()) {
                    SuggestionImpl s = (SuggestionImpl)it.next();
                    list.addAll(s.getSubtasks());
                }
                getList().addRemove(list, null, false, null);
            }
        } else {
            // "Merge" the list when I'm going to no-filter
            categoryTasks = null; // recreate such that they get reinserted etc.
            List oldList = getList().getTasks();
            List suggestions = new ArrayList(oldList.size());
            suggestions.addAll(oldList);
            getList().clear();
            Iterator it = suggestions.iterator();
            while (it.hasNext()) {
                SuggestionImpl s = (SuggestionImpl)it.next();
                add(s);
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
                notifyRun(provider);
                unfiltered = provider;
            } else {
                notifyStop(provider);
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
        private Set disabled = null;

        
        TypeXMLHandler() {
        }

        public Set getDisabled() {
            return disabled;
        }
		
        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startElement(String uri, String localName,
                                 String name, Attributes attrs)
            throws SAXException {
            //System.out.println("startElement(" + name + ")");
            if (name.equals("type")) { // NOI18N
                if (parsingDisabled) {
                    String type = (String)attrs.getValue("id"); // NOI18N
                    if (disabled == null) {
                        disabled = new HashSet(50);
                    }
                    disabled.add(type);
                }
            } else if (name.equals("disabled")) { // NOI18N
                parsingDisabled = true;
            }
        }
            
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (name.equals("disabled")) { // NOI18N
                parsingDisabled = false;
            }

        }
        
        /** No validation - don't read the DTD. Assume importers won't
            require external entities. */
        public InputSource resolveEntity(String pubid, String sysid) {
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }
    }

    /** Read in the SuggestionType registry preferences.
     * @return True iff the registry was completely initialized without error
     */
    private boolean readTypeRegistry() {
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
            writer.write("  <disabled>\n"); // NOI18N
            Iterator it = disabled.iterator();
            while (it.hasNext()) {
                String typeName = (String)it.next();
                writer.write("    <type id=\""); // NOI18N
                writer.write(typeName);
                writer.write("\"/>\n"); // NOI18N
            }
            writer.write("  </disabled>\n"); // NOI18N
            /* TODO
               writer.write("  <noconfirm>\n"); // NOI18N
               writer.write("  </noconfirm>\n"); // NOI18N
            */
            writer.write("</typeregistry>\n"); // NOI18N
            writer.close();
            return true;
        } catch (Exception e) {
            TopManager.getDefault().getErrorManager().notify(
                                           ErrorManager.INFORMATIONAL, e);
        }
        return false;
    }
}
