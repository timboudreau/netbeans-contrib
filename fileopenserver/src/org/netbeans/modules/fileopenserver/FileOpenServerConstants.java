/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.fileopenserver;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public interface FileOpenServerConstants {
    String  PROPERTY_PORT_NUMBER                                    = "FileOpenServer.portNumber";
    int     PROPERTY_PORT_NUMBER_DEFAULT_VALUE                      = 4051;
    String  PROPERTY_START_AT_STARTUP                               = "FileOpenServer.startAtStartup";
    boolean PROPERTY_START_AT_STARTUP_DEFAULT_VALUE                 = true;
    String  PROPERTY_LOG_REQUESTS                                   = "FileOpenServer.logRequests";
    boolean PROPERTY_LOG_REQUESTS_DEFAULT_VALUE                     = false;
    String  PROPERTY_EXTERNAL_EDITOR_COMMAND                        = "FileOpenServer.externalEditorCommand";
    String  PROPERTY_EXTERNAL_EDITOR_COMMAND_DEFAULT_VALUE          = "";
    String  PROPERTY_LINE_NUMBER_STARTS_WITH_0                      = "FileOpenServer.lineNumberStartsWith0";
    boolean PROPERTY_LINE_NUMBER_STARTS_WITH_0_DEFAULT_VALUE        = false;    
    String  PROPERTY_COLUMN_NUMBER_STARTS_WITH_0                    = "FileOpenServer.columnNumberStartsWith0";
    boolean PROPERTY_COLUMN_NUMBER_STARTS_WITH_0_DEFAULT_VALUE      = false;
}
