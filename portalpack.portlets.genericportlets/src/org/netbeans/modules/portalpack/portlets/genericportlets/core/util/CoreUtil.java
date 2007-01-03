/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */

package org.netbeans.modules.portalpack.portlets.genericportlets.core.util;

import java.util.StringTokenizer;

/**
 *
 * @author Satya
 */
public class CoreUtil {
    
    public static final String CORE_LOGGER = "PORTAL_PLUGIN_CORE";
    
    public static boolean validateJavaTypeName(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        if (!name.equals(trimmed)) {
            return false;
        }
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return isIdentifier(name);
            
        }
        return false;
              /*
                else {
                        // qualified name
                        String pkg = name.substring(0, index).trim();
                        if (!validatePackageName(pkg))return false;
                        // sequence of identifiers splitted by dots
                        String type = name.substring(index + 1).trim();
                        if (!scannedIdentifier(type))return false;
                        return true;
                }*/
    }
    
    public static boolean validatePackageName(String name)
    {
        if (name == null || name.trim().equals("")) {
            return true;
        }
        String trimmed = name.trim();
        if (!name.equals(trimmed)) {
            return false;
        }
        
        StringTokenizer st = new StringTokenizer(name,".");
        
        while(st.hasMoreElements()){
            String pkg = (String)st.nextElement();
            if(!isIdentifier(pkg))
                return false;
        }
        
        return true;
              
    }
    
    public static boolean isIdentifier(String name){
        if (name.length()==0)return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))){
            return false;
        }
        for (int a=1;a<name.length();a++){
            if
                    (!Character.isJavaIdentifierPart(name.charAt(a))){
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateString(String name, boolean allowSpaces) {
        if(name == null || name.trim().length() == 0){
            return false;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if(!Character.isLetterOrDigit(c) && !((c == '_') || (allowSpaces && c == ' '))){
                return false;
            }
        }
        return true;
    }
    
    public static boolean validateXmlString(String name) {
        if(name == null || name.trim().length() == 0){
            return true;
        }
        String value = name.trim();
        for(int i=0; i<value.length(); i++) {
            char c = value.charAt(i);
            if((c == '<') || (c == '>')) {
                return false;
            }
        }
        return true;
    }
    
    
}
