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
 * ScriptExecution.java
 *
 * Created on April 13, 2007, 5:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.execution;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.dtrace.script.Script;
import org.openide.util.Utilities;
import org.openide.windows.InputOutput;

/**
 *
 * @author nassern
 */
public class ScriptExecution {
    private Script script;
    private ProcessBuilder procBuilder;
    private Process proc;
    private InputStream in;
    private PrintWriter out;
    private OutputReaderThread outputReaderThread = null;
      
    
    /** Creates a new instance of ScriptExecution */
    public ScriptExecution(Script script) {
        this.script = script;
        List params = new ArrayList();
        params.addAll(Arrays.asList(Utilities.parseParameters(script.getCommand())));
        procBuilder = new ProcessBuilder(params); 
    }
    
    public Script getScript() {
        return script;
    }
     
    public int executeCmd(InputOutput io) {
        int retCode = 0;
        try {
            procBuilder.redirectErrorStream(true);
            proc = procBuilder.start();
            in = proc.getInputStream();
            out = io.getOut();
            outputReaderThread = new OutputReaderThread(in, out);
            outputReaderThread.start();                   
        } catch (IOException ex) {   
            ex.printStackTrace();
        }
        
        try {
            retCode = proc.waitFor();                 
        } catch (InterruptedException ex) {
            // We've interupted the process. Kill it and wait for the process to finish.
            proc.destroy();
            while (retCode < 0) {
                try {
                    retCode= proc.waitFor();
                } catch (InterruptedException ex1) {
                    ex1.getStackTrace();
                }
            }
        }
      
        try {
            outputReaderThread.join();	    // wait for the thread to complete
        } catch (InterruptedException ex2) {
            // On Windows join() throws InterruptedException if process was terminated/interrupted
            ex2.getStackTrace();
        }
      
        try {          
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        
        return retCode;
    }
    
    public int getPid() throws Exception {
        Field pidFld = proc.getClass().getDeclaredField("pid");
        pidFld.setAccessible(true);
        return pidFld.getInt(proc);
    } 
    
    public void destroy() {
      
        if (proc != null) {         
            proc.destroy();
            
            try {                  
                if (out != null) {
                    out.flush();
                    out.close();
                    out = null;
                }

            } catch (Exception ex) {
                ex.getStackTrace();
            } 
                    
        }    
    }
    
    private class OutputReaderThread  extends Thread {
        
        /** This is all output, not just stderr */
        private InputStream in;
        private Reader reader;
        private PrintWriter out;
        private PrintWriter outFile;
 
        
        public OutputReaderThread(InputStream in, PrintWriter out) {
            this.in = in;
            this.reader = new InputStreamReader(in);
            this.out = out;
            setName("OutputReaderThread"); 
        }
        
        public void run() {
            try {  
                int cnt;
                char[] buf = new char[256];
                         
                while (!interrupted() && (cnt = reader.read(buf, 0, 255)) != -1) {
                    if (out != null) {
                        out.write(buf, 0, cnt);
                        out.flush();
                    }
                }

               if (reader != null) {
                    reader.close();
               }                            
            } catch (Exception e) {
                //ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
               e.printStackTrace();
               try {       
                   if (reader != null) {
                       reader.close();
                       reader = null;
                   }                 
               } catch (IOException ex) {
                   ex.printStackTrace();
               }
            }
        }
    }
}

