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

package org.netbeans.modules.portalpack.servers.core.common;

import org.netbeans.modules.portalpack.servers.core.api.PSDeploymentManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;


/**
 *
 */
public class LogManager {
    private ServerLog serverLog;    
    
    private Object serverLogLock = new Object();
    private PSDeploymentManager manager;
    
    
    
    /** Creates a new instance of LogManager */
    public LogManager(PSDeploymentManager manager) {
        this.manager = manager;
    }
    
    // ------- server log (output) ---------------------------------------------
    
    /**
     * Open the server log (output).
     */
    public void openServerLog(final Process process,final String displayName) {
        
        assert process != null;
        synchronized(serverLogLock) {
            if (serverLog != null) {
                serverLog.takeFocus();
                return;
            }
            serverLog = new ServerLog(
                manager.getUri(),
                displayName,
                new InputStreamReader(process.getInputStream()),
                new InputStreamReader(process.getErrorStream()),
                true,
                false, new ServerLogSupport());
            serverLog.start();
        }
        
        new Thread() {
            public void run() {
                try {
                    int ret = process.waitFor();
                    Thread.sleep(2000);  // time for server log
                } catch (InterruptedException e) {
                } finally {
                    serverLog.interrupt();
                }
            }
        }.start();
    }
    
    
      
    /**
     * Stop the server log thread, if started.
     */
    public void closeServerLog() {
        synchronized(serverLogLock) {
            if (serverLog != null) {
                serverLog.interrupt();
                serverLog = null;
            }
        }
    }
    
}