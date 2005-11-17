/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.zeroadmin.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
 * @author nokia.com
 * @version 1.0
 */

public class ZeroAdminServlet extends HttpServlet {
//============================================================================
// Constants
//============================================================================
    
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
        List data = null;
        String user = req.getParameter( P_USER_NAME );
        String mode = req.getParameter( P_MODE );
        StringBuffer cfg = new StringBuffer();
        
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
        } catch( SQLException sqle ) {
            System.err.println(
                    "failed to open connection using datasource: " + DATASOURCE );
            
            sqle.printStackTrace();
        }
        
        if ( user == null ) {
            try {
                if ( dbHelper.isConnected() ) {
                    data = dbHelper.loadUsers();
                }
            } catch( SQLException sqle ) {
                System.err.println(
                        "failed to load user list of NetBeans configuration using datasource: " + DATASOURCE );
                
                sqle.printStackTrace();
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
                            // Do nothing if decoding failed
                            // (just leave the original data as it is...)
                        }
                    }
                } catch( SQLException sqle ) {
                    System.err.println(
                            "failed to load NetBeans configuration for user: " + item );
                    
                    sqle.printStackTrace();
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
                System.err.println(
                        "failed to delete configuration for user: " + user );
                
                sqle.printStackTrace();
            }
        } else {
            cfg.append( "No data or error occured." );
        }
        
        try {
            dbHelper.close();
        } catch( SQLException sqle ) {
            System.err.println(
                    "failed to close connection using datasource: " + DATASOURCE );
            
            sqle.printStackTrace();
        }
        
        PrintWriter wr = resp.getWriter();
        wr.print(cfg.toString());
        wr.close();
    }
    
    /**
     * Load/Store NetBeans configuration.
     * The "user" and "mode" parameters must be given.
     *
     * @see javax.servlet.http.HttpServlet
     */
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
    throws ServletException, IOException {
        String mode = req.getParameter( P_MODE );
        String user = req.getParameter( P_USER_NAME );
        String cfg = req.getParameter( P_USER_CFG );
        
        if ( "save".equals( mode ) ) {
            saveData(user, cfg);
            
            try {
                resp.flushBuffer();
            } catch( IOException e ) {
                e.printStackTrace();
            }
        } else if ( "load".equals( mode ) ) {
            cfg = getData(user);
            resp.setContentType("text/plain");
            PrintWriter wr = resp.getWriter();
            wr.print(cfg);
            wr.close();
        }
    }
    
//============================================================================
// Private methods
//============================================================================
    
    private void saveData( String userName, String data ) throws IOException {
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
            dbHelper.initialize( userName );
            dbHelper.saveData( userName, data );
            dbHelper.close();
        } catch( SQLException sqle ) {
            System.err.println(
                    "failed to save NetBeans configuration using datasource: " + DATASOURCE );
            
            sqle.printStackTrace();
        }
    }
    
    private String getData( String userName ) throws IOException {
        String data = "";
        DBHelper dbHelper = new DBHelper( DATASOURCE );
        
        try {
            dbHelper.connect();
            dbHelper.initialize( userName );
            data = dbHelper.loadData( userName );
            dbHelper.close();
        } catch( SQLException sqle ) {
            System.err.println(
                    "failed to load NetBeans configuration using datasource: " + DATASOURCE );
            
            sqle.printStackTrace();
        }
        
        return data;
    }
    
}