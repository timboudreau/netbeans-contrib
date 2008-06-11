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
package org.netbeans.modules.python.editor.lexer;

import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.CharStream;
import org.netbeans.spi.lexer.LexerInput;

/**
 * Implementation of org.antlr.runtime.CharStream wrapping NetBeans'
 * LexerInput. It is doing things like lookahead (by LexerInput.read() followed
 * by LexerInput.backup() and keeping read characters in buffer.
 * See also other CharStream implementations in ANTLR3, none of them could be
 * used, because all of them pull all data from an array directly.
 * 
 * @author Martin Adamek
 */
final class LexerInputCharStream implements CharStream {

    private final LexerInput input;
    private final List<Character> buffer;
    private final String sourceName;
    private int position = 0;
    private int line = 0;
    private int charPositionInLine = 0;
    /** tracks how deep mark() calls are nested */
    protected int markDepth = 0;
    /** A list of CharStreamState objects that tracks the stream state
     *  values line, charPositionInLine, and p that can change as you
     *  move through the input stream.  Indexed from 1..markDepth.
     *  A null is kept @ index 0.  Create upon first call to mark().
     */
    protected List<CharStreamState> markers;
    /** Track the last mark() call result value for use in rewind(). */
    protected int lastMarker;

    public LexerInputCharStream(LexerInput lexerInput, String sourceName) {
        this.input = lexerInput;
        this.sourceName = sourceName;
        this.buffer = new ArrayList<Character>();
    }

    public void consume() {
        char c = (char) input.read();
        charPositionInLine++;
        if (c == '\n') {
            line++;
            charPositionInLine = 0;
        }
        position++;
        buffer.add(c);
    }

    /** Get int at current input pointer + i ahead where i=1 is next int.
     *  Negative indexes are allowed.  LA(-1) is previous token (token
     *  just matched).  LA(-i) where i is before first token should
     *  yield -1, invalid char / EOF.
     */
    public int LA(int i) {
        int c = -1;
        if (i > 0) {
            for (int x = 1; x <= i; x++) {
                c = input.read();
                if (c == LexerInput.EOF) {
                    input.backup(x);
                    return -1;
                }
            }
            input.backup(i);
            return c;
        } else if (i < 0) {
            assert false : "Negative LA(" + i + ") is not supported";
            return -1;
        } else {
            return input.readText().charAt(0);
        }
    }

    /** Tell the stream to start buffering if it hasn't already.  Return
     *  current input position, index(), or some other marker so that
     *  when passed to rewind() you get back to the same spot.
     *  rewind(mark()) should not affect the input cursor.  The Lexer
     *  track line/col info as well as input index so its markers are
     *  not pure input indexes.  Same for tree node streams.
     */
    public int mark() {
        if (markers == null) {
            markers = new ArrayList<CharStreamState>();
            markers.add(null); // depth 0 means no backtracking, leave blank

        }
        markDepth++;
        CharStreamState state = null;
        if (markDepth >= markers.size()) {
            state = new CharStreamState();
            markers.add(state);
        } else {
            state = (CharStreamState) markers.get(markDepth);
        }
        state.p = position;
        state.line = line;
        state.charPositionInLine = charPositionInLine;
        lastMarker = markDepth;
        return markDepth;
    }

    /** Return the current input symbol index 0..n where n indicates the
     *  last symbol has been read.  The index is the symbol about to be
     *  read not the most recently read symbol.
     */
    public int index() {
        return position;
    }

    /** Reset the stream so that next call to index would return marker.
     *  The marker will usually be index() but it doesn't have to be.  It's
     *  just a marker to indicate what state the stream was in.  This is
     *  essentially calling release() and seek().  If there are markers
     *  created after this marker argument, this routine must unroll them
     *  like a stack.  Assume the state the stream was in when this marker
     *  was created.
     */
    public void rewind(int marker) {
        CharStreamState state = (CharStreamState) markers.get(marker);
        // restore stream state
        seek(state.p);
        line = state.line;
        charPositionInLine = state.charPositionInLine;
        release(marker);
    }

    /** Rewind to the input position of the last marker.
     *  Used currently only after a cyclic DFA and just
     *  before starting a sem/syn predicate to get the
     *  input position back to the start of the decision.
     *  Do not "pop" the marker off the state.  mark(i)
     *  and rewind(i) should balance still. It is
     *  like invoking rewind(last marker) but it should not "pop"
     *  the marker off.  It's like seek(last marker's input position).
     */
    public void rewind() {
        rewind(lastMarker);
    }

    /** You may want to commit to a backtrack but don't want to force the
     *  stream to keep bookkeeping objects around for a marker that is
     *  no longer necessary.  This will have the same behavior as
     *  rewind() except it releases resources without the backward seek.
     *  This must throw away resources for all markers back to the marker
     *  argument.  So if you're nested 5 levels of mark(), and then release(2)
     *  you have to release resources for depths 2..5.
     */
    public void release(int marker) {
        // unwind any other markers made after m and release m
        markDepth = marker;
        // release this marker
        markDepth--;
    }

    /** Set the input cursor to the position indicated by index.  This is
     *  normally used to seek ahead in the input stream.  No buffering is
     *  required to do this unless you know your stream will use seek to
     *  move backwards such as when backtracking.
     *
     *  This is different from rewind in its multi-directional
     *  requirement and in that its argument is strictly an input cursor (index).
     *
     *  For char streams, seeking forward must update the stream state such
     *  as line number.  For seeking backwards, you will be presumably
     *  backtracking using the mark/rewind mechanism that restores state and
     *  so this method does not need to update state when seeking backwards.
     *
     *  Currently, this method is only used for efficient backtracking using
     *  memoization, but in the future it may be used for incremental parsing.
     *
     *  The index is 0..n-1.  A seek to position i means that LA(1) will
     *  return the ith symbol.  So, seeking to 0 means LA(1) will return the
     *  first element in the stream. 
     */
    public void seek(int index) {
        if (index < position) {
            int move = position - index;
            for (int i = 0; i < move; i++) {
                input.backup(1);
                position--;
                buffer.remove(buffer.size() - 1);
            }
        } else if (index > position) {
            while (index > position) {
                consume();
            }
        }
    }

    /** Only makes sense for streams that buffer everything up probably, but
     *  might be useful to display the entire stream or for testing.  This
     *  value includes a single EOF.
     */
    public int size() {
        return buffer.size();
    }

    /** For infinite streams, you don't need this; primarily I'm providing
     *  a useful interface for action code.  Just make sure actions don't
     *  use this on streams that don't support it.
     */
    public String substring(int start, int stop) {
        if (stop > position) {
            assert false : "Substring from unread chars not supported";
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= stop; i++) {
            sb.append(buffer.get(i));
        }
        return sb.toString();
    }

    /** Get the ith character of lookahead.  This is the same usually as
     *  LA(i).  This will be used for labels in the generated
     *  lexer code.  I'd prefer to return a char here type-wise, but it's
     *  probably better to be 32-bit clean and be consistent with LA.
     */
    public int LT(int i) {
        return LA(i);
    }

    /** ANTLR tracks the line information automatically */
    public int getLine() {
        return line;
    }

    /** Because this stream can rewind, we need to be able to reset the line */
    public void setLine(int line) {
        assert false : "setLine(" + line + ")";
    }

    public void setCharPositionInLine(int pos) {
        assert false : "setCharPositionInLine(" + pos + ")";
    }

    /** The index of the character relative to the beginning of the line 0..n-1 */
    public int getCharPositionInLine() {
        return charPositionInLine;
    }

    public class CharStreamState {

        /** Index into the char stream of next lookahead char */
        int p;
        /** What line number is the scanner at before processing buffer[p]? */
        int line;
        /** What char position 0..n-1 in line is scanner before processing buffer[p]? */
        int charPositionInLine;
    }

    public String getSourceName() {
        return sourceName;
    }
}
    
