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
            generateIndex (f1, f2, new File (args[2]), new File (args[3]));
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
    
    
    private static void generateIndex (File dir1, File dir2, File index, File out) throws IOException {
        Set files1 = new TreeSet ();
        allRefs (dir1, "", files1);
        
        Set files2 = new TreeSet ();
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
        
        
        FileReader indexReader = new FileReader (index);
        ParseURLs.Section[] sections = ParseURLs.sections (indexReader, index.toURL());
        indexReader.close ();

        System.out.println("Writing colorized index changes.html");
        
        FileWriter w = new FileWriter (new File (out, "changes.html"));
        indexReader = new FileReader (index);
        int last = 0;
        for (int i = 0; i < sections.length; i++) {
            
            int copy = sections[i].getStart() - last;
            last = sections[i].getStart ();
            char[] arr = new char[copy];
            indexReader.read (arr);
            w.write (arr);
            w.write ("</div>");
            

            if (sections[i].getName () == null) {
                continue;
            }
            
            int ch = sections[i].getChanged (index.getParentFile().toURL(), res);
            
            if (ch >= 75) {
                w.write ("<div style=\"background: #7f7f7f\">");
                continue;
            }
            if (ch >= 50) {
                w.write ("<div style=\"background: #afafaf\">");
                continue;
            }
            if (ch >= 25) {
                w.write ("<div style=\"background: #cfcfcf\">");
                continue;
            }
            if (ch >= 2) {
                w.write ("<div style=\"background: #efefef\">");
                continue;
            }
        }

        // rest
        for (;;) {
            int c = indexReader.read ();
            if (c == -1) break;
            w.write (c);
        }
        w.write ('\n');

        w.close ();
        
        
        System.out.println("Writing index file index.html");
        writeIndex (new File (out, "index.html"), out, dir1, dir2, res);

    }
    
    
    private static void writeIndex (File index, File out, File dir1, File dir2, ContentDiff res) throws IOException {
        FileWriter w = new FileWriter (index);

        w.write ("<html><body>");
        w.write ("<center><h1>Index of Changes</h1></center>");

        final ContentDiff.Cluster[] arr = res.getClusters();
        
        /** Cluster */
        HashSet exclude = new HashSet ();

        w.write ("<UL>\n");
        for (int i = 0; i < arr.length; i++) {
            if (exclude.contains (arr[i])) {
                continue;
            }
            
                
            String name = null;
            { // name of the cluster
                Iterator it = arr[i].getPages().iterator ();
                LOOP: while (it.hasNext ()) {
                    String n = ((ContentDiff.Page)it.next ()).getFileName();
                    if (name == null) {
                        name = n;
                    } else {
                        int min = Math.min (name.length(), n.length());
                        for (int indx = 0; indx < min; indx++) {
                            if (name.charAt (indx) != n.charAt (indx)) {
                                name = n.substring (0, indx);
                                continue LOOP;
                            }
                        }
                        name = name.substring (0, min);
                    }
                }
            }
            
            w.write ("  <LI><b>" + name + "</b> (" + arr[i].getChanged() + "% difference)\n");

            w.write ("  <UL>\n");
            writeOutPages (w, out, dir1, dir2, arr[i], exclude);
            w.write ("  </UL>\n");
        }

        w.write ("</UL>\n");
        w.write ("</body></html>");
        
        w.close ();
    }
    
    private static void writeOutPages (Writer w, File out, File dir1, File dir2, ContentDiff.Cluster c, Set exclude) 
    throws IOException {
        if (!exclude.add (c)) {
            return;
        }
            
        
        boolean change = false;
        Iterator it = c.getPages ().iterator();
        while (it.hasNext()) {
            ContentDiff.Page p = (ContentDiff.Page)it.next ();
            if (p.isAdded()) {
                w.write ("  <LI><a href=\"" + new URL (dir2.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> added\n");
                w.write ('\n');
                change = true;
                continue;
            }
            if (p.isRemoved()) {
                w.write ("  <LI><a href=\"" + new URL (dir1.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> removed\n");
                w.write ('\n');
                change = true;
                continue;
            }
            System.out.println("page: " + p.getFileName() + " change: " + p.getChanged ());
            if (p.getChanged() > 0) {
                w.write (
                    "  <LI><a href=\"" + new URL (out.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> " +
                    " (changed " + p.getChanged () +
                    " %) <a href=\"" + new URL (dir1.toURL (), p.getFileName ()) + "\">old</a> " +
                    "   <a href=\"" + new URL (dir2.toURL (), p.getFileName ()) + "\">new</a> "
                );
                w.write ('\n');
                change = true;
                continue;
            }
        }
        
        writeOutPages (w, out, dir1, dir2, c, exclude);
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
