/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.MessageFormat;
import javax.swing.text.DefaultEditorKit;

import org.openide.util.NbBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.explorer.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.Debug;

import org.netbeans.modules.vcs.advanced.commands.*;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcs.advanced.commands.ConditionedCommandsBuilder.*;
import org.netbeans.modules.vcs.advanced.variables.Condition;

/** User commands panel.
 * 
 * @author Martin Entlicher
 */
//-------------------------------------------
public class UserConditionedCommandsPanel extends JPanel implements CommandChangeListener,
                                                                    EnhancedCustomPropertyEditor,
                                                                    ExplorerManager.Provider,
                                                                    Lookup.Provider {

    private UserConditionedCommandsEditor editor;

    //private Vector commands=null;
    //private CommandList commandList = null;
    private CommandNode commandsNode = null;
    private ConditionedCommands ccommands = null;
    
    private ExplorerManager manager;
    private Lookup lookup;
    
    private transient CommandExecutionContext executionContext;

    static final long serialVersionUID =-5546375234297504708L;

    /**
     * The panel of user commands.
     * @param editor The editor of UserCommand instances passed through CommandNodes
     * @param executionContext The execution context instance, that is used for
     *        UserCommandSupport creation, that wrapps UserCommand.
     */
    public UserConditionedCommandsPanel(UserConditionedCommandsEditor editor) {
        this.editor = editor;
        ccommands = (ConditionedCommands) editor.getValue();
        CommandsTree commands = ccommands.getCommands();
        CommandSupport supp = commands.getCommandSupport();
        UserCommand oldcmd = null;
        if (supp != null && supp instanceof UserCommandSupport) {
            oldcmd = ((UserCommandSupport) supp).getVcsCommand();
            executionContext = ((UserCommandSupport) supp).getExecutionContext();
        }
        //VcsCommand oldcmd = (VcsCommand) commands.getCookie(VcsCommand.class);
        UserCommand newcmd = null;
        if (oldcmd != null) {
            newcmd = new UserCommand();
            newcmd.copyFrom(oldcmd);
        }
        /* The first command can not be conditioned
        ConditionedCommand ccommand = null;
        if (supp != null) {
            ccommand = ccommands.getConditionedCommand(supp.getName());
        }
         */
        commandsNode = createCommandNodes(commands, newcmd, null, null);
        initComponents();
        getExplorerManager().setRootContext(commandsNode/*createNodes()*/);
        getAccessibleContext().setAccessibleName(g("ACS_UserCommandsPanelA11yName"));  // NOI18N
        getAccessibleContext().setAccessibleDescription(g("ACS_UserCommandsPanelA11yDesc"));  // NOI18N
    }

    /** Called when the command is changed.
     */
    public void changed(VcsCommand cmd) {
        editor.setValue(getPropertyValue());
    }
    
    /** Called when new command is added.
     */
    public void added(VcsCommand cmd) {
        editor.setValue(getPropertyValue());
    }
    
    /** Called when the command is removed.
     */
    public void removed(VcsCommand cmd) {
        editor.setValue(getPropertyValue());
    }
    
    private CommandNode createCommandNodes(CommandsTree oldCommands, UserCommand cmd,
                                           Condition c, ConditionedPropertiesCommand cpc) {
        Children newChildren = new Index.ArrayChildren();
        
        CommandNode newCommands = new CommandNode(newChildren, cmd, c, cpc);
        //CommandsTree oldCommands = ccommands.getCommands();
        CommandsTree[] oldNodes = oldCommands.children();
        for(int i = 0; i < oldNodes.length; i++) {
            CommandSupport supp = oldNodes[i].getCommandSupport();
            UserCommand newcmd = null;//oldcmd;
            if (supp != null && (supp instanceof UserCommandSupport)) {
                newcmd = new UserCommand();
                newcmd.copyFrom(((UserCommandSupport) supp).getVcsCommand());
            }
            ConditionedCommand ccommand = null;
            if (supp != null) {
                ccommand = ccommands.getConditionedCommand(supp.getName());
                if (ccommand == null) {
                    ccommand = new ConditionedCommand(supp.getName());
                    ccommand.addCommand(new ConditionedPropertiesCommand((UserCommandSupport) supp), null);
                }
            }
            Node[] newNodes;
            if (!oldNodes[i].hasChildren()) {
                if (ccommand != null) {
                    Condition[] conditions = ccommand.getConditions();
                    newNodes = new CommandNode[conditions.length];
                    for (int j = 0; j < conditions.length; j++) {
                        newNodes[j] = new CommandNode(Children.LEAF, newcmd, conditions[j], ccommand.getCommandFor(conditions[j]));
                    }
                } else {
                    newNodes = new CommandNode[] { new CommandNode(Children.LEAF, newcmd) };
                }
            } else {
                if (ccommand != null) {
                    Condition[] conditions = ccommand.getConditions();
                    newNodes = new CommandNode[conditions.length];
                    for (int j = 0; j < conditions.length; j++) {
                        newNodes[j] = createCommandNodes(oldNodes[i], newcmd, conditions[j], ccommand.getCommandFor(conditions[j]));
                    }
                } else {
                    newNodes = new CommandNode[] { createCommandNodes(oldNodes[i], newcmd, null, null) };
                }
            }
            newChildren.add(newNodes);
        }
        return newCommands;
    }
    
    private ConditionedCommands createCommands(CommandNode oldCommands, UserCommand cmd, CommandExecutionContext executionContext) {
        Children oldChildren = oldCommands.getChildren();
        CommandsTree newCommands = new CommandsTree(new UserCommandSupport(cmd, executionContext));
        ConditionedCommandsBuilder ccbuilder = new ConditionedCommandsBuilder(newCommands);
        createCommandsTree(oldCommands, newCommands, executionContext, ccbuilder);
        return ccbuilder.getConditionedCommands();
    }

    private CommandsTree createCommandsTree(CommandNode oldCommands, CommandsTree newCommands,
                                            CommandExecutionContext executionContext,
                                            ConditionedCommandsBuilder ccbuilder) {
        Children oldChildren = oldCommands.getChildren();
        Node[] oldNodes = oldChildren.getNodes();
        for(int i = 0; i < oldNodes.length; i++) {
            CommandNode commandNode = (CommandNode) oldNodes[i];
            UserCommand newcmd = (UserCommand) commandNode.getCommand();
            Children subChildren = commandNode.getChildren();
            CommandsTree newNode;
            if (newcmd == null) {
                newNode = CommandsTree.EMPTY;
            } else {
                if (Children.LEAF.equals(subChildren)) {
                    newNode = new CommandsTree(new UserCommandSupport(newcmd, executionContext));
                } else {
                    newNode = new CommandsTree(new UserCommandSupport(newcmd, executionContext));
                    createCommandsTree(commandNode, newNode, executionContext, ccbuilder);
                }
            }
            newCommands.add(newNode);
            Condition mainC = commandNode.getMainCondition();
            Collection conditionedProperties = commandNode.getConditionedProperties();
            //ConditionedPropertiesCommand cpcommand = commandNode.getConditionedPropertiesCommand();
            //ConditionedProperty[] cproperties = cpcommand.getConditionedProperties();
            //for (int p = 0; p < cproperties.length; p++) {
            if (conditionedProperties != null) {
                for (Iterator it = conditionedProperties.iterator(); it.hasNext(); ) {
                    ccbuilder.addPropertyToCommand(commandNode.getCommand().getName(), mainC, (ConditionedProperty) it.next());
                }
            }
        }
        return newCommands;
    }

    public void initComponents(){
        GridBagLayout gb=new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);
        //setBorder(new TitledBorder(g("CTL_Commands")));
        setBorder(new EmptyBorder (12, 12, 0, 11));
        
        PropertySheetView propertySheetView = new PropertySheetView();
        try {
            propertySheetView.setSortingMode(org.openide.explorer.propertysheet.PropertySheet.UNSORTED);
        } catch (java.beans.PropertyVetoException exc) {
            // The change was vetoed
        }
        org.openide.awt.SplittedPanel split = new org.openide.awt.SplittedPanel();
        split.setSplitType(org.openide.awt.SplittedPanel.HORIZONTAL);
        //split.add(new CommandTreeView(), org.openide.awt.SplittedPanel.ADD_LEFT);
        org.openide.explorer.view.BeanTreeView beanTreeView = new org.openide.explorer.view.BeanTreeView();
        beanTreeView.getAccessibleContext().setAccessibleName(g("ACS_UserCommandsTreeViewA11yName"));  // NOI18N
        beanTreeView.getAccessibleContext().setAccessibleDescription(g("ACS_UserCommandsTreeViewA11yDesc"));  // NOI18N
        ExplorerPanel explPanel = new ExplorerPanel();
        explPanel.add(beanTreeView);
        manager = explPanel.getExplorerManager();
        split.add(explPanel, org.openide.awt.SplittedPanel.ADD_LEFT);
        split.add(propertySheetView, org.openide.awt.SplittedPanel.ADD_RIGHT);
        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new CommandTreeView(), propertySheetView);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(split, c);
        
        ActionMap map = getActionMap();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true));

        InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        keys.put(KeyStroke.getKeyStroke("control c"), DefaultEditorKit.copyAction);
        keys.put(KeyStroke.getKeyStroke("control x"), DefaultEditorKit.cutAction);
        keys.put(KeyStroke.getKeyStroke("control v"), DefaultEditorKit.pasteAction);
        keys.put(KeyStroke.getKeyStroke("DELETE"), "delete");

        // initialize the lookup variable
        lookup = ExplorerUtils.createLookup (manager, map);
    }
    
    // It is good idea to switch all listeners on and off when the
    // component is shown or hidden.
    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(manager, true);
    }
    
    public void removeNotify() {
        ExplorerUtils.activateActions(manager, false);
        super.removeNotify();
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public Lookup getLookup() {
        return lookup;
    }

    //-------------------------------------------
    public Object getPropertyValue() {
        return createCommands(commandsNode, (UserCommand) commandsNode.getCommand(), executionContext);
    }


    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserConditionedCommandsPanel.class, s);
    }

}
