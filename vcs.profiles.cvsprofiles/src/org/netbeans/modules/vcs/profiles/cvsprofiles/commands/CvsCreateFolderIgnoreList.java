/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsCreateFolderIgnoreList extends Object implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem;
    
    /** Creates new CvsCreateFolderIgnoreList */
    public CvsCreateFolderIgnoreList() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private File getIgnoreFile(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        String dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir = rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
            }
        } else {
            if (module == null) {
                dir = rootDir + File.separator + dir;
            } else {
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        String file = (String) vars.get("FILE");
        dir = dir + File.separator + file;
        File filePath = new File(dir);
        if (filePath.canRead()) {
            return filePath;
        } else {
            return null;
        }
    }
    
    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        String parentIgnoreList = (String) vars.get(CommandLineVcsFileSystem.VAR_PARENT_IGNORE_LIST);
        String[] parentIgnoreListItems = VcsUtilities.getQuotedStrings(parentIgnoreList);
        ArrayList ignoreList = new ArrayList(Arrays.asList(parentIgnoreListItems));
        File file = getIgnoreFile(vars);
        if (file != null) {
            CvsCreateInitialIgnoreList.addFileIgnoreList(file, ignoreList);
        }
        CvsCreateInitialIgnoreList.returnIgnoreList(ignoreList, stdoutDataListener);
        return true;
    }
    
}
