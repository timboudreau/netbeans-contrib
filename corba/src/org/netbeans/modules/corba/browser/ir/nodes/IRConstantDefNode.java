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

import java.io.PrintWriter;
import java.io.IOException;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import org.openide.nodes.*;
import org.openide.TopManager;
import org.openide.util.actions.SystemAction;
import org.openide.actions.OpenAction;
import org.openide.util.datatransfer.ExClipboard;
import org.netbeans.modules.corba.browser.ir.Util;
import org.netbeans.modules.corba.browser.ir.util.Generatable;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupport;
import org.netbeans.modules.corba.browser.ir.util.GenerateSupportFactory;


/**
 * Class ConstantDefNode
 *
 */
public class IRConstantDefNode extends IRLeafNode implements Node.Cookie, Generatable {

    ConstantDef _constant;
  

    private static final String CONSTANT_ICON_BASE =
        "org/netbeans/modules/corba/idl/node/const";
  
  
    private class ConstantCodeGenerator implements GenerateSupport {
    
    
        public String generateHead (int indent, StringHolder currentPrefix){
            return Util.generatePreTypePragmas (_constant.id(), _constant.absolute_name(), currentPrefix, indent);
        }
    
        public String generateSelf (int indent, StringHolder currentPrefix){
            String code = generateHead (indent, currentPrefix);
            String  fill = "";
            for (int i=0; i<indent; i++)
                fill = fill + SPACE;
            code = code + fill + "const ";
            code = code + Util.idlType2TypeString ( _constant.type_def(),((IRContainerNode)getParentNode()).getOwner())+ " ";
            code = code + _constant.name() + " = ";
            code = code + getValue() + ";\n";
            code = code + generateTail(indent) +"\n";
            return code;
        }
    
        public String generateTail (int indent){
            return Util.generatePostTypePragmas (_constant.name(), _constant.id(), indent);
        }
    
        /** Returns the value of constant as String
         *  @return String value
         */
        public String getValue() {
            Any value = _constant.value();
            switch (value.type().kind().value()){
            case TCKind._tk_boolean:
                if (value.extract_boolean())
                    return "TRUE";
                else
                    return "FALSE";
            case TCKind._tk_char:
                return "\'" + new Character(value.extract_char()).toString() + "\'";
            case TCKind._tk_wchar:
                return "\'" + new Character(value.extract_wchar()).toString() + "\'";
            case TCKind._tk_string:
                return "\"" + value.extract_string() + "\"";
            case TCKind._tk_wstring:
                return "\"" + value.extract_wstring() + "\"";
            case TCKind._tk_float:
                return new Float(value.extract_float()).toString();
            case TCKind._tk_double:
                return new Double(value.extract_double()).toString();
            case TCKind._tk_longdouble:
                return new Double(value.extract_double()).toString();
            case TCKind._tk_fixed:
                return value.extract_fixed().toString();
            case TCKind._tk_short:
                return new Short(value.extract_short()).toString();
            case TCKind._tk_long:
                return new Integer(value.extract_long()).toString();
            case TCKind._tk_longlong:
                return new Long (value.extract_longlong()).toString();
            case TCKind._tk_ushort:
                return new Short(value.extract_ushort()).toString();
            case TCKind._tk_ulong:
                return new Integer(value.extract_ulong()).toString();
            case TCKind._tk_ulonglong:
                return new Long (value.extract_ulonglong()).toString();
            case TCKind._tk_enum:
                    try{
                        org.omg.CORBA.portable.InputStream in = value.create_input_stream();
                            int val = in.read_long();
                            String name = value.type().member_name(val);
                            return name;
                        }catch(Exception e){
                            return "";
                        }
            default:
                return "";
            }
        }
        
        public String getRepositoryId () {
            return _constant.id();
        }
  
    }
  

    public IRConstantDefNode(Contained value) {
        super ();
        _constant = ConstantDefHelper.narrow (value);
        this.getCookieSet().add(this);
        setIconBase (CONSTANT_ICON_BASE);
        this.getCookieSet().add ( new ConstantCodeGenerator ());
    }

    public String getDisplayName () {
        if (this.name == null) {
            if (_constant != null)
                this.name = _constant.name ();
            else 
                this.name = "";
        }
        return this.name;
    }

    public String getName () {
        return this.getDisplayName ();
    }
  
    public SystemAction getDefaultAction () {
        SystemAction result = super.getDefaultAction();
        return result == null ? SystemAction.get(OpenAction.class) : result;
    }

    protected Sheet createSheet () {
        Sheet s = Sheet.createDefault ();
        Sheet.Set ss = s.get (Sheet.PROPERTIES);
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Name"), String.class, Util.getLocalizedString("TITLE_Name"), Util.getLocalizedString("TIP_ConstName")) {
                public java.lang.Object getValue () {
                    return _constant.name ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Id"), String.class, Util.getLocalizedString("TITLE_Id"), Util.getLocalizedString("TIP_ConstId")) {
                public java.lang.Object getValue () {
                    return _constant.id ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Version"), String.class, Util.getLocalizedString("TITLE_Version"), 
                                              Util.getLocalizedString("TIP_ConstVersion")) {
                public java.lang.Object getValue () {
                    return _constant.version ();
                }
            });
        ss.put (new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Type"), String.class,Util.getLocalizedString("TITLE_Type"), 
                                              Util.getLocalizedString("TIP_ConstType")) {
                public java.lang.Object getValue () {
                    return Util.typeCode2TypeString (_constant.type ());
                }
            });
    
        ss.put ( new PropertySupport.ReadOnly (Util.getLocalizedString("TITLE_Value"),String.class,Util.getLocalizedString("TITLE_Value"),Util.getLocalizedString("TIP_ConstValue")){
                public java.lang.Object getValue(){
                    return ((ConstantCodeGenerator)IRConstantDefNode.this.getCookie(GenerateSupport.class)).getValue();
                }
            });
    
        return s;
    }

    public void generateCode() {
     
        ExClipboard clipboard = TopManager.getDefault().getClipboard();
        StringSelection genCode = new StringSelection ( this.generateHierarchy ());
        clipboard.setContents(genCode,genCode);
    }

    public void generateCode (PrintWriter out) throws IOException {
        out.println ( this.generateHierarchy ());
    }

    private String generateHierarchy () {
        Node node = this.getParentNode();
        String code ="";
        // Generate the start of namespace
        ArrayList stack = new ArrayList();
        while ( node instanceof IRContainerNode){
            stack.add(node.getCookie(GenerateSupport.class));
            node = node.getParentNode();
        }
        StringHolder currentPrefix = new StringHolder ("");
        int size = stack.size();
        for (int i = size -1 ; i>=0; i--)
            code = code + ((GenerateSupport)stack.get(i)).generateHead((size -i -1), currentPrefix);
        // Generate element itself
        code = code + ((GenerateSupport) this.getCookie (GenerateSupport.class)).generateSelf(size, currentPrefix);
        //Generate tail of namespace
        for (int i = 0; i< stack.size(); i++)
            code = code + ((GenerateSupport)stack.get(i)).generateTail((size -i));
        return code;
    }
}

/*
 * $Log
 * $
 */
