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

package org.netbeans.modules.vcscore.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuKeyListener;

import org.openide.awt.Actions;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.api.vcs.commands.MessagingCommand;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.VcsFSCommandsAction;
import org.netbeans.modules.vcscore.commands.CommandsTree;

/**
 * The menu with VCS commands constructed lazily.
 *
 * @author  Martin Entlicher
 */
public class CommandMenu extends JMenuPlus {
    
    public static final String DEFAULT_ADVANCED_OPTIONS_SIGN =
        org.openide.util.NbBundle.getMessage(CommandMenu.class, "CTL_AdvancedOptionsSign");
    
    private CommandsTree commandRoot;
    private Map filesWithMessages;
    private List switchableList;
    private String advancedOptionsSign;
    private boolean removeDisabled;
    private boolean inMenu;
    private boolean globalExpertMode;
    private ActionListener listener;
    private Map actionCommandMap;
    private boolean popupCreated = false;
    /** two elements: 1) whether CTRL was pressed, 2) expertMode */
    private boolean[] CTRL_Down;

    /**
     * Creates a new instance of CommandMenu.
     * @param commandRoot The root of the tree structure of commands.
     * @param filesWithMessages The map of array of FileObjects and the messages,
     *        that are usedin MessagingCommands
     * @param removeDisabled Remove the disabled commands from the popup menu
     * @param inMenu Whether a menu or a popup-menu should be created.
     */
    public CommandMenu(CommandsTree commandRoot, Map filesWithMessages,
                       boolean removeDisabled, boolean inMenu,
                       boolean globalExpertMode) {
        this(commandRoot, filesWithMessages, DEFAULT_ADVANCED_OPTIONS_SIGN,
             removeDisabled, inMenu, globalExpertMode, null, new HashMap());
    }
    
    private CommandMenu(CommandsTree commandRoot, Map filesWithMessages,
                       String advancedOptionsSign,
                       boolean removeDisabled, boolean inMenu, boolean globalExpertMode,
                       ActionListener listener, Map actionCommandMap) {
        this(commandRoot, filesWithMessages, new ArrayList(), advancedOptionsSign,
             removeDisabled, inMenu, globalExpertMode, listener, actionCommandMap,
             new boolean[] { false, false }); // two elements: 1) whether CTRL was pressed, 2) expertMode
    }
    
    private CommandMenu(CommandsTree commandRoot, Map filesWithMessages,
                        List switchableList, String advancedOptionsSign,
                        boolean removeDisabled, boolean inMenu, boolean globalExpertMode,
                        ActionListener listener, Map actionCommandMap, boolean[] CTRL_Down) {
        super();
        this.commandRoot = commandRoot;
        this.filesWithMessages = filesWithMessages;
        this.switchableList = switchableList;
        this.advancedOptionsSign = advancedOptionsSign;
        this.removeDisabled = removeDisabled;
        this.inMenu = inMenu;
        this.globalExpertMode = globalExpertMode;
        this.CTRL_Down = CTRL_Down;
        CTRL_Down[1] = globalExpertMode;
        this.listener = (listener != null) ?
                         listener :
                         new CommandMenu.CommandActionListener(CTRL_Down, actionCommandMap,
                                                               filesWithMessages);
        this.actionCommandMap = actionCommandMap;
        CommandSupport cmd = (CommandSupport) commandRoot.getCommandSupport();
        if (cmd != null) {
            Actions.setMenuText(this, cmd.getDisplayName(), inMenu);
        }
        if (advancedOptionsSign != null) {
            this.addMenuKeyListener(new CtrlMenuKeyListener(CTRL_Down, switchableList,
                                                            advancedOptionsSign));
        }
    }

    /** Overrides superclass method. Adds lazy popup menu creation
      * if it is necessary. */
    public JPopupMenu getPopupMenu() {
        if (!popupCreated) createPopup();
        return super.getPopupMenu();
    }

    private void createPopup() {
        boolean wasSeparator = true; // In order not to add the separator at the beginning
        boolean willAddSeparator = false; // Whether a separator should be added before the command
        CommandsTree[] children = commandRoot.children();
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
        Map multiCommandsByDisplayName = getMultiCommandsByDisplayName(children, allFiles);
        Set addedDisplayNames = new HashSet();
        for (int i = 0; i < children.length; i++) {
            CommandSupport cmd = (CommandSupport) children[i].getCommandSupport();
            if (cmd == null) {
                // an extra check to not allow more separators, than appropriate
                if (!wasSeparator) {
                    willAddSeparator = true;
                    wasSeparator = true;
                }
                continue;
            }
            String displayName = cmd.getDisplayName();
            //System.out.println("VcsAction.addMenu(): cmd = "+cmd.getName());
            if (displayName == null) continue;
            if (removeDisabled && cmd.getApplicableFiles(allFiles) == null) {
                continue;
            }
            if (willAddSeparator) {
                // Add the separator only when a non-null command follows
                addSeparator();
                willAddSeparator = false;
            }
            wasSeparator = false;           
            JMenuItem item;
            if (children[i].hasChildren()) {
                JMenu submenu;
                submenu = new CommandMenu(children[i], filesWithMessages,
                                          switchableList, advancedOptionsSign,
                                          removeDisabled, inMenu, globalExpertMode,
                                          listener, actionCommandMap, CTRL_Down);
                add(submenu);
                item = submenu;
            } else {
                if (addedDisplayNames.contains(displayName) && multiCommandsByDisplayName.containsKey(displayName)) {
                    continue;
                }
                //item = cmdMenu;
                //                item.addMenuKeyListener(ctrlListener);
                List cmdList = (List) multiCommandsByDisplayName.get(cmd.getDisplayName());
                if (cmdList != null) {
                    item = createItem((CommandSupport[]) cmdList.toArray(new CommandSupport[cmdList.size()]),
                                      displayName, globalExpertMode,
                                      switchableList, advancedOptionsSign, inMenu,
                                      listener, actionCommandMap);
                    addedDisplayNames.add(displayName);
                } else {
                    item = createItem(cmd, globalExpertMode, switchableList,
                                      advancedOptionsSign, inMenu,
                                      listener, actionCommandMap);
                    if (item == null) continue;
                }
                add(item);
            }
            if (!removeDisabled && cmd.getApplicableFiles(allFiles) == null) {
                item.setEnabled(false);
            }
        }
        popupCreated = true;
    }
    
    private static Map getMultiCommandsByDisplayName(CommandsTree[] children,
                                                     FileObject[] allFiles) {
        Map commandsByDisplayNames = new HashMap();
        Map multiCommandsByDisplayName = new HashMap();
        for (int i = 0; i < children.length; i++) {
            CommandSupport cmd = (CommandSupport) children[i].getCommandSupport();
            if (cmd != null && !children[i].hasChildren()) {
                String dn = cmd.getDisplayName();
                if (dn != null) {
                    if (commandsByDisplayNames.containsKey(dn)) {
                        List cmdList = (List) multiCommandsByDisplayName.get(dn);
                        if (cmdList == null) {
                            cmdList = new LinkedList();
                            cmdList.add(commandsByDisplayNames.get(dn));
                            multiCommandsByDisplayName.put(dn, cmdList);
                        }
                        cmdList.add(cmd);
                    }
                    commandsByDisplayNames.put(dn, cmd);
                }
            }
        }
        return multiCommandsByDisplayName;
    }

    /**
     * Create the command menu item.
     * @param cmd the command
     */
    private static JMenuItem createItem(CommandSupport cmd, boolean expertMode,
                                        List switchableList, String advancedOptionsSign,
                                        boolean inMenu, ActionListener listener,
                                        Map actionCommandMap) {
        String label = cmd.getDisplayName();
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        if (label == null) return null;
        boolean hasExpert = cmd.hasExpertMode();
        if (hasExpert && expertMode && advancedOptionsSign != null) {
            label += advancedOptionsSign;
        }
        JMenuItem item = new JMenuItem();
        Actions.setMenuText(item, label, inMenu);
        String commandStr = cmd.getName();
        if (actionCommandMap != null) {
            String base = commandStr;
            int i = 0;
            while (actionCommandMap.containsKey(commandStr)) {
                commandStr = base + ++i;
            }
            actionCommandMap.put(commandStr, cmd);
        }
        item.setActionCommand(commandStr);
        item.addActionListener(listener);
        if (hasExpert) {// && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }
    
    /**
     * Create a menu item, that represents several commands of the same display name.
     * @param cmd the command
     */
    private static JMenuItem createItem(CommandSupport[] cmds, String label, boolean expertMode,
                                        List switchableList, String advancedOptionsSign,
                                        boolean inMenu, ActionListener listener,
                                        Map actionCommandMap) {
        boolean hasExpert = true;
        if (hasExpert) {
            for (int i = 0; i < cmds.length && hasExpert; i++) {
                hasExpert = hasExpert && cmds[i].hasExpertMode();
            }
            if (hasExpert && expertMode && advancedOptionsSign != null) {
                label += advancedOptionsSign;
            }
        }
        JMenuItem item = new JMenuItem();
        Actions.setMenuText(item, label, inMenu);
        String commandStr = cmds[0].getName();
        if (actionCommandMap != null) {
            String base = commandStr;
            int i = 0;
            while (actionCommandMap.containsKey(commandStr)) {
                commandStr = base + ++i;
            }
            actionCommandMap.put(commandStr, cmds);
        }
        item.setActionCommand(commandStr);
        item.addActionListener(listener);
        if (hasExpert) {// && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }
    
    /**
     * Create the command menu item.
     * @param cmd the command
     */
    public static JMenuItem createItem(CommandSupport cmd, boolean expertMode,
                                       String advancedOptionsSign,
                                       boolean inMenu, Map filesWithMessages) {
        String label = cmd.getDisplayName();
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        if (label == null) return null;
        boolean hasExpert = cmd.hasExpertMode();
        if (hasExpert && expertMode && advancedOptionsSign != null) {
            label += advancedOptionsSign;
        }
        boolean[] CTRL_Down = { false, expertMode };
        Map actionCommandMap = new HashMap();
        List switchableList = new ArrayList();
        ActionListener listener = new CommandMenu.CommandActionListener(CTRL_Down,
                                                                        actionCommandMap,
                                                                        filesWithMessages);
        JMenuItem item = new JMenuItem();
        Actions.setMenuText(item, label, inMenu);
        String commandStr = cmd.getName();
        actionCommandMap.put(commandStr, cmd);
        item.setActionCommand(commandStr);
        item.addActionListener(listener);
        if (advancedOptionsSign != null) {
            item.addMenuKeyListener(new CtrlMenuKeyListener(CTRL_Down, switchableList,
                                                            advancedOptionsSign));
        }
        if (hasExpert) {// && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }
    
    private static void undoAdvanceSign(JMenuItem item) {
        MenuKeyListener[] keyListeners = item.getMenuKeyListeners();
        for (int i = 0; i < keyListeners.length; i++) {
            if (keyListeners[i] instanceof CtrlMenuKeyListener) {
                ((CtrlMenuKeyListener) keyListeners[i]).deselectMenu();
            }
        }
        java.awt.Component parent = item.getParent();
        if (parent instanceof JPopupMenu) {
            parent = ((JPopupMenu) parent).getInvoker();
        }
        if (parent instanceof JMenuItem) {
            undoAdvanceSign((JMenuItem) parent);
        }
    }
    
    private static class CommandActionListener extends Object implements ActionListener {
        
        private boolean[] CTRL_Down;
        private Map actionCommandMap;
        private Map filesWithMessages;
        
        public CommandActionListener(boolean[] CTRL_Down, Map actionCommandMap,
                                     Map filesWithMessages) {
            this.CTRL_Down = CTRL_Down;
            this.actionCommandMap = actionCommandMap;
            this.filesWithMessages = filesWithMessages;
        }
        
        /** Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            final boolean changeExpertMode = CTRL_Down[0];
            final boolean expertMode = CTRL_Down[0] ^ CTRL_Down[1];
            Object source = e.getSource();
            if (source instanceof JMenuItem) undoAdvanceSign((JMenuItem) source);
            final String cmdName = e.getActionCommand();
            final CommandSupport[] cmdSupports;
            if (actionCommandMap != null) {
                Object cmd = actionCommandMap.get(cmdName);
                if (cmd instanceof CommandSupport[]) {
                    cmdSupports = (CommandSupport[]) cmd;
                } else {
                    cmdSupports = new CommandSupport[] { (CommandSupport) cmd };
                }
            } else {
                cmdSupports = null;
            }
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    for (Iterator it = filesWithMessages.keySet().iterator(); it.hasNext(); ) {
                        FileObject[] files = (FileObject[]) it.next();
                        String message = (String) filesWithMessages.get(files);
                        Command[] cmds;
                        if (cmdSupports != null) {
                            cmds = new Command[cmdSupports.length];
                            for (int i = 0; i < cmdSupports.length; i++) {
                                cmds[i] = cmdSupports[i].createCommand();
                            }
                        } else {
                            Command cmd = null;
                            try {
                                cmd = VcsManager.getDefault().createCommand(cmdName, files);
                            } catch (IllegalArgumentException iaex) {}
                            if (cmd != null) {
                                cmds = new Command[] { cmd };
                            } else {
                                cmds = new Command[0];
                            }
                        }
                        for (int i = 0; i < cmds.length; i++) {
                            Command cmd = cmds[i];
                            if (cmd.getApplicableFiles(files) == null) {
                                continue;
                            }
                            if (message != null && cmd instanceof MessagingCommand) {
                                ((MessagingCommand) cmd).setMessage(message);
                            }
                            cmd.setGUIMode(true);
                            if (changeExpertMode) cmd.setExpertMode(expertMode);
                            CommandTask task = VcsFSCommandsAction.executeCommand(cmd, files);
                            if (task != null) {
                                try {
                                    task.waitFinished(0);
                                } catch (InterruptedException iex) {
                                    // The command was interrupted, do not run the rest.
                                    break;
                                }
                            } else {
                                // The command was canceled, do not run the rest.
                                break;
                            }
                            //boolean customized = VcsManager.getDefault().showCustomizer(cmd);
                            //if (customized) cmd.execute();
                        }
                    }
                }
            });
        }
        
    }
    
    private static class CtrlMenuKeyListener implements javax.swing.event.MenuKeyListener {
        
        private boolean[] CTRL_Down;
        private List switchableList;
        private String advancedOptionsSign;
        
        public CtrlMenuKeyListener(boolean[] CTRL_Down, List switchableList,
                                   String advancedOptionsSign) {
            this.CTRL_Down = CTRL_Down;
            this.switchableList = switchableList;
            this.advancedOptionsSign = advancedOptionsSign;
        }
        
        public void menuKeyTyped(javax.swing.event.MenuKeyEvent p1) {
        }
        public void menuKeyPressed(javax.swing.event.MenuKeyEvent p1) {
            boolean CTRL_IsDown = p1.getKeyCode() == javax.swing.event.MenuKeyEvent.VK_CONTROL;
//            System.out.println("key pressed=" + newCTRL_Down);
//            System.out.println("is down=" + p1.isControlDown());
            if (CTRL_IsDown) {
                changeCtrlSigns(CTRL_IsDown);
                CTRL_Down[0] = CTRL_IsDown;
            }
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent p1) {
            boolean CTRL_IsUp = p1.getKeyCode() == javax.swing.event.MenuKeyEvent.VK_CONTROL;
//            System.out.println("key Released=" + newCTRL_Down);
//            System.out.println("keykode=" + p1.getKeyText(p1.getKeyCode()));
            if (CTRL_IsUp) {
                changeCtrlSigns(!CTRL_IsUp);
                CTRL_Down[0] = !CTRL_IsUp;
            }
        }
        
        private void changeCtrlSigns(boolean newValue) {
            if (newValue == CTRL_Down[0]) return;
            Iterator it = switchableList.iterator();
            while (it.hasNext()) {
                JMenuItem item = (JMenuItem)it.next();
                String text = item.getText();
                if (newValue ^ CTRL_Down[1]) {
                    // do turn ctrl sign on
                    if (!text.endsWith(advancedOptionsSign)) {
                        text = text + advancedOptionsSign;
                    }
                } else {
                    // turn it off - ctrl released
                    if (text.endsWith(advancedOptionsSign)) {
                        text = text.substring(0, text.length() - advancedOptionsSign.length());
                    }
                }
                item.setText(text);
            }
        }
    
        public void deselectMenu() {
            changeCtrlSigns(false);
            CTRL_Down[0] = false;
        }

    }

}
