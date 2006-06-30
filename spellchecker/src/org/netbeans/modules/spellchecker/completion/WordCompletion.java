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
package org.netbeans.modules.spellchecker.completion;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;
import org.netbeans.modules.spellchecker.ComponentPeer;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

/**
 *
 * @author Jan Lahoda
 */
public class WordCompletion implements CompletionProvider {
    
    /** Creates a new instance of WordCompletion */
    public WordCompletion() {
    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == COMPLETION_QUERY_TYPE) {
            return new AsyncCompletionTask(new Query(), component);
        }
        
        return null;
    }
    
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }
    
    private static class Query extends AsyncCompletionQuery {
        
        protected void query(CompletionResultSet resultSet, Document doc, final int caretOffset) {
            Dictionary d = ComponentPeer.getDictionary(doc);
            final TokenList  l = ComponentPeer.ACCESSOR.lookupTokenList(doc);
            
            if (d != null && l != null && doc instanceof BaseDocument) {
                final BaseDocument bdoc = (BaseDocument) doc;
                final String[] prefix = new String[1];
                
                doc.render(new Runnable() {
                    public void run() {
                        try {
                            int lineStart = Utilities.getRowStart(bdoc, caretOffset);
                            
                            l.setStartOffset(lineStart);
                            
                            while (l.nextWord()) {
                                int start = l.getCurrentWordStartOffset();
                                int end   = l.getCurrentWordStartOffset() + l.getCurrentWordText().length();
                                
                                if (start < caretOffset && end >= caretOffset) {
                                    prefix[0] = l.getCurrentWordText().subSequence(0, caretOffset - start).toString();
                                    return ;
                                }
                            }
                        } catch (BadLocationException e) {
                            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                        }
                    }
                });
                
                if (prefix[0] != null) {
                    for (String proposal : d.findValidWordsForPrefix(prefix[0])) {
                        resultSet.addItem(new WordCompletionItem(caretOffset - prefix[0].length(), proposal));
                    }
                }
            }
            
            resultSet.finish();
        }
    }
    
}
