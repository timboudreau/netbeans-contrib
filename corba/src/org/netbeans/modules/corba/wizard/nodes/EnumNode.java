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
import java.util.StringTokenizer;
import java.util.HashSet;
import org.openide.nodes.Children;
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
public class EnumNode extends AbstractMutableContainerNode implements Node.Cookie,
    EnumEntryCreator {
  
    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/enum";
  
    /** Creates new EnumNode */
    public EnumNode (NamedKey key) {
        super (key);
        this.getCookieSet().add (this);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
        this.buildEntries ();
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (CreateEnumEntryAction.class),
            null,
            SystemAction.get (DestroyAction.class),
            SystemAction.get (RenameAction.class)};
    }
  
    public String generateSelf (int indent) {
        String code = "";
        for (int i=0; i< indent; i++) {
            code = code + SPACE;
        }
        code = code + "enum "+ this.getName() +" { "; // No I18N
        Children cld = this.getChildren();
        Node[] nodes = cld.getNodes();
        for (int i=0; i< nodes.length; i++ ) {
            if (i != 0 )
                code = code + ", "; // No I18N
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent);
        }
        code = code + "};\n"; // No I18N
        return code;
    }
  
    public void createEnumEntry () {
        final ModulePanel panel = new ModulePanel ();
        ExDialogDescriptor descriptor = new ExDialogDescriptor (panel, java.util.ResourceBundle.getBundle("org/netbeans/modules/corba/wizard/nodes/Bundle").getString("TXT_CreateEntry"), true,
                                                                new ActionListener () {
                                                                        public void actionPerformed (ActionEvent event) {
                                                                            if (event.getActionCommand().equals(ExDialogDescriptor.OK)){
                                                                                String name = panel.getName ();
                                                                                NamedKey key = new NamedKey (MutableKey.ENUM_MBR, name);
                                                                                ((MutableChildren)getChildren()).addKey (key);
                                                                            }
                                                                            dialog.setVisible (false);
                                                                            dialog.dispose();
                                                                        }
                                                                    });
        descriptor.disableOk();
        dialog = TopManager.getDefault().createDialog (descriptor);
        dialog.setVisible (true);
    }
  
    private void buildEntries () {
        StringTokenizer tk = new StringTokenizer (((EnumKey)this.key).getValues(),",");
        HashSet keys = new HashSet();
        while (tk.hasMoreTokens ()){
            String name = tk.nextToken().trim();
            keys.add (new NamedKey (MutableKey.ENUM_MBR, name));
        }
        ((MutableChildren)this.getChildren()).addKeys (keys);
    }
}
