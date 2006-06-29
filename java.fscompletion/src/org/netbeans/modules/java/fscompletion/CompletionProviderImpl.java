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
package org.netbeans.modules.java.fscompletion;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.SyntaxSupport;
import org.netbeans.editor.TokenItem;
import org.netbeans.editor.ext.java.JavaTokenContext;
import org.netbeans.modules.editor.fscompletion.spi.support.FSCompletion;
import org.netbeans.modules.editor.java.NbJavaJMISyntaxSupport;
import org.netbeans.modules.javacore.api.JavaModel;
import org.netbeans.modules.javacore.internalapi.JavaMetamodel;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

/**
 *
 * @author Jan Lahoda
 */
public class CompletionProviderImpl implements CompletionProvider {
    
    /** Creates a new instance of CompletionProviderImpl */
    public CompletionProviderImpl() {
    }

    public CompletionTask createTask(int queryType, JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
                FileObject file = od.getPrimaryFile();
                
                JavaModel.getJavaRepository().beginTrans(false);
                
                try {
                    if (doc instanceof BaseDocument) {
                        SyntaxSupport sup = ((BaseDocument) doc).getSyntaxSupport();
                        NbJavaJMISyntaxSupport nbJavaSup = (NbJavaJMISyntaxSupport)sup.get(NbJavaJMISyntaxSupport.class);
                        
                        TokenItem item = nbJavaSup.getTokenChain(caretOffset, caretOffset);
                        
                        if (item != null && item.getTokenID() == JavaTokenContext.STRING_LITERAL) {
                            int start = item.getOffset() + 1;
                            
                            JavaModel.setClassPath(file);
                            
                            ClassPath cp = JavaMetamodel.getManager().getClassPath();
                            FileObject[] roots = cp.getRoots();
                            
                            String text = doc.getText(start, caretOffset - start);
                            
                            resultSet.addAllItems(FSCompletion.completion(roots, roots, text, caretOffset - text.length()));
                            
                            resultSet.setTitle(text);
                        }
                    }
                } catch (IOException e) {
                    ErrorManager.getDefault().notify(e);
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(e);
                } finally {
                    JavaModel.getJavaRepository().endTrans();
                    resultSet.finish();
                }
            }
        }, component);
    }

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }

}
