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

import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.openide.windows.WindowManager;
import org.openide.windows.Mode;
import org.openide.loaders.DataObject;
import org.openide.text.*;
import org.openide.nodes.Node;
import org.openide.cookies.EditorCookie;
import org.openide.util.RequestProcessor;
import org.openide.filesystems.FileObject;
import org.netbeans.modules.tasklist.suggestions.settings.ManagerSettings;
import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.providers.DocumentSuggestionProvider;
import org.netbeans.modules.tasklist.providers.SuggestionProvider;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Broker actively monitors environment and provides
 * suggestion lists (jobs) for:
 * <ul>
 *   <li>{@link #startBroker}, currently opened document (list managed by SuggestionsManager, see getListByRequest)
 *   <li>{@link #startAllOpenedBroker}, all opened documents (list managed by this class)
 *       XXX it does not catch changes/suggestions made by providers on their own
 * </ul>
 *
 * @author Petr Kuzel
 * @author Tor Norbye (transitive from sacked SuggestionManagerImpl)
 */
public final class SuggestionsBroker {

    private static SuggestionsBroker instance;

    private SuggestionList list;

    private int clientCount = 0;

    private SuggestionManagerImpl manager = (SuggestionManagerImpl) SuggestionManagerImpl.getDefault();

    // hook for unit tests
    Env env = new Env();

    // FileObject, Set<Suggestion>
    private Map openedFilesSuggestionsMap;

    private int allOpenedClientsCount = 0;

    /** all opened mode is a client of currently opened  job (this field). */
    private Job allOpenedJob;

    private SuggestionList allOpenedList;

    /** Holds last fileobject that has been opened (and therefore should appear in "allOpened". */
    private FileObject lastOpenedFileObject;

    private static final Logger LOGGER = TLUtils.getLogger(SuggestionsBroker.class);

    private List acceptors = new ArrayList(5);

    private final ProviderAcceptor compound = new ProviderAcceptor() {
        public boolean accept(SuggestionProvider provider) {
            Iterator it = acceptors.iterator();
            while (it.hasNext()) {
                ProviderAcceptor acceptor = (ProviderAcceptor) it.next();
                if (acceptor.accept(provider)) return true;
            }
            return false;
        }
    };

    private SuggestionsBroker() {
    }

    public static SuggestionsBroker getDefault() {
        if (instance == null) {
            instance = new SuggestionsBroker();
        }
        return instance;
    }


    public Job startBroker(ProviderAcceptor acceptor) {
        clientCount++;
        if (clientCount == 1) {
            manager.dispatchRun();
            startActiveSuggestionFetching();
        }
        return new Job(acceptor);
    }

    /** Handle for suggestions foe active document. */
    public class Job {

        private boolean stopped = false;
        private final ProviderAcceptor acceptor;

        private Job(ProviderAcceptor acceptor) {
            this.acceptor = acceptor;
            acceptors.add(acceptor);
        }

        public void stopBroker() {
            if (stopped) return;
            stopped = true;
            acceptors.remove(acceptor);

            clientCount--;
            if (clientCount == 0) {
                stopActiveSuggestionFetching();
                // Get rid of suggestion cache, we cannot invalidate its
                // entries properly without keeping a listener
                if (cache != null) {
                    cache.flush();
                }
                list = null;
                instance = null;
            }
        }

        /**
         * Returns live list containing current suggestions.
         * List is made live by invoking {@link SuggestionsBroker#startBroker} and
         * is abandoned ance last client calls {@link Job#stopBroker}.
         * <p>
         * It's global list so listeners must be carefully unregistered
         * unfortunatelly it's rather complex because list
         * is typically passed to  other clasess (TaskChildren).
         * Hopefully you can WeakListener.
         */
        public SuggestionList getSuggestionsList() {
            return getCurrentSuggestionsList();
        }

    }

    /** Starts monitoring all opened files */
    public AllOpenedJob startAllOpenedBroker(ProviderAcceptor acceptor) {
        allOpenedClientsCount++;
        if (allOpenedClientsCount == 1) {
            openedFilesSuggestionsMap = new HashMap();
            allOpenedJob = startBroker(acceptor);

            TopComponent[] documents = SuggestionsScanner.openedTopComponents();
            SuggestionsScanner scanner = SuggestionsScanner.getDefault();
            List allSuggestions = new LinkedList();
            for (int i = 0; i<documents.length; i++) {
                DataObject dobj = extractDataObject(documents[i]);
                if (dobj == null) continue;
                FileObject fileObject = dobj.getPrimaryFile();
                List suggestions = scanner.scanTopComponent(documents[i], compound);
                openedFilesSuggestionsMap.put(fileObject, suggestions);
                allSuggestions.addAll(suggestions);
            }
            getAllOpenedSuggestionList().addRemove(allSuggestions, null, true, null, null);
        }
        return new AllOpenedJob(acceptor);
    }

    /** Handle to suggestions for all opened files request. */
    public class AllOpenedJob {

        private boolean stopped = false;
        private final ProviderAcceptor acceptor;

        private AllOpenedJob(ProviderAcceptor acceptor) {
            this.acceptor = acceptor;
            acceptors.add(acceptor);
        }

        public SuggestionList getSuggestionList() {
            return getAllOpenedSuggestionList();
        }

        public void stopBroker() {

            if (stopped) return;
            stopped = true;
            acceptors.remove(acceptor);

            allOpenedClientsCount--;
            if (allOpenedClientsCount == 0) {
                allOpenedJob.stopBroker();
                openedFilesSuggestionsMap = null;
            }
        }
    }


    /** Exposes current sugegstion list to Suggestions view constructor. TODO Why? getJob.start() cannot be followed by getList in contructor*/
    final SuggestionList getSuggestionsList() {
        return getCurrentSuggestionsList();
    }

    private SuggestionList getCurrentSuggestionsList() {
        if (list == null) {
            list = new SuggestionList();
        }
        return list;
    }

    private SuggestionList getAllOpenedSuggestionList() {
        if (allOpenedList == null) {
            allOpenedList = new SuggestionList();
        }
        return allOpenedList;
    }

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

    /** Current request reference. Used to correlate register()
     * calls with requests sent to rescan()/clear()
     */
    private volatile Long currRequest = new Long(0);

    /** Points to the last completed request. Set to currRequest
     * when rescan() is done.
     */
    private volatile Comparable finishedRequest = null;

    final Object getCurrRequest() {
        return currRequest;
    }

    /**
     * Start scanning for source items.
     * Attaches top component registry and data object
     * registry listeners to monitor currently edited file.
     */
    private void startActiveSuggestionFetching() {

        LOGGER.info("Starting active suggestions fetching....");  // NOI18N

        // must be removed in docStop
        WindowSystemMonitor monitor = getWindowSystemMonitor();
        monitor.enableOpenCloseEvents();
        env.addTCRegistryListener(monitor);
        env.addDORegistryListener(getDataSystemMonitor());

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

        doRescanInAWT(false);
    }

    /** Cache tracking suggestions in recently visited files */
    private SuggestionCache cache = null;

    /** List of suggestions restored from the cache that we must delete
     when leaving this document */
    private List docSuggestions = null;

    /**
     * Queries passive providers for suggestions. Monitors
     * actual document modification state using DocumentListener
     * and CaretListener. Actual topcomponent is guarded
     * by attached ComponentListener.
     *
     * @param delayed <ul><li>true run {@link #performRescanInRP} later in TimerThread
     *                    <li>false run {@link #performRescanInRP} synchronously
     */
    private void findCurrentFile(boolean delayed) {
        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(getWindowSystemMonitor());
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(getEditorMonitor());
            handleDocHidden(document, dataobject);
        }
        removeCaretListeners();

        // Find which component is showing in it
        // Add my own component listener to it
        // When componentHidden, unregister my own component listener
        // Redo above

        // Locate source editor
        current = env.findActiveEditor();
        if (current == null) {
            // The last editor-support window in the editor was probably
            // just closed - or was not on top
            return;
        }

        // Listen for changes on this component so we know when
        // it's replaced by something else
        //System.err.println("Add component listener to " + tcToDO(current));
        current.addComponentListener(getWindowSystemMonitor());

        DataObject dao = extractDataObject(current);
        if (dao == null) return;

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
            document.removeDocumentListener(getEditorMonitor());
        }
        document = doc;
        doc.addDocumentListener(getEditorMonitor());

        dataobject = dao;
        notSaved = dao.isModified();

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
                manager.register(null, docSuggestions, null, getCurrentSuggestionsList(), true);
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

        if (ManagerSettings.getDefault().isScanOnShow()) {
            if (delayed) {
                performRescanInRP(doc, dataobject, ManagerSettings.getDefault().getShowScanDelay());
            } else {
                performRescanInRP(doc, dataobject, 0);
            }
        }
    }

    private static  DataObject extractDataObject(TopComponent topComponent) {
        Node[] nodes = topComponent.getActivatedNodes();

        if ((nodes == null) || (nodes.length != 1)) {
/*
            if (err.isLoggable(ErrorManager.INFORMATIONAL)) {
                err.log(
                  "Unexpected editor component activated nodes " + // NOI18N
                  " contents: " + nodes); // NOI18N
            }
            */
            return null;
        }

        Node node = nodes[0];

        final DataObject dao = (DataObject) node.getCookie(DataObject.class);
        //err.log("Considering data object " + dao);
        if (dao == null) {
            return null;
        }

        if (!dao.isValid()) {
            //err.log("The data object is not valid!");
            return null;
        }

        return dao;
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
     * Spawns <b>Suggestions Broker thread</b> that finishes actula work
     * asynchronously.
     *
     * @param document The document being edited
     * @param dataobject The Data Object for the file being opened
     * @param delay postpone the action by delay miliseconds
     *
     * @return parametrized task that rescans given dataobject in delay miliseconds
     */
    private RequestProcessor.Task performRescanInRP(final Document document,
                        final DataObject dataobject, int delay) {

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

        // Is MAX_VALUE even feasible here? There's no greater/lessthan
        // comparison, so wrapping around will work just fine, but I may
        // have to check manually and do it myself in case some kind
        // of overflow exception is thrown
        //  Wait, I'm doing a comparison now - look for currRequest.longValue
        assert currRequest.longValue() != Long.MAX_VALUE : "Wrap around logic needed!";  // NOI18N
        currRequest = new Long(currRequest.longValue() + 1);
        final Object origRequest = currRequest;

        // free AWT && Timer threads
        return serializeOnBackground(new Runnable() {
            public void run() {

                scheduledRescan = null;

                // Stale request If so, just drop this one
                if (origRequest != currRequest) return;

                // code is fixing (modifing) document
                if (wait) {
                    waitingEvent = true;
                    return;
                }

                LOGGER.fine("Dispatching rescan() request to providers...");

                setScanning(true);

                List scannedSuggestions = manager.dispatchScan(document, dataobject, compound);

                // update "allOpened" suggestion list

                if (allOpenedClientsCount > 0 && lastOpenedFileObject != null) {
                    // copy clones to private "allOpened" suggestions list
                    // (it must be cloned because tasklist membership is task property)
                    // TODO should task know about its suggestions list? I think it should not.
                    Iterator it = scannedSuggestions.iterator();
                    List clones = new ArrayList(scannedSuggestions.size());
                    while (it.hasNext()) {
                        Task next = (Task) it.next();
                        clones.add(next.cloneTask());
                    }

                    List previous = (List) openedFilesSuggestionsMap.remove(lastOpenedFileObject);
                    openedFilesSuggestionsMap.put(lastOpenedFileObject, clones);

                    getAllOpenedSuggestionList().addRemove(clones, previous, false, null, null);
                }

                if (clientCount > 0) {
                    // copy clones to private "current" suggestions list
                    // (it must be cloned because tasklist membership is task property)
                    Iterator it = scannedSuggestions.iterator();
                    List clones = new ArrayList(scannedSuggestions.size());
                    while (it.hasNext()) {
                        Task next = (Task) it.next();
                        clones.add(next.cloneTask());
                    }

                    List previous = new ArrayList(getCurrentSuggestionsList().getRoot().getSubtasks());
                    getCurrentSuggestionsList().addRemove(clones, previous, false, null, null);

                }

                // enforce comparable requests, works only for single request source
                if ((finishedRequest == null) ||
                        ((Comparable)origRequest).compareTo(finishedRequest) > 0) {
                    finishedRequest = (Comparable) origRequest;
                }
                if (currRequest == finishedRequest) {
                    setScanning(false);  // XXX global state, works only for single request source
                    LOGGER.fine("It was last pending request.");
                }
            }
        }, delay);
    }

    private RequestProcessor rp = new RequestProcessor("Suggestions Broker");  // NOI18N

    /** Enqueue request and perform it on background later on. */
    private RequestProcessor.Task serializeOnBackground(Runnable request, int delay) {
        return rp.post(request, delay , Thread.MIN_PRIORITY);
    }

    /**
     * Grab all the suggestions associated with this document/dataobject
     * and push it into the suggestion cache.
     */
    private void stuffCache(Document document, DataObject dataobject,
                            boolean unregisterOnly) {

        boolean filteredTaskListFixed = false;  //XXX register bellow
        if (filteredTaskListFixed == false) return;

        // XXX Performance: if docSuggestions != null, we should be able
        // to just reuse it, since the document must not have been edited!

        SuggestionList tasklist = getCurrentSuggestionsList();
        Task root = tasklist.getRoot();
        if (root.subtasksCount() == 0) {
            return;
        }
        Iterator it = root.subtasksIterator();
        List sgs = new ArrayList(root.subtasksCount());
        while (it.hasNext()) {
            SuggestionImpl s = (SuggestionImpl) it.next();
            Object seed = s.getSeed();
            // Make sure we don't pick up category nodes here!!!
            if (seed != SuggestionList.CATEGORY_NODE_SEED) {
                sgs.add(s);
            }

            Iterator sit = s.subtasksIterator();
            while (sit.hasNext()) {
                s = (SuggestionImpl) sit.next();
                seed = s.getSeed();
                if (seed != SuggestionList.CATEGORY_NODE_SEED) {
                    sgs.add(s);
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
        // XXX is not it already done by providers, it causes problems
        if (sgs.size() > 0) {
            manager.register(null, null, sgs, tasklist, true);
        }
    }

    /** The topcomponent we're currently tracking as the showing
     editor component */
    private TopComponent current = null;

    private JEditorPane[] editorsWithCaretListener = null;

    /** Add caret listener to dataobject's editor panes. */
    private void addCaretListeners() {

        assert editorsWithCaretListener == null : "addCaretListeners() must not be called twice without removeCaretListeners() => memory leak";  // NOI18N

        EditorCookie edit = (EditorCookie) dataobject.getCookie(EditorCookie.class);
        if (edit != null) {
            JEditorPane panes[] = edit.getOpenedPanes();
            if ((panes != null) && (panes.length > 0)) {
                // We want to know about cursor changes in ALL panes
                editorsWithCaretListener = panes;
                for (int i = 0; i < editorsWithCaretListener.length; i++) {
                    editorsWithCaretListener[i].addCaretListener(getEditorMonitor());
                }
            }
        }
    }

    /** Unregister prebiously added caret listeners. */
    private void removeCaretListeners() {
        if (editorsWithCaretListener != null) {
            for (int i = 0; i < editorsWithCaretListener.length; i++) {
                editorsWithCaretListener[i].removeCaretListener(getEditorMonitor());
            }
        }
        editorsWithCaretListener = null;
    }

    boolean pendingScan = false;

    /** Timed task which keeps track of outstanding scan requests; we don't
     scan briefly selected files */
    private RequestProcessor.Task scheduledRescan;

    /**
     * Plan a rescan (meaning: put delayed task into RP). In whole
     * broker there is only one scheduled task (and at maximum one
     * running concurrenly if delay is smaller than execution time).
     *
     * @param delay If true, don't create a rescan if one isn't already
     * pending, but if one is, delay it.
     * @param scanDelay actual delay value in ms
     */
    private void scheduleRescan(boolean delay, int scanDelay) {

        // This is just a delayer (e.g. for caret motion) - if there isn't
        // already a pending timeout, we're done. Caret motion shouldn't
        // -cause- a rescan, but if one is already planned, we want to delay
        // it.
        if (delay && (scheduledRescan == null)) {
            return;
        }

        // Stop our current timer; the previous node has not
        // yet been scanned; too brief an interval
        if (scheduledRescan != null) {
            scheduledRescan.cancel();
            scheduledRescan = null;
            LOGGER.fine("Scheduled rescan task delayed by " + scanDelay + " ms.");  // NOI18N
        }

        scheduledRescan = performRescanInRP(document, dataobject, scanDelay);
    }

    /** An event ocurred during quiet fix period. */
    private boolean waitingEvent = false;
    private boolean wait = false;

    /**
     * Set fix mode (quiet period) in which self initialized modifications are expected.
     * @param wait <ul> <li> true postpone all listeners until ...
     *                  <li> false ressurect listeners activity
     */
    final void setFixing(boolean wait) {
        boolean wasWaiting = this.wait;
        this.wait = wait;
        if (!wait && wasWaiting && (waitingEvent)) {
            scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            waitingEvent = false;
        }
    }


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


        TopComponent tc = null;
        if (allOpenedClientsCount > 0) {
            tc = env.findActiveEditor();
            if (tc == null) {
                lastOpenedFileObject = null;
            } else {
                DataObject dobj = extractDataObject(tc);
                lastOpenedFileObject = (dobj != null) ? dobj.getPrimaryFile() : null;
            }
        }

        doRescanInAWT(true);

    }

    /**
     * It sends asynchronously to AWT thread.
     * @param delay if true schedule later acording to user settings otherwise do immediatelly
     */
    private void doRescanInAWT(final boolean delay) {
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
                try {
                    if (clientCount > 0) {
                        findCurrentFile(delay);
                    }
                } finally {
                    // XXX findCurrent file spawns a thread
                    pendingScan = false;
                }
            }
        });
    }

    /**
     * Stop scanning for source items, deregistering
     * environment listeners.
     */
    private void stopActiveSuggestionFetching() {

        LOGGER.info("Stopping active suggestions fetching....");  // NOI18N

        if (scheduledRescan != null) {
            scheduledRescan.cancel();
            scheduledRescan = null;
        }

        env.removeTCRegistryListener(getWindowSystemMonitor());
        env.removeDORegistryListener(getDataSystemMonitor());

        // Unregister previous listeners
        if (current != null) {
            current.removeComponentListener(getWindowSystemMonitor());
            current = null;
        }
        if (document != null) {
            document.removeDocumentListener(getEditorMonitor());
            // NOTE: we do NOT null it out since we still need to
            // see if the document is unchanged
        }
        removeCaretListeners();

        handleDocHidden(document, dataobject);
        document = null;
    }


    private void setScanning(boolean scanning) {
        // XXX fishy direct access to view assuming 1:1 relation with list
//        SuggestionList tasklist = getList();
//        TaskListView v = tasklist.getView();
//        if (v instanceof SuggestionsView) {
//            SuggestionsView view = (SuggestionsView) v;
//            view.setScanning(scanning);
//        }
    }

    private void handleDocHidden(Document document, DataObject dataobject) {
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
    }

    private void handleTopComponentClosed(TopComponent tc) {

        componentsChanged();

        DataObject dobj = extractDataObject(tc);
        if (dobj == null) return;

        List previous = (List) openedFilesSuggestionsMap.remove(dobj.getPrimaryFile());
        if (previous != null) {
            getAllOpenedSuggestionList().addRemove(null, previous, false, null, null);
        }
    }

    private void handleTopComponentOpened(TopComponent tc) {
        // XXX assume that tc is currently selected one
        // A: it's called fron shown
        componentsChanged();
    }

    private WindowSystemMonitor windowSystemMonitor;

    /** See note on {@link WindowSystemMonitor#enableOpenCloseEvents} */
    private WindowSystemMonitor getWindowSystemMonitor() {
        if (windowSystemMonitor == null) {
            windowSystemMonitor = new WindowSystemMonitor();
        }
        return windowSystemMonitor;
    }

    private class WindowSystemMonitor implements PropertyChangeListener, ComponentListener {

        /** Previous Set&lt;TopComponent> */
        private Set openedSoFar = null;

        /**
         * Must be called before adding this listener to environment if in hope that
         * it will provide (initial) open/close events.
         */
        private void enableOpenCloseEvents() {
            List list = Arrays.asList(SuggestionsScanner.openedTopComponents());
            openedSoFar = new HashSet(list);
        }

        /** Reacts to changes */
        public void propertyChange(PropertyChangeEvent ev) {
            String prop = ev.getPropertyName();
            if (prop.equals(TopComponent.Registry.PROP_OPENED)) {

                if (allOpenedClientsCount > 0) {
                    // determine what components have been closed, window system does not
                    // provide any other listener to do it in more smart way

                    List list = Arrays.asList(SuggestionsScanner.openedTopComponents());
                    Set actual = new HashSet(list);

                    if (openedSoFar != null) {
                        Iterator it = openedSoFar.iterator();
                        while(it.hasNext()) {
                            TopComponent tc = (TopComponent) it.next();
                            if (actual.contains(tc) ) continue;
                            handleTopComponentClosed(tc);
                        }

                        Iterator ita = actual.iterator();
                        while(ita.hasNext()) {
                            TopComponent tc = (TopComponent) ita.next();
                            if (openedSoFar.contains(tc)) continue;
                            // defer actual action to componentShown, We need to assure opened TC is
                            // selected one. At this moment previous one is still selected.
                            tc.addComponentListener(new ComponentAdapter() {
                                public void componentShown(ComponentEvent e) {
                                    TopComponent tcomp = (TopComponent) e.getComponent();
                                    tcomp.removeComponentListener(this);
                                    handleTopComponentOpened(tcomp);
                                }
                            });
                        }
                    }

                    openedSoFar = actual;
                } else {
                    componentsChanged();
                    openedSoFar = null;
                }
            }
        }

        public void componentShown(ComponentEvent e) {
            // Don't care
        }

        public void componentHidden(ComponentEvent e) {
            //XXX it does not support both "current file" and "all opened" clients at same time
            if (allOpenedClientsCount == 0) {
                componentsChanged();
            }
        }

        public void componentResized(ComponentEvent e) {
            // Don't care
        }

        public void componentMoved(ComponentEvent e) {
            // Don't care
        }

    }


    private DataSystemMonitor dataSystemMonitor;

    private DataSystemMonitor getDataSystemMonitor() {
        if (dataSystemMonitor == null) {
            dataSystemMonitor =  new DataSystemMonitor();
        }
        return dataSystemMonitor;
    }

    /**
     * Listener for DataObject.Registry changes.
     *
     * This class listens for modify-changes of dataobjects such that
     * it can notify files of Save operations.
     */
    private class DataSystemMonitor implements ChangeListener {
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
                    if (ManagerSettings.getDefault().isScanOnSave()) {
                        scheduleRescan(false, ManagerSettings.getDefault().getSaveScanDelay());
                    }
                }
            }
        }
    }

    private EditorMonitor editorMonitor;

    private EditorMonitor getEditorMonitor() {
        if (editorMonitor == null) {
            editorMonitor = new EditorMonitor();
        }
        return editorMonitor;
    }

    private class EditorMonitor implements DocumentListener, CaretListener {

        //XXX missing reset logic
        private int prevLineNo = -1;

        public void changedUpdate(DocumentEvent e) {
            // Do nothing.
            // Changed update is only called for ATTRIBUTE changes in the
            // document, which I define as not relevant to the Document
            // Suggestion Providers.
        }

        public void insertUpdate(DocumentEvent e) {
            if (ManagerSettings.getDefault().isScanOnEdit()) {
                scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            }

            // If there's a visible marker annotation on the line, clear it now
            clearMarker();
        }

        public void removeUpdate(DocumentEvent e) {
            if (ManagerSettings.getDefault().isScanOnEdit()) {
                scheduleRescan(false, ManagerSettings.getDefault().getEditScanDelay());
            }

            // If there's a visible marker annotation on the line, clear it now
            clearMarker();
        }

        /** Moving the cursor position should cause a delay in document scanning,
         * but not trigger a new update */
        public void caretUpdate(CaretEvent caretEvent) {
            scheduleRescan(true, ManagerSettings.getDefault().getEditScanDelay());

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
                Line line = TLUtils.getLineByNumber(dataobject, lineno + 1);
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
                    // XXX badge editor suggestion in tasklist
                    //[SuggestionsView]setCursorLine(line);
                }
            }
        }

        /** Get rid of any annotations marking the current task */
        private void clearMarker() {
            SuggestionsView tlv = SuggestionsView.getCurrentView();
            if (tlv != null) {
                tlv.hideTaskInEditor();
            }
        }

    }



    /**
     * Binding to outer world that can be changed by unit tests
     */
    static class Env {

        void addTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().addPropertyChangeListener(pcl);
        }

        void removeTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().removePropertyChangeListener(pcl);
        }

        void addDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().addChangeListener(cl);

        }

        void removeDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().removeChangeListener(cl);
        }

        public TopComponent findActiveEditor() {

            Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
            if (mode == null) {
                // The editor window was probablyjust closed
                return null;
            }
            TopComponent tc = mode.getSelectedTopComponent();
            if (tc instanceof CloneableEditor) {
                // Found the source editor...
                if (tc.isShowing()) {
                    return tc;
                }
            }
            return null;
        }
    }

}
