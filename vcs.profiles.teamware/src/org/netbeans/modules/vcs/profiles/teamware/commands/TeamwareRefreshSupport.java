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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.modules.vcs.profiles.teamware.util.SFile;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

class TeamwareRefreshSupport {

    private static final File[] EMPTY_FILE_ARRAY = new File[0];
    
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
            SFile sFile = new SFile(file);
            File pFile = new File(sccsDir, "p." + fileName);
            if (!sFile.exists()) {
                if (ignoreFile(file)) {
                    state = "Ignored";
                } else {
                    state = "Local";
                }
            } else {
                revision = sFile.getRevisions().getActiveRevision()
                    .getRevision();
                if (revision == null) {
                    revision = "";
                    stderr.outputLine(file + ": cannot determine revision number");
                }
                boolean writable = file.canWrite();
                if (pFile.exists()) {
                    state = "Editing";
                    if (!writable) {
                        stderr.outputLine(file + " should be writable");
                    }
                } else {
                    if (file.exists()) {
                        if (writable) {
                            state = "Writable but not checked out for editing";
                        } else {
                            state = "Checked in";
                        }
                    } else {
                        state = "Needs checkout";
                    }
                    if (writable) {
                        stderr.outputLine(file + " should not be writable");
                    }
                }
            }
        }
        String[] data = {
            state,
            fileName,
            revision
        };
        return data;
    }
    
    static File[] listFilesInDir(final File dir) {
        if (ignoreFile(dir)) {
            return EMPTY_FILE_ARRAY;
        }
        final Set fileSet = new TreeSet();
        File[] files = dir.listFiles();
        if (files != null) {
            fileSet.addAll(Arrays.asList(files));
        }
        File sccsDir = new File(dir, "SCCS");
        if (sccsDir.exists()) {
            sccsDir.list(new FilenameFilter() {
                public boolean accept(File sdir, String name) {
                    if (name.startsWith("s.")) {
                        fileSet.add(new File(dir, name.substring(2)));
                    }
                    return false;
                }
            });
        }
        return (File[]) fileSet.toArray(EMPTY_FILE_ARRAY);
    }
    
}
