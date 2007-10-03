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

import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.netbeans.api.fileinfo.NonRecursiveFolder;
import org.openide.actions.FileSystemAction;

import org.openide.awt.Actions;
import org.openide.awt.JInlineMenu;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.CheckInCommand;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.actions.CommandMenu;
import org.netbeans.modules.vcscore.actions.ContextAwareDelegateAction;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.grouping.GroupCookie;
import org.netbeans.modules.vcscore.objectintegrity.VcsObjectIntegritySupport;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.WeakList;
import org.openide.filesystems.FileSystem;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;

/**
 * The system action with VCS commands, that are provided by the FileSystem.
 * @author Martin Entlicher
 */
public class VcsFSCommandsAction extends NodeAction implements ActionListener,
                                 ContextAwareDelegateAction.Delegatable {
    
    protected Collection selectedFileObjects = null;
    //protected CommandsTree actionCommandsTree = null;
    // The latest map of providers and associated map of files with the file info
    private Map filesByCommandProviders;

    // List of commands, that can switch the expert mode on/off
    boolean CTRL_Down = false;

    private static final long serialVersionUID = -4196511763565479366L;
    
    /** Creates a new instance of VcsFSCommandsAction */
    public VcsFSCommandsAction() {
    }
    
    /**
     * @return a map of array of FileObjects and associated file info, if any.
     * Currently the info can be a String message, or NonRecursiveFolder.class
     * or <code>null</code>.
     */
    private Map getSelectedFileObjectsFromActiveNodes (Lookup lookup, boolean checkFSAction) {
        Map filesWithInfo = new Table();
        ArrayList files = new ArrayList();
        Node[] nodes = (Node[])lookup.lookup (new Lookup.Template (Node.class)).allInstances().toArray (new Node[0]);
        for (int i = 0; i < nodes.length; i++) {
            boolean isFSAction = !checkFSAction;
            if (checkFSAction) {
                javax.swing.Action[] actions = nodes[i].getActions(true);
                for (int a = 0; a < actions.length; a++) {
                    if (actions[a] instanceof FileSystemAction) {
                        isFSAction = true;
                        break;
                    }
                }
            }
            GroupCookie gc = (GroupCookie) nodes[i].getCookie(GroupCookie.class);
            if (gc != null) {
                // Put any preceding files there to keep the order:
                if (files.size() > 0) {
                    filesWithInfo.put(files.toArray(new FileObject[0]), null);
                    files.clear();
                }
                /*
                if (variablesForSelectedFiles == null) variablesForSelectedFiles = new HashMap();
                Hashtable additionalVars = new Hashtable();
                additionalVars.put(Variables.GROUP_NAME, grNode.getDisplayName());
                String description = grNode.getShortDescription();
                if (description != null) {
                    additionalVars.put(Variables.GROUP_DESCRIPTION, description);
                }
                 */
                String message = gc.getDescription();
                List messageFiles = new ArrayList();
                Enumeration children = nodes[i].getChildren().nodes();
                while (children.hasMoreElements()) {
                    Node nd = (Node) children.nextElement();
                    DataObject dd = (DataObject) nd.getCookie(DataObject.class);
                    if (dd == null) continue;
                    addAllFromSingleFS(dd.getPrimaryFile(), dd.files(), messageFiles);
                }
                filesWithInfo.put(messageFiles.toArray(new FileObject[0]), message);
            } else {
                Lookup.Result nonRecFolders = nodes[i].getLookup().lookup(new Lookup.Template(NonRecursiveFolder.class));
                Collection nrfs = nonRecFolders.allInstances();
                if (nrfs != null && nrfs.size() > 0) {
                    // Put any preceding files there to keep the order:
                    if (files.size() > 0) {
                        filesWithInfo.put(files.toArray(new FileObject[0]), null);
                        files.clear();
                    }
                    List nrFiles = new ArrayList(nrfs.size());
                    Collection fos = new ArrayList(nrfs.size());
                    for (Iterator it = nrfs.iterator(); it.hasNext(); ) {
                        NonRecursiveFolder nrf = (NonRecursiveFolder) it.next();
                        fos.add(nrf.getFolder());
                    }
                    FileObject one = (FileObject) fos.iterator().next();
                    addAllFromSingleFS(one, fos, nrFiles);
                    filesWithInfo.put(nrFiles.toArray(new FileObject[0]), NonRecursiveFolder.class);
                } else {
                    Lookup.Result fileObjects = nodes[i].getLookup().lookup(new Lookup.Template(FileObject.class));
                    Collection fos;
                    if (fileObjects != null && (fos = fileObjects.allInstances()).size() > 0) {
                        FileObject one = (FileObject) fos.iterator().next();
                        // Ignore nodes that represent folders and do not have
                        // FileSystemAction associated. It might be dangerous to
                        // add VCS actions there, because the UI might not be
                        // descriptive enough for the user to see what's going
                        // to happen.
                        if (!isFSAction && one.isFolder()) continue;
                        addAllFromSingleFS(one, fos, files);
                    } else {
                        DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                        if (dd == null) continue;
                        if (dd instanceof DataShadow) {
                            // We want to have the same VCS actions on the link as on the original.
                            dd = ((DataShadow) dd).getOriginal();
                        }
                        // Ignore nodes that represent folders and do not have
                        // FileSystemAction associated. It might be dangerous to
                        // add VCS actions there, because the UI might not be
                        // descriptive enough for the user to see what's going
                        // to happen.
                        if (!isFSAction && dd.getPrimaryFile().isFolder()) continue;
                        addAllFromSingleFS(dd.getPrimaryFile(), dd.files(), files);
                    }
                }
            }
        }
        if (files.size() > 0) filesWithInfo.put(files.toArray(new FileObject[0]), null);
        return filesWithInfo;
    }
    
    /**
     * A workaround for Collection.addAll() method, which is broken. See
     * http://developer.java.sun.com/developer/bugParade/bugs/4715206.html
     * for details.
     * Add all elements from "src" to "dest".
     */
    private static final void addAllWorkaround(Collection src, Collection dest) {
        for (Iterator it = src.iterator(); it.hasNext(); dest.add(it.next()));
    }
    
    /**
     * Add all FileObject, that are from the primary's filesystem to "dest".
     */
    private static final void addAllFromSingleFS(FileObject primary, Collection files, Collection dest) {
        if (files.size() == 1) {
            dest.add(primary);
        } else {
            FileSystem primaryFS = (FileSystem) primary.getAttribute(org.netbeans.modules.vcscore.VcsAttributes.VCS_NATIVE_FS);
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                FileSystem fs = (FileSystem) fo.getAttribute(org.netbeans.modules.vcscore.VcsAttributes.VCS_NATIVE_FS);
                if (primaryFS != null && !primaryFS.equals(fs)) {
                    // We have a secondary file on another filesystem!
                    continue;
                }
                dest.add(fo);
            }
        }
    }
    
    /*
    public void setCommandsTree(CommandsTree commandsTree) {
        this.actionCommandsTree = commandsTree;
    }
     */

    /**
     * Get a human presentable name of the action.
     * @return the name of the action
     */
    public String getName() {
        return org.openide.util.NbBundle.getBundle(VcsFSCommandsAction.class).getString("CTL_Version_Control");
    }
    
    /**
     * Test whether the action should be enabled based on the currently activated nodes.
     * @return true for non-empty set of nodes.
     */
    public boolean enable(Node[] nodes) {
        //System.out.println("VcsFSCommandsAction.enable("+nodes.length+")");
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getCookie(GroupCookie.class) != null) {
                continue;
            } else {
                FileObject fo = (FileObject) nodes[i].getLookup().lookup(FileObject.class);
                if (fo != null) {
                    if (VcsCommandsProvider.findProvider(fo) == null) return false;
                } else {
                    DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                    //if (dd == null) System.out.println("  Node "+nodes[i]+" does not have DataObject !!");
                    if (dd == null) return false;
                    if (dd instanceof DataShadow) {
                        // We want to have the same VCS actions on the link as on the original.
                        dd = ((DataShadow) dd).getOriginal();
                    }
                    FileObject primary = dd.getPrimaryFile();
                    //System.out.println("  Commands Provider("+primary+") = "+VcsCommandsProvider.findProvider(primary));
                    if (VcsCommandsProvider.findProvider(primary) == null) return false;
                }
            }
        }
        return (nodes.length > 0);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        return getPresenter(true, org.openide.util.Utilities.actionsGlobalContext ());
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        return getPresenter(false, org.openide.util.Utilities.actionsGlobalContext());
    }
    
    public JMenuItem getPresenter(boolean inMenu, Lookup lookup) {
        JInlineMenu menu = new JInlineMenu();
        JMenuItem[] items = createMenuItems(inMenu, lookup);
        if (items.length == 0) return menu;
        menu.setMenuItems(items);
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
    public JMenuItem[] createMenuItems(boolean inMenu, Lookup lookup) {
        return createMenuItems(inMenu, lookup, false);
    }
    public JMenuItem[] createMenuItems(boolean inMenu, Lookup lookup, boolean checkFSAction) {
        Map filesWithInfo = getSelectedFileObjectsFromActiveNodes (lookup, checkFSAction);
        //System.out.println("VcsFSCommandsAction.getPresenter(): selected filesWithInfo: "+filesWithInfo);
        ArrayList menuItems = new ArrayList();
        //CommandsTree[] commands = actionCommandsTree.children();
        filesByCommandProviders = findCommandProvidersForFiles(filesWithInfo);
	//System.out.println("filesByCommandProviders.size() = "+filesByCommandProviders.size());
        if (filesByCommandProviders.size() == 0) return new JMenuItem[] {}; // return empty JInlineMenu
        CommandsTree commands;
	//VcsCommandsProvider provider = null;
        boolean globalExpertMode;
        if (filesByCommandProviders.size() == 1) {
            VcsCommandsProvider provider = (VcsCommandsProvider) filesByCommandProviders.keySet().iterator().next();
            //List files = (List) filesByCommandProviders.get(provider);
            if (provider instanceof CommandsTree.Provider) {
                commands = ((CommandsTree.Provider) provider).getCommands();
                globalExpertMode = ((CommandsTree.Provider) provider).isExpertMode();
            } else {
                commands = createDefaultCommandsTree(provider);
                globalExpertMode = false;
            }
        } else {
            commands = mergeProvidedCommands(filesByCommandProviders);
            // TODO it's necessary to get commands of known classes from all
            //      providers or somehow merge together the common commands.
            globalExpertMode = true;
            for (Iterator it = filesByCommandProviders.keySet().iterator(); it.hasNext(); ) {
                VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
                if (provider instanceof CommandsTree.Provider) {
                    globalExpertMode = globalExpertMode && ((CommandsTree.Provider) provider).isExpertMode();
                }
            }
        }
        if (commands == null) {
            return new JMenuItem[] {};
        }
        return createMenuItems(commands, filesWithInfo, inMenu, globalExpertMode);
    }
    
    private JMenuItem[] createMenuItems(CommandsTree commands, Map filesWithInfo,
                                        boolean inMenu, boolean globalExpertMode) {
        ArrayList menuItems = new ArrayList();
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            //System.out.println("GlobAction.getPresenter() subCommands["+i+"] = "+subCommands[i]);
            JMenuItem menuItem = getPopupPresenter(subCommands[i], filesWithInfo,
                                                   inMenu, globalExpertMode);
            //System.out.println("  menu item = "+menuItem);
            if (menuItem != null) menuItems.add(menuItem);
        }
        return (JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(CommandsTree commands, Map filesWithInfo,
                                        boolean inMenu, boolean globalExpertMode) {
        JMenuItem menu;
        //System.out.println("  has Children = "+commands.hasChildren());
        if (commands.hasChildren()) {
            menu = new CommandMenu(commands, filesWithInfo, true, inMenu,
                                   globalExpertMode);
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd == null) return null;
            // TODO expert mode. (Can be a global property ?!?)
            if (cmd.getDisplayName() == null) return null;
            FileObject[] allFiles;
            if (filesWithInfo.size() == 1) {
                allFiles = (FileObject[]) filesWithInfo.keySet().iterator().next();
            } else {
                List files = new ArrayList();
                for (Iterator it = filesWithInfo.keySet().iterator(); it.hasNext(); ) {
                    files.addAll(Arrays.asList((FileObject[]) it.next()));
                }
                allFiles = (FileObject[]) files.toArray(new FileObject[files.size()]);
            }
            if (cmd.getApplicableFiles(allFiles) == null) {
                return null;
                //menu.setEnabled(false);
            }
            menu = CommandMenu.createItem(cmd, globalExpertMode, CommandMenu.DEFAULT_ADVANCED_OPTIONS_SIGN,
                                          inMenu, filesWithInfo);
        }
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
    
    private static CommandsTree mergeProvidedCommands(Map filesByCommandProviders) {
        //CommandsTree root = new CommandsTree(null);
        Iterator it = filesByCommandProviders.keySet().iterator();
        CommandsTree commands = null;
        do {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            if (provider instanceof CommandsTree.Provider) {
                commands = ((CommandsTree.Provider) provider).getCommands();
            }
        } while (commands == null && it.hasNext());
        if (commands == null) return CommandsTree.EMPTY;
        while (it.hasNext()) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            if (provider instanceof CommandsTree.Provider) {
                CommandsTree commands2 = ((CommandsTree.Provider) provider).getCommands();
                commands = mergeCommands(commands, commands2);
            }
        }
        //CommandsTree commands = (CommandsTree) filesByCommandProviders.get(
        return commands;
    }
    
    private static CommandsTree mergeCommands(CommandsTree commands1, CommandsTree commands2) {
        CommandsTree mergedCommands;
        CommandSupport cmdSupp1 = commands1.getCommandSupport();
        CommandSupport cmdSupp2 = commands2.getCommandSupport();
        if (cmdSupp1 == null && cmdSupp2 == null) {
            mergedCommands = new CommandsTree(null);
        } else if (cmdSupp1 == null || cmdSupp2 == null) {
            mergedCommands = null;
        } else {
            CommandSupport cmdSupp3 = new MergedCommandSupport(cmdSupp1, cmdSupp2);
            mergedCommands = new CommandsTree(cmdSupp3);
        }
        if (commands1.hasChildren() && commands2.hasChildren()) {
            CommandsTree[] ch1 = commands1.children();
            CommandsTree[] ch2 = commands2.children();
            for (int i = 0; i < ch1.length && i < ch2.length; i++) {
                CommandsTree child = mergeCommands(ch1[i], ch2[i]);
                if (child != null) mergedCommands.add(child);
            }
        }
        return mergedCommands;
    }
    
    /**
     * Returns a map of providers and the associated files. The associated
     * files are a map of files and appropriate file info (if any).
     */
    private static Map findCommandProvidersForFiles(Map filesWithInfo) {
        //System.out.println("findCommandProvidersForFiles("+filesWithInfo+")");
        Map providers = new HashMap();
        for (Iterator fileLists = filesWithInfo.keySet().iterator(); fileLists.hasNext(); ) {
            //for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
            FileObject[] files = (FileObject[]) fileLists.next();
            Object fileInfo = filesWithInfo.get(files);
            for (int i = 0; i < files.length; i++) {
                FileObject fo = files[i];
                VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
                //System.out.println("  fo = "+fo+" provider = "+provider);
                if (provider != null) {
                    if (providers.containsKey(provider)) {
                        Map infoFiles = (Map) providers.get(provider);
                        List fileList = null;
                        if (infoFiles.values().contains(fileInfo)) {
                            for (Iterator it = infoFiles.keySet().iterator(); it.hasNext(); ) {
                                fileList = (List) it.next();
                                if (fileInfo == null && infoFiles.get(fileList) == null ||
                                    fileInfo != null && fileInfo.equals(infoFiles.get(fileList))) break;
                            }
                        } else {
                            fileList = new ArrayList();
                            infoFiles.put(fileList, fileInfo);
                        }
                        fileList.add(fo);
                    } else {
                        Map infoFiles = new Table();
                        providers.put(provider, infoFiles);
                        List fileList = new ArrayList();
                        fileList.add(fo);
                        infoFiles.put(fileList, fileInfo);
                        //System.out.println("  put("+provider+", "+fileList+")");
                    }
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            Map infoFilesList = (Map) providers.get(provider);
            Map infoFilesArray = new Table();
            for (Iterator it2 = infoFilesList.keySet().iterator(); it2.hasNext(); ) {
                List fileList = (List) it2.next();
                FileObject[] files = (FileObject[]) fileList.toArray(new FileObject[fileList.size()]);
                Object fileInfo = infoFilesList.get(fileList);
                infoFilesArray.put(files, fileInfo);
                //System.out.println("  For provider "+provider+": have files = "+fileList+", with info '"+fileInfo+"'");
            }
            infoFilesList.clear();
            infoFilesList.putAll(infoFilesArray);
        }
        return providers;
    }
    
    private CommandsTree createDefaultCommandsTree(VcsCommandsProvider provider) {
        // TODO
        return new CommandsTree(null);
    }
    
    /**
     * This action does not have a generic performer, since it's composed from all VCS commands.
     * Use actionPerformed() with the command's name as the action command.
     */
    protected void performAction(Node[] nodes) {
    }
    
    /**
     * Execute the command. If the command is CheckInCommand, check for the
     * presence of VcsObjectIntegritySupport is done and AddCommand is performed
     * if necessary. <p>
     *
     * Every command execution, that wish to present the object integrity dialog
     * should go through this method.
     *
     * @param cmd The command to execute.
     * @param files The files to act on. It's necessary to pass the files here
     *        rather than set them directly to the command, because the command
     *        can ignore some files, that would then not be considered for addition.
     * @return The task representing the running command or <code>null</code> when
     *         the command is not executed.
     */
    public static CommandTask executeCommand(Command cmd, FileObject[] files) {
        CommandTask task = null;
        //System.out.println("VcsFSCommandsAction.executeCommand("+cmd+")");
        //System.out.println("   is merged command = "+(cmd instanceof MergedCommandSupport.MergedCommand)+", is check in command = "+(cmd instanceof CheckInCommand));
        boolean customized = true;
        if (!(cmd instanceof MergedCommandSupport.MergedCommand) &&
            (cmd instanceof CheckInCommand)) {
            
            if (files != null && files.length > 0) {
                customized = VcsObjectIntegritySupport.runIntegrityKeeper(files, cmd);
            }
        }
        if (customized) {
            files = cmd.getApplicableFiles(files);
            if (files != null) {
                cmd.setFiles(files);
                customized = VcsManager.getDefault().showCustomizer(cmd);
                if (customized) {
                    task = cmd.execute();
                }
            }
        }
        return task;
    }
    
    /**
     * Get a help context for the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsFSCommandsAction.class);
    }
    
    public javax.swing.Action createContextAwareInstance(Lookup actionContext) {
        return new ContextAwareDelegateAction (this, actionContext);
    }
    
    private static final class MergedCommandSupport extends CommandSupport {
        
        private CommandSupport cmdSupport1;
        private CommandSupport cmdSupport2;
        
        public MergedCommandSupport(CommandSupport cmdSupport1, CommandSupport cmdSupport2) {
            super(getCommonImplInterfaces(cmdSupport1, cmdSupport2));//Command.class);
            this.cmdSupport1 = cmdSupport1;
            this.cmdSupport2 = cmdSupport2;
        }
        
        private static Class[] getCommonImplInterfaces(CommandSupport cmdSupport1, CommandSupport cmdSupport2) {
            Class[] intrf1 = cmdSupport1.createCommand().getClass().getInterfaces();
            Class[] intrf2 = cmdSupport2.createCommand().getClass().getInterfaces();
            List il1 = new ArrayList(Arrays.asList(intrf1));
            List il2 = Arrays.asList(intrf2);
            il1.retainAll(il2);
            il1.add(MergedCommand.class);
            return (Class[]) il1.toArray(new Class[il1.size()]);
        }
        
        /** Transferes the values of getters of this command to setters of the sub-command */
        private static void transferPropertyValues(Command cmd, Command subCommand) {
            Class[] interfaces = cmd.getClass().getInterfaces();
            transferPropertyValues(cmd, interfaces, subCommand);
        }
        
        private static void transferPropertyValues(Command cmd, Class[] interfaces,
                                                   Command subCommand) {
            for (int i = 0; i < interfaces.length; i++) {
                if (Command.class.equals(interfaces[i]) ||
                    VcsDescribedCommand.class.equals(interfaces[i])) continue;
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(interfaces[i]);
                    PropertyDescriptor[] propDescrs = beanInfo.getPropertyDescriptors();
                    for (int j = 0; j < propDescrs.length; j++) {
                        String name = propDescrs[j].getName();
                        Object value = propDescrs[j].getReadMethod().invoke(cmd, new Object[0]);
                        Method writeMethod = propDescrs[j].getWriteMethod();
                        Method subWriteMethod = subCommand.getClass().getMethod(writeMethod.getName(), writeMethod.getParameterTypes());
                        subWriteMethod.invoke(subCommand, new Object[] { value });
                    }
                } catch (IntrospectionException iex) {
                } catch (IllegalAccessException iaex) {
                } catch (IllegalArgumentException iarex) {
                } catch (InvocationTargetException itex) {
                } catch (NoSuchMethodException nsmex) {
                }
                Class[] subinterfaces = interfaces[i].getInterfaces();
                transferPropertyValues(cmd, subinterfaces, subCommand);
            }
        }
        
        /** Perform the actual execution of the command from the provided info.
         * This method might be called multiple times and even concurrently
         * for a single CommandSupport instance. It's recommended not to
         * modify any variables from this instance object in this method.
         */
        protected int execute(CommandTask task) {
            Command cmd = getCommand(task);
            FileObject[] files = cmd.getFiles();
            //System.out.println("MergedCommandSupport.execute("+task+"): cmd = "+cmd+", files = "+files);
            if (files == null) return CommandTask.STATUS_SUCCEEDED;
            FileObject[] files1 = cmdSupport1.getApplicableFiles(files);
            FileObject[] files2 = cmdSupport2.getApplicableFiles(files);
            CommandTask task1 = null;
            CommandTask task2 = null;
            //System.out.println("  files1 = "+files1+", files2 = "+files2);
            if (files1 != null) {
                Command cmd1 = cmdSupport1.createCommand();
                transferPropertyValues(cmd, cmd1);
                cmd1.setGUIMode(cmd.isGUIMode());
                cmd1.setExpertMode(cmd.isExpertMode());
                task1 = executeCommand(cmd1, files1);
            }
            if (files2 != null) {
                Command cmd2 = cmdSupport2.createCommand();
                transferPropertyValues(cmd, cmd2);
                cmd2.setGUIMode(cmd.isGUIMode());
                cmd2.setExpertMode(cmd.isExpertMode());
                task2 = executeCommand(cmd2, files2);
            }
            try {
                if (task1 != null) task1.waitFinished(0);
                if (task2 != null) task2.waitFinished(0);
            } catch (InterruptedException intex) {
                if (task1 != null) task1.stop();
                if (task2 != null) task2.stop();
                return CommandTask.STATUS_INTERRUPTED;
            }
            boolean success = ((task1 != null) ? task1.getExitStatus() == CommandTask.STATUS_SUCCEEDED : true) &&
                              ((task2 != null) ? task2.getExitStatus() == CommandTask.STATUS_SUCCEEDED : true);
            return success ? CommandTask.STATUS_SUCCEEDED : CommandTask.STATUS_FAILED;
        }
        
        /** Find, whether this command can act on a set of files.
         * @param files The array of files to inspect
         * @return an array of files the command can act on or <code>null</code> when
         * it can not act on any file listed.
         */
        public FileObject[] getApplicableFiles(FileObject[] files) {
            FileObject[] fo1 = cmdSupport1.getApplicableFiles(files);
            FileObject[] fo2 = cmdSupport2.getApplicableFiles(files);
            if (fo1 == null) return fo2;
            if (fo2 == null) return fo1;
            //FileObject[] fo3 = new FileObject[fo1.length + fo2.length];
            //System.arraycopy(fo1, 0, fo3, 0, fo1.length);
            //System.arraycopy(fo2, 0, fo3, fo1.length, fo2.length);
            List fos1 = Arrays.asList(fo1);
            List fos2 = new ArrayList(Arrays.asList(fo2));
            fos2.removeAll(fos1);
            fos2.addAll(0, fos1);
            FileObject[] fo3 = (FileObject[]) fos2.toArray(new FileObject[fos2.size()]);
            return fo3;
        }
        
        /** Get the display name of the command. It will be visible on the popup menu under this name.
         * When <code>null</code>, the command will not be visible on the popup menu.
         *
         */
        public String getDisplayName() {
            return cmdSupport1.getDisplayName();
        }
        
        /** Get the name of the command.
         *
         */
        public String getName() {
            return cmdSupport1.getName()+"-"+cmdSupport2.getName();
        }
        
        /** Whether the command supports an expert mode. The command should provide
         * a more complex customizer and/or output if in expert mode. If the
         * command does not differentiate expert mode, it should declare, that
         * it does not have an expert mode.
         * @return true If the command differentiate expert mode, false otherwise
         *
         */
        public boolean hasExpertMode() {
            return cmdSupport1.hasExpertMode() && cmdSupport2.hasExpertMode();
        }
        
        protected Object clone() {
            return new MergedCommandSupport(cmdSupport1, cmdSupport2);
        }
        
        /**
         * Just a marker interface, that the command is the result of a merge
         * of two other commands.
         */
        public static interface MergedCommand {
        }
        
    }

}
