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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.latex.editor.spellchecker;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.text.Document;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.modules.spellchecker.spi.language.TokenListProvider;

/**
 *
 * @author Jan Lahoda
 */
public class LaTeXTokenListProvider implements TokenListProvider {

    private static Map<Document, Reference<LaTeXTokenList>> doc2TokenList = new WeakHashMap<Document, Reference<LaTeXTokenList>>();
    
    public LaTeXTokenListProvider() {
    }

    public TokenList findTokenList(Document document) {
        return findTokenListImpl(document);
    }
    
    static LaTeXTokenList findTokenListImpl(Document document) {
        Reference<LaTeXTokenList> r = doc2TokenList.get(document);
        LaTeXTokenList result = r != null ? r.get() : null;
        
        if (result == null) {
            doc2TokenList.put(document, new WeakReference<LaTeXTokenList>(result = new LaTeXTokenList(document)));
        }
        
        return result;
    }

}
