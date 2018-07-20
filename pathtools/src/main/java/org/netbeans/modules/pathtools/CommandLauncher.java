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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.pathtools;

import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Utilities;

/**
 *
 * @author  Sandip V. Chitale (Sandip.Chitale@Sun.Com)
 */
public class CommandLauncher {

    private static final String FILE_PATH                    = "\\{path\\}";
    private static final String FILE_PARENT_PATH             = "\\{parent-path\\}";
    private static final String FILE_PATH_SLASHES            = "\\{path-slashes\\}";
    private static final String FILE_PARENT_PATH_SLASHES     = "\\{parent-path-slashes\\}";
    private static final String FILE_PATH_BACKSLASHES        = "\\{path-backslashes\\}";
    private static final String FILE_PARENT_PATH_BACKSLASHES = "\\{parent-path-backslashes\\}";

    public static void launch(String command) {
        try {
            String[] commandArray = Utilities.parseParameters(command);
            StatusDisplayer.getDefault().setStatusText("Launching Command: " + command);
            Process process = Runtime.getRuntime().exec(commandArray);
            int status = process.waitFor();
            if (status == 0) {
                StatusDisplayer.getDefault().setStatusText("Launched: Command \"" + command + "\"");
            } else {
                StatusDisplayer.getDefault().setStatusText("Process \"" + command + "\" exited with status : " + status);
            }
        } catch (InterruptedException ex) {
            StatusDisplayer.getDefault().setStatusText(ex.getMessage());
            ErrorManager.getDefault().notify(ex);
        } catch (IOException ioe) {
            StatusDisplayer.getDefault().setStatusText(ioe.getMessage());
            ErrorManager.getDefault().notify(ioe);
        }
    }

    static String convertParameters(String command) {
        return command
                .replaceAll(FILE_PATH, "{0}")
                .replaceAll(FILE_PARENT_PATH, "{1}")
                .replaceAll(FILE_PATH_SLASHES, "{2}")
                .replaceAll(FILE_PARENT_PATH_SLASHES, "{3}")
                .replaceAll(FILE_PATH_BACKSLASHES, "{4}")
                .replaceAll(FILE_PARENT_PATH_BACKSLASHES, "{5}")
                ;
    }
}
