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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.openide.nodes.*;
import org.openide.actions.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.ActionPerformer;
import org.openide.util.datatransfer.NewType;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;

/**
 * The Node representation of a VCS command.
 *
 * @author  Martin Entlicher
 */
public class CommandNode extends AbstractNode {

    private VcsCommand cmd = null;
    private ResourceBundle resourceBundle = null;
    private CommandsIndex index = null;
    
    public static HashMap propertyClassTypes = new HashMap();
    public static HashMap list_propertyClassTypes = new HashMap();
    
    static {
        propertyClassTypes.put(VcsCommand.PROPERTY_ADVANCED_NAME, String.class);
        propertyClassTypes.put(VcsCommand.PROPERTY_CHANGED_REVISION_VAR_NAME, String.class);
        propertyClassTypes.put(VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_CHANGING_REVISION, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_CONCURRENT_EXECUTION, Integer.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_CONFIRMATION_MSG, String.class);
        propertyClassTypes.put(VcsCommand.PROPERTY_EXEC, String.class);
        //propertyClassTypes.put(VcsCommand.PROPERTY_NOT_ON_ROOT, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_NUM_REVISIONS, Integer.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_ON_DIR, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_ON_FILE, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_ON_ROOT, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_PROCESS_ALL_FILES, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER, Boolean.TYPE);
        propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED, String.class);
        propertyClassTypes.put(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED, String.class);
        propertyClassTypes.put(VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT, Boolean.TYPE);
        propertyClassTypes.put(UserCommand.PROPERTY_CHECK_FOR_MODIFICATIONS, Boolean.TYPE);
        propertyClassTypes.put(UserCommand.PROPERTY_DATA_REGEX, String.class);
        propertyClassTypes.put(UserCommand.PROPERTY_ERROR_REGEX, String.class);
        propertyClassTypes.put(UserCommand.PROPERTY_INPUT, String.class);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_ATTR, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_DATE, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_FILE_NAME, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_LOCKER, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_REVISION, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_SIZE, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STATUS, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_STICKY, Integer.TYPE);
        list_propertyClassTypes.put(UserCommand.PROPERTY_LIST_INDEX_TIME, Integer.TYPE);
        //propertyClassTypes.put(UserCommand.PROPERTY_PRECOMMANDS_EXECUTE, Boolean.TYPE);
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
    }

    public void setCommand(VcsCommand cmd) {
        this.cmd = cmd;
        init();
    }
    
    public VcsCommand getCommand() {
        return cmd;
    }
    
    public boolean canCopy() {
        return true;
    }
    
    public boolean canCut() {
        return true;
    }
    
    public boolean canDestroy() {
        return true;
    }
        
    public boolean canRename() {
        return true;
    }
    
    public Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (cmd == null) set.put(new PropertySupport.Name(this));
        else createProperties(cmd, set);
        return sheet;
    }
    
    private void createProperties(final VcsCommand cmd, final Sheet.Set set) {
        if (cmd == null) {
            set.put(new PropertySupport.ReadOnly("label", String.class, g("CTL_Label"), "") {
                        public Object getValue() {
                            return g("CTL_Separator");
                        }
                    });
            set.put(new PropertySupport.ReadOnly("name", String.class, g("CTL_Name"), "") {
                        public Object getValue() {
                            return g("CTL_SeparatorName");
                        }
                    });
            return;
                        
        }
        set.put(new PropertySupport.ReadWrite("label", String.class, g("CTL_Label"), "") {
                    public Object getValue() {
                        //System.out.println("getLabel: cmd = "+cmd);
                        return cmd.getDisplayName();
                    }
                    
                    public void setValue(Object value) {
                        cmd.setDisplayName((String) value);
                        CommandNode.this.setDisplayName((String) value);
                        //cmd.fireChanged();
                    }
                });
        set.put(new PropertySupport.ReadWrite("name", String.class, g("CTL_Name"), "") {
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
        String[] propertyNames = cmd.getPropertyNames();
        if (propertyNames.length != 0) {
            addProperties(set, cmd, propertyClassTypes);
            if (VcsCommand.NAME_REFRESH.equals(cmd.getName()) ||
                VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName())) {

                addProperties(set, cmd, list_propertyClassTypes);
            }
        }
    }
    
    private void addProperties(final Sheet.Set set, final VcsCommand cmd,
                               final HashMap propertyClassTypes) {
        
        for (Iterator it = propertyClassTypes.keySet().iterator(); it.hasNext(); ) {
            String propertyName = (String) it.next();
            String label = null;
            try {
                label = g("CTL_"+propertyName);
            } catch (MissingResourceException exc) {
                label = null;
            }
            //System.out.println("label for property '"+propertyNames[i]+"' = "+label);
            if (label == null) continue;
            Class valueClass = (Class) propertyClassTypes.get(propertyName);
            if (valueClass == null) continue;
            set.put(new PropertySupport.ReadWrite(
                        propertyName, valueClass,
                        label, ""
                    ) {
                        public Object getValue() {
                            //System.out.println("getName: cmd = "+cmd);
                            return cmd.getProperty(getName());
                        }

                        public void setValue(Object value) {
                            cmd.setProperty(getName(), value);
                            //cmd.fireChanged();
                        }
                });
        }
    }

    protected SystemAction [] createActions() {
        ArrayList actions = new ArrayList();
        actions.add(SystemAction.get(MoveUpAction.class));
        actions.add(SystemAction.get(MoveDownAction.class));
        actions.add(null);
        actions.add(SystemAction.get(NewAction.class));
        DeleteAction delete = (DeleteAction) SystemAction.get(DeleteAction.class);
        //delete.setEnabled(true);
        delete.setActionPerformer(new ActionPerformer() {
            public void performAction(SystemAction action) {
                delete();
            }
        });
        if (getParentNode() != null) {  // Delete not present on the root node.
            actions.add(delete);
        }
        actions.add(null);
        actions.add(SystemAction.get(PropertiesAction.class));
        SystemAction[] array = new SystemAction [actions.size()];
        actions.toArray(array);
        return array;
    }
    
    /**
     * Deletes the current command.
     */
    public void delete() {
    }

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
    
}
