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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

/**
 * This class creates CVS/ folder with empty Entries and Repository file
 * and Root file with the current CVSROOT.
 * This is necessary for successful checkout on Windows and checkout
 * by javacvs client, which does not create the CVS folder in the root
 * directory.
 *
 * @author  Martin Entlicher
 */
public class CvsPreCheckoutFolder extends Object implements VcsAdditionalCommand {
    
    private static final String CVS_FOLDER_NAME = "CVS"; // NOI18N
    
    /** Creates a new instance of CvsPreCheckoutFolder */
    public CvsPreCheckoutFolder() {
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                            
        String root = (String) vars.get("ROOTDIR");
        String dir = (String) vars.get("DIR");
        String file = (String) vars.get("FILE");
        // Do nothing when we're not in root.
        if (dir != null && dir.length() > 0 &&
            file != null && file.length() > 0) return true;
        File rootDir = new File(root);
        if (!isACVSFolder(rootDir)) {
            String cvsRoot = (String) vars.get("CVSROOT");
            cvsRoot = Variables.expand(vars, cvsRoot, false);
            if (createCVSFolder(rootDir, cvsRoot)) {
                stdoutNRListener.outputLine("CVS folder with basic administrative files created successfully in "+rootDir.getAbsolutePath()+" directory.");
            } else {
                stderrNRListener.outputLine("A problem in creation of CVS folder with basic administrative files in "+rootDir.getAbsolutePath()+" directory.");
            }
        }
        return true;
    }
    
    private static boolean isACVSFolder(File dir) {
        File[] children = dir.listFiles();
        if (children == null) return false;
        boolean is = false;
        for (int i = 0; i < children.length; i++) {
            if (children[i].getName().equals(CVS_FOLDER_NAME)) {
                return true;
            } else {
                if (children[i].isDirectory() && new File(children[i], CVS_FOLDER_NAME).exists()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean createCVSFolder(File root, String cvsRoot) {
        File cvsFolder = new File(root, CVS_FOLDER_NAME);
        if (!cvsFolder.mkdir()) return false;
        File entries = new File(cvsFolder, "Entries");
        if (!writeContent(entries, "D\n")) return false;
        File repository = new File(cvsFolder, "Repository");
        if (!writeContent(repository, ".\n")) return false;
        File rootF = new File(cvsFolder, "Root");
        if (!writeContent(rootF, cvsRoot + '\n')) return false;
        return true;
    }
    
    private static boolean writeContent(File file, String content) {
        Writer w = null;
        try {
            w = new BufferedWriter(new FileWriter(file));
            w.write(content);
        } catch (IOException ioex) {
            return false;
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException ioex) {}
            }
        }
        return true;
    }
    
}
