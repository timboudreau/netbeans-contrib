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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.NbBundle;
import org.openide.*;
import org.netbeans.modules.corba.wizard.nodes.keys.ValueTypeKey;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.*;
import org.netbeans.modules.corba.wizard.nodes.keys.ValueKey;
import org.netbeans.modules.corba.wizard.nodes.keys.ValueFactoryKey;
import org.netbeans.modules.corba.wizard.nodes.keys.MutableKey;
import org.netbeans.modules.corba.wizard.nodes.utils.ValueCreator;
import org.netbeans.modules.corba.wizard.nodes.utils.ValueFactoryCreator;
/**
 *
 * @author  tzezula
 * @version 
 */
public class ValueTypeNode extends InterfaceNode implements ValueCreator, ValueFactoryCreator {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/value";
    private Dialog dlg;
    
    /** Creates new ValueTypeNode */
    public ValueTypeNode(ValueTypeKey key) {
        super (key);
        this.setName(key.getName());
        this.setIconBase (ICON_BASE);
    }
    
    public String generateSelf (int indent) {
        String code = "";
        for (int i=0; i< indent; i++) {
            code = code + SPACE;
        }
        ValueTypeKey key = (ValueTypeKey) this.key;
        if (key.isAbstract())
            code = code + "abstract ";  // No I18N
        else if (key.isCustom())
            code = code + "custom ";    // No I18N
        code  = code + "valuetype " + this.getName () +" "; // No I18N
        if (key.getbaseInterfaces().length() > 0) {
            code = code +": ";
            if (key.isTruncatable())
                code = code + "truncatable ";
            code = code + key.getbaseInterfaces()+ " ";
        }
        if (key.getSupports().length() > 0) {
            code = code +"supports "+key.getSupports()+ " ";
            
        }
        code = code + "{\n"; // No I18N
        Node[] nodes = this.getChildren().getNodes();
        for (int i=0; i< nodes.length; i++) {
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent+1);
        }
        code = code + "};\n"; // No I18N
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
            SystemAction.get (CreateValueAction.class),
            SystemAction.get (CreateValueFactoryAction.class),
            null,
            SystemAction.get (DestroyAction.class),
            SystemAction.get (EditAction.class)
        };
    }
    
    public void createValue() {
        TopManager tm = TopManager.getDefault();
        final ValuePanel p = new ValuePanel ();
        ExDialogDescriptor dd = new ExDialogDescriptor (p,NbBundle.getBundle (ValueTypeNode.class).getString("TXT_CreateValue"),
            true,
            new ActionListener () {
                public void actionPerformed (ActionEvent event) {
                    if (event.getActionCommand ().equals(ExDialogDescriptor.OK)) {
                        String name = p.getName();
                        String base = p.getType();
                        String length = p.getLength();
                        boolean pub = p.isPublic();
                        ValueKey key = new ValueKey (MutableKey.VALUE,name,base,length,pub);
                        ((MutableChildren)ValueTypeNode.this.getChildren()).addKey (key);
                    }
                    dlg.setVisible (false);
                    dlg.dispose();
                }
        });
        dd.disableOk ();
        dlg = tm.createDialog (dd);
        dlg.setVisible (true);
    }
    
    public void createFactory () {
        TopManager tm = TopManager.getDefault();
        final ValueFactoryPanel p = new ValueFactoryPanel();
        ExDialogDescriptor dd = new ExDialogDescriptor (p, NbBundle.getBundle (ValueTypeNode.class).getString ("TXT_CreateValueFactory"),
        true,
        new ActionListener () {
            public void actionPerformed (ActionEvent event) {
                if (event.getActionCommand().equals (ExDialogDescriptor.OK)) {
                    String name = p.getName();
                    String args = p.getParams();
                    ValueFactoryKey key = new ValueFactoryKey (MutableKey.VALUE_FACTORY, name, args);
                    ((MutableChildren)ValueTypeNode.this.getChildren()).addKey (key);
                }
                dlg.setVisible (false);
                dlg.dispose ();
            }
        });
        dd.disableOk();
        dlg = tm.createDialog (dd);
        dlg.setVisible (true);
    }
    
    public ExPanel getEditPanel () {
        ValueTypePanel p = new ValueTypePanel ();
        ValueTypeKey key = (ValueTypeKey) this.key;
        p.setName (this.getName());
        p.setSupports (key.getSupports());
        p.setBase (key.getbaseInterfaces());
        p.setAbstract (key.isAbstract());
        p.setTruncatable (key.isTruncatable());
        p.setCustom (key.isCustom());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ValueTypePanel) {
            ValueTypePanel vp = (ValueTypePanel) p;
            ValueTypeKey key = (ValueTypeKey) this.key;
            String newName = vp.getName();
            String newSupports = vp.getSupports();
            String newBase = vp.getBase();
            boolean newTruncatable = vp.isTruncatable();
            boolean newCustom = vp.isCustom();
            boolean newAbstract = vp.isAbstract();
            if (!this.getName().equals(newName)) {
                this.setName (newName);
                key.setName (newName);
            }
            if (!key.getSupports().equals (newSupports)) {
                key.setSupports (newSupports);
            }
            if (!key.getbaseInterfaces().equals(newBase)) {
                key.setBaseInterfaces (newBase);
            }
            key.setAbstract (newAbstract);
            key.setCustom (newCustom);
            key.setTruncatable (newTruncatable);
        }
    }

    public boolean canCreateValue() {
        return !((ValueTypeKey)this.key).isAbstract();
    }
    
    public boolean canCreateFactory() {
        return !((ValueTypeKey)this.key).isAbstract();
    }
    
}
