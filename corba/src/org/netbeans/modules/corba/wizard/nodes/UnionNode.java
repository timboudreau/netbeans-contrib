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
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;

/** 
 *
 * @author Tomas Zezula
 * @version 
 */
public class UnionNode extends AbstractMutableContainerNode implements Node.Cookie,
            UnionMemberCreator, UnionDefaultCreator {

  private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/union";

  boolean canAdd;
  
  /** Creates new UnioNode */
  public UnionNode(NamedKey key) {
    super (key);
    this.canAdd = true;
    this.getCookieSet().add(this);
    this.setName (key.getName());
    this.setIconBase (ICON_BASE);
  }
  
  public SystemAction[] createActions () {
    return new SystemAction [] {
      SystemAction.get (CreateUnionMemberAction.class),
      SystemAction.get (CreateUnionDefaultAction.class),
      null,
      SystemAction.get (DestroyAction.class),
      SystemAction.get (RenameAction.class),
    };
  }
  
  public String generateSelf (int indent) {
    String code = new String ();
    String fill = new String ();
    for ( int i=0; i< indent; i++)
      fill = fill + "    ";
    code = fill + "union "+this.getName()+" switch ("+((AliasKey)key).getType() +") {\n"; // No I18N
    Node[] nodes = this.getChildren().getNodes();
    for (int i=0; i<nodes.length; i++) {
      code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent+1);
    }
    code = code + fill + "};\n";
    return code;
  }
  
  public void createUnionMember() {
    TopManager tm = TopManager.getDefault ();
    final UnionMemberPanel panel = new UnionMemberPanel ();
    ExDialogDescriptor descriptor = new ExDialogDescriptor ( panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateMember"), true,
        new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
              String name = panel.getName ();
              String type = panel.getType ();
              String length = panel.getLength ();
              String label = panel.getLabel ();
              UnionMemberKey key = new UnionMemberKey (MutableKey.UNION_MBR, name, type, length, label);
              ((MutableChildren)getChildren ()).addKey (key);
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }

  public void createUnionDefault () {
    TopManager tm = TopManager.getDefault ();
    final AliasPanel panel = new AliasPanel ();
    ExDialogDescriptor descriptor = new ExDialogDescriptor ( panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateUnionDefault"), true,
        new ActionListener () {
          public void actionPerformed ( ActionEvent event) {
            if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
              String name = panel.getName ();
              String type = panel.getType ();
              String length = panel.getLength ();
              UnionMemberKey key = new UnionMemberKey (MutableKey.UNION_MBR, name, type, length, null);
              ((MutableChildren)getChildren()).addKey (key);
              canAdd = false;
            }
            dialog.setVisible (false);
            dialog.dispose ();
          }
        });
    descriptor.disableOk();
    this.dialog = tm.createDialog (descriptor);
    this.dialog.setVisible (true);
  }

  public boolean canAdd () {
    return this.canAdd;
  }

}