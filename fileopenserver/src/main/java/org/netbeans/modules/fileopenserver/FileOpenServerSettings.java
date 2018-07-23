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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
