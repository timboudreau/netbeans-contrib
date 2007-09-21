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
 * PerspectiveManager.java
 */

package org.netbeans.modules.perspective.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.perspective.PerspectiveManager;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.netbeans.modules.perspective.views.Perspective;

/**
 *
 * @author Anuradha G
 */
public class PerspectiveManagerImpl extends PerspectiveManager{

    private static PerspectiveManagerImpl instance;
   private List<Perspective> perspectives = new ArrayList<Perspective>();
    
    private Perspective selected;

    public static synchronized PerspectiveManagerImpl getInstance() {

        if (instance == null) {
            instance = new PerspectiveManagerImpl();
        }

        return instance;
    }

    public List<Perspective> getPerspectives() {

        return Collections.unmodifiableList(perspectives);
    }

    private PerspectiveManagerImpl() {
        
    }

    public void registerPerspective(int index, Perspective perspective) {
        if (perspectives.size() <= index) {
            perspectives.add(perspective);
        } else {
            perspectives.add(index, perspective);
        }
        arrangeIndexs();
        
    }

    public void registerPerspective(Perspective perspective, boolean arrange) {
        //deregistor  if exist
        deregisterPerspective(perspective,arrange);
        if (perspectives.size() > perspective.getIndex()) {
            perspectives.add(perspective.getIndex(), perspective);
        } else {
            perspectives.add(perspective);
        }

        if (arrange) {
            arrangeIndexs();
            
        }
    }

    public void deregisterPerspective(Perspective perspective) {
        deregisterPerspective(perspective, true);
    }
    public void deregisterPerspective(Perspective perspective,boolean arrange) {
        perspectives.remove(perspective);
        if(arrange){
            arrangeIndexs();
        ToolbarStyleSwitchUI.getInstance().reset();
        ToolbarStyleSwitchUI.getInstance().loadQuickPerspectives();
        }
        
    }
    

    public void setSelected(Perspective perspective) {
        
        selected = perspective;
        ModeController.getInstance().switchView(perspective);
        ToolbarStyleSwitchUI.getInstance().setSelected(selected);
    }

    public Perspective getSelected() {
        return selected;   
    }

    public Perspective findPerspectiveByID(String id) {
        for (Perspective perspective : perspectives) {
            if (perspective.getName().equals(id)) {
                return perspective;
            }
        }
        return null;
    }
    public Perspective findPerspectiveByAlias(String alias) {
        for (Perspective perspective : perspectives) {
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
        for (Perspective perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }

    public void arrangeIndexsToExistIndexs() {
        Collections.sort(perspectives);
        for (Perspective perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }
}
