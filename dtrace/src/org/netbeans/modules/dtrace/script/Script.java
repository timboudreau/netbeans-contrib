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
 * Script.java
 *
 * Created on April 4, 2007, 6:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.dtrace.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.netbeans.modules.dtrace.config.ConfigData;
import org.netbeans.modules.dtrace.config.Configure;

/**
 *
 * @author nassern
 */
public class Script {
    private File sFile; 
    private String pid;
    private String name;
    private String path;
    private String args;
    private String execPath;
    private String execArgs;
    private Configure config;
    private ConfigData configData;
    private boolean dScript;
    private boolean graphics;
    
    /** Creates a new instance of Script */
    public Script(File script) {
        sFile = script;
        name = sFile.getName();
        path = sFile.getAbsolutePath();
        pid = "";
        args = "";
        execPath = "";
        execArgs = "";
        dScript = true;
        graphics = false;
        initConfig();
    }
    
    private void initConfig() {
        String xmlName = name;
        String xmlPath = path;
        
        int dot = name.lastIndexOf('.');
        if (dot != -1) {
            if (name.charAt(dot + 1) == 'd') {
                dScript = true;
            } else {
                dScript = false;
            }
            xmlName = name.substring(0, dot);
        } else {
            dScript = false;
        } 
        
        if (name.startsWith("graphics")) {
            graphics = true;
        }
        
        xmlName = xmlName + ".xml";
        dot = xmlPath.lastIndexOf('/');
        if (dot != -1) {
            xmlPath = xmlPath.substring(0, dot);
            xmlPath = xmlPath + "/" + xmlName;
        }
        config = new Configure(xmlPath);
        readConfig();
        
    }
    
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }

    public File getFile() {
        return sFile;
    }
    
    public void setName(String name) {
        this.name = name;
        configData.setScriptName(name);
        
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
        configData.setScriptPath(path);
    }
    
    public String getArgs() {
        return args;
    }
   
    public void setArgs(String args) {
        this.args = args;
        configData.setScriptArgs(args);
    }
    
    public PrintWriter getWriter() {
        PrintWriter out = null;
        try {
            if (path.length() > 0) {
                out = new PrintWriter(new FileWriter(path));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return out;
    }
    
    public void closeWriter(PrintWriter out) {
        out.close();
    }
    
    public BufferedReader getReader() {
        BufferedReader in = null;
        try {
            if (path.length() > 0) {
                in = new BufferedReader(new FileReader(path));
            }
        } catch (IOException ex) {
             ex.printStackTrace();
        }
        return null;
    }
    
    public void closeReader(BufferedReader in) {
        try {
            in.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getPid() {
        return pid;
    }
    
    public void setPid(String pid) {
        this.pid = pid;
        configData.setProcessId(pid);
    }
    
    public String getExecPath() {
        return execPath;
    }
     
    public void setExecPath(String path) {
        this.execPath = path;
        configData.setExecPath(path);
    }
    
    public String getExecArgs() {
        return execArgs;
    }
    
    public void setExecArgs(String args) {
        this.execArgs = args;
        configData.setExecArgs(args);
    }
    
    public void readConfig() {
        configData = config.read();
        
        if ((configData.getScriptName() != null) &&
                (configData.getScriptName().length() > 0) &&
                (name.compareTo(configData.getScriptName()) != 0)) {
            name = configData.getScriptName();
        }
        
        if ((configData.getScriptPath() != null) &&
                (configData.getScriptPath().length() > 0) && 
                (path.compareTo(configData.getScriptPath()) != 0)) {
            path = configData.getScriptPath();
        }
        
        pid = configData.getProcessId();
        args = configData.getScriptArgs();
        execPath = configData.getExecPath();
        execArgs = configData.getExecArgs();
    }
    
    public void writeConfig() {
        configData = new ConfigData();
        configData.setProcessId(pid);
        configData.setExecPath(execPath);
        configData.setExecArgs(execArgs);
        configData.setScriptName(name);
        configData.setScriptPath(path);
        configData.setScriptArgs(args);
        config.write(configData);
    }
    
    public boolean isDScript() {
        return dScript;
    }
    
    public boolean hasGraphics() {
        return graphics;
    }
    
    public String getCommand() {
        StringBuffer cmd = new StringBuffer();
        
        readConfig();
        if (dScript) {
            cmd.append("/usr/sbin/dtrace -s ");
        }
        cmd.append(path);
                
        if (dScript && execPath.length() > 0) {                
            cmd .append(" -c ");
            cmd.append('\"');
            cmd.append(execPath);
            
            if (execArgs.length() > 0 && execArgs.charAt(0) != '\n') {
                cmd.append(" ");
                cmd.append(execArgs);
            }
            cmd.append('\"');                     
        }
        if (pid.length() > 0 && pid.charAt(0) != '\n') {
            cmd.append(" -p ");
            cmd.append(pid);
        }
        
        if (args.length() > 0 && args.charAt(0) != '\n') {
            cmd.append(" ");
            cmd.append(args);
        }  
              
        return cmd.toString();
    }
}
