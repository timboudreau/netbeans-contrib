/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
/*
 * CurrentMultiMode.java
 *
 * Created on Aug 5, 2007, 9:18:44 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.perspective.utils;

import java.util.Set;
import org.netbeans.modules.perspective.hacks.ModeHackTopComponent;
import org.netbeans.modules.perspective.views.Perspective;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Anurdha
 */
public class CurrentPerspectiveReader {

    private WindowManager windowManager = WindowManager.getDefault();

    public CurrentPerspectiveReader(Perspective perspective) {

        genarate(perspective);
    }



    private void genarate(Perspective perspective) {
        Set<? extends Mode> modes = windowManager.getModes();
        for (Mode mode : modes) {
            if (windowManager.isEditorMode(mode)) {
                continue;
            }
            //hack
            if (mode.getName().startsWith("anonymousMode")) {//NOI18n
                if (mode.getTopComponents().length == 1 && mode.getTopComponents()[0] instanceof ModeHackTopComponent) {
                    continue;
                }
                boolean hacked = false;
                for (TopComponent tc : mode.getTopComponents()) {
                    if (tc instanceof ModeHackTopComponent) {
                        hacked = true;
                        break;
                    }
                }
                if (!hacked) {
                    mode.dockInto(new ModeHackTopComponent());
                }
            }
            TopComponent[] topComponents = mode.getTopComponents();
            for (TopComponent tc : topComponents) {
                String tcID = windowManager.findTopComponentID(tc);
                if (!(tcID.startsWith("ModeHackTopComponent")) ){//NOI18n
                    perspective.addComponent(tcID, mode.getName(), tc.isOpened());
                }
            }
            TopComponent stc = mode.getSelectedTopComponent();
            if (stc != null) {
                perspective.addActiveComponent(mode.getName(), windowManager.findTopComponentID(stc));
            }
        }
    }
}
