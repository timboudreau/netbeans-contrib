/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.api.vcs.FileStatusInfo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.util.*;
import java.io.*;

/**
 * Adds selected local files to ignore list.
 * It future we can support CVS/Repository's EmptyDir entry too.
 *
 *
 * @author Petr Kuzel
 */
public class CvsAddToIgnoreList implements VcsAdditionalCommand {

    private VcsFileSystem vfs;

    public boolean exec(Hashtable vars, String[] args, CommandOutputListener stdoutListener, CommandOutputListener stderrListener, CommandDataOutputListener stdoutDataListener, String dataRegex, CommandDataOutputListener stderrDataListener, String errorRegex) {

        // collect data

        Collection c= ExecuteCommand.createProcessingFiles(vfs, vars);
        Iterator it = c.iterator();
        FileObject parent = null;
        List ignoredFiles = new ArrayList(c.size());
        while (it.hasNext()) {
            String next = (String) it.next();
            FileObject fo = vfs.findResource(next);
            String name = fo.getNameExt();
            FileProperties fprops = Turbo.getMeta(fo);
            if (fprops != null && FileStatusInfo.LOCAL.getName().equals(fprops.getStatus())) {
                parent = fo.getParent();
                ignoredFiles.add(name);
            } else {
                stdoutListener.outputLine(name + " is not local, skipping...");
            }
        }

        if (parent == null) return true;

        // append file names to .cvs ignore end

        File folder = FileUtil.toFile(parent);
        if (folder.canWrite() == false) return false;

        File ignore = new File(folder, ".cvsignore");  // NOI18N
        if (ignore.exists() == false) {
            try {
                ignore.createNewFile();
            } catch (IOException e) {
                stderrListener.outputLine(e.getLocalizedMessage());
                return false;
            }
        }

        try {
            OutputStream os = new FileOutputStream(ignore, true);
            PrintStream ps = new PrintStream(os);
            Iterator it2 = ignoredFiles.iterator();
            ps.println();
            while (it2.hasNext()) {
                String next = (String) it2.next();
                ps.println(next);
            }
            ps.close();
            stdoutListener.outputLine(ignore.getPath() + " updated.");

            // XXX better way to invalidate file status, these are ignored now
            // at least it can be declated using command variable
            TurboUtil.refreshFolder(parent);
            return ps.checkError() == false;
        } catch (FileNotFoundException e) {
            stderrListener.outputLine(e.getLocalizedMessage());
            return false;
        }
    }

    /** Called by introspection. */
    public void setFileSystem(VcsFileSystem vfs) {
        this.vfs = vfs;
    }
}
