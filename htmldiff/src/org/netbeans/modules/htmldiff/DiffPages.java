/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.htmldiff;

import java.io.*;

/** Generates HTML page with differences from two readers.
 *
 * @author  Jaroslav Tulach
 */
public final class DiffPages extends Object {
    /** Generates the diff from two provided pages.
     */
    public static void main (String[] args) throws IOException {
        
        File f1 = new File (args[0]); 
        File f2 = new File (args[1]);
        
        FileReader r1 = new FileReader (f1);
        FileReader r2 = new FileReader (f2);
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        
        File f3 = new File (args[2]);
        FileWriter w = new FileWriter (f3);
        for (int i = 0; i < res.length; i++) {
            if (res[i].isDifference()) {
                // put there both
                w.write ("<strike>");
                w.write (res[i].getOld());
                w.write ("</strike><span style=\"background: #FFFF00\">");
                w.write (res[i].getNew());
                w.write ("</span>");
            } else {
                w.write (res[i].getNew ());
            }
        }
        w.close ();
        r1.close ();
        r2.close ();
    }
}
