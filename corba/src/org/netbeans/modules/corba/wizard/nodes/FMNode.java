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

package org.netbeans.modules.corba.wizard.nodes;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
/** 
 *
 * @author  root
 * @version 
 */
abstract public class FMNode extends AbstractMutableContainerNode implements Node.Cookie,
       ModuleCreator, StructCreator, ConstantCreator, ExceptionCreator, AliasCreator, UnionCreator, EnumCreator, InterfaceCreator {

  /** Creates new FMNode */
  public FMNode(NamedKey key) {
    super (key);
  }
  
  public void createModule () {
    final ModulePanel panel = new ModulePanel ();
    TopManager tm = TopManager.getDefault();
    DialogDescriptor descriptor = new DialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateModule"),true,
           DialogDescriptor.OK_CANCEL_OPTION,DialogDescriptor.OK_OPTION,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getSource()==DialogDescriptor.OK_OPTION){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.MODULE, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createConstant () {
    final ConstPanel panel = new ConstPanel ();
    TopManager tm = TopManager.getDefault();
    DialogDescriptor descriptor = new DialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateConstant"), true, 
        DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getSource () == DialogDescriptor.OK_OPTION){
              String name = panel.getName ();
              String type = panel.getType();
              String value = panel.getValue();
              ConstKey key = new ConstKey (MutableKey.CONSTANT, name, type, value);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose();
          }
        });
     this.dialog = tm.createDialog (descriptor);
     this.dialog.setVisible (true);
  }
  
  public void createStruct() {
    final ModulePanel panel = new ModulePanel ();
    TopManager tm = TopManager.getDefault();
    DialogDescriptor descriptor = new DialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateStruct"),true,
           DialogDescriptor.OK_CANCEL_OPTION,DialogDescriptor.OK_OPTION,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getSource()==DialogDescriptor.OK_OPTION){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.STRUCT, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createException () {
    final ModulePanel panel = new ModulePanel ();
    TopManager tm = TopManager.getDefault();
    DialogDescriptor descriptor = new DialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateException"),true,
           DialogDescriptor.OK_CANCEL_OPTION,DialogDescriptor.OK_OPTION,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getSource()==DialogDescriptor.OK_OPTION){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.EXCEPTION, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  public void createAlias() {
    final AliasPanel panel = new AliasPanel ();
    TopManager tm = TopManager.getDefault();
    DialogDescriptor descriptor = new DialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateAlias"),true,
           DialogDescriptor.OK_CANCEL_OPTION,DialogDescriptor.OK_OPTION,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getSource()==DialogDescriptor.OK_OPTION){
                String name = panel.getName();
                String type = panel.getType();
                String length = panel.getLength();
                AliasKey key = new AliasKey ( NamedKey.ALIAS, name, type, length);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createUnion () {
    final UnionPanel panel = new UnionPanel ();
    DialogDescriptor descriptor = new DialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateUnion"), true,
        DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getSource () == DialogDescriptor.OK_OPTION) {
              String name = panel.getName();
              String type = panel.getType();
              AliasKey key = new AliasKey (MutableKey.UNION,name,type,null);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createEnum () {
    final EnumPanel panel = new EnumPanel ();
    DialogDescriptor descriptor = new DialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateEnum"), true,
        DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getSource () == DialogDescriptor.OK_OPTION) {
              String name = panel.getName();
              String values = panel.getValues();
              EnumKey key = new EnumKey (MutableKey.ENUM,name,values);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createInterface () {
    final InterfacePanel panel = new InterfacePanel ();
    DialogDescriptor descriptor = new DialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateInterface"), true,
        DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getSource () == DialogDescriptor.OK_OPTION) {
              String name = panel.getName();
              String base = panel.getBase();
              InterfaceKey key = new InterfaceKey (MutableKey.INTERFACE,name,base);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
}