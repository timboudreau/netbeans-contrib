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

import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.gui.ConstPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;
/**
 *
 * @author  root
 * @version
 */
public class ConstantNode extends AbstractMutableLeafNode {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/const";

    /** Creates new CreateConstantAction */
    public ConstantNode (NamedKey key) {
        super (key);
        this.setName (key.getName ());
        this.setIconBase (ICON_BASE);
    }
  
  
  
    public String generateSelf (int indent){
        String code = new String ();
        String fill = new String ();
        for (int i=0; i<indent; i++)
            fill = fill + SPACE; // No I18N
        ConstKey key = (ConstKey) this.key;
        String type = key.getType();
        String value = key.getValue();
        if ("char".equals(type) && !value.startsWith("\'")) {
            value = "\'" + value + "\'";
        }
        else if ("string".equals(type) && !value.startsWith("\"")) {
            value = "\"" + value + "\"";
        }
        else if ("wchar".equals(type) && !value.startsWith("L\'")) {
            value = "L\'"+value+"\'";
        }
        else if ("wstring".equals(type) && !value.startsWith("L\"")) {
            value = "L\""+value+"\"";
        }
        code = fill + "const "+ type + " "+ this.getName()+ " = "+ value +";\n"; // No I18N
        return code;
    }
    
    public ExPanel getEditPanel () {
        ConstPanel p = new ConstPanel();
        p.setName (this.getName());
        p.setType (((ConstKey)this.key).getType());
        p.setValue (((ConstKey)this.key).getValue());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof ConstPanel) { 
            String newName = ((ConstPanel)p).getName();
            String newValue = ((ConstPanel)p).getValue();
            String newType = ((ConstPanel)p).getType();
            ConstKey key = (ConstKey) this.key;
            if (! key.getName ().equals(newName)) {
                key.setName (newName);
                this.setName (newName);
            }
            if (! key.getValue().equals(newValue))
                key.setValue (newValue);
            if (! key.getType().equals(newType))
                key.setType (newType);
        }
    }
  
}
