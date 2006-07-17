/* The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
package org.netbeans.modules.genericnavigator;

import junit.framework.*;
import java.io.File;

/**
 *
 * @author Tim Boudreau
 */
public class NavigationItemTest extends TestCase {
    public NavigationItemTest(String testName) { super(testName); }
    public static Test suite() {
        TestSuite suite = new TestSuite(NavigationItemTest.class);
        return suite;
    }

    public void testToString() {
        NavigationItem item = cni ("Hello world");
        assertEquals (item.txt, item.toString());
        item = cni ("This <b>is some <i>html</i> isn't </b> it?");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt +
                "'","This is some html isn't it?", item.toString());
        item = cni ("This text has a < character in it");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This is <<< very pathological <<< text but it should work");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This text is <<<< also pathological but it should work>");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
        item = cni ("This text is <<<< also even more >> pathological but it " +
                "should work>");
        assertEquals ("Got '" + item.toString() + "' for '" + item.txt + "'",
                item.txt, item.toString());
    }

    private NavigationItem cni (String s) {
        NavigationItem item = new NavigationItem (null, -1, -1, s, true);
        return item;
    }
}
