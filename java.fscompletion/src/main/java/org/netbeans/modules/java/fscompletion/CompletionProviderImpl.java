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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.fscompletion.spi.support.FSCompletion;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
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
            protected void query(CompletionResultSet resultSet, final Document doc, final int caretOffset) {
                try {
                DataObject od = (DataObject) doc.getProperty(Document.StreamDescriptionProperty);
                FileObject file = od.getPrimaryFile();
                
                final int[] offset = new int[1];
                final String[] string = new String[1];
                
                doc.render(new Runnable() {
                    public void run() {
                        TokenHierarchy th = TokenHierarchy.get(doc);
                        TokenSequence<JavaTokenId> ts = th.tokenSequence(JavaTokenId.language());
                        
                        if (ts != null) {
                            ts.move(caretOffset);
                            
                            if (ts.moveNext() && ts.token().id() == JavaTokenId.STRING_LITERAL) {
                                offset[0] = ts.offset();
                                string[0] = ts.token().text().toString();
                            }
                        }
                    }
                });
                
                if (string[0] != null) {
                    ClassPath boot = ClassPath.getClassPath(file, ClassPath.BOOT);
                    ClassPath exec = ClassPath.getClassPath(file, ClassPath.EXECUTE);
                    
                    if (boot != null && exec != null) {
                        ClassPath cp = ClassPathSupport.createProxyClassPath(boot, exec);
                        FileObject[] roots = cp.getRoots();

                        String text = string[0].substring(1, caretOffset - offset[0]);

                        resultSet.addAllItems(FSCompletion.completion(roots, roots, text, caretOffset - text.length()));
                    }
                }
                
                } catch (IOException e) {
                    Logger.getLogger(CompletionProviderImpl.class.getName()).log(Level.WARNING, null, e);
                } finally {
                    resultSet.finish();
                }
            }
        }, component);
    }

    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return 0;
    }

}
