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

import org.openide.loaders.DataObject;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.netbeans.spi.tasklist.SuggestionContext;
import org.netbeans.spi.tasklist.SuggestionProvider;
import org.netbeans.spi.tasklist.DocumentSuggestionProvider;
import org.netbeans.apihole.tasklist.SPIHole;
import org.netbeans.api.tasklist.SuggestionManager;

import java.util.*;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;

/**
 * Scans for suggestions by delegating to
 * plugged-in providers.
 *
 * @author Petr Kuzel
 */
public final class SuggestionsScanner {

    /** Optional progress monitor */
    private ScanProgress progressMonitor;

    // target manager impl
    private final SuggestionManagerImpl manager;

    private final SuggestionProviders registry;

    // keep default instance (only if a client exists)
    private static Reference instance;

    private SuggestionsScanner() {
        manager = (SuggestionManagerImpl) Lookup.getDefault().lookup(SuggestionManager.class);
        registry = SuggestionProviders.getDefault();
    }

    public static SuggestionsScanner getDefault() {
        if (instance == null) {
            return createDefault();
        }
        SuggestionsScanner scanner = (SuggestionsScanner) instance.get();
        if (scanner == null) {
            return createDefault();
        } else {
            return scanner;
        }
    }

    private static SuggestionsScanner createDefault() {
        SuggestionsScanner scanner = new SuggestionsScanner();
        instance = new WeakReference(scanner);
        return scanner;
    }

    /**
     * Scans recursively for suggestions notifing given progress monitor.
     * @param folders
     * @param list
     * @param monitor
     */
    public final void scan(DataObject.Container[] folders, SuggestionList list, ScanProgress monitor) {
        try {
            progressMonitor = monitor;
            monitor.scanStarted();
            scan(folders, list, true);
        } finally {
            monitor.scanFinished();
            progressMonitor = null;
        }
    }

    /** Iterate over the folder recursively (optional) and scan all files.
     We skip CVS and SCCS folders intentionally. Would be nice if
     the filesystem hid these things from us. */
    public final void scan(DataObject.Container[] folders, SuggestionList list,
                           boolean recursive) {
        // package-private instead of private for the benefit of the testsuite
        for (int i = 0; i < folders.length; i++) {
            if (Thread.interrupted()) return;
            DataObject.Container folder = folders[i];
            scanFolder(folder, recursive, list);
        }
    }

    private void scanFolder(DataObject.Container folder, boolean recursive, SuggestionList list) {
        DataObject[] children = folder.getChildren();
        for (int i = 0; i < children.length; i++) {

            if (Thread.currentThread().isInterrupted()) return;

            DataObject f = children[i];
            if (f instanceof DataObject.Container) {
                if (!recursive) {
                    continue;
                }

                //XXX Skip CVS and SCCS folders
                String name = f.getPrimaryFile().getNameExt();
                if ("CVS".equals(name) || "SCCS".equals(name)) { // NOI18N
                    continue;
                }

                if (progressMonitor == null) {
                    // XXX stil strange that possibly backgournd process writes directly to UI
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(ScanSuggestionsAction.class,
                                    "ScanningFolder", // NOI18N
                                    f.getPrimaryFile().getNameExt()));
                } else {
                    progressMonitor.folderEntered(f.getPrimaryFile());
                }

                scanFolder((DataObject.Container) f, true, list); // recurse!
                if (progressMonitor != null) {
                    progressMonitor.folderScanned(f.getPrimaryFile());
                }

            } else {
                // Get document, and I do mean now!

                if (!f.isValid()) {
                    continue;
                }

                EditorCookie edit =
                        (EditorCookie) f.getCookie(EditorCookie.class);
                if (edit == null) {
                    continue;
                }

                SuggestionContext env = SPIHole.createSuggestionContext(f);

                if (progressMonitor == null) {
                    // XXX stil strange that possibly backgournd process writes directly to UI
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(ScanSuggestionsAction.class,
                                    "ScanningFile", // NOI18N
                                    f.getPrimaryFile().getNameExt()));
                }

                scanLeaf(list, env);
                if (progressMonitor != null) {
                    progressMonitor.fileScanned(f.getPrimaryFile());
                }
            }
        }
    }

    private void scanLeaf(SuggestionList list, SuggestionContext env) {
        List providers = registry.getProviders();
        ListIterator it = providers.listIterator();
        while (it.hasNext()) {
            if (Thread.currentThread().isInterrupted()) return;
            SuggestionProvider provider = (SuggestionProvider) it.next();
// XXX it's not driven by ShowCategoryAction
//            if (((unfiltered == null) ||
//                    (unfiltered == provider)) &&
//                    (provider instanceof DocumentSuggestionProvider)) {

                // FIXME no initialization events possibly fired
                // I guess that reponsibility for recovering from missing
                // lifecycle events should be moved to providers
                // TODO FIXME HACK this instanceof added on 03.12.2003
                // to fix a ClassCastException
                if (provider instanceof DocumentSuggestionProvider) {
                    List l = ((DocumentSuggestionProvider) provider).scan(env);
                    if (l != null) {
                        // XXX ensure that scan returns a homogeneous list of tasks
                        manager.register(provider.getTypes()[0], l, null, list, true);
                    }
                }
//            }
        }
    }

    /**
     * Handles scan method emmited progress callbacks.
     * Implementation can interrupt scanning thread
     * (current thread) by standard thread interruption
     * methods.
     */
    public interface ScanProgress {
        void scanStarted();

        void folderEntered(FileObject folder);

        void fileScanned(FileObject file);

        void folderScanned(FileObject folder);

        void scanFinished();
    }


}
