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

package org.netbeans.modules.edm.editor.utils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import org.netbeans.modules.etl.ui.model.impl.ETLCollaborationModel;
import org.netbeans.modules.sql.framework.model.DBConnectionDefinition;
import org.netbeans.modules.sql.framework.model.DBMetaDataFactory;
import org.netbeans.modules.sql.framework.common.utils.DBExplorerUtil;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBModel;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLModelObjectFactory;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.impl.SourceTableImpl;

/**
 *
 * @author karthikeyan s
 */
public class MashupModelHelper {
    
    /** Creates a new instance of MashupModelHelper */
    public MashupModelHelper() {
    }
    
    /**
     *
     * @param model ETLCollaborationModel
     * @param tblModel DefaultTableModel
     * @param url String
     * @return model ETLCollaborationModel
     */
    public static ETLCollaborationModel getModel(ETLCollaborationModel model, DefaultTableModel tblModel,
            String url) {
        SQLDefinition sqlDefn = model.getSQLDefinition();
        DBMetaDataFactory meta = new DBMetaDataFactory();
        Map<String, SQLDBModel> dbModelMap = new HashMap<String, SQLDBModel>();
        try {
           for (int i = 0; i < tblModel.getRowCount(); i++) {
                String table = (String) tblModel.getValueAt(i, 0);
                String schema = (String) tblModel.getValueAt(i, 1);
                String connectionUrl = (String) tblModel.getValueAt(i, 2);
                String user = (String) tblModel.getValueAt(i, 3);
                String pass = (String) tblModel.getValueAt(i, 4);
                String driver = (String) tblModel.getValueAt(i, 5);
                Connection conn = DBExplorerUtil.createConnection(driver, connectionUrl, user, pass);
                meta.connectDB(conn);
                SQLDBModel dbModel = dbModelMap.get(connectionUrl);
                 if (dbModel == null) { {
                    dbModel = SQLModelObjectFactory.getInstance().createDBModel(
                            SQLConstants.SOURCE_DBMODEL);
                    populateModel(dbModel, driver, user, pass, connectionUrl, meta);
                }
                SourceTable srcTable = new SourceTableImpl(table, schema, "");
                meta.populateColumns(srcTable);
                dbModel.addTable(srcTable);
                dbModelMap.put(connectionUrl, dbModel);
                try {
                    meta.disconnectDB();
                } catch (Exception ex) {
                    //ignore
                }
            }
            
            // add all models.
            SQLDBModel[] models = dbModelMap.values().toArray(new SQLDBModel[0]);
            for(SQLDBModel mdl : models) {
                sqlDefn.addObject(mdl);
            }
            
            // now add the target model.
            SQLDBModel tgtModel = SQLModelObjectFactory.getInstance().createDBModel(
                    SQLConstants.TARGET_DBMODEL);
            DBConnectionDefinition def = null;
            try {
                def = SQLModelObjectFactory.getInstance().createDBConnectionDefinition(url,
                        "AXION", "org.axiondb.jdbc.AxionDriver", url, "sa", "sa", "Descriptive info here");
            } catch (Exception ex) {
                // ignore
            }
            tgtModel.setModelName(url);
            tgtModel.setConnectionDefinition(def);
            sqlDefn.addObject(tgtModel);
            model.setSQLDefinition(sqlDefn);
           }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return model;
    }
    
    public static ETLCollaborationModel getModel(ETLCollaborationModel model, DefaultTableModel tblModel) {
        SQLDefinition sqlDefn = model.getSQLDefinition();
        DBMetaDataFactory meta = new DBMetaDataFactory();
        Map<String, SQLDBModel> dbModelMap = new HashMap<String, SQLDBModel>();
        try {
            for(int i = 0; i < tblModel.getRowCount(); i++) {
                String table = (String) tblModel.getValueAt(i, 0);
                String schema = (String) tblModel.getValueAt(i, 1);
                String connectionUrl = (String) tblModel.getValueAt(i, 2);
                String user = (String) tblModel.getValueAt(i, 3);
                String pass = (String) tblModel.getValueAt(i, 4);
                String driver = (String) tblModel.getValueAt(i, 5);
                Connection conn = DBExplorerUtil.createConnection(driver, connectionUrl, user, pass);
                meta.connectDB(conn);
                SQLDBModel dbModel = dbModelMap.get(connectionUrl);
                if(dbModel == null) {
                    dbModel = SQLModelObjectFactory.getInstance().createDBModel(
                            SQLConstants.SOURCE_DBMODEL);
                   populateModel(dbModel, driver, user, pass, connectionUrl, meta);
                }
                 SourceTable srcTable = new SourceTableImpl(table, schema, "");
                  meta.populateColumns(srcTable);
                dbModel.addTable(srcTable);
                dbModelMap.put(connectionUrl, dbModel);
                try {
                    meta.disconnectDB();
                } catch (Exception ex) {
                    //ignore
                }
            }
            
            // add all models.
            SQLDBModel[] models = dbModelMap.values().toArray(new SQLDBModel[0]);
              for (SQLDBModel mdl : models) {
                sqlDefn.addObject(mdl);
            }
            
            // now add the target model.
            SQLDBModel tgtModel = SQLModelObjectFactory.getInstance().createDBModel(
                    SQLConstants.TARGET_DBMODEL);
            DBConnectionDefinition def = null;
            tgtModel.setConnectionDefinition(def);
            sqlDefn.addObject(tgtModel);
            model.setSQLDefinition(sqlDefn);
         } catch  (Exception ex) {
            ex.printStackTrace();
        }
        return model;
    }
    
    
    
    private static SQLDBModel populateModel(SQLDBModel model, String driver,
        String user, String pass, String url, DBMetaDataFactory meta) {
        DBConnectionDefinition def = null;
        try {
            def = SQLModelObjectFactory.getInstance().createDBConnectionDefinition(url,
                    meta.getDBType(), driver, url, user, pass, "Descriptive info here");
        } catch (Exception ex) {
            // ignore
        }
        model.setModelName(url);
        model.setConnectionDefinition(def);
        return model;
    }
 
    private static Object model;
    private static Object tblModel;
}