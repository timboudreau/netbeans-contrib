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
import org.netbeans.modules.sql.framework.model.impl.SQLGroupByImpl;

/**
 * This class implements the popup provider for the group by operator.
 * @author karthikeyan s
 */

public class GroupByPopupProvider implements PopupMenuProvider {
    
    private SQLGroupByImpl grpby;
    
    private MashupDataObject mObj;
    
    /*
     *  Creates an instance of groupby popup provider
     */ 
    public GroupByPopupProvider(SQLGroupByImpl op, MashupDataObject dObj) {
        grpby = op;
        this.mObj = dObj;
    }
    
    /*
     * return the popup menu for this widget type.
     */ 
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add show sql action.
        JMenuItem showSQL = new JMenuItem("Show SQL");
        showSQL.setAction(new ShowSqlAction(grpby, mObj.getGraphManager(), "Show SQL"));
        menu.add(showSQL);     
        
        menu.addSeparator();
        
        // add edit having condition action.
        JMenuItem editHavingCondition = new JMenuItem("Edit Having Condition");
        editHavingCondition.setAction(new EditHavingConditionAction(mObj, grpby, "Edit Having Condition"));
        menu.add(editHavingCondition);
        
//        // add select column action.
//        JMenuItem selectColumns = new JMenuItem("Select Columns");
//        selectColumns.setAction(new GroupBySelectColumnsAction(mObj,grpby, "Select Columns"));
//        menu.add(selectColumns);        
        
        return menu;
    }
}