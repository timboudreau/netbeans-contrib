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

package org.netbeans.lib.javafx.lexer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This implementation is based on the original code from the JavaFX ANTLR 
 * gramar
 * <a href="https://openjfx-compiler.dev.java.net/source/browse/openjfx-compiler/trunk/src/share/classes/com/sun/tools/javafx/antlr/v3.g?rev=1927&view=markup">
 * v3.g rev 1927</a>.
 *  
 * @author Victor G. Vasilyev
 */
public class JavaFXLexerStateController {

    private static final boolean DEBUG = true; // TODO get it from propery file
    private static final Logger LOG = DEBUG ?
            Logger.getLogger(JavaFXLexerStateController.class.getName()) : null;

    private BraceQuoteTracker quoteStack;
    
    protected JavaFXLexerStateController(Object state) {
        this.quoteStack = (BraceQuoteTracker)state;
        if(DEBUG) LOG.log(Level.INFO, 
                          "JavaFXLexer has instantiated with state [{0}]",
                          quoteStack);
    }
    
    public Object getState() {
        if(DEBUG) LOG.log(Level.INFO, 
                          "JavaFXLexer has requested for state [{0}]",
                          quoteStack);
        return quoteStack;
    }
   
    public void release() {
        if(DEBUG) LOG.log(Level.INFO, "JavaFXLexer has released.");
        quoteStack = null;
    }

    protected void enterBrace(int quote, boolean percentIsFormat) {
        if (quote == 0) {  // exisiting string expression or non string expression
            if (quoteStack != null) {
                ++quoteStack.braceDepth;
                quoteStack.percentIsFormat = percentIsFormat;
            }
        } else {
            quoteStack = new BraceQuoteTracker(quoteStack, (char) quote, percentIsFormat); // push
        }
    }

    /** Return quote kind if we are reentering a quote
     * */
    protected char leaveBrace() {
        if (quoteStack != null && --quoteStack.braceDepth == 0) {
            return quoteStack.quote;
        }
        return 0;
    }

    protected boolean rightBraceLikeQuote(int quote) {
        return quoteStack != null && quoteStack.braceDepth == 1 && 
                    (quote == 0 || quoteStack.quote == (char) quote);
    }

    protected void leaveQuote() {
        assert (quoteStack != null && quoteStack.braceDepth == 0);
        quoteStack = quoteStack.prev; // pop
    }

    protected boolean percentIsFormat() {
        return quoteStack != null && quoteStack.percentIsFormat;
    }

    protected void resetPercentIsFormat() {
        quoteStack.percentIsFormat = false;
    }

    protected boolean inBraceQuote() {
        return quoteStack != null;
    }

    /** Track "He{"l{"l"}o"} world" quotes
     */
    protected static class BraceQuoteTracker {
        private int braceDepth;
        private char quote;
        private boolean percentIsFormat;
        private BraceQuoteTracker prev;
        private BraceQuoteTracker(BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
            this.quote = quote;
            this.percentIsFormat = percentIsFormat;
            this.braceDepth = 1;
            this.prev = prev;
        }

        public int getBraceDepth() {
            return braceDepth;
        }

        public BraceQuoteTracker getPrev() {
            return prev;
        }

        public boolean isPercentIsFormat() {
            return percentIsFormat;
        }

        public char getQuote() {
            return quote;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + ":" + 
                    " braceDepth=" + getBraceDepth() +
                    " quote=" + getQuote() +
                    " percentIsFormat=" + isPercentIsFormat() +
                    " prev=" + (getPrev() == null ? "null" : "not null");
        }
        
    }
    
}
