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
import org.netbeans.api.vcs.commands.CommandTask;

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
 * The cvs add visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsAddVisualizer extends OutputVisualizer {
    
    private static final String ADDED = " added to the repository"; //NOI18N
    private static final String ALREADY_ENTERED = " has already been entered"; //NOI18N
    private static final String SCHEDULING = ": scheduling file `"; //NOI18N
    private static final String DIRECTORY = "Directory "; //NOI18N
    private static final String READDING = ": re-adding file "; //NOI18N
    private static final String RESURRECTED = ", resurrected"; //NOI18N
    private static final String RESUR_VERSION = ", version "; //NOI18N
    
    private String filePath;
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
    private Object outputAccessLock = new Object();
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    /** Creates new CvsAddVisualizer */
    public CvsAddVisualizer() {
        super();
    }
    
    public Map getOutputPanels() {
        debug("getOutputPanel");
        output = new HashMap();
        contentPane = new AddInfoPanel(this);
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
    
    private boolean isOpened = false;
    
    public void open(){
        if (isOpened) return ;
        isOpened = true;
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
    
    private File createFile(String fileName) {        
        Iterator it = files.iterator();
        while(it.hasNext()){
            File file = new  File((String)it.next());
            if(file.getName().equals(fileName))
                return file;
        }
        //directory name
        String name = fileName.replace('\\', '/');        
        int maxLevel = name.length();
        File bestMatch = null;
        String[] paths = new String[files.size()];
        it = files.iterator();
        int i = 0;
        while(it.hasNext()){
            paths[i++] = ((String)it.next()).replace('\\', '/');            
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
                    bestMatch = new File(paths[index]);
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
        if(!finishVisualizer){            
            return;     //don't exit parent visualizer is in use
        }
        debug("exit: "+exit+"task:"+ getVcsTask().getName());      
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
        stdOutputLine(line); //redirect err output to std output
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
            System.err.println("CvsAddVisualizer: "+msg);
    }
    
    
    
}
