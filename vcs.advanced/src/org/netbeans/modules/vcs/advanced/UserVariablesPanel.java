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
import com.netbeans.developer.modules.vcs.*;

/** User variables panel.
 * 
 * @author Michal Fadljevic
 */
//-------------------------------------------
public class UserVariablesPanel extends JPanel implements EnhancedCustomPropertyEditor{
  private Debug E=new Debug("UserVariablesPanel", false);
  //private Debug D=E;

  private JList list=null;
  private DefaultListModel listModel=null;
  private JButton editButton=null;
  private JButton addButton=null;
  private JButton removeButton=null;

  private UserVariablesEditor editor;
  
  //-------------------------------------------
  static final long serialVersionUID =-4165869264994159492L;
  public UserVariablesPanel(UserVariablesEditor editor){
    this.editor = editor;
    initComponents();
    initListeners();
    deselectAll();
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
    Vector variables=(Vector)editor.getValue();
    int len=variables.size();
    for(int i=0;i<len;i++){
      listModel.addElement(variables.elementAt(i).toString ());
    }
    JScrollPane listScrollPane = new JScrollPane(list);
    return listScrollPane;
  }

  //-------------------------------------------
  private JPanel createCommands(){
    editButton=createButton(g("CTL_Edit"));
    editButton.setMnemonic(KeyEvent.VK_E);
    addButton=createButton(g("CTL_Add"));
    addButton.setMnemonic(KeyEvent.VK_A);    
    removeButton=createButton(g("CTL_Remove"));
    removeButton.setMnemonic(KeyEvent.VK_R);

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
    setBorder(new TitledBorder("Variables"));

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
	  editVariable();
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
	  addVariable();
	  //TODO better insertVariable(int index)
	  break;
	case KeyEvent.VK_DELETE:
	  removeVariable();
	  break;
	case KeyEvent.VK_ENTER:
	  editVariable();
	  break;
	default:
	  //D.deb("ignored keyCode="+keyCode);
	}
	updateButtons();
      }
    });

    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e){
	editVariable();
      }
    });

    addButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
	addVariable();
      }
    });

    removeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
	removeVariable();
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
  private void editVariable(){
    //D.deb("editVariable()");
    int index=list.getSelectedIndex();
    if( index<0 ){
      return ;
    }
    VcsConfigVariable var = (VcsConfigVariable) ((Vector) editor.getValue ()).get (index);

    EditUserVariable ev=new EditUserVariable(new Frame(), var);
    ev.setLocationRelativeTo(list);
    ev.show();
    if( ev.wasCancelled()==false ){
      listModel.setElementAt(var.toString (), index);
    }
    list.requestFocus();
    updateButtons();

    editor.setValue( getPropertyValue() );
  }
  

  //-------------------------------------------
  private void addVariable(){
    VcsConfigVariable var = new VcsConfigVariable ("", "", "", false, false, false);
    EditUserVariable ev=new EditUserVariable(new Frame(), var);
    ev.setLocationRelativeTo(list);
    ev.show();
    if( ev.wasCancelled()==false ){
      ((Vector) editor.getValue ()).add (var);
      listModel.addElement(var.toString ());
    }
    list.requestFocus();
    updateButtons();

    editor.setValue( getPropertyValue() );
  }


  //-------------------------------------------
  private void removeVariable(){
    int index=list.getSelectedIndex();
    if( index<0 ){
      return ;
    }
    ((Vector) editor.getValue ()).remove (index);
    listModel.removeElementAt(index);
    updateButtons();

    editor.setValue( getPropertyValue() );
  }


  //-------------------------------------------
  public Object getPropertyValue() {
    //D.deb("getPropertyValue()");
    return editor.getValue ();
  }
  

  //-------------------------------------------
  String g(String s) {
    return NbBundle.getBundle
      ("com.netbeans.developer.modules.vcs.cmdline.Bundle").getString (s);
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
 *  2    Gandalf   1.1         11/27/99 Patrik Knakal   
 *  1    Gandalf   1.0         11/24/99 Martin Entlicher 
 * $
 */
