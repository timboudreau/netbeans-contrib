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
import org.netbeans.modules.corba.browser.ir.util.IRDelegate;

public class IRPrimitiveNode extends IRLeafNode implements IRDelegate {
  
    private static final String PRIMITIVE_ICON_BASE=
        "org/netbeans/modules/corba/idl/node/declarator";
    private IDLType tc;
  
    private class PrimitiveCodeGenerator implements GenerateSupport {
    
    
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
            code = code + Util.idlType2TypeString(tc,((IRContainerNode)getParentNode()).getOwner(),dimension) + " " + getName() + ((dimension.value==null)?"":dimension.value)+";\n";
            return code;
        }
    
        public String generateTail (int indent){
            return "";
        }
        
        public String getRepositoryId () {
            return Util.getLocalizedString("MSG_PrimitiveType");
        }
    
    }

    /** Creates new IRPrimitiveNode */
    public IRPrimitiveNode(IDLType baseType, String name) {
        this.name = name;
        this.tc = baseType;
        this.setIconBase(PRIMITIVE_ICON_BASE);
        this.getCookieSet().add (new PrimitiveCodeGenerator());
    }
  
    public String getName(){
        return this.name;
    }
  
    public String getDisplayName(){
        return this.name;
    }
  
    public Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get( Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_PrimitiveName")){ 
                public java.lang.Object getValue() {
                    return name;
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Type"), String.class, Util.getLocalizedString("TITLE_Type"), Util.getLocalizedString("TIP_PrimitiveType")){
                public java.lang.Object getValue() {
                    return Util.typeCode2TypeString(tc.type(), new StringHolder());
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Dimension"), String.class, Util.getLocalizedString("TITLE_Dimension"), Util.getLocalizedString("TIP_PrimitiveDimension")){
                public java.lang.Object getValue() {
                    StringHolder holder = new StringHolder();
                    Util.typeCode2TypeString (tc.type(), holder);
                    return (holder.value==null)?"":holder.value;
                }
            });
        return s;
    }
  
    public TypeCode getTypeCode () {
        return tc.type();
    }
    
    public org.omg.CORBA.IRObject getIRObject() {
        return this.tc;
    }
    
}
