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

package org.netbeans.modules.vcscore;

import java.util.*;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.cookies.SaveCookie;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.UserCommandTask;
import org.netbeans.modules.vcscore.cmdline.WrappingCommandTask;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;

/**
 * Several static methods.
 *
 * @deprecated This class is retained just for compatibility reasons. Use the
 *             new VCS APIs instead.
 *
 * @author Martin Entlicher
 */
public final class VcsAction extends Object {//NodeAction implements ActionListener {
    
    private VcsAction() {
    }

    /**
     * Do refresh children of a directory.
     * @param path the directory path
     */
    private static void doList(VcsProvider provider, String path) {
        FileObject fo = provider.findResource(path);
        if (fo == null) return ;
        if (fo.isData()) fo = fo.getParent();
        Turbo.getRepositoryMeta(fo);
        TurboUtil.refreshFolder(fo);
    }

    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param execContext the VCS command execution context
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, CommandExecutionContext execContext) {
        return doCommand(files, cmd, additionalVars, execContext, null, null, null, null);
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param execContext the VCS command execution context
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, CommandExecutionContext execContext,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener) {
        return doCommand(files, cmd, additionalVars, execContext, stdoutListener, stderrListener, stdoutDataListener, stderrDataListener, true);
    }
    
    private static class RegexDataListenerBridge extends Object implements RegexErrorListener {
        
        private CommandDataOutputListener dataListener;
        
        public RegexDataListenerBridge(CommandDataOutputListener dataListener) {
            this.dataListener = dataListener;
        }
        
        /**
         * This method is called, with elements of the parsed data.
         * @param elements the elements of parsed data.
         */
        public void outputMatchedGroups(String[] elements) {
            dataListener.outputData(elements);
        }
        
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param execContext the VCS command execution context
     * @param saveProcessingFiles whether save processing files prior command execution
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, CommandExecutionContext execContext,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener,
                                                 boolean saveProcessingFiles) {
        //System.out.println("doCommand("+VcsUtilities.arrayToString((String[]) files.keySet().toArray(new String[0]))+", "+cmd+")");
        if (files.size() == 0) return new VcsCommandExecutor[0];
        if (saveProcessingFiles) {
            assureFilesSaved(files.values());
        }
        UserCommand ucmd = (UserCommand) cmd;
        CommandSupport cmdSupp = execContext.getCommandSupport(cmd.getName());
        if (cmdSupp == null || !cmd.equals(execContext.getCommand(cmd.getName()))) {
            cmdSupp = new UserCommandSupport(ucmd, execContext);
        }
        Command command = cmdSupp.createCommand();
        if (command instanceof VcsDescribedCommand) {
            VcsDescribedCommand dcmd = (VcsDescribedCommand) command;
            if (additionalVars != null) dcmd.setAdditionalVariables(additionalVars);
            if (stdoutListener != null) dcmd.addTextOutputListener(stdoutListener);
            if (stderrListener != null) dcmd.addTextErrorListener(stderrListener);
            if (stdoutDataListener != null) dcmd.addRegexOutputListener(new RegexDataListenerBridge(stdoutDataListener));
            if (stderrDataListener != null) dcmd.addRegexErrorListener(new RegexDataListenerBridge(stderrDataListener));
        }
        if (additionalVars != null) {
            String ctrlInAction = (String) additionalVars.get(Variables.VAR_CTRL_DOWN_IN_ACTION);
            if (ctrlInAction != null && ctrlInAction.length() > 0) {
                command.setExpertMode(true);
            }
        }
        boolean anyWereSet = UserCommandSupport.setCommandFilesFromTable(command, files, execContext);
        if (!anyWereSet) return new VcsCommandExecutor[0];
        if (!VcsManager.getDefault().showCustomizer(command)) return new VcsCommandExecutor[0];
        if (command.getFiles() != null) {
            CommandTask task = command.execute();
            if (task instanceof UserCommandTask) {
                return new VcsCommandExecutor[] { ((UserCommandTask) task).getExecutor() };
            } else if (task instanceof WrappingCommandTask) {
                UserCommandTask[] tasks = ((WrappingCommandTask) task).getTasks();
                VcsCommandExecutor[] executors = new VcsCommandExecutor[tasks.length];
                for (int i = 0; i < executors.length; i++) {
                    executors[i] = tasks[i].getExecutor();
                }
                return executors;
            }
        }
        return new VcsCommandExecutor[0];
    }

    /** Make sure, that the files are saved. If not, save them.
     * Synchronized, so that we do not try to save the same objects twice in parallel.
     * @param fos the collection of FileObjects which or under which modified
     *        files are saved.
     */
    public static synchronized void assureFilesSaved(Collection fos) {
        DataObject[] modified = DataObject.getRegistry().getModified();
        if (modified.length == 0) return ;
        Set files = new HashSet(fos);
        List folders = null;
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (fo == null) {
                files.remove(fo);
                continue;
            }
            if (fo.isFolder()) {
                if (folders == null) {
                    folders = new ArrayList();
                }
                folders.add(fo);
                files.remove(fo);
            }
        }
        if (folders == null) {
            folders = Collections.EMPTY_LIST;
        }
        boolean wasSaved = false;
        for (int i = 0; i < modified.length; i++) {
            Set modifiedFiles = modified[i].files();
            for (Iterator modIt = modifiedFiles.iterator(); modIt.hasNext(); ) {
                FileObject modifiedFile = (FileObject) modIt.next();
                if (shouldBeSaved(modifiedFile, files, folders)) {
                    Node.Cookie cake = modified[i].getCookie(SaveCookie.class);
                    try {
                        if (cake != null) {
                            ((SaveCookie) cake).save();
                            wasSaved = true;
                        }
                    } catch (java.io.IOException exc) {
                        ErrorManager.getDefault().notify(exc);
                    }
                    break;
                }
            }
        }
        if (wasSaved) {
            // If we saved some data, we need to wait at least one second.
            // This will assure, that any further command, that will modify
            // the conent of a saved file will actually change the modification
            // time (time resolution is ~1s). See issue #36065 for details.
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException iex) {}
        }
    }
    
    private static boolean shouldBeSaved(FileObject modifiedFile, Collection files, Collection folders) {
        //modifiedFile = VcsUtilities.convertFileObjects(new FileObject[] { modifiedFile })[0];
        if (modifiedFile == null) return false;
        if (files.contains(modifiedFile)) {
            return true;
        } else {
            for (Iterator it = folders.iterator(); it.hasNext(); ) {
                FileObject folder = (FileObject) it.next();
                if (FileUtil.isParentOf(folder, modifiedFile)) {
                    return true;
                }
            }
            return false;
        }
    }
    
}
