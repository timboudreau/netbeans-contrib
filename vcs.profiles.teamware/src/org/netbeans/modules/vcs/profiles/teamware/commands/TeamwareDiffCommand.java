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
        final File file = new File(baseDir, (String) vars.get("FILE"));
        vars.put("FILE", file.toString());
        File sccsDir = new File(file.getParentFile(), "SCCS");
        File pFile = new File(sccsDir, "p." + file.getName());
        String revArgs = "";
        String name1 = file.getName();
        String name2 = file.getName();
        if (!pFile.exists()) {
            String title = "Diff with earlier revision";
            String message = "Select revision:";
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            JLabel messageLabel = new JLabel(message, JLabel.LEFT);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            panel.add(messageLabel, BorderLayout.NORTH);
            Revision[] revisions = getRevisions(vars, stderr);
            if (revisions == null || revisions.length == 0) {
                stderr.outputLine("No revisions found for " + file);
                return false;
            }
            JList list = new JList(revisions);
            list.setBorder(BorderFactory.createEtchedBorder());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setSelectedIndex(Math.min(revisions.length - 1, 1));
            panel.add(list, BorderLayout.CENTER);
            DialogDescriptor dd = new DialogDescriptor(panel, title);
            DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
            int result = ((Integer) dd.getValue()).intValue();
            Revision selectedRevision = (Revision) list.getSelectedValue();
            if (result == 0 && selectedRevision != null) {
                revArgs = "-r" + selectedRevision.rev;
                name2 += ": " + selectedRevision.rev;
            } else {
                return true;
            }
        } else {
            name1 += " (edited)";
            name2 += " (checked in)";
        }
        final String _name1 = name1;
        final String _name2 = name2;
        final String _revArgs = revArgs;
        try {
            File rFile = File.createTempFile("sccs", "txt");
            rFile.delete();
            vars.put("TMPFILE", rFile.toString());
            vars.put("REVARGS", _revArgs);
            VcsCommand cmd = fileSystem.getCommand("GET_REVISION");
			VcsCommandExecutor ec =
                fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
            fileSystem.getCommandsPool().waitToFinish(ec);
            if (ec.getExitStatus() != 0) {
                return false;
            }
            rFile.deleteOnExit();
            TopComponent c = (TopComponent)
                Diff.getDefault().createDiff(
                    _name1, _name1,
                    new FileReader(file),
                    _name2, _name2,
                    new FileReader(rFile),
                    "text/java");
            stdout.outputLine("c = " + c);
            if (c != null) {
                c.open();
            }
        } catch (IOException e) {
            stderr.outputLine(e.toString());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            stderr.outputLine(e.toString());
            e.printStackTrace(System.err);
        }
        return true;
    }
    
    private static class Revision {
        String rev;
        String comment;
        public String toString() {
            return rev + ": " + comment;
        }
    }
    
    private Revision[] getRevisions(Hashtable vars, final CommandOutputListener stderr) {
        
        VcsCommand cmd = fileSystem.getCommand("PRS");
        VcsCommandExecutor ec =
            fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        final List revisions = new ArrayList();
        ec.addTextOutputListener(new TextOutputListener() {
            Revision r;
            boolean nextLineIsComment = false;
            public void outputLine(String s) {
                if (nextLineIsComment) {
                    r.comment = s;
                    nextLineIsComment = false;
                    revisions.add(r);
                    r = null;
                } else if (s.startsWith("D ")) {
                    try {
                        r = new Revision();
                        r.rev = s.split(" ")[1];
                    } catch (ArrayIndexOutOfBoundsException e) { /* ignore */ }
                } else if (r != null && s.startsWith("COMMENTS:")) {
                    nextLineIsComment = true;
                }
  
            }
        });
        ec.addTextErrorListener(new TextOutputListener() {
            public void outputLine(String s) {
                stderr.outputLine(s);
            }
        });
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(ec);
        } catch (InterruptedException e) {
            return null;
        }
        if (ec.getExitStatus() != 0) {
            return null;
        } else {
            return (Revision[]) revisions.toArray(new Revision[revisions.size()]);
        }
    }

}
