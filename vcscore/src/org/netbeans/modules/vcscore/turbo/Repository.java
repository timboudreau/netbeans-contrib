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
package org.netbeans.modules.vcscore.turbo;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.VcsManager;

import java.io.File;

/**
 * Connects to repository to get fresh status.
 *
 * @author Petr Kuzel
 */
final class Repository {

    /**
     * Can return null on failure = unknown status.
     */
    public static FileProperties get(FileObject fileObject) {
        refresh(fileObject);
        return null;

    }

    /**
     * Run REFRESH on the repository directory.
     * It delegates to profile provider.
     */
    private static void refresh(FileObject cacheDir) {
        CommandSupport engine = null;

        try {
            FileSystem fsystem;
            fsystem = cacheDir.getFileSystem();
            if (fsystem instanceof VcsFileSystem) {
                boolean offLine = ((VcsFileSystem)fsystem).isOffLine();
                if (offLine) {
                    // VcsCommand.NAME_REFRESH_RECURSIVELY for recursive impl
                    engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE);
                } else {
                    engine = ((VcsFileSystem)fsystem).getCommandSupport(VcsCommand.NAME_REFRESH);
                }
            }
        } catch (FileStateInvalidException e) {
            // engine = null
        }

        if (engine != null) {
            Command cmd = engine.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setDiskFiles(new File[] { FileUtil.toFile(cacheDir) });
                ((VcsDescribedCommand) cmd).addDirReaderListener(TurboUtil.dirReaderListener());
                VcsManager.getDefault().showCustomizer(cmd);
                cmd.execute();  // TODO check if I get data in synchonous manner
            }
        }
    }

}
