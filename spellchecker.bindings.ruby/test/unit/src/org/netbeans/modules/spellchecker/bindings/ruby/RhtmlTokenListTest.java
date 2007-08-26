/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.spellchecker.bindings.ruby;

import org.netbeans.modules.ruby.rhtml.lexer.api.RhtmlTokenId;

/**
 *
 * @author Tor Norbye
 */
public class RhtmlTokenListTest extends TokenListTestBase {
    public RhtmlTokenListTest(String testName) {
        super(testName, RhtmlTokenId.language(), "application/x-httpd-eruby");
    }

    public void testHtml() throws Exception {
        tokenListTest("this is html text\n", "this", "is", "html", "text");
    }

    public void testRuby() throws Exception {
        tokenListTest("<% # this is html text %>\n", "this", "is", "html", "text");
    }

    public void testComposite() throws Exception {
        tokenListTest("foo <% bar %> hello <% # hellor %>", "foo", "hello", "hellor");
    }

    public void testComposite2() throws Exception {
        tokenListTest("foo <% # %> hello", "foo", "hello");
    }
}