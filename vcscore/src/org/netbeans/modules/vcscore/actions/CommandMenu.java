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

package org.netbeans.modules.vcscore.actions;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.MenuKeyListener;

import org.netbeans.api.fileinfo.NonRecursiveFolder;

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
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.modules.vcscore.commands.RecursionAwareCommand;
import org.netbeans.modules.vcscore.commands.RecursionAwareCommandSupport;
import org.netbeans.modules.vcscore.settings.GeneralVcsSettings;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileSystem;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;

/**
 * The (popup or main) menu with VCS commands used as FS actions and
 * in main Versioning menu.
 * <p>
 * It contructs UI following CommandsTree model. Before adding
 * an item it checks its suitability using display name existence and
 * enablemes using <code>Command.getApplicableFiles != null</code> tests.
 * <p>
 * XXX There is also a logic that somehow handles commands with the same
 * name (e.g. Add All on folder and on file?).
 * <p>
 * On action it creates commands using VcsManager. XXX the command was already
 * available in <code>createPopup</code> so why is it recreated here? If they are instances
 * of Messaging commands it sets a message. Finally it executes commands
 * setting them to GUI mode, informing integrity support, running
 * command customization and waiting on <code>Command.execute()</code>.
 * <p>
 * getApplicableFiles() is called on CommandSupport when the menu is constructed
 * to know whether the command should be displayed on the popup or not.
 * Later it's called right before the command is executed to know on which files
 * the command can be actually executed.
 *
 * @author  Martin Entlicher
 */
public class CommandMenu extends JMenuPlus {
    
    public static final String DEFAULT_ADVANCED_OPTIONS_SIGN =
        org.openide.util.NbBundle.getMessage(CommandMenu.class, "CTL_AdvancedOptionsSign");
    
    private CommandsTree commandRoot;
    private Map filesWithInfo;
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
     * @param filesWithInfo The map of array of FileObjects and the associated
     *        file info. This is either a String message, used in MessagingCommands,
     *        or NonRecursiveFolder.class, according to which RecursionAwareCommand
     *        is set up.
     * @param removeDisabled Remove the disabled commands from the popup menu
     * @param inMenu Whether a menu or a popup-menu should be created.
     */
    public CommandMenu(CommandsTree commandRoot, Map filesWithInfo,
                       boolean removeDisabled, boolean inMenu,
                       boolean globalExpertMode) {
        this(commandRoot, filesWithInfo, DEFAULT_ADVANCED_OPTIONS_SIGN,
             removeDisabled, inMenu, globalExpertMode, null, new HashMap());
    }
    
    private CommandMenu(CommandsTree commandRoot, Map filesWithInfo,
                       String advancedOptionsSign,
                       boolean removeDisabled, boolean inMenu, boolean globalExpertMode,
                       ActionListener listener, Map actionCommandMap) {
        this(commandRoot, filesWithInfo, new ArrayList(), advancedOptionsSign,
             removeDisabled, inMenu, globalExpertMode, listener, actionCommandMap,
             new boolean[] { false, false }); // two elements: 1) whether CTRL was pressed, 2) expertMode
    }
    
    private CommandMenu(CommandsTree commandRoot, Map filesWithInfo,
                        List switchableList, String advancedOptionsSign,
                        boolean removeDisabled, boolean inMenu, boolean globalExpertMode,
                        ActionListener listener, Map actionCommandMap, boolean[] CTRL_Down) {
        super();
        this.commandRoot = commandRoot;
        this.filesWithInfo = filesWithInfo;
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
                                                               filesWithInfo);
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
        if (filesWithInfo.size() == 1) {
            allFiles = (FileObject[]) filesWithInfo.keySet().iterator().next();
        } else {
            List files = new ArrayList();
            for (Iterator it = filesWithInfo.keySet().iterator(); it.hasNext(); ) {
                files.addAll(Arrays.asList((FileObject[]) it.next()));
            }
            allFiles = (FileObject[]) files.toArray(new FileObject[files.size()]);
        }
        boolean nonRecursive = true; // Whether we're in non-recursive mode
        for (Iterator it = filesWithInfo.values().iterator(); it.hasNext(); ) {
            Object fileInfo = it.next();
            if (fileInfo != NonRecursiveFolder.class) {
                nonRecursive = false;
                break;
            }
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
            if (removeDisabled && (cmd.getApplicableFiles(allFiles) == null ||
                                   !isRecursivnessMatched(nonRecursive, cmd))) {
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
                submenu = new CommandMenu(children[i], filesWithInfo,
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
            if (!removeDisabled && (cmd.getApplicableFiles(allFiles) == null ||
                                    !isRecursivnessMatched(nonRecursive, cmd))) {
                item.setEnabled(false);
            }
        }
        popupCreated = true;
    }
    
    private static boolean isRecursivnessMatched(boolean nonRecursive, CommandSupport cmd) {
        if (nonRecursive) {
            if (cmd instanceof RecursionAwareCommandSupport) {
                return ((RecursionAwareCommandSupport) cmd).canProcessFoldersNonRecursively();
            } else {
                return false;
            }
        } else {
            return true;
        }
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
                                       boolean inMenu, Map filesWithInfo) {
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
                                                                        filesWithInfo);
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
        private Map filesWithInfo;
        private GeneralVcsSettings settings;
        
        public CommandActionListener(boolean[] CTRL_Down, Map actionCommandMap,
        Map filesWithInfo) {
            this.CTRL_Down = CTRL_Down;
            this.actionCommandMap = actionCommandMap;
            this.filesWithInfo = filesWithInfo;
        }
        
        /** Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            final boolean changeExpertMode = CTRL_Down[0];
            final boolean expertMode = CTRL_Down[0] ^ CTRL_Down[1];
            Object source = e.getSource();
            if (source instanceof JMenuItem) undoAdvanceSign((JMenuItem) source);
            final String cmdName = e.getActionCommand();
            String cmdDisplayName;
            final CommandSupport[] cmdSupports;
            if (actionCommandMap != null) {
                Object cmd = actionCommandMap.get(cmdName);
                if (cmd instanceof CommandSupport[]) {
                    cmdSupports = (CommandSupport[]) cmd;
                } else {
                    cmdSupports = new CommandSupport[] { (CommandSupport) cmd };
                }
                cmdDisplayName = Actions.cutAmpersand(cmdSupports[0].getDisplayName());
            } else {
                cmdSupports = null;
                cmdDisplayName = cmdName;
            }
            settings = (GeneralVcsSettings)SharedClassObject.findObject(GeneralVcsSettings.class, true);            
            if(!expertMode && settings.isAdvancedNotification() && cmdSupports[0].hasExpertMode()){
                boolean isFromFS = false;
                if (cmdSupports[0] instanceof UserCommandSupport) {
                    isFromFS = ((UserCommandSupport) cmdSupports[0]).getExecutionContext() instanceof FileSystem;
                }
                if(showFirstTimerDialog(cmdDisplayName, isFromFS))
                    invokeCommand(cmdSupports, cmdName, changeExpertMode, expertMode);
            } else {
                invokeCommand(cmdSupports, cmdName, changeExpertMode, expertMode);         
            }
            org.netbeans.modules.vcscore.ui.fsmanager.VcsManager.addVersioningOpenerListener();
        }
        
        private void invokeCommand(final CommandSupport[] cmdSupports, final String cmdName, final boolean changeExpertMode, final boolean expertMode){
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    mergePossibleFiles(filesWithInfo);
                    for (Iterator it = filesWithInfo.keySet().iterator(); it.hasNext(); ) {
                        FileObject[] files = (FileObject[]) it.next();
                        Object fileInfo = filesWithInfo.get(files);
                        String message = null;
                        boolean nonRecursive = false;
                        if (fileInfo instanceof String) {
                            message = (String) fileInfo;
                        } else if (fileInfo == NonRecursiveFolder.class) {
                            nonRecursive = true;
                        }
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
                            if (cmds[i].getApplicableFiles(files) == null) {
                                cmds[i] = null; // Remove all commands that can not be executed
                            }
                        }
                        for (int i = 0; i < cmds.length; i++) {
                            Command cmd = cmds[i];
                            if (cmd == null) {
                                continue;
                            }
                            if (message != null && cmd instanceof MessagingCommand) {
                                ((MessagingCommand) cmd).setMessage(message);
                            }
                            if (nonRecursive && cmd instanceof RecursionAwareCommand) {
                                ((RecursionAwareCommand) cmd).setRecursionBanned(true);
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
        
        /**
         * Merges the possible files together.
         */
        private static void mergePossibleFiles(Map filesWithInfo) {
            if (filesWithInfo.size() <= 1) return ; // Nothing to merge
            FileObject[] lastFiles = null;
            Object lastInfo = null;
            Object[] mergedInfoPtr = new Object[1];
            Map mergedMap = new LinkedHashMap(filesWithInfo.size());
            for (Iterator it = filesWithInfo.keySet().iterator(); it.hasNext(); ) {
                FileObject[] files = (FileObject[]) it.next();
                Object fileInfo = filesWithInfo.get(files);
                if (lastFiles != null) {
                    if (canMergeInfos(lastInfo, fileInfo, mergedInfoPtr)) {
                        files = mergeFiles(lastFiles, files);
                        fileInfo = mergedInfoPtr[0];
                    } else {
                        mergedMap.put(lastFiles, lastInfo);
                    }
                }
                lastFiles = files;
                lastInfo = fileInfo;
            }
            if (lastFiles != null) {
                mergedMap.put(lastFiles, lastInfo);
            }
            filesWithInfo.clear();
            filesWithInfo.putAll(mergedMap);
        }
        
        private static boolean canMergeInfos(Object i1, Object i2, Object[] mi) {
            if (i1 == i2) {
                mi[0] = i1;
                return true;
            }
            if (i1 == NonRecursiveFolder.class && i2 == null ||
                i1 == null && i2 == NonRecursiveFolder.class) {
                mi[0] = NonRecursiveFolder.class;
                return true;
            }
            return false;
        }
        
        private static FileObject[] mergeFiles(FileObject[] fos1, FileObject[] fos2) {
            FileObject[] fos3 = new FileObject[fos1.length + fos2.length];
            System.arraycopy(fos1, 0, fos3, 0, fos1.length);
            System.arraycopy(fos2, 0, fos3, fos1.length, fos2.length);
            return fos3;
        }
        
        
        /**
         * Shows the warning About Default Switches
         * dialog and gives the possibility to cancel the command.
         * @return Should we proceed executing a CVS command
         */
        private static boolean showFirstTimerDialog(String commandName, boolean isFromFS) {
            JPanel panel = new JPanel();
            JLabel label1 = new JLabel();
            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            final JCheckBox box = new JCheckBox();
            panel.setLayout(new java.awt.GridBagLayout());
            StringBuffer buff = new StringBuffer();
            buff.append(NbBundle.getBundle(CommandMenu.class).getString("CommandMenu.firstTimer1.text")); //NOI18N
            if (isFromFS) {
                buff.append("\n\n");
                buff.append(NbBundle.getBundle(CommandMenu.class).getString("CommandMenu.firstTimer2.text")); //NOI18N
            }
            textArea.setText(buff.toString());
            textArea.getAccessibleContext().setAccessibleName(NbBundle.getBundle(CommandMenu.class).getString("CommandMenu.firstTimer.a11yName")); // NOI18N
            textArea.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CommandMenu.class).getString("CommandMenu.firstTimer.a11yDesc")); // NOI18N
            textArea.setBackground(label1.getBackground());
            textArea.setColumns(50);
            int columnWidth = textArea.getFontMetrics(textArea.getFont()).charWidth('m');
            textArea.setSize(50*columnWidth, 10);
            // It's necessary to set explicitly the width! The height is then computed automatically.
            // If the width is not set explicitly, the height is computed in a wrong way.
            
            GridBagConstraints gridBagConstraints1;
            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;//HORIZONTAL;
            gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
            gridBagConstraints1.weighty = 1.0;
            panel.add(textArea, gridBagConstraints1);
            
            Actions.setMenuText(box, NbBundle.getMessage(CommandMenu.class, "CommandMenu.firstTimerBox"), true); //NOI18N
            box.setSelected(false);
            box.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandMenu.class, "CommandMenu.firstTimerBox.a11yDesc")); // NOI18N
            box.setToolTipText(NbBundle.getMessage(CommandMenu.class, "CommandMenu.firstTimerBox.a11yDesc")); // NOI18N
            gridBagConstraints1 = new java.awt.GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 4;
            gridBagConstraints1.insets = new java.awt.Insets(12, 12, 0, 11);
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.weighty = 0.0;
            panel.add(box, gridBagConstraints1);
            
            String title = NbBundle.getBundle(CommandMenu.class).getString("CommandMenu.firstTimer.title"); //NOI18N
            panel.getAccessibleContext().setAccessibleDescription(title);
            DialogDescriptor dd;
            JButton commandButton = null;
            if (commandName != null) {
                commandButton = new JButton(commandName);
                commandButton.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(CommandMenu.class, "CommandMenu.firstTimer.button.a11yDesc", commandName)); // NOI18N
                Object[] options = new Object[] { commandButton, NotifyDescriptor.CANCEL_OPTION };
                dd = new DialogDescriptor(panel, title, true, options, commandButton,
                DialogDescriptor.DEFAULT_ALIGN, null, null);
            } else {
                dd = new DialogDescriptor(panel, title, true,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_OPTION, null);
            }
            final DialogDescriptor ddFin = dd;
            
            //#47150
            System.setProperty("javahelp.ignore.modality", "true"); //NOI18N
            Object retValue = DialogDisplayer.getDefault().notify(ddFin);
            
            // Assure, that one of the buttons was pressed. If not, do nothing.
            if (commandButton == null && !NotifyDescriptor.OK_OPTION.equals(retValue) ||
            commandButton != null && !commandButton.equals(retValue)) return false;
            if (box.isSelected()) {
                GeneralVcsSettings settings = (GeneralVcsSettings)SharedClassObject.findObject(GeneralVcsSettings.class, true);
                settings.setAdvancedNotification(false);
            }
            return true;
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
