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
import java.util.ArrayList;
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
            SystemAction.get (EditAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for ( int i=0; i< indent; i++)
            fill = fill + SPACE;
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
                                                                                 MutableChildren children = ((MutableChildren)UnionNode.this.getChildren());
                                                                                 int lastIndex = children.getKeysCount();
                                                                                 if (lastIndex > 0) {
                                                                                     MutableKey lastKey = (MutableKey) children.getKey(lastIndex-1);
                                                                                     if ((lastKey instanceof MutableKey) && (((UnionMemberKey)lastKey).isDefaultValue()))
                                                                                         lastIndex--;
                                                                                 }
                                                                                 children.addKey (lastIndex,key);
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
    
    public ExPanel getEditPanel() {
        UnionPanel p = new UnionPanel ();
        p.setName (this.getName());
        p.setType (((AliasKey)this.key).getType());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof UnionPanel) {
            UnionPanel up = (UnionPanel) p;
            String newName = up.getName();
            String newType = up.getType();
            AliasKey key = (AliasKey) this.key;
            if (! key.getName().equals(newName)) {
                this.setName (newName);
                key.setName (newName);
            }
            if (! key.getType().equals(newType)) 
                key.setType (newType);
            
        }
    }

}
