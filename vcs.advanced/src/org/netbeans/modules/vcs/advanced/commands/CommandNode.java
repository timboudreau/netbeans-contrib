/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.commands;

import java.awt.Image;
import java.awt.datatransfer.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.CommandExecutorSupport;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.Table;

/**
 * The Node representation of a VCS command.
 *
 * @author  Martin Entlicher
 */
public class CommandNode extends AbstractNode {

    private static final String DEFAULT_FOLDER = "org/openide/resources/defaultFolder.gif"; // NOI18N
    private static final String DEFAULT_OPEN_FOLDER = "org/openide/resources/defaultFolderOpen.gif"; // NOI18N
    //private static final String DEFAULT_COMMAND = "org/netbeans/modules/vcscore/runtime/commandIcon.gif"; // NOI18N
    private static final String DEFAULT_COMMAND = "org/netbeans/modules/vcs/advanced/commands/commandsJmenuItem.gif"; // NOI18N
    private static final String DEFAULT_HIDDEN_COMMAND_BADGE = "org/netbeans/modules/vcs/advanced/commands/commandsHiddenBadgeIcon.gif"; // NOI18N

    private VcsCommand cmd = null;
    private ResourceBundle resourceBundle = null;
    private CommandsIndex index = null;
    
    private static Table stdandard_propertyClassTypes = new Table();
    private static Table expert_propertyClassTypes = new Table();
    private static Table list_propertyClassTypes = new Table();
    private static Table stdlist_propertyClassTypes = new Table();
    private static Table folder_std_propertyClassTypes = new Table();
    private static Collection FOLDER_COMMAND_PROPERTIES;
    
    private static Collection stdlistCmdNames;
    
    private static Image[] SEPARATOR_ICONS = new Image[4];
    
    static {
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_LABEL_MNEMONIC, String.class);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_EXEC, String.class);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_CONFIRMATION_MSG, String.class);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_NOTIFICATION_SUCCESS_MSG, String.class);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_NOTIFICATION_FAIL_MSG, String.class);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_INPUT_DESCRIPTOR, String.class);
        //propertyClassTypes.put(VcsCommand.PROPERTY_NOT_ON_ROOT, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_ON_DIR, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_ON_FILE, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_ON_ROOT, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER, Boolean.TYPE);
        stdandard_propertyClassTypes.put(UserCommand.PROPERTY_REFRESH_PROCESSED_FILES, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_ON_FAIL, Integer.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT, Boolean.TYPE);
        stdandard_propertyClassTypes.put(VcsCommand.PROPERTY_DISTINGUISH_BINARY_FILES, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_ADVANCED_NAME, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_SUPPORTS_ADVANCED_MODE, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_HIDDEN, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_HIDDEN_TEST_EXPRESSION, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_DISABLED_ON_STATUS, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CONCURRENT_EXECUTION, Integer.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_PROCESS_ALL_FILES, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CLEAN_UNIMPORTANT_FILES_ON_SUCCESS, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_IGNORE_FAIL, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS, Boolean.TYPE);
        expert_propertyClassTypes.put(UserCommand.PROPERTY_DATA_REGEX, String.class);
        expert_propertyClassTypes.put(UserCommand.PROPERTY_ERROR_REGEX, String.class);
        expert_propertyClassTypes.put(UserCommand.PROPERTY_DATA_REGEX_GLOBAL, String.class);
        expert_propertyClassTypes.put(UserCommand.PROPERTY_ERROR_REGEX_GLOBAL, String.class);
        expert_propertyClassTypes.put(UserCommand.PROPERTY_INPUT, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_NUM_REVISIONS, Integer.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CHANGED_REVISION_VAR_NAME, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_CHANGING_REVISION, Boolean.TYPE);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_USER_PARAMS, String[].class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS, String[].class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_CLASS_NAME, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_DISPLAY_NAME, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_COMMANDS_AFTER_SUCCESS, String.class);
        expert_propertyClassTypes.put(VcsCommand.PROPERTY_COMMANDS_AFTER_FAIL, String.class);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_FILE_NAME, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_REMOVED_FILE_NAME, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STATUS, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_REFRESH_FILE_STATUS_SUBSTITUTIONS, String.class);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_LOCKER, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_REVISION, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STICKY, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_TIME, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_DATE, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_SIZE, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_ATTR, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_REFRESH_INFO_FROM_BOTH_DATA_OUTS, Boolean.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_FILE_NAME, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STATUS, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_LOCKER, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_REVISION, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STICKY, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_TIME, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_DATE, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_SIZE, Integer.TYPE);
        stdlist_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_ATTR, Integer.TYPE);
        stdlistCmdNames = Collections.unmodifiableList(Arrays.asList(new String[] {
            VcsCommand.NAME_REFRESH, VcsCommand.NAME_REFRESH_OFFLINE,
            VcsCommand.NAME_REFRESH_RECURSIVELY, VcsCommand.NAME_REFRESH_RECURSIVELY_OFFLINE
        }));
        folder_std_propertyClassTypes.put(VcsCommand.PROPERTY_LABEL_MNEMONIC, String.class);
        FOLDER_COMMAND_PROPERTIES = new HashSet();
        FOLDER_COMMAND_PROPERTIES.add(VcsCommand.PROPERTY_LABEL_MNEMONIC);
    }

    /** Creates new CommandNode */
    public CommandNode(Children children, VcsCommand cmd) {
        super(children);
        this.cmd = cmd;
        init();
    }
    
    private void init() {
        if (cmd != null) {
            setName(cmd.getName());
            setDisplayName(cmd.getDisplayName());
        } else {
            setName("SEPARATOR");
            setDisplayName(g("CTL_Separator"));
        }
        index = new CommandsIndex();
        getCookieSet().add(index);
        fireIconChange();
    }
    
    public void setDisplayName(String s) {
        if (s == null && cmd != null) {
            s = NbBundle.getMessage(CommandNode.class, "LBL_HiddenCommandName", cmd.getName());
        }
        super.setDisplayName(s);
    }
    
    /**
     * Get the Class type of the known command properties.
     * @return the class type or null, when the property is not known
     */
    public static Class getPropertyClass(String propertyName) {
        Class clazz = (Class) stdandard_propertyClassTypes.get(propertyName);
        if (clazz != null) return clazz;
        clazz = (Class) expert_propertyClassTypes.get(propertyName);
        if (clazz != null) return clazz;
        clazz = (Class) list_propertyClassTypes.get(propertyName);
        if (clazz != null) return clazz;
        return null;
    }

    public void setCommand(VcsCommand cmd) {
        this.cmd = cmd;
        init();
    }
    
    public VcsCommand getCommand() {
        return cmd;
    }
    
    /**
     * Get the collection of names of all commands.
     */
    public Collection getAllCommandsNames() {
        CommandNode root = this;
        while (root.getParentNode() != null) root = (CommandNode) root.getParentNode();
        ArrayList commandNames = new ArrayList();
        fillCommandNames(root, commandNames);
        return commandNames;
    }
    
    private static void fillCommandNames(CommandNode node, Collection commandNames) {
        VcsCommand cmd = node.getCommand();
        if (cmd != null) commandNames.add(cmd.getName());
        Node[] subNodes = node.getChildren().getNodes();
        for (int i = 0; i < subNodes.length; i++) {
            fillCommandNames((CommandNode) subNodes[i], commandNames);
        }
    }
    
    private Image getSeparatorIcon(int type) {
        Image icon;
        if (java.beans.BeanInfo.ICON_MONO_16x16 == type) {
            if (SEPARATOR_ICONS[0] == null) {
                try {
                    SEPARATOR_ICONS[0] = java.beans.Introspector.getBeanInfo(javax.swing.JSeparator.class).getIcon(type);
                } catch (java.beans.IntrospectionException exc) {}
            }
            icon = SEPARATOR_ICONS[0];
        } else if (java.beans.BeanInfo.ICON_MONO_32x32 == type) {
            if (SEPARATOR_ICONS[1] == null) {
                try {
                    SEPARATOR_ICONS[1] = java.beans.Introspector.getBeanInfo(javax.swing.JSeparator.class).getIcon(type);
                } catch (java.beans.IntrospectionException exc) {}
            }
            icon = SEPARATOR_ICONS[1];
        } else if (java.beans.BeanInfo.ICON_COLOR_16x16 == type) {
            if (SEPARATOR_ICONS[2] == null) {
                try {
                    SEPARATOR_ICONS[2] = java.beans.Introspector.getBeanInfo(javax.swing.JSeparator.class).getIcon(type);
                } catch (java.beans.IntrospectionException exc) {}
            }
            icon = SEPARATOR_ICONS[2];
        } else if (java.beans.BeanInfo.ICON_COLOR_32x32 == type) {
            if (SEPARATOR_ICONS[3] == null) {
                try {
                    SEPARATOR_ICONS[3] = java.beans.Introspector.getBeanInfo(javax.swing.JSeparator.class).getIcon(type);
                } catch (java.beans.IntrospectionException exc) {}
            }
            icon = SEPARATOR_ICONS[3];
        } else icon = null;
        if (icon != null) {
            return icon;
        } else {
            return super.getIcon(type);
        }
    }
    
    /** Find an icon for this node.
     *
     * @param type constants from {@link java.beans.BeanInfo}
     *
     * @return icon to use to represent the bean
     */
    public Image getIcon (int type) {
        //System.out.println("getIcon("+type+"): cmd = "+cmd);
        if (cmd == null) {
            return getSeparatorIcon(type);
        } else if (isFolderCommand(cmd)) {
            return Utilities.loadImage(DEFAULT_FOLDER);
        } else {
            if (cmd.getDisplayName() == null) {
                return Utilities.mergeImages(Utilities.loadImage(DEFAULT_COMMAND), Utilities.loadImage(DEFAULT_HIDDEN_COMMAND_BADGE), 16, 8);
            } else {
                return Utilities.loadImage(DEFAULT_COMMAND);
            }
        }
    }
    
    public Image getOpenedIcon(int type) {
        //System.out.println("getOpenedIcon("+type+"): cmd = "+cmd);
        return Utilities.loadImage(DEFAULT_OPEN_FOLDER);
    }

    public boolean canCopy() {
        return (getParentNode() != null);
    }
    
    public boolean canCut() {
        return (getParentNode() != null);
    }
    
    /** Copy this node to the clipboard.
     *
     * @return The transferable for VcsCommand
     * @throws IOException if it could not copy
     */
    public Transferable clipboardCopy() throws java.io.IOException {
        return new CommandCopySupport.CommandTransferable(
            CommandCopySupport.COMMAND_COPY_FLAVOR, this);
    }

    /** Cut this node to the clipboard.
     *
     * @return {@link Transferable} with one flavor, {@link COMMAND_CUT_FLAVOR }
     * @throws IOException if it could not cut
     */
    public Transferable clipboardCut() throws java.io.IOException {
        return new CommandCopySupport.CommandTransferable(
            CommandCopySupport.COMMAND_CUT_FLAVOR, this);
    }

    /** Accumulate the paste types that this node can handle
     * for a given transferable.
     * <P>
     * Obtain the paste types from the
     * {@link CopySupport.CommandPaste transfer data} and inserts them into the set.
     *
     * @param t a transferable containing clipboard data
     * @param s a list of {@link PasteType}s that will have added to it all types
     *    valid for this node
     */
    protected void createPasteTypes(Transferable t, java.util.List s) {
        if (Children.LEAF.equals(CommandNode.this.getChildren()))
            return;

        boolean copy = t.isDataFlavorSupported(CommandCopySupport.COMMAND_COPY_FLAVOR);
        boolean cut = t.isDataFlavorSupported(CommandCopySupport.COMMAND_CUT_FLAVOR);

        if (copy || cut) { // copy or cut some command
            CommandNode transNode = null;
            try {
                transNode = (CommandNode) t.getTransferData(t.getTransferDataFlavors()[0]);
            }
            catch (UnsupportedFlavorException e) {} // should not happen
            catch (java.io.IOException e) {} // should not happen
            if (this.equals(transNode) || transNode == null)
                return;

            s.add(new CommandCopySupport.CommandPaste(t, this));
        }
    }

    public boolean canDestroy() {
        return true;
    }
        
    public boolean canRename() {
        return true;
    }
    
    private static boolean isFolderCommand(VcsCommand cmd) {
         String[] propertyNames = cmd.getPropertyNames();
         if (propertyNames.length == 0) return true;
         else {
             for (int i = 0; i < propertyNames.length; i++) {
                 if (!FOLDER_COMMAND_PROPERTIES.contains(propertyNames[i])) return false;
             }
         }
         return true;
    }
    
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (cmd == null) set.put(new PropertySupport.Name(this));
        else {
            createStandardProperties(cmd, set);
            if (!isFolderCommand(cmd)) {
                sheet.put(createExpertProperties(cmd));
                sheet.put(createListProperties(cmd));
            }
        }
        return sheet;
    }
    
    private void createStandardProperties(final VcsCommand cmd, final Sheet.Set set) {
        if (cmd == null) {
            set.put(new PropertySupport.ReadOnly("label", String.class, g("CTL_Label"), g("HINT_Label")) {
                        public Object getValue() {
                            return g("CTL_Separator");
                        }
                    });
            set.put(new PropertySupport.ReadOnly("name", String.class, g("CTL_Name"), g("HINT_Name")) {
                        public Object getValue() {
                            return g("CTL_SeparatorName");
                        }
                    });
            return;
                        
        }
        set.put(new PropertySupport.ReadWrite("label", String.class, g("CTL_Label"), g("HINT_Label")) {
                    public Object getValue() {
                        //System.out.println("getLabel: cmd = "+cmd);
                        return cmd.getDisplayName();
                    }
                    
                    public void setValue(Object value) {
                        cmd.setDisplayName((String) value);
                        CommandNode.this.setDisplayName((String) value);
                        //cmd.fireChanged();
                    }
                    
                    public boolean supportsDefaultValue() {
                        return true;
                    }
                    
                    public void restoreDefaultValue() {
                        cmd.setDisplayName(null);
                        CommandNode.this.setDisplayName(null);
                    }
                });
        set.put(new PropertySupport.ReadWrite("name", String.class, g("CTL_Name"), g("HINT_Name")) {
                    public Object getValue() {
                        //System.out.println("getLabel: cmd = "+cmd);
                        return cmd.getName();
                    }
                    
                    public void setValue(Object value) {
                        cmd.setName((String) value);
                        CommandNode.this.setName((String) value);
                        //cmd.fireChanged();
                    }
                });
        if (isFolderCommand(cmd)) {
            addProperties(set, cmd, folder_std_propertyClassTypes, null);
        } else {
            addProperties(set, cmd, stdandard_propertyClassTypes, null);
            //if (VcsCommand.NAME_REFRESH.equals(cmd.getName()) ||
            //    VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName())) {

            //    addProperties(set, cmd, list_propertyClassTypes, new Integer(-1));
            //}
        }
    }
    
    private Sheet.Set createExpertProperties(final VcsCommand cmd) {
        Sheet.Set set = Sheet.createExpertSet();
        addProperties(set, cmd, expert_propertyClassTypes, null);
        return set;
    }
    
    private Sheet.Set createListProperties(final VcsCommand cmd) {
        Sheet.Set set = new Sheet.Set();//Sheet.createExpertSet();
        set.setName("list");
        set.setDisplayName(g("CTL_ListProperties"));
        set.setShortDescription(g("HINT_ListProperties"));
        Table listTypes;
        if (stdlistCmdNames.contains(cmd.getName())) {
            listTypes = stdlist_propertyClassTypes;
        } else {
            listTypes = list_propertyClassTypes;
        }
        addProperties(set, cmd, listTypes, new Integer(-1));
        return set;
    }
    
    private void addProperties(final Sheet.Set set, final VcsCommand cmd,
                               final Map propertyClassTypes, final Object defaultValue) {
        
        for (Iterator it = propertyClassTypes.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String) it.next();
            String label = null;
            try {
                label = g("CTL_"+propertyName);
            } catch (MissingResourceException exc) {
                label = null;
                //exc.printStackTrace();
            }
            //System.out.println("label for property '"+propertyNames[i]+"' = "+label);
            if (label == null) continue;
            String tooltip;
            try {
                tooltip = g("HINT_"+propertyName);
            } catch (MissingResourceException exc) {
                tooltip = "";
            }
            Class valueClass = (Class) propertyClassTypes.get(propertyName);
            if (valueClass == null) continue;
            set.put(new PropertySupport.ReadWrite(
                        propertyName, valueClass,
                        label, tooltip
                    ) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            String name = getName();
                            Object value = cmd.getProperty(name);
                            /*
                            Class valueType = getValueType();
                            if (!(value.getClass().equals(valueType))) {
                                System.out.println("name = "+name+": value = "+value+", class = "+value.getClass()+", value Type = "+getValueType());
                            }
                             */
                            if (value == null) {
                                value = VcsCommandIO.getDefaultPropertyValue(name);
                            }
                            if (value == null) {
                                if (Boolean.TYPE.equals(getValueType())) {
                                    value = Boolean.FALSE;
                                } else if (Integer.TYPE.equals(getValueType()) &&
                                           defaultValue != null &&
                                           Integer.class.equals(defaultValue.getClass())) {
                                    value = defaultValue;
                                }
                            }
                            return value;
                        }

                        public void setValue(Object value) {
                            Object old = getValue();
                            cmd.setProperty(getName(), value);
                            firePropertyChange(getName(), old, value);
                            if (VcsCommand.PROPERTY_INPUT_DESCRIPTOR.equals(getName())) {
                                cmd.setProperty(CommandExecutorSupport.INPUT_DESCRIPTOR_PARSED, null);
                            }
                            //cmd.fireChanged();
                        }
                        
                        public boolean supportsDefaultValue() {
                            return true;
                        }
                        
                        public void restoreDefaultValue() {
                            Object old = getValue();
                            cmd.setProperty(getName(), defaultValue);
                            firePropertyChange(getName(), old, defaultValue);
                            if (VcsCommand.PROPERTY_INPUT_DESCRIPTOR.equals(getName())) {
                                cmd.setProperty(CommandExecutorSupport.INPUT_DESCRIPTOR_PARSED, null);
                            }
                        }
                });
        }
    }

    protected SystemAction [] createActions() {
        ArrayList actions = new ArrayList();
        actions.add(SystemAction.get(MoveUpAction.class));
        actions.add(SystemAction.get(MoveDownAction.class));
        actions.add(null);
        if (getParentNode() != null) {  // Cut/Copy not present on the root node.
            actions.add(SystemAction.get(CutAction.class));
            actions.add(SystemAction.get(CopyAction.class));
        }
        if (!Children.LEAF.equals(getChildren())) { // Paste only on folders
            actions.add(SystemAction.get(PasteAction.class));
        }
        actions.add(null);
        actions.add(SystemAction.get(NewAction.class));
        /*
        DeleteAction delete = (DeleteAction) SystemAction.get(DeleteAction.class);
        //delete.setEnabled(true);
        delete.setActionPerformer(new ActionPerformer() {
            public void performAction(SystemAction action) {
                delete();
            }
        });
         */
        if (getParentNode() != null) {  // Delete not present on the root node.
            actions.add(SystemAction.get(DeleteAction.class));//delete);
        }
        //actions.add(null);
        //actions.add(SystemAction.get(PropertiesAction.class)); -- properties action has caused problems on modal dialogs
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }
    
    /** Get the new types that can be created in this node.
     */
    public NewType[] getNewTypes() {
        //if (list == null) return new NewType[0];
        return new NewType[] { new NewCommand(), new NewSeparator(), new NewFolder() };
    }
    
    /**
     * Deletes the current command.
     */
    public void delete() {
        try {
            destroy();
        } catch (java.io.IOException exc) {
            // silently ignored
        }
    }
    
    /** Tells whether the given name is a name of an existing command.
     *
    private boolean existsCommandName(String name) {
        CommandNode root = this;
        Node parent;
        while ((parent = root.getParentNode()) != null) {
            root = (CommandNode) parent;
        }
        return existsCommandName(name, root);
    }
    
    private static boolean existsCommandName(String name, CommandNode node) {
        VcsCommand cmd = node.getCommand();
        if (cmd == null) return false;
        String cmdName = cmd.getName();
        if (name.equals(cmdName)) return true;
        Children ch = node.getChildren();
        Node[] childrenNodes = ch.getNodes();
        for (int i = 0; i < childrenNodes.length; i++) {
            if (existsCommandName(name, (CommandNode) childrenNodes[i])) return true;
        }
        return false;
    }
     */

    private String g(String s) {
        if (resourceBundle == null) {
            synchronized (this) {
                if (resourceBundle == null) {
                    resourceBundle = NbBundle.getBundle(CommandNode.class);
                }
            }
        }
        return resourceBundle.getString (s);
    }

    private final class CommandsIndex extends org.openide.nodes.Index.Support {
        
        /**
         * Get the nodes sorted by this index.
         * @return the nodes
         */
        public Node[] getNodes() {
            return CommandNode.this.getChildren().getNodes();
        }
        
        /** Get the node count.
         * @return the count
         */
        public int getNodesCount() {
            return getNodes().length;
        }
        
        /*
        Command[] getSubCommands() {
            Node[] subnodes = getNodes();
            Command[] subcommands = new Command[subnodes.length];
            for(int i = 0; i < subnodes.length; i++) {
                subcommands[i] = ((CommandNode) subnodes[i]).cmd;
            }
            return subcommands;
        }
         */

        /** Reorder by permutation.
         * @param perm the permutation
         */
        public void reorder(int[] perm) {
            //System.out.println("reorder("+Command.getOrderString(perm)+")");
            Children children = CommandNode.this.getChildren();
            if (children instanceof Index.ArrayChildren) {
                Index.ArrayChildren achildren = (Index.ArrayChildren) children;
                achildren.reorder(perm);
            }
            //if (list != null) {
            //    list.reorder(getSubCommands(), perm);
                //((CommandChildren) getChildren()).stateChanged(null);
            //}
            //((ComponentContainer)getRADComponent()).reorderSubComponents(perm);
            //((CommandChildren)getChildren()).updateKeys();
        }
    }
    
    private final class NewSeparator extends NewType {
        
        public String getName() {
            return org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewSeparator_ActionName");
        }
        
        public void create() throws java.io.IOException {
            CommandNode newCommand = new CommandNode(Children.LEAF, null);
            Children ch;
            if (Children.LEAF.equals(CommandNode.this.getChildren())) {
                ch = CommandNode.this.getParentNode().getChildren();
            } else {
                ch = CommandNode.this.getChildren();
            }
                /*
                if (ch instanceof Index.ArrayChildren) {
                    ((Index.ArrayChildren) ch).
                 */
            ch.add(new Node[] { newCommand });
        }
    }
    
    private final class NewCommand extends NewType {

        public String getName() {
            return org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommand_ActionName");
        }
        
        public void create() throws java.io.IOException {
            //System.out.println("create new command: cmd = "+cmd);
            //if (list == null) return;
            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandName"),
                org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandTitle")
                //bundle.getString("CTL_NewCategoryName"),
                //bundle.getString("CTL_NewCategoryTitle")
                );
            //input.setInputText(org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandLabel"));
            if (TopManager.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                return;

            String labelName = input.getInputText();
            String name = labelName.toUpperCase();
            if (getAllCommandsNames().contains(name)) {
                NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_CommandNameAlreadyExists")
                );
                TopManager.getDefault().notify(message);
                return ;
            }
            VcsCommand cmd = new UserCommand();
            cmd.setName(name);
            cmd.setDisplayName(labelName);
            cmd.setProperty(VcsCommand.PROPERTY_EXEC, "");
            //String name = list.createUniqueName(label);
            //cmd.setName(name);
            //int[] order = CommandNode.this.cmd.getOrder();
            //int index = ++order[order.length - 1];
            //cmd.setOrder(order);
            CommandNode newCommand = new CommandNode(Children.LEAF, cmd);
            Children ch;
            if (Children.LEAF.equals(CommandNode.this.getChildren())) {
                ch = CommandNode.this.getParentNode().getChildren();
            } else {
                ch = CommandNode.this.getChildren();
            }
                /*
                if (ch instanceof Index.ArrayChildren) {
                    ((Index.ArrayChildren) ch).
                 */
            ch.add(new Node[] { newCommand });
            /*
            System.out.println("create: index = "+index);
            Command[] commands;
            if (CommandNode.this.cmd.getExec() == null) {
                commands = getSubCommands();
            } else {
                CommandNode parent = (CommandNode) getParentNode();
                if (parent == null) throw new java.io.IOException("No Parent");
                commands = parent.getSubCommands();
            }
            int len = commands.length;
            System.out.println("create: commands.length = "+len);
            if (len > 0) {
                int firstOrder[] = commands[0].getOrder();
                index -= firstOrder[firstOrder.length - 1];
                System.out.println("create: commands[0] =  "+commands[0]+", index = "+index);
            }
            int[] perm = new int[len];
            int i = 0;
            for( ; i < index; i++) {
                perm[i] = i;
            }
            for( ; i < len; i++) {
                perm[i] = i + 1;
            }
            list.reorder(commands, perm);
            list.add(cmd);
            list.fireChanged();
             */
        }
    }
    
    private final class NewFolder extends NewType {
        
        public String getName() {
            return org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewFolder_ActionName");
        }
        
        public void create() throws java.io.IOException {
            NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewFolderName"),
                org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewFolderTitle")
                //bundle.getString("CTL_NewCategoryName"),
                //bundle.getString("CTL_NewCategoryTitle")
                );
            //input.setInputText(org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_NewCommandLabel"));
            if (TopManager.getDefault().notify(input) != NotifyDescriptor.OK_OPTION)
                return;

            String labelName = input.getInputText();
            String name = labelName.toUpperCase();
            if (getAllCommandsNames().contains(name)) {
                NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    org.openide.util.NbBundle.getBundle(CommandNode.class).getString("CTL_CommandNameAlreadyExists")
                );
                TopManager.getDefault().notify(message);
                return ;
            }
            VcsCommand cmd = new UserCommand();
            cmd.setName(name);
            cmd.setDisplayName(labelName);
            CommandNode newCommand = new CommandNode(new Children.Array(), cmd);
            Children ch;
            if (Children.LEAF.equals(CommandNode.this.getChildren())) {
                ch = CommandNode.this.getParentNode().getChildren();
            } else {
                ch = CommandNode.this.getChildren();
            }
            ch.add(new Node[] { newCommand });
        }
    }
    
}
