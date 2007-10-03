/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

import org.netbeans.api.vcs.FileStatusInfo;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.local.FileAttributeQuery;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;

import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.Validators;
import org.netbeans.modules.vcs.profiles.cvsprofiles.list.StatusFilePathsBuilder;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

/**
 * The cvs staus command visualizer.
 *
 * @author  Richard Gregor
 */
public class CvsStatusVisualizer extends OutputVisualizer implements TextErrorListener{

    private static final String UNKNOWN = ": nothing known about";              //NOI18N
    private static final String EXAM_DIR = ": Examining";                       //NOI18N
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
    
    /** The status information currently being filled */
    private StatusInformation statusInformation;
    private ArrayList resultList;
    private boolean readingTags;
    /** The current relative directory to <code>commonPath</code> */
    //private String relativeDirectory;
    /** The directory in which the processed files reside. */
    private File fileDirectory;
    /** Possible file status info map */
    private Map infoMap;
    /** The map of output components (StatusTreeInfoPanel) by file paths */
    private HashMap output;
    /** The map of file directories and associated status file paths builders.
     *  Is <code>null</code> when running on JDK 1.5, useful only on JDK 1.4. */
    private Map statusFilePathsBuildersByFiles;
    /*
     * Used for Status_Get_Tags case. File is already known we need 
     * only to find existing tags. File is set by StatusInfoPanel.
     */
    private File fileFromInfo;
    /**
     * The common path of the task.
     */
    private File commonPath;
    /** 
     * Creates new CvsStatusVisualizer
     */
    public CvsStatusVisualizer() {
        super();
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
        commonPath = rootDir;
        String commonParent = (String) task.getVariables().get("COMMON_PARENT");
        if (commonParent != null && commonParent.length() > 0) {
            commonPath = new File(commonPath, commonParent);
        }
        // Use the statusFilePathsBuildersByFiles only when we do not have the
        // error stream merged in correctly and therefore do not know the file paths.
        if (!Validators.canHaveOutputStreamsMergedCorrectly(task.getVariables())) {
            statusFilePathsBuildersByFiles = new HashMap();
            Iterator  it = files.iterator();
            while(it.hasNext()) {
                String path = (String)it.next();
                File file = getFile(path);
                statusFilePathsBuildersByFiles.put(file,
                    new StatusFilePathsBuilder(commonPath,
                                               (String) task.getVariables().get("CVS_REPOSITORY")));
            }
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
            if (statusFilePathsBuildersByFiles != null) {
                StatusFilePathsBuilder statusFilePathBuilder = (StatusFilePathsBuilder) statusFilePathsBuildersByFiles.get(file);
                if (statusFilePathBuilder != null) {
                    fillFilePaths(file, statusFilePathBuilder);
                }
            }
            if (file.isDirectory()) {
                StatusTreeInfoPanel treePanel = new StatusTreeInfoPanel(file, getCommandsProvider());
                if(files.size() == 1){
                    treePanel.setDataToDisplay(resultList);
                    output.put(file.getPath(),treePanel);
                    return output;
                    //return treePanel;
                }else{
                    treePanel.setDataToDisplay(findResultList(file)); 
                    //tabPane.addTab(file.getName(), treePanel);
                    output.put(file.getPath(),treePanel);
                }
            } else {
                StatusInfoPanel statPanel = new StatusInfoPanel(getCommandsProvider());
                if(files.size() == 1 && statusInformation != null && file != null){
                    statusInformation.setFile(file);
                    statPanel.setData(statusInformation);
                    output.put(file.getPath(),statPanel);
                    return output;
                    //return statPanel;
                }else{
                    statPanel.setData(findStatusInfo(file)); 
                    //tabPane.addTab(file.getName(), statPanel);
                    output.put(file.getPath(),statPanel);
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
            assert statusInfo.getFile() != null: "No File for status info: "+statusInfo+", file = "+file+", statusFilePathBuilder = "+statusFilePathBuilder;
            //boolean found = statusFilePathBuilder.fillStatusInfoFilePath(statusInfo);
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

        if (line.startsWith(UNKNOWN_FILE)) {
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
            statusInformation.setFile(new File(commonPath, fileName));
        }

        if (line.indexOf(EXAM_DIR) >= 0) {
            String relativeDirectory = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
            if (".".equals(relativeDirectory)) {
                fileDirectory = commonPath;
            } else {
                fileDirectory = new File(commonPath, relativeDirectory);
            }
        }
        else if (line.startsWith(FILE)) {            
            statusInformation = new StatusInformation();
            resultList.add(statusInformation);
            processFileAndStatusLine(line.substring(FILE.length()));
        }
        else if (line.startsWith(WORK_REV)) {
            processWorkRev(line.substring(WORK_REV.length()));
        }
        else if (line.startsWith(REP_REV)) {
            processRepRev(line.substring(REP_REV.length())); // fills file field
            
            // #39207 update cache
            File f = statusInformation.getFile();
            if (f != null) {
                FileObject fo = FileUtil.toFileObject(f);
                if (fo != null) {
                    FileAttributeQuery faq = FileAttributeQuery.getDefault();
                    FileProperties fprops = (FileProperties) faq.readAttribute(fo, FileProperties.ID);
                    FileProperties updated = new FileProperties(fprops);
                    String updatedStatus = statusInformation.getStatus();
                    updated.setStatus(updatedStatus);
                    if (fprops == null) {
                        String name = fo.isFolder() ? fo.getNameExt() + "/" : fo.getNameExt();  // NOI18N
                        updated.setName(name);
                    }
                    updated.freeze();
                    if (FileProperties.getStatus(fprops).equals(updatedStatus) == false) {
                        faq.writeAttribute(fo, FileProperties.ID, updated);
                    }
                }
            }
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
            assert false : "Bug: statusInformation must not be null!";
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
            statusInformation.setRepositoryFileName(line.trim().intern());
        } else {
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
        }
        File file = getFileFromInfo();
        //if(file == null)  /* Not reliable. StatusFilePathsBuilder is used instead. */
        //    file = getFileFromRev(statusInformation.getRepositoryFileName());
        if (file != null) {
            statusInformation.setFile(file);
        } else {
            if (statusFilePathsBuildersByFiles != null) {
                for (Iterator it = statusFilePathsBuildersByFiles.values().iterator(); it.hasNext(); ) {
                    StatusFilePathsBuilder statusFilePathBuilder = (StatusFilePathsBuilder) it.next();
                    boolean found = statusFilePathBuilder.fillStatusInfoFilePath(statusInformation);
                    if (found) break;
                }
            } else {
                if (fileDirectory != null) {
                    file = new File(fileDirectory, statusInformation.getFileName());
                    statusInformation.setFile(file);
                } else {
                    statusInformation.setFile(getFileFromProcessedFiles(statusInformation.getFileName()));
                }
            }
        }
        assert statusInformation.getFile() != null: "ERROR: null file in "+statusInformation+" in processRepRev("+line+")";
    }
    
    /** Find the file of that name among processed files. */
    private File getFileFromProcessedFiles(String fileName) {
        File file = null;
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String filePath = (String) it.next(); // Path relative to rootDir
            int fileIndex = filePath.lastIndexOf('/');
            if (fileIndex > 0 && filePath.substring(fileIndex + 1).equals(fileName)) {
                file = new File(rootDir, filePath);
            }
        }
        if (file == null) {
            // ? It's probably in the root?
            file = new File(commonPath, fileName);
        }
        return file;
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
