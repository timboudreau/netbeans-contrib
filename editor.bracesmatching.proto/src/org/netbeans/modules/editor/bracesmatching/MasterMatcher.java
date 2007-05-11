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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.editor.bracesmatching;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcher;
import org.netbeans.modules.editor.bracesmatching.spi.BracesMatcherFactory;
import org.netbeans.modules.editor.bracesmatching.spi.MatcherContext;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Vita Stejskal
 */
public final class MasterMatcher {

    private static final Logger LOG = Logger.getLogger(MasterMatcher.class.getName());
    
    public static final String PROP_ALLOWED_SEARCH_DIRECTION = "nbeditor-bracesMatching-allowedSearchDirection";
            
    public static MasterMatcher get(Document document) {
        synchronized (MM) {
            MasterMatcher mm = MM.get(document);
            if (mm == null) {
                mm = new MasterMatcher(new WeakReference<Document>(document));
                MM.put(document, mm);
            }
            return mm;
        }
    }
    
    public void highlight(
        int caretOffset, 
        Object allowedSearchDirection, 
        OffsetsBag highlights, 
        AttributeSet matchedColoring, 
        AttributeSet mismatchedColoring
    ) {
        synchronized (LOCK) {
            // If there is a task running, cacel it
            if (task != null) {
                task.cancel();
                task = null;
            } else {
                // no task, we may have results already
                if (lastOffset == caretOffset && lastAllowedSearchDirections == allowedSearchDirection) {
                    highlightAreas(lastOrigin, lastMatches, highlights, matchedColoring, mismatchedColoring);
                    return;
                }
            }

            // Remember the last request
            lastOffset = caretOffset;
            lastAllowedSearchDirections = allowedSearchDirection;
            
            // Fire up a new task
            task = PR.post(new Result(documentRef.get(), caretOffset, allowedSearchDirection, highlights, matchedColoring, mismatchedColoring));
        }
    }
    
    public void navigate(
        int caretOffset, 
        Object allowedSearchDirection, 
        Caret caret
    ) {
        RequestProcessor.Task waitFor = null;
        
        synchronized (LOCK) {
            if (task == null) {
                // no task, we may have results already
                if (lastOffset == caretOffset && lastAllowedSearchDirections == allowedSearchDirection) {
                    navigateAreas(lastOrigin, lastMatches, caret);
                    return;
                } else {
                    // Remember the last request
                    lastOffset = caretOffset;
                    lastAllowedSearchDirections = allowedSearchDirection;

                    // Fire up a new task
                    task = PR.post(new Result(documentRef.get(), caretOffset, allowedSearchDirection, null, null, null));
                    waitFor = task;
                }
            } else {
                waitFor = task;
            }
        }
        
        waitFor.waitFinished();
        
        synchronized (LOCK) {
            if (lastOffset == caretOffset && lastAllowedSearchDirections == allowedSearchDirection) {
                navigateAreas(lastOrigin, lastMatches, caret);
            }
        }        
    }
    
    private static final Map<Document, MasterMatcher> MM = new WeakHashMap<Document, MasterMatcher>();
    private static final RequestProcessor PR = new RequestProcessor("EditorBracesMatching", 5, true);

    private final String LOCK = new String("MasterMatcher.LOCK"); //NOI18N

    private final Reference<Document> documentRef;
    
    private RequestProcessor.Task task = null;
    private int lastOffset = -1;
    private Object lastAllowedSearchDirections;
    private int [] lastOrigin;
    private int [] lastMatches;
    
    private MasterMatcher(Reference<Document> documentRef) {
        this.documentRef = documentRef;
    }

    private static void highlightAreas(
        int [] origin, 
        int [] matches,
        OffsetsBag highlights, 
        AttributeSet matchedColoring, 
        AttributeSet mismatchedColoring
    ) {
        // Remove all existing highlights
        highlights.clear();

        if (matches != null && matches.length >= 2) {
            // Highlight the matched origin
            highlights.addHighlight(origin[0], origin[1], matchedColoring);

            // Highlight all the matches
            for(int i = 0; i < matches.length / 2; i++) {
                highlights.addHighlight(matches[i * 2], matches[i * 2 + 1], matchedColoring);
            }
        } else if (origin != null && origin.length >= 2) {
            // Highlight the mismatched origin
            highlights.addHighlight(origin[0], origin[1], mismatchedColoring);
        }
    }
    
    private static void navigateAreas(
        int [] origin, 
        int [] matches,
        Caret caret
    ) {
        if (matches != null && matches.length >= 2) {
            int newDotBackward = Integer.MIN_VALUE;
            int newDotForward = Integer.MAX_VALUE;
            
            for(int i = 0; i < matches.length / 2; i++) {
                if (matches[i * 2] < origin[0] && matches[i * 2] > newDotBackward) {
                    newDotBackward = matches[i * 2 + 1];
                }
                
                if (matches[i * 2] > origin[1] && matches[i * 2] < newDotForward) {
                    newDotForward = matches[i * 2 + 1];
                }
            }
            
            if (newDotBackward != Integer.MIN_VALUE) {
                caret.setDot(newDotBackward);
            } else if (newDotForward != Integer.MAX_VALUE) {
                caret.setDot(newDotForward);
            }
        }
    }
    
    private final class Result implements Runnable {

        private final Document document;
        private final int caretOffset;
        private final Object allowedDirection;
        private final OffsetsBag highlights;
        private final AttributeSet matchedColoring;
        private final AttributeSet mismatchedColoring;

        private boolean inDocumentRender = false;
        private boolean interrupted = false;
        private int [] origin = null;
        private int [] matches = null;
        
        public Result(
            Document document, 
            int caretOffset, 
            Object allowedDirection,
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
                
                synchronized (LOCK) {
                    // If the task was cancelled, we must exit without storing results
                    if (interrupted || Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    // Store the results
                    MasterMatcher.this.lastOrigin = origin;
                    MasterMatcher.this.lastMatches = matches;

                    // Signal that the task is done.
                    MasterMatcher.this.task = null;

                    if (highlights != null) {
                        highlightAreas(origin, matches, highlights, matchedColoring, mismatchedColoring);
                    }
                }
                
                return;
            }

            BracesMatcherFactory provider = findProvider();
            if (provider == null || Thread.currentThread().isInterrupted()) {
                // no provider, no matcher, nothing to do
                return;
            }
            
            try {
                BracesMatcher [] matcher = new BracesMatcher[1];

                if (allowedDirection == null) {
                    origin = findOrigin(provider, true, matcher);
                    if (origin != null) {
                        if (origin[1] < caretOffset) {
                            BracesMatcher [] forwardMatcher = new BracesMatcher[1];
                            int forwardOrigin [] = findOrigin(provider, false, forwardMatcher);
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
                        origin = findOrigin(provider, false, matcher);
                    }
                } else {
                    // If the allowedDirection was specified, but contains rubbish, default is backward.
                    boolean forward = "forward".equalsIgnoreCase(allowedDirection.toString()); //NOI18N
                    origin = findOrigin(provider, !forward, matcher);
                }

                if (origin == null || Thread.currentThread().isInterrupted()) {
                    // no original area, nothing to search for
                    return;
                }
            
                matches = matcher[0].findMatches();
            } catch (InterruptedException e) {
                // We were interrupted, no results
                interrupted = true;
            }
        }

        private BracesMatcherFactory findProvider() {
            MimePath mimePath = null;

            TokenHierarchy<? extends Document> th = TokenHierarchy.get(document);
            if (th != null) {
                TokenSequence<? extends TokenId> embedded = th.tokenSequence();
                TokenSequence<? extends TokenId> seq = null;

                do {
                    seq = embedded;
                    embedded = null;

                    // Find the token at the caret's position
                    seq.move(caretOffset);
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
        
        private int [] findOrigin(BracesMatcherFactory provider, boolean backward, BracesMatcher [] matcher) throws InterruptedException {
            Element paragraph = DocumentUtilities.getParagraphElement(document, caretOffset);
            int lookahead = 0;
            
            if (backward) {
                lookahead = caretOffset - paragraph.getStartOffset();
            } else {
                lookahead = paragraph.getEndOffset() - caretOffset;
            }
            
            if (lookahead > 0) {
                MatcherContext context = SpiAccessor.get().createCaretContext(
                    document, 
                    caretOffset, 
                    backward, 
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
                        if (backward) {
                            if (origin[1] < caretOffset - lookahead || origin[0] > caretOffset) {
                                if (LOG.isLoggable(Level.WARNING)) {
                                    LOG.warning("Origin offsets out of range, " + //NOI18N
                                        "origin = [" + origin[0] + ", " + origin[1] + "], " + //NOI18N
                                        "caretOffset = " + caretOffset + 
                                        ", lookahead = " + lookahead + 
                                        ", searching backwards. " + //NOI18N
                                        "Offsending BracesMatcher: " + matcher); //NOI18N
                                }
                                origin = null;
                            }
                        } else {
                            if ((origin[1] < caretOffset || origin[0] > caretOffset + lookahead)) {
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
                }

                if (origin != null) {
                    LOG.fine("[" + origin[0] + ", " + origin[1] + "] for caret = " + caretOffset + ", lookahead = " + (backward ? "-" : "") + lookahead);
                } else {
                    LOG.fine("[null] for caret = " + caretOffset + ", lookahead = " + (backward ? "-" : "") + lookahead);
                }
                
                return origin;
            } else {
                return null;
            }
        }
        
    } // End of Result class
}
