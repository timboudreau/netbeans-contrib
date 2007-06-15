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

import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

import org.netbeans.modules.sql.framework.model.SQLObject;

/**
 * This class implements the popup provider for the table.
 * @author karthikeyan s
 */

public class TablePopupProvider implements PopupMenuProvider {
    
    private SQLObject obj;
    
    private MashupGraphManager manager;
    
    private MashupDataObject mObj;
    
    public TablePopupProvider(SQLObject obj, MashupDataObject dObj) {
        this.obj = obj;
        this.manager = dObj.getGraphManager();
        this.mObj = dObj;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add show sql action.
        JMenuItem showData = new JMenuItem("Show Data");
        showData.setAction(new ShowDataAction(mObj, obj, "Show Data"));
        menu.add(showData);  
        
        // add show sql action.
        JMenuItem showSQL = new JMenuItem("Show SQL");
        showSQL.setAction(new ShowSqlAction(obj, mObj.getGraphManager(), "Show SQL"));
        menu.add(showSQL);          
        
        menu.addSeparator();
        
        // add select columns action.
        JMenuItem selectColumns = new JMenuItem("Select Columns");
        selectColumns.setAction(new SelectColumnsAction(mObj, obj, "Select Columns"));
        menu.add(selectColumns);   
        
        // add data extraction action
        JMenuItem dataExtraction = new JMenuItem("Filter Condition");
        dataExtraction.setAction(new ExtractionConditionAction(mObj, obj, "Filter Condition"));
        menu.add(dataExtraction);          
        
        menu.addSeparator();
        
        // add remove table action
        JMenuItem remove = new JMenuItem("Remove Table");
        remove.setAction(new RemoveObjectAction(mObj, obj, "Remove Table"));
        menu.add(remove);      
        
         menu.addSeparator();
                     
        return menu;
    }
}