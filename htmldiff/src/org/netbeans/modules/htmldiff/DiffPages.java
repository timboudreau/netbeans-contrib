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
import java.net.URL;
import java.util.*;

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
        
        if (f1.isDirectory() && f2.isDirectory ()) {
            generateIndex (f1, f2, new File (args[2]));
            return;
        }
        
        
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
    
    
    private static void generateIndex (File dir1, File dir2, File out) throws IOException {
        HashSet files1 = new HashSet ();
        allRefs (dir1, "", files1);
        
        HashSet files2 = new HashSet ();
        allRefs (dir2, "", files2);
        
        ContentDiff res = ContentDiff.diff (dir1.toURL(), files1, dir2.toURL (), files2);
        
        // write all changed versions
        Iterator pages = res.getPages ().iterator ();
        while (pages.hasNext ()) {
            ContentDiff.Page p = (ContentDiff.Page)pages.next ();

            File f = new File (out, p.getFileName ());
            f.getParentFile().mkdirs ();
            
            System.out.println("Writing diff for " + p.getFileName());
            FileWriter w = new FileWriter (f);
            p.writeDiff (w);
            w.close ();
        }
        
        FileWriter w = new FileWriter (new File (out, "changes.html"));
        
        w.write ("<html><body>");
        w.write ("<h1>Index of Changes</h1>");
        
        final ContentDiff.Cluster[] arr = res.getClusters();
        /** Cluster -> String */
        HashMap traversed = new HashMap ();
            
        for (int i = arr.length - 1 ; i >= 0; i--) {
            
            StringBuffer sb = new StringBuffer ("<UL>");
            boolean change = false;
            Iterator it = arr[i].getPages().iterator ();
            while (it.hasNext ()) {
                ContentDiff.Page p = (ContentDiff.Page)it.next ();
                if (p.isAdded()) {
                    sb.append ("  <LI><a href=\"" + new URL (dir2.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> added\n");
                    sb.append ('\n');
                    change = true;
                    continue;
                }
                if (p.isRemoved()) {
                    sb.append ("  <LI><a href=\"" + new URL (dir1.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> removed\n");
                    sb.append ('\n');
                    change = true;
                    continue;
                }
                System.out.println("page: " + p.getFileName() + " change: " + p.getChanged ());
                if (p.getChanged() > 0) {
                    sb.append (
                        "  <LI><a href=\"" + new URL (out.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> " +
                        " (changed " + p.getChanged () + 
                        " %) <a href=\"" + new URL (dir1.toURL (), p.getFileName ()) + "\">old</a> " +
                        "   <a href=\"" + new URL (dir2.toURL (), p.getFileName ()) + "\">new</a> "
                    );
                    sb.append ('\n');
                    change = true;
                    continue;
                }
            }
            
            ContentDiff.Cluster[] deps = arr[i].getReferences();
            for (int j = 0; j < deps.length; j++) {
                String sub = (String)traversed.get (deps[j]);
                if (sub != null) {
                    sb.append ("  <LI>" + sub);
                    change = true;
                }
            }
            
            sb.append ("</UL>");
            if (change) {
                w.write (sb.toString ());

                traversed.put (arr[i], sb.toString());
            }
        }
        
        w.write ("</body></html>");
        
        w.close ();
    }
    
    private static void allRefs (File dir, String pref, java.util.Set res) throws IOException {
        String[] arr = dir.list ();
        for (int i = 0; i < arr.length; i++) {
            File f = new File (dir, arr[i]);
            if (f.isDirectory()) {
                allRefs (f, pref + arr[i] + '/', res);
            } else {
                if (arr[i].endsWith (".html")) {
                    res.add (pref + arr[i]);
                }
            }
        }
    }
}
