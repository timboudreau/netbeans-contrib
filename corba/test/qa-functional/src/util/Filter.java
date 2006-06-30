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

package util;

import java.util.ArrayList;

public class Filter {

    ArrayList filterAfter;
    ArrayList filterFrom;
    ArrayList replaceFrom;
    ArrayList replaceTo;

    public Filter () {
        filterAfter = new ArrayList ();
        filterFrom = new ArrayList ();
        replaceFrom = new ArrayList ();
        replaceTo = new ArrayList ();
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
            str = Helper.replaceAll(str, (String) replaceFrom.get (a), (String) replaceTo.get (a));
        return str;
    }
    
}
