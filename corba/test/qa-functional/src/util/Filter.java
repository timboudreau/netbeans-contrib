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

import java.util.ArrayList;

public class Filter {

    ArrayList filterAfter;
    ArrayList replaceFrom;
    ArrayList replaceTo;
    
    public Filter () {
        filterAfter = new ArrayList ();
        replaceFrom = new ArrayList ();
        replaceTo = new ArrayList ();
    }
    
    public void addFilterAfter (String str) {
        filterAfter.add (str);
    }
    
    public void addReplace (String from, String to) {
        replaceFrom.add (from);
        replaceTo.add (to);
    }
    
    public String filter (String str) {
        for (int a = 0; a < filterAfter.size (); a ++) {
            String f = (String) filterAfter.get (a);
            int i = str.indexOf(f);
            if (i >= 0)
                str = str.substring(0, i + f.length());
        }
        for (int a = 0; a < replaceFrom.size (); a ++)
            str = Helper.replaceAll(str, (String) replaceFrom.get (a), (String) replaceTo.get (a));
        return str;
    }
    
}
