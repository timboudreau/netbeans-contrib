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

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.openide.util.Utilities;

import org.netbeans.modules.edm.editor.graph.MashupGraphManager;
import org.netbeans.modules.sql.framework.evaluators.database.DB;
import org.netbeans.modules.sql.framework.evaluators.database.DBFactory;
import org.netbeans.modules.sql.framework.evaluators.database.StatementContext;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import org.netbeans.modules.sql.framework.model.SQLJoinOperator;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.model.SourceTable;

/**
 *
 * @author karthikeyan s
 */
public class ShowSqlAction extends AbstractAction {
    
    private Object obj;
    
    private MashupGraphManager manager;
    
    private static final Image SQL_IMAGE = Utilities.loadImage(
            "org/netbeans/modules/edm/editor/resources/Show_Sql.png"); // NOI18N
    
    /** Creates a new instance of EditJoinAction */
    public ShowSqlAction(Object op, MashupGraphManager manager) {
        super("", new ImageIcon(SQL_IMAGE));
        obj = op;
        this.manager = manager;
    }
    
    public ShowSqlAction(Object op, MashupGraphManager manager, String name) {
        super(name, new ImageIcon(SQL_IMAGE));
        obj = op;
        this.manager = manager;
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            if(obj instanceof SQLJoinOperator) {
                DB db = DBFactory.getInstance().getDatabase(DB.AXIONDB);
                StatementContext context = new StatementContext();
                StringBuffer buf = new StringBuffer("SELECT ");
                SQLDBTable[] tables = (SQLDBTable[])((SQLJoinOperator)obj).getAllSourceTables().toArray(new SQLDBTable[0]);
                int i = 0;
                for(SQLDBTable table : tables) {
                    if(i++ != 0) {
                        buf.append(",");
                    }
                    String sql = db.getStatements().getSelectStatement((SourceTable) table, context).toString().trim();
                    int start = "select".length();
                    int end = sql.indexOf("from") == -1? sql.indexOf("FROM") : sql.indexOf("from");
                    sql = sql.substring(start, end).trim();
                    buf.append(sql);
                }
                buf.append(" FROM ");
                buf.append(db.getEvaluatorFactory().evaluate((SQLJoinOperator)obj, context));
                manager.setLog(buf.toString());
            } else if(obj instanceof SQLDefinition) {
                SQLDefinition defn = (SQLDefinition) obj;
                SQLJoinView[] joinViews = (SQLJoinView[])defn.getObjectsOfType(
                        SQLConstants.JOIN_VIEW).toArray(new SQLJoinView[0]);
                if(joinViews != null && joinViews.length != 0) {                    
                    DB db = DBFactory.getInstance().getDatabase(DB.AXIONDB);
                    StatementContext context = new StatementContext();
                    manager.setLog(db.getStatements().getSelectStatement(joinViews[0], context).toString().trim());
                }
            }
        } catch (Exception ex) {
            manager.setLog("Failed to evaluate join.");
        }
    }
}