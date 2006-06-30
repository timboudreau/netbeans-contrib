/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.wizard.nodes;

import java.io.OutputStream;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;

/**
 *
 * @author  root
 * @version 
 */
public abstract class SENode extends AbstractMutableContainerNode implements Node.Cookie,
    StructMemberCreator {

    private Dialog dialog;
              
    /** Creates new SENode */
    public SENode (NamedKey key) {
        super (key);
        this.getCookieSet().add (this);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] {SystemAction.get (CreateStructMemberAction.class),
                                   null,
                                   SystemAction.get (DestroyAction.class),
                                   SystemAction.get (EditAction.class)
                    };
    }
    public void createStructMember() {
        TopManager tm = TopManager.getDefault ();
        final AliasPanel panel = new AliasPanel ();
        ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateMember"), true,
                                                                new ActionListener () {
                                                                        public void actionPerformed (ActionEvent event) {
                                                                            if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                                                                                String name = panel.getName ();
                                                                                String type = panel.getType ();
                                                                                String length = panel.getLength ();
                                                                                AliasKey key = new AliasKey (MutableKey.STRUCT_MBR, name, type, length);
                                                                                ((MutableChildren)SENode.this.getChildren()).addKey (key);
                                                                            }
                                                                            dialog.setVisible (false);
                                                                            dialog.dispose();
                                                                        }
                                                                    });
        descriptor.disableOk();     
        dialog = tm.createDialog (descriptor);
        dialog.setVisible (true);
    }
}
