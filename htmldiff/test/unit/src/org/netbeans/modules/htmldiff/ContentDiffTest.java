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
import junit.framework.*;

import org.netbeans.junit.*;



/** Test diff for set of pages.
 *
 * @author Jaroslav Tulach
 */
public final class ContentDiffTest extends NbTestCase {

    public ContentDiffTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(ContentDiffTest.class);
        
        return suite;
    }
    
    private ContentDiff diff (final String[] oldPages, final String[] newPages) throws IOException {
        final java.net.URL o = new java.net.URL ("file:/old/");
        final java.net.URL n = new java.net.URL ("file:/new/");
        
        class S implements ContentDiff.Source {
            private Reader page (String[] arr, String page) throws IOException {
                for (int i = 0; i < arr.length; i += 2) {
                    if (arr[i].equals (page)) {
                        return new StringReader (arr[i + 1]);
                    }
                }
                throw new IOException ("Page not found: " + page);
            }
            
            public java.io.Reader getReader(java.net.URL url) throws IOException {
                if (url.toExternalForm().startsWith (o.toExternalForm())) {
                    return page (oldPages, url.toExternalForm ().substring (o.toExternalForm ().length()));
                }
                if (url.toExternalForm().startsWith (n.toExternalForm())) {
                    return page (newPages, url.toExternalForm ().substring (n.toExternalForm ().length()));
                }
                
                throw new IOException ("Wrong url: " + url);
            }    
        }
        
        
        
        return ContentDiff.diff (
            o, even (oldPages),
            n, even (newPages),
            new S ()
        );
    }
    
    private java.util.Set even (String[] arr) {
        java.util.HashSet r = new java.util.HashSet ();
        for (int i = 0; i < arr.length; i += 2) {
            r.add (arr[i]);
        }
        return r;
    }
    
    public void testSimplePage () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple page"
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains ("index.html"));
    }
    
    public void testSimplePageWithURL () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains ("index.html"));
    }
    
    public void testSimplePageWithExternalURL () throws Exception {
        String[] pages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"http://www.netbeans.org\">page</a>."
        };
        
        ContentDiff diff = diff (pages, pages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("One page", 1, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains ("index.html"));
    }

    public void testTwoPages () throws Exception {
        String[] oldPages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        String[] newPages = {
            "new.html", "<h1>Hi</h1> This is a simple <a href=\"new.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("Two independent clusters", 2, diff.getClusters().length);
        assertEquals ("One page in first", 1, diff.getClusters()[0].getPages ().size ());
        assertEquals ("One page in second", 1, diff.getClusters()[1].getPages ().size ());
        
        assertEquals ("No refs outside", 0, diff.getClusters()[0].getReferences ().length);
        assertEquals ("No refs outside", 0, diff.getClusters()[1].getReferences ().length);
    }
    
    public void testTwoPagesInNewVersionReferingToEachOther () throws Exception {
        String[] oldPages = {
            "index.html", "<h1>Hi</h1> This is a simple <a href=\"index.html\">page</a>."
        };
        String[] newPages = {
            "index.html", "<h1>Hi</h1> This is new simple <a href=\"new.html\">page</a>.",
            "new.html", "<h1>Hi</h1> This is a refence to new <a href=\"index.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 1, diff.getClusters().length);
        assertEquals ("Both pages there", 2, diff.getClusters()[0].getPages ().size ());
        assertTrue ("Cluster index.html", diff.getClusters()[0].getPages ().contains ("index.html"));
        assertTrue ("Cluster new.html", diff.getClusters()[0].getPages ().contains ("new.html"));
    }
    
    public void testOnePageRefersToAnother () throws Exception {
        String[] oldPages = {
        };
        String[] newPages = {
            "index.html", "<h1>Hi</h1> This is new simple <a href=\"new.html\">page</a>.",
            "new.html", "<h1>Hi</h1> This is a refence to new <a href=\"new.html\">page</a>."
        };
        
        ContentDiff diff = diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("Two clusters", 2, diff.getClusters().length);
        assertTrue ("First contains index.html as it is the `root`", diff.getClusters()[0].getPages ().contains ("index.html"));
        assertTrue ("Second contains new.html", diff.getClusters()[1].getPages ().contains ("new.html"));
        
        assertEquals ("There is a dep from first cluster to the other",
            diff.getClusters()[1], 
            diff.getClusters()[0].getReferences()[0]
        );
        
        assertEquals ("Second cluster has no deps", 0, diff.getClusters()[1].getReferences().length);
    }
}
