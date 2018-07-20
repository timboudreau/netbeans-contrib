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

/*
 * PerspectiveManager.java
 */
package org.netbeans.modules.perspective.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.modules.perspective.Perspective;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.views.PerspectiveImpl;

/**
 *
 * @author Anuradha G
 */
public class PerspectiveManagerImpl extends PerspectiveManager {

    private static PerspectiveManagerImpl instance;
    private List<PerspectiveImpl> perspectives = new ArrayList<PerspectiveImpl>();
    private PerspectiveImpl selected;

    public static synchronized PerspectiveManagerImpl getInstance() {

        if (instance == null) {
            instance = new PerspectiveManagerImpl();
        }

        return instance;
    }

    public List<PerspectiveImpl> getPerspectives() {

        return Collections.unmodifiableList(perspectives);
    }

    private PerspectiveManagerImpl() {
    }

    public void replasePerspective(int index, PerspectiveImpl perspective) {
        perspectives.set(index, perspective);
    }

    public void registerPerspective(int index, PerspectiveImpl perspective) {
        if (perspectives.size() <= index) {
            perspectives.add(perspective);
        } else {
            perspectives.add(index, perspective);
        }
        arrangeIndexs();

    }

    public void registerPerspective(PerspectiveImpl perspective, boolean arrange) {
        //deregistor  if exist
        deregisterPerspective(perspective, arrange);
        if (perspectives.size() > perspective.getIndex()) {
            perspectives.add(perspective.getIndex(), perspective);
        } else {
            perspectives.add(perspective);
        }

        if (arrange) {
            arrangeIndexs();
        }
    }

    public void deregisterPerspective(PerspectiveImpl perspective) {
        deregisterPerspective(perspective, true);
    }

    public void deregisterPerspective(PerspectiveImpl perspective, boolean arrange) {
        perspectives.remove(perspective);
        if (arrange) {
            arrangeIndexs();
            ToolbarStyleSwitchUI.getInstance().reset();
            ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
        }
    }

    public void setSelected(Perspective perspective) {

        setSelected((PerspectiveImpl) perspective, true);
    }

    public void setSelected(final PerspectiveImpl perspective, boolean switchPerspective) {
        selected = perspective;
        if (switchPerspective &&perspective!=null) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                   ModeController.getInstance().switchView(perspective);
                }
            });
        }
        ToolbarStyleSwitchUI.getInstance().setSelected(selected);
    }

    public PerspectiveImpl getSelected() {
        return selected;
    }

    public PerspectiveImpl findPerspectiveByID(String id) {
        for (PerspectiveImpl perspective : perspectives) {
            if (perspective.getName().equals(id)) {
                return perspective;
            }
        }
        return null;
    }

    public PerspectiveImpl findPerspectiveByAlias(String alias) {
        for (PerspectiveImpl perspective : perspectives) {
            if (perspective.getAlias().equals(alias)) {
                return perspective;
            }
        }
        return null;
    }

    public void clear() {
        perspectives.clear();

        selected = null;
    }

    public void arrangeIndexs() {
        for (PerspectiveImpl perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }

    public void arrangeIndexsToExistIndexs() {
        Collections.sort(perspectives);
        for (PerspectiveImpl perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }
}
