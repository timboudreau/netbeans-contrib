/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.

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

 * Contributor(s):

 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.

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


/*
 * ScriptExecutor.java
 *
 * Created on April 13, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.execution;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.netbeans.modules.dtrace.script.Script;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author nassern
 */
public class ScriptExecutor implements Runnable {
    private Script script;
    private TaskManager taskMgr;
    private  static ArrayList   taskTabs = new ArrayList();
    
    private InputOutput io;
    private PrintWriter out; 
    
    /** Creates a new instance of ScriptExecutor */
    public ScriptExecutor() {

    }
    
    public void stopExecTask(TaskManager tm) {
        ExecutorTask tabExecTask =  tm.getTask();
        if (!tabExecTask.isFinished()) {
            stop(tm);
        }
    }
    
    public void stopAllIoTabs() {
        Iterator i = taskTabs.iterator();
        while (i.hasNext()) {
            TaskManager tm = (TaskManager)i.next();
            InputOutput tabIO = tm.getIO();
            
            if (tabIO != null) {
                tabIO.closeInputOutput();  
            }       
            stopExecTask(tm);
            i.remove();
        }  
        taskTabs.clear();
    }
    
    public ExecutorTask execute(Script script) {
        final ExecutorTask execTask;
        synchronized (this) { 
            this.script = script;
            String displayName = "DTrace - " + script.getName();
            
            Iterator i = taskTabs.iterator();
            while (i.hasNext()) {
                TaskManager tm = (TaskManager)i.next();
                InputOutput tabIO = tm.getIO();
 
                if (tabIO == null) {
                    stopExecTask(tm);
                    i.remove();
                } else if (tabIO != null && tabIO.isClosed()) {
                    stopExecTask(tm);
                    tabIO.closeInputOutput();
                    i.remove();
                }
            }
       
            taskMgr = new TaskManager();
            taskMgr.setScript(script);
            taskMgr.setDisplayName(displayName);
            
            StopAction stopAction = new StopAction(taskMgr);
            taskMgr.setStopAction(stopAction);           
            RerunAction rerunAction = new RerunAction(taskMgr);
            taskMgr.setRerunAction(rerunAction);
            
            io = IOProvider.getDefault().getIO(displayName, new Action[] {rerunAction, stopAction});
            io.select();
            taskMgr.setIO(io);
            execTask = ExecutionEngine.getDefault().execute(displayName, this, io);
            taskMgr.setTask(execTask);
            taskTabs.add(taskMgr);
            CloseTabThread closeTabThread = new CloseTabThread(taskMgr);
            closeTabThread.start();
        }
        
        return execTask;
    }
    
    public ExecutorTask execute(TaskManager taskMgr) {
        final ExecutorTask execTask;
        synchronized (this) { 
            this.script = taskMgr.getScript();
            this.io = taskMgr.getIO();
            this.taskMgr = taskMgr;
            execTask = ExecutionEngine.getDefault().execute(taskMgr.getDisplayName(), this, io);
        }
        
        return execTask;
    } 
    
    public void run() {
        int execStat = 0;
        ScriptExecution scriptExec = new ScriptExecution(script);
        try {  
            taskMgr.setScriptExec (scriptExec);
            taskMgr.getStopAction().setEnabled(true);
            taskMgr.getRerunAction().setEnabled(false);
            execStat = scriptExec.executeCmd(io); 
        } catch(ThreadDeath td) {
         //   scriptExec.destroy();
        }
    }
    
    private void sigkill(int pid) throws IOException {
        int retCode = 0;
        
        //List params = new ArrayList();
        //params.add("kill -2 ");
        //params.add(String.valueOf(pid));
        //new ProcessBuilder(params).start();
        
        String cmd = "kill -15 ";
        cmd += String.valueOf(pid);
        Process proc = Runtime.getRuntime().exec(cmd);
        try {
            retCode = proc.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    } 
    
    public void stop(TaskManager taskMgr) {
        if (taskMgr == null) {
            return;
        }
            
        try {
            sigkill(taskMgr.getScritpExec().getPid());
        } catch (Exception ex) {
            ex.getStackTrace();
        } 
        
        if (!(taskMgr.getScritpExec().getScript().isDScript())) {
            taskMgr.getScritpExec().destroy();
            taskMgr.getTask().stop();
        }     
        
        taskMgr.getRerunAction().setEnabled(true);
    }
    
    
    private class CloseTabThread extends Thread {
        TaskManager taskMgr;
        
        public CloseTabThread(TaskManager taskMgr) {
            this.taskMgr = taskMgr;
        }
        
        @Override
        public void run() {
            if (taskMgr != null) {
                InputOutput io = taskMgr.getIO();
                while (!interrupted()&& io != null && !io.isClosed()) {
                    try {
                         Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (io != null && io.isClosed()) {
                    stopExecTask(taskMgr);
                    taskTabs.remove(taskMgr);
                }
            }
        }
    }
    
    private class TaskManager {
        private ExecutorTask task;
        private ScriptExecution scriptExec;
        private String displayName;
        private InputOutput io;
        private StopAction stopAction;
        private RerunAction rerunAction;
        private Script script;
        
        TaskManager() {
            this.task = null;
            scriptExec = null;
        }
    
        public void setTask(ExecutorTask task) {
            this.task = task;
        }
    
        public ExecutorTask getTask() {
            return task;
        }

        public void setScriptExec(ScriptExecution se) {
            this.scriptExec = se;
        }

        public ScriptExecution getScritpExec() {
            return scriptExec;
        }
        
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public void setIO(InputOutput io) {
            this.io = io;
        }
        
        public InputOutput getIO() {
            return io;
        }
        
        public void setStopAction(StopAction stopAction) {
            this.stopAction = stopAction;
        }
        
        public StopAction getStopAction() {
            return stopAction;
        }
        
        public void setRerunAction(RerunAction rerunAction) {
            this.rerunAction = rerunAction;
        }
        
        public RerunAction getRerunAction() {
            return rerunAction;
        }
        
        public void setScript(Script script) {
            this.script = script;
        }
        
        public Script getScript() {
            return script;
        }
    }
    
    private class StopAction extends AbstractAction {
        
        private TaskManager taskMgr;
        
        public StopAction(TaskManager taskMgr) {
            this.taskMgr = taskMgr;
            setEnabled(false); // initially, until ready
        }

        @Override
        public Object getValue(String key) {
            if (key.equals(Action.SMALL_ICON)) {
                return new ImageIcon(ScriptExecutor.class.getResource("/org/netbeans/modules/dtrace/resources/stop.png"));
            } else if (key.equals(Action.SHORT_DESCRIPTION)) {
                return NbBundle.getMessage(ScriptExecutor.class, "ScriptExecutor.StopAction.stop");
            } else {
                return super.getValue(key);
            }
        }

        public void actionPerformed(ActionEvent e) {
            setEnabled(false); // discourage repeated clicking
            if (taskMgr != null) { 
                stop(taskMgr);
            }
        }
        
        public void setTaskMgr(TaskManager taskMgr) {
            this.taskMgr = taskMgr;
        }
    }
    
    private class RerunAction extends AbstractAction implements FileChangeListener {

        private TaskManager taskMgr;

        public RerunAction(TaskManager taskMgr) {
            this.taskMgr = taskMgr;
            setEnabled(false); // initially, until ready
            FileObject fileObject = FileUtil.toFileObject(taskMgr.getScript().getFile());
            if (fileObject == null || !fileObject.isValid()) {
                return;
            } else {
                fileObject.addFileChangeListener(FileUtil.weakFileChangeListener(this, fileObject));
            }
        }

        @Override
        public Object getValue(String key) {
            if (key.equals(Action.SMALL_ICON)) {
                return new ImageIcon(ScriptExecutor.class.getResource("/org/netbeans/modules/dtrace/resources/rerun.png"));
            } else if (key.equals(Action.SHORT_DESCRIPTION)) {
                return NbBundle.getMessage(ScriptExecutor.class, "ScriptExecutor.RerunAction.rerun");
            } else {
                return super.getValue(key);
            }
        }

        public void actionPerformed(ActionEvent e) {
            setEnabled(false);
            ScriptExecutor exec = new ScriptExecutor();
            exec.execute(taskMgr);
        }

        public void fileDeleted(FileEvent fe) {
            firePropertyChange("enabled", null, false); // NOI18N
        }

        public void fileFolderCreated(FileEvent fe) {}

        public void fileDataCreated(FileEvent fe) {}

        public void fileChanged(FileEvent fe) {}

        public void fileRenamed(FileRenameEvent fe) {}

        public void fileAttributeChanged(FileAttributeEvent fe) {}
    }
}