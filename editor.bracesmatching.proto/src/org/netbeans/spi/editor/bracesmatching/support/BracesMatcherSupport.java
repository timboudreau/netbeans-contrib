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
package org.netbeans.spi.editor.bracesmatching.support;

import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;

/**
 * Some useful implementations of <code>BracesMatcher</code>.
 * 
 * @author Vita Stejskal
 */
public final class BracesMatcherSupport {

    private static final char [] DEFAULT_CHARS = new char [] { '(', ')', '[', ']', '{', '}', '<', '>' }; //NOI18N
   
    /**
     * Creates the default <code>BracesMatcher</code> implementation. The default
     * matcher is used when no other matcher is available. The default matcher
     * is basically a character matcher, which looks for the following character
     * pairs: <code>'(', ')', '[', ']', '{', '}', '&lt;', '&gt;'</code>.
     * 
     * @param context The context for the matcher.
     * @param lowerBound The start offset of the area where the created matcher
     *   should search. Can be <code>-1</code> for no restriction.
     * @param upperBound The end offset of the area where the crated matcher
     *   should search. Can be <code>-1</code> for no restriction.
     * 
     * @return The default matcher.
     * @see #characterMatcher
     */
    public static BracesMatcher defaultMatcher(MatcherContext context, int lowerBound, int upperBound) {
        return new CharacterMatcher(context, lowerBound, upperBound, DEFAULT_CHARS);
    }
    
    /**
     * Creates <code>BracesMatcher</code> for finding character pairs.
     * 
     * <p>The character matcher looks for characters passed in as an
     * array of paired characters that match each other. Any character from
     * the array can be detected as the original character (area). The other
     * character from the pair will then be used to search for the matching area.
     * 
     * <p>The characters in each pair have to be listed in a specific order.
     * The order determines where the matching character should lay in text
     * relatively to the position of the original character. When the first character
     * is detected as the original character the matcher will search for the
     * matching character (ie. the second character from the pair) in the forward
     * direction (ie. towards the end of a document). Similarily when the second
     * character is detected as the original character the matcher will search
     * for the matching character (ie. the first character in the pair) in the
     * backward direction towards the beginning of a document.
     * 
     * <p>In other words each pair should contain the 'opening' character first
     * and the 'closing' character second. For example, when searching for curely
     * braces they should be listed in the following order
     * <code>char [] braces = new char [] { '{', '}' }</code>.
     * 
     * <p>The created matcher can be further restricted to search in a certain
     * area only. This might be useful for restricting the search to a particular
     * lexical token in text (eg. a string literal, javadoc comment, etc.).
     * 
     * @param context The context for the matcher.
     * @param lowerBound The start offset of the area where the created matcher
     *   should search. Can be <code>-1</code> for no restriction.
     * @param upperBound The end offset of the area where the crated matcher
     *   should search. Can be <code>-1</code> for no restriction.
     * @param matchingPairs The array with pairs of matching characters. There
     *   should always be an even number of elements in the array.
     * 
     * @return The character matcher.
     */
    public static BracesMatcher characterMatcher(MatcherContext context, int lowerBound, int upperBound, char... matchingPairs) {
        return new CharacterMatcher(context, lowerBound, upperBound, matchingPairs);
    }
    
    private BracesMatcherSupport() {
    }

    // Used from the layer
    private static BracesMatcherFactory defaultMatcherFactory() {
        return new BracesMatcherFactory() {
            public BracesMatcher createMatcher(MatcherContext context) {
                return defaultMatcher(context, -1, -1);
            }
        };
    }
}
