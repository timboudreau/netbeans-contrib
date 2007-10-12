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
package org.netbeans.modules.perspective.persistence;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.utils.OpenedViewTracker;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * MainPaser.java
 * @author Anuradha G
 */
public class MainParser {

    private static final String BASE_DIR = "perspectives"; //NOI18N

    private static final String EXT = "perspective"; //NOI18N

    private static final String CONFIG_DIR = BASE_DIR + "/config"; //NOI18N

    private static final String BUILTIN_DIR = BASE_DIR + "/builtin"; //NOI18N

 
    private static MainParser instance;
    private PerspectiveParser paser = new PerspectiveParser();
    private FileObject config;
    private FileObject builtin;
  
    private PerspectivePreferences perspectivePreferences = PerspectivePreferences.getInstance();

    private MainParser() {
        //Creating Parser instance
        config = Repository.getDefault().getDefaultFileSystem().findResource(CONFIG_DIR);
        builtin = Repository.getDefault().getDefaultFileSystem().findResource(BUILTIN_DIR);
       
    }

    public static synchronized MainParser getInstance() {
        if (instance == null) {

            instance = new MainParser();
        }
        return instance;
    }

    private synchronized void cleanDir() throws IOException {
        //"Cleaning Dir"
        FileObject[] children = config.getChildren();
        for (FileObject fileObject : children) {
            fileObject.delete();
        }
    }

    public synchronized void restore() {
        PerspectiveManagerImpl.getInstance().clear();
        if (builtin != null) {
            //Loading default Perspectives from layer
            readPerspectives(builtin);
        }

        //Loading Perspectives from Config
        FileObject[] viewChildren = config.getChildren();
        for (FileObject fileObject : viewChildren) {
            try {

                Perspective decoded = paser.decode(fileObject.getInputStream());
                if (decoded != null && (decoded.getName().startsWith("custom_")/*NOI18N*/ || PerspectiveManagerImpl.getInstance().findPerspectiveByID(decoded.getName()) != null)) {

                    PerspectiveManagerImpl.getInstance().registerPerspective(decoded, false);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }


        PerspectiveManagerImpl.getInstance().arrangeIndexsToExistIndexs();

        String id = perspectivePreferences.getSelectedPerspective();
        Perspective selected = null;
        boolean firstTime = false;
        if (id != null) {
            selected = PerspectiveManagerImpl.getInstance().findPerspectiveByID(id);
        }
        if (selected == null) {

            List<Perspective> perspectives = PerspectiveManager.getDefault().getPerspectives();
            //Loading default Perspecive as selected Perspective
            selected = perspectives.size() > 0 ? perspectives.get(0) : null;
            firstTime = true;
        }
        if (selected != null) {
            //if first time views shoude dock else just select to improve startup time
            if (firstTime) {
                final Perspective p = selected;
                WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                    public void run() {
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                PerspectiveManager.getDefault().setSelected(p);
                                ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
                            }
                        });
                    }
                });
            } else {
                PerspectiveManagerImpl.getInstance().setSelected(selected, false);
                ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
            }

        }
    }

    private void readPerspectives(FileObject fileObject) {
        DataFolder folder = DataFolder.findFolder(fileObject);
        Enumeration<DataObject> children = folder.children(true);
        while (children.hasMoreElements()) {

            DataObject dataObject = children.nextElement();
            if (dataObject instanceof DataFolder) {
                continue;
            }
            InstanceCookie ic = dataObject.getCookie(InstanceCookie.class);
            if (ic == null) {
                continue;
            }
            try {
                Perspective perspective = (Perspective) ic.instanceCreate();
                PerspectiveManagerImpl.getInstance().registerPerspective(perspective, false);
            //TODO 
                //perspectivePreferences.readPerspective(perspective);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }




        }


    }

    public synchronized void store() {
        final List<Perspective> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
        for (Perspective perspective : perspectives) {
            try {
                perspective.removePerspectiveListeners();
                FileObject fileObject = config.getFileObject(perspective.getName(), EXT);
                if (fileObject == null) {
                    try {
                        fileObject = config.createData(perspective.getName(), EXT);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                paser.encode(perspective, fileObject.getOutputStream());
            //TODO 
                // perspectivePreferences.persistencePerspective(perspective);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Perspective selected = PerspectiveManagerImpl.getInstance().getSelected();
        if (selected != null) {
            perspectivePreferences.setSelectedPerspective(selected.getName());
            if (perspectivePreferences.isTrackOpened()) {
                new OpenedViewTracker(selected);
            } else {
                PerspectiveManagerImpl.getInstance().setSelected(selected);
            }
        }

    }

    public synchronized void reset() throws IOException {
        perspectivePreferences.reset();
        ToolbarStyleSwitchUI.getInstance().reset();
        cleanDir();
        restore();
    }
}