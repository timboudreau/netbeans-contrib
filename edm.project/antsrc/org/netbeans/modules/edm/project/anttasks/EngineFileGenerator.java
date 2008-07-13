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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.edm.project.anttasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.lang.Exception;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

import org.netbeans.modules.etl.model.impl.ETLDefinitionImpl;
import org.netbeans.modules.sql.framework.common.jdbc.SQLDBConnectionDefinition;
import org.netbeans.modules.sql.framework.model.SQLDBModel;
import org.netbeans.modules.sql.framework.model.SQLDefinition;
import org.netbeans.modules.sql.framework.model.SQLDBTable;
import com.sun.sql.framework.utils.StringUtil;
import com.sun.sql.framework.exception.BaseException;
import org.netbeans.modules.sql.framework.model.DBConnectionDefinition;
import org.netbeans.modules.sql.framework.model.DBTable;
import org.netbeans.modules.sql.framework.model.DatabaseModel;
import org.netbeans.modules.sql.framework.codegen.DB;
import org.netbeans.modules.sql.framework.codegen.DBFactory;
import org.netbeans.modules.sql.framework.codegen.StatementContext;
import org.netbeans.modules.sql.framework.codegen.axion.AxionDB;
import org.netbeans.modules.sql.framework.codegen.axion.AxionPipelineStatements;
import org.netbeans.modules.sql.framework.codegen.axion.AxionStatements;
import org.netbeans.modules.sql.framework.model.SQLConstants;
import org.netbeans.modules.sql.framework.model.SQLJoinView;
import org.netbeans.modules.sql.framework.model.SourceTable;

/**
 * @author
 *
 */
public class EngineFileGenerator  {
    
    private FileOutputStream fos = null;
        /*
         * hold a map of otd oids to connection def
         */
    private HashMap connDefs = new HashMap();
    
    /**
     *
     */
    protected AxionDB db;
    /**
     *
     */
    protected Map<DBConnectionDefinition, String> linkTableMap;
    
    private Object FileUtil;
    
    /**
     *
     */
    protected AxionPipelineStatements pipelineStmts;
    
    /**
     *
     */
    protected AxionStatements stmts;
    
    private EDMProProcessDefinition edmProcessDef = null;
    
    private static final String EDM_FILE_EXT = ".edm";
    
    /**
     *
     */
    public EngineFileGenerator() {
        try {
            edmProcessDef = new EDMProProcessDefinition();
            db = (AxionDB) DBFactory.getInstance().getDatabase(DB.AXIONDB);
            stmts = (AxionStatements) db.getStatements();
            pipelineStmts = db.getAxionPipelineStatements();
            linkTableMap = new HashMap<DBConnectionDefinition, String>();           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void buildInitializationStatements(SQLDBTable table, Map connDefToLinkName) throws BaseException {
        DBConnectionDefinition connDef = this.getConnectionDefinition(table);
        
        
        // Generate a unique name for the DB link, ensuring that the link name is a
        // legal SQL identifier, then generate SQL statement(s).
        String linkName = StringUtil.createSQLIdentifier(connDef.getName());
        
        if (!connDefToLinkName.containsValue(linkName)) {
            this.edmProcessDef.addDbLinkSql(getCreateDBLinkSQL(connDef, linkName));
            connDefToLinkName.put(connDef, linkName);
        }
        
        StatementContext context = new StatementContext();
        context.setUsingFullyQualifiedTablePrefix(false);
        context.setUsingUniqueTableName(table, true);
        String localName = db.getUnescapedName(db.getGeneratorFactory().generate(table, context));
        String createSql = getCreateRemoteTableSQL(table, localName, linkName);
        String dropSql = this.getDropExternalTableSQL(table, localName, true, context);
        this.edmProcessDef.addVTableNode(localName, String.valueOf(table.getObjectType()), dropSql, createSql);
    }
    
    /**
     *
     * @param edmFile
     * @param buildDir
     * @throws java.lang.Exception
     */
    public void generateEngine(File edmFile, File targetDir) throws Exception {        
        String edmFileName = edmFile.getName().substring(0, edmFile.getName().indexOf(".edm"));
        String engineFile = targetDir + File.separator + edmFileName + "_engine.xml";
        
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        Element root = f.newDocumentBuilder().parse(edmFile).getDocumentElement();
        
        ETLDefinitionImpl def = new ETLDefinitionImpl();
        def.parseXML(root);
        
        SQLDefinition sqlDefinition = def.getSQLDefinition();
        populateConnectionDefinitions(sqlDefinition);
        DBConnectionDefinition tgtConnDef = ((SQLDBModel)
                sqlDefinition.getSourceDatabaseModels().get(0)).getConnectionDefinition();
               
        StatementContext joinContext = new StatementContext();
        linkTableMap.clear();
        if(requiresInit(sqlDefinition)) {
            List sources = sqlDefinition.getSourceTables();
            Iterator sourceIter = sources.iterator();
            while(sourceIter.hasNext()) {
                SourceTable st = (SourceTable) sourceIter.next();
                DBConnectionDefinition connDef = getConnectionDefinition(st);
                if(!connDef.getConnectionURL().equals(tgtConnDef.getConnectionURL())) {
                    st.setAliasName("EXT");
                    this.buildInitializationStatements((SQLDBTable)st, linkTableMap);
                    joinContext.setUsingUniqueTableName((SQLDBTable)st, true);
                }
            }
        }
        
        Collection joins = sqlDefinition.getObjectsOfType(SQLConstants.JOIN_VIEW);
        Iterator joinIter = joins.iterator();
        while(joinIter.hasNext()) {            
            SQLJoinView view = (joins.size() > 0 ) ? (SQLJoinView) joinIter.next() : null;
            if( view != null ) {
                if(requiresInit(sqlDefinition)) {
                    joinContext.setUsingFullyQualifiedTablePrefix(false);
                } else {
                    joinContext.setUsingOriginalSourceTableName(true);
                }
                StringBuffer buf = new StringBuffer(stmts.getSelectStatement(view, joinContext).getSQL());
                this.edmProcessDef.setDataMashupQuery(buf.toString());
            }
        }
        
        String engineContent = this.edmProcessDef.generateEngineFile();
        fos = new FileOutputStream(engineFile);        
        fos.write(engineContent.getBytes("UTF-8"));
        fos.flush();
        fos.close();
        
        System.out.println("processed " + engineFile);
        
    }    
    
    private void populateConnectionDefinitions(SQLDefinition def) {
        
        List trgDbmodels = def.getSourceDatabaseModels();
        Iterator iterator = trgDbmodels.iterator();
        while (iterator.hasNext()) {
            SQLDBModel element = (SQLDBModel) iterator.next();
            SQLDBConnectionDefinition originalConndef = (SQLDBConnectionDefinition) element
                    .getConnectionDefinition();
            this.edmProcessDef.setDBConnectionParameters(originalConndef);
        }
    }
    
    private boolean requiresInit(SQLDefinition sqlDefn) {
        boolean required = true;
        if(sqlDefn.getSourceDatabaseModels().size() == 1) {
            Iterator srcIt = sqlDefn.getSourceDatabaseModels().iterator();
            SQLDBModel dbModel = (SQLDBModel) srcIt.next();
            Iterator tgtIt = sqlDefn.getSourceDatabaseModels().iterator();
            if(tgtIt.hasNext()) {
                SQLDBModel tgtModel = (SQLDBModel) tgtIt.next();
                if(tgtModel.getConnectionDefinition().getConnectionURL().equals(
                        dbModel.getConnectionDefinition().getConnectionURL())) {
                    required = false;
                }
            }
        }
        return required;
    }
    
    /**
     * Generates drop external statement for the given SQLDBTable if appropriate.
     *
     * @param table SQLDBTable for which to generate a drop external statement
     * @param localName local name of table as used in the Axion database; may be
     *        different from the table name contained in <code>table</code>
     * @param ifExists true if statement should include an "IF EXISTS" qualifier
     * @param context StatementContext to use in generating statement
     * @return SQL statement representing drop external statement for SQLDBTable.
     * @throws BaseException if error occurs during statement generation
     */
    protected String getDropExternalTableSQL(SQLDBTable table, String localName,
            boolean ifExists, StatementContext context) throws BaseException {
        return stmts.getDropExternalTableStatement(table, localName, ifExists, context).getSQL();
    }   
    
    private DBConnectionDefinition getConnectionDefinition(DBTable table) throws BaseException {
        DatabaseModel dbModel = table.getParent();
        DBConnectionDefinition conDef = dbModel.getConnectionDefinition();
        return conDef;
    }
    
    protected String getCreateDBLinkSQL(DBConnectionDefinition connDef, String linkName) throws BaseException {
        StringBuffer stmtBuf = new StringBuffer(50);
        stmtBuf.append(stmts.getCreateDBLinkStatement(connDef, linkName).getSQL());
        return stmtBuf.toString();
    }
    
    private String getCreateRemoteTableSQL(SQLDBTable table, String localName, String linkName) throws BaseException {
        StringBuffer stmtBuf = new StringBuffer(50);
        if (StringUtil.isNullString(localName)) {
            localName = table.getName();
        }
        
        // Generate a "create external table" statement that references its DB link
        stmtBuf.append(stmts.getCreateRemoteTableStatement(table, localName, linkName).getSQL());
        return stmtBuf.toString();
    }    
}