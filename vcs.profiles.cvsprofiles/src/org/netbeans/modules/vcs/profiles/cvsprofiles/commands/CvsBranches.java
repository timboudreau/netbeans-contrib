/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionListener;

import org.netbeans.modules.vcs.profiles.commands.Diff;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsBranches implements VcsAdditionalCommand, RevisionListener {

    private String rootDir = null;
    private String dir = null;
    private String file = null;
    private String filePath = null;
    Hashtable vars = null;
    CvsLogInfo logInfo = null;
    private String[] diffArgs = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;
    private CommandDataOutputListener stdoutListener = null;
    private CommandDataOutputListener stderrListener = null;
    private String dataRegex = null;
    private String errorRegex = null;
    private VcsFileSystem fileSystem = null;
    private String[] args;
    private CvsBranchFrame branchFrame;

    Hashtable branchPositions = null;
    int graphHeight = 0;
    int graphWidth = 0;
    private Hashtable itemPositionIntervals = null;

    /** Creates new CvsBranches */
    public CvsBranches() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        if (this.fileSystem != null)
            this.fileSystem.addRevisionListener (this);
    }

    /**
     * Calculates the positions of branches and the number of elements along x and y axis.
     */
    private void computeBranchesPositions() {
        CvsRevisionGraphItem root = logInfo.getRevisionGraph().getRoot();
        int xPos = 0;
        int yPos = 0;
        graphHeight = 1;
        graphWidth = 1;
        branchPositions = new Hashtable();
        itemPositionIntervals = new Hashtable();
        computeBranchesPositions(root, xPos, yPos);
    }

    private void computeBranchesPositions(CvsRevisionGraphItem item, int xPos, int yPos) {
        if (xPos >= graphWidth) graphWidth = xPos + 1;
        if (yPos >= graphHeight) graphHeight = yPos + 1;
        if (item == null) return;
        addItemPosition(xPos, yPos);
        computeBranchesPositions(item.next, xPos, yPos + 1);
        item.setXPos(xPos);
        item.setYPos(yPos);
        Vector branches = item.getBranches();
        if (branches != null) {
            Enumeration enum = branches.elements();
            while (enum.hasMoreElements()) {
                CvsRevisionGraphItem branch = (CvsRevisionGraphItem) enum.nextElement();
                int xb = getItemFreePosition(xPos, yPos + 1);
                computeBranchesPositions(branch, xb, yPos + 1);
            }
        }
    }

    private void addItemPosition(int x, int y) {
        Vector intervals = (Vector) itemPositionIntervals.get(new Integer(x));
        if (intervals == null) {
            intervals = new Vector();
            intervals.add(new Integer(y));
            itemPositionIntervals.put(new Integer(x), intervals);
        } else {
            intervals.add(new Integer(y));
            //Enumeration enum = intervals.elements();
            //while(enum.hasMoreElements()) {
            // enum.nextElement();
        }
    }

    private int getItemFreePosition(int xPos, int yPos) {
        int x;
        for(x = xPos + 1; itemPositionIntervals.get(new Integer(x)) != null; x++);
        return x;
    }

    private void computeBranchesPositions_last(CvsRevisionGraphItem item, int xPos, int yPos) {
        if (xPos >= graphWidth) graphWidth = xPos + 1;
        if (yPos >= graphHeight) graphHeight = yPos + 1;
        if (item == null) return;
        Vector branches = item.getBranches();
        if (branches != null) {
            Enumeration enum = branches.elements();
            while (enum.hasMoreElements()) {
                CvsRevisionGraphItem branch = (CvsRevisionGraphItem) enum.nextElement();
                String branchName = branch.getRevision();
                xPos++;
                branchPositions.put(branchName, new java.awt.Point(xPos, yPos + 1));
                computeBranchesPositions(branch.next, xPos, yPos + 1);
            }
        }
        CvsRevisionGraphItem next = item.getNext();
        if (next != null) computeBranchesPositions(next, xPos, yPos + 1);
    }

    private void drawBranches() {
        //logInfo.getRevisionGraph();
        computeBranchesPositions();
        this.branchFrame = new CvsBranchFrame(logInfo, this);
        VcsUtilities.centerWindow(branchFrame);
        branchFrame.setPositions(graphWidth, graphHeight, branchPositions);
        branchFrame.setVisible(true);
        //logInfo.getRevisionGraph().getRoot().print();
    }

    public boolean doDiff(String revision1, String revision2) {
        Diff diff = new Diff();
        diff.setFileSystem(fileSystem);
        String args[] = null;
        if (revision1 != null) {
            if (revision2 != null) {
                args = new String[5];
                args[1] = revision2;
            } else {
                args = new String[4];
            }
            args[0] = revision1;
        } else args = new String[3];
        args[args.length - 3] = "0"; // The output specification
        for(int i = 0; i < 2; i++) args[i + args.length - 2] = diffArgs[i];
        return diff.exec(vars, args, stdoutNRListener, stderrNRListener,
                         stdoutListener, dataRegex, stderrListener, errorRegex);
    }

    public void close() {
        if (this.fileSystem != null)
            this.fileSystem.removeRevisionListener (this);
    }
    
    String getFileName() {
        return file;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        this.vars = vars;
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        this.dataRegex = dataRegex;
        this.stderrListener = stderrListener;
        this.errorRegex = errorRegex;
        this.file = (String) vars.get("FILE"); // NOI18N
        this.filePath = (String) vars.get("PATH"); // NOI18N
        this.args = args;
        boolean success = performWork ();
        if (success)
            drawBranches();
        return success;
    }
    
    private boolean performWork () {
        boolean success;
        this.logInfo = new CvsLogInfo();
        this.logInfo.setFileSystem(fileSystem);
        if (this.args.length < 3) {
            String message = "Too few arguments to View Branches command !"; // NOI18N
            String[] elements = { message };
            if (this.stderrListener != null) this.stderrListener.outputData(elements);
            if (this.stderrNRListener != null) this.stderrNRListener.outputLine(message);
            return false;
        }
        String logInfoArg = args[0];
        //logInfoArgs[0] = args[0];
        diffArgs = new String[2];
        diffArgs[0] = this.args[1];
        diffArgs[1] = this.args[2];
        success = this.logInfo.updateLogInfo(this.vars, logInfoArg, this.stdoutNRListener, this.stderrNRListener);
        return success;
    }
    
    /**
     * One or more revisions has changed.
     * @args whatChanged specifies what actually changed.
     * @args fo the file object whose revisions changed.
     * @args info contains some further informations describing what has changed.
     */
    public void stateChanged(javax.swing.event.ChangeEvent ev) {//int whatChanged, org.openide.filesystems.FileObject fo, Object info) {
        RevisionEvent event = (RevisionEvent) ev;
        String name = event.getFilePath();
        if (name != null && name.equals(this.filePath)) {//fo.getNameExt().equals(this.file)) {
            this.performWork();
            this.computeBranchesPositions();
            this.branchFrame.refresh (this.logInfo);
        }
    }
}

