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
     
    /** Maximum number of characters to keep in the buffer */
    private static final int MAX_BUFFER_SIZE = 3000*80;
    /** When both the buffer and the text area are full, replace only this part
     * of the buffer */
    private static final int FAST_APPEND_SIZE = 100*80;
    /** The maximum number of characters to keep in the text area */
    private static final int MAX_AREA_SIZE = MAX_BUFFER_SIZE - FAST_APPEND_SIZE;

    private String filePath;
    private StringBuffer buff;
    private static Hashtable outputDisplayStuff;
    private static RequestProcessor outputDisplayRequestProcessor;
     

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
    
    /** Creates new CvsUpdateVisualizer */
    public CvsUpdateVisualizer() {
        super();
        buff = new StringBuffer();
        synchronized (CommandOutputVisualizer.class) {
            if (outputDisplayRequestProcessor == null) {
                outputDisplayRequestProcessor = new RequestProcessor("Output Display Request Processor");
                outputDisplayRequestProcessor.post(new OutputDisplayer());
            }
        }
    }

    public Map getOutputPanels() {
        debug("getOutputPanel");
        output = new HashMap();
        contentPane = new UpdateInfoPanel(this); 
        contentPane.setVcsTask(getVcsTask());
        contentPane.setLog(buff);
        contentPane.showStartCommand();
        //System.out.println("getOutputPanel("+this.hashCode()+"), exit = "+exit);
        if (exit != Integer.MIN_VALUE) {
            // The command already finished!
            setExitStatus(exit);
        }
        output.put("",contentPane);//TODO - what's right name?        
        return output;
    }
    
    
    public void open(){
        CommandOutputTopComponent out = CommandOutputTopComponent.getInstance();
        getOutputPanels();
        out.addVisualizer(NbBundle.getMessage(this.getClass(),"CvsUpdateVisualizer.update"),contentPane, true);
        out.open();        
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {        
        debug("Line:"+line);
        appendLog(line);
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

    private void appendLog(String line){
        buff.append(line+"\n");
        if (buff.length() > 36000) { // just approx. constants, tuneup possible..
            buff.delete(0, 8000);
            int index = 0;
            while (index < buff.length() && buff.charAt(index) != '\n') {
                index = index + 1;
            }
            buff.delete(0, index);
            buff.insert(0, NbBundle.getBundle(CvsUpdateVisualizer.class).getString("UpdateLogPanel.logCut") + "\n");
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
        if (contentPane != null && outputInfosToShow != null) {
            for (Iterator it = outputInfosToShow.iterator(); it.hasNext(); ) {
                UpdateInformation info = (UpdateInformation) it.next();
                contentPane.showFileInfoGenerated(info);
            }
            outputInfosToShow = null;
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

    public void setExitStatus(int exit) {
        debug("exit: "+exit);
        //System.out.println("setExitStatus("+this.hashCode()+") ("+exit+"), cp = "+(contentPane != null));
        this.exit = exit;
        if (contentPane != null) { // Check whether we have the GUI created
            if (outputInfosToShow != null) {
                outputDone(); // show cached infos
            }
            if(exit == 0)
                contentPane.showFinishedCommand();
            else
                contentPane.showExecutionFailed();
        }
    }
    
    private void appendTextToArea(javax.swing.JTextArea area, String text) {
        synchronized (outputDisplayStuff) {
            StringBuffer buffer = (StringBuffer) outputDisplayStuff.get(area);
            if (buffer == null) {
                buffer = new StringBuffer(text);
                synchronized (outputDisplayStuff) {
                    outputDisplayStuff.put(area, buffer);
                    if (outputDisplayStuff.size() == 1) {
                        outputDisplayStuff.notify(); // it was empty before!
                    }
                }
            } else {
                buffer.append(text);
                if (buffer.length() > MAX_BUFFER_SIZE) {
                    buffer.delete(0, buffer.length() - MAX_AREA_SIZE  - 1);
                }
            }
        }
    }
    
    private void appendLineToArea(javax.swing.JTextArea area, String line) {
        appendTextToArea(area, line + '\n');
    }
    
    /**
     * Receive a line of error output.
     */
    public void errOutputLine(final String line) {
        // to prevent deadlocks, append output in the AWT thread
        appendLineToArea(contentPane.getErrOutputArea(), line);
    }
    
    private static class OutputDisplayer extends Object implements Runnable {
        
        private java.util.Random random;
        
        public OutputDisplayer() {
            outputDisplayStuff = new Hashtable();
            random = new java.util.Random();
        }
        
        public void run() {
            if (SwingUtilities.isEventDispatchThread()) {
                int index = random.nextInt(outputDisplayStuff.size());
                java.util.Enumeration keysEnum = outputDisplayStuff.keys();
                javax.swing.JTextArea area;
                do {
                    area = (javax.swing.JTextArea) keysEnum.nextElement();
                } while (--index >= 0);
                String append;
                String replace;
                int start;
                int end = area.getDocument().getLength();
                synchronized (outputDisplayStuff) {
                    StringBuffer buffer = (StringBuffer) outputDisplayStuff.get(area);
                    if (buffer.length() >= MAX_AREA_SIZE) {
                        append = null;
                        replace = buffer.substring(buffer.length() - FAST_APPEND_SIZE, buffer.length()).toString();
                        buffer.delete(0, replace.length());
                        start = end - replace.length();
                        if (start < 0) start = 0;
                    } else {
                        buffer = (StringBuffer) outputDisplayStuff.remove(area);
                        append = buffer.toString();
                        start = 0;
                        end += append.length();
                        if (end < MAX_AREA_SIZE) end = 0;
                        else end = end - MAX_AREA_SIZE + FAST_APPEND_SIZE;
                        replace = null;
                    }
                }
                if (append != null) {
                    area.append(append);
                }
                if (end > 0) {
                    area.replaceRange(replace, start, end);
                }
            } else {
                do {
                    synchronized (outputDisplayStuff) {
                        if (outputDisplayStuff.size() == 0) {
                            try {
                                outputDisplayStuff.wait();
                            } catch (InterruptedException iexc) {
                                break;
                            }
                        }
                    }
                    do {
                        try {
                            SwingUtilities.invokeAndWait(this);
                            // Let the AWT to catch it's breath
                            Thread.currentThread().yield();
                            Thread.currentThread().sleep(250);
                        } catch (InterruptedException iexc) {
                            break;
                        } catch (java.lang.reflect.InvocationTargetException itexc) {
                            org.openide.ErrorManager.getDefault().notify(itexc);
                            break;
                        }
                    } while (outputDisplayStuff.size() > 0);
                } while (true);
            }
        }
    }
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsUpdateVisualizer: "+msg);
    }
    
    
    
}
