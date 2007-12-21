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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.netbeans.modules.edm.editor.dataobject.MashupDataObject;
import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.edm.editor.utils.ImageConstants;
import org.netbeans.modules.edm.editor.utils.MashupGraphUtil;
import org.netbeans.modules.sql.framework.codegen.DB;
import org.netbeans.modules.sql.framework.codegen.DBFactory;
import org.netbeans.modules.sql.framework.codegen.StatementContext;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBModel;
import org.netbeans.modules.sql.framework.model.SQLJoinView;

/**
 *
 * @author karthikeyan s
 */
public class TestRunAction extends AbstractAction {
    
    private MashupDataObject mObj;
    
    private MashupGraphManager manager;
    
    /** Creates a new instance of EditJoinAction */
    public TestRunAction(MashupDataObject dObj) {
        super("",new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.RUN)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
    }
    
    public TestRunAction(MashupDataObject dObj, String name) {
        super(name,new ImageIcon(
                MashupGraphUtil.getImage(ImageConstants.RUN)));
        mObj = dObj;
        this.manager = dObj.getGraphManager();
    }
    
    public void actionPerformed(ActionEvent e) {
        SQLJoinView joinView = null;
        SQLJoinView[] joinViews = (SQLJoinView[]) mObj.getModel().getSQLDefinition().getObjectsOfType(
                SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
        if(joinViews != null && joinViews.length != 0) {
            joinView = joinViews[0];
        }
        try {
            DB db = DBFactory.getInstance().getDatabase(DB.AXIONDB);
            StatementContext context = new StatementContext();
            String sql = db.getStatements().getSelectStatement(joinView, context).toString();
            SQLDBModel dbModel = (SQLDBModel) mObj.getModel().
                    getSQLDefinition().getTargetDatabaseModels().get(0);
            manager.showOutput(joinView, mObj.getModel().getSQLDefinition());
        } catch (Exception ex) {
            manager.setLog("Failed to run collaboration");
        }
    }
}