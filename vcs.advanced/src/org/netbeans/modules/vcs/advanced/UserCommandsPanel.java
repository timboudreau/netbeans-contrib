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
import org.openide.nodes.*;
import org.openide.explorer.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.HelpCtx;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandNode;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.Debug;

import org.netbeans.modules.vcs.advanced.commands.*;

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

    static final long serialVersionUID =-5546375234297504708L;

    //-------------------------------------------
    public UserCommandsPanel(UserCommandsEditor editor) {
        this.editor = editor;
        Node commands = (Node) editor.getValue();
        VcsCommand oldcmd = (VcsCommand) commands.getCookie(VcsCommand.class);
        VcsCommand newcmd = null;
        if (oldcmd != null && oldcmd instanceof UserCommand) {
            newcmd = new UserCommand();
            ((UserCommand) newcmd).copyFrom(oldcmd);
        }
        commandsNode = createCommandNodes(commands, newcmd);
        //Vector oldCommands = (Vector) editor.getValue();
        //commands = deepCopy(oldCommands);
        //D.deb("UserCommandsPanel() commands = "+commands); // NOI18N
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
    
    /*
    private Node deepCopy(VcsCommandNode oldCommands) {
        //int len=oldCommands.size();
        Children oldChildren = oldCommands.getChildren();
        Children newChildren = new Children.Array();
        
        Node newCommands = new VcsCommandNode(children, (VcsCommand) oldCommands.getCookie(VcsCommand.class));
        Node[] oldNodes = oldChildren.getNodes();
        //int[] lastOrder = {0};
        //D.deb("deepCopy():");
        for(int i = 0; i < oldNodes.length; i++) {
            UserCommand oldcmd = (UserCommand) oldCommands.elementAt(i);
            //System.out.println("deepCopy: have command = "+oldcmd);
            Command cmd = new Command();
            cmd.copyFrom(oldcmd);
            cmd.setListener(this);
            int order[] = cmd.getOrder();
            //D.deb("i = "+i+", lastOrder = "+UserCommand.getOrderString(lastOrder));
            //D.deb("Have cmd = "+cmd);
            int length = order.length;
            //boolean orderIncreased = false;
            //int[] lastOrderOrig = lastOrder;
            if (lastOrder.length < length) {
                int[] lastOrder1 = new int[length];
                for(int j = 0; j < lastOrder.length; j++) {
                    lastOrder1[j] = lastOrder[j];
                }
                for(int j = lastOrder.length; j < length; j++) {
                    lastOrder1[j] = 0;
                }
                lastOrder = lastOrder1;
                //orderIncreased = true;
                //D.deb("new lastOrder = "+UserCommand.getOrderString(lastOrder));
            } else if (lastOrder.length > length) {
                int[] lastOrder1 = new int[length];
                for(int j = 0; j < length; j++) {
                    lastOrder1[j] = lastOrder[j];
                }
                lastOrder = lastOrder1;
            }
            //if (orderIncreased) length--;
            for(int k = 0; k <= length - 1; k++) {
                for( ; ++lastOrder[k] < order[k]; ) {
                    //D.deb("k = "+k+", j = "+j+", adding separator");
                    Command sepCmd = new Command();
                    sepCmd.setListener(this);
                    sepCmd.setSeparator(true);
                    sepCmd.setDisplayName(g("CTL_COMMAND_SEPARATOR"));
                    if (k < length - 1) {
                        int[] newOrder = new int[k + 1];
                        for(int j = 0; j < k + 1; j++) newOrder[j] = lastOrder[j];
                    //if (orderIncreased) {
                        //lastOrderOrig[k]++;
                        sepCmd.setOrder(newOrder);
                    } else {
                        sepCmd.setOrder(lastOrder);
                    }
                    newCommands.addElement(sepCmd);
                    //System.out.println("deepCopy: -> add command = "+sepCmd);
                }
                lastOrder[k] = order[k];
            }
            //if (orderIncreased) lastOrder[length] = order[length];
            //System.out.println("deepCopy: -> set command = "+cmd);
            newCommands.addElement(cmd);
            //D.deb("adding the command");
            //refCommands.addElement(new Integer(i));
        }
        return newCommands;
    }
     */

    /*
    private CommandNode createNodes() {
        CommandList list = new CommandList();
        this.commandList = list;
        CommandNode node = new CommandNode(list);
        //node.setName((String) vars.get("FILE"));
        if (commands.size() > 0) {
            Command cmd = (Command) commands.get(0);
            if (cmd.getPropertyNames().length == 0) {
                node.setCommand(cmd);
                node.setName(cmd.getName());
                node.setDisplayName(cmd.getDisplayName());
            }
        }
        //CommandExplorer explorer = new CommandExplorer(node);
        for(Enumeration enum = commands.elements(); enum.hasMoreElements(); ) {
            list.add(enum.nextElement());
        }
        return node;
    }
     */
    
    private CommandNode createCommandNodes(Node oldCommands, VcsCommand cmd) {
        Children oldChildren = oldCommands.getChildren();
        Children newChildren = new Index.ArrayChildren();
        
        CommandNode newCommands = new CommandNode(newChildren, cmd);
        Node[] oldNodes = oldChildren.getNodes();
        for(int i = 0; i < oldNodes.length; i++) {
            VcsCommand oldcmd = (VcsCommand) oldNodes[i].getCookie(VcsCommand.class);
            VcsCommand newcmd = oldcmd;
            if (oldcmd instanceof UserCommand) {
                //try {
                newcmd = new UserCommand();
                ((UserCommand) newcmd).copyFrom(oldcmd);
                    //newcmd = (VcsCommand) ((UserCommand) oldcmd).clone();
                //} catch (CloneNotSupportedException exc) {
                //    newcmd = oldcmd;
                //}
            }
            Children subChildren = oldNodes[i].getChildren();
            CommandNode newNode;
            if (Children.LEAF.equals(subChildren)) {
                newNode = new CommandNode(Children.LEAF, newcmd);
            } else {
                newNode = createCommandNodes(oldNodes[i], newcmd);
            }
            newChildren.add(new Node[] { newNode });
        }
        return newCommands;
    }
    
    private Node createCommands(CommandNode oldCommands, VcsCommand cmd) {
        Children oldChildren = oldCommands.getChildren();
        Children newChildren = new Children.Array();
        
        Node newCommands = new VcsCommandNode(newChildren, cmd);
        Node[] oldNodes = oldChildren.getNodes();
        for(int i = 0; i < oldNodes.length; i++) {
            VcsCommand oldcmd = ((CommandNode) oldNodes[i]).getCommand();
            VcsCommand newcmd = oldcmd;
            if (oldcmd instanceof UserCommand) {
                newcmd = new UserCommand();
                ((UserCommand) newcmd).copyFrom(oldcmd);
                //try {
                //    newcmd = (VcsCommand) ((Cloneable) oldcmd).clone();
                //} catch (CloneNotSupportedException exc) {
                //    newcmd = oldcmd;
                //}
            }
            Children subChildren = oldNodes[i].getChildren();
            VcsCommandNode newNode;
            if (Children.LEAF.equals(subChildren)) {
                newNode = new VcsCommandNode(Children.LEAF, newcmd);
            } else {
                newNode = (VcsCommandNode) createCommands((CommandNode) oldNodes[i], newcmd);
            }
            newChildren.add(new Node[] { newNode });
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
        split.add(beanTreeView, org.openide.awt.SplittedPanel.ADD_LEFT);
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
        //D.deb("getPropertyValue() -->"+commands);
        /*
        Vector cmds = new Vector();
        if (commandList != null) {
            for(Iterator it = commandList.iterator(); it.hasNext(); ) {
                Command cmd = (Command) it.next();
                if (!cmd.isSeparator()) cmds.addElement((UserCommand) cmd);
            }
        } else {
            int len = commands.size();
            for(int i = 0; i < len; i++) {
                Command cmd = (Command) commands.get(i);
                if (!cmd.isSeparator()) cmds.addElement((UserCommand) cmd);
            }
        }
        //D.deb("getPropertyValue(): cmds = "+cmds);
        return cmds;
         */
        return createCommands(commandsNode, commandsNode.getCommand());
    }


    //-------------------------------------------
    String g(String s) {
        return NbBundle.getBundle
               ("org.netbeans.modules.vcs.advanced.Bundle").getString (s);
    }
    String  g(String s, Object obj) {
        return MessageFormat.format (g(s), new Object[] { obj });
    }
    String g(String s, Object obj1, Object obj2) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2 });
    }
    String g(String s, Object obj1, Object obj2, Object obj3) {
        return MessageFormat.format (g(s), new Object[] { obj1, obj2, obj3 });
    }
    //-------------------------------------------


}
