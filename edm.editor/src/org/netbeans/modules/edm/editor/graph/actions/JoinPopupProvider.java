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
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;

/**
 * This class implements the popup provider for the join.
 * @author karthikeyan s
 */

public class JoinPopupProvider implements PopupMenuProvider {
    
    private SQLJoinOperator joinOp;
    
    private MashupGraphManager manager;
    
    private MashupDataObject mObj;
    
    public JoinPopupProvider(SQLJoinOperator op, MashupDataObject dObj) {
        joinOp = op;
        this.manager = dObj.getGraphManager();
        this.mObj = dObj;
    }
    
    public JPopupMenu getPopupMenu(Widget widget, Point point) {
        JPopupMenu menu = new JPopupMenu();
        
        // add edit join condition action.
        JMenuItem editJoinCondition = new JMenuItem("Edit Join Condition");
        editJoinCondition.setAction(new EditJoinConditionAction(mObj, joinOp, "Edit Join Condition"));
        menu.add(editJoinCondition);
        
        return menu;
    }
}