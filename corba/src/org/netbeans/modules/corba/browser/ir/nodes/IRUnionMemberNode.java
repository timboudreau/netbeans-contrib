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
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;

/**
 *
 * @author  tzezula
 * @version 
 */
public class IRUnionMemberNode extends IRLeafNode {
    
    public UnionMember _mbr;
    
    private static final String PRIMITIVE_ICON_BASE=
        "org/netbeans/modules/corba/idl/node/declarator";
    
    class UnionMemberCodeGenerator implements GenerateSupport {
        
        public String generateHead (int indent, StringHolder currentPrefix) {
            return "";
        }
        
        public String generateSelf (int indent, StringHolder currentPrexif) {
            String code = "";
            for (int i=0; i< indent; i++)
                code = code + SPACE;
            String value = getLabelValue (_mbr.label);
            if (!value.equals("default"))
                code = code + "case ";
            code = code + value + ": ";
            StringHolder dimension = new StringHolder();
            code = code + Util.idlType2TypeString (_mbr.type_def,((IRContainerNode)getParentNode()).getOwner(),dimension)+" ";
            code = code + _mbr.name + ((dimension.value==null)?"":dimension.value) + ";\n";
            return code;
        }
        
        public String generateTail (int indent) {
            return "";
        }
        
        public String generateLabel (int indent, StringHolder currentPrefix) {
            String code = "";
            for (int i=0; i< indent; i++)
                code = code + SPACE;
            String value = getLabelValue (_mbr.label);
            if (!value.equals("default"))
                code = code + "case ";
            code = code + value + ": ";
            return code;
        }
        
        public String getRepositoryId () {
            return Util.getLocalizedString("MSG_PrimitiveType");
        }
    }

    /** Creates new IRUnionMemberNode */
    public IRUnionMemberNode(UnionMember mbr) {
        this._mbr = mbr;
        this.setIconBase (PRIMITIVE_ICON_BASE);
        this.getCookieSet().add ( new UnionMemberCodeGenerator());
    }
    
    public String getName () {
        return this.getDisplayName();
    }
    
    public String getDisplayName () {
        if ( _mbr == null)
            return "";
        else
            return _mbr.name;
    }
    
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set set = s.get (Sheet.PROPERTIES);
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_UnionMemberName")) {
            public java.lang.Object getValue () {
                return _mbr.name;
            }
        });
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Type"), String.class, Util.getLocalizedString("TITLE_Type"), Util.getLocalizedString("TIP_UnionMemberType")) {
            public java.lang.Object getValue () {
                return Util.typeCode2TypeString(_mbr.type);
            }
        });
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Dimension"), String.class, Util.getLocalizedString("TITLE_Dimension"), Util.getLocalizedString("TIP_UnionMemberDimension")) {
            public java.lang.Object getValue () {
                    StringHolder holder = new StringHolder();
                    Util.typeCode2TypeString (_mbr.type, holder);
                    return (holder.value==null)?"":holder.value;
            }
        });
        set.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Label"), String.class, Util.getLocalizedString("TITLE_Dimension"), Util.getLocalizedString("TIP_UnionMemberLabel")) {
            public java.lang.Object getValue () {
                return getLabelValue (_mbr.label);
            }
        });
        return s;
    }
    
    private String getLabelValue (org.omg.CORBA.Any any) {
        TypeCode tc = any.type();
        switch (tc.kind().value()){
            case TCKind._tk_boolean:
                if (any.extract_boolean())
                    return "TRUE";
                else
                    return "FALSE";
            case TCKind._tk_char:
                return new Character (any.extract_char()).toString();
            case TCKind._tk_short:
                return Short.toString ( any.extract_short());
            case TCKind._tk_long:
                return Integer.toString ( any.extract_long());
            case TCKind._tk_longlong:
                return Long.toString ( any.extract_longlong());
            case TCKind._tk_ushort:
                return Short.toString ( any.extract_ushort());
            case TCKind._tk_ulong:
                return Integer.toString ( any.extract_ulong());
            case TCKind._tk_ulonglong:
                return Long.toString ( any.extract_ulonglong());
            case TCKind._tk_enum:
                // Workaround for some CORBA implementations, which do not support DynAny
                try{
                    org.omg.CORBA.portable.InputStream in = any.create_input_stream();
                    int value = in.read_long();
                    return tc.member_name(value);
                }catch(Exception e){
                    return "?";
                }
            default:
                return "default";
           }
    }
    
    public org.omg.CORBA.TypeCode getTypeCode () {
        return this._mbr.type;
    }

}
