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


public class IREnumDefNode extends IRContainerNode {
  
    private EnumDef _enum;
    private static final String ENUM_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/enum";
  
  
    private static class EnumCodeGenerator implements GenerateSupport {
    
        private EnumDef _enum;
    
        public EnumCodeGenerator (EnumDef enum){
            this._enum = enum;
        }
    
        public String generateHead (int indent){
            String code ="";
            for (int i=0; i<indent; i++)
                code = code + SPACE;
            code = code + "enum " + _enum.name()+" { ";
            return code;
        }
    
        public String generateSelf (int indent){
            String code = generateHead(indent);
            String[] members = _enum.members();
            for (int i = 0; i < members.length; i++){
                if (i != 0)
                    code = code + ", ";
                code = code + members[i]; 
            }
            code = code + generateTail(indent);
            return code;
        }
    
        public String generateTail (int indent){
            return "};\n\n";	 
        }
    
    }

    /** Creates new IREnumDefNode */
    public IREnumDefNode(Contained value) {
        super(new EnumChildren(EnumDefHelper.narrow(value)));
        _enum = EnumDefHelper.narrow(value);
        setIconBase(ENUM_ICON_BASE);
    }
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public String getDisplayName(){
        if (this.name == null) {
            if ( _enum != null){
                this.name = this._enum.name();
            }
            else
                this.name = "";
        }
        return this.name;
    }
  
    public Sheet createSheet(){
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get(Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_EnumName")){
                public java.lang.Object getValue(){
                    return _enum.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString("TITLE_Id"),String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString ("TIP_EnumId")){
                public java.lang.Object getValue(){
                    return _enum.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), Util.getLocalizedString ("TIP_EnumVersion")){
                public java.lang.Object getValue() {
                    return _enum.version();
                }
            });
        return s;
    }
  
    public String getRepositoryId () {
        return this._enum.id();
    }
  
    public GenerateSupport createGenerator () {
        if (this.generator == null)
            this.generator = new EnumCodeGenerator (this._enum);
        return this.generator;
    }
  
    public static GenerateSupport createGeneratorFor (Contained type){
        EnumDef enum = EnumDefHelper.narrow (type);
        if (enum == null)
            return null;
        return new EnumCodeGenerator (enum);
    }

}
