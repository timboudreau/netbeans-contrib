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

package org.netbeans.modules.vcscore.commands;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.openide.filesystems.FileObject;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

//import org.netbeans.modules.vcscore.caching.FileCacheProvider;
//import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
//import org.netbeans.modules.vcscore.versioning.RevisionEvent;
//import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

/**
 * A provider of execution context, that is necessary for the UserCommand
 * to be executed. If this information object is an instance of
 * VcsFileSystem, more information can be obtained from VcsFileSystem.
 *
 * @author  Martin Entlicher
 */
public interface CommandExecutionContext {
    
    /**
     */
    boolean isOffLine();
    
    boolean isExpertMode();
    
    //boolean isProcessUnimportantFiles();
    
    //boolean isImportant(String name);
    
    boolean isPromptForVarsForEachFile();
    
    void setPromptForVarsForEachFile(boolean promptForVarsForEachFile);
    
    boolean isCommandNotification();
    
    void setCommandNotification(boolean commandNotification);
    
    /**  If this is FileSystem, take it from there! If not, get abolute paths.
     * If the execution context is a filesystem, it may wish to 
     *
    String convertFileToPath(java.io.File file);
    
    String convertFileToPath(FileObject file);
     */
    
    Vector getVariables();
    
    void setVariables(Vector variables);
    
    Hashtable getVariablesAsHashtable();
    
    VariableValueAdjustment getVarValueAdjustment();
    
    String[] getEnvironmentVars();
    
    /**
     * Get the map of possible pairs of status name and corresponding FileStatusInfo object.
     */
    Map getPossibleFileStatusInfoMap();
    
    VcsCommand getCommand(String name);
    
    CommandSupport getCommandSupport(String name);
    
    VcsCommandsProvider getCommandsProvider();
    
    //String getCacheIdStr();
    
    //FileCacheProvider getCacheProvider();
    
    //FileStatusProvider getStatusProvider();
    
    String getPassword();
    
    void setPassword(String password);
    
    String getPasswordDescription();
    
    String[] getUserParamsLabels();
    
    String[] getUserLocalParamsLabels();
    
    String[] getUserParams();
    
    void setUserParams(String[] userParams);
    
    boolean isAcceptUserParams();
    
    /**
     * Should be called when the modification in a file or folder is expected
     * and its content should be refreshed.
     */
    void checkForModifications(String path);
    
    //VersioningFileSystem getVersioningFileSystem();
    
    //void fireRevisionsChanged(RevisionEvent rev);
    
    /**
     * Print a debug output. If the debug property is true, the message
     * is printed to the Output Window.
     * @param msg The message to print out.
     */
    void debug(String msg);
    
    /**
     * Print an error output. Force the message to print to the Output Window.
     * The debug property is not considered.
     * @param msg the message to print out.
     */
    void debugErr(String msg);
    
}
