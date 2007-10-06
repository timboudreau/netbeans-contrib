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
import java.util.List;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.utils.OpenedViewTracker;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * MainPaser.java
 * @author Anuradha G
 */
public class MainParser {

    private static final String BASE_DIR = "perspectives"; //NOI18N

    private static final String EXT = "pv"; //NOI18N

    private static final String CONFIG_DIR = BASE_DIR + "/config"; //NOI18N

    private static final String BUILTIN_DIR = BASE_DIR + "/builtin"; //NOI18N

    private static final String DEFAULT_DIR = "/default"; //NOI18N

    private static final String CUSTOM_DIR = "/custom"; //NOI18N

    private static MainParser instance;
    private PerspectiveParser paser = new PerspectiveParser();
    private FileObject config;
    private FileObject builtinDefault;
    private FileObject builtinCustom;

    private MainParser() {
        //Creating Parser instance
        config = Repository.getDefault().getDefaultFileSystem().findResource(CONFIG_DIR);
        builtinDefault = Repository.getDefault().getDefaultFileSystem().findResource(BUILTIN_DIR + DEFAULT_DIR);
        builtinCustom = Repository.getDefault().getDefaultFileSystem().findResource(BUILTIN_DIR + CUSTOM_DIR);
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
        if (!PerspectivePreferences.getInstance().isCompatible()) {
            try {
                cleanDir();
                PerspectivePreferences.getInstance().setCompatible(true);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

        PerspectiveManagerImpl.getInstance().clear();
        if (builtinDefault != null) {
            //Loading default Perspectives from layer
            FileObject[] builtinDefaultChildren = builtinDefault.getChildren();
            processPerspectives(contextLoader, builtinDefaultChildren);
        }
        if (builtinCustom != null) {
            //Loading custom Perspectives from layer
            FileObject[] builtinCustomChildren = builtinCustom.getChildren();
            processPerspectives(contextLoader, builtinCustomChildren);
        }




        //Loading Perspectives from Config
        FileObject[] viewChildren = config.getChildren();
        for (FileObject fileObject : viewChildren) {
            try {

                Perspective decoded = paser.decode(fileObject.getInputStream());
                if (decoded != null && (decoded.getName().startsWith("custom_") || PerspectiveManagerImpl.getInstance().findPerspectiveByID(decoded.getName()) != null)) {

                    PerspectiveManagerImpl.getInstance().registerPerspective(decoded, false);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }


        PerspectiveManagerImpl.getInstance().arrangeIndexsToExistIndexs();

        String id = PerspectivePreferences.getInstance().getSelectedPerspective();
        Perspective selected = null;
        boolean firstTime = false;
        if (id != null) {
            selected = PerspectiveManagerImpl.getInstance().findPerspectiveByID(id);
        } else {
            firstTime = true;
        }
        if (selected == null) {

            List<Perspective> perspectives = PerspectiveManager.getDefault().getPerspectives();
            //Loading default Perspecive as selected Perspective
            selected = perspectives.size() > 0 ? perspectives.get(0) : null;
        }
        if (selected != null) {
            if (firstTime) {
                WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                    public void run() {
                        EventQueue.invokeLater(new Runnable() {

                            public void run() {
                                loadSelectedPerspective();
                            }
                        });
                    }
                });
            } else {
                PerspectiveManagerImpl.getInstance().setSelected(selected, false);
                ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
            }
            
        //Load selected perspective
        }
    }

    private void processPerspectives(ClassLoader contextLoader, FileObject[] builtinChildren) {
        for (FileObject fileObject : builtinChildren) {
            try {
                String name = (String) fileObject.getAttribute("class"); //NOI18N
                Class perspectiveClass = contextLoader.loadClass(name);

                Object object = perspectiveClass.newInstance();
                //Perspective Object Created
                if (object instanceof Perspective) {
                    Perspective perspective = (Perspective) object;
                    PerspectiveManagerImpl.getInstance().registerPerspective(perspective, false);
                }
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void loadSelectedPerspective() {
        String id = PerspectivePreferences.getInstance().getSelectedPerspective();
        Perspective selected = null;

        if (id != null) {
            selected = PerspectiveManagerImpl.getInstance().findPerspectiveByID(id);

        }
        if (selected == null) {

            List<Perspective> perspectives = PerspectiveManager.getDefault().getPerspectives();
            //Loading default Perspecive as selected Perspective
            selected = perspectives.size() > 0 ? perspectives.get(0) : null;
        }
        if (selected != null) {
            PerspectiveManager.getDefault().setSelected(selected);
            ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
          //Load selected perspective
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
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Perspective selected = PerspectiveManagerImpl.getInstance().getSelected();
        if (selected != null) {
            PerspectivePreferences.getInstance().setSelectedPerspective(selected.getName());
            if (PerspectivePreferences.getInstance().isTrackOpened()) {
                new OpenedViewTracker(selected);
            } else {
                PerspectiveManagerImpl.getInstance().setSelected(selected);
            }
        }

    }

    public synchronized void reset() throws IOException {
        PerspectivePreferences.getInstance().reset();
        ToolbarStyleSwitchUI.getInstance().reset();

        cleanDir();
        restore();
        loadSelectedPerspective();
    }
}