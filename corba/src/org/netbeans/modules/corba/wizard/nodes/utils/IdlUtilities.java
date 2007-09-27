/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
