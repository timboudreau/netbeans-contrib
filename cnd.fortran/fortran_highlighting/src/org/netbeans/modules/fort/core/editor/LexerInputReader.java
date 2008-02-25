
package org.netbeans.modules.fort.core.editor;

import java.io.IOException;
import java.io.Reader;
import org.netbeans.spi.lexer.LexerInput;

/**
 * utility class for lexer
 */
class LexerInputReader extends Reader {

    private LexerInput input;
    
    boolean isClosed;
    boolean isEof;
    
    public LexerInputReader(LexerInput input) {
        this.input = input;
        
        isClosed = false;
        isEof = false;
    }
    
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (isClosed) {
            throw new IOException();
        }
        
        if (isEof) {
            return -1;
        }
        
        int ch = LexerInput.EOF + 1;
        int count = 0;
        
        while(count < len && (ch = input.read()) != LexerInput.EOF) {
            cbuf[count + off] = (char) ch;
            count++;
        }
        
        if (ch == LexerInput.EOF) {
            isEof = true;
            if (count == 0) {
                return -1;
            }
        }
        
        return count; 
    }

    public void close() throws IOException {
        isClosed = true;
    }

}
