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

/*
 * IRInitializerNode.java
 *
 * Created on August 28, 2000, 9:37 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.*;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;

/**
 *
 * @author  tzezula
 * @version
 */
public class IRInitializerNode extends IRLeafNode implements Node.Cookie {

    private Initializer _initializer;
    private static final String ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const";
    
    private class InitializerCodeGenerator implements GenerateSupport {
    
        public InitializerCodeGenerator () {
        }
    
        public String generateHead(int indent, StringHolder currentPrefix){
            // Can not have its own prefix
            return "";
        }
    
        /** Because this type of entity has weak identity and is identified by its name and parent
         *  there is no Repository for it, that implies fact that this can't have pragmas at all
         */
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            StringHolder dimension = new StringHolder();
            code = code + "factory " + _initializer.name + " (";
            for (int i=0; i<_initializer.members.length; i++) {
                if (i!=0) 
                    code = code + ", in " + Util.idlType2TypeString(_initializer.members[i].type_def, ((IRContainerNode)getParentNode()).getOwner()) + " " + _initializer.members[i].name;
                else
                    code = code + "in " +  Util.idlType2TypeString(_initializer.members[i].type_def, ((IRContainerNode)getParentNode()).getOwner()) + " " + _initializer.members[i].name;
            }
            code = code +");\n";
            
            return code;
        }
    
        public String generateTail (int indent){
            return "";
        }
        
        public String getRepositoryId () {
            return Util.getLocalizedString("MSG_ValueTypeFactory");
        }
    }

    /** Creates new IRInitializerNode */
    public IRInitializerNode(Initializer initializer) {
        super ();
        this._initializer = initializer;
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add (new InitializerCodeGenerator());
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        if (this.name == null) {
            if (this._initializer != null) {
                this.name = this._initializer.name;
            }
            else {
                this.name = "";
            }
        }
        return this.name;
    }
    
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set set = s.get (Sheet.PROPERTIES);
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_InitializerName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Params"),String.class,Util.getLocalizedString("TITLE_Params"),Util.getLocalizedString("TIP_InitializerParams")) {
            public java.lang.Object getValue () {
                String params = "";
                StructMember[] mbrs = _initializer.members;
                for (int i=0; i< mbrs.length; i++) {
                    if (i>0)
                        params = params + ", in " + Util.typeCode2TypeString (mbrs[i].type)+" "+mbrs[i].name;
                    else
                        params = "in " + Util.typeCode2TypeString (mbrs[i].type)+" "+mbrs[i].name;
                }
                return params;
            }
        });
        return s;
    }
    

}
