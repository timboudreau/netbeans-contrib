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
        return "sequence <"+typeCode2TypeString(tc.content_type(), dimension)+","+tc.length()+"> ";
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
      case TCKind._tk_fixed : return "fixed";
      }
    }catch (org.omg.CORBA.TypeCodePackage.BadKind bk){
    }
    return ""; 
  }
  
  public static String typeCode2TypeString (TypeCode tc) {
    return typeCode2TypeString (tc, null);
  }
  
  public static String getLocalizedString (String txt){
    if (bundle == null)
      bundle = NbBundle.getBundle(Util.class);
    return bundle.getString(txt);
  }
}


/*
 * $Log
 * $
 */
