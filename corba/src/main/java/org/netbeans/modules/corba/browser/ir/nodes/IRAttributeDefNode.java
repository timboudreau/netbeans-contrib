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

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.IRDelegate;

public class IRAttributeDefNode extends IRLeafNode implements IRDelegate {

    private AttributeDef _attribute;
    private static final String ATTRIBUTE_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/attribute";  //NOI18N
  
    private class AttributeCodeGenerator  implements GenerateSupport {
    

        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_attribute.id(), _attribute.absolute_name(), currentPrefix, indent);      //NOI18N
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            switch (_attribute.mode().value()){
            case AttributeMode._ATTR_NORMAL:
                code = code + "attribute ";         //NOI18N
                break;
            case AttributeMode._ATTR_READONLY:
                code = code + "readonly attribute ";    //NOI18N
                break;
            }
            code = code + Util.idlType2TypeString (_attribute.type_def(),((IRContainerNode)getParentNode()).getOwner())+" ";     //NOI18N
            code = code + _attribute.name() + ";\n";        //NOI18N
            code = code + generateTail (indent);
            return code;  
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_attribute.name(), _attribute.id(), indent);      //NOI18N
        }
        
        public String getRepositoryId () {
            return _attribute.id();
        }
    
    }
  
    /** Creates new IRAttributeDefNode */
    public IRAttributeDefNode(Contained value) {
        super();
        _attribute = AttributeDefHelper.narrow(value);
        setIconBase(ATTRIBUTE_ICON_BASE);
        this.getCookieSet().add ( new AttributeCodeGenerator ());
    }
  
    public String getDisplayName(){
        if (this.name == null) {
            if (_attribute != null)
                this.name = _attribute.name();
            else
                this.name = "";
        }
        return this.name;
    }
  
    public String getName(){
        return this.getDisplayName();
    }
  
    protected Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Name"),String.class,Util.getLocalizedString("TITLE_Name"),Util.getLocalizedString("TIP_AttributeName")){
                public java.lang.Object getValue(){
                    return _attribute.name();
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Id"),String.class,Util.getLocalizedString("TITLE_Id"),Util.getLocalizedString("TIP_AttributeId")){
                public java.lang.Object getValue(){
                    return _attribute.id();
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Version"),String.class,Util.getLocalizedString("TITLE_Version"),Util.getLocalizedString("TIP_AttributeVersion")){
                public java.lang.Object getValue(){
                    return _attribute.version();
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Type"),String.class,Util.getLocalizedString("TITLE_Type"),Util.getLocalizedString("TIP_AttributeType")){
                public java.lang.Object getValue(){
                    return Util.typeCode2TypeString(_attribute.type());
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly(Util.getLocalizedString("TITLE_Mode"),String.class,Util.getLocalizedString("TITLE_Mode"),Util.getLocalizedString("TIP_AttributeMode")){
                public java.lang.Object getValue(){
                    switch (_attribute.mode().value()){
                    case AttributeMode._ATTR_NORMAL:
                        return "readwrite";                 // NO I18N
                    case AttributeMode._ATTR_READONLY:
                        return "readonly";                  // NO I18N
                    default:
                        return "";
                    }
                }
            });
        return s;
    }
    
    public org.omg.CORBA.IRObject getIRObject () {
        return this._attribute;
    }
  
}
