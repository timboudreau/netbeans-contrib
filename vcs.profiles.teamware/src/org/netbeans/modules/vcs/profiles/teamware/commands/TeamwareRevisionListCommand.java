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

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.windows.TopComponent;

import org.netbeans.api.diff.Diff;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.RevisionList;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList;

public class TeamwareRevisionListCommand implements VcsAdditionalCommand {

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
        File sccsDir = new File(file.getParentFile(), "SCCS");
        File sFile = new File(sccsDir, "s." + file.getName());
        RevisionList list = getRevisions(sFile);
        String encodedList = null;
        try {
            encodedList = VcsUtilities.encodeValue(list);
        } catch (IOException e) {
            // return null
        }
        stdoutData.outputData(new String[] { encodedList });
        return true;
    }
    
    /** Parse the SCCS file to get revision details */
    private RevisionList getRevisions(File sFile) {
        RevisionList list = new NumDotRevisionList();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(sFile));
            NumDotRevisionItem item = null;
            boolean done = false;
            while (!done) {
                String s = in.readLine();
                if (s == null || s.length() < 2 || s.charAt(0) != (char) 1) {
                    done = true;
                    continue;
                }
                switch (s.charAt(1)) {
                    case 'h': break;
                    case 's': break;
                    case 'd': {
                        String[] data = s.split(" ");
                        item = new NumDotRevisionItem(data[2]);
                        item.setAuthor(data[5]);
                        item.setDate(data[3]);
                        item.setMessage("");
                        break;
                    }
                    case 'c': {
                        if (s.length() > 2) {
                            String commentText = s.substring(3);
                            String existingComment = item.getMessage();
                            if (existingComment.length() > 0) {
                                item.setMessage(existingComment + "\n" + commentText);
                            } else {
                                item.setMessage(commentText);
                            }
                        }
                        break;
                    }
                    case 'e': {
                        list.add(item);
                        break;
                    }
                    case 'i': break;
                    default:
                        done = true;
                }
            }
        } catch (Exception e) {
            // ok, return the revisions that were found
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            // ok
        }
        return list;
    }

}
