/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU General
 * Public License Version 2 only ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP. See the
 * License for the specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header Notice in
 * each file and include the License file at nbbuild/licenses/CDDL-GPL-2-CP.  Sun
 * designates this particular file as subject to the "Classpath" exception as
 * provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the License Header,
 * with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original Software
 * is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun Microsystems, Inc. All
 * Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL or only the
 * GPL Version 2, indicate your decision by adding "[Contributor] elects to include
 * this software in this distribution under the [CDDL or GPL Version 2] license." If
 * you do not indicate a single choice of license, a recipient has the option to
 * distribute your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above. However, if
 * you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then
 * the option applies only if the new code is made subject to such option by the
 * copyright holder.
 */

package org.netbeans.installer.utils.silent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.netbeans.installer.utils.DateUtils;
import org.netbeans.installer.utils.LogManager;
import org.netbeans.installer.utils.env.CheckStatus;
import org.netbeans.installer.utils.helper.ExecutionMode;
import org.netbeans.installer.utils.helper.UiMode;

public class SilentLogManager {
    
    public static final String SILENT_LOGS_DIRECTORY_OPTION = "ssi.silent.logs.directory.option";
    public static final String UNKNOWN_HOST = "unknown_host";
    
    private static SilentLogManager instance = null;
    
    private File logFile = null;
    private boolean dublicateToMainLog = false;
    private BufferedWriter logWriter = null;
    
    private static synchronized SilentLogManager getInstance() {
        if (instance == null) {
            instance = new SilentLogManager(System.getProperty(SILENT_LOGS_DIRECTORY_OPTION), true);
        }
        return instance;
    }
    
    public SilentLogManager(String logsDirectory, boolean dublicateToMainLog) {
        this.dublicateToMainLog = dublicateToMainLog || logsDirectory == null;
        if (logsDirectory != null) {
            String hostName = UNKNOWN_HOST;
            try {
                InetAddress addr = InetAddress.getLocalHost();
                hostName = addr.getHostName();
            } catch (UnknownHostException ex) {}
            logFile = new File(logsDirectory, String.format("ssi-%1$s-%2$s.log", hostName, DateUtils.getTimestamp()));
        }
    }
    
    public static synchronized void forceLog(CheckStatus status, String comments) {
        getInstance().writeLogRecord(String.format("%1$s - %2$s", status.toString(), comments));
    }
    
    public static synchronized void log(CheckStatus status, String comments) {
        if (!isLogManagerActive()) return;
        forceLog(status, comments);
    }
    
    private void writeLogRecord(String record) {
        if (dublicateToMainLog) LogManager.log(record);
        if (logFile != null) {
            try {
                if (logWriter == null) logWriter = new BufferedWriter(new FileWriter(logFile));
                logWriter.write(String.format("[%1$s]: %2$s\n", DateUtils.getFormattedTimestamp(), record));
                logWriter.flush();
            } catch (IOException ex) {
                LogManager.log(ex);
            }
        }
    }

    public static void endLogging() {
        if (getInstance().logWriter != null) try {
            getInstance().logWriter.close();
        } catch (IOException ex) {
            LogManager.log(ex);
        }
    }
    
    public static boolean isLogManagerActive() {
        return UiMode.getCurrentUiMode().equals(UiMode.SILENT) && ExecutionMode.getCurrentExecutionMode().equals(ExecutionMode.NORMAL);
    }
    
    public File getLogFile() {
        return logFile;
    }

    public boolean isDublicateToMainLog() {
        return dublicateToMainLog;
    }

    public void setDublicateToMainLog(boolean dublicateToMainLog) {
        this.dublicateToMainLog = dublicateToMainLog;
    }
    
}
