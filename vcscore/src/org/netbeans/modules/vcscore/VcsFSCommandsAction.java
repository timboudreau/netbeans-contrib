/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openide.awt.Actions;
import org.openide.awt.JInlineMenu;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.actions.CommandMenu;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.grouping.VcsGroupNode;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.WeakList;

/**
 * The system action with VCS commands, that are provided by the FileSystem.
 * @author Martin Entlicher
 */
public class VcsFSCommandsAction extends NodeAction implements ActionListener {
    
    protected Collection selectedFileObjects = null;
    //protected CommandsTree actionCommandsTree = null;
    // The latest map of providers and associated array of files
    private Map filesByCommandProviders;

    private static final String advancedOptionsSign = org.openide.util.NbBundle.getMessage(VcsFSCommandsAction.class, "CTL_AdvancedOptionsSign");
    // List of commands, that can switch the expert mode on/off
    private List switchableList;
    boolean CTRL_Down = false;

    private static final long serialVersionUID = -4196511763565479366L;
    
    /** Creates a new instance of VcsFSCommandsAction */
    public VcsFSCommandsAction() {
    }
    
    public void setSelectedFileObjects(Collection fos) {
        //variablesForSelectedFiles = null;
        if (fos == null) {
            this.selectedFileObjects = getSelectedFileObjectsFromActiveNodes();
            return ;
        }
        this.selectedFileObjects = new WeakList(fos);
    }

    private Collection getSelectedFileObjectsFromActiveNodes() {
        ArrayList files = new ArrayList();
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof VcsGroupNode) {
                VcsGroupNode grNode = (VcsGroupNode) nodes[i];
                /*
                if (variablesForSelectedFiles == null) variablesForSelectedFiles = new HashMap();
                Hashtable additionalVars = new Hashtable();
                additionalVars.put(Variables.GROUP_NAME, grNode.getDisplayName());
                String description = grNode.getShortDescription();
                if (description != null) {
                    additionalVars.put(Variables.GROUP_DESCRIPTION, description);
                }
                 */
                WeakList varFiles = new WeakList();
                Enumeration children = nodes[i].getChildren().nodes();
                while (children.hasMoreElements()) {
                    Node nd = (Node) children.nextElement();
                    DataObject dd = (DataObject) nd.getCookie(DataObject.class);
                    if (dd == null) continue;
                    FileObject primary = dd.getPrimaryFile();
                    files.addAll(dd.files());
                    varFiles.addAll(dd.files());
                    /*
                    variablesForSelectedFiles.put(additionalVars, varFiles);
                     */
                }
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd == null) continue;
                files.addAll(dd.files());
            }
        }
        return new WeakList(files);
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
            if (nodes[i] instanceof VcsGroupNode) {
                continue;
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                //if (dd == null) System.out.println("  Node "+nodes[i]+" does not have DataObject !!");
                if (dd == null) return false;
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
        Collection fileObjects = selectedFileObjects;
        if (fileObjects == null) {
            fileObjects = getSelectedFileObjectsFromActiveNodes();
        }
        //System.out.println("VcsFSCommandsAction.getPresenter(): selected FileObjects: "+fileObjects);
        switchableList = new ArrayList();
        ArrayList menuItems = new ArrayList();
        //CommandsTree[] commands = actionCommandsTree.children();
        filesByCommandProviders = findCommandProvidersForFiles(fileObjects);
	//System.out.println("filesByCommandProviders.size() = "+filesByCommandProviders.size());
        if (filesByCommandProviders.size() == 0) return new JInlineMenu(); // return empty JInlineMenu
        CommandsTree commands;
	//VcsCommandsProvider provider = null;
        if (filesByCommandProviders.size() == 1) {
            VcsCommandsProvider provider = (VcsCommandsProvider) filesByCommandProviders.keySet().iterator().next();
            //List files = (List) filesByCommandProviders.get(provider);
            if (provider instanceof CommandsTree.Provider) {
                commands = ((CommandsTree.Provider) provider).getCommands();
            } else {
                commands = createDefaultCommandsTree(provider);
            }
        } else {
            // it's necessary to get commands of known classes from all
            // providers or somehow merge together the common commands.
            commands = mergeProvidedCommands(filesByCommandProviders);
        }
        JInlineMenu menu = new JInlineMenu();
        if (commands == null) return menu;
        FileObject[] files = (FileObject[]) fileObjects.toArray(new FileObject[fileObjects.size()]);
        menu.setMenuItems(createMenuItems(commands, files, inMenu));
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
    private JMenuItem[] createMenuItems(CommandsTree commands, FileObject[] files, boolean inMenu) {
        ArrayList menuItems = new ArrayList();
        CommandsTree[] subCommands = commands.children();
        for (int i = 0; i < subCommands.length; i++) {
            //System.out.println("GlobAction.getPresenter() subCommands["+i+"] = "+subCommands[i]);
            JMenuItem menuItem = getPopupPresenter(subCommands[i], files, inMenu);
            //System.out.println("  menu item = "+menuItem);
            if (menuItem != null) menuItems.add(menuItem);
        }
        return (JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(CommandsTree commands, FileObject[] files, boolean inMenu) {
        JMenuItem menu;
        //System.out.println("  has Children = "+commands.hasChildren());
        if (commands.hasChildren()) {
            menu = new CommandMenu(commands, files, true, inMenu);
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd == null) return null;
            // TODO expert mode. (Can be a global property ?!?)
            if (cmd.getDisplayName() == null) return null;
            if (cmd.getApplicableFiles(files) == null) {
                return null;
                //menu.setEnabled(false);
            }
            menu = CommandMenu.createItem(cmd, false, CommandMenu.DEFAULT_ADVANCED_OPTIONS_SIGN, inMenu, files);
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
     * Returns a map of providers and the list of associated files.
     */
    private static Map findCommandProvidersForFiles(Collection fileObjects) {
	//System.out.println("findCommandProvidersForFiles("+fileObjects+")");
        Map providers = new Table();//HashMap();
        for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
	    //System.out.println("  fo = "+fo+" provider = "+provider);
            if (provider != null) {
                if (providers.containsKey(provider)) {
                    List files = (List) providers.get(provider);
                    files.add(fo);
                } else {
                    List files = new ArrayList();
                    files.add(fo);
		    //System.out.println("  put("+provider+", "+files+")");
                    providers.put(provider, files);
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            FileObject[] files = (FileObject[]) ((List) providers.get(provider)).toArray(new FileObject[0]);
            //System.out.println("  RE put("+provider+", "+files+")");
            providers.put(provider, files);
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
            super(Command.class);
            this.cmdSupport1 = cmdSupport1;
            this.cmdSupport2 = cmdSupport2;
        }
        
        /** Perform the actual execution of the command from the provided info.
         * This method might be called multiple times and even concurrently
         * for a single CommandSupport instance. It's recommended not to
         * modify any variables from this instance object in this method.
         */
        protected int execute(CommandTask task) {
            Command cmd = getCommand(task);
            FileObject[] files1 = cmdSupport1.getApplicableFiles(cmd.getFiles());
            FileObject[] files2 = cmdSupport2.getApplicableFiles(cmd.getFiles());
            CommandTask task1 = null;
            CommandTask task2 = null;
            if (files1 != null) {
                Command cmd1 = cmdSupport1.createCommand();
                cmd1.setGUIMode(cmd.isGUIMode());
                cmd1.setExpertMode(cmd.isExpertMode());
                boolean customized = VcsManager.getDefault().showCustomizer(cmd1);
                if (customized) task1 = cmd1.execute();
            }
            if (files2 != null) {
                Command cmd2 = cmdSupport2.createCommand();
                cmd2.setGUIMode(cmd.isGUIMode());
                cmd2.setExpertMode(cmd.isExpertMode());
                boolean customized = VcsManager.getDefault().showCustomizer(cmd2);
                if (customized) task2 = cmd2.execute();
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
            FileObject[] fo3 = new FileObject[fo1.length + fo2.length];
            System.arraycopy(fo1, 0, fo3, 0, fo1.length);
            System.arraycopy(fo2, 0, fo3, fo1.length, fo2.length);
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
        
    }

}
