/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.corba.browser.ir.nodes;

import org.omg.CORBA.*;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IRAttributeDefNode extends IRLeafNode {

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
            code = code + Util.typeCode2TypeString (_attribute.type())+" ";     //NOI18N
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
  
}
