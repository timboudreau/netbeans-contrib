/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.searchandreplace.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.queries.SharabilityQuery;

import org.netbeans.modules.searchandreplace.Cancel;
import org.netbeans.modules.searchandreplace.SearchAndReplaceAction;

import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Represents a search over a set of root directories, which can return an
 * array of Item objects representing individual matches in files.
 *
 * @author Timothy Boudreau
 */
public final class Search {
    private final Collection roots;
    private final Cancel cancel;
    private final SearchDescriptor descriptor;
    private ItemStateObserver observer = null;
    private TextFetcher writer = null;
    /**
     * ItemStateObserver which redispatches to any actual observer we have
     * on the event thread.
     */
    ItemStateObserver observerProxy = new ObserverProxy();

    //debugging flag
    static final boolean reallyWrite = true;

    private RequestProcessor rp = new RequestProcessor ("TextSearch", //NOI18N
            Thread.NORM_PRIORITY, true);

    private Pattern pattern = null;

    /** Create a new Search object
     * @param rootFolders A Collection of java.io.Files representing directories
     *  that should be searched.
     * @param descriptor A SearchDescriptor that describes the search text, and
     *  paramaters that affect what files are searched
     * @cancel a Cancellable that will cancel file scanning
     */
    public Search(Collection /* <File> */ rootFolders, SearchDescriptor descriptor, Cancel cancel) {
        this.roots = rootFolders;
        this.cancel = cancel;
        this.descriptor = descriptor;
    }

    /** Get the search text passed in the SearchDescriptor to the constructor
     */
    public String getSearchText() {
        return descriptor.getSearchText();
    }

    /**
     * Returns an array of Item objects representing individual matches from
     * all files this search will search.  IMPORTANT:  Use ItemComparator to
     * sort the items in tail-first order before calling replace() on them.
     * Not to be called from the EQ thread.
     */
    public Item[] search(ProgressHandle progress) throws IOException {
        progress.switchToIndeterminate();
        progress.setDisplayName(NbBundle.getMessage (Search.class,
                "LBL_FindingFiles")); //NOI18N

        Set files = getAllFiles();
        if (files.isEmpty() && !cancel.cancelled) {
            StatusDisplayer.getDefault().setStatusText (
                    NbBundle.getMessage (Search.class, "MSG_NoFiles")); //NOI18N
            return new Item[0];
        } else if (cancel.cancelled) {
            return new Item[0];
        }

        List items = new ArrayList(files.size());
        int pos = 0;
        progress.switchToDeterminate(files.size());
        for (Iterator i = files.iterator(); i.hasNext();) {
            File f = (File) i.next();
            progress.progress(f.getName(), pos++);
            try {
                OneFileItem item = new OneFileItem (f, this);
                items.addAll (item.getItems());
            } catch (IOException ioe) {
                items.add (new Problem (f, ioe));
            }
        }
        Item[] result = (Item[]) items.toArray(new Item[items.size()]);
        return result;
    }

    static boolean UNIT_TESTING = false;
    Set getAllFiles() {
        Set all = new HashSet();
        try {
            for (Iterator i = roots.iterator(); i.hasNext();) {
                if (cancel.cancelled) {
                    return Collections.EMPTY_SET;
                }
                File f = (File) i.next();
                boolean checkVisibility = UNIT_TESTING ? false :
                    !isIgnoredFile(f);

                findFiles (f, all, 0, checkVisibility);
            }
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify (ErrorManager.USER, ioe);
        }
        if (all.isEmpty()) {
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                    SearchAndReplaceAction.class, "MSG_NoFiles"));
        }
        Set result = new TreeSet (new FileComparator());
        result.addAll (all);
        return result;
    }

    /**
     * Determine if a file is ignored by version control
     */
    public static boolean isIgnoredFile (File f) {
        int sharable = SharabilityQuery.getSharability(f);
        return sharable == SharabilityQuery.NOT_SHARABLE;
    }

    private void findFiles (File f, Set all, int depth, boolean checkSharability) throws IOException {
        boolean shouldRecurse = depth == 0 || descriptor.isIncludeSubfolders();
        if (f.exists()) {
            if (f.isFile()) {
                boolean include = true;
                //checkSharability: Dirs not under VCS control are invisible,
                //so if we're searching a root not under VCS control, we
                //shouldn't try to the visibility of anything under it because
                //it will always return false
                if (checkSharability && !descriptor.isIncludeIgnored()) {
                    include &= !isIgnoredFile(f);
                }
                if (!descriptor.isIncludeBinaryFiles()) {
                    include &= !isBinaryFile(f);
                }
                if (include) {
                    all.add (f.getCanonicalFile());
                }
            } else if (shouldRecurse && f.isDirectory()) {
                if (descriptor.isIncludeIgnored() || !isIgnoredFile(f)) {
                    File[] kids = f.listFiles();
                    for (int i=0; i < kids.length; i++) {
                        findFiles (kids[i].getCanonicalFile(), all, depth + 1,
                                checkSharability);
                    }
                }
            }
        }
    }

    /**
     * Determine if a file should be treated as binary, not text (no crlf
     * conversions)
     */
    public static boolean isBinaryFile (File f) {
        if (!UNIT_TESTING) {
            FileObject fob = FileUtil.toFileObject(f);
            if (fob != null) {
                return !fob.getMIMEType().startsWith("text"); //NOI18N
                //XXX deal with content/unknown?
            }
            return true;
        } else {
            //Hack for unit tests
            return f.getName().endsWith(".jar") || f.getName().endsWith( //NOI18N
                    ".class"); //NOI18N
        }
    }

    /**
     * Get the request processor that owns the background thread the Search
     * will do its work on.
     */
    public RequestProcessor getRequestProcessor() {
        return rp;
    }

    /**
     * Fetch the text of an Item.  Since the text is retrieved asynchronously,
     * this method is passed a TextReceiver, which will get its setText()
     * method called on the event thread after it has been loaded on a
     * background thread.
     */
    public void requestText (Item item, TextReceiver receiver) {
        boolean replace = false;
        synchronized(this) {
            if (writer != null) {
                if (writer.replaceLocation(item, receiver)) {
                    return;
                } else {
                    writer.cancel();
                    writer = null;
                }
            }
            if (writer == null) {
                writer = new TextFetcher (item, receiver, getRequestProcessor());
            }
        }
    }

    /**
     * Set an observer which will be notified of state changes (such as
     * Items becoming invalid).
     */
    public void setItemStateObserver (ItemStateObserver observer) {
        this.observer = observer;
    }

    /**
     * ItemStateObserver which redispatches to any actual observer we have
     * on the event thread.
     */
    private final class ObserverProxy implements ItemStateObserver {
        public void becameInvalid(final File file, final String reason) {
            final ItemStateObserver observer = Search.this.observer;
            if (observer == null) return;
            Runnable r = new Runnable() {
                public void run() {
                    if (observer != null) {
                        observer.becameInvalid(file, reason);
                    }
                }
            };
            Mutex.EVENT.readAccess(r);
        }

        public void shouldReplaceChanged(final Item item, final boolean shouldReplace) {
            final ItemStateObserver observer = Search.this.observer;
            if (observer == null) return;
            Runnable r = new Runnable() {
                public void run() {
                    if (observer != null) {
                        observer.shouldReplaceChanged(item, shouldReplace);
                    }
                }
            };
            Mutex.EVENT.readAccess(r);
        }

        public void fileShouldReplaceChanged(final File file, final boolean fileShouldReplace) {
            final ItemStateObserver observer = Search.this.observer;
            if (observer == null) return;
            Runnable r = new Runnable() {
                public void run() {
                    if (observer != null) {
                        observer.fileShouldReplaceChanged(file, fileShouldReplace);
                    }
                }
            };
            Mutex.EVENT.readAccess(r);
        }

        public void replaced(final Item item) {
            final ItemStateObserver observer = Search.this.observer;
            if (observer == null) return;
            Runnable r = new Runnable() {
                public void run() {
                    if (observer != null) {
                        observer.replaced(item);
                    }
                }
            };
            Mutex.EVENT.readAccess(r);
        }
    }

    public String getReplacementText() {
        return !descriptor.isShouldReplace() ? null :
            descriptor.getReplaceText();
    }

    Pattern getPattern() {
        if (pattern == null) {
            pattern = escapePattern(descriptor.getSearchText());
        }
        return pattern;
    }

    Pattern escapePattern(String s) {
        // fix for issue #50170, test for this method created, if necessary refine..
        // [jglick] Probably this would work as well and be a bit more readable:
        // String replacement = "\\Q" + s + "\\E";

        //XXX use quoteReplacement() method instead?
        String replacement = s.replaceAll("([\\(\\)\\[\\]\\^\\*\\.\\$\\{\\}\\?\\+\\\\])", "\\\\$1");
        return Pattern.compile(replacement, descriptor.isCaseSensitive() ? 0 : (Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE));
    }

    private static final class FileComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            File a = (File) o1;
            File b = (File) o2;
            return a.getPath().compareTo(b.getPath());
        }
    }
}
