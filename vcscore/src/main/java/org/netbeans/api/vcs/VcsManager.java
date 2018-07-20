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

package org.netbeans.api.vcs;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.VcsStatusProvider;
//import org.netbeans.spi.vcs.commands.AbstractCommand;

import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.CommandProcessListener;
import org.netbeans.modules.vcscore.commands.CommandTaskInfo;

/**
 * The management of VCS actions. This class is a singleton, the instance is accessible
 * through getDefault(). Do not try to instantiate.
 *
 * @author  Martin Entlicher
 */
public class VcsManager extends Object {
    
    private static VcsManager manager;
    
    /** Creates a new instance of VcsManager.
     * Do not use. This class contains just static methods.
     */
    private VcsManager() {
    }
    
    /**
     * Get the default implementation of VcsManager.
     */
    public static synchronized VcsManager getDefault() {
        if (manager == null) {
            manager = new VcsManager();
        }
        return manager;
    }
    
    /**
     * Get the available commands for a set of files.
     * @return The array of command names.
     */
    public String[] findCommands(FileObject[] files) {
        if (files.length == 0) return new String[0];
        Map filesByProviders = createFilesByCommandProvidersMap(files);
        ArrayList commandNames = null;
        for (Iterator it = filesByProviders.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            String[] names = provider.getCommandNames();
            ArrayList providerCommandNames = new ArrayList();
            for (int i = 0; i < names.length; i++) {
                Command cmd = provider.createCommand(names[i]);
                if (cmd.getApplicableFiles((FileObject[]) ((ArrayList) filesByProviders.get(provider)).toArray(new FileObject[0])) != null) {
                    providerCommandNames.add(names[i]);
                }
            }
            if (commandNames == null) {
                commandNames = providerCommandNames;
            } else {
                commandNames.retainAll(providerCommandNames);
            }
        }
        return (String[]) commandNames.toArray(new String[commandNames.size()]);
    }
    
    /**
     * Create a map of <provider, files> pairs for given set of files.
     * @param files The array of files to create the map from
     * @return The map of providers for individual files as keys and a list of
     *         their associated files as values.
     */
    private static Map createFilesByCommandProvidersMap(FileObject[] files) throws IllegalArgumentException {
        HashMap filesByProviders = new HashMap();
        for (int i = 0; i < files.length; i++) {
            VcsCommandsProvider provider = VcsCommandsProvider.findProvider(files[i]);
            if (provider != null) {
                ArrayList fileList = (ArrayList) filesByProviders.get(provider);
                if (fileList == null) {
                    fileList = new ArrayList();
                    filesByProviders.put(provider, fileList);
                }
                fileList.add(files[i]);
            } else {
                throw new IllegalArgumentException("There is no commands provider for file '"+files[i]+"'");
            }
        }
        return filesByProviders;
    }
    
    /**
     * Create a command of the given name, that will act on the array of files.
     * @param cmdName The name of the command.
     * @param files The array of files to act on.
     * @return The Command instance or <code>null</code> if no such command exist
     *         or can not be run on provided files.
     */
    public Command createCommand(String cmdName, FileObject[] files) throws IllegalArgumentException {
        if (files.length == 0) return null;
        Map filesByProviders = createFilesByCommandProvidersMap(files);
        Command cmd;
        if (filesByProviders.size() == 1) {
            VcsCommandsProvider provider = (VcsCommandsProvider) filesByProviders.keySet().iterator().next();
            cmd = provider.createCommand(cmdName);
            if (cmd != null) {
                files = cmd.getApplicableFiles(files);
                if (files != null) {
                    cmd.setFiles(files);
                } else {
                    cmd = null; // No such command on these files.
                }
            }
        } else {
            Set commands = new HashSet();
            for (Iterator it = filesByProviders.keySet().iterator(); it.hasNext(); ) {
                VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
                Command cmd1 = provider.createCommand(cmdName);
                if (cmd1 != null) {
                    FileObject[] cmdFiles = (FileObject[]) ((ArrayList) filesByProviders.get(provider)).toArray(new FileObject[0]);
                    cmdFiles = cmd1.getApplicableFiles(cmdFiles);
                    if (cmdFiles != null) {
                        cmd1.setFiles(cmdFiles);
                        commands.add(cmd1);
                    }
                }
            }
            if (commands.size() == 0) {
                cmd = null;
            } else if (commands.size() == 1) {
                cmd = (Command) commands.iterator().next();
            } else {
                cmd = createMetaCommand(cmdName, (Command[]) commands.toArray(new Command[commands.size()])); //new MetaCommand(cmdName, commands);
            }
        }
        return cmd;
    }
    
    /**
     * Create a command of the given class, that will act on the array of files.
     * @param cmdClass The class type of the command. It has to be a class or
     *        interface extending {@link org.netbeans.api.vcs.commands.Command}.
     * @param files The array of files to act on.
     * @return The Command instance or <code>null</code> if no such command exist
     *         or can not be run on provided files.
     */
    public Command createCommand(Class cmdClass, FileObject[] files) throws IllegalArgumentException {
        if (files.length == 0) return null;
        Map filesByProviders = createFilesByCommandProvidersMap(files);
        Command cmd;
        if (filesByProviders.size() == 1) {
            VcsCommandsProvider provider = (VcsCommandsProvider) filesByProviders.keySet().iterator().next();
            cmd = provider.createCommand(cmdClass);
            if (cmd != null) {
                files = cmd.getApplicableFiles(files);
                if (files != null) {
                    cmd.setFiles(files);
                } else {
                    cmd = null; // No such command on these files.
                }
            }
        } else {
            Set commands = new HashSet();
            for (Iterator it = filesByProviders.keySet().iterator(); it.hasNext(); ) {
                VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
                Command cmd1 = provider.createCommand(cmdClass);
                if (cmd1 != null) {
                    FileObject[] cmdFiles = (FileObject[]) ((ArrayList) filesByProviders.get(provider)).toArray(new FileObject[0]);
                    cmdFiles = cmd1.getApplicableFiles(cmdFiles);
                    if (cmdFiles != null) {
                        cmd1.setFiles(cmdFiles);
                        commands.add(cmd1);
                    }
                }
            }
            if (commands.size() == 0) {
                cmd = null;
            } else {
                String cmdName = ((Command) commands.iterator().next()).getName();
                cmd = createMetaCommand(cmdName, (Command[]) commands.toArray(new Command[commands.size()]));
                //cmd = new MetaCommand(cmdName, (Command[]) commands.toArray(new Command[commands.size()]));
            }
        }
        return cmd;
    }
    
    /**
     * Let the user to visually customize the command. The method blocks until the
     * customization is done.
     * @param cmd The command to customize.
     * @return True if the customization was successfull (the user pressed the OK button)
     *         or False if the customization was cancelled (the user pressed the Cancel button).
     */
    public boolean showCustomizer(Command cmd) {
        CommandProcessor processor = CommandProcessor.getInstance();
        if (java.awt.EventQueue.isDispatchThread()) {
            return processor.preprocessSynchronous(cmd);
        }
        CustomizationListener custListener = new CustomizationListener(cmd);
        processor.addCommandProcessListener(custListener);
        try {
            processor.preprocess(cmd);
        } catch (Throwable thr) {
            processor.removeCommandProcessListener(custListener);
            // Can not throw Throwable without being declared - wrap into RuntimeException
            RuntimeException rex = new RuntimeException();
            rex.initCause(thr);
            throw rex;
        }
        synchronized (custListener) {
            if (custListener.isPreprocessed()) {
                //System.out.println("Command was already preprocessed.");
                processor.removeCommandProcessListener(custListener);
            } else {
                try {
                    //System.out.println("Waiting for command "+cmd+" preprocess to finish.");
                    custListener.wait();
                    //System.out.println("Command preprocess finished.");
                } catch (InterruptedException iex) {
                    return false;
                } finally {
                    processor.removeCommandProcessListener(custListener);
                }
            }
        }
        return custListener.isCustumizationSuccessfull();
    }
    
    /**
     * Get the file status information.
     * @param file The file to find the status for
     * @return The instance of {@link FileStatusInfo} or <code>null</code> when
     * the file status is unknown or no status provider was found for this file.
     */
    public FileStatusInfo getFileStatus(FileObject file) {
        VcsStatusProvider status = VcsStatusProvider.findProvider(file);
        if (status != null) {
            return status.getStatus(file.getPath());
        } else {
            return null;
        }
    }
    
    private static Command createMetaCommand(String cmdName, Command[] commands) {
        HashSet commonInterfaces = null;
        for (int i = 0; i < commands.length; i++) {
            HashSet intrs = findAllInterfaces(commands[i].getClass());
            if (commonInterfaces == null) {
                commonInterfaces = intrs;
            } else {
                commonInterfaces.retainAll(intrs);
            }
        }
        InvocationHandler handler = new DelegatingCommandInvocationHandler(cmdName, commands);
        Command command =
            (Command) Proxy.newProxyInstance(VcsManager.class.getClassLoader(),
                                             (Class[]) commonInterfaces.toArray(new Class[commonInterfaces.size()]),
                                             handler);
        return command;
    }
    
    private static HashSet findAllInterfaces(Class clazz) {
        HashSet interfaces = new HashSet();
        Class[] intrs = clazz.getInterfaces();
        interfaces.addAll(Arrays.asList(intrs));
        for (int i = 0; i < intrs.length; i++) {
            interfaces.addAll(findAllInterfaces(intrs[i]));
        }
        return interfaces;
    }
    
    private static class CustomizationListener extends Object implements CommandProcessListener {
        
        private Command cmd;
        private boolean status;
        private boolean preprocessed = false;
        
        public CustomizationListener(Command cmd) {
            this.cmd = cmd;
        }
        
        /**
         * Get the commands provider. This method returns <code>null</code>
         * in order to listen to all commands.
         */
        public VcsCommandsProvider getProvider() {
            return null;
        }
        
        /**
         * This method is called when the command is done.
         */
        public void commandDone(CommandTaskInfo info) {
        }
        
        /**
         * Called when the preprocessing of the command finished.
         * @param cmd The command which was preprocessed.
         * @param status The status of preprocessing. If false, the command is not executed.
         */
        public void commandPreprocessed(Command cmd, boolean status) {
            //System.out.println("commandPreprocessed("+cmd+"), this.cmd = "+this.cmd);
            if (this.cmd.equals(cmd)) {
                this.status = status;
                //System.out.println("  equals = true, notification is being called.");
                synchronized (this) {
                    this.preprocessed = true;
                    notify();
                }
            }
        }
        
        /**
         * Called when the command is just to be preprocessed.
         */
        public void commandPreprocessing(Command cmd) {
        }
        
        /**
         * This method is called when the command is just to be started.
         */
        public void commandStarting(CommandTaskInfo info) {
        }
        
        public boolean isPreprocessed() {
            return preprocessed;
        }
        
        public boolean isCustumizationSuccessfull() {
            return status;
        }
        
    }
}
