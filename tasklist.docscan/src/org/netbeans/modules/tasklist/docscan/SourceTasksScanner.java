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

import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.suggestions.SuggestionsScanner;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Scans project for source todos action performer.
 *
 * @author Petr Kuzel
 */
final class SourceTasksScanner {

    /**
     * Scans for tasks in asyncronous threads (a scanner
     * thread and AWT thread).
     *
     * @param view requestor
     */
    public static void scanTasksAsync(final SourceTasksView view) {

        final SuggestionList list = (SuggestionList) view.getList();

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
                        scanProjectSuggestions(list, view);

                    } finally {
                        SwingUtilities.invokeLater(
                            new Runnable() {
                                public void run() {
                                    view.statistics(list.size());
                                    view.setCursor(null);
                                }
                            }
                        );
                    }
                }
            }, 53, Thread.MIN_PRIORITY    //
        );
    }

    static void scanProjectSuggestions(final SuggestionList list, final SourceTasksAction.ScanProgressMonitor view) {
        List project = new ArrayList(23);
        int allFolders = -1;

        if ("project".equals(System.getProperty("todos.project", "repository"))) {
            allFolders = project(project);
        }

        if (allFolders == -1) {
            project.clear();
            allFolders = repository(project);
        }

        DataObject.Container projectFolders[] = new DataObject.Container[project.size()];
        project.toArray(projectFolders);

        final int estimatedFolders = allFolders + 1;
        view.estimate(estimatedFolders);
        SuggestionsScanner.getDefault().scan(projectFolders, list, view);

    }

    static int repository(List folders) {
        Repository repository = Repository.getDefault();
        Enumeration en = repository.fileSystems();

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
                        folders.add(dobj);
                        allFolders += countFolders(fo);
                    }
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return allFolders;
    }

    static int project(List folders) {
        // HACK XXX ProjectCookie is deprecated without replacement
        // access it's registration file directly
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject registration = fs.findResource("Services/Hidden/org-netbeans-modules-projects-ProjectCookieImpl.instance");
        if (registration == null) {
            // it's not installed or some incomaptible version
            return -1;
        } else {
            try {
                int allFolders = -1;
                DataObject dobj = DataObject.find(registration);
                InstanceCookie ic = (InstanceCookie) dobj.getCookie(InstanceCookie.class);
                Object obj = ic.instanceCreate();
                Method method = obj.getClass().getMethod("projectDesktop", new Class[0]);
                Node node = (Node) method.invoke(obj, new Object[0]);
                DataObject prjDO = (DataObject) node.getCookie(DataObject.class);
                if (prjDO instanceof DataObject.Container) {
                    allFolders = 0;
                    DataObject[] kids = ((DataObject.Container)prjDO).getChildren();
                    for (int i=0; i<kids.length; i++) {
                        if (kids[i] instanceof DataObject.Container) {
                            folders.add(kids[i]);
                            allFolders += countFolders(kids[i].getPrimaryFile());
                        }
                    }
                }
                return allFolders;
            } catch (DataObjectNotFoundException e) {
                return -1;
            } catch (IOException e) {
                return -1;
            } catch (ClassNotFoundException e) {
                return -1;
            } catch (NoSuchMethodException e) {
                return -1;
            } catch (IllegalAccessException e) {
                return -1;
            } catch (InvocationTargetException e) {
                return -1;
            }
        }
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

}
