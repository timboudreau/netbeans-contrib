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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

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
public class PvcsGlobalRegister extends Object implements VcsAdditionalCommand {
  
    private CommandExecutionContext context = null;
    
    public void setExecutionContext(CommandExecutionContext context){                
        this.context = context;
    }
    
    /** 
     * Creates new PvcsGlobalRegister
     */
    public PvcsGlobalRegister() {
    }
    
 
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                
        // add aditional variables from cmd as values of customizer 
        Hashtable addVars = new Hashtable(); 
        String dtb = (String)vars.get("PROJECT_DB");                            //NOI18N
        if(dtb != null) addVars.put("PROJECT_DB",dtb);                  //NOI18N
        String project = (String)vars.get("PROJECT");                           //NOI18N
        if(project != null) addVars.put("PROJECT",project);             //NOI18N
        String workPath = (String)vars.get("WORK_PATH");                        //NOI18N
        if(workPath != null) addVars.put("ROOTDIR",workPath);           //NOI18N
        String username = (String)vars.get("USER_NAME");                        //NOI18N
        if(username != null) addVars.put("USER_NAME",username);         //NOI18N     
        
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
