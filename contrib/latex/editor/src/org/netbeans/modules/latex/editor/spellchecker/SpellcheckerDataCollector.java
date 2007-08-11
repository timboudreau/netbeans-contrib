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

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.gsf.CancellableTask;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.retouche.source.CompilationInfo;
import org.netbeans.api.retouche.source.Phase;
import org.netbeans.api.retouche.source.Source.Priority;
import org.netbeans.api.retouche.source.support.EditorAwareSourceTaskFactory;
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
