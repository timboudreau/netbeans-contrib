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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.util.Utilities;

/**
 * After Import and Checkout, it's necessary to copy the non-sharable
 * @author  Martin Entlicher
 */
public class CopyNonSharableFiles extends Object implements VcsAdditionalCommand {
    
    private CommandExecutionContext execContext;
    
    /** Creates a new instance of CopyNonSharableFiles */
    public CopyNonSharableFiles() {
    }
    
    /** Set the VCS file system to use to execute commands.
     */
    public void setExecutionContext(CommandExecutionContext execContext) {
        this.execContext = execContext;
    }
    
    private boolean copyFromOrig(String root, String file, CommandSupport cmdSupp, String origFolder) {
        Command cmd = cmdSupp.createCommand();
        File dest = new File(root);
        File src;
        if (origFolder == null) {
            src = new File(dest.getParentFile(), dest.getName()+"_orig"); // NOI18N
            int folderIndex = file.lastIndexOf('/');
            if (folderIndex > 0) {
                String folder = file.substring(0, folderIndex);
                file = file.substring(folderIndex + 1);
                src = new File(src, folder);
                dest = new File(dest, folder);
            }
        } else {
            src = new File(origFolder);
            int folderIndex = file.lastIndexOf('/');
            if (folderIndex > 0) {
                String folder = file.substring(0, folderIndex);
                file = file.substring(folderIndex + 1);
                src = new File(src, folder);
                dest = new File(dest, folder);
            }
        }
        Map additionalVars = new HashMap();
        additionalVars.put("FILE_TO_COPY", file);
        additionalVars.put("FOLDER_COPY_SRC", src.getAbsolutePath());
        additionalVars.put("FOLDER_COPY_DEST", dest.getAbsolutePath());
        if (Utilities.isWindows()) {
             // Windows are not able to copy if not known whether it's file or folder!!!
            additionalVars.put("FILE_TO_COPY_IS_FOLDER", new File(src, file).isDirectory() ? "true" : "");
        }
        ((VcsDescribedCommand) cmd).setAdditionalVariables(additionalVars);
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        String root;
        String relativePath;
        if (execContext instanceof VcsFileSystem) {
            Collection processingFiles = ExecuteCommand.createProcessingFiles(execContext, vars);
            relativePath = (String) processingFiles.iterator().next();
            root = (String) vars.get("ROOTDIR") + File.separator + relativePath.replace('/', File.separatorChar);
        } else {
            relativePath = "";
            String checkoutRoot = (String) vars.get("CHECKOUT_ROOTDIR");
            root = (String) vars.get("ROOTDIR");
            if (checkoutRoot != null && !checkoutRoot.equals(root)) {
                root = checkoutRoot;
            }
            String reposDir = (String) vars.get("REPOS_DIR");
            if (!".".equals(reposDir)) {
                root += File.separator + reposDir;
            }
        }
        if (args.length < 1) {
            stderrListener.outputLine("Expecting a command name as an argument.");
            return false;
        }
        CommandSupport cmdSupp = execContext.getCommandSupport(args[0]);
        if (cmdSupp == null) {
            stderrListener.outputLine("Command "+args[0]+" does not exist.");
            return false;
        }

        String nonSharableFilesStr = (String) vars.get(SharableImport.VAR_NON_SHARABLE_FILES);
        String[] nonSharableFiles = VcsUtilities.getQuotedStrings(nonSharableFilesStr);
        boolean stat = true;
        int relativeLength = relativePath.length();
        if (relativeLength > 0) relativeLength++; // Separator
        String origFolder = (String) vars.get("IMPORTED_ORIG_FOLDER"); // Might not be defined
        for (int i = 0; i < nonSharableFiles.length; i++) {
            stat = copyFromOrig(root, nonSharableFiles[i].substring(relativeLength), cmdSupp, origFolder);
            if (!stat) break;
        }
        return stat;
    }
}
