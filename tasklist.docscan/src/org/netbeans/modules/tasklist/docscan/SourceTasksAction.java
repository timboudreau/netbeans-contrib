/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import java.util.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.RequestProcessor;
import org.openide.loaders.DataObject;
import org.openide.ErrorManager;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.*;
import org.netbeans.api.tasklist.SuggestionManager;
import org.netbeans.modules.tasklist.suggestions.SuggestionManagerImpl;
import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.suggestions.ScanSuggestionsAction;

/**
 * Opens window with scanned project source tasks.
 *
 * @author Petr Kuzel
 */
public class SourceTasksAction extends CallableSystemAction {

    private static long lastUISync = System.currentTimeMillis();

    protected boolean asynchronous() {
        return false;
    }

    public void performAction() {
        final SuggestionList list = new SourceTasksList();
        // The category should be DIFFERENT from the category used
        // for the default suggestion view (the active scanning view)
        // such that the "Show Suggestions View" action does not
        // locate and reuse these windows - and so they can have different
        // column configurations (filename is not useful in the active
        // suggestions view window, but is critical in the directory
        // scan for example.)
        final SourceTasksView view = new SourceTasksView("TODOs",
            list,
            "org/netbeans/modules/tasklist/docscan/scanned-task.gif" // NOI18N
        );

        view.showInMode();

        RequestProcessor.getDefault().post(
            new Runnable() {
                public void run() {
                    try {
                        try {
                            // block until previous AWT events get processed
                            // it does not survive nested invokeAndWaits
                            SwingUtilities.invokeAndWait(
                                new Runnable() {
                                    public void run() {
                                        view.setCursor(Utilities.createProgressCursor(view));
                                    }
                                }
                            );
                        } catch (InterruptedException ignore) {
                            // XXX
                        } catch (InvocationTargetException e) {
                            ErrorManager.getDefault().notify(e);
                        }
                        scanProjectSuggestions(list);
                        Integer count = new Integer(list.size());
                        StatusDisplayer.getDefault ().setStatusText(
                           NbBundle.getMessage(ScanSuggestionsAction.class,
                                               "ScanDone", count)); // NOI18N
                    } finally {
                        SwingUtilities.invokeLater(
                            new Runnable() {
                                public void run() {
                                    view.setCursor(null);
                                }
                            }
                        );
                    }
                }
            }
        );
    }

    static void scanProjectSuggestions(SuggestionList list) {
        // FIXME access project root
        Repository repository = Repository.getDefault();
        Enumeration en = repository.fileSystems();
        List project = new ArrayList(23);
        int allFolders = 0;
        while (en.hasMoreElements()) {
            FileSystem next = (FileSystem) en.nextElement();
            if (next.isDefault() || next.isHidden() || next.isReadOnly()) {
                continue;
            }
            if (next.isValid()) {
                FileObject fo = next.getRoot();
                try {
                    DataObject dobj = DataObject.find(fo);
                    if (dobj instanceof DataObject.Container) {
                        project.add(dobj);
                        allFolders += countFolders(fo);
                    }
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        DataObject.Container projectFolders[] = new DataObject.Container[project.size()];
        project.toArray(projectFolders);

        SuggestionManagerImpl manager = (SuggestionManagerImpl)
            SuggestionManager.getDefault();

        final int estimatedFolders = allFolders + 1;
        manager.scan(projectFolders, list, new SuggestionManagerImpl.ScanProgress() {

            int realFolders = 0;

            public void folderEntered(FileObject folder) {
                realFolders++;
                StatusDisplayer.getDefault ().setStatusText("" + realFolders + "/" + estimatedFolders + ": scanning " + folder.getPath());
                handlePendingAWTEvents();
            }

            public void fileScanned(FileObject fo) {
                handlePendingAWTEvents();
            }

            public void folderScanned(FileObject fo) {
                handlePendingAWTEvents();
            }
        });

    }

    private static int countFolders(FileObject projectFolder) {
        int count = 0;
        Enumeration en = projectFolder.getChildren(true);
        while (en.hasMoreElements()) {
            FileObject next = (FileObject) en.nextElement();
            if (next.isFolder() == false) continue;
            String name = next.getNameExt();
            //XXX there is discrepancy because CVS folders are skipped by engine
            if ("CVS".equals(name) || "SCCS".equals(name)) { // NOI18N
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * Gives up CPU until all known AWT events get dispatched.
     */
    public static void handlePendingAWTEvents() {
        if (SwingUtilities.isEventDispatchThread()) return;

        long now = System.currentTimeMillis();
        if (now - lastUISync < 103) return;

        lastUISync = now;

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    // nothing no deadlock can occure
                }
            });
        } catch (InterruptedException ignore) {
        } catch (InvocationTargetException ignore) {
        }
    }


    public String getName() {
        return "TODOs";
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx(SourceTasksAction.class);
    }


}
