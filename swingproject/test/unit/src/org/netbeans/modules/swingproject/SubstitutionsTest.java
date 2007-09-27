/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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
