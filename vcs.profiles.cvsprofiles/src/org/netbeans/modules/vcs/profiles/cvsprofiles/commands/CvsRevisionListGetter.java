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

import org.openide.filesystems.*;
import org.openide.loaders.*;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.versioning.RevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionItem;
import org.netbeans.modules.vcscore.versioning.impl.NumDotRevisionList;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The getter of encoded revision list. The list of revisions is created
 * (RevisionList object) and it's serialized encoded value is put to the data
 * output.
 * @author  Martin Entlicher
 */
public class CvsRevisionListGetter extends java.lang.Object implements VcsAdditionalCommand, CommandDataOutputListener {

    private static final String revisionStr = "revision"; // NOI18N
    private static final String lockedStr = "locked by:"; // NOI18N
    private static final String branchesStr = "branches"; // NOI18N
    private static final String dateStr = "date: "; // NOI18N
    private static final String authorStr = "author: "; // NOI18N
    private static final String stateStr = "state"; // NOI18N
    private static final String linesStr = "lines"; // NOI18N
    private static final String symbNamesStr = "symbolic names"; // NOI18N
    private static final String descriptionStr = "description:"; // NOI18N
    private static final String nextRevisionStr = "---------------"; // NOI18N
    private static final String nextFileStr = "=================="; // NOI18N
    private static final char ATTR_DELIM = ';'; // Attribute delimeter for date, author, state, lines
    private static final char ATTR_NAME_END = ':'; // The end of attribute name

    //private StringBuffer logBuffer = new StringBuffer(4096);
    private boolean matchingSymbolicNames = false;
    private boolean matchingDescription = false;
    private boolean matchingRevision = false;
    private String description = "";
    private Hashtable symbolicNames = new Hashtable();
    private ArrayList revisionItems = new ArrayList();
    private ArrayList lastRevisionItems = null;
    private VcsFileSystem fileSystem = null;
    private VcsCommand logCmd = null;
    private String path = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;

    private FileStatusProvider statusProvider = null;
    private String currentRevision = null;
    
    /** Creates new CvsRevisionViewer */
    public CvsRevisionListGetter() {
    }
    
    /*
    private ArrayList createRevisionItems() {
        ArrayList revisionItems = new ArrayList();
        String log = logBuffer.toString();
        return revisionItems;
    }
     */

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Run the log command and return the exit status.
     */
    private synchronized boolean runLog(Hashtable vars) throws InterruptedException {
        //System.out.println("runLog("+prepared+", "+timeout+", "+logDataRegex+")");
        if (this.path == null) {
            this.path = (String) vars.get("PATH");
            this.path = this.path.replace(java.io.File.separatorChar, '/');
        }
        /*
        this.currentRevision = null;
        if (statusProvider != null) {
            this.currentRevision = statusProvider.getFileRevision(path);
        }
         */
        //System.out.println("runLog(): currentRevision = "+currentRevision);
        matchingSymbolicNames = false;
        matchingDescription = false;
        matchingRevision = false;
        symbolicNames = new Hashtable();
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
        addTags();
        return true;
    }
    
    /**
     * Executes the log command to get the logging informations.
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
        this.statusProvider = fileSystem.getStatusProvider();
        this.logCmd = fileSystem.getCommand(args[0]);
        boolean status;
        try {
            status = runLog(new Hashtable(vars));
        } catch (InterruptedException iexc) {
            return false;
        }
        if (false == status) return status;
        //ArrayList revisionItems = createRevisionItems();
        final NumDotRevisionList list = new NumDotRevisionList();
        final NumDotRevisionItem rootItem = new NumDotRevisionItem(null);
        rootItem.addProperty(org.openide.util.NbBundle.getBundle(CvsRevisionListGetter.class).getString("CTL_Description"), description);
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
        //System.out.println("LIST GETTER DONE.");
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
    
    /*
    private synchronized void updateRevision(String path, RevisionNode node, RevisionList list) {
        if (statusProvider != null) {
            String revision = statusProvider.getFileRevision(path);
            if (revision == null) return ;
            //System.out.println("updateRevision(): currentRevision = "+currentRevision+", new revision = "+revision);
            if (currentRevision == null || !currentRevision.equals(revision)) {
                for (Iterator it = list.iterator(); it.hasNext(); ) {
                    RevisionItem item = (RevisionItem) it.next();
                    item.setCurrent(revision.equals(item.getRevision()));
                }
                this.currentRevision = revision;
                node.refreshIcons();
            }
        }
    }
    
    private void adjustItems(RevisionList list) {
        ArrayList work = new ArrayList(revisionItems);
        work.removeAll(lastRevisionItems);
        list.addAll(work); // add all new revisions
        work = new ArrayList(lastRevisionItems);
        work.removeAll(revisionItems);
        list.removeAll(work); // remove all old revisions (some VCS may perhaps allow removing revisions)
    }
    
    private void adjustItem(RevisionList list, String revision) {
        //if (revision == null || 
        RevisionItem[] sortedItems = (RevisionItem[]) revisionItems.toArray(new RevisionItem[0]);
        Arrays.sort(sortedItems);
        for (int i = 0; i < sortedItems.length; i++) {
        //for(Iterator it = revisionItems.iterator(); it.hasNext(); ) {
            RevisionItem item = sortedItems[i];//(RevisionItem) it.next();
            String revision2 = item.getRevision();
            // revision may be null and then I don't know which revision has changed.
            if (revision == null || revision.equals(revision2)) {
                //System.out.println("reload revision: "+revision2);
                list.removeRevision(revision2);
                list.add(item);
            }
        }
        if (revision == null) lastRevisionItems = revisionItems;
    }
     */
    
    /**
     * Add symbolic names to revisions.
     */
    private void addTags() {
        /*
        for(Iterator it = revisionItems.iterator(); it.hasNext(); ) {
            RevisionItem item = (RevisionItem) it.next();
            String revision = item.getRevision();
            if (item.isBranch()) {
                int lastDot = revision.lastIndexOf('.');
                revision = revision.substring(0, lastDot + 1) + "0" + revision.substring(lastDot);
            }
            if (symbolicNames.containsValue(revision)) {
                for(Enumeration enum = symbolicNames.keys(); enum.hasMoreElements(); ) {
                    String tag = (String) enum.nextElement();
                    String symRev = (String) symbolicNames.get(tag);
                    if (symRev.equals(revision)) {
                        item.addTagName(tag);
                    }
                }
            }
        }
         */
        ArrayList additionalRevisionItems = new ArrayList();
        for(Enumeration enum = symbolicNames.keys(); enum.hasMoreElements(); ) {
            String tag = (String) enum.nextElement();
            String symRev = (String) symbolicNames.get(tag);
            boolean added = false;
            for(Iterator it = revisionItems.iterator(); it.hasNext(); ) {
                RevisionItem item = (RevisionItem) it.next();
                String revision = item.getRevision();
                if (item.isBranch()) {
                    int lastDot = revision.lastIndexOf('.');
                    String revision0 = revision.substring(0, lastDot + 1) + "0" + revision.substring(lastDot);
                    // Look for symbolic branch name associated with the tag
                    if (symRev.equals(revision0)) {
                        item.addTagName(tag);
                        added = true;
                        break;
                    }
                }
                if (symRev.equals(revision)) {
                    item.addTagName(tag);
                    added = true;
                    break;
                }
            }
            if (!added) {
                // the revision item was not found => let's suppose that it is an empty branch tag
                int lastDot = symRev.lastIndexOf('.');
                int oneButLastDot = symRev.lastIndexOf('.', lastDot - 1);
                if (oneButLastDot < 0) continue;
                String branch = symRev.substring(0, oneButLastDot) + symRev.substring(lastDot);
                //System.out.println("symRev = "+symRev+", lastDot = "+lastDot+", oneButLastDot = "+oneButLastDot+", branch = "+branch);
                RevisionItem newItem = new NumDotRevisionItem(branch);
                newItem.addTagName(tag);
                additionalRevisionItems.add(newItem);
            }
        }
        revisionItems.addAll(additionalRevisionItems);
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
    
    public void outputData(String[] elements) {
        //logBuffer.append(elements[0]);
        if (elements == null || elements.length == 0 || elements[0] == null) return;
        if (!matchingRevision && elements[0].indexOf(revisionStr) == 0) {
            int endRev = elements[0].indexOf('\t', revisionStr.length() + 1);
            if (endRev < 0) endRev = elements[0].length();
            String revision = elements[0].substring(revisionStr.length(), endRev).trim();
            RevisionItem item = new NumDotRevisionItem(revision);
            //System.out.println("outputData(): revision = '"+revision+"', current = '"+currentRevision+"'");
            //item.setCurrent(revision.equals(currentRevision));
            revisionItems.add(item);
            int lockedIndex = elements[0].indexOf(lockedStr);
            if (lockedIndex > 0) {
                String locker = elements[0].substring(lockedIndex + lockedStr.length(), elements[0].length() - 1).trim();
                item.setLocker(locker);
            }
            matchingRevision = true;
        } else if (elements[0].indexOf(dateStr) == 0) {
            if (revisionItems.size() > 0) {
                RevisionItem item = getLastRevisionItem();
                int index1 = dateStr.length();
                int index2 = elements[0].indexOf(ATTR_DELIM, index1);
                if (index2 < index1) return;
                String date = elements[0].substring(index1, index2);
                item.setDate(date);
                index1 = elements[0].indexOf(authorStr, index2);
                if (index1 < 0) return;
                index1 += authorStr.length();
                index2 = elements[0].indexOf(ATTR_DELIM, index1);
                int len = elements[0].length();
                if (index2 < index1) index2 = len;
                item.setAuthor(elements[0].substring(index1, index2));
                while(index2 < len) {
                    index1 = index2 + 1;
                    while(index1 < len && Character.isWhitespace(elements[0].charAt(index1))) index1++;
                    if (index1 >= len) return;
                    index2 = elements[0].indexOf(ATTR_NAME_END, index1);
                    if (index2 < 0) return;
                    String name = elements[0].substring(index1, index2);
                    index1 = index2 + 1;
                    while(index1 < len && Character.isWhitespace(elements[0].charAt(index1))) index1++;
                    if (index1 >= len) return;
                    index2 = elements[0].indexOf(ATTR_DELIM, index1);
                    if (index2 < 0) index2 = len;
                    String value = elements[0].substring(index1, index2);
                    if (stateStr.equals(name)) name = org.openide.util.NbBundle.getMessage(CvsRevisionListGetter.class, "CTL_state");
                    else if (linesStr.equals(name)) name = org.openide.util.NbBundle.getMessage(CvsRevisionListGetter.class, "CTL_lines");
                    item.addProperty(name, value);
                }
            }
        } else if (elements[0].indexOf(branchesStr) == 0) {
            int bBegin = branchesStr.length()+1;
            int bEnd = elements[0].indexOf(';', bBegin);
            if (bEnd < 0) bEnd = elements[0].length();
            while (bBegin < bEnd) {
                String branch = elements[0].substring(bBegin, bEnd).trim();
                RevisionItem item = new NumDotRevisionItem(branch);
                revisionItems.add(item);
                bBegin = bEnd+1;
                bEnd = elements[0].indexOf(';', bBegin);
                if (bEnd < 0) bEnd = elements[0].length();
            }
        } else if (elements[0].indexOf(nextRevisionStr) != 0 && elements[0].indexOf(nextFileStr) != 0 &&
                    revisionItems.size() > 0) {
            RevisionItem item = getLastRevisionItem();
            String msg = item.getMessage();
            if (msg == null) msg = ""; // NOI18N
            else msg += "\n";
            msg += elements[0];
            item.setMessage(msg);
        }
        if (matchingSymbolicNames) {
            int keyIndex = 0;
            while (elements[0].charAt(keyIndex) == ' ' || elements[0].charAt(keyIndex) == '\t') keyIndex++;
            if (keyIndex == 0) matchingSymbolicNames = false;
            else {
                int valueIndex = keyIndex;
                while (elements[0].charAt(valueIndex) != ':') valueIndex++;
                symbolicNames.put(elements[0].substring(keyIndex, valueIndex).trim(),
                                  elements[0].substring(valueIndex + 1, elements[0].length()).trim());
                /*
                symbolicNames.put(elements[0].substring(valueIndex + 1, elements[0].length()).trim(),
                                  elements[0].substring(keyIndex, valueIndex).trim());
                 */
                //D.deb("Putting to symbolic names: ("+elements[0].substring(valueIndex + 1, elements[0].length()).trim()+ // NOI18N
                //      ", "+elements[0].substring(keyIndex, valueIndex).trim()+")"); // NOI18N
            }
        }
        if (elements[0].indexOf(symbNamesStr) >= 0) matchingSymbolicNames = true;
        if (matchingDescription) {
            if (elements[0].indexOf(nextRevisionStr) == 0) matchingDescription = false;
            else description += elements[0];
        }
        if (matchingRevision && elements[0].indexOf(nextRevisionStr) == 0) {
            matchingRevision = false;
        }
        if (elements[0].indexOf(descriptionStr) == 0) matchingDescription = true;
    }    

}
