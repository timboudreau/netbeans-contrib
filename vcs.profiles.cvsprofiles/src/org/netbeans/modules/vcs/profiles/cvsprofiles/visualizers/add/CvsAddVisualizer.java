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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.add;

import java.awt.Dialog;
import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;
import org.netbeans.modules.vcscore.commands.*;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 * The cvs add visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsAddVisualizer extends OutputVisualizer {
    
    private static final String UNKNOWN = "server: nothing known about"; //NOI18N
    private static final String ADDED = " added to the repository"; //NOI18N
    private static final String WARNING = "server: warning: "; //NOI18N
    private static final String ALREADY_ENTERED = " has already been entered"; //NOI18N
    private static final String SCHEDULING = "server: scheduling file `"; //NOI18N
    private static final String USE_COMMIT = "server: use 'cvs commit' "; //NOI18N
    private static final String DIRECTORY = "Directory "; //NOI18N
    private static final String READDING = "server: re-adding file "; //NOI18N
    private static final String RESURRECTED = ", resurrected"; //NOI18N
    private static final String RESUR_VERSION = ", version "; //NOI18N
    
    /** Maximum number of characters to keep in the buffer */
    private static final int MAX_BUFFER_SIZE = 3000*80;
    /** When both the buffer and the text area are full, replace only this part
     * of the buffer */
    private static final int FAST_APPEND_SIZE = 100*80;
    /** The maximum number of characters to keep in the text area */
    private static final int MAX_AREA_SIZE = MAX_BUFFER_SIZE - FAST_APPEND_SIZE;
    
    private String filePath;
    private static Hashtable outputDisplayStuff;
    private static RequestProcessor outputDisplayRequestProcessor;   
    private AddInformation fileInfoContainer;
    
    /**
     * The local path the command run in.
     */
    private String localPath;
    private AddInfoPanel contentPane = null;
    private HashMap output;
    private int exit = Integer.MIN_VALUE; // unset exit status
    private List outputInfosToShow; // cached information when the command is providing
    // output sooner then the GUI is created.
    private List errorOutputToShow; // cached error output when the command is providing
    // output sooner then the GUI is created.
    
    /** Creates new CvsAddVisualizer */
    public CvsAddVisualizer() {
        super();
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
        contentPane = new AddInfoPanel(this);
        contentPane.setVcsTask(getVcsTask());
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
        String title;
        if (files.size() == 1) {
            String filePath = (String) files.iterator().next();
            java.io.File file = new java.io.File(filePath);
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsAddVisualizer.title_one"), // NOI18N
            new Object[] { commandName,file.getName()});
        }
        else if (files.size() > 1) {
            title = java.text.MessageFormat.format(
            NbBundle.getBundle(this.getClass()).getString("CvsAddVisualizer.title_many"), // NOI18N
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
        if (line.endsWith(ADDED)) {
            String directory =
            line.substring(DIRECTORY.length(), line.indexOf(ADDED));
            addDirectory(directory);
        }
        else if (line.indexOf(SCHEDULING) >= 0) {
            String filename =
            line.substring(line.indexOf(SCHEDULING) + SCHEDULING.length(), line.indexOf('\'')).trim();
            addFile(filename);
        }
        else if (line.indexOf(READDING) >= 0) {
            String filename =
            line.substring(line.indexOf(READDING) + READDING.length(), line.indexOf('(')).trim();
            addFile(filename);
        }
        else if (line.endsWith(RESURRECTED)) {
            String filename =
            line.substring(0, line.length() - RESURRECTED.length());
            resurrectFile(filename);
        }
        // ignore the rest..
    }
    
   /*     private File createFile(String fileName) {
            File locFile = addCommand.getFileEndingWith(fileName);
            if (locFile == null) {
                // in case the exact match was not  achieved using the getFileEndingWith method
                // let's try to find the best match possible.
                // iterate from the back of the filename string and try to match the endings
                // of getFiles(). the best match is picked then.
                // Works ok for files and directories in add, should not probably be used
                // elsewhere where it's possible to have recursive commands and where resulting files
                // are not listed in getFiles()
                String name = fileName.replace('\\', '/');
                File[] files = addCommand.getFiles();
                int maxLevel = name.length();
                File bestMatch = null;
                String[] paths = new String[files.length];
                for (int index = 0; index < files.length; index++) {
                    paths[index] = files[index].getAbsolutePath().replace('\\', '/');
                }
                int start = name.lastIndexOf('/');
                String part = null;
                if (start < 0) {
                    part = name;
                } else {
                    part = name.substring(start + 1);
                }
                while (start >= 0 || part != null) {
                    boolean wasMatch = false;
                    for (int index = 0; index < paths.length; index++) {
                        if (paths[index].endsWith(part)) {
                            bestMatch = files[index];
                            wasMatch = true;
                        }
                    }
                    start = name.lastIndexOf('/', start - 1);
                    if (start < 0 || !wasMatch) {
                        break;
                    }
                    part = name.substring(start + 1);
                }
                return bestMatch;
            }
            return locFile;
        }*/
    
    private File createFile(String fileName) {
        return new File(fileName);
       /* File locFile = addCommand.getFileEndingWith(fileName);
        File locFile = null;
        if (locFile == null) {
            // in case the exact match was not  achieved using the getFileEndingWith method
            // let's try to find the best match possible.
            // iterate from the back of the filename string and try to match the endings
            // of getFiles(). the best match is picked then.
            // Works ok for files and directories in add, should not probably be used
            // elsewhere where it's possible to have recursive commands and where resulting files
            // are not listed in getFiles()
            String name = fileName.replace('\\', '/');
            //File[] files = addCommand.getFiles();
            File[] file = new File[files.size()];
            files.toArray(file);
            int maxLevel = name.length();
            File bestMatch = null;
            String[] paths = new String[file.length];
            for (int index = 0; index < file.length; index++) {
                paths[index] = file[index].getAbsolutePath().replace('\\', '/');
            }
            int start = name.lastIndexOf('/');
            String part = null;
            if (start < 0) {
                part = name;
            } else {
                part = name.substring(start + 1);
            }
            while (start >= 0 || part != null) {
                boolean wasMatch = false;
                for (int index = 0; index < paths.length; index++) {
                    if (paths[index].endsWith(part)) {
                        bestMatch = file[index];
                        wasMatch = true;
                    }
                }
                start = name.lastIndexOf('/', start - 1);
                if (start < 0 || !wasMatch) {
                    break;
                }
                part = name.substring(start + 1);
            }
            return bestMatch;
        }
            return locFile;*/
    }
    
    private void addDirectory(String name) {
        fileInfoContainer = new AddInformation();
        fileInfoContainer.setType(AddInformation.FILE_ADDED);
        String dirName = name.replace('\\', '/');
        fileInfoContainer.setFile(createFile(dirName));
        outputDone();
    }
    
    private void addFile(String name) {        
        fileInfoContainer = new AddInformation();
        fileInfoContainer.setFile(createFile(name));
        fileInfoContainer.setType(AddInformation.FILE_ADDED);
        outputDone();
    }
    
    private void resurrectFile(String line) {
        int versionIndex = line.lastIndexOf(RESUR_VERSION);
        String version = line.substring(versionIndex + RESUR_VERSION.length()).trim();
        String cutLine = line.substring(0, versionIndex).trim();
        int fileIndex = cutLine.lastIndexOf(' ');
        String name = cutLine.substring(fileIndex).trim();
        
        if (DEBUG) {
            System.out.println("line1=" + line);  //NOI18N
            System.out.println("versionIndex=" + versionIndex);  //NOI18N
            System.out.println("version=" + version);  //NOI18N
            System.out.println("fileindex=" + fileIndex); //NOI18N
            System.out.println("filename=" + name); //NOI18N
        }
        
        fileInfoContainer = new AddInformation();
        fileInfoContainer.setType(AddInformation.FILE_RESURRECTED);
        fileInfoContainer.setFile(createFile(name));
        outputDone();
    }
    
    
    public void outputDone() {        
        //System.out.println("outputDone("+this.hashCode()+") ENTERED, fic = "+fileInfoContainer+", cp = "+(contentPane != null)+", oi = "+outputInfosToShow);
        if (contentPane != null) {            
            if (outputInfosToShow != null) {                
                for (Iterator it = outputInfosToShow.iterator(); it.hasNext(); ) {
                    AddInformation info = (AddInformation) it.next();
                    contentPane.showFileInfoGenerated(info);
                }
                outputInfosToShow = null;
            }
            if (errorOutputToShow != null) {                
                javax.swing.JTextArea area = contentPane.getErrOutputArea();
                for (Iterator it = errorOutputToShow.iterator(); it.hasNext(); ) {
                    String line = (String) it.next();
                    appendLineToArea(area, line);
                }
                errorOutputToShow = null;
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
    
    public void setExitStatus(int exit) {
        debug("exit: "+exit);
        //System.out.println("setExitStatus("+this.hashCode()+") ("+exit+"), cp = "+(contentPane != null));
        this.exit = exit;
        if (contentPane != null) { // Check whether we have the GUI created
            if (outputInfosToShow != null || errorOutputToShow != null) {
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
        stdOutputLine(line); //redirect err output to std output
        // to prevent deadlocks, append output in the AWT thread
        if (contentPane != null) {
            if (errorOutputToShow != null) {
                javax.swing.JTextArea area = contentPane.getErrOutputArea();
                for (Iterator it = errorOutputToShow.iterator(); it.hasNext(); ) {
                    String l = (String) it.next();
                    appendLineToArea(area, l);
                }
                errorOutputToShow = null;
            }
            appendLineToArea(contentPane.getErrOutputArea(), line);
        } else {
            if (errorOutputToShow == null) {
                errorOutputToShow = new LinkedList();
            }
            errorOutputToShow.add(line);
        }
        
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
            System.err.println("CvsAddVisualizer: "+msg);
    }
    
    
    
}
