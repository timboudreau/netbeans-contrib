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
