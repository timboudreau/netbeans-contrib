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
}
