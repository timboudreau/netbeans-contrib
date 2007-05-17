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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Vita Stejskal
 */
public final class MasterMatcher {

    private static final Logger LOG = Logger.getLogger(MasterMatcher.class.getName());
    
    public static final String PROP_ALLOWED_SEARCH_DIRECTION = "nbeditor-bracesMatching-allowedSearchDirection"; //NOI18N
    public static final String D_BACKWARD = "backward"; //NOI18N
    public static final String D_FORWARD = "forward"; //NOI18N
    public static final String D_BACKWARD_PREFERRED = "backward-preferred"; //NOI18N
    public static final String D_FORWARD_PREFERRED = "forward-preferred"; //NOI18N
    
    public static final String PROP_SHOW_AMBIGUOUS_ORIGINS = "nbeditor-bracesMatching-showAmbiguousOrigins"; //NOI18N
            
    public static final String PROP_MAX_BACKWARD_LOOKAHEAD = "nbeditor-bracesMatching-maxBackwardLookahead"; //NOI18N
    public static final String PROP_MAX_FORWARD_LOOKAHEAD = "nbeditor-bracesMatching-maxForwardLookahead"; //NOI18N
    private static final int DEFAULT_MAX_LOOKAHEAD = 256;
    
    public static synchronized MasterMatcher get(JTextComponent component) {
        MasterMatcher mm = (MasterMatcher) component.getClientProperty(MasterMatcher.class);
        if (mm == null) {
            mm = new MasterMatcher(component);
            component.putClientProperty(MasterMatcher.class, mm);
        }
        return mm;
    }
    
    public void highlight(
        Document document,
        int caretOffset, 
        OffsetsBag highlights, 
        AttributeSet matchedColoring, 
        AttributeSet mismatchedColoring
    ) {
        synchronized (LOCK) {
            Object allowedSearchDirection = getAllowedDirection();
            boolean showAmbiguousOrigins = getShowAmbiguousOrigins();
            int maxBwdLookahead = getMaxLookahead(true);
            int maxFwdLookahead = getMaxLookahead(false);
            
            if (task != null) {
                // a task is running, perhaps just add a new job to it
                if (lastResult.getCaretOffset() == caretOffset && 
                    lastResult.getAllowedDirection() == allowedSearchDirection &&
                    lastResult.getShowAmbiguousOrigins() == showAmbiguousOrigins &&
                    lastResult.getMaxBwdLookahead() == maxBwdLookahead &&
                    lastResult.getMaxFwdLookahead() == maxBwdLookahead
                ) {
                    lastResult.addHighlightingJob(highlights, matchedColoring, mismatchedColoring);
                } else {
                    // Different request, cancel the current task
                    task.cancel();
                    task = null;
                }
            }

            if (task == null) {
                // Remember the last request
                lastResult = new Result(document, caretOffset, allowedSearchDirection, showAmbiguousOrigins, maxBwdLookahead, maxFwdLookahead);
                lastResult.addHighlightingJob(highlights, matchedColoring, mismatchedColoring);

                // Fire up a new task
                task = PR.post(lastResult);
            }
        }
    }
    
    public void navigate(
        Document document,
        int caretOffset, 
        Caret caret,
        boolean select
    ) {
        RequestProcessor.Task waitFor = null;
        
        synchronized (LOCK) {
            Object allowedSearchDirection = getAllowedDirection();
            boolean showAmbiguousOrigins = getShowAmbiguousOrigins();
            int maxBwdLookahead = getMaxLookahead(true);
            int maxFwdLookahead = getMaxLookahead(false);

            if (task != null) {
                // a task is running, perhaps just add a new job to it
                if (lastResult.getCaretOffset() == caretOffset && 
                    lastResult.getAllowedDirection() == allowedSearchDirection &&
                    lastResult.getShowAmbiguousOrigins() == showAmbiguousOrigins &&
                    lastResult.getMaxBwdLookahead() == maxBwdLookahead &&
                    lastResult.getMaxFwdLookahead() == maxBwdLookahead
                ) {
                    lastResult.addNavigationJob(caret, select);
                    waitFor = task;
                } else {
                    // Different request, cancel the current task
                    task.cancel();
                    task = null;
                }
            }

            if (task == null) {
                // Remember the last request
                lastResult = new Result(document, caretOffset, allowedSearchDirection, showAmbiguousOrigins, maxBwdLookahead, maxFwdLookahead);
                lastResult.addNavigationJob(caret, select);

                // Fire up a new task
                task = PR.post(lastResult);
                waitFor = task;
            }
        }
        
        if (waitFor != null) {
            waitFor.waitFinished();
        }
    }
    
    private static final RequestProcessor PR = new RequestProcessor("EditorBracesMatching", 5, true); //NOI18N

    private final String LOCK = new String("MasterMatcher.LOCK"); //NOI18N

    private final JTextComponent component;
    
    private RequestProcessor.Task task = null;
    private Result lastResult = null;
    
    private MasterMatcher(JTextComponent component) {
        this.component = component;
    }

    private Object getAllowedDirection() {
        Object allowedDirection = component.getClientProperty(PROP_ALLOWED_SEARCH_DIRECTION);
        return allowedDirection != null ? allowedDirection : D_BACKWARD_PREFERRED;
    }

    private boolean getShowAmbiguousOrigins() {
        Object showAmbiguousOrigins = component.getClientProperty(PROP_SHOW_AMBIGUOUS_ORIGINS);
        if (showAmbiguousOrigins instanceof Boolean) {
            return ((Boolean) showAmbiguousOrigins).booleanValue();
        } else if (showAmbiguousOrigins != null) {
            return Boolean.valueOf(showAmbiguousOrigins.toString());
        } else {
            return false;
        }
    }

    private int getMaxLookahead(boolean backward) {
        String propName = backward ? PROP_MAX_BACKWARD_LOOKAHEAD : PROP_MAX_FORWARD_LOOKAHEAD;
        int maxLookahead = DEFAULT_MAX_LOOKAHEAD;
        Object value = component.getClientProperty(propName);
        if (value instanceof Integer) {
            maxLookahead = ((Integer) value).intValue();
        } else if (value != null) {
            try {
                maxLookahead = Integer.valueOf(value.toString());
            } catch (NumberFormatException nfe) {
                LOG.log(Level.WARNING, "Can't parse the value of " + propName + ": '" + value + "'", nfe); //NOI18N
            }
        }
        
        if (maxLookahead > 0 && maxLookahead <= DEFAULT_MAX_LOOKAHEAD) {
            return maxLookahead;
        } else {
            LOG.warning("Invalid value of " + propName + ": " + maxLookahead); //NOI18N
            return DEFAULT_MAX_LOOKAHEAD;
        }
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

    // when navigating: always set the dot after a matching area
    // when selecting: always select the inside between original and matching areas
    //                 do not select the areas themselvs
    private static void navigateAreas(
        int [] origin, 
        int [] matches,
        Caret caret,
        boolean select
    ) {
        if (matches != null && matches.length >= 2) {
            int newDotBackward = Integer.MIN_VALUE;
            int newDotForward = Integer.MAX_VALUE;
            
            for(int i = 0; i < matches.length / 2; i++) {
                if (matches[i * 2] < origin[0] && matches[i * 2] > newDotBackward) {
                    newDotBackward = matches[i * 2 + 1];
                }
                
                if (matches[i * 2] > origin[1] && matches[i * 2] < newDotForward) {
                    if (select) {
                        newDotForward = matches[i * 2];
                    } else {
                        newDotForward = matches[i * 2 + 1];
                    }
                }
            }
            
            if (newDotBackward != Integer.MIN_VALUE) {
                if (select) {
                    caret.setDot(origin[0]);
                    caret.moveDot(newDotBackward);
                } else {
                    caret.setDot(newDotBackward);
                }
            } else if (newDotForward != Integer.MAX_VALUE) {
                if (select) {
                    caret.setDot(origin[1]);
                    caret.moveDot(newDotForward);
                } else {
                    caret.setDot(newDotForward);
                }
            }
        }
    }
    
    private final class Result implements Runnable {

        private final Document document;
        private final int caretOffset;
        private final Object allowedDirection;
        private final boolean showAmbiguousOrigins;
        private final int maxBwdLookahead;
        private final int maxFwdLookahead;

        private boolean inDocumentRender = false;
        private boolean interrupted = false;
        private int [] origin = null;
        private int [] matches = null;

        private final List<Object []> highlightingJobs = new ArrayList<Object []>();
        private final List<Object []> navigationJobs = new ArrayList<Object []>();
        
        public Result(
            Document document, 
            int caretOffset, 
            Object allowedDirection,
            boolean showAmbiguousOrigins,
            int maxBwdLookahead,
            int maxFwdLookahead
        ) {
            this.document = document;
            this.caretOffset = caretOffset;
            this.allowedDirection = allowedDirection;
            this.showAmbiguousOrigins = showAmbiguousOrigins;
            this.maxBwdLookahead = maxBwdLookahead;
            this.maxFwdLookahead = maxFwdLookahead;
        }
        
        // Must be called under the MasterMatcher.LOCK
        public void addHighlightingJob(
            OffsetsBag highlights,
            AttributeSet matchedColoring,
            AttributeSet mismatchedColoring
        ) {
            highlightingJobs.add(new Object[] {
                highlights,
                matchedColoring,
                mismatchedColoring
            });
        }

        // Must be called under the MasterMatcher.LOCK
        public void addNavigationJob(Caret caret, boolean select) {
            navigationJobs.add(new Object [] { caret, select });
        }
        
        public int getCaretOffset() {
            return caretOffset;
        }
        
        public Object getAllowedDirection() {
            return allowedDirection;
        }
        
        public boolean getShowAmbiguousOrigins() {
            return showAmbiguousOrigins;
        }
        
        public int getMaxBwdLookahead() {
            return maxBwdLookahead;
        }
        
        public int getMaxFwdLookahead() {
            return maxFwdLookahead;
        }
        
        // Must be called under the MasterMatcher.LOCK
        public int [] getOrigin() {
            return origin;
        }
        
        // Must be called under the MasterMatcher.LOCK
        public int [] getMatches() {
            return matches;
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

                    for (Object[] job : highlightingJobs) {
                        highlightAreas(origin, matches, (OffsetsBag) job[0], (AttributeSet) job[1], (AttributeSet) job[2]);
                    }
                    
                    for(Object [] job : navigationJobs) {
                        navigateAreas(origin, matches, (Caret) job[0], (Boolean) job[1]);
                    }
                    
                    // Signal that the task is done.
                    MasterMatcher.this.task = null;
                }
                
                return;
            }

            Collection<? extends BracesMatcherFactory> factories = findFactories();
            if (factories.isEmpty() || Thread.currentThread().isInterrupted()) {
                // no provider, no matcher, nothing to do
                return;
            }
            
            try {
                BracesMatcher [] matcher = new BracesMatcher[1];

                if (D_BACKWARD.equalsIgnoreCase(allowedDirection.toString())) {
                    origin = findOrigin(factories, true, matcher);
                } else if (D_FORWARD.equalsIgnoreCase(allowedDirection.toString())) {
                    origin = findOrigin(factories, false, matcher);
                } else {
                    boolean firstBackward;
                    
                    if (D_BACKWARD_PREFERRED.equalsIgnoreCase(allowedDirection.toString())) {
                        firstBackward = true;
                    } else if (D_FORWARD_PREFERRED.equalsIgnoreCase(allowedDirection.toString())) {
                        firstBackward = false;
                    } else {
                        throw new IllegalStateException("The property " + PROP_ALLOWED_SEARCH_DIRECTION + " has invalid value: '" + allowedDirection + "'"); //NOI18N
                    }
                    
                    origin = findOrigin(factories, firstBackward, matcher);
                    if (origin != null) {
                        if ((firstBackward && origin[1] < caretOffset) ||
                            (!firstBackward && origin[0] > caretOffset))
                        {
                            BracesMatcher [] oppositeDirectionMatcher = new BracesMatcher[1];
                            int oppositeDirectionOrigin [] = findOrigin(factories, !firstBackward, oppositeDirectionMatcher);
                            if (oppositeDirectionOrigin != null) {
                                if ((firstBackward && oppositeDirectionOrigin[0] == caretOffset) ||
                                    (!firstBackward && oppositeDirectionOrigin[1] == caretOffset))
                                {
                                    origin = oppositeDirectionOrigin;
                                    matcher = oppositeDirectionMatcher;
                                } else if (!showAmbiguousOrigins) {
                                    origin = null;
                                }
                            }
                        }
                    } else {
                        origin = findOrigin(factories, !firstBackward, matcher);
                    }
                }

                if (origin == null || Thread.currentThread().isInterrupted()) {
                    // no original area, nothing to search for
                    return;
                }
            
                matches = matcher[0].findMatches();
            } catch (BadLocationException ble) {
                LOG.log(Level.WARNING, null, ble);
            } catch (InterruptedException e) {
                // We were interrupted, no results
                interrupted = true;
            }
        }

        private Collection<? extends BracesMatcherFactory> findFactories() {
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

            return MimeLookup.getLookup(mimePath).lookupAll(BracesMatcherFactory.class);
        }
        
        private int [] findOrigin(
            Collection<? extends BracesMatcherFactory> factories, 
            boolean backward, 
            BracesMatcher [] matcher
        ) throws InterruptedException {
            Element paragraph = DocumentUtilities.getParagraphElement(document, caretOffset);
            int lookahead = 0;
            
            if (backward) {
                lookahead = caretOffset - paragraph.getStartOffset();
                if (lookahead > maxBwdLookahead) {
                    lookahead = maxBwdLookahead;
                }
            } else {
                lookahead = paragraph.getEndOffset() - caretOffset;
                if (lookahead > maxFwdLookahead) {
                    lookahead = maxFwdLookahead;
                }
            }
            
            if (lookahead > 0) {
                MatcherContext context = SpiAccessor.get().createCaretContext(
                    document, 
                    caretOffset, 
                    backward, 
                    lookahead
                );

                // Find the first provider that accepts the context
                for(BracesMatcherFactory factory : factories) {
                    matcher[0] = factory.createMatcher(context);
                    if (matcher[0] != null) {
                        break;
                    }
                }

                // Find the original area
                int [] origin = null;
                try {
                    origin = matcher[0].findOrigin();
                } catch (BadLocationException ble) {
                    LOG.log(Level.WARNING, null, ble);
                }
                
                // Check the original area for consistency
                if (origin != null) {
                    if (origin.length == 0) {
                        origin = null;
                    } else if (origin.length != 2) {
                        if (LOG.isLoggable(Level.WARNING)) {
                            LOG.warning("Invalid BracesMatcher implementation, " + //NOI18N
                                "findOrigin() can only return two offsets. " + //NOI18N
                                "Offending BracesMatcher: " + matcher); //NOI18N
                        }
                        origin = null;
                    } else if (origin[0] < 0 || origin[1] > document.getLength() || origin[0] > origin[1]) {
                        if (LOG.isLoggable(Level.WARNING)) {
                            LOG.warning("Invalid origin offsets [" + origin[0] + ", " + origin[1] + "]. " + //NOI18N
                                "Offending BracesMatcher: " + matcher); //NOI18N
                        }
                        origin = null;
                    } else {
                        if (backward) {
                            if (origin[1] < caretOffset - lookahead || origin[0] > caretOffset) {
                                if (LOG.isLoggable(Level.WARNING)) {
                                    LOG.warning("Origin offsets out of range, " + //NOI18N
                                        "origin = [" + origin[0] + ", " + origin[1] + "], " + //NOI18N
                                        "caretOffset = " + caretOffset + //NOI18N
                                        ", lookahead = " + lookahead + //NOI18N
                                        ", searching backwards. " + //NOI18N
                                        "Offending BracesMatcher: " + matcher); //NOI18N
                                }
                                origin = null;
                            }
                        } else {
                            if ((origin[1] < caretOffset || origin[0] > caretOffset + lookahead)) {
                                if (LOG.isLoggable(Level.WARNING)) {
                                    LOG.warning("Origin offsets out of range, " + //NOI18N
                                        "origin = [" + origin[0] + ", " + origin[1] + "], " + //NOI18N
                                        "caretOffset = " + caretOffset + //NOI18N
                                        ", lookahead = " + lookahead + //NOI18N
                                        ", searching forward. " + //NOI18N
                                        "Offending BracesMatcher: " + matcher); //NOI18N
                                }
                                origin = null;
                            }
                        }

                    }
                }

                if (origin != null) {
                    LOG.fine("[" + origin[0] + ", " + origin[1] + "] for caret = " + caretOffset + ", lookahead = " + (backward ? "-" : "") + lookahead); //NOI18N
                } else {
                    LOG.fine("[null] for caret = " + caretOffset + ", lookahead = " + (backward ? "-" : "") + lookahead); //NOI18N
                }
                
                return origin;
            } else {
                return null;
            }
        }
        
    } // End of Result class
}
