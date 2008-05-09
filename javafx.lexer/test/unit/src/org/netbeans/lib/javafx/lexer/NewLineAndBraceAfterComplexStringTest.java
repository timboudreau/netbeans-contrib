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

package org.netbeans.lib.javafx.lexer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.api.javafx.lexer.JFXTokenId;

/**
 *
 * @author Victor G. Vasilyev
 */
public class NewLineAndBraceAfterComplexStringTest  extends LexerTestBase {

    public NewLineAndBraceAfterComplexStringTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
    }

    @After 
    @Override
    public void tearDown() {
        super.tearDown();
    }
    

    @Test
    public void testNewLineAndBraceAfterComplexString() throws Exception {
        setSource("\"{a}{b}\"\n}");
        assertNextTokenIs(JFXTokenId.QUOTE_LBRACE_STRING_LITERAL, "\"{", 0);
        assertNextTokenIs(JFXTokenId.IDENTIFIER, "a", 2);
        assertNextTokenIs(JFXTokenId.RBRACE_LBRACE_STRING_LITERAL, "}{", 3);
        assertNextTokenIs(JFXTokenId.IDENTIFIER, "b", 5);
        assertNextTokenIs(JFXTokenId.RBRACE_QUOTE_STRING_LITERAL, "}\"", 6);
        assertNextTokenIs(JFXTokenId.WS, "\n", 8);
        assertNextTokenIs(JFXTokenId.RBRACE, "}", 9);
    }
}
