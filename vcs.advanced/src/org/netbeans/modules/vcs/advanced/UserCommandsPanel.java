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

package com.netbeans.enterprise.modules.vcs.cmdline;
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

import com.netbeans.developer.modules.vcs.util.*;
import com.netbeans.developer.modules.vcs.cmdline.*;

/** User commands panel.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserCommandsPanel extends JPanel 
  implements EnhancedCustomPropertyEditor {

  private Debug E=new Debug("UserCommandsPanel", false);
  private Debug D=E;

  private JList list=null;
  private DefaultListModel listModel=null;
  private JButton editButton=null;
  private JButton addButton=null;
  private JButton removeButton=null;

  private UserCommandsEditor editor;

  private Vector commands=null;

  //-------------------------------------------
  public UserCommandsPanel(UserCommandsEditor editor){
    this.editor = editor;
    Vector oldCommands=(Vector)editor.getValue();
    commands=deepCopy(oldCommands);
    initComponents();
    initListeners();
    deselectAll();
  }

  //-------------------------------------------
  private Vector deepCopy(Vector oldCommands){
    int len=oldCommands.size();
    Vector newCommands=new Vector(len);
    for(int i=0;i<len;i++){
      UserCommand cmd=(UserCommand)oldCommands.elementAt(i);
      newCommands.addElement( cmd.clone() );
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
      listModel.addElement(uc.toString());
    }
    JScrollPane listScrollPane = new JScrollPane(list);
    return listScrollPane;
  }

  //-------------------------------------------
  private JPanel createCommands(){
    addButton=createButton(g("CTL_Add"));
    editButton=createButton(g("CTL_Edit"));
    removeButton=createButton(g("CTL_Remove"));

    GridLayout panel2Layout=new GridLayout(5,1);
    panel2Layout.setVgap(5);

    JPanel panel2=new JPanel();
    panel2.setLayout(panel2Layout);
    panel2.setBorder(new EmptyBorder(5, 7, 5, 7));

    panel2.add(addButton);
    panel2.add(editButton);
    panel2.add(removeButton);

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
    c.weightx = 0.9;
    c.weighty = 1.0;
    JScrollPane listScrollPane=createList();
    gb.setConstraints(listScrollPane,c);
    add(listScrollPane);    

    JPanel commandPanel=createCommands();
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.1;
    c.weighty = 1.0;

    gb.setConstraints(commandPanel,c);
    add(commandPanel);
  }

  //-------------------------------------------
  private void initListeners(){

    list.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
	//D.deb("valueChanged "+e);
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
	//D.deb("keyPressed() e="+e);
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
	  //D.deb("ignored keyCode="+keyCode);
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

    removeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
	removeCommand();
      }
    });
  }


  //-------------------------------------------
  private void deselectAll(){
    list.clearSelection(); 
    removeButton.setEnabled(false);
    editButton.setEnabled(false);
  }


  //-------------------------------------------
  private void updateButtons(){
    if( list.getSelectedIndex()<0 ){
      deselectAll();
    }
    else{
      removeButton.setEnabled(true);
      editButton.setEnabled(true);
      list.requestFocus();
    }
  }


  //-------------------------------------------
  private void editCommand(){
    //D.deb("editCommand()");
    int index=list.getSelectedIndex();
    if( index<0 ){
      return ;
    }
    UserCommand uc=(UserCommand)commands.elementAt(index);
    EditUserCommand ec=new EditUserCommand(new Frame(),uc);
    ec.setLocationRelativeTo(list);
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
    UserCommand uc=new UserCommand();
    EditUserCommand ec=new EditUserCommand(new Frame(),uc);
    ec.setLocationRelativeTo(list);
    ec.show();
    if( ec.wasCancelled()==false ){
      commands.addElement(uc);
      listModel.addElement(uc.toString());
    }
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
    listModel.removeElementAt(index);
    updateButtons();

    editor.setValue( getPropertyValue() );
  }


  //-------------------------------------------
  public Object getPropertyValue() {
    //D.deb("getPropertyValue() -->"+commands);
    return commands;
  }


  //-------------------------------------------
  String g(String s) {
    return NbBundle.getBundle
      ("com.netbeans.enterprise.modules.vcs.cmdline.Bundle").getString (s);
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
