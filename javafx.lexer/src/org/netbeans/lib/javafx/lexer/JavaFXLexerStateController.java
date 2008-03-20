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
 * 
 * @todo simplify it! Avoid redundant method invocations to improve performance.
 * @todo Remove BraceQuoteTracker.hashCode()
 * @todo Remove BraceQuoteTracker.mutable
 */
public class JavaFXLexerStateController {

    private static final boolean DEBUG = false; 
    private static final Logger LOG = DEBUG ?
            Logger.getLogger(JavaFXLexerStateController.class.getName()) : null;

    private BraceQuoteTracker quoteStack;
    private BraceQuoteTracker lastState;
    
    protected JavaFXLexerStateController(Object state) {
        if (DEBUG) {
            LOG.log(Level.INFO, "JavaFXLexer start instantiation with state [{0}]", state);
        }
        assert(state == null || state instanceof BraceQuoteTracker);
        lastState = (BraceQuoteTracker)state;
        quoteStack = state == null ? 
            null : ((BraceQuoteTracker)state).newWorkingCopy();
         if (DEBUG) {
            LOG.log(Level.INFO, "JavaFXLexer has instantiated with state [{0}]", quoteStack);
        }
    }
    
    public Object getState() {
        if(DEBUG) LOG.log(Level.INFO, 
                          "JavaFXLexer has requested for state [{0}]",
                          quoteStack);
        if(quoteStack == null) {
            lastState = null;
        } else if(!quoteStack.equals(lastState)) {
            lastState = quoteStack.newClone();
        }
        if(DEBUG) LOG.log(Level.INFO, "returned state is [{0}]", lastState);
        return lastState;
    }
   
    public void release() {
        quoteStack = null;
        lastState = null;
        if(DEBUG) LOG.log(Level.INFO, "JavaFXLexer has released.");
    }

    protected void enterBrace(int quote, boolean percentIsFormat) {
        if (quote == 0) {  // exisiting string expression or non string expression
            if (quoteStack != null) {
                ++quoteStack.braceDepth;
                quoteStack.percentIsFormat = percentIsFormat;
            }
        } else {
//            quoteStack = new BraceQuoteTracker(quoteStack, (char) quote, percentIsFormat); // push
            BraceQuoteTracker prevState = lastState;
            quoteStack = new BraceQuoteTracker(lastState, (char) quote, percentIsFormat); // push
            lastState = prevState;
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
//        quoteStack = quoteStack.prev; // pop
        quoteStack = quoteStack.prev == null ? null : quoteStack.prev.newWorkingCopy(); // pop
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
    protected static class BraceQuoteTracker implements Cloneable {
        private int braceDepth;
        private char quote;
        private boolean percentIsFormat;
        private BraceQuoteTracker prev;
        private boolean mutable = true; // mutable working copy by default.
        private BraceQuoteTracker(BraceQuoteTracker prev, char quote, boolean percentIsFormat) {
            this.quote = quote;
            this.percentIsFormat = percentIsFormat;
            this.braceDepth = 1;
            this.prev = prev;
            if (DEBUG) {
                LOG.log(Level.INFO, "<init> of {0}", this.toString());
            }
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
            return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ":" + 
                    "mutable=" + mutable +
                    " braceDepth=" + getBraceDepth() +
                    " quote=" + getQuote() +
                    " percentIsFormat=" + isPercentIsFormat() +
                    " prev=" + 
                    (getPrev() == null ? 
                        "null" : 
                        "@" + Integer.toHexString(getPrev().hashCode()));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final BraceQuoteTracker other = (BraceQuoteTracker) obj;
            if (this.braceDepth != other.braceDepth) {
                return false;
            }
            if (this.quote != other.quote) {
                return false;
            }
            if (this.percentIsFormat != other.percentIsFormat) {
                return false;
            }
            if (this.prev != other.prev && (this.prev == null || !this.prev.equals(other.prev))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + this.braceDepth;
            hash = 89 * hash + this.quote;
            hash = 89 * hash + (this.percentIsFormat ? 1 : 0);
            hash = 89 * hash + (this.mutable ? 1 : 0);
            return hash;
        }

       @Override        
        protected Object clone() throws CloneNotSupportedException {
            BraceQuoteTracker tracker = (BraceQuoteTracker)super.clone();
            tracker.mutable = false; // all clones are immutable!
            if (DEBUG) {
                LOG.log(Level.INFO, "clone() of {0}", this.toString());
                LOG.log(Level.INFO, "clone is {0}", tracker.toString());
            }
            return tracker;
        }

        private BraceQuoteTracker newClone() {
            try {
                return (BraceQuoteTracker) clone();
            } catch (CloneNotSupportedException ex) {
                // This exception should be never occurred.
                Logger.getLogger(JavaFXLexerStateController.class.getName()).
                        log(Level.SEVERE, 
                            "The " + 
                            this.getClass().getName() +
                            " class MUST implement the Cloneable interface."
                            , ex);
                return null;
            }            
        }

        private BraceQuoteTracker newWorkingCopy() {
            try {
                BraceQuoteTracker tracker = (BraceQuoteTracker) this.clone();
                tracker.mutable = true; // all Working Copies are mutable!
                if (DEBUG) {
                    LOG.log(Level.INFO, "newWorkingCopy() of {0}", this.toString());
                    LOG.log(Level.INFO, "newWorkingCopy is {0}", tracker.toString());
                }
                return tracker;
            } catch (CloneNotSupportedException ex) {
//                Logger.getLogger(JavaFXLexerStateController.class.getName()).
//                        log(Level.SEVERE, null, ex);
                // We never should be here.
                return null;
            }
        }
        
    }
    
}
