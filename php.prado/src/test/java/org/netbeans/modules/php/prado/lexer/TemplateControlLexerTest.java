/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.php.prado.lexer;

import junit.framework.TestCase;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Petr PIsl
 */
public class TemplateControlLexerTest extends TestCase {

    public TemplateControlLexerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSimple01() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText("Theme = \"Basic\"", TemplateControlTokenId.language());
        //LexerUtils.printTokenSequence(ts, "testSimple01"); ts.moveStart();
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Basic\"");
    }

    public void testSimple02() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText(" Theme = \"Basic\" ", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Basic\"");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
    }

    public void testUnfinishedProperty() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText(" Them", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Them");
    }

    public void testUnfinishedValue() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText("Theme =\"Ba", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Ba");
    }

    public void testMoreProperties() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText(" Theme = \"Basic\" Icon=\"moje.png\"", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Basic\"");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Icon");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"moje.png\"");
    }

    public void testSeparator01() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText("Theme = \"Basic\", Icon=\"moje.png\"", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Basic\"");
        LexerUtils.next(ts, TemplateControlTokenId.T_SEPARATOR, ",");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Icon");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"moje.png\"");
    }

    public void testSeparator02() throws Exception{
        TokenSequence<?> ts = LexerUtils.seqForText("Theme = \"Basic\",", TemplateControlTokenId.language());
        LexerUtils.next(ts, TemplateControlTokenId.T_PROPERTY, "Theme");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_EQUAL, "=");
        LexerUtils.next(ts, TemplateControlTokenId.T_WHITESPACE, " ");
        LexerUtils.next(ts, TemplateControlTokenId.T_VALUE, "\"Basic\"");
        LexerUtils.next(ts, TemplateControlTokenId.T_SEPARATOR, ",");
    }
}