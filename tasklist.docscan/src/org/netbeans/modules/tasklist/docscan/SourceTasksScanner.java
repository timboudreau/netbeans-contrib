/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.suggestions.SuggestionList;
import org.netbeans.modules.tasklist.suggestions.SuggestionsScanner;
import org.netbeans.modules.tasklist.core.Background;
import org.netbeans.modules.tasklist.core.CancellableRunnable;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.Cancellable;
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
     * Scans for all project tasks in asynchronous threads (a scanner
     * thread and AWT thread).
     *
     * @param view requestor
     */
    public static Background scanTasksAsync(final SourceTasksView view) {

        final SuggestionList list = (SuggestionList) view.getList();

        Bg bg = new Bg(view, list, null);
        return Background.execute(bg);
    }

    /**
     * Scan selected folders for TODOs.
     *
     * @param view target consumer
     * @param folders contetx to scan
     * @return interruptible handle
     */
    public static Background scanTasksAsync(SourceTasksView view, DataObject.Container[] folders) {
        final SuggestionList list = (SuggestionList) view.getList();

        Bg bg = new Bg(view, list, folders);
        return Background.execute(bg);

    }


    static class Bg implements CancellableRunnable {

        private Cancellable cancellable;

        private final SourceTasksView view;
        private final SuggestionList list;
        private final DataObject.Container[] ctx;

        Bg(SourceTasksView view, SuggestionList list, DataObject.Container[] ctx) {
            this.view = view;
            this.list = list;
            this.ctx = ctx;
        }

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
                scanProjectSuggestions(list, view, this);

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

        public boolean cancel() {
            if (cancellable != null) {
                return cancellable.cancel();
            }
            return false;
        }
    };

    static void scanProjectSuggestions(final SuggestionList list, final SourceTasksAction.ScanProgressMonitor view, Bg bg) {
        DataObject.Container projectFolders[] = bg.ctx;

        if (projectFolders == null) {
            List project = new ArrayList(23);
            boolean enabled = false;

            if ("project".equals(System.getProperty("todos.project", "repository"))) {
                enabled = project(project);
            }

            if (enabled == false) {
                project.clear();
                repository(project);
            }

            projectFolders = new DataObject.Container[project.size()];
            project.toArray(projectFolders);
        }

        SuggestionsScanner c = SuggestionsScanner.getDefault();
        c.setUsabilityLimit(Settings.getDefault().getUsabilityLimit());
        bg.cancellable = c;
        c.scan(projectFolders, list, view, new SourceTasksProviderAcceptor());
    }

    static void repository(List folders) {
        Repository repository = Repository.getDefault();
        Enumeration en = repository.fileSystems();

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
                    }
                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static boolean project(List folders) {
        // HACK XXX ProjectCookie is deprecated without replacement
        // access it's registration file directly
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        FileObject registration = fs.findResource("Services/Hidden/org-netbeans-modules-projects-ProjectCookieImpl.instance");
        if (registration == null) {
            // it's not installed or some incomaptible version
            return false;
        } else {
            try {
                DataObject dobj = DataObject.find(registration);
                InstanceCookie ic = (InstanceCookie) dobj.getCookie(InstanceCookie.class);
                Object obj = ic.instanceCreate();
                Method method = obj.getClass().getMethod("projectDesktop", new Class[0]);
                Node node = (Node) method.invoke(obj, new Object[0]);
                DataObject prjDO = (DataObject) node.getCookie(DataObject.class);
                if (prjDO instanceof DataObject.Container) {
                    DataObject[] kids = ((DataObject.Container)prjDO).getChildren();
                    for (int i=0; i<kids.length; i++) {
                        if (kids[i] instanceof DataObject.Container) {
                            folders.add(kids[i]);
                        }
                    }
                }
                return true;
            } catch (DataObjectNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            } catch (ClassNotFoundException e) {
                return false;
            } catch (NoSuchMethodException e) {
                return false;
            } catch (IllegalAccessException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }

}
