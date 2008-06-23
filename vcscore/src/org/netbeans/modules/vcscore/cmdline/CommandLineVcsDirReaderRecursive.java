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

package org.netbeans.modules.vcscore.cmdline;

import java.util.*;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.*;

/**
 * Read VCS directory recursively.
 *
 * @author  Martin Entlicher
 */
public class CommandLineVcsDirReaderRecursive extends ExecuteCommand {

    private String path = null;

    private VcsDirContainer rawData = null;

    private DirReaderListener listener = null;


    /** Creates new CommandLineVcsDirReaderRecursive */
    public CommandLineVcsDirReaderRecursive(DirReaderListener listener, VcsProvider provider,
                                            UserCommand listSub, Map vars) {
        super(provider, listSub, vars);
        this.listener = listener;
        String commonParent = (String) vars.get("COMMON_PARENT");
        String dir = (String) vars.get("DIR"); // NOI18N
        if (commonParent != null && commonParent.length() > 0) {
            dir = commonParent + Variables.expand(vars, "${PS}", false) + dir;
        }
        this.path = dir.replace (java.io.File.separatorChar, '/');
    }

    /**
     * Get the graphical visualization of the command.
     * @return null no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer() {
        return null;
    }
    
    /**
     * The runCommand() method not supported. This method cause the command to always fail.
     */
    protected void runCommand(String[] execs) {
        //fileSystem.debug("LIST_SUB: "+g("MSG_List_command_failed")+"\n"); // NOI18N
        printErrorOutput("Recursive Command can not execute the command. "+
                         "Please supply a class of instance of VcsListRecursiveCommand."); // NOI18N
        printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class,
                         "MSG_List_command_failed")+"\n"); // NOI18N
        exitStatus = VcsCommandExecutor.FAILED;
    }

    /**
     * Loads class of given name with some arguments and execute its list() method.
     * @param className the name of the class to be loaded
     * @param args the arguments
     */
    protected void runClass(String exec, String className, String[] args) {
        boolean success = true;
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       VcsUtilities.getSFSClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            //container.match("LIST_SUB: "+g("ERR_ClassNotFound", className)); // NOI18N
            printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, // NOI18N
                             "ERR_ClassNotFound", className)); // NOI18N
            success = false;
        }
        VcsListRecursiveCommand listCommand = null;
        try {
            listCommand = (VcsListRecursiveCommand) listClass.newInstance();
        } catch (InstantiationException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            //container.match("LIST_SUB: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
            printErrorOutput("LIST_SUB: "+NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, // NOI18N
                             "ERR_CanNotInstantiate", listClass)); // NOI18N
            success = false;
        } catch (IllegalAccessException e) {
            //fileSystem.debug ("LIST_SUB: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
            //container.match(g("LIST_SUB: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            printErrorOutput(NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class,
                             "LIST_SUB: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
            success = false;
        }
        VcsDirContainer filesByName = new VcsDirContainer(path);
        UserCommand listSub = (UserCommand) getCommand();
        if (success) {
            Map vars = getVariables();
            ExecuteCommand.setAdditionalParams(listCommand, getProvider());
            String dataRegex = (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            if (dataRegex == null) dataRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("DATAREGEX", dataRegex); // NOI18N
            String errorRegex = (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            if (errorRegex == null) errorRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("ERRORREGEX", errorRegex); // NOI18N
            String input = (String) listSub.getProperty(UserCommand.PROPERTY_INPUT);
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(listSub.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", listSub.getName()));
            try {
                Hashtable varsHashtable = new Hashtable(vars); // For compatibility reasons
                success = listCommand.listRecursively(varsHashtable, args, filesByName,
                                           new CommandOutputListener() {
                                               public void outputLine(String line) {
                                                   printOutput(line);
                                               }
                                           },
                                           new CommandOutputListener() {
                                               public void outputLine(String line) {
                                                   printErrorOutput(line);
                                               }
                                           },
                                           new CommandDataOutputListener() {
                                               public void outputData(String[] data) {
                                                   printDataOutput(data);
                                               }
                                           }, (String) listSub.getProperty(UserCommand.PROPERTY_DATA_REGEX),
                                           new CommandDataOutputListener() {
                                               public void outputData(String[] data) {
                                                   printDataErrorOutput(data);
                                               }
                                           }, (String) listSub.getProperty(UserCommand.PROPERTY_ERROR_REGEX)
                                          );
            //E.deb("shouldFail = "+shouldFail+" after list with "+filesByName.size()+" elements"); // NOI18N
            /*
            for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
              String fileName=(String)e.nextElement();
              String fileStatus=(String)filesByName.get(fileName);
              E.deb("filesByName: "+fileName+" | "+fileStatus);
        }
            */
            } catch (ThreadDeath td) {
                throw td; // re-throw the ThreadDeath
            } catch (Throwable thr) { // Something bad has happened in the called class!
                success = false;
                ErrorManager.getDefault().notify(
                    ErrorManager.getDefault().annotate(thr,
                        NbBundle.getMessage(CommandLineVcsDirReaderRecursive.class, "ERR_EXC_IN_CLASS", className)));
            }
        }
        rawData = filesByName;
        //rawData = new VcsDirContainer();
        translateElementsRecursively(rawData, listSub);
        //putFilesToDirRecursively(dir, filesByName, rawData);
        exitStatus = (success) ? VcsCommandExecutor.SUCCEEDED : VcsCommandExecutor.FAILED;
    }
    
    private void translateElementsRecursively(VcsDirContainer rawData, UserCommand listSub) {
        Hashtable filesByName = (Hashtable) rawData.getElement();
        if (filesByName != null) {
            Hashtable filesByNameTranslated = new Hashtable();
            for (Enumeration en = filesByName.keys(); en.hasMoreElements(); ) {
                String name = (String) en.nextElement();
                String[] elements = (String[]) filesByName.get(name);
                elements = CommandLineVcsDirReader.translateElements(elements, listSub);
                filesByNameTranslated.put(name, elements);
            }
            rawData.setElement(filesByNameTranslated);
        }
        VcsDirContainer[] subdirs = rawData.getSubdirContainers();
        for(int i = 0; i < subdirs.length; i++) {
            translateElementsRecursively(subdirs[i], listSub);
        }
    }

    public void run() {
        Map vars = getVariables();
        String commonParent = (String) vars.get("COMMON_PARENT");
        String dir = (String) vars.get("DIR"); // NOI18N
        if (commonParent != null && commonParent.length() > 0) {
            dir = commonParent + Variables.expand(vars, "${PS}", false) + dir;
        }
        //path = dir.replace (java.io.File.separatorChar, '/');
        String exec = getExec();
        if (exec == null || exec.trim().length() == 0) {
            //String dirName = (((String) vars.get("DIR"))).replace(((String) vars.get("PS")).charAt(0), '/');

            FileObject folder = getProvider().findResource(path);
            doRefreshRecursively(folder);
            exitStatus = VcsCommandExecutor.SUCCEEDED;
            return;

        } else {
            try {
                super.run();
            } finally {
                try {
                    listener.readDirFinishedRecursive(path, rawData, getExitStatus() == VcsCommandExecutor.SUCCEEDED);
                } catch (ThreadDeath td) {
                    throw td;
                } catch (Throwable t) {
                    ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, t);
                }
            }
        }
    }
    
    private static boolean doRefreshRecursively(FileObject folder) {
        boolean success = TurboUtil.refreshFolder(folder);
        FileObject children[] = folder.getChildren();
        for (int i = 0; i < children.length && success; i++) {
            if (children[i].isFolder()) {
                success = success && doRefreshRecursively(children[i]);
            }
        }
        return success;
    }

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s). <p>
     * This is an empty method, the listener is added nowhere. This class uses
     * the passed listener for the notification.
     */
    public void addFileReaderListener(FileReaderListener l) {
    }

}
