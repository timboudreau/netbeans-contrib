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



/** Test for basic diff stuff.
 *
 * @author Jaroslav Tulach
 */
public final class DiffTest extends NbTestCase {

    public DiffTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(DiffTest.class);
        
        return suite;
    }
    
    public void testSpacesAreKept () throws Exception {
        String vzor = "Is a <b>space</b> there?";
        
        Reader r1 = new StringReader (vzor);
        Reader r2 = new StringReader (vzor);
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        
        assertDifferences ("No differnces", 0, res);
        
        StringBuffer b = new StringBuffer ();
        for (int i = 0; i < res.length; i++) {
            b.append (res[i].getNew());
        }
        
        String s = b.toString();
        
        assertEquals ("Still the same", vzor, s);
    }

    public void testNormalCharactersKept () throws Exception {
        String vzor = "Ahoj # ! ) ( * & ^ % $ ? # @ { } , . ; - = + Jardo";
        
        Reader r1 = new StringReader (vzor);
        Reader r2 = new StringReader (
                      "Ahoj # ! ) ( * & ^ % $ ? mily # @ { } , . ; - = + Jardo"
        );
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        
        assertDifferences ("One difference", 1, res);
        
        StringBuffer b = new StringBuffer ();
        for (int i = 0; i < res.length; i++) {
            b.append (res[i].getNew());
        }
        
        String s = b.toString();
        
        for (int i = 1; i < vzor.length() - 1; i++) {
            if (vzor.charAt (i) != ' ' && vzor.charAt (i - 1) == ' ' && vzor.charAt (i + 1) == ' ') {
                char ch = vzor.charAt (i);
                int index = s.indexOf (ch);
                if (index == -1) {
                    fail ("Character " + ch + " not found in " + s);
                }
            }
        }
    }
    
    public void testAdditionOfASentenceIntoMiddle () throws Exception {
        //
        // Right now changes in tags are ignored, but they need not be
        //
        
        
        Reader r1 = new StringReader (
"    <h1><i>Hello Jarda</i></h1>\n" +
"   well this was the<b>end</b>"
        );
        Reader r2 = new StringReader (
"    <h1><i>Hello Jarda</i></h1>\n" +
"   This is a new and updated page\n" +
"   well this was the<b>end</b>"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertDifferences ("One Addition", 1, res);
        
        assertDifferences ("Differences are like we want them to be", 
            new String[] { "<h1><i>Hello Jarda</i></h1>\n", "", "well this was the<b>end</b>" },
            new String[] { "<h1><i>Hello Jarda</i></h1>\n", "This is a new and updated page", "well this was the<b>end</b>" },
            res
        );
        
        r1.close ();
        r2.close ();
    }
    
    public void testAdditionOfASentence () throws Exception {
        //
        // Right now changes in tags are ignored, but they need not be
        //
        
        
        Reader r1 = new StringReader (
"    <h1><i>Hello Jarda</i></h1>\n"
        );
        Reader r2 = new StringReader (
"    <h1><i>Hello Jarda</i></h1>\n" +
"   This is a new and updated page\n"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertDifferences ("One Addition", 1, res);
        
        assertDifferences ("Differences are like we want them to be", 
            new String[] { "<h1><i>Hello Jarda</i></h1>\n", "", "\n" },
            new String[] { "<h1><i>Hello Jarda</i></h1>\n", "This is a new and updated page", "\n" },
            res
        );
        
        r1.close ();
        r2.close ();
    }
    
    public void testNoDifference () throws Exception {
        //
        // Right now changes in tags are ignored, but they need not be
        //
        
        String s = "<h1>Hello Jarda</h1>";
        Reader r1 = new StringReader (s);
        Reader r2 = new StringReader (s);
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertDifferences ("No difference", 0, res);
        assertEquals ("One text sequence", 1, res.length);
        
        r1.close ();
        r2.close ();
    }
    

    public void testChangeInOneWord () throws Exception {
        Reader r1 = new StringReader (
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"\n" +
"<HTML>\n" +
"  <HEAD>\n" +
"    <TITLE></TITLE>\n" +
"  </HEAD>\n" + 
"  <BODY>\n" +
"    <h1>Hello Jarda</h1>\n" +
"  </BODY>\n" +
"</HTML>\n"
        );
        Reader r2 = new StringReader (
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"\n" +
"<HTML>\n" +
"  <HEAD>\n" +
"    <TITLE></TITLE>\n" +
"  </HEAD>\n" + 
"  <BODY>\n" +
"    <h1>Hello Yarda</h1>\n" +
"  </BODY>\n" +
"</HTML>\n"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertEquals ("Three areas were found", 3, res.length);
        assertDifferences ("One difference", 1, res);
        
        assertDifferences ("Difference in Yarda/Jarda", 
            new String[] { null, "Jarda", null },
            new String[] { null, "Yarda", null },
            res
        );
        
        r1.close ();
        r2.close ();
    }
  
    public void testChangeInOneWordWithDifferentFormating () throws Exception {
        Reader r1 = new StringReader (
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"<HTML><HEAD>\n" +
"    <TITLE></TITLE>\n" +
"  </HEAD>\n" + 
"<BODY>\n" +
"    <h1>Hello Jarda</h1>\n" +
"</BODY>\n" +
"</HTML>\n"
        );
        Reader r2 = new StringReader (
"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
"\n" +
"<HTML>\n" +
"  <HEAD>\n" +
"    <TITLE></TITLE>\n" +
"  </HEAD>\n" + 
"  <BODY>\n" +
"    <h1>Hello Yarda</h1>\n" +
"  </BODY>\n" +
"</HTML>\n"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertEquals ("Three areas were found", 3, res.length);
        assertDifferences ("One difference", 1, res);
        
        assertDifferences ("Difference in Yarda/Jarda", 
            new String[] { null, "Jarda", null },
            new String[] { null, "Yarda", null },
            res
        );
        
        r1.close ();
        r2.close ();
    }
    
    public void testChangeOfATag () throws Exception {
        //
        // Right now changes in tags are ignored, but they need not be
        //
        
        
        Reader r1 = new StringReader (
"    <h1>Hello Jarda</h1>\n"
        );
        Reader r2 = new StringReader (
"    <h2>Hello Yarda</h2>\n"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertEquals ("Three areas were found", 3, res.length);
        assertDifferences ("One difference", 1, res);
        
        assertDifferences ("Difference in Yarda/Jarda", 
            new String[] { null, "Jarda", null },
            new String[] { null, "Yarda", null },
            res
        );
        
        r1.close ();
        r2.close ();
    }
    
    
    public void testAdditionOfATag () throws Exception {
        //
        // Right now changes in tags are ignored, but they need not be
        //
        
        
        Reader r1 = new StringReader (
"    <h1><i>Hello Jarda</i></h1>\n"
        );
        Reader r2 = new StringReader (
"    <h2>Hello Jarda</h2>\n"
        );
        
        
        HtmlDiff[] res = HtmlDiff.diff (r1, r2);
        assertNotNull ("Some result is there", res);
        
        assertDifferences ("No difference", 0, res);
        assertEquals ("One text sequence", 1, res.length);
        
        r1.close ();
        r2.close ();
    }

    
    private static void assertDifferences (String txt, int cnt, HtmlDiff[] res) {
        int was = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i].isDifference()) was++;
        }
        assertEquals (txt, cnt, was);
    }
    
    private static void assertDifferences (String txt, String[] old, String[] n, HtmlDiff[] res) {
        assertEquals (n.length, res.length);
        assertEquals (n.length, old.length);
        
        for (int i = 0; i < res.length; i++) {
            if (old[i] != null) {
                String ideal = old[i].replace ('\n', ' ').trim ();
                String real = res[i].getOld ().replace ('\n', ' ').trim ();
                assertEquals (i + "th index in old", ideal, real);
            }
            if (n[i] != null) {
                String ideal = n[i].replace ('\n', ' ').trim ();
                String real = res[i].getNew ().replace ('\n', ' ').trim ();
                assertEquals (i + "th index in new", ideal, real);
            }
        }
    }
    
}
