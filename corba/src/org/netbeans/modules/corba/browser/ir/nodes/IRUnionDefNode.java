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
import org.omg.CORBA.ORBPackage.InconsistentTypeCode;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.openide.nodes.Sheet;
import org.openide.nodes.PropertySupport;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;



public class IRUnionDefNode extends IRContainerNode {
  
    // public static final boolean DYN_ANY_WORKAROUND = false;
    public static final boolean DYN_ANY_WORKAROUND = true;
    private UnionDef _union;
    private static final String UNION_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/union";
  
    private static class UnionCodeGenerator implements GenerateSupport {
        private UnionDef _union;
    
        public UnionCodeGenerator (UnionDef union) {
            this._union = union;
        }
    
        public String generateHead (int indent, StringHolder currentPrefix) {
            String code = Util.generatePreTypePragmas (_union.id(), _union.absolute_name(), currentPrefix, indent);
            String fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "union " + _union.name() + " switch ( " +Util.typeCode2TypeString(_union.discriminator_type())+ " ) {\n";
            return code;
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = "";
            code = code + generateHead(indent, currentPrefix);
            String fill = "";
            for (int i=0; i<=indent; i++)
                fill =fill + SPACE;
            UnionMember[] members = _union.members();
            StringHolder dimension = new StringHolder();
            for ( int i = 0; i < members.length; i++){
                code = code + fill;
                TypeCode tc = members[i].label.type();
                switch (tc.kind().value()){
                case TCKind._tk_boolean:
                    code = code +"case " +new Boolean (members[i].label.extract_boolean()).toString();
                    break;
                case TCKind._tk_char:
                    code = code +"case "+ "\'" + new Character (members[i].label.extract_char()).toString() + "\'";
                    break;
                case TCKind._tk_short:
                    code = code +"case "+ Short.toString ( members[i].label.extract_short());
                    break;
                case TCKind._tk_long:
                    code = code +"case "+ Integer.toString ( members[i].label.extract_long());
                    break;
                case TCKind._tk_longlong:
                    code = code +"case "+ Long.toString ( members[i].label.extract_longlong());
                    break;
                case TCKind._tk_ushort:
                    code = code +"case "+ Short.toString ( members[i].label.extract_ushort());
                    break;
                case TCKind._tk_ulong:
                    code = code +"case "+ Integer.toString ( members[i].label.extract_ulong());
                    break;
                case TCKind._tk_ulonglong:
                    code = code +"case "+ Long.toString ( members[i].label.extract_ulonglong());
                    break;
                case TCKind._tk_enum:
                    // Workaround for some CORBA implementations, which do not support DynAny
                    if (DYN_ANY_WORKAROUND){
                        try{
                            org.omg.CORBA.portable.InputStream in = members[i].label.create_input_stream();
                            int value = in.read_long();
                            String name = tc.member_name(value);
                            code = code +"case "+ name;
                        }catch(Exception e){org.openide.TopManager.getDefault().notifyException(e);};
                    }
                    else{
                        try{
                            DynEnum denum = ORB.init().create_dyn_enum (tc);
                            denum.from_any ( members[i].label);
                            code = code +"case "+ denum.value_as_string();
                        }catch (InconsistentTypeCode itc){}
                        catch (Invalid invalid){}
                    }
                    break;
                default:
                    code = code + "default";
                }
                code= code + ": ";
                dimension.value = null;
                code = code + Util.typeCode2TypeString (members[i].type, dimension)+" ";
                code = code + members[i].name + ((dimension.value==null)?"":dimension.value) + ";\n";
            } 
            code = code + generateTail (indent);
            return code;
        }
    
        public String generateTail (int indent){
            String code = "";
            for (int i=0; i<indent; i++)
                code =code + SPACE;
            return code + "}; // " + _union.name() +"\n"+Util.generatePostTypePragmas(_union.name(),_union.id(),indent)+"\n";
        }
    
    }

    /** Creates new IRUnionDefNode */
    public IRUnionDefNode(Contained value) {
        super ( new UnionChildren(UnionDefHelper.narrow(value)));
        _union = UnionDefHelper.narrow(value);
        setIconBase(UNION_ICON_BASE);
    }
  
    public String getDisplayName(){
	if (this.name == null) {
    	    if (_union != null)
        	this.name = _union.name();
    	    else
        	this.name = "";
	}
	return this.name;
    }
  
  
    public String getName(){
        return this.getDisplayName();
    }
  
    public Sheet createSheet () {
        Sheet s = Sheet.createDefault();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Name"), String.class, Util.getLocalizedString ("TITLE_Name"),Util.getLocalizedString ("TIP_UnionName")){
                public java.lang.Object getValue () {
                    return _union.name();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Id"), String.class, Util.getLocalizedString ("TITLE_Id"), Util.getLocalizedString ("TIP_UnionId")){
                public java.lang.Object getValue () {
                    return _union.id();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_Version"), String.class, Util.getLocalizedString ("TITLE_Version"),Util.getLocalizedString ("TIP_UnionVersion")){
                public java.lang.Object getValue () {
                    return _union.version();
                }
            });
        ss.put ( new PropertySupport.ReadOnly ( Util.getLocalizedString ("TITLE_DiscriminatorType"), String.class, Util.getLocalizedString ("TITLE_DiscriminatorType"), Util.getLocalizedString ("TIP_UnionDiscriminatorType")){
                public java.lang.Object getValue () {
                    return Util.typeCode2TypeString (_union.discriminator_type());
                }
            });
        return s;
    }
  
    public String getRepositoryId () {
        return this._union.id();
    }
  
    public GenerateSupport createGenerator () {
        if (this.generator == null) 
            this.generator = new UnionCodeGenerator (_union);
        return this.generator;
    }
  
    public static GenerateSupport createGeneratorFor (Contained type){
        UnionDef union = UnionDefHelper.narrow (type);
        if (union == null)
            return null;
        return new UnionCodeGenerator (union);
    }
  
}
