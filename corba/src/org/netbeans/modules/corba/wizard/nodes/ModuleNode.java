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

import java.io.OutputStream;
import org.openide.nodes.Node;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.utils.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ModulePanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;

/**
 *
 * @author  root
 * @version
 */
public class ModuleNode extends FMNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/module";
  
    /** Creates new ModuleNode */
    public ModuleNode (NamedKey key) {
        super (key);
        this.getCookieSet().add(this);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
    public SystemAction[] createActions () {
        return new SystemAction[] {
            SystemAction.get (CreateAliasAction.class),
            SystemAction.get (CreateConstantAction.class),
            SystemAction.get (CreateEnumAction.class),
            SystemAction.get (CreateExceptionAction.class),
            SystemAction.get (CreateFwdDclAction.class),
            SystemAction.get (CreateInterfaceAction.class),
            SystemAction.get (CreateModuleAction.class),
            SystemAction.get (CreateStructAction.class),
            SystemAction.get (CreateUnionAction.class),
            SystemAction.get (CreateValueBoxAction.class),
            SystemAction.get (CreateValueTypeAction.class),
            null,
            SystemAction.get (DestroyAction.class),
            SystemAction.get (EditAction.class)
        };
    }
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for (int i=0; i< indent; i++)
            fill = fill + SPACE;
        code = fill + "module " + this.getName () +" {\n";  // No I18N
        Node[] nodes = this.getChildren().getNodes ();
        for (int i=0; i<nodes.length; i++) {
            code = code + ((AbstractMutableIDLNode)nodes[i]).generateSelf (indent + 1);
            code = code + "\n";
        }
        code = code + fill + "};\n";
        return code;
    }
    
    public ExPanel getEditPanel () {
        ModulePanel p = new ModulePanel ();
        p.setName (this.getName());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ModulePanel) {
            String newName  = ((ModulePanel)p).getName();
            if (!this.key.getName().equals(newName)) {
                this.setName (newName);
                this.key.setName (newName);
            }
        }
    }
  
}
