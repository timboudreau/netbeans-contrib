/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.recognizer;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;

import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRecognizer;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.ProfilesCache;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineFSRecognizer extends FSRecognizer {
    
    private static final String CONFIG_ROOT = "vcs/config"; // NOI18N
    
    public static final String VAR_AUTORECOGNIZE_FROM_FILE = "AUTORECOGNIZE_FROM_FILE"; // NOI18N
    public static final String VAR_AUTORECOGNIZE_FROM_COMMAND = "AUTORECOGNIZE_FROM_COMMAND"; // NOI18N
    
    private Map variablesByProfileNames = new HashMap();
    private Map commandsToFillByProfileNames = new HashMap();
    //private Map variablesFileFillByProfileNames = new HashMap();
    
    /** Creates a new instance of Recognizer */
    public CommandLineFSRecognizer() {
        init();
    }
    
    private void init() {
        FileSystem dfs = Repository.getDefault().getDefaultFileSystem ();
        FileObject fo = dfs.findResource(CONFIG_ROOT);
        if (fo != null) {
            ProfilesCache cache = new ProfilesCache(fo, null);
            String[] profileNames = cache.getProfilesDisplayNames();
            for (int i = 0; i < profileNames.length; i++) {
                if (cache.isOSCompatibleProfile(profileNames[i])) {
                    String profileName = cache.getProfileName(profileNames[i]);
                    Vector profileVars = cache.getProfileVariables(profileNames[i]);
                    variablesByProfileNames.put(profileName, profileVars);
                    for (Iterator varIt = profileVars.iterator(); varIt.hasNext(); ) {
                        VcsConfigVariable var = (VcsConfigVariable) varIt.next();
                        if (VAR_AUTORECOGNIZE_FROM_FILE.equals(var.getName())) {
                            String autorecFromFileStr = var.getValue();
                        } else if (VAR_AUTORECOGNIZE_FROM_COMMAND.equals(var.getName())) {
                            String className = var.getValue();
                            Class execClass = null;
                            try {
                                execClass =  Class.forName(className, true, VcsUtilities.getSFSClassLoader());
                                                           //org.openide.TopManager.getDefault().currentClassLoader());
                            } catch (ClassNotFoundException e) {}
                            if (execClass == null) {
                                try {
                                    execClass =  Class.forName(className, true,
                                                               org.openide.TopManager.getDefault().currentClassLoader());
                                } catch (ClassNotFoundException e) {
                                    ErrorManager.getDefault().notify(e);
                                }
                            }
                            if (execClass != null) commandsToFillByProfileNames.put(profileName, execClass);
                        }
                        
                    }
                }
            }
        }
    }
    
    /*
    private Map getCmdFillVars(String autoFillVarsStr) {
        String[] varsCmds = VcsUtilities.getQuotedStrings(autoFillVarsStr);
        Map autoFillVars = new HashMap();
        for (int i = 0; (i + 1) < varsCmds.length; i += 2) {
            autoFillVars.put(varsCmds[i], varsCmds[i+1]);
        }
        return autoFillVars;
    }
     */

    /**
     * Get a filesystem info for the given physical folder.
     * @param folder The folder, that is to be recognized.
     * @return Filesystem info for the given folder or <code>null</code> when
     *         no filesystem is recognized.
     */
    public FSInfo getFSInfo(File folder) {
        for (Iterator profileIt = commandsToFillByProfileNames.keySet().iterator(); profileIt.hasNext(); ) {
            String profileName = (String) profileIt.next();
            Class execClass = (Class) commandsToFillByProfileNames.get(profileName);
            VcsAdditionalCommand execCommand = null;
            try {
                execCommand = (VcsAdditionalCommand) execClass.newInstance();
            } catch (InstantiationException e) {
                ErrorManager.getDefault().notify(e);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(e);
            }
            if (execCommand != null) {
                Hashtable vars = new Hashtable();
                vars.put("ROOTDIR", folder.getAbsolutePath());
                boolean status = execCommand.exec(vars, new String[0], null,
                                                  null, null, null, null, null);
                if (status) {
                    return new CommandLineVcsFileSystemInfo(folder, profileName, vars);
                }
            }
        }
        return null;
    }
    
}
