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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.log;

import java.awt.Dialog;
import java.io.File;
import java.lang.Runnable;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

import org.openide.windows.*;
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

/**
 * The cvs log command wrapper.
 *
 * @author  Richard Gregor
 */
public class CvsLogVisualizer extends OutputVisualizer {

    private static final String LOGGING_DIR = "server: Logging ";           //NOI18N
    private static final String RCS_FILE = "RCS file: ";                    //NOI18N
    private static final String WORK_FILE = "Working file: ";               //NOI18N
    private static final String REV_HEAD = "head: ";                        //NOI18N
    private static final String BRANCH = "branch: ";                        //NOI18N
    private static final String LOCKS = "locks: ";                          //NOI18N
    private static final String ACCESS_LIST = "access list: ";              //NOI18N
    private static final String SYM_NAME = "symbolic names:";               //NOI18N
    private static final String KEYWORD_SUBST = "keyword substitution: ";   //NOI18N
    private static final String TOTAL_REV = "total revisions: ";            //NOI18N
    private static final String SEL_REV = ";\tselected revisions: ";        //NOI18N
    private static final String DESCRIPTION = "description:";               //NOI18N
    private static final String REVISION = "revision ";                     //NOI18N
    private static final String DATE = "date: ";                            //NOI18N
    private static final String BRANCHES = "branches: ";                    //NOI18N
    private static final String AUTHOR = "  author: ";                      //NOI18N
    private static final String STATE = "  state: ";                        //NOI18N
    private static final String LINES = "  lines: ";                        //NOI18N
    private static final String SPLITTER = "----------------------------";  //NOI18N
    private static final String FINAL_SPLIT = "=============";              //NOI18N
    private static final String ERROR = "server: nothing known about ";     //NOI18N
    private static final String NO_FILE = "no file";                        //NOI18N
    
    private boolean addingSymNames;
    private boolean isTag;
    private boolean addingDescription;
    private boolean addingLogMessage;
    private LogInformation logInfo;
    private ArrayList resultList;
    private ArrayList messageList;
    private LogInformation.Revision revision;
    private StringBuffer tempBuffer = null;
    private String fileDirectory;        
    private Iterator ite;   
    private String filePath;   
    private HashMap file_infoMap;
    
    /** 
     * Creates new CvsLog wrapper
     */
    public CvsLogVisualizer() {
        addingSymNames = false;
        addingDescription = false;
        addingLogMessage = false;
        isTag = false;
        logInfo = null;
        revision = null;
        messageList = new ArrayList(500);
        resultList = new ArrayList();
    }      

    public JComponent getOutputPanel(){
        JTabbedPane tabPane = new JTabbedPane(JTabbedPane.BOTTOM);
        Iterator  it = files.iterator();
        while(it.hasNext()){
            String path = (String)it.next();
            File file = getFile(path);
            if(file.isDirectory()){
                LogTreeInfoPanel treePanel = new LogTreeInfoPanel(file);
                if(files.size() == 1){
                    treePanel.setDataToDisplay(resultList);
                    return treePanel;
                }else{
                    treePanel.setDataToDisplay(findResultList(file)); 
                    tabPane.addTab(file.getName(), treePanel);
                }
            }else{
                LogInfoPanel logPanel = new LogInfoPanel(isTag);
                if(files.size() == 1){
                    logPanel.setData(logInfo);
                    return logPanel;
                }else{
                    logPanel.setData(findLogInfo(file)); 
                    tabPane.addTab(file.getName(), logPanel);
                }
            }
        }
        return tabPane;
    }
    
    /**
     * Returns correct file for given path relative to root
     */
    private File getFile(String relativePath) {          
        StringBuffer path = new StringBuffer();
        path.append(rootDir.getAbsolutePath());
        path.append(File.separator);
        path.append(relativePath);
        return new File(path.toString());
    }
    
    private LogInformation findLogInfo(File file){
        LogInformation logInfo = null;
        debug("findLogInfo: "+file.getAbsolutePath());
        Iterator it = resultList.iterator();
        while(it.hasNext()){
            LogInformation info = (LogInformation)it.next();
            File logFile = info.getFile();
            debug("logFile:"+logFile.getAbsolutePath());
            if(logFile.equals(file)){
                logInfo = info;
                break;
            }            
        }
        debug("info : "+logInfo);
        return logInfo;
    }
    
    /**
     * Returns ArrayList of LogInforamtion for given file
     *
     *@param file The file which the FileInformation objects are lookig for
     */
    private ArrayList findResultList(File file){
        debug("findResultList:"+file.getAbsolutePath());
        ArrayList result = new ArrayList();
        LogInformation logInfo = null;
        Iterator it = resultList.iterator();
        while(it.hasNext()){
            logInfo = (LogInformation)it.next();
            File logFile = logInfo.getFile();
            debug("result logFile:"+logFile.getAbsolutePath());  
            if(logFile.getAbsolutePath().startsWith(file.getAbsolutePath())){
                debug("result ok");
                result.add(logInfo);
            }
        }
        return result;
    }    
  
    /**
     * This method is called, with the output line.
     * @param line The output line.
     */
    public void stdOutputLine(String line) {        
        debug("output:"+line);
        if (line.startsWith(FINAL_SPLIT)) {
            if (addingDescription) {
                addingDescription = false;
                logInfo.setDescription(tempBuffer.toString());
            }
            if (addingLogMessage) {
                addingLogMessage = false;
                revision.setMessage(findUniqueString(tempBuffer.toString(), messageList));
            }
            if (revision != null) {
                logInfo.addRevision(revision);
                revision = null;
            }
            
            if (logInfo != null) {                                
                resultList.add(logInfo);
               // logInfo = null;
                tempBuffer = null;
            }
            return;
        }
        if (addingLogMessage) {
            // first check for the branches tag
            if (line.startsWith(BRANCHES)) {
                processBranches(line.substring(BRANCHES.length()));
            }
            else {
                processLogMessage(line);
                return;
            }
        }
        if (addingSymNames) {            
            processSymbolicNames(line);
        }
        if (addingDescription) {
            processDescription(line);
        }
        // revision stuff first -> will be  the most common to parse
        if (line.startsWith(REVISION)) {
            processRevisionStart(line);
        }
        if (line.startsWith(DATE)) {
            processRevisionDate(line);
        }

        if (line.startsWith(KEYWORD_SUBST)) {
            logInfo.setKeywordSubstitution(line.substring(KEYWORD_SUBST.length()).trim().intern());
            addingSymNames = false;
            return;
        }

        if (line.startsWith(DESCRIPTION)) {
            tempBuffer = new StringBuffer(line.substring(DESCRIPTION.length()));
            addingDescription = true;
        }

        if (line.indexOf(LOGGING_DIR) >= 0) {
            fileDirectory = line.substring(line.indexOf(LOGGING_DIR) + LOGGING_DIR.length()).trim();
            debug("fileDirectory: "+fileDirectory);
            return;
        }
        if (line.startsWith(RCS_FILE)) {
            processRcsFile(line.substring(RCS_FILE.length()));
            return;
        }
        if (line.startsWith(WORK_FILE)) {
            processWorkingFile(line.substring(WORK_FILE.length()));
            return;
        }
        if (line.startsWith(REV_HEAD)) {
            logInfo.setHeadRevision(line.substring(REV_HEAD.length()).trim().intern());
            return;
        }
        if (line.startsWith(BRANCH)) {
            logInfo.setBranch(line.substring(BRANCH.length()).trim().intern());
        }
        if (line.startsWith(LOCKS)) {
            logInfo.setLocks(line.substring(LOCKS.length()).trim().intern());
        }
        if (line.startsWith(ACCESS_LIST)) {
            logInfo.setAccessList(line.substring(ACCESS_LIST.length()).trim().intern());
        }
        if (line.startsWith(SYM_NAME)) {
            addingSymNames = true;
        }
        if (line.startsWith(TOTAL_REV)) {
            int ind = line.indexOf(';');
            if (ind < 0) {
                // no selected revisions here..
                logInfo.setTotalRevisions(line.substring(TOTAL_REV.length()).trim().intern());
                logInfo.setSelectedRevisions("0"); //NOI18N
            }
            else {
                String total = line.substring(0, ind);
                String select = line.substring(ind, line.length());
                logInfo.setTotalRevisions(total.substring(TOTAL_REV.length()).trim().intern());
                logInfo.setSelectedRevisions(select.substring(SEL_REV.length()).trim().intern());
            }
        }
    }
    
    /**
     * for a list of string will return the string that equals the name parameter.
     * To be used everywhere you need to have only one string occupying teh memory space,
     * eg. in Builders to have the revision number strings not repeatedly in memory.
     */
    private String findUniqueString(String name, List list) {
        if (name == null) {
            return null;
        }
        int index = list.indexOf(name);
        if (index >= 0) {
            return (String)list.get(index);
        }
        else {
            String newName = new String(name);
            list.add(newName);
            return newName;
        }
    }    
    
    private void processRcsFile(String line) {
        if (logInfo != null) {
       //
        }
        logInfo = new LogInformation();
        logInfo.setRepositoryFilename(line.trim());
    }

    private void processWorkingFile(String line) {
        String fileName = line.trim();
        if (fileName.startsWith(NO_FILE)) {
            fileName = fileName.substring(8);
        }
        logInfo.setFile(getMatchingFile(line));
    }

    private void processBranches(String line) {
        int ind = line.lastIndexOf(';');
        if (ind > 0) {
            line = line.substring(0, ind);
        }
        revision.setBranches(line.trim());
    }

    private void processLogMessage(String line) {
        if (line.startsWith(SPLITTER)) {
            addingLogMessage = false;
            revision.setMessage(findUniqueString(tempBuffer.toString(), messageList));
            return;
        }
        tempBuffer.append(line + "\n"); //NOI18N
    }

    private void processSymbolicNames(String line) {
        if (!line.startsWith(KEYWORD_SUBST)) {
            line = line.trim();
            int index = line.indexOf(':');
            if (index > 0) {
                isTag = true;
                String symName = line.substring(0, index).trim();
                String revName = line.substring(index + 1, line.length()).trim();
                logInfo.addSymbolicName(symName.intern(), revName.intern());
            }
        }
    }

    private void processDescription(String line) {
        if (line.startsWith(SPLITTER)) {
            addingDescription = false;
            logInfo.setDescription(tempBuffer.toString());
            return;
        }
        tempBuffer.append(line);
    }

    private void processRevisionStart(String line) {
        if (revision != null) {
            logInfo.addRevision(revision);
        }
        revision = logInfo.createNewRevision(
                    line.substring(REVISION.length()).intern());
    }

    private void processRevisionDate(String line) {
        StringTokenizer token = new StringTokenizer(line, ";", false); //NOI18N
        if (token.hasMoreTokens()) {
            revision.setDateString(new String(token.nextToken().substring(DATE.length())));
        }
        if (token.hasMoreTokens()) {
            revision.setAuthor(token.nextToken().substring(AUTHOR.length()).intern());
        }
        if (token.hasMoreTokens()) {
            revision.setState(token.nextToken().substring(STATE.length()).intern());
        }
        if (token.hasMoreTokens()) {
            revision.setLines(token.nextToken().substring(LINES.length()).intern());
        }
        addingLogMessage = true;
        tempBuffer = new StringBuffer();
    }

    /**
     *Finds appropriate file in files and returns its absolute path
     */
    private File getMatchingFile(String fileName){
        debug("matching file: "+fileName);
        StringBuffer filePath = new StringBuffer();
        filePath.append(rootDir.getAbsolutePath());
        filePath.append(File.separator);
        String compPath = fileName.replace('\\', '/');          
        Iterator it = files.iterator();       
        while(it.hasNext()){
            String rawPath = (String)it.next();
            String path = rawPath.replace('\\', '/');
            debug("path:"+path);            
            if(getFile(rawPath).isDirectory()){
                //check if path ends with the same name as fileName starts
                int rawIndex = path.lastIndexOf('/');
                String rawEnd = path.substring(rawIndex+1);
                debug("rawend:"+rawEnd);
                int compIndex = compPath.indexOf('/');
                String compStart = compPath.substring(0,compIndex);
                if(rawEnd.equals(compStart)){
                    debug("equals");
                    filePath.append(path);
                    filePath.append(compPath.substring(compIndex));
                    break;
                }
            }else{
                if(path.endsWith(compPath)){
                    filePath.append(path);
                    break;
                }
            }
        }
        debug("matching file result:"+filePath.toString());
        return new File(filePath.toString());
    }
     
    

    private static boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsLogVisualizer: "+msg);
    }    
 
    
}
