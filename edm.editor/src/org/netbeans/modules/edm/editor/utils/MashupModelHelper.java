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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import org.netbeans.modules.etl.ui.model.impl.ETLCollaborationModel;
import org.netbeans.modules.jdbc.builder.DBMetaData;
import org.netbeans.modules.jdbc.builder.ForeignKeyColumn;
import org.netbeans.modules.jdbc.builder.KeyColumn;
import org.netbeans.modules.jdbc.builder.Table;
import org.netbeans.modules.jdbc.builder.TableColumn;
import org.netbeans.modules.model.database.DBConnectionDefinition;
import org.netbeans.modules.sql.framework.common.utils.DBExplorerConnectionUtil;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLDBModel;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLModelObjectFactory;
import org.netbeans.modules.sql.framework.model.SourceTable;
import org.netbeans.modules.sql.framework.model.impl.ForeignKeyImpl;
import org.netbeans.modules.sql.framework.model.impl.PrimaryKeyImpl;
import org.netbeans.modules.sql.framework.model.impl.SourceColumnImpl;
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
        DBMetaData meta = new DBMetaData();
        Map<String, SQLDBModel> dbModelMap = new HashMap<String, SQLDBModel>();
        try {
            for(int i = 0; i < tblModel.getRowCount(); i++) {
                String table = (String) tblModel.getValueAt(i, 0);
                String schema = (String) tblModel.getValueAt(i, 1);
                String connectionUrl = (String) tblModel.getValueAt(i, 2);
                String user = (String) tblModel.getValueAt(i, 3);
                String pass = (String) tblModel.getValueAt(i, 4);
                String driver = (String) tblModel.getValueAt(i, 5);
                Connection conn = DBExplorerConnectionUtil.createConnection(driver, connectionUrl, user, pass);
                meta.connectDB(conn);
                SQLDBModel dbModel = dbModelMap.get(connectionUrl);
                if(dbModel == null) {
                    dbModel = SQLModelObjectFactory.getInstance().createDBModel(
                            SQLConstants.SOURCE_DBMODEL);
                    populateModel(dbModel, driver, user, pass, connectionUrl, meta);
                }
                SourceTable srcTable = createTable(table, schema, connectionUrl, meta);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return model;
    }
    
    private static SQLDBModel populateModel(SQLDBModel model, String driver,
            String user, String pass, String url, DBMetaData meta) {
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
    
    private static SourceTable createTable(String table, String sch,
            String connectionUrl, DBMetaData meta) throws Exception {
        SourceTable ffTable = new SourceTableImpl(table, sch, "");
        Table t = meta.getTableMetaData(((SourceTableImpl)ffTable).getCatalog(),
                ((SourceTableImpl)ffTable).getSchema(), ((SourceTableImpl)ffTable).getName(), "TABLE");
        meta.checkForeignKeys(t);
        meta.checkPrimaryKeys(t);
        TableColumn[] cols = t.getColumns();
        TableColumn tc = null;
        List pks = t.getPrimaryKeyColumnList();
        List<String> pkCols = new ArrayList<String>();
        Iterator it = pks.iterator();
        while(it.hasNext()) {
            KeyColumn kc = (KeyColumn)it.next();
            pkCols.add(kc.getColumnName());
        }
        if(pks.size()!=0) {
            PrimaryKeyImpl pkImpl = new PrimaryKeyImpl(((KeyColumn)t.getPrimaryKeyColumnList().get(0)).getName(), pkCols, true);
            ((SourceTableImpl)ffTable).setPrimaryKey(pkImpl);
            
        }
        List fkList = t.getForeignKeyColumnList();
        it = fkList.iterator();
        while(it.hasNext()) {
            ForeignKeyColumn fkCol = (ForeignKeyColumn)it.next();
            ForeignKeyImpl fkImpl = new ForeignKeyImpl((SQLDBTable)ffTable, fkCol.getName(), fkCol.getImportKeyName(),
                    fkCol.getImportTableName(), fkCol.getImportSchemaName(), fkCol.getImportCatalogName(), fkCol.getUpdateRule(),
                    fkCol.getDeleteRule(), fkCol.getDeferrability());
            List<String> fkColumns = new ArrayList<String>();
            fkColumns.add(fkCol.getColumnName());
            String catalog = fkCol.getImportCatalogName();
            if (catalog == null) {
                catalog = "";
            }
            String schema = fkCol.getImportSchemaName();
            if(schema == null) {
                schema = "";
            }
            pks = meta.getPrimaryKeys(catalog, schema, fkCol.getImportTableName());
            List<String> pkColumns = new ArrayList<String>();
            Iterator pksIt = pks.iterator();
            while(pksIt.hasNext()) {
                KeyColumn kc = (KeyColumn)pksIt.next();
                pkColumns.add(kc.getColumnName());
            }
            fkImpl.setColumnNames(fkColumns, pkColumns);
            ((SourceTableImpl)ffTable).addForeignKey(fkImpl);
        }
        for (int j = 0; j < cols.length; j++) {
            tc = cols[j];
            SourceColumnImpl ffColumn = new SourceColumnImpl(tc.getName(), tc
                    .getSqlTypeCode(), tc.getNumericScale(), tc
                    .getNumericPrecision(), tc
                    .getIsPrimaryKey(), tc.getIsForeignKey(),
                    false /* isIndexed */, tc.getIsNullable());
            ffColumn.setVisible(true);
            ((SourceTableImpl)ffTable).addColumn(ffColumn);
            
        }
        ((SourceTableImpl)ffTable).setEditable(true);
        ((SourceTableImpl)ffTable).setSelected(true);
        return ffTable;
    }    
}