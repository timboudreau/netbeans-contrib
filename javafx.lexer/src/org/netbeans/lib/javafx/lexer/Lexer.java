/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
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

package org.netbeans.lib.javafx.lexer;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lexer base class provide user code for grammar. This code is called from antlr generated lexer. The main
 * purpose is to cover differences between javafxc lexer customizations and this module.
 *
 * @author Rastislav Komara (<a href="mailto:rastislav.komara@sun.com">RKo</a>)
 */
abstract class Lexer extends org.antlr.runtime.Lexer {
    List<Token> tokens = new ArrayList<Token>();
    public final BraceQuoteTracker NULL_BQT = new BraceQuoteTracker(null, '\'', false);
    private BraceQuoteTracker quoteStack = NULL_BQT;

    protected Lexer(org.antlr.runtime.CharStream charStream, org.antlr.runtime.RecognizerSharedState recognizerSharedState) {
        super(charStream, recognizerSharedState);
    }

    protected Lexer() {
    }

    protected Lexer(CharStream input) {
        super(input);
    }

    /**
     * Allow emitting more than one token from a lexer rule
     */
    public void emit(Token token) {
        state.token = token;
        tokens.add(token);
    }

    /**
     * {@inheritDoc}
     */
    public Token nextToken() {
        if (tokens.size() > 0) {
            return tokens.remove(0);
        }
        super.nextToken();
        if (tokens.size() == 0) {
            emit(Token.EOF_TOKEN);
        }
        return tokens.remove(0);
    }

    /**
     * Gets 'braceQuoteTracker'.
     *
     * @return Value for property 'braceQuoteTracker'.
     */
    public BraceQuoteTracker getBraceQuoteTracker() {
        return quoteStack;
    }

    /**
     * Sets 'braceQuoteTracker'.
     *
     * @param stack Value to set for property 'braceQuoteTracker'.
     */
    public void setBraceQuoteTracker(BraceQuoteTracker stack) {
        quoteStack = stack;
    }

    /**
     * Gets 'sharedState'.
     *
     * @return Value for property 'sharedState'.
     */
    public RecognizerSharedState getSharedState() {
        return state;
    }

    /**
     * Sets 'sharedState'.
     *
     * @param state Value to set for property 'sharedState'.
     */
    public void setSharedState(RecognizerSharedState state) {
        this.state = state;
    }

    /**
     * Creates new {@link Lexer.BraceQuoteTracker} instance. This is
     * factory like method to deal with non static inner class.
     *
     * @param prev            previous stack entry (stack top)
     * @param quote           quote char.
     * @param percentIsFormat true is PercentIsFormat[int] rules matches.
     * @return new instance of {@link Lexer.BraceQuoteTracker} which represents actual stack top.
     */
    BraceQuoteTracker createBQT(BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
        if (prev == null) return NULL_BQT;
        return new BraceQuoteTracker(prev, quote, percentIsFormat);
    }

    protected void processString() {
    }

    protected void processTranslationKey() {
    }

    protected void enterBrace(int quote, boolean nextIsPercent) {
        quoteStack.enterBrace(quote, nextIsPercent);
    }

    protected void leaveQuote() {
        quoteStack.leaveQuote();
    }

    protected boolean rightBraceLikeQuote(int quote) {
        return quoteStack.rightBraceLikeQuote(quote);
    }

    protected void leaveBrace() {
        quoteStack.leaveBrace();
    }

    protected boolean percentIsFormat() {
        return quoteStack.percentIsFormat();
    }

    protected void resetPercentIsFormat() {
        quoteStack.resetPercentIsFormat();
    }

    /**
     * Track "He{"l{"l"}o"} world" quotes
     */
    /*static*/ class BraceQuoteTracker {
        private /*static*/ Logger log = Logger.getLogger(Lexer.BraceQuoteTracker.class.getName());
        //        private BraceQuoteTracker quoteStack = null;
        private int braceDepth;
        private char quote;
        private boolean percentIsFormat;
        private Lexer.BraceQuoteTracker next;

        public BraceQuoteTracker(Lexer.BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
            this.quote = quote;
            this.percentIsFormat = percentIsFormat;
            this.braceDepth = 1;
            this.next = prev;
        }

        /*static*/ void enterBrace(int quote, boolean percentIsFormat) {
            if (quote == 0) {  // exisiting string expression or non string expression
                if (quoteStack != NULL_BQT) {
                    if (log.isLoggable(Level.INFO)) log.info("+B");
                    ++quoteStack.braceDepth;
                    quoteStack.percentIsFormat = percentIsFormat;
                }
            } else {
                quoteStack = new Lexer.BraceQuoteTracker(quoteStack, (char) quote, percentIsFormat); // push
                if (log.isLoggable(Level.INFO)) log.info("+B PUSH => " + quoteStack);
            }
        }

        /**
         * Return quote kind if we are reentering a quote
         *
         * @return return quote on stack.
         */
        /*static*/ char leaveBrace() {
            if (log.isLoggable(Level.INFO)) log.info("-B");
            if (quoteStack != NULL_BQT && --quoteStack.braceDepth == 0) {
                return quoteStack.quote;
            }
            return 0;
        }

        /*static*/ boolean rightBraceLikeQuote(int quote) {
            final boolean b = quoteStack != NULL_BQT && quoteStack.braceDepth == 1 && (quote == 0 || quoteStack.quote == (char) quote);
//            if (log.isLoggable(Level.INFO)) log.info("rightBraceLikeQuote: " + b);
            return b;
        }

        /*static*/ void leaveQuote() {
            assert (quoteStack != NULL_BQT && quoteStack.braceDepth == 0);
            quoteStack = quoteStack.next; // pop
            if (log.isLoggable(Level.INFO)) log.info("+\" POP => " + quoteStack);
        }

        /*static*/ boolean percentIsFormat() {
            return quoteStack != NULL_BQT && quoteStack.percentIsFormat;
        }

        /*static*/ void resetPercentIsFormat() {
            quoteStack.percentIsFormat = false;
        }

        /*static*/ boolean inBraceQuote() {
            final boolean b = quoteStack != NULL_BQT;
            if (log.isLoggable(Level.INFO)) log.info("inBraceQuote: " + b);
            return b;
        }


        /**
         * {@inheritDoc}
         */
        public String toString() {
            return "BQT[" +
                    "depth=" + braceDepth +
                    ", quote=" + Integer.toString(quote) +
                    ", pif=" + percentIsFormat +
                    ", next=" + next +
                    ']';
        }


        /**
         * Gets 'braceDepth'.
         *
         * @return Value for property 'braceDepth'.
         */
        public int getBraceDepth() {
            return braceDepth;
        }

        /**
         * Gets 'quote'.
         *
         * @return Value for property 'quote'.
         */
        public char getQuote() {
            return quote;
        }

        /**
         * Gets 'percentIsFormat'.
         *
         * @return Value for property 'percentIsFormat'.
         */
        public boolean isPercentIsFormat() {
            return percentIsFormat;
        }

        /**
         * Gets 'next'.
         *
         * @return Value for property 'next'.
         */
        public Lexer.BraceQuoteTracker getNext() {
            return next;
        }

        /**
         * Sets brace depth for actual record. Brace depth should be counted from current depth in stack. But. If there
         * are 2 similar braces in sequence only depht is increased, not number of levels.
         *
         * @param depth the number of quotes in line.
         */
        void setBraceDepth(int depth) {
            this.braceDepth = depth;
        }
    }
}
