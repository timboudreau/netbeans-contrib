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
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateModule"),true,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.MODULE, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createConstant () {
    final ConstPanel panel = new ConstPanel ();
    TopManager tm = TopManager.getDefault();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateConstant"), true, 
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
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
     descriptor.disableOk();
     this.dialog = tm.createDialog (descriptor);
     this.dialog.setVisible (true);
  }
  
  public void createStruct() {
    final ModulePanel panel = new ModulePanel ();
    TopManager tm = TopManager.getDefault();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateStruct"),true,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.STRUCT, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createException () {
    final ModulePanel panel = new ModulePanel ();
    TopManager tm = TopManager.getDefault();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateException"),true,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                String name = panel.getName();
                NamedKey key = new NamedKey ( NamedKey.EXCEPTION, name);
                ((MutableChildren)getChildren()).addKey (key);
              }
              dialog.setVisible (false);
              dialog.dispose();
            }
          });
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createAlias() {
    final AliasPanel panel = new AliasPanel ();
    TopManager tm = TopManager.getDefault();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateAlias"),true,
           new ActionListener () {
            public void actionPerformed (ActionEvent event){
              if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
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
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createUnion () {
    final UnionPanel panel = new UnionPanel ();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateUnion"), true,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
              String name = panel.getName();
              String type = panel.getType();
              AliasKey key = new AliasKey (MutableKey.UNION,name,type,null);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    descriptor.disableOk();
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createEnum () {
    final EnumPanel panel = new EnumPanel ();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateEnum"), true,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
              String name = panel.getName();
              String values = panel.getValues();
              EnumKey key = new EnumKey (MutableKey.ENUM,name,values);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    descriptor.disableOk();
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
  public void createInterface () {
    final InterfacePanel panel = new InterfacePanel ();
    ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateInterface"), true,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
              String name = panel.getName();
              String base = panel.getBase();
              InterfaceKey key = new InterfaceKey (MutableKey.INTERFACE,name,base);
              ((MutableChildren)getChildren()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    descriptor.disableOk();
    this.dialog = TopManager.getDefault ().createDialog (descriptor);
    this.dialog.setVisible (true);
  }
  
}