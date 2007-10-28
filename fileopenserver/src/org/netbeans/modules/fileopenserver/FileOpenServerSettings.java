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

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
public class FileOpenServerSettings {
    private static Preferences preferences;

    private static FileOpenServerSettings fileOpenServerSettings;

    /** Creates a new instance of FileOpenServerOptions */
    private FileOpenServerSettings() {
    }

    // Default instance of this system option, for the convenience of associated classes.
    public static FileOpenServerSettings getInstance() {
        if (fileOpenServerSettings == null) {
            fileOpenServerSettings = new FileOpenServerSettings();
        }
        return fileOpenServerSettings;
    }

    private static Preferences getPreferences() {
        if (preferences == null) {
            preferences = NbPreferences.forModule(FileOpenServerSettings.class);
        }
        return preferences;
    }

    public int getPortNumber()  {
        return getPreferences().getInt(FileOpenServerConstants.PROPERTY_PORT_NUMBER,
                FileOpenServerConstants.PROPERTY_PORT_NUMBER_DEFAULT_VALUE);
    }

    public void setPortNumber(int portNumber) {
        getPreferences().putInt(FileOpenServerConstants.PROPERTY_PORT_NUMBER, portNumber);
    }

    public boolean isStartAtStartup() {
        return getPreferences().getBoolean(FileOpenServerConstants.PROPERTY_START_AT_STARTUP,
                FileOpenServerConstants.PROPERTY_START_AT_STARTUP_DEFAULT_VALUE);
    }

    public void setStartAtStartup(boolean startAtStartup) {
        getPreferences().putBoolean(FileOpenServerConstants.PROPERTY_START_AT_STARTUP,
                startAtStartup);
    }

    public boolean isLogRequests()  {
        return getPreferences().getBoolean(FileOpenServerConstants.PROPERTY_LOG_REQUESTS,
                FileOpenServerConstants.PROPERTY_LOG_REQUESTS_DEFAULT_VALUE);
    }

    public void setLogRequests(boolean logRequests) {
        getPreferences().putBoolean(FileOpenServerConstants.PROPERTY_LOG_REQUESTS,
                logRequests);
    }

    public String getExternalEditorCommand()  {
        return getPreferences().get(FileOpenServerConstants.PROPERTY_EXTERNAL_EDITOR_COMMAND,
                FileOpenServerConstants.PROPERTY_EXTERNAL_EDITOR_COMMAND_DEFAULT_VALUE);
    }

    public void setExternalEditorCommand(String externalEditorCommand) {
        getPreferences().put(FileOpenServerConstants.PROPERTY_EXTERNAL_EDITOR_COMMAND,
                externalEditorCommand);
    }

    public boolean isLineNumberStartsWith0() {
        return getPreferences().getBoolean(FileOpenServerConstants.PROPERTY_LINE_NUMBER_STARTS_WITH_0,
                FileOpenServerConstants.PROPERTY_LINE_NUMBER_STARTS_WITH_0_DEFAULT_VALUE);
    }

    public void setLineNumberStartsWith0(boolean lineNumberStartsWith1) {
        getPreferences().putBoolean(FileOpenServerConstants.PROPERTY_LINE_NUMBER_STARTS_WITH_0,
                lineNumberStartsWith1);
    }

    public boolean isColumnNumberStartsWith0() {
        return getPreferences().getBoolean(FileOpenServerConstants.PROPERTY_COLUMN_NUMBER_STARTS_WITH_0,
                FileOpenServerConstants.PROPERTY_COLUMN_NUMBER_STARTS_WITH_0_DEFAULT_VALUE);
    }

    public void setColumnNumberStartsWith0(boolean columnNumberStartsWith1) {
        getPreferences().putBoolean(FileOpenServerConstants.PROPERTY_COLUMN_NUMBER_STARTS_WITH_0,
                columnNumberStartsWith1);
    }

}
