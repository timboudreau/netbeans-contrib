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

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
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
                FileObject ignored = parent.getFileObject(next);
                ignoreRecursively(ignored);
                ps.println(next);
            }
            ps.close();
            stdoutListener.outputLine(ignore.getPath() + " updated.");
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

    /** Assure that ignored file and their descendants status is ignored. */
    private void ignoreRecursively(FileObject fo) {
        FileAttributeQuery faq = FileAttributeQuery.getDefault();
        FileProperties fprops = (FileProperties) faq.readAttribute(fo, FileProperties.ID);
        FileProperties ignoredProps = new FileProperties(fprops);
        ignoredProps.setStatus(Statuses.STATUS_IGNORED);
        faq.writeAttribute(fo, FileProperties.ID, ignoredProps);
        if (fo.isFolder()) {
            FileObject[] children = fo.getChildren();
            for (int i = 0; i < children.length; i++) {
                FileObject next = children[i];
                ignoreRecursively(next);
            }
        }
    }
}
