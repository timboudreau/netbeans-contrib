/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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
        
        GlobalExecutionContext globalContext = (GlobalExecutionContext)context;        
        String profileFileName= globalContext.getProfileName();        
        CommandLineVcsFileSystemInfo info = new CommandLineVcsFileSystemInfo(new File(workPath),profileFileName,addVars);
        FSRegistry registry = FSRegistry.getDefault();               
        registry.register(info);                  
        return true;
    }
}
