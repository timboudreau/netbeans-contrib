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
import org.openide.util.actions.NodeAction;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.spi.vcs.VcsCommandsProvider;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.grouping.VcsGroupNode;
import org.netbeans.modules.vcscore.util.WeakList;
import org.openide.util.RequestProcessor;

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
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof VcsGroupNode) {
                continue;
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd == null) return false;
                FileObject primary = dd.getPrimaryFile();
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
        switchableList = new ArrayList();
        JInlineMenu inlineMenu = new JInlineMenu();
        ArrayList menuItems = new ArrayList();
        //CommandsTree[] commands = actionCommandsTree.children();
        filesByCommandProviders = findCommandProvidersForFiles(fileObjects);
        if (filesByCommandProviders.size() == 1) {
            VcsCommandsProvider provider = (VcsCommandsProvider) filesByCommandProviders.keySet().iterator().next();
            //List files = (List) filesByCommandProviders.get(provider);
            CommandsTree commands;
            if (provider instanceof CommandsTree.Provider) {
                commands = ((CommandsTree.Provider) provider).getCommands();
            } else {
                commands = createDefaultCommandsTree(provider);
            }
            CommandsTree[] subCommands = commands.children();
            for (int i = 0; i < subCommands.length; i++) {
                JMenuItem menuItem = getPopupPresenter(subCommands[i], provider, inMenu);
                if (menuItem != null) menuItems.add(menuItem);
            }
        } else {
            // TODO it's necessary to get commands of known classes from all
            //      providers or somehow merge together the common commands.
        }
        inlineMenu.setMenuItems((JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]));
        return inlineMenu;
    }
    
    /**
     * Returns a map of providers and the list of associated files.
     */
    private static Map findCommandProvidersForFiles(Collection fileObjects) {
        Map providers = new HashMap();
        for (Iterator it = fileObjects.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
            if (provider != null) {
                if (providers.containsKey(provider)) {
                    List files = (List) providers.get(provider);
                    files.add(fo);
                } else {
                    List files = new ArrayList();
                    files.add(fo);
                    providers.put(provider, files);
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            FileObject[] files = (FileObject[]) ((List) providers.get(provider)).toArray(new FileObject[0]);
            providers.put(provider, files);
        }
        return providers;
    }
    
    private CommandsTree createDefaultCommandsTree(VcsCommandsProvider provider) {
        // TODO
        return new CommandsTree(null);
    }
    
    /**
     * Create the command menu item.
     * @param cmd the command
     */
    private static JMenuItem createItem(CommandSupport cmd, boolean expertMode,
                                        List switchableList, String advancedOptionsSign,
                                        boolean inMenu, ActionListener listener) {
        String label = cmd.getDisplayName();
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        if (label == null) return null;
        boolean hasExpert = cmd.hasExpertMode();
        if (hasExpert && expertMode) label += advancedOptionsSign;
        JMenuItem item = new JMenuItem();
        Actions.setMenuText(item, label, inMenu);
        /*
        if (inMenu) {
            char mnemonic = cmd.getDisplayedMnemonic();
            if (mnemonic != '\0') {
                item.setMnemonic(mnemonic);
            }
        }
         */
        item.setActionCommand(cmd.getName());
        item.addActionListener(listener);
        if (hasExpert && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     */
    private JMenuItem getPopupPresenter(CommandsTree commands, VcsCommandsProvider provider, boolean inMenu) {
        JMenuItem menu;
        FileObject[] files = (FileObject[]) filesByCommandProviders.get(provider);
        if (commands.hasChildren()) {
            menu = new VcsFSCommandsAction.CommandMenu(commands, files, switchableList,
                                                       advancedOptionsSign, true,
                                                       inMenu, this);
            if (inMenu) {
                menu.setIcon(getIcon());
            }
            menu.addMenuKeyListener(new CtrlMenuKeyListener());
            ((JMenu) menu).addMenuListener(new javax.swing.event.MenuListener() {
                public void menuDeselected(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu deselected");
                }
                public void menuCanceled(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu canceled");
                }
                public void menuSelected(javax.swing.event.MenuEvent e) {
                    deselectedMenu();
                    //                System.out.println("Selected menu");
                }
            });
        } else {
            CommandSupport cmd = commands.getCommandSupport();
            if (cmd == null) return null;
            // TODO expert mode. (Can be a global property ?!?)
            if (cmd.getDisplayName() == null) return null;
            if (cmd.getApplicableFiles(files) == null) {
                return null;
                //menu.setEnabled(false);
            }
            menu = createItem(cmd, false, switchableList, advancedOptionsSign, inMenu, this);
        }
        if (inMenu && menu != null) {
            menu.setIcon(getIcon());
        }
        return menu;
    }
    
    /**
     * This action does not have a generic performer, since it's composed from all VCS commands.
     * Use actionPerformed() with the command's name as the action command.
     */
    protected void performAction(Node[] nodes) {
    }
    
    public void actionPerformed(final java.awt.event.ActionEvent e) {
        final String cmdName = e.getActionCommand();
        final FileObject[] files = (FileObject[]) getSelectedFileObjectsFromActiveNodes().toArray(new FileObject[0]);
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                Command cmd = null;
                try {
                    cmd = VcsManager.getDefault().createCommand(cmdName, files);
                } catch (IllegalArgumentException iaex) {}
                if (cmd != null) {
                    cmd.setGUIMode(true);
                    if (CTRL_Down) cmd.setExpertMode(CTRL_Down);
                    boolean customized = VcsManager.getDefault().showCustomizer(cmd);
                    if (customized) cmd.execute();
                }
                /*
                for (Iterator it = filesByCommandProviders.keySet().iterator(); it.hasNext(); ) {
                    VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
                    Command cmd = provider.createCommand(cmdName);
                    if (cmd != null) {
                        cmd.setFiles((FileObject[]) filesByCommandProviders.get(provider));
                        cmd.setGUIMode(true);
                        if (CTRL_Down) cmd.setExpertMode(CTRL_Down);
                        cmd.execute();
                    }
                }
                 */
            }
        });
    }
    
    /**
     * Get a help context for the action.
     * @return the help context for this action
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(VcsFSCommandsAction.class);
    }
    
    private class CtrlMenuKeyListener implements javax.swing.event.MenuKeyListener {
        public void menuKeyTyped(javax.swing.event.MenuKeyEvent p1) {
        }
        public void menuKeyPressed(javax.swing.event.MenuKeyEvent p1) {
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || p1.isControlDown(); // NOI18N
//            System.out.println("key pressed=" + newCTRL_Down);
//            System.out.println("is down=" + p1.isControlDown());
            changeCtrlSigns(newCTRL_Down);
            CTRL_Down = newCTRL_Down;
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent p1) {
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || !p1.isControlDown(); // NOI18N
//            System.out.println("key Released=" + newCTRL_Down);
//            System.out.println("keykode=" + p1.getKeyText(p1.getKeyCode()));
            changeCtrlSigns(!newCTRL_Down);
            CTRL_Down = !newCTRL_Down;
        }
    }

    private void deselectedMenu() {
        changeCtrlSigns(false);
        CTRL_Down = false;
    }   

    private void changeCtrlSigns(boolean newValue) {
        if (newValue == CTRL_Down) return;
        Iterator it = switchableList.iterator();
        while (it.hasNext()) {
            JMenuItem item = (JMenuItem)it.next();
            String text = item.getText();
            if (newValue) {
                // do turn ctrl sign on
                if (!text.endsWith(advancedOptionsSign)) {text = text + advancedOptionsSign;}
            }   else { 
                // turn it off - ctrl released
                if (text.endsWith(advancedOptionsSign)) {text = text.substring(0, text.length() - advancedOptionsSign.length());}
            }    
            item.setText(text);
        }    
    }
    
    /**
     * The menu with VCS commands constructed lazily.
     */
    private static class CommandMenu extends JMenuPlus {

        private CommandsTree commandRoot;
        //private boolean onRoot;
        //private boolean onFile;
        //private boolean onDir;
        //private VcsFileSystem fileSystem;
        //private Set statuses;
        private FileObject[] files;
        private List switchableList;
        private String advancedOptionsSign;
        private boolean removeDisabled;
        private boolean inMenu;
        private ActionListener listener;
        private boolean popupCreated = false;
        
        public CommandMenu(CommandsTree commandRoot, FileObject[] files,
                           List switchableList, String advancedOptionsSign,
                           boolean removeDisabled, boolean inMenu,
                           ActionListener listener) {
            super();
            this.commandRoot = commandRoot;
            this.files = files;
            //this.onRoot = onRoot;
            //this.onFile = onFile;
            //this.onDir = onDir;
            //this.fileSystem = fileSystem;
            //this.statuses = statuses;
            this.switchableList = switchableList;
            this.advancedOptionsSign = advancedOptionsSign;
            this.removeDisabled = removeDisabled;
            this.inMenu = inMenu;
            this.listener = listener;
            CommandSupport cmd = (CommandSupport) commandRoot.getCommandSupport();
            if (cmd != null) {
                Actions.setMenuText(this, cmd.getDisplayName(), inMenu);
                /*
                setText(cmd.getDisplayName());
                if (inMenu) {
                    char mnemonic = cmd.getDisplayedMnemonic();
                    if (mnemonic != (char) 0) {
                        setMnemonic(mnemonic);
                    }
                }
                 */
            }
        }
        
        /** Overrides superclass method. Adds lazy popup menu creation
          * if it is necessary. */
        public JPopupMenu getPopupMenu() {
            if (!popupCreated) createPopup();
            return super.getPopupMenu();
        }
        
        private void createPopup() {
            boolean wasSeparator = false;
            boolean wasNullCommand = false;
            //Hashtable variables = fileSystem.getVariablesAsHashtable();
            CommandsTree[] children = commandRoot.children();
            for (int i = 0; i < children.length; i++) {
                CommandSupport cmd = (CommandSupport) children[i].getCommandSupport();
                if (cmd == null) {
                    // an extra check to not allow more separators, than appropriate
                    if (!wasSeparator || wasNullCommand) {
                        addSeparator();
                    }
                    wasSeparator = true;
                    wasNullCommand = true;
                    continue;
                }
                wasNullCommand = false;
                //System.out.println("VcsAction.addMenu(): cmd = "+cmd.getName());
                if (cmd.getDisplayName() == null) continue;
                if (removeDisabled && cmd.getApplicableFiles(files) == null) {
                    continue;
                }
                JMenuItem cmdMenu = createItem(cmd, false, switchableList, advancedOptionsSign, inMenu, listener);
                if (cmdMenu == null) continue;
                wasSeparator = false;
                JMenuItem item;
                if (children[i].hasChildren()) {
                    JMenu submenu;
                    submenu = new VcsFSCommandsAction.CommandMenu(children[i], files,
                                                        switchableList, advancedOptionsSign,
                                                        removeDisabled, inMenu, listener);
                    add(submenu);
                    item = submenu;
                } else {
                    item = cmdMenu;
                    //                item.addMenuKeyListener(ctrlListener);
                    add(item);
                }
                if (!removeDisabled && cmd.getApplicableFiles(files) == null) {
                    item.setEnabled(false);
                }
            }
            popupCreated = true;
        }

    }

}
