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

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

import org.netbeans.api.diff.Diff;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.openide.windows.TopComponent;

public class TeamwareDiffCommand implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem;
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(final Hashtable vars, String[] args,
                        final CommandOutputListener stdout,
                        final CommandOutputListener stderr,
                        final CommandDataOutputListener stdoutData, String dataRegex,
                        final CommandDataOutputListener stderrData, String errorRegex) {

        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File baseDir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            baseDir = new File(baseDir, dirName);
        }
        File file = new File(baseDir, (String) vars.get("FILE"));
        SFile sFile = new SFile(file);
        RevisionList revisions = sFile.getRevisions();
        if (revisions.isEmpty()) {
            stderr.outputLine("No existing revisions");
            return false;
        }
        RevisionItem revision = (RevisionItem) revisions.iterator().next();
        String name1 = file.getName();
        String name2 = name1 + ": " + revision.getDisplayName();
        try {
            String s = TeamwareSupport.getRevision(fileSystem, file,
                revision.getRevision());
            Component c = Diff.getDefault().createDiff(
                name1, name1, new FileReader(file),
                name2, name2, new StringReader(s), "text/java");
            if (c != null) {
                ((TopComponent) c).open();
            }
            return true;
        } catch (InterruptedException e) {
            stderr.outputLine(e.toString());
            return false;
        } catch (IOException e) {
            stderr.outputLine(e.toString());
            return false;
        }
    }
    
}
