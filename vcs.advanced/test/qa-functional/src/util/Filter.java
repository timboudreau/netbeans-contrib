/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Filter {

    ArrayList filterAfter;
    ArrayList filterFrom;
    ArrayList replaceFrom;
    ArrayList replaceTo;
    ArrayList betweenBefore;
    ArrayList betweenAfter;
    
    public Filter () {
        filterAfter = new ArrayList ();
        filterFrom = new ArrayList ();
        replaceFrom = new ArrayList ();
        replaceTo = new ArrayList ();
        betweenBefore = new ArrayList ();
        betweenAfter = new ArrayList ();
    }
    
    public void addFilterAfter (String str) {
        filterAfter.add (str);
    }
    
    public void addFilterFrom (String str) {
        filterFrom.add (str);
    }
    
    public void addReplace (String from, String to) {
        replaceFrom.add (from);
        replaceTo.add (to);
    }
    
    public void addFilterBetween (String before, String after) {
        betweenBefore.add (before);
        betweenAfter.add (after);
    }
    
    public String filter (String str) {
        for (int a = 0; a < filterAfter.size (); a ++) {
            String f = (String) filterAfter.get (a);
            int i = str.indexOf(f);
            if (i >= 0)
                str = str.substring(0, i + f.length());
        }
        for (int a = 0; a < filterFrom.size (); a ++) {
            String f = (String) filterFrom.get (a);
            int i = str.indexOf(f);
            if (i >= 0)
                str = str.substring(0, i);
        }
        for (int a = 0; a < replaceFrom.size (); a ++)
            str = replaceAll(str, (String) replaceFrom.get (a), (String) replaceTo.get (a));
        for (int a = 0; a < betweenBefore.size (); a ++) {
            String bef = (String) betweenBefore.get (a);
            String aft = (String) betweenAfter.get (a);
            int i1 = str.indexOf (bef);
            if (i1 < 0)
                continue;
            i1 += bef.length();
            int i2 = str.indexOf (aft, i1);
            if (i2 < 0)
                continue;
            str = str.substring (0, i1) + str.substring (i2);
        }
        return str;
    }
    
    public static String replaceAll(String str, String from, String to) {
        if ("".equals (from)  ||  to.startsWith(from))
            return str;
        for (;;) {
            int index = str.indexOf(from);
            if (index < 0)
                break;
            str = str.substring(0, index) + to + str.substring(index + from.length());
        }
        return str;
    }
    
    public void filterStringLinesToStream (PrintStream out, String lines) {
        StringTokenizer st = new StringTokenizer (lines, "\n");
        while (st.hasMoreTokens())
            out.println (filter (st.nextToken()));
    }
    
}
