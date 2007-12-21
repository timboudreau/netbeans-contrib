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

import org.netbeans.modules.sql.framework.model.SQLObject;

/**
 * This class implements the popup provider for the table.
 * @author karthikeyan s
 */

public class RuntimeModelPopupProvider implements PopupMenuProvider {
    
    private SQLObject obj;
    
    private MashupDataObject mObj;
    
    public RuntimeModelPopupProvider(SQLObject obj, MashupDataObject dObj) {
        this.obj = obj;
        this.mObj = dObj;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add show sql action.
        JMenuItem edit = new JMenuItem("Edit Runtime Inputs");
        edit.setAction(new RuntimeInputAction(mObj, "Edit Runtime Inputs"));
        menu.add(edit);  
        
        // add select columns action.
        JMenuItem remove = new JMenuItem("Remove Runtime Inputs");
        remove.setAction(new RemoveObjectAction(mObj, 
                mObj.getModel().getSQLDefinition().getRuntimeDbModel(), "Remove Runtime Inputs"));
        menu.add(remove);   
        
        return menu;
    }
}