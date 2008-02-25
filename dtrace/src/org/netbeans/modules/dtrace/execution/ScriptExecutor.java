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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import org.netbeans.modules.dtrace.script.Script;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author nassern
 */
public class ScriptExecutor implements Runnable {
    private Script script;
    private TaskManager taskMgr;
    private LinkedList tasks = new LinkedList();
    
    private InputOutput io;
    private PrintWriter out; 
    
    /** Creates a new instance of ScriptExecutor */
    public ScriptExecutor() {

    }
    
    public ExecutorTask execute(Script script) {
        final ExecutorTask task;
        synchronized (this) { 
            this.script = script;
            String procName = "DTrace - " + script.getName();
            io = IOProvider.getDefault().getIO(procName, true);
            io.select();            
            taskMgr = new TaskManager();
            task = ExecutionEngine.getDefault().execute(procName, this, io);
            taskMgr.setTask(task);
            tasks.add(taskMgr);
        }
        
        return task;
    }
    
    public void run() {
        int execStat = 0;
        ScriptExecution scriptExec = new ScriptExecution(script);
        try {  
            taskMgr.setScriptExec (scriptExec);
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

    
    public void stop() {
        if (tasks.isEmpty()) {
            return;
        }
        
        TaskManager taskMgr = (TaskManager)tasks.getLast();     
        try {
            sigkill(taskMgr.getScritpExec().getPid());
        } catch (Exception ex) {
            ex.getStackTrace();
        } 
        
        if (!(taskMgr.getScritpExec().getScript().isDScript())) {
            taskMgr.getScritpExec().destroy();
            taskMgr.getTask().stop();
        } 
        
        tasks.removeLast();
    }
    
    private class TaskManager {
        private ExecutorTask task;
        private ScriptExecution scriptExec;
    
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
    }
}