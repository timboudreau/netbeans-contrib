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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 * The cvs commit visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsCommitVisualizer extends OutputVisualizer {
    
    public static final String UNKNOWN = "commit: nothing known about `";               //NOI18N
    public static final String EXAM_DIR = "server: Examining";                          //NOI18N
    public static final String CHECKING_IN = "Checking in ";                            //NOI18N
    public static final String REMOVING = "Removing ";                                  //NOI18N
    public static final String NEW_REVISION = "new revision:";                          //NOI18N
    public static final String INITIAL_REVISION = "initial revision:";                  //NOI18N
    public static final String DONE = "done";                                           //NOI18N
    public static final String RCS_FILE = "RCS file: ";                                 //NOI18N
    public static final String ADD = "commit: use `cvs add' to create an entry for ";   //NOI18N       
           
    private CommitInformation commitInformation;
    private boolean isAdding;
    private String fileDirectory;
    
    /**
     * The local path the command run in.
     */
    private String localPath;
    private CommitInfoPanel contentPane = null;
    private HashMap output;
    private int exit = Integer.MIN_VALUE; // unset exit status
    private List outputInfosToShow; // cached information when the command is providing
    // output sooner then the GUI is created.
    private Object outputAccessLock = new Object();
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    /**
     * Creates new CvsCommitVisualizer 
     */
    public CvsCommitVisualizer() {
        super();  
    }
    
    public Map getOutputPanels() {
        debug("getOutputPanel");
        output = new HashMap();
        contentPane = new CommitInfoPanel(this);
        contentPane.setVcsTask(getVcsTask());
        contentPane.setOutputCollector(getOutputCollector());
        contentPane.showStartCommand();
        //System.out.println("getOutputPanel("+this.hashCode()+"), exit = "+exit);
        if (exit != Integer.MIN_VALUE) {
            // The command already finished!
            setExitStatus(exit);
        }
        output.put("",contentPane);//TODO - what's right name?
        synchronized (outputAccessLock) {
            if (errOutput != null) {
                errOutput.setTextArea(contentPane.getErrOutputArea());
            }
            if (stdDataOutput != null) {
                stdDataOutput.setTextArea(contentPane.getDataStdOutputArea());
            }
            if (errDataOutput != null) {
                errDataOutput.setTextArea(contentPane.getDataErrOutputArea());
            }
        }
        return output;
    }
    
    
    public void open(){
        CommandOutputTopComponent out = CommandOutputTopComponent.getInstance();
        getOutputPanels();
        String title;
        if (files.size() == 1) {
            String filePath = (String) files.iterator().next();
            java.io.File file = new java.io.File(filePath);
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsCommitVisualizer.title_one"), // NOI18N
            new Object[] { commandName,file.getName()});
        }
        else if (files.size() > 1) {
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsCommitVisualizer.title_many"), // NOI18N
            new Object[] {commandName, Integer.toString(files.size())});
        }
        else title = commandName;
        out.addVisualizer(title,contentPane, true);
        out.open();
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {        
        if (line.indexOf(UNKNOWN) >= 0) {
            processUnknownFile(line.substring(line.indexOf(UNKNOWN) + UNKNOWN.length()).trim());
        }
        else if (line.indexOf(ADD) > 0) {
            processToAddFile(line.substring(line.indexOf(ADD) + ADD.length()).trim());
        }
        else if (line.startsWith(CHECKING_IN)) {
            // - 1 means to cut the ';' character
            processFile(line.substring(CHECKING_IN.length(), line.length() - 1));
            if (isAdding) {
                commitInformation.setType(commitInformation.ADDED);
                isAdding = false;
            }
            else {
                commitInformation.setType(commitInformation.CHANGED);
            }
        }
        else if (line.startsWith(REMOVING)) {
            processFile(line.substring(REMOVING.length(), line.length() - 1));
            // - 1 means to cut the ';' character
            commitInformation.setType(commitInformation.REMOVED);
        }
        else if (line.indexOf(EXAM_DIR) >= 0) {
            fileDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
        }
        else if (line.startsWith(RCS_FILE)) {
            isAdding = true;
        }
        else if (line.startsWith(DONE)) {
            outputDone();
        }
        else if (line.startsWith(INITIAL_REVISION)) {
            processRevision(line.substring(INITIAL_REVISION.length()));
        }
        else if (line.startsWith(NEW_REVISION)) {
            processRevision(line.substring(NEW_REVISION.length()));
        }
    }
    
    private File createFile(String fileName) {
        return new File(localPath, fileName);
    }
    
    private void processUnknownFile(String line) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInformation.UNKNOWN);
        int index = line.indexOf('\'');
        String fileName = line.substring(0, index - 1).trim();
        commitInformation.setFile(createFile(fileName));
        outputDone();
    }
    
    private void processToAddFile(String line) {
        commitInformation = new CommitInformation();
        commitInformation.setType(commitInformation.TO_ADD);
        String fileName = line.trim();
        if (fileName.endsWith(";")) { //NOI18N
            fileName = fileName.substring(0, fileName.length() - 2);
        }
        commitInformation.setFile(createFile(fileName));
        outputDone();
    }
    
    private void processFile(String filename) {
        if (commitInformation == null) {
            commitInformation = new CommitInformation();
        }
        
        if (filename.startsWith("no file")) { //NOI18N
            filename = filename.substring(8);
        }
        commitInformation.setFile(createFile(filename));
    }
    
    private void processRevision(String revision) {
        int index = revision.indexOf(';');
        if (index >= 0) {
            revision = revision.substring(0, index);
        }
        commitInformation.setRevision(revision.trim());
    }
    
    public void parseEnhancedMessage(String key, Object value) {
    }
    
    public void outputDone() {        
        //System.out.println("outputDone("+this.hashCode()+") ENTERED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
        if (contentPane != null) {
            if (outputInfosToShow != null) {
                for (Iterator it = outputInfosToShow.iterator(); it.hasNext(); ) {
                    CommitInformation info = (CommitInformation) it.next();
                    contentPane.showFileInfoGenerated(info);
                }
                outputInfosToShow = null;
            }
        }
        
        if (commitInformation != null) {
            if (contentPane != null) {
                contentPane.showFileInfoGenerated(commitInformation);
            } else {
                if (outputInfosToShow == null) {
                    outputInfosToShow = new LinkedList();
                }
                outputInfosToShow.add(commitInformation);
            }
            commitInformation = null;
        }
        
        //System.out.println("outputDone("+this.hashCode()+") EXITED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
    }
    
    /** @return false to open immediatelly.
     */
    public boolean openAfterCommandFinish() {
        return false;
    }
    
    public boolean doesDisplayError() {
        return true;
    }
    
    public void setExitStatus(int exit) {
        debug("exit: "+exit);
        //System.out.println("setExitStatus("+this.hashCode()+") ("+exit+"), cp = "+(contentPane != null));
        this.exit = exit;
        if (contentPane != null) { // Check whether we have the GUI created
            if (outputInfosToShow != null) {
                outputDone(); // show cached infos
            }
            contentPane.showFinishedCommand(exit);
        }
    }    
 
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        synchronized (outputAccessLock) {
            if (errOutput == null) {
                errOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    errOutput.setTextArea(contentPane.getErrOutputArea());
                }
            }
            errOutput.addText(line+'\n');
        }
    }
    
    /**
     * Receive the data output.
     */
    public void stdOutputData(final String[] data) {
        synchronized (outputAccessLock) {
            if (stdDataOutput == null) {
                stdDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    stdDataOutput.setTextArea(contentPane.getDataStdOutputArea());
                }
            }
            stdDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
        }
    }
    
    /**
     * Receive the error data output.
     */
    public void errOutputData(final String[] data) {
        synchronized (outputAccessLock) {
            if (errDataOutput == null) {
                errDataOutput = CommandOutputTextProcessor.getDefault().createOutput();
                if (contentPane != null) {
                    errDataOutput.setTextArea(contentPane.getDataErrOutputArea());
                }
            }
            errDataOutput.addText(VcsUtilities.arrayToString(data)+'\n');
        }
    }
    
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsCommitVisualizer: "+msg);
    }
    
    
    
}
