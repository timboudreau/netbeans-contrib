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

package org.netbeans.modules.corba.browser.ir.util;

/**
 *
 * @author  tzezula
 * @version 
 */
public final class StringConvert extends Object {
    
    
    
    public static String toHexChar (char c) {
        int low = c %16;
        int hi = c / 16;
        return "\\x"+table[hi]+table[low];
    }
    
    private static String convertChar (char c) {
        switch (c) {
            case 13:
                return "\\r";   // Carriage Return
            case 10:
                return "\\n";   // New Line
            case 9:
                return "\\t";   // Horizontal Tab
            case 11:
                return "\\v";   // Vertical Tab
            case 8:
                return "\\b";   // Backspace
            case 12:
                return "\\f";   // Form Feed
            case 7:
                return "\\a";   // Alert
            default:
                return toHexChar (c);
        }
    }
    
    public static String convert (char c) {
        if (c <0x20) {
            return convertChar (c);
        }
        else if (c == '\\') {
            return "\\\\";
        }
        else if (c == '\'') {
            return "\\\'";
        }
        else if (c == '\"') {
            return "\\\"";
        }
        else if (c == '?') {
            return "\\?";
        }
        else {
            return new Character (c).toString();
        }
    }

    public static String convert (String str) {
        StringBuffer buffer = new StringBuffer ();
        for (int i=0; i< str.length(); i++) {
            char c = str.charAt (i);
            if (c < 0x20) {
                String newC = convertChar (c); 
                buffer.append (newC);
            }
            else if (c == '\\') {
                buffer.append ("\\\\");
            }
            else if (c == '\'') {
                buffer.append ("\\\'");
            }
            else if (c == '\"') {
                buffer.append ("\\\"");
            }
            else if (c == '?') {
                buffer.append ("\\?");
            }
            else {
                buffer.append (c);
            }
        }
        return buffer.toString();
    }
    
    private final static char table[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

}
