/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.Vector;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDisplayer;

//import org.netbeans.modules.vcs.cmdline.exec.*;

/**
 * The selector of CVS revision.
 *
 * @author  Martin Entlicher
 */
public class CvsRevisionSelector extends Object implements VcsAdditionalCommand {

    private static final String BRANCH_OPTION = "-b"; // NOI18N
    private static final String TAGS_OPTION = "-t"; // NOI18N
    private CvsLogInfo logInfo = new CvsLogInfo();

    /** Creates new CvsRevisionSelector */
    public CvsRevisionSelector() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.logInfo.setFileSystem(fileSystem);
    }

    /**
     * Executes the command passed through arguments.
     * @param vars variables needed to run cvs commands
     * @param args the arguments should be the command to execute
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull,
     *         false if some error has occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
                            
        int startArg = 0;
        boolean branch = args.length > 0 && args[0].equalsIgnoreCase(BRANCH_OPTION);
        if (branch) startArg++;
        boolean tags = args.length > 0 && args[0].equalsIgnoreCase(TAGS_OPTION);
        if (tags) startArg++;
        //String input = (String) vars.get("INPUT"); // NOI18N
        //if (input == null) input = ""; // NOI18N
        //long timeout = ((Long) vars.get("TIMEOUT")).longValue(); // NOI18N
        CvsRevisionChooserPanel crc = new CvsRevisionChooserPanel();
        String cmdName = "";
        if (args.length > startArg) {
            cmdName = args[startArg];
            if (cmdName.charAt(0) == '"') {
                cmdName = cmdName.substring(1, cmdName.length());
                startArg++;
                int quoteIndex = args[startArg].indexOf('"');
                while(args.length >= startArg && quoteIndex < 0) {
                    cmdName += " "+args[startArg]; // NOI18N
                    startArg++;
                    quoteIndex = args[startArg].indexOf('"');
                }
                cmdName += " "+args[startArg].substring(0, quoteIndex); // NOI18N
            }
            cmdName = VcsUtilities.getBundleString(cmdName);
            crc.setCommandName(cmdName);
            //D.deb("Setting command name = "+cmdName); // NOI18N
            startArg++;
        }
        String argsCmd = null;
        if (args.length > startArg) {
            //argsCmd = new String[1];
            argsCmd = args[startArg];
            startArg++;
        }
        boolean success = this.logInfo.updateLogInfo(vars, argsCmd, stdoutNRListener, stderrNRListener);
        if (success) {
            Vector revisions;
            if (branch) {
                revisions = logInfo.getBranchesWithSymbolicNames();
            } else if (tags) {
                Hashtable theTags = logInfo.getSymbolicNames();
                revisions = new Vector(theTags.keySet());
            } else {
                revisions = logInfo.getRevisionsWithSymbolicNames();
            }
            revisions.insertElementAt("HEAD", 0); // NOI18N
            crc.setRevisions(revisions);
            org.openide.DialogDescriptor dd = new org.openide.DialogDescriptor(crc, cmdName);
            success = org.openide.NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(dd));
        } else return false;
        if (success) {
            String revision = crc.getRevision();
            //D.deb("I have revision = "+revision); // NOI18N
            if (revision != null) {
                if (stdoutListener != null) stdoutListener.outputData(new String[] { revision });
            }
        }
        return true;
    }
}
