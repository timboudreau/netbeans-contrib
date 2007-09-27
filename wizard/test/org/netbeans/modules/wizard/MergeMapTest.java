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
/*
 * MergeMapTest.java
 * JUnit based test
 *
 * Created on March 4, 2005, 1:26 PM
 */

package org.netbeans.modules.wizard;

import java.util.Arrays;
import junit.framework.*;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author tim
 */
public class MergeMapTest extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite(MergeMapTest.class);

        return suite;
    }

    MergeMap map = null;

    public MergeMapTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        map = new MergeMap ("a");
    }

    /**
     * Test of isEmpty method, of class org.netbeans.modules.wizard.MergeMap.
     */
    public void testFunctionality() {
        System.out.println("testFunctionality");
        assertTrue (map.isEmpty());
        map.put ("key", "val");
        assertFalse (map.isEmpty());
        assertEquals ("val", map.remove ("key"));
        assertNull (map.get("key"));
        assertTrue (map.isEmpty());
        map.put ("key", "val");
        assertFalse (map.isEmpty());
        
        map.push("next");
        assertFalse (map.isEmpty());
        assertEquals ("val", map.get("key"));
        map.put ("key1", "val1");
        assertFalse (map.isEmpty());
        assertEquals (2, map.size());
        assertEquals("next", map.popAndCalve());
        assertEquals (1, map.size());
        assertNull (map.get("key1"));
        
        map.push ("next");
        assertEquals (2, map.size());
        assertEquals ("val1", map.get("key1"));
        assertFalse (map.isEmpty());
        
        map.push ("farther");
        assertEquals (2, map.size());
        assertEquals ("val", map.get("key"));
        assertEquals ("val1", map.get("key1"));
        assertNull (map.get("key2"));
        assertFalse (map.isEmpty());
        
        map.put ("key2", "val2");
        assertEquals (3, map.size());
        assertEquals ("farther", map.popAndCalve());
        assertEquals (2, map.size());
        assertEquals ("next", map.popAndCalve());
        assertEquals (1, map.size());
        
        Set s = map.keySet();
        HashSet hs = new HashSet (Arrays.asList (new String[] { "key" }));
        assertEquals (s, hs);
        
        map.push ("farther");
        assertEquals (2, map.size());
        assertEquals ("val2", map.get("key2"));
        assertNull (map.get("key1"));
        map.push ("next");
        assertEquals ("val1", map.get("key1"));
        assertEquals (3, map.size());
        
        hs = new HashSet (Arrays.asList (new String[] { "key", "key1", "key2" }));
        assertEquals (map.keySet(), hs);
        
        map.put ("key", "other");
        assertEquals (3, map.size());
        assertEquals ("other", map.get("key"));
        assertEquals ("other", map.remove ("key"));
        assertNull (map.get("key"));
        assertEquals (2, map.size());
        
        try {
           map.clear(); 
           fail ("Exception should have been thrown");
        } catch (UnsupportedOperationException uoe2) {
            //do nothing
        } catch (RuntimeException e) {
            fail ("Wrong exception " + e);
        }
        
        
    }
}
