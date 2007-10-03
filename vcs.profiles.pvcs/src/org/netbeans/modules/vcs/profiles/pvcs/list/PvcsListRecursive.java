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

package org.netbeans.modules.vcs.profiles.pvcs.list;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsListRecursiveCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * PVCS recursive refresh command.
 *
 * @author Martin Entlicher
 */
public class PvcsListRecursive extends VcsListRecursiveCommand implements RegexOutputListener {
    
    private VcsDirContainer filesByNameCont;
    private String dir=null;
    //private String pathToSkip = null;
    private String projectPath;
    
    private CommandExecutionContext context;
    private String currentUserName;
    
    private Pattern noEntitiesPattern;
    private volatile boolean noEntitiesMatched = false;

    /** Creates a new instance of PvcsListRecursive */
    public PvcsListRecursive() {
        noEntitiesPattern = Pattern.compile(PvcsListCommand.NO_ENTITIES_REGEX);
    }
    
    public void setExecutionContext(CommandExecutionContext context) {
        this.context = context;
    }

    private void initDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
                /*
                pathToSkip = module;
            } else {
                pathToSkip = "";
                 */
            }
        } else {
            if (module == null || module.length() == 0) {
                //pathToSkip = dir;
                dir = rootDir + File.separator + dir;
            } else {
                //pathToSkip = module + File.separator + dir;
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar) {
            dir = dir.substring(0, dir.length() - 1);
        }
        //while (pathToSkip.endsWith(File.separator)) pathToSkip = pathToSkip.substring(0, pathToSkip.length() - 1);
        //pathToSkip = "/" + pathToSkip.replace(File.separatorChar, '/');
        //System.out.println("pathToSkip = "+pathToSkip);
    }

    /**
     * List files of CVS Repository recursively.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * @param filesByNameCont listing of files with statuses. For each directory there is a <code>Hashtable</code>
     *                        with files.
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean listRecursively(Hashtable vars, String[] args, VcsDirContainer filesByNameCont,
                                   CommandOutputListener stdoutNRListener,
                                   CommandOutputListener stderrNRListener,
                                   CommandDataOutputListener stdoutListener, String dataRegex,
                                   CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 2) {
            stderrNRListener.outputLine("Expecting list and diff commands as arguments!"); //NOI18N
            return false;
        }
        this.currentUserName = (String) vars.get(VcsFileSystem.VAR_LOCKER_USER_NAME);
        if (this.currentUserName != null) {
            this.currentUserName = Variables.expand(vars, this.currentUserName, false);
        }
        if (this.currentUserName == null || currentUserName.length() == 0) {
            this.currentUserName = System.getProperty("user.name");
        }
        CommandSupport listCmd = context.getCommandSupport(args[0]);
        if (listCmd == null) {
            stderrNRListener.outputLine("Unknown Command: "+args[0]); //NOI18N
            return false;
        }
        CommandSupport diffCmd = context.getCommandSupport(args[1]);
        if (diffCmd == null) {
            stderrNRListener.outputLine("Unknown Command: "+args[1]); //NOI18N
            return false;
        }
        projectPath = "${PROJECT}$[? COMMON_PARENT] [/${COMMON_PARENT_\\/}] []$[? DIR] [/${DIR_\\/}] []";
        projectPath = Variables.expand(vars, projectPath, false);
        if (projectPath.startsWith("//")) { // When PROJECT == /
            projectPath = projectPath.substring(1);
        }
        initDir(vars);
        this.filesByNameCont = filesByNameCont;
        boolean status;
        try {
            status = runCommand(vars, listCmd, this);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (noEntitiesMatched) {
            return true; // No files are returned. Return successfully.
        }
        Map cmdVars = new HashMap(vars);
        cmdVars.put("MODULE", ""); // Reset the MODULE, we'll set DIR as appropriate for every folder.
        try {
            findFileStatus(filesByNameCont, diffCmd, cmdVars);
        } catch (InterruptedException iexc) {
            return false;
        }
        //printOutputData(stdoutListener, filesByNameCont, "");
        return status;
    }
    
    private void printOutputData(CommandDataOutputListener stdoutListener, VcsDirContainer filesByNameCont, String inset) {
        stdoutListener.outputData(new String[] { "Path: ", inset + filesByNameCont.getPath() });
        Hashtable filesByName = (Hashtable) filesByNameCont.getElement();
        if (filesByName != null) {
            for (Iterator it = filesByName.values().iterator(); it.hasNext(); ) {
                String[] statuses = (String[]) it.next();
                stdoutListener.outputData(statuses);
            }
        }
        VcsDirContainer[] subCont = filesByNameCont.getSubdirContainers();
        for (int i = 0; i < subCont.length; i++) {
            printOutputData(stdoutListener, subCont[i], inset + "  ");
        }
    }
    
    private boolean runCommand(Map vars, CommandSupport cmdSupp, RegexOutputListener listener) throws InterruptedException {
        VcsDescribedCommand cmd = (VcsDescribedCommand) cmdSupp.createCommand();
        cmd.setAdditionalVariables(vars);
        cmd.addRegexOutputListener(listener);
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iex) {
            task.stop();
            Thread.currentThread().interrupt();
            throw iex;
        }
        return task.getExitStatus() == CommandTask.STATUS_SUCCEEDED;
    }

    private void findFileStatus(VcsDirContainer filesCont, CommandSupport diffCmdSupp, Map vars) throws InterruptedException {
        Hashtable filesByName = (Hashtable) filesCont.getElement();
        if (filesByName != null) {
            String path = filesCont.getPath();
            vars.put("DIR", path);
            String dir = "${ROOTDIR}${PS}"+path;
            dir = Variables.expand(vars, dir, false);
            for (Iterator it = filesByName.keySet().iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                String[] fileStatuses = (String[]) filesByName.get(name);
                String revision = fileStatuses[6];
                if (revision == null || revision.length() == 0) continue;
                //Hashtable cmdVars = new Hashtable(vars);
                vars.put("WORKFILE", new File(dir, name).getAbsolutePath());
                vars.put("ARCHIVE", fileStatuses[4]);
                vars.put("REVISION", revision);
                Command diffCmd = diffCmdSupp.createCommand();
                ((VcsDescribedCommand) diffCmd).setAdditionalVariables(vars);
                CommandTask task = diffCmd.execute();
                try {
                    task.waitFinished(0);
                } catch (InterruptedException iexc) {
                    task.stop();
                    Thread.currentThread().interrupt();
                    throw iexc;
                }
                if (task.getExitStatus() != CommandTask.STATUS_SUCCEEDED) {
                    fileStatuses[1] = PvcsListCommand.MODIFIED_STATUS;
                } else {
                    fileStatuses[1] = PvcsListCommand.CURRENT_STATUS;
                }
            }
        }
        VcsDirContainer[] subContainers = filesCont.getSubdirContainers();
        for (int i = 0; i < subContainers.length; i++) {
            findFileStatus(subContainers[i], diffCmdSupp, vars);
        }
    }

    private boolean file = false;
    private boolean folder = false;
    //private String lastFile = null;
    //private boolean skipNextName = false;
    private boolean newLockInfo = false;
    private String lastLockedRevision = null;
    private String lastNewRevision = null;
    
    private VcsDirContainer currentContainer = null;
    private String currentProject = null;
    private File currentDir = null;
    private String[] fileStatuses = null;
    
    public void outputMatchedGroups(String[] elements) {
        if (elements[0] == null) return ;
        if (noEntitiesPattern.matcher(elements[0]).matches()) {
            noEntitiesMatched = true;
            return ;
        }
        // Set currentContainer to the correct container according to project path
        // And currentProject to the relative project being processed (with respect to the dir being refreshed)
        if (elements[0].startsWith(projectPath)) {
            String project = elements[0].substring(projectPath.length());
            int fileIndex = project.lastIndexOf('/');
            if (fileIndex >= 0) project = project.substring(0, fileIndex);
            else project = ""; // I'm on the root
            while (project.startsWith("/")) project = project.substring(1);
            //System.out.println("   Have project = '"+project+"'");
            if (currentProject == null || !currentProject.equals(project)) {
                String path = filesByNameCont.getPath();
                if (path.length() > 0 && project.length() > 0) {
                    path = path + "/" + project;
                } else {
                    path = path + project; // One of them is an empty string
                }
                currentContainer = filesByNameCont.addSubdirRecursive(path);
                //System.out.println("  Added container with path '"+path+"' = "+currentContainer);
                currentProject = project;
                currentDir = new File(dir, project);
                //System.out.println("  Current Dir = '"+currentDir+"'");
            }
        }
        if (elements[0].startsWith(PvcsListCommand.ENTITY_TYPE)) {
            String entityType = elements[0].substring(PvcsListCommand.ENTITY_TYPE.length());
            file = PvcsListCommand.ENTITY_VERSIONED_FILE.equals(entityType);
            folder = PvcsListCommand.ENTITY_PROJECT.equals(entityType);
        }
        if (elements[0].startsWith(PvcsListCommand.ENTITY_NAME)) {
            String name = elements[0].substring(PvcsListCommand.ENTITY_NAME.length());
            File file = new File(currentDir, name);
            if (folder) name += "/";
            name = name.intern();
            Hashtable filesByName = (Hashtable) currentContainer.getElement();
            if (filesByName == null) {
                filesByName = new Hashtable();
                currentContainer.setElement(filesByName);
            }
            fileStatuses = new String[7];
            /* 0 - file name
             * 1 - status
             * 2 - locker
             * 3 - new revision
             * 4 - archive
             * 5 - revision count
             * 6 - the locked revision (for diff command only, needed just internally)
             */
            filesByName.put(name, fileStatuses);
            fileStatuses[0] = name;
            //System.out.println("  FILE = '"+file.getAbsolutePath()+"'");
            if (file.exists()) {
                fileStatuses[1] = PvcsListCommand.NO_STATUS;
                //lastFile = file.getAbsolutePath();
            } else {
                fileStatuses[1] = PvcsListCommand.MISSING_STATUS;
                //lastFile = null;
            }
        }
        if (elements[0].startsWith(PvcsListCommand.ARCHIVE_PATH)) {
            if (file && fileStatuses != null) {
                String archive = elements[0].substring(PvcsListCommand.ARCHIVE_PATH.length());
                fileStatuses[4] = archive;
            }
        }
        if (elements[0].startsWith(PvcsListCommand.ARCHIVE_REVISION_COUNT) && fileStatuses != null) {
            String revisionCount = elements[0].substring(PvcsListCommand.ARCHIVE_REVISION_COUNT.length());
            fileStatuses[5] = revisionCount;
        }
        if (elements[0].startsWith(PvcsListCommand.ARCHIVE_LOCK_INFO) && fileStatuses != null) {
            String lockInfo = elements[0].substring(PvcsListCommand.ARCHIVE_LOCK_INFO.length());
            if (!lockInfo.endsWith(PvcsListCommand.LOCK_INFO_END)) { // New structured lock info
                //fileStatuses[4] = (String) archivesByNames.get(fileStatuses[0]);
                newLockInfo = true;
                int index;
                if ((index = elements[0].indexOf(PvcsListCommand.LOCK_INFO_LOCKED_REVISION)) > 0) {
                    lastLockedRevision = elements[0].substring(index + PvcsListCommand.LOCK_INFO_LOCKED_REVISION.length()).trim();
                }
            } else { // Old one-line lock info
                int index = lockInfo.indexOf(PvcsListCommand.LOCKS_SEPARATOR);
                do {
                    int index2 = lockInfo.indexOf(PvcsListCommand.LOCKS_SEPARATOR, index + 1);
                    if (index2 < 0) return ;
                    String lockedRevision = lockInfo.substring(0, index).trim().intern();
                    String newRevision = lockInfo.substring(index + PvcsListCommand.LOCKS_SEPARATOR.length(), index2).intern();
                    index = index2;
                    index2 = lockInfo.indexOf(PvcsListCommand.LOCKS_SEPARATOR, index + 1);
                    String locker = lockInfo.substring(index + PvcsListCommand.LOCKS_SEPARATOR.length(), index2).intern();
                    boolean isCurrentUser = org.openide.util.Utilities.isWindows() ?
                        locker.equalsIgnoreCase(currentUserName) :
                        locker.equals(currentUserName);
                    if (fileStatuses[2] == null || fileStatuses[2].length() == 0) {
                        fileStatuses[2] = locker;
                    } else {
                        if (isCurrentUser) {
                            fileStatuses[2] = locker + ',' + fileStatuses[2];
                        } else {
                            fileStatuses[2] = fileStatuses[2] + ',' + locker;
                        }
                    }
                    if (isCurrentUser) {
                        String fileRevision = fileStatuses[6];
                        //System.out.println("File: '"+fileStatuses[0]+"' - revision: '"+lockedRevision+"' -> '"+newRevision+"', fileRevision: '"+fileRevision+"'");
                        if (fileRevision == null) {
                            fileStatuses[6] = lockedRevision;
                            fileStatuses[3] = newRevision;
                        } else {
                            fileStatuses[0] = ""; // There's more then one revision locked by the current user
                            fileStatuses[3] = "";
                        }
                    }
                    //fileStatuses[4] = (String) archivesByNames.get(fileStatuses[0]);
                    index = lockInfo.indexOf('[', index2);
                    if (index > 0) {
                        index = lockInfo.indexOf(PvcsListCommand.LOCKS_SEPARATOR, index);
                    }
                } while (index > 0);
            }
        }
        if (newLockInfo && elements[0].startsWith(PvcsListCommand.LOCK_INFO_LOCKED_REVISION) && fileStatuses != null) {
            lastLockedRevision = elements[0].substring(PvcsListCommand.LOCK_INFO_LOCKED_REVISION.length()).trim();
        }
        if (newLockInfo && elements[0].startsWith(PvcsListCommand.LOCK_INFO_NEW_REVISION) && fileStatuses != null) {
            lastNewRevision = elements[0].substring(PvcsListCommand.LOCK_INFO_NEW_REVISION.length()).trim();
        }
        if (newLockInfo && elements[0].startsWith(PvcsListCommand.LOCK_INFO_LOCKED_BY) && fileStatuses != null) {
            String locker = elements[0].substring(PvcsListCommand.LOCK_INFO_LOCKED_BY.length()).trim();
            boolean isCurrentUser = org.openide.util.Utilities.isWindows() ?
                locker.equalsIgnoreCase(currentUserName) :
                locker.equals(currentUserName);
            if (isCurrentUser) {
                String fileRevision = fileStatuses[6];
                //System.out.println("File: '"+fileStatuses[0]+"' - revision: '"+lastLockedRevision+"' -> '"+lastNewRevision+"', fileRevision: '"+fileRevision+"'");
                if (fileRevision == null) {
                    fileStatuses[6] = lastLockedRevision;
                    fileStatuses[3] = lastNewRevision;
                } else {
                    fileStatuses[6] = ""; // There's more then one revision locked by the current user
                    fileStatuses[3] = "";
                }
            }
            if (fileStatuses[2] == null || fileStatuses[2].length() == 0) {
                fileStatuses[2] = locker;
            } else {
                if (isCurrentUser) {
                    fileStatuses[2] = locker + ',' + fileStatuses[2];
                } else {
                    fileStatuses[2] = fileStatuses[2] + ',' + locker;
                }
            }
        }
        if (newLockInfo && elements[0].equals(PvcsListCommand.LOCK_INFO_END) && fileStatuses != null) {
            newLockInfo = false;
            lastLockedRevision = null;
            lastNewRevision = null;
        }
    }
}
