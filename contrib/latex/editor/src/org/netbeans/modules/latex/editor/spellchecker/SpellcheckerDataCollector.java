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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.latex.editor.spellchecker;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.lexer.Token;
import org.netbeans.napi.gsfret.source.CompilationInfo;
import org.netbeans.napi.gsfret.source.Phase;
import org.netbeans.napi.gsfret.source.Source.Priority;
import org.netbeans.napi.gsfret.source.support.EditorAwareSourceTaskFactory;
import org.netbeans.modules.latex.model.LaTeXParserResult;
import org.netbeans.modules.latex.model.command.ArgumentContainingNode;
import org.netbeans.modules.latex.model.command.ArgumentNode;
import org.netbeans.modules.latex.model.command.BlockNode;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.DefaultTraverseHandler;
import org.netbeans.modules.latex.model.command.MathNode;
import org.netbeans.modules.latex.model.command.TextNode;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.WeakSet;

/**
 *
 * @author Jan Lahoda
 */
public class SpellcheckerDataCollector implements CancellableTask<CompilationInfo> {

    private AtomicBoolean cancel = new AtomicBoolean();
    
    public void cancel() {
        cancel.set(true);
    }

    public void run(CompilationInfo parameter) throws Exception {
        cancel.set(false);
        
        long startTime = System.currentTimeMillis();
        
        try {
        LaTeXParserResult lpr = (LaTeXParserResult) parameter.getParserResult();
        Document doc = parameter.getDocument();
        VisitorImpl vi = new VisitorImpl(cancel, doc);
        
        lpr.getDocument().traverse(vi);
        
        LaTeXTokenListProvider.findTokenListImpl(doc).setAcceptedTokens(vi.acceptedTokens);
        } finally {
            long endTime = System.currentTimeMillis();
            
            Logger.getLogger("TIMER").log(Level.FINE, "Spellchecker Data Collector", new Object[] {parameter.getFileObject(), endTime - startTime});
        }
    }

    private static final class VisitorImpl extends DefaultTraverseHandler {

        private AtomicBoolean cancel;
        private Document doc;
        private Set<Token> acceptedTokens = new WeakSet<Token>();

        public VisitorImpl(AtomicBoolean cancel, Document doc) {
            this.cancel = cancel;
            this.doc = doc;
        }
        
        @Override
        public boolean argumentStart(ArgumentNode node) {
            if (node.getArgument().isEnumerable()) {
                return false;
            } else {
                ArgumentContainingNode cnode = node.getCommand();

                if (cnode instanceof CommandNode && cnode.getParent() instanceof BlockNode) {
                    return false;
                } else {
                    return !node.getArgument().isCodeLike();
                }
            }
        }

        @Override
        public boolean mathStart(MathNode node) {
            return false;
        }

        @Override
        public boolean textStart(final TextNode node) {
            if (node.getStartingPosition().getDocument() == doc) {
                doc.render(new Runnable() {
                    public void run() {
                        if (cancel.get()) {
                            return;
                        }
                        try {
                            for (Token t : node.getNodeTokens()) {
                                acceptedTokens.add(t);
                            }
                        } catch (IOException e) {
                            Exceptions.printStackTrace(e);
                        }
                    }
                });
            }
            
            return !cancel.get();
        }
        
    }
    
    public static final class Factory extends EditorAwareSourceTaskFactory {

        public Factory() {
            super(Phase.RESOLVED, Priority.NORMAL);
        }
        
        protected CancellableTask<CompilationInfo> createTask(FileObject file) {
            return new SpellcheckerDataCollector();
        }
        
    }
    
}
