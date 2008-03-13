/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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
package org.netbeans.modules.clearcase.client;

import org.netbeans.modules.clearcase.ClearcaseException;
import org.netbeans.modules.clearcase.ClearcaseUnavailableException;
import org.netbeans.modules.versioning.util.Utils;
import org.netbeans.modules.versioning.util.CommandReport;
import org.openide.util.RequestProcessor;
import org.openide.util.Cancellable;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import javax.swing.*;
import java.io.IOException;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.util.*;
import org.netbeans.modules.clearcase.util.ProgressSupport;

/**
 * Interface to Clearcase functionality. 
 * ClearcaseClient commands execution is synchronized and serialized.
 * 
 * @author Maros Sandor
 */
public class ClearcaseClient {

    private Cleartool ct;

    /**
     * Request processor that executes clearcase commands.
     */
    private final RequestProcessor  rp = new RequestProcessor("Clearcase commands", 1); 

    /**
     * Executes the command in a separate thread, command execution and notification happens asynchronously and the method returns
     * immediately.
     * 
     * @param command command to execute
     * @return RequestProcessor.Task
     */    
    public RequestProcessor.Task post(ClearcaseCommand command) {
        return post(new ExecutionUnit(command));    
    }

    /**
     * Execute the commands in a separate thread, command execution and notification happens asynchronously and the method returns
     * immediately.
     * 
     * @param eu commands to execute
     * @return RequestProcessor.Task
     */        
    public RequestProcessor.Task post(ExecutionUnit eu) {
        CommandRunnable commandRunnable = new CommandRunnable(eu);
        RequestProcessor.Task rptask = rp.create(commandRunnable);
        rptask.schedule(0);
        return rptask;
    }
    
    /**
     * Execute a command in a separate thread, command execution and notification happens asynchronously and the method returns
     * immediately.
     * 
     * @param command command to execute
     * @param displayName creates a progress handle with the given display name
     * @return RequestProcessor.Task
     */    
    public RequestProcessor.Task post(String displayName, ClearcaseCommand command) {
        return post(displayName, new ExecutionUnit(command));
    }
    
    /**
     * Execute a command in a separate thread, command execution and notification happens asynchronously and the method returns
     * immediately.
     * 
     * @param eu commands to execute
     * @param displayName creates a progress handle with the given display name
     * @return RequestProcessor.Task
     */  
    public RequestProcessor.Task post(String displayName, final ExecutionUnit eu) {
        ProgressSupport ps = new ProgressSupport(rp, displayName) {            
            private ClearcaseClient.CommandRunnable cr;                        
            @Override
            protected void perform() {
                cr = execImpl(eu);
                setCancellableDelegate(cr);
            }   
        };
        return ps.start();
    }
    
    /**
     * Execute a command synchronously, command execution and notification happens synchronously
     * 
     * @param command command to execute
     * @throws org.netbeans.modules.clearcase.ClearcaseException if the command is invalid, its execution fails, etc.
     */
    public void exec(ClearcaseCommand command) throws ClearcaseException {        
        exec(new ExecutionUnit(command));
    }
    
    /**
     * Execute commands synchronously, command execution and notification happens synchronously
     * 
     * @param eu commands to execute
     * @throws org.netbeans.modules.clearcase.ClearcaseException if the command is invalid, its execution fails, etc.
     */
    public void exec(ExecutionUnit eu) {           
        execImpl(eu);       
    }        
    
    private CommandRunnable execImpl(ExecutionUnit eu) {           
        CommandRunnable commandRunnable = new CommandRunnable(eu);
        commandRunnable.run();
        return commandRunnable;        
    }  
    
    /**
     * Execute a clearcase command, but do not block other commands from execution. Use this call for launching
     * graphical clearcase processes such as History Browser. 
     * 
     * @param cmd command to execute
     * @throws org.netbeans.modules.clearcase.ClearcaseException if the command is invalid, its execution fails, etc.
     */
    public void execAsync(final ClearcaseCommand cmd) throws IOException {
        final Cleartool ctshell = new Cleartool();
        RequestProcessor.Task rptask = RequestProcessor.getDefault().create(new Runnable() {
            public void run() {
                try {
                    // it happens (describe -graphical) that the
                    // cleartools magic prompt closes the external 
                    // commands window
                    ctshell.setFireAndForget(true); 
                    ctshell.exec(cmd);
                } catch (Exception e) {
                    Utils.logError(this, e);
                } finally {
                    try {
                        ctshell.quit();
                    } catch (Exception e) {
                        Utils.logWarn(this, e);
                    }
                }
            }
        });
        rptask.schedule(0);
    }    
    
    private class CommandRunnable implements Runnable, Cancellable, Action {
        
        private final ExecutionUnit eu;
        private boolean             canceled;

        public CommandRunnable(ExecutionUnit eu) {
            this.eu = eu;
        }

        public void run() {            
            if (canceled) return;
            try {
                ensureCleartool();
                for (ClearcaseCommand command : eu) {
                    if (canceled) break;
                    try {
                        ct.exec(command);
                    } catch (Exception e) {                        
                        command.setException(e);
                        eu.setFailedCommand(command);
                        break;
                    }                                        
                    if (command.hasFailed()) {
                        eu.setFailedCommand(command);
                        break;
                    }
                }
            } catch (Exception e) {
                Utils.logError(this, e);
            } 
            if (eu.isNotifyErrors() && eu.getFailedCommand() != null) {
                notifyCommandError();
            }
        }

        /**
         * Pops up a dialog that notifies the user that clearcase command failed.
         * 
         * @param eu
         * @param error
         */
        private void notifyCommandError() {
            final List<String> errors = new ArrayList<String>(100);
            
            Exception exception = eu.getFailedCommand().getThrownException();
            if (exception != null) {
                errors.add(exception.getMessage());
                Utils.logWarn(this, exception);
            }
            
            errors.addAll(eu.getFailedCommand().getCmdError());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    report("Clearcase Command Failure", "Error executing", errors, NotifyDescriptor.ERROR_MESSAGE);        
                }
            });
        }
        
        private void report(String title, String prompt, List<String> messages, int type) {
            boolean emptyReport = true;
            for (String message : messages) {
                if (message != null && message.length() > 0) {
                    emptyReport = false;
                    break;
                }
            }
            if (emptyReport) return;
            
            CommandReport report = new CommandReport(prompt, messages);
            JButton ok = new JButton("OK");
            NotifyDescriptor descriptor = new NotifyDescriptor(
                    report, 
                    title, 
                    NotifyDescriptor.DEFAULT_OPTION,
                    type,
                    new Object [] { ok },
                    ok);
            DialogDisplayer.getDefault().notify(descriptor);
        }
        
        public boolean isCanceled() {
            return canceled;
        }

        public boolean cancel() {
            canceled = true;
            // XXX cancell cleartool
            return true;
        }

        public Object getValue(String key) {
            return null;
        }

        public void putValue(String key, Object value) {
        }

        public void setEnabled(boolean b) {
        }

        public boolean isEnabled() {
            return false;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public void actionPerformed(ActionEvent e) {
        }
    }
    
    public void shutdown() {
        shutdownCleartool();
    }
    
    private void shutdownCleartool() {
        if (ct != null && ct.isValid()) {
            try {
                ct.quit();
            } catch (Exception e) {
                Utils.logFine(this, e);
            }
        }
    }

    private void ensureCleartool() throws ClearcaseException, IOException {
        if (ct == null || !ct.isValid()) {
            try {
                ct = new Cleartool();
            } catch (IOException e) {
                throw new ClearcaseUnavailableException(e);
            }
        }
    }
}
