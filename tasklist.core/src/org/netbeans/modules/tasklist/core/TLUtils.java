/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.core;


import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.ErrorManager;
import org.openide.TopManager;


/** Various utility methods shared by the various tasklist related modules
 *
 ** TODO - use this method everywhere!!!
 *
 * @author Tor Norbye */
public final class TLUtils {

    /** Return the Line object for a particular line in a file
     */
    public static Line getLineByNumber(DataObject dobj, int lineno) {
        // Go to the given line
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc != null) {
                Line.Set ls = lc.getLineSet();
                if (ls != null) {
                    // XXX HACK
                    // I'm subtracting 1 because empirically I've discovered
                    // that the editor highlights whatever line I ask for plus 1
                    Line l = ls.getCurrent(lineno-1);
                    return l;
                }
            }
        } catch (Exception e) {
            TopManager.getDefault().getErrorManager().
                notify(ErrorManager.INFORMATIONAL, e);
        }
        return null;
    }

    /** Replace the given symbol on the line with the new symbol - starting
        roughly at the given column (symbol should be at col or col+1)
    */
    public static void replaceSymbol(StringBuffer sb, String text, int pos, String symbol, 
                                String newSymbol, boolean bold) {
        //System.out.println("replace('" + text + "', " + pos + ", '" + symbol + "', '" + newSymbol + "')");
        if (pos > 0) {
            // For some compilers, the position is off by 1 so make sure 
            // we catch the earliest possible match
            pos--;
        }
        int from = 0;
        int symLen = symbol.length();
        int texLen = text.length();
        while (true) {
            int n = text.indexOf(symbol, pos);
            if (n == -1) {
                break;
            }
            if ((n+symLen < texLen-1) &&
                Character.isJavaIdentifierPart(text.charAt(n+symLen))) {
                pos = n+symLen;
                continue;
            }
            
            for (int i = from; i < n; i++) {
                sb.append(text.charAt(i));
            }
            if (bold) {
                sb.append("<b>");
            }
            sb.append(newSymbol);
            if (bold) {
                sb.append("</b>");
            }
            pos = n+symLen;
            from = pos;
        }
        for (int i = from; i < texLen; i++) {
            sb.append(text.charAt(i));
        }
    }

    /** Get a "window of text with the given line as the middle line.
     * @param line The line we want to obtain a window for.
     * @param currText If non null, use this line instead of the
     *     text on the current line.
     */
    public static void appendSurroundingLine(StringBuffer sb, Line line, 
                                             int offset) {
        DataObject dobj = line.getDataObject();
        try {
            LineCookie lc = (LineCookie)dobj.getCookie(LineCookie.class);
            if (lc == null) {
                return;
            }
            Line.Set ls = lc.getLineSet();
            if (ls == null) {
                return;
            }

            int lineno = line.getLineNumber();
            Line before = ls.getCurrent(lineno+offset);
            sb.append(before.getText());
        } catch (Exception e) {
            TopManager.getDefault().getErrorManager().
                notify(ErrorManager.INFORMATIONAL, e);
        }
    }

    /** Compute first difference position for two strings */
    public static int firstDiff(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        int n;
        if (n1 < n2) {
            n = n1;
        } else {
            n = n2;
        }
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    /** Compute last difference position for two strings. Returns
        DISTANCE FROM THE END! */
    public static int lastDiff(String s1, String s2) {
        int n1 = s1.length()-1;
        int n2 = s2.length()-1;
        int i = 0;
        while ((n2 >= 0) && (n1 >= 0)) {
            if (s1.charAt(n1) != s2.charAt(n2)) {
                return i;
            }
            --n2;
            --n1;
            ++i;
        }
        return i;
    }    
    

}



