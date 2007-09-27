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
import org.openide.nodes.Node;
import org.netbeans.modules.corba.wizard.nodes.keys.*;
import org.netbeans.modules.corba.wizard.nodes.actions.*;
import org.netbeans.modules.corba.wizard.nodes.gui.OperationPanel;
import org.netbeans.modules.corba.wizard.nodes.gui.ExPanel;

/**
 *
 * @author  root
 * @version
 */
public class OperationNode extends AbstractMutableLeafNode implements Node.Cookie {

    private static final String ICON_BASE = "org/netbeans/modules/corba/idl/node/operation";
  
    /** Creates new OperationNode */
    public OperationNode (NamedKey key) {
        super (key);
        this.getCookieSet ().add (this);
        this.setName (key.getName());
        this.setIconBase (ICON_BASE);
    }
  
  
    public String generateSelf (int indent) {
        String code = new String ();
        for (int i=0; i<indent; i++)
            code = code + SPACE;  // No I18N
        OperationKey key = (OperationKey) this.key;
        if (key.isOneway ())
            code = code + "oneway "; // No I18N
        code = code + key.getReturnType () + " "; // No I18N
        code = code + this.getName () + " ("; // No I18N
        code = code + key.getParameters () + ")"; // No I18N
        if (key.getExceptions().length() > 0) {
            code = code + " raises ("+key.getExceptions () +")"; // No I18N
        }
        if (key.getContext().length() > 0) {
            code = code + " context (";  // No I18N
            String ctx = key.getContext ();
            StringTokenizer tk = new StringTokenizer (ctx, ","); // No I18N
            while (tk.hasMoreTokens()) {
                String one = tk.nextToken ().trim();
                if (one.startsWith ("\"") && one.endsWith ("\"")) { // No I18N
                    code = code + one + ", "; // No I18N
                }
                else {
                    code = code + "\"" + one +"\", "; // No I18N
                }
        
            }
            code = code.substring(0,code.length() -2);
            code = code + ")";  // No I18N
        }
        code = code + ";\n"; // No I18N
        return code;
    }
    
    public ExPanel getEditPanel() {
        OperationPanel p = new OperationPanel ();
        p.setName (this.getName());
        OperationKey key = (OperationKey) this.key;
        p.setContext (key.getContext());
        p.setExceptions (key.getExceptions());
        p.setOneway (key.isOneway());
        p.setParameters (key.getParameters());
        p.setReturnType (key.getReturnType());
        return p;
    }
    
    public void reInit (ExPanel p) {
        if (p instanceof OperationPanel) {
            OperationPanel op = (OperationPanel) p;
            OperationKey key = (OperationKey) this.key;
            String newName = op.getName();
            String newCtx = op.getContext();
            String newRet = op.getReturnType();
            String newParams = op.getParameters();
            String newExcept = op.getExceptions();
            boolean ow = op.isOneway();
            if (!key.getName().equals(newName)) {
                this.setName (newName);
                key.setName (newName);
            }
            if (!key.getContext().equals(newCtx))
                key.setContext (newCtx);
            if (!key.getExceptions().equals(newExcept))
                key.setExceptions (newExcept);
            if (!key.getParameters().equals(newParams))
                key.setParameters (newParams);
            if (!key.getReturnType().equals(newRet))
                key.setReturnType (newRet);
            if (key.isOneway() != ow)
                key.setOneway (ow);
        }
    }
  
}
