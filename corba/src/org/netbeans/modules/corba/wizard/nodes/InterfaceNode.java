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
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;

/** 
 *
 * @author  root
 * @version 
 */
public class InterfaceNode extends FMNode implements Node.Cookie,
    OperationCreator, AttributeCreator {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/interface";
    private Dialog dialog;
  
    /** Creates new InterfaceNode */
    public InterfaceNode (NamedKey key) {
        super (key);
        this.getCookieSet().add (this);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for (int i=0; i< indent; i++)
            fill = fill + "    ";
        code = fill + "interface " +  this.getName () + " ";  // No I18N
        if (((InterfaceKey)this.key).getbaseInterfaces().length() > 0) {
            code = code + ": " + ((InterfaceKey)this.key).getbaseInterfaces() + " "; // No I18N
        }
        code = code + "{\n";  // No I18N
        Node[] nodes = this.getChildren().getNodes();
        for (int i=0; i< nodes.length; i++) {
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent+1);
        }
        code = code + fill + "};\n";  // No I18N
        return code;
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (CreateAliasAction.class),
            SystemAction.get (CreateAttributeAction.class),
            SystemAction.get (CreateConstantAction.class),
            SystemAction.get (CreateEnumAction.class),
            SystemAction.get (CreateExceptionAction.class),
            SystemAction.get (CreateOperationAction.class),
            SystemAction.get (CreateStructAction.class),
            SystemAction.get (CreateUnionAction.class),
            null,
            SystemAction.get (DestroyAction.class),
            SystemAction.get (RenameAction.class)
        };
    }
  
    public void createOperation() {
        TopManager tm = TopManager.getDefault ();
        final OperationPanel panel = new OperationPanel ();
        ExDialogDescriptor descriptor = new ExDialogDescriptor ( panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateOperation"), true,
                                                                 new ActionListener () {
                                                                         public void actionPerformed (ActionEvent event) {
                                                                             if (event.getActionCommand ().equals(ExDialogDescriptor.OK)) {
                                                                                 String name = panel.getName ();
                                                                                 String ret = panel.getReturnType ();
                                                                                 String params = panel.getParameters ();
                                                                                 String except = panel.getExceptions();
                                                                                 String context = panel.getContext ();
                                                                                 boolean isOneway = panel.isOneway ();
                                                                                 OperationKey key = new OperationKey (MutableKey.OPERATION, name, ret, params, except, context, isOneway);
                                                                                 ((MutableChildren)getChildren()).addKey (key);
                                                                             }
                                                                             dialog.setVisible (false);
                                                                             dialog.dispose ();
                                                                         }
                                                                     });
        descriptor.disableOk();
        dialog = tm.createDialog (descriptor);
        dialog.setVisible (true);
    }
  
    public void createAttribute() {
        TopManager tm = TopManager.getDefault ();
        final AttributePanel panel = new AttributePanel ();
        ExDialogDescriptor descriptor = new ExDialogDescriptor ( panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateAttribute"), true,
                                                                 new ActionListener () {
                                                                         public void actionPerformed (ActionEvent event) {
                                                                             if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
                                                                                 String name = panel.getName ();
                                                                                 String type = panel.getType ();
                                                                                 boolean isReadOnly = panel.isReadOnly ();
                                                                                 AttributeKey key = new AttributeKey (MutableKey.ATTRIBUTE, name, type, isReadOnly);
                                                                                 ((MutableChildren)getChildren()).addKey (key);
                                                                             }
                                                                             dialog.setVisible (false);
                                                                             dialog.dispose ();
                                                                         }
                                                                     });
        dialog = tm.createDialog (descriptor);
        dialog.setVisible (true);
    }
  
}
