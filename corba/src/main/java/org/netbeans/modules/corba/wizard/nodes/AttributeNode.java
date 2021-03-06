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

import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.actions.DestroyAction;
import org.netbeans.modules.corba.wizard.nodes.actions.EditAction;
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.AttributePanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/**
 *
 * @author  root
 * @version
 */
public class AttributeNode extends AbstractMutableLeafNode implements Node.Cookie{

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/attribute";
  
    /** Creates new AttributeNode */
    public AttributeNode (NamedKey key) {
        super (key);
        this.getCookieSet().add(this);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
  
    public String generateSelf (int indent) {
        String code = new String ();
        String fill = new String ();
        for (int i=0; i<indent; i++)
            fill = fill + SPACE; // No I18N
        AttributeKey key = (AttributeKey) this.key;
        if (key.isReadOnly ()) {
            code = fill + "readonly ";  // No I18N
        }
        else {
            code = fill;
        }
        code = code + "attribute " + key.getType() + " "+this.getName () +";\n"; // No I18N
        return code;
    }
    
    public ExPanel getEditPanel () {
        AttributePanel p = new AttributePanel ();
        String type = ((AttributeKey)this.key).getType();
        boolean ro = ((AttributeKey)this.key).isReadOnly();
        p.setName (this.getName());
        p.setType (type);
        p.setReadOnly (ro);
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof AttributePanel) {
            String newName = ((AttributePanel)p).getName();
            String newType = ((AttributePanel)p).getType();
            boolean ro = ((AttributePanel)p).isReadOnly();
            if (!this.getName().equals(newName)) {
                this.setName (newName);
                ((AttributeKey)this.key).setName (newName);
            }
            if (!newType.equals(((AttributeKey)this.key).getType()))
                ((AttributeKey)this.key).setType (newType);
            if (ro != ((AttributeKey)this.key).isReadOnly())
                ((AttributeKey)this.key).setReadOnly (ro);
        }
    }
}
