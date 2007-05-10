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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
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
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcherFactory;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcher;
import org.netbeans.modules.editor.bracesmatching.spi.MatcherContext;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;
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
        fireHighlightsChange(event.getStartOffset(), event.getEndOffset());
// XXX: not neccessary
//        final int startOffset = event.getStartOffset();
//        final int endOffset = event.getEndOffset();
//        
//        SwingUtilities.invokeLater(new Runnable() {
//            private boolean inDocumentRender = false;
//            public void run() {
//                if (inDocumentRender) {
//                    fireHighlightsChange(startOffset, endOffset);
//                } else {
//                    inDocumentRender = true;
//                    document.render(this);
//                }
//            }
//        });
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
    
    private static BracesMatcherFactory findProvider(Document document, int offset) {
        MimePath mimePath = null;
        
        TokenHierarchy<? extends Document> th = TokenHierarchy.get(document);
        if (th != null) {
            TokenSequence<? extends TokenId> embedded = th.tokenSequence();
            TokenSequence<? extends TokenId> seq = null;
            
            do {
                seq = embedded;
                embedded = null;
                
                // Find the token at the caret's position
                seq.move(offset);
                if (seq.moveNext()) {
                    // Drill down to the embedded sequence
                    embedded = seq.embedded();
                }
                
            } while (embedded != null);
            
            String path = seq.languagePath().mimePath();
            mimePath = MimePath.parse(path);
        } else {
            String mimeType = (String) document.getProperty("mimeType"); //NOI18N
            mimePath = mimeType != null ? MimePath.parse(mimeType) : null;
        }
        
        if (mimePath == null) {
            mimePath = MimePath.EMPTY;
        }

        return MimeLookup.getLookup(mimePath).lookup(BracesMatcherFactory.class);
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

            PR.post(new Result(document, caret.getDot(), null, bag, bracesMatchColoring, bracesMismatchColoring));
        }
    }
    
    private static final class Result implements Runnable {

        private final Document document;
        private final int caretOffset;
        private final Position.Bias allowedDirection;
        private final OffsetsBag highlights;
        private final AttributeSet matchedColoring;
        private final AttributeSet mismatchedColoring;

        private boolean inDocumentRender = false;
        
        public Result(
            Document document, 
            int caretOffset, 
            Position.Bias allowedDirection,
            OffsetsBag highlights,
            AttributeSet matchedColoring,
            AttributeSet mismatchedColoring
        ) {
            this.document = document;
            this.caretOffset = caretOffset;
            this.allowedDirection = allowedDirection;
            this.highlights = highlights;
            this.matchedColoring = matchedColoring;
            this.mismatchedColoring = mismatchedColoring;
        }
        
        // ------------------------------------------------
        // Runnable implementation
        // ------------------------------------------------
        
        public void run() {
            // Read lock the document
            if (!inDocumentRender) {
                inDocumentRender = true;
                document.render(this);
            }

            BracesMatcherFactory provider = findProvider(document, caretOffset);
            if (provider == null || Thread.interrupted()) {
                // no provider, no matcher, nothing to do
                return;
            }
            
            int [] origin = null;
            BracesMatcher [] matcher = new BracesMatcher[1];
            
            if (allowedDirection == null) {
                origin = findOrigin(provider, Position.Bias.Backward, matcher);
                if (origin != null) {
                    if (origin[1] < caretOffset) {
                        BracesMatcher [] forwardMatcher = new BracesMatcher[1];
                        int forwardOrigin [] = findOrigin(provider, Position.Bias.Forward, forwardMatcher);
                        if (forwardOrigin != null) {
                            if (forwardOrigin[0] == caretOffset) {
                                origin = forwardOrigin;
                                matcher = forwardMatcher;
                            } else {
                                origin = null;
                            }
                        }
                    }
                } else {
                    origin = findOrigin(provider, Position.Bias.Forward, matcher);
                }
            } else {
                origin = findOrigin(provider, allowedDirection, matcher);
            }
            
            if (origin == null || Thread.interrupted()) {
                // no original area, nothing to search for
                return;
            }
            
            // Fire up the matching task
            int [] matches = matcher[0].findMatches();

            // If the task was cancelled then exit
            if (Thread.interrupted()) {
                return;
            }

            if (highlights != null) {
                if (matches != null && matches.length >= 2) {
                    // Highlight the matched origin
                    highlights.addHighlight(origin[0], origin[1], matchedColoring);

                    // Highlight all the matches
                    for(int i = 0; i < matches.length / 2; i++) {
                        highlights.addHighlight(matches[i * 2], matches[i * 2 + 1], matchedColoring);
                    }
                } else {
                    // Highlight the mismatched origin
                    highlights.addHighlight(origin[0], origin[1], mismatchedColoring);
                }
            }
        }

        private int [] findOrigin(BracesMatcherFactory provider, Position.Bias direction, BracesMatcher [] matcher) {
            Element paragraph = DocumentUtilities.getParagraphElement(document, caretOffset);
            int lookahead = 0;
            
            if (direction == Position.Bias.Backward) {
                lookahead = caretOffset - paragraph.getStartOffset();
            } else if (direction == Position.Bias.Forward) {
                lookahead = paragraph.getEndOffset() - caretOffset;
            }
            
            if (lookahead > 0) {
                MatcherContext context = SpiAccessor.get().createCaretContext(
                    document, 
                    caretOffset, 
                    direction, 
                    lookahead
                );

                matcher[0] = provider.createMatcher(context);
                int [] origin = matcher[0].findOrigin();
                
                // Check the origin for consistency
                if (origin != null) {
                    if (origin.length == 0) {
                        origin = null;
                    } else if (origin.length != 2) {
                        if (LOG.isLoggable(Level.WARNING)) {
                            LOG.warning("Invalid BracesMatcher implementation, " + //NOI18N
                                "findOrigin() can only return two offsets. " + //NOI18N
                                "Offsending BracesMatcher: " + matcher); //NOI18N
                        }
                        origin = null;
                    } else if (origin[0] < 0 || origin[1] > document.getLength() || origin[0] >= origin[1]) {
                        if (LOG.isLoggable(Level.WARNING)) {
                            LOG.warning("Invalid origin offsets [" + origin[0] + ", " + origin[1] + "]. " + //NOI18N
                                "Offsending BracesMatcher: " + matcher); //NOI18N
                        }
                        origin = null;
                    } else {
                        if (direction == Position.Bias.Backward && 
                            (origin[1] < caretOffset - lookahead || origin[0] > caretOffset))
                        {
                            if (LOG.isLoggable(Level.WARNING)) {
                                LOG.warning("Origin offsets out of range, " + //NOI18N
                                    "origin = [" + origin[0] + ", " + origin[1] + "], " + //NOI18N
                                    "caretOffset = " + caretOffset + 
                                    ", lookahead = " + lookahead + 
                                    ", searching backwards. " + //NOI18N
                                    "Offsending BracesMatcher: " + matcher); //NOI18N
                            }
                            origin = null;
                        } else if (direction == Position.Bias.Forward && 
                            (origin[1] < caretOffset || origin[0] > caretOffset + lookahead))
                        {
                            if (LOG.isLoggable(Level.WARNING)) {
                                LOG.warning("Origin offsets out of range, " + //NOI18N
                                    "origin = [" + origin[0] + ", " + origin[1] + "], " + //NOI18N
                                    "caretOffset = " + caretOffset + 
                                    ", lookahead = " + lookahead + 
                                    ", searching forward. " + //NOI18N
                                    "Offsending BracesMatcher: " + matcher); //NOI18N
                            }
                            origin = null;
                        }

                    }
                }

                if (origin != null) {
                    LOG.fine("[" + origin[0] + ", " + origin[1] + "] for caret = " + caretOffset + ", lookahead = " + (direction == Position.Bias.Backward ? "-" : "") + lookahead);
                } else {
                    LOG.fine("[null] for caret = " + caretOffset + ", lookahead = " + (direction == Position.Bias.Backward ? "-" : "") + lookahead);
                }
                
                return origin;
            } else {
                return null;
            }
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
