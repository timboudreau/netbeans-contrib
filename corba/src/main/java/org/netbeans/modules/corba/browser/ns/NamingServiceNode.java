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

package org.netbeans.modules.corba.browser.ns;

import java.util.ResourceBundle;
import java.util.Stack;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.openide.nodes.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.settings.CORBASupportSettings;
/**
 *
 * @author  Tomas Zezula
 */
public abstract class NamingServiceNode extends AbstractNode {

    private String name;
    private String kind;
    private String ior;
    private ORB orb;
    private ResourceBundle bundle;
    private boolean interfaceInitialized;
    private org.omg.CORBA.InterfaceDef interfaceDef;
    protected CORBASupportSettings css;

    /** Creates new NamingServiceNode */
    public NamingServiceNode(Children cld) {
        super (cld);
    }
    
    public void setName (String n) {
        this.name = n;
    }

    public String getName () {
        return this.name;
    }
    
    public String getAbsoluteNameAsString () {
        Stack stack = this.getHierarchy ();
        StringBuffer result = new StringBuffer ();
        while (stack.size() > 0) {
            NamingServiceNode node = (NamingServiceNode) stack.pop();
            result.append ("/"+node.getName());
            String kind = node.getKind ();
            if (kind != null && kind.length()>0) {
                result.append ("."+node.getKind());
            }
        }
        return result.toString();
    }
    
    public String[][] getAbsoluteNameAsArray () {
        Stack stack = this.getHierarchy ();
        String[][] result = new String[2][stack.size()];
        for (int i=0; stack.size() > 0; i++) {
            NamingServiceNode node = (NamingServiceNode) stack.pop();
            result[0][i] = node.getName();
            result[1][i] = node.getKind();
        }
        return result;
    }
    
    public NameComponent[] getAbsoluteNameAsCosNamingName () {
        Stack stack = this.getHierarchy ();
        NameComponent[] result = new NameComponent [stack.size()];
        for (int i=0; stack.size()>0; i++) {
            NamingServiceNode node = (NamingServiceNode) stack.pop();
            result[i] = new NameComponent ();
            result[i].id = node.getName();
            result[i].kind = node.getKind();
        }
        return result;
    }

    public void setKind (String n) {
        this.kind = n;
    }

    public String getKind () {
        return this.kind;
    }
    
    public String getIOR () {
        return this.ior;
    }
    
    public void setIOR (String ior) {
        this.ior = ior;
    }
    
    public org.omg.CORBA.InterfaceDef getInterface () {
        if (!this.interfaceInitialized) {
            this.interfaceDef = this.createInterface();
            this.interfaceInitialized = true;
        }
        return this.interfaceDef;
    }
    
    public ORB getORB () {
        if (this.orb == null)
            this.lazyInit();
        return this.orb;
    }
    
    protected abstract org.omg.CORBA.InterfaceDef createInterface ();
    
    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put(new PropertySupport.ReadOnly("Name", String.class, this.getLocalizedString("CTL_Name"), this.getLocalizedString("TIP_Name")) {
            public java.lang.Object getValue() {
                return NamingServiceNode.this.getName();
            }
        });
        ss.put(new PropertySupport.ReadOnly("Kind", String.class, this.getLocalizedString("CTL_Kind"), this.getLocalizedString("TIP_Kind")) {
            public java.lang.Object getValue() {
                return getKind();
            }
        });
        ss.put(new PropertySupport.ReadOnly("IOR", String.class, this.getLocalizedString("CTL_IOR"), this.getLocalizedString("TIP_IOR")) {
            public java.lang.Object getValue() {
                return getIOR();
            }
        });        
        return s;
    }
    
    protected String getLocalizedString (String key) {
        if (this.bundle == null)
            this.bundle = NbBundle.getBundle (NamingServiceNode.class);
        return this.bundle.getString (key);
    }
    
    protected void lazyInit () {
        css = (CORBASupportSettings) CORBASupportSettings.findObject (CORBASupportSettings.class, true);
        this.orb = css.getORB ();
    }
    
    private Stack getHierarchy () {
        Stack stack = new Stack ();
        Node node = this;
        Node rootNSNode = ContextNode.getDefault();
        while ((node instanceof NamingServiceNode) && (node != rootNSNode)) {
            stack.push (node);
            node = node.getParentNode ();
        }
        stack.pop (); // The mount point should go out
        return stack;
    }

}
