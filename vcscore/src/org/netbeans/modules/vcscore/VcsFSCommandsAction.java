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
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.grouping.GroupCookie;
import org.netbeans.modules.vcscore.objectintegrity.VcsObjectIntegritySupport;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.WeakList;
import org.openide.filesystems.FileSystem;

/**
 * The system action with VCS commands, that are provided by the FileSystem.
 * @author Martin Entlicher
 */
public class VcsFSCommandsAction extends NodeAction implements ActionListener {
    
    protected Collection selectedFileObjects = null;
    //protected CommandsTree actionCommandsTree = null;
    // The latest map of providers and associated map of files with the message
    private Map filesByCommandProviders;

    // List of commands, that can switch the expert mode on/off
    private List switchableList;
    boolean CTRL_Down = false;

    private static final long serialVersionUID = -4196511763565479366L;
    
    /** Creates a new instance of VcsFSCommandsAction */
    public VcsFSCommandsAction() {
    }
    
    /**
     * @return a map of array of FileObjects and their messages if any.
     */
    private Map getSelectedFileObjectsFromActiveNodes() {
        Map filesWithMessages = new Table();
        ArrayList files = new ArrayList();
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            GroupCookie gc = (GroupCookie) nodes[i].getCookie(GroupCookie.class);
            if (gc != null) {
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
                    //messageFiles.addAll(dd.files());
                    //addAllWorkaround(dd.files(), messageFiles);
                    addAllFromSingleFS(dd.getPrimaryFile(), dd.files(), messageFiles);
                    /*
                    variablesForSelectedFiles.put(additionalVars, varFiles);
                     */
                }
                filesWithMessages.put(messageFiles.toArray(new FileObject[0]), message);
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd == null) continue;
                if (dd instanceof DataShadow) {
                    // We want to have the same VCS actions on the link as on the original.
                    dd = ((DataShadow) dd).getOriginal();
                }
                //files.addAll(dd.files());
                //addAllWorkaround(dd.files(), files);
                addAllFromSingleFS(dd.getPrimaryFile(), dd.files(), files);
            }
        }
        if (files.size() > 0) filesWithMessages.put(files.toArray(new FileObject[0]), null);
        return filesWithMessages;
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
    protected boolean enable(Node[] nodes) {
        //System.out.println("VcsFSCommandsAction.enable("+nodes.length+")");
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getCookie(GroupCookie.class) != null) {
                continue;
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
        return (nodes.length > 0);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     */
    public JMenuItem getMenuPresenter() {
        return getPresenter(true);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    public JMenuItem getPopupPresenter() {
        return getPresenter(false);
    }
    
    private JMenuItem getPresenter(boolean inMenu) {
        JInlineMenu menu = new JInlineMenu();
        JMenuItem[] items = createMenuItems(inMenu);
        if (items.length == 0) return menu;
        menu.setMenuItems(items);
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
    public JMenuItem[] createMenuItems(boolean inMenu) {
        Map filesWithMessages = getSelectedFileObjectsFromActiveNodes();
        //System.out.println("VcsFSCommandsAction.getPresenter(): selected filesWithMessages: "+filesWithMessages);
        switchableList = new ArrayList();
        ArrayList menuItems = new ArrayList();
        //CommandsTree[] commands = actionCommandsTree.children();
        filesByCommandProviders = findCommandProvidersForFiles(filesWithMessages);
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
        return createMenuItems(commands, filesWithMessages, inMenu, globalExpertMode);
    }
    
    private JMenuItem[] createMenuItems(CommandsTree commands, Map filesWithMessages,
                                        boolean inMenu, boolean globalExpertMode) {
        ArrayList menuItems = new ArrayList();
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            //System.out.println("GlobAction.getPresenter() subCommands["+i+"] = "+subCommands[i]);
            JMenuItem menuItem = getPopupPresenter(subCommands[i], filesWithMessages,
                                                   inMenu, globalExpertMode);
            //System.out.println("  menu item = "+menuItem);
            if (menuItem != null) menuItems.add(menuItem);
        }
        return (JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(CommandsTree commands, Map filesWithMessages,
                                        boolean inMenu, boolean globalExpertMode) {
        JMenuItem menu;
        //System.out.println("  has Children = "+commands.hasChildren());
        if (commands.hasChildren()) {
            menu = new CommandMenu(commands, filesWithMessages, true, inMenu,
                                   globalExpertMode);
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd == null) return null;
            // TODO expert mode. (Can be a global property ?!?)
            if (cmd.getDisplayName() == null) return null;
            FileObject[] allFiles;
            if (filesWithMessages.size() == 1) {
                allFiles = (FileObject[]) filesWithMessages.keySet().iterator().next();
            } else {
                List files = new ArrayList();
                for (Iterator it = filesWithMessages.keySet().iterator(); it.hasNext(); ) {
                    files.addAll(Arrays.asList((FileObject[]) it.next()));
                }
                allFiles = (FileObject[]) files.toArray(new FileObject[files.size()]);
            }
            if (cmd.getApplicableFiles(allFiles) == null) {
                return null;
                //menu.setEnabled(false);
            }
            menu = CommandMenu.createItem(cmd, globalExpertMode, CommandMenu.DEFAULT_ADVANCED_OPTIONS_SIGN,
                                          inMenu, filesWithMessages);
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
     * files are a map of files and appropriate messages (if any).
     */
    private static Map findCommandProvidersForFiles(Map filesWithMessages) {
        //System.out.println("findCommandProvidersForFiles("+filesWithMessages+")");
        Map providers = new HashMap();
        for (Iterator fileLists = filesWithMessages.keySet().iterator(); fileLists.hasNext(); ) {
            //for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
            FileObject[] files = (FileObject[]) fileLists.next();
            String message = (String) filesWithMessages.get(files);
            for (int i = 0; i < files.length; i++) {
                FileObject fo = files[i];
                VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
                //System.out.println("  fo = "+fo+" provider = "+provider);
                if (provider != null) {
                    if (providers.containsKey(provider)) {
                        Map msgFiles = (Map) providers.get(provider);
                        List fileList = null;
                        if (msgFiles.values().contains(message)) {
                            for (Iterator it = msgFiles.keySet().iterator(); it.hasNext(); ) {
                                fileList = (List) it.next();
                                if (message == null && msgFiles.get(fileList) == null ||
                                    message != null && message.equals(msgFiles.get(fileList))) break;
                            }
                        } else {
                            fileList = new ArrayList();
                            msgFiles.put(fileList, message);
                        }
                        fileList.add(fo);
                    } else {
                        Map msgFiles = new Table();
                        providers.put(provider, msgFiles);
                        List fileList = new ArrayList();
                        fileList.add(fo);
                        msgFiles.put(fileList, message);
                        //System.out.println("  put("+provider+", "+fileList+")");
                    }
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            Map msgFilesList = (Map) providers.get(provider);
            Map msgFilesArray = new Table();
            for (Iterator it2 = msgFilesList.keySet().iterator(); it2.hasNext(); ) {
                List fileList = (List) it2.next();
                FileObject[] files = (FileObject[]) fileList.toArray(new FileObject[fileList.size()]);
                String message = (String) msgFilesList.get(fileList);
                msgFilesArray.put(files, message);
                //System.out.println("  For provider "+provider+": have files = "+fileList+", with message '"+message+"'");
            }
            msgFilesList.clear();
            msgFilesList.putAll(msgFilesArray);
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
     * @return The task representing the running command.
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
            cmd.setFiles(files);
            customized = VcsManager.getDefault().showCustomizer(cmd);
            if (customized) {
                task = cmd.execute();
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
