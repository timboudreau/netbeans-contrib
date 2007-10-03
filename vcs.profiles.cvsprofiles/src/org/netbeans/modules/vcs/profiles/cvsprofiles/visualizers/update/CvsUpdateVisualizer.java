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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.Iterator;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.OutputVisualizer;

import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.openide.util.NbBundle;

/**
 * The cvs update visualizer.
 * Reads the output from standard output only, which is merged with the error
 * output in the correct order.
 * When the text that normally goes to the error output is not found,
 * it will try to find out the "best" path.
 *
 * @author  Richard Gregor
 */
public class CvsUpdateVisualizer extends OutputVisualizer {

    public static final String UNKNOWN = ": nothing known about"; //NOI18N
    public static final String EXAM_DIR = ": Updating"; //NOI18N
    public static final String TO_ADD = ": use `cvs add' to create an entry for"; //NOI18N
    public static final String STATES = "U P A R M C ? "; //NOI18N
    public static final String WARNING = ": warning: "; //NOI18N
    public static final String SERVER = "server: "; //NOI18N
    public static final String UPDATE = "update: "; //NOI18N
    public static final String PERTINENT = "is not (any longer) pertinent"; //NOI18N
    public static final String MERGING = "Merging differences between "; //NOI18N
    private static final String MERGING_INTO = "into"; // NOI18N
    public static final String CONFLICTS = "rcsmerge: warning: conflicts during merge"; //NOI18N
    public static final String NOT_IN_REPOSITORY = "is no longer in the repository"; //NOI18N;
     
    private String filePath;    

    private UpdateInformation fileInfoContainer;

    private UpdateInfoPanel contentPane = null;
    private HashMap output;
    private int exit = Integer.MIN_VALUE; // unset exit status
    private List outputInfosToShow; // cached information when the command is providing
                                    // output sooner then the GUI is created.
    private Object outputAccessLock = new Object();
    private CommandOutputTextProcessor.TextOutput errOutput;
    private CommandOutputTextProcessor.TextOutput stdDataOutput;
    private CommandOutputTextProcessor.TextOutput errDataOutput;
    
    private boolean haveErrorOutput = true; // Whether we have the error output merged in. Be optimistic.
    
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

    /** @return componnet that was added in getOutputPanels under <tt>""</tt> name. */
    protected final UpdateInfoPanel getContentPane() {
        return contentPane;
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
            filePath = line.substring(line.indexOf(EXAM_DIR) + EXAM_DIR.length()).trim();
            if (".".equals(filePath)) filePath = ""; // NOI18N
            return;
        }
        else if (line.startsWith(MERGING)) {
            int fileIndex = line.indexOf(MERGING_INTO, MERGING.length());
            //System.out.println("   file index = "+fileIndex);
            String fileName = null;
            if (fileIndex > 0) {
                fileName = line.substring(fileIndex + MERGING_INTO.length()).trim();
                //System.out.println("  file = "+createFile(fileName));
                if (fileInfoContainer != null && (fileInfoContainer.getFile() != null && !createFile(fileName).equals(fileInfoContainer.getFile()))) {
                    outputDone();
                }
            }
            if (fileInfoContainer == null) {
                fileInfoContainer = new UpdateInformation();
            }
            if (!"A".equals(fileInfoContainer.getType())) {
                // All but added files are merged.
                fileInfoContainer.setType(UpdateInformation.MERGED_FILE);
            }
            if (fileName != null) {
                fileInfoContainer.setFile(createFile(fileName));
            }
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
            int index = line.indexOf(SERVER);
            if (index < 0) {
                index = line.indexOf(UPDATE);
                if (index < 0) {
                    index = line.indexOf(':');
                    index++;
                } else {
                    index += UPDATE.length();
                }
            } else {
                index += SERVER.length();
            }
            String filename = line.substring(index,
                                             line.indexOf(NOT_IN_REPOSITORY)).trim();
            if (filename.startsWith("`") && filename.endsWith("'")) { // (cvs 1.12) // NOI18N
                filename = filename.substring(1, filename.length() - 1);
            }
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
        boolean haveToGuessPath;
        String path;
        int sep = fileName.lastIndexOf('/');
        if (sep > 0) {
            path = fileName;
            haveToGuessPath = false;
        } else {
            path = (filePath != null && filePath.length() > 0) ?
                    filePath + File.separator + fileName :
                    fileName;
            haveToGuessPath = true;
        }
        File file;
        if (commonParentStr.length() > 0) {
            file = new File(commonParentStr, path);
        } else {
            file = new File(path);
        }
        //System.out.println("createFile("+commonParentStr+", "+path+") = "+file.getPath());
        if (haveToGuessPath && filePath == null && !(new File(rootDir, file.getPath()).exists())) {
            haveErrorOutput = false; // We most probably do not have the error output merged in.
            file = createFileBestMatch(fileName);
        }
        return file;
    }

    private File createFileBestMatch(String fileName) {
        Iterator it = files.iterator();
        while(it.hasNext()){
            File file = new  File((String)it.next());
            if(file.getName().equals(fileName))
                return file;
        }
        //directory name
        String name = fileName.replace('\\', '/');        
        //System.out.println("  createFile("+fileName+"), name = "+name);
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
        int end = name.length() - 1;
        while (start >= 0 || part != null) {
            boolean wasMatch = false;
            for (int index = 0; index < paths.length; index++) {
                //System.out.println("     '"+paths[index]+"' ends with '"+part+"' = "+paths[index].endsWith(part));
                if (paths[index].endsWith(part)) {
                    String path = paths[index] + name.substring(end);
                    if (path.startsWith("/")) path = path.substring(1);
                    if (path.startsWith("./")) path = path.substring(2);
                    bestMatch = new File(path);
                    wasMatch = true;
                }
            }
            if (wasMatch) {
                break;
            }
            end = start;
            if (start > 0) {
                start = name.substring(0, end).lastIndexOf('/');
                if (start < 0) start = -1; // slash is "virtually" at -1 position.
            } else {
                break;
            }
            //System.out.println("   start = "+start+", end = "+end);
            part = name.substring(start + 1, end);
        }
        //System.out.println("  return '"+bestMatch+"'");
        if (bestMatch == null) {
            if (fileInfoContainer != null) {
                File lastFile = fileInfoContainer.getFile();
                if (lastFile != null) {
                    if (name.indexOf('/') > 0) {
                        bestMatch = new File(name);
                    } else {
                        bestMatch = new File(lastFile.getParentFile(), name);
                    }
                }
            }
            if (bestMatch == null) {
                if (paths.length > 0 && !".".equals(paths[0])) {
                    bestMatch = new File(paths[0] + File.separator + fileName);
                } else {
                    bestMatch = new File(fileName);
                }
            }
        }
        return bestMatch;        
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
            if (!file.equals(fileInfoContainer.getFile())) {
                outputDone();
                //return;
            }
        }

        //outputDone();
        ensureExistingFileInfoContainer();
        

        if (fileInfoContainer.getType() != UpdateInformation.MERGED_FILE || !line.substring(0, 1).trim().equals("M")) {
            fileInfoContainer.setType(line.substring(0, 1));
        }
        fileInfoContainer.setFile(file);
        //outputDone();
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
            if (!haveErrorOutput && UpdateInformation.MERGED_FILE.equals(fileInfoContainer.getType())) {
                // we need to check Entries for conflicts
                if (hasConflicts(new File(rootDir, fileInfoContainer.getFile().getPath()))) {
                    fileInfoContainer.setType("C"); // NOI18N
                }
            }
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
    
    private static boolean hasConflicts(File file) {
        //System.out.println("  hasConflicts("+file+")");
        File folder = file.getParentFile();
        File entries = new File(folder, "CVS"+File.separator+"Entries");
        String name = file.getName();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(entries));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("/")) {
                    int end = line.indexOf('/', 1);
                    if (end > 0) {
                        String entryName = line.substring(1, end);
                        if (name.equals(entryName)) {
                            int begin = line.indexOf('/', end + 1);
                            end = line.indexOf('/', begin + 1);
                            //System.out.println("    have line = '"+line+"', searching for '+' in "+line.substring(begin, end));
                            if (begin > 0 && end > begin) {
                                // If there is '+', there are conflicts
                                return line.substring(begin, end).indexOf('+') > 0;
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfExc) {
            // ignore
        } catch (IOException ioExc) {
            // ignore
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException exc) {}
        }
        //System.out.println("  did not find the file in Entries!");
        return false;
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
        if (fileInfoContainer != null) {
            outputDone();
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
    
    private static final boolean DEBUG = false;
    private static void debug(String msg){
        if(DEBUG)
            System.err.println("CvsUpdateVisualizer: "+msg);
    }
    
}
