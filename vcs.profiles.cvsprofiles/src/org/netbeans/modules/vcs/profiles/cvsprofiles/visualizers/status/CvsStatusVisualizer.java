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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.status;

import java.awt.Dialog;
import java.io.File;
import java.lang.Runnable;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;

import org.netbeans.modules.vcs.profiles.cvsprofiles.list.StatusFilePathsBuilder;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

/**
 * The cvs staus command visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsStatusVisualizer extends OutputVisualizer implements TextErrorListener{

    private static final String UNKNOWN = "server: nothing known about";        //NOI18N
    private static final String EXAM_DIR = "server: Examining";                 //NOI18N
    private static final String NOT_IN_REPOSITORY = "No revision control file"; //NOI18N
    private static final String FILE = "File: ";                                //NOI18N
    private static final String STATUS = "Status:";                             //NOI18N
    private static final String NO_FILE_FILENAME = "no file";                   //NOI18N
    private static final String WORK_REV = "   Working revision:";              //NOI18N
    private static final String REP_REV = "   Repository revision:";            //NOI18N
    private static final String TAG = "   Sticky Tag:";                         //NOI18N
    private static final String DATE = "   Sticky Date:";                       //NOI18N
    private static final String OPTIONS = "   Sticky Options:";                 //NOI18N
    private static final String EXISTING_TAGS = "   Existing Tags:";            //NOI18N
    private static final String EMPTY_BEFORE_TAGS = "   ";                      //NOI18N
    private static final String NO_TAGS = "No Tags Exist";                      //NOI18N
    private static final String UNKNOWN_FILE = "? ";                            //NOI18N
    private static final String STATUS_UNKNOWN = "Unknown";                     //NOI18N
    
    private boolean addingSymNames;
    private boolean addingDescription;
    private boolean addingLogMessage;
    private StatusInformation statusInformation;
    private ArrayList resultList;
    private StringBuffer tempBuffer = null;
    private String fileDirectory;
    private boolean beginning;
    private boolean readingTags;
    private String relativeDirectory;
    private Map infoMap;
    private HashMap output;
    /** The map of file directories and associated status file paths builders. */
    private Map statusFilePathsBuildersByFiles;
    /*
     * Used for Status_Get_Tags case. File is already known we need 
     * only to find existing tags. File is set by StatusInfoPanel.
     */
    private File fileFromInfo;
    /** 
     * Creates new CvsStatusVisualizer
     */
    public CvsStatusVisualizer() {
        super();
        this.beginning = true;        
        statusInformation = null;                
        resultList = new ArrayList();
    }       
    
    /**
     * After instatiation this method is called with the map of all possible
     * file statuses. The map contains raw file status strings as keys
     * and appropriate {@link org.netbeans.api.vcs.FileStatusInfo} objects
     * as values.
     * @return The file status map.
     */
    public void setPossibleFileStatusInfoMap(Map infoMap) {
        this.infoMap = infoMap;
    }
    
    public Map getInfoMap(){
        return infoMap;
    }
    
    public void setVcsTask(VcsDescribedTask task) {
        super.setVcsTask(task);
        statusFilePathsBuildersByFiles = new HashMap();
        Iterator  it = files.iterator();
        File commonPath = rootDir;
        String commonParent = (String) task.getVariables().get("COMMON_PARENT");
        if (commonParent != null && commonParent.length() > 0) {
            commonPath = new File(commonPath, commonParent);
        }
        while(it.hasNext()) {
            String path = (String)it.next();
            File file = getFile(path);
            statusFilePathsBuildersByFiles.put(file,
                new StatusFilePathsBuilder(commonPath,
                                           (String) task.getVariables().get("CVS_REPOSITORY")));
        }
    }
    
    public Map getOutputPanels(){
        debug("getOtputPanel");
        //JTabbedPane tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
        output = new HashMap();
        Iterator  it = files.iterator();
        debug("while");
        while(it.hasNext()){
            String path = (String)it.next();
            File file = getFile(path);
            StatusFilePathsBuilder statusFilePathBuilder = (StatusFilePathsBuilder) statusFilePathsBuildersByFiles.get(file);
            if (statusFilePathBuilder != null) {
                fillFilePaths(file, statusFilePathBuilder);
            }
            if (file.isDirectory()) {
                StatusTreeInfoPanel treePanel = new StatusTreeInfoPanel(file, getCommandsProvider());
                if(files.size() == 1){
                    treePanel.setDataToDisplay(resultList);
                    output.put(file.getName(),treePanel);
                    return output;
                    //return treePanel;
                }else{
                    treePanel.setDataToDisplay(findResultList(file)); 
                    //tabPane.addTab(file.getName(), treePanel);
                    output.put(file.getName(),treePanel);
                }
            } else {
                StatusInfoPanel statPanel = new StatusInfoPanel(getCommandsProvider());
                if(files.size() == 1 && statusInformation != null && file != null){
                    statusInformation.setFile(file);
                    statPanel.setData(statusInformation);
                    output.put(file.getName(),statPanel);
                    return output;
                    //return statPanel;
                }else{
                    statPanel.setData(findStatusInfo(file)); 
                    //tabPane.addTab(file.getName(), statPanel);
                    output.put(file.getName(),statPanel);
                }
            }
        }
        //return tabPane;
        return output;
    }
    
    private StatusInformation findStatusInfo(File file){
        StatusInformation statusInfo = null;
        debug("findStatusInfo: "+file.getAbsolutePath());
        Iterator it = resultList.iterator();
        while(it.hasNext()){
            StatusInformation info = (StatusInformation)it.next();
            File statFile = info.getFile();
            debug("statFile:"+statFile.getAbsolutePath());
            if(statFile.equals(file)){
                statusInfo = info;
                break;
            }            
        }
        debug("info : "+statusInfo);
        return statusInfo;
    }
    
    /**
     * Returns ArrayList of StatusInforamtion for given file
     *
     *@param file The file which the FileInformation objects are lookig for
     */
    private ArrayList findResultList(File file){
        debug("findResultList:"+file.getAbsolutePath());
        ArrayList result = new ArrayList();
        StatusInformation statusInfo = null;
        Iterator it = resultList.iterator();
        while(it.hasNext()){
            statusInfo = (StatusInformation)it.next();
            File statFile = statusInfo.getFile();
            debug("result statFile:"+statFile.getAbsolutePath());  
            if(statFile.getAbsolutePath().startsWith(file.getAbsolutePath())){
                debug("result ok");
                result.add(statusInfo);
            }
        }
        return result;
    }
    
    private void fillFilePaths(File file, StatusFilePathsBuilder statusFilePathBuilder) {
        Iterator it = resultList.iterator();
        while (it.hasNext()) {
            StatusInformation statusInfo = (StatusInformation) it.next();
            if (statusInfo.getFile() != null) {
                continue;
            }
            boolean found = statusFilePathBuilder.fillStatusInfoFilePath(statusInfo);
            //if (!found) {
                //statusInfo.setFile(null);
            //}
        }        
    }
    
    
    public boolean doesDisplayError() {
        return false;
    }
    
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {  
        debug("output:"+line);
        if (readingTags) {
            if (line.indexOf(NO_TAGS) > 0) {
                if (statusInformation == null) {
                    statusInformation = new StatusInformation();
                    resultList.add(statusInformation);
                }
                statusInformation.setAllExistingTags(Collections.EMPTY_LIST);
                readingTags = false;
                return;
            }

            int bracket = line.indexOf("\t(");
            if (bracket > 0) {
                // it's another tag..
                String tag = line.substring(0, bracket).trim();
                String rev = line.substring(bracket + 2, line.length() - 1);

                if (statusInformation == null) {
                    statusInformation = new StatusInformation();
                    resultList.add(statusInformation);
                }
                statusInformation.addExistingTag(tag, rev);
            }
            else {
                if (line.trim().length() == 0) {
                    // an empty line, we've finished reading tags
                    readingTags = false;
                }
                return;
            }
        }

        if (line.startsWith(UNKNOWN_FILE) && beginning) {            
            statusInformation = new StatusInformation();
            resultList.add(statusInformation);
            String fileName = line.substring(UNKNOWN_FILE.length());
            if (fileName.startsWith("./")) {
                fileName = fileName.substring(2);
            }
            statusInformation.setFileName(fileName);
            String status;
            if(infoMap != null)
                status = ((FileStatusInfo)infoMap.get(STATUS_UNKNOWN)).getDisplayName();
            else
                status = STATUS_UNKNOWN;                      
            statusInformation.setStatus(status);
        }

        if (line.startsWith(UNKNOWN)) {            
            beginning = false;
        }
        else if (line.indexOf(EXAM_DIR) >= 0) {
            relativeDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
            beginning = false;
        }
        else if (line.startsWith(FILE)) {            
            statusInformation = new StatusInformation();
            resultList.add(statusInformation);
            processFileAndStatusLine(line.substring(FILE.length()));
            beginning = false;
        }
        else if (line.startsWith(WORK_REV)) {
            processWorkRev(line.substring(WORK_REV.length()));
        }
        else if (line.startsWith(REP_REV)) {
            processRepRev(line.substring(REP_REV.length()));
        }
        else if (line.startsWith(TAG)) {
            processTag(line.substring(TAG.length()));
        }
        else if (line.startsWith(DATE)) {
            processDate(line.substring(DATE.length()));
        }
        else if (line.startsWith(OPTIONS)) {
            processOptions(line.substring(OPTIONS.length()));          
        }
        else if (line.startsWith(EXISTING_TAGS)) {
            readingTags = true;
        }
    }
    
    /**
     * Receive a line of error output.
     * The processed folders are going here.
     *
    public void errOutputLine(final String line) {
       // Not implemented, StatusFilePathsBuilder can work without examining paths 
    }
     */
    
    
    private void processFileAndStatusLine(String line) {
        int statusIndex = line.lastIndexOf(STATUS);
        String fileName = line.substring(0, statusIndex).trim();
        if (fileName.startsWith(NO_FILE_FILENAME)) {
            fileName = fileName.substring(8);
        }
        statusInformation.setFileName(fileName);
        File file = getFileFromInfo();
        //if(file == null)
        //    file = getFile(fileName);
        if (file != null) {
            statusInformation.setFile(file);
        }

        String status = new String(line.substring(statusIndex + 8).trim());        
        if(infoMap != null) {
            FileStatusInfo fsInfo = (FileStatusInfo) infoMap.get(status);
            if (fsInfo != null) {
                statusInformation.setStatusLC(fsInfo.getDisplayName());
            }
        }
        statusInformation.setStatus(status);
    }

    private boolean assertNotNull() {
        if (statusInformation == null) {
            System.err.println("Bug: statusInformation must not be null!");
            return false;
        }

        return true;
    }

    private void processWorkRev(String line) {
        if (!assertNotNull()) {
            return;
        }
        line = line.trim();
        int space = line.indexOf('\t');
        if (space > 0) {
            line = line.substring(0, space);
        }
        statusInformation.setWorkingRevision(line.intern());
    }

    private void processRepRev(String line) {
        if (!assertNotNull()) {
            return;
        }
        line = line.trim();
        if (line.startsWith(NOT_IN_REPOSITORY)) {
            statusInformation.setRepositoryRevision(line.trim().intern());
            return;
        }
        int firstSpace = line.indexOf('\t');
        if (firstSpace > 0) {
            statusInformation.setRepositoryRevision(
                    line.substring(0, firstSpace).trim().intern());
            statusInformation.setRepositoryFileName(
                    new String(line.substring(firstSpace).trim()));
        }
        else {
            statusInformation.setRepositoryRevision(""); //NOI18N
            statusInformation.setRepositoryFileName(""); //NOI18N
        }
        
        File file = getFileFromInfo();
        //if(file == null)  /* Not reliable. StatusFilePathsBuilder is used instead. */
        //    file = getFileFromRev(statusInformation.getRepositoryFileName());
        if (file != null) {
            statusInformation.setFile(file);
        }
    }

    private void processTag(String line) {
        if (!assertNotNull()) {
            return;
        }
        statusInformation.setStickyTag(line.trim().intern());
    }

    private void processDate(String line) {
        if (!assertNotNull()) {
            return;
        }
        statusInformation.setStickyDate(line.trim().intern());
    }

    private void processOptions(String line) {
        if (!assertNotNull()) {
            return;
        }
        statusInformation.setStickyOptions(line.trim().intern());
    }

    public void parseEnhancedMessage(String key, Object value) {
    }  
    
    /**
     * Returns correct file for given path relative to root
     */
    private File getFile(String relativePath) {
        debug("getFile:"+relativePath);
        if ("".equals(relativePath) || ".".equals(relativePath)) {
            return new File(rootDir.getAbsolutePath());
        }
        StringBuffer path = new StringBuffer();        
        path.append(rootDir.getAbsolutePath());
        debug("root dir: "+rootDir.getAbsolutePath());
        path.append(File.separator);
        path.append(relativePath);
        return new File(path.toString());
    }
    
    /*
     * CvsStatusVisualizer is also TextErrorListener because of GetTags action of StatusInfoPanel
     */
    public void outputLine(String line) {        
        stdOutputLine(line);
    }
    
    public StatusInformation getStatusInfo(){
        return statusInformation;
    }
    
    public File getFileFromInfo(){
        return fileFromInfo;
    }
    
    public void setFileFromInfo(File file){
        fileFromInfo = file;
    }
    
    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsStatusVisualizer: "+msg);
    }
    

    
}
