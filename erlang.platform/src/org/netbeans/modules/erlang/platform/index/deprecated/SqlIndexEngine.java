/*
 * Copyright (c) 2005-2006, AIOTrade Computing Co.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.netbeans.modules.erlang.platform.index.deprecated;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.netbeans.fpi.gsf.Index.SearchResult;
import org.netbeans.fpi.gsf.Index.SearchScope;
import org.netbeans.fpi.gsf.IndexDocument;
import org.netbeans.fpi.gsf.NameKind;
import org.netbeans.modules.erlang.editing.semantic.ErlFunction;
import org.netbeans.modules.erlang.platform.index.ErlangGsfLanguage;
import org.netbeans.modules.gsf.Language;
import org.netbeans.modules.gsf.LanguageRegistry;
import org.openide.filesystems.FileUtil;


/**
 * This class implements persistence via NetBeans Node system
 * Actually it will be an application context
 *
 * @author Caoyuan Deng
 */
public class SqlIndexEngine extends org.netbeans.modules.gsfret.source.usages.Index {
    
    private static final int VERSION = 0;
    private static final int SUBVERSION = 109;
    private static final String NB_USER_DIR = "netbeans.user";   //NOI18N
    private static final String INDEX_DIR = "var"+File.separatorChar+"cache"+File.separatorChar+"gsf-index"+File.separatorChar+VERSION+'.'+SUBVERSION;    //NOI18N
    private static File cacheFolder;
    
    private static final String TABLE_EXISTS_MARK = Long.toString(Long.MAX_VALUE);
    private static final String MODULE_TABLE   = "MODULE";
    private static final String FUNCTION_TABLE = "FUNCTION";
    
    private Properties dbProp;
    private String dbUrl;
    private Connection conn;
    
    private static SqlIndexEngine sqlIndexengine;
    public static SqlIndexEngine create() {
        if (sqlIndexengine == null) {
            Language language = LanguageRegistry.getInstance().getLanguageByMimeType(ErlangGsfLanguage.MIME_TYPE);
            sqlIndexengine = new SqlIndexEngine(language);
        }
        return sqlIndexengine;
    }
    
    private SqlIndexEngine(Language language) {
        super(language);
        init();
    }
    
    private void init() {
        checkAndCreateDatabaseIfNecessary();
    }
    
    private void checkAndCreateDatabaseIfNecessary() {
        String strDbDriver = "org.h2.Driver";
        
        try {
            Class.forName(strDbDriver);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        cacheFolder = getCacheFolder();
        assert cacheFolder != null;
        String strDbDir = cacheFolder.getAbsolutePath();
        dbUrl = "jdbc:h2:" + strDbDir + "/db/" + "erlybird";
        
        String strDbUser     = "dbuser";
        String strDbPassword = "dbuserpwd";
        
        dbProp = new Properties();
        dbProp.put("user", strDbUser);
        dbProp.put("password", strDbPassword);
        
        /** test if database exists, if not, create it: */
        
        /** derby special properties */
        dbProp.put("create", "true");
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbProp);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        try {
            if (conn != null && ! conn.isClosed()) {
                /** check and create tables if necessary */
                if (! isTableExists(MODULE_TABLE, conn)) {
                    createModuleTable(conn);
                }
                if (! isTableExists(FUNCTION_TABLE, conn)) {
                    createFunctionTable();
                }
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        /** derby special properties */
        dbProp.remove("create");
    }
    
    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(dbUrl, dbProp);
                /**
                 * Try to set Transaction Isolation to TRANSACTION_READ_UNCOMMITTED
                 * level to get better perfomance.
                 */
                try {
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                } catch (SQLException ex) {
                    /**
                     * As not all databases support TRANSACTION_READ_UNCOMMITTED level,
                     * we should catch exception and ignore it here to avoid break the
                     * followed actions.
                     */
                }
                
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return conn;
    }
    
    /**
     * @param conn a db connection
     * @return true if exists, false if none
     */
    private synchronized boolean isTableExists(String tableName, Connection conn) {
        if (conn == null) {
            return false;
        }
        
        try {
            Statement stmt = conn.createStatement();
            
            StringBuilder sb = new StringBuilder(200);
            String existsTestStr = sb
                    .append("SELECT * FROM ").append(tableName)
                    .append(" WHERE qname = '").append(TABLE_EXISTS_MARK).append("'")
                    .toString();
            try {
                ResultSet rs = stmt.executeQuery(existsTestStr);
                if (rs.next()) {
                    return true;
                }
            } catch (SQLException ex) {
                /** may be caused by not exist, so don't need to report */
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private synchronized void createModuleTable(Connection conn) {
        if (conn == null) {
            return;
        }
        
        try {
            Statement stmt = conn.createStatement();
            
            StringBuilder sb = new StringBuilder(200);
            String stmtCreatTableStr_h2_hsqldb = sb
                    .append("CREATE CACHED TABLE ")
                    .append(MODULE_TABLE)
                    .append(" (")
                    .append("qid INTEGER NOT NULL IDENTITY(1, 1) PRIMARY KEY, ")
                    .append("qname CHAR(80) not null, ")
                    .append("qurl CHAR(80)")
                    .append(")")
                    .toString();
            
            String stmtStr = stmtCreatTableStr_h2_hsqldb;
            stmt.executeUpdate(stmtStr);
            
            /** index name in db is glode name, so, use idx_tableName_xxx to identify them */
            sb.setLength(0);
            stmtStr = sb
                    .append("CREATE INDEX idx_")
                    .append(MODULE_TABLE)
                    .append("_qname ON ")
                    .append(MODULE_TABLE)
                    .append(" (qname)")
                    .toString();
            stmt.executeUpdate(stmtStr);
            
            /** insert a mark record for testing if table exists further */
            sb.setLength(0);
            stmtStr = sb
                    .append("INSERT INTO ").append(MODULE_TABLE)
                    .append(" (qname) VALUES (")
                    .append("'").append(TABLE_EXISTS_MARK).append("'")
                    .append(")")
                    .toString();
            stmt.executeUpdate(stmtStr);
            
            stmt.close();
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public synchronized long storeModule(String name, String url) {
        long moduleId = -1;
        
        Connection conn = getConnection();
        if (conn != null) {
            name = name.replace("'", "");
            StringBuilder sb = new StringBuilder(200);
            try {
                boolean exists = false;
                String stmtStr = sb
                        .append("SELECT qid FROM ").append(MODULE_TABLE)
                        .append(" WHERE qname = '").append(name).append("'")
                        .toString();
                Statement stmt = conn.createStatement();
                try {
                    ResultSet rs = stmt.executeQuery(stmtStr);
                    if (rs.next()) {
                        exists = true;
                    }
                } catch (SQLException ex) {
                    /** may be caused by not exist, so don't need to report */
                }
                
                sb.setLength(0);
                if (exists) {
                    stmtStr = sb
                            .append("UPDATE ").append(MODULE_TABLE)
                            .append(" SET qname = ?, qurl = ? ")
                            .append( "WHERE qname = '").append(name).append("'")
                            .toString();
                } else {
                    stmtStr = sb
                            .append("INSERT INTO ").append(MODULE_TABLE)
                            .append(" (qname, qurl)")
                            .append(" VALUES (?, ?)")
                            .toString();
                }
                PreparedStatement pstmt = conn.prepareStatement(stmtStr);
                if (name.length() > 80) name = name.substring(0, 80);
                if ( url.length() > 80) url = url.substring(0, 80);
                pstmt.setString(1, name);
                pstmt.setString(2, url);
                
                pstmt.execute();
                conn.commit();
                
                sb.setLength(0);
                stmtStr = sb
                        .append("SELECT qid FROM ").append(MODULE_TABLE)
                        .append(" WHERE qname = '").append(name).append("'")
                        .toString();
                try {
                    ResultSet rs = stmt.executeQuery(stmtStr);
                    if (rs.next()) {
                        moduleId = rs.getLong("qid");
                        /**
                         * In case of module is new inserted or updated, just clean
                         * all functions belongs to it for later functions update
                         */
                        sb.setLength(0);
                        stmtStr = sb
                                .append("DELETE FROM ").append(FUNCTION_TABLE)
                                .append(" WHERE qmodule_id = ?")
                                .toString();
                        pstmt = conn.prepareStatement(stmtStr);
                        pstmt.setLong(1, moduleId);
                        
                        pstmt.execute();
                    }
                } catch (SQLException ex) {
                    /** may be caused by not exist, so don't need to report */
                }
                
                stmt.close();
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return moduleId;
    }
    
    public synchronized String searchModuleUrl(String name) {
        String result = null;
        Connection conn = getConnection();
        if (conn != null) {
            name = name.replace("'", "");
            StringBuilder sb = new StringBuilder(200);
            try {
                String strStmt = sb
                        .append("SELECT * FROM ").append(MODULE_TABLE)
                        .append(" WHERE qname = '").append(name).append("'")
                        .toString();
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(strStmt);
                if (rs.next()) {
                    result = rs.getString("qurl");
                }
                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    private Statement searchModulesStmt;
    private Collection<String> modulesBuf = new ArrayList<String>();
    public synchronized Collection<String> searchModules() {
        modulesBuf.clear();
        Connection conn = getConnection();
        if (conn == null) {
            return modulesBuf;
        }
        
        StringBuilder sb = new StringBuilder(200);
        String strStmt = sb
                .append("SELECT qname FROM ").append(MODULE_TABLE)
                .toString();
        try {
            if (searchModulesStmt == null || searchModulesStmt.getConnection() == conn) {
                searchModulesStmt = conn.createStatement();
            }
            ResultSet rs = searchModulesStmt.executeQuery(strStmt);
            while (rs.next()) {
                String module = rs.getString("qname");
                if (module.length() > 0 && ! module.equals(TABLE_EXISTS_MARK)) {
                    if (Character.isUpperCase(module.charAt(0))) {
                        sb.setLength(0);
                        module = sb.append("'").append(module).append("'").toString();
                    }
                    modulesBuf.add(module);
                }
            }
            rs.close();
            return modulesBuf;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return modulesBuf;
    }
    
    /**
     * @return connection for following usage
     */
    private synchronized Connection createFunctionTable() {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }
        
        try {
            Statement stmt = conn.createStatement();
            
            /**
             * Only one identity column is allowed in each table. Identity
             * columns are autoincrement columns. They must be of INTEGER or
             * BIGINT type and are automatically primary key columns (as a
             * result, multi-column primary keys are not possible with an
             * IDENTITY column present)
             */
            StringBuilder sb = new StringBuilder(200);
            String stmtCreatTableStr_h2_hsqldb = sb
                    .append("CREATE CACHED TABLE ")
                    .append(FUNCTION_TABLE)
                    .append(" (")
                    .append("qid INTEGER NOT NULL IDENTITY(1, 1) PRIMARY KEY, ") // IDENTITY(startInt, incrementInt)
                    .append("qname CHAR(80) not null, ")
                    .append("qoffset INTEGER, ")
                    .append("qarity SMALLINT, ")
                    .append("qmodule_id INTEGER")
                    .append(")")
                    .toString();
            
            String stmtStr = stmtCreatTableStr_h2_hsqldb;
            stmt.executeUpdate(stmtStr);
            
            /** index name in db is glode name, so, use idx_tableName_xxx to identify them */
            sb.setLength(0);
            stmtStr = sb
                    .append("CREATE INDEX idx_")
                    .append(FUNCTION_TABLE)
                    .append("_qname ON ")
                    .append(FUNCTION_TABLE)
                    .append(" (qname)")
                    .toString();
            stmt.executeUpdate(stmtStr);
            
            sb.setLength(0);
            stmtStr = sb
                    .append("CREATE INDEX idx_")
                    .append(FUNCTION_TABLE)
                    .append("_qmodule_id ON ")
                    .append(FUNCTION_TABLE)
                    .append(" (qmodule_id)")
                    .toString();
            stmt.executeUpdate(stmtStr);
            
            /** insert a mark record for testing if table exists further */
            sb.setLength(0);
            stmtStr = sb
                    .append("INSERT INTO ").append(FUNCTION_TABLE)
                    .append(" (qname) VALUES (").append(TABLE_EXISTS_MARK).append(")")
                    .toString();
            stmt.executeUpdate(stmtStr);
            
            stmt.close();
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return conn;
    }
    
    private PreparedStatement storeFunctionsStmt;
    public synchronized void storeFunctions(Collection<ErlFunction> functions, long moduleId) {
        Connection conn = getConnection();
        if (conn != null) {
            StringBuilder sb = new StringBuilder(200);
            String stmtStr = sb
                    .append("INSERT INTO ").append(FUNCTION_TABLE)
                    .append(" (qname, qoffset, qarity, qmodule_id)")
                    .append(" VALUES (?, ?, ?, ?)")
                    .toString();
            try {
                if (storeFunctionsStmt == null || storeFunctionsStmt.getConnection() != conn) {
                    storeFunctionsStmt = conn.prepareStatement(stmtStr);
                }
                for (ErlFunction function : functions) {
                    String name = function.getName();
                    name = name.replace("'", "");
                    if (name.length() > 80) name = name.substring(0, 80);
                    storeFunctionsStmt.setString(1, name);
                    storeFunctionsStmt.setInt   (2, function.getOffset());
                    storeFunctionsStmt.setInt   (3, function.getArity());
                    storeFunctionsStmt.setLong  (4, moduleId);
                    
                    storeFunctionsStmt.addBatch();
                }
                storeFunctionsStmt.executeBatch();
                
                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private Statement searchFunctionsStmt;
    private Collection<ErlFunction> functionsBuf = new ArrayList<ErlFunction>();
    public synchronized Collection<ErlFunction> searchFunctions(String module) {
        functionsBuf.clear();
        Connection conn = getConnection();
        if (conn != null) {
            module = module.replace("'", "");
            StringBuilder sb = new StringBuilder(200);
            String strStmt = sb
                    .append("SELECT b.* FROM ").append(MODULE_TABLE).append(" AS a")
                    .append(" LEFT JOIN ").append(FUNCTION_TABLE).append(" AS b")
                    .append(" WHERE a.qid = b.qmodule_id AND ")
                    .append(" a.qname = '").append(module).append("'")
                    .toString();
            try {
                if (searchFunctionsStmt == null || searchFunctionsStmt.getConnection() != conn) {
                    searchFunctionsStmt = conn.createStatement();
                }
                ResultSet rs = searchFunctionsStmt.executeQuery(strStmt);
                while (rs.next()) {
                    String name = rs.getString("qname");
                    if (name.length() > 0) {
                        if (Character.isUpperCase(name.charAt(0))) {
                            sb.setLength(0);
                            name = sb.append("'").append(name).append("'").toString();
                        }
                        ErlFunction function = new ErlFunction(
                                name, rs.getInt("qoffset"), 0, rs.getInt("qarity"));
                        functionsBuf.add(function);
                    }
                }
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return functionsBuf;
    }
    
    public void closeConnection() {
        try {
            if (conn != null && ! conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            
        }
    }
    
    public void test(String sql) {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    System.out.println(rs.toString());
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private synchronized void deleteFunctions(long moduleId) {
        Connection conn = getConnection();
        if (conn == null) {
            return;
        }
        
        try {
            String stmtStr = new StringBuilder(100)
                    .append("DELETE FROM ").append(FUNCTION_TABLE)
                    .append(" WHERE qmodule_id = ?")
                    .toString();
            
            PreparedStatement pstmt = conn.prepareStatement(stmtStr);
            
            pstmt.setLong(1, moduleId);
            pstmt.execute();
            
            pstmt.close();
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void shutdown() {
        /**
         * Derby special action:
         *
         * In embedded mode, an application should shut down Derby.
         * If the application fails to shut down Derby explicitly,
         * the Derby does not perform a checkpoint when the JVM shuts down,
         * which means that the next connection will be slower.
         * Explicitly shutting down Derby with the URL is preferred.
         * This style of shutdown will always throw an "exception".
         *
         * --------------------------------------------------------
         *
         * For h2 or hsqldb and many other databases:
         *
         * By default, a database is closed when the last connection is closed.
         * However, if it is never closed, the database is closed when the
         * virtual machine exists normally.
         *
         */
        
        boolean SQLExGot = false;
        try {
            dbProp.put("shutdown", "true");
            Connection conn = DriverManager.getConnection(dbUrl, dbProp);
        } catch (SQLException ex) {
            SQLExGot = true;
        }
        
        if (SQLExGot == true) {
            /** shutdown sucessfully */
        }
        
    }
    
    /**
     *  Returns non cached netbeans user dir.
     *  For performance reasons the returned {@link File} is not normalized.
     *  Client is responsible to call {@link FileUtil.normalizeFile}
     *  before using the returned value.
     *  @return netbeans user dir.
     */
    static String getNbUserDir() {
        final String nbUserProp = System.getProperty(NB_USER_DIR);
        return nbUserProp;
    }
    
    
    private static synchronized File getCacheFolder() {
        if (cacheFolder == null) {
            final String nbUserDirProp = getNbUserDir();
            assert nbUserDirProp != null;
            final File nbUserDir = new File(nbUserDirProp);
            cacheFolder = FileUtil.normalizeFile(new File(nbUserDir, INDEX_DIR));
            if (!cacheFolder.exists()) {
                boolean created = cacheFolder.mkdirs();
                assert created : "Cannot create cache folder";  //NOI18N
            } else {
                assert cacheFolder.isDirectory() && cacheFolder.canRead() && cacheFolder.canWrite();
            }
        }
        return cacheFolder;
    }
    
    public void close() throws IOException {
        shutdown();
    }
    
    public void clear() throws IOException {
        /** @TODO */
    }
    
    public boolean isUpToDate(String resourceName, long timeStamp) throws IOException {
        /** @TODO */
        return false;
    }
    
    public boolean isValid(boolean tryOpen) throws IOException {
        /** @TODO */
        return true;
    }

    @Override
    public void search(String key, String name, NameKind kind, Set<SearchScope> scope, Set<SearchResult> result, Set<String> includeKeys) throws IOException {
        /** @TODO */;
    }
    
    
    public void store(String fileUrl, List<IndexDocument> documents) throws IOException {
        /** @TODO */
    }

    @Override
    public Map<String, String> getTimeStamps() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
}

