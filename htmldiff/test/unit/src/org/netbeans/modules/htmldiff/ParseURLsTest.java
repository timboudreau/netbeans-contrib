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
import junit.framework.*;

import org.netbeans.junit.*;



/** Does parsing of HTML page for URLs works?
 *
 * @author Jaroslav Tulach
 */
public final class ParseURLsTest extends NbTestCase {

    public ParseURLsTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(ParseURLsTest.class);
        
        return suite;
    }
    public void testNoURLsThere () throws Exception {
        String vzor = "Is a <pre>   <b>spaces</b>     between</PRE> there?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:///home/test");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("Nothing", 0, res.length);
    }
    
    public void testGlobalURLThere () throws Exception {
        String vzor = "Is a <pre>   <a href=\"http://www.netbeans.org\">spaces</a>     between</PRE> there?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:///home/test");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("One url", 1, res.length);
        assertEquals ("The right", "http://www.netbeans.org", res[0].toExternalForm());
    }
    
    public void testRelativeURLThere () throws Exception {
        String vzor = "Is a <pre>   <a href=\"other.html\">spaces</a>     between</PRE> there?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("One url", 1, res.length);
        assertEquals ("The right", "file:/home/other.html", res[0].toExternalForm());
    }
    
    public void testURLInComment() throws Exception {
        String vzor = "Is a <!--   <a href=\"other.html\">spaces</a>     between --> there?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("Nothingk because is in comment", 0, res.length);
    }
    
    
    public void testNewLineInMiddle () throws Exception {
        String vzor = "Ahoj <a\n  href=\n\"other.html\">spaces</a>?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("One url", 1, res.length);
        assertEquals ("The right", "file:/home/other.html", res[0].toExternalForm());
    }
    
    public void testTwoURLsInTheSameFile () throws Exception {
        String vzor = "Ahoj <a href=\"other.html\">spaces</a> and another <a href=\"other.html\">new spaces</a>?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("Two url", 2, res.length);
        assertEquals ("The right", "file:/home/other.html", res[0].toExternalForm());
        assertEquals ("Both are the same", res[0], res[1]);
    }
    
    public void testBrokenURL () throws Exception {
        String vzor = "Ahoj <a href=\"other.html\">spaces</a> <a href=\"unknownprotocol://ble\">Ble</a>?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        URL[] res = ParseURLs.parse (r1, base);
        
        assertNotNull (res);
        assertEquals ("One url", 1, res.length);
        assertEquals ("The right", "file:/home/other.html", res[0].toExternalForm());
    }


    public void testParsingWithOneSection () throws Exception {
        String vzor = "Ahoj <a href=\"other.html\">spaces</a> <a href=\"another.html\">Ble</a>?";
        
        Reader r1 = new StringReader (vzor);
        
        URL base = new URL ("file:/home/test.html");
        
        ParseURLs.Section[] res = ParseURLs.sections (r1, base);
        
        assertNotNull (res);
        assertEquals ("One section", 1, res.length);
        assertEquals ("No name", null, res[0].getName());
        assertEquals ("Starts at 0", 0, res[0].getStart());
        
        URL[] urls = res[0].getURLs();
        assertEquals ("Two there", 2, urls.length);
        assertTrue ("other.html", urls[0].toExternalForm().endsWith("other.html"));
        assertTrue ("another.html", urls[1].toExternalForm().endsWith("another.html"));
    }
    
    public void testParseMoreSections () throws Exception {
        String bef = "Ahoj <a href=\"#hi\">spaces</a> ";
        String sec = "<a name=\"ahoj\">section</a>";
        String mid = "<a href=\"another.html\">A URLs</a>?";
        String nex = "<a name=\"hi\">hello</a>";
        String las = "<a href=\"last.html\">Last URL</a>.";
        
        String all = bef + sec + mid + nex + las;
        Reader r1 = new StringReader (all);
        
        URL base = new URL ("file:/home/test.html");
        
        ParseURLs.Section[] res = ParseURLs.sections (r1, base);
        
        assertNotNull (res);
        assertEquals ("Three section", 3, res.length);
        assertEquals ("No name", null, res[0].getName());
        assertEquals ("ahoj", res[1].getName());
        assertEquals ("hi", res[2].getName());
        assertEquals ("Starts at 0", 0, res[0].getStart());
        
        assertEquals ("Starts after bef", bef.length(), res[1].getStart());
        String after1 = all.substring (res[1].getStart ());
        if (!after1.startsWith (sec)) {
            fail ("Should start with " + sec + " but is " + after1);
        }
        
        String after2 = all.substring (res[2].getStart ());
        if (!after2.startsWith (nex)) {
            fail ("Should start with " + nex + " but is " + after2);
        }
        
        assertEquals ("Nothing there", 1, res[0].getURLs().length);
        assertEquals ("One", 1, res[1].getURLs ().length);
        assertEquals ("One too", 1, res[2].getURLs ().length);
    }
    
    public void testResolveChangedInContextDiff () throws Exception {
        String[] oldPages = {
            "base.html", "<h1>Hi</h1> This is a simple <a href=\"base.html#in-my-middle\">page</a>.",
            "s.html", "<h1>Hi</h1> This is a simple <a href=\"base.html#in-my-middle\">page</a>."
        };
        String[] newPages = {
            "s.html", "simple page"
        };
        
        ContentDiff diff = ContentDiffTest.diff (oldPages, newPages);
        
        assertNotNull (diff);
        assertEquals ("One cluster", 2, diff.getClusters().length);
        assertEquals ("Remove means big change", 100, diff.getClusters()[1].getChanged ());
        assertTrue ("Some change for second cluster", 0 < diff.getClusters()[0].getChanged ());
        assertTrue ("but not whole", diff.getClusters()[0].getChanged () < 100);
        
        String sec = 
            "<a name=\"ahoj\">section</a>" +
            "<a href=\"base.html\">A URLs</a>?" +
            
            "<a name=\"2\">2nd section</a>" +
            "<a href=\"base.html\">A URLs</a>?" +
            "<a href=\"s.html\">s</a>?" +
            
            "<a name=\"3\">3rd section</a>" +
            "<a href=\"s.html\">s</a>?";
       
        
        URL base = new URL ("file:/home/");
        ParseURLs.Section[] res = ParseURLs.sections (new StringReader (sec), base);
        
        assertEquals ("4 sections", 4, res.length);
        assertEquals ("No urls in first section", 0, res[0].getChanged(base, diff));

        assertEquals ("ahoj is the name of second", "ahoj", res[1].getName());
        assertEquals ("One url in the second", 100, res[1].getChanged (base, diff));

        assertEquals ("2 is the name of second", "2", res[2].getName());
        assertEquals (
            "Changes of 1st and 2nd cluster", 
            (diff.getClusters()[0].getChanged () + diff.getClusters ()[1].getChanged ()) / 2, 
            res[2].getChanged (base, diff));

        assertEquals ("3 is the name of second", "3", res[3].getName());
        assertEquals (
            "Changes in 1st cluster", 
            diff.getClusters()[0].getChanged (), 
            res[3].getChanged (base, diff)
        );
    }
}
