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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.netbeans.lib.cvsclient.CVSRoot;
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
public class CvsGlobalRegister extends Object implements VcsAdditionalCommand {
 
    private static final String ROOT_DIR = "ROOTDIR"; // NOI18N
    private static final String CVS_DIR = "CVS"; // NOI18N
    private static final String CVS_LOCAL = "local"; // NOI18N
    private static final String CVS_EXT = "ext";  // NOI18N
    
    private CommandExecutionContext context = null;
    
    public void setExecutionContext(CommandExecutionContext context){                
        this.context = context;
    }
    
    /** 
     * Creates new CvsGlobalRegister
     */
    public CvsGlobalRegister() {
    }
    
 
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        
        String dirName = (String) vars.get(ROOT_DIR);
        String rootStr = (String) vars.get("CVS_ROOT");// NOI18N
        if((dirName == null)|| (dirName.length() ==0))
            return false;        
        String serverType = null;
        String repository = null;
        String userName = null;
        String serverName = null;
        String serverPort = null;
        CVSRoot cvsroot = null;
        try {
            cvsroot = CVSRoot.parse(rootStr);
            serverType = cvsroot.getMethod();            
            if (serverType == null) {
                if (cvsroot.isLocal()) {
                    serverType = CVS_LOCAL;
                } else {
                    serverType = CVS_EXT;
                }
            }
            repository = cvsroot.getRepository();
            userName = cvsroot.getUserName();
            serverName = cvsroot.getHostName();
            int port = cvsroot.getPort();
            if (port > 0) {
                serverPort = Integer.toString(port);
            }
        } catch (IllegalArgumentException iaex) {
            //doesn't matter - nothing will be filled in
        }
                
        // add aditional variables from cmd as values of customizer 
        Hashtable addVars = new Hashtable(); 
        if(serverType != null) addVars.put("SERVERTYPE",serverType);                       // NOI18N
        if(serverName != null) addVars.put("CVS_SERVER",serverName);                        // NOI18N
        if(serverPort != null) addVars.put("ENVIRONMENT_VAR_CVS_CLIENT_PORT",serverPort);   // NOI18N
        else vars.remove("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        vars.remove("BUILT-IN"); // Not to alter that variable
        if(userName != null) addVars.put("CVS_USERNAME",userName);                          // NOI18N
        if(repository != null) addVars.put("CVS_REPOSITORY",repository);                    // NOI18N
        String builtin = (String) vars.get("BUILT-IN-GLB");                                 // NOI18N
        if(builtin != null) addVars.put("BUILT-IN",builtin);                                // NOI18N
        String cvsexe = (String) vars.get("CVS_EXE");                                       // NOI18N
        if(cvsexe != null) addVars.put("CVS_EXE",cvsexe);                                   // NOI18N
        String rsh = (String) vars.get("ENVIRONMENT_VAR_CVS_RSH");                          // NOI18N
        if(rsh != null) addVars.put("ENVIRONMENT_VAR_CVS_RSH",rsh);                         // NOI18N
        String shell = (String) vars.get("SHELL");                                          // NOI18N
        if(shell != null) addVars.put("SHELL",shell);                                       // NOI18N
        addVars.put("USER_IS_LOGGED_IN","true");                                            // NOI18N 
        GlobalExecutionContext globalContext = (GlobalExecutionContext)context;        
        String profileFileName= globalContext.getProfileName();        
        CommandLineVcsFileSystemInfo info = new CommandLineVcsFileSystemInfo(new File(dirName),profileFileName,addVars);
        FSRegistry registry = FSRegistry.getDefault();               
        registry.register(info);                  
        return true;
    }
}
