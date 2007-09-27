/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.zeroadmin.server;

import java.sql.*;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


/**
 * Handles the retrieval and storing of NetBeans configuration of a user.
 * This class saves the data as a CLOB to the SQL database.
 *
 * @author CLi
 * @version 1.0
 */

public class DBHelper {
//============================================================================
// Attributes
//============================================================================

    private final String _dbURL;                // The database URL
    private final String _dbDriver;             // The database driver
    private Connection _dbConn;                 // The database connection
    
//============================================================================
// Constructors
//============================================================================
    
    /**
     * Creates a new DBHelper. This constructor is meant for servlets and EJBs.
     *
     * @param dbURL             the database URL
     */
    public DBHelper( String dbURL ) {
        _dbURL = dbURL;
        _dbDriver = null;
    }
    
    /**
     * Creates a new DBHelper. This constructor is meant for JDBC clients.
     *
     * @param dbDriver          the class name of the database driver
     * @param dbURL             the database URL
     */
    public DBHelper( String dbDriver, String dbURL ) {
        _dbURL = dbURL;
        _dbDriver = dbDriver;
    }
    
//============================================================================
// Public methods
//============================================================================
    
    /**
     * Connect to the database. The previous active connection will be dropped.
     *
     * @throws SQLException     if the connection fails
     */
    public void connect()
    throws SQLException {
        if ( _dbConn != null ) {
            closeConnection( _dbConn );
        }
        
        _dbConn = openConnection( _dbURL, new Properties() );
    }
    
    /**
     * Close connection to database.
     *
     * @throws SQLException     if the closing fails
     */
    public void close()
    throws SQLException {
        if ( _dbConn != null ) {
            closeConnection( _dbConn );
        }
    }
    
    /**
     * Return treu if we are connected.
     */
    public boolean isConnected() {
        boolean isConnected = false;
        
        try {
            if ( _dbConn != null && !_dbConn.isClosed() ) {
                isConnected = true;
            }
        } catch( Exception e ) {
            // Do nothing if isClosed fails...
        }
        
        return isConnected;
    }
    
    /**
     * Initializes the database for the user.
     *
     * @param userName          the user name
     */
    public void initialize( String userName ) {
        if ( _dbConn != null ) {
            String tableSQL = "CREATE TABLE DES_NB_CONFIG " +
                    "(nbconfig_user VARCHAR2(32) PRIMARY KEY, " +
                    "nbconfig_data CLOB)";
            
            String insertSQL = "INSERT INTO DES_NB_CONFIG(nbconfig_user, " +
                    "nbconfig_data) VALUES(?,?)";
            
            try {
                createTable( tableSQL, _dbConn );
            } catch( Exception e ) {
                // Do nothing if table exists or some other error...
            }
            
            try {
                // Insert empty data for user
                saveClob( insertSQL, userName, "", _dbConn );
            } catch( Exception e ) {
                // Do nothing if row exists or some other error...
            }
        }
    }
    
    /**
     * Stores the given string as a CLOB in the database.
     *
     * @param userName          the user name
     * @param data              the data to store
     *
     * @throws SQLException     if the database operation fails
     */
    public void saveData( String userName, String data )
    throws SQLException {
        if ( _dbConn != null ) {
            String updateSQL = "UPDATE DES_NB_CONFIG SET " +
                    "nbconfig_data = ? WHERE " +
                    "nbconfig_user = ?";
            
            saveClob( updateSQL, userName, data, _dbConn );
        }
    }
    
    /**
     * Retrieves the CLOB of the given user from the database.
     *
     * @param userName          the user name
     * @return                  the user data or an empty string
     *
     * @throws SQLException     if the database operation fails
     */
    public String loadData( String userName )
    throws SQLException {
        String data = "";
        
        if ( _dbConn != null ) {
            String fetchSQL = "SELECT nbconfig_data FROM DES_NB_CONFIG WHERE " +
                    "nbconfig_user = ?";
            
            data = loadClob( fetchSQL, userName, _dbConn );
        }
        
        return data;
    }
    
    /**
     * Deletes the configuration row of the given user from the database.
     *
     * @param userName          the user name
     *
     * @throws SQLException     if the database operation fails
     */
    public void deleteData( String userName )
    throws SQLException {
        if ( _dbConn != null ) {
            String modifySQL = "DELETE FROM DES_NB_CONFIG WHERE " +
                    "nbconfig_user = ?";
            
            PreparedStatement pstmt = _dbConn.prepareStatement( modifySQL );
            
            try {
                pstmt.setString( 1, userName );
                pstmt.executeUpdate();
            } catch( SQLException se ) {
                throw se;
            } finally {
                try {
                    if ( pstmt != null ) {
                        pstmt.close();
                    }
                } catch( Exception e ) {
                    System.err.println( "failed to release SQL statement: " +
                            e.toString() );
                    
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Retrieves the configuration storage users.
     * @return                  the list of user names
     *
     * @throws SQLException     if the database operation fails
     */
    public List loadUsers() throws SQLException {
        List data = null;
        
        if ( _dbConn != null ) {
            data = loadUsers( _dbConn );
        }
        
        return data;
    }
    
//============================================================================
// Protected methods
//============================================================================
    
    /**
     * Opens a new database connection. The given URL can represent a JDBC
     * URL or an DataSource URL used in an application server. For example:
     * <pre>
     * java:comp/env/jdbc/nbconfig_datasource (used in a servlet)
     * jdbc:oracle:thin:user/passwd@zeus:1521:mydatabase (used in a JDBC client)
     * </pre>
     * The props parameter is only used for an URL starting with "jdbc:".
     *
     * @param dbURL             the database URL used to connect
     * @param props             the custom properties used for the connection
     * @return                  the new database connection
     *
     * @throws SQLException     if the connection opening fails
     */
    protected Connection openConnection( String dbURL, Properties props )
    throws SQLException {
        Connection conn;
        
        // Sanity checks
        if ( dbURL == null  ) {
            throw new IllegalArgumentException(
                    "openConnection does not accept a null dbURL parameter." );
        }
        
        if ( !dbURL.startsWith( "jdbc:" ) && !dbURL.startsWith( "java:" ) ) {
            throw new IllegalArgumentException(
                    "dbURL parameter must start with 'jdbc:' or 'java:'." );
        }
        
        // Different mechanism depending on URL
        if ( dbURL.startsWith( "jdbc:" ) ) {
            // Check if driver has been registered
            Enumeration enum = DriverManager.getDrivers();
            
            while ( enum.hasMoreElements() ) {
                Driver driverObj = ( Driver )enum.nextElement();
                
                if ( driverObj.getClass().getName().equals( _dbDriver ) ) {
                    DriverManager.deregisterDriver( driverObj );
                    break;
                }
            }
            
            try {
                // Load driver (and let it register itself with the DriverManager)
                Class.forName( _dbDriver );
            } catch( ClassNotFoundException cnfe ) {
                cnfe.printStackTrace();
                throw new SQLException( cnfe.toString() );
            }
            
            conn = DriverManager.getConnection( _dbURL, props );
        } else {
            try {
                InitialContext ctx = new InitialContext();
                Object dsObj = ctx.lookup( dbURL );
                DataSource ds = ( DataSource )dsObj;
                conn = ds.getConnection();
            } catch( NamingException ne ) {
                ne.printStackTrace();
                throw new SQLException( ne.toString() );
            }
        }
        
        return conn;
    }
    
    /**
     * Closes and frees resources held by the given connection.
     *
     * @param dbConnection      the active database connection
     *
     * @throws SQLException     if the connection closing fails
     */
    protected void closeConnection( Connection dbConnection )
    throws SQLException {
        // Sanity check
        if ( dbConnection == null ) {
            throw new IllegalArgumentException(
                    "closeConnection does not accept a null dbConnection parameter." );
        }
        
        if ( !dbConnection.isClosed() ) {
            if ( !dbConnection.getAutoCommit() &&
                    !dbConnection.isReadOnly() ) {
                try {
                    dbConnection.commit();
                } catch( SQLException sqle ) {
                    System.err.println( "failed to commit changes: " +
                            sqle.toString() );
                    
                    sqle.printStackTrace();
                }
            }
            
            dbConnection.close();
        }
    }
    
    /**
     * Creates the NetBeans configuration data table.
     *
     * @param dbTableSQL        the table creation SQL string
     * @param dbConnection      the active database connection
     *
     * @throws SQLException     if the creation fails
     */
    protected void createTable( String dbTableSQL, Connection dbConnection )
    throws SQLException {
        Statement stmt = dbConnection.createStatement();
        
        try {
            stmt.executeUpdate( dbTableSQL );
        } catch( SQLException se ) {
            throw se;
        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL statement: " +
                        e.toString() );
                
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Updates or inserts the configuration data of the given user in the
     * database. The update or insert SQL string should contain one placeholder
     * for the user name and another for the actual CLOB data.
     *
     * @param dbModifySQL       the update or insert SQL string
     * @param dbUser            the user name
     * @param dbData            the configuration data
     * @param dbConnection      the active database connection
     *
     * @throws SQLException     if the update or insert fails
     */
    protected void saveClob( String dbModifySQL, String dbUser,
            String dbData, Connection dbConnection ) throws SQLException {
        PreparedStatement pstmt = dbConnection.prepareStatement( dbModifySQL );
        
        try {
            pstmt.setString( 1, dbUser );
            pstmt.setString( 2, dbData );
            pstmt.executeUpdate();
        } catch( SQLException se ) {
            throw se;
        } finally {
            try {
                if ( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL statement: " +
                        e.toString() );
                
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Retrieves the configuration data of the given user from the
     * database. The fetch SQL string should contain a placeholder
     * for the user name.
     *
     * @param dbFetchSQL        the select SQL string
     * @param dbUser            the user name
     * @param dbConnection      the active database connection
     * @return                  the CLOB data or an empty string
     *
     * @throws SQLException     if the select fails
     */
    protected String loadClob( String dbFetchSQL, String dbUser,
            Connection dbConnection ) throws SQLException {
        String data = "";
        ResultSet rset = null;
        PreparedStatement pstmt = dbConnection.prepareStatement( dbFetchSQL );
        
        try {
            pstmt.setString( 1, dbUser );
            rset = pstmt.executeQuery();
            
            while ( rset.next() ) {
                data = rset.getString( 1 );
            }
            
            if ( data == null ) {
                data = "";
            }
        } catch( SQLException se ) {
            throw se;
        } finally {
            try {
                if ( rset != null ) {
                    rset.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL result set: " +
                        e.toString() );
                
                e.printStackTrace();
            }
            
            try {
                if ( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL statement: " +
                        e.toString() );
                
                e.printStackTrace();
            }
        }
        
        return data;
    }
    
    /**
     * Retrieves configuration data users.
     *
     * @param dbConnection      the active database connection
     * @return                  the list of names
     *
     * @throws SQLException     if the select fails
     */
    protected List loadUsers( Connection dbConnection ) throws SQLException {
        List data = null;
        ResultSet rset = null;
        String dbFetchSQL = "SELECT nbconfig_user FROM DES_NB_CONFIG" +
                " ORDER BY nbconfig_user";
        
        PreparedStatement pstmt = dbConnection.prepareStatement( dbFetchSQL );
        
        try {
            rset = pstmt.executeQuery();
            
            while ( rset.next() ) {
                if ( data == null ) {
                    data = new ArrayList();
                }
                
                data.add( rset.getString( 1 ) );
            }
        } catch( SQLException se ) {
            throw se;
        } finally {
            try {
                if ( rset != null ) {
                    rset.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL result set: " +
                        e.toString() );
                
                e.printStackTrace();
            }
            
            try {
                if ( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
                System.err.println( "failed to release SQL statement: " +
                        e.toString() );
                
                e.printStackTrace();
            }
        }
        
        return data;
    }
}