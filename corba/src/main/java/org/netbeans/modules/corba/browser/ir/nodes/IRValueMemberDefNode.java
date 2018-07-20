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
 * IRValueMemberDefNode.java
 *
 * Created on August 22, 2000, 4:11 PM
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.TopManager;
import org.openide.nodes.Sheet;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;
import org.netbeans.modules.corba.browser.ir.util.IRDelegate;
/**
 *
 * @author  tzezula
 * @version 
 */
public class IRValueMemberDefNode extends IRLeafNode implements Node.Cookie, IRDelegate {
    
    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/declarator";
    private ValueMemberDef _member;
    
    private class ValueMemberCodeGenerator implements GenerateSupport {
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_member.id(), _member.absolute_name(), currentPrefix, indent);      //NOI18N
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            short modifier = _member.access();
            switch (modifier) {
                case PRIVATE_MEMBER.value:
                    code = code + "private ";
                    break;
                case PUBLIC_MEMBER.value:
                    code = code + "public ";
                    break;
                }
            code = code + Util.idlType2TypeString(_member.type_def(),((IRContainerNode)getParentNode()).getOwner()) + " " + _member.name()+ ";\n";        //NOI18N
            code = code + generateTail (indent);
            return code;  
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_member.name(), _member.id(), indent);      //NOI18N
        }
        
        public String getRepositoryId () {
            return _member.id();
        }
    }

    /** Creates new IRValueMemberDefNode */
    public IRValueMemberDefNode(Contained contained) {
        super ();
        this._member = ValueMemberDefHelper.narrow (contained);
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add ( new ValueMemberCodeGenerator());
    }
    
    public String getDisplayName () {
        return this.getName();
    }
    
    public String getName () {
        if (this.name == null) {
            if (this._member != null) {
                this.name = this._member.name();
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
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ValueMemberName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_ValueMemberId")) {
            public java.lang.Object getValue () {
                return _member.id();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_ValueMemberVersion")) {
            public java.lang.Object getValue () {
                return _member.version();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Type"), String.class, Util.getLocalizedString("TITLE_Type"), Util.getLocalizedString ("TIP_ValueMemberType")) {
            public java.lang.Object getValue () {
                return Util.typeCode2TypeString(_member.type());
            }
        });
        set.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Visibility"), String.class, Util.getLocalizedString("TITLE_Visibility"), Util.getLocalizedString ("TIP_ValueMemberVisibility")) {
            public java.lang.Object getValue () {
                short modifier = _member.access();
                switch (modifier) {
                    case PRIVATE_MEMBER.value:
                        return "private";
                    case PUBLIC_MEMBER.value:
                        return "public";
                    default:
                        return "unknown";
                }
            }
        }); 
        return s;
    }

    public org.omg.CORBA.IRObject getIRObject() {
        return this._member;
    }
    
}
