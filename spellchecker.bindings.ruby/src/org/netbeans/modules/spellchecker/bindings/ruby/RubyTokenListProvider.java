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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.spellchecker.bindings.ruby;

import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.ruby.RubyMimeResolver;
import org.netbeans.modules.ruby.RubyUtils;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;

/**
 *
 * @author Jan Lahoda
 */
public class RubyTokenListProvider implements TokenListProvider {
    /** Creates a new instance of RubyTokenListProvider */
    public RubyTokenListProvider() {
    }

    public TokenList findTokenList(Document doc) {
        if (!(doc instanceof BaseDocument)) {
            return null;
        }
        BaseDocument bdoc = (BaseDocument)doc;
        if (RubyUtils.isRhtmlDocument(doc)) {
            return new RhtmlTokenList(bdoc);
        }
        String mimeType = (String)doc.getProperty("mimeType");
        if (RubyMimeResolver.RUBY_MIME_TYPE.equals(mimeType)) {
            return new RubyTokenList(bdoc);
        }

        return null;
    }
}