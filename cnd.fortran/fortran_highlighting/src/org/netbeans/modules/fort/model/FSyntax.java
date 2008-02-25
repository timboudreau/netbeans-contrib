
package org.netbeans.modules.fort.model;

import java.io.Reader;
import org.netbeans.modules.fort.model.lang.syntax.FHighlightLexer;
import org.netbeans.modules.fort.model.lang.syntax.FParser;

/**
 * interface for fortran syntax (used in highlighting)
 * @author Andrey Gubichev
 */
public interface FSyntax {
    /**
     * creates new Parser
     */
    FParser createParser();
    /**
     * creates lexer for highlighting
     */
    FHighlightLexer createHighlightLexer(Reader input, Object state);
}
