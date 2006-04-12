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
package org.netbeans.modules.spellchecker.bindings.java;

import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;

/**
 *
 * @author Jan Lahoda
 */
public class JavaTokenListProvider implements TokenListProvider {
    
    /** Creates a new instance of JavaTokenListProvider */
    public JavaTokenListProvider() {
    }

    public TokenList findTokenList(Document doc) {
        if ("text/x-java".equals(doc.getProperty("mimeType")) && doc instanceof BaseDocument)
            return new JavaTokenList((BaseDocument) doc);

        return null;
    }
    
}
