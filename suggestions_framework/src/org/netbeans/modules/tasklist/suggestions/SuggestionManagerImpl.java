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

package org.netbeans.modules.tasklist.suggestions;

import java.awt.Image;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.netbeans.modules.tasklist.core.TaskListView;
import org.netbeans.modules.tasklist.core.TLUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JEditorPane;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import org.openide.util.Lookup;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionContext;
import org.netbeans.apihole.tasklist.SPIHole;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
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
 * <p>
 * It's (not yet well implemented) bridge between providers and their
 * clients.
 *
 * @author Tor Norbye
 */
final public class SuggestionManagerImpl extends DefaultSuggestionManager {

    private final boolean stats = System.getProperty("netbeans.tasklist.stats") != null;


    /** Create nnew SuggestionManagerImpl.  Public because it needs to be
     * instantiated via lookup, but DON'T go creating instances of this class!
     */
    public SuggestionManagerImpl() {
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
     * {@link SuggestionView} for details.
     *
     * @param id The String id of the Suggestion Type we're
     *    interested in. You may pass null to ask about any/all
     *    Suggestion Types. See the {@link org.netbeans.modules.tasklist.client.Suggestion} documentation
     for how Suggestion Types are registered and named.
     *
     * @return True iff the suggestions are observed by the user.
     */
    public boolean isObserved(String id) {
        TopComponent.Registry registry = TopComponent.getRegistry();
        Set opened = registry.getOpened();
        Iterator it = opened.iterator();
        while (it.hasNext()) {
            TopComponent next = (TopComponent) it.next();
            if (next instanceof SuggestionView) {
                SuggestionView view = (SuggestionView) next;
                if (view.isObserved(id)) return true;
            }
        }
        return false;
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


    // messages got from SuggestionView class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // it's the second role, live list of suggestions

    /** Called when the Suggestions View is opened */
    void dispatchPrepare() {
        if (!prepared) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                try {
                    provider.notifyPrepare();
                } catch (RuntimeException e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                } catch (ThreadDeath e) {
                    throw e;
                } catch (Error e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
            }
            prepared = true;
        }
    }

    /** Called when the Suggestions View is made visible
     *
     * @todo If a filter was in effect when we left the window, we should
     * NOT notify the filtered-out SuggestionProviders!
     */
    void dispatchRun() {
        if (!running) {
            if (!prepared) {
                dispatchPrepare();
            }
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                if ((unfiltered == null) ||
                        (unfiltered == provider)) {
                    try {
                        provider.notifyRun();
                    } catch (RuntimeException e) {
                        ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    } catch (ThreadDeath e) {
                        throw e;
                    } catch (Error e) {
                        ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }

                }
            }
            running = true;
        }
    }

    /** Called when the Suggestions View is hidden */
    void dispatchStop() {
        if (running) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                if ((unfiltered == null) ||
                        (unfiltered == provider)) {
                    try {
                        provider.notifyStop();
                    } catch (RuntimeException e) {
                        ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    } catch (ThreadDeath e) {
                        throw e;
                    } catch (Error e) {
                        ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                        ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    }

                }
            }
            running = false;
        }
    }

    /** Called when the Suggestions View is closed */
    void notifyViewClosed() {
        if (prepared) {
            if (running) {
                dispatchStop();
            }
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                provider.notifyFinish();
            }
            prepared = false;
        }

    }


    // there is only one SuggestionManager instance
    private static SuggestionList list = null;

    /**
     * Return the live TaskList that we're managing
     * XXX the list it is retrieved from last
     * active suggections view. However there could
     * be several lists visible. Introduce ProxyList
     * delegating to set of lists.
     */
    static SuggestionList getList() {

        TopComponent.Registry registry = TopComponent.getRegistry();
        Set opened = registry.getOpened();
        Iterator it = opened.iterator();
        while (it.hasNext()) {
            TopComponent next = (TopComponent) it.next();
            if (next instanceof SuggestionView) {
                SuggestionView view = (SuggestionView) next;
                SuggestionList model = view.getSuggestionsModel();
                if (model != null) return model;
            }
        }

        //XXX Original code that should not be reached, well docHidden() gets here
        //assert false : "Original code that should not be reached";
        if (list == null) {
            TaskListView view =
                    TaskListView.getTaskListView(SuggestionsView.CATEGORY); // NOI18N
            if (view == null) {
                view = SuggestionsView.createSuggestionsView();
                // Let user open the window
                //   TODO Find a way to manage the tasklist so that I -don't-
                //   have to create it now; only when it's opened by the user!
                //view.showInMode();
            }
            list = (SuggestionList) view.getList();
        }
        return list;
    }


    /**
     * Return true iff the type of suggestion indicated by the
     * id argument is enabled. By default, all suggestion types
     * are enabled, but users can disable suggestion types they're
     * not interested in.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link org.netbeans.modules.tasklist.client.Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type is enabled
     */
    public synchronized boolean isEnabled(String id) {
        ManagerSettings settings = ManagerSettings.getDefault();
        return settings != null ? settings.isEnabled(id) : true;
    }

    /**
     * Store whether or not a particular Suggestion type should be
     * enabled.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link org.netbeans.modules.tasklist.client.Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     * @param enabled True iff the suggestion type should be enabled
     * @param dontSave If true, don't save the registry file. Used for batch
     *       updates where we call setEnabled repeatedly and plan to
     *       call writeTypeRegistry() at the end.
     */
    public synchronized void setEnabled(String id, boolean enabled,
                                        boolean dontSave) {
        SuggestionType type = SuggestionTypes.getDefault().getType(id);

        ManagerSettings.getDefault().setEnabled(id, enabled);

        // Enable/disable providers "live"
        toggleProvider(type, enabled);

        if (!dontSave) {
            writeTypeRegistry();
        }

    }

    /** Enable/disable a provider identified by a type. The provider
     * will only be disabled if all of its OTHER types are disabled. */
    private void toggleProvider(SuggestionType type, boolean enable) {
        // Update the suggestions list: when disabling, rip out suggestions
        // of the same type, and when enabling, trigger a recompute in case
        // we have pending suggestions
        SuggestionProvider provider = getProvider(type);
        if (provider == null) {
            // This seems to happen when modules are uninstalled for example
            return;
        }
        // XXX Note - there could be multiple providers for a type!
        // You really should iterate here!!!
        toggleProvider(provider, type, enable, false);
    }

    /** Same as toggleProvider, but the allTypes parameter allows you
     * to specify that ALL the types should be enabled/disabled */
    private void toggleProvider(SuggestionProvider provider,
                                SuggestionType type, boolean enable,
                                boolean allTypes) {
        if (enable) {
            // XXX This isn't exactly right. Make sure we do the
            // right life cycle for each provider.
            // especially handle DocumetSuggestionProviders
            provider.notifyPrepare();
            provider.notifyRun();
        } else {
            if (!allTypes) {
                String typeNames[] = provider.getTypes();
                for (int j = 0; j < typeNames.length; j++) {
                    if (!typeNames[j].equals(type.getName())) {
                        if (isEnabled(typeNames[j])) {
                            // Found other enabled provider - bail
                            getList().removeCategory(type);
                            return;
                        }
                    }
                }
            }

            // Remove suggestions of this type
            // XXX This isn't exactly right. Make sure we do the
            // right life cycle for each provider.
            // especially handle DocumetSuggestionProviders
            provider.notifyStop();
            provider.notifyFinish();

            String typeNames[] = provider.getTypes();
            for (int j = 0; j < typeNames.length; j++) {
                if (isEnabled(typeNames[j])) {
                    // Found other enabled provider - bail
                    getList().removeCategory(type);
                    return;
                }
            }
        }
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
     *    {@link org.netbeans.modules.tasklist.client.Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type should have a
     *    confirmation dialog.
     */
    public synchronized boolean isConfirm(SuggestionType type) {
        return ManagerSettings.getDefault().isConfirm(type);
    }


    /**
     * Store whether or not a particular Suggestion type should produce
     * a confirmation popup.
     * <p>
     *
     * @param type The Suggestion Type. See the
     *    {@link org.netbeans.modules.tasklist.client.Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     * @param write Write to disk the update iff true.
     * @param confirm True iff the suggestion type should have a confirmation
     *     dialog
     */
    synchronized void setConfirm(SuggestionType type, boolean confirm, boolean write) {
        ManagerSettings.getDefault().setConfirm(type, confirm);
        if (write) {
            writeTypeRegistry();
        }
    }

    /** Notify the SuggestionManager that a particular category filter
     * is in place.
     *
     * @param type SuggestionType to be shown, or
     *     null if the view should not be filtered (e.g. show all)
     */
    void notifyFiltered(SuggestionList tasklist, SuggestionType type) {
        SuggestionType prevFilterType = getUnfilteredType();
        setUnfilteredType(type);

        if (type != null) {
            // "Flatten" the list when I'm filtering so that I don't show
            // category nodes!
            List oldList = tasklist.getRoot().getSubtasks();

            if (oldList != null) {
                List allTasks = new ArrayList(oldList.size());
                allTasks.addAll(oldList);
                tasklist.clear();
                Collection types = SuggestionTypes.getDefault().getAllTypes();
                Iterator it = types.iterator();
                while (it.hasNext()) {
                    SuggestionType t = (SuggestionType) it.next();
                    ArrayList list = new ArrayList(100);
                    Iterator all = allTasks.iterator();
                    SuggestionImpl category =
                            tasklist.getCategoryTask(t, false);
                    tasklist.removeCategory(category, true);
                    while (all.hasNext()) {
                        SuggestionImpl sg = (SuggestionImpl) all.next();
                        if (sg.getSType() == t) {
                            if ((sg == category) &&
                                    sg.hasSubtasks()) { // category node
                                list.addAll(sg.getSubtasks());
                            } else {
                                list.add(sg);
                            }
                        }
                    }
                    // XXX what a hell means these register over here
                    register(t.getName(), list, null, tasklist, true);
                }
            }
        } else {
            tasklist.clearCategoryTasks();
            List oldList = tasklist.getRoot().getSubtasks();
            List suggestions = new ArrayList();
            if (oldList != null)
                suggestions.addAll(oldList);
            tasklist.clear();
            Iterator it = suggestions.iterator();
            List group = null;
            SuggestionType prevType = null;
            while (it.hasNext()) {
                SuggestionImpl s = (SuggestionImpl) it.next();
                if (s.getSType() != prevType) {
                    if (group != null) {
                        register(prevType.getName(), group, null,
                                tasklist, true);
                        group.clear();
                    } else {
                        group = new ArrayList(50);
                    }
                    prevType = s.getSType();
                }
                if (group == null) {
                    group = new ArrayList(50);
                }
                group.add(s);
            }
            if ((group != null) && (group.size() > 0)) {
                register(prevType.getName(), group, null, tasklist, true);
            }
        }

        unfiltered = null;

        // Do NOT NOT NOT confuse disabling modules for performance
        // (to achieve filtering) with disabling modules done by the
        // user! In particular, applying a filter and then removing it
        // should not leave previously undisabled module disabled!

        List providers = getProviders();
        SuggestionTypes suggestionTypes = SuggestionTypes.getDefault();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            SuggestionProvider provider = (SuggestionProvider) it.next();

            // XXX This will process diabled providers/types as well!
            String typeNames[] = provider.getTypes();
            if (type != null) {
                // We're adding a filter: gotta disable all providers
                // that do not provide the given type
                boolean enabled = false;
                for (int j = 0; j < typeNames.length; j++) {
                    SuggestionType tp = suggestionTypes.getType(typeNames[j]);
                    if (tp == type) {
                        enabled = true;
                    }
                }
                if (enabled) {
                    // The provider should be enabled - it provides info
                    // for this type
                    unfiltered = provider;
                    if (prevFilterType != null) {
                        SuggestionType sg = suggestionTypes.getType(typeNames[0]);
                        toggleProvider(provider, sg, true, true);
                    } // else:
                    // The provider is already enabled - we're coming
                    // from an unfiltered view (and disabled providers
                    // in an unfiltered view shouldn't be available as
                    // filter categories)
                } else {
                    SuggestionType sg = suggestionTypes.getType(typeNames[0]);
                    toggleProvider(provider, sg, false, true);
                }
            } else {
                // We're removing a filter: enable all providers
                // (that are not already disabled by the user); and
                // don't enable a module that's already enabled (the
                // previously filtered type - prevFilterType)
                boolean isPrev = false;
                for (int j = 0; j < typeNames.length; j++) {
                    SuggestionType tp = suggestionTypes.getType(typeNames[j]);
                    if (prevFilterType == tp) {
                        // This provider is responsible for the previous
                        // filter - nothing to do (already enabled)
                        // bail
                        isPrev = true;
                        break;
                    }
                }
                if (isPrev) {
                    continue;
                }

                SuggestionType sg = suggestionTypes.getType(typeNames[0]);
                toggleProvider(provider, sg, true, true);
            }

        }
    }



    boolean isExpandedType(SuggestionType type) {
        return ManagerSettings.getDefault().isExpandedType(type);
    }


    void setExpandedType(SuggestionType type, boolean expanded) {
        ManagerSettings.getDefault().setExpandedType(type, expanded);
    }


    void scheduleNodeExpansion(SuggestionsView view,
                               SuggestionImpl target) {
        view.scheduleNodeExpansion(target, 0);
    }





    // Consult super for correct javadoc
    public void register(String type, List add, List remove, Object request) {
        SPIMonitor.log("  Response on " + request + " " + type + " add: " + ((add != null) ? ""+add.size() : "null") + " remove:" + ((remove != null) ? ""+remove.size() : "null"));
        //System.out.println("register(" + type + ", " + add +
        //                   ", " + remove + ", " + request + ")");
        SuggestionList target = getListByRequest(request);
        if (target == null) {
            // This is a result from a previous request - e.g. a long
            // running request which was "cancelled" but too late to
            // stop the provider from computing and providing a result.
            //System.err.println("[MGR] ignoring request: " + request + " remove:" + remove);
            return;
        }

        if ((type == null) && (add != null) && (remove != null)) {
            // Gotta break up the calls since we cannot handle
            // both adds and removes with mixed types. Do removes,
            // then adds.
            register(type, null, remove, target, !switchingFiles);
            register(type, add, null, target, !switchingFiles);
        } else {
            register(type, add, remove, target, !switchingFiles);
        }
    }

    private SuggestionList getListByRequest(Object request) {

        SuggestionsBroker broker = SuggestionsBroker.getDefault();

        if ((request != null) /* once it was dispatched in must be completed regardless && (request == broker.getCurrRequest())*/) {
            return broker.getSuggestionsList();
//        } else if (request != null) {
//            return null;
        } else {
            return getList();
        }
    }

    /** When true, we're in the process of switching files, so a register
     removal looks like an "unknown" sized list */
    private boolean switchingFiles = false;


    /** The given document has been opened
     * <p>
     * @param document The document being opened
     * @param dataobject The Data Object for the file being opened
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
/* Not yet called from anywhere. Does anyone need it?
    private void docOpened(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider)it.next();
            // if ((unfiltered == null) || (provider == unfiltered))
            provider.docOpened(document, dataobject);
        }
    }
*/


    /** Return true iff the given provider should rescan when a file is shown */
    private boolean scanOnShow(DocumentSuggestionProvider provider) {
        // For now, just use "global" flag (shared for all providers)
        return ManagerSettings.getDefault().isScanOnShow();
    }

    /** Return true iff the given provider should rescan when a file is saved */
    private boolean scanOnSave(DocumentSuggestionProvider provider) {
        // For now, just use "global" flag (shared for all providers)
        return ManagerSettings.getDefault().isScanOnSave();
    }

    /** Return true iff the given provider should rescan when a file is edited */
    private boolean scanOnEdit(DocumentSuggestionProvider provider) {
        // For now, just use "global" flag (shared for all providers)
        return ManagerSettings.getDefault().isScanOnEdit();
    }

    /**
     * The given document has been saved - and a short time period
     * has passed.
     * <p>
     * @param document The document being saved.
     * @param dataobject The Data Object for the file being saved
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
/*
    public void docSavedStable(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider =
                (DocumentSuggestionProvider)it.next();
            if ((unfiltered == null) || (provider == unfiltered)) {
                provider.docSaved(document, dataobject);
            }
        }
        rescan(document, dataobject);
    }
*/




    /**
     * The given document has been edited or saved, and a time interval
     * (by default around 2 seconds I think) has passed without any
     * further edits or saves.
     * <p>
     * Update your Suggestions as necessary. This may mean removing
     * previously registered Suggestions, or editing existing ones,
     * or adding new ones, depending on the current contents of the
     * document.
     * <p>
     * @param document The document being edited
     * @param dataobject The Data Object for the file being opened
     */
    void dispatchRescan(Document document, DataObject dataobject, final Object request) {

        assert request != null : "Precondition for SuggestionsBroker.getCurrRequest()";  // NOI18N

        long start = 0, end = 0, total = 0;
        List providers = getDocProviders();
        Iterator it = providers.iterator();

        SuggestionContext ctx = SPIHole.createSuggestionContext(dataobject);
        while (it.hasNext()) {
            if (SuggestionsBroker.getDefault().getCurrRequest() != request) return;

            DocumentSuggestionProvider provider = (DocumentSuggestionProvider) it.next();
            if ((unfiltered == null) || (provider == unfiltered)) {
                if (stats) {
                    start = System.currentTimeMillis();
                }
                try {
                    SPIMonitor.log("Enter rescan " + request + " " + provider.getClass());
                    provider.rescan(ctx, request);
                } catch (RuntimeException e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                } catch (ThreadDeath e) {
                    throw e;
                } catch (Error e) {
                    ErrorManager.getDefault().annotate(e, "Skipping faulty provider (" + provider + ").");  // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                } finally {
                    SPIMonitor.log("Leave rescan " + request + " " + provider.getClass());
                }
                if (stats) {
                    end = System.currentTimeMillis();
                    System.out.println("Scan time for provider " + provider.getClass().getName() + " = " + (end - start) + " ms");
                    total += (end - start);
                }
            }
        }
        if (stats) {
            System.out.println("TOTAL SCAN TIME = " + total + "\n");
        }
    }

    /** Tell the DocumentListener to wait updating itself indefinitely
     * (until it's told not to wait again). When it's told to stop waiting,
     * itself, it will call rescan if that was called and cancelled
     * at some point during the wait
     * <p>
     * Typically, you should NOT call this method. It's intended for use
     * by the Suggestions framework to allow for example the modal fix
     * dialog which provides confirmations to suspend document updates until
     * all fix actions have been processed.
     */
    void setFixing(boolean wait) {
        // dispatch to hardcoded listeners
        SuggestionsBroker.getDefault().setFixing(wait);
    }


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






    /** Enable/disable/confirm the given collections of SuggestionTypes */
    void editTypes(List enabled, List disabled, List confirmation) {
        Iterator it = enabled.iterator();
        while (it.hasNext()) {
            SuggestionType type = (SuggestionType) it.next();
            if (!isEnabled(type.getName())) {
                setEnabled(type.getName(), true, true);
            }
        }

        it = disabled.iterator();
        while (it.hasNext()) {
            SuggestionType type = (SuggestionType) it.next();
            if (isEnabled(type.getName())) {
                setEnabled(type.getName(), false, true);
            }
        }

        Iterator allIt = SuggestionTypes.getDefault().getAllTypes().iterator();
        while (allIt.hasNext()) {
            SuggestionType t = (SuggestionType) allIt.next();
            it = confirmation.iterator();
            boolean found = false;
            while (it.hasNext()) {
                SuggestionType type = (SuggestionType) it.next();
                if (type == t) {
                    found = true;
                    break;
                }
            }
            setConfirm(t, !found, false);
        }

        // Flush changes to disk
        writeTypeRegistry();
    }

    private void writeTypeRegistry() {
        ManagerSettings.getDefault().store();
    }


    // delegate to providers registry ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private List getProviders() {
        return SuggestionProviders.getDefault().getProviders();
    }

    private List getDocProviders() {
        return SuggestionProviders.getDefault().getDocProviders();
    }

    private SuggestionProvider getProvider(SuggestionType type) {
        return SuggestionProviders.getDefault().getProvider(type);
    }

}
