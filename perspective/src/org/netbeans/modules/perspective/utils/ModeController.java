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
 */ /*
 * ModeController.java
 *
 */

package org.netbeans.modules.perspective.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.perspective.persistence.PerspectivePreferences;
import org.netbeans.modules.perspective.views.PerspectiveImpl;
import org.netbeans.modules.perspective.PerspectiveListener;
import org.netbeans.modules.perspective.hacks.ModeHackTopComponent;
import org.netbeans.modules.perspective.views.PerspectiveMode;
import org.netbeans.modules.perspective.views.View;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Anuradha G
 */
class ModeController {

    private WindowManager windowManager;
    private static ModeController instance;

    static synchronized ModeController getInstance() {
        if (instance == null) {
            instance = new ModeController();
        }
        return instance;
    }

    private ModeController() {
        windowManager = WindowManager.getDefault();
    }
    private void setActiveView(View  view){
        TopComponent topComponent = windowManager.findTopComponent(view.getTopcomponentID());
        if(topComponent!=null)
            topComponent.requestActive();
    }
    private void dock(String mode, String id, boolean open, int index) {
        Mode windowMode = windowManager.findMode(mode);

        TopComponent topComponent = windowManager.findTopComponent(id);

        if (windowMode == null || topComponent == null) {
            return;
        }
        Mode old=windowManager.findMode(topComponent);
        if (old.getName().startsWith("anonymousMode")) {//NOI18N              
            if(old.getTopComponents().length<=1)    
            old.dockInto(new ModeHackTopComponent());
            }
        if (!windowMode.equals(old)) {
            windowMode.dockInto(topComponent);
        }
        if (open) {
            topComponent.openAtTabPosition(index);
            
        }
    }
    PerspectiveImpl selected;

    public void switchView(PerspectiveImpl perspective) {
        List<PerspectiveListener> perspetiveListners ;
        if (perspective == null) {
            return;
        }
        if (selected != null) {
            //Notify closing
            perspetiveListners = selected.getPerspectiveListeners();
            for (PerspectiveListener perspetiveListner : perspetiveListners) {
                perspetiveListner.perspetiveOpening();
            }
            if (PerspectivePreferences.getInstance().isTrackOpened()) {
                //track opened Tc to perspective
                new OpenedViewTracker(selected);
            }
        }
        perspetiveListners = perspective.getPerspectiveListeners();
        for (PerspectiveListener perspetiveListner : perspetiveListners) {
            perspetiveListner.perspetiveOpening();
        }

        selected = perspective;
        if (PerspectivePreferences.getInstance().isCloseOpened()) {
            //close opened TC's
            closeAll();
        }
        Set<PerspectiveMode> perspectiveModes = perspective.getPerspectiveModes();
        for (PerspectiveMode perspectiveMode : perspectiveModes) {
            Set<View> views = perspectiveMode.getViews();
            for (View view : views) {
                 dock(perspectiveMode.getId(), view.getTopcomponentID(),
                         view.isOpen(), view.getIndex());
            }
            View active=perspectiveMode.getActiveView();
            if(active!=null)
                setActiveView(active);
        }

        //end switch
    }

    private void closeAll() {
        Set<TopComponent> opened = windowManager.getRegistry().getOpened();
        List<TopComponent> tcs = new ArrayList<TopComponent>(opened);
        for (TopComponent tc : tcs) {

            if (windowManager.isEditorTopComponent(tc)) {
                continue;
            }
            tc.close();
        }
    }
    
}
