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
import javax.xml.parsers.*;
import org.w3c.dom.*;

/** Generates HTML page with differences from two readers.
 *
 * @author  Jaroslav Tulach
 */
public final class DiffPages extends Object {
    /** Generates the diff from two provided pages.
     */
    public static void main (String[] args) throws Exception {
        
        File f1 = new File (args[0]); 
        
        if (args.length == 1) {
            updateLinks (f1, null, null, null, null);
            return;
        }
        
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
        
        /*
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
        */
        
        System.out.println("Writing index file index.html");
        updateLinks (index, out, dir1, dir2, res);

    }
    
    private static void updateLinks (File index, File out, File dir1, File dir2, ContentDiff res) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new NullER ());
            Document doc = builder.parse(index);
            
            Element newList = doc.createElement("ul");

            NodeList list = doc.getElementsByTagName("a");
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item (i);

                Node href = n.getAttributes().getNamedItem("href");
                if (href != null) {
                    String s = href.getNodeValue();

                    //String s = arr[i].toExternalForm();
                    int hash = s.indexOf ('#');
                    if (hash != -1) {
                        s = s.substring (0, hash);
                    }
                    /*
                    if (s.startsWith (b)) {
                        s = s.substring (b.length());
                    }
                     */
                    ContentDiff.Page page = res.findPage (s);
                    if (page != null) {
                        ContentDiff.Cluster cluster = res.findCluster (page);
                        Element ref;
                        
                        if (cluster.getChanged () == 100) {
                            ref = doc.createElement("span");
                            ref.setAttribute("style", "background: #FFFF00");
                            ref.appendChild (doc.createTextNode(" (New!)"));
                        } else {
                            ref = doc.createElement("em");
                            ref.appendChild (doc.createTextNode(" (" + cluster.getChanged() + "% change)"));
                        }
                        n.appendChild (ref);
                        
                        File pages = new File (out, s.substring (0, s.indexOf ('/')) + "/list.html");
                        
                        writeIndex (cluster, pages, out, out, out, res);
                    }
                }
            }
            
            
        
            FileWriter w = new FileWriter (new File (out, "index.html"));
            w.write (doc.getDocumentElement().toString());
            w.close ();
            
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            IOException e = new IOException (ex.getMessage());
            org.openide.ErrorManager.getDefault ().annotate (e, ex);
            throw e;
        } catch (org.xml.sax.SAXException ex) {
            ex.printStackTrace();
            IOException e = new IOException (ex.getMessage());
            org.openide.ErrorManager.getDefault ().annotate (e, ex);
            throw e;
        }
    }
    
    
    private static void writeIndex (ContentDiff.Cluster cluster, File index, File out, File dir1, File dir2, ContentDiff res) throws IOException {
        FileWriter w = new FileWriter (index);

        w.write ("<html><body>");
        w.write ("<center><h1>Index of " + cluster.getChanged () + "% Differences</h1></center>");

        /** Cluster */
        HashSet exclude = new HashSet ();

        w.write ("  <UL>\n");
        writeOutPages (w, out, dir1, dir2, cluster, exclude);
        w.write ("  </UL>\n");

        w.write ("</body></html>");
        
        w.close ();
    }
    
    private static void writeOutPages (Writer w, File out, File dir1, File dir2, ContentDiff.Cluster c, Set exclude) 
    throws IOException {
        if (!exclude.add (c)) {
            return;
        }
            
        class Cmp implements Comparator {
            public int compare (Object o1, Object o2) {
                ContentDiff.Page p1 = (ContentDiff.Page)o1;
                ContentDiff.Page p2 = (ContentDiff.Page)o2;
                
                if (p1.getChanged() == p2.getChanged ()) {
                    return p1.compareTo(p2);
                } else {
                    return p2.getChanged () - p1.getChanged ();
                }
            }
        }
        
        boolean change = false;
        TreeSet ts = new TreeSet (new Cmp ());
        ts.addAll (c.getPages ());
        Iterator it = ts.iterator();
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
            if (p.getChanged() > 0) {
                w.write (
                    "  <LI><a href=\"" + new URL (out.toURL (), p.getFileName()) + "\">" + p.getFileName () + "</a> " +
                    " (changed " + p.getChanged () +
                    " %)");
                if (dir1 != null && dir2 != null && !dir1.equals (dir2)) {
                    w.write ("<a href=\"" + new URL (dir1.toURL (), p.getFileName ()) + "\">old</a> " +
                        "   <a href=\"" + new URL (dir2.toURL (), p.getFileName ()) + "\">new</a> "
                    );
                }
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
    
    /** No entity resolver.
     */
    private static final class NullER implements org.xml.sax.EntityResolver {
        
        public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, IOException {
            return new org.xml.sax.InputSource (new StringReader (""));
        }
        
    }
}

