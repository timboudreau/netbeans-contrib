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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.util.*;

import org.openide.filesystems.*;
import org.openide.loaders.*;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The Revision Explorer data parser for VSS.
 * @author  Martin Entlicher
 */
public class VssRevisionListGetter extends Object implements VcsAdditionalCommand, CommandDataOutputListener {

    private static final String revisionStr = "*****************  Version "; // NOI18N
    private static final String labelStr = "**********************"; // NOI18N
    private static final String userStr = "User: "; // NOI18N
    private static final String dateStr = "Date: "; // NOI18N
    private static final String timeStr = "Time: "; // NOI18N
    private static final String commentStr = "Comment: "; // NOI18N
    private static final String labelTextStr = "Label: \""; // NOI18N
    private static final String labelTextEndStr = "\""; // NOI18N
    private static final String labelCommentStr = "Label comment: "; // NOI18N

    private ArrayList revisionItems = new ArrayList();
    private ArrayList lastRevisionItems = null;
    private VcsFileSystem fileSystem = null;
    private VcsCommand logCmd = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;

    /** Creates new VssRevisionListGetter */
    public VssRevisionListGetter() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Run the vlog command and return the exit status.
     */
    private boolean runLog(Hashtable vars) throws InterruptedException {
        //System.out.println("runLog("+prepared+", "+timeout+", "+logDataRegex+")");
        revisionItems = new ArrayList();
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(logCmd, vars);
        vce.addDataOutputListener(this);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            throw iexc;
        }
        if (vce.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus()); // NOI18N
            return false;
        }
        //addTags();
        return true;
    }
    
    /**
     * Executes the vlog command to get the logging informations.
     * @param vars variables needed to run cvs commands
     * @param args the arguments,
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
    public boolean exec(final Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.logCmd = fileSystem.getCommand(args[0]);
        boolean status;
        try {
            status = runLog(new Hashtable(vars));
        } catch (InterruptedException iexc) {
            return false;
        }
        if (false == status) return status;
        final NumDotRevisionList list = new NumDotRevisionList();
        NumDotRevisionItem rootItem = new NumDotRevisionItem(null);
        Object[] revisionItemsSorted = revisionItems.toArray(new RevisionItem[0]);
        Arrays.sort(revisionItemsSorted);
        revisionItems = new ArrayList(Arrays.asList(revisionItemsSorted));
        lastRevisionItems = revisionItems;
        for(Iterator it = revisionItems.iterator(); it.hasNext(); ) {
            RevisionItem item = (RevisionItem) it.next();
            //System.out.println("CvsRevisionViewer:list.add("+item.getRevision()+")");
            list.add(item);
        }
        returnEncodedList(list, stdoutListener);
        return true;
    }
    
    private void returnEncodedList(NumDotRevisionList list, CommandDataOutputListener dataListener) {
        String encodedList;
        try {
            encodedList = VcsUtilities.encodeValue(list);
        } catch (java.io.IOException ioExc) {
            encodedList = null;
        }
        dataListener.outputData(new String[] { encodedList });
    }
    
    /**
     * Get the last revision item obtained.
     */
    private RevisionItem getLastRevisionItem() {
        RevisionItem item = null;
        for(int i = revisionItems.size() - 1; i >= 0; i--) {
            RevisionItem it = (RevisionItem) revisionItems.get(i);
            if (!it.isBranch()) {
                //System.out.println("getLastRevision: "+it.getRevision());
                item = it;
                break;
            }
        }
        return item;
    }
    
    private boolean gettingComment = false;
    private boolean gettingLabelComment = false;
    private boolean gettingLabel = false;
    private ArrayList globalLabels = new ArrayList();
    private Map lastLabelProperties = null;
    private String txtLabel = NbBundle.getMessage(VssRevisionListGetter.class, "CTL_LabelText");
    private String txtLabelUser = NbBundle.getMessage(VssRevisionListGetter.class, "CTL_LabelUser");
    private String txtLabelDate = NbBundle.getMessage(VssRevisionListGetter.class, "CTL_LabelDate");
    private String txtLabelComment = NbBundle.getMessage(VssRevisionListGetter.class, "CTL_LabelComment");
    
    private void addGlobalLabels(RevisionItem item) {
        for (int i = 0; i < globalLabels.size(); i += 2) {
            String label = (String) globalLabels.get(i);
            Map properties = (Map) globalLabels.get(i+1);
            item.addAdditionalPropertiesSet(label, properties);
        }
        globalLabels.clear();
    }

    public void outputData(String[] elements) {
        if (elements == null || elements.length == 0 || elements[0] == null) return;
        if (elements[0].indexOf(revisionStr) == 0) {
            gettingComment = false;
            gettingLabelComment = false;
            gettingLabel = false;
            int endRev = elements[0].indexOf('*', revisionStr.length());
            if (endRev < 0) endRev = elements[0].length();
            String revision = elements[0].substring(revisionStr.length(), endRev).trim();
            String revisionVCS = revision;
            revision += ".0";
            NumDotRevisionItem item = new NumDotRevisionItem(revision);
            item.setRevisionVCS(revisionVCS);
            addGlobalLabels(item);
            revisionItems.add(item);
        } else if (elements[0].indexOf(userStr) == 0) {
            gettingComment = false;
            gettingLabelComment = false;
            RevisionItem item = getLastRevisionItem();
            int index1 = userStr.length();
            int index2 = elements[0].indexOf(dateStr, index1);
            if (index2 < index1) return;
            String user = elements[0].substring(index1, index2).trim();
            if (gettingLabel) {
                lastLabelProperties.put(txtLabelUser, user);
            } else {
                item.setAuthor(user);
            }
            index1 = index2 + dateStr.length(); // date
            index2 = elements[0].indexOf(timeStr, index1);
            if (index2 < index1) return;
            int len = elements[0].length();
            if (index2 < index1) index2 = len;
            String date = elements[0].substring(index1, index2).trim();
            //item.setDate(elements[0].substring(index1, index2).trim());
            index1 = index2 + timeStr.length(); // time
            String time = elements[0].substring(index1).trim();
            if (gettingLabel) {
                lastLabelProperties.put(txtLabelDate, date + " " + time);
            } else {
                item.setDate(date + " " + time);
            }
        } else if (elements[0].indexOf(commentStr) == 0) {
            RevisionItem item = getLastRevisionItem();
            String msg = elements[0].substring(commentStr.length());
            item.setMessage(msg);
            gettingComment = true;
            gettingLabelComment = false;
        } else if (elements[0].indexOf(labelStr) == 0) {
            gettingLabel = true;
            gettingComment = false;
            gettingLabelComment = false;
            globalLabels.add("".intern());
            globalLabels.add(lastLabelProperties = new HashMap());
        } else if (elements[0].indexOf(labelTextStr) == 0) {
            String labelText = elements[0].substring(labelTextStr.length());
            if (labelText.endsWith(labelTextEndStr)) labelText = labelText.substring(0, labelText.length() - labelTextEndStr.length());
            if (gettingLabel) {
                globalLabels.set(globalLabels.size() - 2, labelText);
                lastLabelProperties.put(txtLabel, labelText);
            } else {
                RevisionItem item = getLastRevisionItem();
                item.addProperty(txtLabel, labelText);
            }
            gettingComment = false;
            gettingLabelComment = false;
        } else if (elements[0].indexOf(labelCommentStr) == 0) {
            String labelComment = elements[0].substring(labelCommentStr.length());
            if (gettingLabel) {
                lastLabelProperties.put(txtLabelComment, labelComment);
            } else {
                RevisionItem item = getLastRevisionItem();
                item.addProperty(txtLabelComment, labelComment);
            }
            gettingLabelComment = true;
            gettingComment = false;
        } else if (gettingComment && revisionItems.size() > 0) {
            RevisionItem item = getLastRevisionItem();
            String msg = item.getMessage();
            if (msg == null) return ;
            msg += "\n" + elements[0];
            item.setMessage(msg);
        } else if (gettingLabelComment) {
            if (gettingLabel) {
                String labelComment = (String) lastLabelProperties.get(txtLabelComment);
                if (labelComment == null) {
                    labelComment = elements[0];
                } else {
                    labelComment += "\n" + elements[0];
                }
                lastLabelProperties.put(txtLabelComment, labelComment);
            } else {
                RevisionItem item = getLastRevisionItem();
                String labelComment = (String) item.getAdditionalProperties().get(txtLabelComment);
                if (labelComment == null) {
                    labelComment = elements[0];
                } else {
                    labelComment += "\n" + elements[0];
                }
                item.addProperty(txtLabelComment, labelComment);
            }
        }
    }
}
