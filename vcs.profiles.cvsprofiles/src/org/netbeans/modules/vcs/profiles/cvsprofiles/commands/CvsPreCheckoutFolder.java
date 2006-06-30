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
                            
        String root = (String) vars.get("CHECKOUT_ROOTDIR"); // For CHECKOUT_IMPORTED global command
        if (root == null) {
            root = (String) vars.get("ROOTDIR");
        }
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
