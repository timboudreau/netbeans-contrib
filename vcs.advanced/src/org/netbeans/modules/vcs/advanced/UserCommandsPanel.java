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
import java.text.*;

import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.*;

import org.netbeans.modules.vcs.util.*;
import org.netbeans.modules.vcs.cmdline.*;

/** User commands panel.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserCommandsPanel extends JPanel
    implements EnhancedCustomPropertyEditor {

    private Debug E=new Debug("UserCommandsPanel", true); // NOI18N
    private Debug D=E;

    private JList list=null;
    private DefaultListModel listModel=null;
    private JButton editButton=null;
    private JButton addButton=null;
    private JButton addSeparatorButton=null;
    private JButton removeButton=null;
    private JButton moveUpButton=null;
    private JButton moveDownButton=null;

    private UserCommandsEditor editor;

    private Vector commands=null;
    private Vector refCommands=new Vector();

    static final long serialVersionUID =-5546375234297504708L;

    //-------------------------------------------
    public UserCommandsPanel(UserCommandsEditor editor){
        this.editor = editor;
        Vector oldCommands=(Vector)editor.getValue();
        commands=deepCopy(oldCommands, refCommands);
        D.deb("UserCommandsPanel() commands = "+commands); // NOI18N
        initComponents();
        initListeners();
        deselectAll();
    }

    //-------------------------------------------
    private Vector deepCopy(Vector oldCommands, Vector refCommands){
        int len=oldCommands.size();
        Vector newCommands=new Vector(len);
        int lastOrder = 0;
        for(int i=0; i<len; i++){
            UserCommand cmd = (UserCommand) oldCommands.elementAt(i);
            int order = cmd.getOrder();
            for(int j = lastOrder + 1; j < order; j++) {
                newCommands.addElement(null);
                refCommands.addElement(null);
            }
            lastOrder = order;
            newCommands.addElement(cmd.clone());
            refCommands.addElement(new Integer(i));
        }
        return newCommands;
    }

    //-------------------------------------------
    private JButton createButton(String name){
        JButton button = new JButton(name);
        return button;
    }

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

    //-------------------------------------------
    private JPanel createCommands(){
        addButton=createButton(g("CTL_Add")); // NOI18N
        addButton.setMnemonic(KeyEvent.VK_D);
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

        GridLayout panel2Layout=new GridLayout(6,1);
        panel2Layout.setVgap(5);

        JPanel panel2=new JPanel();
        panel2.setLayout(panel2Layout);
        panel2.setBorder(new EmptyBorder(5, 7, 5, 7));

        panel2.add(addButton);
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
        JScrollPane listScrollPane=createList();
        gb.setConstraints(listScrollPane,c);
        add(listScrollPane);

        c = new GridBagConstraints();
        JPanel commandPanel=createCommands();
        //c.fill = GridBagConstraints.BOTH;
        //c.weightx = 0.1;
        //c.weighty = 1.0;

        gb.setConstraints(commandPanel,c);
        add(commandPanel);
        Dimension preferred = listScrollPane.getPreferredSize();
        preferred.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().width*0.8), preferred.height);
        listScrollPane.setPreferredSize(preferred);
    }

    //-------------------------------------------
    private void initListeners(){

        list.addListSelectionListener(new ListSelectionListener(){
                                          public void valueChanged(ListSelectionEvent e){
                                              //D.deb("valueChanged "+e); // NOI18N
                                              updateButtons();
                                          }
                                      });

        list.addMouseListener(new MouseAdapter() {
                                  public void mouseClicked(MouseEvent e){
                                      if( e.getClickCount()==2 ){
                                          editCommand();
                                      }
                                      updateButtons();
                                  }
                              });

        list.addKeyListener(new KeyAdapter() {
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
        list.clearSelection();
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
    }


    //-------------------------------------------
    private void updateButtons(){
        int index = list.getSelectedIndex();
        if (index < 0) {
            deselectAll();
        }
        else {
            removeButton.setEnabled(true);
            editButton.setEnabled(true);
            moveUpButton.setEnabled(index > 0);
            moveDownButton.setEnabled(index < (listModel.getSize() - 1));
            list.requestFocus();
        }
    }


    //-------------------------------------------
    private void editCommand(){
        //D.deb("editCommand()"); // NOI18N
        int index=list.getSelectedIndex();
        if( index<0 ){
            return ;
        }
        UserCommand uc=(UserCommand)commands.elementAt(index);
        if (uc == null) return;
        EditUserCommand ec=new EditUserCommand(new Frame(),uc);
        ec.setLocationRelativeTo(this/*list*/);
        ec.show();
        if( ec.wasCancelled()==false ){
            listModel.setElementAt(uc.toString(),index);
        }
        list.requestFocus();
        updateButtons();

        editor.setValue( getPropertyValue() );
    }


    //-------------------------------------------
    private void addCommand(){
        int index=list.getSelectedIndex();
        if( index<0 ){
            index = listModel.getSize() - 1;
        }
        UserCommand uc=new UserCommand();
        uc.setOrder(index+1);
        EditUserCommand ec=new EditUserCommand(new Frame(),uc);
        ec.setLocationRelativeTo(list);
        ec.show();
        if( ec.wasCancelled()==false ){
            UserCommand.shiftCommands(commands, index+1, 1);
            commands.insertElementAt(uc, index+1);
            listModel.insertElementAt(uc.toString(), index+1);
        }
        list.requestFocus();
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    //-------------------------------------------
    private void addSeparatorCommand(){
        int index=list.getSelectedIndex();
        if( index<0 ){
            index = listModel.getSize() - 1;
        }
        UserCommand.shiftCommands(commands, index+1, 1);
        listModel.insertElementAt(g("CTL_COMMAND_SEPARATOR"), index+1); // NOI18N
        commands.insertElementAt(null, index+1);
        list.requestFocus();
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    //-------------------------------------------
    private void removeCommand(){
        int index=list.getSelectedIndex();
        if( index<0 ){
            return ;
        }
        commands.removeElementAt(index);
        UserCommand.shiftCommands(commands, index, -1);
        listModel.removeElementAt(index);
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    //-------------------------------------------
    private void moveUpCommand() {
        int index=list.getSelectedIndex();
        if (index <= 0) {
            return ;
        }
        //UserCommand.shiftCommands(commands, index, 1);
        swapCommands(index-1, index);
        list.requestFocus();
        list.setSelectedIndex(index-1);
        list.ensureIndexIsVisible(index-1);
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    //-------------------------------------------
    private void moveDownCommand() {
        int index=list.getSelectedIndex();
        if (index < 0 || index >= listModel.getSize()-1) {
            return ;
        }
        //UserCommand.shiftCommands(commands, index, 1);
        swapCommands(index, index+1);
        list.requestFocus();
        list.setSelectedIndex(index+1);
        list.ensureIndexIsVisible(index+1);
        updateButtons();

        editor.setValue( getPropertyValue() );
    }

    /**
     * Swap two commands in the vectors of commands. index1 has to be smaller than index2.
     * @param index1 the index of the first command
     * @param index2 the index of the second command
     */
    private void swapCommands(int index1, int index2) {
        UserCommand uc1 = (UserCommand) commands.get(index1);
        UserCommand uc2 = (UserCommand) commands.get(index2);
        if (uc1 == null && uc2 == null) return;
        if (uc1 == null) {
            uc2.setOrder(uc2.getOrder() - 1);
            listModel.setElementAt(g("CTL_COMMAND_SEPARATOR"), index2); // NOI18N
            listModel.setElementAt(uc2.toString(), index1);
        } else if (uc2 == null) {
            uc1.setOrder(uc1.getOrder() + 1);
            listModel.setElementAt(uc1.toString(), index2);
            listModel.setElementAt(g("CTL_COMMAND_SEPARATOR"), index1); // NOI18N
        } else {
            int order1 = uc1.getOrder();
            int order2 = uc2.getOrder();
            uc1.setOrder(order2);
            uc2.setOrder(order1);
            listModel.setElementAt(uc1.toString(), index2);
            listModel.setElementAt(uc2.toString(), index1);
        }
        commands.setElementAt(uc1, index2);
        commands.setElementAt(uc2, index1);
    }

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
