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
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
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
        this.buildEntries (((EnumKey)this.key).getValuesAndClear());
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (CreateEnumEntryAction.class),
            null,
            SystemAction.get (DestroyAction.class),
            SystemAction.get (EditAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = "";
        for (int i=0; i< indent; i++) {
            code = code + SPACE;
        }
        code = code + "enum "+ this.getName() +" {"; // No I18N
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
    
    public ExPanel getEditPanel () {
        EnumPanel p = new EnumPanel ();
        p.setName (this.getName());
        ArrayList list = ((MutableChildren)this.getChildren()).getKeys();
        Iterator it = list.iterator();
        String entries = "";
        for  (int i=0; it.hasNext(); i++) {
            if (i!=0)
                entries = entries + ", ";
            entries = entries +((NamedKey)it.next()).getName();
        }
        p.setValues (entries);
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof EnumPanel) {
            String newName = ((EnumPanel)p).getName();
            String newValues = ((EnumPanel)p).getValues();
            EnumKey key = (EnumKey) this.key;
            if (!key.getName().equals(newName)) {
                this.setName(newName);
                key.setName (newName);
            }
            buildEntries (newValues);
        }
    }
  
    private void buildEntries (String initialKeys) {
        StringTokenizer tk = new StringTokenizer (initialKeys,",");
        ArrayList keys = new ArrayList();
        while (tk.hasMoreTokens ()){
            String name = tk.nextToken().trim();
            keys.add (new NamedKey (MutableKey.ENUM_MBR, name));
        }
        ((MutableChildren)this.getChildren()).removeAllKeys(false);
        ((MutableChildren)this.getChildren()).addKeys (keys);
    }
}
