/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.recognizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Lookup;
import org.openide.util.WeakListener;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.registry.FSRecognizer;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.Profile;
import org.netbeans.modules.vcs.advanced.ProfilesFactory;
import org.netbeans.modules.vcs.advanced.variables.ConditionedVariables;

/**
 *
 * @author  Martin Entlicher
 */
public class CommandLineFSRecognizer extends FSRecognizer implements PropertyChangeListener {
    
    private static final String CONFIG_ROOT = "vcs/config"; // NOI18N
    
    public static final String VAR_AUTORECOGNIZE_FROM_FILE = "AUTORECOGNIZE_FROM_FILE"; // NOI18N
    public static final String VAR_AUTORECOGNIZE_FROM_COMMAND = "AUTORECOGNIZE_FROM_COMMAND"; // NOI18N
    
    private Map variablesByProfileNames = new HashMap();
    private Map displayTypesByProfileNames = new HashMap();
    private Map commandsToFillByProfileNames = new HashMap();
    
    /** Creates a new instance of Recognizer */
    public CommandLineFSRecognizer() {
        init();
    }
    
    private void init() {
        ProfilesFactory profilesFactory = ProfilesFactory.getDefault();
        profilesFactory.addPropertyChangeListener(WeakListener.propertyChange(this, profilesFactory));
        String profileNames[] = profilesFactory.getProfilesNames();
        for (int i = 0; i < profileNames.length; i++) {
            if (profilesFactory.isOSCompatibleProfile(profileNames[i])) {
                registerProfile(profilesFactory.getProfile(profileNames[i]));
            }
        }
    }
    
    private void registerProfile(Profile profile) {
        String profileName = profile.getName();
        ConditionedVariables cvars = profile.getVariables();
        Collection profileVars = cvars.getSelfConditionedVariables(profile.getConditions(), Variables.getDefaultVariablesMap());
        Hashtable profileVarsByName = new Hashtable();
        variablesByProfileNames.put(profileName, profileVarsByName);
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
                                                   (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class));
                    } catch (ClassNotFoundException e) {
                        ErrorManager.getDefault().notify(e);
                    }
                }
                if (execClass != null) commandsToFillByProfileNames.put(profileName, execClass);
            }
            profileVarsByName.put(var.getName(), var.getValue());
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
    public FSInfo findFSInfo(File folder) {
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
                Hashtable vars = (Hashtable) variablesByProfileNames.get(profileName);
                if (vars == null) vars = new Hashtable();
                else vars = new Hashtable(vars);
                vars.put("ROOTDIR", folder.getAbsolutePath());
                //System.out.println("Executing "+execClass+"("+folder.getAbsolutePath()+")");
                boolean status = execCommand.exec(vars, new String[0], null,
                                                  null, null, null, null, null);
                if (status) {
                    String root = (String) vars.get("ROOTDIR");
                    //System.out.println("  root = "+root);
                    folder = new File(root);
                    return new CommandLineVcsFileSystemInfo(folder, profileName, vars);
                }
            }
        }
        return null;
    }
    
    /**
     * Create an empty customizable filesystem info.
     * That is intended for creating of new filesystem information,
     * that were not recognized automatically.
     */
    public FSInfo createFSInfo() {
        return new CommandLineVcsFileSystemInfo(new File(""), null, null);
    }
    
    /**
     * This method gets called when ProfilesFactory property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProfilesFactory.PROP_PROFILE_ADDED.equals(evt.getPropertyName())) {
            String profileName = (String) evt.getNewValue();
            Profile profile = ProfilesFactory.getDefault().getProfile(profileName);
            if (ProfilesFactory.getDefault().isOSCompatibleProfile(profileName)) {
                registerProfile(profile);
            }
        } else if (ProfilesFactory.PROP_PROFILE_REMOVED.equals(evt.getPropertyName())) {
            String profileName = (String) evt.getOldValue();
            variablesByProfileNames.remove(profileName);
            displayTypesByProfileNames.remove(profileName);
            commandsToFillByProfileNames.remove(profileName);
        }
    }
    
}
