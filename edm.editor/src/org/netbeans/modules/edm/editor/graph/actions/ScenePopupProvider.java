/*
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use this
 * file except in compliance with the License.  You can obtain a copy of the
 *  License at http://www.netbeans.org/cddl.html
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License. If applicable, add the following below the
 * CDDL Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2006 Sun Microsystems, Inc. All Rights Reserved
 *
 */

package org.netbeans.modules.edm.editor.graph.actions;

import java.awt.Point;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.graph.actions.AutoLayoutAction;
import org.netbeans.modules.edm.editor.graph.actions.EditJoinAction;

/**
 * This class implements the popup provider for the scene.
 * @author karthikeyan s
 */

public class ScenePopupProvider implements PopupMenuProvider {
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    public ScenePopupProvider(MashupDataObject dObj, MashupGraphManager manager) {
        mObj = dObj;
        this.manager = manager;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add auto layout action.
        JMenuItem layout = new JMenuItem("Auto Layout");
        layout.setAction(new AutoLayoutAction(mObj, "Auto Layout"));
        menu.add(layout);
        
        menu.addSeparator();
        
        // add edit join view action.
        JMenuItem edit = new JMenuItem("Edit Join");
        edit.setAction(new EditJoinAction(mObj, "Edit Join"));
        menu.add(edit);
        
        // Edit connection action.
        JMenuItem editDB = new JMenuItem("Edit Database Properties");
        editDB.setAction(new EditConnectionAction(mObj, "Edit Database Properties"));
        menu.add(editDB);
        
        // Edit Runtime input action.
        JMenuItem editRuntime = new JMenuItem("Edit Runtime Input Arguments");
        editRuntime.setAction(new RuntimeInputAction(mObj, "Edit Runtime Input Arguments"));
        menu.add(editRuntime);
        
        menu.addSeparator();
        
        // add run action.
        JMenuItem run = new JMenuItem("Run");
        run.setAction(new TestRunAction(mObj, "Run"));
        menu.add(run);       
        
        return menu;
    }
}