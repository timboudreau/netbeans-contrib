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
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;


public class IRStructDefNode extends IRContainerNode {

    private StructDef _struct;
    private static final String STRUCT_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/struct";
  
    private static class StructCodeGenerator implements GenerateSupport {
        private StructDef _struct;
    
        public StructCodeGenerator ( StructDef struct){
            this._struct = struct;
        }
    
        public String generateHead (int indent) {
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + "  ";
            code = code + "struct " + _struct.name() + " {\n";
            return code;
        }
    
        public String generateSelf (int indent) {
            String code = "";
            String fill = "";
            code = code + generateHead (indent);
            StructMember[] members = _struct.members();
            for (int i=0; i<= indent; i++)
                fill = fill + "  ";
            for (int i = 0; i < members.length; i++){
                StringHolder dimension = new StringHolder();
                code = code + fill + Util.typeCode2TypeString(members[i].type,dimension)+" "+members[i].name+((dimension.value==null)?"":dimension.value)+";\n";
            }
            code = code + generateTail (indent);
            return code;
        }
    
        public String generateTail (int indent) {
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + "  ";
            code = code + "}; //" + _struct.name() + "\n\n";
            return code;
        }
    
    }
  
    /** Creates new IRStructDefNode */
    public IRStructDefNode(Contained value) {
        super ( new StructChildren (StructDefHelper.narrow(value)));
        _struct = StructDefHelper.narrow(value);
        setIconBase(STRUCT_ICON_BASE);
    }
  
    public String getDisplayName(){
        if (_struct != null)
            return _struct.name();
        else
            return "";
    }
  
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Name"), String.class, Util.getLocalizedString ("TITLE_Name"),Util.getLocalizedString ("TIP_StructName")){
                public java.lang.Object getValue () {
                    return _struct.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Id"), String.class, Util.getLocalizedString ("TITLE_Id"), Util.getLocalizedString ("TIP_StructId")){
                public java.lang.Object getValue () {
                    return _struct.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Version"), String.class, Util.getLocalizedString ("TITLE_Version"),Util.getLocalizedString ("TIP_StructVersion")){
                public java.lang.Object getValue () {
                    return _struct.version();
                }
            });
        return s;
    }
  
    public GenerateSupport createGenerator (){
        if (this.generator == null)
            this.generator = new StructCodeGenerator (_struct);
        return this.generator;
    }
  
    public String getRepositoryId() {
        return this._struct.id();
    }
  
    public static GenerateSupport createGeneratorFor (Contained type){
        StructDef struct = StructDefHelper.narrow ( type);
        if (struct == null)
            return null;
        return new StructCodeGenerator (struct);
    }
  
  
}
