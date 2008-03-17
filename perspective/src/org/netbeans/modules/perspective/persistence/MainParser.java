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
import java.util.concurrent.Callable;
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

    private static final String LAYER_DIR = "perspectives"; //NOI18N
    private static MainParser instance;
    private FileObject perspectiveBase;
    private PerspectivePreferences perspectivePreferences = PerspectivePreferences.getInstance();

    private MainParser() {
        //Creating Parser instance
        perspectiveBase = Repository.getDefault().getDefaultFileSystem().findResource(LAYER_DIR);

    }

    public static synchronized MainParser getInstance() {
        if (instance == null) {

            instance = new MainParser();
        }
        return instance;
    }

    private void clear(FileObject fo) {
        FileObject[] children = fo.getChildren();
        try {
            for (FileObject fileObject : children) {

                if (fileObject.isFolder()) {
                    clear(fileObject);
                    fileObject.delete();
                } else {
                    fileObject.delete();
                }

            }
            fo.delete();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public synchronized void restore() {
        PerspectiveManagerImpl.getInstance().clear();
        //Loading default Perspectives from layer
        readPerspectives(perspectiveBase);
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

            PerspectiveManagerImpl.getInstance().setSelected(selected,
                    PerspectivePreferences.getInstance().isFirstLoad());
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

    public synchronized void persistPerspective(PerspectiveImpl p) {
        PerspectiveWriter perspectiveWriter = new PerspectiveWriter();
        hidePerspective(p);
        try {
            perspectiveWriter.writePerspective(perspectiveBase, p);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public synchronized void hidePerspective(PerspectiveImpl p) {
        FileObject fo = Repository.getDefault().getDefaultFileSystem().
                findResource(LAYER_DIR + "/" + p.getName());
        if (fo != null) {
            clear(fo);
        }
    }

    public synchronized void store() {
        //reset to selected
        PerspectiveImpl selected = PerspectiveManagerImpl.getInstance().getSelected();
        if (selected != null) {
            perspectivePreferences.setSelectedPerspective(selected.getName());
            if (perspectivePreferences.isTrackOpened()) {
                new OpenedViewTracker(selected);
                persistPerspective(selected);
            } else {
                PerspectiveManagerImpl.getInstance().setSelected(selected);

            }
            PerspectivePreferences.getInstance().setFirstLoad(false);
        }

    }
    
    public void resetPerspective(PerspectiveImpl p){
       FileObject fo = Repository.getDefault().getDefaultFileSystem().
                findResource(LAYER_DIR + "/" + p.getName());
        if (fo != null) {
            Callable callable = (Callable) fo.getAttribute("removeWritables");
        if (callable != null) {
            try {
                callable.call();
                PerspectiveReader reader = new PerspectiveReader();
                PerspectiveImpl newPi = reader.readPerspective(fo);
                newPi.setIndex(p.getIndex());
                PerspectiveManagerImpl.getInstance().replasePerspective(p.getIndex(), newPi);
                 PerspectiveManagerImpl.getInstance().setSelected(newPi);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        }
    
    }
    
    public synchronized void reset() throws IOException {
        perspectivePreferences.reset();
        ToolbarStyleSwitchUI.getInstance().reset();
        Callable callable = (Callable) perspectiveBase.getAttribute("removeWritables");
        if (callable != null) {
            try {
                callable.call();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        PerspectivePreferences.getInstance().setFirstLoad(true);
        restore();
    }
}
