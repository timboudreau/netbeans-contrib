/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 * 
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.netbeans.modules.vcscore.commands.CommandOutputListener;

class TeamwareRefreshSupport {

    static boolean ignoreFile(File file) {
        String leaf = file.getName();
        if (leaf.startsWith(".") || leaf.endsWith("~")) {
            return true;
        }
        if (file.isDirectory()) {
            return leaf.equals("SCCS") || leaf.equals("Codemgr_wsdata")
                || leaf.equals("deleted_files");
        } else {
            return false;
        }
    }
    
    static String[] listFile(
        File file,
        File sccsDir,
        CommandOutputListener stderr) {
            
        String state = null;
        String fileName = file.getName();
        String revision = "";
        if (file.isDirectory()) {
            if (ignoreFile(file)) {
                state = "Ignored";
            } else {
                state = "";
            }
            fileName += "/";
        } else {
            File sFile = new File(sccsDir, "s." + fileName);
            File pFile = new File(sccsDir, "p." + fileName);
            if (!sFile.exists()) {
                if (ignoreFile(file)) {
                    state = "Ignored";
                } else {
                    state = "Local";
                }
            } else {
                try {
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(sFile));
                    while (in.read() != '\n') { }
                    while (in.read() != '\n') { }
                    for (int j = 0; j < 5; j ++) {
                        in.read();
                    }
                    StringBuffer sb = new StringBuffer();
                    for (int j; (j = in.read()) != ' '; sb.append((char) j)) { }
                    in.close();
                    revision = sb.toString();
                } catch (IOException e) {
                    stderr.outputLine(file + ": cannot determine revision number");
                    // ignore;
                }
                boolean writable = file.canWrite();
                if (pFile.exists()) {
                    state = "Editing";
                    if (!writable) {
                        stderr.outputLine(file + " should be writable");
                    }
                } else {
                    state = "Checked in";
                    if (writable) {
                        stderr.outputLine(file + " should not be writable");
                    }
                }
            }
        }
        String[] data = {
            state,
            file.getName(),
            revision
        };
        return data;
    }
    
}
