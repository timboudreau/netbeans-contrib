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

import java.io.IOException;
import java.util.List;
import org.netbeans.modules.perspective.utils.PerspectiveManagerImpl;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.utils.OpenedViewTracker;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 * MainPaser.java
 * @author Anuradha G
 */
public class MainParser {

    private static final String BASE_DIR = "perspectives"; //NOI18N
    private static final String EXT = "perspective"; //NOI18N
    private static final String CONFIG_DIR = BASE_DIR + "/config"; //NOI18N
    private static final String BUILTIN_DIR = "perspective"; //NOI18N
    private static MainParser instance;
    private PerspectiveParser paser = new PerspectiveParser();
    private FileObject config;
    private FileObject perspectiveBase;
    private PerspectivePreferences perspectivePreferences = PerspectivePreferences.getInstance();

    private MainParser() {
        //Creating Parser instance
        config = Repository.getDefault().getDefaultFileSystem().findResource(CONFIG_DIR);
        perspectiveBase = Repository.getDefault().getDefaultFileSystem().findResource(BUILTIN_DIR);

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
        if (perspectiveBase != null) {
            //Loading default Perspectives from layer
            readPerspectives(perspectiveBase);
        }

        //Loading Perspectives from Config
        FileObject[] viewChildren = config.getChildren();
        for (FileObject fileObject : viewChildren) {
            try {

                PerspectiveImpl decoded = paser.decode(fileObject.getInputStream());
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
        PerspectiveImpl selected = null;
        if (id != null) {
            selected = PerspectiveManagerImpl.getInstance().findPerspectiveByID(id);
        }
        if (selected == null) {

            List<PerspectiveImpl> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
            //Loading default Perspecive as selected PerspectiveImpl
            selected = perspectives.size() > 0 ? perspectives.get(0) : null;
        }
        if (selected != null) {

            PerspectiveManagerImpl.getInstance().setSelected(selected, false);
            ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();


        }
    }

    private void readPerspectives(FileObject fo) {
        PerspectiveReader reader = new PerspectiveReader();
        for (FileObject fileObject : fo.getChildren()) {
            PerspectiveImpl p = reader.readPerspective(fileObject);
            PerspectiveManagerImpl.getInstance().registerPerspective(p, false);
        }
    }

    public synchronized void store() {
        final List<PerspectiveImpl> perspectives = PerspectiveManagerImpl.getInstance().getPerspectives();
        for (PerspectiveImpl p : perspectives) {
            try {
                p.removePerspectiveListeners();
                FileObject fileObject = config.getFileObject(p.getName(), EXT);
                if (fileObject == null) {
                    try {
                        fileObject = config.createData(p.getName(), EXT);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                paser.encode(p, fileObject.getOutputStream());
            //TODO 
            // perspectivePreferences.persistencePerspective(perspective);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        PerspectiveImpl selected = PerspectiveManagerImpl.getInstance().getSelected();
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
