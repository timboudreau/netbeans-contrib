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

import org.netbeans.api.tasklist.*;
import org.netbeans.modules.tasklist.core.TaskNode;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;
import org.netbeans.spi.tasklist.SuggestionProvider;
import org.netbeans.spi.tasklist.SuggestionContext;
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
 * XXX it plays two not well separated roles:
 * <ul>
 * <li>creates and registers suggestions scanned by independent scanner
 * <li>creates, registers and (moreless) visualizes suggestions
 *     for currently displayed source file
 * </ul>
 * It causes problems as each role needs different settings.
 * Secondly it's not well accomodated to manage more suggestion lists.
 *
 * @author Tor Norbye
 */
final public class SuggestionManagerImpl extends DefaultSuggestionManager
        implements DocumentListener, CaretListener, ComponentListener,
        PropertyChangeListener {

    private final boolean stats = System.getProperty("netbeans.tasklist.stats") != null;

    /** Cache tracking suggestions in recently visited files */
    private SuggestionCache cache = null;

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
     *    Suggestion Types. See the {@link Suggestion} documentation
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
    void notifyViewOpened() {
        if (!prepared) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                provider.notifyPrepare();
            }
            prepared = true;

            // The window system doesn't generate TopComponent.componentShowing
            // when the view is opened (or, it may generate it before the
            // componentOpened call). This will be a no-op in that case,
            // since running=true will already be the case.
            notifyViewShowing();
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
                SuggestionProvider provider = (SuggestionProvider) it.next();
                if ((unfiltered == null) ||
                        (unfiltered == provider)) {
                    provider.notifyRun();
                }
            }
            startActiveSuggestionFetching();
            running = true;
        }
    }

    /** Called when the Suggestions View is hidden */
    void notifyViewHidden() {
        if (running) {
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                if ((unfiltered == null) ||
                        (unfiltered == provider)) {
                    provider.notifyStop();
                }
            }
            stopActiveSuggestionFetching();
            running = false;

        }
    }

    /** Called when the Suggestions View is closed */
    void notifyViewClosed() {
        if (prepared) {
            if (running) {
                notifyViewHidden();
            }
            List providers = getProviders();
            ListIterator it = providers.listIterator();
            while (it.hasNext()) {
                SuggestionProvider provider = (SuggestionProvider) it.next();
                provider.notifyFinish();
            }
            prepared = false;
        }

        // Get rid of suggestion cache, we cannot invalidate its
        // entries properly without keeping a listener
        if (cache != null) {
            cache.flush();
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
                view = new SuggestionsView();
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
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     *
     * @return True iff the given suggestion type is enabled
     */
    public synchronized boolean isEnabled(String id) {
        return ManagerSettings.getDefault().isEnabled(id);
    }

    /**
     * Store whether or not a particular Suggestion type should be
     * enabled.
     * <p>
     *
     * @param id The String id of the Suggestion Type. See the
     *    {@link Suggestion} documentation for how Suggestion Types
     *    are registered and named.
     * @param enabled True iff the suggestion type should be enabled
     * @param dontSave If true, don't save the registry file. Used for batch
     *       updates where we call setEnabled repeatedly and plan to
     *       call writeTypeRegistry() at the end.
     */
    public synchronized void setEnabled(String id, boolean enabled,
                                        boolean dontSave) {
        SuggestionType type = SuggestionTypes.getTypes().getType(id);

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
            provider.notifyPrepare();
            provider.notifyRun();

            if ((document != null) &&
                    (provider instanceof DocumentSuggestionProvider)) {
                SuggestionContext env = SPIHole.createSuggestionContext(dataobject);
                ((DocumentSuggestionProvider) provider).docShown(env);
                ((DocumentSuggestionProvider) provider).rescan(env, currRequest);
            }
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
            if (provider instanceof DocumentSuggestionProvider) {
                SuggestionContext env = SPIHole.createSuggestionContext(dataobject);
                ((DocumentSuggestionProvider) provider).clear(env, currRequest);
                ((DocumentSuggestionProvider) provider).docHidden(env);
            }
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
     *    {@link Suggestion} documentation for how Suggestion Types
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
     *    {@link Suggestion} documentation for how Suggestion Types
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
            List oldList = tasklist.getTasks();

            if (oldList != null) {
                List allTasks = new ArrayList(oldList.size());
                allTasks.addAll(oldList);
                tasklist.clear();
                Collection types = SuggestionTypes.getTypes().getAllTypes();
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
                    register(t.getName(), list, null, tasklist, null, true);
                }
            }
        } else {
            tasklist.clearCategoryTasks();
            List oldList = tasklist.getTasks();
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
                                tasklist, null, true);
                        group.clear();
                    } else {
                        group = new ArrayList(50);
                    }
                    prevType = s.getSType();
                }
                group.add(s);
            }
            if ((group != null) && (group.size() > 0)) {
                register(prevType.getName(), group, null, tasklist, null,
                        true);
            }
        }

        unfiltered = null;

        // Do NOT NOT NOT confuse disabling modules for performance
        // (to achieve filtering) with disabling modules done by the
        // user! In particular, applying a filter and then removing it
        // should not leave previously undisabled module disabled!

        List providers = getProviders();
        SuggestionTypes suggestionTypes = SuggestionTypes.getTypes();
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
        SuggestionImpl s = (SuggestionImpl) TaskNode.getTask(node);
        if (s.getLine() == line) {
            if (erase == null) {
                origIcon = new ArrayList(20);
                erase = new ArrayList(20);
            }
            origIcon.add(s.getIcon());
            //s.setHighlighted(true);
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
                SuggestionImpl s = (SuggestionImpl) it.next();
                Image icon = (Image) itorig.next();
                s.setIcon(icon);
                //s.setHighlighted(false);
            }
        }
        erase = null;
        origIcon = null;


        if (line == null) {
            // Prevent line==null from highlighting all suggestions
            // without an associated line position...
            return;
        }

        SuggestionsView view = SuggestionsView.getCurrentView();
        if (view != null) {
            Node node = view.getEffectiveRoot();
            highlightNode(view, node, line);
        }
    }


    // Consult super for correct javadoc
    public void register(String type, List add, List remove, Object request) {
        //System.out.println("register(" + type + ", " + add +
        //                   ", " + remove + ", " + request + ")");
        if ((type == null) && (add != null) && (remove != null)) {
            // Gotta break up the calls since we cannot handle
            // both adds and removes with mixed types. Do removes,
            // then adds.
            register(type, null, remove, getList(), request, !switchingFiles);
            register(type, add, null, getList(), request, !switchingFiles);
        } else {
            register(type, add, remove, getList(), request, !switchingFiles);
        }
    }

    public synchronized void register(String typeName,
                                      List addList, List removeList,
                                      SuggestionList tasklist,
                                      Object request,
                                      boolean sizeKnown) {
        //System.err.println("register(" + typeName + ", " + addList +
        //                   ", " + removeList + "," + tasklist + ", " +
        //                   request + ", " + sizeKnown + ")");

        if ((request != null) && (request != currRequest)) {
            // This is a result from a previous request - e.g. a long
            // running request which was "cancelled" but too late to
            // stop the provider from computing and providing a result.
            return;
        } else {
            super.register(typeName, addList, removeList, tasklist, sizeKnown);
        }
    }

    /** When true, we're in the process of switching files, so a register
     removal looks like an "unknown" sized list */
    private boolean switchingFiles = false;



    /*
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
     *
     */


    private Document document = null;
    private DataObject dataobject = null;
    private boolean notSaved = false;


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

    /** Set to the request generation when a new file has been shown */
    private volatile Long haveShown = null;

    /** Set to the request generation when a file has been saved */
    private volatile Long haveSaved = null;

    /** Set to the request generation when a file has been edited */
    private volatile Long haveEdited = null;

    /** Current request reference. Used to correlate register()
     * calls with requests sent to rescan()/clear()
     */
    private volatile Long currRequest = new Long(0);

    /** Points to the last completed request. Set to currRequest
     * when rescan() is done.
     */
    private volatile Long finishedRequest = null;

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

    /** List of suggestions restored from the cache that we must delete
     when leaving this document */
    private List docSuggestions = null;

    private void setScanning(boolean scanning) {
        SuggestionList tasklist = getList();
        TaskListView v = tasklist.getView();
        if (v instanceof SuggestionsView) {
            SuggestionsView view = (SuggestionsView) v;
            view.setScanning(scanning);
        }
    }

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
     * <p>
     * This method is called internally by the toolkit and should not be
     * called directly by programs.
     */
    public void rescan(final Document document,
                       final DataObject dataobject) {
        setScanning(true);
        if ((docSuggestions != null) && (docSuggestions.size() > 0)) {
            // Clear out previous items before a rescan
            register(null, null, docSuggestions, getList(), null, true);
            docSuggestions = null;
        }

        /* Scan requests are run in a separate "background" thread.
           However, what happens if the user switches to a different
           tab -while- a scan job is running? If the scan hasn't
           started, the timer is removed, but if the scan is in
           progress, we have to know to discard registered results.
           For that reason, we have a "current request" reference that
           we pass with scan requests, and that scanners will hand
           back with scan results. The reference is an integer.
           When we switch to a new tab, we increment the integer.
           So if we get a registration, with an "old" integer (not the
           current one), we know the results are obsolete.
           We also need to know if the current scan is done (to know
           whether or not we should flush these results into the cache,
           or if scanning must begin from the beginning when we return
           to this file.)   For that reason, we also have a "finished
           request" integer which points to the most recent finished
           request; we only stuff the cache if finished == current.
           We can also use the request flag to bail in the middle of
           iterating over providers in case a new request has arrived.
        */

        final Long origRequest = currRequest;
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                long start = 0, end = 0, total = 0;
                List providers = getDocProviders();
                ListIterator it = providers.listIterator();

                boolean saved = (haveSaved == currRequest);
                boolean edited = (haveEdited == currRequest);
                boolean shown = (haveShown == currRequest);

                while (it.hasNext()) {
                    DocumentSuggestionProvider provider = (DocumentSuggestionProvider) it.next();
                    // Has the request changed? If so, just drop this one
                    if (origRequest != currRequest) {
                        break;
                    }
                    if ((unfiltered == null) || (provider == unfiltered)) {
                        if ((saved && scanOnSave(provider))
                                || (edited && scanOnEdit(provider))
                                || (shown && scanOnShow(provider))) {
                            if (stats) {
                                start = System.currentTimeMillis();
                            }
                            provider.rescan(SPIHole.createSuggestionContext(dataobject), origRequest);
                            if (stats) {
                                end = System.currentTimeMillis();
                                System.out.println("Scan time for provider " + provider.getClass().getName() + " = " + (end - start) + " ms");
                                total += (end - start);
                            }
                        }
                    }
                }
                if ((finishedRequest == null) ||
                        (origRequest.longValue() > finishedRequest.longValue())) {
                    finishedRequest = origRequest;
                }
                if (stats) {
                    System.out.println("TOTAL SCAN TIME = " + total + "\n");
                }
                if (currRequest == finishedRequest) {
                    setScanning(false);
                }
            }
        });
    }

    /**
     * The given document has been "shown"; it is now visible.
     * <p>
     * @param document The document being shown
     * @param dataobject The Data Object for the file being opened
     */
    private void fetchDocumentSuggestions(Document document, DataObject dataobject) {
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider) it.next();
            if (((unfiltered == null) || (provider == unfiltered))
                    && scanOnShow(provider)) {
                provider.docShown(SPIHole.createSuggestionContext(dataobject));
            }
        }
        haveShown = currRequest;
    }

    /**
     * The given document has been "hidden"; it's still open, but
     * the editor containing the document is not visible.
     * <p>
     * @param document The document being hidden
     * @param dataobject The Data Object for the file being opened
     */
    private void cleanDocumentSuggestions(Document document, DataObject dataobject) {
        // Update expansion state before we remove the nodes
        getList().flushExpansion();

        // This is not right - runTimer is telling us whether we have
        // a request pending - (and we should indeed kill the timer
        // if we do) - but we need to know if a RequestProcessor is
        // actually running.
        if (currRequest != finishedRequest) {
            if (cache != null) {
                cache.remove(document);
            }
            // Remove the items we've registered so far... (partial
            // registration) since we're in the middle of a request
            stuffCache(document, dataobject, true);
        } else {
            stuffCache(document, dataobject, false);
        }

        docSuggestions = null;
        List providers = getDocProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            DocumentSuggestionProvider provider = (DocumentSuggestionProvider) it.next();
            if ((unfiltered == null) || (provider == unfiltered)) {
                SuggestionContext env = SPIHole.createSuggestionContext(dataobject);
                provider.clear(env, currRequest);
                provider.docHidden(env);
            }
        }
    }

    /**
     * Grab all the suggestions associated with this document/dataobject
     * and push it into the suggestion cache.
     */
    private void stuffCache(Document document, DataObject dataobject,
                            boolean unregisterOnly) {
        // XXX Performance: if docSuggestions != null, we should be able
        // to just reuse it, since the document must not have been edited!

        SuggestionList tasklist = getList();
        if (tasklist.getTasks() == null) {
            return;
        }
        Iterator it = tasklist.getTasks().iterator();
        List sgs = new ArrayList(tasklist.getTasks().size());
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl) it.next();
            Object seed = s.getSeed();
            // Make sure we don't pick up category nodes here!!!
            if (seed instanceof DocumentSuggestionProvider) {
                sgs.add(s);
            }

            if (s.hasSubtasks()) {
                Iterator sit = s.getSubtasks().iterator();
                while (sit.hasNext()) {
                    s = (SuggestionImpl) sit.next();
                    seed = s.getSeed();
                    if (seed instanceof DocumentSuggestionProvider) {
                        sgs.add(s);
                    }
                }
            }
        }
        if (!unregisterOnly) {
            if (cache == null) {
                cache = new SuggestionCache();
            }
            cache.add(document, dataobject, sgs);
        }

        // Get rid of tasks from list
        if (sgs.size() > 0) {
            register(null, null, sgs, getList(), null, true);
        }
    }

    public void changedUpdate(DocumentEvent e) {
        // Do nothing.
        // Changed update is only called for ATTRIBUTE changes in the
        // document, which I define as not relevant to the Document
        // Suggestion Providers.
    }

    public void insertUpdate(DocumentEvent e) {
        haveEdited = currRequest;
        scheduleRescan(e, false, ManagerSettings.getDefault().getEditScanDelay());

        // If there's a visible marker annotation on the line, clear it now
        clearMarker();
    }

    public void removeUpdate(DocumentEvent e) {
        haveEdited = currRequest;
        scheduleRescan(e, false, ManagerSettings.getDefault().getEditScanDelay());

        // If there's a visible marker annotation on the line, clear it now
        clearMarker();
    }

    /** Get rid of any annotations marking the current task */
    private void clearMarker() {
        SuggestionsView tlv = SuggestionsView.getCurrentView();
        if (tlv != null) {
            tlv.hideTask();
        }
    }

    /** Plan a rescan (meaning: start timer)
     * @param delay If true, don't create a rescan if one isn't already
     * pending, but if one is, delay it.
     * @param docEvent Document edit event. May be null. */
    private void scheduleRescan(final DocumentEvent docEvent, boolean delay,
                                int scanDelay) {
        if (wait) {
            if (docEvent != null) {
                // Caret updates shouldn't clear my docEvent
                waitEvent = docEvent;
            }
            return;
        }

        // This is just a delayer (e.g. for caret motion) - if there isn't
        // already a pending timeout, we're done. Caret motion shouldn't
        // -cause- a rescan, but if one is already planned, we want to delay
        // it.
        if (delay && (runTimer == null)) {
            return;
        }

        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
        }
        currDelay = scanDelay;
        runTimer = new Timer(currDelay,
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        runTimer = null;
                        if (!wait) {
                            rescan(document, dataobject);
                        }
                    }
                });
        runTimer.setRepeats(false);
        runTimer.setCoalesce(true);
        runTimer.start();
    }

    /** Most recent delay */
    private int currDelay = 500;

    private DocumentEvent waitEvent = null;
    private boolean wait = false;

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
        boolean wasWaiting = this.wait;
        this.wait = wait;
        if (!wait && wasWaiting && (waitEvent != null)) {
            scheduleRescan(waitEvent, false, currDelay);
        }
    }


    /** Listener on <code>DataObject.Registry</code> for save operations. */
    private static DORegistryListener rl;

    /**
     * Start scanning for source items.
     * Attaches top component registry and data object
     * registry listeners to monitor currently edited file.
     */
    private void startActiveSuggestionFetching() {

        // must be removed in docStop
        TopComponent.getRegistry().addPropertyChangeListener(this);

        // must be removed in docStop
        if (rl == null) {
            rl = new DORegistryListener();
            DataObject.getRegistry().addChangeListener(rl);
        }

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
        WindowManager manager = WindowManager.getDefault();
        manager.addPropertyChangeListener(this);
        Workspace workspace = WindowManager.getDefault().
            getCurrentWorkspace();
        workspace.addPropertyChangeListener(this);
        */

        if (pendingScan) {
            return;
        }
        pendingScan = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // docStop() might have happened
                // in the mean time - make sure we don't do a
                // findCurrentFile(true) when we're not supposed to
                // be processing views
                if (running) {
                    findCurrentFile(false);
                }
                pendingScan = false;
            }
        });

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
     * Queries passive providers for suggestions. Monitors
     * actual document modification state using DocumentListener
     * and CaretListener. Actual topcomponent is guarded
     * by attached ComponentListener.
     *
     * @param delayed
     */
    private void findCurrentFile(boolean delayed) {
        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(this);
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(this);
            switchingFiles = true;
            cleanDocumentSuggestions(document, dataobject);
            switchingFiles = false;
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
        Mode mode = workspace.findMode(EditorSupport.EDITOR_MODE);
        if (mode == null) {
            // The editor window was probablyjust closed
            return;
        }
        TopComponent[] tcs = mode.getTopComponents();
        for (int j = 0; j < tcs.length; j++) {
            TopComponent tc = tcs[j];
            /*
            if (tc instanceof EditorSupport.Editor) {
                // Found the source editor...
                if (tc.isShowing()) {
		    current = tc;
                    break;
                }
            } else */ if (tc instanceof CloneableEditor) {
                // Found the source editor...
                if (tc.isShowing()) {
                    current = tc;
                    break;
                }
            }
        }
        if (current == null) {
            // The last editor-support window in the editor was probably
            // just closed - or was not on top
            return;
        }

        // Listen for changes on this component so we know when
        // it's replaced by something else
        //System.err.println("Add component listener to " + tcToDO(current));
        current.addComponentListener(this);

        Node[] nodes = current.getActivatedNodes();

        if ((nodes == null) || (nodes.length != 1)) {
/*
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                err.log(
                  "Unexpected editor component activated nodes " + // NOI18N
                  " contents: " + nodes); // NOI18N
            }
            */
            return;
        }

        Node node = nodes[0];

        final DataObject dao = (DataObject) node.getCookie(DataObject.class);
        //err.log("Considering data object " + dao);
        if (dao == null) {
            return;
        }

        if (!dao.isValid()) {
            //err.log("The data object is not valid!");
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

        final EditorCookie edit = (EditorCookie) dao.getCookie(EditorCookie.class);
        if (edit == null) {
            //err.log("No editor cookie - not doing anything");
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
            //err.log("No document handle...");
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
        notSaved = dao.isModified();

        // TODO: Is MAX_VALUE even feasible here? There's no greater/lessthan
        // comparison, so wrapping around will work just fine, but I may
        // have to check manually and do it myself in case some kind
        // of overflow exception is thrown
        //  Wait, I'm doing a comparison now - look for currRequest.longValue
        currRequest = new Long(currRequest.intValue() + 1);

        fetchDocumentSuggestions(doc, dataobject);
        addCaretListeners();

        // XXX Use scheduleRescan instead? (but then I have to call docShown instead of rescan;
        //haveShown = currRequest;
        //scheduleRescan(null, false, showScanDelay);

        if (cache != null) {
            // TODO check scanOnShow too! (when we have scanOnOpen
            // as default instead of scanOnShow as is the case now.
            // The semantics of the flag need to change before we
            // check it here; it's always true. Make it user selectable.)
            docSuggestions = cache.lookup(document);
            if (docSuggestions != null) {
                register(null, docSuggestions, null, getList(), null, true);
                // TODO Consider putting the above on a runtimer - but
                // a much shorter runtimer (0.1 seconds or something like
                // that) such that the editor gets a chance to draw itself
                // etc.

                // Also wipe out the cache items since we will replace them
                // when docHidden is called, or when docEdited is called,
                // etc.
                //cache.remove(document);

                // Remember that we're done "scanning"
                finishedRequest = currRequest;
                return;
            }
        }

        if (delayed) {
            runTimer = new Timer(ManagerSettings.getDefault().getShowScanDelay(),
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            runTimer = null;
/*
                             if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                                 err.log("Timer expired - time to scan " + dao);
                             }
                             */
                            rescan(doc, dataobject);
                        }
                    });
            runTimer.setRepeats(false);
            runTimer.setCoalesce(true);
            runTimer.start();
        } else {
            // Do it right away
            rescan(doc, dataobject);
        }
    }

    JEditorPane[] editors = null;

    private void addCaretListeners() {
        EditorCookie edit = (EditorCookie) dataobject.getCookie(EditorCookie.class);
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

    public void componentShown(ComponentEvent e) {
        // Don't care
    }

    public void componentHidden(ComponentEvent e) {
        componentsChanged();
    }

    public void componentResized(ComponentEvent e) {
        // Don't care
    }

    public void componentMoved(ComponentEvent e) {
        // Don't care
    }


    boolean pendingScan = false;

    /** The set of visible top components changed */
    private void componentsChanged() {
        // We may receive "changed events" from different sources:
        // componentHidden (which is the only source which tells us
        // when you've switched between two open tabs) and
        // TopComponent.registry's propertyChange on PROP_OPENED
        // (which is the only source telling us about tabs closing).

        // However, there is some overlap - when you open a new
        // tab, we get notified by both. So coalesce these events by
        // enquing a change lookup on the next iteration through the
        // event loop; if a second notification comes in during the
        // same event processing iterationh it's simply discarded.

        if (pendingScan) {
            return;
        }
        pendingScan = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // docStop() might have happened
                // in the mean time - make sure we don't do a
                // findCurrentFile(true) when we're not supposed to
                // be processing views
                if (running) {
                    findCurrentFile(true);
                }
                pendingScan = false;
            }
        });
    }

    /**
     * Stop scanning for source items, deregistering
     * environment listeners.
     */
    private void stopActiveSuggestionFetching() {
        if (runTimer != null) {
            runTimer.stop();
            runTimer = null;
        }

        TopComponent.getRegistry().removePropertyChangeListener(this);

        if (rl != null) {
            DataObject.getRegistry().removeChangeListener(rl);
            rl = null;
        }

        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(this);
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(this);
            // NOTE: we do NOT null it out since we still need to
            // see if the document is unchanged
        }
        if (editors != null) {
            removeCaretListeners();
        }

        switchingFiles = true;
        cleanDocumentSuggestions(document, dataobject);
        switchingFiles = false;
        document = null;
    }

    /** Timer which keeps track of outstanding scan requests; we don't
     scan briefly selected files */
    private Timer runTimer;


    /** Reacts to changes */
    public void propertyChange(PropertyChangeEvent ev) {
        String prop = ev.getPropertyName();
        if (prop.equals(TopComponent.Registry.PROP_OPENED)) {
            componentsChanged();
        }
    }

    int prevLineNo = -1;

    /** Moving the cursor position should cause a delay in document scanning,
     * but not trigger a new update */
    public void caretUpdate(CaretEvent caretEvent) {
        scheduleRescan(null, true, currDelay);

        // Check to see if I have any existing errors on this line - and if so,
        // highlight them.
        if (document instanceof StyledDocument) {
            int offset = caretEvent.getDot();
            int lineno = NbDocument.findLineNumber((StyledDocument) document, offset);
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
            line = TLUtils.getLineByNumber(dataobject, lineno + 1);
            /*
            try {
                LineCookie lc = (LineCookie)dataobject.getCookie(LineCookie.class);
                if (lc != null) {
                    Line.Set ls = lc.getLineSet();
                    if (ls != null) {
                        line = ls.getCurrent(lineno);
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
            */
            if (line != null) {
                setCursorLine(line);
            }
        }
    }

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

        Iterator allIt = SuggestionTypes.getTypes().getAllTypes().iterator();
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

    /**
     * Listener for DataObject.Registry changes.
     *
     * This class listens for modify-changes of dataobjects such that
     * it can notify files of Save operations.
     */
    class DORegistryListener implements javax.swing.event.ChangeListener {
        public void stateChanged(ChangeEvent e) {
            /* Not sure what the source is, but it isn't dataobject
                 and the javadoc doesn't say anything specific, so
                 I guess I can't rely on that as a filter
            if (e.getSource() != dataobject) {
                // If you reinstate this in some way, make sure it
                // works for Save ALL as well!!!
                return;
            }
            */
            Set mods = DataObject.getRegistry().getModifiedSet();
            boolean wasModified = notSaved;
            notSaved = mods.contains(dataobject);
            if (notSaved != wasModified) {
                if (!notSaved) {
                    haveSaved = currRequest;
                    scheduleRescan(null, false, ManagerSettings.getDefault().getSaveScanDelay());
                }
            }
        }
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
