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
            code = code +": "+ key.getbaseInterfaces()+ " ";
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
                        boolean pub = p.isPublic();
                        ValueKey key = new ValueKey (MutableKey.VALUE,name,base, pub);
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
