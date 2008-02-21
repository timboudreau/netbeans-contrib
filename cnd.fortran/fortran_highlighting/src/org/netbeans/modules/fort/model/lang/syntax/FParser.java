

package org.netbeans.modules.fort.model.lang.syntax;

import java.io.Reader;
import java.util.List;
import org.netbeans.modules.fort.model.lang.FCompoundStatement;

/**
 * fortran parser interface
 * @author Andrey Gubichev
 */
public interface FParser {    
    List<Integer> getFuncsBounds();
    FCompoundStatement parse(Reader reader);
}

