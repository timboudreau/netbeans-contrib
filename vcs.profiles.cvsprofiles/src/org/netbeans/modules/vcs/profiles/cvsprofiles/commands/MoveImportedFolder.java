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
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.openide.filesystems.FileUtil;

/**
 * After Import and before Checkout is executed, it's necessary to move
 * the imported sources to a backup directory.
 *
 * @author  Martin Entlicher
 */
public class MoveImportedFolder extends Object implements VcsAdditionalCommand {
    
    private File createOrig(File root, File[] files) {
        String orig = "ORIG";
        boolean exists = true;
        int i = 0;
        while (exists) {
            exists = false;
            for (int j = 0; j < files.length; j++) {
                if (orig.equalsIgnoreCase(files[j].getName())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                orig = "ORIG"+i;
            }
        }
        File origFolder = new File(root, orig);
        origFolder.mkdir();
        return origFolder;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {

        String rootPath = (String) vars.get("ROOTDIR");
        String checkoutPath = (String) vars.get("CHECKOUT_ROOTDIR");
        String reposDir = (String) vars.get("REPOS_DIR");
        File root = FileUtil.normalizeFile(new File(rootPath));
        File checkoutRoot = FileUtil.normalizeFile(new File(checkoutPath, reposDir));
        if (!root.equals(checkoutRoot)) {
            // Nothing will be moved when the checkout folder is different
            vars.put("IMPORTED_ORIG_FOLDER", root.getAbsolutePath());
            return true;
        }
        File[] files = root.listFiles();
        File orig = createOrig(root, files);
        for (int i = 0; i < files.length; i++) {
            files[i].renameTo(new File(root, orig.getName() + File.separator + files[i].getName()));
        }
        vars.put("IMPORTED_ORIG_FOLDER", orig.getAbsolutePath());
        return true;
    }
}
