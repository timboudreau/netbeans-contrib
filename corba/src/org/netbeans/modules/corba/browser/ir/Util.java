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

package org.netbeans.modules.corba.browser.ir;

import java.util.ResourceBundle;
import org.omg.CORBA.*;
import org.openide.util.NbBundle;
import org.netbeans.modules.corba.browser.ir.util.AssertException;


/*
 * @author Karel Gardas
 */

public class Util {
    
    private static ResourceBundle bundle;

    public static String TypeCode2String (TypeCode tc) {

        switch (tc.kind ().value ()) {
        case TCKind._tk_null : return "null";
        case TCKind._tk_void : return "void";
        case TCKind._tk_short : return "short";
        case TCKind._tk_long : return "long";
        case TCKind._tk_ushort : return "unsigned short";
        case TCKind._tk_ulong : return "unsigned long";
        case TCKind._tk_float : return "float";
        case TCKind._tk_double : return "double";
        case TCKind._tk_boolean : return "boolean";
        case TCKind._tk_char : return "char";
        case TCKind._tk_octet : return "octet";
        case TCKind._tk_any : return "any";
        case TCKind._tk_TypeCode : return "TypeCode";
        case TCKind._tk_Principal : return "Principal";
        case TCKind._tk_objref : return "objref";
        case TCKind._tk_struct : return "struct";
        case TCKind._tk_union : return "union";
        case TCKind._tk_enum : return "enum";
        case TCKind._tk_string : return "string";
        case TCKind._tk_sequence : return "sequence";
        case TCKind._tk_array : return "array";
        case TCKind._tk_alias : return "alias";
        case TCKind._tk_except : return "exception";
        case TCKind._tk_longlong : return "long long";
        case TCKind._tk_ulonglong : return "unsigned long long";
        case TCKind._tk_longdouble : return "long double";
        case TCKind._tk_wchar : return "wchar";
        case TCKind._tk_wstring : return "wstring";
        case TCKind._tk_fixed : return "fixed";
        }
        return "";
    }


    public static String typeCode2TypeString (TypeCode tc, StringHolder dimension) {
        try{
            switch (tc.kind ().value ()) {
            case TCKind._tk_abstract_interface : return tc.name();
            case TCKind._tk_null : return "null";
            case TCKind._tk_void : return "void";
            case TCKind._tk_short : return "short";
            case TCKind._tk_long : return "long";
            case TCKind._tk_ushort : return "unsigned short";
            case TCKind._tk_ulong : return "unsigned long";
            case TCKind._tk_float : return "float";
            case TCKind._tk_double : return "double";
            case TCKind._tk_boolean : return "boolean";
            case TCKind._tk_char : return "char";
            case TCKind._tk_octet : return "octet";
            case TCKind._tk_any : return "any";
            case TCKind._tk_TypeCode : return "TypeCode";
            case TCKind._tk_Principal : return "Principal";
            case TCKind._tk_objref : return tc.name();
            case TCKind._tk_struct : return tc.name();
            case TCKind._tk_union : return tc.name();
            case TCKind._tk_enum : return tc.name();
            case TCKind._tk_string :
                return ( tc.length() == 0) ? "string" : "string <"+tc.length()+">";
            case TCKind._tk_sequence :
                if (tc.length() != 0)
                    return "sequence <"+typeCode2TypeString(tc.content_type(), dimension)+","+tc.length()+"> ";
                else
                    return "sequence <"+typeCode2TypeString(tc.content_type(), dimension)+"> ";
            case TCKind._tk_array :
                if (dimension != null){
                    if (dimension.value == null)
                        dimension.value = new String();
                    dimension.value = dimension.value + "["+Integer.toString(tc.length())+"]";
                }
                return typeCode2TypeString (tc.content_type(), dimension);
            case TCKind._tk_alias : return tc.name();
            case TCKind._tk_except : return tc.name();
            case TCKind._tk_longlong : return "long long";
            case TCKind._tk_ulonglong : return "unsigned long long";
            case TCKind._tk_longdouble : return "long double";
            case TCKind._tk_wchar : return "wchar";
            case TCKind._tk_wstring :
                return ( tc.length() == 0) ? "wstring" : "wstring <"+tc.length()+">";
            case TCKind._tk_fixed : 
                try {
                    short digits = tc.fixed_digits();
                    short scale = tc.fixed_scale();
                    return "fixed <"+digits+","+scale+">";
                }catch (Exception e)
                {
                    return "fixed";
                }
            case TCKind._tk_native : return tc.name();
            case TCKind._tk_value_box: return tc.name();
            case TCKind._tk_value: return tc.name();
            }
        }catch (org.omg.CORBA.TypeCodePackage.BadKind bk){
        }
        return "";
    }

    public static String typeCode2TypeString (TypeCode tc) {
        return typeCode2TypeString (tc, null);
    }
    
    public static String generatePostTypePragmas (String name, String repositoryId, int indent) {
        if (repositoryId.startsWith ("IDL:")) {
            if (repositoryId.endsWith (":1.0")) {
                /** Ok looks like: "IDL:xxxxxxx:1.0" */
                return "";
             }
             else {
                /** looks like: "IDL:xxxxxxxx:?.?" */
                String fill = "";
                for (int i=0; i< indent; i++)
                    fill = fill + org.netbeans.modules.corba.browser.ir.nodes.IRAbstractNode.SPACE;
                return fill + "#pragma version " + name +" "+ repositoryId.substring (repositoryId.lastIndexOf (':')+1)+"\n\n";
             }
         }
         else {
            /** looks like: "DCE:xxxxxxxx"  */
            String fill = "";
            for (int i=0; i< indent; i++)
                fill = fill + org.netbeans.modules.corba.browser.ir.nodes.IRAbstractNode.SPACE;
            return fill + "#pragma ID " + name + " \"" + repositoryId +"\"\n\n";
        }
    }
    
    
    public static final String generatePreTypePragmas (String repositoryId, String absoluteName, org.omg.CORBA.StringHolder currentPrefix ,int indent) {
        if (!repositoryId.startsWith ("IDL:"))
            return "";  // Not CORBA IDL, probably DCE 
        java.util.StringTokenizer tk = new java.util.StringTokenizer (repositoryId,":");
        if (tk.countTokens() != 3)
            return "";
        tk.nextToken();  // Skeep it;
        String id = tk.nextToken();
        if (!absoluteName.startsWith("::"))
            return ""; // Bad absolute name
        absoluteName = absoluteName.substring (2);
        StringBuffer sb = new StringBuffer (absoluteName);
        for (int i=0; i<sb.length(); i++) {
            if (sb.charAt(i) == ':') {
                sb.setCharAt (i,'/');
                sb.deleteCharAt (++i);
            }
        }
        absoluteName = new String (sb);
        String prefix = "";
        int i,j;
        for (i=id.length()-1, j=absoluteName.length()-1; i>=0 && j>=0; i--, j--) {
            if (id.charAt(i) != absoluteName.charAt(j)) {
                prefix = id.substring (0,i+1);
                i=0;    // Found
                break;
            }
         } 
        if (i>0) { // Was found?
            prefix = id.substring (0,i);
        }
        
         if (prefix.equals (currentPrefix.value))
            return "";
         else {
            currentPrefix.value = prefix;
            String code = "";
            for (i=0; i< indent; i++) {
                code = code + org.netbeans.modules.corba.browser.ir.nodes.IRAbstractNode.SPACE;
            }
            code = code + "#pragma prefix \"" + prefix +"\"\n";
            return code;
         }
    }

    public static String getLocalizedString (String txt){
        if (bundle == null)
            bundle = NbBundle.getBundle(Util.class);
        return bundle.getString(txt);
    }
    
    public static void assert (boolean condition, String message) throws AssertException {
        if (! condition)
            throw new AssertException ("Assertion Failed: "+message);  // No I18N
    }
    
    public static String idlType2TypeString (IDLType type, Contained parent, StringHolder dimension) {
        String suffix;
        
        if (dimension != null)
           suffix = typeCode2TypeString (type.type(),dimension);
        else
           suffix = typeCode2TypeString (type.type());
        
        switch (type.type().kind().value()) {
            case TCKind._tk_struct:
            case TCKind._tk_alias:
            case TCKind._tk_enum:
            case TCKind._tk_except:
            case TCKind._tk_objref:
            case TCKind._tk_union:
            case TCKind._tk_value:
            case TCKind._tk_abstract_interface:
            case TCKind._tk_value_box:
                try {
                    Contained contained = ContainedHelper.narrow (type);
                    if (contained == null)
                        return suffix;   // We can not procede, we return at least part of name, can be OK
                    Contained myContainer = ContainedHelper.narrow (contained.defined_in());
                    if (contained == null)
                        return suffix;  // We can not procede, we return at least part of name, can be OK
                    String preffix= myContainer.absolute_name();
                    String absName;
                    if (parent == null) {
                        absName = "";
                    }
                    else {
                        absName = parent.absolute_name();
                    }
                    String partialName = absName;
                    while (partialName.length()>0) {
                        if (partialName.equals(preffix))
                            return suffix;
                        int index = partialName.lastIndexOf("::");
                        if (index == -1)
                            break;
                        partialName = partialName.substring (0,index);
                    }
                    return preffix+"::"+suffix;
                }catch (org.omg.CORBA.SystemException se) {
                    return suffix;
                }
            case TCKind._tk_Principal:
            case TCKind._tk_TypeCode:
                return "CORBA::"+suffix;
            default:
                return suffix;
        }
    }
    
    public static String idlType2TypeString (IDLType type, Contained parent) {
        return idlType2TypeString (type, parent, null);
    }
}


/*
 * $Log
 * $
 */
