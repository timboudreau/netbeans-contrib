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

package org.netbeans.modules.corba.wizard.nodes.utils;

import java.util.StringTokenizer;
/**
 *
 * @author  Tomas Zezula
 * @version 
 */
public class IdlUtilities {
    
    private static String[] primitiveTypes;

    /** Creates new IdlUtilities */
    private IdlUtilities() {
    }
    
    
    
    public static final boolean isValidIDLIdentifier (String ident) {
        if (ident == null)
            return false;
        ident = ident.trim();
        if (ident.length() == 0)
            return false;
        String up_ident = ident.toUpperCase ();
        if (!(up_ident.charAt(0) >= 'A' && up_ident.charAt(0) <='Z') &&
            !(up_ident.charAt(0)>=192 && up_ident.charAt(0) <= 220 && up_ident.charAt(0) != 208 && up_ident.charAt(0) != 215) &&
            !(ident.charAt(0) == 254 || ident.charAt(0) == 255) &&
             (ident.charAt(0) !='_'))
            return false;
        
        for (int i=0; i< up_ident.length(); i++) {
            char c = up_ident.charAt(i);
            if (c >= 'A' && c <= 'Z')
                continue;
            else if (c>=192 && c<=220 && c!=208 && c!=215)  
                continue;
            else if (Character.isDigit (c))
                continue;
            else if (c == '_')
                continue;
            else if (ident.charAt (i) == 254 || ident.charAt (i) == 255)
                continue;
            else
                return false;
        }
        return true;
    }
    
    
    public static boolean validLength (String length) {
        length = length.trim ();
        if (length.length() == 0)
            return true;
        if (length.charAt (length.length()-1) ==',')
            return false;
        StringTokenizer tk = new StringTokenizer (length, ",");
        while (tk.hasMoreTokens()) {
            String token = tk.nextToken().trim();
            try {
                Integer.parseInt (token);
            }catch (NumberFormatException nfe) {
                if (!IdlUtilities.isValidIDLIdentifier (token))
                    return false;
            }
        }
        return true;
    }
    
    public static String[] getIDLPrimitiveTypes () {
        if (primitiveTypes == null)
            primitiveTypes = new String[] {
                                    "any",                  // No I18N
                                    "boolean",              // No I18N
                                    "char",                 // No I18N
                                    "CORBA::TypeCode",      // No I18N
                                    "CORBA::Principle",     // No I18N
                                    "double",               // No I18N
                                    "fixed<,>",             // No I18N
                                    "float",                // No I18N
                                    "long",                 // No I18N
                                    "long double",          // No I18N
                                    "long long",            // No I18N
                                    "Object",               // No I18N
                                    "octet",                // No I18N
                                    "short",                // No I18N
                                    "string",               // No I18N
                                    "unsigned long",        // No I18N
                                    "unsigned long long",   // No I18N
                                    "unsigned short",       // No I18N
                                    "ValueBase",            // No I18N
                                    "void",                 // No I18N
                                    "wchar",                // No I18N
                                    "wstring"               // No I18N
                                 };
        return primitiveTypes;
    }

}
