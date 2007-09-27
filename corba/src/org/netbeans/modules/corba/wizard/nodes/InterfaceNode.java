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
            fill = fill + SPACE;
        if (((InterfaceKey)key).isAbstract())
            code = fill + "abstract "; // No I18N
        else
            code = fill;
        code = code + "interface " +  this.getName () + " ";  // No I18N
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
            SystemAction.get (EditAction.class)
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
        descriptor.disableOk();                                                             
        dialog = tm.createDialog (descriptor);
        dialog.setVisible (true);
    }
    
    public ExPanel getEditPanel() {
        InterfacePanel p = new InterfacePanel();
        p.setName (this.getName());
        p.setBase (((InterfaceKey)this.key).getbaseInterfaces());
        p.setAbstract (((InterfaceKey)this.key).isAbstract());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof InterfacePanel) {
            String newName = ((InterfacePanel)p).getName();
            String newBase = ((InterfacePanel)p).getBase();
            boolean abst = ((InterfacePanel)p).isAbstract();
            InterfaceKey key = (InterfaceKey)this.key;
            if (!newName.equals (key.getName())) {
                this.setName (newName);
                key.setName (newName);
            }
            if (!newBase.equals (key.getbaseInterfaces())) {
                key.setBaseInterfaces (newBase);
            }
            if (abst != key.isAbstract()) {
                key.setAbstract (abst);
            }
        }
    }
  
}
