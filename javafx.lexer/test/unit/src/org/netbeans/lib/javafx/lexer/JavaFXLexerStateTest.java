/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.netbeans.api.javafx.lexer.JavaFXTokenId;

/**
 *
 * @author Victor G. Vasilyev
 */
public class JavaFXLexerStateTest extends LexerStateTestBase {
    
    private static final String IDENTIFIER = "a";
    private static final String EMPTY_BLOCK = "{}";
    private static final String EMPTY_STRING_LITERAL = "\"\"";
    private static final String WHITESPACE = " ";
    private static final String EMPTY_QUOTE_LBRACE_STRING_LITERAL = "\"{";
    private static final String EMPTY_RBRACE_QUOTE_STRING_LITERAL = "}\"";
    private static final String SEMICOLON = ";";
    
    public JavaFXLexerStateTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void stateAfterInstantiation() {
        System.out.println("stateAfterInstantiation");
        Object expResult = null;
        Object result = instance.state();
        assertEquals(expResult, result);
    }

    @Test
    public void state1() {
        System.out.println("state1");
        instance.setSource("a");
        nextToken();
        assertEquals(token.id, JavaFXTokenId.IDENTIFIER);
        assertNull(instance.state());
    }

    @Test
    public void state2() {
        System.out.println("state2");
        instance.setSource("a{}\"\" \" {b}\"");
        nextToken();
        assertEquals(token.id, JavaFXTokenId.IDENTIFIER);
        assertNull(instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.LBRACE);
        assertNull("The LBRACEs should not be counted outside of the string literals.", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.RBRACE);
        assertNull("The LBRACEs should not be counted outside of the string literals.", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.STRING_LITERAL);
        assertNull("A state should not be saved after simple string literal", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.WHITESPACE);
        assertNull("A state should not be saved after WHITESPACE", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.QUOTE_LBRACE_STRING_LITERAL);
        assertStateHas(1, '\"', false);
        nextToken();
        assertEquals(token.id, JavaFXTokenId.IDENTIFIER);
        assertStateHas(1, '\"', false);
        nextToken();
        assertEquals(token.id, JavaFXTokenId.RBRACE_QUOTE_STRING_LITERAL);
        assertNull("A state should be null outside saved of the string literals.", instance.state());
    }
    
    @Test
    public void state3() {
        System.out.println("state3");
        instance.setSource(IDENTIFIER + EMPTY_BLOCK + 
                EMPTY_STRING_LITERAL + 
                WHITESPACE +
                EMPTY_QUOTE_LBRACE_STRING_LITERAL + IDENTIFIER + EMPTY_RBRACE_QUOTE_STRING_LITERAL +
                SEMICOLON);
        nextToken();
        assertEquals(token.id, JavaFXTokenId.IDENTIFIER);
        assertNull(instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.LBRACE);
        assertNull("The LBRACEs should not be counted outside of the string literals.", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.RBRACE);
        assertNull("The LBRACEs should not be counted outside of the string literals.", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.STRING_LITERAL);
        assertNull("A state should not be saved after simple string literal", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.WHITESPACE);
        assertNull("A state should not be saved after WHITESPACE", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.QUOTE_LBRACE_STRING_LITERAL);
        assertStateHas(1, '\"', false);
        nextToken();
        assertEquals(token.id, JavaFXTokenId.IDENTIFIER);
        assertStateHas(1, '\"', false);
        nextToken();
        assertEquals(token.id, JavaFXTokenId.RBRACE_QUOTE_STRING_LITERAL);
        assertNull("A state should be null at the last token of the string literals.", instance.state());
        nextToken();
        assertEquals(token.id, JavaFXTokenId.SEMICOLON);
        assertNull("A state should be null outside of the string literals.", instance.state());
    }
    
}