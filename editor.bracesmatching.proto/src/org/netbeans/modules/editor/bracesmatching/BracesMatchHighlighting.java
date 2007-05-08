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
package org.netbeans.modules.editor.bracesmatching;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.highlighting.HighlightsChangeEvent;
import org.netbeans.spi.editor.highlighting.HighlightsChangeListener;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.WeakListeners;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatchProvider;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatchTask;
import org.netbeans.modules.editor.bracesmatching.spi.CaretContext;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Vita Stejskal
 */
public class BracesMatchHighlighting extends AbstractHighlightsContainer 
    implements ChangeListener, PropertyChangeListener, HighlightsChangeListener, DocumentListener 
{
    private static final Logger LOG = Logger.getLogger(BracesMatchHighlighting.class.getName());
    
    private static final String BRACES_MATCH_COLORING = "braces-match"; //NOI18N
    private static final String BRACES_MISMATCH_COLORING = "braces-mismatch"; //NOI18N
    
    private final String LOCK = new String("BracesMatchHighlighting-LOCK");
    
    private final JTextComponent component;
    private final Document document;
    
    private Caret caret = null;
    private ChangeListener caretListener;
    
    private final OffsetsBag bag;
    private final AttributeSet bracesMatchColoring;
    private final AttributeSet bracesMismatchColoring;

    private static final RequestProcessor PR = new RequestProcessor("BracesMatching", 1, true);
    private RequestProcessor.Task task = null;
    
    public BracesMatchHighlighting(JTextComponent component, Document document) {
        this.document = document;
        
        String mimeType = (String) document.getProperty("mimeType"); //NOI18N
        MimePath mimePath = MimePath.parse(mimeType);

        // Load the colorings
        FontColorSettings fcs = MimeLookup.getLookup(mimePath).lookup(FontColorSettings.class);
        this.bracesMatchColoring = fcs.getFontColors(BRACES_MATCH_COLORING);
        this.bracesMismatchColoring = fcs.getFontColors(BRACES_MISMATCH_COLORING);
        
        // Create and hook up the highlights bag
        this.bag = new OffsetsBag(document, false); // don't merge highlights
        this.bag.addHighlightsChangeListener(this);
        
        // Hook up the component
        this.component = component;
        this.component.addPropertyChangeListener(WeakListeners.propertyChange(this, this.component));

        // Hook up the caret
        this.caret = component.getCaret();
        if (this.caret != null) {
            this.caretListener = WeakListeners.change(this, this.caret);
            this.caret.addChangeListener(caretListener);
        }

        // Refresh the layer
        refresh();
    }

    // ------------------------------------------------
    // AbstractHighlightsContainer implementation
    // ------------------------------------------------
    
    public HighlightsSequence getHighlights(int startOffset, int endOffset) {
        return bag.getHighlights(startOffset, endOffset);
    }

    // ------------------------------------------------
    // HighlightsChangeListener implementation
    // ------------------------------------------------
    
    public void highlightChanged(HighlightsChangeEvent event) {
        final int startOffset = event.getStartOffset();
        final int endOffset = event.getEndOffset();
        
        SwingUtilities.invokeLater(new Runnable() {
            private boolean inDocumentRender = false;
            public void run() {
                if (inDocumentRender) {
                    fireHighlightsChange(startOffset, endOffset);
                } else {
                    inDocumentRender = true;
                    document.render(this);
                }
            }
        });
    }

    // ------------------------------------------------
    // DocumentListener implementation
    // ------------------------------------------------
    
    public void insertUpdate(DocumentEvent e) {
        refresh();
    }

    public void removeUpdate(DocumentEvent e) {
        refresh();
    }

    public void changedUpdate(DocumentEvent e) {
        refresh();
    }
    
    // ------------------------------------------------
    // ChangeListener implementation
    // ------------------------------------------------
    
    public void stateChanged(ChangeEvent e) {
        refresh();
    }

    // ------------------------------------------------
    // PropertyChangeListener implementation
    // ------------------------------------------------
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == null || "caret".equals(evt.getPropertyName())) { //NOI18N
            if (caret != null) {
                caret.removeChangeListener(caretListener);
                caretListener = null;
            }
            
            caret = component.getCaret();
            
            if (caret != null) {
                caretListener = WeakListeners.change(this, caret);
                caret.addChangeListener(caretListener);
            }
            
            refresh();
        }
    }

    // ------------------------------------------------
    // private implementation
    // ------------------------------------------------
    
    private BracesMatchProvider findProvider(CaretContext context) {
        BracesMatchProvider provider = null;
        
        TokenHierarchy<? extends Document> th = TokenHierarchy.get(context.getDocument());
        if (th != null) {
            TokenSequence<? extends TokenId> embedded = th.tokenSequence();
            TokenSequence<? extends TokenId> seq = null;
            
            do {
                seq = embedded;
                embedded = null;
                
                // Find the token at the caret's position
                seq.move(context.getCaretOffset());
                if (seq.moveNext()) {
                    // Drill down to the embedded sequence
                    embedded = seq.embedded();
                }
                
            } while (embedded != null);
            
            String mimePath = seq.languagePath().mimePath();
            Lookup lookup = MimeLookup.getLookup(MimePath.parse(mimePath));
            provider = lookup.lookup(BracesMatchProvider.class);
        }
        
        return provider;
    }
    
    private void refresh() {
        synchronized (LOCK) {
            // If there is a task running, cacel it
            if (task != null) {
                task.cancel();
                task = null;
            }

            // Remove all existing highlights
            bag.clear();

            // If there is no caret, we have nothing to do
            if (caret == null) {
                return;
            }

            Result async = new Result();
            CaretContext context = SpiAccessor.get().createCaretContext(document, caret.getDot(), async);
            BracesMatchProvider provider = findProvider(context);

            if (provider != null) {
                BracesMatchTask matcher = provider.createTask(context);
                async.initMatcher(matcher);
                
                if (matcher.canMatch()) {
                    if (matcher.isAsynchronous()) {
                        task = PR.post(async);
                    } else {
                        async.run();
                    }
                }
            }
        }
    }
    
    private final class Result implements Runnable, BracesMatchTaskResult {

        private final OffsetsBag privateBag; // to make sure that cancelled tasks do not set any highlights
        private BracesMatchTask matcher;
        
        private int originalTokenStart = -1;
        private int originalTokenEnd = -1;
        private boolean hasMatchingTokens = false;
        
        public Result() {
            this.privateBag = new OffsetsBag(document, false);
        }
        
        public void initMatcher(BracesMatchTask matcher) {
            assert matcher != null : "The 'matcher' parameter must not be null."; //NOI18N
            assert this.matcher == null : "The matcher has already been initialized."; //NOI18N
            this.matcher = matcher;
        }
        
        // ------------------------------------------------
        // Runnable implementation
        // ------------------------------------------------
        
        public void run() {
            // Fire up the matching task
            matcher.findMatchingTokens();

            // If the task was cancelled then exit
            if (Thread.interrupted()) {
                return;
            }

            if (!hasMatchingTokens) {
                // mismatch ?
                if (originalTokenStart != -1 && originalTokenEnd != -1) {
                    privateBag.addHighlight(originalTokenStart, originalTokenEnd, bracesMismatchColoring);
                }
            }
            
            bag.setHighlights(privateBag);
        }

        // ------------------------------------------------
        // BracesMatchTaskResultImpl implementation
        // ------------------------------------------------
        
        public void setOriginalToken(int startOffset, int endOffset) {
            assert originalTokenStart == -1 && originalTokenEnd == -1 : "There can only be one original token."; //NOI18N
            privateBag.addHighlight(startOffset, endOffset, bracesMatchColoring);
            originalTokenStart = startOffset;
            originalTokenEnd = endOffset;
        }

        public void addMatchingToken(int startOffset, int endOffset) {
            privateBag.addHighlight(startOffset, endOffset, bracesMatchColoring);
            hasMatchingTokens = true;
        }
    } // End of Result class
    
    public static final class Factory implements HighlightsLayerFactory {
        public HighlightsLayer[] createLayers(Context context) {
            return new HighlightsLayer [] {
                HighlightsLayer.create(
                    "org-netbeans-modules-editor-bracesmatching-BracesMatchHighlighting", //NOI18N
                    ZOrder.SHOW_OFF_RACK, 
                    true, 
                    new BracesMatchHighlighting(context.getComponent(), context.getDocument())
                )
            };
        }
    } // End of Factory class
}
