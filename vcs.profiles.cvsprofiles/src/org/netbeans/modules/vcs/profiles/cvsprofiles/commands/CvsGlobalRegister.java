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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.netbeans.lib.cvsclient.CVSRoot;

import org.netbeans.modules.vcs.advanced.globalcommands.GlobalExecutionContext;
import org.netbeans.modules.vcs.advanced.recognizer.CommandLineVcsFileSystemInfo;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.turbo.TurboUtil;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

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
        
        String dirName = (String) vars.get("CHECKOUT_ROOTDIR");
        if (dirName == null) {
            dirName = (String) vars.get(ROOT_DIR);
        }
        if ((dirName == null) || (dirName.length() ==0))
            return false;
        File dir = new File(dirName);
        FileObject fo;
        try {
            fo = FileUtil.toFileObject(dir);
        } catch (IllegalArgumentException iaex) {
            fo = null;
        }
        FSRegistry registry = FSRegistry.getDefault();
        FSInfo[] registeredInfos = registry.getRegistered();
        for (int i = 0; i < registeredInfos.length; i++) {
            File fsRoot = registeredInfos[i].getFSRoot();
            FileObject fsfo;
            try {
                fsfo = FileUtil.toFileObject(fsRoot);
            } catch (IllegalArgumentException iaex) {
                fsfo = null;
            }
            boolean haveFOs = fo != null && fsfo != null;
            if ((haveFOs && (fsfo.equals(fo) || FileUtil.isParentOf(fsfo, fo))) ||
                (!haveFOs && dir.equals(fsRoot))) {
                if (fo != null) {
                    TurboUtil.refreshRecursively(fo);
                }
                return true; // It's already registered
            }
        }
        
        String serverType = (String) vars.get("SERVERTYPE"); // NOI18N
        String repository = (String) vars.get("CVS_REPOSITORY"); // NOI18N
        String userName = (String) vars.get("CVS_USERNAME"); // NOI18N
        String serverName = (String) vars.get("CVS_SERVER"); // NOI18N
        String serverPort = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT"); // NOI18N
                
        // add aditional variables from cmd as values of customizer 
        Hashtable addVars = new Hashtable(); 
        if(serverType != null) addVars.put("SERVERTYPE",serverType);                       // NOI18N
        if(serverName != null) addVars.put("CVS_SERVER",serverName);                        // NOI18N
        if(serverPort != null) addVars.put("ENVIRONMENT_VAR_CVS_CLIENT_PORT",serverPort);   // NOI18N
        else vars.remove("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        addVars.put("BUILT-IN", vars.get("BUILT-IN"));                                      // NOI18N
        if(userName != null) addVars.put("CVS_USERNAME",userName);                          // NOI18N
        if(repository != null) addVars.put("CVS_REPOSITORY",repository);                    // NOI18N
        String cvsexe = (String) vars.get("CVS_EXE");                                       // NOI18N
        if(cvsexe != null) addVars.put("CVS_EXE",cvsexe);                                   // NOI18N
        String rsh = (String) vars.get("ENVIRONMENT_VAR_CVS_RSH");                          // NOI18N
        if(rsh != null) addVars.put("ENVIRONMENT_VAR_CVS_RSH",rsh);                         // NOI18N
        String shell = (String) vars.get("SHELL");                                          // NOI18N
        if(shell != null) addVars.put("SHELL",shell);                                       // NOI18N
        addVars.put("USER_IS_LOGGED_IN","true");                                            // NOI18N 
        GlobalExecutionContext globalContext = (GlobalExecutionContext)context;        
        String profileFileName= globalContext.getProfileName();        
        CommandLineVcsFileSystemInfo info = new CommandLineVcsFileSystemInfo(dir, profileFileName, addVars);
        registry.register(info);                  
        return true;
    }
}
