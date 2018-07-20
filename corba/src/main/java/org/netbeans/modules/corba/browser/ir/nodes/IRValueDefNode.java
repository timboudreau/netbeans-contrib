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
 * IRValueDefNode.java
 *
 * Created on August 22, 2000, 7:38 PM
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

/**
 *
 * @author  tzezula
 * @version 
 */
public class IRValueDefNode extends IRContainerNode {

    private static final String ICON_BASE = 
        "org/netbeans/modules/corba/idl/node/declarator";
    
    private ValueDef _value;
    
    private class ValueCodeGenerator implements GenerateSupport {
    
        public String generateHead (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas (_value.id(), _value.absolute_name(), currentPrefix, indent);      //NOI18N
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "valuetype " + _value.name() + " {\n";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = Util.generatePreTypePragmas (_value.id(), _value.absolute_name(), currentPrefix, indent);      //NOI18N;
            String prefixBackUp = currentPrefix.value;
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill;
            if (_value.is_abstract()) 
                code = code + "abstract ";
            if (_value.is_custom())
                code = code + "custom ";
            code = code + "valuetype " + _value.name() + " ";
            if (_value.base_value() != null || _value.abstract_base_values().length > 0) {
                code = code + ": ";
                if (_value.is_truncatable())
                    code = code + "truncatable ";
                if (_value.base_value() != null)
                    code = code + _value.base_value().name() + " ";
                for (int i=0; i< _value.abstract_base_values().length; i++) {
                    if (i==0 && _value.base_value() == null)
                        code = code + _value.abstract_base_values()[i].name() + " ";
                    else
                        code = code + ", " + _value.abstract_base_values()[i].name() + " ";
                }
            }
            
            if (_value.supported_interfaces() != null && _value.supported_interfaces().length > 0) {
                code = code + "supports ";
                for (int i=0; i<_value.supported_interfaces().length; i++) {
                    if (i==0) 
                        code = code + _value.supported_interfaces()[i].name()+" ";
                    else
                        code = code + "," + _value.supported_interfaces()[i].name()+" ";
                }
            }
            code = code + "{\n\n";
          
            /**
           Contained[] contained = _value.contents (DefinitionKind.dk_all, true);
            for (int i=0 ; i < contained.length; i++){
                // Workaround for bug in Jdk 1.2 implementation
                // if MARSHAL exception ocured, try to introspect
                // object in another way.
                int dk;
                try{
                    dk = contained[i].def_kind().value();
                }catch (org.omg.CORBA.MARSHAL marshalException){
                    if (contained[i]._is_a("IDL:omg.org/CORBA/OperationDef:1.0"))
                        dk = DefinitionKind._dk_Operation;
                    else 
                        throw new RuntimeException ("Inner Exception is: "+marshalException);
                }
                switch (dk){
                case DefinitionKind._dk_Exception:
                    code = code + IRExceptionDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Struct:
                    code = code + IRStructDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Union:
                    code = code + IRUnionDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Constant:
                    code = code + IRConstantDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Attribute:
                    code = code + IRAttributeDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Operation:
                    code = code + IROperationDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Alias:
                    code = code + IRAliasDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Enum:
                    code = code + IREnumDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_Native:
                    code = code + IRNativeDefNode.createGeneratorFor (contained[i]).generateSelf(indent + 1, currentPrefix);
                    break;
                case DefinitionKind._dk_ValueMember:
                    code = code + IRValueMemberDefNode.createGeneratorFor (contained[i]).generateSelf (indent+1, currentPrefix);
                    break;
               }
            }
            Initializer[] initializers = _value.initializers();
            for (int i=0; i< initializers.length; i++) {
              code = code + IRInitializerNode.createGeneratorFor (initializers[i]).generateSelf (indent +1, currentPrefix);
            }*/
            
            Children cld = (Children) getChildren ();
            if (cld.getState () == Children.NOT_INITIALIZED)
                ((Children)getChildren()).state = Children.SYNCHRONOUS;
            Node[] nodes = cld.getNodes();
            for (int i=0; i< nodes.length; i++) {
                GenerateSupport gs = (GenerateSupport) nodes[i].getCookie (GenerateSupport.class);
                if (gs != null)
                    code = code + gs.generateSelf (indent+1, currentPrefix);
            }
            code = code + generateTail (indent);
            currentPrefix.value = prefixBackUp;
            return code;  
        }
    
        public String generateTail (int indent){
            String fill = "";
            String code = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "};  //" + _value.name() + "\n\n";
            code = code + Util.generatePostTypePragmas (_value.name(), _value.id(), indent);      //NOI18N
            return code;
        }

        public String getRepositoryId () {
            return _value.id();
        }
        
    }
    
    /** Creates new IRValueDefNode */
    public IRValueDefNode (Contained contained) {
        super ( new ValueChildren(ValueDefHelper.narrow(contained)));
        _value = ((ValueChildren)this.getChildren()).getValueStub();
        this.setIconBase (ICON_BASE);
        this.getCookieSet().add (this);
        this.getCookieSet().add (new ValueCodeGenerator());
    }
    
    public String getDisplayName () {
        if (this.name == null) {
            if (this._value != null) {
                this.name = this._value.name();
            }
            else {
                this.name = "";
            }
        }
        return this.name;
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set set = s.get (Sheet.PROPERTIES);
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ValueName")) {
            public java.lang.Object getValue () {
                return name;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_ValueId")) {
            public java.lang.Object getValue () {
                return _value.id ();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString("TIP_ValueVersion")) {
            public java.lang.Object getValue () {
                return _value.version ();
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Abstract"), String.class, Util.getLocalizedString("TITLE_Abstract"), Util.getLocalizedString("TIP_ValueAbstract")) {
            public java.lang.Object getValue () {
                if (_value.is_abstract ())
                    return Util.getLocalizedString ("MSG_Yes");
                else
                    return Util.getLocalizedString ("MSG_No");
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Custom"), String.class, Util.getLocalizedString("TITLE_Custom"), Util.getLocalizedString("TIP_ValueCustom")) {
            public java.lang.Object getValue () {
                if (_value.is_custom ()) 
                    return Util.getLocalizedString ("MSG_Yes");
                else
                    return Util.getLocalizedString ("MSG_No");
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Truncatable"), String.class, Util.getLocalizedString("TITLE_Truncatable"), Util.getLocalizedString("TIP_ValueTruncatable")) {
            public java.lang.Object getValue () {
                if (_value.is_truncatable ())
                    return Util.getLocalizedString ("MSG_Yes");
                else
                    return Util.getLocalizedString ("MSG_No");
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Base"), String.class, Util.getLocalizedString("TITLE_Base"), Util.getLocalizedString("TIP_ValueBase")) {
            public java.lang.Object getValue () {
                if  (_value != null && _value.base_value()!=null) 
                    return _value.base_value().name();
                else
                    return "";
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_AbstractBase"), String.class, Util.getLocalizedString("TITLE_AbstractBase"), Util.getLocalizedString("TIP_ValueAbstractBase")) {
            public java.lang.Object getValue () {
                ValueDef[] avs = _value.abstract_base_values ();
		if (avs == null)
		    return "";
                String list = "";
                for (int i=0; i<avs.length; i++) {
                    if (i!=0)
                        list = list + ", " + avs[i].name();
                    else
                        list = avs[i].name();
                }
                return list;
            }
        });
        set.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Supports"), String.class, Util.getLocalizedString("TITLE_Supports"), Util.getLocalizedString("TIP_ValueSupports")) {
            public java.lang.Object getValue () {
                InterfaceDef[] ints = _value.supported_interfaces ();
		if (ints == null)
		    return "";
                String list = "";
                for (int i=0; i<ints.length; i++) {
                    if (i!=0)
                        list = list + ", " + ints[i].name();
                    else
                        list = ints[i].name();
                }
                return list;
            }
        });
        return s;
    }
    

    public org.omg.CORBA.Contained getOwner () {
        return this._value;
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this._value;
    }    
    
}
