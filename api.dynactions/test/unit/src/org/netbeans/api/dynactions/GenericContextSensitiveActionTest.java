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
package org.netbeans.api.dynactions;

import java.util.Collection;
import junit.framework.TestCase;

/**
 *
 * @author Tim Boudreau
 */
public class GenericContextSensitiveActionTest extends TestCase {
    
    public GenericContextSensitiveActionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testPutGetValue() throws Exception {
        System.out.println("testPutGetValue");
        A a = new A();
        for (char c = 'a'; c < 'e'; c++) {
            String s = new String (new char[] { c, (char) (c + 1), (char) (c + 2)});
            a.putValue (new String(new char[] { c }), s);
        }
        for (char c = 'a'; c < 'e'; c++) {
            String test = (String) a.getValue (new String(new char[] { c }));
            String s = new String (new char[] { c, (char) (c + 1), (char) (c + 2)});
            assertEquals (test, s);
        }
        for (char c = 'a'; c < 'e'; c++) {
            String test = (new String(new char[] { c }));
            if (c % 2 == 0) {
                a.putValue (test, null);
            }
        }
        for (char c = 'a'; c < 'e'; c++) {
            String test = (String) a.getValue (new String(new char[] { c }));
            String s = new String (new char[] { c, (char) (c + 1), (char) (c + 2)});
            assertEquals (test, c % 2 != 0 ? s : null);
        }
        for (char c = 'a'; c < 'e'; c++) {
            String test = (new String(new char[] { c }));
            if (c % 2 == 0) {
                a.putValue (test, "woo hoo");
            }
        }
        for (char c = 'a'; c < 'e'; c++) {
            String test = (String) a.getValue (new String(new char[] { c }));
            String s = new String (new char[] { c, (char) (c + 1), (char) (c + 2)});
            assertEquals (test, c % 2 != 0 ? s : "woo hoo");
        }
    }
    
    private static final class A extends GenericContextSensitiveAction<String> {
        
        public A() {
            super (String.class);
        }

        @Override
        protected void performAction(String t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
