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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.netbeans.modules.vcs.advanced.globalcommands.GlobalExecutionContext;
import org.netbeans.modules.vcs.advanced.recognizer.CommandLineVcsFileSystemInfo;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;

/**
 *
 * @author  Richard Gregor
 */
public class VssGlobalRegister extends Object implements VcsAdditionalCommand {
   
    private CommandExecutionContext context = null;
    
    public void setExecutionContext(CommandExecutionContext context){                
        this.context = context;
    }
    
    /** 
     * Creates new VssGlobalRegister
     */
    public VssGlobalRegister() {
    }
    
 
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                
        // add aditional variables from cmd as values of customizer 
        Hashtable addVars = new Hashtable(); 
        String ssdir = (String)vars.get("ENVIRONMENT_VAR_SSDIR");                            //NOI18N
        if(ssdir != null) addVars.put("ENVIRONMENT_VAR_SSDIR",ssdir);                  //NOI18N
        String project = (String)vars.get("PROJECT");                           //NOI18N
        if(project != null) addVars.put("PROJECT",project);             //NOI18N
        String cmd = (String)vars.get("VSSCMD_EXE");                    //NOI18N
	if(cmd != null) addVars.put("VSSCMD_EXE",cmd);                  //NOI18N
        String workPath = (String)vars.get("ROOTDIR");                        //NOI18N
        if(workPath != null) addVars.put("ROOTDIR",workPath);           //NOI18N
        String username = (String)vars.get("USER_NAME");                        //NOI18N
        if(username != null) addVars.put("USER_NAME",username);         //NOI18N 
        String password = (String)vars.get("PASSWORD");                 //NOI18N
        if (password != null) addVars.put("PASSWORD", password);
        
        File dir = new File(workPath);
        FSRegistry registry = FSRegistry.getDefault();               
        FSInfo[] registeredInfos = registry.getRegistered();
        for (int i = 0; i < registeredInfos.length; i++) {
            if (dir.equals(registeredInfos[i].getFSRoot())) {
                return true; // It's already registered
            }
        }
        GlobalExecutionContext globalContext = (GlobalExecutionContext)context;        
        String profileFileName= globalContext.getProfileName();        
        CommandLineVcsFileSystemInfo info = new CommandLineVcsFileSystemInfo(dir, profileFileName, addVars);
        registry.register(info);                  
        return true;
    }
}
