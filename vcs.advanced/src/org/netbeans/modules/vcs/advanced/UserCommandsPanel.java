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

package org.netbeans.modules.vcs.advanced;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.MessageFormat;

import org.openide.util.NbBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.explorer.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.Debug;

import org.netbeans.modules.vcs.advanced.commands.*;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandsTree;
import org.netbeans.spi.vcs.commands.CommandSupport;

/** User commands panel.
 * 
 * @author Martin Entlicher
 */
//-------------------------------------------
public class UserCommandsPanel extends JPanel
    implements CommandChangeListener, EnhancedCustomPropertyEditor,
                ExplorerManager.Provider {

    private Debug E=new Debug("UserCommandsPanel", true); // NOI18N
    private Debug D=E;

    private UserCommandsEditor editor;

    //private Vector commands=null;
    //private CommandList commandList = null;
    private CommandNode commandsNode = null;
    
    private ExplorerManager manager = null;
    
    private transient VcsFileSystem fileSystem;

    static final long serialVersionUID =-5546375234297504708L;

    /**
     * The panel of user commands.
     * @param editor The editor of UserCommand instances passed through CommandNodes
     * @param fileSystem The VCS filesystem instance, that is used for
     *        UserCommandSupport creation, that wrapps UserCommand.
     */
    public UserCommandsPanel(UserCommandsEditor editor) {
        this.editor = editor;
        CommandsTree commands = (CommandsTree) editor.getValue();
        CommandSupport supp = commands.getCommandSupport();
        UserCommand oldcmd = null;
        if (supp != null && supp instanceof UserCommandSupport) {
            oldcmd = ((UserCommandSupport) supp).getVcsCommand();
            fileSystem = ((UserCommandSupport) supp).getVcsFileSystem();
        }
        //VcsCommand oldcmd = (VcsCommand) commands.getCookie(VcsCommand.class);
        UserCommand newcmd = null;
        if (oldcmd != null) {
            newcmd = new UserCommand();
            newcmd.copyFrom(oldcmd);
        }
        commandsNode = createCommandNodes(commands, newcmd);
        initComponents();
        getExplorerManager().setRootContext(commandsNode/*createNodes()*/);
        ExplorerActions actions = new ExplorerActions();
        actions.attach(getExplorerManager());
        HelpCtx.setHelpIDString (this, "VCS_CommandEditor"); // NOI18N
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
    
    private CommandNode createCommandNodes(CommandsTree oldCommands, UserCommand cmd) {
        Children newChildren = new Index.ArrayChildren();
        
        CommandNode newCommands = new CommandNode(newChildren, cmd);
        CommandsTree[] oldNodes = oldCommands.children();
        for(int i = 0; i < oldNodes.length; i++) {
            CommandSupport supp = oldNodes[i].getCommandSupport();
            UserCommand newcmd = null;//oldcmd;
            if (supp != null && (supp instanceof UserCommandSupport)) {
                newcmd = new UserCommand();
                newcmd.copyFrom(((UserCommandSupport) supp).getVcsCommand());
            }
            CommandNode newNode;
            if (!oldNodes[i].hasChildren()) {
                newNode = new CommandNode(Children.LEAF, newcmd);
            } else {
                newNode = createCommandNodes(oldNodes[i], newcmd);
            }
            newChildren.add(new Node[] { newNode });
        }
        return newCommands;
    }
    
    private CommandsTree createCommands(CommandNode oldCommands, UserCommand cmd, VcsFileSystem fileSystem) {
        Children oldChildren = oldCommands.getChildren();
        CommandsTree newCommands = new CommandsTree(new UserCommandSupport(cmd, fileSystem));
        
        Node[] oldNodes = oldChildren.getNodes();
        for(int i = 0; i < oldNodes.length; i++) {
            UserCommand oldcmd = (UserCommand) ((CommandNode) oldNodes[i]).getCommand();
            UserCommand newcmd = null;
            if (oldcmd != null) {
                newcmd = new UserCommand();
                newcmd.copyFrom(oldcmd);
            }
            Children subChildren = oldNodes[i].getChildren();
            CommandsTree newNode;
            if (newcmd == null) {
                newNode = CommandsTree.EMPTY;
            } else {
                if (Children.LEAF.equals(subChildren)) {
                    newNode = new CommandsTree(new UserCommandSupport(newcmd, fileSystem));
                } else {
                    newNode = createCommands((CommandNode) oldNodes[i], newcmd, fileSystem);
                }
            }
            newCommands.add(newNode);
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
    }
    
    public ExplorerManager getExplorerManager() {
        if (manager == null) {
            synchronized(this) {
                if (manager == null) {
                    manager = new ExplorerManager();
                }
            }
        }
        return manager;
    }
    
    //-------------------------------------------
    public Object getPropertyValue() {
        return createCommands(commandsNode, (UserCommand) commandsNode.getCommand(), fileSystem);
    }


    //-------------------------------------------
    private String g(String s) {
        return NbBundle.getMessage(UserCommandsPanel.class, s);
    }


}
