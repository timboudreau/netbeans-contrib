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
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.text.*;

import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.*;
import org.openide.*;

import org.netbeans.modules.vcs.util.*;
import org.netbeans.modules.vcs.cmdline.*;

/** User commands panel.
 * 
 * @author Michal Fadljevic, Martin Entlicher
 */
//-------------------------------------------
public class UserCommandsPanel extends JPanel
    implements EnhancedCustomPropertyEditor {

    private Debug E=new Debug("UserCommandsPanel", true); // NOI18N
    private Debug D=E;

    //private JList list=null;
    //private DefaultListModel listModel=null;
    private JTree tree = null;
    private DefaultTreeModel treeModel = null;
    private JButton editButton=null;
    private JButton addButton=null;
    private JButton addFolderButton=null;
    private JButton addSeparatorButton=null;
    private JButton removeButton=null;
    private JButton moveUpButton=null;
    private JButton moveDownButton=null;

    private UserCommandsEditor editor;

    private Vector commands=null;
    //private Vector refCommands=new Vector();

    static final long serialVersionUID =-5546375234297504708L;

    //-------------------------------------------
    public UserCommandsPanel(UserCommandsEditor editor){
        this.editor = editor;
        Vector oldCommands=(Vector)editor.getValue();
        commands=deepCopy(oldCommands/*, refCommands*/);
        D.deb("UserCommandsPanel() commands = "+commands); // NOI18N
        initComponents();
        initListeners();
        deselectAll();
    }

    //-------------------------------------------
    private Vector deepCopy(Vector oldCommands/*, Vector refCommands*/){
        int len=oldCommands.size();
        Vector newCommands=new Vector(len);
        int[] lastOrder = {0};
        D.deb("deepCopy():");
        for(int i=0; i<len; i++){
            UserCommand cmd = (UserCommand) oldCommands.elementAt(i);
            int order[] = cmd.getOrder();
            D.deb("i = "+i+", lastOrder = "+UserCommand.getOrderString(lastOrder));
            D.deb("Have cmd = "+cmd);
            int length = order.length;
            if (lastOrder.length < length) {
                int[] lastOrder1 = new int[length];
                for(int j = 0; j < lastOrder.length; j++) {
                    lastOrder1[j] = lastOrder[j];
                }
                for(int j = lastOrder.length; j < length; j++) {
                    lastOrder1[j] = 0;
                }
                lastOrder = lastOrder1;
                D.deb("new lastOrder = "+UserCommand.getOrderString(lastOrder));
            }
            for(int k = length - 1; k >= 0; k--) {
                for(int j = lastOrder[k] + 1; j < order[k]; j++) {
                    D.deb("k = "+k+", j = "+j+", adding separator");
                    newCommands.addElement(null);
                }
                lastOrder[k] = order[k];
            }
            newCommands.addElement(cmd.clone());
            D.deb("adding the command");
            //refCommands.addElement(new Integer(i));
        }
        return newCommands;
    }

    //-------------------------------------------
    private JButton createButton(String name){
        JButton button = new JButton(name);
        return button;
    }

    /*
    //-------------------------------------------
    private JScrollPane createList(){
      list=new JList();
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      listModel=new DefaultListModel();
      list.setModel(listModel);
      int len=commands.size();
      for(int i=0;i<len;i++){
        UserCommand uc=(UserCommand)commands.elementAt(i);
        if (uc == null) listModel.addElement(g("CTL_COMMAND_SEPARATOR")); // NOI18N
        else listModel.addElement(uc.toString());
      }
      JScrollPane listScrollPane = new JScrollPane(list);
      return listScrollPane;
}
    */

    private int addNodes2Tree(Vector commands, int from, int[] lastOrder, DefaultMutableTreeNode node) {
        int len = commands.size();
        int l = lastOrder.length;
        int i;
        int numSeparators = 0;
        //int lastOrderEnd = 0;
        D.deb("addNodes2Tree() from = "+from+", lastOrder = "+UserCommand.getOrderString(lastOrder));
        //System.out.println("addNodes2Tree() from = "+from+", lastOrder = "+UserCommand.getOrderString(lastOrder));
        for(i = from; i < len; i++) {
            UserCommand uc = (UserCommand) commands.elementAt(i);
            //System.out.println("i = "+i+", uc = "+uc+", numSep = "+numSeparators);
            Object item = null;
            if (uc == null) {
                numSeparators++;
                continue;
            }
            if (uc != null) {
                int[] order = uc.getOrder();
                D.deb("Have command = "+uc+"\n     order = "+UserCommand.getOrderString(order));
                //System.out.println("Have command = "+uc+"\n     order = "+UserCommand.getOrderString(order));
                if (order.length <= l) break;
                int j = 0;
                for(; j < l; j++) {
                    if (lastOrder[j] != order[j]) break;
                }
                if (j < l) break;
                if (numSeparators > 0) {
                    item = g("CTL_COMMAND_SEPARATOR");
                    DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(item);
                    subnode.setAllowsChildren(false);
                    for( ; numSeparators > 0; numSeparators--) {
                        node.add(subnode);
                        //System.out.println("Adding to tree: i = "+i+", numSep = "+numSeparators+", numChild = "+
                        //                    node.getChildCount()+", numChild(tree) = "+treeModel.getChildCount(node));
                        //treeModel.insertNodeInto(subnode, node, treeModel.getChildCount(node));
                        //treeModel.reload();
                    }
                }
                item = uc;
                DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(item);
                subnode.setAllowsChildren(uc.getExec() == null);
                if (order.length >= l + 2) {
                    int[] suborder = new int[order.length - 1];
                    for(int k = 0; k < order.length - 1; k++) {
                        suborder[k] = order[k];
                    }
                    i += addNodes2Tree(commands, i + 1, suborder, subnode);
                }
                D.deb("Adding "+item);
                node.add(subnode);
                //System.out.println("Adding to tree: i = "+i+", numSep = "+numSeparators+", numChild = "+
                //                    node.getChildCount()+", numChild(tree) = "+treeModel.getChildCount(node));
                //treeModel.insertNodeInto(subnode, node, node.getChildCount());
            }
        }
        D.deb("returning "+(i - from - numSeparators));
        return i - from - numSeparators;
    }

    private JScrollPane createTree() {
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
        int len=commands.size();
        int i = 0;
        if (len > 0) {
            UserCommand uc = (UserCommand) commands.elementAt(i);
            if (uc.getExec() == null) {
                treeRoot = new DefaultMutableTreeNode(uc);
                i++;
            }
        }
        int[] lastOrder = new int[0];
        treeModel = new DefaultTreeModel(treeRoot);
        treeModel.setAsksAllowsChildren(true);
        addNodes2Tree(commands, i, lastOrder, treeRoot);
        treeModel.reload();
        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        return treeScrollPane;
    }

    //-------------------------------------------
    private JPanel createCommands(){
        addButton=createButton(g("CTL_Add")); // NOI18N
        addButton.setMnemonic(KeyEvent.VK_D);
        addFolderButton=createButton(g("CTL_AddFolder")); // NOI18N
        addFolderButton.setMnemonic(KeyEvent.VK_F);
        addSeparatorButton=createButton(g("CTL_Add_Separator")); // NOI18N
        addSeparatorButton.setMnemonic(KeyEvent.VK_S);
        editButton=createButton(g("CTL_Edit")); // NOI18N
        editButton.setMnemonic(KeyEvent.VK_T);
        removeButton=createButton(g("CTL_Remove")); // NOI18N
        removeButton.setMnemonic(KeyEvent.VK_M);
        moveUpButton=createButton(g("CTL_MoveUp")); // NOI18N
        //moveUpButton.setMnemonic(KeyEvent.VK_KP_UP);
        moveDownButton=createButton(g("CTL_MoveDown")); // NOI18N
        //moveDownButton.setMnemonic(KeyEvent.VK_KP_DOWN);

        GridLayout panel2Layout=new GridLayout(7,1);
        panel2Layout.setVgap(5);

        JPanel panel2=new JPanel();
        panel2.setLayout(panel2Layout);
        panel2.setBorder(new EmptyBorder(5, 7, 5, 7));

        panel2.add(addButton);
        panel2.add(addFolderButton);
        panel2.add(addSeparatorButton);
        panel2.add(editButton);
        panel2.add(removeButton);
        panel2.add(moveUpButton);
        panel2.add(moveDownButton);

        JPanel panel=new JPanel(new BorderLayout());
        panel.add(panel2,BorderLayout.NORTH);
        return panel;
    }

    //-------------------------------------------
    public void initComponents(){
        GridBagLayout gb=new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);
        setBorder(new TitledBorder("Commands"));

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        JScrollPane treeScrollPane=createTree();
        gb.setConstraints(treeScrollPane, c);
        add(treeScrollPane);

        c = new GridBagConstraints();
        JPanel commandPanel=createCommands();
        //c.fill = GridBagConstraints.BOTH;
        //c.weightx = 0.1;
        //c.weighty = 1.0;

        gb.setConstraints(commandPanel,c);
        add(commandPanel);
        Dimension preferred = treeScrollPane.getPreferredSize();
        preferred.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width*0.8), preferred.height);
        treeScrollPane.setPreferredSize(preferred);
    }

    //-------------------------------------------
    private void initListeners(){
        /*
        list.addListSelectionListener(new ListSelectionListener(){
          public void valueChanged(ListSelectionEvent e){
        //D.deb("valueChanged "+e); // NOI18N
        updateButtons();
          }
    });
        */
        tree.addTreeSelectionListener(new TreeSelectionListener() {
                                          public void valueChanged(TreeSelectionEvent e) {
                                              updateButtons();
                                          }
                                      });

        tree.addMouseListener(new MouseAdapter() {
                                  public void mouseClicked(MouseEvent e){
                                      if( e.getClickCount()==2 ){
                                          editCommand();
                                      }
                                      updateButtons();
                                  }
                              });

        tree.addKeyListener(new KeyAdapter() {
                                public void keyPressed(KeyEvent e){
                                    //D.deb("keyPressed() e="+e); // NOI18N
                                    int keyCode=e.getKeyCode();
                                    switch( keyCode ){
                                    case KeyEvent.VK_INSERT:
                                        addCommand();
                                        //TODO better insertVariable(int index)
                                        break;
                                    case KeyEvent.VK_DELETE:
                                        removeCommand();
                                        break;
                                    case KeyEvent.VK_ENTER:
                                        editCommand();
                                        break;
                                    default:
                                        //D.deb("ignored keyCode="+keyCode); // NOI18N
                                    }
                                    updateButtons();
                                }
                            });

        editButton.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e){
                                             editCommand();
                                         }
                                     });

        addButton.addActionListener(new ActionListener(){
                                        public void actionPerformed(ActionEvent e){
                                            addCommand();
                                        }
                                    });

        addFolderButton.addActionListener(new ActionListener(){
                                              public void actionPerformed(ActionEvent e){
                                                  addFolderCommand();
                                              }
                                          });

        addSeparatorButton.addActionListener(new ActionListener(){
                                                 public void actionPerformed(ActionEvent e){
                                                     addSeparatorCommand();
                                                 }
                                             });

        removeButton.addActionListener(new ActionListener(){
                                           public void actionPerformed(ActionEvent e){
                                               removeCommand();
                                           }
                                       });

        moveUpButton.addActionListener(new ActionListener(){
                                           public void actionPerformed(ActionEvent e){
                                               moveUpCommand();
                                           }
                                       });

        moveDownButton.addActionListener(new ActionListener(){
                                             public void actionPerformed(ActionEvent e){
                                                 moveDownCommand();
                                             }
                                         });
    }


    //-------------------------------------------
    private void deselectAll(){
        //list.clearSelection();
        tree.clearSelection();
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
    }


    //-------------------------------------------
    private void updateButtons(){
        /*
        int index = list.getSelectedIndex();
        if (index < 0) {
          deselectAll();
    }
        */
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            deselectAll();
        } else {
            removeButton.setEnabled(true);
            editButton.setEnabled(true);
            moveUpButton.setEnabled(true/* index > 0*/);
            moveDownButton.setEnabled(true/*index < (listModel.getSize() - 1)*/);
            tree.requestFocus();
        }
    }

    /**
     * Get the absolut row number of the last node in the path
     * @param path the path
     * @return the row number or -1 if the last node of the path is not found
     */
    private int getAbsoluteRowForPath(TreePath path) {
        int row = 0;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration enum = node.preorderEnumeration();
        DefaultMutableTreeNode finalNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        node = (DefaultMutableTreeNode) enum.nextElement();
        for( ; enum.hasMoreElements() && node != finalNode; row++) {
            node = (DefaultMutableTreeNode) enum.nextElement();
        }
        if (node != finalNode) return -1;
        return row;
    }

    /**
     * Get the path to the node located on an absolute row.
     * @param row the row number
     * @return the path or null when no node is located on this row
     */
    private TreePath getPathForAbsoluteRow(int row) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration enum = node.preorderEnumeration();
        node = (DefaultMutableTreeNode) enum.nextElement();
        int r = 0;
        for( ; enum.hasMoreElements() && r < row; r++) {
            node = (DefaultMutableTreeNode) enum.nextElement();
        }
        if (r == row) return new TreePath(node.getPath());
        else return null;
    }

    /**
     * Should be called when the tree has changed and a selection path has to be set
     * @param path the new selection path
     */
    private void commandsChanged(TreePath path) {
        tree.requestFocus();
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
        tree.repaint();
        updateButtons();
        editor.setValue( getPropertyValue() );
    }

    //-------------------------------------------
    private void editCommand(){
        //D.deb("editCommand()"); // NOI18N
        /*
        int index=list.getSelectedIndex();
        if( index<0 ){
          return ;
    }
        */
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) return;
        Object uo = node.getUserObject();
        if (!(uo instanceof UserCommand)) return;
        UserCommand uc = (UserCommand) uo;//commands.elementAt(index);
        if (uc == null) return;
        if (uc.getExec() == null) {
            String label = uc.getLabel();
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine (g("MSG_CommandFolderName"), g("MSG_CommandFolderName")); // NOI18N
            nd.setInputText(label);
            if (NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) {
                uc.setLabel(nd.getInputText());
            }
        } else {
            EditUserCommand ec = new EditUserCommand(new Frame(), uc);
            ec.setLocationRelativeTo(this/*list*/);
            ec.show();
            if( ec.wasCancelled() == false ) {
                //listModel.setElementAt(uc.toString(), index);
                node.setUserObject(uc);
            }
        }
        tree.requestFocus();
        updateButtons();

        editor.setValue( getPropertyValue() );
    }


    //-------------------------------------------
    private void addCommand(){
        //int index=list.getSelectedIndex();
        //if( index<0 ){
        //  index = listModel.getSize() - 1;
        //}
        TreePath path = tree.getSelectionPath();
        if (path == null) path = new TreePath(((DefaultMutableTreeNode) treeModel.getRoot()).getLastLeaf().getPath());
        UserCommand lastUC = null;
        int distance = 0;
        //if (path != null) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object item;
        while(true) {
            item = node.getUserObject();
            if (item instanceof UserCommand) break;
            distance++;
            node = node.getPreviousNode();
            if (node == null) {
                E.err("No correct node found for addition.");
                return;
            }
        }
        lastUC = (UserCommand) item;
        //}
        //if (lastUC == null) {
        //  lastUC = (UserCommand) commands.get(commands.size() - 1);
        //}
        int[] order = lastUC.getOrder();
        order[order.length - 1] += distance + 1;
        UserCommand uc=new UserCommand();
        uc.setOrder(order);
        EditUserCommand ec=new EditUserCommand(new Frame(),uc);
        ec.setLocationRelativeTo(this);
        ec.show();
        if( ec.wasCancelled()==false ){
            UserCommand.shiftCommands(commands, order, 1);
            int index = commands.indexOf(lastUC);
            if (index < 0) index = commands.size();
            else index += distance;
            commands.insertElementAt(uc, index+1);
            //listModel.insertElementAt(uc.toString(), index+1);
            if (((DefaultMutableTreeNode) path.getLastPathComponent()).getAllowsChildren()) {
                node = (DefaultMutableTreeNode) path.getLastPathComponent();
            } else {
                int pathLen = path.getPathCount();
                if (pathLen > 1) {
                    node = (DefaultMutableTreeNode) path.getPathComponent(pathLen - 2);
                } else {
                    node = (DefaultMutableTreeNode) treeModel.getRoot();
                }
            }
            //node.insert(new DefaultMutableTreeNode(uc), node.getIndex((TreeNode) path.getLastPathComponent()));
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(uc);
            newNode.setAllowsChildren(false);
            treeModel.insertNodeInto(newNode, node,
                                     node.getIndex((TreeNode) path.getLastPathComponent()) + 1);
            commandsChanged(new TreePath(newNode.getPath()));
        }
    }

    //-------------------------------------------
    private void addFolderCommand() {
        TreePath path = tree.getSelectionPath();
        if (path == null) path = new TreePath(((DefaultMutableTreeNode) treeModel.getRoot()).getLastLeaf().getPath());
        UserCommand lastUC = null;
        int distance = 0;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object item;
        while(true) {
            item = node.getUserObject();
            if (item instanceof UserCommand) break;
            distance++;
            node = node.getPreviousNode();
            if (node == null) {
                E.err("No correct node found for addition.");
                return;
            }
        }
        lastUC = (UserCommand) item;
        int[] order = lastUC.getOrder();
        order[order.length - 1] += distance + 1;
        int[] newOrder = new int[order.length + 1];
        for(int i = 0; i < order.length; i++) newOrder[i] = order[i];
        newOrder[order.length] = -1;
        UserCommand uc=new UserCommand();
        uc.setExec(null);
        uc.setOrder(newOrder);
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine (g("MSG_CommandFolderName"), g("MSG_CommandFolderName")); // NOI18N
        if(NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) {
            String name = nd.getInputText ();
            uc.setLabel(name);
            uc.setName(UserCommand.getUniqueName(name, commands));
            UserCommand.shiftCommands(commands, order, 1);
            int index = commands.indexOf(lastUC);
            if (index < 0) index = commands.size();
            else index += distance;
            commands.insertElementAt(uc, index+1);
            if (((DefaultMutableTreeNode) path.getLastPathComponent()).getAllowsChildren()) {
                node = (DefaultMutableTreeNode) path.getLastPathComponent();
            } else {
                int pathLen = path.getPathCount();
                if (pathLen > 1) {
                    node = (DefaultMutableTreeNode) path.getPathComponent(pathLen - 2);
                } else {
                    node = (DefaultMutableTreeNode) treeModel.getRoot();
                }
            }
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(uc);
            newNode.setAllowsChildren(true);
            treeModel.insertNodeInto(newNode, node,
                                     node.getIndex((TreeNode) path.getLastPathComponent()) + 1);
            commandsChanged(new TreePath(newNode.getPath()));
        }
    }

    //-------------------------------------------
    private void addSeparatorCommand(){
        /*
        int index=list.getSelectedIndex();
        if( index<0 ){
          index = listModel.getSize() - 1;
    }
        */
        TreePath path = tree.getSelectionPath();
        if (path == null) path = new TreePath(((DefaultMutableTreeNode) treeModel.getRoot()).getLastLeaf().getPath());
        UserCommand lastUC = null;
        int distance = 0;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object item;
        while(true) {
            item = node.getUserObject();
            if (item instanceof UserCommand) break;
            distance++;
            node = node.getPreviousNode();
            if (node == null) {
                E.err("No correct node found for addition.");
                return;
            }
        }
        lastUC = (UserCommand) item;
        int[] order = lastUC.getOrder();
        order[order.length - 1] += distance + 1;
        UserCommand.shiftCommands(commands, order, 1);
        int index = commands.indexOf(lastUC);
        if (index < 0) index = commands.size();
        commands.insertElementAt(null, index+1);
        if (((DefaultMutableTreeNode) path.getLastPathComponent()).getAllowsChildren()) {
            node = (DefaultMutableTreeNode) path.getLastPathComponent();
        } else {
            int pathLen = path.getPathCount();
            if (pathLen > 1) {
                node = (DefaultMutableTreeNode) path.getPathComponent(pathLen - 2);
            } else {
                node = (DefaultMutableTreeNode) treeModel.getRoot();
            }
        }
        //node.insert(new DefaultMutableTreeNode(g("CTL_COMMAND_SEPARATOR")), node.getIndex((TreeNode) path.getLastPathComponent()));
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(g("CTL_COMMAND_SEPARATOR"));
        newNode.setAllowsChildren(false);
        treeModel.insertNodeInto(newNode, node,
                                 node.getIndex((TreeNode) path.getLastPathComponent()) + 1);
        commandsChanged(new TreePath(newNode.getPath()));
    }

    //-------------------------------------------
    private void removeCommand(){
        TreePath path = tree.getSelectionPath();
        D.deb("Remove: path = "+path);
        if (path == null) path = new TreePath(((DefaultMutableTreeNode) treeModel.getRoot()).getLastLeaf().getPath());
        int index = getAbsoluteRowForPath(path);
        D.deb("remove: index = "+index);
        UserCommand lastUC = null;
        int distance = 0;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object item;
        while(true) {
            item = node.getUserObject();
            if (item instanceof UserCommand) break;
            distance++;
            node = node.getPreviousNode();
            if (node == null) {
                E.err("No correct node found for addition.");
                return;
            }
        }
        lastUC = (UserCommand) item;
        int[] order = lastUC.getOrder();
        order[order.length - 1] += distance + 1;
        D.deb("Found lastUC = "+lastUC+", distance = "+distance);
        commands.removeElementAt(index);
        UserCommand.shiftCommands(commands, order, -1);
        node = (DefaultMutableTreeNode) node.getParent();
        /*
        int pathLen = path.getPathCount();
        if (pathLen > 1) {
          node = (DefaultMutableTreeNode) path.getPathComponent(pathLen - 2);
    } else {
          node = (DefaultMutableTreeNode) tree.getModel().getRoot();
    }
        */
        int childIndex = node.getIndex((TreeNode) path.getLastPathComponent());
        D.deb("childIndex to remove = "+childIndex);
        //node.remove(childIndex);
        treeModel.removeNodeFromParent((MutableTreeNode) path.getLastPathComponent());
        tree.repaint();
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    /**
     * Move the selected command upward.
     */
    private void moveUpCommand() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        int index = getAbsoluteRowForPath(path);
        if (index == 0) return;
        //UserCommand.shiftCommands(commands, index, 1);
        //swapCommands(index - 1, index);
        index = moveCommands(index, index - 1);
        D.deb("moveUpCommand(): new index = "+index);
        path = getPathForAbsoluteRow(index);
        commandsChanged(path);
    }

    /**
     * Move the selected command downward.
     */
    private void moveDownCommand() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        int index = getAbsoluteRowForPath(path);
        if (index >= commands.size() - 1) return;
        //UserCommand.shiftCommands(commands, index, 1);
        //swapCommands(index, index + 1);
        index = moveCommands(index, index + 1);
        D.deb("moveDownCommand(): new index = "+index);
        path = getPathForAbsoluteRow(index);
        commandsChanged(path);
    }

    /**
     * Swap two commands in the same folder.
     * @param index1 the index of the first command
     * @param index2 the index of the second command
     * @param node1 the node of the first command
     * @param node2 the node of the second command
     * @param parent the common parent
     * @param uc1 the first command
     * @param uc2 the second command
     */
    private void swapCommands(int index1, int index2, DefaultMutableTreeNode node1, DefaultMutableTreeNode node2,
                              DefaultMutableTreeNode parent, UserCommand uc1, UserCommand uc2) {
        swapCommands(node1, node2, parent);
        commands.setElementAt(uc1, index2);
        commands.setElementAt(uc2, index1);
    }

    /**
     * Swap two commands in the same folder. Only swap in the tree is made.
     * @param node1 the node of the first command
     * @param node2 the node of the second command
     * @param parent the common parent
     */
    private void swapCommands(DefaultMutableTreeNode node1, DefaultMutableTreeNode node2,
                              DefaultMutableTreeNode parent) {
        int ni1 = treeModel.getIndexOfChild(parent, node1);
        int ni2 = treeModel.getIndexOfChild(parent, node2);
        treeModel.removeNodeFromParent(node1);
        treeModel.removeNodeFromParent(node2);
        if (ni1 < ni2) {
            treeModel.insertNodeInto(node2, parent, ni1);
            treeModel.insertNodeInto(node1, parent, ni2);
        } else {
            treeModel.insertNodeInto(node1, parent, ni2);
            treeModel.insertNodeInto(node2, parent, ni1);
        }
    }

    /**
     * Ensures that the separator is not the last one in the folder (except the root folder).
     * @param node the separator node
     * @return true when some changes in the tree were done, false otherwise
     */
    private boolean ensureSepIsNotLast(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode sibling = node.getNextSibling();
        if (sibling == null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (parent == null) return false; // It doesn't have parent, can't do anything
            DefaultMutableTreeNode grandparent = (DefaultMutableTreeNode) parent.getParent();
            if (grandparent == null) return false; // It doesn't have grandparent, can't do anything
            treeModel.removeNodeFromParent(node);
            treeModel.insertNodeInto(node, grandparent, grandparent.getIndex(parent) + 1);
            ensureSepIsNotLast(node); // Check it again
            D.deb("ensureSepIsNotLast() returns TRUE");
            return true;
        }
        return false;
    }

    /**
     * Move two commands in the vectors of commands.
     * @param index1 the index of the first command
     * @param index2 the index of the second command
     * @return the new index of the first command
     */
    private int moveCommands(int index1, int index2) {
        boolean down = index1 < index2;
        boolean up = index1 > index2;
        boolean returnSecond = true; // will return index2
        D.deb("moveCommands("+index1+", "+index2+")  down = "+down+", up = "+up);
        UserCommand uc1 = (UserCommand) commands.get(index1);
        UserCommand uc2 = (UserCommand) commands.get(index2);
        D.deb("uc1 = "+uc1+", uc2 = "+uc2);
        TreePath path1 = getPathForAbsoluteRow(index1);
        TreePath path2 = getPathForAbsoluteRow(index2);
        DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path1.getLastPathComponent();
        DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path2.getLastPathComponent();
        DefaultMutableTreeNode parent1 = (DefaultMutableTreeNode) node1.getParent();
        DefaultMutableTreeNode parent2 = (DefaultMutableTreeNode) node2.getParent();
        //if (parent1 != parent2) return; // Only commands with the same parent will be swapped
        if (uc1 == null && uc2 == null) return index2;
        if (uc1 == null) { // moving a separator
            if (parent1 == parent2) { // We're in the same parent, just swap the commands
                int[] order = uc2.getOrder();
                if (uc2.getExec() == null) { // The second is a folder
                    if (order.length < 2) { // It is the root, leave without change
                        return index1;
                    }
                    int[] parentOrder = new int[order.length - 1];
                    for(int i = 0; i < parentOrder.length; i++) {
                        parentOrder[i] = order[i];
                    }
                    order[order.length - 2] += index1 - index2; // DOWN: index1 < index2, UP: index1 > index2
                    UserCommand.shiftCommands(commands, parentOrder, index1 - index2);
                    order[order.length - 1]++;
                    UserCommand.shiftCommands(commands, order, 1);
                    order[order.length - 1]--;
                    commands.setElementAt(uc1, index2);
                    commands.setElementAt(uc2, index1);
                    treeModel.removeNodeFromParent(node1);
                    treeModel.insertNodeInto(node1, node2, 0);
                } else {
                    order[order.length - 1] += index1 - index2;
                    swapCommands(index1, index2, node1, node2, parent1, uc1, uc2);
                }
                uc2.setOrder(order);
                boolean moved = ensureSepIsNotLast(node1);
                D.deb("ensureSepIsNotLast() returned "+moved);
                if (moved) {
                    int[] parentOrder = new int[order.length - 1];
                    for(int i = 0; i < parentOrder.length; i++) {
                        parentOrder[i] = order[i];
                    }
                    parentOrder[parentOrder.length - 1]++;
                    UserCommand.shiftCommands(commands, parentOrder, 1);
                }
                return index2;
            } else { // We have different parents, only UP is possible for the Separator
                int[] order = uc2.getOrder();
                if (parent2.isNodeChild(parent1)) { // The Separator is the parent of uc2
                    if (order.length < 2) { // It is the root, leave without change
                        return index1;
                    }
                    //order[order.length - 2]++;
                    //uc2.setOrder(order);
                    int[] parentOrder = new int[order.length - 1];
                    for(int i = 0; i < parentOrder.length; i++) {
                        parentOrder[i] = order[i];
                    }
                    //parentOrder[parentOrder.length - 1]++;
                    UserCommand.shiftCommands(commands, parentOrder, 1);
                    order[order.length - 1]++;
                    UserCommand.shiftCommands(commands, order, -1);
                } else { //The Separator is going before uc2
                    order[order.length - 1]++;
                    uc2.setOrder(order);
                    int[] parentOrder = new int[order.length - 1];
                    for(int i = 0; i < parentOrder.length; i++) {
                        parentOrder[i] = order[i];
                    }
                    parentOrder[parentOrder.length - 1]++;
                    UserCommand.shiftCommands(commands, parentOrder, -1);
                }
                treeModel.removeNodeFromParent(node1); // I remove the separator
                treeModel.insertNodeInto(node1, parent2, parent2.getIndex(node2)); // and insert it before node2
                commands.setElementAt(uc1, index2);
                commands.setElementAt(uc2, index1);
            }
        } else if (parent1 == parent2) { // We're in the same parent, just swap the commands
            int[] order = uc1.getOrder();
            if (uc2 != null && uc2.getExec() == null) { // The second is a folder
                int[] order2 = uc2.getOrder();
                if (order2.length < 2) { // It is the root, leave without change
                    return index1;
                }
                if (up) { // There is an empty folder upward, just go inside
                    if (uc1.getExec() == null) { // it is a folder
                        UserCommand.addSuborder(commands, order, order2[order2.length - 1] + 1);
                        int[] newOrder = new int[order2.length - 1];
                        for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                        newOrder[newOrder.length - 1]++;
                        UserCommand.shiftCommands(commands, newOrder, -1); // move uc2 and rest up
                        newOrder = new int[order2.length + 1];
                        for(int i = 0; i < order.length; i++) newOrder[i] = order[i];
                        newOrder[order.length - 1] = order2[order2.length - 1] + 1;
                        newOrder[order.length] = order[order.length - 1];
                        UserCommand.shiftCommands(commands, newOrder, -1, newOrder.length - 3, 0); // reduce the index in uc1 folder
                    } else {
                        UserCommand.shiftCommands(commands, order, -1);
                        int[] newOrder = new int[order.length + 1];
                        for(int i = 0; i < order.length; i++) newOrder[i] = order[i];
                        newOrder[order.length - 1]--;
                        newOrder[order.length] = order2[order2.length - 1] + 1;
                        uc1.setOrder(newOrder);
                        returnSecond = false;
                    }
                } else { // moving DOWN
                    if (uc1.getExec() == null) { // it is a folder
                        order2[order2.length - 1]++;
                        UserCommand.shiftCommands(commands, order2, +1); // move the content of uc2 down to make space for uc1
                        order2[order2.length - 1]--;
                        UserCommand.addSuborder(commands, order, order2[order2.length - 1] + 1);
                        int[] newOrder = new int[order2.length - 1];
                        for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                        newOrder[newOrder.length - 1]++;
                        UserCommand.shiftCommands(commands, newOrder, -1); // move uc2 and rest up
                    } else {
                        order[order.length - 1]++;
                        UserCommand.shiftCommands(commands, order, -1); // move uc2 and rest up
                        order[order.length - 1]--;
                        order2[order2.length - 2]--;
                        order2[order2.length - 1]++;
                        UserCommand.shiftCommands(commands, order2, +1); // move the content of uc2 down to make space for uc1
                        uc1.setOrder(order2);
                    }
                    if (uc1.getExec() == null) { // I moved the folder, I have to move the commands in it as well
                        int index3 = UserCommand.getLastCommandIndex(commands, uc1.getOrder(), index1);
                        if (index3 >= 0) {
                            index3++;
                            if (up) {
                                commands.remove(index2);
                                commands.insertElementAt(uc2, index3);
                            } else {
                                UserCommand uc3 = (UserCommand) commands.get(index3);
                                commands.remove(index3);
                                commands.insertElementAt(uc3, index1);
                            }
                        }
                    } else {
                        commands.setElementAt(uc1, index2);
                        commands.setElementAt(uc2, index1);
                    }
                }
                treeModel.removeNodeFromParent(node1); // I remove the command/folder uc1
                treeModel.insertNodeInto(node1, node2, 0); // and insert it to node2 (uc2)
            } else { // uc2 is a separator or an ordinary command
                if (uc1.getExec() != null) { // if the moving uc1 is not a folder
                    order[order.length - 1] -= index1 - index2;
                    uc1.setOrder(order);
                } else { // the uc1 is a folder
                    UserCommand.shiftCommands(commands, order, index2 - index1, order.length - 2, 0);
                }
                if (uc2 != null) {
                    int[] order2 = uc2.getOrder();
                    order2[order2.length - 1] += index1 - index2;
                    uc2.setOrder(order2);
                }
                if (uc1.getExec() == null) { // I moved the folder, I have to move the commands in it as well
                    int index3 = UserCommand.getLastCommandIndex(commands, uc1.getOrder(), index1);
                    if (index3 >= 0) {
                        index3++;
                        if (up) {
                            commands.remove(index2);
                            commands.insertElementAt(uc2, index3 - 1);
                        } else {
                            UserCommand uc3 = (UserCommand) commands.get(index3);
                            commands.remove(index3);
                            commands.insertElementAt(uc3, index1);
                        }
                    }
                    swapCommands(node1, node2, parent1);
                } else {
                    swapCommands(index1, index2, node1, node2, parent1, uc1, uc2);
                }
            }
        } else { // We're in different parents
            int[] order = uc1.getOrder();
            if (uc2 != null && uc2.getExec() == null && up && node2.isNodeChild(node1)) { // The second is a folder
                int[] order2 = uc2.getOrder();
                if (order2.length < 2) { // It is the root, leave without change
                    return index1;
                }
                // We are going outside a folder we are in (uc1 is on the top inside uc2)
                //if (up) {
                //if (node2.isNodeChild(node1)) { // We are going outside a folder we are in (uc1 is on the top inside uc2)
                /*
                if (!parent2.isNodeChild(parent1)) { // There is an empty folder upward, just go inside
                  if (uc1.getExec() == null) { // it is a folder
                    order2[order2.length - 1]++;
                    int[] suborder = order;
                    for(int i = order.length - 1; i < order2.length; i++) {
                      UserCommand.addSuborder(commands, order, order2[i]);
                      if (i < order2.length - 1) {
                        int[] subsuborder = new int[suborder.length + 1];
                        for(int j = 0; j < i; j++) subsuborder[j] = suborder[j];
                        subsuborder[i] = order2[i];
                        for(int j = i + 1; j < subsuborder.length; j++) subsuborder[j] = suborder[j - 1];
                        suborder = subsuborder;
                      }
                    }
                    int[] newOrder = new int[order2.length - 1];
                    for(int i =0; i < newOrder.length; i++) newOrder[i] = order[i];
                    newOrder[newOrder.length - 1]++;
                    UserCommand.shiftCommands(commands, newOrder, -1); // move uc2 and rest up
                    newOrder = new int[order2.length + 1];
                    for(int i =0; i < order.length; i++) newOrder[i] = order[i];
                    newOrder[order.length - 1] = order2[order2.length - 1] + 1;
                    newOrder[order.length] = order[order.length - 1];
                    UserCommand.shiftCommands(commands, newOrder, -1, newOrder.length - 3, 0); // reduce the index in uc1 folder
                  } else {
                    UserCommand.shiftCommands(commands, order, -1);
                    int[] newOrder = new int[order2.length];
                    for(int i = 0; i < order.length; i++) newOrder[i] = order[i];
                    newOrder[order.length - 1]--;
                    for(int i = order.length; i < newOrder.length; i++) newOrder[i] = order2[i];
                    newOrder[newOrder.length - 1]++;
                    uc1.setOrder(newOrder);
                    returnSecond = false;
                  }
            } else {
                  */
                if (uc1.getExec() == null) { // it is a folder
                    int[] newOrder = new int[order2.length - 1];
                    for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                    int index3 = UserCommand.getLastCommandIndex(commands, order, index1);
                    UserCommand.shiftCommands(commands, newOrder, 1, newOrder.length - 1, index3 + 1); // Shift the content of uc2 without uc1 DOWN
                    UserCommand.removeSuborder(commands, order);
                    order2[order2.length - 2]++;
                    uc2.setOrder(order2);
                    order2[order2.length - 1]++;
                    UserCommand.shiftCommands(commands, order2, -1); // shift the content of uc2 upward
                    commands.remove(index2);
                    commands.insertElementAt(uc2, index3);
                    treeModel.removeNodeFromParent(node1);
                    treeModel.insertNodeInto(node1, parent2, parent2.getIndex(node2));
                } else { // it is a command
                    int[] newOrder = new int[order.length - 1];
                    for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                    commands.setElementAt(uc1, index2);
                    commands.setElementAt(uc2, index1);
                    D.deb("I had uc1 = "+uc1);
                    uc1.setOrder(newOrder);
                    D.deb("Setting new order uc1 = "+uc1);
                    newOrder = new int[order2.length - 1];
                    for(int i = 0; i < newOrder.length; i++) newOrder[i] = order2[i];
                    UserCommand.shiftCommands(commands, newOrder, 1, newOrder.length - 1, index1); // Shift uc2 and all next down
                    order2[order2.length - 2]++;
                    order2[order2.length - 1]++;
                    UserCommand.shiftCommands(commands, order2, -1);
                    treeModel.removeNodeFromParent(node1);
                    treeModel.insertNodeInto(node1, parent2, parent2.getIndex(node2));
                    D.deb("At the end I have uc1 = "+uc1);
                }
                //}
                //}/* else { // moving DOWN
                /*
                if (uc1.getExec() == null) { // it is a folder
                  order2[order2.length - 1]++;
                  UserCommand.shiftCommands(commands, order2, +1); // move the content of uc2 down to make space for uc1
                  order2[order2.length - 1]--;
                  UserCommand.addSuborder(commands, order, order2[order2.length - 1] + 1);
                  int[] newOrder = new int[order2.length - 1];
                  for(int i =0; i < newOrder.length; i++) newOrder[i] = order[i];
                  newOrder[newOrder.length - 1]++;
                  UserCommand.shiftCommands(commands, newOrder, -1); // move uc2 and rest up
            } else {
                  order[order.length - 1]++;
                  UserCommand.shiftCommands(commands, order, -1); // move uc2 and rest up
                  order[order.length - 1]--;
                  order2[order2.length - 2]--;
                  order2[order2.length - 1]++;
                  UserCommand.shiftCommands(commands, order2, +1); // move the content of uc2 down to make space for uc1
                  uc1.setOrder(order2);
            }
                if (uc1.getExec() == null) { // I moved the folder, I have to move the commands in it as well
                  int index3 = UserCommand.getLastCommandIndex(commands, uc1.getOrder(), index1);
                  if (index3 >= 0) {
                    index3++;
                    if (up) {
                      commands.remove(index2);
                      commands.insertElementAt(uc2, index3);
                    } else {
                      UserCommand uc3 = (UserCommand) commands.get(index3);
                      commands.remove(index3);
                      commands.insertElementAt(uc3, index1);
                    }
                  }
            } else {
                  commands.setElementAt(uc1, index2);
                  commands.setElementAt(uc2, index1);
            }
                treeModel.removeNodeFromParent(node1); // I remove the command/folder uc1
                treeModel.insertNodeInto(node1, node2, 0); // and insert it to node2 (uc2)
            }*/
                //} else { // uc2 is a separator or an ordinary command
                //  if (up) { // I'm going inside a folder above me.
                /*
                if (uc1.getExec() == null) { // I'm a folder
                  UserCommand.addSuborder(commands, order, order2[order2.length - 1] + 1);
                  int[] newOrder = new int[order.length - 1];
                  for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                  UserCommand.shiftCommands(commands, newOrder, -1); // Shift me and all after UP
            } else {
                  UserCommand.shiftCommands(commands, order, -1); // Shift me and all after UP
            }
                treeModel.removeNodeFromParent(node1);
                treeModel.insertNodeInto(node1, parent2
                */
                // handeled below
                //  } else { // I'm a command going DOWN outside the folder
                // handeled below
                //  }
            } else {
                if (up && !node2.isNodeChild(node1)) { // I'm going inside a folder above me.
                    D.deb("Going inside the folder above me");
                    DefaultMutableTreeNode node3p = (DefaultMutableTreeNode) node1.getPreviousSibling(); // the sibling is a folder
                    if (node3p == null) { // I have no previous sibling => nowhere to go
                        return index1;
                    }
                    DefaultMutableTreeNode node3 = (DefaultMutableTreeNode) node3p.getLastChild();
                    if (node3 == null) { // I have no child (that should be an error)
                        return index1;
                    }
                    UserCommand uc3 = (UserCommand) node3.getUserObject();
                    int[] order3 = uc3.getOrder();
                    int suborder = (uc3.getExec() == null) ? order3[order3.length - 2] : order3[order3.length - 1];
                    suborder++;
                    if (uc1.getExec() == null) { // I'm a folder
                        UserCommand.addSuborder(commands, order, suborder);
                        int[] newOrder = new int[order.length - 1];
                        for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                        UserCommand.shiftCommands(commands, newOrder, -1); // Shift me and all after UP
                    } else {
                        UserCommand.shiftCommands(commands, order, -1); // Shift me and all after UP
                        int[] newOrder = new int[order.length + 1];
                        for(int i = 0; i < order.length; i++) newOrder[i] = order[i];
                        newOrder[newOrder.length - 1] = suborder;
                        newOrder[newOrder.length - 2]--;
                        uc1.setOrder(newOrder);
                    }
                    treeModel.removeNodeFromParent(node1);
                    treeModel.insertNodeInto(node1, node3p, node3p.getIndex(node3) + 1);
                    returnSecond = false;
                }
                if (down) { // We're in different folders and going DOWN =>
                    DefaultMutableTreeNode nextSibling = (DefaultMutableTreeNode) node1.getNextSibling();
                    if (nextSibling == null) { // definitely going outside the current folder, don't care about uc2
                        if (uc1.getExec() == null) { // I'm a folder
                            int[] newOrder = new int[order.length - 2];
                            for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                            //newOrder[newOrder.length - 1]++;
                            UserCommand.shiftCommands(commands, newOrder, +1, newOrder.length - 1, index1); // Shift everything under me down
                            order[order.length - 3]++;
                            UserCommand.removeSuborder(commands, order);
                            //UserCommand.shiftCommands(commands, order, +1
                        } else { // I'm a command
                            int[] newOrder = new int[order.length - 1];
                            for(int i = 0; i < newOrder.length; i++) newOrder[i] = order[i];
                            //newOrder[newOrder.length - 1]++;
                            UserCommand.shiftCommands(commands, newOrder, +1, newOrder.length - 1, index1); // Shift everything under me down
                            newOrder[newOrder.length - 1]++;
                            uc1.setOrder(newOrder);
                        }
                        treeModel.removeNodeFromParent(node1);
                        DefaultMutableTreeNode grandParent = (DefaultMutableTreeNode) parent1.getParent();
                        if (grandParent != null) {
                            treeModel.insertNodeInto(node1, grandParent, grandParent.getIndex(parent1) + 1);
                        } else {
                            E.err("Unexpected node configuration.");
                        }
                        returnSecond = false;
                    } else { // swapping me (folder) with my next sibling
                        Object sibling = nextSibling.getUserObject();
                        UserCommand uc3 = null;
                        if (sibling instanceof UserCommand) uc3 = (UserCommand) sibling;
                        int index3 = UserCommand.getLastCommandIndex(commands, order, index1) + 1;
                        UserCommand.shiftCommands(commands, order, 1, order.length - 2, index1); // shift only uc1 DOWN
                        if (uc3 == null || uc3.getExec() != null) { // the second is a command or a separator
                            if (uc3 != null) {
                                int[] order3 = uc3.getOrder();
                                order3[order3.length - 1]--;
                                uc3.setOrder(order3);
                            }
                            commands.remove(index3);
                            commands.insertElementAt(uc3, index1);
                        } else { // the second is a folder too
                            int[] order3 = uc3.getOrder();
                            UserCommand.shiftCommands(commands, order3, -1, order3.length - 2, index3); // Shift only uc3 UP
                            order3[order3.length - 2]--;
                            UserCommand.moveCommands(commands, order3, index1);
                        }
                        treeModel.removeNodeFromParent(node1);
                        treeModel.insertNodeInto(node1, parent1, parent1.getIndex(nextSibling) + 1);
                    }
                }
            }
        }
        /*
        if (uc2 == null) {
          int[] order = uc1.getOrder();
          order[order.length - 1]++;
          uc1.setOrder(order);
          node1.setUserObject(g("CTL_COMMAND_SEPARATOR"));
          node2.setUserObject(uc1);
          //listModel.setElementAt(uc1.toString(), index2);
          //listModel.setElementAt(g("CTL_COMMAND_SEPARATOR"), index1); // NOI18N
    } else {
          int[] order1 = uc1.getOrder();
          int[] order2 = uc2.getOrder();
          uc1.setOrder(order2);
          uc2.setOrder(order1);
          node1.setUserObject(uc2);
          node2.setUserObject(uc1);
          //listModel.setElementAt(uc1.toString(), index2);
          //listModel.setElementAt(uc2.toString(), index1);
    }
        //commands.setElementAt(uc1, index2);
        //commands.setElementAt(uc2, index1);
        */
        D.deb("At the final end of moveCommands I have:");
        D.deb("uc1 = "+uc1);
        D.deb("uc2 = "+uc1);
        treeModel.reload();
        return (returnSecond) ? index2 : index1;
    }

    /**
     * Swap two commands in the vectors of commands. index1 has to be smaller than index2.
     * @param index1 the index of the first command
     * @param index2 the index of the second command
     */
    private void swapCommands(int index1, int index2) {
        UserCommand uc1 = (UserCommand) commands.get(index1);
        UserCommand uc2 = (UserCommand) commands.get(index2);
        TreePath path1 = getPathForAbsoluteRow(index1);
        TreePath path2 = getPathForAbsoluteRow(index2);
        DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) path1.getLastPathComponent();
        DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) path2.getLastPathComponent();
        DefaultMutableTreeNode parent1 = (DefaultMutableTreeNode) node1.getParent();
        DefaultMutableTreeNode parent2 = (DefaultMutableTreeNode) node2.getParent();
        if (parent1 != parent2) return; // Only commands with the same parent will be swapped
        if (uc1 == null && uc2 == null) return;
        if (uc1 == null) {
            int[] order = uc2.getOrder();
            order[order.length - 1]--;
            uc2.setOrder(order);
            node1.setUserObject(uc2);
            node2.setUserObject(g("CTL_COMMAND_SEPARATOR"));
            //listModel.setElementAt(g("CTL_COMMAND_SEPARATOR"), index2); // NOI18N
            //listModel.setElementAt(uc2.toString(), index1);
        } else if (uc2 == null) {
            int[] order = uc1.getOrder();
            order[order.length - 1]++;
            uc1.setOrder(order);
            node1.setUserObject(g("CTL_COMMAND_SEPARATOR"));
            node2.setUserObject(uc1);
            //listModel.setElementAt(uc1.toString(), index2);
            //listModel.setElementAt(g("CTL_COMMAND_SEPARATOR"), index1); // NOI18N
        } else {
            int[] order1 = uc1.getOrder();
            int[] order2 = uc2.getOrder();
            uc1.setOrder(order2);
            uc2.setOrder(order1);
            node1.setUserObject(uc2);
            node2.setUserObject(uc1);
            //listModel.setElementAt(uc1.toString(), index2);
            //listModel.setElementAt(uc2.toString(), index1);
        }
        commands.setElementAt(uc1, index2);
        commands.setElementAt(uc2, index1);
        treeModel.reload();
    }

    /**
     * Move the content of a folder of commands.
     * @param order the order base of commands to move
     * @param index the index in the Vector of commands where start moving
     * @param direction the direction of move
     *
    void moveFolderContent(int[] order, int index, int direction) {
      for(int i = index; i < commands.size(); i++) {
        UserCommand uc = (UserCommand) commands.get(i);
        if (uc != null) {
          int[] order = uc.getOrder();
          int ol = order.length;
          int bl = baseOrder.length;
          if (ol >= bl) {
            int k;
            for(k = 0; k < bl - 1; k++) {
              if (order[k] != baseOrder[k]) break;
            }
            if (k < bl - 1) continue;
            if (order[k] >= baseOrder[k]) {
      
}
    */

    //-------------------------------------------
    public Object getPropertyValue() {
        //D.deb("getPropertyValue() -->"+commands);
        Vector cmds = new Vector();
        int len = commands.size();
        for(int i = 0; i < len; i++) {
            UserCommand uc = (UserCommand) commands.get(i);
            if (uc != null) cmds.addElement(uc);
        }
        D.deb("getPropertyValue(): cmds = "+cmds);
        return cmds;
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

/*
 * <<Log>>
 *  19   Jaga      1.16.1.1    3/15/00  Martin Entlicher setLocation changed
 *  18   Jaga      1.16.1.0    3/9/00   Martin Entlicher Fix of long panel width.
 *  17   Gandalf   1.16        1/27/00  Martin Entlicher NOI18N
 *  16   Gandalf   1.15        11/30/99 Martin Entlicher 
 *  15   Gandalf   1.14        11/27/99 Patrik Knakal   
 *  14   Gandalf   1.13        10/25/99 Pavel Buzek     copyright
 *  13   Gandalf   1.12        10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  12   Gandalf   1.11        10/7/99  Pavel Buzek     
 *  11   Gandalf   1.10        9/30/99  Pavel Buzek     
 *  10   Gandalf   1.9         9/8/99   Pavel Buzek     class model changed, 
 *       customization improved, several bugs fixed
 *  9    Gandalf   1.8         8/31/99  Pavel Buzek     
 *  8    Gandalf   1.7         6/30/99  Ian Formanek    reflected change in 
 *       enhanced property editors
 *  7    Gandalf   1.6         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  6    Gandalf   1.5         5/27/99  Michal Fadljevic 
 *  5    Gandalf   1.4         5/4/99   Michal Fadljevic 
 *  4    Gandalf   1.3         5/4/99   Michal Fadljevic 
 *  3    Gandalf   1.2         4/26/99  Michal Fadljevic 
 *  2    Gandalf   1.1         4/22/99  Michal Fadljevic 
 *  1    Gandalf   1.0         4/21/99  Michal Fadljevic 
 * $
 */
