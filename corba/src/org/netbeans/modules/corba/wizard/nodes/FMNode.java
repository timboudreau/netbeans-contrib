/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
    ModuleCreator, StructCreator, ConstantCreator, ExceptionCreator, AliasCreator, UnionCreator,
    EnumCreator, InterfaceCreator, ValueBoxCreator, FwdDclCreator, ValueTypeCreator {

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
    
    public void createValueBox () {
        final ValueBoxPanel panel = new ValueBoxPanel ();
        TopManager tm = TopManager.getDefault();
        ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateValueBox"), true,
        new ActionListener () {
            public void actionPerformed (ActionEvent event) {
                if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                    String name = panel.getName();
                    String type = panel.getType();
                    AliasKey key = new AliasKey (MutableKey.VALUE_BOX,name,type,null);
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
    
    public void createForwardDcl() {
        final ForwardDclPanel panel = new ForwardDclPanel ();
        TopManager tm = TopManager.getDefault();
        ExDialogDescriptor descriptor = new ExDialogDescriptor (panel,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateFwdDcl"),true,
                                                                new ActionListener () {
                                                                    public void actionPerformed (ActionEvent event) {
                                                                        if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
                                                                            String name = panel.getName();
                                                                            boolean intf = panel.isInterface();
                                                                            ForwardDclKey key = new ForwardDclKey (MutableKey.FORWARD_DCL,name,intf);
                                                                            ((MutableChildren)getChildren()).addKey (key);
                                                                        }
                                                                        dialog.setVisible (false);
                                                                        dialog.dispose ();
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
                                                                                boolean abstr = panel.isAbstract();
                                                                                InterfaceKey key = new InterfaceKey (MutableKey.INTERFACE,name,base,abstr);
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
  
    public void createValueType() {
        TopManager tm = TopManager.getDefault();
        final ValueTypePanel p = new ValueTypePanel ();
        ExDialogDescriptor dd = new ExDialogDescriptor (p,java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateValueType"), true,
        new ActionListener() {
            public void actionPerformed (ActionEvent event) {
                if (event.getActionCommand().equals(ExDialogDescriptor.OK)) {
                    String name = p.getName();
                    String base = p.getBase();
                    String supports = p.getSupports();
                    boolean isAbstract = p.isAbstract();
                    boolean isTruncatable = p.isTruncatable();
                    boolean isCustom = p.isCustom();
                    ValueTypeKey key = new ValueTypeKey (MutableKey.VALUETYPE, name, base, supports, isAbstract, isCustom, isTruncatable);
                    ((MutableChildren)FMNode.this.getChildren()).addKey (key);
                }
                dialog.setVisible (false);
                dialog.dispose();
            }
        });
        dd.disableOk();
        this.dialog = tm.createDialog (dd);
        this.dialog.setVisible (true);
    }
    
}
