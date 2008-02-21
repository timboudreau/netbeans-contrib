

package org.netbeans.modules.fort.model.lang;

import java.util.List;

/**
 *
 * @author Andrey Gubichev
 */
public interface FFile extends FNamedElement, FScope {
    /** Gets this file absolute path */
    String getAbsolutePath();
    /** Gets the project, to which the file belongs*/
    FProject getProject();
    /** Gets this file text */
    String getText();
    /** Gets this file text */
    String getText(int start, int end);
    /** Sorted (by start offset) list of declarations in the file */
    List<FOffsetableDeclaration> getDeclarations();

    /** 
     * Returns true if the file has been already parsed
     * (i.e. was parsed since last change),
     * otherwise false 
     */
    boolean isParsed();  
    
    /*
     * Checks whether the file needs to be parsed,
     * if yes, scedules parsing this file.
     * If wait parameter is true, waits until this file is parsed.
     * If the file is already parsed, immediately returns.
     *
     * @param wait determines whether to wait until the file is parsed:
     * if true, waits, otherwise doesn't wait, just puts the given file
     * into parser queue
     */
    void scheduleParsing(boolean wait) throws InterruptedException;    
}
