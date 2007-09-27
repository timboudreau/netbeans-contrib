/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.erd.wizard;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.modules.dbschema.DBException;
import org.netbeans.modules.dbschema.DBIdentifier;
import org.netbeans.modules.dbschema.SchemaElement;
import org.netbeans.modules.dbschema.SchemaElementUtil;
import org.netbeans.modules.dbschema.jdbcimpl.ConnectionProvider;
import org.netbeans.modules.dbschema.jdbcimpl.SchemaElementImpl;
import org.netbeans.modules.dbschema.util.NameUtil;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author Andrei Badea
 */
public class DBSchemaManager {

    public static final String DBSCHEMA_EXT = "dbschema"; // NOI18N

    private final EventRequestProcessor erp = new EventRequestProcessor();

    private DatabaseConnection oldDBConn;
    private boolean oldDBConnWasConnected;
    private Connection conn;
    private SchemaElement schemaElement;

    private SQLException exception;

    private FileObject schemaFileObject;
    private SchemaElement fileSchemaElement;

    public SchemaElement getSchemaElement(final DatabaseConnection dbconn) throws SQLException {
        assert SwingUtilities.isEventDispatchThread();

        if (oldDBConn == dbconn) {
            return schemaElement;
        }

        schemaElement = null;

        List<EventRequestProcessor.Action> actions = new ArrayList<EventRequestProcessor.Action>();

        if (oldDBConn != null && !oldDBConnWasConnected) {
            // need to disconnect the old connection
            actions.add(new EventRequestProcessor.AsynchronousAction() {
                public void run() {
                    ConnectionManager.getDefault().disconnect(oldDBConn);
                    oldDBConn = null;
                    conn = null;
                }

                public String getMessage() {
                    return NbBundle.getMessage(DBSchemaManager.class, "LBL_ClosingPreviousConnection");
                }
            });
        } else {
            // no need to disconnect the old connection, just cleanup
            // before connecting the new connection
            oldDBConn = null;
            conn = null;
        }

        actions.add(new EventRequestProcessor.SynchronousAction() {
            public void run() {
                ConnectionManager.getDefault().showConnectionDialog(dbconn);
                conn = dbconn.getJDBCConnection();
            }

            public boolean isEnabled() {
                conn = dbconn.getJDBCConnection();
                oldDBConnWasConnected = conn != null;
                return !oldDBConnWasConnected;
            }
        });

        actions.add(new EventRequestProcessor.AsynchronousAction() {
            public void run() {
                oldDBConn = dbconn;

                ConnectionProvider connectionProvider = null;
                try {
                    connectionProvider = new ConnectionProvider(conn, dbconn.getDriverClass());
                } catch (SQLException e) {
                    exception = e;
                    return;
                }

                SchemaElementImpl schemaElementImpl = new SchemaElementImpl(connectionProvider);
                try {
                    schemaElementImpl.setName(DBIdentifier.create("dbschema"));
                } catch (DBException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                    return;
                }
                schemaElement = new SchemaElement(schemaElementImpl);

                schemaElementImpl.initTables(connectionProvider);
            }

            public boolean isEnabled() {
                return conn != null;
            }

            public String getMessage() {
                return "Retriving Schema";//NbBundle.getMessage(DBSchemaManager.class, "LBL_RetrievingSchema");
            }
        });

        exception = null;
        erp.invoke(actions);
        if (exception != null) {
            throw exception;
        }

        return schemaElement;
    }

    public SchemaElement getSchemaElement(final FileObject fo) {
        assert SwingUtilities.isEventDispatchThread();

        if (fo == schemaFileObject) {
            return fileSchemaElement;
        }

        schemaFileObject = null;
        fileSchemaElement = null;

        List<EventRequestProcessor.Action> actions = new ArrayList<EventRequestProcessor.Action>();

        actions.add(new EventRequestProcessor.AsynchronousAction() {
            public void run() {
                schemaFileObject = fo;
                fileSchemaElement = SchemaElementUtil.forName(fo);
            }

            public String getMessage() {
                return "heya";
            }
        });

        erp.invoke(actions);

        return fileSchemaElement;
    }

    public static FileObject writeDBSchema(SchemaElement schemaElement, FileObject folder, String projectName) throws IOException {
        String schemaName = schemaElement.getSchema().getName();
        String fileName = (schemaName != null && schemaName != "" ? schemaName + "_" : "") + projectName; // NOI18N
        // #65887: the schema name should not contain the schema db element separator
        fileName = fileName.replace(NameUtil.dbElementSeparator, '_'); // NOI18N

        String freeFileName = FileUtil.findFreeFileName(folder, fileName, DBSCHEMA_EXT);
        DBIdentifier schemaElementName = DBIdentifier.create(freeFileName);

        try {
            schemaElement.setName(schemaElementName);
        } catch (DBException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }

        FileObject dbSchemaFile = folder.createData(freeFileName, DBSCHEMA_EXT);
        FileLock lock = dbSchemaFile.lock();
        try {
            OutputStream os = new BufferedOutputStream(dbSchemaFile.getOutputStream(lock));
            try {
                schemaElement.save(os);
            } finally {
                os.close();
            }
        } finally {
            lock.releaseLock();
        }

        return dbSchemaFile;
    }
}
