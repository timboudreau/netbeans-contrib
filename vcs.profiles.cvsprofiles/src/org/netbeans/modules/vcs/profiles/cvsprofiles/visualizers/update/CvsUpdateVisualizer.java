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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.table.RevisionComparator;
import org.netbeans.modules.vcscore.util.table.DateComparator;
import org.netbeans.modules.vcscore.util.table.IntegerComparator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;

/**
 * The cvs update visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsUpdateVisualizer extends OutputVisualizer {

    /**
     * Sent by MergedResponse when 2 files were merged.
     * The value is a String instance that tells the full path to the file.
     */
    public static final String MERGED_PATH = "Merged_Response_File_Path"; // NOI18N
    
    public static final String UNKNOWN = "server: nothing known about"; //NOI18N
    public static final String EXAM_DIR = "server: Updating"; //NOI18N
    public static final String TO_ADD = "server: use `cvs add' to create an entry for"; //NOI18N
    public static final String STATES = "U P A R M C ? "; //NOI18N
    public static final String WARNING = "server: warning: "; //NOI18N
    public static final String SERVER = "server: "; //NOI18N
    public static final String PERTINENT = "is not (any longer) pertinent"; //NOI18N
    public static final String MERGING = "Merging differences between "; //NOI18N
    public static final String CONFLICTS = "rcsmerge: warning: conflicts during merge"; //NOI18N
    public static final String NOT_IN_REPOSITORY = "is no longer in the repository"; //NOI18N;
     
    private String filePath;    

    private UpdateInformation fileInfoContainer;

    /**
     * The local path the command run in.
     */
    private String localPath;
    private UpdateInfoPanel contentPane = null;
    private HashMap output;
    private int exit = Integer.MIN_VALUE; // unset exit status
    private List outputInfosToShow; // cached information when the command is providing
                                    // output sooner then the GUI is created.
    private Object outputAccessLock = new Object();
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    /** Creates new CvsUpdateVisualizer */
    public CvsUpdateVisualizer() {
        super();
    }

    public Map getOutputPanels() {
        debug("getOutputPanel");
        output = new HashMap();
        contentPane = new UpdateInfoPanel(this);
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
            NbBundle.getBundle(this.getClass()).getString("CvsUpdateVisualizer.title_one"), // NOI18N
            new Object[] { commandName,file.getName()});
        }
        else if (files.size() > 1) {
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsUpdateVisualizer.title_many"), // NOI18N
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
        debug("Line:"+line);        
        if (line.indexOf(UNKNOWN) >= 0) {
            processUnknownFile(line, line.indexOf(UNKNOWN) + UNKNOWN.length());
        }
        else if (line.indexOf(TO_ADD) >= 0) {
            processUnknownFile(line, line.indexOf(TO_ADD) + TO_ADD.length());
        }
        else if (line.indexOf(EXAM_DIR) >= 0) {
            return;
        }
        else if (line.startsWith(MERGING)) {
            outputDone();
            if (fileInfoContainer == null) {
                fileInfoContainer = new UpdateInformation();
            }
            fileInfoContainer.setType(UpdateInformation.MERGED_FILE);
        }
        else if (line.startsWith(CONFLICTS)) {
            if (fileInfoContainer != null) {
                fileInfoContainer.setType("C"); //NOI18N
            }
        }
        else if (line.indexOf(WARNING) >= 0) {
            if (line.indexOf(PERTINENT) > 0) {
                String filename = line.substring(line.indexOf(WARNING) + WARNING.length(),
                                                 line.indexOf(PERTINENT)).trim();
                processNotPertinent(filename);
            }
            return;
        }
        else if (line.indexOf(NOT_IN_REPOSITORY) > 0) {
            String filename = line.substring(line.indexOf(SERVER) + SERVER.length(),
                                             line.indexOf(NOT_IN_REPOSITORY)).trim();
            processNotPertinent(filename);
            return;
        }
        else {
            // otherwise
            if (line.length() > 2) {
                String firstChar = line.substring(0, 2);
                if (STATES.indexOf(firstChar) >= 0) {
                    processFile(line);
                    return;
                }
            }
        }
    }

       
    private File createFile(String fileName) {
        return new File(localPath, fileName);
    }

    private void ensureExistingFileInfoContainer() {
        if (fileInfoContainer != null) {
            return;
        }
        fileInfoContainer = new UpdateInformation();
    }

    private void processUnknownFile(String line, int index) {
        outputDone();
        fileInfoContainer = new UpdateInformation();
        fileInfoContainer.setType("?"); //NOI18N
        String fileName = (line.substring(index)).trim();
        fileInfoContainer.setFile(createFile(fileName));
    }

    private void processFile(String line) {
        
        String fileName = line.substring(2).trim();

        if (fileName.startsWith("no file")) { //NOI18N
            fileName = fileName.substring(8);
        }

        if (fileName.startsWith("./")) { //NOI18N
            fileName = fileName.substring(2);
        }

        File file = createFile(fileName);
        if (fileInfoContainer != null) {
            // sometimes (when locally modified.. the merged response is followed by mesage M <file> or C <file>..
            // check the file.. if equals.. it's the same one.. don't send again.. the prior type has preference
            if (fileInfoContainer.getFile() == null) {
                // is null in case the global switch -n is used - then no Enhanced message is sent, and no
                // file is assigned the merged file..
                fileInfoContainer.setFile(file);
            }
            if (file.equals(fileInfoContainer.getFile())) {
                outputDone();
                return;
            }
        }

        //outputDone();
        ensureExistingFileInfoContainer();
        

        fileInfoContainer.setType(line.substring(0, 1));
        fileInfoContainer.setFile(file);
        outputDone();
    }

    private void processLog(String line) {
        ensureExistingFileInfoContainer();
    }

    private void processNotPertinent(String fileName) {
        outputDone();
        File fileToDelete = createFile(fileName);

        ensureExistingFileInfoContainer();

        // HACK - will create a non-cvs status in order to be able to have consistent info format
        fileInfoContainer.setType(UpdateInformation.PERTINENT_STATE);
        fileInfoContainer.setFile(fileToDelete);
    }

    public void parseEnhancedMessage(String key, Object value) {
        if (key.equals(MERGED_PATH)) {
            if (fileInfoContainer != null) {
                String path = value.toString();
                File newFile = new File(path);
                fileInfoContainer.setFile(newFile);
            }
        }
    }
   
    public void outputDone() {
        //System.out.println("outputDone("+this.hashCode()+") ENTERED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
        if (contentPane != null) {
            if (outputInfosToShow != null) {
                for (Iterator it = outputInfosToShow.iterator(); it.hasNext(); ) {
                    UpdateInformation info = (UpdateInformation) it.next();
                    contentPane.showFileInfoGenerated(info);
                }
                outputInfosToShow = null;
            }
        }
        
        if (fileInfoContainer != null) {
            if (contentPane != null) {
                contentPane.showFileInfoGenerated(fileInfoContainer);        
            } else {
                if (outputInfosToShow == null) {
                    outputInfosToShow = new LinkedList();
                }
                outputInfosToShow.add(fileInfoContainer);
            }
            fileInfoContainer = null;
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
            System.err.println("CvsUpdateVisualizer: "+msg);
    }
    
    
    
}
