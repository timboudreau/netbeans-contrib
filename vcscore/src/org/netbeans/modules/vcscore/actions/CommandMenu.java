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

package org.netbeans.modules.vcscore.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openide.awt.Actions;
import org.openide.awt.JMenuPlus;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.commands.CommandSupport;

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
    private FileObject[] files;
    private List switchableList;
    private String advancedOptionsSign;
    private boolean removeDisabled;
    private boolean inMenu;
    private ActionListener listener;
    private Map actionCommandMap;
    private boolean popupCreated = false;
    private boolean[] CTRL_Down = { false };

    /** Creates a new instance of CommandMenu */
    public CommandMenu(CommandsTree commandRoot, FileObject[] files,
                       boolean removeDisabled, boolean inMenu) {
        this(commandRoot, files, DEFAULT_ADVANCED_OPTIONS_SIGN, removeDisabled,
             inMenu, null, new HashMap());
    }
    
    public CommandMenu(CommandsTree commandRoot, FileObject[] files,
                       String advancedOptionsSign,
                       boolean removeDisabled, boolean inMenu,
                       ActionListener listener, Map actionCommandMap) {
        this(commandRoot, files, new ArrayList(), advancedOptionsSign,
             removeDisabled, inMenu, listener, actionCommandMap);
    }
    
    private CommandMenu(CommandsTree commandRoot, FileObject[] files,
                        List switchableList, String advancedOptionsSign,
                        boolean removeDisabled, boolean inMenu,
                        ActionListener listener, Map actionCommandMap) {
        super();
        this.commandRoot = commandRoot;
        this.files = files;
        this.switchableList = switchableList;
        this.advancedOptionsSign = advancedOptionsSign;
        this.removeDisabled = removeDisabled;
        this.inMenu = inMenu;
        this.listener = (listener != null) ?
                         listener :
                         new CommandMenu.CommandActionListener(CTRL_Down, actionCommandMap,
                                                               files);
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
            JMenuItem cmdMenu = createItem(cmd, false, switchableList,
                                           advancedOptionsSign, inMenu,
                                           listener, actionCommandMap);
            if (cmdMenu == null) continue;
            wasSeparator = false;
            JMenuItem item;
            if (children[i].hasChildren()) {
                JMenu submenu;
                submenu = new CommandMenu(children[i], files,
                                          switchableList, advancedOptionsSign,
                                          removeDisabled, inMenu, listener,
                                          actionCommandMap);
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
        if (hasExpert && (!expertMode)) {
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
                                       boolean inMenu, FileObject[] files) {
        String label = cmd.getDisplayName();
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        if (label == null) return null;
        boolean hasExpert = cmd.hasExpertMode();
        if (hasExpert && expertMode && advancedOptionsSign != null) {
            label += advancedOptionsSign;
        }
        boolean[] CTRL_Down = { false };
        Map actionCommandMap = new HashMap();
        List switchableList = new ArrayList();
        ActionListener listener = new CommandMenu.CommandActionListener(CTRL_Down,
                                                                        actionCommandMap,
                                                                        files);
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
        if (hasExpert && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }
    
    private static class CommandActionListener extends Object implements ActionListener {
        
        private boolean[] CTRL_Down;
        private Map actionCommandMap;
        private FileObject[] files;
        
        public CommandActionListener(boolean[] CTRL_Down, Map actionCommandMap,
                                     FileObject[] files) {
            this.CTRL_Down = CTRL_Down;
            this.actionCommandMap = actionCommandMap;
            this.files = files;
        }
        
        /** Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            final String cmdName = e.getActionCommand();
            final CommandSupport cmdSupport;
            if (actionCommandMap != null) {
                cmdSupport = (CommandSupport) actionCommandMap.get(cmdName);
            } else {
                cmdSupport = null;
            }
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    Command cmd = null;
                    if (cmdSupport != null) {
                        cmd = cmdSupport.createCommand();
                        cmd.setFiles(files);
                    } else {
                        try {
                            cmd = VcsManager.getDefault().createCommand(cmdName, files);
                        } catch (IllegalArgumentException iaex) {}
                    }
                    if (cmd != null) {
                        cmd.setGUIMode(true);
                        if (CTRL_Down[0]) cmd.setExpertMode(CTRL_Down[0]);
                        boolean customized = VcsManager.getDefault().showCustomizer(cmd);
                        if (customized) cmd.execute();
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
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || p1.isControlDown(); // NOI18N
//            System.out.println("key pressed=" + newCTRL_Down);
//            System.out.println("is down=" + p1.isControlDown());
            changeCtrlSigns(newCTRL_Down);
            CTRL_Down[0] = newCTRL_Down;
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent p1) {
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || !p1.isControlDown(); // NOI18N
//            System.out.println("key Released=" + newCTRL_Down);
//            System.out.println("keykode=" + p1.getKeyText(p1.getKeyCode()));
            changeCtrlSigns(!newCTRL_Down);
            CTRL_Down[0] = !newCTRL_Down;
        }
        
        private void changeCtrlSigns(boolean newValue) {
            if (newValue == CTRL_Down[0]) return;
            Iterator it = switchableList.iterator();
            while (it.hasNext()) {
                JMenuItem item = (JMenuItem)it.next();
                String text = item.getText();
                if (newValue) {
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
    
    }

    /*
    private void deselectedMenu() {
        changeCtrlSigns(false);
        CTRL_Down = false;
    }
     */  

}
