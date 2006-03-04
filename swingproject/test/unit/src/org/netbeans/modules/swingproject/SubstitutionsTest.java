/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.swingproject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import junit.framework.*;

/**
 *
 * @author Tim Boudreau
 */
public class SubstitutionsTest extends TestCase {
    
    public SubstitutionsTest(String testName) {
        super(testName);
    }

    Substitutions substs = null;
    protected void setUp() throws Exception {
        InputStream stream = SubstitutionsTest.class.getResourceAsStream("SubstitutionTestData.properties");
        System.err.println("Got stream " + stream);
        substs = new Substitutions (stream,
                "MyProject", "com.foo.me");
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SubstitutionsTest.class);
        
        return suite;
    }

    public void testPackageToPath() {
        System.out.println("testPackageToPath");
        String path = substs.basePath;
        assertEquals ("com/foo/me", path);
    }

    public void testSubstitutePath() {
        System.out.println("testSubstitutePath");
        assertEquals ("src/com/foo/me/Main.java", substs.substitutePath ("src/swingtemplate/Main.java"));
        assertEquals ("src/com/foo/me/MainPanel.java", substs.substitutePath ("src/swingtemplate/MainPanel.java"));
        assertEquals ("src/resources/defaults.properties", substs.substitutePath ("src/resources/defaults.properties"));
        assertEquals ("foo", substs.substitutePath ("foo"));
    }

    public void testSubstituteContent() throws Exception {
        System.out.println("testSubstituteContent");
        String s = "/** foo */ \npackage swingtemplate; \n public class Foo {\n public static " +
                "void main (String[] args) {\n    System.out.println(\"APP_NAME\");\n  }\n}\n";

        String expected = "/** foo */ \npackage com.foo.me; \n public class Foo {\n public static " +
                "void main (String[] args) {\n    System.out.println(\"MyProject\");\n  }\n}\n";

        ByteArrayInputStream in = new ByteArrayInputStream (s.getBytes());
        ByteArrayInputStream nue = (ByteArrayInputStream)
            substs.substituteContent (s.length() * 2, in, "Main.java");

        byte[] b = new byte[s.length() * 3];
        int len = nue.read(b);

        String actual = new String (b, 0, len);
        assertEquals (expected, actual);
    }
}
