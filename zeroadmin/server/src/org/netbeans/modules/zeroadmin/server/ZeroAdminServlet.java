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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.xerces.impl.dv.util.Base64;


/**
 * This servlet is responsible for retrieving and storing NetBeans configuration
 * data to an SQL database. The configuration is stored as a CLOB in the
 * database.<p>
 *
 * This servlet co-operates with the zeroadmin NetBeans module.<p>
 *
 * Supported parameters:<p>
 * <pre>
 * mode - "load" or "save"
 * user - the user name
 * cfg - NetBeans configuration (Base64 encoded)
 * </pre>
 *
 * @author David Strupl
 * @version 1.1
 */

public class ZeroAdminServlet extends HttpServlet {
//============================================================================
// Constants
//============================================================================
    private static Logger log = Logger.getLogger(DBHelper.class.getName());
    
    private static final String P_MODE = "mode";         // The mode
    private static final String P_USER_NAME = "user";    // The user name
    private static final String P_USER_CFG = "cfg";      // The configuration
    
    private static final String DATASOURCE = "java:comp/env/jdbc/nbconfig";
    
//============================================================================
// Protected methods
//============================================================================
    
    /**
     * Load NetBeans configuration info.
     * Supports the "user" parameter.
     *
     * @see javax.servlet.http.HttpServlet
     */
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException {
        log.entering(getClass().getName(), "doGet");
        
        List data = null;
        String user = req.getParameter( P_USER_NAME );
        String mode = req.getParameter( P_MODE );
        StringBuffer cfg = new StringBuffer();
        
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
            dbHelper.initialize(null); // make sure the tables are there
        } catch( SQLException sqle ) {
            log.log(Level.WARNING, 
                    "failed to open connection using datasource: " + DATASOURCE,
                    sqle);
        }
        
        if ( user == null ) {
            try {
                if ( dbHelper.isConnected() ) {
                    data = dbHelper.loadUsers();
                }
            } catch( SQLException sqle ) {
                log.log(Level.WARNING, 
                    "failed to load user list of NetBeans configuration using datasource: " +
                    DATASOURCE, sqle);
            }
        } else {
            data = new ArrayList();
            data.add( user );
        }
        
        // HTML TABLE
        if ( mode == null && data != null && dbHelper.isConnected() ) {
            resp.setContentType("text/html");
            cfg.append( "<html><head></head><body>\n" );
            cfg.append( "<table border=\"1\">\n" );
            
            for ( int i = 0; i < data.size(); i++ ) {
                String config = null;
                String item = ( String )data.get( i );
                
                try {
                    config = dbHelper.loadData( item );
                    
                    if ( config != null ) {
                        try {
                            byte[] buf = Base64.decode( config );
                            config = new String( buf, "UTF-8" );
                        } catch( Exception e ) {
                            log.log(Level.FINE, "Decoding failed for data: " +
                                    config, e);
                        }
                    }
                } catch( SQLException sqle ) {
                    log.log(Level.WARNING, 
                        "failed to load NetBeans configuration for user: " + item,
                        sqle);
                    config = sqle.toString();
                }
                
                cfg.append( "<tr><td>" );
                cfg.append( item );
                cfg.append( "</td><td><pre>" );
                cfg.append( config );
                cfg.append( "</pre></td></tr>\n" );
            }
            
            cfg.append( "</table>\n" );
            cfg.append( "</body></html>" );
        }
        // TEXT LIST OF USERS
        else if ( "list".equals( mode ) && data != null && dbHelper.isConnected() ) {
            resp.setContentType("text/plain");
            for ( int i = 0; i < data.size(); i++ ) {
                cfg.append( data.get( i ) );
                
                if ( i < ( data.size() - 1 ) ) {
                    cfg.append( ",\n" );
                }
            }
        }
        // DELETE USER FROM DB
        else if ( "delete".equals( mode ) && data != null && dbHelper.isConnected() ) {
            String userName = ( String )data.get( 0 );
            
            try {
                dbHelper.deleteData( userName );
            } catch( SQLException sqle ) {
                log.log(Level.WARNING, 
                  "failed to delete configuration for user: " + user, sqle);
            }
        } else {
            cfg.append( "No data or error occured." );
        }
        
        try {
            dbHelper.close();
        } catch( SQLException sqle ) {
            log.log(Level.WARNING, "failed to close connection using datasource: " +
                DATASOURCE, sqle);
        }
        
        PrintWriter wr = resp.getWriter();
        wr.print(cfg.toString());
        wr.close();
        log.exiting(getClass().getName(), "doGet");
    }
    
    /**
     * Load/Store NetBeans configuration.
     * The "user" and "mode" parameters must be given.
     *
     * @see javax.servlet.http.HttpServlet
     */
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException {
        log.entering(getClass().getName(), "doPost");
        
        String mode = req.getParameter( P_MODE );
        String user = req.getParameter( P_USER_NAME );
        String cfg = req.getParameter( P_USER_CFG );
        
        if ( "save".equals( mode ) ) {
            saveData(user, cfg);
            
            try {
                resp.flushBuffer();
            } catch( IOException e ) {
                log.log(Level.WARNING, "user == " + user, e);
            }
        } else if ( "load".equals( mode ) ) {
            cfg = getData(user);
            resp.setContentType("text/plain");
            PrintWriter wr = resp.getWriter();
            wr.print(cfg);
            wr.close();
        }
        log.exiting(getClass().getName(), "doPost");
    }
    
//============================================================================
// Private methods
//============================================================================
    
    private void saveData( String userName, String data ) throws IOException {
        log.entering(getClass().getName(), "saveData", userName);
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
            dbHelper.initialize( userName );
            dbHelper.saveData( userName, data );
            dbHelper.close();
        } catch( SQLException sqle ) {
            log.log(Level.WARNING, 
                "failed to save NetBeans configuration using datasource: " + 
                DATASOURCE, sqle);
        }
        log.exiting(getClass().getName(), "saveData for user"+ userName);
    }
    
    private String getData( String userName ) throws IOException {
        log.entering(getClass().getName(), "getData", userName);
        String data = "";
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
            dbHelper.initialize( userName );
            data = dbHelper.loadData( userName );
            dbHelper.close();
        } catch( SQLException sqle ) {
            log.log(Level.WARNING,
                "failed to load NetBeans configuration using datasource: " +
                DATASOURCE, sqle);
        }
        log.exiting(getClass().getName(), "getData", data);
        return data;
    }
}
