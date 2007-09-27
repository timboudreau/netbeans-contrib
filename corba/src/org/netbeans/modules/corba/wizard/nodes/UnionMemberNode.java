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

import java.util.StringTokenizer;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.UnionMemberPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.AliasPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;

/**
 *
 * @author  root
 * @version
 */
public class UnionMemberNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/unionmember";
  
    /** Creates new UnionMemberNode */
    public UnionMemberNode (NamedKey key) {
        super (key);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i=0; i< indent; i++)
            code = code + SPACE;  // No I18N
        UnionMemberKey key = (UnionMemberKey) this.key;
        if (key.isDefaultValue ()) {
            code = code + "default: " + key.getType() + " " + this.getName (); // No I18N
        }
        else {
            // Bug for wchar, char
            code = code +"case " + key.getLabel() + ": ";  // No I18N
            code = code + key.getType () + " " + this.getName (); // No I18N 
        }
        // Handle array here
        if (key.getLength().length() > 0) {
            StringTokenizer tk = new StringTokenizer (key.getLength(),",");
            while (tk.hasMoreTokens()) {
                String dim = tk.nextToken().trim();
                code = code + " ["+ dim +"]";   // No I18N
            }
        }
        code = code + ";\n"; // No I18N
        return code;
    }


    public void destroy () {
        if (((UnionMemberKey)this.key).isDefaultValue())
            ((UnionNode)this.getParentNode ()).canAdd = true;
        super.destroy ();
    }
    
    public ExPanel getEditPanel () {
        UnionMemberKey key = (UnionMemberKey) this.key;
        
        if (key.isDefaultValue()) {
            AliasPanel p = new AliasPanel ();
            p.setName (this.getName());
            p.setType (((UnionMemberKey)this.key).getType());
            p.setLength (((UnionMemberKey)this.key).getLength());
            return p;
        }
        else {
            UnionMemberPanel p = new UnionMemberPanel();
            p.setName (this.getName());
            p.setType (((UnionMemberKey)this.key).getType());
            p.setLabel (((UnionMemberKey)this.key).getLabel());
            p.setLength (((UnionMemberKey)this.key).getLength());
            return p;
        }
    }
    
    public void reInit (ExPanel p) {
        String newName;
        String newType;
        String newLabel;
        String newLength;
        
        if (p instanceof UnionMemberPanel) {
            UnionMemberPanel up = (UnionMemberPanel) p;
            newName = up.getName();
            newType = up.getType();
            newLabel = up.getLabel();
            newLength = up.getLength();
            
        }
        else if ( p instanceof AliasPanel) {
            AliasPanel ap = (AliasPanel) p;
            newName = ap.getName();
            newType = ap.getType();
            newLength = ap.getLength();
            newLabel = null;
        }
        else 
            return;
        
        UnionMemberKey key = (UnionMemberKey) this.key;
        if (! key.getName().equals(newName)) {
            this.setName (newName);
            key.setName (newName);
        }
        if (! key.getType().equals(newType))
            key.setType (newType);
        if (newLabel!=null && !key.getLabel().equals(newLabel))
            key.setLabel (newLabel);
        if (! key.getLength().equals(newLength))
            key.setLength (newLength);
    }
}
