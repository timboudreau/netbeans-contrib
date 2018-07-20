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
import org.openide.util.*;

import org.netbeans.modules.vcscore.*;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.caching.StatusFormat;
import org.netbeans.modules.vcscore.commands.*;

/**
 * Read a single VCS directory.
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class CommandLineVcsDirReader extends ExecuteCommand {

    private List rawData = new ArrayList();
    private boolean classRunning = false;

    private DirReaderListener listener = null ;


    //-------------------------------------------
    public CommandLineVcsDirReader(DirReaderListener listener, VcsProvider provider,
                                   UserCommand list, Map vars) {
        super(provider, list, vars);
        //super("VCS-DirReader-"+((String)vars.get("DIR"))); // NOI18N
        this.listener = listener;
    }

    /**
     * Get the graphical visualization of the command.
     * @return null no visualization is desired.
     */
    public VcsCommandVisualizer getVisualizer() {
        return null;
    }
    
    //-------------------------------------------
    public List getRawData(){
        return rawData;
    }

    protected void printDataOutput(String[] data) {
        super.printDataOutput(data);
        if (!classRunning) {
            // Do not add the data here for the class command!
            // They should be passed through filesByName table instead.
            // Command-line commands however pass the data through data output
            data = translateElements(data, (UserCommand) getCommand());
            rawData.add(data);
        }
    }

    /**
     * Loads class of given name with some arguments and execute its list() method.
     * @param className the name of the class to be loaded
     * @param args the arguments
     */
    protected void runClass(String exec, String className, String[] args) {
        classRunning = true;
        boolean success = true;
        Class listClass = null;
        try {
            listClass =  Class.forName(className, true,
                                       VcsUtilities.getSFSClassLoader());
        } catch (ClassNotFoundException e) {
            //fileSystem.debug ("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            //container.match("LIST: "+g("ERR_ClassNotFound", className)); // NOI18N
            try {
                printErrorOutput("CLASS EXEC: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_ClassNotFound", className)); // NOI18N
            } catch(java.util.MissingResourceException mrexc) {
                // Likely to be called when the module is being uninstalled
                printErrorOutput("CLASS EXEC: Class " + className + " not found"); // NOI18N
            }
            success = false;
        }
        VcsListCommand listCommand = null;
        if (success) {
            try {
                listCommand = (VcsListCommand) listClass.newInstance();
            } catch (InstantiationException e) {
                //fileSystem.debug ("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
                //container.match("LIST: "+g("ERR_CanNotInstantiate", listClass)); // NOI18N
                printErrorOutput("CLASS LIST: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_CanNotInstantiate", listClass)); // NOI18N
                success = false;
            } catch (IllegalAccessException e) {
                //fileSystem.debug ("LIST: "+g("ERR_IllegalAccessOnClass", listClass)); // NOI18N
                //container.match(g("LIST: "+"ERR_IllegalAccessOnClass", listClass)); // NOI18N
                printErrorOutput("CLASS LIST: " + NbBundle.getMessage(CommandLineVcsDirReader.class,
                                                                      "ERR_IllegalAccessOnClass", listClass)); // NOI18N
                success = false;
            }
        }
        Hashtable filesByName = new Hashtable();
        UserCommand list = (UserCommand) getCommand();
        if (success) {
            Map vars = getVariables();
            ExecuteCommand.setAdditionalParams(listCommand, getProvider());
            String dataRegex = (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX);
            if (dataRegex == null) dataRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("DATAREGEX", dataRegex); // NOI18N
            String errorRegex = (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX);
            if (errorRegex == null) errorRegex = ExecuteCommand.DEFAULT_REGEX;
            vars.put("ERRORREGEX", errorRegex); // NOI18N
            String input = (String) list.getProperty(UserCommand.PROPERTY_INPUT);
            if (input != null) vars.put("INPUT", input); // NOI18N
            //vars.put("TIMEOUT", new Long(list.getTimeout())); // NOI18N
            //TopManager.getDefault().setStatusText(g("MSG_Command_name_running", list.getName()));
            try {
                Hashtable varsHashtable = new Hashtable(vars); // For compatibility reasons
                success = listCommand.list(varsHashtable, args, filesByName,
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
                                       }, (String) list.getProperty(UserCommand.PROPERTY_DATA_REGEX),
                                       new CommandDataOutputListener() {
                                           public void outputData(String[] data) {
                                               printDataErrorOutput(data);
                                           }
                                       }, (String) list.getProperty(UserCommand.PROPERTY_ERROR_REGEX)
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
                        NbBundle.getMessage(CommandLineVcsDirReader.class, "ERR_EXC_IN_CLASS", className)));
            }
        }
        //if (!shouldFail) {
        //String[] elements = new String[2];
        //File parent = new File(dir.getAbsolutePath());
        for(Enumeration e = filesByName.keys(); e.hasMoreElements() ;) {
            String fileName = (String) e.nextElement();
            String[] elements = (String[]) filesByName.get(fileName);
            //elements[0] = fileName;
            //elements[1] = fileStatus;
            //fileSystem.debug("stdout: "+VcsUtilities.arrayToString(elements)); // NOI18N
            elements = translateElements(elements, list);
            rawData.add(elements);
        }
        exitStatus = (success) ? VcsCommandExecutor.SUCCEEDED : VcsCommandExecutor.FAILED;
    }

    //-------------------------------------------
    public void run() {
        try {
            super.run();
        } finally {
            String commonParent = (String) getVariables().get("COMMON_PARENT");
            String dir = (String) getVariables().get("DIR"); // NOI18N
            dir = dir.replace (java.io.File.separatorChar, '/');
            if (commonParent != null) {
                commonParent = commonParent.replace (java.io.File.separatorChar, '/');
                if (commonParent.length() > 0) dir = commonParent + "/" + dir;
            }
            try {
                listener.readDirFinished(dir, rawData, getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            } catch (ThreadDeath td) {
                throw td;
            } catch (Throwable t) {
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, t);
            }
        }
    }

    /**
     * Add a file reader listener, that gets the updated attributes of the
     * processed file(s). <p>
     * This is an empty method, the listener is added nowhere. This class uses
     * the passed listener for the notification.
     */
    public void addFileReaderListener(FileReaderListener l) {
    }

    
    /**
     * Translate elements obtained from the command line reader to elements used by {@link RefreshCommandSupport}
     * @param elements the elements from command line reader
     * @param list the refresh command
     * @return new set of elements in the correct form for <code>RefreshCommandSupport</code>
     */
    public static String[] translateElements(String[] cmdElements, UserCommand list) {
        int removedIndex = VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_REMOVED_FILE_NAME);
        if (removedIndex >= 0 && cmdElements.length > removedIndex && cmdElements[removedIndex] != null) {
            return new String[] { cmdElements[removedIndex] };
        }
        int n = StatusFormat.NUM_ELEMENTS;
        int[] index = new int[n];
        index[StatusFormat.ELEMENT_INDEX_FILE_NAME] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_FILE_NAME);
        index[StatusFormat.ELEMENT_INDEX_STATUS] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_STATUS);
        index[StatusFormat.ELEMENT_INDEX_LOCKER] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_LOCKER);
        index[StatusFormat.ELEMENT_INDEX_REVISION] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_REVISION);
        index[StatusFormat.ELEMENT_INDEX_STICKY] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_STICKY);
        index[StatusFormat.ELEMENT_INDEX_ATTR] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_ATTR);
        index[StatusFormat.ELEMENT_INDEX_DATE] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_DATE);
        index[StatusFormat.ELEMENT_INDEX_TIME] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_TIME);
        index[StatusFormat.ELEMENT_INDEX_SIZE] =
            VcsCommandIO.getIntegerPropertyAssumeNegative(list, UserCommand.PROPERTY_LIST_INDEX_SIZE);
        String[] elements = new String[n];
        int cmdn = cmdElements.length;
        for(int i = 0; i < n; i++) {
            if (index[i] >= 0 && index[i] < cmdn) elements[i] = cmdElements[index[i]];
        }
        return elements;
    }

}
