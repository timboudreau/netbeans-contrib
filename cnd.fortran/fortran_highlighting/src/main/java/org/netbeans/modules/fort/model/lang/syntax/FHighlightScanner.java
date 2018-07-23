
package org.netbeans.modules.fort.model.lang.syntax;

import java.io.Reader;

/**
 * interface for fortran scanner
 * @author Andrey Gubichev
 */
public interface FHighlightScanner<T extends FTokenId> {    
    /**
     * @return next token
     */
    T getNext();
    /**
     * reset reading
     */
    void reset(Reader reader);
     /**
     * @return offset
     */
   int getOffset();
}
