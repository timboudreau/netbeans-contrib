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

import org.openide.windows.TopComponent;

import org.netbeans.api.diff.Diff;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class TeamwareRevisionDiffCommand implements VcsAdditionalCommand {

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
        final File file = new File(baseDir, (String) vars.get("FILE"));
        String revision1 = (String) vars.get("REVISION1");
        String revision2 = (String) vars.get("REVISION2");
        Component c = null;
        try {
            if (revision2 == null) {
                // diff between current version and specified revision
                String name1 = file.getName();
                String name2 = name1 + ": " + revision1;
                String s = TeamwareSupport.getRevision(fileSystem, file, revision1);
                c = Diff.getDefault().createDiff(
                    name1, name1, new FileReader(file),
                    name2, name2, new StringReader(s), "text/java");
            } else {
                String name1 = file.getName() + ": " + revision1;
                String name2 = file.getName() + ": " + revision2;
                String s1 = TeamwareSupport.getRevision(fileSystem, file, revision1);
                String s2 = TeamwareSupport.getRevision(fileSystem, file, revision2);
                c = Diff.getDefault().createDiff(
                    name1, name1, new StringReader(s1),
                    name2, name2, new StringReader(s2), "text/java");
            }
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
